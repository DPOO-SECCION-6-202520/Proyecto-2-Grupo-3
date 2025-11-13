package modelo.eventos;

import java.util.Date;

/**
 * Clase que representa una oferta especial para eventos o localidades.
 * Las ofertas son creadas por organizadores y tienen fecha de expiración.
 */
public class Oferta {
    private String id;
    private String descripcion;
    private double porcentajeDescuento; // Ej: 0.2 para 20% de descuento
    private Date fechaInicio;
    private Date fechaExpiracion;
    private Evento evento;
    private Localidad localidad; // Opcional: si es específica para localidad
    private boolean activa;
    
    // Constantes para tipos de oferta
    public static final String TIPO_EVENTO = "evento";
    public static final String TIPO_LOCALIDAD = "localidad";
    
    /**
     * Constructor para oferta de evento completo
     */
    public Oferta(String id, String descripcion, double porcentajeDescuento, 
                 Date fechaInicio, Date fechaExpiracion, Evento evento) {
        validarParametros(id, descripcion, porcentajeDescuento, fechaInicio, fechaExpiracion);
        
        this.id = id;
        this.descripcion = descripcion;
        this.porcentajeDescuento = porcentajeDescuento;
        this.fechaInicio = fechaInicio;
        this.fechaExpiracion = fechaExpiracion;
        this.evento = evento;
        this.localidad = null;
        this.activa = true;
    }
    
    /**
     * Constructor para oferta específica de localidad
     */
    public Oferta(String id, String descripcion, double porcentajeDescuento, 
                 Date fechaInicio, Date fechaExpiracion, Evento evento, Localidad localidad) {
        validarParametros(id, descripcion, porcentajeDescuento, fechaInicio, fechaExpiracion);
        
        this.id = id;
        this.descripcion = descripcion;
        this.porcentajeDescuento = porcentajeDescuento;
        this.fechaInicio = fechaInicio;
        this.fechaExpiracion = fechaExpiracion;
        this.evento = evento;
        this.localidad = localidad;
        this.activa = true;
    }
    
    private void validarParametros(String id, String descripcion, double porcentajeDescuento, 
                                 Date fechaInicio, Date fechaExpiracion) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("El ID no puede ser nulo o vacío");
        }
        if (descripcion == null || descripcion.trim().isEmpty()) {
            throw new IllegalArgumentException("La descripción no puede ser nula o vacía");
        }
        if (porcentajeDescuento <= 0 || porcentajeDescuento > 1) {
            throw new IllegalArgumentException("El porcentaje de descuento debe estar entre 0 y 1");
        }
        if (fechaInicio == null || fechaExpiracion == null) {
            throw new IllegalArgumentException("Las fechas no pueden ser nulas");
        }
        if (fechaExpiracion.before(fechaInicio)) {
            throw new IllegalArgumentException("La fecha de expiración no puede ser anterior a la fecha de inicio");
        }
    }
    
    // ==================== MÉTODOS GETTER ====================
    
    public String getId() { return id; }
    public String getDescripcion() { return descripcion; }
    public double getPorcentajeDescuento() { return porcentajeDescuento; }
    public Date getFechaInicio() { return fechaInicio; }
    public Date getFechaExpiracion() { return fechaExpiracion; }
    public Evento getEvento() { return evento; }
    public Localidad getLocalidad() { return localidad; }
    public boolean isActiva() { return activa; }
    
    /**
     * @return tipo de oferta (evento o localidad)
     */
    public String getTipo() {
        return localidad == null ? TIPO_EVENTO : TIPO_LOCALIDAD;
    }
    
    // ==================== MÉTODOS DE ESTADO ====================
    
    /**
     * Verifica si la oferta está vigente (activa y en fecha)
     */
    public boolean estaVigente() {
        Date ahora = new Date();
        return activa && 
               !ahora.before(fechaInicio) && 
               !ahora.after(fechaExpiracion);
    }
    
    /**
     * Activa la oferta
     */
    public void activar() {
        this.activa = true;
        System.out.println("Oferta " + id + " activada");
    }
    
    /**
     * Desactiva la oferta
     */
    public void desactivar() {
        this.activa = false;
        System.out.println("Oferta " + id + " desactivada");
    }
    
    /**
     * Aplica el descuento a un precio base
     */
    public double aplicarDescuento(double precioBase) {
        if (estaVigente()) {
            double precioConDescuento = precioBase * (1 - porcentajeDescuento);
            System.out.println("Descuento aplicado: $" + precioBase + " → $" + precioConDescuento + 
                             " (" + (porcentajeDescuento * 100) + "%)");
            return precioConDescuento;
        } else {
            System.out.println("Oferta no vigente - Se mantiene precio original: $" + precioBase);
            return precioBase;
        }
    }
    
    // ==================== MÉTODOS DE INFORMACIÓN ====================
    
    public String getInformacionDetallada() {
        StringBuilder info = new StringBuilder();
        info.append("=== INFORMACIÓN DE OFERTA ===\n");
        info.append("ID: ").append(id).append("\n");
        info.append("Descripción: ").append(descripcion).append("\n");
        info.append("Descuento: ").append(porcentajeDescuento * 100).append("%\n");
        info.append("Tipo: ").append(getTipo()).append("\n");
        info.append("Evento: ").append(evento.getNombre()).append("\n");
        
        if (localidad != null) {
            info.append("Localidad: ").append(localidad.getTipoLocalidad()).append("\n");
        }
        
        info.append("Fecha inicio: ").append(fechaInicio).append("\n");
        info.append("Fecha expiración: ").append(fechaExpiracion).append("\n");
        info.append("Estado: ").append(estaVigente() ? "VIGENTE" : "NO VIGENTE").append("\n");
        info.append("Activa: ").append(activa ? "SÍ" : "NO").append("\n");
        
        return info.toString();
    }
    
    @Override
    public String toString() {
        return String.format(
            "Oferta[id=%s, desc=%s, descuento=%.1f%%, tipo=%s, vigente=%s]",
            id, descripcion, porcentajeDescuento * 100, getTipo(), estaVigente()
        );
    }
}   