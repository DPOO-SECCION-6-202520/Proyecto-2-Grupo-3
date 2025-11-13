package modelo;

import modelo.tiquetes.TiqueteReventa;
import modelo.tiquetes.Contraoferta;
import modelo.persistencia.ProcesoEntreUsuarios;
import modelo.persistencia.GestorPersistencia;
import modelo.usuarios.Usuario;
import modelo.usuarios.Administrador;
import modelo.usuarios.Comprador;
import modelo.usuarios.Organizador;
import modelo.eventos.Evento;
import modelo.eventos.Venue;
import modelo.eventos.Localidad;
import modelo.eventos.Oferta;
import modelo.tiquetes.Tiquete;
import modelo.pagos.Pagos;
import modelo.pagos.Compra;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * Clase controladora central del sistema que orquesta todas las funcionalidades.
 * Implementa el patrón Singleton para tener una única instancia.
 * Responsable de coordinar entre todos los componentes del sistema.
 */
public class Aplicacion {
    private static Aplicacion instancia;
    
    // Reemplazar colecciones individuales con gestor de persistencia
    private GestorPersistencia gestorPersistencia;
    private ArrayList<Compra> compras; // Las compras se mantienen separadas por ahora
    private HashMap<String, Usuario> sesionesActivas;
    
    // Servicios
    private Pagos servicioPagos;
    
    // Constructor privado para Singleton
    private Aplicacion() {
        this.gestorPersistencia = new GestorPersistencia();
        this.compras = new ArrayList<>();
        this.sesionesActivas = new HashMap<>();
        this.servicioPagos = Pagos.getInstancia();
        
        // Cargar datos existentes
        gestorPersistencia.cargarTodosLosDatos();
        
        // Si no hay datos, inicializar con datos de prueba
        if (gestorPersistencia.getUsuarios().isEmpty()) {
            System.out.println("No se encontraron datos guardados. Inicializando con datos de prueba...");
            inicializarDatosPrueba();
            guardarDatos(); // Guardar los datos de prueba
        } else {
            System.out.println("Datos cargados exitosamente del almacenamiento.");
        }
    }
    
    /**
     * Método Singleton para obtener la instancia única
     */
    public static Aplicacion getInstancia() {
        if (instancia == null) {
            instancia = new Aplicacion();
        }
        return instancia;
    }
    
    // ==================== MÉTODOS DE AUTENTICACIÓN ====================
    
    /**
     * Inicia sesión de un usuario
     */
    public Usuario iniciarSesion(String login, String password) {
        // Buscar usuario a través del gestor de persistencia
        for (Usuario usuario : gestorPersistencia.getUsuarios()) {
            if (usuario.validarCredenciales(login, password)) {
                sesionesActivas.put(login, usuario);
                System.out.println("Sesión iniciada: " + usuario.getLogin() + " (" + usuario.getTipoUsuario() + ")");
                return usuario;
            }
        }
        System.out.println("Error: Credenciales incorrectas");
        return null;
    }
    
    /**
     * Cierra sesión de un usuario
     */
    public void cerrarSesion(String login) {
        if (sesionesActivas.containsKey(login)) {
            sesionesActivas.remove(login);
            System.out.println("Sesión cerrada: " + login);
        }
    }
    
    /**
     * Obtiene el usuario actualmente autenticado
     */
    public Usuario getUsuarioActual(String login) {
        return sesionesActivas.get(login);
    }
    
    // ==================== MÉTODOS DE REGISTRO ====================
    
    /**
     * Registra un nuevo comprador
     */
    public Comprador registrarComprador(String login, String password) {
        // Verificar si el usuario ya existe
        if (gestorPersistencia.buscarUsuarioPorLogin(login) != null) {
            System.out.println("Error: El usuario ya existe");
            return null;
        }
        
        Comprador nuevoComprador = new Comprador(login, password);
        gestorPersistencia.agregarUsuario(nuevoComprador);
        guardarDatos();
        System.out.println("Comprador registrado: " + login);
        return nuevoComprador;
    }
    
