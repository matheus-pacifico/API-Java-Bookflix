package pacifico.mvm.bookflix.service;

import java.util.List;
import java.util.Optional;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pacifico.mvm.bookflix.dto.UsuarioDTO;
import pacifico.mvm.bookflix.exception.DataIntegrityException;
import pacifico.mvm.bookflix.exception.ObjectNotFoundException;
import pacifico.mvm.bookflix.model.Usuario;
import pacifico.mvm.bookflix.repository.UsuarioRepository;

@Service
public class UsuarioService {

	private final UsuarioRepository usuarioRepository;
	private static final UsuarioDTO usuarioDTO = new UsuarioDTO();
	
	public UsuarioService(UsuarioRepository usuarioRepository) {
		this.usuarioRepository = usuarioRepository;
	}

	public Usuario find(Integer id) {
		Optional<Usuario> objeto = usuarioRepository.findById(id); 
		return objeto.orElseThrow(() -> new ObjectNotFoundException( 
				 "Usuário não encontrado! Id: " + id));		
	}
	
	@Transactional
	public Usuario insert (Usuario obj) {
		obj.setId(null);
		return usuarioRepository.save(obj);
	}

	public Usuario update(Usuario objetoEditado) {
		Usuario objetoAtualizado = find(objetoEditado.getId());
		objetoAtualizado = objetoEditado;
		return usuarioRepository.save(objetoAtualizado);
	}
	
	@Transactional
	public void delete(Integer id, Usuario objeto) {
		if(!objeto.equals(find(id))) {
			throw new IllegalArgumentException("O usuário a ser removido é diferente do usuário cadastrado no banco de dados");
		}
		deleteById(id);
	}
	
	public List<Usuario> findAll() {
		return usuarioRepository.findAll();
	}
	
	@Transactional
	public void deleteById(Integer id) {
		find(id);
		try {
			usuarioRepository.deleteById(id);	
		}
		catch (DataIntegrityViolationException e) {
			throw new DataIntegrityException("Não é possível remover. Verifique a integridade referencial.");
		}
	}
	
	public void save(Usuario usuario) {
		usuarioRepository.saveAndFlush(usuario);
	}
	
	public Optional<Usuario> findById(Integer id) {
		return usuarioRepository.findById(id);
	}
	
	public Usuario fromDTO(UsuarioDTO objetoDTO) {
		return usuarioDTO.fromDTO(objetoDTO);
	}
	
	public Usuario fromNewDTO(UsuarioDTO objetoNewDTO) {
		return usuarioDTO.fromNewDTO(objetoNewDTO);
	}
	
	public Usuario usuarioWithoutAvaliacaoDasObras(Usuario usuario) {
		return usuarioDTO.usuarioWithoutAvaliacaoDasObras(usuario);
	}
        
    public void validateUsuarioId(Integer paramPathId, Integer usuarioBodyId) {
    	if(!paramPathId.equals(usuarioBodyId)) {
    		throw new IllegalArgumentException("O id da URL é diferente do id do usuário informado no corpo da solicitação");
    	}
    }
	
}
