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
import pacifico.mvm.bookflix.dto.AutorDTO;
import pacifico.mvm.bookflix.model.Autor;
import pacifico.mvm.bookflix.service.AutorService;

@RestController
@RequestMapping(value = "/api/v1/autor")
public class AutorController {
	
	private final AutorService autorService;
	
    public AutorController(AutorService autorService) {
		this.autorService = autorService;
	}

	@GetMapping(value = "/mostrar/{id}")
	public ResponseEntity<Autor> find(@PathVariable Integer id) {	
		Autor objeto = autorService.autorWithoutAvaliacoesDaObra(autorService.find(id));
		return ResponseEntity.ok().body(objeto);
	}
	
    @PostMapping(value = "/adicionar")
	public ResponseEntity<Void> insert(@Valid @RequestBody AutorDTO objetoNewDTO) {
		Autor objeto = autorService.fromNewDTO(objetoNewDTO);
		objeto = autorService.insert(objeto);
		URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
				.path("/{id}").buildAndExpand(objeto.getId()).toUri();
		return ResponseEntity.created(uri).build();
	}
	
	@PutMapping(value = "/atualizar/{id}")
	public ResponseEntity<Void> update(@Valid @RequestBody AutorDTO objetoDTO, @PathVariable Integer id) {
		autorService.validateAutorId(id, objetoDTO.getId());
		Autor objeto = autorService.fromDTO(objetoDTO);
		objeto = autorService.update(objeto);
		return ResponseEntity.noContent().build();
	}
		
	@DeleteMapping(value = "/remover/{id}")
	public ResponseEntity<Void> delete(@RequestBody Autor objeto, @PathVariable Integer id) {
		autorService.delete(id, objeto);
		return ResponseEntity.noContent().build();
	}
	
	@DeleteMapping(value = "/deletar/{id}")
	public ResponseEntity<Void> deleteById(@PathVariable Integer id) {
		autorService.deleteById(id);
		return ResponseEntity.noContent().build();
	}
	
	@GetMapping(value = "/exibir")
	public ResponseEntity<List<Autor>> findAll() {
		return ResponseEntity.ok().body(autorService.findAll());
	}	

}