    /**
     * Registra un nuevo organizador
     */
    public Organizador registrarOrganizador(String login, String password) {
        if (gestorPersistencia.buscarUsuarioPorLogin(login) != null) {
            System.out.println("Error: El usuario ya existe");
            return null;
        }
        
        Organizador nuevoOrganizador = new Organizador(login, password);
        gestorPersistencia.agregarUsuario(nuevoOrganizador);
        guardarDatos();
        System.out.println("Organizador registrado: " + login);
        return nuevoOrganizador;
    }
    
    /**
     * Crea un administrador (solo para inicialización)
     */
    private Administrador crearAdministrador(String login, String password) {
        Administrador admin = new Administrador(login, password);
        gestorPersistencia.agregarUsuario(admin);
        return admin;
    }
    
    // ==================== MÉTODOS DE GESTIÓN DE EVENTOS ====================
    
    /**
     * Crea un nuevo evento (solo organizadores)
     */
    public Evento crearEvento(Organizador organizador, String id, String nombre, Date fechaHora, Venue venue) {
        if (!gestorPersistencia.getUsuarios().contains(organizador)) {
            System.out.println("Error: Organizador no registrado");
            return null;
        }
        
        if (!venue.isAprobado()) {
            System.out.println("Error: El venue no está aprobado");
            return null;
        }
        
        if (!venue.estaDisponible(fechaHora)) {
            System.out.println("Error: El venue no está disponible para esa fecha");
            return null;
        }
        
        Evento nuevoEvento = organizador.crearEvento(id, nombre, fechaHora, venue);
        gestorPersistencia.agregarEvento(nuevoEvento);
        venue.programarEvento(nuevoEvento, fechaHora);
        
        guardarDatos();
        System.out.println("Evento creado (pendiente de aprobación): " + nombre);
        return nuevoEvento;
    }
    
    /**
     * Aprueba un evento (solo administradores)
     */
    public boolean aprobarEvento(Administrador admin, Evento evento) {
        if (!gestorPersistencia.getEventos().contains(evento)) {
            System.out.println("Error: Evento no encontrado");
            return false;
        }
        
        evento.aprobar();
        guardarDatos();
        System.out.println("Evento aprobado: " + evento.getNombre());
        return true;
    }
    
    /**
     * Obtiene eventos disponibles para compra
     */
    public ArrayList<Evento> getEventosDisponibles() {
        return gestorPersistencia.getEventosDisponibles();
    }
    
    // ==================== MÉTODOS DE COMPRA ====================
    
    /**
     * Procesa la compra de tiquetes
     */
    public Compra comprarTiquetes(Comprador comprador, Evento evento, Localidad localidad, 
                                 int cantidad, double porcentajeAdicional, double cobroFijo) {
        if (!comprador.getTipoUsuario().equals("comprador")) {
            System.out.println("Error: Solo los compradores pueden comprar tiquetes");
            return null;
        }
        
        if (!evento.estaActivo()) {
            System.out.println("Error: El evento no está activo");
            return null;
        }
        
        if (!localidad.haySuficienteDisponibilidad(cantidad)) {
            System.out.println("Error: No hay suficientes tiquetes disponibles");
            return null;
        }
        
        // Validar restricciones de compra
        if (!servicioPagos.validarRestriccionesCompra(localidad.obtenerTiquetesDisponibles(cantidad), 10)) {
            return null;
        }
        
        ArrayList<Tiquete> tiquetesComprados = comprador.comprarTiquete(
            evento, localidad, cantidad, porcentajeAdicional, cobroFijo
        );
        
        if (tiquetesComprados.isEmpty()) {
            System.out.println("Error: La compra falló");
            return null;
        }
        
        // Crear registro de compra
        String compraId = "COMP-" + System.currentTimeMillis();
        double montoTotal = servicioPagos.calcularPrecioTotal(tiquetesComprados, porcentajeAdicional, cobroFijo);
        Compra compra = new Compra(compraId, new Date(), montoTotal, tiquetesComprados, comprador);
        compras.add(compra);
        
        // Registrar proceso de compra
        ProcesoEntreUsuarios procesoCompra = new ProcesoEntreUsuarios(
            "PROC-" + System.currentTimeMillis(),
            ProcesoEntreUsuarios.TipoProceso.COMPRA_TIQUETE,
            new Date(),
            comprador
        );
        for (Tiquete tiquete : tiquetesComprados) {
            procesoCompra.agregarTiquete(tiquete);
        }
        procesoCompra.setMonto(montoTotal);
        procesoCompra.setEstado("completado");
        procesoCompra.setDescripcion("Compra de " + cantidad + " tiquetes para " + evento.getNombre());
        
        gestorPersistencia.agregarProceso(procesoCompra);
        guardarDatos();
        
        System.out.println("Compra registrada: " + compraId);

        if (compra != null) {
            compra.setEstado("aprobada");
            System.out.println("Compra registrada y aprobada: " + compra.getId());
        }
        return compra;
    }
    
