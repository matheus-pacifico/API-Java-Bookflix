package pacifico.mvm.bookflix.dto;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.validation.constraints.NotNull;
import pacifico.mvm.bookflix.model.Arquivo;
import pacifico.mvm.bookflix.model.Autor;
import pacifico.mvm.bookflix.model.Avaliacao;
import pacifico.mvm.bookflix.model.Obra;
import pacifico.mvm.bookflix.model.Professor;
import pacifico.mvm.bookflix.model.Usuario;

public class ObraDTO implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private Integer id;
	@NotNull(message="Preenchimento obrigatório")
	private String ifsn;
	private String titulo;
	private String area;
	private String descricao;
	private int ano;
	private Professor professor;
	private Arquivo arquivo;
	private List<Avaliacao> avaliacoes;
	private List<Autor> autores;
		
	public ObraDTO() {
	}

	public ObraDTO(Obra objeto) {
		this.id = objeto.getId();
		this.ifsn = objeto.getIfsn();
		this.titulo = objeto.getTitulo();
		this.area = objeto.getArea();
		this.descricao = objeto.getDescricao();
		this.ano = objeto.getAno();
		this.professor = objeto.getProfessor();
		this.arquivo = objeto.getArquivo();
		this.avaliacoes = objeto.getAvaliacoes();
		this.autores = objeto.getAutores();
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

	public Arquivo getArquivo() {
		return arquivo;
	}

	public void setArquivo(Arquivo arquivo) {
		this.arquivo = arquivo;
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

	public Obra fromDTO(ObraDTO objetoDTO) {
		Obra obraAuxiliar = new Obra();
		obraAuxiliar.setId(objetoDTO.getId());
		obraAuxiliar.setIfsn(objetoDTO.getIfsn());
		obraAuxiliar.setTitulo(objetoDTO.getTitulo());
		obraAuxiliar.setArea(objetoDTO.getArea());
		obraAuxiliar.setDescricao(objetoDTO.getDescricao());
		obraAuxiliar.setAno(objetoDTO.getAno());
		obraAuxiliar.setProfessor(objetoDTO.getProfessor());
		obraAuxiliar.setArquivo(objetoDTO.getArquivo());
		obraAuxiliar.setAutores(objetoDTO.getAutores());
		obraAuxiliar.setAvaliacoes(objetoDTO.getAvaliacoes());
		return obraAuxiliar;
	}
	
	public Obra fromNewDTO(ObraDTO objetoNewDTO) {
		Obra obra = new Obra(null, objetoNewDTO.getIfsn(), objetoNewDTO.getTitulo(), 
			objetoNewDTO.getArea(), objetoNewDTO.getDescricao(), objetoNewDTO.getAno(), objetoNewDTO.getProfessor(),
			objetoNewDTO.getArquivo());
		obra.setAutores(objetoNewDTO.getAutores());
		return obra;
	}

	public Obra obraWithoutSomeAttributes(Obra objeto) {
		if (objeto.getProfessor() != null) {
			objeto.setProfessor(professorOnlyWithNameAndSiape(objeto.getProfessor()));
		}
		if (objeto.getAutores() != null) {
			objeto.setAutores(listOfAutoresOnlyWithName((objeto.getAutores())));
		}
		if (objeto.getAvaliacoes() != null) {	
			objeto.setAvaliacoes(listOfAvaliacoesOnlyWithUsersNameWithoutObra(objeto.getAvaliacoes()));
		}
		return objeto;
	}
	
	private Professor professorOnlyWithNameAndSiape(Professor professor) {
    	professor.setId(null);
    	professor.setUsuario(usuarioOnlyWithName(professor.getUsuario()));
    	professor.setObras(null);
    	return professor;
    }
    
    private Usuario usuarioOnlyWithName(Usuario usuario) {
    	return new Usuario(null, usuario.getNome(), null,null,null);
    }
	
	private Avaliacao avaliacaoWithoutUsersDataExceptName(Avaliacao avaliacao) {
		avaliacao.setUsuario(usuarioOnlyWithName(avaliacao.getUsuario()));
		avaliacao.setObra(null);
		return avaliacao;
	}
	
	private List<Avaliacao> listOfAvaliacoesOnlyWithUsersNameWithoutObra(List<Avaliacao> avaliacoes) {
		return avaliacoes.stream().map(a -> avaliacaoWithoutUsersDataExceptName(a))
				.collect(Collectors.toList());
	}
	
	private Autor autorOnlyWithName(Autor autor) {
		return new Autor(null, autor.getNome(), null);
	}

	private List<Autor> listOfAutoresOnlyWithName(List<Autor> autor) {
		return autor.stream().map(a -> autorOnlyWithName(a))
				.collect(Collectors.toList());
	}		
	
}
