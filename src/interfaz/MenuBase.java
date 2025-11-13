package interfaz;

import modelo.Aplicacion;
import modelo.usuarios.Usuario;
import interfaz.util.ValidadorEntradas;

/**
 * Clase base abstracta para todos los menús
 */
public abstract class MenuBase {
    protected Aplicacion aplicacion;
    protected Usuario usuario;
    
    public MenuBase(Aplicacion aplicacion, Usuario usuario) {
        this.aplicacion = aplicacion;
        this.usuario = usuario;
    }
    
    /**
     * Método abstracto que debe implementar cada menú específico
     */
    public abstract void mostrarMenu();
    
    /**
     * Muestra el encabezado del menú
     */
    protected void mostrarEncabezado(String titulo) {
        ValidadorEntradas.limpiarConsola();
        System.out.println("=".repeat(50));
        System.out.println(titulo);
        System.out.println("Usuario: " + usuario.getLogin() + " (" + usuario.getTipoUsuario() + ")");
        System.out.println("Saldo: $" + usuario.getSaldoVirtual());
        System.out.println("=".repeat(50));
    }
    
    /**
     * Muestra un mensaje de despedida
     */
    protected void mostrarDespedida() {
        System.out.println("\n¡Hasta pronto! Cerrando sesión...");
        aplicacion.cerrarSesion(usuario.getLogin());
        ValidadorEntradas.pausar();
    }
}