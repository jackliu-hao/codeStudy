/*     */ package com.mysql.cj.protocol.a;
/*     */ 
/*     */ import com.mysql.cj.Constants;
/*     */ import com.mysql.cj.Messages;
/*     */ import com.mysql.cj.callback.MysqlCallback;
/*     */ import com.mysql.cj.callback.MysqlCallbackHandler;
/*     */ import com.mysql.cj.callback.UsernameCallback;
/*     */ import com.mysql.cj.conf.PropertyDefinitions;
/*     */ import com.mysql.cj.conf.PropertyKey;
/*     */ import com.mysql.cj.conf.PropertySet;
/*     */ import com.mysql.cj.conf.RuntimeProperty;
/*     */ import com.mysql.cj.exceptions.ExceptionFactory;
/*     */ import com.mysql.cj.exceptions.ExceptionInterceptor;
/*     */ import com.mysql.cj.exceptions.UnableToConnectException;
/*     */ import com.mysql.cj.exceptions.WrongArgumentException;
/*     */ import com.mysql.cj.protocol.AuthenticationPlugin;
/*     */ import com.mysql.cj.protocol.AuthenticationProvider;
/*     */ import com.mysql.cj.protocol.Protocol;
/*     */ import com.mysql.cj.protocol.ServerSession;
/*     */ import com.mysql.cj.protocol.a.authentication.AuthenticationKerberosClient;
/*     */ import com.mysql.cj.protocol.a.authentication.AuthenticationLdapSaslClientPlugin;
/*     */ import com.mysql.cj.protocol.a.authentication.AuthenticationOciClient;
/*     */ import com.mysql.cj.protocol.a.authentication.CachingSha2PasswordPlugin;
/*     */ import com.mysql.cj.protocol.a.authentication.MysqlClearPasswordPlugin;
/*     */ import com.mysql.cj.protocol.a.authentication.MysqlNativePasswordPlugin;
/*     */ import com.mysql.cj.protocol.a.authentication.MysqlOldPasswordPlugin;
/*     */ import com.mysql.cj.protocol.a.authentication.Sha256PasswordPlugin;
/*     */ import com.mysql.cj.protocol.a.result.OkPacket;
/*     */ import com.mysql.cj.util.StringUtils;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.HashMap;
/*     */ import java.util.LinkedList;
/*     */ import java.util.List;
/*     */ import java.util.Map;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class NativeAuthenticationProvider
/*     */   implements AuthenticationProvider<NativePacketPayload>
/*     */ {
/*     */   private static final int AUTH_411_OVERHEAD = 33;
/*     */   private static final String NONE = "none";
/*     */   private String seed;
/*     */   private String username;
/*     */   private String password;
/*     */   private String database;
/*     */   private boolean useConnectWithDb;
/*     */   private ExceptionInterceptor exceptionInterceptor;
/*     */   private PropertySet propertySet;
/*     */   private Protocol<NativePacketPayload> protocol;
/*  88 */   private Map<String, AuthenticationPlugin<NativePacketPayload>> authenticationPlugins = null;
/*     */ 
/*     */ 
/*     */   
/*  92 */   private String clientDefaultAuthenticationPluginName = null;
/*     */ 
/*     */ 
/*     */   
/*     */   private boolean clientDefaultAuthenticationPluginExplicitelySet = false;
/*     */ 
/*     */ 
/*     */   
/* 100 */   private String serverDefaultAuthenticationPluginName = null;
/*     */   
/*     */   private MysqlCallbackHandler callbackHandler;
/*     */   
/*     */   public NativeAuthenticationProvider() {
/* 105 */     this.callbackHandler = (cb -> {
/*     */         if (cb instanceof UsernameCallback) {
/*     */           this.username = ((UsernameCallback)cb).getUsername();
/*     */         }
/*     */       });
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void init(Protocol<NativePacketPayload> prot, PropertySet propSet, ExceptionInterceptor excInterceptor) {
/* 116 */     this.protocol = prot;
/* 117 */     this.propertySet = propSet;
/* 118 */     this.exceptionInterceptor = excInterceptor;
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
/*     */ 
/*     */   
/*     */   public void connect(String user, String pass, String db) {
/* 134 */     ServerSession sessState = this.protocol.getServerSession();
/* 135 */     this.username = user;
/* 136 */     this.password = pass;
/* 137 */     this.database = db;
/*     */     
/* 139 */     NativeCapabilities capabilities = (NativeCapabilities)sessState.getCapabilities();
/* 140 */     NativePacketPayload buf = capabilities.getInitialHandshakePacket();
/*     */     
/* 142 */     PropertyDefinitions.SslMode sslMode = (PropertyDefinitions.SslMode)this.propertySet.getEnumProperty(PropertyKey.sslMode).getValue();
/* 143 */     int capabilityFlags = capabilities.getCapabilityFlags();
/* 144 */     if ((capabilityFlags & 0x800) == 0 && sslMode != PropertyDefinitions.SslMode.DISABLED && sslMode != PropertyDefinitions.SslMode.PREFERRED)
/*     */     {
/* 146 */       throw (UnableToConnectException)ExceptionFactory.createException(UnableToConnectException.class, Messages.getString("MysqlIO.15"), getExceptionInterceptor()); } 
/* 147 */     if ((capabilityFlags & 0x8000) == 0)
/*     */     {
/* 149 */       throw (UnableToConnectException)ExceptionFactory.createException(UnableToConnectException.class, "CLIENT_SECURE_CONNECTION is required", getExceptionInterceptor()); } 
/* 150 */     if ((capabilityFlags & 0x80000) == 0)
/*     */     {
/* 152 */       throw (UnableToConnectException)ExceptionFactory.createException(UnableToConnectException.class, "CLIENT_PLUGIN_AUTH is required", getExceptionInterceptor());
/*     */     }
/*     */     
/* 155 */     sessState.setStatusFlags(capabilities.getStatusFlags());
/* 156 */     int authPluginDataLength = capabilities.getAuthPluginDataLength();
/*     */     
/* 158 */     StringBuilder fullSeed = new StringBuilder((authPluginDataLength > 0) ? authPluginDataLength : 20);
/* 159 */     fullSeed.append(capabilities.getSeed());
/* 160 */     fullSeed.append((authPluginDataLength > 0) ? buf
/* 161 */         .readString(NativeConstants.StringLengthDataType.STRING_FIXED, "ASCII", authPluginDataLength - 8) : buf.readString(NativeConstants.StringSelfDataType.STRING_TERM, "ASCII"));
/* 162 */     this.seed = fullSeed.toString();
/*     */     
/* 164 */     this
/* 165 */       .useConnectWithDb = (this.database != null && this.database.length() > 0 && !((Boolean)this.propertySet.getBooleanProperty(PropertyKey.createDatabaseIfNotExist).getValue()).booleanValue());
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
/* 194 */     long clientParam = (capabilityFlags & 0x1 | (((Boolean)this.propertySet.getBooleanProperty(PropertyKey.useAffectedRows).getValue()).booleanValue() ? 0 : (capabilityFlags & 0x2)) | capabilityFlags & 0x4 | (this.useConnectWithDb ? (capabilityFlags & 0x8) : 0) | (((Boolean)this.propertySet.getBooleanProperty(PropertyKey.useCompression).getValue()).booleanValue() ? (capabilityFlags & 0x20) : 0) | ((((Boolean)this.propertySet.getBooleanProperty(PropertyKey.allowLoadLocalInfile).getValue()).booleanValue() || this.propertySet.getStringProperty(PropertyKey.allowLoadLocalInfileInPath).isExplicitlySet()) ? (capabilityFlags & 0x80) : 0) | capabilityFlags & 0x200 | (((Boolean)this.propertySet.getBooleanProperty(PropertyKey.interactiveClient).getValue()).booleanValue() ? (capabilityFlags & 0x400) : 0) | ((this.propertySet.getEnumProperty(PropertyKey.sslMode).getValue() != PropertyDefinitions.SslMode.DISABLED) ? (capabilityFlags & 0x800) : 0) | capabilityFlags & 0x2000 | 0x8000 | (((Boolean)this.propertySet.getBooleanProperty(PropertyKey.allowMultiQueries).getValue()).booleanValue() ? (capabilityFlags & 0x10000) : 0) | capabilityFlags & 0x20000 | capabilityFlags & 0x40000 | 0x80000 | ("none".equals(this.propertySet.getStringProperty(PropertyKey.connectionAttributes).getValue()) ? 0 : (capabilityFlags & 0x100000)) | capabilityFlags & 0x200000 | (((Boolean)this.propertySet.getBooleanProperty(PropertyKey.disconnectOnExpiredPasswords).getValue()).booleanValue() ? 0 : (capabilityFlags & 0x400000)) | (((Boolean)this.propertySet.getBooleanProperty(PropertyKey.trackSessionState).getValue()).booleanValue() ? (capabilityFlags & 0x800000) : 0) | capabilityFlags & 0x1000000 | capabilityFlags & 0x8000000 | capabilityFlags & 0x10000000);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 200 */     sessState.setClientParam(clientParam);
/*     */ 
/*     */     
/* 203 */     if ((clientParam & 0x800L) != 0L) {
/* 204 */       this.protocol.negotiateSSLConnection();
/*     */     }
/*     */     
/* 207 */     if (buf.isOKPacket()) {
/* 208 */       throw ExceptionFactory.createException(Messages.getString("AuthenticationProvider.UnexpectedAuthenticationApproval"), getExceptionInterceptor());
/*     */     }
/*     */     
/* 211 */     proceedHandshakeWithPluggableAuthentication(buf);
/*     */     
/* 213 */     this.password = null;
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
/*     */ 
/*     */   
/*     */   private void loadAuthenticationPlugins() {
/*     */     List<String> disabledAuthenticationPlugins;
/* 230 */     RuntimeProperty<String> defaultAuthenticationPluginProp = this.propertySet.getStringProperty(PropertyKey.defaultAuthenticationPlugin);
/* 231 */     String defaultAuthenticationPluginValue = (String)defaultAuthenticationPluginProp.getValue();
/* 232 */     if (defaultAuthenticationPluginValue == null || "".equals(defaultAuthenticationPluginValue.trim())) {
/* 233 */       throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, 
/* 234 */           Messages.getString("AuthenticationProvider.BadDefaultAuthenticationPlugin", new Object[] { defaultAuthenticationPluginValue
/* 235 */             }), getExceptionInterceptor());
/*     */     }
/*     */ 
/*     */     
/* 239 */     String disabledPlugins = (String)this.propertySet.getStringProperty(PropertyKey.disabledAuthenticationPlugins).getValue();
/*     */     
/* 241 */     if (disabledPlugins != null && !"".equals(disabledPlugins)) {
/* 242 */       disabledAuthenticationPlugins = StringUtils.split(disabledPlugins, ",", true);
/*     */     } else {
/* 244 */       disabledAuthenticationPlugins = Collections.EMPTY_LIST;
/*     */     } 
/*     */     
/* 247 */     this.authenticationPlugins = new HashMap<>();
/* 248 */     List<AuthenticationPlugin<NativePacketPayload>> pluginsToInit = new LinkedList<>();
/*     */ 
/*     */     
/* 251 */     pluginsToInit.add(new MysqlNativePasswordPlugin());
/* 252 */     pluginsToInit.add(new MysqlClearPasswordPlugin());
/* 253 */     pluginsToInit.add(new Sha256PasswordPlugin());
/* 254 */     pluginsToInit.add(new CachingSha2PasswordPlugin());
/* 255 */     pluginsToInit.add(new MysqlOldPasswordPlugin());
/* 256 */     pluginsToInit.add(new AuthenticationLdapSaslClientPlugin());
/* 257 */     pluginsToInit.add(new AuthenticationKerberosClient());
/* 258 */     pluginsToInit.add(new AuthenticationOciClient());
/*     */ 
/*     */     
/* 261 */     String authenticationPluginClasses = (String)this.propertySet.getStringProperty(PropertyKey.authenticationPlugins).getValue();
/* 262 */     if (authenticationPluginClasses != null && !"".equals(authenticationPluginClasses.trim())) {
/* 263 */       List<String> pluginsToCreate = StringUtils.split(authenticationPluginClasses, ",", true);
/* 264 */       for (String className : pluginsToCreate) {
/*     */         try {
/* 266 */           pluginsToInit.add((AuthenticationPlugin<NativePacketPayload>)Class.forName(className).newInstance());
/* 267 */         } catch (Throwable t) {
/* 268 */           throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, 
/* 269 */               Messages.getString("AuthenticationProvider.BadAuthenticationPlugin", new Object[] { className }), t, this.exceptionInterceptor);
/*     */         } 
/*     */       } 
/*     */     } 
/*     */ 
/*     */     
/* 275 */     boolean defaultFound = false;
/* 276 */     for (AuthenticationPlugin<NativePacketPayload> plugin : pluginsToInit) {
/* 277 */       String pluginProtocolName = plugin.getProtocolPluginName();
/* 278 */       String pluginClassName = plugin.getClass().getName();
/* 279 */       boolean disabledByProtocolName = disabledAuthenticationPlugins.contains(pluginProtocolName);
/* 280 */       boolean disabledByClassName = disabledAuthenticationPlugins.contains(pluginClassName);
/*     */       
/* 282 */       if (disabledByProtocolName || disabledByClassName) {
/*     */         
/* 284 */         if (!defaultFound && (defaultAuthenticationPluginValue
/* 285 */           .equals(pluginProtocolName) || defaultAuthenticationPluginValue.equals(pluginClassName)))
/* 286 */           throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, 
/* 287 */               Messages.getString("AuthenticationProvider.BadDisabledAuthenticationPlugin", new Object[] { disabledByClassName ? pluginClassName : pluginProtocolName
/*     */                 
/* 289 */                 }), getExceptionInterceptor()); 
/*     */         continue;
/*     */       } 
/* 292 */       this.authenticationPlugins.put(pluginProtocolName, plugin);
/* 293 */       if (!defaultFound && (defaultAuthenticationPluginValue
/* 294 */         .equals(pluginProtocolName) || defaultAuthenticationPluginValue.equals(pluginClassName))) {
/* 295 */         this.clientDefaultAuthenticationPluginName = pluginProtocolName;
/* 296 */         this.clientDefaultAuthenticationPluginExplicitelySet = defaultAuthenticationPluginProp.isExplicitlySet();
/* 297 */         defaultFound = true;
/*     */       } 
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/* 303 */     if (!defaultFound) {
/* 304 */       throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, 
/* 305 */           Messages.getString("AuthenticationProvider.DefaultAuthenticationPluginIsNotListed", new Object[] { defaultAuthenticationPluginValue
/* 306 */             }), getExceptionInterceptor());
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private AuthenticationPlugin<NativePacketPayload> getAuthenticationPlugin(String pluginName) {
/* 326 */     AuthenticationPlugin<NativePacketPayload> plugin = this.authenticationPlugins.get(pluginName);
/*     */     
/* 328 */     if (plugin == null) {
/* 329 */       return null;
/*     */     }
/*     */     
/* 332 */     if (!plugin.isReusable()) {
/*     */       try {
/* 334 */         plugin = (AuthenticationPlugin<NativePacketPayload>)plugin.getClass().newInstance();
/* 335 */       } catch (Throwable t) {
/* 336 */         throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, 
/* 337 */             Messages.getString("AuthenticationProvider.BadAuthenticationPlugin", new Object[] { plugin.getClass().getName() }), t, 
/* 338 */             getExceptionInterceptor());
/*     */       } 
/*     */     }
/*     */     
/* 342 */     plugin.init(this.protocol, this.callbackHandler);
/* 343 */     return plugin;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void checkConfidentiality(AuthenticationPlugin<?> plugin) {
/* 353 */     if (plugin.requiresConfidentiality() && !this.protocol.getSocketConnection().isSSLEstablished()) {
/* 354 */       throw ExceptionFactory.createException(
/* 355 */           Messages.getString("AuthenticationProvider.AuthenticationPluginRequiresSSL", new Object[] { plugin.getProtocolPluginName()
/* 356 */             }), getExceptionInterceptor());
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
/*     */ 
/*     */   
/*     */   private void proceedHandshakeWithPluggableAuthentication(NativePacketPayload challenge) {
/*     */     String pluginName;
/* 373 */     ServerSession serverSession = this.protocol.getServerSession();
/*     */     
/* 375 */     if (this.authenticationPlugins == null) {
/* 376 */       loadAuthenticationPlugins();
/*     */     }
/*     */     
/* 379 */     boolean forChangeUser = true;
/* 380 */     if (challenge != null) {
/* 381 */       this.serverDefaultAuthenticationPluginName = challenge.readString(NativeConstants.StringSelfDataType.STRING_TERM, "ASCII");
/* 382 */       forChangeUser = false;
/*     */     } 
/*     */     
/* 385 */     serverSession.getCharsetSettings().configurePreHandshake(forChangeUser);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 392 */     if (this.clientDefaultAuthenticationPluginExplicitelySet) {
/* 393 */       pluginName = this.clientDefaultAuthenticationPluginName;
/*     */     } else {
/* 395 */       pluginName = (this.serverDefaultAuthenticationPluginName != null) ? this.serverDefaultAuthenticationPluginName : this.clientDefaultAuthenticationPluginName;
/*     */     } 
/*     */     
/* 398 */     AuthenticationPlugin<NativePacketPayload> plugin = getAuthenticationPlugin(pluginName);
/*     */     
/* 400 */     if (plugin == null) {
/*     */       
/* 402 */       pluginName = this.clientDefaultAuthenticationPluginName;
/* 403 */       plugin = getAuthenticationPlugin(pluginName);
/*     */     } 
/*     */     
/* 406 */     boolean skipPassword = false;
/* 407 */     if (pluginName.equals(Sha256PasswordPlugin.PLUGIN_NAME) && !pluginName.equals(this.clientDefaultAuthenticationPluginName) && 
/* 408 */       !this.protocol.getSocketConnection().isSSLEstablished() && this.propertySet
/* 409 */       .getStringProperty(PropertyKey.serverRSAPublicKeyFile).getValue() == null && 
/* 410 */       !((Boolean)this.propertySet.getBooleanProperty(PropertyKey.allowPublicKeyRetrieval).getValue()).booleanValue()) {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 417 */       plugin = getAuthenticationPlugin(this.clientDefaultAuthenticationPluginName);
/* 418 */       skipPassword = true;
/*     */     } 
/*     */     
/* 421 */     checkConfidentiality(plugin);
/*     */ 
/*     */ 
/*     */     
/* 425 */     NativePacketPayload fromServer = new NativePacketPayload(StringUtils.getBytes(this.seed));
/* 426 */     String sourceOfAuthData = this.serverDefaultAuthenticationPluginName;
/*     */     
/* 428 */     NativePacketPayload lastSent = null;
/* 429 */     NativePacketPayload lastReceived = challenge;
/* 430 */     ArrayList<NativePacketPayload> toServer = new ArrayList<>();
/*     */     
/* 432 */     boolean firstPacket = true;
/*     */ 
/*     */     
/* 435 */     int mfaNthFactor = 1;
/*     */ 
/*     */     
/* 438 */     int counter = 100;
/* 439 */     while (0 < counter--) {
/*     */ 
/*     */ 
/*     */       
/* 443 */       plugin.setAuthenticationParameters(this.username, skipPassword ? null : getNthFactorPassword(mfaNthFactor));
/* 444 */       plugin.setSourceOfAuthData(sourceOfAuthData);
/* 445 */       plugin.nextAuthenticationStep(fromServer, toServer);
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 450 */       if (firstPacket) {
/* 451 */         NativePacketPayload authData = toServer.isEmpty() ? new NativePacketPayload(0) : toServer.get(0);
/* 452 */         if (forChangeUser) {
/*     */           
/* 454 */           lastSent = createChangeUserPacket(serverSession, plugin.getProtocolPluginName(), authData);
/* 455 */           this.protocol.send(lastSent, lastSent.getPosition());
/*     */         } else {
/*     */           
/* 458 */           lastSent = createHandshakeResponsePacket(serverSession, plugin.getProtocolPluginName(), authData);
/* 459 */           this.protocol.send(lastSent, lastSent.getPosition());
/*     */         } 
/* 461 */         firstPacket = false;
/* 462 */       } else if (!toServer.isEmpty()) {
/*     */         
/* 464 */         toServer.forEach(b -> this.protocol.send(b, b.getPayloadLength()));
/*     */       } 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 470 */       lastReceived = (NativePacketPayload)this.protocol.checkErrorMessage();
/*     */       
/* 472 */       if (lastReceived.isOKPacket()) {
/*     */         
/* 474 */         OkPacket ok = OkPacket.parse(lastReceived, null);
/* 475 */         serverSession.setStatusFlags(ok.getStatusFlags(), true);
/* 476 */         serverSession.getServerSessionStateController().setSessionStateChanges(ok.getSessionStateChanges());
/*     */ 
/*     */         
/* 479 */         plugin.destroy();
/*     */         break;
/*     */       } 
/* 482 */       if (lastReceived.isAuthMethodSwitchRequestPacket()) {
/*     */         
/* 484 */         skipPassword = false;
/* 485 */         pluginName = lastReceived.readString(NativeConstants.StringSelfDataType.STRING_TERM, "ASCII");
/* 486 */         if (plugin.getProtocolPluginName().equals(pluginName)) {
/* 487 */           plugin.reset();
/*     */         } else {
/*     */           
/* 490 */           plugin.destroy();
/* 491 */           plugin = getAuthenticationPlugin(pluginName);
/* 492 */           if (plugin == null) {
/* 493 */             throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, 
/* 494 */                 Messages.getString("AuthenticationProvider.BadAuthenticationPlugin", new Object[] { pluginName }), getExceptionInterceptor());
/*     */           }
/*     */         } 
/*     */         
/* 498 */         checkConfidentiality(plugin);
/* 499 */         fromServer = new NativePacketPayload(lastReceived.readBytes(NativeConstants.StringSelfDataType.STRING_EOF));
/*     */       }
/* 501 */       else if (lastReceived.isAuthNextFactorPacket()) {
/*     */         
/* 503 */         mfaNthFactor++;
/* 504 */         skipPassword = false;
/* 505 */         pluginName = lastReceived.readString(NativeConstants.StringSelfDataType.STRING_TERM, "ASCII");
/* 506 */         if (plugin.getProtocolPluginName().equals(pluginName)) {
/* 507 */           plugin.reset();
/*     */         } else {
/*     */           
/* 510 */           plugin.destroy();
/* 511 */           plugin = getAuthenticationPlugin(pluginName);
/* 512 */           if (plugin == null) {
/* 513 */             throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, 
/* 514 */                 Messages.getString("AuthenticationProvider.BadAuthenticationPlugin", new Object[] { pluginName }), getExceptionInterceptor());
/*     */           }
/*     */         } 
/*     */         
/* 518 */         checkConfidentiality(plugin);
/* 519 */         fromServer = new NativePacketPayload(lastReceived.readBytes(NativeConstants.StringSelfDataType.STRING_EOF));
/*     */       }
/*     */       else {
/*     */         
/* 523 */         if (!this.protocol.versionMeetsMinimum(5, 5, 16)) {
/* 524 */           lastReceived.setPosition(lastReceived.getPosition() - 1);
/*     */         }
/* 526 */         fromServer = new NativePacketPayload(lastReceived.readBytes(NativeConstants.StringSelfDataType.STRING_EOF));
/*     */       } 
/*     */       
/* 529 */       sourceOfAuthData = pluginName;
/*     */     } 
/*     */     
/* 532 */     if (counter == 0) {
/* 533 */       throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, 
/* 534 */           Messages.getString("CommunicationsException.TooManyAuthenticationPluginNegotiations"), getExceptionInterceptor());
/*     */     }
/*     */     
/* 537 */     this.protocol.afterHandshake();
/*     */     
/* 539 */     if (!this.useConnectWithDb) {
/* 540 */       this.protocol.changeDatabase(this.database);
/*     */     }
/*     */   }
/*     */   
/*     */   private String getNthFactorPassword(int nthFactor) {
/* 545 */     switch (nthFactor) {
/*     */       case 1:
/* 547 */         return (this.password == null) ? (String)this.protocol.getPropertySet().getStringProperty(PropertyKey.password1).getValue() : this.password;
/*     */       case 2:
/* 549 */         return (String)this.protocol.getPropertySet().getStringProperty(PropertyKey.password2).getValue();
/*     */       case 3:
/* 551 */         return (String)this.protocol.getPropertySet().getStringProperty(PropertyKey.password3).getValue();
/*     */     } 
/* 553 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private Map<String, String> getConnectionAttributesMap(String attStr) {
/* 559 */     Map<String, String> attMap = new HashMap<>();
/*     */     
/* 561 */     if (attStr != null) {
/* 562 */       String[] pairs = attStr.split(",");
/* 563 */       for (String pair : pairs) {
/* 564 */         int keyEnd = pair.indexOf(":");
/* 565 */         if (keyEnd > 0 && keyEnd + 1 < pair.length()) {
/* 566 */           attMap.put(pair.substring(0, keyEnd), pair.substring(keyEnd + 1));
/*     */         }
/*     */       } 
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 574 */     attMap.put("_client_name", "MySQL Connector/J");
/* 575 */     attMap.put("_client_version", "8.0.28");
/* 576 */     attMap.put("_runtime_vendor", Constants.JVM_VENDOR);
/* 577 */     attMap.put("_runtime_version", Constants.JVM_VERSION);
/* 578 */     attMap.put("_client_license", "GPL");
/*     */     
/* 580 */     return attMap;
/*     */   }
/*     */ 
/*     */   
/*     */   private void appendConnectionAttributes(NativePacketPayload buf, String attributes, String enc) {
/* 585 */     NativePacketPayload lb = new NativePacketPayload(100);
/* 586 */     Map<String, String> attMap = getConnectionAttributesMap(attributes);
/*     */     
/* 588 */     for (String key : attMap.keySet()) {
/* 589 */       lb.writeBytes(NativeConstants.StringSelfDataType.STRING_LENENC, StringUtils.getBytes(key, enc));
/* 590 */       lb.writeBytes(NativeConstants.StringSelfDataType.STRING_LENENC, StringUtils.getBytes(attMap.get(key), enc));
/*     */     } 
/*     */     
/* 593 */     buf.writeInteger(NativeConstants.IntegerDataType.INT_LENENC, lb.getPosition());
/* 594 */     buf.writeBytes(NativeConstants.StringLengthDataType.STRING_FIXED, lb.getByteBuffer(), 0, lb.getPosition());
/*     */   }
/*     */   
/*     */   public ExceptionInterceptor getExceptionInterceptor() {
/* 598 */     return this.exceptionInterceptor;
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
/*     */   
/*     */   public void changeUser(String user, String pass, String db) {
/* 613 */     this.username = user;
/* 614 */     this.password = pass;
/* 615 */     this.database = db;
/* 616 */     proceedHandshakeWithPluggableAuthentication(null);
/* 617 */     this.password = null;
/*     */   }
/*     */ 
/*     */   
/*     */   private NativePacketPayload createHandshakeResponsePacket(ServerSession serverSession, String pluginName, NativePacketPayload authData) {
/* 622 */     long clientParam = serverSession.getClientParam();
/* 623 */     int collationIndex = serverSession.getCharsetSettings().configurePreHandshake(false);
/* 624 */     String enc = serverSession.getCharsetSettings().getPasswordCharacterEncoding();
/*     */     
/* 626 */     int userLength = (this.username == null) ? 0 : this.username.length();
/*     */ 
/*     */ 
/*     */     
/* 630 */     NativePacketPayload last_sent = new NativePacketPayload(88 + 3 * userLength + (this.useConnectWithDb ? (3 * this.database.length()) : 0));
/*     */     
/* 632 */     last_sent.writeInteger(NativeConstants.IntegerDataType.INT4, clientParam);
/* 633 */     last_sent.writeInteger(NativeConstants.IntegerDataType.INT4, 16777215L);
/* 634 */     last_sent.writeInteger(NativeConstants.IntegerDataType.INT1, collationIndex);
/* 635 */     last_sent.writeBytes(NativeConstants.StringLengthDataType.STRING_FIXED, new byte[23]);
/*     */ 
/*     */     
/* 638 */     last_sent.writeBytes(NativeConstants.StringSelfDataType.STRING_TERM, StringUtils.getBytes(this.username, enc));
/* 639 */     if ((clientParam & 0x200000L) != 0L) {
/*     */       
/* 641 */       last_sent.writeBytes(NativeConstants.StringSelfDataType.STRING_LENENC, authData.readBytes(NativeConstants.StringSelfDataType.STRING_EOF));
/*     */     } else {
/*     */       
/* 644 */       last_sent.writeInteger(NativeConstants.IntegerDataType.INT1, authData.getPayloadLength());
/* 645 */       last_sent.writeBytes(NativeConstants.StringSelfDataType.STRING_EOF, authData.getByteBuffer());
/*     */     } 
/*     */     
/* 648 */     if (this.useConnectWithDb) {
/* 649 */       last_sent.writeBytes(NativeConstants.StringSelfDataType.STRING_TERM, StringUtils.getBytes(this.database, enc));
/*     */     }
/*     */     
/* 652 */     last_sent.writeBytes(NativeConstants.StringSelfDataType.STRING_TERM, StringUtils.getBytes(pluginName, enc));
/*     */ 
/*     */     
/* 655 */     if ((clientParam & 0x100000L) != 0L) {
/* 656 */       appendConnectionAttributes(last_sent, (String)this.propertySet.getStringProperty(PropertyKey.connectionAttributes).getValue(), enc);
/*     */     }
/* 658 */     return last_sent;
/*     */   }
/*     */ 
/*     */   
/*     */   private NativePacketPayload createChangeUserPacket(ServerSession serverSession, String pluginName, NativePacketPayload authData) {
/* 663 */     long clientParam = serverSession.getClientParam();
/* 664 */     int collationIndex = serverSession.getCharsetSettings().configurePreHandshake(false);
/* 665 */     String enc = serverSession.getCharsetSettings().getPasswordCharacterEncoding();
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 670 */     NativePacketPayload last_sent = new NativePacketPayload(88 + 3 * this.username.length() + (this.useConnectWithDb ? (3 * this.database.length()) : 1) + 1);
/*     */     
/* 672 */     last_sent.writeInteger(NativeConstants.IntegerDataType.INT1, 17L);
/*     */ 
/*     */     
/* 675 */     last_sent.writeBytes(NativeConstants.StringSelfDataType.STRING_TERM, StringUtils.getBytes(this.username, enc));
/*     */     
/* 677 */     if (authData.getPayloadLength() < 256) {
/*     */       
/* 679 */       last_sent.writeInteger(NativeConstants.IntegerDataType.INT1, authData.getPayloadLength());
/* 680 */       last_sent.writeBytes(NativeConstants.StringSelfDataType.STRING_EOF, authData.getByteBuffer(), 0, authData.getPayloadLength());
/*     */     } else {
/* 682 */       last_sent.writeInteger(NativeConstants.IntegerDataType.INT1, 0L);
/*     */     } 
/*     */     
/* 685 */     if (this.useConnectWithDb) {
/* 686 */       last_sent.writeBytes(NativeConstants.StringSelfDataType.STRING_TERM, StringUtils.getBytes(this.database, enc));
/*     */     } else {
/*     */       
/* 689 */       last_sent.writeInteger(NativeConstants.IntegerDataType.INT1, 0L);
/*     */     } 
/*     */     
/* 692 */     last_sent.writeInteger(NativeConstants.IntegerDataType.INT1, collationIndex);
/* 693 */     last_sent.writeInteger(NativeConstants.IntegerDataType.INT1, 0L);
/*     */ 
/*     */     
/* 696 */     last_sent.writeBytes(NativeConstants.StringSelfDataType.STRING_TERM, StringUtils.getBytes(pluginName, enc));
/*     */ 
/*     */     
/* 699 */     if ((clientParam & 0x100000L) != 0L) {
/* 700 */       appendConnectionAttributes(last_sent, (String)this.propertySet.getStringProperty(PropertyKey.connectionAttributes).getValue(), enc);
/*     */     }
/* 702 */     return last_sent;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\protocol\a\NativeAuthenticationProvider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */