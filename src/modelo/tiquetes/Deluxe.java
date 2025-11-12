package modelo.tiquetes;

import modelo.eventos.Evento;
import modelo.eventos.Localidad;
import java.util.ArrayList;
import java.util.Date;

/**
 * Clase que representa un paquete Deluxe que incluye beneficios adicionales.
 * Hereda de Tiquete y añade mercancía y tiquetes adicionales.
 */
public class Deluxe extends Tiquete {
    private ArrayList<String> beneficiosAdicionales;
    private ArrayList<Tiquete> tiquetesAdicionales;
    
    /**
     * Constructor de Deluxe
     * @param id - identificador único
     * @param precioBase - precio base del paquete
     * @param fechaHora - fecha y hora
     * @param localidad - localidad principal
     * @param evento - evento principal
     */
    public Deluxe(String id, double precioBase, Date fechaHora, Localidad localidad, Evento evento) {
        super(id, precioBase, fechaHora, localidad, evento);
        this.beneficiosAdicionales = new ArrayList<>();
        this.tiquetesAdicionales = new ArrayList<>();
        this.transferible = false; // Los paquetes Deluxe NO son transferibles según requerimientos
    }

    // Constructor vacío para persistencia
    public Deluxe() {
        super();
        this.beneficiosAdicionales = new ArrayList<>();
        this.tiquetesAdicionales = new ArrayList<>();
        this.transferible = false; // Los Deluxe no son transferibles
    }

    // Setters para persistencia
    public void setBeneficiosAdicionales(ArrayList<String> beneficiosAdicionales) { this.beneficiosAdicionales = beneficiosAdicionales; }
    public void setTiquetesAdicionales(ArrayList<Tiquete> tiquetesAdicionales) { this.tiquetesAdicionales = tiquetesAdicionales; }
    
    // ==================== MÉTODOS GETTER ====================
    
    /**
     * @return lista de beneficios adicionales
     */
    public ArrayList<String> getBeneficiosAdicionales() {
        return new ArrayList<>(beneficiosAdicionales);
    }
    
    /**
     * @return lista de tiquetes adicionales incluidos
     */
    public ArrayList<Tiquete> getTiquetes() {
        return new ArrayList<>(tiquetesAdicionales);
    }
    
    // ==================== MÉTODOS DE GESTIÓN ====================
    
    /**
     * Agrega un beneficio adicional al paquete
     * @param beneficio - beneficio a agregar
     */
    public void agregarBeneficio(String beneficio) {
        if (beneficio != null && !beneficio.trim().isEmpty()) {
            beneficiosAdicionales.add(beneficio);
            System.out.println("Beneficio '" + beneficio + "' agregado al paquete Deluxe " + id);
        }
    }
    
    /**
     * Agrega un tiquete adicional al paquete
     * @param tiquete - tiquete adicional a incluir
     */
    public void agregarTiqueteAdicional(Tiquete tiquete) {
        if (tiquete != null) {
            tiquetesAdicionales.add(tiquete);
            System.out.println("Tiquete adicional agregado al paquete Deluxe " + id);
        }
    }
    
    /**
     * Calcula el valor total de los beneficios (estimado)
     * @return valor estimado de beneficios
     */
    public double getValorBeneficios() {
        // Valor estimado de cada beneficio
        return beneficiosAdicionales.size() * 50.0;
    }
    
    /**
     * Sobrescribe el método de transferibilidad
     * Los paquetes Deluxe NUNCA son transferibles
     */
    @Override
    public boolean puedeSerTransferido() {
        return false; // Según requerimientos, los Deluxe no son transferibles
    }
    
    /**
     * @return descripción completa del paquete Deluxe
     */
    public String getDescripcionCompleta() {
        StringBuilder descripcion = new StringBuilder();
        descripcion.append("Paquete Deluxe: ").append(id).append("\n");
        descripcion.append("Evento: ").append(evento.getNombre()).append("\n");
        descripcion.append("Beneficios incluidos (").append(beneficiosAdicionales.size()).append("):\n");
        
        for (String beneficio : beneficiosAdicionales) {
            descripcion.append("  - ").append(beneficio).append("\n");
        }
        
        descripcion.append("Tiquetes adicionales: ").append(tiquetesAdicionales.size());
        
        return descripcion.toString();
    }
    
    @Override
    public String toString() {
        return "Deluxe{" +
                "id='" + id + '\'' +
                ", precioBase=" + precioBase +
                ", beneficios=" + beneficiosAdicionales.size() +
                ", tiquetesAdicionales=" + tiquetesAdicionales.size() +
                ", valorBeneficios=" + getValorBeneficios() +
                '}';
    }
}