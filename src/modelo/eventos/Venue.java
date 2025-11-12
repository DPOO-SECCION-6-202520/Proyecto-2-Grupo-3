package modelo.eventos;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Calendar;

/**
 * Clase que representa un venue (escenario) donde se realizan eventos.
 * Un venue tiene nombre, ubicación, capacidad, restricciones y localidades.
 */
public class Venue {
    private String id;
    private String nombre;
    private String ubicacion;
    private int capacidad;
    private ArrayList<String> restricciones;
    private ArrayList<Localidad> localidades;
    private boolean aprobado; // Para venues sugeridos por organizadores
    private HashMap<Date, Evento> eventosProgramados; // Control de eventos por fecha
    
    /**
     * Constructor de Venue
     * @param id - identificador único
     * @param nombre - nombre del venue
     * @param ubicacion - ubicación física
     * @param capacidad - capacidad máxima de personas
     */
    public Venue(String id, String nombre, String ubicacion, int capacidad) {
        this.id = id;
        this.nombre = nombre;
        this.ubicacion = ubicacion;
        this.capacidad = capacidad;
        this.restricciones = new ArrayList<>();
        this.localidades = new ArrayList<>(); // INICIALIZAR LOCALIDADES
        this.aprobado = false; // Por defecto no está aprobado
        this.eventosProgramados = new HashMap<>();
    }

    // Constructor vacío para persistencia
    public Venue() {
        this.restricciones = new ArrayList<>();
        this.localidades = new ArrayList<>();
        this.eventosProgramados = new HashMap<>();
        this.aprobado = false;
    }

    // Setters nuevos para persistencia
    public void setId(String id) { this.id = id; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public void setUbicacion(String ubicacion) { this.ubicacion = ubicacion; }
    public void setCapacidad(int capacidad) { this.capacidad = capacidad; }
    public void setAprobado(boolean aprobado) { this.aprobado = aprobado; }
    public void setRestricciones(ArrayList<String> restricciones) { this.restricciones = restricciones; }
    public void setLocalidades(ArrayList<Localidad> localidades) { this.localidades = localidades; }
    
    // ==================== MÉTODOS GETTER ====================
    
    public String getId() { return id; }
    public String getNombre() { return nombre; }
    public String getUbicacion() { return ubicacion; }
    public int getCapacidad() { return capacidad; }
    public boolean isAprobado() { return aprobado; }
    
    /**
     * @return lista de restricciones del venue
     */
    public ArrayList<String> getRestricciones() {
        return new ArrayList<>(restricciones);
    }
    
    /**
     * @return lista de eventos programados en este venue
     */
    public ArrayList<Evento> getEventosProgramados() {
        return new ArrayList<>(eventosProgramados.values());
    }
    
    // ==================== MÉTODOS DE LOCALIDADES ====================
    
    /**
     * @return lista de localidades del venue
     */
    public ArrayList<Localidad> getLocalidades() {
        return new ArrayList<>(localidades);
    }
    
    /**
     * Agrega una localidad al venue
     * @param localidad - localidad a agregar
     */
    public void agregarLocalidad(Localidad localidad) {
        if (localidad != null && !localidades.contains(localidad)) {
            localidades.add(localidad);
            System.out.println("Localidad " + localidad.getTipoLocalidad() + " agregada al venue " + nombre);
        }
    }
    
    /**
     * Crea y agrega una nueva localidad al venue
     * @param id - identificador de la localidad
     * @param tipoLocalidad - tipo de localidad
     * @param numerada - si es numerada
     * @param capacidad - capacidad de la localidad
     * @param precioBase - precio base
     * @return la localidad creada
     */
    public Localidad crearLocalidad(String id, String tipoLocalidad, boolean numerada, 
                                   int capacidad, double precioBase) {
        Localidad nuevaLocalidad = new Localidad(id, tipoLocalidad, numerada, this, capacidad, precioBase);
        agregarLocalidad(nuevaLocalidad);
        return nuevaLocalidad;
    }
    
    /**
     * Obtiene una localidad por su tipo
     * @param tipoLocalidad - tipo de localidad a buscar
     * @return la localidad encontrada o null
     */
    public Localidad getLocalidadPorTipo(String tipoLocalidad) {
        for (Localidad localidad : localidades) {
            if (localidad.getTipoLocalidad().equals(tipoLocalidad)) {
                return localidad;
            }
        }
        return null;
    }
    
    /**
     * Verifica si el venue tiene localidades numeradas
     * @return true si tiene al menos una localidad numerada
     */
    public boolean tieneLocalidadesNumeradas() {
        for (Localidad localidad : localidades) {
            if (localidad.isNumerada()) {
                return true;
            }
        }
        return false;
    }
    
    // ==================== MÉTODOS DE GESTIÓN ====================
    
    /**
     * Aprueba el venue (solo administrador)
     */
    public void aprobar() {
        this.aprobado = true;
        System.out.println("Venue '" + nombre + "' ha sido aprobado.");
    }
    
    /**
     * Rechaza el venue
     */
    public void rechazar() {
        this.aprobado = false;
        System.out.println("Venue '" + nombre + "' ha sido rechazado.");
    }
    
    /**
     * Agrega una restricción al venue
     * @param restriccion - restricción a agregar
     */
    public void agregarRestriccion(String restriccion) {
        if (restriccion != null && !restriccion.trim().isEmpty() && !restricciones.contains(restriccion)) {
            restricciones.add(restriccion);
            System.out.println("Restricción '" + restriccion + "' agregada al venue '" + nombre + "'");
        }
    }
    
    /**
     * Elimina una restricción del venue
     * @param restriccion - restricción a eliminar
     */
    public void eliminarRestriccion(String restriccion) {
        if (restricciones.contains(restriccion)) {
            restricciones.remove(restriccion);
            System.out.println("Restricción '" + restriccion + "' eliminada del venue '" + nombre + "'");
        }
    }
    
    // ==================== MÉTODOS DE DISPONIBILIDAD ====================
    
    /**
     * Verifica si el venue está disponible para una fecha/hora
     * @param fechaHora - fecha y hora a verificar
     * @return true si está disponible
     */
    public boolean estaDisponible(Date fechaHora) {
        if (fechaHora == null) {
            return false;
        }
        
        // Verificar si ya hay un evento programado para esa fecha/hora
        for (Date fechaProgramada : eventosProgramados.keySet()) {
            if (esMismaFecha(fechaHora, fechaProgramada)) {
                System.out.println("Venue '" + nombre + "' no disponible para " + fechaHora + 
                                 " - Ya tiene evento programado");
                return false;
            }
        }
        
        System.out.println("Venue '" + nombre + "' disponible para " + fechaHora);
        return true;
    }
    
    /**
     * Programa un evento en el venue para una fecha/hora específica
     * @param evento - evento a programar
     * @param fechaHora - fecha y hora del evento
     * @return true si se pudo programar
     */
    public boolean programarEvento(Evento evento, Date fechaHora) {
        if (evento == null || fechaHora == null) {
            System.out.println("Error: Datos inválidos para programar evento");
            return false;
        }
        
        if (!estaDisponible(fechaHora)) {
            System.out.println("Error: No se puede programar evento - Venue no disponible");
            return false;
        }
        
        if (!aprobado) {
            System.out.println("Error: No se puede programar evento - Venue no aprobado");
            return false;
        }
        
        eventosProgramados.put(fechaHora, evento);
        System.out.println("Evento '" + evento.getNombre() + "' programado en venue '" + 
                         nombre + "' para " + fechaHora);
        return true;
    }
    
    /**
     * Cancela un evento programado en el venue
     * @param fechaHora - fecha y hora del evento a cancelar
     */
    public void cancelarEventoProgramado(Date fechaHora) {
        if (eventosProgramados.containsKey(fechaHora)) {
            Evento evento = eventosProgramados.remove(fechaHora);
            System.out.println("Evento '" + evento.getNombre() + "' cancelado en venue '" + 
                             nombre + "' para " + fechaHora);
        } else {
            System.out.println("No hay evento programado para la fecha " + fechaHora + " en venue '" + nombre + "'");
        }
    }
    
    /**
     * Obtiene el evento programado para una fecha/hora específica
     * @param fechaHora - fecha y hora a consultar
     * @return evento programado o null si no hay
     */
    public Evento getEventoParaFecha(Date fechaHora) {
        for (Date fechaProgramada : eventosProgramados.keySet()) {
            if (esMismaFecha(fechaHora, fechaProgramada)) {
                return eventosProgramados.get(fechaProgramada);
            }
        }
        return null;
    }
    
    /**
     * Verifica si dos fechas son el mismo día (ignora hora)
     * @param fechaHora1 - primera fecha/hora a comparar
     * @param fechaHora2 - segunda fecha/hora a comparar
     * @return true si son el mismo día
     */
    private boolean esMismaFecha(Date fechaHora1, Date fechaHora2) {
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal1.setTime(fechaHora1);
        cal2.setTime(fechaHora2);
        
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
               cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH) &&
               cal1.get(Calendar.DAY_OF_MONTH) == cal2.get(Calendar.DAY_OF_MONTH);
    }
    
    // ==================== MÉTODOS DE INFORMACIÓN ====================
    
    /**
     * Obtiene información detallada del venue
     */
    public String getInformacionDetallada() {
        StringBuilder info = new StringBuilder();
        info.append("=== INFORMACIÓN DEL VENUE ===\n");
        info.append("Nombre: ").append(nombre).append("\n");
        info.append("ID: ").append(id).append("\n");
        info.append("Ubicación: ").append(ubicacion).append("\n");
        info.append("Capacidad: ").append(capacidad).append(" personas\n");
        info.append("Estado: ").append(aprobado ? "APROBADO" : "PENDIENTE").append("\n");
        info.append("Eventos programados: ").append(eventosProgramados.size()).append("\n");
        info.append("Localidades: ").append(localidades.size()).append("\n");
        info.append("Restricciones: ").append(restricciones.size()).append("\n");
        
        if (!localidades.isEmpty()) {
            info.append("Lista de localidades:\n");
            for (Localidad localidad : localidades) {
                info.append("  - ").append(localidad.getTipoLocalidad())
                    .append(" (Capacidad: ").append(localidad.getCapacidad())
                    .append(", Precio: $").append(localidad.getPrecioBase())
                    .append(", Numerada: ").append(localidad.isNumerada() ? "SÍ" : "NO")
                    .append(")\n");
            }
        }
        
        if (!restricciones.isEmpty()) {
            info.append("Lista de restricciones:\n");
            for (String restriccion : restricciones) {
                info.append("  - ").append(restriccion).append("\n");
            }
        }
        
        if (!eventosProgramados.isEmpty()) {
            info.append("Próximos eventos:\n");
            for (Date fechaHora : eventosProgramados.keySet()) {
                Evento evento = eventosProgramados.get(fechaHora);
                info.append("  - ").append(evento.getNombre()).append(" (").append(fechaHora).append(")\n");
            }
        }
        
        return info.toString();
    }
    
    /**
     * @return capacidad disponible (podría calcularse basado en eventos programados)
     */
    public int getCapacidadDisponible() {
        // En una implementación más avanzada, esto consideraría los eventos programados
        return capacidad;
    }
    
    /**
     * Calcula la capacidad total de todas las localidades
     */
    public int getCapacidadTotalLocalidades() {
        int capacidadTotal = 0;
        for (Localidad localidad : localidades) {
            capacidadTotal += localidad.getCapacidad();
        }
        return capacidadTotal;
    }
    
    @Override
    public String toString() {
        return "Venue{" +
                "id='" + id + '\'' +
                ", nombre='" + nombre + '\'' +
                ", ubicacion='" + ubicacion + '\'' +
                ", capacidad=" + capacidad +
                ", localidades=" + localidades.size() +
                ", aprobado=" + aprobado +
                ", eventosProgramados=" + eventosProgramados.size() +
                ", restricciones=" + restricciones.size() +
                '}';
    }
}