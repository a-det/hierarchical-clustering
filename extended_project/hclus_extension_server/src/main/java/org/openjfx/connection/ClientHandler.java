package org.openjfx.connection;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.ServerSocket;
import java.io.IOException;
import org.openjfx.controller.LogController;

/**
 * Classe che raggruppa tutti gli oggetti utili a stabilire la connessione con
 * il client e gestirne la connessione con il client.
 */
public class ClientHandler implements Runnable {

  private ServerSocket serverSocket;
  private LogController logController;
  private Socket communicationSocket;
  private ObjectInputStream in;
  private ObjectOutputStream out;

  // unica istanza della classe singleton ClientHandler
  private static ClientHandler instance;

  /**
   * Costruttore privato della classe ClientHandler
   * 
   * @param serverSocket  socket del server per la connessione con il client
   * @param logController controller per la gestione dei log
   */
  private ClientHandler(ServerSocket serverSocket, LogController logController) {
    this.serverSocket = serverSocket;
    this.logController = logController;
  }

  /**
   * Instanzia l'unica istanza della classe singleton ClientHandler richiamando il
   * costruttore privato e la restituisce
   * 
   * @param serverSocket  socket del server per la connessione con il client
   * @param logController controller per la gestione dei log
   * @return l'unica istanza della classe singleton ClientHandler
   */
  static ClientHandler getInstance(ServerSocket serverSocket, LogController logController) {
    if (instance == null) {
      instance = new ClientHandler(serverSocket, logController);
    }
    return instance;
  }

  /**
   * Restituisce l'unica istanza della classe singleton ClientHandler
   * 
   * @return l'unica istanza della classe singleton ClientHandler
   */
  public static ClientHandler getInstance() {
    return instance;
  }

  /**
   * Implementa la logica del thread che rimane in attesa di una
   * richiesta di connessione da un client, gestendola ed
   * inizializzando i relativi stream di input e output nel socket di
   * comunicazione.
   */
  @Override
  public void run() {
    try {
      logController.log("In attesa della connessione di un Client ...");
      communicationSocket = serverSocket.accept();
      logController.log("Client connesso");

      try {
        in = new ObjectInputStream(communicationSocket.getInputStream());
        logController.log("Stream di input stabilito");
        out = new ObjectOutputStream(communicationSocket.getOutputStream());
        logController.log("Stream di output stabilito");
      } catch (IOException e) {
        throw new ObjectIOStreamException();
      }
    } catch (IOException e) {
      logController.log("Errore durante la gestione del client: " + e.getMessage());
    } catch (ObjectIOStreamException e) {
      logController.log("Errore durante la creazione degli stream di input e output");
    }
  }

  /**
   * Chiude il socket di comunicazione con il client ed i relativi
   * stream di input e output.
   */
  public void closeCommunicationSocket() {
    try {
      if (in != null)
        in.close();
      if (out != null)
        out.close();
      if (communicationSocket != null && !communicationSocket.isClosed())
        communicationSocket.close();
      logController.log("Chiusa la connessione con il client");
    } catch (IOException e) {
      logController.log("Errore nella chiusura della connessione con il client");
    }
  }

  /**
   * Restituisce lo stream di input del socket di comunicazione con il
   * client.
   * 
   * @return stream di input del socket di comunicazione con il client
   */
  ObjectInputStream getIn() {
    return in;
  }

  /**
   * Restituisce lo stream di output del socket di comunicazione con il
   * client.
   * 
   * @return stream di output del socket di comunicazione con il client
   */
  ObjectOutputStream getOut() {
    return out;
  }

  /**
   * Avvia il thread che gestisce la connessione con il client.
   */
  void start() {
    new Thread(this).start();
  }
}
