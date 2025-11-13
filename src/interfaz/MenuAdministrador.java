package interfaz;

import modelo.Aplicacion;
import modelo.usuarios.Administrador;
import modelo.usuarios.Usuario;
import modelo.eventos.Evento;
import modelo.eventos.Venue;
import modelo.tiquetes.TiqueteReventa;
import interfaz.util.ValidadorEntradas;
import modelo.eventos.Localidad;

import java.util.ArrayList;

/**
 * Menú para usuarios administradores
 */
public class MenuAdministrador extends MenuBase {
    
    public MenuAdministrador(Aplicacion aplicacion, Usuario usuario) {
        super(aplicacion, usuario);
    }
    
    @Override
    public void mostrarMenu() {
        Administrador admin = (Administrador) usuario;
        boolean salir = false;
        
        while (!salir) {
            mostrarEncabezado("MENÚ ADMINISTRADOR");
            
            System.out.println("1. Crear nuevo venue");
            System.out.println("2. Aprobar eventos pendientes");
            System.out.println("3. Aprobar venues pendientes");
            System.out.println("4. Ver reportes de ganancias");
            System.out.println("5. Eliminar oferta de reventa");
            System.out.println("6. Configurar tarifas del sistema");
            System.out.println("0. Cerrar sesión");
            
            int opcion = ValidadorEntradas.leerEntero("\nSeleccione una opción: ", 0, 6);
            
            switch (opcion) {
                case 1:
                    crearVenue(admin);
                    break;
                case 2:
                    aprobarEventos(admin);
                    break;
                case 3:
                    aprobarVenues(admin);
                    break;
                case 4:
                    verReportesGanancias(admin);
                    break;
                case 5:
                    eliminarOfertaReventa(admin);
                    break;
                case 6:
                    configurarTarifas(admin);
                    break;
                case 0:
                    salir = true;
                    break;
            }
        }
        
        mostrarDespedida();
    }
    
    private void crearVenue(Administrador admin) {
        mostrarEncabezado("CREAR NUEVO VENUE");
        
        String id = "V" + System.currentTimeMillis();
        String nombre = ValidadorEntradas.leerString("Nombre del venue: ", 3, 100);
        String ubicacion = ValidadorEntradas.leerString("Ubicación: ", 5, 200);
        int capacidad = ValidadorEntradas.leerEntero("Capacidad máxima: ", 1, 1000000);
        
        // Crear venue
        Venue venue = aplicacion.crearVenue(admin, id, nombre, ubicacion, capacidad);
        if (venue != null) {
            System.out.println("¡Venue creado exitosamente!");
            
            // Preguntar si desea agregar localidades
            boolean agregarLocalidades = ValidadorEntradas.leerBooleano("¿Desea agregar localidades ahora?");
            if (agregarLocalidades) {
                agregarLocalidadesAVenue(venue);
            }
        }
        
        ValidadorEntradas.pausar();
    }
    
    private void agregarLocalidadesAVenue(Venue venue) {
        System.out.println("\n--- AGREGAR LOCALIDADES AL VENUE ---");
        
        boolean continuar = true;
        while (continuar) {
            String idLocalidad = "L" + System.currentTimeMillis();
            String tipoLocalidad = ValidadorEntradas.leerString("Tipo de localidad (ej: VIP, General, Platea): ");
            boolean numerada = ValidadorEntradas.leerBooleano("¿Es localidad numerada?");
            int capacidad = ValidadorEntradas.leerEntero("Capacidad de la localidad: ", 1, venue.getCapacidad());
            double precioBase = ValidadorEntradas.leerDouble("Precio base: $", 0.01, 1000.0);
            
            // Crear localidad
            Localidad localidad = venue.crearLocalidad(idLocalidad, tipoLocalidad, numerada, capacidad, precioBase);
            if (localidad != null) {
                System.out.println("Localidad '" + tipoLocalidad + "' agregada exitosamente.");
            }
            
            continuar = ValidadorEntradas.leerBooleano("¿Agregar otra localidad?");
        }
        
        aplicacion.guardarDatos();
    }
    
    private void aprobarEventos(Administrador admin) {
        mostrarEncabezado("APROBAR EVENTOS PENDIENTES");
        
        ArrayList<Evento> eventos = aplicacion.getTodosLosEventos();
        ArrayList<Evento> eventosPendientes = new ArrayList<>();
        
        // Filtrar eventos pendientes
        for (Evento evento : eventos) {
            if (!evento.isAprobado() && !evento.isCancelado()) {
                eventosPendientes.add(evento);
            }
        }
        
        if (eventosPendientes.isEmpty()) {
            System.out.println("No hay eventos pendientes de aprobación.");
            ValidadorEntradas.pausar();
            return;
        }
        
        System.out.println("Eventos pendientes de aprobación:");
        for (int i = 0; i < eventosPendientes.size(); i++) {
            Evento evento = eventosPendientes.get(i);
            System.out.println((i + 1) + ". " + evento.getNombre());
            System.out.println("   Organizador: " + evento.getOrganizador().getLogin());
            System.out.println("   Fecha: " + evento.getFechaHora());
            System.out.println("   Venue: " + evento.getVenue().getNombre());
            System.out.println("----------------------------------------");
        }
        
        int opcion = ValidadorEntradas.leerEntero("Seleccione evento a aprobar (0 para cancelar): ", 0, eventosPendientes.size());
        
        if (opcion > 0) {
            Evento evento = eventosPendientes.get(opcion - 1);
            boolean aprobar = ValidadorEntradas.leerBooleano("¿Aprobar el evento '" + evento.getNombre() + "'?");
            
            if (aprobar) {
                aplicacion.aprobarEvento(admin, evento);
                System.out.println("Evento aprobado exitosamente.");
            } else {
                System.out.println("Evento no aprobado.");
            }
        }
        
        ValidadorEntradas.pausar();
    }
    
