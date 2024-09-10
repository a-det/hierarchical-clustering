import data.Data;
import java.net.Socket;
import java.sql.SQLException;

import utility.Keyboard;
import database.DbAccess;
import java.net.ServerSocket;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import distance.SingleLinkDistance;
import distance.AverageLinkDistance;
import clustering.HierarchicalClusterMiner;

import java.io.IOException;
import data.NoDataException;
import data.InvalidSizeException;
import java.io.FileNotFoundException;
import clustering.InvalidDepthException;
import database.DatabaseConnectionException;
import clustering.InvalidPathException;

/**
 * Classe che rappresenta il server dell'applicazione e contiene tutte le
 * operazioni che questo deve eseguire per effettuare l'operazzione di
 * clustering
 * agglomerativo.
 */
public class ServerMain {

  /**
   * Metodo principale della classe ServerMain che si occupa di gestire la
   * connessione con il client e di eseguire l'operazione di clustering
   * agglomerativo.
   * 
   * @param args argomenti passati da riga di comando
   */
  public static void main(String[] args) {

    Data data = null;
    DbAccess db = null;

    ServerMain serverMain = new ServerMain();
    ServerSocket serverSocket = serverMain.askServerPort();
    System.out.println("Server socket aperto correttamente");

    try {
      Socket socket = serverSocket.accept();
      System.out.println("Connessione con il client stabilita: " + socket);

      ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
      System.out.println("Stream di input aperto");
      ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
      System.out.println("Stream di output aperto");

      try {
        db = serverMain.handleDatabaseConnection(in, out);

        data = serverMain.askForTable(in, out, db);

        Integer choice = (Integer) in.readObject();

        if (choice == 2) {
          serverMain.load(in, out, data);
        } else {
          serverMain.create(in, out, data);
        }
      } catch (ClassNotFoundException e) {
        e.printStackTrace();
      } catch (InvalidSizeException e) {
        out.writeObject("Errore: esempi di differente dimensione nel dataset.");
      } finally {
        socket.close();
      }
    } catch (IOException e) {
      System.out.println("Errore nella comunicazione con il client.");
    } finally {
      try {
        serverSocket.close();
        System.out.println("Chiuso " + serverSocket);
      } catch (IOException e) {
        System.out.println("Errore nella chiusura del server socket.");
      }
    }
  }

  /**
   * Metodo che chiede all'utente di inserire la porta sulla quale eseguire il
   * server.
   * 
   * @return porta su cui eseguire il server
   */
  private ServerSocket askServerPort() {

    while (true) {
      try {
        System.out.print("Inserire la porta del server: ");
        Integer port = 0;
        while (true) {
          port = Keyboard.readInt();
          if (port < 1024 || port > 65535) {
            System.out.println("Errore: inserire un numero di porta compreso tra 1024 e 65535.");
            System.out.print("Inserire la porta del server: ");
          } else {
            return new ServerSocket(port);
          }
        }
      } catch (IOException e) {
        System.out.println("Errore nell'apertura del server socket. Riprovare inserendo una porta differente.");
      }
    }

  }

  /**
   * Metodo che si occupa di gestire la connessione al database dal quale leggere
   * il dataset di esempi.
   * 
   * @param in  stream di input per ricevere i dati dal client
   * @param out stream di output per inviare i dati al client
   * @return oggetto che modella la connessione al database
   * @throws IOException nel caso in cui si verifichi un errore nella
   *                     comunicazione con il client
   */
  private DbAccess handleDatabaseConnection(ObjectInputStream in, ObjectOutputStream out) throws IOException {
    boolean isAuthenticated = false;
    DbAccess db = null;
    String dbName = null;
    String username = null;
    String password = null;

    while (!isAuthenticated) {
      try {
        dbName = (String) in.readObject();
        username = (String) in.readObject();
        password = (String) in.readObject();
        System.out.println("Tentativo di accesso al database di: " + username);

        db = new DbAccess(dbName, username, password);
        System.out.println("Connessione al database...");
        db.initConnection();
        System.out.println("Connessione al database riuscita");
        isAuthenticated = true;
        out.writeObject("OK");
      } catch (ClassNotFoundException e) {
        System.out.println("Errore: riferimento a classi non trovate.");
        out.writeObject("Errore: riferimento a classi non trovate.");
      } catch (DatabaseConnectionException e) {
        System.out.println("Errore: Connessione al database non riuscita. Riprova.");
        out.writeObject("Errore: Connessione al database non riuscita. Riprova.");
      } catch (IOException e) {
        System.out.println("Errore: Connessione al database non riuscita. Riprova.");
        out.writeObject("Errore: Connessione al database non riuscita. Riprova.");
      }
    }
    return db;
  }

