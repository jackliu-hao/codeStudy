/*     */ package com.sun.mail.util;
/*     */ 
/*     */ import java.util.Properties;
/*     */ import javax.mail.Session;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class PropUtil
/*     */ {
/*     */   public static int getIntProperty(Properties props, String name, int def) {
/*  62 */     return getInt(getProp(props, name), def);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean getBooleanProperty(Properties props, String name, boolean def) {
/*  70 */     return getBoolean(getProp(props, name), def);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static int getIntSessionProperty(Session session, String name, int def) {
/*  78 */     return getInt(getProp(session.getProperties(), name), def);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean getBooleanSessionProperty(Session session, String name, boolean def) {
/*  86 */     return getBoolean(getProp(session.getProperties(), name), def);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean getBooleanSystemProperty(String name, boolean def) {
/*     */     try {
/*  94 */       return getBoolean(getProp(System.getProperties(), name), def);
/*  95 */     } catch (SecurityException sex) {
/*     */ 
/*     */       
/*     */       try {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 104 */         String value = System.getProperty(name);
/* 105 */         if (value == null)
/* 106 */           return def; 
/* 107 */         if (def) {
/* 108 */           return !value.equalsIgnoreCase("false");
/*     */         }
/* 110 */         return value.equalsIgnoreCase("true");
/* 111 */       } catch (SecurityException securityException) {
/* 112 */         return def;
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static Object getProp(Properties props, String name) {
/* 122 */     Object val = props.get(name);
/* 123 */     if (val != null) {
/* 124 */       return val;
/*     */     }
/* 126 */     return props.getProperty(name);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static int getInt(Object value, int def) {
/* 134 */     if (value == null)
/* 135 */       return def; 
/* 136 */     if (value instanceof String) {
/*     */       try {
/* 138 */         return Integer.parseInt((String)value);
/* 139 */       } catch (NumberFormatException nfex) {}
/*     */     }
/* 141 */     if (value instanceof Integer)
/* 142 */       return ((Integer)value).intValue(); 
/* 143 */     return def;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static boolean getBoolean(Object value, boolean def) {
/* 151 */     if (value == null)
/* 152 */       return def; 
/* 153 */     if (value instanceof String) {
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 158 */       if (def) {
/* 159 */         return !((String)value).equalsIgnoreCase("false");
/*     */       }
/* 161 */       return ((String)value).equalsIgnoreCase("true");
/*     */     } 
/* 163 */     if (value instanceof Boolean)
/* 164 */       return ((Boolean)value).booleanValue(); 
/* 165 */     return def;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\sun\mai\\util\PropUtil.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       1.1.3
 */