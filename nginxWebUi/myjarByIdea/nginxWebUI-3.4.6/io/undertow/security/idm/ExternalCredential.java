package io.undertow.security.idm;

import java.io.Serializable;

public class ExternalCredential implements Serializable, Credential {
   public static final ExternalCredential INSTANCE = new ExternalCredential();

   private ExternalCredential() {
   }
}
