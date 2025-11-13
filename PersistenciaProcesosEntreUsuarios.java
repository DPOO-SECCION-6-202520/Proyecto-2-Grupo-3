package modelo.persistencia;

import modelo.usuarios.Usuario;
import modelo.eventos.Evento;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Clase para manejar la persistencia de procesos entre usuarios
 */
public class PersistenciaProcesosEntreUsuarios {
    private static final String ARCHIVO_PROCESOS = "data/procesos_entre_usuarios.csv";
    private static final String SEPARADOR = ",";
    private static final String SEPARADOR_TIQUETES = ";";
    private static final SimpleDateFormat formatoFecha = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    
    /**
     * Guarda todos los procesos en archivo CSV
     */
    public void guardarProcesos(ArrayList<ProcesoEntreUsuarios> procesos) {
        crearDirectorioSiNoExiste();
        
        try (PrintWriter writer = new PrintWriter(new FileWriter(ARCHIVO_PROCESOS))) {
            // Escribir encabezado
            writer.println("id,tipo,fecha,usuarioOrigen,usuarioDestino,tiquetes,eventoId,monto,estado,descripcion");
            
            // Escribir cada proceso
            for (ProcesoEntreUsuarios proceso : procesos) {
                writer.println(convertirProcesoACSV(proceso));
            }
            
            System.out.println("Procesos entre usuarios guardados en: " + ARCHIVO_PROCESOS);
            
        } catch (IOException e) {
            System.err.println("Error al guardar procesos: " + e.getMessage());
        }
    }
    
    /**
     * Carga todos los procesos desde archivo CSV
     */
    public ArrayList<ProcesoEntreUsuarios> cargarProcesos(ArrayList<Usuario> usuarios, ArrayList<Evento> eventos) {
        ArrayList<ProcesoEntreUsuarios> procesos = new ArrayList<>();
        File archivo = new File(ARCHIVO_PROCESOS);
        
        if (!archivo.exists()) {
            System.out.println("Archivo de procesos no encontrado. Se creará uno nuevo.");
            return procesos;
        }
        
        try (BufferedReader reader = new BufferedReader(new FileReader(ARCHIVO_PROCESOS))) {
            String linea;
            boolean primeraLinea = true;
            
            while ((linea = reader.readLine()) != null) {
                if (primeraLinea) {
                    primeraLinea = false;
                    continue; // Saltar encabezado
                }
                
                ProcesoEntreUsuarios proceso = convertirCSVAProceso(linea, usuarios, eventos);
                if (proceso != null) {
                    procesos.add(proceso);
                }
            }
            
            System.out.println("Procesos cargados: " + procesos.size());
            
        } catch (IOException e) {
            System.err.println("Error al cargar procesos: " + e.getMessage());
        }
        
        return procesos;
    }
    
    /**
     * Convierte un proceso a formato CSV
     */
    private String convertirProcesoACSV(ProcesoEntreUsuarios proceso) {
        StringBuilder csv = new StringBuilder();
        
        csv.append(proceso.getId()).append(SEPARADOR);
        csv.append(proceso.getTipo().name()).append(SEPARADOR);
        csv.append(formatoFecha.format(proceso.getFecha())).append(SEPARADOR);
        csv.append(proceso.getUsuarioOrigen().getLogin()).append(SEPARADOR);
        
        // Usuario destino (puede ser null)
        if (proceso.getUsuarioDestino() != null) {
            csv.append(proceso.getUsuarioDestino().getLogin());
        }
        csv.append(SEPARADOR);
        
        // Tiquetes
        StringBuilder tiquetesStr = new StringBuilder();
        for (int i = 0; i < proceso.getTiquetes().size(); i++) {
            if (i > 0) tiquetesStr.append(SEPARADOR_TIQUETES);
            tiquetesStr.append(proceso.getTiquetes().get(i).getId());
        }
        csv.append(tiquetesStr.toString()).append(SEPARADOR);
        
        // Evento (puede ser null)
        if (proceso.getEvento() != null) {
            csv.append(proceso.getEvento().getId());
        }
        csv.append(SEPARADOR);
        
        csv.append(proceso.getMonto()).append(SEPARADOR);
        csv.append(proceso.getEstado()).append(SEPARADOR);
        csv.append(escaparCSV(proceso.getDescripcion()));
        
        return csv.toString();
    }
    
    /**
     * Convierte una línea CSV a objeto ProcesoEntreUsuarios
     */
    private ProcesoEntreUsuarios convertirCSVAProceso(String lineaCSV, ArrayList<Usuario> usuarios, ArrayList<Evento> eventos) {
        try {
            String[] partes = lineaCSV.split(SEPARADOR, -1);
            
            if (partes.length < 10) {
                System.err.println("Línea CSV inválida: " + lineaCSV);
                return null;
            }
            
            String id = partes[0].trim();
            ProcesoEntreUsuarios.TipoProceso tipo = ProcesoEntreUsuarios.TipoProceso.valueOf(partes[1].trim());
            Date fecha = formatoFecha.parse(partes[2].trim());
            String usuarioOrigenLogin = partes[3].trim();
            String usuarioDestinoLogin = partes[4].trim();
            String eventoId = partes[6].trim();
            double monto = Double.parseDouble(partes[7].trim());
            String estado = partes[8].trim();
            String descripcion = desescaparCSV(partes[9].trim());
            
            // Buscar usuario origen
            Usuario usuarioOrigen = buscarUsuarioPorLogin(usuarioOrigenLogin, usuarios);
            if (usuarioOrigen == null) {
                System.err.println("Usuario origen no encontrado para proceso: " + id);
                return null;
            }
            
            ProcesoEntreUsuarios proceso = new ProcesoEntreUsuarios(id, tipo, fecha, usuarioOrigen);
            
            // Buscar usuario destino (puede ser null)
            if (!usuarioDestinoLogin.isEmpty()) {
                Usuario usuarioDestino = buscarUsuarioPorLogin(usuarioDestinoLogin, usuarios);
                proceso.setUsuarioDestino(usuarioDestino);
            }
            
            // Buscar evento (puede ser null)
            if (!eventoId.isEmpty()) {
                Evento evento = buscarEventoPorId(eventoId, eventos);
                proceso.setEvento(evento);
            }
            
            // Configurar otros atributos
            proceso.setMonto(monto);
            proceso.setEstado(estado);
            proceso.setDescripcion(descripcion);
            
            // Los tiquetes se cargarán después cuando tengamos la lista completa de tiquetes
            // Por ahora solo guardamos los IDs
            
            return proceso;
            
        } catch (Exception e) {
            System.err.println("Error al convertir CSV a proceso: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Busca un usuario por login
     */
    private Usuario buscarUsuarioPorLogin(String login, ArrayList<Usuario> usuarios) {
        for (Usuario usuario : usuarios) {
            if (usuario.getLogin().equals(login)) {
                return usuario;
            }
        }
        return null;
    }
    
    /**
     * Busca un evento por ID
     */
    private Evento buscarEventoPorId(String eventoId, ArrayList<Evento> eventos) {
        for (Evento evento : eventos) {
            if (evento.getId().equals(eventoId)) {
                return evento;
            }
        }
        return null;
    }
    
    // ... (métodos auxiliares escaparCSV, desescaparCSV, crearDirectorioSiNoExiste)
    
    /**
     * Escapa comas y comillas en valores CSV
     */
    private String escaparCSV(String valor) {
        if (valor.contains(",") || valor.contains("\"") || valor.contains("\n")) {
            return "\"" + valor.replace("\"", "\"\"") + "\"";
        }
        return valor;
    }
    
    /**
     * Desescapa valores CSV
     */
    private String desescaparCSV(String valor) {
        if (valor.startsWith("\"") && valor.endsWith("\"")) {
            return valor.substring(1, valor.length() - 1).replace("\"\"", "\"");
        }
        return valor;
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
}