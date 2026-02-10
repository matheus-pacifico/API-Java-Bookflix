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
import pacifico.mvm.bookflix.dto.AutenticacaoDTO;
import pacifico.mvm.bookflix.model.Autenticacao;
import pacifico.mvm.bookflix.service.AutenticacaoService;

@RestController
@RequestMapping(value = "/api/v1/autenticacao")
public class AutenticacaoController {
	
	private final AutenticacaoService autenticacaoService;
	
    public AutenticacaoController(AutenticacaoService autenticacaoService) {
		this.autenticacaoService = autenticacaoService;
	}

	@GetMapping(value = "/mostrar/{id}")
	public ResponseEntity<Autenticacao> find(@PathVariable Integer id) {
		Autenticacao objeto = autenticacaoService.autenticacaoWithoutObra(autenticacaoService.find(id));
		return ResponseEntity.ok().body(objeto);
	}

    @PostMapping(value = "/adicionar")
	public ResponseEntity<Void> insert(@Valid @RequestBody AutenticacaoDTO objetoNewDTO) {
		Autenticacao objeto = autenticacaoService.fromNewDTO(objetoNewDTO);
		objeto = autenticacaoService.insert(objeto);
		URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
				.path("/{id}").buildAndExpand(objeto.getId()).toUri();
		return ResponseEntity.created(uri).build();
	}
	
	@PutMapping(value = "/atualizar/{id}")
	public ResponseEntity<Void> update(@Valid @RequestBody AutenticacaoDTO objetoDTO, @PathVariable Integer id) {
		autenticacaoService.validateAvaliacaoId(id, objetoDTO.getId());
		Autenticacao objeto = autenticacaoService.fromDTO(objetoDTO);
		objeto = autenticacaoService.update(objeto);
		return ResponseEntity.noContent().build();
	}
	
	@DeleteMapping(value = "/remover/{id}")
	public ResponseEntity<Void> delete(@RequestBody Autenticacao objeto, @PathVariable Integer id) {
		autenticacaoService.delete(id, objeto);
		return ResponseEntity.noContent().build();
	}
	
	@DeleteMapping(value = "/deletar/{id}")
	public ResponseEntity<Void> deleteById(@PathVariable Integer id) {
		autenticacaoService.deleteById(id);
		return ResponseEntity.noContent().build();
	}
	
	@GetMapping(value = "/exibir")
	public ResponseEntity<List<Autenticacao>> findAll() {
		return ResponseEntity.ok().body(autenticacaoService.findAll());
	}
	
}
