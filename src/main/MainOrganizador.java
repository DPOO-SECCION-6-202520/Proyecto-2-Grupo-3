package main;

import modelo.Aplicacion;
import modelo.usuarios.Usuario;
import interfaz.MenuOrganizador;
import interfaz.util.ValidadorEntradas;

/**
 * Punto de entrada para usuarios organizadores
 */
public class MainOrganizador {
    
    public static void main(String[] args) {
        System.out.println("=== SISTEMA DE VENTA DE TIQUETES ===");
        System.out.println("       MODO ORGANIZADOR\n");
        
        Aplicacion aplicacion = Aplicacion.getInstancia();
        boolean autenticado = false;
        Usuario usuario = null;
        
        while (!autenticado) {
            System.out.println("=== INICIO DE SESIÓN ===");
            
            String login = ValidadorEntradas.leerString("Usuario: ");
            String password = ValidadorEntradas.leerString("Contraseña: ");
            
            usuario = aplicacion.iniciarSesion(login, password);
            
            if (usuario != null) {
                if (usuario.getTipoUsuario().equals("organizador")) {
                    autenticado = true;
                    System.out.println("\n¡Bienvenido, " + usuario.getLogin() + "!");
                } else {
                    System.out.println("Error: Esta aplicación es solo para organizadores.");
                    aplicacion.cerrarSesion(login);
                }
            } else {
                System.out.println("\nError: Credenciales incorrectas.");
                
                boolean intentarNuevamente = ValidadorEntradas.leerBooleano("¿Desea intentar nuevamente?");
                if (!intentarNuevamente) {
                    System.out.println("Saliendo del sistema...");
                    aplicacion.cerrarAplicacion();
                    return;
                }
            }
        }
        
        // Por ahora solo mensaje - luego agregaremos MenuOrganizador
        MenuOrganizador menu = new MenuOrganizador(aplicacion, usuario);
        menu.mostrarMenu();
        System.out.println("Funcionalidades disponibles próximamente.");
        ValidadorEntradas.pausar();
        
        aplicacion.cerrarSesion(usuario.getLogin());
        aplicacion.cerrarAplicacion();
    }
}