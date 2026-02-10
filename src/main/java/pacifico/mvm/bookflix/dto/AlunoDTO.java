package pacifico.mvm.bookflix.dto;

import java.io.Serializable;

import jakarta.validation.constraints.NotEmpty;
import pacifico.mvm.bookflix.model.Aluno;
import pacifico.mvm.bookflix.model.Usuario;

public class AlunoDTO implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private Integer id;
	@NotEmpty
	private String ra;
	private int turma;
	private Usuario usuario;
				
	public AlunoDTO() {
	}
	
	public AlunoDTO(Aluno objeto) {
		this.id = objeto.getId();
		this.ra = objeto.getRa();
		this.turma = objeto.getTurma();
		this.usuario = objeto.getUsuario();
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getRa() {
		return ra;
	}

	public void setRa(String ra) {
		this.ra = ra;
	}

	public int getTurma() {
		return turma;
	}

	public void setTurma(int turma) {
		this.turma = turma;
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}
	
	public Aluno fromDTO(AlunoDTO objetoDTO) {
		Aluno alunoAuxiliar = new Aluno();
		alunoAuxiliar.setId(objetoDTO.getId());
		alunoAuxiliar.setRa(objetoDTO.getRa());
		alunoAuxiliar.setTurma(objetoDTO.getTurma());
		alunoAuxiliar.setUsuario(objetoDTO.getUsuario());
		return alunoAuxiliar;
	}
	
	public Aluno fromNewDTO(AlunoDTO objetoNewDTO) {
		return new Aluno(null, objetoNewDTO.getRa(), objetoNewDTO.getTurma(), objetoNewDTO.getUsuario());
	} 
	
}
