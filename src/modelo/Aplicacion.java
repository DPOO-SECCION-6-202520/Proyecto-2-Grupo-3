package modelo;

import modelo.persistencia.GestorPersistencia;
import modelo.persistencia.ProcesoEntreUsuarios;
import modelo.persistencia.Solicitud;
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