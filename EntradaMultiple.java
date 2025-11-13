package modelo.tiquetes;

import modelo.eventos.Evento;
import modelo.eventos.Localidad;
import java.util.ArrayList;
import java.util.Date;

/**
 * Clase que representa una entrada múltiple (para grupos o temporadas).
 * Hereda de Tiquete y contiene múltiples tiquetes individuales.
 * Corrige problemas de precio y transferencia individual.
 */
public class EntradaMultiple extends Tiquete {
    private int numEntradas;
    private ArrayList<Tiquete> tiquetesIncluidos;
    private double descuento; // Descuento por compra múltiple (ej: 0.1 para 10%)
    
    // Constructor de EntradaMultiple
    public EntradaMultiple(String id, double precioBaseIndividual, Date fechaHora, 
                          Localidad localidad, Evento evento, int numEntradas, double descuento) {
        // Precio total = (precio individual * cantidad) con descuento aplicado
        super(id, precioBaseIndividual * numEntradas * (1 - descuento), fechaHora, localidad, evento);
        this.numEntradas = numEntradas;
        this.descuento = descuento;
        this.tiquetesIncluidos = new ArrayList<>();
        this.transferible = true; // Las entradas múltiples son transferibles
        
        // Generar tiquetes individuales con precio correcto
        generarTiquetesIndividuales(precioBaseIndividual);
    }

    // Constructor vacío para persistencia
    public EntradaMultiple() {
        super();
        this.tiquetesIncluidos = new ArrayList<>();
    }
    //setters para persistencia
    public void setNumEntradas(int numEntradas) { this.numEntradas = numEntradas; }
    public void setDescuento(double descuento) { this.descuento = descuento; }
    public void setTiquetesIncluidos(ArrayList<Tiquete> tiquetesIncluidos) { this.tiquetesIncluidos = tiquetesIncluidos; }
    
    // ==================== MÉTODOS GETTER ====================
    
    public int getNumEntradas() { return numEntradas; }
    public double getDescuento() { return descuento; }
    
    // Devuelve copia de la lista de tiquetes individuales incluidos
    public ArrayList<Tiquete> getTiquetes() {
        return new ArrayList<>(tiquetesIncluidos);
    }
    
    // ==================== MÉTODOS DE GESTIÓN CORREGIDOS ====================
    
    /**
     * Genera los tiquetes individuales con precio correcto
     * @param precioBaseIndividual - precio base de cada tiquete individual
     */
    private void generarTiquetesIndividuales(double precioBaseIndividual) {
        double precioIndividual = precioBaseIndividual * (1 - descuento);
        
        for (int i = 1; i <= numEntradas; i++) {
            String tiqueteId = this.id + "-IND-" + i;
            Tiquete tiqueteIndividual = new Tiquete(tiqueteId, precioIndividual, fechaHora, localidad, evento);
            tiqueteIndividual.setTransferible(true); // Los tiquetes individuales son transferibles
            tiquetesIncluidos.add(tiqueteIndividual);
        }
        System.out.println("Generados " + numEntradas + " tiquetes individuales para entrada múltiple " + id);
        System.out.println("Precio individual: $" + precioIndividual + " - Precio total paquete: $" + this.precioBase);
    }
    
    /**
     * Calcula el precio total considerando el descuento
     */
    public double getPrecioConDescuento() {
        return precioBase; // Ya incluye el descuento aplicado en el constructor
    }
    
    /**
     * Verifica si algún tiquete individual ya fue transferido, utilizado o está vencido
     * @return true si todos los tiquetes están intactos y vigentes
     */
    public boolean estaCompleto() {
        for (Tiquete tiquete : tiquetesIncluidos) {
            if (tiquete.isUtilizado() || !tiquete.getEsTransferible() || !tiquete.estaVigente()) {
                return false;
            }
        }
        return true;
    }
    
    // ==================== MÉTODOS NUEVOS PARA TRANSFERENCIAS INDIVIDUALES ====================
    
    /**
     * Transfiere un tiquete individual del paquete
     * @param indice - índice del tiquete individual (0 a numEntradas-1)
     * @return true si la transferencia fue exitosa
     */
    public boolean transferirTiqueteIndividual(int indice) {
        if (indice >= 0 && indice < tiquetesIncluidos.size()) {
            Tiquete tiquete = tiquetesIncluidos.get(indice);
            if (tiquete.puedeSerTransferido()) {
                tiquete.setTransferible(false); // Ya no se puede transferir nuevamente
                System.out.println("Tiquete individual " + tiquete.getId() + " transferido exitosamente");
                return true;
            } else {
                System.out.println("Error: El tiquete individual no se puede transferir");
            }
        } else {
            System.out.println("Error: Índice de tiquete individual inválido");
        }
        return false;
    }
    
    /**
     * Obtiene un tiquete individual por índice
     * @param indice - índice del tiquete (0 a numEntradas-1)
     * @return el tiquete individual o null si el índice es inválido
     */
    public Tiquete getTiqueteIndividual(int indice) {
        if (indice >= 0 && indice < tiquetesIncluidos.size()) {
            return tiquetesIncluidos.get(indice);
        }
        return null;
    }
    
    /**
     * Marca un tiquete individual como utilizado
     * @param indice - índice del tiquete a utilizar
     */
    public void utilizarTiqueteIndividual(int indice) {
        Tiquete tiquete = getTiqueteIndividual(indice);
        if (tiquete != null && tiquete.estaVigente()) {
            tiquete.marcarComoUtilizado();
            System.out.println("Tiquete individual " + tiquete.getId() + " marcado como utilizado");
        } else {
            System.out.println("Error: No se puede utilizar el tiquete individual");
        }
    }
    
    /**
     * Verifica cuántos tiquetes individuales están disponibles (no utilizados y vigentes)
     * @return número de tiquetes disponibles
     */
    public int getTiquetesDisponibles() {
        int disponibles = 0;
        for (Tiquete tiquete : tiquetesIncluidos) {
            if (tiquete.estaVigente() && !tiquete.isUtilizado()) {
                disponibles++;
            }
        }
        return disponibles;
    }
    
    /**
     * Verifica si un tiquete individual específico está disponible
     * @param indice - índice del tiquete
     * @return true si está disponible
     */
    public boolean isTiqueteIndividualDisponible(int indice) {
        Tiquete tiquete = getTiqueteIndividual(indice);
        return tiquete != null && tiquete.estaVigente() && !tiquete.isUtilizado();
    }
    
    // ==================== SOBRESCRITURA DE MÉTODOS ====================
    
    /**
     * Sobrescribe el método de transferibilidad
     * Solo es transferible como paquete completo si todos los tiquetes están intactos
     */
    @Override
    public boolean puedeSerTransferido() {
        return super.puedeSerTransferido() && estaCompleto();
    }
    
    /**
     * Sobrescribe el método de vigencia
     * Considera la vigencia de todos los tiquetes individuales
     */
    @Override
    public boolean estaVigente() {
        // El paquete está vigente si al menos un tiquete individual está vigente
        for (Tiquete tiquete : tiquetesIncluidos) {
            if (tiquete.estaVigente()) {
                return true;
            }
        }
        return false;
    }
    
    // ==================== MÉTODOS DE INFORMACIÓN ====================
    
    /**
     * Muestra información detallada de todos los tiquetes individuales
     */
    public void mostrarTiquetesIndividuales() {
        System.out.println("=== Tiquetes Individuales de " + this.id + " ===");
        for (int i = 0; i < tiquetesIncluidos.size(); i++) {
            Tiquete tiquete = tiquetesIncluidos.get(i);
            String estado = tiquete.isUtilizado() ? "UTILIZADO" : 
                           tiquete.estaVigente() ? "VIGENTE" : "VENCIDO";
            String transferible = tiquete.getEsTransferible() ? "TRANSFERIBLE" : "NO TRANSFERIBLE";
            
            System.out.println((i + 1) + ". " + tiquete.getId() + 
                             " - Precio: $" + tiquete.getPrecioBase() +
                             " - Estado: " + estado +
                             " - " + transferible);
        }
    }
    
    @Override
    public String toString() {
        return "EntradaMultiple{" +
                "id='" + id + '\'' +
                ", numEntradas=" + numEntradas +
                ", disponibles=" + getTiquetesDisponibles() +
                ", descuento=" + (descuento * 100) + "%" +
                ", precioTotal=$" + String.format("%.2f", precioBase) +
                ", transferibleCompleto=" + puedeSerTransferido() +
                ", vigente=" + estaVigente() +
                '}';
    }
}