    private void aprobarVenues(Administrador admin) {
        mostrarEncabezado("APROBAR VENUES PENDIENTES");
        
        ArrayList<Venue> venues = aplicacion.getVenues();
        ArrayList<Venue> venuesPendientes = new ArrayList<>();
        
        // Filtrar venues pendientes
        for (Venue venue : venues) {
            if (!venue.isAprobado()) {
                venuesPendientes.add(venue);
            }
        }
        
        if (venuesPendientes.isEmpty()) {
            System.out.println("No hay venues pendientes de aprobación.");
            ValidadorEntradas.pausar();
            return;
        }
        
        System.out.println("Venues pendientes de aprobación:");
        for (int i = 0; i < venuesPendientes.size(); i++) {
            Venue venue = venuesPendientes.get(i);
            System.out.println((i + 1) + ". " + venue.getNombre());
            System.out.println("   Ubicación: " + venue.getUbicacion());
            System.out.println("   Capacidad: " + venue.getCapacidad());
            System.out.println("   Localidades: " + venue.getLocalidades().size());
            System.out.println("----------------------------------------");
        }
        
        int opcion = ValidadorEntradas.leerEntero("Seleccione venue a aprobar (0 para cancelar): ", 0, venuesPendientes.size());
        
        if (opcion > 0) {
            Venue venue = venuesPendientes.get(opcion - 1);
            boolean aprobar = ValidadorEntradas.leerBooleano("¿Aprobar el venue '" + venue.getNombre() + "'?");
            
            if (aprobar) {
                aplicacion.aprobarVenue(admin, venue);
                System.out.println("Venue aprobado exitosamente.");
            } else {
                System.out.println("Venue no aprobado.");
            }
        }
        
        ValidadorEntradas.pausar();
    }
    
    private void verReportesGanancias(Administrador admin) {
        mostrarEncabezado("REPORTES DE GANANCIAS");
        
        // Usar el método existente del administrador
        admin.observarGanancias(aplicacion.getTodasLasCompras());
        
        ValidadorEntradas.pausar();
    }
    
    private void eliminarOfertaReventa(Administrador admin) {
        mostrarEncabezado("ELIMINAR OFERTA DE REVENTA");
        
        ArrayList<TiqueteReventa> reventasActivas = aplicacion.getTiquetesEnReventa();
        if (reventasActivas.isEmpty()) {
            System.out.println("No hay ofertas de reventa activas.");
            ValidadorEntradas.pausar();
            return;
        }
        
        System.out.println("Ofertas de reventa activas:");
        for (int i = 0; i < reventasActivas.size(); i++) {
            TiqueteReventa reventa = reventasActivas.get(i);
            System.out.println((i + 1) + ". " + reventa.getTiquete().getId() + " | " +
                             reventa.getTiquete().getEvento().getNombre() + " | $" +
                             reventa.getPrecioReventa() + " | Vendedor: " +
                             reventa.getVendedor().getLogin());
        }
        
        int opcion = ValidadorEntradas.leerEntero("Seleccione oferta a eliminar (0 para cancelar): ", 0, reventasActivas.size());
        
        if (opcion > 0) {
            TiqueteReventa reventa = reventasActivas.get(opcion - 1);
            boolean eliminar = ValidadorEntradas.leerBooleano("¿Eliminar esta oferta de reventa?");
            
            if (eliminar) {
                aplicacion.borrarOfertaReventa(admin, reventa);
                System.out.println("Oferta eliminada exitosamente.");
            } else {
                System.out.println("Operación cancelada.");
            }
        }
        
        ValidadorEntradas.pausar();
    }
    
    private void configurarTarifas(Administrador admin) {
        mostrarEncabezado("CONFIGURAR TARIFAS DEL SISTEMA");
        
        System.out.println("Tarifas actuales:");
        System.out.println("Porcentaje adicional: " + (admin.getPorcentajeAdicional() * 100) + "%");
        System.out.println("Cobro fijo por tiquete: $" + admin.getCobroFijo());
        
        System.out.println("\n¿Desea modificar las tarifas?");
        boolean modificar = ValidadorEntradas.leerBooleano("Modificar tarifas");
        
        if (modificar) {
            double nuevoPorcentaje = ValidadorEntradas.leerDouble("Nuevo porcentaje adicional (0-1): ", 0, 1);
            double nuevoCobroFijo = ValidadorEntradas.leerDouble("Nuevo cobro fijo: $", 0, 100);
            
            admin.fijarPorcentajeAdicional(nuevoPorcentaje);
            admin.fijarCobroFijo(nuevoCobroFijo);
            aplicacion.guardarDatos();
            
            System.out.println("¡Tarifas actualizadas exitosamente!");
        }
        
        ValidadorEntradas.pausar();
    }
}