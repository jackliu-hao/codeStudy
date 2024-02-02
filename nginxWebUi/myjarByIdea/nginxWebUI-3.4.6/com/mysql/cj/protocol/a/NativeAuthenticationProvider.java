package com.mysql.cj.protocol.a;

import com.mysql.cj.Constants;
import com.mysql.cj.Messages;
import com.mysql.cj.callback.MysqlCallbackHandler;
import com.mysql.cj.callback.UsernameCallback;
import com.mysql.cj.conf.PropertyDefinitions;
import com.mysql.cj.conf.PropertyKey;
import com.mysql.cj.conf.PropertySet;
import com.mysql.cj.conf.RuntimeProperty;
import com.mysql.cj.exceptions.ExceptionFactory;
import com.mysql.cj.exceptions.ExceptionInterceptor;
import com.mysql.cj.exceptions.UnableToConnectException;
import com.mysql.cj.exceptions.WrongArgumentException;
import com.mysql.cj.protocol.AuthenticationPlugin;
import com.mysql.cj.protocol.AuthenticationProvider;
import com.mysql.cj.protocol.Protocol;
import com.mysql.cj.protocol.ServerSession;
import com.mysql.cj.protocol.a.authentication.AuthenticationKerberosClient;
import com.mysql.cj.protocol.a.authentication.AuthenticationLdapSaslClientPlugin;
import com.mysql.cj.protocol.a.authentication.AuthenticationOciClient;
import com.mysql.cj.protocol.a.authentication.CachingSha2PasswordPlugin;
import com.mysql.cj.protocol.a.authentication.MysqlClearPasswordPlugin;
import com.mysql.cj.protocol.a.authentication.MysqlNativePasswordPlugin;
import com.mysql.cj.protocol.a.authentication.MysqlOldPasswordPlugin;
import com.mysql.cj.protocol.a.authentication.Sha256PasswordPlugin;
import com.mysql.cj.protocol.a.result.OkPacket;
import com.mysql.cj.util.StringUtils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class NativeAuthenticationProvider implements AuthenticationProvider<NativePacketPayload> {
   private static final int AUTH_411_OVERHEAD = 33;
   private static final String NONE = "none";
   private String seed;
   private String username;
   private String password;
   private String database;
   private boolean useConnectWithDb;
   private ExceptionInterceptor exceptionInterceptor;
   private PropertySet propertySet;
   private Protocol<NativePacketPayload> protocol;
   private Map<String, AuthenticationPlugin<NativePacketPayload>> authenticationPlugins = null;
   private String clientDefaultAuthenticationPluginName = null;
   private boolean clientDefaultAuthenticationPluginExplicitelySet = false;
   private String serverDefaultAuthenticationPluginName = null;
   private MysqlCallbackHandler callbackHandler = (cb) -> {
      if (cb instanceof UsernameCallback) {
         this.username = ((UsernameCallback)cb).getUsername();
      }

   };

   public void init(Protocol<NativePacketPayload> prot, PropertySet propSet, ExceptionInterceptor excInterceptor) {
      this.protocol = prot;
      this.propertySet = propSet;
      this.exceptionInterceptor = excInterceptor;
   }

   public void connect(String user, String pass, String db) {
      ServerSession sessState = this.protocol.getServerSession();
      this.username = user;
      this.password = pass;
      this.database = db;
      NativeCapabilities capabilities = (NativeCapabilities)sessState.getCapabilities();
      NativePacketPayload buf = capabilities.getInitialHandshakePacket();
      PropertyDefinitions.SslMode sslMode = (PropertyDefinitions.SslMode)this.propertySet.getEnumProperty(PropertyKey.sslMode).getValue();
      int capabilityFlags = capabilities.getCapabilityFlags();
      if ((capabilityFlags & 2048) == 0 && sslMode != PropertyDefinitions.SslMode.DISABLED && sslMode != PropertyDefinitions.SslMode.PREFERRED) {
         throw (UnableToConnectException)ExceptionFactory.createException(UnableToConnectException.class, Messages.getString("MysqlIO.15"), this.getExceptionInterceptor());
      } else if ((capabilityFlags & '耀') == 0) {
         throw (UnableToConnectException)ExceptionFactory.createException(UnableToConnectException.class, "CLIENT_SECURE_CONNECTION is required", this.getExceptionInterceptor());
      } else if ((capabilityFlags & 524288) == 0) {
         throw (UnableToConnectException)ExceptionFactory.createException(UnableToConnectException.class, "CLIENT_PLUGIN_AUTH is required", this.getExceptionInterceptor());
      } else {
         sessState.setStatusFlags(capabilities.getStatusFlags());
         int authPluginDataLength = capabilities.getAuthPluginDataLength();
         StringBuilder fullSeed = new StringBuilder(authPluginDataLength > 0 ? authPluginDataLength : 20);
         fullSeed.append(capabilities.getSeed());
         fullSeed.append(authPluginDataLength > 0 ? buf.readString(NativeConstants.StringLengthDataType.STRING_FIXED, "ASCII", authPluginDataLength - 8) : buf.readString(NativeConstants.StringSelfDataType.STRING_TERM, "ASCII"));
         this.seed = fullSeed.toString();
         this.useConnectWithDb = this.database != null && this.database.length() > 0 && !(Boolean)this.propertySet.getBooleanProperty(PropertyKey.createDatabaseIfNotExist).getValue();
         long clientParam = (long)(capabilityFlags & 1 | ((Boolean)this.propertySet.getBooleanProperty(PropertyKey.useAffectedRows).getValue() ? 0 : capabilityFlags & 2) | capabilityFlags & 4 | (this.useConnectWithDb ? capabilityFlags & 8 : 0) | ((Boolean)this.propertySet.getBooleanProperty(PropertyKey.useCompression).getValue() ? capabilityFlags & 32 : 0) | (!(Boolean)this.propertySet.getBooleanProperty(PropertyKey.allowLoadLocalInfile).getValue() && !this.propertySet.getStringProperty(PropertyKey.allowLoadLocalInfileInPath).isExplicitlySet() ? 0 : capabilityFlags & 128) | capabilityFlags & 512 | ((Boolean)this.propertySet.getBooleanProperty(PropertyKey.interactiveClient).getValue() ? capabilityFlags & 1024 : 0) | (this.propertySet.getEnumProperty(PropertyKey.sslMode).getValue() != PropertyDefinitions.SslMode.DISABLED ? capabilityFlags & 2048 : 0) | capabilityFlags & 8192 | '耀' | ((Boolean)this.propertySet.getBooleanProperty(PropertyKey.allowMultiQueries).getValue() ? capabilityFlags & 65536 : 0) | capabilityFlags & 131072 | capabilityFlags & 262144 | 524288 | ("none".equals(this.propertySet.getStringProperty(PropertyKey.connectionAttributes).getValue()) ? 0 : capabilityFlags & 1048576) | capabilityFlags & 2097152 | ((Boolean)this.propertySet.getBooleanProperty(PropertyKey.disconnectOnExpiredPasswords).getValue() ? 0 : capabilityFlags & 4194304) | ((Boolean)this.propertySet.getBooleanProperty(PropertyKey.trackSessionState).getValue() ? capabilityFlags & 8388608 : 0) | capabilityFlags & 16777216 | capabilityFlags & 134217728 | capabilityFlags & 268435456);
         sessState.setClientParam(clientParam);
         if ((clientParam & 2048L) != 0L) {
            this.protocol.negotiateSSLConnection();
         }

         if (buf.isOKPacket()) {
            throw ExceptionFactory.createException(Messages.getString("AuthenticationProvider.UnexpectedAuthenticationApproval"), this.getExceptionInterceptor());
         } else {
            this.proceedHandshakeWithPluggableAuthentication(buf);
            this.password = null;
         }
      }
   }

   private void loadAuthenticationPlugins() {
      RuntimeProperty<String> defaultAuthenticationPluginProp = this.propertySet.getStringProperty(PropertyKey.defaultAuthenticationPlugin);
      String defaultAuthenticationPluginValue = (String)defaultAuthenticationPluginProp.getValue();
      if (defaultAuthenticationPluginValue != null && !"".equals(defaultAuthenticationPluginValue.trim())) {
         String disabledPlugins = (String)this.propertySet.getStringProperty(PropertyKey.disabledAuthenticationPlugins).getValue();
         List disabledAuthenticationPlugins;
         if (disabledPlugins != null && !"".equals(disabledPlugins)) {
            disabledAuthenticationPlugins = StringUtils.split(disabledPlugins, ",", true);
         } else {
            disabledAuthenticationPlugins = Collections.EMPTY_LIST;
         }

         this.authenticationPlugins = new HashMap();
         List<AuthenticationPlugin<NativePacketPayload>> pluginsToInit = new LinkedList();
         pluginsToInit.add(new MysqlNativePasswordPlugin());
         pluginsToInit.add(new MysqlClearPasswordPlugin());
         pluginsToInit.add(new Sha256PasswordPlugin());
         pluginsToInit.add(new CachingSha2PasswordPlugin());
         pluginsToInit.add(new MysqlOldPasswordPlugin());
         pluginsToInit.add(new AuthenticationLdapSaslClientPlugin());
         pluginsToInit.add(new AuthenticationKerberosClient());
         pluginsToInit.add(new AuthenticationOciClient());
         String authenticationPluginClasses = (String)this.propertySet.getStringProperty(PropertyKey.authenticationPlugins).getValue();
         Iterator var8;
         if (authenticationPluginClasses != null && !"".equals(authenticationPluginClasses.trim())) {
            List<String> pluginsToCreate = StringUtils.split(authenticationPluginClasses, ",", true);
            var8 = pluginsToCreate.iterator();

            while(var8.hasNext()) {
               String className = (String)var8.next();

               try {
                  pluginsToInit.add((AuthenticationPlugin)Class.forName(className).newInstance());
               } catch (Throwable var14) {
                  throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, Messages.getString("AuthenticationProvider.BadAuthenticationPlugin", new Object[]{className}), var14, this.exceptionInterceptor);
               }
            }
         }

         boolean defaultFound = false;
         var8 = pluginsToInit.iterator();

         String pluginProtocolName;
         String pluginClassName;
         boolean disabledByClassName;
         do {
            label63:
            do {
               while(var8.hasNext()) {
                  AuthenticationPlugin<NativePacketPayload> plugin = (AuthenticationPlugin)var8.next();
                  pluginProtocolName = plugin.getProtocolPluginName();
                  pluginClassName = plugin.getClass().getName();
                  boolean disabledByProtocolName = disabledAuthenticationPlugins.contains(pluginProtocolName);
                  disabledByClassName = disabledAuthenticationPlugins.contains(pluginClassName);
                  if (disabledByProtocolName || disabledByClassName) {
                     continue label63;
                  }

                  this.authenticationPlugins.put(pluginProtocolName, plugin);
                  if (!defaultFound && (defaultAuthenticationPluginValue.equals(pluginProtocolName) || defaultAuthenticationPluginValue.equals(pluginClassName))) {
                     this.clientDefaultAuthenticationPluginName = pluginProtocolName;
                     this.clientDefaultAuthenticationPluginExplicitelySet = defaultAuthenticationPluginProp.isExplicitlySet();
                     defaultFound = true;
                  }
               }

               if (!defaultFound) {
                  throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, Messages.getString("AuthenticationProvider.DefaultAuthenticationPluginIsNotListed", new Object[]{defaultAuthenticationPluginValue}), this.getExceptionInterceptor());
               }

               return;
            } while(defaultFound);
         } while(!defaultAuthenticationPluginValue.equals(pluginProtocolName) && !defaultAuthenticationPluginValue.equals(pluginClassName));

         throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, Messages.getString("AuthenticationProvider.BadDisabledAuthenticationPlugin", new Object[]{disabledByClassName ? pluginClassName : pluginProtocolName}), this.getExceptionInterceptor());
      } else {
         throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, Messages.getString("AuthenticationProvider.BadDefaultAuthenticationPlugin", new Object[]{defaultAuthenticationPluginValue}), this.getExceptionInterceptor());
      }
   }

   private AuthenticationPlugin<NativePacketPayload> getAuthenticationPlugin(String pluginName) {
      AuthenticationPlugin<NativePacketPayload> plugin = (AuthenticationPlugin)this.authenticationPlugins.get(pluginName);
      if (plugin == null) {
         return null;
      } else {
         if (!plugin.isReusable()) {
            try {
               plugin = (AuthenticationPlugin)plugin.getClass().newInstance();
            } catch (Throwable var4) {
               throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, Messages.getString("AuthenticationProvider.BadAuthenticationPlugin", new Object[]{plugin.getClass().getName()}), var4, this.getExceptionInterceptor());
            }
         }

         plugin.init(this.protocol, this.callbackHandler);
         return plugin;
      }
   }

   private void checkConfidentiality(AuthenticationPlugin<?> plugin) {
      if (plugin.requiresConfidentiality() && !this.protocol.getSocketConnection().isSSLEstablished()) {
         throw ExceptionFactory.createException(Messages.getString("AuthenticationProvider.AuthenticationPluginRequiresSSL", new Object[]{plugin.getProtocolPluginName()}), this.getExceptionInterceptor());
      }
   }

   private void proceedHandshakeWithPluggableAuthentication(NativePacketPayload challenge) {
      ServerSession serverSession = this.protocol.getServerSession();
      if (this.authenticationPlugins == null) {
         this.loadAuthenticationPlugins();
      }

      boolean forChangeUser = true;
      if (challenge != null) {
         this.serverDefaultAuthenticationPluginName = challenge.readString(NativeConstants.StringSelfDataType.STRING_TERM, "ASCII");
         forChangeUser = false;
      }

      serverSession.getCharsetSettings().configurePreHandshake(forChangeUser);
      String pluginName;
      if (this.clientDefaultAuthenticationPluginExplicitelySet) {
         pluginName = this.clientDefaultAuthenticationPluginName;
      } else {
         pluginName = this.serverDefaultAuthenticationPluginName != null ? this.serverDefaultAuthenticationPluginName : this.clientDefaultAuthenticationPluginName;
      }

      AuthenticationPlugin<NativePacketPayload> plugin = this.getAuthenticationPlugin(pluginName);
      if (plugin == null) {
         pluginName = this.clientDefaultAuthenticationPluginName;
         plugin = this.getAuthenticationPlugin(pluginName);
      }

      boolean skipPassword = false;
      if (pluginName.equals(Sha256PasswordPlugin.PLUGIN_NAME) && !pluginName.equals(this.clientDefaultAuthenticationPluginName) && !this.protocol.getSocketConnection().isSSLEstablished() && this.propertySet.getStringProperty(PropertyKey.serverRSAPublicKeyFile).getValue() == null && !(Boolean)this.propertySet.getBooleanProperty(PropertyKey.allowPublicKeyRetrieval).getValue()) {
         plugin = this.getAuthenticationPlugin(this.clientDefaultAuthenticationPluginName);
         skipPassword = true;
      }

      this.checkConfidentiality(plugin);
      NativePacketPayload fromServer = new NativePacketPayload(StringUtils.getBytes(this.seed));
      String sourceOfAuthData = this.serverDefaultAuthenticationPluginName;
      NativePacketPayload lastSent = null;
      ArrayList<NativePacketPayload> toServer = new ArrayList();
      boolean firstPacket = true;
      int mfaNthFactor = 1;

      int counter;
      for(counter = 100; 0 < counter--; sourceOfAuthData = pluginName) {
         plugin.setAuthenticationParameters(this.username, skipPassword ? null : this.getNthFactorPassword(mfaNthFactor));
         plugin.setSourceOfAuthData(sourceOfAuthData);
         plugin.nextAuthenticationStep(fromServer, toServer);
         if (firstPacket) {
            NativePacketPayload authData = toServer.isEmpty() ? new NativePacketPayload(0) : (NativePacketPayload)toServer.get(0);
            if (forChangeUser) {
               lastSent = this.createChangeUserPacket(serverSession, plugin.getProtocolPluginName(), authData);
               this.protocol.send(lastSent, lastSent.getPosition());
            } else {
               lastSent = this.createHandshakeResponsePacket(serverSession, plugin.getProtocolPluginName(), authData);
               this.protocol.send(lastSent, lastSent.getPosition());
            }

            firstPacket = false;
         } else if (!toServer.isEmpty()) {
            toServer.forEach((b) -> {
               this.protocol.send(b, b.getPayloadLength());
            });
         }

         NativePacketPayload lastReceived = (NativePacketPayload)this.protocol.checkErrorMessage();
         if (lastReceived.isOKPacket()) {
            OkPacket ok = OkPacket.parse(lastReceived, (String)null);
            serverSession.setStatusFlags(ok.getStatusFlags(), true);
            serverSession.getServerSessionStateController().setSessionStateChanges(ok.getSessionStateChanges());
            plugin.destroy();
            break;
         }

         if (lastReceived.isAuthMethodSwitchRequestPacket()) {
            skipPassword = false;
            pluginName = lastReceived.readString(NativeConstants.StringSelfDataType.STRING_TERM, "ASCII");
            if (plugin.getProtocolPluginName().equals(pluginName)) {
               plugin.reset();
            } else {
               plugin.destroy();
               plugin = this.getAuthenticationPlugin(pluginName);
               if (plugin == null) {
                  throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, Messages.getString("AuthenticationProvider.BadAuthenticationPlugin", new Object[]{pluginName}), this.getExceptionInterceptor());
               }
            }

            this.checkConfidentiality(plugin);
            fromServer = new NativePacketPayload(lastReceived.readBytes(NativeConstants.StringSelfDataType.STRING_EOF));
         } else if (lastReceived.isAuthNextFactorPacket()) {
            ++mfaNthFactor;
            skipPassword = false;
            pluginName = lastReceived.readString(NativeConstants.StringSelfDataType.STRING_TERM, "ASCII");
            if (plugin.getProtocolPluginName().equals(pluginName)) {
               plugin.reset();
            } else {
               plugin.destroy();
               plugin = this.getAuthenticationPlugin(pluginName);
               if (plugin == null) {
                  throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, Messages.getString("AuthenticationProvider.BadAuthenticationPlugin", new Object[]{pluginName}), this.getExceptionInterceptor());
               }
            }

            this.checkConfidentiality(plugin);
            fromServer = new NativePacketPayload(lastReceived.readBytes(NativeConstants.StringSelfDataType.STRING_EOF));
         } else {
            if (!this.protocol.versionMeetsMinimum(5, 5, 16)) {
               lastReceived.setPosition(lastReceived.getPosition() - 1);
            }

            fromServer = new NativePacketPayload(lastReceived.readBytes(NativeConstants.StringSelfDataType.STRING_EOF));
         }
      }

      if (counter == 0) {
         throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, Messages.getString("CommunicationsException.TooManyAuthenticationPluginNegotiations"), this.getExceptionInterceptor());
      } else {
         this.protocol.afterHandshake();
         if (!this.useConnectWithDb) {
            this.protocol.changeDatabase(this.database);
         }

      }
   }

   private String getNthFactorPassword(int nthFactor) {
      switch (nthFactor) {
         case 1:
            return this.password == null ? (String)this.protocol.getPropertySet().getStringProperty(PropertyKey.password1).getValue() : this.password;
         case 2:
            return (String)this.protocol.getPropertySet().getStringProperty(PropertyKey.password2).getValue();
         case 3:
            return (String)this.protocol.getPropertySet().getStringProperty(PropertyKey.password3).getValue();
         default:
            return null;
      }
   }

   private Map<String, String> getConnectionAttributesMap(String attStr) {
      Map<String, String> attMap = new HashMap();
      if (attStr != null) {
         String[] pairs = attStr.split(",");
         String[] var4 = pairs;
         int var5 = pairs.length;

         for(int var6 = 0; var6 < var5; ++var6) {
            String pair = var4[var6];
            int keyEnd = pair.indexOf(":");
            if (keyEnd > 0 && keyEnd + 1 < pair.length()) {
               attMap.put(pair.substring(0, keyEnd), pair.substring(keyEnd + 1));
            }
         }
      }

      attMap.put("_client_name", "MySQL Connector/J");
      attMap.put("_client_version", "8.0.28");
      attMap.put("_runtime_vendor", Constants.JVM_VENDOR);
      attMap.put("_runtime_version", Constants.JVM_VERSION);
      attMap.put("_client_license", "GPL");
      return attMap;
   }

   private void appendConnectionAttributes(NativePacketPayload buf, String attributes, String enc) {
      NativePacketPayload lb = new NativePacketPayload(100);
      Map<String, String> attMap = this.getConnectionAttributesMap(attributes);
      Iterator var6 = attMap.keySet().iterator();

      while(var6.hasNext()) {
         String key = (String)var6.next();
         lb.writeBytes(NativeConstants.StringSelfDataType.STRING_LENENC, StringUtils.getBytes(key, enc));
         lb.writeBytes(NativeConstants.StringSelfDataType.STRING_LENENC, StringUtils.getBytes((String)attMap.get(key), enc));
      }

      buf.writeInteger(NativeConstants.IntegerDataType.INT_LENENC, (long)lb.getPosition());
      buf.writeBytes((NativeConstants.StringLengthDataType)NativeConstants.StringLengthDataType.STRING_FIXED, lb.getByteBuffer(), 0, lb.getPosition());
   }

   public ExceptionInterceptor getExceptionInterceptor() {
      return this.exceptionInterceptor;
   }

   public void changeUser(String user, String pass, String db) {
      this.username = user;
      this.password = pass;
      this.database = db;
      this.proceedHandshakeWithPluggableAuthentication((NativePacketPayload)null);
      this.password = null;
   }

   private NativePacketPayload createHandshakeResponsePacket(ServerSession serverSession, String pluginName, NativePacketPayload authData) {
      long clientParam = serverSession.getClientParam();
      int collationIndex = serverSession.getCharsetSettings().configurePreHandshake(false);
      String enc = serverSession.getCharsetSettings().getPasswordCharacterEncoding();
      int userLength = this.username == null ? 0 : this.username.length();
      NativePacketPayload last_sent = new NativePacketPayload(88 + 3 * userLength + (this.useConnectWithDb ? 3 * this.database.length() : 0));
      last_sent.writeInteger(NativeConstants.IntegerDataType.INT4, clientParam);
      last_sent.writeInteger(NativeConstants.IntegerDataType.INT4, 16777215L);
      last_sent.writeInteger(NativeConstants.IntegerDataType.INT1, (long)collationIndex);
      last_sent.writeBytes(NativeConstants.StringLengthDataType.STRING_FIXED, new byte[23]);
      last_sent.writeBytes(NativeConstants.StringSelfDataType.STRING_TERM, StringUtils.getBytes(this.username, enc));
      if ((clientParam & 2097152L) != 0L) {
         last_sent.writeBytes(NativeConstants.StringSelfDataType.STRING_LENENC, authData.readBytes(NativeConstants.StringSelfDataType.STRING_EOF));
      } else {
         last_sent.writeInteger(NativeConstants.IntegerDataType.INT1, (long)authData.getPayloadLength());
         last_sent.writeBytes(NativeConstants.StringSelfDataType.STRING_EOF, authData.getByteBuffer());
      }

      if (this.useConnectWithDb) {
         last_sent.writeBytes(NativeConstants.StringSelfDataType.STRING_TERM, StringUtils.getBytes(this.database, enc));
      }

      last_sent.writeBytes(NativeConstants.StringSelfDataType.STRING_TERM, StringUtils.getBytes(pluginName, enc));
      if ((clientParam & 1048576L) != 0L) {
         this.appendConnectionAttributes(last_sent, (String)this.propertySet.getStringProperty(PropertyKey.connectionAttributes).getValue(), enc);
      }

      return last_sent;
   }

   private NativePacketPayload createChangeUserPacket(ServerSession serverSession, String pluginName, NativePacketPayload authData) {
      long clientParam = serverSession.getClientParam();
      int collationIndex = serverSession.getCharsetSettings().configurePreHandshake(false);
      String enc = serverSession.getCharsetSettings().getPasswordCharacterEncoding();
      NativePacketPayload last_sent = new NativePacketPayload(88 + 3 * this.username.length() + (this.useConnectWithDb ? 3 * this.database.length() : 1) + 1);
      last_sent.writeInteger(NativeConstants.IntegerDataType.INT1, 17L);
      last_sent.writeBytes(NativeConstants.StringSelfDataType.STRING_TERM, StringUtils.getBytes(this.username, enc));
      if (authData.getPayloadLength() < 256) {
         last_sent.writeInteger(NativeConstants.IntegerDataType.INT1, (long)authData.getPayloadLength());
         last_sent.writeBytes((NativeConstants.StringSelfDataType)NativeConstants.StringSelfDataType.STRING_EOF, authData.getByteBuffer(), 0, authData.getPayloadLength());
      } else {
         last_sent.writeInteger(NativeConstants.IntegerDataType.INT1, 0L);
      }

      if (this.useConnectWithDb) {
         last_sent.writeBytes(NativeConstants.StringSelfDataType.STRING_TERM, StringUtils.getBytes(this.database, enc));
      } else {
         last_sent.writeInteger(NativeConstants.IntegerDataType.INT1, 0L);
      }

      last_sent.writeInteger(NativeConstants.IntegerDataType.INT1, (long)collationIndex);
      last_sent.writeInteger(NativeConstants.IntegerDataType.INT1, 0L);
      last_sent.writeBytes(NativeConstants.StringSelfDataType.STRING_TERM, StringUtils.getBytes(pluginName, enc));
      if ((clientParam & 1048576L) != 0L) {
         this.appendConnectionAttributes(last_sent, (String)this.propertySet.getStringProperty(PropertyKey.connectionAttributes).getValue(), enc);
      }

      return last_sent;
   }
}
