/*     */ package com.mysql.cj;
/*     */ 
/*     */ import com.mysql.cj.conf.PropertyKey;
/*     */ import com.mysql.cj.conf.RuntimeProperty;
/*     */ import com.mysql.cj.exceptions.ExceptionFactory;
/*     */ import com.mysql.cj.exceptions.WrongArgumentException;
/*     */ import com.mysql.cj.protocol.ProtocolEntityFactory;
/*     */ import com.mysql.cj.protocol.Resultset;
/*     */ import com.mysql.cj.protocol.ServerCapabilities;
/*     */ import com.mysql.cj.protocol.ServerSession;
/*     */ import com.mysql.cj.protocol.a.NativeMessageBuilder;
/*     */ import com.mysql.cj.protocol.a.NativePacketPayload;
/*     */ import com.mysql.cj.protocol.a.ResultsetFactory;
/*     */ import com.mysql.cj.result.IntegerValueFactory;
/*     */ import com.mysql.cj.result.Row;
/*     */ import com.mysql.cj.result.StringValueFactory;
/*     */ import com.mysql.cj.result.ValueFactory;
/*     */ import com.mysql.cj.util.StringUtils;
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.OutputStreamWriter;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.nio.CharBuffer;
/*     */ import java.nio.charset.Charset;
/*     */ import java.nio.charset.CharsetEncoder;
/*     */ import java.nio.charset.UnsupportedCharsetException;
/*     */ import java.util.Collections;
/*     */ import java.util.HashMap;
/*     */ import java.util.HashSet;
/*     */ import java.util.Locale;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class NativeCharsetSettings
/*     */   extends CharsetMapping
/*     */   implements CharsetSettings
/*     */ {
/*     */   private NativeSession session;
/*     */   private ServerSession serverSession;
/*  70 */   public Map<Integer, String> collationIndexToCollationName = null;
/*  71 */   public Map<String, Integer> collationNameToCollationIndex = null;
/*  72 */   public Map<Integer, String> collationIndexToCharsetName = null;
/*  73 */   public Map<String, Integer> charsetNameToMblen = null;
/*  74 */   public Map<String, String> charsetNameToJavaEncoding = null;
/*  75 */   public Map<String, Integer> charsetNameToCollationIndex = null;
/*  76 */   public Map<String, String> javaEncodingUcToCharsetName = null;
/*  77 */   public Set<String> multibyteEncodings = null;
/*     */   
/*  79 */   private Integer sessionCollationIndex = null;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  84 */   private String metadataEncoding = null;
/*     */ 
/*     */ 
/*     */   
/*     */   private int metadataCollationIndex;
/*     */ 
/*     */   
/*  91 */   private String errorMessageEncoding = "Cp1252";
/*     */   
/*     */   protected RuntimeProperty<String> characterEncoding;
/*     */   
/*     */   protected RuntimeProperty<String> passwordCharacterEncoding;
/*     */   
/*     */   protected RuntimeProperty<String> characterSetResults;
/*     */   
/*     */   protected RuntimeProperty<String> connectionCollation;
/*     */   
/*     */   protected RuntimeProperty<Boolean> cacheServerConfiguration;
/*     */   
/*     */   private boolean requiresEscapingEncoder;
/* 104 */   private NativeMessageBuilder commandBuilder = null;
/*     */   
/* 106 */   private static final Map<String, Map<Integer, String>> customCollationIndexToCollationNameByUrl = new HashMap<>();
/* 107 */   private static final Map<String, Map<String, Integer>> customCollationNameToCollationIndexByUrl = new HashMap<>();
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 112 */   private static final Map<String, Map<Integer, String>> customCollationIndexToCharsetNameByUrl = new HashMap<>();
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 117 */   private static final Map<String, Map<String, Integer>> customCharsetNameToMblenByUrl = new HashMap<>();
/*     */   
/* 119 */   private static final Map<String, Map<String, String>> customCharsetNameToJavaEncodingByUrl = new HashMap<>();
/* 120 */   private static final Map<String, Map<String, Integer>> customCharsetNameToCollationIndexByUrl = new HashMap<>();
/* 121 */   private static final Map<String, Map<String, String>> customJavaEncodingUcToCharsetNameByUrl = new HashMap<>();
/* 122 */   private static final Map<String, Set<String>> customMultibyteEncodingsByUrl = new HashMap<>();
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 127 */   private static Charset jvmPlatformCharset = null;
/*     */ 
/*     */   
/*     */   private boolean platformDbCharsetMatches = true;
/*     */ 
/*     */ 
/*     */   
/*     */   static {
/* 135 */     OutputStreamWriter outWriter = null;
/*     */     
/*     */     try {
/* 138 */       outWriter = new OutputStreamWriter(new ByteArrayOutputStream());
/* 139 */       jvmPlatformCharset = Charset.forName(outWriter.getEncoding());
/*     */     } finally {
/*     */       try {
/* 142 */         if (outWriter != null) {
/* 143 */           outWriter.close();
/*     */         }
/* 145 */       } catch (IOException iOException) {}
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private NativeMessageBuilder getCommandBuilder() {
/* 152 */     if (this.commandBuilder == null) {
/* 153 */       this.commandBuilder = new NativeMessageBuilder(this.serverSession.supportsQueryAttributes());
/*     */     }
/* 155 */     return this.commandBuilder;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void checkForCharsetMismatch() {
/* 162 */     String characterEncodingValue = (String)this.characterEncoding.getValue();
/* 163 */     if (characterEncodingValue != null) {
/* 164 */       Charset characterEncodingCs = Charset.forName(characterEncodingValue);
/* 165 */       Charset encodingToCheck = jvmPlatformCharset;
/*     */       
/* 167 */       if (encodingToCheck == null) {
/* 168 */         encodingToCheck = Charset.forName(Constants.PLATFORM_ENCODING);
/*     */       }
/*     */       
/* 171 */       this.platformDbCharsetMatches = (encodingToCheck == null) ? false : encodingToCheck.equals(characterEncodingCs);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean doesPlatformDbCharsetMatches() {
/* 177 */     return this.platformDbCharsetMatches;
/*     */   }
/*     */   
/*     */   public NativeCharsetSettings(NativeSession sess) {
/* 181 */     this.session = sess;
/* 182 */     this.serverSession = this.session.getServerSession();
/*     */     
/* 184 */     this.characterEncoding = sess.getPropertySet().getStringProperty(PropertyKey.characterEncoding);
/* 185 */     this.characterSetResults = this.session.getPropertySet().getProperty(PropertyKey.characterSetResults);
/* 186 */     this.passwordCharacterEncoding = this.session.getPropertySet().getStringProperty(PropertyKey.passwordCharacterEncoding);
/* 187 */     this.connectionCollation = this.session.getPropertySet().getStringProperty(PropertyKey.connectionCollation);
/* 188 */     this.cacheServerConfiguration = sess.getPropertySet().getBooleanProperty(PropertyKey.cacheServerConfiguration);
/*     */     
/* 190 */     tryAndFixEncoding(this.characterEncoding, true);
/* 191 */     tryAndFixEncoding(this.passwordCharacterEncoding, true);
/* 192 */     if (!"null".equalsIgnoreCase((String)this.characterSetResults.getValue())) {
/* 193 */       tryAndFixEncoding(this.characterSetResults, false);
/*     */     }
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
/*     */   private void tryAndFixEncoding(RuntimeProperty<String> encodingProperty, boolean replaceImpermissibleEncodings) {
/* 207 */     String oldEncoding = (String)encodingProperty.getValue();
/* 208 */     if (oldEncoding != null) {
/* 209 */       if (replaceImpermissibleEncodings && ("UnicodeBig".equalsIgnoreCase(oldEncoding) || "UTF-16".equalsIgnoreCase(oldEncoding) || "UTF-16LE"
/* 210 */         .equalsIgnoreCase(oldEncoding) || "UTF-32".equalsIgnoreCase(oldEncoding))) {
/* 211 */         encodingProperty.setValue("UTF-8");
/*     */       } else {
/*     */         try {
/* 214 */           StringUtils.getBytes("abc", oldEncoding);
/* 215 */         } catch (WrongArgumentException waEx) {
/*     */           
/* 217 */           String newEncoding = getStaticJavaEncodingForMysqlCharset(oldEncoding);
/* 218 */           if (newEncoding == null) {
/* 219 */             throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, Messages.getString("StringUtils.0", new Object[] { oldEncoding }), this.session
/* 220 */                 .getExceptionInterceptor());
/*     */           }
/* 222 */           StringUtils.getBytes("abc", newEncoding);
/* 223 */           encodingProperty.setValue(newEncoding);
/*     */         } 
/*     */       } 
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public int configurePreHandshake(boolean reset) {
/* 231 */     if (reset) {
/* 232 */       this.sessionCollationIndex = null;
/*     */     }
/*     */ 
/*     */     
/* 236 */     if (this.sessionCollationIndex != null) {
/* 237 */       return this.sessionCollationIndex.intValue();
/*     */     }
/*     */     
/* 240 */     ServerCapabilities capabilities = this.serverSession.getCapabilities();
/* 241 */     String encoding = this.passwordCharacterEncoding.getStringValue();
/* 242 */     if (encoding == null) {
/* 243 */       String connectionColl = this.connectionCollation.getStringValue();
/* 244 */       if ((connectionColl == null || (this.sessionCollationIndex = getStaticCollationIndexForCollationName(connectionColl)) == null) && (
/* 245 */         encoding = (String)this.characterEncoding.getValue()) == null)
/*     */       {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 252 */         this.sessionCollationIndex = Integer.valueOf(255);
/*     */       }
/*     */     } 
/*     */     
/* 256 */     if (this.sessionCollationIndex == null && (
/* 257 */       this.sessionCollationIndex = Integer.valueOf(getStaticCollationIndexForJavaEncoding(encoding, capabilities.getServerVersion()))).intValue() == 0) {
/* 258 */       throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, Messages.getString("StringUtils.0", new Object[] { encoding }));
/*     */     }
/*     */ 
/*     */     
/* 262 */     if (this.sessionCollationIndex.intValue() > 255 || 
/* 263 */       isStaticImpermissibleCollation(this.sessionCollationIndex.intValue())) {
/* 264 */       this.sessionCollationIndex = Integer.valueOf(255);
/*     */     }
/*     */     
/* 267 */     if (this.sessionCollationIndex.intValue() == 255 && 
/* 268 */       !capabilities.getServerVersion().meetsMinimum(new ServerVersion(8, 0, 1))) {
/* 269 */       this.sessionCollationIndex = Integer.valueOf(45);
/*     */     }
/*     */ 
/*     */     
/* 273 */     this.errorMessageEncoding = getStaticJavaEncodingForCollationIndex(this.sessionCollationIndex);
/*     */     
/* 275 */     String csName = getStaticMysqlCharsetNameForCollationIndex(this.sessionCollationIndex);
/* 276 */     this.serverSession.getServerVariables().put("character_set_results", csName);
/*     */     
/* 278 */     this.serverSession.getServerVariables().put("character_set_client", csName);
/* 279 */     this.serverSession.getServerVariables().put("character_set_connection", csName);
/* 280 */     this.serverSession.getServerVariables().put("collation_connection", getStaticCollationNameForCollationIndex(this.sessionCollationIndex));
/*     */     
/* 282 */     return this.sessionCollationIndex.intValue();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void configurePostHandshake(boolean dontCheckServerMatch) {
/* 288 */     buildCollationMapping();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 294 */     String requiredCollation = this.connectionCollation.getStringValue();
/* 295 */     String requiredEncoding = (String)this.characterEncoding.getValue();
/* 296 */     String passwordEncoding = (String)this.passwordCharacterEncoding.getValue();
/*     */     
/* 298 */     String sessionCharsetName = getServerDefaultCharset();
/* 299 */     String sessionCollationClause = "";
/*     */     
/*     */     try {
/*     */       Integer requiredCollationIndex;
/*     */       
/* 304 */       if (requiredCollation != null && (requiredCollationIndex = getCollationIndexForCollationName(requiredCollation)) != null) {
/* 305 */         if (isImpermissibleCollation(requiredCollationIndex.intValue())) {
/* 306 */           if (this.serverSession.getCapabilities().getServerVersion().meetsMinimum(new ServerVersion(8, 0, 1))) {
/* 307 */             requiredCollationIndex = Integer.valueOf(255);
/* 308 */             requiredCollation = "utf8mb4_0900_ai_ci";
/*     */           } else {
/* 310 */             requiredCollationIndex = Integer.valueOf(45);
/* 311 */             requiredCollation = "utf8mb4_general_ci";
/*     */           } 
/*     */         }
/* 314 */         sessionCollationClause = " COLLATE " + requiredCollation;
/* 315 */         sessionCharsetName = getMysqlCharsetNameForCollationIndex(requiredCollationIndex);
/* 316 */         requiredEncoding = getJavaEncodingForCollationIndex(requiredCollationIndex, requiredEncoding);
/* 317 */         this.sessionCollationIndex = requiredCollationIndex;
/*     */       } 
/*     */       
/* 320 */       if (requiredEncoding != null) {
/* 321 */         if (sessionCollationClause.length() == 0) {
/* 322 */           sessionCharsetName = getMysqlCharsetForJavaEncoding(requiredEncoding.toUpperCase(Locale.ENGLISH), this.serverSession.getServerVersion());
/*     */         
/*     */         }
/*     */       }
/*     */       else {
/*     */         
/* 328 */         if (!StringUtils.isNullOrEmpty(passwordEncoding)) {
/* 329 */           if (this.serverSession.getCapabilities().getServerVersion().meetsMinimum(new ServerVersion(8, 0, 1))) {
/* 330 */             this.sessionCollationIndex = Integer.valueOf(255);
/* 331 */             requiredCollation = "utf8mb4_0900_ai_ci";
/*     */           } else {
/* 333 */             this.sessionCollationIndex = Integer.valueOf(45);
/* 334 */             requiredCollation = "utf8mb4_general_ci";
/*     */           } 
/* 336 */           sessionCollationClause = " COLLATE " + getCollationNameForCollationIndex(this.sessionCollationIndex);
/*     */         } 
/*     */         
/* 339 */         if ((requiredEncoding = getJavaEncodingForCollationIndex(this.sessionCollationIndex, requiredEncoding)) == null)
/*     */         {
/* 341 */           throw ExceptionFactory.createException(Messages.getString("Connection.5", new Object[] { this.sessionCollationIndex.toString() }), this.session
/* 342 */               .getExceptionInterceptor());
/*     */         }
/*     */         
/* 345 */         sessionCharsetName = getMysqlCharsetNameForCollationIndex(this.sessionCollationIndex);
/*     */       }
/*     */     
/* 348 */     } catch (ArrayIndexOutOfBoundsException outOfBoundsEx) {
/* 349 */       throw ExceptionFactory.createException(Messages.getString("Connection.6", new Object[] { this.sessionCollationIndex }), this.session
/* 350 */           .getExceptionInterceptor());
/*     */     } 
/*     */     
/* 353 */     this.characterEncoding.setValue(requiredEncoding);
/*     */     
/* 355 */     if (sessionCharsetName != null) {
/* 356 */       boolean isCharsetDifferent = !characterSetNamesMatches(sessionCharsetName);
/*     */       
/* 358 */       boolean isCollationDifferent = (sessionCollationClause.length() > 0 && !requiredCollation.equalsIgnoreCase(this.serverSession.getServerVariable("collation_connection")));
/* 359 */       if (dontCheckServerMatch || isCharsetDifferent || isCollationDifferent) {
/* 360 */         this.session.sendCommand(getCommandBuilder().buildComQuery(null, "SET NAMES " + sessionCharsetName + sessionCollationClause), false, 0);
/* 361 */         this.serverSession.getServerVariables().put("character_set_client", sessionCharsetName);
/* 362 */         this.serverSession.getServerVariables().put("character_set_connection", sessionCharsetName);
/*     */         
/* 364 */         if (sessionCollationClause.length() > 0) {
/* 365 */           this.serverSession.getServerVariables().put("collation_connection", requiredCollation);
/*     */         } else {
/* 367 */           int idx = getCollationIndexForMysqlCharsetName(sessionCharsetName);
/* 368 */           if (idx == 255 && 
/* 369 */             !this.serverSession.getCapabilities().getServerVersion().meetsMinimum(new ServerVersion(8, 0, 1))) {
/* 370 */             idx = 45;
/*     */           }
/* 372 */           this.serverSession.getServerVariables().put("collation_connection", getCollationNameForCollationIndex(Integer.valueOf(idx)));
/*     */         } 
/*     */       } 
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 384 */     String sessionResultsCharset = this.serverSession.getServerVariable("character_set_results");
/* 385 */     String characterSetResultsValue = (String)this.characterSetResults.getValue();
/* 386 */     if (StringUtils.isNullOrEmpty(characterSetResultsValue) || "null".equalsIgnoreCase(characterSetResultsValue)) {
/* 387 */       if (!StringUtils.isNullOrEmpty(sessionResultsCharset) && !"NULL".equalsIgnoreCase(sessionResultsCharset)) {
/* 388 */         this.session.sendCommand(getCommandBuilder().buildComQuery(null, "SET character_set_results = NULL"), false, 0);
/* 389 */         this.serverSession.getServerVariables().put("character_set_results", null);
/*     */       } 
/*     */       
/* 392 */       String defaultMetadataCharsetMysql = this.serverSession.getServerVariable("character_set_system");
/* 393 */       this.metadataEncoding = (defaultMetadataCharsetMysql != null) ? getJavaEncodingForMysqlCharset(defaultMetadataCharsetMysql) : "UTF-8";
/* 394 */       this.errorMessageEncoding = "UTF-8";
/*     */     } else {
/*     */       
/* 397 */       String resultsCharsetName = getMysqlCharsetForJavaEncoding(characterSetResultsValue.toUpperCase(Locale.ENGLISH), this.serverSession
/* 398 */           .getServerVersion());
/*     */       
/* 400 */       if (resultsCharsetName == null) {
/* 401 */         throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, 
/* 402 */             Messages.getString("Connection.7", new Object[] { characterSetResultsValue }), this.session.getExceptionInterceptor());
/*     */       }
/*     */       
/* 405 */       if (!resultsCharsetName.equalsIgnoreCase(sessionResultsCharset)) {
/* 406 */         this.session.sendCommand(getCommandBuilder().buildComQuery(null, "SET character_set_results = " + resultsCharsetName), false, 0);
/* 407 */         this.serverSession.getServerVariables().put("character_set_results", resultsCharsetName);
/*     */       } 
/*     */       
/* 410 */       this.metadataEncoding = characterSetResultsValue;
/* 411 */       this.errorMessageEncoding = characterSetResultsValue;
/*     */     } 
/*     */     
/* 414 */     this.metadataCollationIndex = getCollationIndexForJavaEncoding(this.metadataEncoding, this.serverSession.getServerVersion());
/*     */     
/* 416 */     checkForCharsetMismatch();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     try {
/* 423 */       CharsetEncoder enc = Charset.forName((String)this.characterEncoding.getValue()).newEncoder();
/* 424 */       CharBuffer cbuf = CharBuffer.allocate(1);
/* 425 */       ByteBuffer bbuf = ByteBuffer.allocate(1);
/*     */       
/* 427 */       cbuf.put("¥");
/* 428 */       cbuf.position(0);
/* 429 */       enc.encode(cbuf, bbuf, true);
/* 430 */       if (bbuf.get(0) == 92) {
/* 431 */         this.requiresEscapingEncoder = true;
/*     */       } else {
/* 433 */         cbuf.clear();
/* 434 */         bbuf.clear();
/*     */         
/* 436 */         cbuf.put("₩");
/* 437 */         cbuf.position(0);
/* 438 */         enc.encode(cbuf, bbuf, true);
/* 439 */         if (bbuf.get(0) == 92) {
/* 440 */           this.requiresEscapingEncoder = true;
/*     */         }
/*     */       } 
/* 443 */     } catch (UnsupportedCharsetException ucex) {
/*     */       
/* 445 */       byte[] bbuf = StringUtils.getBytes("¥", (String)this.characterEncoding.getValue());
/* 446 */       if (bbuf[0] == 92) {
/* 447 */         this.requiresEscapingEncoder = true;
/*     */       } else {
/* 449 */         bbuf = StringUtils.getBytes("₩", (String)this.characterEncoding.getValue());
/* 450 */         if (bbuf[0] == 92) {
/* 451 */           this.requiresEscapingEncoder = true;
/*     */         }
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   private boolean characterSetNamesMatches(String mysqlEncodingName) {
/* 459 */     return (mysqlEncodingName != null && mysqlEncodingName.equalsIgnoreCase(this.serverSession.getServerVariable("character_set_client")) && mysqlEncodingName
/* 460 */       .equalsIgnoreCase(this.serverSession.getServerVariable("character_set_connection")));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getServerDefaultCharset() {
/* 470 */     String charset = getStaticMysqlCharsetNameForCollationIndex(this.sessionCollationIndex);
/* 471 */     return (charset != null) ? charset : this.serverSession.getServerVariable("character_set_server");
/*     */   }
/*     */ 
/*     */   
/*     */   public String getErrorMessageEncoding() {
/* 476 */     return this.errorMessageEncoding;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getMetadataEncoding() {
/* 481 */     return this.metadataEncoding;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getMetadataCollationIndex() {
/* 486 */     return this.metadataCollationIndex;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean getRequiresEscapingEncoder() {
/* 491 */     return this.requiresEscapingEncoder;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getPasswordCharacterEncoding() {
/* 496 */     return getStaticJavaEncodingForCollationIndex(this.sessionCollationIndex);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void buildCollationMapping() {
/* 505 */     Map<Integer, String> customCollationIndexToCollationName = null;
/* 506 */     Map<String, Integer> customCollationNameToCollationIndex = null;
/* 507 */     Map<Integer, String> customCollationIndexToCharsetName = null;
/* 508 */     Map<String, Integer> customCharsetNameToMblen = null;
/* 509 */     Map<String, String> customCharsetNameToJavaEncoding = new HashMap<>();
/* 510 */     Map<String, String> customJavaEncodingUcToCharsetName = new HashMap<>();
/* 511 */     Map<String, Integer> customCharsetNameToCollationIndex = new HashMap<>();
/* 512 */     Set<String> customMultibyteEncodings = new HashSet<>();
/*     */     
/* 514 */     String databaseURL = this.session.getHostInfo().getDatabaseUrl();
/*     */     
/* 516 */     if (((Boolean)this.cacheServerConfiguration.getValue()).booleanValue()) {
/* 517 */       synchronized (customCollationIndexToCharsetNameByUrl) {
/* 518 */         customCollationIndexToCollationName = customCollationIndexToCollationNameByUrl.get(databaseURL);
/* 519 */         customCollationNameToCollationIndex = customCollationNameToCollationIndexByUrl.get(databaseURL);
/* 520 */         customCollationIndexToCharsetName = customCollationIndexToCharsetNameByUrl.get(databaseURL);
/* 521 */         customCharsetNameToMblen = customCharsetNameToMblenByUrl.get(databaseURL);
/* 522 */         customCharsetNameToJavaEncoding = customCharsetNameToJavaEncodingByUrl.get(databaseURL);
/* 523 */         customJavaEncodingUcToCharsetName = customJavaEncodingUcToCharsetNameByUrl.get(databaseURL);
/* 524 */         customCharsetNameToCollationIndex = customCharsetNameToCollationIndexByUrl.get(databaseURL);
/* 525 */         customMultibyteEncodings = customMultibyteEncodingsByUrl.get(databaseURL);
/*     */       } 
/*     */     }
/*     */     
/* 529 */     if (customCollationIndexToCharsetName == null && ((Boolean)this.session.getPropertySet().getBooleanProperty(PropertyKey.detectCustomCollations).getValue()).booleanValue()) {
/* 530 */       customCollationIndexToCollationName = new HashMap<>();
/* 531 */       customCollationNameToCollationIndex = new HashMap<>();
/* 532 */       customCollationIndexToCharsetName = new HashMap<>();
/* 533 */       customCharsetNameToMblen = new HashMap<>();
/* 534 */       customCharsetNameToJavaEncoding = new HashMap<>();
/* 535 */       customJavaEncodingUcToCharsetName = new HashMap<>();
/* 536 */       customCharsetNameToCollationIndex = new HashMap<>();
/* 537 */       customMultibyteEncodings = new HashSet<>();
/*     */       
/* 539 */       String customCharsetMapping = (String)this.session.getPropertySet().getStringProperty(PropertyKey.customCharsetMapping).getValue();
/* 540 */       if (customCharsetMapping != null) {
/* 541 */         String[] pairs = customCharsetMapping.split(",");
/* 542 */         for (String pair : pairs) {
/* 543 */           int keyEnd = pair.indexOf(":");
/* 544 */           if (keyEnd > 0 && keyEnd + 1 < pair.length()) {
/* 545 */             String charset = pair.substring(0, keyEnd);
/* 546 */             String encoding = pair.substring(keyEnd + 1);
/* 547 */             customCharsetNameToJavaEncoding.put(charset, encoding);
/* 548 */             customJavaEncodingUcToCharsetName.put(encoding.toUpperCase(Locale.ENGLISH), charset);
/*     */           } 
/*     */         } 
/*     */       } 
/*     */       
/* 553 */       IntegerValueFactory integerValueFactory = new IntegerValueFactory(this.session.getPropertySet());
/*     */       
/*     */       try {
/* 556 */         NativePacketPayload resultPacket = this.session.sendCommand(getCommandBuilder().buildComQuery(null, "select c.COLLATION_NAME, c.CHARACTER_SET_NAME, c.ID, cs.MAXLEN, c.IS_DEFAULT='Yes' from INFORMATION_SCHEMA.COLLATIONS as c left join INFORMATION_SCHEMA.CHARACTER_SETS as cs on cs.CHARACTER_SET_NAME=c.CHARACTER_SET_NAME"), false, 0);
/*     */ 
/*     */ 
/*     */         
/* 560 */         Resultset rs = this.session.getProtocol().readAllResults(-1, false, resultPacket, false, null, (ProtocolEntityFactory)new ResultsetFactory(Resultset.Type.FORWARD_ONLY, null));
/* 561 */         StringValueFactory stringValueFactory = new StringValueFactory(this.session.getPropertySet());
/*     */         Row r;
/* 563 */         while ((r = (Row)rs.getRows().next()) != null) {
/* 564 */           String collationName = (String)r.getValue(0, (ValueFactory)stringValueFactory);
/* 565 */           String charsetName = (String)r.getValue(1, (ValueFactory)stringValueFactory);
/* 566 */           int collationIndex = ((Number)r.getValue(2, (ValueFactory)integerValueFactory)).intValue();
/* 567 */           int maxlen = ((Number)r.getValue(3, (ValueFactory)integerValueFactory)).intValue();
/* 568 */           boolean isDefault = (((Number)r.getValue(4, (ValueFactory)integerValueFactory)).intValue() > 0);
/*     */           
/* 570 */           if (collationIndex >= 1024 || 
/* 571 */             !collationName.equals(getStaticCollationNameForCollationIndex(Integer.valueOf(collationIndex))) || 
/* 572 */             !charsetName.equals(getStaticMysqlCharsetNameForCollationIndex(Integer.valueOf(collationIndex)))) {
/* 573 */             customCollationIndexToCollationName.put(Integer.valueOf(collationIndex), collationName);
/* 574 */             customCollationNameToCollationIndex.put(collationName, Integer.valueOf(collationIndex));
/* 575 */             customCollationIndexToCharsetName.put(Integer.valueOf(collationIndex), charsetName);
/* 576 */             if (isDefault) {
/* 577 */               customCharsetNameToCollationIndex.put(charsetName, Integer.valueOf(collationIndex));
/*     */             } else {
/* 579 */               customCharsetNameToCollationIndex.putIfAbsent(charsetName, Integer.valueOf(collationIndex));
/*     */             } 
/*     */           } 
/*     */ 
/*     */ 
/*     */           
/* 585 */           if (getStaticMysqlCharsetByName(charsetName) == null) {
/* 586 */             customCharsetNameToMblen.put(charsetName, Integer.valueOf(maxlen));
/* 587 */             if (maxlen > 1) {
/* 588 */               String enc = customCharsetNameToJavaEncoding.get(charsetName);
/* 589 */               if (enc != null) {
/* 590 */                 customMultibyteEncodings.add(enc.toUpperCase(Locale.ENGLISH));
/*     */               }
/*     */             }
/*     */           
/*     */           } 
/*     */         } 
/* 596 */       } catch (IOException e) {
/* 597 */         throw ExceptionFactory.createException(e.getMessage(), e, this.session.getExceptionInterceptor());
/*     */       } 
/*     */       
/* 600 */       if (((Boolean)this.cacheServerConfiguration.getValue()).booleanValue()) {
/* 601 */         synchronized (customCollationIndexToCharsetNameByUrl) {
/* 602 */           customCollationIndexToCollationNameByUrl.put(databaseURL, Collections.unmodifiableMap(customCollationIndexToCollationName));
/* 603 */           customCollationNameToCollationIndexByUrl.put(databaseURL, Collections.unmodifiableMap(customCollationNameToCollationIndex));
/* 604 */           customCollationIndexToCharsetNameByUrl.put(databaseURL, Collections.unmodifiableMap(customCollationIndexToCharsetName));
/* 605 */           customCharsetNameToMblenByUrl.put(databaseURL, Collections.unmodifiableMap(customCharsetNameToMblen));
/* 606 */           customCharsetNameToJavaEncodingByUrl.put(databaseURL, Collections.unmodifiableMap(customCharsetNameToJavaEncoding));
/* 607 */           customJavaEncodingUcToCharsetNameByUrl.put(databaseURL, Collections.unmodifiableMap(customJavaEncodingUcToCharsetName));
/* 608 */           customCharsetNameToCollationIndexByUrl.put(databaseURL, Collections.unmodifiableMap(customCharsetNameToCollationIndex));
/* 609 */           customMultibyteEncodingsByUrl.put(databaseURL, Collections.unmodifiableSet(customMultibyteEncodings));
/*     */         } 
/*     */       }
/*     */     } 
/*     */     
/* 614 */     if (customCollationIndexToCharsetName != null) {
/* 615 */       this.collationIndexToCollationName = customCollationIndexToCollationName;
/* 616 */       this.collationNameToCollationIndex = customCollationNameToCollationIndex;
/* 617 */       this.collationIndexToCharsetName = customCollationIndexToCharsetName;
/* 618 */       this.charsetNameToMblen = customCharsetNameToMblen;
/* 619 */       this.charsetNameToJavaEncoding = customCharsetNameToJavaEncoding;
/* 620 */       this.javaEncodingUcToCharsetName = customJavaEncodingUcToCharsetName;
/* 621 */       this.charsetNameToCollationIndex = customCharsetNameToCollationIndex;
/* 622 */       this.multibyteEncodings = customMultibyteEncodings;
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public Integer getCollationIndexForCollationName(String collationName) {
/* 628 */     Integer collationIndex = null;
/* 629 */     if (this.collationNameToCollationIndex == null || (collationIndex = this.collationNameToCollationIndex.get(collationName)) == null) {
/* 630 */       collationIndex = getStaticCollationIndexForCollationName(collationName);
/*     */     }
/* 632 */     return collationIndex;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getCollationNameForCollationIndex(Integer collationIndex) {
/* 637 */     String collationName = null;
/* 638 */     if (collationIndex != null && (this.collationIndexToCollationName == null || (
/* 639 */       collationName = this.collationIndexToCollationName.get(collationIndex)) == null)) {
/* 640 */       collationName = getStaticCollationNameForCollationIndex(collationIndex);
/*     */     }
/* 642 */     return collationName;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getMysqlCharsetNameForCollationIndex(Integer collationIndex) {
/* 647 */     String charset = null;
/* 648 */     if (this.collationIndexToCharsetName == null || (charset = this.collationIndexToCharsetName.get(collationIndex)) == null) {
/* 649 */       charset = getStaticMysqlCharsetNameForCollationIndex(collationIndex);
/*     */     }
/* 651 */     return charset;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getJavaEncodingForCollationIndex(int collationIndex) {
/* 656 */     return getJavaEncodingForCollationIndex(Integer.valueOf(collationIndex), (String)this.characterEncoding.getValue());
/*     */   }
/*     */   
/*     */   public String getJavaEncodingForCollationIndex(Integer collationIndex, String fallBackJavaEncoding) {
/* 660 */     String encoding = null;
/* 661 */     String charset = null;
/* 662 */     if (collationIndex.intValue() != -1) {
/* 663 */       if (this.collationIndexToCharsetName != null && (charset = this.collationIndexToCharsetName.get(collationIndex)) != null) {
/* 664 */         encoding = getJavaEncodingForMysqlCharset(charset, fallBackJavaEncoding);
/*     */       }
/* 666 */       if (encoding == null) {
/* 667 */         encoding = getStaticJavaEncodingForCollationIndex(collationIndex, fallBackJavaEncoding);
/*     */       }
/*     */     } 
/* 670 */     return (encoding != null) ? encoding : fallBackJavaEncoding;
/*     */   }
/*     */   
/*     */   public int getCollationIndexForJavaEncoding(String javaEncoding, ServerVersion version) {
/* 674 */     return getCollationIndexForMysqlCharsetName(getMysqlCharsetForJavaEncoding(javaEncoding, version));
/*     */   }
/*     */   
/*     */   public int getCollationIndexForMysqlCharsetName(String charsetName) {
/* 678 */     Integer index = null;
/* 679 */     if (this.charsetNameToCollationIndex == null || (index = this.charsetNameToCollationIndex.get(charsetName)) == null) {
/* 680 */       index = Integer.valueOf(getStaticCollationIndexForMysqlCharsetName(charsetName));
/*     */     }
/* 682 */     return index.intValue();
/*     */   }
/*     */   
/*     */   public String getJavaEncodingForMysqlCharset(String mysqlCharsetName) {
/* 686 */     String encoding = null;
/* 687 */     if (this.charsetNameToJavaEncoding == null || (encoding = this.charsetNameToJavaEncoding.get(mysqlCharsetName)) == null) {
/* 688 */       encoding = getStaticJavaEncodingForMysqlCharset(mysqlCharsetName);
/*     */     }
/* 690 */     return encoding;
/*     */   }
/*     */   
/*     */   public String getJavaEncodingForMysqlCharset(String mysqlCharsetName, String javaEncoding) {
/* 694 */     String encoding = null;
/* 695 */     if (this.charsetNameToJavaEncoding == null || (encoding = this.charsetNameToJavaEncoding.get(mysqlCharsetName)) == null) {
/* 696 */       encoding = getStaticJavaEncodingForMysqlCharset(mysqlCharsetName, javaEncoding);
/*     */     }
/* 698 */     return encoding;
/*     */   }
/*     */   
/*     */   public String getMysqlCharsetForJavaEncoding(String javaEncoding, ServerVersion version) {
/* 702 */     String charset = null;
/* 703 */     if (this.javaEncodingUcToCharsetName == null || (charset = this.javaEncodingUcToCharsetName.get(javaEncoding.toUpperCase(Locale.ENGLISH))) == null) {
/* 704 */       charset = getStaticMysqlCharsetForJavaEncoding(javaEncoding, version);
/*     */     }
/* 706 */     return charset;
/*     */   }
/*     */   
/*     */   public boolean isImpermissibleCollation(int collationIndex) {
/* 710 */     String charsetName = null;
/* 711 */     if (this.collationIndexToCharsetName != null && (charsetName = this.collationIndexToCharsetName.get(Integer.valueOf(collationIndex))) != null && (
/* 712 */       charsetName.equals("ucs2") || charsetName.equals("utf16") || charsetName.equals("utf16le") || charsetName
/* 713 */       .equals("utf32"))) {
/* 714 */       return true;
/*     */     }
/*     */     
/* 717 */     return isStaticImpermissibleCollation(collationIndex);
/*     */   }
/*     */   
/*     */   public boolean isMultibyteCharset(String javaEncodingName) {
/* 721 */     if (this.multibyteEncodings != null && this.multibyteEncodings.contains(javaEncodingName.toUpperCase(Locale.ENGLISH))) {
/* 722 */       return true;
/*     */     }
/* 724 */     return isStaticMultibyteCharset(javaEncodingName);
/*     */   }
/*     */ 
/*     */   
/*     */   public int getMaxBytesPerChar(String javaCharsetName) {
/* 729 */     return getMaxBytesPerChar((Integer)null, javaCharsetName);
/*     */   }
/*     */ 
/*     */   
/*     */   public int getMaxBytesPerChar(Integer charsetIndex, String javaCharsetName) {
/* 734 */     String charset = null;
/* 735 */     if ((charset = getMysqlCharsetNameForCollationIndex(charsetIndex)) == null)
/*     */     {
/* 737 */       charset = getStaticMysqlCharsetForJavaEncoding(javaCharsetName, this.serverSession.getServerVersion());
/*     */     }
/* 739 */     Integer mblen = null;
/* 740 */     if (this.charsetNameToMblen == null || (mblen = this.charsetNameToMblen.get(charset)) == null) {
/* 741 */       mblen = Integer.valueOf(getStaticMblen(charset));
/*     */     }
/* 743 */     return (mblen != null) ? mblen.intValue() : 1;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\NativeCharsetSettings.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */