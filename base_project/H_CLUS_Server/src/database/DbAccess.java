package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

//import javax.management.InstanceNotFoundException;

/**
 * Gestisce l'accesso al DB per la lettura dei dati di training
 * 
 * @author Map Tutor
 *
 */
public class DbAccess {

  private String DRIVER_CLASS_NAME = "com.mysql.cj.jdbc.Driver";
  private String DBMS = "jdbc:mysql";
  private String SERVER = "localhost";
  private String DATABASE;
  private Integer PORT = 3306;
  private String USER_ID;
  private String PASSWORD;

  private Connection conn;

  /**
   * Costruttore della classe.
   * @param currentDB nome del database corrente
   * @param currentUser nome dell'utente corrente
   * @param currentPassword password dell'utente corrente
   */
  public DbAccess(String currentDB, String currentUser, String currentPassword) {
    DATABASE = currentDB;
    USER_ID = currentUser;
    PASSWORD = currentPassword;
  }

  /**
   * Inizializza la connessione al database.
   *
   * @throws DatabaseConnectionException Eccezione lanciata se la connessione al
   *                                     database fallisce.
   */
  public void initConnection() throws DatabaseConnectionException {
    try {
      Class.forName(DRIVER_CLASS_NAME);
    } catch (ClassNotFoundException e) {
      System.out.println("[!] Driver not found: " + e.getMessage());
      throw new DatabaseConnectionException(e.toString());
    }
    String connectionString = DBMS + "://" + SERVER + ":" + PORT + "/" + DATABASE
        + "?user=" + USER_ID + "&password=" + PASSWORD + "&serverTimezone=UTC";

    try {
      conn = DriverManager.getConnection(connectionString);
    } catch (SQLException e) {

      throw new DatabaseConnectionException(e.toString());
    }
  }

  /**
   * Restituisce la connessione al database.
   *
   * @return Connessione al database.
   * @throws DatabaseConnectionException Eccezione lanciata se la connessione al
   *                                     database fallisce.
   */
  Connection getConnection() throws DatabaseConnectionException {
    this.initConnection();
    return conn;
  }

  /**
   * Chiude la connessione al database.
   *
   * @throws SQLException Eccezione lanciata se si verifica un errore durante la
   *                      chiusura della connessione.
   */
  public void closeConnection() throws SQLException {
    conn.close();
  }

}
