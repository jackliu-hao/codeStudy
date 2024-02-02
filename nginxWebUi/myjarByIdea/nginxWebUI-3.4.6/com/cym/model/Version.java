package com.cym.model;

public class Version {
   String version;
   String docker;
   String url;
   String update;

   public String getUpdate() {
      return this.update;
   }

   public void setUpdate(String update) {
      this.update = update;
   }

   public String getDocker() {
      return this.docker;
   }

   public void setDocker(String docker) {
      this.docker = docker;
   }

   public String getVersion() {
      return this.version;
   }

   public void setVersion(String version) {
      this.version = version;
   }

   public String getUrl() {
      return this.url;
   }

   public void setUrl(String url) {
      this.url = url;
   }
}
