package pmmg.rpm7.seo;

import java.net.SocketTimeoutException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.apache.log4j.Logger;

import pmmg.rpm7.seo.modelos.Municipio;
import pmmg.rpm7.seo.modelos.Rat;
import pmmg.rpm7.seo.modelos.Rat.RatMapper;
import pmmg.rpm7.seo.modelos.RatProdutividade.RatProdutividadeMapper;
import pmmg.rpm7.seo.modelos.RatProdutividade;

public class RatImport {
	private SidsFactory f = new SidsFactory();
	private RatParser parser = new RatParser();
	private Map<String, String> unidades;
	private static Logger log = Logger.getLogger(RatImport.class);
	private SqlSession session;
	
	
	public RatImport(){
		session = f.getFactory().openSession();
		f.logar("pm1277524", "@casp123+-");
		unidades = f.getUnidades();
		log.info("Qtd de unidades importadas:" + unidades.size());
		
		processaRatsAbertos();
	}
	
	public void importarDiaAnterior() throws SocketTimeoutException{
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DAY_OF_YEAR, -1);
		log.info("Iniciando importacao do dia: " + cal.getTime().toString());
		List<String> relatorio = f.getListagemRAT(new Municipio(312230, "DIVINOPOLIS"), cal.getTime());
		log.info("Listagem de rat obtida com sucesso.");
		for(String linha : relatorio){
			if(!linha.startsWith("20")) continue;
			Rat rat = new Rat();
			parser.processaLinha(linha, rat);
			Rat r = session.getMapper(RatMapper.class).getRat(rat.getId());
			if(r != null && "Fechado".equals(r.getEstado())){
				log.info("Rat nr " + r.getId() + " ja cadastrado no banco de dados");
				continue;
			}
			processaProdutividade(rat);
			
			gravarRat(rat);
		}
	}
	
	public void importar(Date inicio, Date fim) throws SocketTimeoutException{
		Calendar calInicial = Calendar.getInstance();
		calInicial.setTime(inicio);
		Calendar calFinal = Calendar.getInstance();
		calFinal.setTime(fim);
		while(calInicial.before(calFinal)){
			log.info("Iniciando importacao a partir do dia: " + calInicial.getTime().toString());
			List<String> relatorio = f.getListagemRAT(new Municipio(312230, "DIVINOPOLIS"), calInicial.getTime());
			log.info("Listagem de rat obtida com sucesso.");
			for(String linha : relatorio){
				if(!linha.startsWith("20")) continue;
				Rat rat = new Rat();
				parser.processaLinha(linha, rat);
				Rat r = session.getMapper(RatMapper.class).getRat(rat.getId());
				if(r != null && "Fechado".equals(r.getEstado())){
					log.info("Rat nr " + r.getId() + " ja cadastrado no banco de dados");
					continue;
				}
				processaProdutividade(rat);
				
				gravarRat(rat);
			}
			calInicial.add(Calendar.DAY_OF_YEAR, 1);
		}
	}
	
	private void processaProdutividade(Rat rat){
		log.info("Obtendo produtividade do RAT " + rat.getId());
		if("Fechado".equals(rat.getEstado())){
			List<String> relProd;
			relProd = f.getProdutividade(rat);
			parser.setProdutividade(relProd, rat, unidades);
		}

	}
	
	private void gravarRat(Rat rat){
		RatMapper mapper = session.getMapper(RatMapper.class);
		RatProdutividadeMapper prodMapper = session.getMapper(RatProdutividadeMapper.class);
		mapper.inserir(rat);
		if(rat.getProdutividade() != null){
			for(RatProdutividade p : rat.getProdutividade())
				prodMapper.inserir(p);
		}

		session.commit();
		log.info("Rat nr: " + rat.getId() + " gravado com sucesso");

	}
	
	public void fecharConeccoes(){
		session.close();
		f.sair();
	}
	
	private void processaRatsAbertos(){
		log.info("Iniciando processamento de RAT abertos");
		SqlSession session = f.getFactory().openSession();
		List<Rat> ratAbertos = session.getMapper(RatMapper.class).getRatsAbertos("Aberto");
		for(Rat rat : ratAbertos){
			if(f.isRatFechado(rat.getId())){
				log.info("Rat nr " + rat.getId() + " fechado");
				rat.setEstado("Fechado");
				processaProdutividade(rat);
				gravarRat(rat);
			}
		}
	}
	
	public static void main(String[] args) {
		RatImport rat = null;
		while(true){
			try{
				rat = new RatImport();
				if(args.length == 2){
					SimpleDateFormat fmt = new SimpleDateFormat("dd/MM/yy");
					try {
						rat.importar(fmt.parse(args[0]), fmt.parse(args[1]));
					} catch (ParseException e) {
						log.error(e);
					}
				}else 
					rat.importarDiaAnterior();
				log.info("Terminado importacao de RAT.");
				rat.fecharConeccoes();
				break;
			}catch(SocketTimeoutException e){
				log.error("Não foi possível conectar ao servidor SIDS, tentando novamente em 1 minuto.");
				if(rat != null)	rat.fecharConeccoes();
				rat = null;
				try{ Thread.sleep(10000);}catch(InterruptedException e1){}
			}
		}
	}
}