    // ==================== MÉTODOS DE GESTIÓN DE VENUES ====================
    
    /**
     * Crea un nuevo venue (solo administradores)
     */
    public Venue crearVenue(Administrador admin, String id, String nombre, String ubicacion, int capacidad) {
        Venue nuevoVenue = admin.crearVenue(id, nombre, ubicacion, capacidad);
        gestorPersistencia.agregarVenue(nuevoVenue);
        guardarDatos();
        System.out.println("Venue creado y aprobado: " + nombre);
        return nuevoVenue;
    }
    
    /**
     * Aprueba un venue sugerido (solo administradores)
     */
    public boolean aprobarVenue(Administrador admin, Venue venue) {
        if (!gestorPersistencia.getVenues().contains(venue)) {
            System.out.println("Error: Venue no encontrado");
            return false;
        }
        
        admin.aprobarVenue(venue);
        guardarDatos();
        System.out.println("Venue aprobado: " + venue.getNombre());
        return true;
    }
    
    // ==================== MÉTODOS DE OFERTAS ====================
    
    /**
     * Crea una oferta (solo organizadores)
     */
    public Oferta crearOferta(Organizador organizador, String id, String descripcion, 
                             double porcentajeDescuento, Date fechaInicio, Date fechaExpiracion,
                             Evento evento, Localidad localidad) {
        if (!organizador.getEventosCreados().contains(evento)) {
            System.out.println("Error: El organizador no es dueño de este evento");
            return null;
        }
        
        Oferta oferta = organizador.generarOferta(id, descripcion, porcentajeDescuento, 
                                                 fechaInicio, fechaExpiracion, evento, localidad);
        System.out.println("Oferta creada: " + descripcion);
        return oferta;
    }

    // ==================== MÉTODOS DE REVENTA ====================
    
