package modelo.persistencia;

import modelo.eventos.Venue;
import modelo.eventos.Localidad;

import java.io.*;
import java.util.ArrayList;

/**
 * Clase para manejar la persistencia de venues en archivo CSV
 * Incluye las localidades como objetos serializados en el mismo archivo
 */
public class PersistenciaVenues {
    private static final String ARCHIVO_VENUES = "data/venues.csv";
    private static final String SEPARADOR = ",";
    private static final String SEPARADOR_LOCALIDADES = ";";
    private static final String SEPARADOR_ATRIBUTOS = "|";
    
    /**
     * Guarda todos los venues en archivo CSV (incluyendo localidades)
     */
    public void guardarVenues(ArrayList<Venue> venues) {
        crearDirectorioSiNoExiste();
        
        try (PrintWriter writer = new PrintWriter(new FileWriter(ARCHIVO_VENUES))) {
            // Escribir encabezado
            writer.println("id,nombre,ubicacion,capacidad,aprobado,restricciones,localidades");
            
            // Escribir cada venue
            for (Venue venue : venues) {
                writer.println(convertirVenueACSV(venue));
            }
            
            System.out.println("Venues guardados en: " + ARCHIVO_VENUES);
            
        } catch (IOException e) {
            System.err.println("Error al guardar venues: " + e.getMessage());
        }
    }
    
    /**
     * Carga todos los venues desde archivo CSV (incluyendo localidades)
     */
    public ArrayList<Venue> cargarVenues() {
        ArrayList<Venue> venues = new ArrayList<>();
        File archivo = new File(ARCHIVO_VENUES);
        
        if (!archivo.exists()) {
            System.out.println("Archivo de venues no encontrado. Se creará uno nuevo.");
            return venues;
        }
        
        try (BufferedReader reader = new BufferedReader(new FileReader(ARCHIVO_VENUES))) {
            String linea;
            boolean primeraLinea = true;
            
            while ((linea = reader.readLine()) != null) {
                if (primeraLinea) {
                    primeraLinea = false;
                    continue; // Saltar encabezado
                }
                
                Venue venue = convertirCSVAVenue(linea);
                if (venue != null) {
                    venues.add(venue);
                }
            }
            
            System.out.println("Venues cargados: " + venues.size());
            
        } catch (IOException e) {
            System.err.println("Error al cargar venues: " + e.getMessage());
        }
        
        return venues;
    }
    
    /**
     * Convierte un venue a formato CSV (incluyendo localidades)
     */
    private String convertirVenueACSV(Venue venue) {
        StringBuilder csv = new StringBuilder();
        
        csv.append(venue.getId()).append(SEPARADOR);
        csv.append(escaparCSV(venue.getNombre())).append(SEPARADOR);
        csv.append(escaparCSV(venue.getUbicacion())).append(SEPARADOR);
        csv.append(venue.getCapacidad()).append(SEPARADOR);
        csv.append(venue.isAprobado()).append(SEPARADOR);
        
        // Convertir restricciones a string separado por punto y coma
        StringBuilder restriccionesStr = new StringBuilder();
        for (int i = 0; i < venue.getRestricciones().size(); i++) {
            if (i > 0) restriccionesStr.append(";");
            restriccionesStr.append(escaparRestriccion(venue.getRestricciones().get(i)));
        }
        csv.append(restriccionesStr.toString()).append(SEPARADOR);
        
        // Convertir localidades a string
        StringBuilder localidadesStr = new StringBuilder();
        for (int i = 0; i < venue.getLocalidades().size(); i++) {
            if (i > 0) localidadesStr.append(SEPARADOR_LOCALIDADES);
            localidadesStr.append(convertirLocalidadACSV(venue.getLocalidades().get(i)));
        }
        csv.append(localidadesStr.toString());
        
        return csv.toString();
    }
    
    /**
     * Convierte una localidad a formato CSV simplificado
     */
    private String convertirLocalidadACSV(Localidad localidad) {
        StringBuilder csv = new StringBuilder();
        
        csv.append(localidad.getId()).append(SEPARADOR_ATRIBUTOS);
        csv.append(escaparCSV(localidad.getTipoLocalidad())).append(SEPARADOR_ATRIBUTOS);
        csv.append(localidad.isNumerada()).append(SEPARADOR_ATRIBUTOS);
        csv.append(localidad.getCapacidad()).append(SEPARADOR_ATRIBUTOS);
        csv.append(localidad.getPrecioBase());
        
        return csv.toString();
    }
    
    /**
     * Convierte una línea CSV a objeto Venue (incluyendo localidades)
     */
    private Venue convertirCSVAVenue(String lineaCSV) {
        try {
            String[] partes = lineaCSV.split(SEPARADOR, -1);
            
            if (partes.length < 7) {
                System.err.println("Línea CSV inválida: " + lineaCSV);
                return null;
            }
            
            String id = partes[0].trim();
            String nombre = desescaparCSV(partes[1].trim());
            String ubicacion = desescaparCSV(partes[2].trim());
            int capacidad = Integer.parseInt(partes[3].trim());
            boolean aprobado = Boolean.parseBoolean(partes[4].trim());
            String restriccionesStr = partes[5].trim();
            String localidadesStr = partes[6].trim();
            
            Venue venue = new Venue();
            venue.setId(id);
            venue.setNombre(nombre);
            venue.setUbicacion(ubicacion);
            venue.setCapacidad(capacidad);
            venue.setAprobado(aprobado);
            
            // Procesar restricciones
            ArrayList<String> restricciones = new ArrayList<>();
            if (!restriccionesStr.isEmpty()) {
                String[] restriccionesArray = restriccionesStr.split(";");
                for (String restriccion : restriccionesArray) {
                    restricciones.add(desescaparRestriccion(restriccion.trim()));
                }
            }
            venue.setRestricciones(restricciones);
            
            // Procesar localidades
            ArrayList<Localidad> localidades = new ArrayList<>();
            if (!localidadesStr.isEmpty()) {
                String[] localidadesArray = localidadesStr.split(SEPARADOR_LOCALIDADES);
                for (String localidadStr : localidadesArray) {
                    Localidad localidad = convertirCSVALocalidad(localidadStr, venue);
                    if (localidad != null) {
                        localidades.add(localidad);
                    }
                }
            }
            venue.setLocalidades(localidades);
            
            return venue;
            
        } catch (Exception e) {
            System.err.println("Error al convertir CSV a venue: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Convierte una string de localidad a objeto Localidad
     */
    private Localidad convertirCSVALocalidad(String localidadCSV, Venue venue) {
        try {
            String[] partes = localidadCSV.split("\\" + SEPARADOR_ATRIBUTOS, -1);
            
            if (partes.length < 5) {
                System.err.println("Localidad CSV inválida: " + localidadCSV);
                return null;
            }
            
            String id = partes[0].trim();
            String tipoLocalidad = desescaparCSV(partes[1].trim());
            boolean numerada = Boolean.parseBoolean(partes[2].trim());
            int capacidad = Integer.parseInt(partes[3].trim());
            double precioBase = Double.parseDouble(partes[4].trim());
            
            Localidad localidad = new Localidad();
            localidad.setId(id);
            localidad.setTipoLocalidad(tipoLocalidad);
            localidad.setNumerada(numerada);
            localidad.setVenue(venue);
            localidad.setCapacidad(capacidad);
            localidad.setPrecioBase(precioBase);
            
            return localidad;
            
        } catch (Exception e) {
            System.err.println("Error al convertir CSV a localidad: " + e.getMessage());
            return null;
        }
    }
    
    // ... (métodos auxiliares escaparCSV, desescaparCSV, etc. se mantienen igual)
    /**
     * Escapa caracteres especiales en restricciones
     */
    private String escaparRestriccion(String restriccion) {
        return restriccion.replace(";", "\\;").replace(",", "\\,").replace("|", "\\|");
    }
    
    /**
     * Desescapa restricciones
     */
    private String desescaparRestriccion(String restriccion) {
        return restriccion.replace("\\;", ";").replace("\\,", ",").replace("\\|", "|");
    }
    
    /**
     * Escapa comas y comillas en valores CSV
     */
    private String escaparCSV(String valor) {
        if (valor.contains(",") || valor.contains("\"") || valor.contains("\n") || valor.contains(";") || valor.contains("|")) {
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