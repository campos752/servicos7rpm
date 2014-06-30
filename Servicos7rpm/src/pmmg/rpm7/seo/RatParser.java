package pmmg.rpm7.seo;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.apache.log4j.Logger;

import pmmg.rpm7.seo.modelos.Rat;
import pmmg.rpm7.seo.modelos.RatProdutividade;

public class RatParser {
		private static Logger log = Logger.getLogger(RatParser.class);
	
	public void processaLinha(String linha, Rat rat){
		SimpleDateFormat fmtRat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		Scanner scnCampo = new Scanner(linha);
		scnCampo.useDelimiter(";");
		while(scnCampo.hasNext()){
			String nrAtividade = scnCampo.next();
			if(!nrAtividade.startsWith("20"))
				break;
			rat.setId(nrAtividade);
			rat.setEstado(scnCampo.next());
			String natureza = scnCampo.next();
			rat.setCodNatureza(natureza.substring(1, 6));
			rat.setDescNatureza(natureza.substring(9));
			try{
				rat.setDataInicio(fmtRat.parse(scnCampo.next()));
			}catch(ParseException e){
				log.error(linha, e);
			}
			String digitador = scnCampo.next();
			rat.setNomeDigitador(digitador.split("/")[0]);
			try{
				Integer m = Integer.parseInt(digitador.split("/")[1]);
				rat.setMatDigitador(m);
			}catch(NumberFormatException e){
				log.warn(linha, e);
			}
			String endereco = scnCampo.next();
			log.info(endereco);
			Scanner scnEndereco = new Scanner(endereco);
			StringBuilder strNomeLogradouro = new StringBuilder();
			while(scnEndereco.hasNext()){
				String str = scnEndereco.next();
				// Obtem o tipo de logradouro
				if(rat.getTipoLogradouro() == null){
					rat.setTipoLogradouro(str);
					continue;
				}
				
				//Obtem o nome do logradouro
				if(!"No.".equals(str) && rat.getEndereco() == null){
					strNomeLogradouro.append(str).append(" ");
					continue;
				}else if (rat.getEndereco() == null)
					rat.setEndereco(strNomeLogradouro.toString().trim());
				//Obtem o numero do endereco
				if("No.".equals(str)){
					str = scnEndereco.next();
					if(str.indexOf(',')>=0)
						str = str.substring(0, str.length() - 1);
					if(str.matches("\\d+"))
						rat.setNrEndereco(Integer.parseInt(str));
					else
						rat.setNrEndereco(0);
				}
				StringBuilder builderComplemento = new StringBuilder();
				while(scnEndereco.hasNext()){
					String str1 = scnEndereco.next();
					builderComplemento.append(str1).append(" ");
					if(str1.indexOf(',')>=0){
						String complemento = builderComplemento.toString();
						rat.setComplemento(complemento.substring(0, complemento.length() - 2));
					}else if ("-".equals(str1)){
						String bairro = builderComplemento.toString().trim();
						if(bairro.length() > 2)
							rat.setBairro(bairro.substring(0, bairro.length() - 2));
						builderComplemento = new StringBuilder();
					}else if("/".equals(str1)){
						String municipio = builderComplemento.toString();
						rat.setMunicipio(municipio.substring(0, municipio.length() - 2));
						break;
					}
				}
			}
			scnEndereco.close();
		}
		scnCampo.close();
	}
	
	public void setProdutividade(List<String> relProd, Rat rat, Map<String, String> unidades){
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
}
