package pmmg.rpm7.seo.modelos;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="municipios_7rpm")

public class Municipio {
	@Id
	@Column(name = "cod_municipio_fato")
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
	
	
	
}
