package clustering;

/**
 * Eccezione lanciata quando si cerca di creare un cluster con un valore di profondita' non valido.
 */
public class InvalidDepthException extends Exception {

  /**
   * Costruttore di default della classe InvalidDepthException
   */
  InvalidDepthException() {
  }

  /**
   * Costruttore che accetta un messaggio di errore della classe InvalidDepthException
   * @param message Messaggio di errore
   */
  InvalidDepthException(String message) {
    super(message);
  }

}