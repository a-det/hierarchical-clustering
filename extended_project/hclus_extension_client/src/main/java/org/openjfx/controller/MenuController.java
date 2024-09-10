package org.openjfx.controller;

// import di javafx generali
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.RadioButton;
import org.openjfx.connection.ClientOperations;

// import di javafx webview
import javafx.scene.web.WebView;
import javafx.scene.web.WebEngine;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Priority;
import java.io.File;
import netscape.javascript.JSObject;
import java.io.IOException;
import javafx.stage.Stage;
import javafx.scene.Scene;

// import per la finestra di dialogo
import java.util.Optional;
import javafx.stage.WindowEvent;
import javafx.scene.control.Alert;
import javafx.application.Platform;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ButtonBar.ButtonData;

/**
 * MenuController e' il controller per la schermata relativa alle scelte
 * possibili per l'utente.
 * Gestisce l'interazione dell'utente con i campi di testo, i pulsanti e le
 * etichette presenti nella schermata.
 */
public class MenuController {

  // Componenti della schermata
  @FXML
  private TextField serverFilePath;
  @FXML
  private Button caricaButton;
  @FXML
  private Button generaButton;
  @FXML
  private TextField depth;
  @FXML
  private RadioButton singleLink;
  @FXML
  private RadioButton averageLink;
  @FXML
  private Label status;
  @FXML
  private TextField savingPath;
  @FXML
  private WebView webView;
  @FXML
  private WebEngine engine;

  /**
   * Il metodo initialize() viene chiamato automaticamente per inizializzare
   * il controller dopo che il file fxml e' stato caricato.
   * Imposta i listener per il pulsante di connessione.
   */
  @FXML
  private void initialize() {
    caricaButton.setOnAction(event -> load());
    generaButton.setOnAction(event -> generate());
  }

  /**
   * Carica il dendrogramma dal server utilizzando il path
   * fornito dall'utente e lo visualizza.
   */
  private void load() {
    String path = serverFilePath.getText();
    if (path.isEmpty()) {
      path = serverFilePath.getPromptText();
    }

    String resp = ClientOperations.getInstance().load(path);
    if (resp.equals("OK")) {
      String[] result = ClientOperations.getInstance().receiveJsonDendrogram();
      String deserializedDendrogram = result[0];
      Integer deserializedDepth = Integer.parseInt(result[1]);
      status.setText("Dendrogramma caricato.");

      // Visualizzazione del dendrogramma
      loadWebView(deserializedDendrogram, deserializedDepth);
    } else {
      status.setText(resp);
      try {
        ClientOperations.getInstance().continueApplicationSignal();
      } catch (IOException e) {
        status.setText("Errore: verificare la connessione con il server.");
      }
    }

  }

  /**
   * Popup che chiede all'utente se vuole uscire dall'applicazione.
   * Se l'utente conferma l'uscita, chiude l'applicazione.
   * Altrimenti ritorna al menu.
   * 
   * @param event l'evento di chiusura della finestra
   */
  private void askUser(WindowEvent event, Stage stage) {
    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
    alert.setTitle("Conferma uscita");
    alert.setHeaderText("Sicuro di voler uscire?");
    alert.setContentText("Fai la tua scelta:");

    ButtonType buttonstopApplicationSignal = new ButtonType("Chiudi App");
    ButtonType buttonChooseAnother = new ButtonType("Menu ");
    ButtonType buttonCancel = new ButtonType("Annulla", ButtonData.CANCEL_CLOSE);

    // Imposta i pulsanti della finestra di dialogo
    alert.getButtonTypes().setAll(buttonstopApplicationSignal, buttonChooseAnother, buttonCancel);

    Optional<ButtonType> result = alert.showAndWait();

    if (result.isPresent()) {
      if (result.get() == buttonstopApplicationSignal) {
        try {
          ClientOperations.getInstance().stopApplicationSignal();
          ClientOperations.getInstance().close();
          Platform.exit();
        } catch (IOException e) {
          status
              .setText("Errore nell'operazione di invio del segnale di chiusura al server. Il client verra' arrestato");
          ClientOperations.getInstance().close();
          Platform.exit();
        }

      } else if (result.get() == buttonChooseAnother) {
        try {
          ClientOperations.getInstance().continueApplicationSignal();
        } catch (IOException e) {
          status.setText("Errore: verificare la connessione con il server. ");
        }
        choseAnotherFunction(stage, alert);
      }
    }
  }

