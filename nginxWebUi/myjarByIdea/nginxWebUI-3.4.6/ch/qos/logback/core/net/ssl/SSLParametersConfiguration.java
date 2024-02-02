package ch.qos.logback.core.net.ssl;

import ch.qos.logback.core.spi.ContextAwareBase;
import ch.qos.logback.core.util.OptionHelper;
import ch.qos.logback.core.util.StringCollectionUtil;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SSLParametersConfiguration extends ContextAwareBase {
   private String includedProtocols;
   private String excludedProtocols;
   private String includedCipherSuites;
   private String excludedCipherSuites;
   private Boolean needClientAuth;
   private Boolean wantClientAuth;
   private String[] enabledProtocols;
   private String[] enabledCipherSuites;
   private Boolean hostnameVerification;

   public void configure(SSLConfigurable socket) {
      socket.setEnabledProtocols(this.enabledProtocols(socket.getSupportedProtocols(), socket.getDefaultProtocols()));
      socket.setEnabledCipherSuites(this.enabledCipherSuites(socket.getSupportedCipherSuites(), socket.getDefaultCipherSuites()));
      if (this.isNeedClientAuth() != null) {
         socket.setNeedClientAuth(this.isNeedClientAuth());
      }

      if (this.isWantClientAuth() != null) {
         socket.setWantClientAuth(this.isWantClientAuth());
      }

      if (this.hostnameVerification != null) {
         this.addInfo("hostnameVerification=" + this.hostnameVerification);
         socket.setHostnameVerification(this.hostnameVerification);
      }

   }

   public boolean getHostnameVerification() {
      return this.hostnameVerification == null ? false : this.hostnameVerification;
   }

   public void setHostnameVerification(boolean hostnameVerification) {
      this.hostnameVerification = hostnameVerification;
   }

   private String[] enabledProtocols(String[] supportedProtocols, String[] defaultProtocols) {
      if (this.enabledProtocols == null) {
         if (OptionHelper.isEmpty(this.getIncludedProtocols()) && OptionHelper.isEmpty(this.getExcludedProtocols())) {
            this.enabledProtocols = (String[])Arrays.copyOf(defaultProtocols, defaultProtocols.length);
         } else {
            this.enabledProtocols = this.includedStrings(supportedProtocols, this.getIncludedProtocols(), this.getExcludedProtocols());
         }

         String[] var3 = this.enabledProtocols;
         int var4 = var3.length;

         for(int var5 = 0; var5 < var4; ++var5) {
            String protocol = var3[var5];
            this.addInfo("enabled protocol: " + protocol);
         }
      }

      return this.enabledProtocols;
   }

   private String[] enabledCipherSuites(String[] supportedCipherSuites, String[] defaultCipherSuites) {
      if (this.enabledCipherSuites == null) {
         if (OptionHelper.isEmpty(this.getIncludedCipherSuites()) && OptionHelper.isEmpty(this.getExcludedCipherSuites())) {
            this.enabledCipherSuites = (String[])Arrays.copyOf(defaultCipherSuites, defaultCipherSuites.length);
         } else {
            this.enabledCipherSuites = this.includedStrings(supportedCipherSuites, this.getIncludedCipherSuites(), this.getExcludedCipherSuites());
         }

         String[] var3 = this.enabledCipherSuites;
         int var4 = var3.length;

         for(int var5 = 0; var5 < var4; ++var5) {
            String cipherSuite = var3[var5];
            this.addInfo("enabled cipher suite: " + cipherSuite);
         }
      }

      return this.enabledCipherSuites;
   }

   private String[] includedStrings(String[] defaults, String included, String excluded) {
      List<String> values = new ArrayList(defaults.length);
      values.addAll(Arrays.asList(defaults));
      if (included != null) {
         StringCollectionUtil.retainMatching(values, (String[])this.stringToArray(included));
      }

      if (excluded != null) {
         StringCollectionUtil.removeMatching(values, (String[])this.stringToArray(excluded));
      }

      return (String[])values.toArray(new String[values.size()]);
   }

   private String[] stringToArray(String s) {
      return s.split("\\s*,\\s*");
   }

   public String getIncludedProtocols() {
      return this.includedProtocols;
   }

   public void setIncludedProtocols(String protocols) {
      this.includedProtocols = protocols;
   }

   public String getExcludedProtocols() {
      return this.excludedProtocols;
   }

   public void setExcludedProtocols(String protocols) {
      this.excludedProtocols = protocols;
   }

   public String getIncludedCipherSuites() {
      return this.includedCipherSuites;
   }

   public void setIncludedCipherSuites(String cipherSuites) {
      this.includedCipherSuites = cipherSuites;
   }

   public String getExcludedCipherSuites() {
      return this.excludedCipherSuites;
   }

   public void setExcludedCipherSuites(String cipherSuites) {
      this.excludedCipherSuites = cipherSuites;
   }

   public Boolean isNeedClientAuth() {
      return this.needClientAuth;
   }

   public void setNeedClientAuth(Boolean needClientAuth) {
      this.needClientAuth = needClientAuth;
   }

   public Boolean isWantClientAuth() {
      return this.wantClientAuth;
   }

   public void setWantClientAuth(Boolean wantClientAuth) {
      this.wantClientAuth = wantClientAuth;
   }
}
