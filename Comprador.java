package modelo.usuarios;

import java.util.ArrayList;
import modelo.tiquetes.Tiquete;
import modelo.eventos.Evento;
import modelo.eventos.Localidad;
import modelo.pagos.Pagos;
import modelo.pagos.Compra;
import java.util.Date;

/**
 * Clase que representa a un Comprador en el sistema.
 * Puede comprar tiquetes, solicitar reembolsos y ver su historial.
 */
public class Comprador extends Usuario {
    // Atributo específico del comprador: historial de tiquetes comprados
    private ArrayList<Tiquete> historialTiquetes;
    
    //Constructor de Comprador
    public Comprador(String login, String password) {
        // Llama al constructor padre con tipoUsuario "comprador"
        super(login, password, "comprador");
        this.historialTiquetes = new ArrayList<>();
    }

    //Constructor vacio para persistencia
    public Comprador() {
        super();
        this.tipoUsuario = "comprador";
        this.historialTiquetes = new ArrayList<>();
    }
    
    // ==================== MÉTODOS DE COMPRA ====================
    
    /**
     * Compra tiquetes para un evento y localidad específicos
     * @param evento - evento para el cual comprar tiquetes
     * @param localidad - localidad deseada
     * @param cantidad - cantidad de tiquetes a comprar
     * @param porcentajeAdicional - porcentaje de servicio del administrador
     * @param cobroFijo - cargo fijo del administrador
     * @return lista de tiquetes comprados o lista vacía si falló
     */
    public ArrayList<Tiquete> comprarTiquete(Evento evento, Localidad localidad, int cantidad, 
                                           double porcentajeAdicional, double cobroFijo) {
        System.out.println("Comprador " + this.login + " está comprando " + cantidad + 
                         " tiquetes para evento: " + evento.getNombre());
        
        // Validaciones básicas
        if (evento == null || localidad == null || cantidad <= 0) {
            System.out.println("Error: Parámetros inválidos para la compra");
            return new ArrayList<>();
        }
        
        if (!evento.isAprobado()) {
            System.out.println("Error: El evento no está aprobado");
            return new ArrayList<>();
        }
        
        if (!localidad.hayDisponibilidad() || localidad.getTiquetesDisponibles() < cantidad) {
            System.out.println("Error: No hay suficientes tiquetes disponibles en la localidad");
            return new ArrayList<>();
        }
        
        // Crear tiquetes para la compra
        ArrayList<Tiquete> tiquetesAComprar = new ArrayList<>();
        for (int i = 0; i < cantidad; i++) {
            String tiqueteId = "TQ-" + evento.getId() + "-" + localidad.getId() + "-" + System.currentTimeMillis() + "-" + i;
            double precioBase = localidad.getPrecioBase();
            Tiquete tiquete = new Tiquete(tiqueteId, precioBase, evento.getFechaHora(), localidad, evento);
            tiquetesAComprar.add(tiquete);
        }
        
        // Usar el servicio de pagos para calcular el total
        Pagos servicioPagos = Pagos.getInstancia();
        double montoTotal = servicioPagos.calcularPrecioTotal(tiquetesAComprar, porcentajeAdicional, cobroFijo);
        
        // Procesar pago con saldo virtual
        if (servicioPagos.procesarPagoConSaldo(this, montoTotal)) {
            // Pago exitoso - completar la compra
            
            // Crear compra
            String compraId = "COMP-" + System.currentTimeMillis();
            Compra compra = new Compra(compraId, new Date(), montoTotal, tiquetesAComprar, this);
            compra.setEstado("aprobada");
            
            // Agregar tiquetes al historial del comprador
            for (Tiquete tiquete : tiquetesAComprar) {
                agregarTiqueteAlHistorial(tiquete);
                // Agregar tiquete a la localidad (actualizar disponibilidad)
                localidad.agregarTiquete(tiquete);
            }
            
            System.out.println("Compra exitosa: " + cantidad + " tiquetes comprados por " + 
                             this.login + " - Total: $" + montoTotal);
            
            return tiquetesAComprar;
        } else {
            System.out.println("Compra fallida: Saldo insuficiente");
            return new ArrayList<>();
        }
    }

    /**
     * Transfiere un tiquete a otro usuario verificando contraseña
     * @param tiquete - tiquete a transferir
     * @param usuarioDestino - usuario que recibirá el tiquete
     * @param password - contraseña para verificar identidad (como requiere el proyecto)
     * @return true si la transferencia fue exitosa
     */
    public boolean transferirTiquete(Tiquete tiquete, Usuario usuarioDestino, String password) {
        System.out.println("Iniciando transferencia de tiquete: " + tiquete.getId());
        
        // Verificar contraseña del usuario que transfiere
        if (!this.validarCredenciales(this.login, password)) {
            System.out.println("Error: Contraseña incorrecta para transferencia");
            return false;
        }
        
        // Verificar que el tiquete pertenece al usuario
        if (!historialTiquetes.contains(tiquete)) {
            System.out.println("Error: El tiquete no pertenece al usuario");
            return false;
        }
        
        // Verificar que el tiquete es transferible
        if (!tiquete.getEsTransferible()) {
            System.out.println("Error: Este tiquete no es transferible");
            return false;
        }
        
        // Verificar que el tiquete está vigente
        if (!tiquete.estaVigente()) {
            System.out.println("Error: El tiquete no está vigente para transferencia");
            return false;
        }
        
        // Verificar que el destino es un Comprador
        if (!(usuarioDestino instanceof Comprador)) {
            System.out.println("Error: Solo se puede transferir a otros compradores");
            return false;
        }
        
        // Realizar transferencia
        this.historialTiquetes.remove(tiquete);
        ((Comprador) usuarioDestino).agregarTiqueteAlHistorial(tiquete);
        
        System.out.println("Transferencia exitosa: Tiquete " + tiquete.getId() + 
                         " transferido de " + this.login + " a " + usuarioDestino.getLogin());
        return true;
    }
    
    /**
     * Solicita un reembolso para un tiquete específico
     */
    public void solicitarReembolso(Tiquete tiquete) {
        if (tiquete == null || !historialTiquetes.contains(tiquete)) {
            System.out.println("Error: Tiquete no válido o no pertenece al usuario");
            return;
        }
        
        if (!tiquete.estaVigente()) {
            System.out.println("Error: El tiquete no está vigente para reembolso");
            return;
        }
        
        System.out.println("Solicitud de reembolso enviada por: " + this.login);
        System.out.println("Tiquete: " + tiquete.getId());
        System.out.println("Evento: " + tiquete.getEvento().getNombre());
        System.out.println("Precio base: $" + tiquete.getPrecioBase());
        
        // La solicitud queda pendiente de aprobación del administrador
        // TODO: En una implementación completa, aquí se crearía una solicitud en PersistenciaSolicitudes
    }
    
    // ==================== MÉTODOS DE GESTIÓN DE HISTORIAL ====================
    
    /**
     * Agrega un tiquete al historial del comprador
     * @param tiquete - tiquete a agregar
     */
    public void agregarTiqueteAlHistorial(Tiquete tiquete) {
        if (tiquete != null) {
            this.historialTiquetes.add(tiquete);
            System.out.println("Tiquete " + tiquete.getId() + " agregado al historial de " + this.login);
        }
    }
    
    /**
     * @return todo el historial de tiquetes del comprador
     */
    public ArrayList<Tiquete> getHistorialTiquetes() {
        return new ArrayList<>(historialTiquetes);
    }
    
    /**
     * Muestra el historial de tiquetes por consola
     */
    public void mostrarHistorialTiquetes() {
        System.out.println("=== Historial de Tiquetes de " + this.login + " ===");
        if (historialTiquetes.isEmpty()) {
            System.out.println("No hay tiquetes en el historial.");
        } else {
            for (int i = 0; i < historialTiquetes.size(); i++) {
                Tiquete tiquete = historialTiquetes.get(i);
                String estado = tiquete.estaVigente() ? "VIGENTE" : "USADO/VENCIDO";
                System.out.println((i + 1) + ". " + tiquete + " - " + estado);
            }
        }
    }
    
    /**
     * Obtiene los tiquetes vigentes (no vencidos)
     * @return lista de tiquetes vigentes
     */
    public ArrayList<Tiquete> getTiquetesVigentes() {
        ArrayList<Tiquete> vigentes = new ArrayList<>();
        for (Tiquete tiquete : historialTiquetes) {
            if (tiquete.estaVigente()) {
                vigentes.add(tiquete);
            }
        }
        return vigentes;
    }
    
    /**
     * Obtiene el saldo virtual del comprador
     * @return saldo virtual
     */
    public double getSaldoVirtual() {
        return saldoVirtual;
    }
    
    @Override
    public String toString() {
        return "Comprador{" +
                "login='" + login + '\'' +
                ", saldoVirtual=" + saldoVirtual +
                ", cantidadTiquetes=" + historialTiquetes.size() +
                '}';
    }
}