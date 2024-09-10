package database;

/**
 * Classe che modella l'eccezione che viene sollevata nel caso in cui una
 * tabella letta dal database non contenga alcuna entry.
 */
public class EmptySetException extends Exception {

  /**
   * Costruttore di default della classe EmptySetException.
   */
  EmptySetException() {
  }

  /**
   * Costruttore con messaggio della classe EmptySetException.
   * 
   * @param message messaggio di errore
   */    
  EmptySetException(String message) {
    super(message);
  }
    
}
