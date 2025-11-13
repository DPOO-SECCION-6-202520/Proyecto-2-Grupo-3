package interfaz;

import modelo.Aplicacion;
import modelo.usuarios.Organizador;
import modelo.usuarios.Usuario;
import modelo.eventos.Evento;
import modelo.eventos.Venue;
import modelo.eventos.Localidad;
import modelo.eventos.Oferta;
import interfaz.util.ValidadorEntradas;

import java.util.ArrayList;
import java.util.Date;

/**
 * Menú para usuarios organizadores
 */
public class MenuOrganizador extends MenuBase {
    
    public MenuOrganizador(Aplicacion aplicacion, Usuario usuario) {
        super(aplicacion, usuario);
    }
    
    @Override
    public void mostrarMenu() {
        Organizador organizador = (Organizador) usuario;
        boolean salir = false;
        
        while (!salir) {
            mostrarEncabezado("MENÚ ORGANIZADOR");
            
            System.out.println("1. Crear nuevo evento");
            System.out.println("2. Ver mis eventos");
            System.out.println("3. Asignar tiquetes a evento");
            System.out.println("4. Crear oferta especial");
            System.out.println("5. Ver reportes de ventas");
            System.out.println("6. Ver ganancias");
            System.out.println("0. Cerrar sesión");
            
            int opcion = ValidadorEntradas.leerEntero("\nSeleccione una opción: ", 0, 6);
            
            switch (opcion) {
                case 1:
                    crearEvento(organizador);
                    break;
                case 2:
                    verMisEventos(organizador);
                    break;
                case 3:
                    asignarTiquetes(organizador);
                    break;
                case 4:
                    crearOferta(organizador);
                    break;
                case 5:
                    verReportesVentas(organizador);
                    break;
                case 6:
                    verGanancias(organizador);
                    break;
                case 0:
                    salir = true;
                    break;
            }
        }
        
        mostrarDespedida();
    }
    
    private void crearEvento(Organizador organizador) {
        mostrarEncabezado("CREAR NUEVO EVENTO");
        
        // Obtener venues aprobados
        ArrayList<Venue> venues = aplicacion.getVenuesAprobados();
        if (venues.isEmpty()) {
            System.out.println("Error: No hay venues aprobados disponibles.");
            System.out.println("Contacte al administrador para crear venues.");
            ValidadorEntradas.pausar();
            return;
        }
        
        // Datos del evento
        String id = "E" + System.currentTimeMillis();
        String nombre = ValidadorEntradas.leerString("Nombre del evento: ", 3, 100);
        
        // Seleccionar venue
        System.out.println("\nSeleccione un venue:");
        for (int i = 0; i < venues.size(); i++) {
            Venue venue = venues.get(i);
            System.out.println((i + 1) + ". " + venue.getNombre() + " - " + 
                             venue.getUbicacion() + " (Capacidad: " + venue.getCapacidad() + ")");
        }
        
        int opcionVenue = ValidadorEntradas.leerEntero("Venue: ", 1, venues.size()) - 1;
        Venue venue = venues.get(opcionVenue);
        
        // Verificar disponibilidad del venue
        System.out.println("\nVerificando disponibilidad del venue...");
        // Por simplicidad, asumimos que está disponible
        // En una implementación real, aquí se verificarían fechas
        
        // Fecha del evento (simplificado - 7 días en el futuro)
        Date fechaEvento = new Date(System.currentTimeMillis() + (7L * 24 * 60 * 60 * 1000));
        System.out.println("Fecha del evento configurada para: " + fechaEvento);
        
        // Crear evento
        Evento evento = aplicacion.crearEvento(organizador, id, nombre, fechaEvento, venue);
        if (evento != null) {
            System.out.println("¡Evento creado exitosamente!");
            System.out.println("ID del evento: " + evento.getId());
            System.out.println("Nota: El evento debe ser aprobado por un administrador.");
        } else {
            System.out.println("Error: No se pudo crear el evento.");
        }
        
        ValidadorEntradas.pausar();
    }
    
    private void verMisEventos(Organizador organizador) {
        mostrarEncabezado("MIS EVENTOS");
        
        ArrayList<Evento> eventos = organizador.getEventosCreados();
        if (eventos.isEmpty()) {
            System.out.println("No has creado ningún evento.");
        } else {
            for (Evento evento : eventos) {
                System.out.println("ID: " + evento.getId());
                System.out.println("Nombre: " + evento.getNombre());
                System.out.println("Fecha: " + evento.getFechaHora());
                System.out.println("Venue: " + evento.getVenue().getNombre());
                System.out.println("Estado: " + (evento.isAprobado() ? "APROBADO" : "PENDIENTE"));
                System.out.println("Tiquetes totales: " + evento.getTiquetes().size());
                System.out.println("Tiquetes vendidos: " + evento.getTiquetesVendidos());
                System.out.println("Porcentaje vendido: " + String.format("%.1f", evento.getPorcentajeVendido()) + "%");
                System.out.println("----------------------------------------");
            }
        }
        
        ValidadorEntradas.pausar();
    }
    
    private void asignarTiquetes(Organizador organizador) {
        mostrarEncabezado("ASIGNAR TIQUETES A EVENTO");
        
        ArrayList<Evento> eventos = organizador.getEventosCreados();
        if (eventos.isEmpty()) {
            System.out.println("No tienes eventos creados.");
            ValidadorEntradas.pausar();
            return;
        }
        
        // Seleccionar evento
        System.out.println("Seleccione un evento:");
        for (int i = 0; i < eventos.size(); i++) {
            Evento evento = eventos.get(i);
            System.out.println((i + 1) + ". " + evento.getNombre() + 
                             " (" + (evento.isAprobado() ? "Aprobado" : "Pendiente") + ")");
        }
        
        int opcionEvento = ValidadorEntradas.leerEntero("Evento: ", 1, eventos.size()) - 1;
        Evento evento = eventos.get(opcionEvento);
        
        if (!evento.isAprobado()) {
            System.out.println("Error: El evento debe estar aprobado para asignar tiquetes.");
            ValidadorEntradas.pausar();
            return;
        }
        
        // Seleccionar localidad del venue
        ArrayList<Localidad> localidades = evento.getVenue().getLocalidades();
        if (localidades.isEmpty()) {
            System.out.println("Error: El venue no tiene localidades configuradas.");
            ValidadorEntradas.pausar();
            return;
        }
        
        System.out.println("\nSeleccione una localidad:");
        for (int i = 0; i < localidades.size(); i++) {
            Localidad localidad = localidades.get(i);
            System.out.println((i + 1) + ". " + localidad.getTipoLocalidad() + 
                             " (Capacidad: " + localidad.getCapacidad() + ")");
        }
        
        int opcionLocalidad = ValidadorEntradas.leerEntero("Localidad: ", 1, localidades.size()) - 1;
        Localidad localidad = localidades.get(opcionLocalidad);
        
        // Cantidad y precio
        int cantidad = ValidadorEntradas.leerEntero("Cantidad de tiquetes: ", 1, localidad.getCapacidad());
        double precioBase = ValidadorEntradas.leerDouble("Precio base por tiquete: $", 0.01, 1000.0);
        
        // Asignar tiquetes
        organizador.asignarTiquetes(evento, localidad, cantidad, precioBase);
        aplicacion.guardarDatos();
        
        System.out.println("¡Tiquetes asignados exitosamente!");
        ValidadorEntradas.pausar();
    }
    
