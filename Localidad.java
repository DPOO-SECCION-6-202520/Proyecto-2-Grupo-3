package modelo.eventos;

import modelo.tiquetes.Tiquete;
import java.util.ArrayList;

/**
 * Clase que representa una localidad dentro de un venue.
 * Una localidad tiene tipo, si es numerada, y tiquetes asociados.
 */
public class Localidad {
    private String id;
    private String tipoLocalidad; // Ej: "VIP", "General", "Platea"
    private boolean numerada;
    private Venue venue;
    private ArrayList<Tiquete> tiquetes;
    private int capacidad;
    private double precioBase; // Precio base para esta localidad
    private ArrayList<Oferta> ofertas;
    
    /**
     * Constructor de Localidad
     * @param id - identificador único
     * @param tipoLocalidad - tipo de localidad
     * @param numerada - si es numerada o no
     * @param venue - venue al que pertenece
     * @param capacidad - capacidad de la localidad
     * @param precioBase - precio base de los tiquetes en esta localidad
     */
    public Localidad(String id, String tipoLocalidad, boolean numerada, Venue venue, int capacidad, double precioBase) {
        this.id = id;
        this.tipoLocalidad = tipoLocalidad;
        this.numerada = numerada;
        this.venue = venue;
        this.capacidad = capacidad;
        this.precioBase = precioBase;
        this.tiquetes = new ArrayList<>();
        this.ofertas = new ArrayList<>(); // INICIALIZADO EN CONSTRUCTOR
    }

    //constructor vacío para persistencia
    public Localidad() {
        this.tiquetes = new ArrayList<>();
        this.ofertas = new ArrayList<>();
    }

    //Setters para persistencia
    public void setId(String id) { this.id = id; }
    public void setTipoLocalidad(String tipoLocalidad) { this.tipoLocalidad = tipoLocalidad; }
    public void setNumerada(boolean numerada) { this.numerada = numerada; }
    public void setVenue(Venue venue) { this.venue = venue; }
    public void setCapacidad(int capacidad) { this.capacidad = capacidad; }
    public void setPrecioBase(double precioBase) { this.precioBase = precioBase; }
    public void setTiquetes(ArrayList<Tiquete> tiquetes) { this.tiquetes = tiquetes; }
    public void setOfertas(ArrayList<Oferta> ofertas) { this.ofertas = ofertas; }
    
    // ==================== MÉTODOS GETTER ====================
    
    public String getId() { return id; }
    public String getTipoLocalidad() { return tipoLocalidad; }
    public boolean isNumerada() { return numerada; }
    public Venue getVenue() { return venue; }
    public int getCapacidad() { return capacidad; }
    public double getPrecioBase() { return precioBase; }
    
    /**
     * @return lista de tiquetes de esta localidad
     */
    public ArrayList<Tiquete> getTiquetesLocalidad() {
        return new ArrayList<>(tiquetes);
    }
    
    // ==================== MÉTODOS DE GESTIÓN DE OFERTAS ====================
    
    /**
     * Agrega una oferta a la localidad
     * @param oferta - oferta a agregar
     */
    public void agregarOferta(Oferta oferta) {
        if (oferta != null && !ofertas.contains(oferta)) {
            ofertas.add(oferta);
            System.out.println("Oferta agregada a la localidad: " + oferta.getDescripcion());
        }
    }
    
    /**
     * @return lista de ofertas de esta localidad
     */
    public ArrayList<Oferta> getOfertas() {
        return new ArrayList<>(ofertas);
    }
    
    /**
     * Calcula el precio aplicando todas las ofertas vigentes
     * @return precio final con descuentos aplicados
     */
    public double getPrecioConOfertas() {
        double precioFinal = precioBase;
        for (Oferta oferta : ofertas) {
            if (oferta.estaVigente()) {
                precioFinal = oferta.aplicarDescuento(precioFinal);
            }
        }
        return precioFinal;
    }
    
    /**
     * Verifica si la localidad tiene ofertas vigentes
     * @return true si hay ofertas activas
     */
    public boolean tieneOfertasVigentes() {
        for (Oferta oferta : ofertas) {
            if (oferta.estaVigente()) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Obtiene las ofertas vigentes de esta localidad
     * @return lista de ofertas vigentes
     */
    public ArrayList<Oferta> getOfertasVigentes() {
        ArrayList<Oferta> vigentes = new ArrayList<>();
        for (Oferta oferta : ofertas) {
            if (oferta.estaVigente()) {
                vigentes.add(oferta);
            }
        }
        return vigentes;
    }
    
    // ==================== MÉTODOS DE GESTIÓN DE TIQUETES ====================
    
    /**
     * @return cantidad de tiquetes disponibles en esta localidad
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
     * @return cantidad de tiquetes vendidos en esta localidad
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
        if (capacidad == 0) return 0;
        return (getTiquetesVendidos() * 100.0) / capacidad;
    }
    
    /**
     * Agrega un tiquete a la localidad
     * @param tiquete - tiquete a agregar
     */
    public void agregarTiquete(Tiquete tiquete) {
        if (tiquete != null && !tiquetes.contains(tiquete)) {
            if (getTiquetesDisponibles() < capacidad) {
                tiquetes.add(tiquete);
                System.out.println("Tiquete " + tiquete.getId() + " agregado a localidad " + tipoLocalidad);
            } else {
                System.out.println("No hay capacidad en la localidad " + tipoLocalidad);
            }
        }
    }
    
    /**
     * Crea y agrega un nuevo tiquete a esta localidad
     * @param evento - evento al que pertenece el tiquete
     * @return el tiquete creado o null si no hay capacidad
     */
    public Tiquete crearTiquete(modelo.eventos.Evento evento) {
        if (evento == null || !hayDisponibilidad()) {
            return null;
        }
        
        String tiqueteId = "TQ-" + evento.getId() + "-" + this.id + "-" + System.currentTimeMillis();
        
        // Usar precio con ofertas si hay ofertas vigentes
        double precioFinal = tieneOfertasVigentes() ? getPrecioConOfertas() : precioBase;
        
        Tiquete nuevoTiquete = new Tiquete(tiqueteId, precioFinal, evento.getFechaHora(), this, evento);
        agregarTiquete(nuevoTiquete);
        
        return nuevoTiquete;
    }
    
    /**
     * Obtiene un tiquete disponible de esta localidad
     * @return tiquete disponible o null si no hay
     */
    public Tiquete obtenerTiqueteDisponible() {
        for (Tiquete tiquete : tiquetes) {
            if (tiquete.estaVigente()) {
                return tiquete;
            }
        }
        return null;
    }
    
    /**
     * Obtiene múltiples tiquetes disponibles
     * @param cantidad - cantidad de tiquetes necesarios
     * @return lista de tiquetes disponibles
     */
    public ArrayList<Tiquete> obtenerTiquetesDisponibles(int cantidad) {
        ArrayList<Tiquete> disponibles = new ArrayList<>();
        int contador = 0;
        
        for (Tiquete tiquete : tiquetes) {
            if (tiquete.estaVigente() && contador < cantidad) {
                disponibles.add(tiquete);
                contador++;
            }
        }
        
        return disponibles;
    }
    
    /**
     * Verifica si hay tiquetes disponibles
     * @return true si hay disponibilidad
     */
    public boolean hayDisponibilidad() {
        return getTiquetesDisponibles() > 0;
    }
    
    /**
     * Verifica si hay suficiente disponibilidad para una cantidad específica
     * @param cantidad - cantidad requerida
     * @return true si hay suficiente disponibilidad
     */
    public boolean haySuficienteDisponibilidad(int cantidad) {
        return getTiquetesDisponibles() >= cantidad;
    }
    
    // ==================== MÉTODOS DE INFORMACIÓN ====================
    
    /**
     * Obtiene información detallada de la localidad
     */
    public String getInformacionDetallada() {
        StringBuilder info = new StringBuilder();
        info.append("=== INFORMACIÓN DE LOCALIDAD ===\n");
        info.append("Tipo: ").append(tipoLocalidad).append("\n");
        info.append("ID: ").append(id).append("\n");
        info.append("Venue: ").append(venue.getNombre()).append("\n");
        info.append("Numerada: ").append(numerada ? "SÍ" : "NO").append("\n");
        info.append("Capacidad: ").append(capacidad).append("\n");
        info.append("Precio base: $").append(precioBase).append("\n");
        
        if (tieneOfertasVigentes()) {
            info.append("Precio con ofertas: $").append(getPrecioConOfertas()).append("\n");
            info.append("Ofertas vigentes: ").append(getOfertasVigentes().size()).append("\n");
        }
        
        info.append("Tiquetes disponibles: ").append(getTiquetesDisponibles()).append("\n");
        info.append("Tiquetes vendidos: ").append(getTiquetesVendidos()).append("\n");
        info.append("Porcentaje vendido: ").append(String.format("%.1f", getPorcentajeVendido())).append("%\n");
        
        return info.toString();
    }
    
    /**
     * @return descripción resumida de la localidad
     */
    public String getDescripcion() {
        String descripcion = tipoLocalidad + " - $" + precioBase;
        if (tieneOfertasVigentes()) {
            descripcion += " → $" + getPrecioConOfertas() + " (OFERTA)";
        }
        descripcion += " - " + getTiquetesDisponibles() + " disponibles";
        return descripcion;
    }
    
    @Override
    public String toString() {
        String info = "Localidad{" +
                "tipo='" + tipoLocalidad + '\'' +
                ", numerada=" + numerada +
                ", capacidad=" + capacidad +
                ", precioBase=$" + precioBase;
        
        if (tieneOfertasVigentes()) {
            info += ", precioOferta=$" + getPrecioConOfertas();
        }
        
        info += ", disponibles=" + getTiquetesDisponibles() +
                ", porcentajeVendido=" + String.format("%.1f", getPorcentajeVendido()) + "%" +
                '}';
        
        return info;
    }
}