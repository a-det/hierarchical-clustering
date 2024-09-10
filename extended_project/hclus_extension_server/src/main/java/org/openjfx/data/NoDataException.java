package org.openjfx.data;

/**
 * Classe che gestisce il caso in cui le tabelle del database siano vuote,
 * inesistenti o abbiano dati non numerici.
 */
public class NoDataException extends Exception {

  /**
   * Costruttore di default della classe NoDataException.
   */
  NoDataException() {
  }

  /**
   * Costruttore con messaggio della classe NoDataException.
   * 
   * @param message Messaggio di errore.
   */
  NoDataException(String message) {
    super(message);
  }

}
