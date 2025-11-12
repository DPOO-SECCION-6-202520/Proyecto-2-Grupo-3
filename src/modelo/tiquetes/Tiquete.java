package modelo.tiquetes;

import modelo.eventos.Evento;
import modelo.eventos.Localidad;
import java.util.Date;

/**
 * Clase base que representa un tiquete en el sistema.
 * Un tiquete tiene precio, fecha, identificación y puede ser transferible.
 */
public class Tiquete {
    protected String id;
    protected double precioBase;
    protected Date fechaHora;
    protected boolean transferible;
    protected Localidad localidad;
    protected Evento evento;
    protected boolean utilizado; // Para controlar si ya fue usado
    
    // Constructor de Tiquete
    public Tiquete(String id, double precioBase, Date fechaHora, Localidad localidad, Evento evento) {
        this.id = id; // identificador único del tiquete
        this.precioBase = precioBase;// precio base sin cargos adicionales
        this.fechaHora = fechaHora; // fecha y hora del evento
        this.localidad = localidad;// localidad a la que pertenece
        this.evento = evento; //evento al que pertenece
        this.transferible = true; // Por defecto es transferible
        this.utilizado = false; // Por defecto no utilizado
    }

    //constructor vacío para persistencia
    public Tiquete() {
        this.transferible = true;
        this.utilizado = false;
    }
    //Setters para persistencia
     public void setId(String id) { this.id = id; }
    public void setPrecioBase(double precioBase) { this.precioBase = precioBase; }
    public void setFechaHora(Date fechaHora) { this.fechaHora = fechaHora; }
    public void setLocalidad(Localidad localidad) { this.localidad = localidad; }
    public void setEvento(Evento evento) { this.evento = evento; }
    public void setUtilizado(boolean utilizado) { this.utilizado = utilizado; }
    
    // ==================== MÉTODOS GETTER ====================
    
    public String getId() { return id; }

    public double getPrecioBase() { return precioBase; }

    public Date getFechaHora() { return fechaHora; }

    public boolean getEsTransferible() { return transferible; }

    public Localidad getLocalidad() { return localidad; }

    public Evento getEvento() { return evento; }

    public boolean isUtilizado() { return utilizado; }
    
    // ==================== MÉTODOS SETTER ====================
    
    public void setTransferible(boolean transferible) {
        this.transferible = transferible;
    }
    
    public void marcarComoUtilizado() {
        this.utilizado = true;
        System.out.println("Tiquete " + id + " marcado como utilizado.");
    }
    
    // ==================== MÉTODOS DE VALIDACIÓN ====================
    
    /**
     * Verifica si el tiquete está vigente (no vencido y no utilizado)
     * true si el tiquete está vigente
     */
    public boolean estaVigente() {
        Date ahora = new Date();
        return !utilizado && fechaHora.after(ahora);
    }
    
    /**
     * Verifica si el tiquete puede ser transferido
     * true si es transferible y está vigente
     */
    public boolean puedeSerTransferido() {
        return transferible && estaVigente();
    }
    
    @Override
    public String toString() {
        return "Tiquete{" +
                "id='" + id + '\'' +
                ", precioBase=" + precioBase +
                ", evento=" + (evento != null ? evento.getNombre() : "N/A") +
                ", localidad=" + (localidad != null ? localidad.getTipoLocalidad() : "N/A") +
                ", transferible=" + transferible +
                ", vigente=" + estaVigente() +
                '}';
    }
}