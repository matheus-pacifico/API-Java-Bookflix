package pacifico.mvm.bookflix.repository;

import org.hibernate.boot.model.FunctionContributions;
import org.hibernate.boot.model.FunctionContributor;
import org.hibernate.type.StandardBasicTypes;

public class CustomSqlFunctions implements FunctionContributor {

	@Override
	public void contributeFunctions(FunctionContributions functionContributions) {
	    functionContributions.getFunctionRegistry().registerPattern(
	        "unaccent", 
	        "unaccent(?1)", 
	        functionContributions.getTypeConfiguration().getBasicTypeRegistry().resolve(StandardBasicTypes.STRING)
	    );
	}
	
}
