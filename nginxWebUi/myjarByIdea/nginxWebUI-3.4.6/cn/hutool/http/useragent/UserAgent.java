package cn.hutool.http.useragent;

import java.io.Serializable;

public class UserAgent implements Serializable {
   private static final long serialVersionUID = 1L;
   private boolean mobile;
   private Browser browser;
   private String version;
   private Platform platform;
   private OS os;
   private String osVersion;
   private Engine engine;
   private String engineVersion;

   public boolean isMobile() {
      return this.mobile;
   }

   public void setMobile(boolean mobile) {
      this.mobile = mobile;
   }

   public Browser getBrowser() {
      return this.browser;
   }

   public void setBrowser(Browser browser) {
      this.browser = browser;
   }

   public Platform getPlatform() {
      return this.platform;
   }

   public void setPlatform(Platform platform) {
      this.platform = platform;
   }

   public OS getOs() {
      return this.os;
   }

   public void setOs(OS os) {
      this.os = os;
   }

   public String getOsVersion() {
      return this.osVersion;
   }

   public void setOsVersion(String osVersion) {
      this.osVersion = osVersion;
   }

   public Engine getEngine() {
      return this.engine;
   }

   public void setEngine(Engine engine) {
      this.engine = engine;
   }

   public String getVersion() {
      return this.version;
   }

   public void setVersion(String version) {
      this.version = version;
   }

   public String getEngineVersion() {
      return this.engineVersion;
   }

   public void setEngineVersion(String engineVersion) {
      this.engineVersion = engineVersion;
   }
}
