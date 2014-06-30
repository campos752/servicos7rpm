package pmmg.rpm7.seo;

import static org.junit.Assert.*;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Scanner;

import org.junit.Test;

import pmmg.rpm7.seo.modelos.Rat;

public class SidsFactoryTest {
	private SidsFactory s;
	private RatParser parser = new RatParser();
	
	public SidsFactoryTest(){
		s = new SidsFactory();
	}
	
	@Test
	public void testLogar() {
		s.logar("pm1277524", "@casp123+-");
//		Municipio m = new Municipio();
//		m.setId(312230);
//		m.setNome("DIVINOPOLIS");
//		List<String> r = s.getRelatorio(m, new Date(), SidsFactory.REL_CONSOLIDADO, "2014-RAT-0005737539");
//		assertEquals(4, r.size());
		s.getUnidades();
		s.sair();
	}
	
	@Test
	public void testProcessaLinha() throws FileNotFoundException{
		Scanner scn = new Scanner(new FileInputStream("relatorio.csv"), "ISO-8859-1");
		while(scn.hasNextLine()){
			Rat r = new Rat();
			String str = scn.nextLine();
			parser.processaLinha(str, r);
			System.out.println(r.getTipoLogradouro() + "\t Endereco: " + r.getEndereco() + "\t Nr: " + r.getNrEndereco() +
					"\t Compl: " + r.getComplemento() + "\t Bairro: " + r.getBairro() + "\t Mun.: " + r.getMunicipio());
		}
		scn.close();
	}
	
	@Test
	public void testImportaUeop(){
		Rat r = new Rat();
		r.setId("2014-RAT-0004328711");
		s.logar("pm1277524", "@casp123+-");
		s.importaUeop(r);
		assertTrue(r.getCodUnidade().equals("M2639"));
		assertTrue("4 GP/3 PEL PM RV/7 CIA PM IND MAT".equals(r.getNomeUnidade()));
	}
	
	@Test
	public void testaRatAberto(){
		Rat r = new Rat();
		r.setId("2014-RAT-0006593999");
		s.logar("pm1277524", "@casp123+-");	
		assertFalse(s.isRatFechado(r.getId()));
		s.sair();
	}
}
