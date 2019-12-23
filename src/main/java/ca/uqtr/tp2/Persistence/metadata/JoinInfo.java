package ca.uqtr.tp2.Persistence.metadata;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.Collection;
import java.util.List;

import ca.uqtr.tp2.Persistence.annotation.Join;
import ca.uqtr.tp2.Persistence.annotation.Table;
import ca.uqtr.tp2.Persistence.exception.ForeignKeyNotFoundException;
import ca.uqtr.tp2.Persistence.exception.JoinReflectionException;
import ca.uqtr.tp2.Persistence.exception.MissingTableAnnotation;
import ca.uqtr.tp2.Persistence.exception.NoPrimaryKeyFoundException;
import ca.uqtr.tp2.Persistence.exception.PrimaryKeyColumnNotFoundException;
import ca.uqtr.tp2.Reflection.ReflectionManager;

/**
 * Represent a join metadata
 *
 */
public class JoinInfo {
	private String tableName;
	private String foreignKey;
	private AnnotatedElement annotatedElement;
	/**
	 * Initializes a newly created JoinInfo object so that it represents a JoinInfo. 
	 *
	 * @param tableName
	 * @param foreignKey
	 * @param annotatedElement
	 */
	public JoinInfo(String tableName, String foreignKey, AnnotatedElement annotatedElement) {
		super();
		this.tableName = tableName;
		this.foreignKey = foreignKey;
		this.annotatedElement = annotatedElement;
	}
	
	public JoinInfo(AnnotatedElement annotatedElement) throws JoinReflectionException, PrimaryKeyColumnNotFoundException, ForeignKeyNotFoundException, MissingTableAnnotation, NoPrimaryKeyFoundException {
		if(annotatedElement == null) {
			throw new NullPointerException("can't create metadata from null");
		}
		
		this.annotatedElement = annotatedElement;
		
		Join annotation = annotatedElement.getAnnotation(Join.class);
		
		if(annotation.tableName().isEmpty()) {
			this.checkTableNameFromInnerType(annotatedElement);
		}else {
			this.tableName = annotation.tableName();
		}
		
		if(annotation.fk().isEmpty()) {
			this.checkForeignKeyFromInnerType(annotatedElement);
		}else {
			this.foreignKey = annotation.fk();
		}
	}
	
	private void checkForeignKeyFromInnerType(AnnotatedElement el) throws JoinReflectionException, PrimaryKeyColumnNotFoundException, ForeignKeyNotFoundException {
		Field f = (Field) el;
		Class<?> searchFksBaseClass;
		Class<?> pksClassToScan;
		
		if(f.getType().equals(List.class)) {
			// Many To One
			ParameterizedType parameterizedType = (ParameterizedType) f.getGenericType();
			pksClassToScan = f.getDeclaringClass();
			searchFksBaseClass = (Class<?>) parameterizedType.getActualTypeArguments()[0];
		}else {
			// One To One
			pksClassToScan = f.getType();
			searchFksBaseClass = f.getDeclaringClass();
		}
		
		boolean fkFound = false;
		List<PrimaryKeyInfo> pks = ReflectionManager.getPrimaryKeys(pksClassToScan);
		
		for(PrimaryKeyInfo pk : pks) {
			for(ColumnInfo colInfo : ReflectionManager.getColumns(searchFksBaseClass)) {
				fkFound = colInfo.getName().compareTo(pk.getName()) == 0;
				if (fkFound) {
					this.foreignKey = colInfo.getName();
					break;
				};
			}
			if (fkFound) break;
		}
		
		if (!fkFound)
			throw new ForeignKeyNotFoundException();
		
		
	}
	
	private void checkTableNameFromInnerType(AnnotatedElement el) throws MissingTableAnnotation, PrimaryKeyColumnNotFoundException, NoPrimaryKeyFoundException {
		Field f = (Field) el;
		Class<?> innerType = f.getType();
		
		if(this.isManyToOne()) {
			ParameterizedType parameterizedType = (ParameterizedType) f.getGenericType();
			innerType = (Class<?>) parameterizedType.getActualTypeArguments()[0];
		}
		
		if(!innerType.isAnnotationPresent(Table.class))
			throw new MissingTableAnnotation(innerType);
		
		this.tableName = ReflectionManager.getTableInfo(innerType).getTableName();
	}
	/**
	 * @return the tableName
	 */
	public String getTableName() {
		return tableName;
	}
	/**
	 * @param tableName the tableName to set
	 */
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	/**
	 * @return the foreignKey
	 */
	public String getForeignKey() {
		return foreignKey;
	}
	/**
	 * @param foreignKey the foreignKey to set
	 */
	public void setForeignKey(String foreignKey) {
		this.foreignKey = foreignKey;
	}
	/**
	 * @return the annotatedElement
	 */
	public AnnotatedElement getAnnotatedElement() {
		return annotatedElement;
	}
	/**
	 * @param annotatedElement the annotatedElement to set
	 */
	public void setAnnotatedAlement(AnnotatedElement annotatedElement) {
		this.annotatedElement = annotatedElement;
	}
	
	public boolean isManyToOne() {
		Field f = (Field) this.annotatedElement;
		
		if(f.getType().equals(List.class)
			|| f.getType().equals(Collection.class)
			|| f.getType().equals(Iterable.class)) {
			return true;
		}
		
		return false;
	}
	
	@Override
	public String toString() {
		return "tableName: " + tableName + ", fk: " + foreignKey;
	}
}
