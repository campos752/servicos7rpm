package sco;

import java.util.ArrayList;

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
//		assertThat(p.asText()).contains("Portal PM");
		Noticia n = new Noticia();
		n.titulo = "Titulo";
		n.resumo = "Resumo";
		n.conteudo = "Conteudo";
//		n.imagens = new ArrayList<String>();
//		n.imagens.add("./1392904136463.jpg");
		i.cadastrarNoticia(n);
		p = i.getPaginaAtual();
		assertThat(p.asText()).contains("sucesso");
	}
}