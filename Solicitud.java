package modelo.persistencia;

import modelo.eventos.Venue;
import modelo.usuarios.Usuario;
import modelo.tiquetes.Tiquete;
import modelo.eventos.Evento;
import java.util.Date;

/**
 * Clase que representa una solicitud pendiente en el sistema
 */
public class Solicitud {
    public enum TipoSolicitud {
        APROBACION_EVENTO,
        CANCELACION_EVENTO,
        REEMBOLSO_TIQUETE,
        APROBACION_VENUE,
        TRANSFERENCIA_TIQUETE
    }
    
    private String id;
    private TipoSolicitud tipo;
    private Date fechaSolicitud;
    private Usuario solicitante;
    private String descripcion;
    private String estado; // "pendiente", "aprobada", "rechazada"
    private String respuesta; // Respuesta del administrador
    private Date fechaRespuesta;
    private Usuario administrador; // Quien resolvió la solicitud
    
    // Datos específicos según el tipo de solicitud
    private Evento evento;
    private Tiquete tiquete;
    private Venue venue;
    private double montoReembolso;
    
    // Constructor
    public Solicitud(String id, TipoSolicitud tipo, Date fechaSolicitud, Usuario solicitante, String descripcion) {
        this.id = id;
        this.tipo = tipo;
        this.fechaSolicitud = fechaSolicitud;
        this.solicitante = solicitante;
        this.descripcion = descripcion;
        this.estado = "pendiente";
    }
    
    // Getters y Setters
    public String getId() { return id; }
    public TipoSolicitud getTipo() { return tipo; }
    public Date getFechaSolicitud() { return fechaSolicitud; }
    public Usuario getSolicitante() { return solicitante; }
    public String getDescripcion() { return descripcion; }
    public String getEstado() { return estado; }
    public String getRespuesta() { return respuesta; }
    public Date getFechaRespuesta() { return fechaRespuesta; }
    public Usuario getAdministrador() { return administrador; }
    public Evento getEvento() { return evento; }
    public Tiquete getTiquete() { return tiquete; }
    public Venue getVenue() { return venue; }
    public double getMontoReembolso() { return montoReembolso; }
    
    public void setEstado(String estado) { this.estado = estado; }
    public void setRespuesta(String respuesta) { this.respuesta = respuesta; }
    public void setFechaRespuesta(Date fechaRespuesta) { this.fechaRespuesta = fechaRespuesta; }
    public void setAdministrador(Usuario administrador) { this.administrador = administrador; }
    public void setEvento(Evento evento) { this.evento = evento; }
    public void setTiquete(Tiquete tiquete) { this.tiquete = tiquete; }
    public void setVenue(Venue venue) { this.venue = venue; }
    public void setMontoReembolso(double montoReembolso) { this.montoReembolso = montoReembolso; }
    
    /**
     * Resuelve la solicitud (aprueba o rechaza)
     */
    public void resolver(Usuario administrador, String respuesta, boolean aprobada) {
        this.administrador = administrador;
        this.respuesta = respuesta;
        this.estado = aprobada ? "aprobada" : "rechazada";
        this.fechaRespuesta = new Date();
    }
    
    /**
     * Verifica si la solicitud está pendiente
     */
    public boolean estaPendiente() {
        return "pendiente".equals(estado);
    }
    
    @Override
    public String toString() {
        return "Solicitud{" +
                "id='" + id + '\'' +
                ", tipo=" + tipo +
                ", estado='" + estado + '\'' +
                ", solicitante=" + (solicitante != null ? solicitante.getLogin() : "N/A") +
                ", fechaSolicitud=" + fechaSolicitud +
                '}';
    }
}