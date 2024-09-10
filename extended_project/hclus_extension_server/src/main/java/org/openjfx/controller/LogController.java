package org.openjfx.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import org.openjfx.connection.ServerMain;
import org.openjfx.connection.ClientHandler;
import javafx.stage.Stage;

/**
 * LogController e' il controller per la schermata che mostra il log
 * delle operazioni eseguite dal server, una volta che il server viene
 * avviato dall'utente.
 */
public class LogController {

  // Componenti della schermata di log
  @FXML
  private TextArea logArea;

  @FXML
  private Button stopButton;

  // Coda che contiene i messaggi da mostrare nel log
  private final BlockingQueue<String> logQueue = new LinkedBlockingQueue<>();

  // Flag che assicura la gestione thread safe per il thread del log
  private volatile boolean running = true;

  // Thread che si occupa di gestire il log
  private Thread logThread;

  /**
   * Il metodo initialize() viene chiamato automaticamente per inizializzare
   * il controller dopo che il file fxml e' stato caricato.
   * Imposta il listener per il pulsante stopButton.
   */
  public void initialize() {
    // public in quanto viene richiamato automaticamente da javafx
    // al momento del caricamento del file fxml
    stopButton.setOnAction(event -> shutdown());

    // Imposta la corretta chiusura dell'applicazione
    // anche premendo il tasto [X] della finestra
    setClose();

    // Starta il thread per il log
    logThread = new Thread(this::processLogQueue);
    logThread.start();
  }

  /**
   * Imposta la chiusura dell'applicazione anche premendo il tasto [X]
   * della finestra.
   */
  private void setClose() {
    Platform.runLater(() -> {
      Stage stage = (Stage) stopButton.getScene().getWindow();
      stage.setOnCloseRequest(event -> shutdown());
    });
  }

  /**
   * Inserisce un nuovo messaggio nella coda del log.
   * 
   * @param message Messaggio da inserire nel log.
   */
  public void log(String message) {
    logQueue.offer(message);
  }

  /**
   * Processa la coda del log, aggiungendo i messaggi nella schermata
   * dell'applicazione.
   * 
   * @throws InterruptedException Se il thread viene interrotto mentre si trova
   *                              nello stato d'attesa.
   */
  private void processLogQueue() {
    while (running) {
      try {
        String message = logQueue.take();
        Platform.runLater(() -> logArea.appendText(message + "\n"));
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
        break;
      }
    }
  }

  /**
   * Si occupa di chiudere correttamente
   * il server, terminando i thread in esecuzione,
   * i socket aperti e l'applicazione grafica.
   */
  public void shutdown() {
    log("Processo di chiusura in corso...");

    ClientHandler.getInstance().closeCommunicationSocket(); // Chiude il socket di connessione con il client

    try {
      ServerMain.getInstance().closeServerSocket(); // Chiude il serverSocket
    } catch (IOException e) {
      log("Errore durante la chiusura del serverSocket");
    }

    ServerMain.getInstance().stopRunning(); // Stoppa il thread che gestisce le operazioni del server

    running = false; // Segnale di stop al thread del log
    logThread.interrupt(); // Assicura che il thread del log venga interrotto in caso stesse
                           // eseguendo operazioni bloccanti (es. take())

    Platform.exit(); // Chiude la GUI
  }

}