  /**
   * Metodo che si occupa di chiedere al client il nome della tabella da cui
   * estrarre il dataset di esempi e di caricarne il contenuto.
   * 
   * @param in  stream di input per ricevere i dati dal client
   * @param out stream di output per inviare i dati al client
   * @param db  oggetto che modella la connessione al database
   * @return dataset di esempi letti dal database
   * @throws IOException            nel caso in cui si verifichi un errore nella
   *                                comunicazione con il client
   * @throws ClassNotFoundException nel caso in cui si referenzi una classe non
   *                                trovata
   */
  private Data askForTable(ObjectInputStream in, ObjectOutputStream out, DbAccess db)
      throws IOException, ClassNotFoundException {
    Data data = null;
    while (true) {
      in.readObject();
      String tablename = (String) in.readObject();
      System.out.println("Nome della tabella ricevuto: " + tablename);
      try {
        data = new Data(tablename, db);
        System.out.println("Dati tabella caricati correttamente.");
        out.writeObject("OK");
        try {
          db.closeConnection();
        } catch (SQLException e) {
          System.out.println("Errore nella chiusura della connessione con il database.");
          System.out.println("E' comunque possibile utilizzare la tabella precedentemente caricata.");
        }
        break;
      } catch (NoDataException e) {
        System.out.println(e.getMessage());
        out.writeObject(e.getMessage());
      } catch (DatabaseConnectionException e) {
        System.out.println("Errore nella connessione al database.");
        out.writeObject("Errore nella connessione al database.");
      }
    }
    return data;
  }

  /**
   * Metodo che si occupa di caricare un dendrogramma precedentemente salvato.
   * 
   * @param in   stream di input per ricevere i dati dal client
   * @param out  stream di output per inviare i dati al client
   * @param data dataset di esempi letti dal database
   * @throws IOException nel caso in cui si verifichi un errore nella
   *                     comunicazione con il client
   */
  private void load(ObjectInputStream in, ObjectOutputStream out, Data data) throws IOException {
    Boolean flag = true;
    while (true) {
      try {
        if (!flag) {
          in.readObject();
        } else {
          flag = false;
        }
        String path = (String) in.readObject();
        HierarchicalClusterMiner hcm = HierarchicalClusterMiner.loadHierarchicalClusterMiner(path);
        out.writeObject("OK");
        out.writeObject(hcm.toString(data));
        break;
      } catch (ClassNotFoundException e) {
        out.writeObject("Errore: riferimento a classi non trovate.");
      } catch (FileNotFoundException e) {
        out.writeObject("Errore: file non trovato.");
      } catch (IOException e) {
        out.writeObject("Errore nell'operazione di lettura dal file.");
      } catch (InvalidPathException e) {
        out.writeObject("Errore: percorso del file non valido.");
      }
    }
  }

  /**
   * Metodo che si occupa di creare un dendrogramma a partire dal dataset di
   * esempi e di serializzarlo in un file il cui percorso viene richiesto
   * all'utente.
   * 
   * @param in   stream di input per ricevere i dati dal client
   * @param out  stream di output per inviare i dati al client
   * @param data dataset di esempi letti dal database
   * @throws IOException            nel caso in cui si verifichi un errore nella
   *                                comunicazione con il client
   * @throws ClassNotFoundException nel caso in cui si referenzi una classe non
   *                                trovata
   * @throws InvalidSizeException   nel caso in cui si calcoli la distanza tra
   *                                esempi di dimensione differente
   */
  private void create(ObjectInputStream in, ObjectOutputStream out, Data data)
      throws IOException, ClassNotFoundException, InvalidSizeException {
    Boolean flag = true;
    while (true) {
      if (!flag) {
        in.readObject();
      } else {
        flag = false;
      }
      try {
        Integer depth = (Integer) in.readObject();
        Integer dChoice = (Integer) in.readObject(); // single or average link
        HierarchicalClusterMiner.checkDepth(depth, data.getNumberOfExample());
        HierarchicalClusterMiner hcm = new HierarchicalClusterMiner(depth);
        if (dChoice == 1) {
          hcm.mine(data, new SingleLinkDistance());
        } else {
          hcm.mine(data, new AverageLinkDistance());
        }
        out.writeObject("OK");
        while (true) {
          try {
            out.writeObject(hcm.toString(data));
            String path = (String) in.readObject();
            hcm.save(path);
            break;
          } catch (InvalidPathException e) {
            out.writeObject("Errore: percorso del file non valido.");
          }
        }
        out.writeObject("OK");
        break;
      } catch (InvalidDepthException e) {
        out.writeObject("Errore: dimensione del dendrogramma non valida. Inserire un intero compreso tra 1 e "
            + data.getNumberOfExample());
      }
    }
  }
}
