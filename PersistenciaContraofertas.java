package modelo.persistencia;

import modelo.tiquetes.Contraoferta;
import modelo.tiquetes.TiqueteReventa;
import modelo.usuarios.Usuario;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * Persistencia para contraofertas
 */
public class PersistenciaContraofertas {
    private static final String ARCHIVO_CONTRAS = "data/contraofertas.csv";
    private static final String SEPARADOR = ",";
    private static final SimpleDateFormat formatoFecha = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    
    public void guardarContraofertas(ArrayList<Contraoferta> contraofertas) {
        crearDirectorioSiNoExiste();
        
        try (PrintWriter writer = new PrintWriter(new FileWriter(ARCHIVO_CONTRAS))) {
            writer.println("id,reventaId,compradorLogin,precioOfertado,fechaOferta,estado");
            
            for (Contraoferta contra : contraofertas) {
                writer.println(convertirContraACSV(contra));
            }
            
            System.out.println("Contraofertas guardadas en: " + ARCHIVO_CONTRAS);
            
        } catch (IOException e) {
            System.err.println("Error al guardar contraofertas: " + e.getMessage());
        }
    }
    
    public ArrayList<Contraoferta> cargarContraofertas(ArrayList<TiqueteReventa> reventas, ArrayList<Usuario> usuarios) {
        ArrayList<Contraoferta> contraofertas = new ArrayList<>();
        File archivo = new File(ARCHIVO_CONTRAS);
        
        if (!archivo.exists()) {
            return contraofertas;
        }
        
        try (BufferedReader reader = new BufferedReader(new FileReader(ARCHIVO_CONTRAS))) {
            String linea;
            boolean primeraLinea = true;
            
            while ((linea = reader.readLine()) != null) {
                if (primeraLinea) {
                    primeraLinea = false;
                    continue;
                }
                
                Contraoferta contra = convertirCSVAContra(linea, reventas, usuarios);
                if (contra != null) {
                    contraofertas.add(contra);
                }
            }
            
        } catch (IOException e) {
            System.err.println("Error al cargar contraofertas: " + e.getMessage());
        }
        
        return contraofertas;
    }
    
    private String convertirContraACSV(Contraoferta contra) {
        StringBuilder csv = new StringBuilder();
        
        csv.append(contra.getId()).append(SEPARADOR);
        csv.append(contra.getTiqueteReventa().getId()).append(SEPARADOR);
        csv.append(contra.getComprador().getLogin()).append(SEPARADOR);
        csv.append(contra.getPrecioOfertado()).append(SEPARADOR);
        csv.append(formatoFecha.format(contra.getFechaOferta())).append(SEPARADOR);
        csv.append(contra.getEstado());
        
        return csv.toString();
    }
    
    private Contraoferta convertirCSVAContra(String lineaCSV, ArrayList<TiqueteReventa> reventas, ArrayList<Usuario> usuarios) {
        try {
            String[] partes = lineaCSV.split(SEPARADOR, -1);
            
            if (partes.length < 6) {
                return null;
            }
            
            String id = partes[0].trim();
            String reventaId = partes[1].trim();
            String compradorLogin = partes[2].trim();
            double precioOfertado = Double.parseDouble(partes[3].trim());
            String estado = partes[5].trim();
            
            // Buscar reventa
            TiqueteReventa reventa = buscarReventaPorId(reventaId, reventas);
            if (reventa == null) {
                return null;
            }
            
            // Buscar comprador
            Usuario comprador = buscarUsuarioPorLogin(compradorLogin, usuarios);
            if (comprador == null) {
                return null;
            }
            
            Contraoferta contra = new Contraoferta(id, reventa, comprador, precioOfertado);
            contra.setEstado(estado);
            
            return contra;
            
        } catch (Exception e) {
            System.err.println("Error al convertir CSV a contraoferta: " + e.getMessage());
            return null;
        }
    }
    
    private TiqueteReventa buscarReventaPorId(String reventaId, ArrayList<TiqueteReventa> reventas) {
        for (TiqueteReventa reventa : reventas) {
            if (reventa.getId().equals(reventaId)) {
                return reventa;
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