/*     */ package org.noear.solon.core.wrap;
/*     */ 
/*     */ import java.lang.reflect.Parameter;
/*     */ import java.lang.reflect.ParameterizedType;
/*     */ import java.lang.reflect.Type;
/*     */ import org.noear.solon.Utils;
/*     */ import org.noear.solon.annotation.Body;
/*     */ import org.noear.solon.annotation.Header;
/*     */ import org.noear.solon.annotation.Param;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class ParamWrap
/*     */ {
/*     */   private final Parameter parameter;
/*     */   private String name;
/*     */   private String defaultValue;
/*     */   private boolean required;
/*     */   private boolean requireBody;
/*     */   private boolean requireHeader;
/*     */   private ParameterizedType genericType;
/*     */   
/*     */   public ParamWrap(Parameter parameter) {
/*  26 */     this.parameter = parameter;
/*  27 */     this.name = parameter.getName();
/*     */     
/*  29 */     Param paramAnno = parameter.<Param>getAnnotation(Param.class);
/*  30 */     Header headerAnno = parameter.<Header>getAnnotation(Header.class);
/*  31 */     Body bodyAnno = parameter.<Body>getAnnotation(Body.class);
/*     */     
/*  33 */     if (paramAnno != null) {
/*  34 */       String name2 = Utils.annoAlias(paramAnno.value(), paramAnno.name());
/*  35 */       if (Utils.isNotEmpty(name2)) {
/*  36 */         this.name = name2;
/*     */       }
/*     */       
/*  39 */       if (!"\n\t\t\n\t\t\n\n\t\t\t\t\n".equals(paramAnno.defaultValue())) {
/*  40 */         this.defaultValue = paramAnno.defaultValue();
/*     */       }
/*     */       
/*  43 */       this.required = paramAnno.required();
/*     */     } 
/*     */     
/*  46 */     if (headerAnno != null) {
/*  47 */       String name2 = Utils.annoAlias(headerAnno.value(), headerAnno.name());
/*  48 */       if (Utils.isNotEmpty(name2)) {
/*  49 */         this.name = name2;
/*     */       }
/*     */       
/*  52 */       if (!"\n\t\t\n\t\t\n\n\t\t\t\t\n".equals(headerAnno.defaultValue())) {
/*  53 */         this.defaultValue = headerAnno.defaultValue();
/*     */       }
/*     */       
/*  56 */       this.required = headerAnno.required();
/*  57 */       this.requireHeader = true;
/*     */     } 
/*     */     
/*  60 */     if (bodyAnno != null) {
/*  61 */       this.requireBody = true;
/*     */     }
/*     */     
/*  64 */     Type tmp = parameter.getParameterizedType();
/*  65 */     if (tmp instanceof ParameterizedType) {
/*  66 */       this.genericType = (ParameterizedType)tmp;
/*     */     } else {
/*  68 */       this.genericType = null;
/*     */     } 
/*     */   }
/*     */   
/*     */   public Parameter getParameter() {
/*  73 */     return this.parameter;
/*     */   }
/*     */   
/*     */   public String getName() {
/*  77 */     return this.name;
/*     */   }
/*     */   
/*     */   public ParameterizedType getGenericType() {
/*  81 */     return this.genericType;
/*     */   }
/*     */   
/*     */   public Class<?> getType() {
/*  85 */     return this.parameter.getType();
/*     */   }
/*     */   
/*     */   public boolean required() {
/*  89 */     return this.required;
/*     */   }
/*     */   
/*     */   public boolean requireBody() {
/*  93 */     return this.requireBody;
/*     */   }
/*     */   
/*     */   public boolean requireHeader() {
/*  97 */     return this.requireHeader;
/*     */   }
/*     */   
/*     */   public String defaultValue() {
/* 101 */     return this.defaultValue;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\solon\core\wrap\ParamWrap.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */