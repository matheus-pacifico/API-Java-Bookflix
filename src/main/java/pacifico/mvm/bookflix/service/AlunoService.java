package pacifico.mvm.bookflix.service;

import java.util.List;
import java.util.Optional;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pacifico.mvm.bookflix.dto.AlunoDTO;
import pacifico.mvm.bookflix.exception.DataIntegrityException;
import pacifico.mvm.bookflix.exception.ObjectNotFoundException;
import pacifico.mvm.bookflix.model.Aluno;
import pacifico.mvm.bookflix.repository.AlunoRepository;

@Service
public class AlunoService {

	private final AlunoRepository alunoRepository;
	private static final AlunoDTO alunoDTO = new AlunoDTO();
	
	public AlunoService(AlunoRepository alunoRepository) {
		this.alunoRepository = alunoRepository;
	}

	public Aluno find(Integer id) {
		Optional<Aluno> objeto = alunoRepository.findById(id); 
		return objeto.orElseThrow(() -> new ObjectNotFoundException( 
				 "Aluno não encontrado! Id: " + id));		
	}
	
	@Transactional
	public Aluno insert (Aluno objeto) {
		objeto.setId(null);
		return alunoRepository.save(objeto);
	}

	public Aluno update(Aluno objetoEditado) {
		Aluno objetoAtualizado = find(objetoEditado.getId());
		objetoAtualizado = objetoEditado;
		return alunoRepository.save(objetoAtualizado);
	}
	
	@Transactional
	public void delete(Integer id, Aluno objeto) {
		if(!objeto.equals(find(id))) {
			throw new IllegalArgumentException("O aluno a ser removido é diferente do aluno cadastrado no banco de dados");
		}
		deleteById(id);
	}
		
	public List<Aluno> findAll() {
		return alunoRepository.findAll();
	}
	
	@Transactional
	public void deleteById(Integer id) {
		find(id);
		try {
			alunoRepository.deleteById(id);	
		} catch (DataIntegrityViolationException e) {
			throw new DataIntegrityException("Não é possível remover. Verifique a integridade referencial.");
		}
	}
	
	public void save(Aluno aluno) {
		alunoRepository.saveAndFlush(aluno);
	}
	
	public Aluno fromDTO(AlunoDTO objetoDTO) {
		return alunoDTO.fromDTO(objetoDTO);
	}
	
	public Aluno fromNewDTO(AlunoDTO objetoNewDTO) {
		return alunoDTO.fromNewDTO(objetoNewDTO);
	}
	
	public List<Aluno> findAlunosByTurma(int turma) {		
		return alunoRepository.findAlunosByTurma(turma);
	}
	
	public Aluno findByRa(String ra) {
		Optional<Aluno> objeto = alunoRepository.findByRa(ra); 
		return objeto.orElseThrow(() -> new ObjectNotFoundException( 
				 "Aluno não encontrado! RA: " + ra));	
	}
    
    public void validateAlunoId(Integer paramPathId, Integer alunoBodyId) {
    	if(!paramPathId.equals(alunoBodyId)) {
    		throw new IllegalArgumentException("O id da URL é diferente do id do aluno informado no corpo da solicitação");
    	}
    }
	
}
