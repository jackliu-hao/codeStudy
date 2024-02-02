package io.undertow.servlet.api;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class SecurityInfo<T extends SecurityInfo> implements Cloneable {
   private volatile EmptyRoleSemantic emptyRoleSemantic;
   private final Set<String> rolesAllowed;
   private volatile TransportGuaranteeType transportGuaranteeType;

   public SecurityInfo() {
      this.emptyRoleSemantic = SecurityInfo.EmptyRoleSemantic.DENY;
      this.rolesAllowed = new HashSet();
      this.transportGuaranteeType = TransportGuaranteeType.NONE;
   }

   public EmptyRoleSemantic getEmptyRoleSemantic() {
      return this.emptyRoleSemantic;
   }

   public T setEmptyRoleSemantic(EmptyRoleSemantic emptyRoleSemantic) {
      this.emptyRoleSemantic = emptyRoleSemantic;
      return this;
   }

   public TransportGuaranteeType getTransportGuaranteeType() {
      return this.transportGuaranteeType;
   }

   public T setTransportGuaranteeType(TransportGuaranteeType transportGuaranteeType) {
      this.transportGuaranteeType = transportGuaranteeType;
      return this;
   }

   public T addRoleAllowed(String role) {
      this.rolesAllowed.add(role);
      return this;
   }

   public T addRolesAllowed(String... roles) {
      this.rolesAllowed.addAll(Arrays.asList(roles));
      return this;
   }

   public T addRolesAllowed(Collection<String> roles) {
      this.rolesAllowed.addAll(roles);
      return this;
   }

   public Set<String> getRolesAllowed() {
      return new HashSet(this.rolesAllowed);
   }

   public T clone() {
      SecurityInfo info = this.createInstance();
      info.emptyRoleSemantic = this.emptyRoleSemantic;
      info.transportGuaranteeType = this.transportGuaranteeType;
      info.rolesAllowed.addAll(this.rolesAllowed);
      return info;
   }

   protected T createInstance() {
      return new SecurityInfo();
   }

   public static enum EmptyRoleSemantic {
      PERMIT,
      DENY,
      AUTHENTICATE;
   }
}
