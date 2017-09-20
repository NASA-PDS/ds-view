package gov.nasa.pds.tracking.tracking.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

public class CertificationStatus extends DBConnector {
	
	public static Logger logger = Logger.getLogger(CertificationStatus.class);

	private static final String TABLENAME = "certification_status";
	private static final String PRODUCTTABLENAME= "product";

	public static final String LOGIDENTIFIERCOLUME = "logical_identifier";
	public static final String VERSIONCOLUME = "version_id";
	public static final String DATECOLUME = "status_date_time";
	public static final String STATUSCOLUME = "status";
	public static final String EMAILCOLUME = "electronic_mail_address";
	public static final String COMMENTCOLUME = "comment";

	private static Connection connect = null;
	private static Statement statement = null;
	private static PreparedStatement prepareStm = null;
	private static ResultSet resultSet = null;

	private String logIdentifier = null;
	private String version = null;
	private String date = null;
	private String status = null;
	private String email = null;
	private String comment = null;
	
	/**
	 * @return the logIdentifier
	 */
	public String getLogIdentifier() {
		return logIdentifier;
	}

	/**
	 * @param logIdentifier, the logIdentifier to set
	 */
	public void setLogIdentifier(String logIdentifier) {
		this.logIdentifier = logIdentifier;
	}

	/**
	 * @return the version
	 */
	public String getVersion() {
		return version;
	}

	/**
	 * @param version, the version to set
	 */
	public void setVersion(String version) {
		this.version = version;
	}

	/**
	 * @return the date
	 */
	public String getDate() {
		return date;
	}

	/**
	 * @param date, the date to set
	 */
	public void setDate(String date) {
		this.date = date;
	}

	/**
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * @param status, the status to set
	 */
	public void setStatus(String status) {
		this.status = status;
	}

	/**
	 * @return the email
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * @param email, the email to set
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * @return the comment
	 */
	public String getComment() {
		return comment;
	}

	/**
	 * @param comment, the comment to set
	 */
	public void setComment(String comment) {
		this.comment = comment;
	}
	
	public CertificationStatus() throws ClassNotFoundException, SQLException {
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * @param logical_identifier
	 * @param version
	 * @param date
	 * @param status
	 * @param mail
	 * @param comment
	 */
	public void insertCertificationStatus(String logical_identifier, String version, String date, String status, String mail, String comment) {
		try {
			// Setup the connection with the DB
			connect = DriverManager.getConnection(db_url, db_user, db_pwd);
			connect.setAutoCommit(false);
			
			prepareStm = connect.prepareStatement("INSERT INTO " + TABLENAME + " (" 
													+ LOGIDENTIFIERCOLUME + ", " 
													+ VERSIONCOLUME + ", "
													+ DATECOLUME + ", "
													+ STATUSCOLUME + ", "
													+ EMAILCOLUME + ", "
													+ COMMENTCOLUME + ") VALUES (?, ?, ?, ?, ?, ?)");
			prepareStm.setString(1, logical_identifier);
			prepareStm.setString(2, version);
			prepareStm.setString(3, date);
			prepareStm.setString(4, status);
			prepareStm.setString(5, email);
			prepareStm.setString(6, comment);
			
			prepareStm.executeUpdate();
			
			connect.commit();
			logger.info("The Certification Status status " + status + " for the product: " + logical_identifier + ", has been added.");
			
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
	        close(prepareStm);
	    }
	}
	
	/**
	 * @param logical_identifier
	 * @param ver
	 * @return
	 */
	@SuppressWarnings("finally")
	public static CertificationStatus getLatestCertificationStatus(String logical_identifier, String ver) {

		CertificationStatus certifStatus = null;
		try {
			// Setup the connection with the DB
			connect = DriverManager.getConnection(db_url, db_user, db_pwd);

			statement = connect.createStatement();
			
			resultSet = statement.executeQuery("select * from " + TABLENAME 
					+ " where " + LOGIDENTIFIERCOLUME + " = '" + logical_identifier + "' and "
					+ VERSIONCOLUME + " = '" + ver + "' order by " + DATECOLUME);

			if (resultSet.next()){
				certifStatus = new CertificationStatus();

				certifStatus.setLogIdentifier(resultSet.getString(LOGIDENTIFIERCOLUME));
				certifStatus.setVersion(resultSet.getString(VERSIONCOLUME));
				certifStatus.setStatus(resultSet.getString(STATUSCOLUME));
				certifStatus.setEmail(resultSet.getString(EMAILCOLUME));
				certifStatus.setComment(resultSet.getString(COMMENTCOLUME));
				certifStatus.setDate(resultSet.getString(DATECOLUME));
				
			}	

		} catch (Exception e) {
			logger.error(e);
		} finally {
			close(statement);
			return certifStatus;
		}
	}
	
	/**
	 * @param logical_identifier
	 * @param ver
	 * @return
	 */
	@SuppressWarnings("finally")
	public static List<CertificationStatus> getCertificationStatusList(String logical_identifier, String ver) {
		
		List<CertificationStatus> certifStatuses = new ArrayList<CertificationStatus>();
		CertificationStatus certifStatus = null;
		try {
			// Setup the connection with the DB
			connect = DriverManager.getConnection(db_url, db_user, db_pwd);

			statement = connect.createStatement();
			
			resultSet = statement.executeQuery("select * from " + TABLENAME 
					+ " where " + LOGIDENTIFIERCOLUME + " = '" + logical_identifier + "' and "
					+ VERSIONCOLUME + " = '" + ver + "' order by " + DATECOLUME + " DESC");

			while (resultSet.next()){
				certifStatus = new CertificationStatus();

				certifStatus.setLogIdentifier(resultSet.getString(LOGIDENTIFIERCOLUME));
				certifStatus.setVersion(resultSet.getString(VERSIONCOLUME));
				certifStatus.setStatus(resultSet.getString(STATUSCOLUME));
				certifStatus.setEmail(resultSet.getString(EMAILCOLUME));
				certifStatus.setComment(resultSet.getString(COMMENTCOLUME));
				certifStatus.setDate(resultSet.getString(DATECOLUME));
				
				certifStatuses.add(certifStatus);
			}	

		} catch (Exception e) {
			logger.error(e);
		} finally {
			close(statement);
			return certifStatuses;
		}
	}
	
	/**
	 * @param stm
	 */
	private static void close(Statement stm) {
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
}
