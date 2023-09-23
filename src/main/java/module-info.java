module pe.puyu {
  requires transitive javafx.graphics;
  requires javafx.controls;
  requires javafx.fxml;
  requires transitive javafx.base;

  opens pe.puyu.app to javafx.fxml, javafx.graphics;
  opens pe.puyu.controller to javafx.fxml, javafx.graphics;

  requires json;
  requires socket.io.client;
  requires transitive engine.io.client;
  
  requires ch.qos.logback.core;
  requires ch.qos.logback.classic;
  requires org.slf4j;

  requires java.desktop;
  requires transitive com.dustinredmond.fxtrayicon;  

  requires com.google.gson;
  requires org.hildan.fxgson;
  opens pe.puyu.model to com.google.gson, org.hildan.fxgson;


  requires net.harawata.appdirs;

  exports pe.puyu.app;
}
