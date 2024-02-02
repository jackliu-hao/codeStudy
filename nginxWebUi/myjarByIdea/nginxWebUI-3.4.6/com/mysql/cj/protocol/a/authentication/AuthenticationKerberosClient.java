package com.mysql.cj.protocol.a.authentication;

import com.mysql.cj.Messages;
import com.mysql.cj.callback.MysqlCallbackHandler;
import com.mysql.cj.callback.UsernameCallback;
import com.mysql.cj.exceptions.CJException;
import com.mysql.cj.exceptions.ExceptionFactory;
import com.mysql.cj.protocol.AuthenticationPlugin;
import com.mysql.cj.protocol.Protocol;
import com.mysql.cj.protocol.a.NativeConstants;
import com.mysql.cj.protocol.a.NativePacketPayload;
import com.mysql.cj.util.StringUtils;
import java.security.Principal;
import java.security.PrivilegedActionException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.login.AppConfigurationEntry;
import javax.security.auth.login.Configuration;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;
import javax.security.auth.login.AppConfigurationEntry.LoginModuleControlFlag;
import javax.security.sasl.Sasl;
import javax.security.sasl.SaslClient;
import javax.security.sasl.SaslException;

public class AuthenticationKerberosClient implements AuthenticationPlugin<NativePacketPayload> {
   public static String PLUGIN_NAME = "authentication_kerberos_client";
   private static final String LOGIN_CONFIG_ENTRY = "MySQLConnectorJ";
   private static final String AUTHENTICATION_MECHANISM = "GSSAPI";
   private String sourceOfAuthData;
   private MysqlCallbackHandler usernameCallbackHandler;
   private String user;
   private String password;
   private String userPrincipalName;
   private Subject subject;
   private String cachedPrincipalName;
   private CallbackHandler credentialsCallbackHandler;
   private SaslClient saslClient;

   public AuthenticationKerberosClient() {
      this.sourceOfAuthData = PLUGIN_NAME;
      this.usernameCallbackHandler = null;
      this.user = null;
      this.password = null;
      this.userPrincipalName = null;
      this.subject = null;
      this.cachedPrincipalName = null;
      this.credentialsCallbackHandler = (cbs) -> {
         Callback[] var2 = cbs;
         int var3 = cbs.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            Callback cb = var2[var4];
            if (NameCallback.class.isAssignableFrom(cb.getClass())) {
               ((NameCallback)cb).setName(this.userPrincipalName);
            } else {
               if (!PasswordCallback.class.isAssignableFrom(cb.getClass())) {
                  throw new UnsupportedCallbackException(cb, cb.getClass().getName());
               }

               ((PasswordCallback)cb).setPassword(this.password == null ? new char[0] : this.password.toCharArray());
            }
         }

      };
      this.saslClient = null;
   }

   public void init(Protocol<NativePacketPayload> prot, MysqlCallbackHandler cbh) {
      this.usernameCallbackHandler = cbh;
   }

   public void reset() {
      if (this.saslClient != null) {
         try {
            this.saslClient.dispose();
         } catch (SaslException var2) {
         }
      }

      this.user = null;
      this.password = null;
      this.saslClient = null;
   }

   public void destroy() {
      this.reset();
      this.userPrincipalName = null;
      this.subject = null;
      this.cachedPrincipalName = null;
   }

   public String getProtocolPluginName() {
      return PLUGIN_NAME;
   }

   public boolean requiresConfidentiality() {
      return false;
   }

   public boolean isReusable() {
      return false;
   }

   public void setAuthenticationParameters(String user, String password) {
      this.user = user;
      this.password = password;
      if (this.user == null) {
         try {
            this.initializeAuthentication();
            int pos = this.cachedPrincipalName.indexOf(64);
            if (pos >= 0) {
               this.user = this.cachedPrincipalName.substring(0, pos);
            } else {
               this.user = this.cachedPrincipalName;
            }
         } catch (CJException var4) {
            this.user = System.getProperty("user.name");
         }

         if (this.usernameCallbackHandler != null) {
            this.usernameCallbackHandler.handle(new UsernameCallback(this.user));
         }
      }

   }

   public void setSourceOfAuthData(String sourceOfAuthData) {
      this.sourceOfAuthData = sourceOfAuthData;
   }

   public boolean nextAuthenticationStep(NativePacketPayload fromServer, List<NativePacketPayload> toServer) {
      toServer.clear();
      if (this.sourceOfAuthData.equals(PLUGIN_NAME) && fromServer.getPayloadLength() != 0) {
         if (this.saslClient == null) {
            try {
               int servicePrincipalNameLength = (int)fromServer.readInteger(NativeConstants.IntegerDataType.INT2);
               String servicePrincipalName = fromServer.readString(NativeConstants.StringLengthDataType.STRING_VAR, "ASCII", servicePrincipalNameLength);
               String primary = "";
               String instance = "";
               int posAt = servicePrincipalName.indexOf(64);
               if (posAt < 0) {
                  posAt = servicePrincipalName.length();
               }

               int posSlash = servicePrincipalName.lastIndexOf(47, posAt);
               if (posSlash >= 0) {
                  primary = servicePrincipalName.substring(0, posSlash);
                  instance = servicePrincipalName.substring(posSlash + 1, posAt);
               } else {
                  primary = servicePrincipalName.substring(0, posAt);
               }

               int userPrincipalRealmLength = (int)fromServer.readInteger(NativeConstants.IntegerDataType.INT2);
               String userPrincipalRealm = fromServer.readString(NativeConstants.StringLengthDataType.STRING_VAR, "ASCII", userPrincipalRealmLength);
               this.userPrincipalName = this.user + "@" + userPrincipalRealm;
               this.initializeAuthentication();

               try {
                  this.saslClient = (SaslClient)Subject.doAs(this.subject, () -> {
                     return Sasl.createSaslClient(new String[]{"GSSAPI"}, (String)null, primary, instance, (Map)null, (CallbackHandler)null);
                  });
               } catch (PrivilegedActionException var14) {
                  throw (SaslException)var14.getException();
               }
            } catch (SaslException var15) {
               throw ExceptionFactory.createException((String)Messages.getString("AuthenticationKerberosClientPlugin.FailCreateSaslClient", new Object[]{"GSSAPI"}), (Throwable)var15);
            }

            if (this.saslClient == null) {
               throw ExceptionFactory.createException(Messages.getString("AuthenticationKerberosClientPlugin.FailCreateSaslClient", new Object[]{"GSSAPI"}));
            }
         }

         if (!this.saslClient.isComplete()) {
            try {
               Subject.doAs(this.subject, () -> {
                  byte[] response = this.saslClient.evaluateChallenge(fromServer.readBytes(NativeConstants.StringSelfDataType.STRING_EOF));
                  if (response != null) {
                     NativePacketPayload bresp = new NativePacketPayload(response);
                     bresp.setPosition(0);
                     toServer.add(bresp);
                  }

                  return null;
               });
            } catch (PrivilegedActionException var13) {
               throw ExceptionFactory.createException((String)Messages.getString("AuthenticationKerberosClientPlugin.ErrProcessingAuthIter", new Object[]{"GSSAPI"}), (Throwable)var13.getException());
            }
         }

         return true;
      } else {
         return true;
      }
   }

   private void initializeAuthentication() {
      if (this.subject == null || this.cachedPrincipalName == null || !this.cachedPrincipalName.equals(this.userPrincipalName)) {
         String loginConfigFile = System.getProperty("java.security.auth.login.config");
         Configuration loginConfig = null;
         if (StringUtils.isNullOrEmpty(loginConfigFile)) {
            final String localUser = this.userPrincipalName;
            final boolean debug = Boolean.getBoolean("sun.security.jgss.debug");
            loginConfig = new Configuration() {
               public AppConfigurationEntry[] getAppConfigurationEntry(String name) {
                  Map<String, String> options = new HashMap();
                  options.put("useTicketCache", "true");
                  options.put("renewTGT", "false");
                  if (localUser != null) {
                     options.put("principal", localUser);
                  }

                  options.put("debug", Boolean.toString(debug));
                  return new AppConfigurationEntry[]{new AppConfigurationEntry("com.sun.security.auth.module.Krb5LoginModule", LoginModuleControlFlag.REQUIRED, options)};
               }
            };
         }

         try {
            LoginContext loginContext = new LoginContext("MySQLConnectorJ", (Subject)null, this.credentialsCallbackHandler, loginConfig);
            loginContext.login();
            this.subject = loginContext.getSubject();
            this.cachedPrincipalName = ((Principal)this.subject.getPrincipals().iterator().next()).getName();
         } catch (LoginException var5) {
            throw ExceptionFactory.createException((String)Messages.getString("AuthenticationKerberosClientPlugin.FailAuthenticateUser"), (Throwable)var5);
         }
      }
   }
}
