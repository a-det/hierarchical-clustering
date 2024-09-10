package org.openjfx.conversion;

/**
 * Classe utile per la gestione delle eccezioni legate agli errori di lettura e
 * scrittura di file.
 */
public class ReadWriteFileException extends Exception {

  /**
   * Costruttore vuoto della classe ReadingFileException
   */
  ReadWriteFileException() {
  }

  /**
   * Costruttore con messaggio di errore della classe ReadingFileException
   * 
   * @param message messaggio di errore
   */
  ReadWriteFileException(String message) {
    super(message);
  }

}
