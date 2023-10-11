package pe.puyu.model.beans;

import javafx.beans.property.SimpleStringProperty;

public class UserConfig {
  private final SimpleStringProperty logoPath = new SimpleStringProperty();

  public String getLogoPath() {
    return logoPath.get();
  }

  public void setLogoPath(String logoPath) {
    this.logoPath.set(logoPath);
  }

  public SimpleStringProperty logoPathProperty() {
    return logoPath;
  }

  public void copyFrom(UserConfig userConfig) {
    this.logoPath.set(userConfig.getLogoPath());
  }
}
