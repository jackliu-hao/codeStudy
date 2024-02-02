package io.undertow.servlet.handlers.security;

import io.undertow.servlet.UndertowServletLogger;
import io.undertow.servlet.api.DeploymentInfo;
import io.undertow.servlet.api.SecurityConstraint;
import io.undertow.servlet.api.SecurityInfo;
import io.undertow.servlet.api.SingleConstraintMatch;
import io.undertow.servlet.api.TransportGuaranteeType;
import io.undertow.servlet.api.WebResourceCollection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SecurityPathMatches {
   private static Set<String> KNOWN_METHODS;
   private final boolean denyUncoveredHttpMethods;
   private final PathSecurityInformation defaultPathSecurityInformation;
   private final Map<String, PathSecurityInformation> exactPathRoleInformation;
   private final Map<String, PathSecurityInformation> prefixPathRoleInformation;
   private final Map<String, PathSecurityInformation> extensionRoleInformation;

   private SecurityPathMatches(boolean denyUncoveredHttpMethods, PathSecurityInformation defaultPathSecurityInformation, Map<String, PathSecurityInformation> exactPathRoleInformation, Map<String, PathSecurityInformation> prefixPathRoleInformation, Map<String, PathSecurityInformation> extensionRoleInformation) {
      this.denyUncoveredHttpMethods = denyUncoveredHttpMethods;
      this.defaultPathSecurityInformation = defaultPathSecurityInformation;
      this.exactPathRoleInformation = exactPathRoleInformation;
      this.prefixPathRoleInformation = prefixPathRoleInformation;
      this.extensionRoleInformation = extensionRoleInformation;
   }

   public boolean isEmpty() {
      return this.defaultPathSecurityInformation.excludedMethodRoles.isEmpty() && this.defaultPathSecurityInformation.perMethodRequiredRoles.isEmpty() && this.defaultPathSecurityInformation.defaultRequiredRoles.isEmpty() && this.exactPathRoleInformation.isEmpty() && this.prefixPathRoleInformation.isEmpty() && this.extensionRoleInformation.isEmpty();
   }

   public SecurityPathMatch getSecurityInfo(String path, String method) {
      RuntimeMatch currentMatch = new RuntimeMatch();
      this.handleMatch(method, this.defaultPathSecurityInformation, currentMatch);
      PathSecurityInformation match = (PathSecurityInformation)this.exactPathRoleInformation.get(path);
      PathSecurityInformation extensionMatch = null;
      if (match != null) {
         this.handleMatch(method, match, currentMatch);
         return new SecurityPathMatch(currentMatch.type, this.mergeConstraints(currentMatch));
      } else {
         match = (PathSecurityInformation)this.prefixPathRoleInformation.get(path);
         if (match != null) {
            this.handleMatch(method, match, currentMatch);
            return new SecurityPathMatch(currentMatch.type, this.mergeConstraints(currentMatch));
         } else {
            int qsPos = -1;
            boolean extension = false;

            for(int i = path.length() - 1; i >= 0; --i) {
               char c = path.charAt(i);
               String ext;
               if (c == '?') {
                  ext = path.substring(0, i);
                  match = (PathSecurityInformation)this.exactPathRoleInformation.get(ext);
                  if (match != null) {
                     this.handleMatch(method, match, currentMatch);
                     return new SecurityPathMatch(currentMatch.type, this.mergeConstraints(currentMatch));
                  }

                  qsPos = i;
                  extension = false;
               } else if (c == '/') {
                  extension = true;
                  ext = path.substring(0, i);
                  match = (PathSecurityInformation)this.prefixPathRoleInformation.get(ext);
                  if (match != null) {
                     this.handleMatch(method, match, currentMatch);
                     return new SecurityPathMatch(currentMatch.type, this.mergeConstraints(currentMatch));
                  }
               } else if (c == '.' && !extension) {
                  extension = true;
                  if (qsPos == -1) {
                     ext = path.substring(i + 1, path.length());
                  } else {
                     ext = path.substring(i + 1, qsPos);
                  }

                  extensionMatch = (PathSecurityInformation)this.extensionRoleInformation.get(ext);
               }
            }

            if (extensionMatch != null) {
               this.handleMatch(method, extensionMatch, currentMatch);
               return new SecurityPathMatch(currentMatch.type, this.mergeConstraints(currentMatch));
            } else {
               return new SecurityPathMatch(currentMatch.type, this.mergeConstraints(currentMatch));
            }
         }
      }
   }

   private SingleConstraintMatch mergeConstraints(RuntimeMatch currentMatch) {
      if (currentMatch.uncovered && this.denyUncoveredHttpMethods) {
         return new SingleConstraintMatch(SecurityInfo.EmptyRoleSemantic.DENY, Collections.emptySet());
      } else {
         Set<String> allowedRoles = new HashSet();
         Iterator var3 = currentMatch.constraints.iterator();

         while(var3.hasNext()) {
            SingleConstraintMatch match = (SingleConstraintMatch)var3.next();
            if (match.getRequiredRoles().isEmpty()) {
               return new SingleConstraintMatch(match.getEmptyRoleSemantic(), Collections.emptySet());
            }

            allowedRoles.addAll(match.getRequiredRoles());
         }

         return new SingleConstraintMatch(SecurityInfo.EmptyRoleSemantic.PERMIT, allowedRoles);
      }
   }

   private void handleMatch(String method, PathSecurityInformation exact, RuntimeMatch currentMatch) {
      List<SecurityInformation> roles = exact.defaultRequiredRoles;
      Iterator var5 = roles.iterator();

      while(true) {
         SecurityInformation role;
         do {
            if (!var5.hasNext()) {
               List<SecurityInformation> methodInfo = (List)exact.perMethodRequiredRoles.get(method);
               Iterator var9;
               if (methodInfo != null) {
                  currentMatch.uncovered = false;
                  var9 = methodInfo.iterator();

                  while(var9.hasNext()) {
                     SecurityInformation role = (SecurityInformation)var9.next();
                     this.transport(currentMatch, role.transportGuaranteeType);
                     currentMatch.constraints.add(new SingleConstraintMatch(role.emptyRoleSemantic, role.roles));
                  }
               }

               var9 = exact.excludedMethodRoles.iterator();

               while(var9.hasNext()) {
                  ExcludedMethodRoles excluded = (ExcludedMethodRoles)var9.next();
                  if (!excluded.methods.contains(method)) {
                     currentMatch.uncovered = false;
                     this.transport(currentMatch, excluded.securityInformation.transportGuaranteeType);
                     currentMatch.constraints.add(new SingleConstraintMatch(excluded.securityInformation.emptyRoleSemantic, excluded.securityInformation.roles));
                  }
               }

               return;
            }

            role = (SecurityInformation)var5.next();
            this.transport(currentMatch, role.transportGuaranteeType);
            currentMatch.constraints.add(new SingleConstraintMatch(role.emptyRoleSemantic, role.roles));
         } while(role.emptyRoleSemantic != SecurityInfo.EmptyRoleSemantic.DENY && role.roles.isEmpty());

         currentMatch.uncovered = false;
      }
   }

   private void transport(RuntimeMatch match, TransportGuaranteeType other) {
      if (other.ordinal() > match.type.ordinal()) {
         match.type = other;
      }

   }

   public void logWarningsAboutUncoveredMethods() {
      if (!this.denyUncoveredHttpMethods) {
         this.logWarningsAboutUncoveredMethods(this.exactPathRoleInformation, "", "");
         this.logWarningsAboutUncoveredMethods(this.prefixPathRoleInformation, "", "/*");
         this.logWarningsAboutUncoveredMethods(this.extensionRoleInformation, "*.", "");
      }

   }

   private void logWarningsAboutUncoveredMethods(Map<String, PathSecurityInformation> matches, String prefix, String suffix) {
      Iterator var4 = matches.entrySet().iterator();

      while(true) {
         Map.Entry entry;
         do {
            if (!var4.hasNext()) {
               return;
            }

            entry = (Map.Entry)var4.next();
         } while(((PathSecurityInformation)entry.getValue()).perMethodRequiredRoles.isEmpty() && ((PathSecurityInformation)entry.getValue()).excludedMethodRoles.isEmpty());

         Set<String> missing = new HashSet(KNOWN_METHODS);
         Iterator it = ((PathSecurityInformation)entry.getValue()).perMethodRequiredRoles.keySet().iterator();

         String val;
         while(it.hasNext()) {
            val = (String)it.next();
            missing.remove(val);
         }

         it = missing.iterator();

         while(true) {
            while(it.hasNext()) {
               val = (String)it.next();
               Iterator var9 = ((PathSecurityInformation)entry.getValue()).excludedMethodRoles.iterator();

               while(var9.hasNext()) {
                  ExcludedMethodRoles excluded = (ExcludedMethodRoles)var9.next();
                  if (!excluded.methods.contains(val)) {
                     it.remove();
                     break;
                  }
               }
            }

            if (!missing.isEmpty()) {
               UndertowServletLogger.ROOT_LOGGER.unsecuredMethodsOnPath(prefix + (String)entry.getKey() + suffix, missing);
            }
            break;
         }
      }
   }

   public static Builder builder(DeploymentInfo deploymentInfo) {
      return new Builder(deploymentInfo);
   }

   // $FF: synthetic method
   SecurityPathMatches(boolean x0, PathSecurityInformation x1, Map x2, Map x3, Map x4, Object x5) {
      this(x0, x1, x2, x3, x4);
   }

   static {
      Set<String> methods = new HashSet();
      methods.add("GET");
      methods.add("POST");
      methods.add("PUT");
      methods.add("DELETE");
      methods.add("OPTIONS");
      methods.add("HEAD");
      methods.add("TRACE");
      methods.add("CONNECT");
      KNOWN_METHODS = Collections.unmodifiableSet(methods);
   }

   private static final class RuntimeMatch {
      TransportGuaranteeType type;
      final List<SingleConstraintMatch> constraints;
      boolean uncovered;

      private RuntimeMatch() {
         this.type = TransportGuaranteeType.NONE;
         this.constraints = new ArrayList();
         this.uncovered = true;
      }

      // $FF: synthetic method
      RuntimeMatch(Object x0) {
         this();
      }
   }

   private static final class SecurityInformation {
      final Set<String> roles;
      final TransportGuaranteeType transportGuaranteeType;
      final SecurityInfo.EmptyRoleSemantic emptyRoleSemantic;

      private SecurityInformation(Set<String> roles, TransportGuaranteeType transportGuaranteeType, SecurityInfo.EmptyRoleSemantic emptyRoleSemantic) {
         this.emptyRoleSemantic = emptyRoleSemantic;
         this.roles = new HashSet(roles);
         this.transportGuaranteeType = transportGuaranteeType;
      }

      // $FF: synthetic method
      SecurityInformation(Set x0, TransportGuaranteeType x1, SecurityInfo.EmptyRoleSemantic x2, Object x3) {
         this(x0, x1, x2);
      }
   }

   private static final class ExcludedMethodRoles {
      final Set<String> methods;
      final SecurityInformation securityInformation;

      ExcludedMethodRoles(Set<String> methods, SecurityInformation securityInformation) {
         this.methods = methods;
         this.securityInformation = securityInformation;
      }
   }

   private static class PathSecurityInformation {
      final List<SecurityInformation> defaultRequiredRoles;
      final Map<String, List<SecurityInformation>> perMethodRequiredRoles;
      final List<ExcludedMethodRoles> excludedMethodRoles;

      private PathSecurityInformation() {
         this.defaultRequiredRoles = new ArrayList();
         this.perMethodRequiredRoles = new HashMap();
         this.excludedMethodRoles = new ArrayList();
      }

      // $FF: synthetic method
      PathSecurityInformation(Object x0) {
         this();
      }
   }

   public static class Builder {
      private final DeploymentInfo deploymentInfo;
      private final PathSecurityInformation defaultPathSecurityInformation;
      private final Map<String, PathSecurityInformation> exactPathRoleInformation;
      private final Map<String, PathSecurityInformation> prefixPathRoleInformation;
      private final Map<String, PathSecurityInformation> extensionRoleInformation;

      private Builder(DeploymentInfo deploymentInfo) {
         this.defaultPathSecurityInformation = new PathSecurityInformation();
         this.exactPathRoleInformation = new HashMap();
         this.prefixPathRoleInformation = new HashMap();
         this.extensionRoleInformation = new HashMap();
         this.deploymentInfo = deploymentInfo;
      }

      public void addSecurityConstraint(SecurityConstraint securityConstraint) {
         Set<String> roles = this.expandRolesAllowed(securityConstraint.getRolesAllowed());
         SecurityInformation securityInformation = new SecurityInformation(roles, securityConstraint.getTransportGuaranteeType(), securityConstraint.getEmptyRoleSemantic());
         Iterator var4 = securityConstraint.getWebResourceCollections().iterator();

         while(var4.hasNext()) {
            WebResourceCollection webResources = (WebResourceCollection)var4.next();
            if (webResources.getUrlPatterns().isEmpty()) {
               this.setupPathSecurityInformation(this.defaultPathSecurityInformation, securityInformation, webResources);
            }

            Iterator var6 = webResources.getUrlPatterns().iterator();

            while(var6.hasNext()) {
               String pattern = (String)var6.next();
               String part;
               PathSecurityInformation info;
               if (pattern.endsWith("/*")) {
                  part = pattern.substring(0, pattern.length() - 2);
                  info = (PathSecurityInformation)this.prefixPathRoleInformation.get(part);
                  if (info == null) {
                     this.prefixPathRoleInformation.put(part, info = new PathSecurityInformation());
                  }

                  this.setupPathSecurityInformation(info, securityInformation, webResources);
               } else if (pattern.startsWith("*.")) {
                  part = pattern.substring(2, pattern.length());
                  info = (PathSecurityInformation)this.extensionRoleInformation.get(part);
                  if (info == null) {
                     this.extensionRoleInformation.put(part, info = new PathSecurityInformation());
                  }

                  this.setupPathSecurityInformation(info, securityInformation, webResources);
               } else {
                  PathSecurityInformation info = (PathSecurityInformation)this.exactPathRoleInformation.get(pattern);
                  if (info == null) {
                     this.exactPathRoleInformation.put(pattern, info = new PathSecurityInformation());
                  }

                  this.setupPathSecurityInformation(info, securityInformation, webResources);
               }
            }
         }

      }

      private Set<String> expandRolesAllowed(Set<String> rolesAllowed) {
         Set<String> roles = new HashSet(rolesAllowed);
         if (roles.contains("*")) {
            roles.remove("*");
            roles.addAll(this.deploymentInfo.getSecurityRoles());
         }

         return roles;
      }

      private void setupPathSecurityInformation(PathSecurityInformation info, SecurityInformation securityConstraint, WebResourceCollection webResources) {
         if (webResources.getHttpMethods().isEmpty() && webResources.getHttpMethodOmissions().isEmpty()) {
            info.defaultRequiredRoles.add(securityConstraint);
         } else {
            Object securityInformations;
            if (!webResources.getHttpMethods().isEmpty()) {
               for(Iterator var4 = webResources.getHttpMethods().iterator(); var4.hasNext(); ((List)securityInformations).add(securityConstraint)) {
                  String method = (String)var4.next();
                  securityInformations = (List)info.perMethodRequiredRoles.get(method);
                  if (securityInformations == null) {
                     info.perMethodRequiredRoles.put(method, securityInformations = new ArrayList());
                  }
               }
            } else if (!webResources.getHttpMethodOmissions().isEmpty()) {
               info.excludedMethodRoles.add(new ExcludedMethodRoles(webResources.getHttpMethodOmissions(), securityConstraint));
            }
         }

      }

      public SecurityPathMatches build() {
         return new SecurityPathMatches(this.deploymentInfo.isDenyUncoveredHttpMethods(), this.defaultPathSecurityInformation, this.exactPathRoleInformation, this.prefixPathRoleInformation, this.extensionRoleInformation);
      }

      // $FF: synthetic method
      Builder(DeploymentInfo x0, Object x1) {
         this(x0);
      }
   }
}
