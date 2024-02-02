/*     */ package com.mysql.cj.protocol.a.authentication;
/*     */ 
/*     */ import com.mysql.cj.Messages;
/*     */ import com.mysql.cj.callback.MysqlCallback;
/*     */ import com.mysql.cj.callback.MysqlCallbackHandler;
/*     */ import com.mysql.cj.callback.UsernameCallback;
/*     */ import com.mysql.cj.conf.PropertyKey;
/*     */ import com.mysql.cj.exceptions.CJException;
/*     */ import com.mysql.cj.exceptions.ExceptionFactory;
/*     */ import com.mysql.cj.protocol.AuthenticationPlugin;
/*     */ import com.mysql.cj.protocol.Message;
/*     */ import com.mysql.cj.protocol.Protocol;
/*     */ import com.mysql.cj.protocol.a.NativeConstants;
/*     */ import com.mysql.cj.protocol.a.NativePacketPayload;
/*     */ import com.mysql.cj.sasl.ScramShaSaslProvider;
/*     */ import com.mysql.cj.util.StringUtils;
/*     */ import java.io.IOException;
/*     */ import java.security.PrivilegedActionException;
/*     */ import java.security.Provider;
/*     */ import java.security.Security;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Locale;
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
/*     */ public class AuthenticationLdapSaslClientPlugin
/*     */   implements AuthenticationPlugin<NativePacketPayload>
/*     */ {
/*     */   public AuthenticationLdapSaslClientPlugin() {
/* 109 */     this.protocol = null;
/* 110 */     this.usernameCallbackHandler = null;
/* 111 */     this.user = null;
/* 112 */     this.password = null;
/*     */     
/* 114 */     this.authMech = null;
/* 115 */     this.saslClient = null;
/* 116 */     this.subject = null;
/*     */     
/* 118 */     this.firstPass = true;
/*     */     
/* 120 */     this.credentialsCallbackHandler = (cbs -> {
/*     */         for (Callback cb : cbs) {
/*     */           if (NameCallback.class.isAssignableFrom(cb.getClass())) {
/*     */             ((NameCallback)cb).setName(this.user);
/*     */           } else if (PasswordCallback.class.isAssignableFrom(cb.getClass())) {
/*     */             char[] passwordChars = (this.password == null) ? new char[0] : this.password.toCharArray();
/*     */             ((PasswordCallback)cb).setPassword(passwordChars);
/*     */           } else {
/*     */             throw new UnsupportedCallbackException(cb, cb.getClass().getName());
/*     */           } 
/*     */         } 
/*     */       });
/*     */   }
/*     */   
/*     */   public static String PLUGIN_NAME = "authentication_ldap_sasl_client"; private static final String LOGIN_CONFIG_ENTRY = "MySQLConnectorJ"; private static final String LDAP_SERVICE_NAME = "ldap"; private Protocol<?> protocol; private MysqlCallbackHandler usernameCallbackHandler; private String user;
/* 135 */   public void init(Protocol<NativePacketPayload> prot) { this.protocol = prot;
/*     */ 
/*     */     
/* 138 */     Security.addProvider((Provider)new ScramShaSaslProvider()); }
/*     */   private String password;
/*     */   private AuthenticationMechanisms authMech;
/*     */   private SaslClient saslClient;
/*     */   private Subject subject; private boolean firstPass; private CallbackHandler credentialsCallbackHandler; private enum AuthenticationMechanisms {
/* 143 */     SCRAM_SHA_1("SCRAM-SHA-1", "MYSQLCJ-SCRAM-SHA-1"), SCRAM_SHA_256("SCRAM-SHA-256", "MYSQLCJ-SCRAM-SHA-256"), GSSAPI("GSSAPI", "GSSAPI"); private String mechName = null; private String saslServiceName = null; AuthenticationMechanisms(String mechName, String serviceName) { this.mechName = mechName; this.saslServiceName = serviceName; } static AuthenticationMechanisms fromValue(String mechName) { for (AuthenticationMechanisms am : values()) { if (am.mechName.equalsIgnoreCase(mechName)) return am;  }  throw ExceptionFactory.createException(Messages.getString("AuthenticationLdapSaslClientPlugin.UnsupportedAuthMech", new String[] { mechName })); } String getMechName() { return this.mechName; } String getSaslServiceName() { return this.saslServiceName; } } public void init(Protocol<NativePacketPayload> prot, MysqlCallbackHandler cbh) { init(prot);
/* 144 */     this.usernameCallbackHandler = cbh; }
/*     */ 
/*     */ 
/*     */   
/*     */   public void reset() {
/* 149 */     if (this.saslClient != null) {
/*     */       try {
/* 151 */         this.saslClient.dispose();
/* 152 */       } catch (SaslException saslException) {}
/*     */     }
/*     */ 
/*     */     
/* 156 */     this.user = null;
/* 157 */     this.password = null;
/* 158 */     this.authMech = null;
/* 159 */     this.saslClient = null;
/* 160 */     this.subject = null;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void destroy() {
/* 166 */     this.protocol = null;
/* 167 */     reset();
/*     */   }
/*     */ 
/*     */   
/*     */   public String getProtocolPluginName() {
/* 172 */     return PLUGIN_NAME;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean requiresConfidentiality() {
/* 177 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isReusable() {
/* 182 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setAuthenticationParameters(String user, String password) {
/* 187 */     this.user = user;
/* 188 */     this.password = password;
/*     */     
/* 190 */     if (this.user == null) {
/*     */       
/* 192 */       this.user = System.getProperty("user.name");
/* 193 */       if (this.usernameCallbackHandler != null) {
/* 194 */         this.usernameCallbackHandler.handle((MysqlCallback)new UsernameCallback(this.user));
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean nextAuthenticationStep(NativePacketPayload fromServer, List<NativePacketPayload> toServer) {
/* 201 */     toServer.clear();
/*     */     
/* 203 */     if (this.saslClient == null) {
/*     */       
/* 205 */       String authMechId = fromServer.readString(NativeConstants.StringSelfDataType.STRING_EOF, "ASCII");
/*     */       try {
/* 207 */         this.authMech = AuthenticationMechanisms.fromValue(authMechId);
/* 208 */       } catch (CJException e) {
/* 209 */         if (this.firstPass) {
/* 210 */           this.firstPass = false;
/*     */ 
/*     */           
/* 213 */           return true;
/*     */         } 
/* 215 */         throw e;
/*     */       } 
/* 217 */       this.firstPass = false; try {
/*     */         String ldapServerHostname; String loginConfigFile; Configuration loginConfig;
/*     */         LoginContext loginContext;
/* 220 */         switch (this.authMech) {
/*     */           
/*     */           case GSSAPI:
/* 223 */             ldapServerHostname = (String)this.protocol.getPropertySet().getStringProperty(PropertyKey.ldapServerHostname).getValue();
/* 224 */             if (StringUtils.isNullOrEmpty(ldapServerHostname)) {
/* 225 */               String krb5Kdc = System.getProperty("java.security.krb5.kdc");
/* 226 */               if (!StringUtils.isNullOrEmpty(krb5Kdc)) {
/* 227 */                 ldapServerHostname = krb5Kdc;
/* 228 */                 int dotIndex = krb5Kdc.indexOf('.');
/* 229 */                 if (dotIndex > 0) {
/* 230 */                   ldapServerHostname = krb5Kdc.substring(0, dotIndex).toLowerCase(Locale.ENGLISH);
/*     */                 }
/*     */               } 
/*     */             } 
/* 234 */             if (StringUtils.isNullOrEmpty(ldapServerHostname)) {
/* 235 */               throw ExceptionFactory.createException(Messages.getString("AuthenticationLdapSaslClientPlugin.MissingLdapServerHostname"));
/*     */             }
/*     */ 
/*     */             
/* 239 */             loginConfigFile = System.getProperty("java.security.auth.login.config");
/* 240 */             loginConfig = null;
/* 241 */             if (StringUtils.isNullOrEmpty(loginConfigFile)) {
/* 242 */               final String localUser = this.user;
/* 243 */               final boolean debug = Boolean.getBoolean("sun.security.jgss.debug");
/* 244 */               loginConfig = new Configuration()
/*     */                 {
/*     */                   public AppConfigurationEntry[] getAppConfigurationEntry(String name) {
/* 247 */                     Map<String, String> options = new HashMap<>();
/* 248 */                     options.put("useTicketCache", "true");
/* 249 */                     options.put("renewTGT", "false");
/* 250 */                     options.put("principal", localUser);
/* 251 */                     options.put("debug", Boolean.toString(debug));
/* 252 */                     return new AppConfigurationEntry[] { new AppConfigurationEntry("com.sun.security.auth.module.Krb5LoginModule", AppConfigurationEntry.LoginModuleControlFlag.REQUIRED, options) };
/*     */                   }
/*     */                 };
/*     */             } 
/*     */ 
/*     */ 
/*     */             
/* 259 */             loginContext = new LoginContext("MySQLConnectorJ", null, this.credentialsCallbackHandler, loginConfig);
/* 260 */             loginContext.login();
/* 261 */             this.subject = loginContext.getSubject();
/*     */ 
/*     */             
/*     */             try {
/* 265 */               String localLdapServerHostname = ldapServerHostname;
/* 266 */               this.saslClient = Subject.<SaslClient>doAs(this.subject, () -> Sasl.createSaslClient(new String[] { this.authMech.getSaslServiceName() }, null, "ldap", localLdapServerHostname, null, null));
/*     */             
/*     */             }
/* 269 */             catch (PrivilegedActionException e) {
/*     */               
/* 271 */               throw (SaslException)e.getException();
/*     */             } 
/*     */             break;
/*     */           
/*     */           case SCRAM_SHA_1:
/*     */           case SCRAM_SHA_256:
/* 277 */             this.saslClient = Sasl.createSaslClient(new String[] { this.authMech.getSaslServiceName() }, null, null, null, null, this.credentialsCallbackHandler);
/*     */             break;
/*     */         } 
/*     */       
/* 281 */       } catch (LoginException|SaslException e) {
/* 282 */         throw ExceptionFactory.createException(
/* 283 */             Messages.getString("AuthenticationLdapSaslClientPlugin.FailCreateSaslClient", new Object[] { this.authMech.getMechName() }), e);
/*     */       } 
/*     */       
/* 286 */       if (this.saslClient == null) {
/* 287 */         throw ExceptionFactory.createException(
/* 288 */             Messages.getString("AuthenticationLdapSaslClientPlugin.FailCreateSaslClient", new Object[] { this.authMech.getMechName() }));
/*     */       }
/*     */     } 
/*     */     
/* 292 */     if (!this.saslClient.isComplete()) {
/*     */       
/*     */       try {
/* 295 */         Subject.doAs(this.subject, () -> {
/*     */               byte[] response = this.saslClient.evaluateChallenge(fromServer.readBytes(NativeConstants.StringSelfDataType.STRING_EOF));
/*     */               if (response != null) {
/*     */                 NativePacketPayload bresp = new NativePacketPayload(response);
/*     */                 bresp.setPosition(0);
/*     */                 toServer.add(bresp);
/*     */               } 
/*     */               return null;
/*     */             });
/* 304 */       } catch (PrivilegedActionException e) {
/* 305 */         throw ExceptionFactory.createException(
/* 306 */             Messages.getString("AuthenticationLdapSaslClientPlugin.ErrProcessingAuthIter", new Object[] { this.authMech.getMechName() }), e
/* 307 */             .getException());
/*     */       } 
/*     */     }
/* 310 */     return true;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\protocol\a\authentication\AuthenticationLdapSaslClientPlugin.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */