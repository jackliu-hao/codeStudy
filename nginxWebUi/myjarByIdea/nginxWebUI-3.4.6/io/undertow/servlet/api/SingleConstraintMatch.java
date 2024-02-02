package io.undertow.servlet.api;

import java.util.Set;

public class SingleConstraintMatch {
   private final SecurityInfo.EmptyRoleSemantic emptyRoleSemantic;
   private final Set<String> requiredRoles;

   public SingleConstraintMatch(SecurityInfo.EmptyRoleSemantic emptyRoleSemantic, Set<String> requiredRoles) {
      this.emptyRoleSemantic = emptyRoleSemantic;
      this.requiredRoles = requiredRoles;
   }

   public SecurityInfo.EmptyRoleSemantic getEmptyRoleSemantic() {
      return this.emptyRoleSemantic;
   }

   public Set<String> getRequiredRoles() {
      return this.requiredRoles;
   }

   public String toString() {
      return "SingleConstraintMatch{emptyRoleSemantic=" + this.emptyRoleSemantic + ", requiredRoles=" + this.requiredRoles + '}';
   }
}
