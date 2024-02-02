/*     */ package org.noear.solon.data.util;
/*     */ 
/*     */ import java.lang.reflect.Method;
/*     */ import java.util.Map;
/*     */ import java.util.regex.Matcher;
/*     */ import java.util.regex.Pattern;
/*     */ import org.noear.solon.Utils;
/*     */ import org.noear.solon.core.aspect.Invocation;
/*     */ import org.noear.solon.core.wrap.ClassWrap;
/*     */ import org.noear.solon.core.wrap.FieldWrap;
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
/*     */ public class InvKeys
/*     */ {
/*     */   public static String buildByInv(Invocation inv) {
/*  27 */     Method method = inv.method().getMethod();
/*     */     
/*  29 */     StringBuilder keyB = new StringBuilder();
/*     */     
/*  31 */     keyB.append(method.getDeclaringClass().getName()).append(":");
/*  32 */     keyB.append(method.getName()).append(":");
/*     */     
/*  34 */     inv.argsAsMap().forEach((k, v) -> keyB.append(k).append("_").append(v));
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  39 */     return Utils.md5(keyB.toString());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String buildByTmlAndInv(String tml, Invocation inv) {
/*  49 */     return buildByTmlAndInv(tml, inv, null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String buildByTmlAndInv(String tml, Invocation inv, Object rst) {
/*  60 */     if (tml.indexOf("$") < 0) {
/*  61 */       return tml;
/*     */     }
/*     */     
/*  64 */     Map map = inv.argsAsMap();
/*  65 */     String str2 = tml;
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  70 */     Pattern pattern = Pattern.compile("\\$\\{(\\w*\\.?\\w+)\\}");
/*  71 */     Matcher m = pattern.matcher(tml);
/*  72 */     while (m.find()) {
/*  73 */       String mark = m.group(0);
/*  74 */       String name = m.group(1);
/*     */       
/*  76 */       if (map.containsKey(name)) {
/*     */         
/*  78 */         String val = String.valueOf(map.get(name));
/*     */         
/*  80 */         str2 = str2.replace(mark, val); continue;
/*  81 */       }  if (name.contains(".")) {
/*     */         Object obj;
/*     */         
/*  84 */         String fieldKey = null;
/*  85 */         String fieldVal = null;
/*  86 */         if (name.startsWith(".")) {
/*  87 */           obj = rst;
/*  88 */           fieldKey = name.substring(1);
/*     */         } else {
/*  90 */           String[] cf = name.split("\\.");
/*  91 */           obj = map.get(cf[0]);
/*  92 */           fieldKey = cf[1];
/*     */         } 
/*     */         
/*  95 */         if (obj != null) {
/*  96 */           Object valTmp = null;
/*     */           
/*  98 */           if (obj instanceof Map) {
/*  99 */             valTmp = ((Map)obj).get(fieldKey);
/*     */           } else {
/* 101 */             FieldWrap fw = ClassWrap.get(obj.getClass()).getFieldWrap(fieldKey);
/* 102 */             if (fw == null) {
/* 103 */               throw new IllegalArgumentException("Missing cache tag parameter (result field): " + name);
/*     */             }
/*     */             
/*     */             try {
/* 107 */               valTmp = fw.getValue(obj);
/* 108 */             } catch (ReflectiveOperationException e) {
/* 109 */               throw new RuntimeException(e);
/*     */             } 
/*     */           } 
/*     */           
/* 113 */           if (valTmp != null) {
/* 114 */             fieldVal = valTmp.toString();
/*     */           }
/*     */         } 
/*     */         
/* 118 */         if (fieldVal == null) {
/* 119 */           fieldVal = "null";
/*     */         }
/*     */         
/* 122 */         str2 = str2.replace(mark, fieldVal);
/*     */         continue;
/*     */       } 
/* 125 */       throw new IllegalArgumentException("Missing cache tag parameter: " + name);
/*     */     } 
/*     */ 
/*     */     
/* 129 */     return str2;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\solon\dat\\util\InvKeys.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */