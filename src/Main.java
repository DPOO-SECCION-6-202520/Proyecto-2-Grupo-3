import modelo.Aplicacion;

/**
 * Clase principal para demostrar el funcionamiento del sistema
 */
public class Main {
    public static void main(String[] args) {
        System.out.println("=".repeat(60));
        System.out.println("SISTEMA DE VENTA DE TIQUETES - PROYECTO 1");
        System.out.println("=".repeat(60));
        
        // Obtener la instancia del sistema
        Aplicacion sistema = Aplicacion.getInstancia();
        
        // Ejecutar demostración automática
        sistema.ejecutarDemostracion();
        
        System.out.println("\n" + "=".repeat(60));
        System.out.println("SISTEMA LISTO PARA USO");
        System.out.println("=".repeat(60));
        System.out.println("Credenciales de prueba:");
        System.out.println("   Admin: admin / admin123");
        System.out.println("   Organizador: promotor1 / promo123"); 
        System.out.println("   Comprador: cliente1 / cliente123");
        System.out.println("=".repeat(60));
    }
}