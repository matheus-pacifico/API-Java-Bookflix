package pacifico.mvm.bookflix.service;

import java.util.List;
import java.util.Optional;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pacifico.mvm.bookflix.dto.ProfessorDTO;
import pacifico.mvm.bookflix.exception.DataIntegrityException;
import pacifico.mvm.bookflix.exception.ObjectNotFoundException;
import pacifico.mvm.bookflix.model.Obra;
import pacifico.mvm.bookflix.model.Professor;
import pacifico.mvm.bookflix.repository.ProfessorRepository;

@Service
public class ProfessorService {

	private final ProfessorRepository professorRepository;
	private static final ProfessorDTO professorDTO = new ProfessorDTO();
	
	public ProfessorService(ProfessorRepository professorRepository) {
		this.professorRepository = professorRepository;
	}

	public Professor find(Integer id) {
		Optional<Professor> objeto = professorRepository.findById(id); 
		return objeto.orElseThrow(() -> new ObjectNotFoundException( 
				 "Professor não encontrado! Id: " + id));		
	}
	
	@Transactional
	public Professor insert (Professor objeto) {
		objeto.setId(null);
		return professorRepository.save(objeto);
	}

	public Professor update(Professor objetoEditado) {
		Professor objetoAtualizado = find(objetoEditado.getId());
		objetoAtualizado = objetoEditado;
		return professorRepository.save(objetoAtualizado);
	}
	
	@Transactional
	public void delete(Integer id, Professor objeto) {
		if(!objeto.equals(find(id))) {
			throw new IllegalArgumentException("O professor a ser removido é diferente da professor cadastrado no banco de dados");
		}
		deleteById(id);
	}
	
	public List<Professor> findAll() {
		return professorRepository.findAll();
	}
	
	@Transactional
	public void deleteById(Integer id) {
		find(id);
		try {
			professorRepository.deleteById(id);	
		}
		catch (DataIntegrityViolationException e) {
			throw new DataIntegrityException("Não é possível remover. Verifique a integridade referencial.");
		}
	}
	
	public void save(Professor professor) {
		professorRepository.saveAndFlush(professor);
	}
	
	public Optional<Professor> findById(Integer id) {
		return professorRepository.findById(id);
	}
	
	public Professor fromDTO(ProfessorDTO objetoDTO) {
		return professorDTO.fromDTO(objetoDTO);
	}
	
	public Professor fromNewDTO(ProfessorDTO objetoNewDTO) {
		return professorDTO.fromNewDTO(objetoNewDTO);
	}
	
	public List<Obra> listObrasPostedByProfessor(Integer id){
		Professor professor = find(id);
		return professor.getObras();
	}
    
    public void validateProfessorId(Integer paramPathId, Integer professorBodyId) {
    	if(!paramPathId.equals(professorBodyId)) {
    		throw new IllegalArgumentException("O id da URL é diferente do id do professor informado no corpo da solicitação");
    	}
    }
	
}
