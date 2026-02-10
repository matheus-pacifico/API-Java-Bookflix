package pacifico.mvm.bookflix.controller;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import jakarta.validation.Valid;
import pacifico.mvm.bookflix.dto.UsuarioDTO;
import pacifico.mvm.bookflix.model.Usuario;
import pacifico.mvm.bookflix.service.UsuarioService;
  
@RestController
@RequestMapping(value = "/api/v1/usuario")
public class UsuarioController {
  
    private UsuarioService usuarioService;
  
    public UsuarioController(UsuarioService usuarioService) {
		this.usuarioService = usuarioService;
	}

	@GetMapping(value = "/mostrar/{id}")
    public ResponseEntity<Usuario> find(@PathVariable Integer id) { 
    	Usuario objeto = usuarioService.usuarioWithoutAvaliacaoDasObras(usuarioService.find(id)); 
    	return ResponseEntity.ok().body(objeto); 
    }
   
    @PostMapping(value = "/adicionar")
    public ResponseEntity<Void> insert(@Valid @RequestBody UsuarioDTO objetoNewDTO) {
		Usuario objeto = usuarioService.fromNewDTO(objetoNewDTO);
		objeto = usuarioService.insert(objeto);
		URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
				.path("/{id}").buildAndExpand(objeto.getId()).toUri();
		return ResponseEntity.created(uri).build();
	}
	
	@PutMapping(value = "/atualizar/{id}")
	public ResponseEntity<Void> update(@Valid @RequestBody UsuarioDTO objetoDTO, @PathVariable Integer id) {
		usuarioService.validateUsuarioId(id, objetoDTO.getId());
		Usuario objeto = usuarioService.fromDTO(objetoDTO);
		objeto = usuarioService.update(objeto);
		return ResponseEntity.noContent().build();
	}
	
	@DeleteMapping(value = "/remover/{id}")
	public ResponseEntity<Void> delete(@RequestBody Usuario objeto, @PathVariable Integer id) {
		usuarioService.delete(id, objeto);
		return ResponseEntity.noContent().build();
	}
	
	@DeleteMapping(value = "/deletar/{id}")
	public ResponseEntity<Void> deleteById(@PathVariable Integer id) {
		usuarioService.deleteById(id);
		return ResponseEntity.noContent().build();
	}
	
	@GetMapping(value = "/exibir")
	public ResponseEntity<List<Usuario>> findAll() {
		return ResponseEntity.ok().body(usuarioService.findAll());
	}
	
}
 