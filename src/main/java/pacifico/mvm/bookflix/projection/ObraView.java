package pacifico.mvm.bookflix.projection;

import java.util.List;

public interface ObraView {
		
	String getIfsn();
	String getTitulo();
	String getArea();
	int getAno();
	String getDescricao();
	String getIdArquivo();
	List<String> getAutores();
	
}
