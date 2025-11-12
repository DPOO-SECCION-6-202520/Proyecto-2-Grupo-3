package modelo.persistencia;

import modelo.usuarios.Usuario;
import modelo.tiquetes.Tiquete;
import modelo.eventos.Evento;
import java.util.Date;
import java.util.ArrayList;

/**
 * Clase que representa un proceso entre usuarios
 */
public class ProcesoEntreUsuarios {
    public enum TipoProceso {
        TRANSFERENCIA_TIQUETE,
        COMPRA_TIQUETE,
        CANCELACION_EVENTO,
        SOLICITUD_REEMBOLSO,
        AUTORIZACION_REEMBOLSO
    }
    
    private String id;
    private TipoProceso tipo;
    private Date fecha;
    private Usuario usuarioOrigen;
    private Usuario usuarioDestino;
    private ArrayList<Tiquete> tiquetes;
    private Evento evento;
    private double monto;
    private String estado; // "pendiente", "aprobado", "rechazado", "completado"
    private String descripcion;
    
    // Constructor
    public ProcesoEntreUsuarios(String id, TipoProceso tipo, Date fecha, Usuario usuarioOrigen) {
        this.id = id;
        this.tipo = tipo;
        this.fecha = fecha;
        this.usuarioOrigen = usuarioOrigen;
        this.tiquetes = new ArrayList<>();
        this.estado = "pendiente";
    }
    
    // Getters y Setters
    public String getId() { return id; }
    public TipoProceso getTipo() { return tipo; }
    public Date getFecha() { return fecha; }
    public Usuario getUsuarioOrigen() { return usuarioOrigen; }
    public Usuario getUsuarioDestino() { return usuarioDestino; }
    public ArrayList<Tiquete> getTiquetes() { return tiquetes; }
    public Evento getEvento() { return evento; }
    public double getMonto() { return monto; }
    public String getEstado() { return estado; }
    public String getDescripcion() { return descripcion; }
    
    public void setUsuarioDestino(Usuario usuarioDestino) { this.usuarioDestino = usuarioDestino; }
    public void setEvento(Evento evento) { this.evento = evento; }
    public void setMonto(double monto) { this.monto = monto; }
    public void setEstado(String estado) { this.estado = estado; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    
    public void agregarTiquete(Tiquete tiquete) {
        if (tiquete != null) {
            this.tiquetes.add(tiquete);
        }
    }
    
    @Override
    public String toString() {
        return "ProcesoEntreUsuarios{" +
                "id='" + id + '\'' +
                ", tipo=" + tipo +
                ", fecha=" + fecha +
                ", usuarioOrigen=" + (usuarioOrigen != null ? usuarioOrigen.getLogin() : "N/A") +
                ", usuarioDestino=" + (usuarioDestino != null ? usuarioDestino.getLogin() : "N/A") +
                ", tiquetes=" + tiquetes.size() +
                ", estado='" + estado + '\'' +
                '}';
    }
}