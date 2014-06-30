package pmmg.rpm7.seo.modelos;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

public class RatProdutividade {
	private Long id;
	private String nrAtividade;
	private String descricao;
	private Integer qtd;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getNrAtividade() {
		return nrAtividade;
	}
	public void setNrAtividade(String nrAtividade) {
		this.nrAtividade = nrAtividade;
	}
	public String getDescricao() {
		return descricao;
	}
	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	public Integer getQtd() {
		return qtd;
	}
	public void setQtd(Integer qtd) {
		this.qtd = qtd;
	}
	
	public static interface RatProdutividadeMapper{
		@Select("select id, descricao, nr_atividade, qtd from rat_prod where nr_atividade = #{nrAtividade}")
		@Results(value = {
			@Result(property = "id", column = "id"),
			@Result(property = "nrAtividade", column = "nr_atividade"),
			@Result(property = "descricao", column = "descricao"),
			@Result(property = "qtd", column = "qtd")
		})
		public List<RatProdutividade> getProdutividade(Rat rat);
		
		@Insert("insert into rat_prod (nr_atividade, descricao, qtd) values (#{nrAtividade}, #{descricao}, #{qtd}) "
				+ "on duplicate key update nr_atividade = #{nrAtividade}, descricao = #{descricao}, qtd = #{qtd}")
		@Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
		
		void inserir(RatProdutividade prod);
	}
	
}
