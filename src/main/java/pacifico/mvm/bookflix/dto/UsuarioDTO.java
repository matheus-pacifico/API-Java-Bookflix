package pacifico.mvm.bookflix.dto;

import java.io.Serializable;
import java.util.List;

import pacifico.mvm.bookflix.model.Aluno;
import pacifico.mvm.bookflix.model.Avaliacao;
import pacifico.mvm.bookflix.model.Professor;
import pacifico.mvm.bookflix.model.Usuario;

public class UsuarioDTO implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private Integer id;
	private String nome;
	private Professor professor;
	private Aluno aluno;
	private List<Avaliacao> avaliacoes;
	
	public UsuarioDTO() {
	}
	
	public UsuarioDTO(Usuario objeto) {
		this.id = objeto.getId();
		this.nome = objeto.getNome();
		this.professor = objeto.getProfessor();
		this.aluno = objeto.getAluno();
		this.avaliacoes = objeto.getAvaliacoes();
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

	public Professor getProfessor() {
		return professor;
	}

	public void setProfessor(Professor professor) {
		this.professor = professor;
	}

	public Aluno getAluno() {
		return aluno;
	}

	public void setAluno(Aluno aluno) {
		this.aluno = aluno;
	}

	public List<Avaliacao> getAvaliacoes() {
		return avaliacoes;
	}

	public void setAvaliacoes(List<Avaliacao> avaliacoes) {
		this.avaliacoes = avaliacoes;
	}
	
	public Usuario fromDTO(UsuarioDTO objetoDTO) {
		Usuario usuarioAuxiliar = new Usuario();
		usuarioAuxiliar.setId(objetoDTO.getId());
		usuarioAuxiliar.setNome(objetoDTO.getNome());
		usuarioAuxiliar.setProfessor(objetoDTO.getProfessor());
		usuarioAuxiliar.setAluno(objetoDTO.getAluno());
		usuarioAuxiliar.setAvaliacoes(objetoDTO.getAvaliacoes());
		return usuarioAuxiliar;
	}
	
	public Usuario fromNewDTO(UsuarioDTO objetoNewDTO) {
		return new Usuario(null , objetoNewDTO.getNome(), null, null, null);
	}
	
	public Usuario usuarioWithoutAvaliacaoDasObras(Usuario usuario) {
		if(usuario.getProfessor() != null) usuario.getProfessor().getObras().forEach(o -> o.setAvaliacoes(null));
		
		return usuario;
	}

}
