package pacifico.mvm.bookflix.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import pacifico.mvm.bookflix.model.Aluno;

@Repository
public interface AlunoRepository extends JpaRepository<Aluno, Integer> {
	
	Optional<Aluno> findByRa(String ra);

	@Query("SELECT A FROM Aluno A WHERE A.turma = :turma")
	List<Aluno> findAlunosByTurma(@Param("turma") int turma);
	
}
