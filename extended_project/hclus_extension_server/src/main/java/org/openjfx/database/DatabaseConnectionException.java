package org.openjfx.database;

/**
 * Classe che rappresenta l'eccezione che viene sollevata nel caso in cui
 * non sia possibile stabilire una connessione con il database.
 */
public class DatabaseConnectionException extends Exception {

  /**
   * Costruttore di default della classe DatabaseConnectionException.
   */
  DatabaseConnectionException() {
  }

  /**
   * Costruttore della classe DatabaseConnectionException.
   * 
   * @param message Messaggio di errore.
   */
  DatabaseConnectionException(String message) {
    super(message);
  }

}
