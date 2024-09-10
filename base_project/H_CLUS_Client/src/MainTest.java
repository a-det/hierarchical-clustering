import java.net.Socket;
import java.io.IOException;
import java.net.InetAddress;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Classe che implementa il client per la comunicazione con il server
 */
public class MainTest {

  private static ObjectOutputStream out;
  private static ObjectInputStream in;
  private Socket socket;

  /**
   * Costruttore della classe che inizializza la connessione
   * con il server dati indirizzo IP e porta
   * 
   * @throws IOException se si verifica un errore in una operazione di I/O
   */
  public MainTest() throws IOException {
    while (true) {
      try {
        System.out.print("Inserisci l'indizzo IP del server: ");
        String ip = Keyboard.readString();
        System.out.print("Inserisci la porta del server: ");
        Integer port = Keyboard.readInt();

        InetAddress addr = InetAddress.getByName(ip);
        System.out.println("addr = " + addr);
        socket = new Socket(addr, port);
        System.out.println(socket);
        break;
      } catch (IOException e) {
        System.out.println("Errore nella connessione con il server.");
      } catch (IllegalArgumentException e) {
        System.out.println("Porta non valida.");
      }
    }

    out = new ObjectOutputStream(socket.getOutputStream());
    in = new ObjectInputStream(socket.getInputStream());
  }

  /**
   * Metodo per l'acquisizione della scelta dell'utente
   * 
   * @return answer la scelta dell'utente
   */
  private Integer menu() {
    Integer answer;
    System.out.println("Scegli una opzione");
    do {
      System.out.println("(1) Carica Dendrogramma da File");
      System.out.println("(2) Apprendi Dendrogramma da Database");
      System.out.print("Risposta: ");
      answer = Keyboard.readInt();
    } while (answer <= 0 || answer > 2);
    return answer;

  }

  /**
   * Metodo per la scelta della tabella da consultare del DB
   * 
   * @throws IOException            se si verifica un errore in una operazione di
   *                                I/O
   * @throws ClassNotFoundException se non viene trovata la classe della quale e'
   *                                istanza l'oggetto serializzato
   */
  private void loadDataOnServer() throws IOException, ClassNotFoundException {
    boolean flag = false;
    do {
      System.out.print("Nome tabella: ");
      String tableName = Keyboard.readString();
      out.writeObject(0);
      out.writeObject(tableName);
      String risposta = (String) (in.readObject());
      if (risposta.equals("OK"))
        flag = true;
      else
        System.out.println(risposta);
    } while (flag == false);
  }

  /**
   * Metodo per il caricamento del dendrogramma da file il cui percorso e' stato
   * inserito dall'utente. Nel caso in cui il caricamento vada a buon fine, il
   * dendrogramma viene stampato
   * 
   * @throws IOException            se si verifica un errore in una operazione di
   *                                I/O
   * @throws ClassNotFoundException se non viene trovata la classe della quale e'
   *                                istanza l'oggetto serializzato
   */
  private void loadDedrogramFromFileOnServer() throws IOException, ClassNotFoundException {
    while (true) {
      out.writeObject(2);
      System.out.println("Inserire il nome dell'archivio (comprensivo di estensione): ");
      String fileName = Keyboard.readString();

      out.writeObject(fileName);
      String risposta = (String) (in.readObject());
      if (risposta.equals("OK")) {
        System.out.println(in.readObject());
        break;
      } else {
        System.out.println(risposta);
      }
    }
  }

  /**
   * Metodo per la generazione del dendrogramma utilizzando il dataset letto dal
   * database. Si richiede inoltre all'utente di inserire la profondita' del
   * dendrogramma e il tipo di distanza da utilizzare. Il dendrogramma viene poi
   * serializzato e salvato in un file il cui percorso e' richiesto all'utente.
   * 
   * @throws IOException            se si verifica un errore in una operazione di
   *                                I/O
   * @throws ClassNotFoundException se non viene trovata la classe della quale e'
   *                                istanza l'oggetto serializzato
   */
  private void mineDedrogramOnServer() throws IOException, ClassNotFoundException {
    String risposta = "";
    while (true) {
      if (!risposta.equals("OK")) {
        out.writeObject(1);
        System.out.println("Introdurre la profondita' del dendrogramma: ");
        Integer depth = Keyboard.readInt();
        out.writeObject(depth);
        Integer dType = -1;
        do {
          System.out.println("Distanza: single-link (1), average-link (2): ");
          dType = Keyboard.readInt();
        } while (dType <= 0 || dType > 2);
        out.writeObject(dType);
        risposta = (String) in.readObject();
      }
      
      if (risposta.equals("OK")) {
        System.out.println(in.readObject()); // stampo il dendrogramma che il server mi sta inviando
        System.out.println("Inserire il nome dell'archivio (comprensivo di estensione): ");
        String fileName = Keyboard.readString();
        out.writeObject(fileName);
        String resp = (String) in.readObject();
        if (resp.equals("OK"))
          break;
        else {
          System.out.println(resp);
        }
      } else
        System.out.println(risposta); // stampo il messaggio di errore
    }
  }

  /**
   * Metodo principale che gestisce la comunicazione con il server sfruttando i
   * metodi della classe
   * 
   * @param args argomenti passati da riga di comando
   */
  public static void main(String[] args) {
    MainTest main = null;
    try {
      main = new MainTest();
      System.out.println("Connessione con il server stabilita.");

      while (true) {
        System.out.print("Inserisci il nome del database a cui accedere: ");
        String dbName = Keyboard.readString();
        out.writeObject(dbName);
        System.out.print("Inserisci il nome utente con il quale accedere al DB: ");
        String message = Keyboard.readString();
        out.writeObject(message);
        System.out.print("Inserisci la password: ");
        message = Keyboard.readString();
        out.writeObject(message);
        String answer = (String) in.readObject();
        if (answer.equals("OK")) {
          System.out.println("Accesso al DB effettuato con successo.");
          break;
        } else {
          System.out.println(answer);
        }
      }

      // operazione di ricezione e apertura della connessione lato server
      main.loadDataOnServer();
      Integer scelta = main.menu();
      if (scelta == 1)
        main.loadDedrogramFromFileOnServer();
      else
        main.mineDedrogramOnServer();

      main.socket.close();
    } catch (IOException | ClassNotFoundException e) {
      System.out.println(e);
      return;
    }
  }
}