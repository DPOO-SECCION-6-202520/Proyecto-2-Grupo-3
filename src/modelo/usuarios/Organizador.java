package modelo.usuarios;

import java.util.ArrayList;
import modelo.eventos.Evento;
import modelo.eventos.Venue;
import modelo.eventos.Localidad;
import modelo.eventos.Oferta;
import modelo.tiquetes.Tiquete;
import modelo.pagos.Compra;
import java.util.Date;

/**
 * Clase que representa a un Organizador de eventos.
 * Puede crear eventos, gestionar localidades, generar ofertas y ver sus ganancias.
 * También puede comprar tiquetes como un comprador normal.
 */
public class Organizador extends Usuario {
    // Lista de eventos creados por este organizador
    private ArrayList<Evento> eventosCreados;
    
    //Constructor de Organizador
    public Organizador(String login, String password) {
        // Llama al constructor padre con tipoUsuario "organizador"
        super(login, password, "organizador");
        this.eventosCreados = new ArrayList<>();
    }
    
    //Constructor vacio para persistencia
    public Organizador() {
        super();
        this.tipoUsuario = "organizador";
        this.eventosCreados = new ArrayList<>();
    }

    // ==================== MÉTODOS DE GESTIÓN DE EVENTOS ====================
    
    /**
     * Crea un nuevo evento real
     * @param id - identificador del evento
     * @param nombre - nombre del evento
     * @param fechaHora - fecha y hora del evento
     * @param venue - venue donde se realizará
     * @return el evento creado
     */
    public Evento crearEvento(String id, String nombre, Date fechaHora, Venue venue) {
        System.out.println("Organizador " + this.login + " está creando el evento: " + nombre);
        
        Evento nuevoEvento = new Evento(id, nombre, fechaHora, venue, this);
        this.eventosCreados.add(nuevoEvento);
        
        System.out.println("   Evento '" + nombre + "' creado por " + this.login);
        System.out.println("   Fecha: " + fechaHora);
        System.out.println("   Venue: " + venue.getNombre());
        
        return nuevoEvento;
    }
    
    /**
     * Asigna tiquetes a un evento en una localidad específica
     * @param evento - evento al que asignar tiquetes
     * @param localidad - localidad para los tiquetes
     * @param cantidad - cantidad de tiquetes
     * @param precioBase - precio base de los tiquetes
     */
    public void asignarTiquetes(Evento evento, Localidad localidad, int cantidad, double precioBase) {
        if (evento == null || localidad == null || cantidad <= 0) {
            System.out.println("Error: Parámetros inválidos para asignar tiquetes");
            return;
        }
        
        if (!eventosCreados.contains(evento)) {
            System.out.println("Error: El organizador no es dueño de este evento");
            return;
        }
        
        System.out.println("Asignando " + cantidad + " tiquetes al evento '" + evento.getNombre() + "'");
        System.out.println("Localidad: " + localidad.getTipoLocalidad());
        System.out.println("Precio base: $" + precioBase);
        
        for (int i = 0; i < cantidad; i++) {
            String tiqueteId = "TQ-" + evento.getId() + "-" + localidad.getId() + "-" + i;
            Tiquete tiquete = new Tiquete(tiqueteId, precioBase, evento.getFechaHora(), localidad, evento);
            evento.agregarTiquete(tiquete);
            localidad.agregarTiquete(tiquete);
        }
        
        System.out.println(cantidad + " tiquetes asignados exitosamente");
    }
    
    // ==================== MÉTODOS DE OFERTAS ====================
    
    /**
     * Genera una oferta especial para un evento o localidad específica
     * @param id - identificador único de la oferta
     * @param descripcion - descripción de la oferta
     * @param porcentajeDescuento - porcentaje de descuento (ej: 0.2 para 20%)
     * @param fechaInicio - fecha de inicio de la oferta
     * @param fechaExpiracion - fecha de expiración de la oferta
     * @param evento - evento al que aplica la oferta
     * @param localidad - localidad específica (opcional, si es null aplica a todo el evento)
     * @return la oferta creada o null si hubo error
     */
    public Oferta generarOferta(String id, String descripcion, double porcentajeDescuento,
                               Date fechaInicio, Date fechaExpiracion, Evento evento, Localidad localidad) {
        // Validar que el organizador es dueño del evento
        if (!eventosCreados.contains(evento)) {
            System.out.println("Error: El organizador no es dueño de este evento");
            return null;
        }
        
        // Validar que la localidad pertenece al venue del evento (si se especifica localidad)
        if (localidad != null && !evento.getVenue().getLocalidades().contains(localidad)) {
            System.out.println("Error: La localidad no pertenece al venue del evento");
            return null;
        }
        
        try {
            // Crear la oferta usando la clase Oferta que ya implementaste
            Oferta nuevaOferta;
            if (localidad != null) {
                nuevaOferta = new Oferta(id, descripcion, porcentajeDescuento, fechaInicio, fechaExpiracion, evento, localidad);
                // Agregar la oferta a la localidad
                localidad.agregarOferta(nuevaOferta);
            } else {
                nuevaOferta = new Oferta(id, descripcion, porcentajeDescuento, fechaInicio, fechaExpiracion, evento);
                // Agregar la oferta al evento
                evento.agregarOferta(nuevaOferta);
            }
            
            System.out.println("   Oferta creada exitosamente por " + this.login);
            System.out.println("   Descripción: " + descripcion);
            System.out.println("   Descuento: " + (porcentajeDescuento * 100) + "%");
            System.out.println("   Evento: " + evento.getNombre());
            if (localidad != null) {
                System.out.println("   Localidad: " + localidad.getTipoLocalidad());
            }
            System.out.println("   Válida desde: " + fechaInicio + " hasta: " + fechaExpiracion);
            
            return nuevaOferta;
            
        } catch (IllegalArgumentException e) {
            System.out.println("Error al crear oferta: " + e.getMessage());
            return null;
        }
    }
        
