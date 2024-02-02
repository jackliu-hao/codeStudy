package io.undertow.servlet.handlers.security;

import io.undertow.UndertowLogger;
import io.undertow.security.handlers.AuthenticationConstraintHandler;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.servlet.api.SecurityInfo;
import io.undertow.servlet.api.SingleConstraintMatch;
import io.undertow.servlet.handlers.ServletRequestContext;
import java.util.Iterator;
import java.util.List;

public class ServletAuthenticationConstraintHandler extends AuthenticationConstraintHandler {
   public ServletAuthenticationConstraintHandler(HttpHandler next) {
      super(next);
   }

   protected boolean isAuthenticationRequired(HttpServerExchange exchange) {
      if (exchange.getRelativePath().endsWith("/j_security_check")) {
         return true;
      } else {
         List<SingleConstraintMatch> constraints = ((ServletRequestContext)exchange.getAttachment(ServletRequestContext.ATTACHMENT_KEY)).getRequiredConstrains();
         boolean authenticationRequired = false;
         Iterator var4 = constraints.iterator();

         while(var4.hasNext()) {
            SingleConstraintMatch constraint = (SingleConstraintMatch)var4.next();
            if (constraint.getRequiredRoles().isEmpty()) {
               if (constraint.getEmptyRoleSemantic() == SecurityInfo.EmptyRoleSemantic.DENY) {
                  return false;
               }

               if (constraint.getEmptyRoleSemantic() == SecurityInfo.EmptyRoleSemantic.AUTHENTICATE) {
                  authenticationRequired = true;
               }
            } else {
               authenticationRequired = true;
            }
         }

         if (authenticationRequired) {
            UndertowLogger.SECURITY_LOGGER.debugf("Authenticating required for request %s", exchange);
         }

         return authenticationRequired;
      }
   }
}
