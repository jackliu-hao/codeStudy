package io.undertow.servlet.api;

import io.undertow.servlet.UndertowServletMessages;

public class SecurityRoleRef {
   private final String role;
   private final String linkedRole;

   public SecurityRoleRef(String role, String linkedRole) {
      if (role == null) {
         throw UndertowServletMessages.MESSAGES.paramCannotBeNull("role");
      } else {
         this.role = role;
         this.linkedRole = linkedRole;
      }
   }

   public String getRole() {
      return this.role;
   }

   public String getLinkedRole() {
      return this.linkedRole;
   }
}
