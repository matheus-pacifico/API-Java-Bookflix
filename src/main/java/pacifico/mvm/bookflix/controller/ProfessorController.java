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
import pacifico.mvm.bookflix.dto.ProfessorDTO;
import pacifico.mvm.bookflix.model.Professor;
import pacifico.mvm.bookflix.service.ProfessorService;

@RestController
@RequestMapping(value = "/api/v1/professor")
public class ProfessorController {
	
	private final ProfessorService professorService;
	
	public ProfessorController(ProfessorService professorService) {
		this.professorService = professorService;
	}

	@GetMapping(value = "/mostrar/{id}")
	public ResponseEntity<Professor> find(@PathVariable Integer id) {
		Professor objeto = professorService.find(id);
		return ResponseEntity.ok().body(objeto);
	}
		
	@PostMapping(value = "/adicionar")
	public ResponseEntity<Void> insert(@Valid @RequestBody ProfessorDTO objetoNewDTO) {
		Professor objeto = professorService.fromNewDTO(objetoNewDTO);
		objeto = professorService.insert(objeto);
		URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
				.path("/{id}").buildAndExpand(objeto.getId()).toUri();
		return ResponseEntity.created(uri).build();
	}
	
	@PutMapping(value = "/atualizar/{id}")
	public ResponseEntity<Void> update(@Valid @RequestBody ProfessorDTO objetoDTO, @PathVariable Integer id) {
		professorService.validateProfessorId(id, objetoDTO.getId());
		Professor objeto = professorService.fromDTO(objetoDTO);
		objeto = professorService.update(objeto);
		return ResponseEntity.noContent().build();
	}
	
	@DeleteMapping(value = "/remover/{id}")
	public ResponseEntity<Void> delete(@RequestBody Professor objeto, @PathVariable Integer id) {
		professorService.delete(id, objeto);
		return ResponseEntity.noContent().build();
	}
	
	@DeleteMapping(value = "/deletar/{id}")
	public ResponseEntity<Void> deleteById(@PathVariable Integer id) {
		professorService.deleteById(id);
		return ResponseEntity.noContent().build();
	}
	
	@GetMapping(value = "/exibir")
	public ResponseEntity<List<Professor>> findAll() {
		return ResponseEntity.ok().body(professorService.findAll());
	}	

}
