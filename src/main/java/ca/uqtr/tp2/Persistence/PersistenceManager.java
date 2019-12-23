package ca.uqtr.tp2.Persistence;

import java.lang.reflect.Field;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import ca.uqtr.tp2.Persistence.interfaces.EntityManager;
import ca.uqtr.tp2.Persistence.exception.PersitenceException;
import ca.uqtr.tp2.Persistence.exception.PrimaryKeyColumnNotFoundException;
import ca.uqtr.tp2.Persistence.exception.QueryBuilderException;
import ca.uqtr.tp2.Reflection.ReflectionManager;

import ca.uqtr.tp2.Persistence.exception.ReflectionException;
import ca.uqtr.tp2.Persistence.exception.SQLSyntaxException;
import ca.uqtr.tp2.Persistence.metadata.ColumnInfo;
import ca.uqtr.tp2.Persistence.metadata.JoinInfo;
import ca.uqtr.tp2.Persistence.metadata.PrimaryKeyInfo;

/**
 * persistence manager class
 *
 */
public class PersistenceManager implements EntityManager {
	private Connection connection;
	

	public PersistenceManager() throws SQLException {
		this.connection = getConnection("jdbc:postgresql://localhost:5432/inf1035", "postgres", "root");
	}

	/**
	 * getConnection method: for create the connection with the database
	 * @param url
	 * @param user
	 * @param password
	 */
	public static Connection getConnection(String url, String user, String password) throws SQLException {
		return DriverManager.getConnection(url, user, password);
	}

	/**
	 * persistence manager class
	 * @param bean
	 * @param sql
	 */
	@Override
	public <T> List<T> retrieve(Class<T> bean, String sql) throws PersitenceException {
		List<ColumnInfo> columns = ReflectionManager.getColumns(bean); // Recuperation des colonnes

		List<JoinInfo> joins = ReflectionManager.getRelations(bean, true); // Recuperation des jointures

		try {

		//------------------------------------------------------
			Statement stmt;                                   //|
			stmt = this.connection.createStatement();         //| Le resultat du requete SQL
			ResultSet result;                                 //|
			result = stmt.executeQuery(sql);                  //|
		//------------------------------------------------------
			List<T> beans = new ArrayList<T>();

			//-----------------------------------------While: on parcoure dans result
			while (result.next()) {
				T beanInstance = bean.newInstance();

				//-------------------------------------For: 1. On parcours chaque colonne trouvée dans le bean
				for (ColumnInfo colInfo : columns) {
					Object value = result.getObject(colInfo.getName());// 2. On récupére la valeur du colonne

					if (value != null) {
						Field f = (Field) colInfo.getAnnotatedElement();// 3. On récupére l'attribut
						// 4. On s'assure que l'attribut soit accessible
						if (!f.isAccessible()) {
							f.setAccessible(true);
						}
						f.set(beanInstance, value);
					}
				}

				// On récupérer les beans annotee par l'annotation @Join
				for (JoinInfo joinInfo : joins) {
					Class<?> innerType = ReflectionManager.getGenericInnerClass(joinInfo.getAnnotatedElement());
					Object innerBean = null;
					String query = null;
					String[] select = null, from = null, where = null;

					select = ReflectionManager.buildSelect(innerType);
					from = new String[] { joinInfo.getTableName() };
					where = new String[] {
							String.format("%s.%s = ?", joinInfo.getTableName(), joinInfo.getForeignKey()) };

					query = ReflectionManager.buildQuery(select, from, where);

					PreparedStatement innerStmt = this.connection.prepareStatement(query);

					if (joinInfo.isManyToOne()) {
						innerBean = new ArrayList<Object>();
						PrimaryKeyInfo pk = ReflectionManager.getPrimaryKeys(bean).stream()
								.filter(p -> p.getName().compareTo(joinInfo.getForeignKey()) == 0).findFirst().get();
						innerStmt.setObject(1, result.getObject(pk.getName()));
					} else {
						innerBean = innerType.newInstance();
						innerStmt.setObject(1, result.getObject(joinInfo.getForeignKey()));
					}

					query = innerStmt.toString();

					if (innerBean instanceof List) {
						innerBean = this.retrieve(innerType, query);
					} else {
						innerBean = this.retrieve(innerType, query).get(0);
					}

					Field f = (Field) joinInfo.getAnnotatedElement();

					if (!f.isAccessible()) {
						f.setAccessible(true);
					}

					f.set(beanInstance, innerBean);
				}

				beans.add(beanInstance);
			}

			result.close();
			stmt.close();

			return beans;
		} catch (InstantiationException e) {
			throw new ReflectionException("Can't instanciate object: " + e.getMessage());
		} catch (IllegalAccessException e) {
			throw new ReflectionException(
					"Illegal access to method/attribute, did you forgot to setAccessible(true): " + e.getMessage());
		} catch (SecurityException e) {
			throw new ReflectionException("Security violation: " + e.getMessage());
		} catch (IllegalArgumentException e) {
			throw new ReflectionException("Invalid arguments: " + e.getMessage());
		} catch (SQLException e) {
			throw new SQLSyntaxException("You're query have some syntax issue: (" + sql + ") " + e.getMessage());
		} catch (PrimaryKeyColumnNotFoundException e) {
			throw new ReflectionException("No primary key found: " + e.getMessage());
		} catch (QueryBuilderException e1) {
			throw new PersitenceException(e1.getMessage(), e1);
		}
	}


}
