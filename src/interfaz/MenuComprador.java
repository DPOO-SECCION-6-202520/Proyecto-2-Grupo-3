package interfaz;

import modelo.Aplicacion;
import modelo.usuarios.Comprador;
import modelo.usuarios.Usuario;
import modelo.eventos.Evento;
import modelo.eventos.Localidad;
import modelo.tiquetes.Tiquete;
import modelo.tiquetes.TiqueteReventa;
import modelo.tiquetes.Contraoferta;
import interfaz.util.ValidadorEntradas;

import java.util.ArrayList;

/**
 * Menú para usuarios compradores
 */
public class MenuComprador extends MenuBase {
    
    public MenuComprador(Aplicacion aplicacion, Usuario usuario) {
        super(aplicacion, usuario);
    }
    
    @Override
    public void mostrarMenu() {
        Comprador comprador = (Comprador) usuario;
        boolean salir = false;
        
        while (!salir) {
            mostrarEncabezado("MENÚ COMPRADOR");
            
            System.out.println("1. Ver eventos disponibles");
            System.out.println("2. Comprar tiquetes");
            System.out.println("3. Ver mis tiquetes");
            System.out.println("4. Poner tiquete en reventa");
            System.out.println("5. Ver tiquetes en reventa");
            System.out.println("6. Comprar tiquete en reventa");
            System.out.println("7. Hacer contraoferta");
            System.out.println("8. Ver mis contraofertas");
            System.out.println("9. Ver saldo virtual");
            System.out.println("10. Recargar saldo");
            System.out.println("0. Cerrar sesión");
            
            int opcion = ValidadorEntradas.leerEntero("\nSeleccione una opción: ", 0, 10);
            
            switch (opcion) {
                case 1:
                    verEventosDisponibles();
                    break;
                case 2:
                    comprarTiquetes(comprador);
                    break;
                case 3:
                    verMisTiquetes(comprador);
                    break;
                case 4:
                    ponerTiqueteEnReventa(comprador);
                    break;
                case 5:
                    verTiquetesEnReventa();
                    break;
                case 6:
                    comprarTiqueteReventa(comprador);
                    break;
                case 7:
                    hacerContraoferta(comprador);
                    break;
                case 8:
                    verMisContraofertas(comprador);
                    break;
                case 9:
                    verSaldoVirtual(comprador);
                    break;
                case 10:
                    recargarSaldo(comprador);
                    break;
                case 0:
                    salir = true;
                    break;
            }
        }
        
        mostrarDespedida();
    }
    
    private void verEventosDisponibles() {
        mostrarEncabezado("EVENTOS DISPONIBLES");
        
        ArrayList<Evento> eventos = aplicacion.getEventosDisponibles();
        if (eventos.isEmpty()) {
            System.out.println("No hay eventos disponibles en este momento.");
        } else {
            for (int i = 0; i < eventos.size(); i++) {
                Evento evento = eventos.get(i);
                System.out.println((i + 1) + ". " + evento.getNombre());
                System.out.println("   Fecha: " + evento.getFechaHora());
                System.out.println("   Lugar: " + evento.getVenue().getNombre());
                System.out.println("   Tiquetes disponibles: " + evento.getTiquetesDisponibles());
                System.out.println("   Precios: ");
                for (Localidad localidad : evento.getVenue().getLocalidades()) {
                    if (localidad.hayDisponibilidad()) {
                        System.out.println("     - " + localidad.getTipoLocalidad() + ": $" + 
                                         localidad.getPrecioBase() + " (" + 
                                         localidad.getTiquetesDisponibles() + " disponibles)");
                    }
                }
                System.out.println();
            }
        }
        
        ValidadorEntradas.pausar();
    }
    
