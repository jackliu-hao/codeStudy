package org.h2.engine;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Properties;
import org.h2.command.dml.SetTypes;
import org.h2.message.DbException;
import org.h2.security.SHA256;
import org.h2.store.fs.FileUtils;
import org.h2.store.fs.encrypt.FilePathEncrypt;
import org.h2.store.fs.rec.FilePathRec;
import org.h2.util.IOUtils;
import org.h2.util.NetworkConnectionInfo;
import org.h2.util.SortedProperties;
import org.h2.util.StringUtils;
import org.h2.util.TimeZoneProvider;
import org.h2.util.Utils;

public class ConnectionInfo implements Cloneable {
   private static final HashSet<String> KNOWN_SETTINGS;
   private static final HashSet<String> IGNORED_BY_PARSER;
   private Properties prop = new Properties();
   private String originalURL;
   private String url;
   private String user;
   private byte[] filePasswordHash;
   private byte[] fileEncryptionKey;
   private byte[] userPasswordHash;
   private TimeZoneProvider timeZone;
   private String name;
   private String nameNormalized;
   private boolean remote;
   private boolean ssl;
   private boolean persistent;
   private boolean unnamed;
   private NetworkConnectionInfo networkConnectionInfo;

   public ConnectionInfo(String var1) {
      this.name = var1;
      this.url = "jdbc:h2:" + var1;
      this.parseName();
   }

   public ConnectionInfo(String var1, Properties var2, String var3, Object var4) {
      var1 = remapURL(var1);
      this.originalURL = this.url = var1;
      if (!var1.startsWith("jdbc:h2:")) {
         throw this.getFormatException();
      } else {
         if (var2 != null) {
            this.readProperties(var2);
         }

         if (var3 != null) {
            this.prop.put("USER", var3);
         }

         if (var4 != null) {
            this.prop.put("PASSWORD", var4);
         }

         this.readSettingsFromURL();
         Object var5 = this.prop.remove("TIME ZONE");
         if (var5 != null) {
            this.timeZone = TimeZoneProvider.ofId(var5.toString());
         }

         this.setUserName(this.removeProperty("USER", ""));
         this.name = this.url.substring("jdbc:h2:".length());
         this.parseName();
         this.convertPasswords();
         String var6 = this.removeProperty("RECOVER_TEST", (String)null);
         if (var6 != null) {
            FilePathRec.register();

            try {
               Utils.callStaticMethod("org.h2.store.RecoverTester.init", var6);
            } catch (Exception var8) {
               throw DbException.convert(var8);
            }

            this.name = "rec:" + this.name;
         }

      }
   }

   private static boolean isKnownSetting(String var0) {
      return KNOWN_SETTINGS.contains(var0);
   }

   public static boolean isIgnoredByParser(String var0) {
      return IGNORED_BY_PARSER.contains(var0);
   }

   public ConnectionInfo clone() throws CloneNotSupportedException {
      ConnectionInfo var1 = (ConnectionInfo)super.clone();
      var1.prop = (Properties)this.prop.clone();
      var1.filePasswordHash = Utils.cloneByteArray(this.filePasswordHash);
      var1.fileEncryptionKey = Utils.cloneByteArray(this.fileEncryptionKey);
      var1.userPasswordHash = Utils.cloneByteArray(this.userPasswordHash);
      return var1;
   }

   private void parseName() {
      if (".".equals(this.name)) {
         this.name = "mem:";
      }

      if (this.name.startsWith("tcp:")) {
         this.remote = true;
         this.name = this.name.substring("tcp:".length());
      } else if (this.name.startsWith("ssl:")) {
         this.remote = true;
         this.ssl = true;
         this.name = this.name.substring("ssl:".length());
      } else if (this.name.startsWith("mem:")) {
         this.persistent = false;
         if ("mem:".equals(this.name)) {
            this.unnamed = true;
         }
      } else if (this.name.startsWith("file:")) {
         this.name = this.name.substring("file:".length());
         this.persistent = true;
      } else {
         this.persistent = true;
      }

      if (this.persistent && !this.remote) {
         this.name = IOUtils.nameSeparatorsToNative(this.name);
      }

   }

