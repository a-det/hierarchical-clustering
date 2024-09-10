module hclus_extension_client {
  requires javafx.controls;
  requires javafx.fxml;
  requires javafx.web;
  requires jdk.jsobject;
  requires transitive javafx.graphics;

  opens org.openjfx to javafx.fxml;

  exports org.openjfx;
}
