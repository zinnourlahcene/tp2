package ca.uqtr.tp2.Persistence.interfaces;

import java.util.List;

import ca.uqtr.tp2.Persistence.exception.PersitenceException;

public interface EntityManager {
	<T> List<T> retrieve(Class<T> bean, String sql) throws PersitenceException;
}
