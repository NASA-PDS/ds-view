/**
 * Copyright 2010-2017, by the California Institute of Technology.
 */
package gov.nasa.pds.tracking.tracking.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

/**
 * @author danyu dan.yu@jpl.nasa.gov
 *
 */
public class ReferenceDao extends DBConnector {

	public static Logger logger = Logger.getLogger(ReferenceDao.class);

	public static final String INST_TABLENAME  = "instrument_reference";
	public static final String INVES_TABLENAME  = "investigation_reference";
	public static final String NODE_TABLENAME  = "node_reference";
	
	public static final String LOG_IDENTIFIERCOLUMN = "logical_identifier";
	public static final String REFERENCECOLUMN = "reference";
	public static final String TITLECOLUMN = "title";
		
	private ResultSet resultSet = null;

	/**
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	public ReferenceDao() throws ClassNotFoundException, SQLException {
		// TODO Auto-generated constructor stub
	}

	public List<Reference> getProductAllReferences(String tableName) {
		
		List<Reference> refs = new ArrayList<Reference>();
		
		Connection connect = null;
		Statement statement = null;
		
		if (tableName.equalsIgnoreCase(INST_TABLENAME) || tableName.equalsIgnoreCase(INVES_TABLENAME) 
				||tableName.equalsIgnoreCase(NODE_TABLENAME)){

			Reference ref = null;
			try {
				// Setup the connection with the DB
				connect = getConnection();
	
				statement = connect.createStatement();
	
				logger.debug("select * from " + tableName
												+ " order by " + TITLECOLUMN);
				
				resultSet = statement.executeQuery("select * from " + tableName
												+ " order by " + TITLECOLUMN);
	
				while (resultSet.next()) {
					ref = new Reference();
	
					ref.setLog_identifier(resultSet.getString(LOG_IDENTIFIERCOLUMN));
					ref.setReference(resultSet.getString(REFERENCECOLUMN));
					ref.setTitle(resultSet.getString(TITLECOLUMN));
					ref.setReferenceType(tableName);
					
					refs.add(ref);
				}
	
			} catch (Exception e) {
				logger.error(e);
			} finally {
				close(connect, statement);
			}
		}else{
			logger.error("Please check the reference table name: instrument_reference, investigation_reference or node_reference.");
		}
		return refs;
	}
	
	/**
	 * Product instrument/investigation/node Reference Query - Query the instrument_reference table for a list of instrument/investigation/node references for a product.
	 * @param identifier
	 * @param tableName
	 * @return  a list of product references for the logical identifier.
	 */
	public List<Reference> getProductReferences(String identifier, String tableName) {
		
		List<Reference> refs = new ArrayList<Reference>();
		
		Connection connect = null;
		Statement statement = null;
		
		if (tableName.equalsIgnoreCase(INST_TABLENAME) || tableName.equalsIgnoreCase(INVES_TABLENAME) 
				||tableName.equalsIgnoreCase(NODE_TABLENAME)){

			Reference ref = null;
			try {
				// Setup the connection with the DB
				connect = getConnection();
	
				statement = connect.createStatement();
	
				logger.debug("select * from " + tableName
												+ " where " + LOG_IDENTIFIERCOLUMN + " = '" + identifier
												+ "' order by " + TITLECOLUMN);
				
				resultSet = statement.executeQuery("select * from " + tableName
												+ " where " + LOG_IDENTIFIERCOLUMN + " = '" + identifier
												+ "' order by " + TITLECOLUMN);
	
				while (resultSet.next()) {
					ref = new Reference();
	
					ref.setLog_identifier(resultSet.getString(LOG_IDENTIFIERCOLUMN));
					ref.setReference(resultSet.getString(REFERENCECOLUMN));
					ref.setTitle(resultSet.getString(TITLECOLUMN));
					ref.setReferenceType(tableName);
					
					refs.add(ref);
				}
	
			} catch (Exception e) {
				logger.error(e);
			} finally {
				close(connect, statement);
			}
		}else{
			logger.error("Please check the reference table name: instrument_reference, investigation_reference or node_reference.");
		}
		return refs;
	}
	
	/**
	 * @param stm
	 */
	private void close(Connection connect, Statement stm) {
		try {
			if (resultSet != null) {
				resultSet.close();
			}

			if (stm != null) {
				stm.close();
			}

			if (connect != null) {
				connect.setAutoCommit(true);
				connect.close();				
			}
		} catch (Exception e) {
			logger.error(e);
		}
	}

	
	/**
	 * @param ref - Reference object
	 */
	public int insertReference(Reference ref) {
		
		int success = 0;
		
		Connection connect = null;
		PreparedStatement prepareStm = null;

		try {
			// Setup the connection with the DB
			connect = getConnection();
			connect.setAutoCommit(false);
			
			prepareStm = connect.prepareStatement("INSERT INTO " + ref.getReferenceType() + " (" + LOG_IDENTIFIERCOLUMN + ", "
																					+ REFERENCECOLUMN + ", "
																					+ TITLECOLUMN + ") VALUES (?, ?, ?)");
			prepareStm.setString(1, ref.getLog_identifier());
			prepareStm.setString(2, ref.getReference());
			prepareStm.setString(3, ref.getTitle());
			
			prepareStm.executeUpdate();
			
			connect.commit();
			logger.info("The reference, " + ref.getTitle() + ", for  " + ref.getLog_identifier() + " has been added in table: " + ref.getReferenceType() +".");
			success = 1;
			
		} catch (Exception e) {
			logger.error(e);
			if (connect != null) {
	            try {
	            	logger.error("Transaction is being rolled back");
	                connect.rollback();
	            } catch(SQLException excep) {
	            	logger.error(excep);
	            }
	        }
			success = 0;
	    } finally {
	        close(connect, prepareStm);
	    }

		return success;
	}
	
	/**
	 * @param ref - Reference object
	 */
	public Reference updateReference(Reference ref) {
		
		Reference updatedRef = null;
		Connection connect = null;
		PreparedStatement prepareStm = null;

		try {
			// Setup the connection with the DB
			connect = getConnection();
			connect.setAutoCommit(false);
			prepareStm = connect.prepareStatement("UPDATE " + ref.getReferenceType() + " SET " + TITLECOLUMN + " = ? "
														+ "WHERE " + LOG_IDENTIFIERCOLUMN + " = ? "
														+ "AND " + REFERENCECOLUMN + " = ?");
			
			prepareStm.setString(1, ref.getTitle());
			prepareStm.setString(2, ref.getLog_identifier());
			prepareStm.setString(3, ref.getReference());
			
			
			prepareStm.executeUpdate();
			
			connect.commit();
			logger.info("The reference for  " + ref.getTitle() + " has been updated in table " + ref.getReferenceType() + ".");
			updatedRef = ref;
			
		} catch (Exception e) {
			logger.error(e);
			if (connect != null) {
	            try {
	            	logger.error("Transaction is being rolled back");
	                connect.rollback();
	            } catch(SQLException excep) {
	            	logger.error(excep);
	            }
	        }
	    } finally {
	        close(connect, prepareStm);
	    }
		return updatedRef;
	}

}
