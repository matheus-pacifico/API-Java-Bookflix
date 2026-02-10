package pacifico.mvm.bookflix.service;

import java.util.List;
import java.util.Optional;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pacifico.mvm.bookflix.dto.AutorDTO;
import pacifico.mvm.bookflix.exception.DataIntegrityException;
import pacifico.mvm.bookflix.exception.ObjectNotFoundException;
import pacifico.mvm.bookflix.model.Autor;
import pacifico.mvm.bookflix.repository.AutorRepository;

@Service
public class AutorService {
	
	private final AutorRepository autorRepository;
	private static final AutorDTO autorDTO = new AutorDTO();
	
	public AutorService(AutorRepository autorRepository) {
		this.autorRepository = autorRepository;
	}

	public Autor find(Integer id) {
		Optional<Autor> objeto = autorRepository.findById(id); 
		return objeto.orElseThrow(() -> new ObjectNotFoundException( 
				 "Autor não encontrado! Id: " + id));		
	}
	
	@Transactional
	public Autor insert (Autor objeto) {
		objeto.setId(null);
		return autorRepository.save(objeto);
	}
	
	public Autor update(Autor objetoEditado) {
		Autor objetoAtualizado = find(objetoEditado.getId());
		objetoAtualizado = objetoEditado;
		return autorRepository.save(objetoAtualizado);
	}
	
	@Transactional
	public void delete(Integer id, Autor objeto) {
		if(!objeto.equals(find(id))) {
			throw new IllegalArgumentException("O autor a ser removido é diferente do autor cadastrado no banco de dados");
		}
		deleteById(id);		
	}
	
	public List<Autor> findAll() {
		return autorRepository.findAll();
	}
	
	public void deleteById(Integer id) {
		find(id);
		try {
			autorRepository.deleteById(id);	
		} catch (DataIntegrityViolationException e) {
			throw new DataIntegrityException("Não é possível remover. Verifique a integridade referencial.");
		}		
	}
	
	public void save(Autor avaliacao) {
		autorRepository.saveAndFlush(avaliacao);
	}
	
	public Autor fromDTO(AutorDTO objetoDTO) {
		return autorDTO.fromDTO(objetoDTO);
	}
	
	public Autor fromNewDTO(AutorDTO objetoNewDTO) {
		return autorDTO.fromNewDTO(objetoNewDTO);
	}
	
	public Autor autorWithoutAvaliacoesDaObra(Autor autor) {
		return autorDTO.autorWithoutAvaliacoesDaObra(autor);
	}
    
    public void validateAutorId(Integer paramPathId, Integer autorBodyId) {
    	if(!paramPathId.equals(autorBodyId)) {
    		throw new IllegalArgumentException("O id da URL é diferente do id do autor informado no corpo da solicitação");
    	}
    }

}
