package pmmg.rpm7.seo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.PersistenceUnit;
import javax.swing.text.TableView.TableRow;

import org.apache.log4j.Logger;

import pmmg.rpm7.seo.modelos.Municipio;
import pmmg.rpm7.seo.modelos.Rat;

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
	@PersistenceUnit
	private EntityManager entityManager;
	private EntityManagerFactory factory;
	
	public SidsFactory(){
		webClient.getOptions().setUseInsecureSSL(true);
		webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
		webClient.getOptions().setThrowExceptionOnScriptError(false);
		webClient.getOptions().setPrintContentOnFailingStatusCode(false);
		
		factory = Persistence.createEntityManagerFactory("7rpm");
		entityManager = factory.createEntityManager();
	}
	
	public HtmlPage getPaginaAtual(){
		return paginaAtual;
	}
	
	public EntityManager getEntityManager() {
		return entityManager;
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
	
	public List<String> getRelatorio(Municipio municipio, Date data, int tipo, String nr_atividade){
		List<String> resultado = null;
		log.info("Iniciando processamento de Relatorios");
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
			params.add(new NameValuePair("ind_relatorio_consolidado", (tipo == REL_CONSOLIDADO?"S":"N")));
			params.add(new NameValuePair("num_atividade_ano", (nr_atividade == null?"":nr_atividade.substring(0, 4))));
			params.add(new NameValuePair("num_atividade_seq", (nr_atividade == null?"":nr_atividade.substring(9))));
			params.add(new NameValuePair("id_natureza", ""));
			params.add(new NameValuePair("cod_natureza", ""));
			params.add(new NameValuePair("desc_natureza", ""));
			params.add(new NameValuePair("naturezasURL", "https://web.sids.mg.gov.br/reds/treeServlet?t=nat&data=20140110"));
			params.add(new NameValuePair("cod_municipio_unidade_resp", tipo == REL_CONSOLIDADO?"":String.valueOf(municipio.getId())));
			params.add(new NameValuePair("nom_municipio_unidade_resp", tipo == REL_CONSOLIDADO?"":municipio.getNome().toUpperCase()));
			params.add(new NameValuePair("municipiosURL", "https://web.sids.mg.gov.br/reds/treeServlet?t=mun&p1=MG&data=20130423"));
			params.add(new NameValuePair("carregaCodigosMunicipiosResp", "false"));
			params.add(new NameValuePair("id_unidade_resp", tipo == REL_CONSOLIDADO?"":"10"));
			params.add(new NameValuePair("cod_unidade_resp", tipo == REL_CONSOLIDADO?"":"M0584"));
			params.add(new NameValuePair("nome_unidade_resp", tipo == REL_CONSOLIDADO?"":"7+RPM+(M0584)"));
			if(nr_atividade==null) params.add(new NameValuePair("ind_listar_subordinadas_resp", "on"));
			params.add(new NameValuePair("dta_inicio", fmt.format(data)));
			params.add(new NameValuePair("hra_inicio", "00:00"));
			params.add(new NameValuePair("dta_fim", fmt.format(data)));
			params.add(new NameValuePair("hra_fim", "23:59"));
			params.add(new NameValuePair("consultar", "Pesquisar"));
			params.add(new NameValuePair("maxResults", "20"));
			params.add(new NameValuePair("firstItem", "1"));
			params.add(new NameValuePair("lastItem", "20"));
			params.add(new NameValuePair("pageNum", ""));
			if(nr_atividade==null) params.add(new NameValuePair("num_atividade", ""));
			requisicao.setRequestParameters(params);
			
			paginaAtual = webClient.getPage(requisicao);
			log.info("Resultado de pesquisa retornado");
			
			HtmlImageInput csv = paginaAtual.getElementByName("CSV");
			Page p = csv.click();
			log.info("Exportando para CSV");
			InputStream in = p.getWebResponse().getContentAsStream();
			BufferedReader buf = new BufferedReader(new InputStreamReader(in, "ISO-8859-15"), 8192);
			String linha;
	        while ((linha = buf.readLine()) != null) {
	        	if(resultado == null) resultado = new ArrayList<String>();
	        	resultado.add(linha);
			}
			buf.close();
		} catch (FailingHttpStatusCodeException | IOException e) {
			log.error(e);
		}
		
		return resultado;
	}
	
	public void processaLinha(String linha, Rat rat){
		SimpleDateFormat fmtRat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		String[] dados = linha.split(";");
		rat.setId(dados[0]);
		log.info("Obtendo dados da RAT nr: " + rat.getId());
		rat.setEstado(dados[1]);
		rat.setCodNatureza(dados[2].substring(1, 6));
		rat.setDescNatureza(dados[2].substring(9));
		try{
			rat.setDataInicio(fmtRat.parse(dados[3]));
		}catch(ParseException e){
			log.error(linha, e);
		}
		rat.setNomeDigitador(dados[4].split("/")[0]);
		try{
			Integer m = Integer.parseInt(dados[4].split("/")[1]);
			rat.setMatDigitador(m);
		}catch(NumberFormatException e){
			log.warn(linha, e);
		}
		String end = dados[5];
		int idxNr = end.indexOf(" No. ");
		int idxTraco = end.indexOf(" - ");
		int idxVirgula = end.indexOf(",", idxNr);
		if(idxNr < 0){ // Operação em intercessao de logradouros
			String str = end.substring(0, end.indexOf(" - "));
			rat.setTipoLogradouro(str.substring(0, str.indexOf(" ")));
			rat.setEndereco(str.split(" / ")[0].substring(str.indexOf(' ') + 1));
			rat.setComplemento(str.split(" / ")[1]);
			rat.setMunicipio(end.substring(3, end.substring(idxTraco).indexOf(" / ")));
		}else{
			rat.setTipoLogradouro(end.substring(0, end.indexOf(' ')));
			rat.setEndereco(end.substring(end.indexOf(' ') + 1, idxNr));
			rat.setMunicipio(end.substring(idxTraco + 3, end.indexOf(" / ", idxTraco)));
		}
		
		if(idxVirgula > 0){ // Possui numeracao
			try{
				String strNr = end.substring(idxNr + 5, idxVirgula);
				if(strNr.indexOf(" - ")>0){ //Possui complemento
					log.info("Possiu complemento");
					String nr = strNr.substring(0, strNr.indexOf(" - "));
					if(nr.matches("\\d+"))
						rat.setNrEndereco(Integer.parseInt(nr));
					else
						log.warn("Numero não encontrado: " + nr);
					rat.setComplemento(strNr.substring(strNr.indexOf(" - ") + 3));
				}else{
					if(strNr.matches("\\d+"))
						rat.setNrEndereco(Integer.parseInt(strNr));
					else
						log.warn("Numero não encontrado: " + strNr);
				}
				rat.setBairro(end.substring(idxVirgula + 1, end.indexOf(" - ", idxVirgula)));
			}catch(NumberFormatException e){
				log.warn(linha, e);
			}
		}
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
		@SuppressWarnings("unchecked")
		List<Municipio> municipios = entityManager.createQuery("select m from Municipio m").getResultList();
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
		entityManager.close();
		factory.close();
	}

}
