package pacifico.mvm.bookflix.controller;

import java.net.URI;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import jakarta.persistence.Tuple;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import pacifico.mvm.bookflix.model.Obra;
import pacifico.mvm.bookflix.projection.ObraPersist;
import pacifico.mvm.bookflix.projection.ObraView;
import pacifico.mvm.bookflix.service.ArquivoService;
import pacifico.mvm.bookflix.service.ObraService;

@RestController
@RequestMapping(value = "/api/v1/obra")
@Validated
public class ObraController {

	private final ObraService obraService;
	private final ArquivoService arquivoService;
	
	public ObraController(ObraService obraService, ArquivoService arquivoService) {
		this.obraService = obraService;
		this.arquivoService = arquivoService;
	}

	@GetMapping(value = "/mostrar/{id}")
	public ResponseEntity<Obra> search(@PathVariable Integer id) {
		return ResponseEntity.ok().body(obraService.find(id));
	}
	
	@PostMapping(value = "/adicionar")
	public ResponseEntity<Void> insert(@Valid @RequestBody ObraPersist objetoPersist) throws Exception {
		URI uri = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/api/v1/obra/mostrar/{id}")
                .buildAndExpand(obraService.insert(objetoPersist).getId())
                .toUri();
		return ResponseEntity.created(uri).build();
	}
	
	@PutMapping(value = "/atualizar")
	public ResponseEntity<Void> update(@Valid @RequestBody ObraPersist objetoPersist) throws Exception {
		Tuple oldFileInfo = obraService.update(objetoPersist);
		if (oldFileInfo != null) {
		    CompletableFuture.runAsync(() -> {
		        try {
		        	arquivoService.deleteFile(
		        			oldFileInfo.get("caminho_arquivo", String.class), 
		        			oldFileInfo.get("id_arquivo", String.class));
		        	System.out.println("foii");
		        } catch (Exception e) {
		        	System.err.println("nao foi");
		        }
		    });
		}
		return ResponseEntity.noContent().build();
	}
	
	@DeleteMapping(value = "/remover/{id}")
	public ResponseEntity<Void> delete(@RequestBody Obra objeto, @PathVariable Integer id) {
		obraService.delete(id, objeto);
		return ResponseEntity.noContent().build();
	}
	
	@DeleteMapping(value = "/deletar/{id}")
	public ResponseEntity<Void> deleteById(@PathVariable Integer id) {
		obraService.deleteById(id);
		return ResponseEntity.noContent().build();
	}
	
	@GetMapping(value = "/listar")
	public ResponseEntity<List<Obra>> findAll() {
		return ResponseEntity.ok().body(obraService.findAll());
	}
	
    @GetMapping(value = "/listar-paginado")
    public ResponseEntity<Page<Obra>> listAll(@PageableDefault(page = 0, size = 10) Pageable pageable) {
        return ResponseEntity.ok().body(obraService.listAll(pageable));
    }
	
	@GetMapping(value = "/mostrar/ifsn/{ifsn}")
	public ResponseEntity<Obra> findByIfsn(@NotBlank @PathVariable String ifsn) {
		return ResponseEntity.ok().body(obraService.obraWithoutSomeAttributes(obraService.findByIfsn(ifsn)));
	}
	
	@GetMapping(value = "/search")
	public ResponseEntity<Page<ObraView>> searchObra(@NotBlank @RequestParam String q,
			@PageableDefault(page = 0, size = 10) Pageable pageable) {
		return ResponseEntity.ok().body(obraService.searchObra(q, pageable));
	}

	@GetMapping(value = "/search/titulo/{titulo}")
	public ResponseEntity<Page<ObraView>> searchByTitulo(@NotBlank @PathVariable String titulo,
			@PageableDefault(page = 0, size = 10) Pageable pageable) {
		return ResponseEntity.ok().body(obraService.searchObraByTitulo(titulo, pageable));
	}
	
	@GetMapping(value = "/search/ifsn/{ifsn}")
	public ResponseEntity<Page<ObraView>> searchByIfsn(@NotBlank @PathVariable String ifsn,
			@PageableDefault(page = 0, size = 10) Pageable pageable) {
		return ResponseEntity.ok().body(obraService.searchObraByIfsn(ifsn, pageable));
	}
	
	@GetMapping(value = "/search/area/{area}")
	public ResponseEntity<Page<ObraView>> searchByArea(@NotBlank @PathVariable String area,
			@PageableDefault(page = 0, size = 10) Pageable pageable) {
		return ResponseEntity.ok().body(obraService.searchObraByArea(area, pageable));
	}
	
	@GetMapping(value = "/search/ano/{ano}")
	public ResponseEntity<Page<ObraView>> searchByAno(@PathVariable int ano,
			@PageableDefault(page = 0, size = 10) Pageable pageable) {
		return ResponseEntity.ok().body(obraService.searchObraByAno(ano, pageable));
	}
	
}
