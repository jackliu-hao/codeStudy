package cn.hutool.extra.ssh;

public class Connector {
   private String host;
   private int port;
   private String user;
   private String password;
   private String group;

   public Connector() {
   }

   public Connector(String user, String password, String group) {
      this.user = user;
      this.password = password;
      this.group = group;
   }

   public Connector(String host, int port, String user, String password) {
      this.host = host;
      this.port = port;
      this.user = user;
      this.password = password;
   }

   public String getHost() {
      return this.host;
   }

   public void setHost(String host) {
      this.host = host;
   }

   public int getPort() {
      return this.port;
   }

   public void setPort(int port) {
      this.port = port;
   }

   public String getUser() {
      return this.user;
   }

   public void setUser(String name) {
      this.user = name;
   }

   public String getPassword() {
      return this.password;
   }

   public void setPassword(String password) {
      this.password = password;
   }

   public String getGroup() {
      return this.group;
   }

   public void setGroup(String group) {
      this.group = group;
   }

   public String toString() {
      return "Connector [host=" + this.host + ", port=" + this.port + ", user=" + this.user + ", password=" + this.password + "]";
   }
}
