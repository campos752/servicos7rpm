package sco;

import models.Noticia;

import org.junit.Test;

import static org.fest.assertions.Assertions.*;

import com.gargoylesoftware.htmlunit.html.HtmlPage;

public class IntranetTest {

	@Test
	public void testeCadastro() {
		IntranetFactory i = new IntranetFactory();
		i.getFormularioCadNoticia();
		HtmlPage p = i.getPaginaAtual();
		assertThat(p.asText()).contains("Incluir notícia");
		Noticia n = new Noticia();
		n.titulo = "Titulo";
		n.resumo = "Resumo";
		n.conteudo = "Conteudo";
		i.cadastrarNoticia(n);
		p = i.getPaginaAtual();
		assertThat(p.asText()).contains("sucesso");
	}
}