package pacifico.mvm.bookflix.service;

import java.text.Normalizer;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nimbusds.jwt.JWTClaimsSet;

import jakarta.persistence.Tuple;
import pacifico.mvm.bookflix.dto.ObraDTO;
import pacifico.mvm.bookflix.exception.DataIntegrityException;
import pacifico.mvm.bookflix.exception.ObjectNotFoundException;
import pacifico.mvm.bookflix.exception.TokenProcessingException;
import pacifico.mvm.bookflix.model.Arquivo;
import pacifico.mvm.bookflix.model.Obra;
import pacifico.mvm.bookflix.projection.ObraPersist;
import pacifico.mvm.bookflix.projection.ObraView;
import pacifico.mvm.bookflix.repository.ObraRepository;
import pacifico.mvm.bookflix.security.JwsService;

@Service
public class ObraService {

	private final ObraRepository obraRepository;
	private static final ObraDTO obraDTO = new ObraDTO();
	private static final int MIN_PAGE_SIZE = 10;
    private static final int MAX_PAGE_SIZE = 40;
	private final JwsService jwsService;
	
	public ObraService(ObraRepository obraRepository, JwsService jwsService) {
		this.obraRepository = obraRepository;
		this.jwsService = jwsService;
	}

	public Obra find(Integer id) {
		return obraRepository.findById(id)
				.orElseThrow(() -> new ObjectNotFoundException( 
				 "Obra não encontrada! Id: " + id));		
	}
	
	public Obra findByIfsn(String ifsn) {
		Obra obra = obraRepository.findByIfsn(ifsn)
	            .orElseThrow(() -> new ObjectNotFoundException("Obra não encontrada! IFSN: " + ifsn));
	    return obraWithoutSomeAttributes(obra);	
	}
	
	public Page<Obra> listAll(Pageable pageable) {
		return obraRepository.findAll(getConstrainedPageable(pageable));
	}
	
	public Obra insert(ObraPersist obraPersist) throws Exception {
		return insert(fromNewProjection(obraPersist));
	}
	
	@Transactional
	public Obra insert(Obra objeto) {
		objeto.setId(null);
		if(objeto.getAutores() != null) {
			for (int i = 0; i < objeto.getAutores().size(); i++) {
				objeto.getAutores().get(i).setObra(objeto);
			}
		}
		return obraRepository.save(objeto);
	}

	@Transactional
	public Tuple update(ObraPersist objetoEditado) throws Exception {		
		if (objetoEditado.id() == null) {
			throw new IllegalArgumentException("O id da obra não pode ser nulo");    		
		}
	    Obra objetoAtualizado = fromProjection(objetoEditado);
	    Tuple oldFileInfo = obraRepository.findArquivoInfoByObraId(objetoEditado.id());
	    oldFileInfo = correctFileInfoAndGetOldFileInfo(oldFileInfo, objetoAtualizado);
	    for (int i = 0; i < objetoAtualizado.getAutores().size(); i++) {
	        objetoAtualizado.getAutores().get(i).setObra(objetoAtualizado);
	    }
		obraRepository.save(objetoAtualizado);
	    return oldFileInfo;
	}
	
	private Tuple correctFileInfoAndGetOldFileInfo(Tuple oldFileInfo, Obra objetoAtualizado) {
		if(oldFileInfo == null) {
			return null;
		}
	    if (objetoAtualizado.getArquivo() == null) {
	    	objetoAtualizado.setArquivo(
	    		new Arquivo(
	    			oldFileInfo.get("id", Integer.class), 
	    			oldFileInfo.get("id_arquivo", String.class),
	    			oldFileInfo.get("caminho_arquivo", String.class), 
	    			oldFileInfo.get("nome_arquivo", String.class),
	    			objetoAtualizado
	    		)
	    	);
	    	return null;
	    }
    	objetoAtualizado.getArquivo().setId(oldFileInfo.get("id", Integer.class));
    	objetoAtualizado.getArquivo().setObra(objetoAtualizado);
    	if (objetoAtualizado.getArquivo().getIdArquivo()
    			.compareTo(oldFileInfo.get("id_arquivo", String.class)) == 0) {
    		return null;
    	}
    	return oldFileInfo;
	}
	
	@Transactional
	public void delete(Integer id, Obra objeto) {
		if(!objeto.equals(find(id))) {
			throw new IllegalArgumentException("A obra a ser removida é diferente da obra cadastrada no banco de dados");
		}
		deleteById(id);
	}
	
