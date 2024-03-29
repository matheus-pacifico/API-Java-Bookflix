package br.edu.ifms.bookflix.repository;

import br.edu.ifms.bookflix.model.Autor;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AutorRepository extends JpaRepository<Autor, Integer>{

}
