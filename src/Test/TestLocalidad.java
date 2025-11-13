package Tests;

import static org.junit.Assert.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Date;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import modelo.eventos.Evento;
import modelo.eventos.Localidad;
import modelo.eventos.Oferta;
import modelo.eventos.Venue;
import modelo.tiquetes.Tiquete;
import modelo.usuarios.Comprador;
import modelo.usuarios.Organizador;

public class TestLocalidad {
	
	private Localidad localidad1;
	private Localidad localidad2;
	private Venue venue1;
	private Venue venue2;
	private Tiquete tiquete1;
	private Tiquete tiquete2;
	private Tiquete tiquete3;
	private Tiquete tiquete4;
	private Tiquete tiquete5;
	private Oferta oferta1;
	private Oferta oferta2;
	private Date fecha1;
	private Date fecha2;
	private Date fecha3;
	private Evento evento1;
	private Evento evento2;
	private Organizador organizador1;
	private Organizador organizador2;
	private Comprador comprador;
	
	@BeforeEach
    public void setup() throws Exception {
    	fecha1 = new Date(2027, 5, 20, 18, 0);
    	fecha2 = new Date(2026, 6, 15, 20, 0);
    	fecha3 = new Date(2024, 1, 20, 18, 0);
    	venue1 = new Venue("V001", "Auditorio Nacional", "Ciudad de México", 5000);
    	venue2 = new Venue("V002", "Teatro Colón", "Buenos Aires", 3000);
    	localidad1 = new Localidad("L001", "Gramilla", false, venue1, 400, 300.0);
    	localidad2 = new Localidad("L002", "Mayores", true, venue2, 2, 100.0);
    	organizador1 = new Organizador("Pedro","Pedro1010");
    	organizador2 = new Organizador("Shakira", "Skai123");
    	evento1 = new Evento("E001", "Concierto de Rock", fecha1, venue1, organizador1);
    	evento2 = new Evento("E002", "Obra de Teatro", fecha2, venue2, organizador2);
    	tiquete1 = new Tiquete("T001", 400, fecha1, localidad1, evento1);
    	tiquete2 = new Tiquete("T002", 100.0, fecha2, localidad2, evento2);
    	tiquete3 = new Tiquete("T003", 100.0, fecha1, localidad1, evento1);
    	tiquete4 = new Tiquete("T004", 400.0, fecha1, localidad1, evento1);
    	tiquete5 = new Tiquete("TQ-E001-L001-" + System.currentTimeMillis(), 300.0, fecha1, localidad1, evento1);
    	comprador = new Comprador("Comprador", "123");
    	oferta1 = new Oferta("OF001", "40% de descuento en localidad de mayores", 0.4, fecha3, fecha1, evento1);
    	oferta2 = new Oferta("OF002", "10% de descuento en localidad de Premium", 0.1, fecha3, fecha1, evento2);
        }
        
    @AfterEach
    public void tearDown( ) throws Exception
    {
    	
    	
    }

    @Test
    void testAgregarOferta() 
    {
    	assertEquals(0, localidad1.getOfertas().size(), "El tamanio es incorrecto");
    	localidad1.agregarOferta(oferta1);
    	assertEquals(1, localidad1.getOfertas().size(), "El tamanio es incorrecto");
    	localidad1.agregarOferta(oferta1);
    	assertEquals(1, localidad1.getOfertas().size(), "El tamanio es incorrecto");
    	localidad1.agregarOferta(null);
    	assertEquals(1, localidad1.getOfertas().size(), "El tamanio es incorrecto");
    }
    
    @Test
    void testPrecioConOferta() 
    {
    	localidad1.agregarOferta(oferta1);
    	assertEquals(180.0, localidad1.getPrecioConOfertas(), "El precio es incorrecto");
    	localidad1.agregarOferta(oferta2);
    	assertEquals(162.0, localidad1.getPrecioConOfertas(), "El precio es incorrecto");
    }
    
    @Test
    void testTieneOfertasVigentesOfertasVigentes() 
    {
    	assertFalse("No hay ofertas", localidad1.tieneOfertasVigentes());
    	localidad1.agregarOferta(oferta1);
    	assertTrue(localidad1.tieneOfertasVigentes(), "Hay ofertas vigentes");
    	assertEquals(1, localidad1.getOfertasVigentes().size(), "Hay una oferta vigente");
    }
    
    @Test
    void testAgregarTiquete() 
    {
    	assertEquals(0, localidad1.getTiquetesDisponibles(), "El tamanio es incorrecto");
    	localidad1.agregarTiquete(tiquete1);
    	assertEquals(1, localidad1.getTiquetesDisponibles(), "El tamanio es incorrecto");
    	localidad1.agregarTiquete(tiquete1);
    	assertEquals(1, localidad1.getTiquetesDisponibles(), "El tamanio es incorrecto");
    	localidad1.agregarTiquete(null);
    	assertEquals(1, localidad1.getTiquetesDisponibles(), "El tamanio es incorrecto");
    	localidad2.agregarTiquete(tiquete1);
    	localidad2.agregarTiquete(tiquete2);
    	localidad2.agregarTiquete(tiquete3);
    	assertEquals(2, localidad2.getTiquetesDisponibles(), "El tamanio es incorrecto");
    	
    }
    
    @Test
    void testCrearNuevoTiquete() 
    {
    	localidad1.crearTiquete(evento1);
    	localidad1.agregarTiquete(tiquete1);
    	assertEquals(1, localidad1.getTiquetesDisponibles(), "El tamanio es incorrecto");
    	assertEquals(null, localidad2.crearTiquete(null), "No hay evento");
    	assertEquals(null, localidad2.crearTiquete(evento1), "No hay disponibilidad");
    	localidad2.agregarTiquete(tiquete1);
    	assertEquals(null, localidad2.crearTiquete(null), "No hay disponibilidad");
    	assertEquals(tiquete5, localidad1.crearTiquete(evento1), "Tiquete incorrecto");
    }
    
    @Test
    void testObtenerTiqueteDisponible() 
    {
    	assertEquals(null, localidad1.obtenerTiqueteDisponible(), "No hay tiquetes");
    	localidad1.agregarTiquete(tiquete1);
    	assertEquals(tiquete1, localidad1.obtenerTiqueteDisponible(), "Tiquete incorrecto");
    }
    
    @Test
    void testObtenerTiquetesDisponibles() 
    {
    	assertEquals(0, localidad1.obtenerTiquetesDisponibles(2).size(), "No hay tiquetes");
    	localidad1.agregarTiquete(tiquete1);
    	localidad1.agregarTiquete(tiquete2);
    	localidad1.agregarTiquete(tiquete3);
    	assertEquals(2, localidad1.obtenerTiquetesDisponibles(2).size(), "No se puede seleccionar mas tiquetes");
    	assertEquals(3, localidad1.obtenerTiquetesDisponibles(3).size(), "Hay 3 Tiquetes");
    }
    
    @Test
    void testHaySuficienteDisponibilidad()
    {
    	assertFalse("No hay tiquetes", localidad1.haySuficienteDisponibilidad(5));
    	localidad1.agregarTiquete(tiquete1);
    	localidad1.agregarTiquete(tiquete2);
    	localidad1.agregarTiquete(tiquete3);
    	assertFalse("No hay suficiente disponibilidad", localidad1.haySuficienteDisponibilidad(5));
    	assertTrue(localidad1.haySuficienteDisponibilidad(2), "Hay suficiente disponibilidad");
    }
    
    @Test
    void testInformacionDetallada() 
    {
    	String numerada;
    	if (localidad1.isNumerada())
    	{
    		numerada = "SÍ";
    	}
    	else
    	{
    		numerada = "NO";
    	}
    	assertEquals("=== INFORMACIÓN DE LOCALIDAD ===\nTipo: " + localidad1.getTipoLocalidad() + "\nID: " + localidad1.getId() + "\nVenue: " + localidad1.getVenue().getNombre() + "\nNumerada: " + numerada + "\nCapacidad: " + localidad1.getCapacidad() + "\nPrecio base: $" + localidad1.getPrecioBase() + "\nTiquetes disponibles: " + localidad1.getTiquetesDisponibles() + "\nTiquetes vendidos: " + localidad1.getTiquetesVendidos() + "\nPorcentaje vendido: " + String.format("%.1f", localidad1.getPorcentajeVendido()) + "%\n", localidad1.getInformacionDetallada(), "El Texto no es correcto");
    	localidad1.agregarTiquete(tiquete1);
    	localidad1.agregarOferta(oferta1);
    	assertEquals("=== INFORMACIÓN DE LOCALIDAD ===\nTipo: " + localidad1.getTipoLocalidad() + "\nID: " + localidad1.getId() + "\nVenue: " + localidad1.getVenue().getNombre() + "\nNumerada: " + numerada + "\nCapacidad: " + localidad1.getCapacidad() + "\nPrecio base: $" + localidad1.getPrecioBase() + "\nPrecio con ofertas: " + localidad1.getPrecioConOfertas() + "\nOfertas vigentes: " + localidad1.getOfertasVigentes().size() + "\nTiquetes disponibles: " + localidad1.getTiquetesDisponibles() + "\nTiquetes vendidos: " + localidad1.getTiquetesVendidos() + "\nPorcentaje vendido: " + String.format("%.1f", localidad1.getPorcentajeVendido()) + "%\n", localidad1.getInformacionDetallada(), "El Texto no es correcto");
    }
    
    @Test
    void testDescripcion() 
    {
    	assertEquals(localidad1.getTipoLocalidad() + " - $" + localidad1.getPrecioBase() + " - " + localidad1.getTiquetesDisponibles() + " disponibles", localidad1.getDescripcion(), "El texto es incorrecto");
    	localidad1.agregarTiquete(tiquete1);
    	localidad1.agregarTiquete(tiquete2);
    	localidad1.agregarTiquete(tiquete3);
    	localidad1.agregarOferta(oferta1);
    	assertEquals(localidad1.getTipoLocalidad() + "- $" + localidad1.getPrecioBase() + " → $" + localidad1.getPrecioConOfertas()  + "(OFERTA)" + " - " + localidad1.getTiquetesDisponibles() + " disponibles", localidad1.getDescripcion(), "El texto es incorrecto");
    }
    
}
