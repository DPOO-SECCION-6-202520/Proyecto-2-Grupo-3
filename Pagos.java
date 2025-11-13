package modelo.pagos;

import modelo.usuarios.Usuario;
import modelo.tiquetes.Tiquete;
import modelo.tiquetes.EntradaMultiple;
import java.util.ArrayList;

/**
 * Clase Service Provider que se encarga de todos los cálculos y procesos de pago.
 */
public class Pagos {
    private static Pagos instancia;
    
    private Pagos() {
        // Constructor privado para evitar instanciación directa
    }
    
    /**
     * Método Singleton para obtener la instancia única
     */
    public static Pagos getInstancia() {
        if (instancia == null) {
            instancia = new Pagos();
        }
        return instancia;
    }
    
    // ==================== CÁLCULOS DE PRECIO ====================
    
    /**
     * Calcula el precio total de una compra incluyendo cargos adicionales
     * @param tiquetes - lista de tiquetes a comprar
     * @param porcentajeAdicional - porcentaje de servicio (ej: 0.15 para 15%)
     * @param cobroFijo - cargo fijo por emisión/impresión
     * @return precio total a pagar
     */
    public double calcularPrecioTotal(ArrayList<Tiquete> tiquetes, double porcentajeAdicional, double cobroFijo) {
        // NUEVO: Validaciones agregadas
        if (tiquetes == null || tiquetes.isEmpty()) {
            throw new IllegalArgumentException("La lista de tiquetes no puede estar vacía");
        }
        if (porcentajeAdicional < 0) {
            throw new IllegalArgumentException("El porcentaje adicional no puede ser negativo");
        }
        if (cobroFijo < 0) {
            throw new IllegalArgumentException("El cobro fijo no puede ser negativo");
        }
        
        double subtotal = calcularSubtotal(tiquetes);
        double cargoServicio = subtotal * porcentajeAdicional;
        double total = subtotal + cargoServicio + (cobroFijo * tiquetes.size());
        
        System.out.println("=== DESGLOSE DE PAGO ===");
        System.out.println("Subtotal: $" + subtotal);
        System.out.println("Cargo por servicio (" + (porcentajeAdicional * 100) + "%): $" + cargoServicio);
        System.out.println("Cargos fijos (" + tiquetes.size() + " tiquetes x $" + cobroFijo + "): $" + (cobroFijo * tiquetes.size()));
        System.out.println("TOTAL: $" + total);
        
        return total;
    }
    
    /**
     * Calcula el subtotal sumando los precios base de los tiquetes
     * @param tiquetes - lista de tiquetes
     * @return subtotal sin cargos adicionales
     */
    private double calcularSubtotal(ArrayList<Tiquete> tiquetes) {
        double subtotal = 0.0;
        for (Tiquete tiquete : tiquetes) {
            // Considerar ofertas en el precio
            double precioTiquete = tiquete.getPrecioBase();
            
            // Verificar si la localidad tiene ofertas vigentes
            modelo.eventos.Localidad localidad = tiquete.getLocalidad();
            if (localidad != null && localidad.tieneOfertasVigentes()) {
                precioTiquete = localidad.getPrecioConOfertas();
            }
            
            if (tiquete instanceof EntradaMultiple) {
                // Para entrada múltiple, usamos el precio con descuento
                EntradaMultiple entradaMultiple = (EntradaMultiple) tiquete;
                subtotal += entradaMultiple.getPrecioConDescuento();
            } else {
                // Para tiquetes normales y Deluxe, usamos el precio (que puede incluir ofertas)
                subtotal += precioTiquete;
            }
        }
        return subtotal;
    }
    
    /**
     * Aplica descuentos por ofertas a un precio base
     */
    public double aplicarDescuentos(double subtotal, double porcentajeDescuento) {
        if (porcentajeDescuento > 0 && porcentajeDescuento <= 1) {
            double descuento = subtotal * porcentajeDescuento;
            System.out.println("Descuento aplicado: $" + descuento + " (" + (porcentajeDescuento * 100) + "%)");
            return subtotal - descuento;
        }
        return subtotal;
    }
    
    /**
     * Calcula el monto a reembolsar según el tipo de reembolso
     * @param tiquete - tiquete a reembolsar
     * @param esCancelacionEvento - true si es por cancelación de evento
     * @param cobroFijo - cargo fijo por emisión (necesario para cálculo correcto)
     * @return monto a reembolsar
     */
    public double calcularMontoReembolso(Tiquete tiquete, boolean esCancelacionEvento, double cobroFijo) {
        double montoReembolso;
        
        if (esCancelacionEvento) {
            // CORRECCIÓN: Por cancelación: precio base MENOS costo de emisión
            montoReembolso = Math.max(0, tiquete.getPrecioBase() - cobroFijo);
            System.out.println("Reembolso por cancelación: $" + montoReembolso + 
                             " (precio base: $" + tiquete.getPrecioBase() + 
                             " - costo emisión: $" + cobroFijo + ")");
        } else {
            // Por calamidad: solo se reembolsa precio base (según requerimientos)
            montoReembolso = tiquete.getPrecioBase();
            System.out.println("Reembolso por calamidad: $" + montoReembolso + " (precio base)");
        }
        
        return montoReembolso;
    }
    
    // ==================== PROCESOS DE PAGO ====================
    
