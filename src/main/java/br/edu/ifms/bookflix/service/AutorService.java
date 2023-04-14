package br.edu.ifms.bookflix.service;

import br.edu.ifms.bookflix.model.Autor;

import br.edu.ifms.bookflix.repository.AutorRepository;

import br.edu.ifms.bookflix.dto.AutorDTO;

import br.edu.ifms.bookflix.service.exception.DataIntegrityException;
import br.edu.ifms.bookflix.service.exception.ObjectNotFoundException;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AutorService {
	
	@Autowired
	private AutorRepository autoresRepository;
	private AutorDTO autoresDTO = new AutorDTO();
	
	public Autor find(Integer id) {
		Optional<Autor> objeto = autoresRepository.findById(id); 
		return objeto.orElseThrow(() -> new ObjectNotFoundException( 
				 "Autor não encontrado! Id: " + id));		
	}
	
	@Transactional
	public Autor insert (Autor objeto) {
		objeto.setId(null);
		return autoresRepository.save(objeto);
	}
	
	public Autor update(Autor objetoEditado) {
		Autor objetoAtualizado = find(objetoEditado.getId());
		objetoAtualizado = objetoEditado;
		return autoresRepository.save(objetoAtualizado);
	}
	
	@Transactional
	public void delete(Integer id, Autor objeto) {
		if(!objeto.equals(find(id))) {
			throw new IllegalArgumentException("O autor a ser removido é diferente do autor cadastrado no banco de dados");
		}
		deleteById(id);		
	}
	
	public List<Autor> findAll() {
		return autoresRepository.findAll();
	}
	
	public void deleteById(Integer id) {
		find(id);
		try {
			autoresRepository.deleteById(id);	
		} catch (DataIntegrityViolationException e) {
			throw new DataIntegrityException("Não é possível remover. Verifique a integridade referencial.");
		}		
	}
	
	public void save(Autor avaliacao) {
		autoresRepository.saveAndFlush(avaliacao);
	}
	
	public Autor fromDTO(AutorDTO objetoDTO) {
		return autoresDTO.fromDTO(objetoDTO);
	}
	
	public Autor fromNewDTO(AutorDTO objetoNewDTO) {
		return autoresDTO.fromNewDTO(objetoNewDTO);
	}
	
	public Autor autorWithoutAvaliacoesDaObra(Autor autor) {
		return autoresDTO.autorWithoutAvaliacoesDaObra(autor);
	}
    
    public void intParamaterValidator(String param) {
    	if(!param.matches("[0-9]+")) {
    		throw new IllegalArgumentException("O parâmetro tem que ser um número inteiro");
    	}
    }
    
    public void validateAutorId(Integer paramPathId, Integer autorBodyId) {
    	if(!paramPathId.equals(autorBodyId)) {
    		throw new IllegalArgumentException("O id da URL é diferente do id do autor informado no corpo da solicitação");
    	}
    }

}
