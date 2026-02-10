package pacifico.mvm.bookflix.projection;

import java.util.List;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import pacifico.mvm.bookflix.model.Autor;
import pacifico.mvm.bookflix.model.Professor;

public record ObraPersist(
		@Min(value = 1, message="O id informado é inválido")
		Integer id,
		@NotBlank(message="Preenchimento obrigatório")
		String ifsn, 
		@NotBlank(message="Preenchimento obrigatório")
		String titulo, 
		@NotBlank(message="Preenchimento obrigatório")
		String area, 
		@NotBlank(message="Preenchimento obrigatório")
		String descricao, 
		@NotNull(message="Preenchimento obrigatório")
		String fileInfo,
		@NotNull(message="Preenchimento obrigatório")
		int ano, 
		Professor professor,
		@NotNull(message="Preenchimento obrigatório")
		List<Autor> autores) {
}
