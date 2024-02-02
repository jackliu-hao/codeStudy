package io.undertow.servlet.core;

import io.undertow.security.idm.Account;
import io.undertow.servlet.api.AuthorizationManager;
import io.undertow.servlet.api.Deployment;
import io.undertow.servlet.api.SecurityInfo;
import io.undertow.servlet.api.SecurityRoleRef;
import io.undertow.servlet.api.ServletInfo;
import io.undertow.servlet.api.SingleConstraintMatch;
import io.undertow.servlet.api.TransportGuaranteeType;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;

public class DefaultAuthorizationManager implements AuthorizationManager {
   public static final DefaultAuthorizationManager INSTANCE = new DefaultAuthorizationManager();

   private DefaultAuthorizationManager() {
   }

   public boolean isUserInRole(String role, Account account, ServletInfo servletInfo, HttpServletRequest request, Deployment deployment) {
      Map<String, Set<String>> principalVersusRolesMap = deployment.getDeploymentInfo().getPrincipalVersusRolesMap();
      Set<String> roles = (Set)principalVersusRolesMap.get(account.getPrincipal().getName());
      Iterator var8 = servletInfo.getSecurityRoleRefs().iterator();

      SecurityRoleRef ref;
      do {
         if (!var8.hasNext()) {
            if (roles != null && roles.contains(role)) {
               return true;
            }

            return account.getRoles().contains(role);
         }

         ref = (SecurityRoleRef)var8.next();
      } while(!ref.getRole().equals(role));

      if (roles != null && roles.contains(ref.getLinkedRole())) {
         return true;
      } else {
         return account.getRoles().contains(ref.getLinkedRole());
      }
   }

   public boolean canAccessResource(List<SingleConstraintMatch> constraints, Account account, ServletInfo servletInfo, HttpServletRequest request, Deployment deployment) {
      if (constraints != null && !constraints.isEmpty()) {
         Iterator var6 = constraints.iterator();

         boolean found;
         do {
            if (!var6.hasNext()) {
               return true;
            }

            SingleConstraintMatch constraint = (SingleConstraintMatch)var6.next();
            found = false;
            Set<String> roleSet = constraint.getRequiredRoles();
            if (roleSet.isEmpty() && constraint.getEmptyRoleSemantic() != SecurityInfo.EmptyRoleSemantic.DENY) {
               found = true;
            } else if (account != null) {
               if (roleSet.contains("**") && !deployment.getDeploymentInfo().getSecurityRoles().contains("**")) {
                  found = true;
               } else {
                  Set<String> roles = (Set)deployment.getDeploymentInfo().getPrincipalVersusRolesMap().get(account.getPrincipal().getName());
                  Iterator var11 = roleSet.iterator();

                  while(var11.hasNext()) {
                     String role = (String)var11.next();
                     if (roles != null && roles.contains(role)) {
                        found = true;
                        break;
                     }

                     if (account.getRoles().contains(role)) {
                        found = true;
                        break;
                     }
                  }
               }
            }
         } while(found);

         return false;
      } else {
         return true;
      }
   }

   public TransportGuaranteeType transportGuarantee(TransportGuaranteeType currentConnectionGuarantee, TransportGuaranteeType configuredRequiredGuarentee, HttpServletRequest request) {
      return configuredRequiredGuarentee;
   }
}