    /**
     * Pone un tiquete en reventa
     */
    public TiqueteReventa ponerTiqueteEnReventa(Comprador vendedor, Tiquete tiquete, double precioReventa) {
        if (!vendedor.getTipoUsuario().equals("comprador")) {
            System.out.println("Error: Solo los compradores pueden revender tiquetes");
            return null;
        }
        
        if (!vendedor.getHistorialTiquetes().contains(tiquete)) {
            System.out.println("Error: El tiquete no pertenece al vendedor");
            return null;
        }
        
        // Verificar que el tiquete puede ser revendido
        if (tiquete instanceof modelo.tiquetes.Deluxe) {
            System.out.println("Error: Los tiquetes Deluxe no se pueden revender");
            return null;
        }
        
        if (!tiquete.puedeSerTransferido()) {
            System.out.println("Error: El tiquete no se puede transferir");
            return null;
        }
        
        // Verificar si ya está en reventa
        for (TiqueteReventa reventa : gestorPersistencia.getReventas()) {
            if (reventa.getTiquete().equals(tiquete) && reventa.isActivo()) {
                System.out.println("Error: El tiquete ya está en reventa");
                return null;
            }
        }
        
        // Crear reventa
        String reventaId = "REV-" + System.currentTimeMillis();
        TiqueteReventa reventa = new TiqueteReventa(reventaId, tiquete, vendedor, precioReventa);
        gestorPersistencia.agregarReventa(reventa);
        
        // Registrar proceso
        ProcesoEntreUsuarios proceso = new ProcesoEntreUsuarios(
            "PROC-REV-" + System.currentTimeMillis(),
            ProcesoEntreUsuarios.TipoProceso.TRANSFERENCIA_TIQUETE,
            new Date(),
            vendedor
        );
        proceso.agregarTiquete(tiquete);
        proceso.setDescripcion("Tiquete puesto en reventa por " + precioReventa);
        proceso.setEstado("pendiente");
        gestorPersistencia.agregarProceso(proceso);
        
        guardarDatos();
        System.out.println("Tiquete puesto en reventa: " + tiquete.getId() + " por $" + precioReventa);
        return reventa;
    }
    
    /**
     * Compra un tiquete en reventa
     */
    public boolean comprarTiqueteReventa(Comprador comprador, TiqueteReventa reventa) {
        if (!comprador.getTipoUsuario().equals("comprador")) {
            System.out.println("Error: Solo los compradores pueden comprar tiquetes en reventa");
            return false;
        }
        
        if (!reventa.isActivo()) {
            System.out.println("Error: El tiquete ya no está disponible en reventa");
            return false;
        }
        
        if (!reventa.puedeSerRevendido()) {
            System.out.println("Error: El tiquete no se puede revender");
            return false;
        }
        
        // Procesar pago
        if (!servicioPagos.procesarPagoConSaldo(comprador, reventa.getPrecioReventa())) {
            System.out.println("Error: Saldo insuficiente para comprar el tiquete en reventa");
            return false;
        }
        
        // Transferir el pago al vendedor
        servicioPagos.procesarReembolsoSaldo(
            reventa.getVendedor(), 
            reventa.getPrecioReventa(), 
            "Venta de tiquete en reventa: " + reventa.getTiquete().getId()
        );
        
        // Transferir el tiquete
        Tiquete tiquete = reventa.getTiquete();
        Comprador vendedor = (Comprador) reventa.getVendedor();
        
        // Remover del vendedor y agregar al comprador
        vendedor.getHistorialTiquetes().remove(tiquete);
        comprador.agregarTiqueteAlHistorial(tiquete);
        
        // Desactivar reventa
        reventa.setActivo(false);
        
        // Registrar proceso
        ProcesoEntreUsuarios proceso = new ProcesoEntreUsuarios(
            "PROC-COMP-REV-" + System.currentTimeMillis(),
            ProcesoEntreUsuarios.TipoProceso.TRANSFERENCIA_TIQUETE,
            new Date(),
            vendedor
        );
        proceso.agregarTiquete(tiquete);
        proceso.setUsuarioDestino(comprador);
        proceso.setMonto(reventa.getPrecioReventa());
        proceso.setDescripcion("Tiquete comprado en reventa por " + comprador.getLogin());
        proceso.setEstado("completado");
        gestorPersistencia.agregarProceso(proceso);
        
        guardarDatos();
        System.out.println("Tiquete comprado en reventa: " + tiquete.getId() + " por $" + reventa.getPrecioReventa());
        return true;
    }
    
    // ==================== MÉTODOS DE CONTRADOFERTAS ====================
    
