package modelo.persistencia;

import modelo.tiquetes.Tiquete;
import modelo.tiquetes.TiqueteReventa;
import modelo.tiquetes.Contraoferta;

import modelo.usuarios.Usuario;
import modelo.usuarios.Administrador;
import modelo.usuarios.Comprador;
import modelo.usuarios.Organizador;
import modelo.eventos.Evento;
import modelo.eventos.Venue;

import java.util.ArrayList;

/**
 * Clase coordinadora que maneja todas las operaciones de persistencia del sistema
 */
public class GestorPersistencia {
    // Instancias de todas las persistencias
    private PersistenciaUsuarios persistenciaUsuarios;
    private PersistenciaEventos persistenciaEventos;
    private PersistenciaVenues persistenciaVenues;
    private PersistenciaProcesosEntreUsuarios persistenciaProcesos;
    private PersistenciaSolicitudes persistenciaSolicitudes;
    private PersistenciaReventas persistenciaReventas;
    private PersistenciaContraofertas persistenciaContraofertas;

    // Colecciones en memoria
    private ArrayList<Usuario> usuarios;
    private ArrayList<Evento> eventos;
    private ArrayList<Venue> venues;
    private ArrayList<ProcesoEntreUsuarios> procesos;
    private ArrayList<Solicitud> solicitudes;
    private ArrayList<TiqueteReventa> reventas;
    private ArrayList<Contraoferta> contraofertas;
    
    public GestorPersistencia() {
        // Inicializar persistencias
        this.persistenciaUsuarios = new PersistenciaUsuarios();
        this.persistenciaEventos = new PersistenciaEventos();
        this.persistenciaVenues = new PersistenciaVenues();
        this.persistenciaProcesos = new PersistenciaProcesosEntreUsuarios();
        this.persistenciaSolicitudes = new PersistenciaSolicitudes();
        this.persistenciaReventas = new PersistenciaReventas();
        this.persistenciaContraofertas = new PersistenciaContraofertas();
        
        // Inicializar colecciones
        this.usuarios = new ArrayList<>();
        this.eventos = new ArrayList<>();
        this.venues = new ArrayList<>();
        this.procesos = new ArrayList<>();
        this.solicitudes = new ArrayList<>();
        this.reventas = new ArrayList<>();
        this.contraofertas = new ArrayList<>();
    }
    
    /**
     * Carga todos los datos del sistema desde los archivos CSV
     */
    public void cargarTodosLosDatos() {
        System.out.println("=== CARGANDO DATOS DEL SISTEMA ===");
        
        // Cargar en el orden correcto para mantener referencias
        this.venues = persistenciaVenues.cargarVenues();
        System.out.println("Venues cargados: " + venues.size());
        
        // Cargar usuarios
        this.usuarios = persistenciaUsuarios.cargarUsuarios();
        System.out.println("Usuarios cargados: " + usuarios.size());
        
        // Obtener solo los organizadores para cargar eventos
        ArrayList<Organizador> organizadores = obtenerOrganizadores();
        
        // Cargar eventos (necesita venues y organizadores)
        this.eventos = persistenciaEventos.cargarEventos(venues, organizadores);
        System.out.println("Eventos cargados: " + eventos.size());
        
        // Cargar procesos (necesita usuarios y eventos)
        this.procesos = persistenciaProcesos.cargarProcesos(usuarios, eventos);
        System.out.println("Procesos cargados: " + procesos.size());
        
        // Cargar solicitudes (necesita usuarios, eventos y venues)
        this.solicitudes = persistenciaSolicitudes.cargarSolicitudes(usuarios, eventos, venues);
        System.out.println("Solicitudes cargadas: " + solicitudes.size());

        // Obtener todos los tiquetes del sistema (de usuarios)
        ArrayList<Tiquete> todosLosTiquetes = obtenerTodosLosTiquetes();
        
        // Cargar reventas (necesita tiquetes y usuarios)
        this.reventas = persistenciaReventas.cargarReventas(todosLosTiquetes, usuarios);
        System.out.println("Reventas cargadas: " + reventas.size());
        
        // Cargar contraofertas (necesita reventas y usuarios)
        this.contraofertas = persistenciaContraofertas.cargarContraofertas(reventas, usuarios);
        System.out.println("Contraofertas cargadas: " + contraofertas.size());
        
        System.out.println("=== CARGA DE DATOS COMPLETADA ===");
    }
    