	public List<Obra> findAll() {
		return obraRepository.findAll(); 
	}
	
	@Transactional
	public void deleteById(Integer id) {
        int status = obraRepository.deleteObraCascade(id);
        if (status == 0) {
            throw new ObjectNotFoundException("Não foi possível excluir: obra não encontrada.");
        } else if (status == -1) {
            throw new DataIntegrityException("Não foi possível excluir: violação de chave estrangeira.");
        }
	}
	
	public void save(Obra obra) {
		obraRepository.saveAndFlush(obra);
	}
    
    private Obra fromProjection(ObraPersist obra) throws Exception {
    	Obra newObra = new Obra(
    			obra.id(),
    			obra.ifsn(),
    			obra.titulo(),
    			obra.area(),
    			obra.descricao(),
    			obra.ano(),
    			obra.professor(),
    			null
    	);
    	newObra.setAutores(obra.autores());
    	if (obra.fileInfo() != null && !obra.fileInfo().isBlank()) {
    		JWTClaimsSet fileInfo;
    		try {
    			fileInfo = jwsService.getClaimsIfIsAValidToken(obra.fileInfo());
    		} catch (SecurityException e) {
    			throw new IllegalStateException("As informações do arquivo enviado são inválidas.");
    		} catch (TokenProcessingException e) {
    			throw new TokenProcessingException("Não foi possível verificar as informações do arquivo enviado.");
    		}
    		newObra.setArquivo(
    				new Arquivo( 
    						fileInfo.getClaim("id").toString(), 
        	    			fileInfo.getClaim("path").toString(),
        	    			fileInfo.getClaim("name").toString()));
    	}
    	return newObra;
    }
    
    private Obra fromNewProjection(ObraPersist obra) throws IllegalArgumentException, IllegalStateException {
		if (obra.fileInfo() == null || obra.fileInfo().isBlank()) {
			throw new IllegalArgumentException("Envie um arquivo antes de enviar a obra");
		}
		JWTClaimsSet fileInfo;
		try {
			fileInfo = jwsService.getClaimsIfIsAValidToken(obra.fileInfo());
		} catch (SecurityException e) {
			throw new IllegalStateException("As informações do arquivo enviado são inválidas.");
		} catch (TokenProcessingException e) {
			throw new TokenProcessingException("Não foi possível verificar as informações do arquivo enviado.");
		}
    	Obra newObra = new Obra(
    			null,
    			obra.ifsn(),
    			obra.titulo(),
    			obra.area(),
    			obra.descricao(),
    			obra.ano(),
    			obra.professor(),
    			new Arquivo( 
    	    			fileInfo.getClaim("id").toString(), 
    	    			fileInfo.getClaim("path").toString(),
    	    			fileInfo.getClaim("name").toString())
    	);
    	newObra.setAutores(obra.autores());
    	return newObra;
    }
	
    public Page<ObraView> searchObra(String pesquisa, Pageable pageable) {
        return obraRepository.searchObra(unaccentedParam(pesquisa), getConstrainedPageable(pageable));
    }

    public Page<ObraView> searchObraByIfsn(String ifsn, Pageable pageable) {
        return obraRepository.searchObraByIfsn(unaccentedParam(ifsn), getConstrainedPageable(pageable));
    }
    
    public Page<ObraView> searchObraByTitulo(String titulo, Pageable pageable) {
        return obraRepository.searchObraByTitulo(unaccentedParam(titulo), getConstrainedPageable(pageable));
    }

    public Page<ObraView> searchObraByArea(String area, Pageable pageable) {
        return obraRepository.searchObraByArea(unaccentedParam(area), getConstrainedPageable(pageable));
    }

    public Page<ObraView> searchObraByAno(int ano, Pageable pageable) {
        return obraRepository.searchObraByAno(ano, getConstrainedPageable(pageable));
    }

    public Obra obraWithoutSomeAttributes(Obra objeto) {
    	return obraDTO.obraWithoutSomeAttributes(objeto);
    }
    
    private static String unaccentedParam(String parameter) {
    	return Normalizer.normalize(parameter, Normalizer.Form.NFD)
    			.replaceAll("[^\\p{ASCII}]",  "");
    }
    
    private Pageable getConstrainedPageable(Pageable pageable) {
        int pageSize = Math.max(MIN_PAGE_SIZE, Math.min(pageable.getPageSize(), MAX_PAGE_SIZE));
    	return PageRequest.of(pageable.getPageNumber(), pageSize, pageable.getSort());
    }
    
}
