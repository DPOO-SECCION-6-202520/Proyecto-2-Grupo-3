package modelo.persistencia;

import modelo.usuarios.Usuario;
import modelo.eventos.Evento;
import modelo.eventos.Venue;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Clase para manejar la persistencia de solicitudes pendientes
 */
public class PersistenciaSolicitudes {
    private static final String ARCHIVO_SOLICITUDES = "data/solicitudes.csv";
    private static final String SEPARADOR = ",";
    private static final SimpleDateFormat formatoFecha = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    
    /**
     * Guarda todas las solicitudes en archivo CSV
     */
    public void guardarSolicitudes(ArrayList<Solicitud> solicitudes) {
        crearDirectorioSiNoExiste();
        
        try (PrintWriter writer = new PrintWriter(new FileWriter(ARCHIVO_SOLICITUDES))) {
            // Escribir encabezado
            writer.println("id,tipo,fechaSolicitud,solicitanteLogin,descripcion,estado,respuesta,fechaRespuesta,adminLogin,eventoId,venueId,tiqueteId,montoReembolso");
            
            // Escribir cada solicitud
            for (Solicitud solicitud : solicitudes) {
                writer.println(convertirSolicitudACSV(solicitud));
            }
            
            System.out.println("Solicitudes guardadas en: " + ARCHIVO_SOLICITUDES);
            
        } catch (IOException e) {
            System.err.println("Error al guardar solicitudes: " + e.getMessage());
        }
    }
    
    /**
     * Carga todas las solicitudes desde archivo CSV
     */
    public ArrayList<Solicitud> cargarSolicitudes(ArrayList<Usuario> usuarios, ArrayList<Evento> eventos, ArrayList<Venue> venues) {
        ArrayList<Solicitud> solicitudes = new ArrayList<>();
        File archivo = new File(ARCHIVO_SOLICITUDES);
        
        if (!archivo.exists()) {
            System.out.println("Archivo de solicitudes no encontrado. Se creará uno nuevo.");
            return solicitudes;
        }
        
        try (BufferedReader reader = new BufferedReader(new FileReader(ARCHIVO_SOLICITUDES))) {
            String linea;
            boolean primeraLinea = true;
            
            while ((linea = reader.readLine()) != null) {
                if (primeraLinea) {
                    primeraLinea = false;
                    continue; // Saltar encabezado
                }
                
                Solicitud solicitud = convertirCSVASolicitud(linea, usuarios, eventos, venues);
                if (solicitud != null) {
                    solicitudes.add(solicitud);
                }
            }
            
            System.out.println("Solicitudes cargadas: " + solicitudes.size());
            
        } catch (IOException e) {
            System.err.println("Error al cargar solicitudes: " + e.getMessage());
        }
        
        return solicitudes;
    }
    
    /**
     * Convierte una solicitud a formato CSV
     */
    private String convertirSolicitudACSV(Solicitud solicitud) {
        StringBuilder csv = new StringBuilder();
        
        csv.append(solicitud.getId()).append(SEPARADOR);
        csv.append(solicitud.getTipo().name()).append(SEPARADOR);
        csv.append(formatoFecha.format(solicitud.getFechaSolicitud())).append(SEPARADOR);
        csv.append(solicitud.getSolicitante().getLogin()).append(SEPARADOR);
        csv.append(escaparCSV(solicitud.getDescripcion())).append(SEPARADOR);
        csv.append(solicitud.getEstado()).append(SEPARADOR);
        csv.append(escaparCSV(solicitud.getRespuesta())).append(SEPARADOR);
        
        // Fecha respuesta (puede ser null)
        if (solicitud.getFechaRespuesta() != null) {
            csv.append(formatoFecha.format(solicitud.getFechaRespuesta()));
        }
        csv.append(SEPARADOR);
        
        // Administrador (puede ser null)
        if (solicitud.getAdministrador() != null) {
            csv.append(solicitud.getAdministrador().getLogin());
        }
        csv.append(SEPARADOR);
        
        // Evento (puede ser null)
        if (solicitud.getEvento() != null) {
            csv.append(solicitud.getEvento().getId());
        }
        csv.append(SEPARADOR);
        
        // Venue (puede ser null)
        if (solicitud.getVenue() != null) {
            csv.append(solicitud.getVenue().getId());
        }
        csv.append(SEPARADOR);
        
        // Tiquete ID (simplificado - solo guardamos el ID)
        if (solicitud.getTiquete() != null) {
            csv.append(solicitud.getTiquete().getId());
        }
        csv.append(SEPARADOR);
        
        csv.append(solicitud.getMontoReembolso());
        
        return csv.toString();
    }
    
    /**
     * Convierte una línea CSV a objeto Solicitud
     */
    private Solicitud convertirCSVASolicitud(String lineaCSV, ArrayList<Usuario> usuarios, ArrayList<Evento> eventos, ArrayList<Venue> venues) {
        try {
            String[] partes = lineaCSV.split(SEPARADOR, -1);
            
            if (partes.length < 13) {
                System.err.println("Línea CSV inválida: " + lineaCSV);
                return null;
            }
            
            String id = partes[0].trim();
            Solicitud.TipoSolicitud tipo = Solicitud.TipoSolicitud.valueOf(partes[1].trim());
            Date fechaSolicitud = formatoFecha.parse(partes[2].trim());
            String solicitanteLogin = partes[3].trim();
            String descripcion = desescaparCSV(partes[4].trim());
            String estado = partes[5].trim();
            String respuesta = desescaparCSV(partes[6].trim());
            String fechaRespuestaStr = partes[7].trim();
            String adminLogin = partes[8].trim();
            String eventoId = partes[9].trim();
            String venueId = partes[10].trim();
            double montoReembolso = Double.parseDouble(partes[12].trim());
            
            // Buscar solicitante
            Usuario solicitante = buscarUsuarioPorLogin(solicitanteLogin, usuarios);
            if (solicitante == null) {
                System.err.println("Solicitante no encontrado para solicitud: " + id);
                return null;
            }
            
            Solicitud solicitud = new Solicitud(id, tipo, fechaSolicitud, solicitante, descripcion);
            solicitud.setEstado(estado);
            solicitud.setRespuesta(respuesta);
            solicitud.setMontoReembolso(montoReembolso);
            
            // Fecha respuesta (puede ser null)
            if (!fechaRespuestaStr.isEmpty()) {
                Date fechaRespuesta = formatoFecha.parse(fechaRespuestaStr);
                solicitud.setFechaRespuesta(fechaRespuesta);
            }
            
            // Administrador (puede ser null)
            if (!adminLogin.isEmpty()) {
                Usuario administrador = buscarUsuarioPorLogin(adminLogin, usuarios);
                solicitud.setAdministrador(administrador);
            }
            
            // Evento (puede ser null)
            if (!eventoId.isEmpty()) {
                Evento evento = buscarEventoPorId(eventoId, eventos);
                solicitud.setEvento(evento);
            }
            
            // Venue (puede ser null)
            if (!venueId.isEmpty()) {
                Venue venue = buscarVenuePorId(venueId, venues);
                solicitud.setVenue(venue);
            }
            
            // El tiquete se manejaría de manera similar, pero por simplicidad solo guardamos el ID
            
            return solicitud;
            
        } catch (Exception e) {
            System.err.println("Error al convertir CSV a solicitud: " + e.getMessage());
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
    
    /**
     * Busca un venue por ID
     */
    private Venue buscarVenuePorId(String venueId, ArrayList<Venue> venues) {
        for (Venue venue : venues) {
            if (venue.getId().equals(venueId)) {
                return venue;
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
    
    /**
     * Obtiene las solicitudes pendientes
     */
    public ArrayList<Solicitud> getSolicitudesPendientes(ArrayList<Solicitud> todasLasSolicitudes) {
        ArrayList<Solicitud> pendientes = new ArrayList<>();
        for (Solicitud solicitud : todasLasSolicitudes) {
            if (solicitud.estaPendiente()) {
                pendientes.add(solicitud);
            }
        }
        return pendientes;
    }
}