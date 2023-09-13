module pe.puyu {
	requires transitive javafx.graphics;
 	requires javafx.controls;
	requires javafx.fxml;
	requires transitive javafx.base;
	
	opens pe.puyu.app to javafx.fxml, javafx.graphics;
	exports pe.puyu.app;
}
