package com.mysql.cj.sasl;

import java.security.AccessController;
import java.security.InvalidParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import java.security.ProviderException;
import java.util.List;
import java.util.Map;

public final class ScramShaSaslProvider extends Provider {
   private static final long serialVersionUID = 866717063477857937L;
   private static final String INFO = "MySQL Connector/J SASL provider (implements client mechanisms for MYSQLCJ-SCRAM-SHA-1 and MYSQLCJ-SCRAM-SHA-256)";

   public ScramShaSaslProvider() {
      super("MySQLScramShaSasl", 1.0, "MySQL Connector/J SASL provider (implements client mechanisms for MYSQLCJ-SCRAM-SHA-1 and MYSQLCJ-SCRAM-SHA-256)");
      AccessController.doPrivileged(() -> {
         this.putService(new ProviderService(this, "SaslClientFactory", "MYSQLCJ-SCRAM-SHA-1", ScramShaSaslClientFactory.class.getName()));
         this.putService(new ProviderService(this, "SaslClientFactory", "MYSQLCJ-SCRAM-SHA-256", ScramShaSaslClientFactory.class.getName()));
         return null;
      });
   }

   private static final class ProviderService extends Provider.Service {
      public ProviderService(Provider provider, String type, String algorithm, String className) {
         super(provider, type, algorithm, className, (List)null, (Map)null);
      }

      public Object newInstance(Object constructorParameter) throws NoSuchAlgorithmException {
         String type = this.getType();
         if (constructorParameter != null) {
            throw new InvalidParameterException("constructorParameter not used with " + type + " engines");
         } else {
            String algorithm = this.getAlgorithm();
            if (type.equals("SaslClientFactory")) {
               if (algorithm.equals("MYSQLCJ-SCRAM-SHA-1")) {
                  return new ScramShaSaslClientFactory();
               }

               if (algorithm.equals("MYSQLCJ-SCRAM-SHA-256")) {
                  return new ScramShaSaslClientFactory();
               }
            }

            throw new ProviderException("No implementation for " + algorithm + " " + type);
         }
      }
   }
}
