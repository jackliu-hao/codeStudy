/*     */ package org.noear.solon.aspect.asm;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import org.noear.solon.core.event.EventBus;
/*     */ import org.objectweb.asm.ClassReader;
/*     */ import org.objectweb.asm.ClassVisitor;
/*     */ import org.objectweb.asm.MethodVisitor;
/*     */ 
/*     */ 
/*     */ public class TargetClassVisitor
/*     */   extends ClassVisitor
/*     */ {
/*     */   private boolean isFinal;
/*  15 */   private List<MethodBean> methods = new ArrayList<>();
/*  16 */   private List<MethodBean> declaredMethods = new ArrayList<>();
/*  17 */   private List<MethodBean> constructors = new ArrayList<>();
/*     */   
/*     */   public TargetClassVisitor() {
/*  20 */     super(524288);
/*     */   }
/*     */ 
/*     */   
/*     */   public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
/*  25 */     super.visit(version, access, name, signature, superName, interfaces);
/*  26 */     if ((access & 0x10) == 16) {
/*  27 */       this.isFinal = true;
/*     */     }
/*  29 */     if (superName != null) {
/*  30 */       List<MethodBean> beans = initMethodBeanByParent(superName);
/*  31 */       if (beans != null && !beans.isEmpty()) {
/*  32 */         for (MethodBean bean : beans) {
/*  33 */           if (!this.methods.contains(bean)) {
/*  34 */             this.methods.add(bean);
/*     */           }
/*     */         } 
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
/*  43 */     if ("<init>".equals(name)) {
/*     */       
/*  45 */       MethodBean constructor = new MethodBean(access, name, descriptor);
/*  46 */       this.constructors.add(constructor);
/*  47 */     } else if (!"<clinit>".equals(name)) {
/*     */       
/*  49 */       if ((access & 0x10) == 16 || (access & 0x8) == 8)
/*     */       {
/*  51 */         return super.visitMethod(access, name, descriptor, signature, exceptions);
/*     */       }
/*  53 */       MethodBean methodBean = new MethodBean(access, name, descriptor);
/*  54 */       this.declaredMethods.add(methodBean);
/*  55 */       if ((access & 0x1) == 1 && (access & 0x400) != 1024)
/*     */       {
/*  57 */         if (!this.methods.contains(methodBean)) {
/*  58 */           this.methods.add(methodBean);
/*     */         }
/*     */       }
/*     */     } 
/*     */     
/*  63 */     return super.visitMethod(access, name, descriptor, signature, exceptions);
/*     */   }
/*     */   
/*     */   public boolean isFinal() {
/*  67 */     return this.isFinal;
/*     */   }
/*     */   
/*     */   public List<MethodBean> getMethods() {
/*  71 */     return this.methods;
/*     */   }
/*     */   
/*     */   public List<MethodBean> getDeclaredMethods() {
/*  75 */     return this.declaredMethods;
/*     */   }
/*     */   
/*     */   public List<MethodBean> getConstructors() {
/*  79 */     return this.constructors;
/*     */   }
/*     */   
/*     */   private List<MethodBean> initMethodBeanByParent(String superName) {
/*     */     try {
/*  84 */       if (superName != null && !superName.isEmpty()) {
/*  85 */         ClassReader reader = new ClassReader(superName);
/*  86 */         TargetClassVisitor visitor = new TargetClassVisitor();
/*  87 */         reader.accept(visitor, 2);
/*  88 */         List<MethodBean> beans = new ArrayList<>();
/*  89 */         for (MethodBean methodBean : visitor.methods) {
/*     */           
/*  91 */           if ((methodBean.access & 0x10) == 16 || (methodBean.access & 0x8) == 8) {
/*     */             continue;
/*     */           }
/*     */ 
/*     */           
/*  96 */           if ((methodBean.access & 0x1) == 1) {
/*  97 */             beans.add(methodBean);
/*     */           }
/*     */         } 
/* 100 */         return beans;
/*     */       } 
/* 102 */     } catch (Exception ex) {
/* 103 */       EventBus.push(ex);
/*     */     } 
/* 105 */     return null;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\solon\aspect\asm\TargetClassVisitor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */