package Tests;

import modelo.eventos.Venue;
import modelo.eventos.Evento;
import modelo.pagos.Compra;
import modelo.tiquetes.Tiquete;
import modelo.eventos.Localidad;
import modelo.usuarios.Administrador;
import modelo.usuarios.Comprador;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Date;

class administradorTest {
	private Administrador administrador;
	private Comprador compradorTesteo;
	private Venue venueTesteo;
	private Evento eventoTesteo;
	private Localidad localidadTesteo;
	
	void setup() {
		administrador = new Administrador("adminUser","adminPassword");
		venueTesteo = new Venue("1234","Estadio prueba","Bogotá", 1000);
		compradorTesteo.agregarSaldo(1000);
		localidadTesteo = new Localidad("1122","", false , venueTesteo, 500, 1000);
		eventoTesteo   = new Evento("1122","Concierto prueba", new Date(System.currentTimeMillis() + 86400000), venueTesteo, null);
	}
	
	@Test
	void testConstructor() {
		assertEquals("adminUser", administrador.getLogin());
		assertEquals("adminPassword", administrador.getPassword());
		assertEquals(0.10, administrador.getPorcentajeAdicional(), 0.001);
        assertEquals(5.0, administrador.getCobroFijo(), 0.001);
	}
	
	@Test
	void testSaldoVirtual() {
		assertEquals(0.0, administrador.getSaldoVirtual(), 0.001);
		 UnsupportedOperationException exception = assertThrows(UnsupportedOperationException.class,() -> administrador.setSaldoVirtual(100.0));
		 assertEquals("El administrador no maneja un saldo virtual", exception.getMessage());
		 UnsupportedOperationException exception_2 = assertThrows(UnsupportedOperationException.class,() -> administrador.agregarSaldo(50.0));
		 assertEquals("El administrador no puede tener un saldo virtual", exception_2.getMessage());
	}
	
	@Test
	void testConfiguracionValoresNegativos() {
		double porcentaje_inicial = administrador.getPorcentajeAdicional();
		double cobro_porcentaje_fijo = administrador.getCobroFijo();
		administrador.fijarPorcentajeAdicional(-0.05);
		administrador.fijarCobroFijo(-5);
		
		assertEquals(porcentaje_inicial, administrador.getPorcentajeAdicional(), 0.001);
		assertEquals(cobro_porcentaje_fijo, administrador.getCobroFijo(), 0.001);
		
	}
	
	@Test
	void testAprovarVenue() {
		assertFalse(venueTesteo.isAprobado());
		administrador.aprobarVenue(venueTesteo);
		assertTrue(venueTesteo.isAprobado());
	}
	
	@Test
	void testCrearVenue() {
		Venue nuevo_venue = administrador.crearVenue("1123", "Nuevo Venue", "Cartagena", 1000);
		
		assertEquals("1123", nuevo_venue.getId());
		assertEquals("Nuevo Venue", nuevo_venue.getNombre());
		assertEquals("Cartagena", nuevo_venue.getUbicacion());
		assertEquals(1000, nuevo_venue.getCapacidad());
		
		assertTrue(nuevo_venue.isAprobado());
	}
	
	@Test
	void testCancelarEvento() {
		assertFalse(eventoTesteo.isCancelado());
		ArrayList<Comprador> no_hay_compradores = new ArrayList<>();
		administrador.cancelarEvento(eventoTesteo, no_hay_compradores);
		assertTrue(eventoTesteo.isCancelado());
	}
	
	@Test
	void testObservarGananciasListaVacia() {
		ArrayList<Compra> compras = new ArrayList<>();
		Tiquete tiquete_1 = new Tiquete("tiquete1", 50, new Date(), localidadTesteo, eventoTesteo);
		Tiquete tiquete_2 = new Tiquete("tiquete2", 50, new Date(), localidadTesteo, eventoTesteo);
		
		ArrayList<Tiquete> tiquetes_comprados = new ArrayList<>();
		tiquetes_comprados.add(tiquete_1);
		tiquetes_comprados.add(tiquete_2);
		
		Compra compra = new Compra("001", new Date(), 125, tiquetes_comprados, compradorTesteo);
		compra.setEstado("aprobada");
		compras.add(compra);
	}
	
	@Test
	void testAutorizarReembolsoDatosInvalidos() {
		Tiquete tiquete = new Tiquete("002",100,new Date(), localidadTesteo, eventoTesteo);
		assertDoesNotThrow(() -> administrador.autorizarReembolso(null, tiquete, "Test"));
		assertDoesNotThrow(() -> administrador.autorizarReembolso(compradorTesteo, null, "Test"));
	}
	
	
	
	
	
	
}
