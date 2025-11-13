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

public class TestVenue {
	
	private Localidad localidad1;
	private Localidad localidad2;
	private Localidad localidad3;
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
    	localidad2 = new Localidad("L003", "Menores", true, venue1, 100, 100.0);
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
    void testAgregarLocalidad() 
    {
    	venue1.agregarLocalidad(localidad1);
    	assertEquals(1, venue1.getLocalidades().size(), "El tamanio es incorrecto");
    	venue1.agregarLocalidad(localidad1);
    	assertEquals(1, venue1.getLocalidades().size(), "El tamanio es incorrecto");
    	venue1.agregarLocalidad(null);
    	assertEquals(1, venue1.getLocalidades().size(), "El tamanio es incorrecto");
    	venue1.agregarLocalidad(localidad2);
    	assertEquals(2, venue1.getLocalidades().size(), "El tamanio es incorrecto");
    }
    
    @Test
    void testCrearLocalidad() 
    {
    	venue1.crearLocalidad("L003", "Menores", true, 100, 100.0);
    	assertEquals(1, venue1.getLocalidades().size(), "El tamanio es incorrecto");
    	assertEquals(localidad3, venue1.crearLocalidad("L003", "Menores", true, 100, 100.0));
    }
    
    @Test
    void testLocalidadPorTipo() 
    {
    	venue1.agregarLocalidad(localidad2);
    	assertEquals(localidad2, venue1.getLocalidadPorTipo("Mayores"), "Hay una localidad de ese tipo");
    	assertEquals(null, venue1.getLocalidadPorTipo("Menores"), "No hay localidad de ese tipo");
    }
}