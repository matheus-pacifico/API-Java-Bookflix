package pacifico.mvm.bookflix.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import pacifico.mvm.bookflix.model.Avaliacao;
import pacifico.mvm.bookflix.model.Obra;
import pacifico.mvm.bookflix.model.Usuario;

public class AvaliacaoDTO implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private Integer id;	
	private String comentario;
	private int nota;
	private Usuario usuario;
	private Obra obra;
			
	public AvaliacaoDTO() {

	}
	
	public AvaliacaoDTO(Avaliacao avaliacao) {
		this.id = avaliacao.getId();
		this.comentario = avaliacao.getComentario();
		this.nota = avaliacao.getNota();
		this.usuario = avaliacao.getUsuario();
		this.obra = avaliacao.getObra();
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getComentario() {
		return comentario;
	}

	public void setComentario(String comentario) {
		this.comentario = comentario;
	}

	public int getNota() {
		return nota;
	}

	public void setNota(int nota) {
		this.nota = nota;
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	public Obra getObra() {
		return obra;
	}

	public void setObra(Obra obra) {
		this.obra = obra;
	}	
	
	public Avaliacao fromDTO(AvaliacaoDTO objetoDTO) {
		return new Avaliacao(objetoDTO.getId(), objetoDTO.getComentario(), objetoDTO.getNota(),
				objetoDTO.getUsuario(), objetoDTO.getObra());
	}
	
	public Avaliacao fromNewDTO(AvaliacaoDTO objetoNewDTO) {
		return new Avaliacao(null, objetoNewDTO.getComentario(), objetoNewDTO.getNota(), 
				objetoNewDTO.getUsuario(), objetoNewDTO.getObra());
	}
	
	public Avaliacao avaliacaoWithoutUsuariosDataExceptName(Avaliacao avaliacao) {
		avaliacao.getUsuario().setAutenticacao(null);
		avaliacao.getUsuario().setProfessor(null);
		avaliacao.getUsuario().setAluno(null);
		avaliacao.getUsuario().setAvaliacoes(null);
		avaliacao.getUsuario().setId(null);
		return avaliacao;
	}
	
	public List<Avaliacao> listOfAvaliacoesWithoutUsuariosDataExceptName(List<Avaliacao> avaliacoes) {
		List<Avaliacao> avaliacoesSemDadosExcetoNome = new ArrayList<>();
		avaliacoes.forEach(a -> avaliacaoWithoutUsuariosDataExceptName(a));
		
		avaliacoesSemDadosExcetoNome.addAll(avaliacoes);
		
		return avaliacoesSemDadosExcetoNome;
	}
	
}