    // ==================== MÉTODOS FINANCIEROS ====================
    
    /**
     * Revisa las ganancias reales de los eventos organizados
     * @param compras - lista de compras del sistema para calcular ganancias
     */
    public void revisarGanancias(ArrayList<Compra> compras) {
        System.out.println("=== GANANCIAS DEL ORGANIZADOR: " + this.login + " ===");
        
        double gananciasTotales = 0;
        int eventosConVentas = 0;
        
        for (Evento evento : eventosCreados) {
            if (evento.isAprobado()) {
                double gananciasEvento = calcularGananciasEvento(evento, compras);
                if (gananciasEvento > 0) {
                    eventosConVentas++;
                    gananciasTotales += gananciasEvento;
                    
                    System.out.println("Evento: " + evento.getNombre() + 
                                     " - Ganancias: $" + String.format("%.2f", gananciasEvento) +
                                     " - Tiquetes vendidos: " + contarTiquetesVendidos(evento, compras));
                }
            }
        }
        
        System.out.println("--- RESUMEN ---");
        System.out.println("Eventos con ventas: " + eventosConVentas + " de " + eventosCreados.size());
        System.out.println("GANANCIAS TOTALES: $" + String.format("%.2f", gananciasTotales));
    }
    
    /**
     * Calcula ganancias para un evento específico
     */
    private double calcularGananciasEvento(Evento evento, ArrayList<Compra> compras) {
        double ganancias = 0;
        for (Compra compra : compras) {
            if ("aprobada".equals(compra.getEstado()) && compraContieneEvento(compra, evento)) {
                // Organizador recibe solo el precio base (sin cargos adicionales)
                for (Tiquete tiquete : compra.getTiquetes()) {
                    if (tiquete.getEvento().equals(evento)) {
                        ganancias += tiquete.getPrecioBase();
                    }
                }
            }
        }
        return ganancias;
    }
    
    /**
     * Cuenta tiquetes vendidos para un evento
     */
    private int contarTiquetesVendidos(Evento evento, ArrayList<Compra> compras) {
        int vendidos = 0;
        for (Compra compra : compras) {
            if ("aprobada".equals(compra.getEstado()) && compraContieneEvento(compra, evento)) {
                for (Tiquete tiquete : compra.getTiquetes()) {
                    if (tiquete.getEvento().equals(evento)) {
                        vendidos++;
                    }
                }
            }
        }
        return vendidos;
    }
    
    /**
     * Verifica si una compra contiene tiquetes de un evento
     */
    private boolean compraContieneEvento(Compra compra, Evento evento) {
        for (Tiquete tiquete : compra.getTiquetes()) {
            if (tiquete.getEvento().equals(evento)) {
                return true;
            }
        }
        return false;
    }
    
    // ==================== MÉTODOS ADMINISTRATIVOS ====================
    
    /**
     * Sugiere un nuevo venue al administrador
     * @param id - identificador del venue
     * @param nombre - nombre del venue
     * @param ubicacion - ubicación
     * @param capacidad - capacidad máxima
     * @return el venue sugerido (no aprobado)
     */
    public Venue sugerirVenue(String id, String nombre, String ubicacion, int capacidad) {
        System.out.println("   Venue sugerido por el organizador: " + this.login);
        System.out.println("   Nombre: " + nombre);
        System.out.println("   Ubicación: " + ubicacion);
        System.out.println("   Capacidad: " + capacidad);
        
        Venue venueSugerido = new Venue(id, nombre, ubicacion, capacidad);
        // El venue queda como no aprobado, pendiente de aprobación del administrador
        
        return venueSugerido;
    }
    
