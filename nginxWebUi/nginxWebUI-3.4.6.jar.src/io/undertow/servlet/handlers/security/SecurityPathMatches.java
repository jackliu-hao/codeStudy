/*     */ package io.undertow.servlet.handlers.security;
/*     */ 
/*     */ import io.undertow.servlet.UndertowServletLogger;
/*     */ import io.undertow.servlet.api.DeploymentInfo;
/*     */ import io.undertow.servlet.api.SecurityConstraint;
/*     */ import io.undertow.servlet.api.SecurityInfo;
/*     */ import io.undertow.servlet.api.SingleConstraintMatch;
/*     */ import io.undertow.servlet.api.TransportGuaranteeType;
/*     */ import io.undertow.servlet.api.WebResourceCollection;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.HashMap;
/*     */ import java.util.HashSet;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class SecurityPathMatches
/*     */ {
/*     */   private static Set<String> KNOWN_METHODS;
/*     */   private final boolean denyUncoveredHttpMethods;
/*     */   private final PathSecurityInformation defaultPathSecurityInformation;
/*     */   private final Map<String, PathSecurityInformation> exactPathRoleInformation;
/*     */   private final Map<String, PathSecurityInformation> prefixPathRoleInformation;
/*     */   private final Map<String, PathSecurityInformation> extensionRoleInformation;
/*     */   
/*     */   static {
/*  47 */     Set<String> methods = new HashSet<>();
/*  48 */     methods.add("GET");
/*  49 */     methods.add("POST");
/*  50 */     methods.add("PUT");
/*  51 */     methods.add("DELETE");
/*  52 */     methods.add("OPTIONS");
/*  53 */     methods.add("HEAD");
/*  54 */     methods.add("TRACE");
/*  55 */     methods.add("CONNECT");
/*  56 */     KNOWN_METHODS = Collections.unmodifiableSet(methods);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private SecurityPathMatches(boolean denyUncoveredHttpMethods, PathSecurityInformation defaultPathSecurityInformation, Map<String, PathSecurityInformation> exactPathRoleInformation, Map<String, PathSecurityInformation> prefixPathRoleInformation, Map<String, PathSecurityInformation> extensionRoleInformation) {
/*  67 */     this.denyUncoveredHttpMethods = denyUncoveredHttpMethods;
/*  68 */     this.defaultPathSecurityInformation = defaultPathSecurityInformation;
/*  69 */     this.exactPathRoleInformation = exactPathRoleInformation;
/*  70 */     this.prefixPathRoleInformation = prefixPathRoleInformation;
/*  71 */     this.extensionRoleInformation = extensionRoleInformation;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isEmpty() {
/*  78 */     return (this.defaultPathSecurityInformation.excludedMethodRoles.isEmpty() && this.defaultPathSecurityInformation.perMethodRequiredRoles
/*  79 */       .isEmpty() && this.defaultPathSecurityInformation.defaultRequiredRoles
/*  80 */       .isEmpty() && this.exactPathRoleInformation
/*  81 */       .isEmpty() && this.prefixPathRoleInformation
/*  82 */       .isEmpty() && this.extensionRoleInformation
/*  83 */       .isEmpty());
/*     */   }
/*     */   
/*     */   public SecurityPathMatch getSecurityInfo(String path, String method) {
/*  87 */     RuntimeMatch currentMatch = new RuntimeMatch();
/*  88 */     handleMatch(method, this.defaultPathSecurityInformation, currentMatch);
/*  89 */     PathSecurityInformation match = this.exactPathRoleInformation.get(path);
/*  90 */     PathSecurityInformation extensionMatch = null;
/*  91 */     if (match != null) {
/*  92 */       handleMatch(method, match, currentMatch);
/*  93 */       return new SecurityPathMatch(currentMatch.type, mergeConstraints(currentMatch));
/*     */     } 
/*     */     
/*  96 */     match = this.prefixPathRoleInformation.get(path);
/*  97 */     if (match != null) {
/*  98 */       handleMatch(method, match, currentMatch);
/*  99 */       return new SecurityPathMatch(currentMatch.type, mergeConstraints(currentMatch));
/*     */     } 
/*     */     
/* 102 */     int qsPos = -1;
/* 103 */     boolean extension = false;
/* 104 */     for (int i = path.length() - 1; i >= 0; i--) {
/* 105 */       char c = path.charAt(i);
/* 106 */       if (c == '?') {
/*     */         
/* 108 */         String part = path.substring(0, i);
/* 109 */         match = this.exactPathRoleInformation.get(part);
/* 110 */         if (match != null) {
/* 111 */           handleMatch(method, match, currentMatch);
/* 112 */           return new SecurityPathMatch(currentMatch.type, mergeConstraints(currentMatch));
/*     */         } 
/* 114 */         qsPos = i;
/* 115 */         extension = false;
/* 116 */       } else if (c == '/') {
/* 117 */         extension = true;
/* 118 */         String part = path.substring(0, i);
/* 119 */         match = this.prefixPathRoleInformation.get(part);
/* 120 */         if (match != null) {
/* 121 */           handleMatch(method, match, currentMatch);
/* 122 */           return new SecurityPathMatch(currentMatch.type, mergeConstraints(currentMatch));
/*     */         } 
/* 124 */       } else if (c == '.' && 
/* 125 */         !extension) {
/* 126 */         String ext; extension = true;
/*     */         
/* 128 */         if (qsPos == -1) {
/* 129 */           ext = path.substring(i + 1, path.length());
/*     */         } else {
/* 131 */           ext = path.substring(i + 1, qsPos);
/*     */         } 
/* 133 */         extensionMatch = this.extensionRoleInformation.get(ext);
/*     */       } 
/*     */     } 
/*     */ 
/*     */     
/* 138 */     if (extensionMatch != null) {
/* 139 */       handleMatch(method, extensionMatch, currentMatch);
/* 140 */       return new SecurityPathMatch(currentMatch.type, mergeConstraints(currentMatch));
/*     */     } 
/* 142 */     return new SecurityPathMatch(currentMatch.type, mergeConstraints(currentMatch));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private SingleConstraintMatch mergeConstraints(RuntimeMatch currentMatch) {
/* 149 */     if (currentMatch.uncovered && this.denyUncoveredHttpMethods) {
/* 150 */       return new SingleConstraintMatch(SecurityInfo.EmptyRoleSemantic.DENY, Collections.emptySet());
/*     */     }
/* 152 */     Set<String> allowedRoles = new HashSet<>();
/* 153 */     for (SingleConstraintMatch match : currentMatch.constraints) {
/* 154 */       if (match.getRequiredRoles().isEmpty()) {
/* 155 */         return new SingleConstraintMatch(match.getEmptyRoleSemantic(), Collections.emptySet());
/*     */       }
/* 157 */       allowedRoles.addAll(match.getRequiredRoles());
/*     */     } 
/*     */     
/* 160 */     return new SingleConstraintMatch(SecurityInfo.EmptyRoleSemantic.PERMIT, allowedRoles);
/*     */   }
/*     */   
/*     */   private void handleMatch(String method, PathSecurityInformation exact, RuntimeMatch currentMatch) {
/* 164 */     List<SecurityInformation> roles = exact.defaultRequiredRoles;
/* 165 */     for (SecurityInformation role : roles) {
/* 166 */       transport(currentMatch, role.transportGuaranteeType);
/* 167 */       currentMatch.constraints.add(new SingleConstraintMatch(role.emptyRoleSemantic, role.roles));
/* 168 */       if (role.emptyRoleSemantic == SecurityInfo.EmptyRoleSemantic.DENY || !role.roles.isEmpty()) {
/* 169 */         currentMatch.uncovered = false;
/*     */       }
/*     */     } 
/* 172 */     List<SecurityInformation> methodInfo = exact.perMethodRequiredRoles.get(method);
/* 173 */     if (methodInfo != null) {
/* 174 */       currentMatch.uncovered = false;
/* 175 */       for (SecurityInformation role : methodInfo) {
/* 176 */         transport(currentMatch, role.transportGuaranteeType);
/* 177 */         currentMatch.constraints.add(new SingleConstraintMatch(role.emptyRoleSemantic, role.roles));
/*     */       } 
/*     */     } 
/* 180 */     for (ExcludedMethodRoles excluded : exact.excludedMethodRoles) {
/* 181 */       if (!excluded.methods.contains(method)) {
/* 182 */         currentMatch.uncovered = false;
/* 183 */         transport(currentMatch, excluded.securityInformation.transportGuaranteeType);
/* 184 */         currentMatch.constraints.add(new SingleConstraintMatch(excluded.securityInformation.emptyRoleSemantic, excluded.securityInformation.roles));
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   private void transport(RuntimeMatch match, TransportGuaranteeType other) {
/* 190 */     if (other.ordinal() > match.type.ordinal()) {
/* 191 */       match.type = other;
/*     */     }
/*     */   }
/*     */   
/*     */   public void logWarningsAboutUncoveredMethods() {
/* 196 */     if (!this.denyUncoveredHttpMethods) {
/* 197 */       logWarningsAboutUncoveredMethods(this.exactPathRoleInformation, "", "");
/* 198 */       logWarningsAboutUncoveredMethods(this.prefixPathRoleInformation, "", "/*");
/* 199 */       logWarningsAboutUncoveredMethods(this.extensionRoleInformation, "*.", "");
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   private void logWarningsAboutUncoveredMethods(Map<String, PathSecurityInformation> matches, String prefix, String suffix) {
/* 205 */     for (Map.Entry<String, PathSecurityInformation> entry : matches.entrySet()) {
/* 206 */       if (((PathSecurityInformation)entry.getValue()).perMethodRequiredRoles.isEmpty() && ((PathSecurityInformation)entry.getValue()).excludedMethodRoles.isEmpty()) {
/*     */         continue;
/*     */       }
/* 209 */       Set<String> missing = new HashSet<>(KNOWN_METHODS);
/* 210 */       for (String m : ((PathSecurityInformation)entry.getValue()).perMethodRequiredRoles.keySet()) {
/* 211 */         missing.remove(m);
/*     */       }
/* 213 */       Iterator<String> it = missing.iterator();
/* 214 */       while (it.hasNext()) {
/* 215 */         String val = it.next();
/* 216 */         for (ExcludedMethodRoles excluded : ((PathSecurityInformation)entry.getValue()).excludedMethodRoles) {
/* 217 */           if (!excluded.methods.contains(val)) {
/* 218 */             it.remove();
/*     */           }
/*     */         } 
/*     */       } 
/*     */       
/* 223 */       if (!missing.isEmpty()) {
/* 224 */         UndertowServletLogger.ROOT_LOGGER.unsecuredMethodsOnPath(prefix + (String)entry.getKey() + suffix, missing);
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public static Builder builder(DeploymentInfo deploymentInfo) {
/* 231 */     return new Builder(deploymentInfo);
/*     */   }
/*     */   
/*     */   public static class Builder {
/*     */     private final DeploymentInfo deploymentInfo;
/* 236 */     private final SecurityPathMatches.PathSecurityInformation defaultPathSecurityInformation = new SecurityPathMatches.PathSecurityInformation();
/* 237 */     private final Map<String, SecurityPathMatches.PathSecurityInformation> exactPathRoleInformation = new HashMap<>();
/* 238 */     private final Map<String, SecurityPathMatches.PathSecurityInformation> prefixPathRoleInformation = new HashMap<>();
/* 239 */     private final Map<String, SecurityPathMatches.PathSecurityInformation> extensionRoleInformation = new HashMap<>();
/*     */     
/*     */     private Builder(DeploymentInfo deploymentInfo) {
/* 242 */       this.deploymentInfo = deploymentInfo;
/*     */     }
/*     */     
/*     */     public void addSecurityConstraint(SecurityConstraint securityConstraint) {
/* 246 */       Set<String> roles = expandRolesAllowed(securityConstraint.getRolesAllowed());
/* 247 */       SecurityPathMatches.SecurityInformation securityInformation = new SecurityPathMatches.SecurityInformation(roles, securityConstraint.getTransportGuaranteeType(), securityConstraint.getEmptyRoleSemantic());
/* 248 */       for (WebResourceCollection webResources : securityConstraint.getWebResourceCollections()) {
/* 249 */         if (webResources.getUrlPatterns().isEmpty())
/*     */         {
/* 251 */           setupPathSecurityInformation(this.defaultPathSecurityInformation, securityInformation, webResources);
/*     */         }
/* 253 */         for (String pattern : webResources.getUrlPatterns()) {
/* 254 */           if (pattern.endsWith("/*")) {
/* 255 */             String part = pattern.substring(0, pattern.length() - 2);
/* 256 */             SecurityPathMatches.PathSecurityInformation pathSecurityInformation = this.prefixPathRoleInformation.get(part);
/* 257 */             if (pathSecurityInformation == null) {
/* 258 */               this.prefixPathRoleInformation.put(part, pathSecurityInformation = new SecurityPathMatches.PathSecurityInformation());
/*     */             }
/* 260 */             setupPathSecurityInformation(pathSecurityInformation, securityInformation, webResources); continue;
/* 261 */           }  if (pattern.startsWith("*.")) {
/* 262 */             String part = pattern.substring(2, pattern.length());
/* 263 */             SecurityPathMatches.PathSecurityInformation pathSecurityInformation = this.extensionRoleInformation.get(part);
/* 264 */             if (pathSecurityInformation == null) {
/* 265 */               this.extensionRoleInformation.put(part, pathSecurityInformation = new SecurityPathMatches.PathSecurityInformation());
/*     */             }
/* 267 */             setupPathSecurityInformation(pathSecurityInformation, securityInformation, webResources); continue;
/*     */           } 
/* 269 */           SecurityPathMatches.PathSecurityInformation info = this.exactPathRoleInformation.get(pattern);
/* 270 */           if (info == null) {
/* 271 */             this.exactPathRoleInformation.put(pattern, info = new SecurityPathMatches.PathSecurityInformation());
/*     */           }
/* 273 */           setupPathSecurityInformation(info, securityInformation, webResources);
/*     */         } 
/*     */       } 
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     private Set<String> expandRolesAllowed(Set<String> rolesAllowed) {
/* 281 */       Set<String> roles = new HashSet<>(rolesAllowed);
/* 282 */       if (roles.contains("*")) {
/* 283 */         roles.remove("*");
/* 284 */         roles.addAll(this.deploymentInfo.getSecurityRoles());
/*     */       } 
/*     */       
/* 287 */       return roles;
/*     */     }
/*     */     
/*     */     private void setupPathSecurityInformation(SecurityPathMatches.PathSecurityInformation info, SecurityPathMatches.SecurityInformation securityConstraint, WebResourceCollection webResources) {
/* 291 */       if (webResources.getHttpMethods().isEmpty() && webResources
/* 292 */         .getHttpMethodOmissions().isEmpty()) {
/* 293 */         info.defaultRequiredRoles.add(securityConstraint);
/* 294 */       } else if (!webResources.getHttpMethods().isEmpty()) {
/* 295 */         for (String method : webResources.getHttpMethods()) {
/* 296 */           List<SecurityPathMatches.SecurityInformation> securityInformations = info.perMethodRequiredRoles.get(method);
/* 297 */           if (securityInformations == null) {
/* 298 */             info.perMethodRequiredRoles.put(method, securityInformations = new ArrayList<>());
/*     */           }
/* 300 */           securityInformations.add(securityConstraint);
/*     */         } 
/* 302 */       } else if (!webResources.getHttpMethodOmissions().isEmpty()) {
/* 303 */         info.excludedMethodRoles.add(new SecurityPathMatches.ExcludedMethodRoles(webResources.getHttpMethodOmissions(), securityConstraint));
/*     */       } 
/*     */     }
/*     */     
/*     */     public SecurityPathMatches build() {
/* 308 */       return new SecurityPathMatches(this.deploymentInfo.isDenyUncoveredHttpMethods(), this.defaultPathSecurityInformation, this.exactPathRoleInformation, this.prefixPathRoleInformation, this.extensionRoleInformation);
/*     */     } }
/*     */   
/*     */   private static class PathSecurityInformation {
/*     */     private PathSecurityInformation() {}
/*     */     
/* 314 */     final List<SecurityPathMatches.SecurityInformation> defaultRequiredRoles = new ArrayList<>();
/* 315 */     final Map<String, List<SecurityPathMatches.SecurityInformation>> perMethodRequiredRoles = new HashMap<>();
/* 316 */     final List<SecurityPathMatches.ExcludedMethodRoles> excludedMethodRoles = new ArrayList<>();
/*     */   }
/*     */   
/*     */   private static final class ExcludedMethodRoles {
/*     */     final Set<String> methods;
/*     */     final SecurityPathMatches.SecurityInformation securityInformation;
/*     */     
/*     */     ExcludedMethodRoles(Set<String> methods, SecurityPathMatches.SecurityInformation securityInformation) {
/* 324 */       this.methods = methods;
/* 325 */       this.securityInformation = securityInformation;
/*     */     }
/*     */   }
/*     */   
/*     */   private static final class SecurityInformation {
/*     */     final Set<String> roles;
/*     */     final TransportGuaranteeType transportGuaranteeType;
/*     */     final SecurityInfo.EmptyRoleSemantic emptyRoleSemantic;
/*     */     
/*     */     private SecurityInformation(Set<String> roles, TransportGuaranteeType transportGuaranteeType, SecurityInfo.EmptyRoleSemantic emptyRoleSemantic) {
/* 335 */       this.emptyRoleSemantic = emptyRoleSemantic;
/* 336 */       this.roles = new HashSet<>(roles);
/* 337 */       this.transportGuaranteeType = transportGuaranteeType;
/*     */     } }
/*     */   
/*     */   private static final class RuntimeMatch { private RuntimeMatch() {}
/*     */     
/* 342 */     TransportGuaranteeType type = TransportGuaranteeType.NONE;
/* 343 */     final List<SingleConstraintMatch> constraints = new ArrayList<>();
/*     */     boolean uncovered = true; }
/*     */ 
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\servlet\handlers\security\SecurityPathMatches.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */