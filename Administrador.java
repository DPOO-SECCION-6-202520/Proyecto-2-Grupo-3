package modelo.usuarios;

import modelo.eventos.Venue;
import modelo.eventos.Evento;
import modelo.pagos.Pagos;
import modelo.pagos.Compra;
import modelo.tiquetes.Tiquete;
import java.util.ArrayList;

/**
 * Clase que representa al Administrador del sistema.
 * Hereda de Usuario y añade funcionalidades administrativas.
 * El Administrador no tiene saldo virtual y no puede comprar tiquetes.
 */
public class Administrador extends Usuario {
    // Atributos específicos del administrador para configurar cobros
    private double porcentajeAdicional;
    private double cobroFijo;
    
    //Constructor de Administrador
    public Administrador(String login, String password) {
        // Llama al constructor padre con tipoUsuario "administrador"
        super(login, password, "administrador");
        this.porcentajeAdicional = 0.10; // 10% por defecto
        this.cobroFijo = 5.0; // $5 por defecto
    }

    //Constructor vacio para persistencia
    public Administrador() {
        super();
        this.tipoUsuario = "administrador";
    }

    // Setters nuevos para persistencia
    public void setPorcentajeAdicional(double porcentajeAdicional) { 
        this.porcentajeAdicional = porcentajeAdicional; 
    }
    public void setCobroFijo(double cobroFijo) { 
        this.cobroFijo = cobroFijo; 
    }


    // ==================== SOBRESCRIBIR MÉTODOS DE SALDO PARA QUE NO PUEDA COMPRAR ====================

    @Override
    public void setSaldoVirtual(double nuevoSaldo) {
        // Ignorar - Administrador no tiene saldo
        throw new UnsupportedOperationException("El administrador no puede tener saldo virtual");
    }

    @Override
    public void agregarSaldo(double cantidad) {
        throw new UnsupportedOperationException("El administrador no puede tener saldo virtual");
    }
    
    @Override
    public double getSaldoVirtual() {
        return 0.0; // Siempre 0 para administrador
    }
    
    // ==================== MÉTODOS DE CONFIGURACIÓN ====================
    
    //Permite al administrador fijar el porcentaje adicional de servicio
    public void fijarPorcentajeAdicional(double nuevoPorcentaje) {
        if (nuevoPorcentaje >= 0) {
            this.porcentajeAdicional = nuevoPorcentaje;
            System.out.println("Porcentaje adicional actualizado a: " + (nuevoPorcentaje * 100) + "%");
        } else {
            System.out.println("Error: El porcentaje no puede ser negativo");
        }
    }
    
    // Permite al administrador fijar el cobro fijo por emisión/impresión
    public void fijarCobroFijo(double nuevoCobroFijo) {
        if (nuevoCobroFijo >= 0) {
            this.cobroFijo = nuevoCobroFijo;
            System.out.println("Cobro fijo actualizado a: $" + nuevoCobroFijo);
        } else {
            System.out.println("Error: El cobro fijo no puede ser negativo");
        }
    }
    
    // ==================== MÉTODOS ADMINISTRATIVOS ====================
    
    /**
     * Aprueba un venue sugerido por un organizador
     * @param venue - venue a aprobar
     */
    public void aprobarVenue(Venue venue) {
        if (venue != null) {
            venue.aprobar();
            System.out.println("Venue '" + venue.getNombre() + "' aprobado por el administrador: " + this.login);
        }
    }
    
    /**
     * Crea un nuevo venue en el sistema
     * @param id - identificador único
     * @param nombre - nombre del venue
     * @param ubicacion - ubicación física
     * @param capacidad - capacidad máxima
     * @return el venue creado
     */
    public Venue crearVenue(String id, String nombre, String ubicacion, int capacidad) {
        Venue nuevoVenue = new Venue(id, nombre, ubicacion, capacidad);
        nuevoVenue.aprobar(); // El administrador aprueba automáticamente los venues que crea
        System.out.println("Nuevo venue creado y aprobado: " + nombre);
        return nuevoVenue;
    }
    
    /**
     * Cancela un evento y maneja los reembolsos
     * @param evento - evento a cancelar
     * @param compradoresConTiquetes - compradores con tiquetes del evento
     */
    public void cancelarEvento(Evento evento, ArrayList<Comprador> compradoresConTiquetes) {
        if (evento != null) {
            evento.rechazar(); // Marcamos el evento como rechazado/cancelado
            System.out.println("Evento '" + evento.getNombre() + "' cancelado por el administrador: " + this.login);
            
            // Procesar reembolsos a los compradores afectados
            procesarReembolsosPorCancelacion(evento, compradoresConTiquetes);
        }
    }
    
    /**
     * Procesa reembolsos por cancelación de evento
     * @param evento - evento cancelado
     * @param compradores - compradores con tiquetes del evento
     */
    private void procesarReembolsosPorCancelacion(Evento evento, ArrayList<Comprador> compradores) {
        Pagos servicioPagos = Pagos.getInstancia();
        int reembolsosProcesados = 0;
        double totalReembolsado = 0;
        
        for (Comprador comprador : compradores) {
            // Buscar tiquetes del comprador para este evento
            ArrayList<Tiquete> tiquetesEvento = obtenerTiquetesDelEvento(comprador, evento);
            
            for (Tiquete tiquete : tiquetesEvento) {
                // DELEGAR CÁLCULO A PAGOS - Cancelación: true
                double montoReembolso = servicioPagos.calcularMontoReembolso(tiquete, true, this.cobroFijo);
                
                if (montoReembolso > 0) {
                    servicioPagos.procesarReembolsoSaldo(comprador, montoReembolso, 
                        "Cancelación evento: " + evento.getNombre());
                    reembolsosProcesados++;
                    totalReembolsado += montoReembolso;
                    
                    // Marcar tiquete como no vigente
                    tiquete.marcarComoUtilizado();
                }
            }
        }
        
        System.out.println("Reembolsos procesados: " + reembolsosProcesados + " tiquetes");
        System.out.println("Total reembolsado: $" + totalReembolsado);
    }
    
    /**
     * Obtiene los tiquetes de un comprador para un evento específico
     */
    private ArrayList<Tiquete> obtenerTiquetesDelEvento(Comprador comprador, Evento evento) {
        ArrayList<Tiquete> tiquetesEvento = new ArrayList<>();
        for (Tiquete tiquete : comprador.getHistorialTiquetes()) {
            if (tiquete.getEvento().equals(evento) && tiquete.estaVigente()) {
                tiquetesEvento.add(tiquete);
            }
        }
        return tiquetesEvento;
    }
    
    /**
     * Realiza reembolso a un usuario específico
     * @param usuario - usuario a reembolsar
     * @param monto - monto a reembolsar
     * @param motivo - motivo del reembolso
     */
    public void reembolsar(Usuario usuario, double monto, String motivo) {
        if (usuario != null && monto > 0) {
            Pagos servicioPagos = Pagos.getInstancia();
            servicioPagos.procesarReembolsoSaldo(usuario, monto, motivo);
            System.out.println("Reembolso realizado por administrador " + this.login);
        }
    }
    
    /**
     * Autoriza un reembolso solicitado por un cliente por calamidad
     * @param comprador - cliente que solicita el reembolso
     * @param tiquete - tiquete a reembolsar
     * @param motivo - motivo de la autorización
     */
    public void autorizarReembolso(Comprador comprador, Tiquete tiquete, String motivo) {
        if (comprador != null && tiquete != null && comprador.getHistorialTiquetes().contains(tiquete)) {
            if (tiquete.estaVigente()) {
                // DELEGAR CÁLCULO A PAGOS - Calamidad: false
                Pagos servicioPagos = Pagos.getInstancia();
                double montoReembolso = servicioPagos.calcularMontoReembolso(tiquete, false, this.cobroFijo);
                
                servicioPagos.procesarReembolsoSaldo(comprador, montoReembolso, 
                    "Reembolso por calamidad: " + motivo);
                
                // Marcar tiquete como utilizado
                tiquete.marcarComoUtilizado();
                
                System.out.println("Reembolso autorizado para: " + comprador.getLogin() + 
                                 " - Monto: $" + montoReembolso);
            } else {
                System.out.println("Error: El tiquete no está vigente para reembolso");
            }
        } else {
            System.out.println("Error: Datos inválidos para reembolso");
        }
    }
    
    /**
     * Observa las ganancias reales de la plataforma basadas en compras reales
     * @param compras - lista de todas las compras del sistema
     */
    public void observarGanancias(ArrayList<Compra> compras) {
        System.out.println("=== REPORTE DE GANANCIAS REALES - Administrador: " + this.login + " ===");
        
        double gananciasTotales = 0;
        double gananciasPorCobroFijo = 0;
        double gananciasPorPorcentaje = 0;
        int comprasProcesadas = 0;
        
        for (Compra compra : compras) {
            if ("aprobada".equals(compra.getEstado())) {
                comprasProcesadas++;
                
                // Calcular ganancias de esta compra
                int cantidadTiquetes = compra.getTiquetes().size();
                double subtotal = calcularSubtotalCompra(compra);
                
                // Ganancias por cobro fijo
                double gananciaCobroFijo = cantidadTiquetes * this.cobroFijo;
                
                // Ganancias por porcentaje adicional
                double gananciaPorcentaje = subtotal * this.porcentajeAdicional;
                
                double gananciaTotalCompra = gananciaCobroFijo + gananciaPorcentaje;
                gananciasTotales += gananciaTotalCompra;
                gananciasPorCobroFijo += gananciaCobroFijo;
                gananciasPorPorcentaje += gananciaPorcentaje;
                
                System.out.println("Compra " + compra.getId() + 
                                 " - Tiquetes: " + cantidadTiquetes +
                                 " - Ganancias: $" + String.format("%.2f", gananciaTotalCompra));
            }
        }
        
        System.out.println("--- RESUMEN DETALLADO ---");
        System.out.println("Compras procesadas: " + comprasProcesadas);
        System.out.println("Ganancias por cobro fijo ($" + cobroFijo + " por tiquete): $" + 
                         String.format("%.2f", gananciasPorCobroFijo));
        System.out.println("Ganancias por porcentaje adicional (" + (porcentajeAdicional * 100) + "%): $" + 
                         String.format("%.2f", gananciasPorPorcentaje));
        System.out.println("GANANCIAS TOTALES: $" + String.format("%.2f", gananciasTotales));
    }
    
    /**
     * Calcula el subtotal de una compra (suma de precios base de tiquetes)
     */
    private double calcularSubtotalCompra(Compra compra) {
        double subtotal = 0;
        for (Tiquete tiquete : compra.getTiquetes()) {
            subtotal += tiquete.getPrecioBase();
        }
        return subtotal;
    }
    
    /**
     * Observa ganancias por evento específico
     * @param evento - evento a analizar
     * @param compras - compras relacionadas con el evento
     */
    public void observarGananciasPorEvento(Evento evento, ArrayList<Compra> compras) {
        System.out.println("=== GANANCIAS POR EVENTO: " + evento.getNombre() + " ===");
        
        double gananciasEvento = 0;
        int tiquetesVendidos = 0;
        
        for (Compra compra : compras) {
            if ("aprobada".equals(compra.getEstado()) && compraContieneEvento(compra, evento)) {
                int tiquetesCompra = compra.getTiquetes().size();
                double subtotal = calcularSubtotalCompra(compra);
                double gananciaCompra = (tiquetesCompra * this.cobroFijo) + (subtotal * this.porcentajeAdicional);
                
                gananciasEvento += gananciaCompra;
                tiquetesVendidos += tiquetesCompra;
            }
        }
        
        System.out.println("Tiquetes vendidos: " + tiquetesVendidos);
        System.out.println("Ganancias del evento: $" + String.format("%.2f", gananciasEvento));
        System.out.println("Porcentaje de venta: " + 
                         calcularPorcentajeVenta(tiquetesVendidos, evento.getTiquetes().size()) + "%");
    }
    
    /**
     * Verifica si una compra contiene tiquetes de un evento específico
     */
    private boolean compraContieneEvento(Compra compra, Evento evento) {
        for (Tiquete tiquete : compra.getTiquetes()) {
            if (tiquete.getEvento().equals(evento)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Calcula porcentaje de venta
     */
    private double calcularPorcentajeVenta(int vendidos, int total) {
        if (total == 0) return 0;
        return (vendidos * 100.0) / total;
    }
    
    // ==================== GETTERS ====================
    
    public double getPorcentajeAdicional() { return porcentajeAdicional; }
    
    public double getCobroFijo() { return cobroFijo; }
    
    //convierte la info a String
    @Override
    public String toString() {
        return "Administrador{" +
                "login='" + login + '\'' +
                ", porcentajeAdicional=" + (porcentajeAdicional * 100) + "%" +
                ", cobroFijo=$" + cobroFijo +
                '}';
    }
}