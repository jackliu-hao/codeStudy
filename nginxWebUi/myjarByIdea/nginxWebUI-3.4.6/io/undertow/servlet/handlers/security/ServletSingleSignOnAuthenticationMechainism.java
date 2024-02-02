package io.undertow.servlet.handlers.security;

import io.undertow.security.impl.SingleSignOnManager;

/** @deprecated */
@Deprecated
public class ServletSingleSignOnAuthenticationMechainism extends ServletSingleSignOnAuthenticationMechanism {
   public ServletSingleSignOnAuthenticationMechainism(SingleSignOnManager storage) {
      super(storage);
   }
}
