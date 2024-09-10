package org.openjfx;

/**
 * La classe Main e' l'entry point dell'applicazione.
 * L'esistenza di questa classe e' dovuta a una particolare
 * interazione tra Gradle e JavaFX, che richiede che l'entry point
 * sia in una classe che non estenda Application.
 */
public class Main {

  /**
   * Il metodo main() avvia la GUI richiamando il metodo main() della classe
   * ServerGUI.
   * 
   * @param args Argomenti passati da riga di comando
   */
  public static void main(String[] args) {
    ServerGUI.main(args);
  }
}