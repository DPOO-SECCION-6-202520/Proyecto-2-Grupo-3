package modelo.pagos;

import modelo.usuarios.Usuario;
import modelo.tiquetes.Tiquete;
import java.util.ArrayList;
import java.util.Date;

/**
 * Clase que representa una compra en el sistema.
 * Contiene información sobre tiquetes, monto, estado y comprador.
 */
public class Compra {
    private String id;
    private Date fecha;
    private double montoTotal;
    private String estado; // "pendiente", "aprobada", "rechazada", "reembolsada"
    private ArrayList<Tiquete> tiquetes;
    private Usuario comprador;
    
    /**
     * Constructor de Compra
     */
    public Compra(String id, Date fecha, double montoTotal, ArrayList<Tiquete> tiquetes, Usuario comprador) {
        this.id = id;
        this.fecha = fecha;
        this.montoTotal = montoTotal;
        this.tiquetes = new ArrayList<>(tiquetes);
        this.comprador = comprador;
        this.estado = "pendiente"; // Estado inicial
    }
    
    // ==================== MÉTODOS GETTER ====================
    
    public String getId() { return id; }
    public Date getFecha() { return fecha; }
    public double getMontoTotal() { return montoTotal; }
    public String getEstado() { return estado; }
    public ArrayList<Tiquete> getTiquetes() { return new ArrayList<>(tiquetes); }
    public Usuario getComprador() { return comprador; }
    
    // ==================== MÉTODOS SETTER ====================
    
    public void setEstado(String estado) {
        this.estado = estado;
    }
    
    // ==================== MÉTODOS DE UTILIDAD ====================
    
    /**
     * @return true si la compra está aprobada
     */
    public boolean estaAprobada() {
        return "aprobada".equals(estado);
    }
    
    /**
     * @return true si la compra puede ser reembolsada
     */
    public boolean puedeSerReembolsada() {
        return estaAprobada() && !"reembolsada".equals(estado);
    }
    
    @Override
    public String toString() {
        return "Compra{" +
                "id='" + id + '\'' +
                ", fecha=" + fecha +
                ", montoTotal=" + montoTotal +
                ", estado='" + estado + '\'' +
                ", tiquetes=" + tiquetes.size() +
                ", comprador=" + comprador.getLogin() +
                '}';
    }
}