/*     */ package org.h2.security.auth;
/*     */ 
/*     */ import java.util.Arrays;
/*     */ import java.util.Collection;
/*     */ import java.util.HashMap;
/*     */ import org.h2.util.Utils;
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
/*     */ public class ConfigProperties
/*     */ {
/*     */   private HashMap<String, String> properties;
/*     */   
/*     */   public ConfigProperties() {
/*  22 */     this.properties = new HashMap<>();
/*     */   }
/*     */   
/*     */   public ConfigProperties(PropertyConfig... paramVarArgs) {
/*  26 */     this((paramVarArgs == null) ? null : Arrays.<PropertyConfig>asList(paramVarArgs));
/*     */   }
/*     */   
/*     */   public ConfigProperties(Collection<PropertyConfig> paramCollection) {
/*  30 */     this.properties = new HashMap<>();
/*  31 */     if (paramCollection != null) {
/*  32 */       for (PropertyConfig propertyConfig : paramCollection) {
/*  33 */         if (this.properties.putIfAbsent(propertyConfig.getName(), propertyConfig.getValue()) != null) {
/*  34 */           throw new AuthConfigException("duplicate property " + propertyConfig.getName());
/*     */         }
/*     */       } 
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
/*     */   public String getStringValue(String paramString1, String paramString2) {
/*  48 */     String str = this.properties.get(paramString1);
/*  49 */     if (str == null) {
/*  50 */       return paramString2;
/*     */     }
/*  52 */     return str;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getStringValue(String paramString) {
/*  63 */     String str = this.properties.get(paramString);
/*  64 */     if (str == null) {
/*  65 */       throw new AuthConfigException("missing config property " + paramString);
/*     */     }
/*  67 */     return str;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getIntValue(String paramString, int paramInt) {
/*  78 */     String str = this.properties.get(paramString);
/*  79 */     if (str == null) {
/*  80 */       return paramInt;
/*     */     }
/*  82 */     return Integer.parseInt(str);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getIntValue(String paramString) {
/*  93 */     String str = this.properties.get(paramString);
/*  94 */     if (str == null) {
/*  95 */       throw new AuthConfigException("missing config property " + paramString);
/*     */     }
/*  97 */     return Integer.parseInt(str);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean getBooleanValue(String paramString, boolean paramBoolean) {
/* 108 */     String str = this.properties.get(paramString);
/* 109 */     if (str == null) {
/* 110 */       return paramBoolean;
/*     */     }
/* 112 */     return Utils.parseBoolean(str, paramBoolean, true);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\security\auth\ConfigProperties.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */