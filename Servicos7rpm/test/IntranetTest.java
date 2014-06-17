

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Test;

import pmmg.rpm7.sco.IntranetFactory;
import pmmg.rpm7.sco.Noticia;

import com.gargoylesoftware.htmlunit.html.HtmlPage;

public class IntranetTest {

	@Test
	public void testeCadastro() {
		IntranetFactory i = new IntranetFactory();
		i.getFormularioCadNoticia();
		HtmlPage p = i.getPaginaAtual();
		Noticia n = new Noticia();
		n.titulo = "Titulo";
		n.resumo = "Resumo";
		n.conteudo = "Conteudo";
		n.imagens = new ArrayList<String>();
		n.imagens.add("1392904136463.jpg");
		i.cadastrarNoticia(n);
		p = i.getPaginaAtual();
		assertTrue(p.asText().indexOf("sucesso") > 0);
	}
}