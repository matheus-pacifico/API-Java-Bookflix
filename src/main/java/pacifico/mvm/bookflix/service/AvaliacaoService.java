package pacifico.mvm.bookflix.service;

import java.util.List;
import java.util.Optional;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pacifico.mvm.bookflix.dto.AvaliacaoDTO;
import pacifico.mvm.bookflix.exception.DataIntegrityException;
import pacifico.mvm.bookflix.exception.ObjectNotFoundException;
import pacifico.mvm.bookflix.model.Avaliacao;
import pacifico.mvm.bookflix.repository.AvaliacaoRepository;

@Service
public class AvaliacaoService {

	private final AvaliacaoRepository avaliacaoRepository;
	private static final AvaliacaoDTO avaliacaoDTO = new AvaliacaoDTO();
	
	public AvaliacaoService(AvaliacaoRepository avaliacaoRepository) {
		this.avaliacaoRepository = avaliacaoRepository;
	}

	public Avaliacao find(Integer id) {
		Optional<Avaliacao> objeto = avaliacaoRepository.findById(id);
		return objeto.orElseThrow(() -> new ObjectNotFoundException( 
				 "Avaliação não encontrada! Id: " + id));		
	}
	
	@Transactional
	public Avaliacao insert (Avaliacao objeto) {
		objeto.setId(null);
		return avaliacaoRepository.save(objeto);
	}
	
	public Avaliacao update(Avaliacao objetoEditado) {
		Avaliacao objetoAtualizado = find(objetoEditado.getId());
		objetoAtualizado = objetoEditado;
		return avaliacaoRepository.save(objetoAtualizado);
	}
	
	@Transactional
	public void delete(Integer id, Avaliacao objeto) {
		if(!objeto.equals(find(id))) {
			throw new IllegalArgumentException("A avaliação a ser removida é diferente da avaliação cadastrada no banco de dados");
		}
		deleteById(id);
	}
	
	public List<Avaliacao> findAll() {
		return avaliacaoDTO.listOfAvaliacoesWithoutUsuariosDataExceptName(avaliacaoRepository.findAll());
	}
	
	@Transactional
	public void deleteById(Integer id) {
		find(id);
		try {
			avaliacaoRepository.deleteById(id);	
		} catch (DataIntegrityViolationException e) {
			throw new DataIntegrityException("Não é possível remover. Verifique a integridade referencial.");
		}
	}
	
	public void save(Avaliacao avaliacao) {
		avaliacaoRepository.saveAndFlush(avaliacao);
	}
	
	public Avaliacao fromDTO(AvaliacaoDTO objetoDTO) {
		return avaliacaoDTO.fromDTO(objetoDTO);
	}
	
	public Avaliacao fromNewDTO(AvaliacaoDTO objetoNewDTO) {
		return avaliacaoDTO.fromNewDTO(objetoNewDTO);
	}
	
	public Avaliacao avaliacaoWithoutUsuariosDataExceptName(Avaliacao avaliacao) {
		return avaliacaoDTO.avaliacaoWithoutUsuariosDataExceptName(avaliacao);
	}
    
    public void validateAvaliacaoId(Integer paramPathId, Integer avaliacaoBodyId) {
    	if(!paramPathId.equals(avaliacaoBodyId)) {
    		throw new IllegalArgumentException("O id da URL é diferente do id da avaliação informada no corpo da solicitação");
    	}
    }

}