    /**
     * Procesa un pago con saldo virtual
     * @param usuario - usuario que realiza el pago
     * @param montoTotal - monto total a pagar
     * @return true si el pago fue exitoso
     */
    public boolean procesarPagoConSaldo(Usuario usuario, double montoTotal) {
        if (usuario.getSaldoVirtual() >= montoTotal) {
            double nuevoSaldo = usuario.getSaldoVirtual() - montoTotal;
            usuario.setSaldoVirtual(nuevoSaldo);
            System.out.println("Pago exitoso. Saldo restante: $" + nuevoSaldo);
            return true;
        } else {
            System.out.println("Error: Saldo insuficiente. Saldo actual: $" + usuario.getSaldoVirtual() + ", Required: $" + montoTotal);
            return false;
        }
    }
    
    /**
     * Simula procesamiento con pasarela de pago externa
     * @param montoTotal - monto a cobrar
     * @param metodoPago - método de pago (tarjeta, transferencia, etc.)
     * @return true si el pago fue aprobado
     */
    public boolean procesarPagoExterno(double montoTotal, String metodoPago) {
        System.out.println("Procesando pago de $" + montoTotal + " con " + metodoPago + "...");
        
        // Simulación de procesamiento con pasarela externa
        // En un sistema real, aquí se integraría con la API de la pasarela de pago
        boolean pagoAprobado = simularAprobacionPasarela();
        
        if (pagoAprobado) {
            System.out.println("Pago aprobado por la pasarela externa");
            return true;
        } else {
            System.out.println("Pago rechazado por la pasarela externa");
            return false;
        }
    }
    
    /**
     * Simula la aprobación de una pasarela de pago externa
     * @return true si el pago es aprobado (simulación)
     */
    private boolean simularAprobacionPasarela() {
        // Simulación: 90% de probabilidad de aprobación
        return Math.random() > 0.1;
    }
    
    // ==================== APROBACIÓN DE PAGOS ====================
    
    /**
     * Aprueba un pago pendiente (rol del administrador)
     * @param compra - compra a aprobar
     * @return true si fue aprobado exitosamente
     */
    public boolean aprobarPago(Compra compra) {
        if (compra != null && "pendiente".equals(compra.getEstado())) {
            compra.setEstado("aprobado");
            System.out.println("Pago aprobado para la compra: " + compra.getId());
            
            // Aquí se podrían realizar otras acciones como enviar tiquetes al usuario
            // o notificar al organizador sobre la venta
            
            return true;
        } else {
            System.out.println("No se puede aprobar el pago. Estado inválido.");
            return false;
        }
    }
    
    /**
     * Rechaza un pago pendiente (rol del administrador)
     * @param compra - compra a rechazar
     * @param razon - razón del rechazo
     * @return true si fue rechazado exitosamente
     */
    public boolean rechazarPago(Compra compra, String razon) {
        if (compra != null && "pendiente".equals(compra.getEstado())) {
            compra.setEstado("rechazado");
            System.out.println("Pago rechazado para la compra: " + compra.getId());
            System.out.println("Razón: " + razon);
            return true;
        } else {
            System.out.println("No se puede rechazar el pago. Estado inválido.");
            return false;
        }
    }
    
    // ==================== PROCESAMIENTO DE REEMBOLSOS ====================
    
    /**
     * Procesa un reembolso al saldo virtual del usuario
     * @param usuario - usuario a reembolsar
     * @param monto - monto a reembolsar
     * @param motivo - motivo del reembolso
     */
    public void procesarReembolsoSaldo(Usuario usuario, double monto, String motivo) {
        if (monto > 0) {
            usuario.agregarSaldo(monto);
            System.out.println("Reembolso procesado: $" + monto + " agregado al saldo de " + usuario.getLogin());
            System.out.println("Motivo: " + motivo);
            System.out.println("Nuevo saldo: $" + usuario.getSaldoVirtual());
        } else {
            System.out.println("Error: Monto de reembolso inválido");
        }
    }
    
    // ==================== VALIDACIONES ====================
    
    /**
     * Valida si una compra cumple con las restricciones de tiquetes máximos
     * @param tiquetes - lista de tiquetes a validar
     * @param maxTiquetesPorTransaccion - máximo permitido por transacción
     * @return true si cumple con las restricciones
     */
    public boolean validarRestriccionesCompra(ArrayList<Tiquete> tiquetes, int maxTiquetesPorTransaccion) {
        int cantidadTiquetesIndividuales = calcularTiquetesIndividuales(tiquetes);
        
        if (cantidadTiquetesIndividuales > maxTiquetesPorTransaccion) {
            System.out.println("Error: La compra excede el límite de " + maxTiquetesPorTransaccion + " tiquetes por transacción");
            System.out.println("Tiquetes en compra: " + cantidadTiquetesIndividuales + " (límite: " + maxTiquetesPorTransaccion + ")");
            return false;
        }
        
        System.out.println("Validación exitosa: " + cantidadTiquetesIndividuales + " tiquetes dentro del límite");
        return true;
    }
    
    /**
     * Calcula la cantidad total de tiquetes individuales en una compra
     * Considera que las entradas múltiples cuentan como 1 para restricciones
     * @param tiquetes - lista de tiquetes
     * @return cantidad de "unidades" para restricciones
     */
    private int calcularTiquetesIndividuales(ArrayList<Tiquete> tiquetes) {
        // las entradas múltiples cuentan como 1 para restricciones
        return tiquetes.size(); // Cada tiquete (simple, múltiple o Deluxe) cuenta como 1 unidad
    }
}