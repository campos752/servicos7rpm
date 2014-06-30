package pmmg.rpm7.seo.modelos;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

public class Rat {
	private String id;
	private String estado;
	private String codNatureza;
	private String descNatureza;
	private Date dataInicio;
	private Integer matDigitador;
	private String nomeDigitador;
	private String tipoLogradouro;
	private String endereco;
	private Integer nrEndereco;
	private String complemento;
	private String bairro;
	private String municipio;
	private String codUnidade;
	private String nomeUnidade;
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
	
	public static interface RatMapper{
	
		@Insert("insert into rat (nr_atividade, estado, bairro, natureza, cod_unidade_servico, endereco_compl, data_inicio, "
				+ "descricao, endereco, matricula_digitador, municipio, nome_digitador, nome_unidade_servico, num_endereco, "
				+ "tipo_logradouro) values (#{id}, #{estado}, #{bairro}, #{codNatureza}, #{codUnidade}, #{complemento}, #{dataInicio}, "
				+ "#{descNatureza}, #{endereco}, #{matDigitador}, #{municipio}, #{nomeDigitador}, #{nomeUnidade}, "
				+ "#{nrEndereco}, #{tipoLogradouro}) on duplicate key update estado = #{estado}, bairro = #{bairro}, natureza = #{codNatureza}, "
				+ "cod_unidade_servico = #{codUnidade}, endereco_compl = #{complemento}, data_inicio = #{dataInicio}, "
				+ "descricao = #{descNatureza}, endereco = #{endereco}, matricula_digitador = #{matDigitador}, municipio = #{municipio}, "
				+ "nome_digitador = #{nomeDigitador}, nome_unidade_servico = #{nomeUnidade}, num_endereco = #{nrEndereco}, "
				+ "tipo_logradouro = #{tipoLogradouro}")
		void inserir(Rat rat);
		
		@Select("select nr_atividade, estado, bairro, natureza, cod_unidade_servico, endereco_compl, data_inicio, "
				+ "descricao, endereco, matricula_digitador, municipio, nome_digitador, nome_unidade_servico, num_endereco, "
				+ "tipo_logradouro from rat where estado = #{estado}")
		@Results(value = {
				@Result(property = "bairro", column = "bairro"),
				@Result(property = "codNatureza", column = "natureza"),
				@Result(property = "codUnidade", column = "cod_unidade_servico"),
				@Result(property = "complemento", column = "endereco_compl"), 
				@Result(property = "dataInicio", column = "data_inicio"),
				@Result(property = "descNatureza", column = "descricao"),
				@Result(property = "endereco", column = "endereco"),
				@Result(property = "estado", column = "estado"),
				@Result(property = "id", column = "nr_atividade"),
				@Result(property = "matDigitador", column = "matricula_digitador"),
				@Result(property = "municipio", column = "municipio"),
				@Result(property = "nomeDigitador", column = "none_digitador"),
				@Result(property = "nomeUnidade", column = "nome_unidade_servico"),
				@Result(property = "nrEndereco", column = "num_endereco"),
				@Result(property = "tipoLogradouro", column = "tipo_logradouro")
		})
		List<Rat> getRatsAbertos(String estado);
		
		@Select("select nr_atividade, estado, bairro, natureza, cod_unidade_servico, endereco_compl, data_inicio, "
				+ "descricao, endereco, matricula_digitador, municipio, nome_digitador, nome_unidade_servico, num_endereco, "
				+ "tipo_logradouro from rat where nr_atividade = #{nrAtividade}")
		@Results(value = {
				@Result(property = "bairro", column = "bairro"),
				@Result(property = "codNatureza", column = "natureza"),
				@Result(property = "codUnidade", column = "cod_unidade_servico"),
				@Result(property = "complemento", column = "endereco_compl"), 
				@Result(property = "dataInicio", column = "data_inicio"),
				@Result(property = "descNatureza", column = "descricao"),
				@Result(property = "endereco", column = "endereco"),
				@Result(property = "estado", column = "estado"),
				@Result(property = "id", column = "nr_atividade"),
				@Result(property = "matDigitador", column = "matricula_digitador"),
				@Result(property = "municipio", column = "municipio"),
				@Result(property = "nomeDigitador", column = "none_digitador"),
				@Result(property = "nomeUnidade", column = "nome_unidade_servico"),
				@Result(property = "nrEndereco", column = "num_endereco"),
				@Result(property = "tipoLogradouro", column = "tipo_logradouro")
		})
		
		Rat getRat(String nrAtividade);
	}
	
}
