package org.openjfx.connection;

import java.io.IOException;

/**
 * Classe singoletto responsabile della gestione delle operazioni eseguite
 * dal client.
 */
public class ClientOperations {

  private static ClientOperations instance;
  private ServerHandler client;

  /**
   * Costruttore privato della classe singoletto
   */
  private ClientOperations() {
  }

  /**
   * Restituisce l'unica istanza della classe ClientOperations
   * @return unica istanza della classe ClientOperations
   */
  public static ClientOperations getInstance() {
    if (instance == null) {
      instance = new ClientOperations();
    }
    return instance;
  }

  /**
   * Inizializza la connessione con il server
   * 
   * @param ip   indirizzo ip del server
   * @param port porta del servver
   * @throws IOException se si verificano errori di I/O durante la connesione con
   *                     il server
   */
  public void clientInizialization(String ip, Integer port) throws IOException {
    getInstance().client = new ServerHandler(ip, port);
  }

  /**
   * Inizializza la connessione con il database
   * 
   * @param DB        nome del database a cui connettersi
   * @param username  username del database
   * @param password  password del database
   * @param tableName nome della tabella del database da cui prendere i dati
   * @return stringa di risposta
   */
  public String initDBConnection(String DB, String username, String password, String tableName) {
    try {
      getInstance().client.getOut().writeObject(DB);
      getInstance().client.getOut().writeObject(username);
      getInstance().client.getOut().writeObject(password);
      getInstance().client.getOut().writeObject(tableName);
      try {
        return (String) getInstance().client.getIn().readObject();
      } catch (ClassNotFoundException e) {
        return "Errore: Ricevuto un dato di tipo non valido.";
      }
    } catch (IOException e) {
      return "Errore: verificare la connessione con il server.";
    }
  }

  /**
   * Genera il dendrogramma con la profondita' e il tipo di distanza scelti
   * 
   * @param depth  profondita' del dendrogramma
   * @param choice tipo di distanza da utilizzare per generare il dendrogramma
   * @return esito dell'operazione di generazione del dendrogramma
   */
  public String generate(Integer depth, Integer choice) {
    try {
      getInstance().client.getOut().writeObject(1);
      getInstance().client.getOut().writeObject(depth);
      getInstance().client.getOut().writeObject(choice);
      return (String) getInstance().client.getIn().readObject();
    } catch (IOException e) {
      return e.getMessage();
    } catch (ClassNotFoundException e) {
      return e.getMessage();
    }
  }

  /**
   * Carica un dendrogramma da un file il cui percorso e' fornito dall'utente
   * 
   * @param path percorso del file da caricare
   * @return esito dell'operazione di caricamento del dendrogramma da file
   */
  public String load(String path) {
    try {
      getInstance().client.getOut().writeObject(2);
      getInstance().client.getOut().writeObject(path);
      return (String) getInstance().client.getIn().readObject();
    } catch (IOException | ClassNotFoundException e) {
      return e.getMessage();
    }
  }

  /**
   * Riceve il dendrogramma in formato json dal server e la relativa profondita'
   * 
   * @return array di stringhe contenente il dendrogramma in formato json e la sua
   *         profondita'
   */
  public String[] receiveJsonDendrogram() {
    String[] result = new String[2];
    try {
      result[0] = (String) getInstance().client.getIn().readObject();
      result[1] = (String) getInstance().client.getIn().readObject().toString();
    } catch (IOException e) {
      result[0] = e.getMessage();
    } catch (ClassNotFoundException e) {
      result[0] = e.getMessage();
    }
    return result;
  }

  /**
   * Esegue la serializzazione del dendrogramma in un file il cui percorso e'
   * fornito dall'utente
   * 
   * @param path percorso del file in cui salvare il dendrogramma
   * @return esito dell'operazione di serializzazione
   */
  public String save(String path) {
    try {
      getInstance().client.getOut().writeObject(path);
      String resp = (String) getInstance().client.getIn().readObject();
      return resp;
    } catch (IOException | ClassNotFoundException e) {
      return "Errore nell'operazione di salvataggio del dendrogramma";
    }
  }

  /**
   * Chiude la connessione con il server e
   * i relativi stream di input e output
   */
  public void close() {
    try {
      if (getInstance().client != null)
        getInstance().client.close();
    } catch (IOException e) {
      // nel caso in cui venga sollevata questa eccezione, la chiusura della
      // connessione tra client e server non e' stata effettuata correttamente.
      // Ad esempio se si chiude prima il server e poi il client.
      // Si procede ad informare l'utente via terminale ed a chiudere l'applicazione.
      System.out.println("Chiusura forzata della connessione con il server a causa di un errore di comunicazione.");
      System.out.println("Situazione che si verifica, ad esempio, se si chiude prima il server e poi il client.");
    }

  }

  /**
   * Invia il segnale al server per continuare l'applicazione
   * 
   * @throws IOException se si verificano errori di I/O nell'invio
   *                     dell'oggetto al server
   */
  public void continueApplicationSignal() throws IOException {
    getInstance().client.getOut().writeObject(4);
  }

  /**
   * Invia il segnale al server per chiudere l'applicazione
   * 
   * @throws IOException se si verificano errori di I/O nell'invio
   *                     dell'oggetto al server
   */
  public void stopApplicationSignal() throws IOException {
    getInstance().client.getOut().writeObject(3);
  }

}
