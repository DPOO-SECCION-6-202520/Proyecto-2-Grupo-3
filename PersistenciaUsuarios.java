package modelo.persistencia;

import modelo.usuarios.Usuario;
import modelo.usuarios.Administrador;
import modelo.usuarios.Comprador;
import modelo.usuarios.Organizador;
import modelo.tiquetes.Tiquete;
import modelo.tiquetes.EntradaMultiple;
import modelo.tiquetes.Deluxe;

import java.io.*;
import java.util.ArrayList;

/**
 * Clase para manejar la persistencia de usuarios en archivo CSV
 * Ahora incluye los tiquetes de cada comprador
 */
public class PersistenciaUsuarios {
    private static final String ARCHIVO_USUARIOS = "data/usuarios.csv";
    private static final String SEPARADOR = ",";
    private static final String SEPARADOR_TIQUETES = ";";
    private static final String SEPARADOR_ATRIBUTOS = "|";
    
    /**
     * Guarda todos los usuarios en archivo CSV (incluyendo tiquetes de compradores)
     */
    public void guardarUsuarios(ArrayList<Usuario> usuarios) {
        crearDirectorioSiNoExiste();
        
        try (PrintWriter writer = new PrintWriter(new FileWriter(ARCHIVO_USUARIOS))) {
            // Escribir encabezado
            writer.println("login,password,saldoVirtual,tipoUsuario,porcentajeAdicional,cobroFijo,tiquetes");
            
            // Escribir cada usuario
            for (Usuario usuario : usuarios) {
                writer.println(convertirUsuarioACSV(usuario));
            }
            
            System.out.println("Usuarios guardados en: " + ARCHIVO_USUARIOS);
            
        } catch (IOException e) {
            System.err.println("Error al guardar usuarios: " + e.getMessage());
        }
    }
    
    /**
     * Carga todos los usuarios desde archivo CSV (incluyendo tiquetes de compradores)
     */
    public ArrayList<Usuario> cargarUsuarios() {
        ArrayList<Usuario> usuarios = new ArrayList<>();
        File archivo = new File(ARCHIVO_USUARIOS);
        
        if (!archivo.exists()) {
            System.out.println("Archivo de usuarios no encontrado. Se creará uno nuevo.");
            return usuarios;
        }
        
        try (BufferedReader reader = new BufferedReader(new FileReader(ARCHIVO_USUARIOS))) {
            String linea;
            boolean primeraLinea = true;
            
            while ((linea = reader.readLine()) != null) {
                if (primeraLinea) {
                    primeraLinea = false;
                    continue; // Saltar encabezado
                }
                
                Usuario usuario = convertirCSVAUsuario(linea);
                if (usuario != null) {
                    usuarios.add(usuario);
                }
            }
            
            System.out.println("Usuarios cargados: " + usuarios.size());
            
        } catch (IOException e) {
            System.err.println("Error al cargar usuarios: " + e.getMessage());
        }
        
        return usuarios;
    }
    
    /**
     * Convierte un usuario a formato CSV (incluyendo tiquetes si es comprador)
     */
    private String convertirUsuarioACSV(Usuario usuario) {
        StringBuilder csv = new StringBuilder();
        
        // Campos comunes a todos los usuarios
        csv.append(usuario.getLogin()).append(SEPARADOR);
        csv.append(usuario.getPassword()).append(SEPARADOR);
        csv.append(usuario.getSaldoVirtual()).append(SEPARADOR);
        csv.append(usuario.getTipoUsuario()).append(SEPARADOR);
        
        // Campos específicos por tipo de usuario
        if (usuario instanceof Administrador) {
            Administrador admin = (Administrador) usuario;
            csv.append(admin.getPorcentajeAdicional()).append(SEPARADOR);
            csv.append(admin.getCobroFijo()).append(SEPARADOR);
        } else {
            // Para compradores y organizadores, estos campos van vacíos
            csv.append(SEPARADOR); // porcentajeAdicional vacío
            csv.append(SEPARADOR); // cobroFijo vacío
        }
        
        // Tiquetes (solo para compradores)
        if (usuario instanceof Comprador) {
            Comprador comprador = (Comprador) usuario;
            StringBuilder tiquetesStr = new StringBuilder();
            for (int i = 0; i < comprador.getHistorialTiquetes().size(); i++) {
                if (i > 0) tiquetesStr.append(SEPARADOR_TIQUETES);
                tiquetesStr.append(convertirTiqueteACSV(comprador.getHistorialTiquetes().get(i)));
            }
            csv.append(tiquetesStr.toString());
        } else {
            csv.append(""); // Sin tiquetes
        }
        
        return csv.toString();
    }
    
    /**
     * Convierte un tiquete a formato CSV simplificado
     */
    private String convertirTiqueteACSV(Tiquete tiquete) {
        StringBuilder csv = new StringBuilder();
        
        csv.append(tiquete.getId()).append(SEPARADOR_ATRIBUTOS);
        csv.append(tiquete.getPrecioBase()).append(SEPARADOR_ATRIBUTOS);
        csv.append(tiquete.getEsTransferible()).append(SEPARADOR_ATRIBUTOS);
        csv.append(tiquete.isUtilizado()).append(SEPARADOR_ATRIBUTOS);
        
        // Tipo de tiquete
        if (tiquete instanceof EntradaMultiple) {
            csv.append("ENTRADA_MULTIPLE");
        } else if (tiquete instanceof Deluxe) {
            csv.append("DELUXE");
        } else {
            csv.append("NORMAL");
        }
        
        return csv.toString();
    }
    
