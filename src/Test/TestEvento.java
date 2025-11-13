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

public class TestEvento {
	
	private Evento evento1;
	private Evento evento2;
	private Date fecha1;
	private Date fecha2;
	private Venue venue1;
	private Venue venue2;
	private Tiquete tiquete1;
	private Tiquete tiquete2;
	private Tiquete tiquete3;
	private Tiquete tiquete4;
	private Oferta oferta;
	private Organizador organizador1;
	private Organizador organizador2;
    private ArrayList<Oferta> ofertas;
    private Localidad localidad1;
    private Localidad localidad2;
    private Date fecha3;
    private Comprador comprador;

    @BeforeEach
    public void setup() throws Exception {
    	fecha1 = new Date(2027, 5, 20, 18, 0);
    	fecha2 = new Date(2026, 6, 15, 20, 0);
    	fecha3 = new Date(2024, 1, 20, 18, 0);
    	venue1 = new Venue("V001", "Auditorio Nacional", "Ciudad de México", 5000);
    	venue2 = new Venue("V002", "Teatro Colón", "Buenos Aires", 3000);
    	localidad1 = new Localidad("L001", "Gramilla", false, venue1, 400, 300.0);
    	localidad2 = new Localidad("L002", "Mayores", true, venue1, 200, 100.0);
    	organizador1 = new Organizador("Pedro","Pedro1010");
    	organizador2 = new Organizador("Shakira", "Skai123");
    	evento1 = new Evento("E001", "Concierto de Rock", fecha1, venue1, organizador1);
    	evento2 = new Evento("E002", "Obra de Teatro", fecha2, venue2, organizador2);
    	tiquete1 = new Tiquete("T001", 400, fecha1, localidad1, evento1);
    	tiquete2 = new Tiquete("T002", 100.0, fecha2, localidad2, evento2);
    	tiquete3 = new Tiquete("T003", 100.0, fecha1, localidad2, evento1);
    	tiquete4 = new Tiquete("T004", 400.0, fecha1, localidad1, evento1);
    	comprador = new Comprador("Comprador", "123");
    	oferta = new Oferta("OF001", "40% de descuento en localidad de mayores", 0.4, fecha3, fecha1, evento1);
        }
        
    @AfterEach
    public void tearDown( ) throws Exception
    {
    	
    	
    }

    @Test
    void testAprobarRechazarCancelar() 
    {
        evento1.aprobar();
        assertTrue(evento1.isAprobado(), "El evento debe estar aprobado");
        assertEquals(false, evento1.isCancelado(), "El evento no debe estar cancelado");
        assertEquals(false, evento2.isAprobado(), "El evento no debe estar aprobado");
        evento1.rechazar();
        assertEquals(false, evento1.isAprobado(), "El evento debe ser rechazado");
        evento1.cancelar();
        assertTrue(evento1.isCancelado(), "El evento debe estar cancelado");
    }
    
    @Test
    void testEstaActivo() 
    {
    	assertEquals(false, evento1.estaActivo(), "El evento no debe estar activo");
    	evento1.aprobar();
    	assertTrue(evento1.estaActivo(), "El evento debe estar activo");
    	evento1.cancelar();
    	assertEquals(false, evento1.estaActivo(), "El evento no debe estar activo");
    	evento1.rechazar();
    	assertEquals(false, evento1.estaActivo(), "El evento no debe estar activo");
    }
    
    @Test
    void testEstaVencido() 
    {
    	assertEquals(false, evento1.estaVencido(), "La fecha ya paso");
    	assertTrue(evento2.estaVencido(), "La fecha no ha pasado");
    }
    
    @Test
    void testAgregarTiquete() 
    {
    	evento1.agregarTiquete(tiquete1);
    	assertEquals(1, evento1.getTiquetes().size(), "Hay 1 tiquete");
    	evento1.agregarTiquete(tiquete2);
    	evento1.agregarTiquete(tiquete3);
    	assertEquals(3, evento1.getTiquetes().size(), "Hay 3 tiquetes");
    	evento1.agregarTiquete(tiquete1);
    	assertEquals(3, evento1.getTiquetes().size(), "Hay 3 tiquetes");
    	
    }
    
    @Test
    void testTiquetesDisponiblesHayTiqueteDisponible() 
    {
    	assertEquals(false, evento1.hayTiquetesDisponibles(), "No hay eventos disponibles");
    	evento1.agregarTiquete(tiquete1);
    	evento1.agregarTiquete(tiquete2);
    	assertEquals(2, evento1.getTiquetesDisponibles(), "Hay 2 tiquetes disponibles");
    	assertTrue(evento1.hayTiquetesDisponibles(), "Hay eventos disponibles");
    	
    }
    
    @Test
    void testTiquetesvendidosPctgVendido() 
    {
    	assertEquals(0, evento1.getTiquetesVendidos(), "No hay tiquetes para vender");
    	evento1.agregarTiquete(tiquete1);
    	evento1.agregarTiquete(tiquete2);
    	evento1.agregarTiquete(tiquete4);
    	evento1.aprobar();
    	System.out.println("a");
    	System.out.println("a");
    	System.out.println(localidad1.hayDisponibilidad());
    	comprador.comprarTiquete(evento1, localidad1, 1, 0.2, 20);
    	assertEquals(1, evento1.getTiquetesVendidos(), "Se ha vendido un tiquete");
    	assertEquals(50, evento1.getPorcentajeVendido(), "se han vendido la mitad de los tiquetes");
    	evento1.agregarTiquete(tiquete3);
    	evento1.agregarTiquete(tiquete4);
    	assertEquals(25, evento1.getPorcentajeVendido(), "Se ha vendido el 25% de los tiquetes");
    }
    
    @Test
    void testTiquetesDisponiblesPorLocalidadHayTiquetesDisponiblesEnLocalidad() 
    {
    	assertEquals(false, evento1.hayTiquetesDisponiblesEnLocalidad(localidad1), "No hay tiquetes en la localidad");
    	evento1.agregarTiquete(tiquete1);
    	evento1.agregarTiquete(tiquete2);
    	assertEquals(1, evento1.getTiquetesDisponiblesPorLocalidad(localidad1).size(), "Hay un tiquete");
    	assertTrue(evento1.hayTiquetesDisponiblesEnLocalidad(localidad1), "Hay tiquetes en la localidad");
    	assertEquals(1, evento1.getTiquetesDisponiblesPorLocalidad(localidad2).size(), "Hay un tiquete");
    	evento1.agregarTiquete(tiquete4);
    	assertEquals(2, evento1.getTiquetesDisponiblesPorLocalidad(localidad1).size(), "Hay 2 tiquetes");
    }
    
    @Test
    void testAgregarOferta() 
    {
    	assertEquals(0, evento1.getOfertas().size(), "No existen ofertas");
    	evento1.agregarOferta(oferta);
    	assertEquals(1, evento1.getOfertas().size(), "Hay una oferta");
    	evento1.agregarOferta(null);
    	assertEquals(1, evento1.getOfertas().size(), "Hay una oferta");
    	evento1.agregarOferta(oferta);
    	assertEquals(1, evento1.getOfertas().size(), "Hay una oferta");
    }
    
    @Test
    void testOfertasVigentesTieneOfertasVigentes() 
    {
    	assertFalse("Hay ofertas vigentes", evento1.tieneOfertasVigentes());
    	evento1.agregarOferta(oferta);
    	assertEquals(1, evento1.getOfertasVigentes().size(), "Hay una oferta vigente");
    	assertTrue(evento1.tieneOfertasVigentes(), "Hay ofertas vigentes");
    }
    
    
    @Test
    void testInformacionDetallada() 
    {
    	assertEquals("=== INFORMACIÓN DEL EVENTO ===\nNombre: " + evento1.getNombre() + "\nID: " + evento1.getId() + "\nFecha: " + evento1.getFechaHora() + "\nVenue: " + evento1.getVenue().getNombre() + "\nOrganizador: " + evento1.getOrganizador().getLogin() + "\nEstado: " + evento1.getEstado() + "\nTiquetes totales: " + String.valueOf(evento1.getTiquetes().size()) + "\nTiquetes disponibles: " + evento1.getTiquetesDisponibles() + "\nTiquetes vendidos: " + evento1.getTiquetesVendidos() + "\nPorcentaje vendido: " + String.format("%.1f", evento1.getPorcentajeVendido()) + "%\n", evento1.getInformacionDetallada(), "El Texto no es correcto");
    	evento1.agregarTiquete(tiquete1);
    	evento1.agregarOferta(oferta);
    	assertEquals("=== INFORMACIÓN DEL EVENTO ===\nNombre: " + evento1.getNombre() + "\nID: " + evento1.getId() + "\nFecha: " + evento1.getFechaHora() + "\nVenue: " + evento1.getVenue().getNombre() + "\nOrganizador: " + evento1.getOrganizador().getLogin() + "\nEstado: " + evento1.getEstado() + "\nTiquetes totales: " + String.valueOf(evento1.getTiquetes().size()) + "\nTiquetes disponibles: " + evento1.getTiquetesDisponibles() + "\nTiquetes vendidos: " + evento1.getTiquetesVendidos() + "\nPorcentaje vendido: " + String.format("%.1f", evento1.getPorcentajeVendido()) + "%\n", evento1.getInformacionDetallada(), "El Texto no es correcto");
    }
    
    @Test
    void testGetEstado() 
    {
    	assertEquals("PENDIENTE DE APROBACIÓN", evento1.getEstado(), "El estado es incorrecto");
    	evento1.aprobar();
    	assertEquals("AGOTADO", evento1.getEstado(), "El estado es incorrecto");
    	evento1.agregarTiquete(tiquete1);
    	evento1.agregarTiquete(tiquete2);
    	assertEquals("DISPONIBLE", evento1.getEstado(), "El estado es incorrceto");
    	evento1.cancelar();
    	assertEquals("CANCELADO", evento1.getEstado(), "El estado es incorrecto");
    	
    }

}