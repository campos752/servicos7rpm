package pmmg.rpm7.seo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.log4j.Logger;

import pmmg.rpm7.seo.modelos.Municipio;
import pmmg.rpm7.seo.modelos.Municipio.MunicipioMapper;
import pmmg.rpm7.seo.modelos.Rat.RatMapper;
import pmmg.rpm7.seo.modelos.Rat;
import pmmg.rpm7.seo.modelos.RatProdutividade.RatProdutividadeMapper;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.HttpMethod;
import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlImageInput;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlPasswordInput;
import com.gargoylesoftware.htmlunit.html.HtmlTable;
import com.gargoylesoftware.htmlunit.html.HtmlTableRow;
import com.gargoylesoftware.htmlunit.html.HtmlTextInput;
import com.gargoylesoftware.htmlunit.util.NameValuePair;

public class SidsFactory {
	public static final int REL_DETALHADO = 0;
	public static final int REL_CONSOLIDADO = 1;
	
	private WebClient webClient = new WebClient(BrowserVersion.FIREFOX_24);
	private Logger log = Logger.getLogger(SidsFactory.class);
	private HtmlPage paginaAtual;
	private boolean isPagRelCarregada = false;
	private SqlSessionFactory factory;
	
	public SidsFactory(){
		webClient.getOptions().setUseInsecureSSL(true);
		webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
		webClient.getOptions().setThrowExceptionOnScriptError(false);
		webClient.getOptions().setPrintContentOnFailingStatusCode(false);
		
		Reader r;
		try {
			r = Resources.getResourceAsReader("SqlMapConfig.xml");
			factory = new SqlSessionFactoryBuilder().build(r);
			factory.getConfiguration().addMapper(MunicipioMapper.class);
			factory.getConfiguration().addMapper(RatMapper.class);
			factory.getConfiguration().addMapper(RatProdutividadeMapper.class);
		} catch (IOException e) {
			log.error(e);
		}
	}
	
	public HtmlPage getPaginaAtual(){
		return paginaAtual;
	}
	
	public SqlSessionFactory getFactory() {
		return factory;
	}

	public void logar(String usuario, String senha){
		try {
			paginaAtual = webClient.getPage("https://web.sids.mg.gov.br/reds/index.do");
			log.info("Pagina inicial carregada.");
			HtmlForm formLogin = paginaAtual.getFormByName("usernamePasswordLoginForm");
			HtmlTextInput txtUsuario = formLogin.getInputByName("josso_username");
			HtmlPasswordInput txtSenha = formLogin.getInputByName("josso_password");
			txtUsuario.setValueAttribute(usuario);
			txtSenha.setValueAttribute(senha);
			paginaAtual = formLogin.getInputByName("submit_btn").click();
			log.info("Login efetuado");
			isPagRelCarregada = false;
		} catch (FailingHttpStatusCodeException | IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public List<String> getListagemRAT(Municipio municipio, Date data){
		List<String> resultado = null;
		log.info("Iniciando processamento de listagem de RAT");
		if(paginaAtual == null){
			log.error("Login nao efetuado no site.");
			return null;
		}
		try {
			if(!isPagRelCarregada){
				paginaAtual = webClient.getPage("https://web.sids.mg.gov.br/reds/atividade/consultarAtividade.do?operation=loadForSearch&cod_tipo_atividade=RAT");
				log.info("Pagina de pesquisa carregada");
				isPagRelCarregada = true;
			}
			SimpleDateFormat fmt = new SimpleDateFormat("dd/MM/yyyy");
			WebRequest requisicao = new WebRequest(new URL("https://web.sids.mg.gov.br/reds/atividade/consultarAtividade.do"), HttpMethod.POST);
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new NameValuePair("isCSTG", "false"));
			params.add(new NameValuePair("isCSTO", "false"));
			params.add(new NameValuePair("cod_tipo_atividade", "RAT"));
			params.add(new NameValuePair("id_orgao", "0"));
			params.add(new NameValuePair("origem", "0"));
			params.add(new NameValuePair("dta_corrente", fmt.format(new Date())));
			params.add(new NameValuePair("maxDiasPeriodoConsulta", "3650"));
			params.add(new NameValuePair("ajaxFlag", ""));
			params.add(new NameValuePair("operation", "search"));
			params.add(new NameValuePair("ajaxRequest", "N"));
			params.add(new NameValuePair("ind_relatorio_consolidado", "N"));
			params.add(new NameValuePair("num_atividade_ano", ""));
			params.add(new NameValuePair("num_atividade_seq", ""));
			params.add(new NameValuePair("id_natureza", ""));
			params.add(new NameValuePair("cod_natureza", ""));
			params.add(new NameValuePair("desc_natureza", ""));
			params.add(new NameValuePair("naturezasURL", "https://web.sids.mg.gov.br/reds/treeServlet?t=nat&data=20140110"));
			params.add(new NameValuePair("cod_municipio_unidade_resp", String.valueOf(municipio.getId())));
			params.add(new NameValuePair("nom_municipio_unidade_resp", municipio.getNome().toUpperCase()));
			params.add(new NameValuePair("municipiosURL", "https://web.sids.mg.gov.br/reds/treeServlet?t=mun&p1=MG&data=20130423"));
			params.add(new NameValuePair("carregaCodigosMunicipiosResp", "false"));
			params.add(new NameValuePair("id_unidade_resp", "10"));
			params.add(new NameValuePair("cod_unidade_resp", "M0584"));
			params.add(new NameValuePair("nome_unidade_resp", "7+RPM+(M0584)"));
			params.add(new NameValuePair("ind_listar_subordinadas_resp", "on"));
			params.add(new NameValuePair("dta_inicio", fmt.format(data)));
			params.add(new NameValuePair("hra_inicio", "00:00"));
			params.add(new NameValuePair("dta_fim", fmt.format(data)));
			params.add(new NameValuePair("hra_fim", "23:59"));
			params.add(new NameValuePair("consultar", "Pesquisar"));
			params.add(new NameValuePair("maxResults", "20"));
			params.add(new NameValuePair("firstItem", "1"));
			params.add(new NameValuePair("lastItem", "20"));
			params.add(new NameValuePair("pageNum", ""));
			params.add(new NameValuePair("num_atividade", ""));
			requisicao.setRequestParameters(params);
			
			paginaAtual = webClient.getPage(requisicao);
			log.info("Pagina com resultado obtida, obtendo arquivo CSV");
			
			HtmlImageInput csv = paginaAtual.getElementByName("CSV");
			Page p = csv.click();
			InputStream in = p.getWebResponse().getContentAsStream();
			BufferedReader buf = new BufferedReader(new InputStreamReader(in, "ISO-8859-15"), 8192);
			String linha;
	        while ((linha = buf.readLine()) != null) {
	        	if(resultado == null) resultado = new ArrayList<String>();
	        	resultado.add(linha);
			}
			buf.close();
			log.info("LIstagem obtida com sucesso.");
		} catch (FailingHttpStatusCodeException | IOException e) {
			log.error(e);
		}
		return resultado;
	}
	
	public List<String> getProdutividade(Rat rat){
		List<String> resultado = null;
		log.info("Iniciando processamento de Relatorio de Produtividade");
		if(paginaAtual == null){
			log.error("Login nao efetuado no site.");
			return null;
		}
		try {
			if(!isPagRelCarregada){
				paginaAtual = webClient.getPage("https://web.sids.mg.gov.br/reds/atividade/consultarAtividade.do?operation=loadForSearch&cod_tipo_atividade=RAT");
				log.info("Pagina de pesquisa carregada");
				isPagRelCarregada = true;
			}
			
			SimpleDateFormat fmt = new SimpleDateFormat("dd/MM/yyyy");
			WebRequest requisicao = new WebRequest(new URL("https://web.sids.mg.gov.br/reds/atividade/consultarAtividade.do"), HttpMethod.POST);
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new NameValuePair("isCSTG", "false"));
			params.add(new NameValuePair("isCSTO", "false"));
			params.add(new NameValuePair("cod_tipo_atividade", "RAT"));
			params.add(new NameValuePair("id_orgao", "0"));
			params.add(new NameValuePair("origem", "0"));
			params.add(new NameValuePair("dta_corrente", fmt.format(new Date())));
			params.add(new NameValuePair("maxDiasPeriodoConsulta", "3650"));
			params.add(new NameValuePair("ajaxFlag", ""));
			params.add(new NameValuePair("operation", "search"));
			params.add(new NameValuePair("ajaxRequest", "N"));
			params.add(new NameValuePair("ind_relatorio_consolidado", "S"));
			params.add(new NameValuePair("num_atividade_ano", rat.getId().substring(0, 4)));
			params.add(new NameValuePair("num_atividade_seq", rat.getId().substring(9)));
			params.add(new NameValuePair("id_natureza", ""));
			params.add(new NameValuePair("cod_natureza", ""));
			params.add(new NameValuePair("desc_natureza", ""));
			params.add(new NameValuePair("naturezasURL", "https://web.sids.mg.gov.br/reds/treeServlet?t=nat&data=20140110"));
			params.add(new NameValuePair("cod_municipio_unidade_resp", ""));
			params.add(new NameValuePair("nom_municipio_unidade_resp", ""));
			params.add(new NameValuePair("municipiosURL", "https://web.sids.mg.gov.br/reds/treeServlet?t=mun&p1=MG&data=20130423"));
			params.add(new NameValuePair("carregaCodigosMunicipiosResp", "false"));
			params.add(new NameValuePair("id_unidade_resp", ""));
			params.add(new NameValuePair("cod_unidade_resp", ""));
			params.add(new NameValuePair("nome_unidade_resp", ""));
			params.add(new NameValuePair("dta_inicio", ""));
			params.add(new NameValuePair("hra_inicio", ""));
			params.add(new NameValuePair("dta_fim", ""));
			params.add(new NameValuePair("hra_fim", ""));
			params.add(new NameValuePair("consultar", "Pesquisar"));
			params.add(new NameValuePair("maxResults", "20"));
			params.add(new NameValuePair("firstItem", "1"));
			params.add(new NameValuePair("lastItem", "20"));
			params.add(new NameValuePair("pageNum", ""));
			requisicao.setRequestParameters(params);
			
			paginaAtual = webClient.getPage(requisicao);
			log.info("Pagina pesquisada retornada, obtendo arquivo CSV");
			
			HtmlImageInput csv = paginaAtual.getElementByName("CSV");
			Page p = csv.click();
			InputStream in = p.getWebResponse().getContentAsStream();
			BufferedReader buf = new BufferedReader(new InputStreamReader(in, "ISO-8859-15"), 8192);
			String linha;
	        while ((linha = buf.readLine()) != null) {
	        	if(resultado == null) resultado = new ArrayList<String>();
	        	resultado.add(linha);
			}
			buf.close();
			log.info("Relatorio de produtividade obtido com sucesso.");
		} catch (FailingHttpStatusCodeException | IOException e) {
			log.error(e);
		}
		
		return resultado;

	}

	
	public void importaUeop(Rat rat){
		try {
			log.info("Enviando requisicao de codigo e nome da unidade");
			WebRequest requisicao = new WebRequest(new URL("https://web.sids.mg.gov.br/reds/atividade/consultarAtividade.do"), HttpMethod.POST);
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new NameValuePair("num_atividade_consulta", rat.getId()));
			params.add(new NameValuePair("operation", "open"));
			params.add(new NameValuePair("ajaxRequest", "S"));
			requisicao.setRequestParameters(params);
			
			HtmlPage pagina = webClient.getPage(requisicao);
			pagina = webClient.getPage("https://web.sids.mg.gov.br/reds/dialogs/resumoEvento.do?operation=loadResumoAtividade");
			HtmlTable tabela = (HtmlTable)pagina.getElementsByTagName("table").get(0);
			for(HtmlTableRow tr: tabela.getBodies().get(0).getRows()){
				if(tr.asText().indexOf("Unidade") >= 0){
					String str = tr.asText();
					if(str.indexOf("(M")>0){
						rat.setCodUnidade(str.substring(str.indexOf('(') + 1, str.indexOf(')')));
						rat.setNomeUnidade(str.substring(str.indexOf('\t') + 1, str.indexOf("(M")).trim());
					}else
						log.warn("Erro ao processar codigo unidade: " + tr.asText());
					
				}
			}
			log.info("Unidade importada com sucesso: " + rat.getNomeUnidade());
		} catch (FailingHttpStatusCodeException | IOException e) {
			e.printStackTrace();
		}
	}
	
	public Map<String, String> getUnidades(){
		Map<String, String> unidades = null;
		SqlSession s = factory.openSession();
		List<Municipio> municipios = s.getMapper(MunicipioMapper.class).getTodos();
		
		try{
			for(Municipio m : municipios){
				WebRequest r = new WebRequest(new URL("https://web.sids.mg.gov.br/reds/treeServlet?t=unid&p1=0&p2=" + m.getId()), HttpMethod.GET);
				HtmlPage p = webClient.getPage(r);
				List<HtmlAnchor> itens = p.getAnchors();
				for(HtmlAnchor a : itens){
					if(unidades == null) unidades = new HashMap<String, String>();
					String str = a.getAttribute("title");
					int ini = str.indexOf(" (M");
					unidades.put(str.substring(0, ini), str.substring(ini + 2, str.indexOf(")", ini)));
				}
			}
		}catch(FailingHttpStatusCodeException | IOException e){
			log.error(e);
		}finally{
			s.close();
		}
		
		return unidades;
	}
	
	public void atualizaRat(Rat rat){
		log.info("Iniciando processamento de Rat: " + rat.getId());
		if(paginaAtual == null){
			log.error("Login nao efetuado no site.");
			return;
		}
		try {
			if(!isPagRelCarregada){
				paginaAtual = webClient.getPage("https://web.sids.mg.gov.br/reds/atividade/consultarAtividade.do?operation=loadForSearch&cod_tipo_atividade=RAT");
				log.info("Pagina de pesquisa carregada");
				isPagRelCarregada = true;
			}
			
			SimpleDateFormat fmt = new SimpleDateFormat("dd/MM/yyyy");
			WebRequest requisicao = new WebRequest(new URL("https://web.sids.mg.gov.br/reds/atividade/consultarAtividade.do"), HttpMethod.POST);
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new NameValuePair("isCSTG", "false"));
			params.add(new NameValuePair("isCSTO", "false"));
			params.add(new NameValuePair("cod_tipo_atividade", "RAT"));
			params.add(new NameValuePair("id_orgao", "0"));
			params.add(new NameValuePair("origem", "0"));
			params.add(new NameValuePair("dta_corrente", fmt.format(new Date())));
			params.add(new NameValuePair("maxDiasPeriodoConsulta", "3650"));
			params.add(new NameValuePair("ajaxFlag", ""));
			params.add(new NameValuePair("operation", "search"));
			params.add(new NameValuePair("ajaxRequest", "N"));
			params.add(new NameValuePair("ind_relatorio_consolidado", "N"));
			params.add(new NameValuePair("num_atividade_ano", rat.getId().substring(0, 4)));
			params.add(new NameValuePair("num_atividade_seq", rat.getId().substring(9)));
			params.add(new NameValuePair("id_natureza", ""));
			params.add(new NameValuePair("cod_natureza", ""));
			params.add(new NameValuePair("desc_natureza", ""));
			params.add(new NameValuePair("naturezasURL", "https://web.sids.mg.gov.br/reds/treeServlet?t=nat&data=20140110"));
			params.add(new NameValuePair("cod_municipio_unidade_resp", ""));
			params.add(new NameValuePair("nom_municipio_unidade_resp", ""));
			params.add(new NameValuePair("municipiosURL", "https://web.sids.mg.gov.br/reds/treeServlet?t=mun&p1=MG&data=20130423"));
			params.add(new NameValuePair("carregaCodigosMunicipiosResp", "false"));
			params.add(new NameValuePair("id_unidade_resp", ""));
			params.add(new NameValuePair("cod_unidade_resp", ""));
			params.add(new NameValuePair("nome_unidade_resp", ""));
			params.add(new NameValuePair("dta_inicio", ""));
			params.add(new NameValuePair("hra_inicio", ""));
			params.add(new NameValuePair("dta_fim", ""));
			params.add(new NameValuePair("hra_fim", ""));
			params.add(new NameValuePair("consultar", "Pesquisar"));
			params.add(new NameValuePair("maxResults", "20"));
			params.add(new NameValuePair("firstItem", "1"));
			params.add(new NameValuePair("lastItem", "20"));
			params.add(new NameValuePair("pageNum", ""));
			params.add(new NameValuePair("num_atividade", ""));
			requisicao.setRequestParameters(params);
			
			paginaAtual = webClient.getPage(requisicao);
			log.info("Resultado de Rat retornado");
			HtmlTable tabela = paginaAtual.getHtmlElementById("resumeCollectionTable");
			for(HtmlTableRow linha : tabela.getRows()){
				if(linha.getCell(1).asText().indexOf("-RAT-")<0)
					continue;
				rat.setEstado(linha.getCell(2).asText());
			}
		} catch (FailingHttpStatusCodeException | IOException e) {
			log.error(e);
		}
	}
	
	public boolean isRatFechado(String nrAtividade){
		log.info("Verificado se RAT " + nrAtividade + " esta aberto");
		if(paginaAtual == null){
			log.error("Login nao efetuado no site.");
			return false;
		}
		try {
			if(!isPagRelCarregada){
				paginaAtual = webClient.getPage("https://web.sids.mg.gov.br/reds/atividade/consultarAtividade.do?operation=loadForSearch&cod_tipo_atividade=RAT");
				log.info("Pagina de pesquisa carregada");
				isPagRelCarregada = true;
			}
			
			SimpleDateFormat fmt = new SimpleDateFormat("dd/MM/yyyy");
			WebRequest requisicao = new WebRequest(new URL("https://web.sids.mg.gov.br/reds/atividade/consultarAtividade.do"), HttpMethod.POST);
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new NameValuePair("isCSTG", "false"));
			params.add(new NameValuePair("isCSTO", "false"));
			params.add(new NameValuePair("cod_tipo_atividade", "RAT"));
			params.add(new NameValuePair("id_orgao", "0"));
			params.add(new NameValuePair("origem", "0"));
			params.add(new NameValuePair("dta_corrente", fmt.format(new Date())));
			params.add(new NameValuePair("maxDiasPeriodoConsulta", "3650"));
			params.add(new NameValuePair("ajaxFlag", ""));
			params.add(new NameValuePair("operation", "search"));
			params.add(new NameValuePair("ajaxRequest", "N"));
			params.add(new NameValuePair("ind_relatorio_consolidado", "N"));
			params.add(new NameValuePair("num_atividade_ano", nrAtividade.substring(0, 4)));
			params.add(new NameValuePair("num_atividade_seq", nrAtividade.substring(9)));
			params.add(new NameValuePair("id_natureza", ""));
			params.add(new NameValuePair("cod_natureza", ""));
			params.add(new NameValuePair("desc_natureza", ""));
			params.add(new NameValuePair("naturezasURL", "https://web.sids.mg.gov.br/reds/treeServlet?t=nat&data=20140110"));
			params.add(new NameValuePair("cod_municipio_unidade_resp", ""));
			params.add(new NameValuePair("nom_municipio_unidade_resp", ""));
			params.add(new NameValuePair("municipiosURL", "https://web.sids.mg.gov.br/reds/treeServlet?t=mun&p1=MG&data=20130423"));
			params.add(new NameValuePair("carregaCodigosMunicipiosResp", "false"));
			params.add(new NameValuePair("id_unidade_resp", ""));
			params.add(new NameValuePair("cod_unidade_resp", ""));
			params.add(new NameValuePair("nome_unidade_resp", ""));
			params.add(new NameValuePair("dta_inicio", ""));
			params.add(new NameValuePair("hra_inicio", ""));
			params.add(new NameValuePair("dta_fim", ""));
			params.add(new NameValuePair("hra_fim", ""));
			params.add(new NameValuePair("consultar", "Pesquisar"));
			params.add(new NameValuePair("maxResults", "20"));
			params.add(new NameValuePair("firstItem", ""));
			params.add(new NameValuePair("lastItem", ""));
			requisicao.setRequestParameters(params);
			
			paginaAtual = webClient.getPage(requisicao);
//			log.info("Resultado de Rat retornado >> " + paginaAtual.asText());
			if(paginaAtual.asText().indexOf("Fechado") >=0)
				return true;
			else 
				return false;
		} catch (FailingHttpStatusCodeException | IOException e) {
			log.error(e);
			return false;
		}
	}
	
	public void sair(){
		if(paginaAtual == null){
			log.error("Login nao efetuado no site.");
			return;
		}
		try {
			paginaAtual = webClient.getPage("https://web.sids.mg.gov.br/reds/externo/access.do?operation=logout");
			log.info("Efetuado logout na sistema");
		} catch (FailingHttpStatusCodeException | IOException e) {
			log.error(e);
		}
		webClient.closeAllWindows();
	}

}
