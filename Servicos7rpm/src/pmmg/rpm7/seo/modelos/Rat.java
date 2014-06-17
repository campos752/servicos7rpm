package pmmg.rpm7.seo.modelos;

import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name="rat")
public class Rat {
	@Id
	@Column(name = "nr_atividade")
	private String id;
	private String estado;
	@Column(name = "natureza")
	private String codNatureza;
	@Column(name = "descricao")
	private String descNatureza;
	@Column(name = "data_inicio")
	@Temporal(TemporalType.TIMESTAMP)
	private Date dataInicio;
	@Column(name = "matricula_digitador")
	private Integer matDigitador;
	@Column(name = "nome_digitador")
	private String nomeDigitador;
	@Column(name = "tipo_logradouro")
	private String tipoLogradouro;
	private String endereco;
	@Column(name = "num_endereco")
	private Integer nrEndereco;
	@Column(name = "endereco_compl")
	private String complemento;
	private String bairro;
	private String municipio;
	@Column(name = "cod_unidade_servico")
	private String codUnidade;
	@Column(name = "nome_unidade_servico")
	private String nomeUnidade;
 
	@OneToMany(cascade={ CascadeType.ALL}, fetch=FetchType.EAGER, orphanRemoval = true)
	@JoinColumn(name = "nr_atividade")
    private List<RatProdutividade> produtividade;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getEstado() {
		return estado;
	}
	public void setEstado(String estado) {
		this.estado = estado;
	}
	public String getCodNatureza() {
		return codNatureza;
	}
	public void setCodNatureza(String codNatureza) {
		this.codNatureza = codNatureza;
	}
	public String getDescNatureza() {
		return descNatureza;
	}
	public void setDescNatureza(String descNatureza) {
		this.descNatureza = descNatureza;
	}
	public Date getDataInicio() {
		return dataInicio;
	}
	public void setDataInicio(Date dataInicio) {
		this.dataInicio = dataInicio;
	}
	public Integer getMatDigitador() {
		return matDigitador;
	}
	public void setMatDigitador(Integer matDigitador) {
		this.matDigitador = matDigitador;
	}
	public String getNomeDigitador() {
		return nomeDigitador;
	}
	public void setNomeDigitador(String nomeDigitador) {
		this.nomeDigitador = nomeDigitador;
	}
	public String getEndereco() {
		return endereco;
	}
	public void setEndereco(String endereco) {
		this.endereco = endereco;
	}
	public String getMunicipio() {
		return municipio;
	}
	public void setMunicipio(String municipio) {
		this.municipio = municipio;
	}
	public String getCodUnidade() {
		return codUnidade;
	}
	public void setCodUnidade(String codUnidade) {
		this.codUnidade = codUnidade;
	}
	public String getNomeUnidade() {
		return nomeUnidade;
	}
	public void setNomeUnidade(String nomeUnidade) {
		this.nomeUnidade = nomeUnidade;
	}
	public String getTipoLogradouro() {
		return tipoLogradouro;
	}
	public void setTipoLogradouro(String tipoLogradouro) {
		this.tipoLogradouro = tipoLogradouro;
	}
	public Integer getNrEndereco() {
		return nrEndereco;
	}
	public void setNrEndereco(Integer nrEndereco) {
		this.nrEndereco = nrEndereco;
	}
	public String getBairro() {
		return bairro;
	}
	public void setBairro(String bairro) {
		this.bairro = bairro;
	}
	public List<RatProdutividade> getProdutividade() {
		return produtividade;
	}
	public void setProdutividade(List<RatProdutividade> produtividade) {
		this.produtividade = produtividade;
	}
	public String getComplemento() {
		return complemento;
	}
	public void setComplemento(String complemento) {
		this.complemento = complemento;
	}
	
}
