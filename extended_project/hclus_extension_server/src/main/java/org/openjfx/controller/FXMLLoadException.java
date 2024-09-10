package org.openjfx.controller;

/**
 * Eccezione personalizzata per la gestione degli errori
 * di caricamento dei file fxml.
 */
 class FXMLLoadException extends Exception {

  /**
   * Costruttore vuoto della classe FXMLLoadException.
   */
  FXMLLoadException() {
  }

  /**
   * Costruttore della classe FXMLLoadException.
   * 
   * @param message messaggio di errore
   */
  FXMLLoadException(String message) {
    super(message);
  }
}