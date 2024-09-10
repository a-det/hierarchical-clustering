package org.openjfx;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.image.Image;

/**
 * La classe ServerGUI e' responsabile per la creazione e visualizzazione
 * dell'interfaccia grafica dell'applicazione JavaFX.
 * Estende la classe Application e sovrascrive il metodo start() per impostare
 * il tema, caricare il file FXML,
 * creare la scena e mostrare lo stage(finestra).
 */
public class ServerGUI extends Application {

  /**
   * Il metodo start() viene chiamato quando l'applicazione JavaFX viene lanciata.
   * Imposta il tema, carica il file FXML creando lo stage (finestra) e la scena.
   * Mostra la finestra di benvenuto welcome.fxml con i suoi componenti.
   * WelcomeController e' il controller per la schermata di benvenuto.
   * 
   * @param stage Finestra dell'applicazione JavaFX
   * @throws Exception Eccezione generica
   */
  @Override
  public void start(Stage stage) throws Exception {

    // Imposta il tema
    Application.setUserAgentStylesheet("file:src/main/resources/org/openjfx/style/dracula.css");

    // Carica il file FXML
    Parent root = FXMLLoader.load(getClass().getResource("fxml/welcome.fxml"));

    // Mostra la finestra con i suoi componenti
    stage.getIcons().add(new Image("file:src/main/resources/org/openjfx/img/icon.png"));
    Scene scene = new Scene(root, 650, 400);
    stage.setTitle("Hierarchical Clustering Server");
    stage.setScene(scene);
    stage.show();
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
