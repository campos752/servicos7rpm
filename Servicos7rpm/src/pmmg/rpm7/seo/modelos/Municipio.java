package pmmg.rpm7.seo.modelos;

import java.util.List;

import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

public class Municipio {
	private Integer id;
	private String nome;
	
	public Municipio(){
		
	}
	
	public Municipio(Integer id, String nome){
		this.id = id;
		this.nome = nome;
	}
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getNome() {
		return nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}
	
	public static interface MunicipioMapper {
		@Select("select * from municipios_7rpm")
		@Results(value = {
				@Result(property = "id", column = "cod_municipio_fato"),
				@Result(property = "nome", column = "nome"),
		})
		List<Municipio> getTodos();
	}
	
	
}