    /**
     * Convierte una línea CSV a objeto Usuario (incluyendo tiquetes si es comprador)
     */
    private Usuario convertirCSVAUsuario(String lineaCSV) {
        try {
            String[] partes = lineaCSV.split(SEPARADOR, -1); // -1 para mantener campos vacíos
            
            if (partes.length < 4) {
                System.err.println("Línea CSV inválida: " + lineaCSV);
                return null;
            }
            
            String login = partes[0].trim();
            String password = partes[1].trim();
            double saldoVirtual = Double.parseDouble(partes[2].trim());
            String tipoUsuario = partes[3].trim();
            
            Usuario usuario = crearUsuarioPorTipo(tipoUsuario);
            if (usuario != null) {
                usuario.setLogin(login);
                usuario.setPassword(password);
                usuario.setSaldoVirtual(saldoVirtual);
                usuario.setTipoUsuario(tipoUsuario);
                
                // Configurar campos específicos según tipo
                if (usuario instanceof Administrador && partes.length >= 6) {
                    Administrador admin = (Administrador) usuario;
                    if (!partes[4].isEmpty()) {
                        admin.setPorcentajeAdicional(Double.parseDouble(partes[4].trim()));
                    }
                    if (!partes[5].isEmpty()) {
                        admin.setCobroFijo(Double.parseDouble(partes[5].trim()));
                    }
                }
                
                // Cargar tiquetes si es comprador
                if (usuario instanceof Comprador && partes.length >= 7) {
                    Comprador comprador = (Comprador) usuario;
                    String tiquetesStr = partes[6].trim();
                    if (!tiquetesStr.isEmpty()) {
                        ArrayList<Tiquete> tiquetes = cargarTiquetesDesdeCSV(tiquetesStr);
                        // Aquí necesitaríamos agregar los tiquetes al comprador
                        // Pero necesitamos un método en Comprador para establecer el historial completo
                        for (Tiquete tiquete : tiquetes) {
                            comprador.agregarTiqueteAlHistorial(tiquete);
                        }
                    }
                }
            }
            
            return usuario;
            
        } catch (Exception e) {
            System.err.println("Error al convertir CSV a usuario: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Carga tiquetes desde string CSV
     */
    private ArrayList<Tiquete> cargarTiquetesDesdeCSV(String tiquetesCSV) {
        ArrayList<Tiquete> tiquetes = new ArrayList<>();
        
        if (tiquetesCSV.isEmpty()) {
            return tiquetes;
        }
        
        String[] tiquetesArray = tiquetesCSV.split(SEPARADOR_TIQUETES);
        for (String tiqueteStr : tiquetesArray) {
            Tiquete tiquete = convertirCSVATiquete(tiqueteStr);
            if (tiquete != null) {
                tiquetes.add(tiquete);
            }
        }
        
        return tiquetes;
    }
    
    /**
     * Convierte una string de tiquete a objeto Tiquete
     */
    private Tiquete convertirCSVATiquete(String tiqueteCSV) {
        try {
            String[] partes = tiqueteCSV.split("\\" + SEPARADOR_ATRIBUTOS, -1);
            
            if (partes.length < 5) {
                System.err.println("Tiquete CSV inválido: " + tiqueteCSV);
                return null;
            }
            
            String id = partes[0].trim();
            double precioBase = Double.parseDouble(partes[1].trim());
            boolean transferible = Boolean.parseBoolean(partes[2].trim());
            boolean utilizado = Boolean.parseBoolean(partes[3].trim());
            
            // Por simplicidad, creamos tiquetes básicos por ahora
            // En una implementación completa, necesitaríamos más información
            Tiquete tiquete = new Tiquete();
            tiquete.setId(id);
            tiquete.setPrecioBase(precioBase);
            tiquete.setTransferible(transferible);
            if (utilizado) {
                tiquete.marcarComoUtilizado();
            }
            
            return tiquete;
            
        } catch (Exception e) {
            System.err.println("Error al convertir CSV a tiquete: " + e.getMessage());
            return null;
        }
    }
    
    // ... (métodos crearUsuarioPorTipo, crearDirectorioSiNoExiste, etc. se mantienen igual)
    
    /**
     * Crea la instancia correcta según el tipo de usuario
     */
    private Usuario crearUsuarioPorTipo(String tipoUsuario) {
        switch (tipoUsuario.toLowerCase()) {
            case "administrador":
                return new Administrador();
            case "comprador":
                return new Comprador();
            case "organizador":
                return new Organizador();
            default:
                System.err.println("Tipo de usuario desconocido: " + tipoUsuario);
                return null;
        }
    }
    
    /**
     * Crea el directorio data/ si no existe
     */
    private void crearDirectorioSiNoExiste() {
        File directorio = new File("data");
        if (!directorio.exists()) {
            directorio.mkdirs();
        }
    }
    
    /**
     * Busca un usuario por login
     */
    public Usuario buscarUsuarioPorLogin(String login, ArrayList<Usuario> usuarios) {
        for (Usuario usuario : usuarios) {
            if (usuario.getLogin().equals(login)) {
                return usuario;
            }
        }
        return null;
    }
}