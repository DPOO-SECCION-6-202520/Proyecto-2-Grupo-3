package interfaz.util;

import java.util.Scanner;

/**
 * Clase utilitaria para validar entradas del usuario
 */
public class ValidadorEntradas {
    private static Scanner scanner = new Scanner(System.in);
    
    /**
     * Lee un entero validado
     */
    public static int leerEntero(String mensaje, int min, int max) {
        while (true) {
            try {
                System.out.print(mensaje);
                int valor = Integer.parseInt(scanner.nextLine().trim());
                if (valor >= min && valor <= max) {
                    return valor;
                } else {
                    System.out.println("Error: El valor debe estar entre " + min + " y " + max);
                }
            } catch (NumberFormatException e) {
                System.out.println("Error: Por favor ingrese un número válido");
            }
        }
    }
    
    /**
     * Lee un double validado
     */
    public static double leerDouble(String mensaje, double min, double max) {
        while (true) {
            try {
                System.out.print(mensaje);
                double valor = Double.parseDouble(scanner.nextLine().trim());
                if (valor >= min && valor <= max) {
                    return valor;
                } else {
                    System.out.println("Error: El valor debe estar entre " + min + " y " + max);
                }
            } catch (NumberFormatException e) {
                System.out.println("Error: Por favor ingrese un número válido");
            }
        }
    }
    
    /**
     * Lee un string no vacío
     */
    public static String leerString(String mensaje) {
        while (true) {
            System.out.print(mensaje);
            String valor = scanner.nextLine().trim();
            if (!valor.isEmpty()) {
                return valor;
            } else {
                System.out.println("Error: Este campo no puede estar vacío");
            }
        }
    }
    
    /**
     * Lee un string con longitud específica
     */
    public static String leerString(String mensaje, int minLength, int maxLength) {
        while (true) {
            System.out.print(mensaje);
            String valor = scanner.nextLine().trim();
            if (valor.length() >= minLength && valor.length() <= maxLength) {
                return valor;
            } else {
                System.out.println("Error: El texto debe tener entre " + minLength + " y " + maxLength + " caracteres");
            }
        }
    }
    
    /**
     * Lee un booleano (s/n)
     */
    public static boolean leerBooleano(String mensaje) {
        while (true) {
            System.out.print(mensaje + " (s/n): ");
            String valor = scanner.nextLine().trim().toLowerCase();
            if (valor.equals("s") || valor.equals("si") || valor.equals("y") || valor.equals("yes")) {
                return true;
            } else if (valor.equals("n") || valor.equals("no")) {
                return false;
            } else {
                System.out.println("Error: Por favor ingrese 's' para sí o 'n' para no");
            }
        }
    }
    
    /**
     * Pausa la ejecución hasta que el usuario presione Enter
     */
    public static void pausar() {
        System.out.println("\nPresione Enter para continuar...");
        scanner.nextLine();
    }
    
    /**
     * Limpia la consola (simulado)
     */
    public static void limpiarConsola() {
        for (int i = 0; i < 50; i++) {
            System.out.println();
        }
    }
}