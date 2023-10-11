package pe.puyu.model.beans;

import javafx.beans.property.SimpleStringProperty;

public class BifrostConfig {

  public BifrostConfig() {
    urlBifrost = new SimpleStringProperty();
    namespace = new SimpleStringProperty();
    ruc = new SimpleStringProperty();
    branch = new SimpleStringProperty();
  }

  public void copyFrom(BifrostConfig bifrostConfig) {
    urlBifrost.set(bifrostConfig.getUrlBifrost());
    namespace.set(bifrostConfig.getNamespace());
    ruc.set(bifrostConfig.getRuc());
    branch.set(bifrostConfig.getBranch());
  }

  public String getUrlBifrost() {
    return urlBifrost.get();
  }

  public void setUrlBifrost(String urlBifrost) {
    this.urlBifrost.set(urlBifrost.trim());
  }

  public String getNamespace() {
    return namespace.get();
  }

  public void setNamespace(String namespace) {
    this.namespace.set(namespace.trim());
  }

  public String getRuc() {
    return ruc.get();
  }

  public void setRuc(String ruc) {
    this.ruc.set(ruc.trim());
  }

  public String getBranch() {
    return branch.get();
  }

  public void setBranch(String branch) {
    this.branch.set(branch.trim());
  }

  public SimpleStringProperty urlBifrostProperty() {
    return urlBifrost;
  }

  public SimpleStringProperty rucProperty() {
    return ruc;
  }

  public SimpleStringProperty namespaceProperty() {
    return namespace;
  }

  public SimpleStringProperty branchProperty() {
    return branch;
  }

  private SimpleStringProperty urlBifrost;
  private SimpleStringProperty namespace;
  private SimpleStringProperty ruc;
  private SimpleStringProperty branch;
}
