package org.openjfx.connection;

/**
 * Classe utile per la gestione delle eccezioni legate alle istanziazioni degli
 * stream di
 * input e output del socket per la connessione tra client e server.
 */
class ObjectIOStreamException extends Exception {

  /**
   * Costruttore vuoto della classe ObjectIOStreamException.
   */
  ObjectIOStreamException() {
  }

  /**
   * Costruttore della classe ObjectIOStreamException.
   * 
   * @param message messaggio di errore
   */
  ObjectIOStreamException(String message) {
    super(message);
  }

}