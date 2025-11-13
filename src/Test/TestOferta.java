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

public class TestOferta {
	
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
	private Date fecha4;
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
    	fecha4 = new Date(2024, 3, 20, 18, 0);
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
    	oferta2 = new Oferta("OF002", "10% de descuento en localidad de Premium", 0.1, fecha3, fecha4, evento2, localidad1);
        }
        
    @AfterEach
    public void tearDown( ) throws Exception
    {
    	
    	
    }
    
    @Test
    void testEstaVigente() 
    {
    	assertTrue(oferta1.estaVigente(), "Debe estar vigente");
    	assertFalse("No debe estar vigente", oferta2.estaVigente());
    }
    
    @Test
    void testAplicarDescuento() 
    {
    	assertEquals(300.0, oferta1.aplicarDescuento(500.0), "El valor es incorrecto");
    	assertEquals(500.0, oferta2.aplicarDescuento(500.0), "El valor esnincorrecto");
    }
    
    @Test
    void testInformacionDetallada() 
    {
    	oferta1.activar();
    	String estado;
    	if (oferta1.estaVigente())
    	{
    		estado = "VIGENTE";
    	}
    	else
    	{
    		estado = "NO VIGENTE";
    	}
    	String activa;
    	if (oferta1.isActiva())
    	{
    		activa = "SÍ";
    	}
    	else
    	{
    		activa = "NO";
    	}
    	assertEquals("=== INFORMACIÓN DE OFERTA ===\n" + "ID: " + oferta1.getId() + "\nDescripción: " + oferta1.getDescripcion() + "\nDescuento: 40.0%\nTipo: " + oferta1.getTipo() + "\nEvento: " + oferta1.getEvento().getNombre() + "\nFecha inicio: " + oferta1.getFechaInicio() + "\nFecha expiración: " + oferta1.getFechaExpiracion() + "\nEstado: " + estado + "\nActiva: " + activa + "\n", oferta1.getInformacionDetallada(), "El Texto no es correcto");
    	oferta2.desactivar();
    	if (oferta1.estaVigente())
    	{
    		estado = "VIGENTE";
    	}
    	else
    	{
    		estado = "NO VIGENTE";
    	}
    	if (oferta1.isActiva())
    	{
    		activa = "SÍ";
    	}
    	else
    	{
    		activa = "NO";
    	}
    	assertEquals("=== INFORMACIÓN DE OFERTA ===\n" + "ID: " + oferta2.getId() + "\nDescripcion: " + oferta2.getDescripcion() + "\nDescuento: 10.0%\nTipo: " + oferta2.getTipo() + "\nEvento: " + oferta2.getEvento().getNombre() + "Localidad: " + oferta2.getLocalidad().getTipoLocalidad() + "\n" + "\nFecha inicio: " + oferta2.getFechaInicio() + "\nFecha expiración: " + oferta2.getFechaExpiracion() + "\nEstado: " + estado + "\nActiva: " + activa + "\n", oferta1.getInformacionDetallada(), "El Texto no es correcto");
    }
}