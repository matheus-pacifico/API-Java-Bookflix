package pacifico.mvm.bookflix.dto;

import java.io.Serializable;
import java.util.List;

import jakarta.validation.constraints.NotEmpty;
import pacifico.mvm.bookflix.model.Obra;
import pacifico.mvm.bookflix.model.Professor;
import pacifico.mvm.bookflix.model.Usuario;

public class ProfessorDTO implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private Integer id;
	@NotEmpty
	private String siape;
	private Usuario usuario;
	private List<Obra> obras;
				
	public ProfessorDTO() {
	}
	
	public ProfessorDTO(Professor objeto) {
		this.id = objeto.getId();
		this.siape = objeto.getSiape();
		this.usuario = objeto.getUsuario();
		this.obras = objeto.getObras();
	}    

	public Integer getId() {
		return id;
	}
		public void setId(Integer id) {
		this.id = id;
	}

	public String getSiape() {
		return siape;
	}

	public void setSiape(String siape) {
		this.siape = siape;
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	public List<Obra> getObras() {
		return obras;
	}

	public void setObras(List<Obra> obras) {
		this.obras = obras;
	}
	
	public Professor fromDTO(ProfessorDTO objetoDTO) {
		Professor professorAuxiliar = new Professor();
		professorAuxiliar.setId(objetoDTO.getId());
		professorAuxiliar.setSiape(objetoDTO.getSiape());
		professorAuxiliar.setUsuario(objetoDTO.getUsuario());
		professorAuxiliar.setObras(objetoDTO.getObras());
		return professorAuxiliar;
	}
	
	public Professor fromNewDTO(ProfessorDTO objetoNewDTO) {
		return new Professor(null, objetoNewDTO.getSiape(), objetoNewDTO.getUsuario());
	}
	
}
