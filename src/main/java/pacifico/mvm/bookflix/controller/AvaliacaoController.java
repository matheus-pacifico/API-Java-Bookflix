package pacifico.mvm.bookflix.controller;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

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
import pacifico.mvm.bookflix.dto.AvaliacaoDTO;
import pacifico.mvm.bookflix.model.Avaliacao;
import pacifico.mvm.bookflix.service.AvaliacaoService;

@RestController
@RequestMapping(value = "api/v1/avaliacao")
public class AvaliacaoController {
	
	private final AvaliacaoService avaliacaoService;
	
    public AvaliacaoController(AvaliacaoService avaliacaoService) {
		this.avaliacaoService = avaliacaoService;
	}

	@GetMapping(value = "/mostrar/{id}")
	public ResponseEntity<Avaliacao> find(@PathVariable Integer id) {
		Avaliacao objeto = avaliacaoService.avaliacaoWithoutUsuariosDataExceptName(avaliacaoService.find(id));
		return ResponseEntity.ok().body(objeto);
	}
	
    @PostMapping(value = "/adicionar")
	public ResponseEntity<Void> insert(@Valid @RequestBody AvaliacaoDTO objetoNewDTO) {
		Avaliacao objeto = avaliacaoService.fromNewDTO(objetoNewDTO);
		objeto = avaliacaoService.insert(objeto);
		URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
			.path("/{id}").buildAndExpand(objeto.getId()).toUri();
		return ResponseEntity.created(uri).build();
	}
	
	@PutMapping(value = "/atualizar/{id}")
	public ResponseEntity<Void> update(@Valid @RequestBody AvaliacaoDTO objetoDTO, @PathVariable Integer id) {
		avaliacaoService.validateAvaliacaoId(id, objetoDTO.getId());
		Avaliacao objeto = avaliacaoService.fromDTO(objetoDTO);
		objeto = avaliacaoService.update(objeto);
		return ResponseEntity.noContent().build();
	}
		
	@DeleteMapping(value = "/remover/{id}")
	public ResponseEntity<Void> delete(@RequestBody Avaliacao objeto, @PathVariable Integer id) {
		avaliacaoService.delete(id, objeto);
		return ResponseEntity.noContent().build();
	}
	
	@DeleteMapping(value = "/deletar/{id}")
	public ResponseEntity<Void> deleteById(@PathVariable Integer id) {
		avaliacaoService.deleteById(id);
		return ResponseEntity.noContent().build();
	}
	
	@GetMapping(value = "/exibir")
	public ResponseEntity<List<AvaliacaoDTO>> findAll() {		
		List<Avaliacao> lista = avaliacaoService.findAll();
		List<AvaliacaoDTO> listaDTO = lista.stream().map(objeto -> new AvaliacaoDTO(objeto)).collect(Collectors.toList());
		return ResponseEntity.ok().body(listaDTO);
	}	

}
