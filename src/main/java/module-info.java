module pe.puyu {
	requires transitive javafx.graphics;
 	requires javafx.controls;
	requires javafx.fxml;
	requires transitive javafx.base;
	opens pe.puyu.app to javafx.fxml, javafx.graphics;
	
  requires json;
  requires socket.io.client;
  requires transitive engine.io.client;

	exports pe.puyu.app;
}
