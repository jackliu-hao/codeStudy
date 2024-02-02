package com.mysql.cj.sasl;

import com.mysql.cj.util.StringUtils;
import java.io.IOException;
import java.util.Map;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.sasl.SaslClient;
import javax.security.sasl.SaslClientFactory;
import javax.security.sasl.SaslException;

public class ScramShaSaslClientFactory implements SaslClientFactory {
   private static final String[] SUPPORTED_MECHANISMS = new String[]{"MYSQLCJ-SCRAM-SHA-1", "MYSQLCJ-SCRAM-SHA-256"};

   public SaslClient createSaslClient(String[] mechanisms, String authorizationId, String protocol, String serverName, Map<String, ?> props, CallbackHandler cbh) throws SaslException {
      String[] var7 = mechanisms;
      int var8 = mechanisms.length;

      for(int var9 = 0; var9 < var8; ++var9) {
         String mech = var7[var9];
         if (mech.equals("MYSQLCJ-SCRAM-SHA-1")) {
            return new ScramSha1SaslClient(authorizationId, this.getUsername(mech, authorizationId, cbh), this.getPassword(mech, cbh));
         }

         if (mech.equals("MYSQLCJ-SCRAM-SHA-256")) {
            return new ScramSha256SaslClient(authorizationId, this.getUsername(mech, authorizationId, cbh), this.getPassword(mech, cbh));
         }
      }

      return null;
   }

   public String[] getMechanismNames(Map<String, ?> props) {
      return (String[])SUPPORTED_MECHANISMS.clone();
   }

   private String getUsername(String prefix, String authorizationId, CallbackHandler cbh) throws SaslException {
      if (cbh == null) {
         throw new SaslException("Callback handler required to get username.");
      } else {
         try {
            String prompt = prefix + " authentication id:";
            NameCallback ncb = StringUtils.isNullOrEmpty(authorizationId) ? new NameCallback(prompt) : new NameCallback(prompt, authorizationId);
            cbh.handle(new Callback[]{ncb});
            String userName = ncb.getName();
            return userName;
         } catch (UnsupportedCallbackException | IOException var7) {
            throw new SaslException("Cannot get username", var7);
         }
      }
   }

   private String getPassword(String prefix, CallbackHandler cbh) throws SaslException {
      if (cbh == null) {
         throw new SaslException("Callback handler required to get password.");
      } else {
         try {
            String prompt = prefix + " password:";
            PasswordCallback pcb = new PasswordCallback(prompt, false);
            cbh.handle(new Callback[]{pcb});
            String password = new String(pcb.getPassword());
            pcb.clearPassword();
            return password;
         } catch (UnsupportedCallbackException | IOException var6) {
            throw new SaslException("Cannot get password", var6);
         }
      }
   }
}