    /**
     * Solicita la cancelación de un evento específico
     * @param evento - evento a cancelar
     * @param motivo - motivo de la cancelación
     */
    public void solicitarCancelarEvento(Evento evento, String motivo) {
        if (evento == null || !eventosCreados.contains(evento)) {
            System.out.println("Error: Evento inválido o no pertenece al organizador");
            return;
        }
        
        System.out.println("   Solicitud de cancelación de evento enviada por: " + this.login);
        System.out.println("   Evento: " + evento.getNombre());
        System.out.println("   Motivo: " + motivo);
        System.out.println("Pendiente de autorización del administrador");
        
        // La solicitud queda pendiente - el administrador debe autorizar
    }
    
    // ==================== MÉTODOS DE GESTIÓN DE LISTA DE EVENTOS ====================
    
    /**
     * Agrega un evento a la lista del organizador
     * @param evento - evento a agregar
     */
    public void agregarEvento(Evento evento) {
        if (evento != null && !eventosCreados.contains(evento)) {
            this.eventosCreados.add(evento);
            System.out.println("Evento '" + evento.getNombre() + "' agregado al organizador " + this.login);
        }
    }
    
    /**
     * @return lista de eventos creados por este organizador
     */
    public ArrayList<Evento> getEventosCreados() {
        return new ArrayList<>(eventosCreados);
    }
    
    /**
     * Muestra los eventos creados por consola
     */
    public void mostrarEventosCreados() {
        System.out.println("=== Eventos de " + this.login + " ===");
        if (eventosCreados.isEmpty()) {
            System.out.println("No hay eventos creados.");
        } else {
            for (int i = 0; i < eventosCreados.size(); i++) {
                Evento evento = eventosCreados.get(i);
                String estado = evento.isAprobado() ? "APROBADO" : "PENDIENTE";
                System.out.println((i + 1) + ". " + evento.getNombre() + 
                                 " - " + estado + 
                                 " - Tiquetes: " + evento.getTiquetes().size());
            }
        }
    }
    
    /**
     * Obtiene el porcentaje de venta de un evento específico
     * @param evento - evento a consultar
     * @param compras - compras del sistema
     * @return porcentaje de venta (0-100)
     */
    public double getPorcentajeVentaEvento(Evento evento, ArrayList<Compra> compras) {
        int tiquetesVendidos = contarTiquetesVendidos(evento, compras);
        int tiquetesTotales = evento.getTiquetes().size();
        
        if (tiquetesTotales == 0) return 0;
        return (tiquetesVendidos * 100.0) / tiquetesTotales;
    }
    
    /**
     * Revisa ganancias y porcentaje de venta por localidad de un evento
     */
    public void revisarGananciasPorLocalidad(Evento evento, ArrayList<Compra> compras) {
        System.out.println("=== GANANCIAS POR LOCALIDAD - " + evento.getNombre() + " ===");
        
        for (Localidad localidad : evento.getVenue().getLocalidades()) {
            double gananciasLocalidad = calcularGananciasLocalidad(evento, localidad, compras);
            int vendidos = contarTiquetesVendidosLocalidad(evento, localidad, compras);
            int total = localidad.getTiquetesLocalidad().size();
            double porcentaje = total > 0 ? (vendidos * 100.0) / total : 0;
            
            System.out.println("Localidad: " + localidad.getTipoLocalidad() +
                             " - Ganancias: $" + String.format("%.2f", gananciasLocalidad) +
                             " - Vendidos: " + vendidos + "/" + total +
                             " (" + String.format("%.1f", porcentaje) + "%)");
        }
    }
    
    /**
     * Calcula ganancias para una localidad específica de un evento
     */
    private double calcularGananciasLocalidad(Evento evento, Localidad localidad, ArrayList<Compra> compras) {
        double ganancias = 0;
        for (Compra compra : compras) {
            if ("aprobada".equals(compra.getEstado())) {
                for (Tiquete tiquete : compra.getTiquetes()) {
                    if (tiquete.getEvento().equals(evento) && tiquete.getLocalidad().equals(localidad)) {
                        ganancias += tiquete.getPrecioBase();
                    }
                }
            }
        }
        return ganancias;
    }
    
    /**
     * Cuenta tiquetes vendidos para una localidad específica
     */
    private int contarTiquetesVendidosLocalidad(Evento evento, Localidad localidad, ArrayList<Compra> compras) {
        int vendidos = 0;
        for (Compra compra : compras) {
            if ("aprobada".equals(compra.getEstado())) {
                for (Tiquete tiquete : compra.getTiquetes()) {
                    if (tiquete.getEvento().equals(evento) && tiquete.getLocalidad().equals(localidad)) {
                        vendidos++;
                    }
                }
            }
        }
        return vendidos;
    }
    
    @Override
    public String toString() {
        return "Organizador{" +
                "login='" + login + '\'' +
                ", saldoVirtual=" + saldoVirtual +
                ", cantidadEventos=" + eventosCreados.size() +
                '}';
    }
}