    /**
     * Guarda todos los datos del sistema en los archivos CSV
     */
    public void guardarTodosLosDatos() {
        System.out.println("=== GUARDANDO DATOS DEL SISTEMA ===");
        
        persistenciaUsuarios.guardarUsuarios(usuarios);
        persistenciaVenues.guardarVenues(venues);
        persistenciaEventos.guardarEventos(eventos);
        persistenciaProcesos.guardarProcesos(procesos);
        persistenciaSolicitudes.guardarSolicitudes(solicitudes);
        persistenciaReventas.guardarReventas(reventas);
        persistenciaContraofertas.guardarContraofertas(contraofertas);
        
        System.out.println("=== GUARDADO DE DATOS COMPLETADO ===");
    }
    
    /**
     * Obtiene solo los organizadores de la lista de usuarios
     */
    private ArrayList<Organizador> obtenerOrganizadores() {
        ArrayList<Organizador> organizadores = new ArrayList<>();
        for (Usuario usuario : usuarios) {
            if (usuario instanceof Organizador) {
                organizadores.add((Organizador) usuario);
            }
        }
        return organizadores;
    }

    /**
     * Obtiene todos los tiquetes del sistema (de compradores)
     */
    private ArrayList<Tiquete> obtenerTodosLosTiquetes() {
        ArrayList<Tiquete> todosLosTiquetes = new ArrayList<>();
        
        for (Usuario usuario : usuarios) {
            if (usuario instanceof Comprador) {
                Comprador comprador = (Comprador) usuario;
                todosLosTiquetes.addAll(comprador.getHistorialTiquetes());
            }
        }
        
        return todosLosTiquetes;
    }
    
    // ==================== MÉTODOS DE ACCESO A COLECCIONES ====================
    
    public ArrayList<Usuario> getUsuarios() {
        return new ArrayList<>(usuarios);
    }
    
    public ArrayList<Evento> getEventos() {
        return new ArrayList<>(eventos);
    }
    
    public ArrayList<Venue> getVenues() {
        return new ArrayList<>(venues);
    }
    
    public ArrayList<ProcesoEntreUsuarios> getProcesos() {
        return new ArrayList<>(procesos);
    }
    
    public ArrayList<Solicitud> getSolicitudes() {
        return new ArrayList<>(solicitudes);
    }
    
    public ArrayList<Solicitud> getSolicitudesPendientes() {
        return persistenciaSolicitudes.getSolicitudesPendientes(solicitudes);
    }

    public ArrayList<TiqueteReventa> getReventas() {
        return new ArrayList<>(reventas);
    }
    
    public ArrayList<Contraoferta> getContraofertas() {
        return new ArrayList<>(contraofertas);
    }

    public ArrayList<TiqueteReventa> getReventasActivas() {
        ArrayList<TiqueteReventa> activas = new ArrayList<>();
        for (TiqueteReventa reventa : reventas) {
            if (reventa.isActivo() && reventa.puedeSerRevendido()) {
                activas.add(reventa);
            }
        }
        return activas;
    }
    
    public ArrayList<Contraoferta> getContraofertasPendientes() {
        ArrayList<Contraoferta> pendientes = new ArrayList<>();
        for (Contraoferta contra : contraofertas) {
            if (contra.estaPendiente()) {
                pendientes.add(contra);
            }
        }
        return pendientes;
    }
    
    // ==================== MÉTODOS DE AGREGACIÓN ====================
    
    public void agregarUsuario(Usuario usuario) {
        if (usuario != null && !usuarios.contains(usuario)) {
            usuarios.add(usuario);
        }
    }
    
    public void agregarEvento(Evento evento) {
        if (evento != null && !eventos.contains(evento)) {
            eventos.add(evento);
        }
    }
    
    public void agregarVenue(Venue venue) {
        if (venue != null && !venues.contains(venue)) {
            venues.add(venue);
        }
    }
    
    public void agregarProceso(ProcesoEntreUsuarios proceso) {
        if (proceso != null && !procesos.contains(proceso)) {
            procesos.add(proceso);
        }
    }
    
    public void agregarSolicitud(Solicitud solicitud) {
        if (solicitud != null && !solicitudes.contains(solicitud)) {
            solicitudes.add(solicitud);
        }
    }

    public void agregarReventa(TiqueteReventa reventa) {
        if (reventa != null && !reventas.contains(reventa)) {
            reventas.add(reventa);
        }
    }
    
