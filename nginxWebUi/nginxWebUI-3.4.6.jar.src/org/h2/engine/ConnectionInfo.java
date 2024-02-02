/*     */ package org.h2.engine;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.util.Arrays;
/*     */ import java.util.HashMap;
/*     */ import java.util.HashSet;
/*     */ import java.util.Properties;
/*     */ import org.h2.command.dml.SetTypes;
/*     */ import org.h2.message.DbException;
/*     */ import org.h2.security.SHA256;
/*     */ import org.h2.store.fs.FileUtils;
/*     */ import org.h2.store.fs.encrypt.FilePathEncrypt;
/*     */ import org.h2.store.fs.rec.FilePathRec;
/*     */ import org.h2.util.IOUtils;
/*     */ import org.h2.util.NetworkConnectionInfo;
/*     */ import org.h2.util.SortedProperties;
/*     */ import org.h2.util.StringUtils;
/*     */ import org.h2.util.TimeZoneProvider;
/*     */ import org.h2.util.Utils;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class ConnectionInfo
/*     */   implements Cloneable
/*     */ {
/*     */   private static final HashSet<String> KNOWN_SETTINGS;
/*     */   private static final HashSet<String> IGNORED_BY_PARSER;
/*  38 */   private Properties prop = new Properties();
/*     */   
/*     */   private String originalURL;
/*     */   
/*     */   private String url;
/*     */   
/*     */   private String user;
/*     */   
/*     */   private byte[] filePasswordHash;
/*     */   
/*     */   private byte[] fileEncryptionKey;
/*     */   
/*     */   private byte[] userPasswordHash;
/*     */   
/*     */   private TimeZoneProvider timeZone;
/*     */   
/*     */   private String name;
/*     */   
/*     */   private String nameNormalized;
/*     */   
/*     */   private boolean remote;
/*     */   
/*     */   private boolean ssl;
/*     */   
/*     */   private boolean persistent;
/*     */   private boolean unnamed;
/*     */   private NetworkConnectionInfo networkConnectionInfo;
/*     */   
/*     */   public ConnectionInfo(String paramString) {
/*  67 */     this.name = paramString;
/*  68 */     this.url = "jdbc:h2:" + paramString;
/*  69 */     parseName();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ConnectionInfo(String paramString1, Properties paramProperties, String paramString2, Object paramObject) {
/*  83 */     paramString1 = remapURL(paramString1);
/*  84 */     this.originalURL = this.url = paramString1;
/*  85 */     if (!paramString1.startsWith("jdbc:h2:")) {
/*  86 */       throw getFormatException();
/*     */     }
/*  88 */     if (paramProperties != null) {
/*  89 */       readProperties(paramProperties);
/*     */     }
/*  91 */     if (paramString2 != null) {
/*  92 */       this.prop.put("USER", paramString2);
/*     */     }
/*  94 */     if (paramObject != null) {
/*  95 */       this.prop.put("PASSWORD", paramObject);
/*     */     }
/*  97 */     readSettingsFromURL();
/*  98 */     Object object = this.prop.remove("TIME ZONE");
/*  99 */     if (object != null) {
/* 100 */       this.timeZone = TimeZoneProvider.ofId(object.toString());
/*     */     }
/* 102 */     setUserName(removeProperty("USER", ""));
/* 103 */     this.name = this.url.substring("jdbc:h2:".length());
/* 104 */     parseName();
/* 105 */     convertPasswords();
/* 106 */     String str = removeProperty("RECOVER_TEST", (String)null);
/* 107 */     if (str != null) {
/* 108 */       FilePathRec.register();
/*     */       try {
/* 110 */         Utils.callStaticMethod("org.h2.store.RecoverTester.init", new Object[] { str });
/* 111 */       } catch (Exception exception) {
/* 112 */         throw DbException.convert(exception);
/*     */       } 
/* 114 */       this.name = "rec:" + this.name;
/*     */     } 
/*     */   }
/*     */   
/*     */   static {
/* 119 */     String[] arrayOfString1 = { "ACCESS_MODE_DATA", "AUTO_RECONNECT", "AUTO_SERVER", "AUTO_SERVER_PORT", "CACHE_TYPE", "FILE_LOCK", "JMX", "NETWORK_TIMEOUT", "OLD_INFORMATION_SCHEMA", "OPEN_NEW", "PAGE_SIZE", "RECOVER" };
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 129 */     String[] arrayOfString2 = { "AUTHREALM", "AUTHZPWD", "AUTOCOMMIT", "CIPHER", "CREATE", "FORBID_CREATION", "IGNORE_UNKNOWN_SETTINGS", "IFEXISTS", "INIT", "NO_UPGRADE", "PASSWORD", "PASSWORD_HASH", "RECOVER_TEST", "USER" };
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 139 */     HashSet<String> hashSet = new HashSet(128);
/* 140 */     hashSet.addAll(SetTypes.getTypes());
/* 141 */     for (String str : arrayOfString1) {
/* 142 */       if (!hashSet.add(str)) {
/* 143 */         throw DbException.getInternalError(str);
/*     */       }
/*     */     } 
/* 146 */     for (String str : arrayOfString2) {
/* 147 */       if (!hashSet.add(str)) {
/* 148 */         throw DbException.getInternalError(str);
/*     */       }
/*     */     } 
/* 151 */     KNOWN_SETTINGS = hashSet;
/* 152 */     arrayOfString2 = new String[] { "ASSERT", "BINARY_COLLATION", "DB_CLOSE_ON_EXIT", "PAGE_STORE", "UUID_COLLATION" };
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 159 */     hashSet = new HashSet<>(32);
/* 160 */     for (String str : arrayOfString1) {
/* 161 */       hashSet.add(str);
/*     */     }
/* 163 */     for (String str : arrayOfString2) {
/* 164 */       hashSet.add(str);
/*     */     }
/* 166 */     IGNORED_BY_PARSER = hashSet;
/*     */   }
/*     */   
/*     */   private static boolean isKnownSetting(String paramString) {
/* 170 */     return KNOWN_SETTINGS.contains(paramString);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean isIgnoredByParser(String paramString) {
/* 183 */     return IGNORED_BY_PARSER.contains(paramString);
/*     */   }
/*     */ 
/*     */   
/*     */   public ConnectionInfo clone() throws CloneNotSupportedException {
/* 188 */     ConnectionInfo connectionInfo = (ConnectionInfo)super.clone();
/* 189 */     connectionInfo.prop = (Properties)this.prop.clone();
/* 190 */     connectionInfo.filePasswordHash = Utils.cloneByteArray(this.filePasswordHash);
/* 191 */     connectionInfo.fileEncryptionKey = Utils.cloneByteArray(this.fileEncryptionKey);
/* 192 */     connectionInfo.userPasswordHash = Utils.cloneByteArray(this.userPasswordHash);
/* 193 */     return connectionInfo;
/*     */   }
/*     */   
/*     */   private void parseName() {
/* 197 */     if (".".equals(this.name)) {
/* 198 */       this.name = "mem:";
/*     */     }
/* 200 */     if (this.name.startsWith("tcp:")) {
/* 201 */       this.remote = true;
/* 202 */       this.name = this.name.substring("tcp:".length());
/* 203 */     } else if (this.name.startsWith("ssl:")) {
/* 204 */       this.remote = true;
/* 205 */       this.ssl = true;
/* 206 */       this.name = this.name.substring("ssl:".length());
/* 207 */     } else if (this.name.startsWith("mem:")) {
/* 208 */       this.persistent = false;
/* 209 */       if ("mem:".equals(this.name)) {
/* 210 */         this.unnamed = true;
/*     */       }
/* 212 */     } else if (this.name.startsWith("file:")) {
/* 213 */       this.name = this.name.substring("file:".length());
/* 214 */       this.persistent = true;
/*     */     } else {
/* 216 */       this.persistent = true;
/*     */     } 
/* 218 */     if (this.persistent && !this.remote) {
/* 219 */       this.name = IOUtils.nameSeparatorsToNative(this.name);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setBaseDir(String paramString) {
/* 230 */     if (this.persistent) {
/* 231 */       String str2, str1 = FileUtils.unwrap(FileUtils.toRealPath(paramString));
/* 232 */       boolean bool = FileUtils.isAbsolute(this.name);
/*     */       
/* 234 */       String str3 = null;
/* 235 */       if (paramString.endsWith(File.separator)) {
/* 236 */         paramString = paramString.substring(0, paramString.length() - 1);
/*     */       }
/* 238 */       if (bool) {
/* 239 */         str2 = this.name;
/*     */       } else {
/* 241 */         str2 = FileUtils.unwrap(this.name);
/* 242 */         str3 = this.name.substring(0, this.name.length() - str2.length());
/* 243 */         str2 = paramString + File.separatorChar + str2;
/*     */       } 
/* 245 */       String str4 = FileUtils.unwrap(FileUtils.toRealPath(str2));
/* 246 */       if (str4.equals(str1) || !str4.startsWith(str1))
/*     */       {
/*     */         
/* 249 */         throw DbException.get(90028, str4 + " outside " + str1);
/*     */       }
/*     */       
/* 252 */       if (!str1.endsWith("/") && !str1.endsWith("\\"))
/*     */       {
/* 254 */         if (str4.charAt(str1.length()) != '/')
/*     */         {
/*     */ 
/*     */           
/* 258 */           throw DbException.get(90028, str4 + " outside " + str1);
/*     */         }
/*     */       }
/* 261 */       if (!bool) {
/* 262 */         this.name = str3 + paramString + File.separatorChar + FileUtils.unwrap(this.name);
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isRemote() {
/* 273 */     return this.remote;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isPersistent() {
/* 282 */     return this.persistent;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   boolean isUnnamedInMemory() {
/* 291 */     return this.unnamed;
/*     */   }
/*     */   
/*     */   private void readProperties(Properties paramProperties) {
/* 295 */     Object[] arrayOfObject = paramProperties.keySet().toArray();
/* 296 */     DbSettings dbSettings = null;
/* 297 */     for (Object object1 : arrayOfObject) {
/* 298 */       String str = StringUtils.toUpperEnglish(object1.toString());
/* 299 */       if (this.prop.containsKey(str)) {
/* 300 */         throw DbException.get(90066, str);
/*     */       }
/* 302 */       Object object2 = paramProperties.get(object1);
/* 303 */       if (isKnownSetting(str)) {
/* 304 */         this.prop.put(str, object2);
/*     */       } else {
/* 306 */         if (dbSettings == null) {
/* 307 */           dbSettings = getDbSettings();
/*     */         }
/* 309 */         if (dbSettings.containsKey(str)) {
/* 310 */           this.prop.put(str, object2);
/*     */         }
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   private void readSettingsFromURL() {
/* 317 */     DbSettings dbSettings = DbSettings.DEFAULT;
/* 318 */     int i = this.url.indexOf(';');
/* 319 */     if (i >= 0) {
/* 320 */       String str1 = this.url.substring(i + 1);
/* 321 */       this.url = this.url.substring(0, i);
/* 322 */       String str2 = null;
/* 323 */       String[] arrayOfString = StringUtils.arraySplit(str1, ';', false);
/* 324 */       for (String str : arrayOfString) {
/* 325 */         if (!str.isEmpty()) {
/*     */ 
/*     */           
/* 328 */           int j = str.indexOf('=');
/* 329 */           if (j < 0) {
/* 330 */             throw getFormatException();
/*     */           }
/* 332 */           String str3 = str.substring(j + 1);
/* 333 */           String str4 = str.substring(0, j);
/* 334 */           str4 = StringUtils.toUpperEnglish(str4);
/* 335 */           if (isKnownSetting(str4) || dbSettings.containsKey(str4)) {
/* 336 */             String str5 = this.prop.getProperty(str4);
/* 337 */             if (str5 != null && !str5.equals(str3)) {
/* 338 */               throw DbException.get(90066, str4);
/*     */             }
/* 340 */             this.prop.setProperty(str4, str3);
/*     */           } else {
/* 342 */             str2 = str4;
/*     */           } 
/*     */         } 
/* 345 */       }  if (str2 != null && 
/* 346 */         !Utils.parseBoolean(this.prop.getProperty("IGNORE_UNKNOWN_SETTINGS"), false, false)) {
/* 347 */         throw DbException.get(90113, str2);
/*     */       }
/*     */     } 
/*     */   }
/*     */   
/*     */   private void preservePasswordForAuthentication(Object paramObject) {
/* 353 */     if ((!isRemote() || isSSL()) && this.prop.containsKey("AUTHREALM") && paramObject != null) {
/* 354 */       this.prop.put("AUTHZPWD", (paramObject instanceof char[]) ? new String((char[])paramObject) : paramObject);
/*     */     }
/*     */   }
/*     */   
/*     */   private char[] removePassword() {
/* 359 */     Object object = this.prop.remove("PASSWORD");
/* 360 */     preservePasswordForAuthentication(object);
/* 361 */     if (object == null)
/* 362 */       return new char[0]; 
/* 363 */     if (object instanceof char[]) {
/* 364 */       return (char[])object;
/*     */     }
/* 366 */     return object.toString().toCharArray();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void convertPasswords() {
/* 375 */     char[] arrayOfChar = removePassword();
/* 376 */     boolean bool = removeProperty("PASSWORD_HASH", false);
/* 377 */     if (getProperty("CIPHER", (String)null) != null) {
/*     */       
/* 379 */       byte b = -1; byte b1; int i;
/* 380 */       for (b1 = 0, i = arrayOfChar.length; b1 < i; b1++) {
/* 381 */         if (arrayOfChar[b1] == ' ') {
/* 382 */           b = b1;
/*     */           break;
/*     */         } 
/*     */       } 
/* 386 */       if (b < 0) {
/* 387 */         throw DbException.get(90050);
/*     */       }
/* 389 */       char[] arrayOfChar1 = Arrays.copyOfRange(arrayOfChar, b + 1, arrayOfChar.length);
/* 390 */       char[] arrayOfChar2 = Arrays.copyOf(arrayOfChar, b);
/* 391 */       Arrays.fill(arrayOfChar, false);
/* 392 */       arrayOfChar = arrayOfChar1;
/* 393 */       this.fileEncryptionKey = FilePathEncrypt.getPasswordBytes(arrayOfChar2);
/* 394 */       this.filePasswordHash = hashPassword(bool, "file", arrayOfChar2);
/*     */     } 
/* 396 */     this.userPasswordHash = hashPassword(bool, this.user, arrayOfChar);
/*     */   }
/*     */ 
/*     */   
/*     */   private static byte[] hashPassword(boolean paramBoolean, String paramString, char[] paramArrayOfchar) {
/* 401 */     if (paramBoolean) {
/* 402 */       return StringUtils.convertHexToBytes(new String(paramArrayOfchar));
/*     */     }
/* 404 */     if (paramString.isEmpty() && paramArrayOfchar.length == 0) {
/* 405 */       return new byte[0];
/*     */     }
/* 407 */     return SHA256.getKeyPasswordHash(paramString, paramArrayOfchar);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean getProperty(String paramString, boolean paramBoolean) {
/* 418 */     return Utils.parseBoolean(getProperty(paramString, (String)null), paramBoolean, false);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean removeProperty(String paramString, boolean paramBoolean) {
/* 429 */     return Utils.parseBoolean(removeProperty(paramString, (String)null), paramBoolean, false);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   String removeProperty(String paramString1, String paramString2) {
/* 440 */     if (SysProperties.CHECK && !isKnownSetting(paramString1)) {
/* 441 */       throw DbException.getInternalError(paramString1);
/*     */     }
/* 443 */     Object object = this.prop.remove(paramString1);
/* 444 */     return (object == null) ? paramString2 : object.toString();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getName() {
/* 453 */     if (!this.persistent) {
/* 454 */       return this.name;
/*     */     }
/* 456 */     if (this.nameNormalized == null) {
/* 457 */       if (!FileUtils.isAbsolute(this.name) && !this.name.contains("./") && !this.name.contains(".\\") && !this.name.contains(":/") && 
/* 458 */         !this.name.contains(":\\"))
/*     */       {
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 464 */         throw DbException.get(90011, this.originalURL);
/*     */       }
/* 466 */       String str1 = ".mv.db";
/* 467 */       String str2 = FileUtils.toRealPath(this.name + str1);
/* 468 */       String str3 = FileUtils.getName(str2);
/* 469 */       if (str3.length() < str1.length() + 1) {
/* 470 */         throw DbException.get(90138, this.name);
/*     */       }
/* 472 */       this.nameNormalized = str2.substring(0, str2.length() - str1.length());
/*     */     } 
/* 474 */     return this.nameNormalized;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public byte[] getFilePasswordHash() {
/* 483 */     return this.filePasswordHash;
/*     */   }
/*     */   
/*     */   byte[] getFileEncryptionKey() {
/* 487 */     return this.fileEncryptionKey;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getUserName() {
/* 496 */     return this.user;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   byte[] getUserPasswordHash() {
/* 505 */     return this.userPasswordHash;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   String[] getKeys() {
/* 514 */     return this.prop.keySet().<String>toArray(new String[this.prop.size()]);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   String getProperty(String paramString) {
/* 524 */     Object object = this.prop.get(paramString);
/* 525 */     if (!(object instanceof String)) {
/* 526 */       return null;
/*     */     }
/* 528 */     return object.toString();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   int getProperty(String paramString, int paramInt) {
/* 539 */     if (SysProperties.CHECK && !isKnownSetting(paramString)) {
/* 540 */       throw DbException.getInternalError(paramString);
/*     */     }
/* 542 */     String str = getProperty(paramString);
/* 543 */     return (str == null) ? paramInt : Integer.parseInt(str);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getProperty(String paramString1, String paramString2) {
/* 554 */     if (SysProperties.CHECK && !isKnownSetting(paramString1)) {
/* 555 */       throw DbException.getInternalError(paramString1);
/*     */     }
/* 557 */     String str = getProperty(paramString1);
/* 558 */     return (str == null) ? paramString2 : str;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   String getProperty(int paramInt, String paramString) {
/* 569 */     String str1 = SetTypes.getTypeName(paramInt);
/* 570 */     String str2 = getProperty(str1);
/* 571 */     return (str2 == null) ? paramString : str2;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   int getIntProperty(int paramInt1, int paramInt2) {
/* 582 */     String str1 = SetTypes.getTypeName(paramInt1);
/* 583 */     String str2 = getProperty(str1, (String)null);
/*     */     try {
/* 585 */       return (str2 == null) ? paramInt2 : Integer.decode(str2).intValue();
/* 586 */     } catch (NumberFormatException numberFormatException) {
/* 587 */       return paramInt2;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   boolean isSSL() {
/* 597 */     return this.ssl;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setUserName(String paramString) {
/* 607 */     this.user = StringUtils.toUpperEnglish(paramString);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setUserPasswordHash(byte[] paramArrayOfbyte) {
/* 616 */     this.userPasswordHash = paramArrayOfbyte;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setFilePasswordHash(byte[] paramArrayOfbyte) {
/* 625 */     this.filePasswordHash = paramArrayOfbyte;
/*     */   }
/*     */   
/*     */   public void setFileEncryptionKey(byte[] paramArrayOfbyte) {
/* 629 */     this.fileEncryptionKey = paramArrayOfbyte;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setProperty(String paramString1, String paramString2) {
/* 640 */     if (paramString2 != null) {
/* 641 */       this.prop.setProperty(paramString1, paramString2);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getURL() {
/* 651 */     return this.url;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getOriginalURL() {
/* 660 */     return this.originalURL;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setOriginalURL(String paramString) {
/* 669 */     this.originalURL = paramString;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public TimeZoneProvider getTimeZone() {
/* 678 */     return this.timeZone;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   DbException getFormatException() {
/* 687 */     return DbException.get(90046, new String[] { "jdbc:h2:{ {.|mem:}[name] | [file:]fileName | {tcp|ssl}:[//]server[:port][,server2[:port]]/name }[;key=value...]", this.url });
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setServerKey(String paramString) {
/* 696 */     this.remote = true;
/* 697 */     this.persistent = false;
/* 698 */     this.name = paramString;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public NetworkConnectionInfo getNetworkConnectionInfo() {
/* 707 */     return this.networkConnectionInfo;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setNetworkConnectionInfo(NetworkConnectionInfo paramNetworkConnectionInfo) {
/* 716 */     this.networkConnectionInfo = paramNetworkConnectionInfo;
/*     */   }
/*     */   
/*     */   public DbSettings getDbSettings() {
/* 720 */     DbSettings dbSettings = DbSettings.DEFAULT;
/* 721 */     HashMap<Object, Object> hashMap = new HashMap<>(64);
/* 722 */     for (Object object : this.prop.keySet()) {
/* 723 */       String str = object.toString();
/* 724 */       if (!isKnownSetting(str) && dbSettings.containsKey(str)) {
/* 725 */         hashMap.put(str, this.prop.getProperty(str));
/*     */       }
/*     */     } 
/* 728 */     return DbSettings.getInstance((HashMap)hashMap);
/*     */   }
/*     */   
/*     */   private static String remapURL(String paramString) {
/* 732 */     String str = SysProperties.URL_MAP;
/* 733 */     if (str != null && !str.isEmpty()) {
/*     */       
/*     */       try {
/* 736 */         SortedProperties sortedProperties = SortedProperties.loadProperties(str);
/* 737 */         String str1 = sortedProperties.getProperty(paramString);
/* 738 */         if (str1 == null) {
/* 739 */           sortedProperties.put(paramString, "");
/* 740 */           sortedProperties.store(str);
/*     */         } else {
/* 742 */           str1 = str1.trim();
/* 743 */           if (!str1.isEmpty()) {
/* 744 */             return str1;
/*     */           }
/*     */         } 
/* 747 */       } catch (IOException iOException) {
/* 748 */         throw DbException.convert(iOException);
/*     */       } 
/*     */     }
/* 751 */     return paramString;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void cleanAuthenticationInfo() {
/* 758 */     removeProperty("AUTHREALM", false);
/* 759 */     removeProperty("AUTHZPWD", false);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\engine\ConnectionInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */