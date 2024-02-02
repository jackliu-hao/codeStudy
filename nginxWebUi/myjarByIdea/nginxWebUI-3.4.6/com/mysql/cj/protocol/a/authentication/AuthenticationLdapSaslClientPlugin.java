package com.mysql.cj.protocol.a.authentication;

import com.mysql.cj.Messages;
import com.mysql.cj.callback.MysqlCallbackHandler;
import com.mysql.cj.callback.UsernameCallback;
import com.mysql.cj.conf.PropertyKey;
import com.mysql.cj.exceptions.CJException;
import com.mysql.cj.exceptions.ExceptionFactory;
import com.mysql.cj.protocol.AuthenticationPlugin;
import com.mysql.cj.protocol.Protocol;
import com.mysql.cj.protocol.a.NativeConstants;
import com.mysql.cj.protocol.a.NativePacketPayload;
import com.mysql.cj.sasl.ScramShaSaslProvider;
import com.mysql.cj.util.StringUtils;
import java.security.PrivilegedActionException;
import java.security.Security;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
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

public class AuthenticationLdapSaslClientPlugin implements AuthenticationPlugin<NativePacketPayload> {
   public static String PLUGIN_NAME = "authentication_ldap_sasl_client";
   private static final String LOGIN_CONFIG_ENTRY = "MySQLConnectorJ";
   private static final String LDAP_SERVICE_NAME = "ldap";
   private Protocol<?> protocol = null;
   private MysqlCallbackHandler usernameCallbackHandler = null;
   private String user = null;
   private String password = null;
   private AuthenticationMechanisms authMech = null;
   private SaslClient saslClient = null;
   private Subject subject = null;
   private boolean firstPass = true;
   private CallbackHandler credentialsCallbackHandler = (cbs) -> {
      Callback[] var2 = cbs;
      int var3 = cbs.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         Callback cb = var2[var4];
         if (NameCallback.class.isAssignableFrom(cb.getClass())) {
            ((NameCallback)cb).setName(this.user);
         } else {
            if (!PasswordCallback.class.isAssignableFrom(cb.getClass())) {
               throw new UnsupportedCallbackException(cb, cb.getClass().getName());
            }

            char[] passwordChars = this.password == null ? new char[0] : this.password.toCharArray();
            ((PasswordCallback)cb).setPassword(passwordChars);
         }
      }

   };

   public void init(Protocol<NativePacketPayload> prot) {
      this.protocol = prot;
      Security.addProvider(new ScramShaSaslProvider());
   }

   public void init(Protocol<NativePacketPayload> prot, MysqlCallbackHandler cbh) {
      this.init(prot);
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
      this.authMech = null;
      this.saslClient = null;
      this.subject = null;
   }

   public void destroy() {
      this.protocol = null;
      this.reset();
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
         this.user = System.getProperty("user.name");
         if (this.usernameCallbackHandler != null) {
            this.usernameCallbackHandler.handle(new UsernameCallback(this.user));
         }
      }

   }

   public boolean nextAuthenticationStep(NativePacketPayload fromServer, List<NativePacketPayload> toServer) {
      toServer.clear();
      if (this.saslClient == null) {
         String authMechId = fromServer.readString(NativeConstants.StringSelfDataType.STRING_EOF, "ASCII");

         try {
            this.authMech = AuthenticationLdapSaslClientPlugin.AuthenticationMechanisms.fromValue(authMechId);
         } catch (CJException var12) {
            if (this.firstPass) {
               this.firstPass = false;
               return true;
            }

            throw var12;
         }

         this.firstPass = false;

         try {
            switch (this.authMech) {
               case GSSAPI:
                  String ldapServerHostname = (String)this.protocol.getPropertySet().getStringProperty(PropertyKey.ldapServerHostname).getValue();
                  String krb5Kdc;
                  if (StringUtils.isNullOrEmpty(ldapServerHostname)) {
                     krb5Kdc = System.getProperty("java.security.krb5.kdc");
                     if (!StringUtils.isNullOrEmpty(krb5Kdc)) {
                        ldapServerHostname = krb5Kdc;
                        int dotIndex = krb5Kdc.indexOf(46);
                        if (dotIndex > 0) {
                           ldapServerHostname = krb5Kdc.substring(0, dotIndex).toLowerCase(Locale.ENGLISH);
                        }
                     }
                  }

                  if (StringUtils.isNullOrEmpty(ldapServerHostname)) {
                     throw ExceptionFactory.createException(Messages.getString("AuthenticationLdapSaslClientPlugin.MissingLdapServerHostname"));
                  }

                  krb5Kdc = System.getProperty("java.security.auth.login.config");
                  Configuration loginConfig = null;
                  if (StringUtils.isNullOrEmpty(krb5Kdc)) {
                     final String localUser = this.user;
                     final boolean debug = Boolean.getBoolean("sun.security.jgss.debug");
                     loginConfig = new Configuration() {
                        public AppConfigurationEntry[] getAppConfigurationEntry(String name) {
                           Map<String, String> options = new HashMap();
                           options.put("useTicketCache", "true");
                           options.put("renewTGT", "false");
                           options.put("principal", localUser);
                           options.put("debug", Boolean.toString(debug));
                           return new AppConfigurationEntry[]{new AppConfigurationEntry("com.sun.security.auth.module.Krb5LoginModule", LoginModuleControlFlag.REQUIRED, options)};
                        }
                     };
                  }

                  LoginContext loginContext = new LoginContext("MySQLConnectorJ", (Subject)null, this.credentialsCallbackHandler, loginConfig);
                  loginContext.login();
                  this.subject = loginContext.getSubject();

                  try {
                     this.saslClient = (SaslClient)Subject.doAs(this.subject, () -> {
                        return Sasl.createSaslClient(new String[]{this.authMech.getSaslServiceName()}, (String)null, "ldap", ldapServerHostname, (Map)null, (CallbackHandler)null);
                     });
                     break;
                  } catch (PrivilegedActionException var10) {
                     throw (SaslException)var10.getException();
                  }
               case SCRAM_SHA_1:
               case SCRAM_SHA_256:
                  this.saslClient = Sasl.createSaslClient(new String[]{this.authMech.getSaslServiceName()}, (String)null, (String)null, (String)null, (Map)null, this.credentialsCallbackHandler);
            }
         } catch (SaslException | LoginException var11) {
            throw ExceptionFactory.createException((String)Messages.getString("AuthenticationLdapSaslClientPlugin.FailCreateSaslClient", new Object[]{this.authMech.getMechName()}), (Throwable)var11);
         }

         if (this.saslClient == null) {
            throw ExceptionFactory.createException(Messages.getString("AuthenticationLdapSaslClientPlugin.FailCreateSaslClient", new Object[]{this.authMech.getMechName()}));
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
         } catch (PrivilegedActionException var9) {
            throw ExceptionFactory.createException((String)Messages.getString("AuthenticationLdapSaslClientPlugin.ErrProcessingAuthIter", new Object[]{this.authMech.getMechName()}), (Throwable)var9.getException());
         }
      }

      return true;
   }

   private static enum AuthenticationMechanisms {
      SCRAM_SHA_1("SCRAM-SHA-1", "MYSQLCJ-SCRAM-SHA-1"),
      SCRAM_SHA_256("SCRAM-SHA-256", "MYSQLCJ-SCRAM-SHA-256"),
      GSSAPI("GSSAPI", "GSSAPI");

      private String mechName = null;
      private String saslServiceName = null;

      private AuthenticationMechanisms(String mechName, String serviceName) {
         this.mechName = mechName;
         this.saslServiceName = serviceName;
      }

      static AuthenticationMechanisms fromValue(String mechName) {
         AuthenticationMechanisms[] var1 = values();
         int var2 = var1.length;

         for(int var3 = 0; var3 < var2; ++var3) {
            AuthenticationMechanisms am = var1[var3];
            if (am.mechName.equalsIgnoreCase(mechName)) {
               return am;
            }
         }

         throw ExceptionFactory.createException(Messages.getString("AuthenticationLdapSaslClientPlugin.UnsupportedAuthMech", new String[]{mechName}));
      }

      String getMechName() {
         return this.mechName;
      }

      String getSaslServiceName() {
         return this.saslServiceName;
      }
   }
}
