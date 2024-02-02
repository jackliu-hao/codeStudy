/*     */ package com.mysql.cj.protocol.a.authentication;
/*     */ 
/*     */ import com.mysql.cj.Messages;
/*     */ import com.mysql.cj.callback.MysqlCallback;
/*     */ import com.mysql.cj.callback.MysqlCallbackHandler;
/*     */ import com.mysql.cj.callback.UsernameCallback;
/*     */ import com.mysql.cj.exceptions.CJException;
/*     */ import com.mysql.cj.exceptions.ExceptionFactory;
/*     */ import com.mysql.cj.protocol.AuthenticationPlugin;
/*     */ import com.mysql.cj.protocol.Message;
/*     */ import com.mysql.cj.protocol.Protocol;
/*     */ import com.mysql.cj.protocol.a.NativeConstants;
/*     */ import com.mysql.cj.protocol.a.NativePacketPayload;
/*     */ import com.mysql.cj.util.StringUtils;
/*     */ import java.io.IOException;
/*     */ import java.security.Principal;
/*     */ import java.security.PrivilegedActionException;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import javax.security.auth.Subject;
/*     */ import javax.security.auth.callback.Callback;
/*     */ import javax.security.auth.callback.CallbackHandler;
/*     */ import javax.security.auth.callback.NameCallback;
/*     */ import javax.security.auth.callback.PasswordCallback;
/*     */ import javax.security.auth.callback.UnsupportedCallbackException;
/*     */ import javax.security.auth.login.AppConfigurationEntry;
/*     */ import javax.security.auth.login.Configuration;
/*     */ import javax.security.auth.login.LoginContext;
/*     */ import javax.security.auth.login.LoginException;
/*     */ import javax.security.sasl.Sasl;
/*     */ import javax.security.sasl.SaslClient;
/*     */ import javax.security.sasl.SaslException;
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
/*     */ public class AuthenticationKerberosClient
/*     */   implements AuthenticationPlugin<NativePacketPayload>
/*     */ {
/*     */   public AuthenticationKerberosClient() {
/*  74 */     this.sourceOfAuthData = PLUGIN_NAME;
/*     */     
/*  76 */     this.usernameCallbackHandler = null;
/*  77 */     this.user = null;
/*  78 */     this.password = null;
/*  79 */     this.userPrincipalName = null;
/*     */     
/*  81 */     this.subject = null;
/*  82 */     this.cachedPrincipalName = null;
/*     */     
/*  84 */     this.credentialsCallbackHandler = (cbs -> {
/*     */         for (Callback cb : cbs) {
/*     */           if (NameCallback.class.isAssignableFrom(cb.getClass())) {
/*     */             ((NameCallback)cb).setName(this.userPrincipalName);
/*     */           } else if (PasswordCallback.class.isAssignableFrom(cb.getClass())) {
/*     */             ((PasswordCallback)cb).setPassword((this.password == null) ? new char[0] : this.password.toCharArray());
/*     */           } else {
/*     */             throw new UnsupportedCallbackException(cb, cb.getClass().getName());
/*     */           } 
/*     */         } 
/*     */       });
/*     */     
/*  96 */     this.saslClient = null;
/*     */   }
/*     */   public static String PLUGIN_NAME = "authentication_kerberos_client"; private static final String LOGIN_CONFIG_ENTRY = "MySQLConnectorJ"; private static final String AUTHENTICATION_MECHANISM = "GSSAPI"; private String sourceOfAuthData; private MysqlCallbackHandler usernameCallbackHandler; private String user;
/*     */   public void init(Protocol<NativePacketPayload> prot, MysqlCallbackHandler cbh) {
/* 100 */     this.usernameCallbackHandler = cbh;
/*     */   }
/*     */   private String password; private String userPrincipalName; private Subject subject; private String cachedPrincipalName; private CallbackHandler credentialsCallbackHandler; private SaslClient saslClient;
/*     */   
/*     */   public void reset() {
/* 105 */     if (this.saslClient != null) {
/*     */       try {
/* 107 */         this.saslClient.dispose();
/* 108 */       } catch (SaslException saslException) {}
/*     */     }
/*     */ 
/*     */     
/* 112 */     this.user = null;
/* 113 */     this.password = null;
/* 114 */     this.saslClient = null;
/*     */   }
/*     */ 
/*     */   
/*     */   public void destroy() {
/* 119 */     reset();
/* 120 */     this.userPrincipalName = null;
/* 121 */     this.subject = null;
/* 122 */     this.cachedPrincipalName = null;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getProtocolPluginName() {
/* 127 */     return PLUGIN_NAME;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean requiresConfidentiality() {
/* 132 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isReusable() {
/* 137 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setAuthenticationParameters(String user, String password) {
/* 142 */     this.user = user;
/* 143 */     this.password = password;
/*     */     
/* 145 */     if (this.user == null) {
/*     */       
/*     */       try {
/* 148 */         initializeAuthentication();
/* 149 */         int pos = this.cachedPrincipalName.indexOf('@');
/* 150 */         if (pos >= 0) {
/* 151 */           this.user = this.cachedPrincipalName.substring(0, pos);
/*     */         } else {
/* 153 */           this.user = this.cachedPrincipalName;
/*     */         } 
/* 155 */       } catch (CJException e) {
/*     */         
/* 157 */         this.user = System.getProperty("user.name");
/*     */       } 
/* 159 */       if (this.usernameCallbackHandler != null) {
/* 160 */         this.usernameCallbackHandler.handle((MysqlCallback)new UsernameCallback(this.user));
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void setSourceOfAuthData(String sourceOfAuthData) {
/* 167 */     this.sourceOfAuthData = sourceOfAuthData;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean nextAuthenticationStep(NativePacketPayload fromServer, List<NativePacketPayload> toServer) {
/* 172 */     toServer.clear();
/*     */     
/* 174 */     if (!this.sourceOfAuthData.equals(PLUGIN_NAME) || fromServer.getPayloadLength() == 0)
/*     */     {
/*     */       
/* 177 */       return true;
/*     */     }
/*     */     
/* 180 */     if (this.saslClient == null) {
/*     */ 
/*     */       
/*     */       try {
/*     */ 
/*     */ 
/*     */         
/* 187 */         int servicePrincipalNameLength = (int)fromServer.readInteger(NativeConstants.IntegerDataType.INT2);
/* 188 */         String servicePrincipalName = fromServer.readString(NativeConstants.StringLengthDataType.STRING_VAR, "ASCII", servicePrincipalNameLength);
/*     */         
/* 190 */         String primary = "";
/* 191 */         String instance = "";
/*     */         
/* 193 */         int posAt = servicePrincipalName.indexOf('@');
/* 194 */         if (posAt < 0) {
/* 195 */           posAt = servicePrincipalName.length();
/*     */         }
/* 197 */         int posSlash = servicePrincipalName.lastIndexOf('/', posAt);
/* 198 */         if (posSlash >= 0) {
/* 199 */           primary = servicePrincipalName.substring(0, posSlash);
/* 200 */           instance = servicePrincipalName.substring(posSlash + 1, posAt);
/*     */         } else {
/* 202 */           primary = servicePrincipalName.substring(0, posAt);
/*     */         } 
/*     */         
/* 205 */         int userPrincipalRealmLength = (int)fromServer.readInteger(NativeConstants.IntegerDataType.INT2);
/* 206 */         String userPrincipalRealm = fromServer.readString(NativeConstants.StringLengthDataType.STRING_VAR, "ASCII", userPrincipalRealmLength);
/* 207 */         this.userPrincipalName = this.user + "@" + userPrincipalRealm;
/*     */         
/* 209 */         initializeAuthentication();
/*     */ 
/*     */         
/*     */         try {
/* 213 */           String localPrimary = primary;
/* 214 */           String localInstance = instance;
/* 215 */           this.saslClient = Subject.<SaslClient>doAs(this.subject, () -> Sasl.createSaslClient(new String[] { "GSSAPI" }, null, localPrimary, localInstance, null, null));
/*     */         }
/* 217 */         catch (PrivilegedActionException e) {
/*     */           
/* 219 */           throw (SaslException)e.getException();
/*     */         }
/*     */       
/* 222 */       } catch (SaslException e) {
/* 223 */         throw ExceptionFactory.createException(
/* 224 */             Messages.getString("AuthenticationKerberosClientPlugin.FailCreateSaslClient", new Object[] { "GSSAPI" }), e);
/*     */       } 
/*     */       
/* 227 */       if (this.saslClient == null) {
/* 228 */         throw ExceptionFactory.createException(
/* 229 */             Messages.getString("AuthenticationKerberosClientPlugin.FailCreateSaslClient", new Object[] { "GSSAPI" }));
/*     */       }
/*     */     } 
/*     */     
/* 233 */     if (!this.saslClient.isComplete()) {
/*     */       
/*     */       try {
/* 236 */         Subject.doAs(this.subject, () -> {
/*     */               byte[] response = this.saslClient.evaluateChallenge(fromServer.readBytes(NativeConstants.StringSelfDataType.STRING_EOF));
/*     */               if (response != null) {
/*     */                 NativePacketPayload bresp = new NativePacketPayload(response);
/*     */                 bresp.setPosition(0);
/*     */                 toServer.add(bresp);
/*     */               } 
/*     */               return null;
/*     */             });
/* 245 */       } catch (PrivilegedActionException e) {
/* 246 */         throw ExceptionFactory.createException(
/* 247 */             Messages.getString("AuthenticationKerberosClientPlugin.ErrProcessingAuthIter", new Object[] { "GSSAPI" }), e
/* 248 */             .getException());
/*     */       } 
/*     */     }
/* 251 */     return true;
/*     */   }
/*     */   
/*     */   private void initializeAuthentication() {
/* 255 */     if (this.subject != null && this.cachedPrincipalName != null && this.cachedPrincipalName.equals(this.userPrincipalName)) {
/*     */       return;
/*     */     }
/*     */ 
/*     */ 
/*     */     
/* 261 */     String loginConfigFile = System.getProperty("java.security.auth.login.config");
/* 262 */     Configuration loginConfig = null;
/* 263 */     if (StringUtils.isNullOrEmpty(loginConfigFile)) {
/* 264 */       final String localUser = this.userPrincipalName;
/* 265 */       final boolean debug = Boolean.getBoolean("sun.security.jgss.debug");
/* 266 */       loginConfig = new Configuration()
/*     */         {
/*     */           public AppConfigurationEntry[] getAppConfigurationEntry(String name) {
/* 269 */             Map<String, String> options = new HashMap<>();
/* 270 */             options.put("useTicketCache", "true");
/* 271 */             options.put("renewTGT", "false");
/* 272 */             if (localUser != null) {
/* 273 */               options.put("principal", localUser);
/*     */             }
/* 275 */             options.put("debug", Boolean.toString(debug));
/* 276 */             return new AppConfigurationEntry[] { new AppConfigurationEntry("com.sun.security.auth.module.Krb5LoginModule", AppConfigurationEntry.LoginModuleControlFlag.REQUIRED, options) };
/*     */           }
/*     */         };
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     try {
/* 285 */       LoginContext loginContext = new LoginContext("MySQLConnectorJ", null, this.credentialsCallbackHandler, loginConfig);
/* 286 */       loginContext.login();
/* 287 */       this.subject = loginContext.getSubject();
/* 288 */       this.cachedPrincipalName = ((Principal)this.subject.getPrincipals().iterator().next()).getName();
/* 289 */     } catch (LoginException e) {
/* 290 */       throw ExceptionFactory.createException(Messages.getString("AuthenticationKerberosClientPlugin.FailAuthenticateUser"), e);
/*     */     } 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\protocol\a\authentication\AuthenticationKerberosClient.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */