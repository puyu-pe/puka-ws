module pe.puyu.pukafx {
	requires transitive javafx.graphics;
	requires javafx.controls;
	requires javafx.fxml;

	requires java.desktop;
	requires transitive com.dustinredmond.fxtrayicon;

	requires ch.qos.logback.classic;
	requires ch.qos.logback.core;
	requires org.slf4j;

	requires com.google.gson;
	requires org.hildan.fxgson;
	opens pe.puyu.pukafx.model to com.google.gson, org.hildan.fxgson;

	requires net.harawata.appdirs;

	requires socket.io.client;
	requires engine.io.client;
	requires org.json;

	requires escpos.coffee;
	requires com.fazecast.jSerialComm;
	requires pe.puyu.jticketdesing;

	opens pe.puyu.pukafx.views to javafx.fxml, javafx.graphics;
	opens pe.puyu.pukafx.app to javafx.fxml, javafx.graphics;

	exports pe.puyu.pukafx.app;
}