    /**
     * Crea una contraoferta para un tiquete en reventa
     */
    public Contraoferta crearContraoferta(Comprador comprador, TiqueteReventa reventa, double precioOfertado) {
        if (!comprador.getTipoUsuario().equals("comprador")) {
            System.out.println("Error: Solo los compradores pueden hacer contraofertas");
            return null;
        }
        
        if (!reventa.isActivo()) {
            System.out.println("Error: El tiquete ya no está disponible en reventa");
            return null;
        }
        
        if (precioOfertado >= reventa.getPrecioReventa()) {
            System.out.println("Error: La contraoferta debe ser menor al precio de reventa");
            return null;
        }
        
        // Verificar si ya existe una contraoferta pendiente del mismo comprador
        for (Contraoferta contra : gestorPersistencia.getContraofertasPorReventa(reventa)) {
            if (contra.getComprador().equals(comprador) && contra.estaPendiente()) {
                System.out.println("Error: Ya tienes una contraoferta pendiente para este tiquete");
                return null;
            }
        }
        
        // Crear contraoferta
        String contraId = "CONTRA-" + System.currentTimeMillis();
        Contraoferta contraoferta = new Contraoferta(contraId, reventa, comprador, precioOfertado);
        gestorPersistencia.agregarContraoferta(contraoferta);
        
        // Registrar proceso
        ProcesoEntreUsuarios proceso = new ProcesoEntreUsuarios(
            "PROC-CONTRA-" + System.currentTimeMillis(),
            ProcesoEntreUsuarios.TipoProceso.TRANSFERENCIA_TIQUETE,
            new Date(),
            comprador
        );
        proceso.agregarTiquete(reventa.getTiquete());
        proceso.setUsuarioDestino(reventa.getVendedor());
        proceso.setMonto(precioOfertado);
        proceso.setDescripcion("Contraoferta de $" + precioOfertado + " para tiquete " + reventa.getTiquete().getId());
        proceso.setEstado("pendiente");
        gestorPersistencia.agregarProceso(proceso);
        
        guardarDatos();
        System.out.println("Contraoferta creada: $" + precioOfertado + " para tiquete " + reventa.getTiquete().getId());
        return contraoferta;
    }
    
    /**
     * Acepta una contraoferta
     */
    public boolean aceptarContraoferta(Comprador vendedor, Contraoferta contraoferta) {
        if (!vendedor.getTipoUsuario().equals("comprador")) {
            System.out.println("Error: Solo los compradores pueden aceptar contraofertas");
            return false;
        }
        
        if (!contraoferta.getTiqueteReventa().getVendedor().equals(vendedor)) {
            System.out.println("Error: Solo el vendedor puede aceptar la contraoferta");
            return false;
        }
        
        if (!contraoferta.estaPendiente()) {
            System.out.println("Error: La contraoferta ya no está pendiente");
            return false;
        }
        
        TiqueteReventa reventa = contraoferta.getTiqueteReventa();
        Comprador comprador = (Comprador) contraoferta.getComprador();
        
        // Procesar la venta
        if (!comprarTiqueteReventa(comprador, reventa)) {
            System.out.println("Error: No se pudo procesar la venta de la contraoferta");
            return false;
        }
        
        // Aceptar la contraoferta
        contraoferta.aceptar();
        
        // Rechazar otras contraofertas pendientes para el mismo tiquete
        for (Contraoferta otraContra : gestorPersistencia.getContraofertasPorReventa(reventa)) {
            if (!otraContra.equals(contraoferta) && otraContra.estaPendiente()) {
                otraContra.rechazar();
            }
        }
        
        guardarDatos();
        System.out.println("Contraoferta aceptada: tiquete vendido a " + comprador.getLogin());
        return true;
    }
    
    /**
     * Rechaza una contraoferta
     */
    public boolean rechazarContraoferta(Comprador vendedor, Contraoferta contraoferta) {
        if (!vendedor.getTipoUsuario().equals("comprador")) {
            System.out.println("Error: Solo los compradores pueden rechazar contraofertas");
            return false;
        }
        
        if (!contraoferta.getTiqueteReventa().getVendedor().equals(vendedor)) {
            System.out.println("Error: Solo el vendedor puede rechazar la contraoferta");
            return false;
        }
        
        if (!contraoferta.estaPendiente()) {
            System.out.println("Error: La contraoferta ya no está pendiente");
            return false;
        }
        
        contraoferta.rechazar();
        
        // Registrar proceso
        ProcesoEntreUsuarios proceso = new ProcesoEntreUsuarios(
            "PROC-RECHAZO-" + System.currentTimeMillis(),
            ProcesoEntreUsuarios.TipoProceso.TRANSFERENCIA_TIQUETE,
            new Date(),
            vendedor
        );
        proceso.agregarTiquete(contraoferta.getTiqueteReventa().getTiquete());
        proceso.setUsuarioDestino(contraoferta.getComprador());
        proceso.setMonto(contraoferta.getPrecioOfertado());
        proceso.setDescripcion("Contraoferta rechazada por el vendedor");
        proceso.setEstado("rechazado");
        gestorPersistencia.agregarProceso(proceso);
        
        guardarDatos();
        System.out.println("Contraoferta rechazada");
        return true;
    }
    
    // ==================== MÉTODOS ADMINISTRATIVOS ====================
    
    /**
     * Elimina una oferta de reventa (solo administrador)
     */
    public boolean borrarOfertaReventa(Administrador admin, TiqueteReventa reventa) {
        if (!admin.getTipoUsuario().equals("administrador")) {
            System.out.println("Error: Solo los administradores pueden borrar ofertas de reventa");
            return false;
        }
        
        if (!reventa.isActivo()) {
            System.out.println("Error: La oferta de reventa ya no está activa");
            return false;
        }
        
        // Desactivar la reventa
        reventa.setActivo(false);
        
        // Rechazar todas las contraofertas pendientes
        for (Contraoferta contra : gestorPersistencia.getContraofertasPorReventa(reventa)) {
            if (contra.estaPendiente()) {
                contra.rechazar();
            }
        }
        
        // Registrar proceso
        ProcesoEntreUsuarios proceso = new ProcesoEntreUsuarios(
            "PROC-BORRAR-" + System.currentTimeMillis(),
            ProcesoEntreUsuarios.TipoProceso.TRANSFERENCIA_TIQUETE,
            new Date(),
            admin
        );
        proceso.agregarTiquete(reventa.getTiquete());
        proceso.setUsuarioDestino(reventa.getVendedor());
        proceso.setDescripcion("Oferta de reventa eliminada por administrador");
        proceso.setEstado("cancelado");
        gestorPersistencia.agregarProceso(proceso);
        
        guardarDatos();
        System.out.println("Oferta de reventa eliminada por administrador: " + reventa.getId());
        return true;
    }
    
    // ==================== MÉTODOS DE CONSULTA ====================
    
    /**
     * Obtiene todos los eventos del sistema
     */
    public ArrayList<Evento> getTodosLosEventos() {
        return gestorPersistencia.getEventos();
    }
    
    /**
     * Obtiene venues aprobados
     */
    public ArrayList<Venue> getVenuesAprobados() {
        return gestorPersistencia.getVenuesAprobados();
    }
    
    /**
     * Obtiene compras del sistema
     */
    public ArrayList<Compra> getTodasLasCompras() {
        return new ArrayList<>(compras);
    }
    
    /**
     * Guarda todos los datos del sistema
     */
    public void guardarDatos() {
        gestorPersistencia.guardarTodosLosDatos();
    }
    
    /**
     * Cierra la aplicación y guarda los datos
     */
    public void cerrarAplicacion() {
        guardarDatos();
        System.out.println("Aplicación cerrada. Datos guardados correctamente.");
    }

    /**
     * Obtiene tiquetes en reventa activos
     */
    public ArrayList<TiqueteReventa> getTiquetesEnReventa() {
        return gestorPersistencia.getReventasActivas();
    }
    
