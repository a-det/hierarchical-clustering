package org.openjfx.connection;

import java.util.Map;
import java.util.TreeMap;
import java.net.ServerSocket;
import javafx.concurrent.Task;

import org.openjfx.database.DbAccess;
import org.openjfx.data.Data;
import org.openjfx.conversion.JsonDendrogram;
import org.openjfx.controller.LogController;
import org.openjfx.distance.SingleLinkDistance;
import org.openjfx.distance.AverageLinkDistance;

import java.io.IOException;
import java.io.EOFException;
import java.sql.SQLException;
import java.lang.ClassNotFoundException;
import org.openjfx.data.NoDataException;
import org.openjfx.data.InvalidSizeException;
import org.openjfx.conversion.InvalidPathException;
import org.openjfx.clustering.InvalidDepthException;
import org.openjfx.conversion.ReadWriteFileException;
import org.openjfx.clustering.HierarchicalClusterMiner;
import org.openjfx.database.DatabaseConnectionException;

/**
 * Classe singoletto che gestisce le operazioni eseguite dal server.
 */
public class ServerMain {

  private ServerSocket serverSocket;
  private ClientHandler clientHandler;
  private volatile boolean running = true;

  // unica istanza della classe
  private static ServerMain instance;

  /**
   * Costruttore privato della classe singoletto ServerMain.
   */
  private ServerMain() {
  }

  /**
   * Restituisce l'unica istanza della classe singoletto ServerMain.
   * 
   * @return l'unica istanza della classe singoletto ServerMain
   */
  public static ServerMain getInstance() {
    if (instance == null) {
      instance = new ServerMain();
    }
    return instance;
  }

  /**
   * Imposta lo stato dell'esecuzione a falso.
   */
  public void stopRunning() {
    getInstance().running = false;
  }

  /**
   * Restituisce il server socket.
   * 
   * @return server socket
   */
  public ServerSocket getServerSocket() {
    return getInstance().serverSocket;
  }

  /**
   * Apre il server socket sulla porta specificata.
   * 
   * @param port porta su cui aprire il server socket
   * @return stringa di conferma dell'apertura del server socket
   * @throws IOException se si verifica un errore durante l'apertura del socket
   */
  public String openServerSocket(Integer port) throws IOException {
    getInstance().serverSocket = new ServerSocket(port);
    return "Server Socket aperto\n" + getInstance().serverSocket.toString();
  }

  /**
   * Esegue le operazioni effetive del server durante l'intera comunicazione di
   * quest'ultimo con il client.
   * 
   * @param logController controller per aggiornare il log
   */
  public void executeServerOperations(LogController logController) {
    logController.log("Inizializzazione operazioni del server...");

    // Crea un task per eseguire le operazioni del server
    Task<Void> serverTask = new Task<Void>() {
      @Override
      protected Void call() throws Exception {
        try {
          clientHandler = ClientHandler.getInstance(serverSocket, logController);
          clientHandler.start();
          Data data = null;

          data = handleDatabaseOperations(logController);

          handleClientChoices(logController, data);

          Thread.yield();

        } catch (EOFException e) {
          logController.log("Client disconnesso prematuramente");
          ClientHandler.getInstance().closeCommunicationSocket();
        } catch (ClassNotFoundException e) {
          logController.log("Errore nella lettura degli oggetti.");
          logController.log("Riavviare l'applicazione");
          ClientHandler.getInstance().closeCommunicationSocket();
        } catch (IOException e) {
          logController.log("Errore nell'operazione di I/O");
          logController.log("Riavviare l'applicazione");
          ClientHandler.getInstance().closeCommunicationSocket();
        } catch (InvalidSizeException e) {
          logController.log("Errore: dimensione degli esempi del database non valida");
          clientHandler.getOut().writeObject("Errore: dimensione degli esempi del database non valida");
        }
        return null;
      }
    };
    // Esegue la Task in un thread
    new Thread(serverTask).start();
  }

