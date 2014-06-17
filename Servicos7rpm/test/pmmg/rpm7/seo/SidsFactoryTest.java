package pmmg.rpm7.seo;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Date;

import javax.persistence.EntityManager;

import org.junit.Test;

import pmmg.rpm7.seo.modelos.Municipio;
import pmmg.rpm7.seo.modelos.Rat;
import pmmg.rpm7.seo.modelos.RatProdutividade;

public class SidsFactoryTest {
	private SidsFactory s;
	
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
	public void testProcessaLinha(){
		Rat r = new Rat();
		s.processaLinha("2014-RAT-0004370879;Fechado;(Y07003) OPERACAO DE INCURSAO EM ZONA QUENTE DE CRIMINALIDADE;01/05/2014 18:00;CARLOS ROBERTO TEIXEIRA/1034735;RUA SAMUEL BERNARDES / RUA DONA MARIA MARTINS DE ABREU - LAGOA DA PRATA / MG", r);
		assertTrue(r.getEndereco().equals("SAMUEL BERNARDES"));
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
	public void gravar(){
		EntityManager em = s.getEntityManager();
		Rat rat = s.getEntityManager().find(Rat.class, "2014-RAT-0005847497");
		rat.setId("2014-RAT-0005847497");
		rat.setBairro("CENTRO");
		rat.setCodNatureza("Y10000");
		rat.setCodUnidade("M2560");
		rat.setComplemento("FUNDOS");
		rat.setDataInicio(new Date());
		rat.setDescNatureza("DESCRICAO DE OPERACAO");
		rat.setEndereco("RUA UM");
		rat.setMatDigitador(1111111);
		rat.setMunicipio("DIVINOPOLIS");
		rat.setNomeDigitador("CB JOSE");
		rat.setNomeUnidade("23 BPM/7 RPM");
		rat.setNrEndereco(200);
		rat.setProdutividade(new ArrayList<RatProdutividade>());
		RatProdutividade p1 = new RatProdutividade();
		p1.setNrAtividade("2014-RAT-0005847497");
		p1.setDescricao("Veic fiscalizados");
		p1.setQtd(1);
		RatProdutividade p2 = new RatProdutividade();
		p2.setNrAtividade("2014-RAT-0005847497");
		p2.setDescricao("Veic retidos");
		p2.setQtd(2);
		RatProdutividade p3 = new RatProdutividade();
		p3.setNrAtividade("2014-RAT-0005847497");
		p3.setDescricao("Veic apreendidos");
		p3.setQtd(3);
		rat.getProdutividade().add(p1);
		rat.getProdutividade().add(p2);
		rat.getProdutividade().add(p3);
		em.getTransaction().begin();
		em.persist(rat);
		em.getTransaction().commit();
		System.out.println("Rat adicionado com sucesso");
		em.close();
	}
	
	@Test
	public void testaRatAberto(){
		Rat r = new Rat();
		r.setId("2014-RAT-0006184315");
		s.logar("pm1277524", "@casp123+-");
		s.atualizaRat(r);
		s.sair();
	}
	
	@Test
	public void testJPA(){
		Municipio m = s.getEntityManager().find(Municipio.class, 312230);
		System.out.println(m.getNome());
	}

}