  /**
   * Torna al menu dove poter eseguire altre operazioni.
   * 
   * @param stage la finestra del menu in cui tornare
   * @param alert la finestra in cui stampare gli eventuali errori
   */
  private void choseAnotherFunction(Stage stage, Alert alert) {
    try {
      Parent menu = FXMLLoader.load(getClass().getResource("/org/openjfx/fxml/menu.fxml"));
      stage.setScene(new Scene(menu, stage.getScene().getWidth(), stage.getScene().getHeight()));
      stage.getScene().setUserData("menu");
    } catch (IOException e) {
      alert.setContentText("Errore durante il cambio scena FXML");
    }
  }

  /**
   * Genera il dendrogramma con al profondita' e il tipo di distanza scelti
   * dall'utente. A seguito della generazione del dendrogramma, si procede alla
   * sua serializzazione in un file il cui path viene fornito dall'utente.
   */
  private void generate() {
    String risp = null;
    String savePath = null;
    String jsonString = null;

    try {
      Integer depth = Integer.parseInt(this.depth.getText());
      if (singleLink.isSelected()) {
        risp = ClientOperations.getInstance().generate(depth, 1);
      } else {
        risp = ClientOperations.getInstance().generate(depth, 2);
      }
      if (risp.equals("OK")) {

        savePath = savingPath.getText();
        if (savePath.isEmpty()) {
          savePath = savingPath.getPromptText();
        }
        jsonString = ClientOperations.getInstance().save(savePath);
        if (!(jsonString.equals("Errore: percorso non valido")
            || jsonString.equals("Errore: impossibile leggere il file")
            || jsonString.equals("Errore nell'operazione di salvataggio del dendrogramma"))) {

          loadWebView(jsonString, depth);

        } else {
          status.setText(jsonString);
          try {
            ClientOperations.getInstance().continueApplicationSignal();
          } catch (IOException e) {
            status.setText("Errore: verificare la connessione con il server. ");
          }
        }

      } else {
        status.setText(risp);
        try {
          ClientOperations.getInstance().continueApplicationSignal();
        } catch (IOException e) {
          status.setText("Errore: verificare la connessione con il server.");
        }
      }

    } catch (NumberFormatException e) {
      status.setText("Inserire un valore numerico per la profondita'.");
    }

  }

  /**
   * Carica il dendrogramma nel WebView.
   * 
   * @param deserializedDendrogram il dendrogramma deserializzato da visualizzare
   * @param deserializedDepth      la profondita' del dendrogramma scelta dl
   */
  private void loadWebView(String deserializedDendrogram, Integer deserializedDepth) {
    Stage stage = (Stage) caricaButton.getScene().getWindow();
    WebView webView = new WebView();
    VBox.setVgrow(webView, Priority.ALWAYS);
    WebEngine webEngine = webView.getEngine();
    File file = new File(
        "src/main/resources/org/openjfx/dendrogram_visualizer.html");
    webEngine.load(file.toURI().toString());
    webEngine.documentProperty().addListener((obs, oldDoc, newDoc) -> {
      if (newDoc != null) {

        // netscape.javascript.JSObject permette di interagire con JavaScript da Java
        JSObject window = (JSObject) webEngine.executeScript("window");

        // Espressioni regolari per assicurare la formattazione del JSON
        String updatedJson = deserializedDendrogram.replaceAll("\"a\"", "\"name\"");
        updatedJson = updatedJson.replaceAll("\"b\"", "\"children\"");

        // Esegue la funzione in JavaScript per visualizzare il dendrogramma
        window.call("fillData", updatedJson, deserializedDepth);

      }
    });
    // Cambio scena
    Scene scene = new Scene(webView, caricaButton.getScene().getWidth(), caricaButton.getScene().getHeight());
    scene.setUserData("webView");
    stage.setScene(scene);

    // Imposta l'evento di chiusura della finestra che apre il pop up
    stage.setOnCloseRequest(event -> {
      if (stage.getScene().getUserData().equals("webView")) {
        event.consume(); // assorbe l'evento per evitare la chiusura della finestra immediata
        askUser(event, stage);
      } else {
        ClientOperations.getInstance().close();
        Platform.exit();
      }
    });
  }
}