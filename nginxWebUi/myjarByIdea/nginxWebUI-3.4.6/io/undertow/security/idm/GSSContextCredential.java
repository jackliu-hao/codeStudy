package io.undertow.security.idm;

import org.ietf.jgss.GSSContext;

public class GSSContextCredential implements Credential {
   private final GSSContext gssContext;

   public GSSContextCredential(GSSContext gssContext) {
      this.gssContext = gssContext;
   }

   public GSSContext getGssContext() {
      return this.gssContext;
   }
}
