package org.openjfx.conversion;

/**
 * Classe utile per la gestione delle eccezioni legate al percorso
 * dei file in cui salvare il dendrogramma.
 */
public class InvalidPathException extends Exception {

  /**
   * Costruttore vuoto della classe InvalidPathException.
   */
  InvalidPathException() {
  }

  /**
   * Costruttore con messaggio di errore della classe InvalidPathException.
   * 
   * @param message messaggio di errore
   */
  InvalidPathException(String message) {
    super(message);
  }

}