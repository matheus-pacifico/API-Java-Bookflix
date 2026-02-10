package pacifico.mvm.bookflix.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(indexes = { @Index(name = "idx_ifsn", columnList = "ifsn") })
public class Obra implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@Column(nullable = false, unique = true)
	private String ifsn;
	private String titulo;
	private String area;
	@Column(length = 1600)
	private String descricao;
	private int ano;

	@JsonIgnore
	@ManyToOne
	@JoinColumn(name="professor_id")
	private Professor professor;
	
	@JsonIgnoreProperties(value = {"obra"})
	@OneToOne(cascade = {CascadeType.PERSIST, CascadeType.REFRESH, 
			CascadeType.MERGE, CascadeType.DETACH})
	@JoinColumn(name = "arquivo_id", unique = true, nullable = false)
	private Arquivo arquivo;
	
	@OneToMany(mappedBy = "obra", cascade = CascadeType.REMOVE)
	private List<Avaliacao> avaliacoes = new ArrayList<>();
	
	@OneToMany(mappedBy = "obra", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Autor> autores = new ArrayList<>();
	
	public Obra() {

	}

	public Obra(Integer id, String ifsn, String titulo, String area, String descricao, int ano,
			Professor professor, Arquivo arquivo) {
		this.id = id;
		this.ifsn = ifsn;
		this.titulo = titulo;
		this.area = area;
		this.descricao = descricao;
		this.ano = ano;
		this.professor = professor;
		this.arquivo = arquivo;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getIfsn() {
		return ifsn;
	}

	public void setIfsn(String ifsn) {
		this.ifsn = ifsn;
	}

	public String getTitulo() {
		return titulo;
	}

	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}
	
	public String getArea() {
		return area;
	}

	public void setArea(String area) {
		this.area = area;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public int getAno() {
		return ano;
	}

	public void setAno(int ano) {
		this.ano = ano;
	}

	public Professor getProfessor() {
		return professor;
	}

	public void setProfessor(Professor professor) {
		this.professor = professor;
	}

	public Arquivo getArquivo() {
		return arquivo;
	}

	public void setArquivo(Arquivo arquivo) {
		this.arquivo = arquivo;
	}

	public List<Avaliacao> getAvaliacoes() {
		return avaliacoes;
	}

	public void setAvaliacoes(List<Avaliacao> avaliacoes) {
		this.avaliacoes = avaliacoes;
	}

	public List<Autor> getAutores() {
		return autores;
	}

	public void setAutores(List<Autor> autores) {
		this.autores = autores;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Obra other = (Obra) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}	
   
}