    /**
     * Obtiene contraofertas pendientes de un vendedor
     */
    public ArrayList<Contraoferta> getContraofertasPendientesParaVendedor(Comprador vendedor) {
        ArrayList<Contraoferta> contraofertasVendedor = new ArrayList<>();
        for (TiqueteReventa reventa : gestorPersistencia.getReventasPorVendedor(vendedor)) {
            contraofertasVendedor.addAll(gestorPersistencia.getContraofertasPorReventa(reventa));
        }
        return contraofertasVendedor;
    }
    
    /**
     * Obtiene las reventas de un comprador
     */
    public ArrayList<TiqueteReventa> getReventasDeComprador(Comprador comprador) {
        return gestorPersistencia.getReventasPorVendedor(comprador);
    }
    
    /**
     * Obtiene las contraofertas de un comprador
     */
    public ArrayList<Contraoferta> getContraofertasDeComprador(Comprador comprador) {
        return gestorPersistencia.getContraofertasPorComprador(comprador);
    }
    
    // ==================== MÉTODOS DE INICIALIZACIÓN ====================
    
    /**
     * Inicializa datos de prueba para demostración
     */
    private void inicializarDatosPrueba() {
        System.out.println("=== INICIALIZANDO DATOS DE PRUEBA ===");
        
        // Crear administrador
        Administrador admin = crearAdministrador("admin", "admin123");
        
        // Crear venues
        Venue venue1 = crearVenue(admin, "V1", "Estadio Nacional", "San José", 50000);
        Venue venue2 = crearVenue(admin, "V2", "Auditorio Nacional", "San José", 3000);
        
        // Crear y agregar localidades a los venues
        Localidad localidadVIP1 = new Localidad("L1", "VIP", true, venue1, 100, 150.0);
        Localidad localidadGeneral1 = new Localidad("L2", "General", false, venue1, 400, 50.0);
        venue1.agregarLocalidad(localidadVIP1);
        venue1.agregarLocalidad(localidadGeneral1);
        
        Localidad localidadVIP2 = new Localidad("L3", "VIP", true, venue2, 50, 200.0);
        Localidad localidadGeneral2 = new Localidad("L4", "General", false, venue2, 200, 75.0);
        venue2.agregarLocalidad(localidadVIP2);
        venue2.agregarLocalidad(localidadGeneral2);
        
        // Crear organizadores
        Organizador org1 = registrarOrganizador("promotor1", "promo123");
        Organizador org2 = registrarOrganizador("promotor2", "promo123");
        
        // Crear compradores
        Comprador comp1 = registrarComprador("cliente1", "cliente123");
        Comprador comp2 = registrarComprador("cliente2", "cliente123");
        
        // Recargar saldos de prueba
        comp1.agregarSaldo(1000.0);
        comp2.agregarSaldo(500.0);
        
        // Crear eventos
        Date fecha1 = new Date(System.currentTimeMillis() + 86400000 * 7); // 7 días en el futuro
        Date fecha2 = new Date(System.currentTimeMillis() + 86400000 * 14); // 14 días en el futuro
        
        Evento evento1 = crearEvento(org1, "E1", "Concierto de Rock", fecha1, venue1);
        Evento evento2 = crearEvento(org2, "E2", "Festival de Jazz", fecha2, venue2);
        
        // Aprobar eventos
        if (evento1 != null) aprobarEvento(admin, evento1);
        if (evento2 != null) aprobarEvento(admin, evento2);
        
        // Asignar tiquetes a eventos
        org1.asignarTiquetes(evento1, localidadVIP1, 50, 150.0);
        org1.asignarTiquetes(evento1, localidadGeneral1, 200, 50.0);
        
        org2.asignarTiquetes(evento2, localidadVIP2, 30, 200.0);
        org2.asignarTiquetes(evento2, localidadGeneral2, 100, 75.0);
        
        System.out.println("  Datos de prueba inicializados correctamente");
        System.out.println("   - 2 Venues creados con localidades");
        System.out.println("   - 2 Organizadores registrados");
        System.out.println("   - 2 Compradores registrados");
        System.out.println("   - 2 Eventos creados y aprobados");
        System.out.println("   - Tiquetes asignados a eventos");
    }
    
