package sco;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;

import play.Logger;
import play.Play;
import models.Noticia;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.ElementNotFoundException;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlCheckBoxInput;
import com.gargoylesoftware.htmlunit.html.HtmlFileInput;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlHiddenInput;
import com.gargoylesoftware.htmlunit.html.HtmlInput;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlPasswordInput;
import com.gargoylesoftware.htmlunit.html.HtmlSubmitInput;
import com.gargoylesoftware.htmlunit.html.HtmlTextArea;
import com.gargoylesoftware.htmlunit.html.HtmlTextInput;

public class IntranetFactory {

	private WebClient webClient = new WebClient(BrowserVersion.FIREFOX_24);
	private HtmlPage paginaAtual;

	public IntranetFactory() {
		webClient.getOptions().setThrowExceptionOnScriptError(false);
		webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
		webClient.getOptions().setUseInsecureSSL(true);
		webClient.getOptions().setJavaScriptEnabled(true);
		webClient.getOptions().setRedirectEnabled(true);
		;
		webClient.getOptions().setCssEnabled(true);
	}

	public void getFormularioCadNoticia() {
		try {
			paginaAtual = webClient.getPage("https://intranet.policiamilitar.mg.gov.br/AutenticacaoSSO/login.action?josso_back_to=https://intranet.policiamilitar.mg.gov.br/auth-pmmg/josso_security_check");
			Logger.info("Site carregado com sucesso.");
			HtmlTextInput textLogin = (HtmlTextInput) paginaAtual.getElementById("textLogin");
			HtmlPasswordInput senha = (HtmlPasswordInput) paginaAtual.getElementById("senha");
			HtmlSubmitInput btn = (HtmlSubmitInput) paginaAtual.getElementById("formlogin__login");
			textLogin.setValueAttribute("1277524");
			senha.setValueAttribute("machado");
			paginaAtual = btn.click();
			Logger.info("Pagina inicial carregada");
			
			HtmlAnchor hrefCominicaçãoSocial = paginaAtual.getAnchorByHref("javascript:openLegadoApp('/comunicacao/default.asp');");
			paginaAtual = hrefCominicaçãoSocial.click();
			HtmlAnchor hrefManutencao = paginaAtual.getAnchorByText("Manutenção");
			paginaAtual = hrefManutencao.click();
			HtmlAnchor hrefAdmGeral = paginaAtual.getAnchorByText("Administração Geral");
			paginaAtual = hrefAdmGeral.click();
			HtmlAnchor hrefNoticias = paginaAtual.getAnchorByText("Notícias");
			paginaAtual = hrefNoticias.click();
			HtmlAnchor hrefIncluir = paginaAtual.getAnchorByText("Incluir");
			paginaAtual = hrefIncluir.click();

		} catch (ElementNotFoundException e) {
			e.printStackTrace();
		} catch (FailingHttpStatusCodeException e) {
			e.printStackTrace();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void cadastrarNoticia(Noticia noticia) {
		HtmlForm formCadNoticia = paginaAtual.getFormByName("cadNoticia");
		HtmlTextInput titulo = formCadNoticia.getInputByName("entity.titulo");
		titulo.setValueAttribute(noticia.titulo);
		HtmlHiddenInput unidade = formCadNoticia.getInputByName("entity.unidade.id");
		unidade.setValueAttribute("584");
		formCadNoticia.getInputByName("entity.unidade.nome").setValueAttribute("7 RPM");
		HtmlCheckBoxInput destaque = formCadNoticia.getInputByName("entity.destaque");
		destaque.setChecked(true);
		HtmlTextArea resumo = formCadNoticia.getTextAreaByName("entity.resumo");
		resumo.setText(noticia.resumo);
		List<HtmlInput> listaFileInput = formCadNoticia
				.getInputsByName("upload");
		if (noticia.imagens != null) {
			for (int i = 0; i < noticia.imagens.size()	&& i < listaFileInput.size(); i++) {
				HtmlHiddenInput localImagem1 = formCadNoticia.getInputByName("entity.localImagem1");
				HtmlHiddenInput nomeImagem1 = formCadNoticia.getInputByName("entity.nomeImagem1");
				HtmlHiddenInput typeImagem1 = formCadNoticia.getInputByName("entity.contentTypeImagem5");
				localImagem1.setValueAttribute(noticia.imagens.get(i));
				nomeImagem1.setValueAttribute("./1392904136463.jpg");
				typeImagem1.setValueAttribute("image/jpeg");
//				HtmlFileInput fileInput = (HtmlFileInput) listaFileInput.get(i);
//				fileInput.setValueAttribute(noticia.imagens.get(i));
			}
		}
		HtmlTextArea inputTexto = formCadNoticia.getTextAreaByName("entity.texto");
		inputTexto.setText(noticia.conteudo);
		try {
			paginaAtual = formCadNoticia.getInputByName("cmdSalvar").click();
		} catch (ElementNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public HtmlPage getPaginaAtual() {
		return paginaAtual;
	}
}