package org.openjfx.connection;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;

/**
 * Classe che si occupa di gestire la comunicazione con il server.
 */
public class ServerHandler {

  private static ObjectOutputStream out;
  private static ObjectInputStream in;
  private Socket socket;

  /**
   * Costruttore della classe ServerHandler.
   * 
   * @param ip   indirizzo ip del server
   * @param port porta del server
   * @throws IOException se si verificano errori di I/O
   */
  public ServerHandler(String ip, Integer port) throws IOException {
    while (true) {

      InetAddress addr = InetAddress.getByName(ip);
      socket = new Socket(addr, port);
      break;
    }

    out = new ObjectOutputStream(socket.getOutputStream());
    in = new ObjectInputStream(socket.getInputStream());
  }

  /**
   * Metodo che restituisce l'oggetto ObjectOutputStream.
   * 
   * @return out oggetto di tipo ObjectOutputStream
   */
  public ObjectOutputStream getOut() {
    return out;
  }

  /**
   * Metodo che restituisce l'oggetto ObjectInputStream.
   * 
   * @return in oggetto di tipo ObjectInputStream
   */
  public ObjectInputStream getIn() {
    return in;
  }

  /**
   * Metodo che chiude la comunicazione con il server.
   * 
   * @throws IOException se si verificano errori durante l'operazione di chiusura
   *                     del socket e dei relativi stream.
   */
  public void close() throws IOException {
    out.close();
    in.close();
    socket.close();
  }

}