package pmmg.rpm7.seo;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;

import org.apache.log4j.Logger;

import pmmg.rpm7.seo.modelos.Municipio;
import pmmg.rpm7.seo.modelos.Rat;
import pmmg.rpm7.seo.modelos.RatProdutividade;

public class RatImport {
	private SidsFactory f = new SidsFactory();
	private static Logger log = Logger.getLogger(RatImport.class);
	private  EntityManager em = f.getEntityManager();
	
	public RatImport(){
		f.logar("pm1277524", "@casp123+-");
		Map<String, String> unidades = f.getUnidades();
		log.info("Qtd de unidades importadas:" + unidades.size());
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DAY_OF_YEAR, -1);
		log.info("Iniciando importacao a partir do dia: " + cal.getTime().toString());
		List<String> relatorio = f.getRelatorio(new Municipio(312230, "DIVINOPOLIS"), cal.getTime(), 
				SidsFactory.REL_DETALHADO, null);
		log.info("Relatorio detalhado obtido com sucesso.");
		for(String linha : relatorio){
			if(!linha.startsWith("20")) continue;
			Rat rat = new Rat();
			f.processaLinha(linha, rat);
			log.info("Obtendo produtividade do RAT");
			if("Fechado".equals(rat.getEstado())){
				List<String> relProd = f.getRelatorio(null, cal.getTime(), SidsFactory.REL_CONSOLIDADO, rat.getId());
				for(String prod : relProd){
					log.info("Iten de produtividade: " + prod);
					if(prod.startsWith("Unidade")) continue;
					String[] dados = prod.split(";");
					if(dados[0].length() > 0){
						rat.setCodUnidade(unidades.get(dados[0]));
						rat.setNomeUnidade(dados[0]);
					}
					RatProdutividade p = new RatProdutividade();
					p.setNrAtividade(rat.getId());
					p.setDescricao(dados[1]);
					p.setQtd(Integer.parseInt(dados[2]));
					if(rat.getProdutividade() == null) rat.setProdutividade(new ArrayList<RatProdutividade>());
					rat.getProdutividade().add(p);
				}
			}
			em.getTransaction().begin();
			em.persist(rat);
			em.getTransaction().commit();
			log.info("Rat gravado com sucesso");
		}
		f.sair();
	}
	
	private void processaRatsAbertos(){
		@SuppressWarnings("unchecked")
		List<Rat> ratAbertos = em.createQuery("select r from Rat r").getResultList();
		for(Rat rat : ratAbertos){
			
		}
	}
	
	public static void main(String[] args) {
		new RatImport();
	}
}