    private void crearOferta(Organizador organizador) {
        mostrarEncabezado("CREAR OFERTA ESPECIAL");
        
        ArrayList<Evento> eventos = organizador.getEventosCreados();
        if (eventos.isEmpty()) {
            System.out.println("No tienes eventos creados.");
            ValidadorEntradas.pausar();
            return;
        }
        
        // Seleccionar evento
        System.out.println("Seleccione un evento:");
        for (int i = 0; i < eventos.size(); i++) {
            Evento evento = eventos.get(i);
            System.out.println((i + 1) + ". " + evento.getNombre());
        }
        
        int opcionEvento = ValidadorEntradas.leerEntero("Evento: ", 1, eventos.size()) - 1;
        Evento evento = eventos.get(opcionEvento);
        
        // Datos de la oferta
        String id = "OF" + System.currentTimeMillis();
        String descripcion = ValidadorEntradas.leerString("Descripción de la oferta: ", 5, 200);
        double porcentajeDescuento = ValidadorEntradas.leerDouble("Porcentaje de descuento (0.01 - 0.50): ", 0.01, 0.50);
        
        // Fechas (simplificado)
        Date fechaInicio = new Date();
        Date fechaExpiracion = new Date(System.currentTimeMillis() + (3L * 24 * 60 * 60 * 1000)); // 3 días
        
        // Seleccionar localidad (opcional)
        System.out.println("\n¿La oferta es para una localidad específica?");
        boolean ofertaLocalidad = ValidadorEntradas.leerBooleano("Oferta para localidad específica");
        
        Localidad localidad = null;
        if (ofertaLocalidad) {
            ArrayList<Localidad> localidades = evento.getVenue().getLocalidades();
            System.out.println("Seleccione una localidad:");
            for (int i = 0; i < localidades.size(); i++) {
                System.out.println((i + 1) + ". " + localidades.get(i).getTipoLocalidad());
            }
            int opcionLocalidad = ValidadorEntradas.leerEntero("Localidad: ", 1, localidades.size()) - 1;
            localidad = localidades.get(opcionLocalidad);
        }
        
        // Crear oferta
        Oferta oferta = aplicacion.crearOferta(organizador, id, descripcion, porcentajeDescuento, 
                                              fechaInicio, fechaExpiracion, evento, localidad);
        
        if (oferta != null) {
            System.out.println("¡Oferta creada exitosamente!");
        }
        
        ValidadorEntradas.pausar();
    }
    
    private void verReportesVentas(Organizador organizador) {
        mostrarEncabezado("REPORTES DE VENTAS");
        
        ArrayList<Evento> eventos = organizador.getEventosCreados();
        if (eventos.isEmpty()) {
            System.out.println("No tienes eventos creados.");
        } else {
            for (Evento evento : eventos) {
                if (evento.isAprobado()) {
                    System.out.println("Evento: " + evento.getNombre());
                    System.out.println("  Tiquetes totales: " + evento.getTiquetes().size());
                    System.out.println("  Tiquetes vendidos: " + evento.getTiquetesVendidos());
                    System.out.println("  Porcentaje vendido: " + String.format("%.1f", evento.getPorcentajeVendido()) + "%");
                    
                    // Por localidad
                    for (Localidad localidad : evento.getVenue().getLocalidades()) {
                        int vendidosLocalidad = 0;
                        for (modelo.tiquetes.Tiquete tiquete : localidad.getTiquetesLocalidad()) {
                            if (!tiquete.estaVigente()) {
                                vendidosLocalidad++;
                            }
                        }
                        System.out.println("  " + localidad.getTipoLocalidad() + ": " + 
                                         vendidosLocalidad + "/" + localidad.getTiquetesLocalidad().size() + 
                                         " vendidos");
                    }
                    System.out.println("----------------------------------------");
                }
            }
        }
        
        ValidadorEntradas.pausar();
    }
    
    private void verGanancias(Organizador organizador) {
        mostrarEncabezado("GANANCIAS");
        
        // Usar el método existente del organizador
        organizador.revisarGanancias(aplicacion.getTodasLasCompras());
        
        ValidadorEntradas.pausar();
    }
}