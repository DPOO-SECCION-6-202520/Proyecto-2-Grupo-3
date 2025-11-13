package main;

import modelo.Aplicacion;
import modelo.usuarios.Usuario;
import interfaz.MenuComprador;
import interfaz.util.ValidadorEntradas;

/**
 * Punto de entrada para usuarios compradores
 */
public class MainComprador {
    
    public static void main(String[] args) {
        System.out.println("=== SISTEMA DE VENTA DE TIQUETES ===");
        System.out.println("       MODO COMPRADOR\n");
        
        // Obtener la instancia del sistema
        Aplicacion aplicacion = Aplicacion.getInstancia();
        
        boolean autenticado = false;
        Usuario usuario = null;
        
        // Ciclo de autenticación
        while (!autenticado) {
            System.out.println("=== INICIO DE SESIÓN ===");
            
            String login = ValidadorEntradas.leerString("Usuario: ");
            String password = ValidadorEntradas.leerString("Contraseña: ");
            
            usuario = aplicacion.iniciarSesion(login, password);
            
            if (usuario != null) {
                if (usuario.getTipoUsuario().equals("comprador")) {
                    autenticado = true;
                    System.out.println("\n¡Bienvenido, " + usuario.getLogin() + "!");
                } else {
                    System.out.println("Error: Esta aplicación es solo para compradores.");
                    System.out.println("Use la aplicación correspondiente para su tipo de usuario.");
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
        
        // Mostrar menú del comprador
        MenuComprador menu = new MenuComprador(aplicacion, usuario);
        menu.mostrarMenu();
        
        // Guardar datos al salir
        aplicacion.cerrarAplicacion();
    }
}