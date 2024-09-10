package clustering;

/**
 * Classe che rappresenta l'eccezione lanciata quando il percorso
 * specificato non risulta essere valido.
 */
public class InvalidPathException extends Exception {

  /**
   * Costruttore vuoto della classe InvalidPathException
   */
  InvalidPathException() {
  }

  /**
   * Costruttore della classe InvalidPathException
   * 
   * @param message messaggio di errore
   */
  InvalidPathException(String message) {
    super(message);
  }
}