    // ==================== MÉTODOS DE DEMOSTRACIÓN ====================
    
    /**
     * Ejecuta una demostración completa del sistema
     */
    public void ejecutarDemostracion() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("DEMOSTRACIÓN DEL SISTEMA");
        System.out.println("=".repeat(50));
        
        try {
            // 1. Iniciar sesiones
            System.out.println("\n1. INICIANDO SESIONES:");
            Usuario admin = iniciarSesion("admin", "admin123");
            Usuario org1 = iniciarSesion("promotor1", "promo123");
            Usuario comp1 = iniciarSesion("cliente1", "cliente123");
            
            // 2. Mostrar eventos disponibles
            System.out.println("\n2. EVENTOS DISPONIBLES:");
            ArrayList<Evento> eventosDisponibles = getEventosDisponibles();
            for (Evento evento : eventosDisponibles) {
                System.out.println( evento.getNombre() + 
                                 " (" + evento.getTiquetesDisponibles() + " tiquetes disponibles)");
            }
            
            // 3. Realizar compra de prueba
            System.out.println("\n3. REALIZANDO COMPRA DE PRUEBA:");
            if (comp1 instanceof Comprador && !eventosDisponibles.isEmpty()) {
                Evento evento = eventosDisponibles.get(0);
                ArrayList<Localidad> localidades = evento.getVenue().getLocalidades();
                
                if (!localidades.isEmpty()) {
                    Localidad localidad = localidades.get(0);
                    Compra compra = comprarTiquetes((Comprador) comp1, evento, localidad, 2, 0.15, 5.0);
                    if (compra != null) {
                        System.out.println("   Compra exitosa: " + compra.getId());
                    }
                }
            }
            
            // 4. Mostrar reporte de ganancias
            System.out.println("\n4. REPORTES DE GANANCIAS:");
            if (admin instanceof Administrador) {
                ((Administrador) admin).observarGanancias(compras);
            }
            
            // 5. Crear oferta de prueba
            System.out.println("\n5. CREANDO OFERTA DE PRUEBA:");
            if (org1 instanceof Organizador && !eventosDisponibles.isEmpty()) {
                Evento evento = eventosDisponibles.get(0);
                ArrayList<Localidad> localidades = evento.getVenue().getLocalidades();
                
                if (!localidades.isEmpty()) {
                    Localidad localidad = localidades.get(0);
                    Date fechaInicio = new Date();
                    Date fechaExpiracion = new Date(System.currentTimeMillis() + 86400000 * 2);
                    
                    Oferta oferta = crearOferta((Organizador) org1, "OF1", "Oferta Especial", 0.2, 
                                               fechaInicio, fechaExpiracion, evento, localidad);
                    if (oferta != null) {
                        System.out.println("   Oferta creada: " + oferta.getDescripcion());
                    }
                }
            }
            
            System.out.println("\n" + "=".repeat(50));
            System.out.println("DEMOSTRACIÓN COMPLETADA EXITOSAMENTE");
            System.out.println("=".repeat(50));
            
        } catch (Exception e) {
            System.out.println("Error durante la demostración: " + e.getMessage());
        }
    }
    
    // ==================== GETTERS PARA ACCESO EXTERNO ====================
    
    public ArrayList<Usuario> getUsuarios() { return gestorPersistencia.getUsuarios(); }
    public ArrayList<Evento> getEventos() { return gestorPersistencia.getEventos(); }
    public ArrayList<Venue> getVenues() { return gestorPersistencia.getVenues(); }
    public ArrayList<Compra> getCompras() { return new ArrayList<>(compras); }
}