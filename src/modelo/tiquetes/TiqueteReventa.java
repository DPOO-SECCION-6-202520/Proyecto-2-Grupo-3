package modelo.tiquetes;

import modelo.usuarios.Usuario;
import java.util.Date;

/**
 * Clase que representa un tiquete en reventa
 */
public class TiqueteReventa {
    private String id;
    private Tiquete tiquete;
    private Usuario vendedor;
    private double precioReventa;
    private Date fechaPublicacion;
    private boolean activo;
    
    public TiqueteReventa(String id, Tiquete tiquete, Usuario vendedor, double precioReventa) {
        this.id = id;
        this.tiquete = tiquete;
        this.vendedor = vendedor;
        this.precioReventa = precioReventa;
        this.fechaPublicacion = new Date();
        this.activo = true;
    }
    
    // Getters y Setters
    public String getId() { return id; }
    public Tiquete getTiquete() { return tiquete; }
    public Usuario getVendedor() { return vendedor; }
    public double getPrecioReventa() { return precioReventa; }
    public Date getFechaPublicacion() { return fechaPublicacion; }
    public boolean isActivo() { return activo; }
    
    public void setPrecioReventa(double precioReventa) { this.precioReventa = precioReventa; }
    public void setActivo(boolean activo) { this.activo = activo; }
    
    /**
     * Verifica si el tiquete puede ser revendido
     */
    public boolean puedeSerRevendido() {
        return tiquete.puedeSerTransferido() && 
               !(tiquete instanceof Deluxe) && // Los Deluxe no se pueden revender
               activo;
    }
    
    @Override
    public String toString() {
        return "TiqueteReventa{" +
                "id='" + id + '\'' +
                ", tiquete=" + tiquete.getId() +
                ", evento=" + tiquete.getEvento().getNombre() +
                ", precioReventa=" + precioReventa +
                ", activo=" + activo +
                '}';
    }
}