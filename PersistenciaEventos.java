package modelo.persistencia;

import modelo.eventos.Evento;
import modelo.eventos.Venue;
import modelo.usuarios.Organizador;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Clase para manejar la persistencia de eventos en archivo CSV
 */
public class PersistenciaEventos {
    private static final String ARCHIVO_EVENTOS = "data/eventos.csv";
    private static final String SEPARADOR = ",";
    private static final SimpleDateFormat formatoFecha = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    
    /**
     * Guarda todos los eventos en archivo CSV
     */
    public void guardarEventos(ArrayList<Evento> eventos) {
        crearDirectorioSiNoExiste();
        
        try (PrintWriter writer = new PrintWriter(new FileWriter(ARCHIVO_EVENTOS))) {
            // Escribir encabezado
            writer.println("id,nombre,fechaHora,venueId,organizadorLogin,aprobado,cancelado");
            
            // Escribir cada evento
            for (Evento evento : eventos) {
                writer.println(convertirEventoACSV(evento));
            }
            
            System.out.println("Eventos guardados en: " + ARCHIVO_EVENTOS);
            
        } catch (IOException e) {
            System.err.println("Error al guardar eventos: " + e.getMessage());
        }
    }
    
    /**
     * Carga todos los eventos desde archivo CSV
     */
    public ArrayList<Evento> cargarEventos(ArrayList<Venue> venues, ArrayList<Organizador> organizadores) {
        ArrayList<Evento> eventos = new ArrayList<>();
        File archivo = new File(ARCHIVO_EVENTOS);
        
        if (!archivo.exists()) {
            System.out.println("Archivo de eventos no encontrado. Se creará uno nuevo.");
            return eventos;
        }
        
        try (BufferedReader reader = new BufferedReader(new FileReader(ARCHIVO_EVENTOS))) {
            String linea;
            boolean primeraLinea = true;
            
            while ((linea = reader.readLine()) != null) {
                if (primeraLinea) {
                    primeraLinea = false;
                    continue; // Saltar encabezado
                }
                
                Evento evento = convertirCSVAEvento(linea, venues, organizadores);
                if (evento != null) {
                    eventos.add(evento);
                }
            }
            
            System.out.println("Eventos cargados: " + eventos.size());
            
        } catch (IOException e) {
            System.err.println("Error al cargar eventos: " + e.getMessage());
        }
        
        return eventos;
    }
    
    /**
     * Convierte un evento a formato CSV
     */
    private String convertirEventoACSV(Evento evento) {
        StringBuilder csv = new StringBuilder();
        
        csv.append(evento.getId()).append(SEPARADOR);
        csv.append(escaparCSV(evento.getNombre())).append(SEPARADOR);
        csv.append(formatoFecha.format(evento.getFechaHora())).append(SEPARADOR);
        csv.append(evento.getVenue().getId()).append(SEPARADOR);
        csv.append(evento.getOrganizador().getLogin()).append(SEPARADOR);
        csv.append(evento.isAprobado()).append(SEPARADOR);
        csv.append(evento.isCancelado());
        
        return csv.toString();
    }
    
    /**
     * Convierte una línea CSV a objeto Evento
     */
    private Evento convertirCSVAEvento(String lineaCSV, ArrayList<Venue> venues, ArrayList<Organizador> organizadores) {
        try {
            String[] partes = lineaCSV.split(SEPARADOR, -1);
            
            if (partes.length < 7) {
                System.err.println("Línea CSV inválida: " + lineaCSV);
                return null;
            }
            
            String id = partes[0].trim();
            String nombre = desescaparCSV(partes[1].trim());
            Date fechaHora = formatoFecha.parse(partes[2].trim());
            String venueId = partes[3].trim();
            String organizadorLogin = partes[4].trim();
            boolean aprobado = Boolean.parseBoolean(partes[5].trim());
            boolean cancelado = Boolean.parseBoolean(partes[6].trim());
            
            // Buscar venue por ID
            Venue venue = buscarVenuePorId(venueId, venues);
            if (venue == null) {
                System.err.println("Venue no encontrado para evento: " + id);
                return null;
            }
            
            // Buscar organizador por login
            Organizador organizador = buscarOrganizadorPorLogin(organizadorLogin, organizadores);
            if (organizador == null) {
                System.err.println("Organizador no encontrado para evento: " + id);
                return null;
            }
            
            Evento evento = new Evento();
            evento.setId(id);
            evento.setNombre(nombre);
            evento.setFechaHora(fechaHora);
            evento.setVenue(venue);
            evento.setOrganizador(organizador);
            evento.setAprobado(aprobado);
            evento.setCancelado(cancelado);
            
            return evento;
            
        } catch (Exception e) {
            System.err.println("Error al convertir CSV a evento: " + e.getMessage());
            return null;
        }
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
    
    /**
     * Busca un organizador por login
     */
    private Organizador buscarOrganizadorPorLogin(String login, ArrayList<Organizador> organizadores) {
        for (Organizador organizador : organizadores) {
            if (organizador.getLogin().equals(login)) {
                return organizador;
            }
        }
        return null;
    }
    
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