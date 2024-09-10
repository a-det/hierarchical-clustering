module hclus_extension_server {
  requires javafx.controls;
  requires javafx.fxml;
  requires java.sql;
  requires com.google.gson;
  requires transitive javafx.graphics;

  opens org.openjfx to javafx.fxml;

  exports org.openjfx;
}
