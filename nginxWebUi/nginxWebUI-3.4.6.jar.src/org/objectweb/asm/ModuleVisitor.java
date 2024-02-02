/*     */ package org.objectweb.asm;
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
/*     */ public abstract class ModuleVisitor
/*     */ {
/*     */   protected final int api;
/*     */   protected ModuleVisitor mv;
/*     */   
/*     */   public ModuleVisitor(int api) {
/*  57 */     this(api, null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ModuleVisitor(int api, ModuleVisitor moduleVisitor) {
/*  69 */     if (api != 589824 && api != 524288 && api != 458752 && api != 393216 && api != 327680 && api != 262144 && api != 17432576)
/*     */     {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/*  76 */       throw new IllegalArgumentException("Unsupported api " + api);
/*     */     }
/*  78 */     if (api == 17432576) {
/*  79 */       Constants.checkAsmExperimental(this);
/*     */     }
/*  81 */     this.api = api;
/*  82 */     this.mv = moduleVisitor;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void visitMainClass(String mainClass) {
/*  91 */     if (this.mv != null) {
/*  92 */       this.mv.visitMainClass(mainClass);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void visitPackage(String packaze) {
/* 102 */     if (this.mv != null) {
/* 103 */       this.mv.visitPackage(packaze);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void visitRequire(String module, int access, String version) {
/* 116 */     if (this.mv != null) {
/* 117 */       this.mv.visitRequire(module, access, version);
/*     */     }
/*     */   }
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
/*     */   public void visitExport(String packaze, int access, String... modules) {
/* 131 */     if (this.mv != null) {
/* 132 */       this.mv.visitExport(packaze, access, modules);
/*     */     }
/*     */   }
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
/*     */   public void visitOpen(String packaze, int access, String... modules) {
/* 146 */     if (this.mv != null) {
/* 147 */       this.mv.visitOpen(packaze, access, modules);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void visitUse(String service) {
/* 158 */     if (this.mv != null) {
/* 159 */       this.mv.visitUse(service);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void visitProvide(String service, String... providers) {
/* 171 */     if (this.mv != null) {
/* 172 */       this.mv.visitProvide(service, providers);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void visitEnd() {
/* 181 */     if (this.mv != null)
/* 182 */       this.mv.visitEnd(); 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\objectweb\asm\ModuleVisitor.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */