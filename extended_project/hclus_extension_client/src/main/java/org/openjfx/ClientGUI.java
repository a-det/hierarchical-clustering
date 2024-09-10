package org.openjfx;

import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.fxml.FXMLLoader;
import javafx.scene.image.Image;
import javafx.application.Application;
import org.openjfx.connection.ClientOperations;

/**
 * La classe ClientGUI e' responsabile per la creazione e visualizzazione
 * dell'interfaccia grafica dell'applicazione JavaFX.
 * Estende la classe Application e sovrascrive il metodo start()
 * per impostare il tema, caricare il file FXML,
 * creare la scena e mostrare lo stage (finestra).
 */
public class ClientGUI extends Application {

  /**
   * Metodo che avvia l'applicazione impostandone il tema e caricando il file FXML
   * della prima schermata
   */
  @Override
  public void start(Stage stage) throws Exception {

    // Imposta il tema
    Application.setUserAgentStylesheet("file:src/main/resources/org/openjfx/style/dracula.css");

    // Carica il file FXML della prima schermata
    Parent root = FXMLLoader.load(getClass().getResource("fxml/welcome.fxml"));
    stage.getIcons().add(new Image("file:src/main/resources/org/openjfx/img/icon.png"));
    stage.setTitle("Hierarchical Clustering Client");
    stage.setScene(new Scene(root, 600, 500));
    stage.show();
    stage.setOnCloseRequest(event -> ClientOperations.getInstance().close());
  }

  /**
   * Il metodo main() avvia l'applicazione JavaFX.
   * 
   * @param args Argomenti passati da riga di comando
   */
  public static void main(String[] args) {
    launch(args);
  }
}
