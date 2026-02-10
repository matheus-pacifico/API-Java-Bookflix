package pacifico.mvm.bookflix.controller;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
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
import jakarta.validation.constraints.NotBlank;
import pacifico.mvm.bookflix.dto.AlunoDTO;
import pacifico.mvm.bookflix.model.Aluno;
import pacifico.mvm.bookflix.service.AlunoService;

@RestController
@RequestMapping(value = "/api/v1/aluno")
@Validated
public class AlunoController {
  
    private final AlunoService alunoService;
  
    public AlunoController(AlunoService alunoService) {
		this.alunoService = alunoService;
	}

	@GetMapping(value = "/mostrar/{id}")
    public ResponseEntity<Aluno> find(@PathVariable Integer id) {
	    Aluno objeto = alunoService.find(id); 
	    return ResponseEntity.ok().body(objeto); 
    }
   
    @PostMapping(value = "/adicionar")
    public ResponseEntity<Void> insert(@Valid @RequestBody AlunoDTO objetoNewDTO) {
	    Aluno objeto = alunoService.fromNewDTO(objetoNewDTO);
	    objeto = alunoService.insert(objeto);
	    URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
			   .path("/{id}").buildAndExpand(objeto.getId()).toUri();
	    return ResponseEntity.created(uri).build();
	}
	
	@PutMapping(value = "/atualizar/{id}")
	public ResponseEntity<Void> update(@Valid @RequestBody AlunoDTO objetoDTO, @PathVariable Integer id) {
		alunoService.validateAlunoId(id, objetoDTO.getId());
		Aluno objeto = alunoService.fromDTO(objetoDTO);
		objeto = alunoService.update(objeto);
		return ResponseEntity.noContent().build();
	}
	
	@DeleteMapping(value = "/remover/{id}")
	public ResponseEntity<Void> delete(@RequestBody Aluno objeto, @PathVariable Integer id) {
		alunoService.delete(id, objeto);
		return ResponseEntity.noContent().build();
	}
	
	@DeleteMapping(value = "/deletar/{id}")
	public ResponseEntity<Void> deleteById(@PathVariable Integer id) {
		alunoService.deleteById(id);
		return ResponseEntity.noContent().build();
	}
	
	@GetMapping(value = "/exibir")
	public ResponseEntity<List<Aluno>> findAll() {
		return ResponseEntity.ok().body(alunoService.findAll());
	}
	
	@GetMapping(value = "/exibir/turma/{turma}")
	public ResponseEntity<List<Aluno>> findAlunosByTurma(@PathVariable int turma) {
		return ResponseEntity.ok().body(alunoService.findAlunosByTurma(turma));
	}	
	
	@GetMapping(value = "/mostrar/ra/{ra}")
	public ResponseEntity<Aluno> findByRa(@NotBlank @PathVariable String ra) {
		return ResponseEntity.ok().body(alunoService.findByRa(ra));
	}
  
}
 