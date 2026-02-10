package pacifico.mvm.bookflix.service;

import java.util.List;
import java.util.Optional;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pacifico.mvm.bookflix.dto.AutenticacaoDTO;
import pacifico.mvm.bookflix.exception.DataIntegrityException;
import pacifico.mvm.bookflix.exception.ObjectNotFoundException;
import pacifico.mvm.bookflix.model.Autenticacao;
import pacifico.mvm.bookflix.repository.AutenticacaoRepository;

@Service
public class AutenticacaoService {

	private final AutenticacaoRepository autenticacaoRepository;
	private static final AutenticacaoDTO autenticacaoDTO = new AutenticacaoDTO();
	
	public AutenticacaoService(AutenticacaoRepository autenticacaoRepository) {
		this.autenticacaoRepository = autenticacaoRepository;
	}

	public Autenticacao find(Integer id) {
		Optional<Autenticacao> objeto = autenticacaoRepository.findById(id); 
		return objeto.orElseThrow(() -> new ObjectNotFoundException( 
				 "Autenticação não encontrada! Id: " + id));		
	}
	
	@Transactional
	public Autenticacao insert (Autenticacao objeto) {
		objeto.setId(null);
		return autenticacaoRepository.save(objeto);
		
	}
	
	public Autenticacao update(Autenticacao objetoEditado) {
		Autenticacao objetoAtualizado = find(objetoEditado.getId());
		objetoAtualizado = objetoEditado;
		return autenticacaoRepository.save(objetoAtualizado);
	}
	
	@Transactional
	public void delete(Integer id, Autenticacao objeto) {
		if(!objeto.equals(find(id))) {
			throw new IllegalArgumentException("A autenticação a ser removida é diferente da autenticação cadastrada no banco de dados");
		}
		deleteById(id);
	}
	
	public List<Autenticacao> findAll() {
		return autenticacaoRepository.findAll();
	}
	
	@Transactional
	public void deleteById(Integer id) {
		find(id);
		try {
			autenticacaoRepository.deleteById(id);	
		} catch (DataIntegrityViolationException e) {
			throw new DataIntegrityException("Não é possível remover. Verifique a integridade referencial.");
		}
	}
	
	public void save(Autenticacao autenticacao) {
		autenticacaoRepository.saveAndFlush(autenticacao);
	}
	
	public Autenticacao fromDTO(AutenticacaoDTO objetoDTO) {
		return autenticacaoDTO.fromDTO(objetoDTO);
	}
	
	public Autenticacao fromNewDTO(AutenticacaoDTO objetoNewDTO) {
		return autenticacaoDTO.fromNewDTO(objetoNewDTO);
	}
	
	public Autenticacao autenticacaoWithoutObra(Autenticacao autenticacao) {
		return autenticacaoDTO.autenticacaoWithoutObra(autenticacao);
	}
    
    public void validateAvaliacaoId(Integer paramPathId, Integer obraBodyId) {
    	if(!paramPathId.equals(obraBodyId)) {
    		throw new IllegalArgumentException("O id da URL é diferente do id da avaliação informada no corpo da solicitação");
    	}
    }
	
}