   public void setBaseDir(String var1) {
      if (this.persistent) {
         String var2 = FileUtils.unwrap(FileUtils.toRealPath(var1));
         boolean var3 = FileUtils.isAbsolute(this.name);
         String var5 = null;
         if (var1.endsWith(File.separator)) {
            var1 = var1.substring(0, var1.length() - 1);
         }

         String var4;
         if (var3) {
            var4 = this.name;
         } else {
            var4 = FileUtils.unwrap(this.name);
            var5 = this.name.substring(0, this.name.length() - var4.length());
            var4 = var1 + File.separatorChar + var4;
         }

         String var6 = FileUtils.unwrap(FileUtils.toRealPath(var4));
         if (var6.equals(var2) || !var6.startsWith(var2)) {
            throw DbException.get(90028, var6 + " outside " + var2);
         }

         if (!var2.endsWith("/") && !var2.endsWith("\\") && var6.charAt(var2.length()) != '/') {
            throw DbException.get(90028, var6 + " outside " + var2);
         }

         if (!var3) {
            this.name = var5 + var1 + File.separatorChar + FileUtils.unwrap(this.name);
         }
      }

   }

   public boolean isRemote() {
      return this.remote;
   }

   public boolean isPersistent() {
      return this.persistent;
   }

   boolean isUnnamedInMemory() {
      return this.unnamed;
   }

   private void readProperties(Properties var1) {
      Object[] var2 = var1.keySet().toArray();
      DbSettings var3 = null;
      Object[] var4 = var2;
      int var5 = var2.length;

      for(int var6 = 0; var6 < var5; ++var6) {
         Object var7 = var4[var6];
         String var8 = StringUtils.toUpperEnglish(var7.toString());
         if (this.prop.containsKey(var8)) {
            throw DbException.get(90066, var8);
         }

         Object var9 = var1.get(var7);
         if (isKnownSetting(var8)) {
            this.prop.put(var8, var9);
         } else {
            if (var3 == null) {
               var3 = this.getDbSettings();
            }

            if (var3.containsKey(var8)) {
               this.prop.put(var8, var9);
            }
         }
      }

   }

   private void readSettingsFromURL() {
      DbSettings var1 = DbSettings.DEFAULT;
      int var2 = this.url.indexOf(59);
      if (var2 >= 0) {
         String var3 = this.url.substring(var2 + 1);
         this.url = this.url.substring(0, var2);
         String var4 = null;
         String[] var5 = StringUtils.arraySplit(var3, ';', false);
         String[] var6 = var5;
         int var7 = var5.length;

         for(int var8 = 0; var8 < var7; ++var8) {
            String var9 = var6[var8];
            if (!var9.isEmpty()) {
               int var10 = var9.indexOf(61);
               if (var10 < 0) {
                  throw this.getFormatException();
               }

               String var11 = var9.substring(var10 + 1);
               String var12 = var9.substring(0, var10);
               var12 = StringUtils.toUpperEnglish(var12);
               if (!isKnownSetting(var12) && !var1.containsKey(var12)) {
                  var4 = var12;
               } else {
                  String var13 = this.prop.getProperty(var12);
                  if (var13 != null && !var13.equals(var11)) {
                     throw DbException.get(90066, var12);
                  }

                  this.prop.setProperty(var12, var11);
               }
            }
         }

         if (var4 != null && !Utils.parseBoolean(this.prop.getProperty("IGNORE_UNKNOWN_SETTINGS"), false, false)) {
            throw DbException.get(90113, var4);
         }
      }

   }

   private void preservePasswordForAuthentication(Object var1) {
      if ((!this.isRemote() || this.isSSL()) && this.prop.containsKey("AUTHREALM") && var1 != null) {
         this.prop.put("AUTHZPWD", var1 instanceof char[] ? new String((char[])((char[])var1)) : var1);
      }

   }

   private char[] removePassword() {
      Object var1 = this.prop.remove("PASSWORD");
      this.preservePasswordForAuthentication(var1);
      if (var1 == null) {
         return new char[0];
      } else {
         return var1 instanceof char[] ? (char[])((char[])var1) : var1.toString().toCharArray();
      }
   }

