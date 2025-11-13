package modelo.tiquetes;

import modelo.usuarios.Usuario;
import java.util.Date;

/**
 * Clase que representa una contraoferta para un tiquete en reventa
 */
public class Contraoferta {
    private String id;
    private TiqueteReventa tiqueteReventa;
    private Usuario comprador;
    private double precioOfertado;
    private Date fechaOferta;
    private String estado; // "pendiente", "aceptada", "rechazada"
    
    public Contraoferta(String id, TiqueteReventa tiqueteReventa, Usuario comprador, double precioOfertado) {
        this.id = id;
        this.tiqueteReventa = tiqueteReventa;
        this.comprador = comprador;
        this.precioOfertado = precioOfertado;
        this.fechaOferta = new Date();
        this.estado = "pendiente";
    }
    
    // Getters y Setters
    public String getId() { return id; }
    public TiqueteReventa getTiqueteReventa() { return tiqueteReventa; }
    public Usuario getComprador() { return comprador; }
    public double getPrecioOfertado() { return precioOfertado; }
    public Date getFechaOferta() { return fechaOferta; }
    public String getEstado() { return estado; }
    
    public void setEstado(String estado) { this.estado = estado; }
    
    /**
     * Acepta la contraoferta
     */
    public void aceptar() {
        this.estado = "aceptada";
    }
    
    /**
     * Rechaza la contraoferta
     */
    public void rechazar() {
        this.estado = "rechazada";
    }
    
    public boolean estaPendiente() {
        return "pendiente".equals(estado);
    }
    
    @Override
    public String toString() {
        return "Contraoferta{" +
                "id='" + id + '\'' +
                ", tiquete=" + tiqueteReventa.getTiquete().getId() +
                ", comprador=" + comprador.getLogin() +
                ", precioOfertado=" + precioOfertado +
                ", estado='" + estado + '\'' +
                '}';
    }
}