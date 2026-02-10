package pacifico.mvm.bookflix.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import jakarta.persistence.Tuple;
import pacifico.mvm.bookflix.model.Obra;
import pacifico.mvm.bookflix.projection.ObraView;

@Repository
public interface ObraRepository extends JpaRepository<Obra, Integer> {
	
	Optional<Obra> findByIfsn(String ifsn);
	
    @Query(value = "SELECT fn_delete_obra_cascade(:id)", nativeQuery = true)
    int deleteObraCascade(@Param("id") Integer id);	

	@Query(value = """
		    SELECT O.id, O.ifsn, O.titulo, O.area, O.ano, O.descricao, AR.id_arquivo,
			    ARRAY_AGG(A.nome) AS autores
			FROM obra O
			LEFT JOIN autor A ON O.id = A.obra_id
			INNER JOIN Arquivo AR ON AR.id = O.arquivo_id
			WHERE
			    TO_TSVECTOR('portuguese',
			        i_unaccent(ifsn) || ' ' || i_unaccent(titulo) || ' ' || i_unaccent(area) || ' ' || i_unaccent(descricao)
			    ) @@ PLAINTO_TSQUERY('portuguese', i_unaccent(:q))
			    OR EXISTS (
			        SELECT 1
			        FROM autor A_sub
			        WHERE A_sub.obra_id = O.id
			        AND TO_TSVECTOR('portuguese', i_unaccent(A_sub.nome))
			            @@ PLAINTO_TSQUERY('portuguese', i_unaccent(:q))
			    )
			GROUP BY O.id, AR.id_arquivo
		""", nativeQuery = true)
	Page<ObraView> searchObra(@Param("q") String pesquisa, Pageable pageable);

	@Query(value = """			
			SELECT o.id, o.ifsn, o.titulo, o.area, o.ano, o.descricao, ar.id_arquivo, ARRAY_AGG(a.nome) AS autores 
			FROM obra o left join autor a on a.obra_id = o.id left join arquivo ar on ar.id = o.arquivo_id 
			WHERE unaccent(o.ifsn) ILIKE CONCAT(:ifsn, '%')
			GROUP BY o.id, ar.id_arquivo
		""", nativeQuery = true)
	Page<ObraView> searchObraByIfsn(@Param("ifsn") String ifsn, Pageable pageable);
	
	@Query(value = """			
			SELECT o.id, o.ifsn, o.titulo, o.area, o.ano, o.descricao, ar.id_arquivo, ARRAY_AGG(a.nome) AS autores 
			FROM obra o left join autor a on a.obra_id = o.id left join arquivo ar on ar.id = o.arquivo_id 
			WHERE unaccent(o.titulo) ILIKE CONCAT('%', :titulo, '%')
			GROUP BY o.id, ar.id_arquivo
		""", nativeQuery = true)
	Page<ObraView> searchObraByTitulo(@Param("titulo") String titulo, Pageable pageable);
		
	@Query(value = """			
			SELECT o.id, o.ifsn, o.titulo, o.area, o.ano, o.descricao, ar.id_arquivo, ARRAY_AGG(a.nome) AS autores 
			FROM obra o left join autor a on a.obra_id = o.id left join arquivo ar on ar.id = o.arquivo_id  
			WHERE unaccent(o.area)
			ILIKE CONCAT('%', :area, '%')
			GROUP BY o.id, ar.id_arquivo
		""", nativeQuery = true)
	Page<ObraView> searchObraByArea(@Param("area") String area, Pageable pageable);

	@Query(value = """
			SELECT o.id, o.ifsn, o.titulo, o.area, o.ano, o.descricao, ar.id_arquivo, ARRAY_AGG(a.nome) AS autores 
			FROM obra o left join autor a on a.obra_id = o.id left join arquivo ar on ar.id = o.arquivo_id 
			WHERE o.ano = :ano
			GROUP BY o.id, ar.id_arquivo
		""", nativeQuery = true)
	Page<ObraView> searchObraByAno(@Param("ano") int ano, Pageable pageable);
	
	@Query(value = """
			SELECT a.caminho_arquivo, a.id_arquivo, a.nome_arquivo, a.id
			FROM Obra o INNER JOIN Arquivo a 
			ON a.id = o.arquivo_id 
			WHERE o.id = :id
		""", nativeQuery = true)
	Tuple findArquivoInfoByObraId(@Param("id") Integer id);
	
}