   private void convertPasswords() {
      char[] var1 = this.removePassword();
      boolean var2 = this.removeProperty("PASSWORD_HASH", false);
      if (this.getProperty("CIPHER", (String)null) != null) {
         int var3 = -1;
         int var4 = 0;

         for(int var5 = var1.length; var4 < var5; ++var4) {
            if (var1[var4] == ' ') {
               var3 = var4;
               break;
            }
         }

         if (var3 < 0) {
            throw DbException.get(90050);
         }

         char[] var6 = Arrays.copyOfRange(var1, var3 + 1, var1.length);
         char[] var7 = Arrays.copyOf(var1, var3);
         Arrays.fill(var1, '\u0000');
         var1 = var6;
         this.fileEncryptionKey = FilePathEncrypt.getPasswordBytes(var7);
         this.filePasswordHash = hashPassword(var2, "file", var7);
      }

      this.userPasswordHash = hashPassword(var2, this.user, var1);
   }

   private static byte[] hashPassword(boolean var0, String var1, char[] var2) {
      if (var0) {
         return StringUtils.convertHexToBytes(new String(var2));
      } else {
         return var1.isEmpty() && var2.length == 0 ? new byte[0] : SHA256.getKeyPasswordHash(var1, var2);
      }
   }

   public boolean getProperty(String var1, boolean var2) {
      return Utils.parseBoolean(this.getProperty(var1, (String)null), var2, false);
   }

   public boolean removeProperty(String var1, boolean var2) {
      return Utils.parseBoolean(this.removeProperty(var1, (String)null), var2, false);
   }

   String removeProperty(String var1, String var2) {
      if (SysProperties.CHECK && !isKnownSetting(var1)) {
         throw DbException.getInternalError(var1);
      } else {
         Object var3 = this.prop.remove(var1);
         return var3 == null ? var2 : var3.toString();
      }
   }

   public String getName() {
      if (!this.persistent) {
         return this.name;
      } else {
         if (this.nameNormalized == null) {
            if (!FileUtils.isAbsolute(this.name) && !this.name.contains("./") && !this.name.contains(".\\") && !this.name.contains(":/") && !this.name.contains(":\\")) {
               throw DbException.get(90011, this.originalURL);
            }

            String var1 = ".mv.db";
            String var2 = FileUtils.toRealPath(this.name + var1);
            String var3 = FileUtils.getName(var2);
            if (var3.length() < var1.length() + 1) {
               throw DbException.get(90138, this.name);
            }

            this.nameNormalized = var2.substring(0, var2.length() - var1.length());
         }

         return this.nameNormalized;
      }
   }

   public byte[] getFilePasswordHash() {
      return this.filePasswordHash;
   }

   byte[] getFileEncryptionKey() {
      return this.fileEncryptionKey;
   }

   public String getUserName() {
      return this.user;
   }

   byte[] getUserPasswordHash() {
      return this.userPasswordHash;
   }

   String[] getKeys() {
      return (String[])this.prop.keySet().toArray(new String[this.prop.size()]);
   }

   String getProperty(String var1) {
      Object var2 = this.prop.get(var1);
      return !(var2 instanceof String) ? null : var2.toString();
   }

   int getProperty(String var1, int var2) {
      if (SysProperties.CHECK && !isKnownSetting(var1)) {
         throw DbException.getInternalError(var1);
      } else {
         String var3 = this.getProperty(var1);
         return var3 == null ? var2 : Integer.parseInt(var3);
      }
   }

   public String getProperty(String var1, String var2) {
      if (SysProperties.CHECK && !isKnownSetting(var1)) {
         throw DbException.getInternalError(var1);
      } else {
         String var3 = this.getProperty(var1);
         return var3 == null ? var2 : var3;
      }
   }

   String getProperty(int var1, String var2) {
      String var3 = SetTypes.getTypeName(var1);
      String var4 = this.getProperty(var3);
      return var4 == null ? var2 : var4;
   }

   int getIntProperty(int var1, int var2) {
      String var3 = SetTypes.getTypeName(var1);
      String var4 = this.getProperty(var3, (String)null);

      try {
         return var4 == null ? var2 : Integer.decode(var4);
      } catch (NumberFormatException var6) {
         return var2;
      }
   }

   boolean isSSL() {
      return this.ssl;
   }

   public void setUserName(String var1) {
      this.user = StringUtils.toUpperEnglish(var1);
   }

   public void setUserPasswordHash(byte[] var1) {
      this.userPasswordHash = var1;
   }

   public void setFilePasswordHash(byte[] var1) {
      this.filePasswordHash = var1;
   }

   public void setFileEncryptionKey(byte[] var1) {
      this.fileEncryptionKey = var1;
   }

