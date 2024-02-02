package cn.hutool.db.ds.pooled;

import cn.hutool.db.DbRuntimeException;
import cn.hutool.db.dialect.DriverUtil;
import java.util.Properties;

public class DbConfig {
   private String driver;
   private String url;
   private String user;
   private String pass;
   private int initialSize;
   private int minIdle;
   private int maxActive;
   private long maxWait;
   private Properties connProps;

   public DbConfig() {
   }

   public DbConfig(String url, String user, String pass) {
      this.init(url, user, pass);
   }

   public void init(String url, String user, String pass) {
      this.url = url;
      this.user = user;
      this.pass = pass;
      this.driver = DriverUtil.identifyDriver(url);

      try {
         Class.forName(this.driver);
      } catch (ClassNotFoundException var5) {
         throw new DbRuntimeException(var5, "Get jdbc driver from [{}] error!", new Object[]{url});
      }
   }

   public String getDriver() {
      return this.driver;
   }

   public void setDriver(String driver) {
      this.driver = driver;
   }

   public String getUrl() {
      return this.url;
   }

   public void setUrl(String url) {
      this.url = url;
   }

   public String getUser() {
      return this.user;
   }

   public void setUser(String user) {
      this.user = user;
   }

   public String getPass() {
      return this.pass;
   }

   public void setPass(String pass) {
      this.pass = pass;
   }

   public int getInitialSize() {
      return this.initialSize;
   }

   public void setInitialSize(int initialSize) {
      this.initialSize = initialSize;
   }

   public int getMinIdle() {
      return this.minIdle;
   }

   public void setMinIdle(int minIdle) {
      this.minIdle = minIdle;
   }

   public int getMaxActive() {
      return this.maxActive;
   }

   public void setMaxActive(int maxActive) {
      this.maxActive = maxActive;
   }

   public long getMaxWait() {
      return this.maxWait;
   }

   public void setMaxWait(long maxWait) {
      this.maxWait = maxWait;
   }

   public Properties getConnProps() {
      return this.connProps;
   }

   public void setConnProps(Properties connProps) {
      this.connProps = connProps;
   }

   public void addConnProps(String key, String value) {
      if (null == this.connProps) {
         this.connProps = new Properties();
      }

      this.connProps.setProperty(key, value);
   }
}
