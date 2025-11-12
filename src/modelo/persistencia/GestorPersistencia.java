package modelo.persistencia;

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
    
    // Colecciones en memoria
    private ArrayList<Usuario> usuarios;
    private ArrayList<Evento> eventos;
    private ArrayList<Venue> venues;
    private ArrayList<ProcesoEntreUsuarios> procesos;
    private ArrayList<Solicitud> solicitudes;
    
    public GestorPersistencia() {
        // Inicializar persistencias
        this.persistenciaUsuarios = new PersistenciaUsuarios();
        this.persistenciaEventos = new PersistenciaEventos();
        this.persistenciaVenues = new PersistenciaVenues();
        this.persistenciaProcesos = new PersistenciaProcesosEntreUsuarios();
        this.persistenciaSolicitudes = new PersistenciaSolicitudes();
        
        // Inicializar colecciones
        this.usuarios = new ArrayList<>();
        this.eventos = new ArrayList<>();
        this.venues = new ArrayList<>();
        this.procesos = new ArrayList<>();
        this.solicitudes = new ArrayList<>();
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
            // Aquí podrías llamar al método de inicialización de datos de prueba
            // que ya tienes en Aplicacion.java
        }
    }
}