    public void agregarContraoferta(Contraoferta contraoferta) {
        if (contraoferta != null && !contraofertas.contains(contraoferta)) {
            contraofertas.add(contraoferta);
        }
    }
    
    // ==================== MÉTODOS DE BÚSQUEDA ====================
    
    public Usuario buscarUsuarioPorLogin(String login) {
        for (Usuario usuario : usuarios) {
            if (usuario.getLogin().equals(login)) {
                return usuario;
            }
        }
        return null;
    }
    
    public Evento buscarEventoPorId(String id) {
        for (Evento evento : eventos) {
            if (evento.getId().equals(id)) {
                return evento;
            }
        }
        return null;
    }
    
    public Venue buscarVenuePorId(String id) {
        for (Venue venue : venues) {
            if (venue.getId().equals(id)) {
                return venue;
            }
        }
        return null;
    }

    public TiqueteReventa buscarReventaPorId(String id) {
        for (TiqueteReventa reventa : reventas) {
            if (reventa.getId().equals(id)) {
                return reventa;
            }
        }
        return null;
    }
    
    public Contraoferta buscarContraofertaPorId(String id) {
        for (Contraoferta contra : contraofertas) {
            if (contra.getId().equals(id)) {
                return contra;
            }
        }
        return null;
    }
    
    public ArrayList<TiqueteReventa> getReventasPorVendedor(Usuario vendedor) {
        ArrayList<TiqueteReventa> reventasVendedor = new ArrayList<>();
        for (TiqueteReventa reventa : reventas) {
            if (reventa.getVendedor().equals(vendedor) && reventa.isActivo()) {
                reventasVendedor.add(reventa);
            }
        }
        return reventasVendedor;
    }
    
    public ArrayList<Contraoferta> getContraofertasPorComprador(Usuario comprador) {
        ArrayList<Contraoferta> contraofertasComprador = new ArrayList<>();
        for (Contraoferta contra : contraofertas) {
            if (contra.getComprador().equals(comprador)) {
                contraofertasComprador.add(contra);
            }
        }
        return contraofertasComprador;
    }
    
    public ArrayList<Contraoferta> getContraofertasPorReventa(TiqueteReventa reventa) {
        ArrayList<Contraoferta> contraofertasReventa = new ArrayList<>();
        for (Contraoferta contra : contraofertas) {
            if (contra.getTiqueteReventa().equals(reventa) && contra.estaPendiente()) {
                contraofertasReventa.add(contra);
            }
        }
        return contraofertasReventa;
    }
    
    public ArrayList<Comprador> getCompradores() {
        ArrayList<Comprador> compradores = new ArrayList<>();
        for (Usuario usuario : usuarios) {
            if (usuario instanceof Comprador) {
                compradores.add((Comprador) usuario);
            }
        }
        return compradores;
    }
    
    public ArrayList<Organizador> getOrganizadores() {
        ArrayList<Organizador> organizadores = new ArrayList<>();
        for (Usuario usuario : usuarios) {
            if (usuario instanceof Organizador) {
                organizadores.add((Organizador) usuario);
            }
        }
        return organizadores;
    }
    
    public ArrayList<Administrador> getAdministradores() {
        ArrayList<Administrador> administradores = new ArrayList<>();
        for (Usuario usuario : usuarios) {
            if (usuario instanceof Administrador) {
                administradores.add((Administrador) usuario);
            }
        }
        return administradores;
    }
    
    /**
     * Obtiene eventos aprobados y activos
     */
    public ArrayList<Evento> getEventosDisponibles() {
        ArrayList<Evento> disponibles = new ArrayList<>();
        for (Evento evento : eventos) {
            if (evento.estaActivo() && evento.hayTiquetesDisponibles()) {
                disponibles.add(evento);
            }
        }
        return disponibles;
    }
    
    /**
     * Obtiene venues aprobados
     */
    public ArrayList<Venue> getVenuesAprobados() {
        ArrayList<Venue> aprobados = new ArrayList<>();
        for (Venue venue : venues) {
            if (venue.isAprobado()) {
                aprobados.add(venue);
            }
        }
        return aprobados;
    }
    
    /**
     * Inicializa datos por defecto si no hay datos guardados
     */
    public void inicializarDatosPorDefecto() {
        if (usuarios.isEmpty() && eventos.isEmpty() && venues.isEmpty()) {
            System.out.println("Inicializando datos por defecto...");
        }
    }
}