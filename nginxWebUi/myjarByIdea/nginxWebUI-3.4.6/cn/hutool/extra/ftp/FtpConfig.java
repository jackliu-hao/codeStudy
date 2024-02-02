package cn.hutool.extra.ftp;

import java.io.Serializable;
import java.nio.charset.Charset;

public class FtpConfig implements Serializable {
   private static final long serialVersionUID = 1L;
   private String host;
   private int port;
   private String user;
   private String password;
   private Charset charset;
   private long connectionTimeout;
   private long soTimeout;
   private String serverLanguageCode;
   private String systemKey;

   public static FtpConfig create() {
      return new FtpConfig();
   }

   public FtpConfig() {
   }

   public FtpConfig(String host, int port, String user, String password, Charset charset) {
      this(host, port, user, password, charset, (String)null, (String)null);
   }

   public FtpConfig(String host, int port, String user, String password, Charset charset, String serverLanguageCode, String systemKey) {
      this.host = host;
      this.port = port;
      this.user = user;
      this.password = password;
      this.charset = charset;
      this.serverLanguageCode = serverLanguageCode;
      this.systemKey = systemKey;
   }

   public String getHost() {
      return this.host;
   }

   public FtpConfig setHost(String host) {
      this.host = host;
      return this;
   }

   public int getPort() {
      return this.port;
   }

   public FtpConfig setPort(int port) {
      this.port = port;
      return this;
   }

   public String getUser() {
      return this.user;
   }

   public FtpConfig setUser(String user) {
      this.user = user;
      return this;
   }

   public String getPassword() {
      return this.password;
   }

   public FtpConfig setPassword(String password) {
      this.password = password;
      return this;
   }

   public Charset getCharset() {
      return this.charset;
   }

   public FtpConfig setCharset(Charset charset) {
      this.charset = charset;
      return this;
   }

   public long getConnectionTimeout() {
      return this.connectionTimeout;
   }

   public FtpConfig setConnectionTimeout(long connectionTimeout) {
      this.connectionTimeout = connectionTimeout;
      return this;
   }

   public long getSoTimeout() {
      return this.soTimeout;
   }

   public FtpConfig setSoTimeout(long soTimeout) {
      this.soTimeout = soTimeout;
      return this;
   }

   public String getServerLanguageCode() {
      return this.serverLanguageCode;
   }

   public FtpConfig setServerLanguageCode(String serverLanguageCode) {
      this.serverLanguageCode = serverLanguageCode;
      return this;
   }

   public String getSystemKey() {
      return this.systemKey;
   }

   public FtpConfig setSystemKey(String systemKey) {
      this.systemKey = systemKey;
      return this;
   }
}
