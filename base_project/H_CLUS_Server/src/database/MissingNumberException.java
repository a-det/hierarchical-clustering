package database;

/**
 * Classe che modella l'eccezione che viene sollevata nel caso in cui
 * all'interno della tabella del databse siano presenti dei valori non numerici.
 */
public class MissingNumberException extends Exception {

  /**
   * Costuttore di default della classe MissingNumberException.
   */
  MissingNumberException() {
  }

  /**
   * Costruttore con messaggio della classe MissingNumberException.
   * 
   * @param message messaggio di errore
   */
  MissingNumberException(String message) {
    super(message);
  }

}
