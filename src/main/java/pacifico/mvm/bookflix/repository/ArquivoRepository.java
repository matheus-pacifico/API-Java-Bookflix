package pacifico.mvm.bookflix.repository;

import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import jakarta.persistence.Tuple;
import pacifico.mvm.bookflix.model.Arquivo;

@Repository
public interface ArquivoRepository extends JpaRepository<Arquivo, Integer> {

	@Query(value = """
			SELECT A.caminho_arquivo 
			FROM Arquivo A INNER JOIN Obra O 
			ON A.id = O.arquivo_id 
			WHERE O.ifsn = :ifsn
		""", nativeQuery = true)
	String getCaminhoArquivoByObraIfsn(@Param("ifsn") String ifsn);
	
	@Query(value = """
			SELECT A.caminho_arquivo, A.id_arquivo
			FROM Arquivo A INNER JOIN Obra O 
			ON A.id = O.arquivo_id 
			WHERE O.ifsn = :ifsn AND A.nome_arquivo = :ofn
		""", nativeQuery = true)
	Tuple getPathAndIdToDeleteFile(@Param("ifsn") String ifsn, @Param("ofn") String originalFileName);
	
    @Query(value = """
    		SELECT UNNEST(CAST(:fileIdsToFind AS VARCHAR[])) 
    		EXCEPT 
            SELECT A.id_arquivo FROM Arquivo A 
            INNER JOIN Obra O 
            ON O.arquivo_id = A.id  
            WHERE A.id_arquivo IN (:fileIdsToFind)
            """, nativeQuery = true)
    Set<String> getUnregisteredFilesIds(@Param("fileIdsToFind") String[] fileIdsToFind);

}
