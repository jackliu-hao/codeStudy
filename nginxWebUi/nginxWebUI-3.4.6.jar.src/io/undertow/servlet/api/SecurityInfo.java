/*     */ package io.undertow.servlet.api;
/*     */ 
/*     */ import java.util.Arrays;
/*     */ import java.util.Collection;
/*     */ import java.util.HashSet;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class SecurityInfo<T extends SecurityInfo>
/*     */   implements Cloneable
/*     */ {
/*     */   public enum EmptyRoleSemantic
/*     */   {
/*  39 */     PERMIT,
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  44 */     DENY,
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  49 */     AUTHENTICATE;
/*     */   }
/*     */ 
/*     */   
/*  53 */   private volatile EmptyRoleSemantic emptyRoleSemantic = EmptyRoleSemantic.DENY;
/*  54 */   private final Set<String> rolesAllowed = new HashSet<>();
/*  55 */   private volatile TransportGuaranteeType transportGuaranteeType = TransportGuaranteeType.NONE;
/*     */   
/*     */   public EmptyRoleSemantic getEmptyRoleSemantic() {
/*  58 */     return this.emptyRoleSemantic;
/*     */   }
/*     */   
/*     */   public T setEmptyRoleSemantic(EmptyRoleSemantic emptyRoleSemantic) {
/*  62 */     this.emptyRoleSemantic = emptyRoleSemantic;
/*  63 */     return (T)this;
/*     */   }
/*     */   
/*     */   public TransportGuaranteeType getTransportGuaranteeType() {
/*  67 */     return this.transportGuaranteeType;
/*     */   }
/*     */   
/*     */   public T setTransportGuaranteeType(TransportGuaranteeType transportGuaranteeType) {
/*  71 */     this.transportGuaranteeType = transportGuaranteeType;
/*  72 */     return (T)this;
/*     */   }
/*     */   
/*     */   public T addRoleAllowed(String role) {
/*  76 */     this.rolesAllowed.add(role);
/*  77 */     return (T)this;
/*     */   }
/*     */   
/*     */   public T addRolesAllowed(String... roles) {
/*  81 */     this.rolesAllowed.addAll(Arrays.asList(roles));
/*  82 */     return (T)this;
/*     */   }
/*     */   public T addRolesAllowed(Collection<String> roles) {
/*  85 */     this.rolesAllowed.addAll(roles);
/*  86 */     return (T)this;
/*     */   }
/*     */   public Set<String> getRolesAllowed() {
/*  89 */     return new HashSet<>(this.rolesAllowed);
/*     */   }
/*     */ 
/*     */   
/*     */   public T clone() {
/*  94 */     SecurityInfo info = (SecurityInfo)createInstance();
/*  95 */     info.emptyRoleSemantic = this.emptyRoleSemantic;
/*  96 */     info.transportGuaranteeType = this.transportGuaranteeType;
/*  97 */     info.rolesAllowed.addAll(this.rolesAllowed);
/*  98 */     return (T)info;
/*     */   }
/*     */   
/*     */   protected T createInstance() {
/* 102 */     return (T)new SecurityInfo();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\servlet\api\SecurityInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */