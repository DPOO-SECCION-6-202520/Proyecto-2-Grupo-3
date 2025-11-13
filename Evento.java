package modelo.eventos;

import modelo.usuarios.Organizador;
import modelo.tiquetes.Tiquete;
import java.util.ArrayList;
import java.util.Date;

/**
 * Clase que representa un evento en el sistema.
 * Un evento tiene fecha, nombre, venue, organizador y tiquetes asociados.
 */
public class Evento {
    private String id;
    private Date fechaHora;
    private String nombre;
    private Venue venue;
    private Organizador organizador;
    private ArrayList<Tiquete> tiquetes;
    private boolean aprobado; // Para control de aprobación por administrador
    private boolean cancelado; // Para control de cancelación
    private ArrayList<Oferta> ofertas = new ArrayList<>();
    
    //Constructor de Evento
    public Evento(String id, String nombre, Date fechaHora, Venue venue, Organizador organizador) {
        this.id = id; // identificador único del evento
        this.nombre = nombre; // nombre del evento
        this.fechaHora = fechaHora; // fecha y hora del evento
        this.venue = venue; // venue donde se realiza el evento
        this.organizador = organizador; // organizador que crea el evento
        this.tiquetes = new ArrayList<>();
        this.aprobado = false; // Por defecto no está aprobado
        this.cancelado = false; // Por defecto no está cancelado
    }

    //constructor vacío para persistencia
    public Evento() {
        this.tiquetes = new ArrayList<>();
        this.ofertas = new ArrayList<>();
        this.aprobado = false;
        this.cancelado = false;
    }

    //Setters nuevos para persistencia
    public void setId(String id) { this.id = id; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public void setFechaHora(Date fechaHora) { this.fechaHora = fechaHora; }
    public void setVenue(Venue venue) { this.venue = venue; }
    public void setOrganizador(Organizador organizador) { this.organizador = organizador; }
    public void setAprobado(boolean aprobado) { this.aprobado = aprobado; }
    public void setCancelado(boolean cancelado) { this.cancelado = cancelado; }
    public void setTiquetes(ArrayList<Tiquete> tiquetes) { this.tiquetes = tiquetes; }
    public void setOfertas(ArrayList<Oferta> ofertas) { this.ofertas = ofertas; }
    
    // ==================== MÉTODOS GETTER ====================
    
    public String getId() {
        return id; 
    }

    public Date getFechaHora() {
        return fechaHora; 
    }

    public String getNombre() {
        return nombre; 
    }

    public Venue getVenue() { 
        return venue; 
    }

    public Organizador getOrganizador() { 
        return organizador; 
    }

    public boolean isAprobado() { 
        return aprobado; 
    }
    
    public boolean isCancelado() {
        return cancelado;
    }
    
    // devuelve lista de tiquetes del evento
    public ArrayList<Tiquete> getTiquetes() {
        return new ArrayList<>(tiquetes);
    }
    
    // ==================== MÉTODOS DE ESTADO ====================
    
    /**
     * Aprueba el evento (solo puede hacerlo el administrador)
     */
    public void aprobar() {
        this.aprobado = true;
        this.cancelado = false;
        System.out.println("Evento '" + nombre + "' ha sido aprobado.");
    }
    
    /**
     * Rechaza el evento
     */
    public void rechazar() {
        this.aprobado = false;
        System.out.println("Evento '" + nombre + "' ha sido rechazado.");
    }
    
    /**
     * Cancela el evento
     */
    public void cancelar() {
        this.cancelado = true;
        System.out.println("Evento '" + nombre + "' ha sido cancelado.");
    }
    
    /**
     * Verifica si el evento está activo (aprobado y no cancelado)
     */
    public boolean estaActivo() {
        return aprobado && !cancelado;
    }
    
    /**
     * Verifica si el evento ya pasó (fecha vencida)
     */
    public boolean estaVencido() {
        Date ahora = new Date();
        return fechaHora.before(ahora);
    }
    
    // ==================== MÉTODOS DE GESTIÓN DE TIQUETES ====================
    
    /**
     * Agrega un tiquete al evento
     * @param tiquete - tiquete a agregar
     */
    public void agregarTiquete(Tiquete tiquete) {
        if (tiquete != null && !tiquetes.contains(tiquete)) {
            tiquetes.add(tiquete);
            System.out.println("Tiquete " + tiquete.getId() + " agregado al evento '" + nombre + "'");
        }
    }
    
    /**
     * @return cantidad de tiquetes disponibles (no vendidos y vigentes)
     */
    public int getTiquetesDisponibles() {
        int disponibles = 0;
        for (Tiquete tiquete : tiquetes) {
            if (tiquete.estaVigente()) {
                disponibles++;
            }
        }
        return disponibles;
    }
    
    /**
     * @return cantidad de tiquetes vendidos (no vigentes)
     */
    public int getTiquetesVendidos() {
        int vendidos = 0;
        for (Tiquete tiquete : tiquetes) {
            if (!tiquete.estaVigente()) {
                vendidos++;
            }
        }
        return vendidos;
    }
    
    /**
     * @return porcentaje de tiquetes vendidos (0-100)
     */
    public double getPorcentajeVendido() {
        int total = tiquetes.size();
        if (total == 0) return 0;
        return (getTiquetesVendidos() * 100.0) / total;
    }
    
    /**
     * Obtiene tiquetes disponibles para una localidad específica
     * @param localidad - localidad a filtrar
     * @return lista de tiquetes disponibles en la localidad
     */
    public ArrayList<Tiquete> getTiquetesDisponiblesPorLocalidad(modelo.eventos.Localidad localidad) {
        ArrayList<Tiquete> disponibles = new ArrayList<>();
        for (Tiquete tiquete : tiquetes) {
            if (tiquete.estaVigente() && tiquete.getLocalidad().equals(localidad)) {
                disponibles.add(tiquete);
            }
        }
        return disponibles;
    }
    
    /**
     * Verifica si hay tiquetes disponibles
     */
    public boolean hayTiquetesDisponibles() {
        return getTiquetesDisponibles() > 0;
    }
    
    /**
     * Verifica si hay tiquetes disponibles en una localidad específica
     */
    public boolean hayTiquetesDisponiblesEnLocalidad(modelo.eventos.Localidad localidad) {
        return getTiquetesDisponiblesPorLocalidad(localidad).size() > 0;
    }

    public void agregarOferta(Oferta oferta) {
    if (oferta != null && !ofertas.contains(oferta)) {
        ofertas.add(oferta);
        System.out.println("Oferta agregada al evento: " + oferta.getDescripcion());
    }
}

public ArrayList<Oferta> getOfertas() {
    return new ArrayList<>(ofertas);
}

public ArrayList<Oferta> getOfertasVigentes() {
    ArrayList<Oferta> vigentes = new ArrayList<>();
    for (Oferta oferta : ofertas) {
        if (oferta.estaVigente()) {
            vigentes.add(oferta);
        }
    }
    return vigentes;
}

public boolean tieneOfertasVigentes() {
    return !getOfertasVigentes().isEmpty();
}
    
    // ==================== MÉTODOS DE INFORMACIÓN ====================
    
    /**
     * Obtiene información detallada del evento
     */
    public String getInformacionDetallada() {
        StringBuilder info = new StringBuilder();
        info.append("=== INFORMACIÓN DEL EVENTO ===\n");
        info.append("Nombre: ").append(nombre).append("\n");
        info.append("ID: ").append(id).append("\n");
        info.append("Fecha: ").append(fechaHora).append("\n");
        info.append("Venue: ").append(venue.getNombre()).append("\n");
        info.append("Organizador: ").append(organizador.getLogin()).append("\n");
        info.append("Estado: ").append(getEstado()).append("\n");
        info.append("Tiquetes totales: ").append(tiquetes.size()).append("\n");
        info.append("Tiquetes disponibles: ").append(getTiquetesDisponibles()).append("\n");
        info.append("Tiquetes vendidos: ").append(getTiquetesVendidos()).append("\n");
        info.append("Porcentaje vendido: ").append(String.format("%.1f", getPorcentajeVendido())).append("%\n");
        
        return info.toString();
    }
    
    /**
     * @return estado descriptivo del evento
     */
    public String getEstado() {
        if (cancelado) return "CANCELADO";
        if (!aprobado) return "PENDIENTE DE APROBACIÓN";
        if (estaVencido()) return "FINALIZADO";
        if (hayTiquetesDisponibles()) return "DISPONIBLE";
        return "AGOTADO";
    }
    
    @Override
    public String toString() {
        return "Evento{" +
                "id='" + id + '\'' +
                ", nombre='" + nombre + '\'' +
                ", fechaHora=" + fechaHora +
                ", estado=" + getEstado() +
                ", tiquetesDisponibles=" + getTiquetesDisponibles() +
                ", porcentajeVendido=" + String.format("%.1f", getPorcentajeVendido()) + "%" +
                '}';
    }
}