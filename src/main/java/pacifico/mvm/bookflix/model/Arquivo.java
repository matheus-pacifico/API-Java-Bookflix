package pacifico.mvm.bookflix.model;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(indexes = {
		@Index(name = "idx_id_arquivo", columnList = "id_arquivo", unique = true),
		@Index(name = "idx_caminho_arquivo", columnList = "caminho_arquivo", unique = true)
})
public class Arquivo implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@Column(nullable = false, unique = true)
	private String idArquivo;
	@Column(nullable = false, unique = true)
	private String caminhoArquivo;
	private String nomeArquivo;
	
	@JsonIgnoreProperties("arquivo")
	@OneToOne(mappedBy = "arquivo")
	private Obra obra;

	public Arquivo() {

	}

	public Arquivo(String idArquivo, String caminhoArquivo, String nomeArquivo) {
		this.idArquivo = idArquivo;
		this.caminhoArquivo = caminhoArquivo;
		this.nomeArquivo = nomeArquivo;
	}

	public Arquivo(Integer id, String idArquivo, String caminhoArquivo, String nomeArquivo) {
		this.id = id;
		this.idArquivo = idArquivo;
		this.caminhoArquivo = caminhoArquivo;
		this.nomeArquivo = nomeArquivo;
	}

	public Arquivo(Integer id, String idArquivo, String caminhoArquivo, String nomeArquivo, Obra obra) {
		this.id = id;
		this.idArquivo = idArquivo;
		this.caminhoArquivo = caminhoArquivo;
		this.nomeArquivo = nomeArquivo;
		this.obra = obra;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getIdArquivo() {
		return idArquivo;
	}

	public void setIdArquivo(String idArquivo) {
		this.idArquivo = idArquivo;
	}

	public String getCaminhoArquivo() {
		return caminhoArquivo;
	}

	public void setCaminhoArquivo(String caminhoArquivo) {
		this.caminhoArquivo = caminhoArquivo;
	}

	public String getNomeArquivo() {
		return nomeArquivo;
	}

	public void setNomeArquivo(String nomeArquivo) {
		this.nomeArquivo = nomeArquivo;
	}

	public Obra getObra() {
		return obra;
	}

	public void setObra(Obra obra) {
		this.obra = obra;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Arquivo other = (Arquivo) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
	
}
