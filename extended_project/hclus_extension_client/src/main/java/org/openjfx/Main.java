package org.openjfx;

/**
 * La classe Main e' l'entry point dell'applicazione.
 * L'esistenza di questa classe e' dovuta ad una particolare
 * interazione tra Gradle e JavaFX, che richiede che l'entry point
 * sia in una classe che non estenda Application.
 */
public class Main {

  /**
   * Il metodo main() avvia la GUI richiamando il metodo main() della classe
   * ClientGUI.
   * 
   * @param args Argomenti passati da riga di comando
   */
  public static void main(String[] args) {
    ClientGUI.main(args);
  }
}