   public void setProperty(String var1, String var2) {
      if (var2 != null) {
         this.prop.setProperty(var1, var2);
      }

   }

   public String getURL() {
      return this.url;
   }

   public String getOriginalURL() {
      return this.originalURL;
   }

   public void setOriginalURL(String var1) {
      this.originalURL = var1;
   }

   public TimeZoneProvider getTimeZone() {
      return this.timeZone;
   }

   DbException getFormatException() {
      return DbException.get(90046, "jdbc:h2:{ {.|mem:}[name] | [file:]fileName | {tcp|ssl}:[//]server[:port][,server2[:port]]/name }[;key=value...]", this.url);
   }

   public void setServerKey(String var1) {
      this.remote = true;
      this.persistent = false;
      this.name = var1;
   }

   public NetworkConnectionInfo getNetworkConnectionInfo() {
      return this.networkConnectionInfo;
   }

   public void setNetworkConnectionInfo(NetworkConnectionInfo var1) {
      this.networkConnectionInfo = var1;
   }

   public DbSettings getDbSettings() {
      DbSettings var1 = DbSettings.DEFAULT;
      HashMap var2 = new HashMap(64);
      Iterator var3 = this.prop.keySet().iterator();

      while(var3.hasNext()) {
         Object var4 = var3.next();
         String var5 = var4.toString();
         if (!isKnownSetting(var5) && var1.containsKey(var5)) {
            var2.put(var5, this.prop.getProperty(var5));
         }
      }

      return DbSettings.getInstance(var2);
   }

   private static String remapURL(String var0) {
      String var1 = SysProperties.URL_MAP;
      if (var1 != null && !var1.isEmpty()) {
         try {
            SortedProperties var2 = SortedProperties.loadProperties(var1);
            String var3 = var2.getProperty(var0);
            if (var3 == null) {
               var2.put(var0, "");
               var2.store(var1);
            } else {
               var3 = var3.trim();
               if (!var3.isEmpty()) {
                  return var3;
               }
            }
         } catch (IOException var4) {
            throw DbException.convert(var4);
         }
      }

      return var0;
   }

   public void cleanAuthenticationInfo() {
      this.removeProperty("AUTHREALM", false);
      this.removeProperty("AUTHZPWD", false);
   }

   static {
      String[] var0 = new String[]{"ACCESS_MODE_DATA", "AUTO_RECONNECT", "AUTO_SERVER", "AUTO_SERVER_PORT", "CACHE_TYPE", "FILE_LOCK", "JMX", "NETWORK_TIMEOUT", "OLD_INFORMATION_SCHEMA", "OPEN_NEW", "PAGE_SIZE", "RECOVER"};
      String[] var1 = new String[]{"AUTHREALM", "AUTHZPWD", "AUTOCOMMIT", "CIPHER", "CREATE", "FORBID_CREATION", "IGNORE_UNKNOWN_SETTINGS", "IFEXISTS", "INIT", "NO_UPGRADE", "PASSWORD", "PASSWORD_HASH", "RECOVER_TEST", "USER"};
      HashSet var2 = new HashSet(128);
      var2.addAll(SetTypes.getTypes());
      String[] var3 = var0;
      int var4 = var0.length;

      int var5;
      String var6;
      for(var5 = 0; var5 < var4; ++var5) {
         var6 = var3[var5];
         if (!var2.add(var6)) {
            throw DbException.getInternalError(var6);
         }
      }

      var3 = var1;
      var4 = var1.length;

      for(var5 = 0; var5 < var4; ++var5) {
         var6 = var3[var5];
         if (!var2.add(var6)) {
            throw DbException.getInternalError(var6);
         }
      }

      KNOWN_SETTINGS = var2;
      var1 = new String[]{"ASSERT", "BINARY_COLLATION", "DB_CLOSE_ON_EXIT", "PAGE_STORE", "UUID_COLLATION"};
      var2 = new HashSet(32);
      var3 = var0;
      var4 = var0.length;

      for(var5 = 0; var5 < var4; ++var5) {
         var6 = var3[var5];
         var2.add(var6);
      }

      var3 = var1;
      var4 = var1.length;

      for(var5 = 0; var5 < var4; ++var5) {
         var6 = var3[var5];
         var2.add(var6);
      }

      IGNORED_BY_PARSER = var2;
   }
}
