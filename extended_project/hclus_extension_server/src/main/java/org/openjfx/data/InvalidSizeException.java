package org.openjfx.data;

/**
 * Classe che rappresenta l'eccezione lanciata nel caso in cui si provi a
 * calcolare la distanza tra due esempi di lunghezza differente.
 */
public class InvalidSizeException extends Exception {

  /**
   * Costruttore di default
   */
  public InvalidSizeException() {
  }

  /**
   * Costruttore che prende in input un messaggio da stampare
   * 
   * @param message messaggio da stampare
   */
  public InvalidSizeException(String message) {
    super(message);
  }

}
