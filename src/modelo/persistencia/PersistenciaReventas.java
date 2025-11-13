package modelo.persistencia;

import modelo.tiquetes.TiqueteReventa;
import modelo.tiquetes.Tiquete;
import modelo.usuarios.Usuario;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * Persistencia para tiquetes en reventa
 */
public class PersistenciaReventas {
    private static final String ARCHIVO_REVENTAS = "data/reventas.csv";
    private static final String SEPARADOR = ",";
    private static final SimpleDateFormat formatoFecha = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    
    public void guardarReventas(ArrayList<TiqueteReventa> reventas) {
        crearDirectorioSiNoExiste();
        
        try (PrintWriter writer = new PrintWriter(new FileWriter(ARCHIVO_REVENTAS))) {
            writer.println("id,tiqueteId,vendedorLogin,precioReventa,fechaPublicacion,activo");
            
            for (TiqueteReventa reventa : reventas) {
                writer.println(convertirReventaACSV(reventa));
            }
            
            System.out.println("Reventas guardadas en: " + ARCHIVO_REVENTAS);
            
        } catch (IOException e) {
            System.err.println("Error al guardar reventas: " + e.getMessage());
        }
    }
    
    public ArrayList<TiqueteReventa> cargarReventas(ArrayList<Tiquete> tiquetes, ArrayList<Usuario> usuarios) {
        ArrayList<TiqueteReventa> reventas = new ArrayList<>();
        File archivo = new File(ARCHIVO_REVENTAS);
        
        if (!archivo.exists()) {
            return reventas;
        }
        
        try (BufferedReader reader = new BufferedReader(new FileReader(ARCHIVO_REVENTAS))) {
            String linea;
            boolean primeraLinea = true;
            
            while ((linea = reader.readLine()) != null) {
                if (primeraLinea) {
                    primeraLinea = false;
                    continue;
                }
                
                TiqueteReventa reventa = convertirCSVAReventa(linea, tiquetes, usuarios);
                if (reventa != null) {
                    reventas.add(reventa);
                }
            }
            
        } catch (IOException e) {
            System.err.println("Error al cargar reventas: " + e.getMessage());
        }
        
        return reventas;
    }
    
    private String convertirReventaACSV(TiqueteReventa reventa) {
        StringBuilder csv = new StringBuilder();
        
        csv.append(reventa.getId()).append(SEPARADOR);
        csv.append(reventa.getTiquete().getId()).append(SEPARADOR);
        csv.append(reventa.getVendedor().getLogin()).append(SEPARADOR);
        csv.append(reventa.getPrecioReventa()).append(SEPARADOR);
        csv.append(formatoFecha.format(reventa.getFechaPublicacion())).append(SEPARADOR);
        csv.append(reventa.isActivo());
        
        return csv.toString();
    }
    
    private TiqueteReventa convertirCSVAReventa(String lineaCSV, ArrayList<Tiquete> tiquetes, ArrayList<Usuario> usuarios) {
        try {
            String[] partes = lineaCSV.split(SEPARADOR, -1);
            
            if (partes.length < 6) {
                return null;
            }
            
            String id = partes[0].trim();
            String tiqueteId = partes[1].trim();
            String vendedorLogin = partes[2].trim();
            double precioReventa = Double.parseDouble(partes[3].trim());
            boolean activo = Boolean.parseBoolean(partes[5].trim());
            
            // Buscar tiquete
            Tiquete tiquete = buscarTiquetePorId(tiqueteId, tiquetes);
            if (tiquete == null) {
                return null;
            }
            
            // Buscar vendedor
            Usuario vendedor = buscarUsuarioPorLogin(vendedorLogin, usuarios);
            if (vendedor == null) {
                return null;
            }
            
            TiqueteReventa reventa = new TiqueteReventa(id, tiquete, vendedor, precioReventa);
            reventa.setActivo(activo);
            
            return reventa;
            
        } catch (Exception e) {
            System.err.println("Error al convertir CSV a reventa: " + e.getMessage());
            return null;
        }
    }
    
    private Tiquete buscarTiquetePorId(String tiqueteId, ArrayList<Tiquete> tiquetes) {
        for (Tiquete tiquete : tiquetes) {
            if (tiquete.getId().equals(tiqueteId)) {
                return tiquete;
            }
        }
        return null;
    }
    
    private Usuario buscarUsuarioPorLogin(String login, ArrayList<Usuario> usuarios) {
        for (Usuario usuario : usuarios) {
            if (usuario.getLogin().equals(login)) {
                return usuario;
            }
        }
        return null;
    }
    
    private void crearDirectorioSiNoExiste() {
        File directorio = new File("data");
        if (!directorio.exists()) {
            directorio.mkdirs();
        }
    }
}