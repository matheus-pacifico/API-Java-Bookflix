package pacifico.mvm.bookflix.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

@Component
public class DatabaseConfiguration implements ApplicationRunner {

    private final DataSource dataSource;
    private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseConfiguration.class);

    public DatabaseConfiguration(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        try (Connection conn = dataSource.getConnection(); 
        		Statement stmt = conn.createStatement()) {
            createUnaccentExtension(stmt);
            createImmutableUnaccentFunction(stmt);
            createObraFullTextSearchIndex(stmt);
            createAutorFullTextSearchIndex(stmt);
            createObraCascadeDeleteFunction(stmt);
            LOGGER.info("Database initialized successfully!");
        } catch (SQLException e) {
        	LOGGER.warn("SQL Warning Code: {}, SQLState: {}, Message: {}", e.getErrorCode(), e.getSQLState(), e.getMessage());
        }
    }  
 
    private void createUnaccentExtension(Statement stmt) throws SQLException {
    	stmt.execute("CREATE EXTENSION IF NOT EXISTS unaccent;");
    }
    
    private void createImmutableUnaccentFunction(Statement stmt) throws SQLException {
    	stmt.execute("CREATE OR REPLACE FUNCTION public.i_unaccent(text) \n"
    			+ "RETURNS text \n"
    			+ "LANGUAGE 'sql' \n"
    			+ "COST 100 \n"
    			+ "IMMUTABLE STRICT PARALLEL SAFE \n"
    			+ "AS $BODY$ \n"
    			+ "	SELECT public.unaccent('public.unaccent', $1);\n"
    			+ "$BODY$;");
    }
    
    private void createObraFullTextSearchIndex(Statement stmt) throws SQLException {
    	stmt.execute("CREATE INDEX IF NOT EXISTS idx_obra_fts\n"
    			+ "    ON public.obra USING gin\n"
    			+ "    (to_tsvector('portuguese'::regconfig, (((((i_unaccent(ifsn::text) || ' '::text) || i_unaccent(titulo::text)) || ' '::text) || i_unaccent(area::text)) || ' '::text) || i_unaccent(descricao::text)))\n"
    			+ "    TABLESPACE pg_default;");
    }
    
    private void createAutorFullTextSearchIndex(Statement stmt) throws SQLException {
    	stmt.execute("CREATE INDEX IF NOT EXISTS idx_autor_nome_fts\n"
    			+ "    ON public.autor USING gin\n"
    			+ "    (to_tsvector('portuguese'::regconfig, i_unaccent(nome::text)))\n"
    			+ "    TABLESPACE pg_default;");
    }

	private void createObraCascadeDeleteFunction(Statement stmt) throws SQLException {
		stmt.execute("CREATE OR REPLACE FUNCTION fn_delete_obra_cascade(p_obra_id INT)\n"
				+ "RETURNS INT AS $$\n"
				+ "DECLARE\n"
				+ "    v_deleted INT;\n"
				+ "BEGIN\n"
				+ "    DELETE FROM avaliacao WHERE obra_id = p_obra_id;\n"
				+ "    DELETE FROM autor WHERE obra_id = p_obra_id;\n"
				+ "    DELETE FROM obra WHERE id = p_obra_id;\n"
				+ "    GET DIAGNOSTICS v_deleted = ROW_COUNT;\n"
				+ "\n"
				+ "    IF v_deleted = 0 THEN\n"
				+ "        RETURN 0;\n"
				+ "    ELSE\n"
				+ "        RETURN 1;\n"
				+ "    END IF;\n"
				+ "\n"
				+ "EXCEPTION\n"
				+ "    WHEN foreign_key_violation THEN\n"
				+ "        RETURN -1;\n"
				+ "END;\n"
				+ "$$ LANGUAGE plpgsql;");
	}
    
}