  /**
   * Gestisce le operazioni di connessione al database.
   * 
   * @param logController controller per aggiornare il log
   * @return i dati caricati dal database
   * @throws IOException            se si verifica un caso anomalo durante la
   *                                comunicazione
   * @throws ClassNotFoundException se si verifica un errore nella lettura degli
   *                                oggetti
   */
  private Data handleDatabaseOperations(LogController logController) throws IOException, ClassNotFoundException {
    DbAccess db = null;
    Data data = null;
    while (getInstance().running) {
      if (getInstance().clientHandler.getIn() != null && clientHandler.getOut() != null) {

        String dbName = (String) clientHandler.getIn().readObject();
        String username = (String) clientHandler.getIn().readObject();
        String password = (String) clientHandler.getIn().readObject();
        String tableName = (String) clientHandler.getIn().readObject();

        logController.log("Tentativo di connessione al database " + dbName + " con username " + username);

        boolean flag = false;
        try {
          db = new DbAccess(dbName, username, password);
          db.initConnection();
          logController.log("Connessione al database riuscita");
        } catch (DatabaseConnectionException e) {
          logController.log("Errore: Connessione al database non riuscita. Riprova");
          clientHandler.getOut().writeObject("Errore: Connessione al database non riuscita. Riprova");
          flag = true;
        }

        if (!flag) {
          logController.log("Tentativo di caricare la tabella richiesta: " + tableName);
          try {
            data = new Data(tableName, db);
            logController.log("Tabella caricata correttamente");
            clientHandler.getOut().writeObject("Dati tabella caricati correttamente");
            db.closeConnection();
            logController.log("Chiusura della connessione con il database.");
            break;
          } catch (NoDataException e) {
            logController.log("Errore: " + e.getMessage());
            clientHandler.getOut().writeObject("Errore: " + e.getMessage());
          } catch (DatabaseConnectionException e) {
            logController.log("Errore: Connessione al database non riuscita. Riprova");
            clientHandler.getOut().writeObject("Errore: Connessione al database non riuscita. Riprova");
          } catch (SQLException e) {
            logController.log(
                "Errore nella chiusura della connessione con il database.");
            logController.log("E' comunque possibile utilizzare la tabella precedentemente caricata.");
          }
        }
      }
    }
    return data;
  }

  /**
   * Gestisce le scelte del client.
   * 
   * @param logController controller per aggiornare il log
   * @param data          dati da utilizzare per la creazione del dendrogramma
   * @throws InvalidSizeException   se la dimensione degli esempi non e' valida
   * @throws IOException            se si verifica un errore di I/O
   * @throws ClassNotFoundException se si verifica un errore nella lettura degli
   *                                oggetti
   */
  private void handleClientChoices(LogController logController, Data data)
      throws InvalidSizeException, IOException, ClassNotFoundException {
    while (getInstance().running) {
      Integer choice = (Integer) clientHandler.getIn().readObject();
      if (choice == 2) {
        handleFileLoad(logController);
      } else if (choice == 1) {
        handleDendrogramOperation(logController, data);
      }

      Integer close = (Integer) clientHandler.getIn().readObject();
      if (close == 3)
        break;
    }
  }

  /**
   * Gestisce le operazioni di caricamento da file.
   * 
   * @param logController controller per aggiornare il log
   * @throws IOException            se si verifica un errore di I/O
   * @throws ClassNotFoundException se si verifica un errore nella lettura degli
   *                                oggetti
   */
  private void handleFileLoad(LogController logController) throws IOException, ClassNotFoundException {
    try {
      String percorso = (String) getInstance().clientHandler.getIn().readObject();
      logController.log("Tentativo di caricamento del file in " + percorso);
      JsonDendrogram hcm = JsonDendrogram.loadJsonDendrogram(percorso);
      logController.log("File caricato correttamente");
      clientHandler.getOut().writeObject("OK");
      logController.log("Invio del Json e della profondita' al client ...");
      clientHandler.getOut().writeObject(hcm.getJson());
      logController.log("Json inviato correttamente");
      clientHandler.getOut().writeObject(hcm.getDepth());
      logController.log("Profondita' inviata correttamente");
    } catch (InvalidPathException e) {
      logController.log("Errore: percorso non valido");
      clientHandler.getOut().writeObject("Errore: percorso non valido");
    } catch (ReadWriteFileException e) {
      logController.log(e.getMessage());
      clientHandler.getOut().writeObject(e.getMessage());
    }
  }

  /**
   * Gestisce le operazioni di creazione del dendrogramma.
   * 
   * @param logController controller per aggiornare il log
   * @param data          dati da utilizzare per la creazione del dendrogramma
   * @throws IOException            se si verifica un errore di I/O
   * @throws ClassNotFoundException se si verifica un errore nella lettura degli
   *                                oggetti
   * @throws InvalidSizeException   se la dimensione degli esempi non e' valida
   */
  private void handleDendrogramOperation(LogController logController, Data data)
      throws IOException, ClassNotFoundException, InvalidSizeException {
    try {
      Integer depth = (Integer) getInstance().clientHandler.getIn().readObject();
      logController.log("Profondita' ricevuta: " + depth);
      Integer dChoice = (Integer) clientHandler.getIn().readObject();
      logController.log("Scelta ricevuta: " + dChoice);

      HierarchicalClusterMiner.checkDepth(depth, data.getNumberOfExample());
      logController.log("Profondita' valida");
      HierarchicalClusterMiner hcm = new HierarchicalClusterMiner(data.getNumberOfExample());
      logController.log("Costruzione del dendrogramma in corso ...");
      Map<Integer, Map<String, String>> minedDendrogram = new TreeMap<>();

      if (dChoice == 1) {
        minedDendrogram = hcm.mine(data, new SingleLinkDistance());
      } else {
        minedDendrogram = hcm.mine(data, new AverageLinkDistance());
      }

      logController.log("Dendrogramma costruito correttamente");

      clientHandler.getOut().writeObject("OK");

      saveAndSendDendrogram(minedDendrogram, depth, logController);

    } catch (InvalidDepthException e) {
      clientHandler.getOut()
          .writeObject("Errore: dimensione del dendrogramma non valida. Inserire un intero compreso tra 0 e "
              + data.getNumberOfExample());
      logController.log("Errore: dimensione del dendrogramma non valida");
    }
  }

  /**
   * Salva il dendrogramma e lo invia al client.
   * 
   * @param minedDendrogram dendrogramma da salvare e inviare
   * @param depth           profondita' del dendrogramma
   * @param logController   controller per aggiornare il log
   * @throws IOException            se si verifica un errore di I/O
   * @throws ClassNotFoundException se si verifica un errore nella lettura degli
   *                                oggetti
   */
  private void saveAndSendDendrogram(Map<Integer, Map<String, String>> minedDendrogram, Integer depth,
      LogController logController) throws IOException, ClassNotFoundException {

    try {
      Map<String, Object> dendrogram = JsonDendrogram.buildDendrogram(minedDendrogram);
      JsonDendrogram jsonDendrogram = new JsonDendrogram(dendrogram, depth);

      logController.log("Lettura del percorso di salvataggio scelto dal client");
      String path = (String) getInstance().clientHandler.getIn().readObject();

      jsonDendrogram.saveJsonDendrogram(path);
      logController.log("Dendrogramma salvato corretttamnte");
      clientHandler.getOut().writeObject(jsonDendrogram.getJson());
      logController.log("Dendrogramma inviato al client");
    } catch (ReadWriteFileException e) {
      logController.log("Errore nel creare il file");
      clientHandler.getOut().writeObject("Errore nel creare il file");
    } catch (InvalidPathException e) {
      logController.log("Errore: percorso non valido");
      clientHandler.getOut().writeObject("Errore: percorso non valido");
    }

  }

  /**
   * Chiude il server socket.
   * 
   * @throws IOException se si verifica un errore nella chiusura del server socket
   */
  public void closeServerSocket() throws IOException {
    getInstance().serverSocket.close();
  }

}