    private void comprarTiquetes(Comprador comprador) {
        mostrarEncabezado("COMPRAR TIQUETES");
        
        ArrayList<Evento> eventos = aplicacion.getEventosDisponibles();
        if (eventos.isEmpty()) {
            System.out.println("No hay eventos disponibles para comprar.");
            ValidadorEntradas.pausar();
            return;
        }
        
        // Seleccionar evento
        System.out.println("Seleccione un evento:");
        for (int i = 0; i < eventos.size(); i++) {
            System.out.println((i + 1) + ". " + eventos.get(i).getNombre());
        }
        
        int opcionEvento = ValidadorEntradas.leerEntero("Evento: ", 1, eventos.size()) - 1;
        Evento evento = eventos.get(opcionEvento);
        
        // Seleccionar localidad
        ArrayList<Localidad> localidades = evento.getVenue().getLocalidades();
        System.out.println("\nSeleccione una localidad:");
        for (int i = 0; i < localidades.size(); i++) {
            Localidad localidad = localidades.get(i);
            if (localidad.hayDisponibilidad()) {
                System.out.println((i + 1) + ". " + localidad.getTipoLocalidad() + 
                                 " - $" + localidad.getPrecioBase() + 
                                 " (" + localidad.getTiquetesDisponibles() + " disponibles)");
            }
        }
        
        int opcionLocalidad = ValidadorEntradas.leerEntero("Localidad: ", 1, localidades.size()) - 1;
        Localidad localidad = localidades.get(opcionLocalidad);
        
        if (!localidad.hayDisponibilidad()) {
            System.out.println("Error: No hay tiquetes disponibles en esta localidad.");
            ValidadorEntradas.pausar();
            return;
        }
        
        // Seleccionar cantidad
        int maxTiquetes = Math.min(localidad.getTiquetesDisponibles(), 10); // Máximo 10 por transacción
        int cantidad = ValidadorEntradas.leerEntero("Cantidad de tiquetes (1-" + maxTiquetes + "): ", 1, maxTiquetes);
        
        // Obtener porcentaje adicional y cobro fijo del administrador
        double porcentajeAdicional = 0.15; // Valor por defecto
        double cobroFijo = 5.0; // Valor por defecto
        
        // Realizar compra
        aplicacion.comprarTiquetes(comprador, evento, localidad, cantidad, porcentajeAdicional, cobroFijo);
        
        ValidadorEntradas.pausar();
    }
    
    private void verMisTiquetes(Comprador comprador) {
        mostrarEncabezado("MIS TIQUETES");
        
        ArrayList<Tiquete> tiquetes = comprador.getHistorialTiquetes();
        if (tiquetes.isEmpty()) {
            System.out.println("No tienes tiquetes en tu historial.");
        } else {
            System.out.println("Total de tiquetes: " + tiquetes.size());
            System.out.println("\nTiquetes vigentes:");
            int contadorVigentes = 0;
            for (Tiquete tiquete : tiquetes) {
                if (tiquete.estaVigente()) {
                    contadorVigentes++;
                    System.out.println(" - " + tiquete.getId() + " | " + 
                                     tiquete.getEvento().getNombre() + " | " +
                                     tiquete.getLocalidad().getTipoLocalidad() + " | $" +
                                     tiquete.getPrecioBase() + " | " +
                                     (tiquete.getEsTransferible() ? "Transferible" : "No transferible"));
                }
            }
            
            if (contadorVigentes == 0) {
                System.out.println("No tienes tiquetes vigentes.");
            }
        }
        
        ValidadorEntradas.pausar();
    }
    
    private void ponerTiqueteEnReventa(Comprador comprador) {
        mostrarEncabezado("PONER TIQUETE EN REVENTA");
        
        ArrayList<Tiquete> tiquetesVigentes = comprador.getTiquetesVigentes();
        if (tiquetesVigentes.isEmpty()) {
            System.out.println("No tienes tiquetes vigentes para revender.");
            ValidadorEntradas.pausar();
            return;
        }
        
        // Seleccionar tiquete
        System.out.println("Seleccione un tiquete para revender:");
        for (int i = 0; i < tiquetesVigentes.size(); i++) {
            Tiquete tiquete = tiquetesVigentes.get(i);
            System.out.println((i + 1) + ". " + tiquete.getId() + " | " + 
                             tiquete.getEvento().getNombre() + " | " +
                             tiquete.getLocalidad().getTipoLocalidad() + " | Precio original: $" +
                             tiquete.getPrecioBase());
        }
        
        int opcionTiquete = ValidadorEntradas.leerEntero("Tiquete: ", 1, tiquetesVigentes.size()) - 1;
        Tiquete tiquete = tiquetesVigentes.get(opcionTiquete);
        
        if (!tiquete.getEsTransferible()) {
            System.out.println("Error: Este tiquete no es transferible.");
            ValidadorEntradas.pausar();
            return;
        }
        
        // Precio de reventa
        double precioReventa = ValidadorEntradas.leerDouble("Precio de reventa: $", 0.01, 10000.0);
        
        // Poner en reventa
        aplicacion.ponerTiqueteEnReventa(comprador, tiquete, precioReventa);
        
        ValidadorEntradas.pausar();
    }
    
    private void verTiquetesEnReventa() {
        mostrarEncabezado("TIQUETES EN REVENTA");
        
        ArrayList<TiqueteReventa> reventas = aplicacion.getTiquetesEnReventa();
        if (reventas.isEmpty()) {
            System.out.println("No hay tiquetes en reventa en este momento.");
        } else {
            for (int i = 0; i < reventas.size(); i++) {
                TiqueteReventa reventa = reventas.get(i);
                Tiquete tiquete = reventa.getTiquete();
                System.out.println((i + 1) + ". " + tiquete.getId() + " | " + 
                                 tiquete.getEvento().getNombre() + " | " +
                                 tiquete.getLocalidad().getTipoLocalidad() + " | " +
                                 "Precio reventa: $" + reventa.getPrecioReventa() + " | " +
                                 "Vendedor: " + reventa.getVendedor().getLogin());
            }
        }
        
        ValidadorEntradas.pausar();
    }
    
    private void comprarTiqueteReventa(Comprador comprador) {
        mostrarEncabezado("COMPRAR TIQUETE EN REVENTA");
        
        ArrayList<TiqueteReventa> reventas = aplicacion.getTiquetesEnReventa();
        if (reventas.isEmpty()) {
            System.out.println("No hay tiquetes en reventa disponibles.");
            ValidadorEntradas.pausar();
            return;
        }
        
        // Seleccionar tiquete en reventa
        System.out.println("Seleccione un tiquete en reventa:");
        for (int i = 0; i < reventas.size(); i++) {
            TiqueteReventa reventa = reventas.get(i);
            Tiquete tiquete = reventa.getTiquete();
            System.out.println((i + 1) + ". " + tiquete.getId() + " | " + 
                             tiquete.getEvento().getNombre() + " | $" +
                             reventa.getPrecioReventa());
        }
        
        int opcionReventa = ValidadorEntradas.leerEntero("Tiquete: ", 1, reventas.size()) - 1;
        TiqueteReventa reventa = reventas.get(opcionReventa);
        
        // Confirmar compra
        boolean confirmar = ValidadorEntradas.leerBooleano("¿Confirmar compra por $" + reventa.getPrecioReventa() + "?");
        if (confirmar) {
            aplicacion.comprarTiqueteReventa(comprador, reventa);
        } else {
            System.out.println("Compra cancelada.");
        }
        
        ValidadorEntradas.pausar();
    }
    
    private void hacerContraoferta(Comprador comprador) {
        mostrarEncabezado("HACER CONTRADOFERTA");
        
        ArrayList<TiqueteReventa> reventas = aplicacion.getTiquetesEnReventa();
        if (reventas.isEmpty()) {
            System.out.println("No hay tiquetes en reventa disponibles.");
            ValidadorEntradas.pausar();
            return;
        }
        
        // Seleccionar tiquete en reventa
        System.out.println("Seleccione un tiquete para hacer contraoferta:");
        for (int i = 0; i < reventas.size(); i++) {
            TiqueteReventa reventa = reventas.get(i);
            Tiquete tiquete = reventa.getTiquete();
            System.out.println((i + 1) + ". " + tiquete.getId() + " | " + 
                             tiquete.getEvento().getNombre() + " | " +
                             "Precio actual: $" + reventa.getPrecioReventa());
        }
        
        int opcionReventa = ValidadorEntradas.leerEntero("Tiquete: ", 1, reventas.size()) - 1;
        TiqueteReventa reventa = reventas.get(opcionReventa);
        
        // Precio de contraoferta (debe ser menor al precio de reventa)
        double precioOfertado = ValidadorEntradas.leerDouble("Su oferta (debe ser menor a $" + 
                                                           reventa.getPrecioReventa() + "): $", 
                                                           0.01, reventa.getPrecioReventa() - 0.01);
        
        // Crear contraoferta
        aplicacion.crearContraoferta(comprador, reventa, precioOfertado);
        
        ValidadorEntradas.pausar();
    }
    
    private void verMisContraofertas(Comprador comprador) {
        mostrarEncabezado("MIS CONTRADOFERTAS");
        
        ArrayList<Contraoferta> contraofertas = aplicacion.getContraofertasDeComprador(comprador);
        if (contraofertas.isEmpty()) {
            System.out.println("No has hecho ninguna contraoferta.");
        } else {
            for (Contraoferta contra : contraofertas) {
                Tiquete tiquete = contra.getTiqueteReventa().getTiquete();
                System.out.println(" - Tiquete: " + tiquete.getId() + " | " + 
                                 tiquete.getEvento().getNombre());
                System.out.println("   Oferta: $" + contra.getPrecioOfertado() + 
                                 " | Estado: " + contra.getEstado());
                System.out.println();
            }
        }
        
        ValidadorEntradas.pausar();
    }
    
    private void verSaldoVirtual(Comprador comprador) {
        mostrarEncabezado("SALDO VIRTUAL");
        System.out.println("Saldo actual: $" + comprador.getSaldoVirtual());
        ValidadorEntradas.pausar();
    }
    
    private void recargarSaldo(Comprador comprador) {
        mostrarEncabezado("RECARGAR SALDO");
        
        double monto = ValidadorEntradas.leerDouble("Monto a recargar: $", 0.01, 10000.0);
        comprador.agregarSaldo(monto);
        aplicacion.guardarDatos();
        
        System.out.println("¡Recarga exitosa! Nuevo saldo: $" + comprador.getSaldoVirtual());
        ValidadorEntradas.pausar();
    }
}