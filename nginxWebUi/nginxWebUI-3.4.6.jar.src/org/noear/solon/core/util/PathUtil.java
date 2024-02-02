/*     */ package org.noear.solon.core.util;
/*     */ 
/*     */ import java.net.URLDecoder;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import java.util.regex.Matcher;
/*     */ import java.util.regex.Pattern;
/*     */ import org.noear.solon.Solon;
/*     */ import org.noear.solon.Utils;
/*     */ import org.noear.solon.core.NvMap;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class PathUtil
/*     */ {
/*     */   public static String mergePath(String path1, String path2) {
/*  18 */     if (Utils.isEmpty(path1) || "**".equals(path1) || "/**".equals(path1)) {
/*  19 */       if (path2.startsWith("/")) {
/*  20 */         return path2;
/*     */       }
/*  22 */       return "/" + path2;
/*     */     } 
/*     */ 
/*     */     
/*  26 */     if (!path1.startsWith("/")) {
/*  27 */       path1 = "/" + path1;
/*     */     }
/*     */     
/*  30 */     if (Utils.isEmpty(path2)) {
/*  31 */       if (path1.endsWith("*")) {
/*     */         
/*  33 */         int idx = path1.lastIndexOf('/') + 1;
/*  34 */         if (idx < 1) {
/*  35 */           return "/";
/*     */         }
/*  37 */         return path1.substring(0, idx) + path2;
/*     */       } 
/*     */       
/*  40 */       return path1;
/*     */     } 
/*     */ 
/*     */     
/*  44 */     if (path2.startsWith("/")) {
/*  45 */       path2 = path2.substring(1);
/*     */     }
/*     */     
/*  48 */     if (path1.endsWith("/")) {
/*  49 */       return path1 + path2;
/*     */     }
/*  51 */     if (path1.endsWith("*")) {
/*     */       
/*  53 */       int idx = path1.lastIndexOf('/') + 1;
/*  54 */       if (idx < 1) {
/*  55 */         return path2;
/*     */       }
/*  57 */       return path1.substring(0, idx) + path2;
/*     */     } 
/*     */     
/*  60 */     return path1 + "/" + path2;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*  65 */   public static final Pattern pathKeyExpr = Pattern.compile("\\{([^\\\\}]+)\\}");
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static NvMap pathVarMap(String path, String expr) {
/*  71 */     NvMap _map = new NvMap();
/*     */ 
/*     */     
/*  74 */     if (expr.indexOf("{") >= 0) {
/*  75 */       String path2 = null;
/*     */       try {
/*  77 */         path2 = URLDecoder.decode(path, Solon.encoding());
/*  78 */       } catch (Throwable ex) {
/*  79 */         path2 = path;
/*     */       } 
/*     */       
/*  82 */       Matcher pm = pathKeyExpr.matcher(expr);
/*     */       
/*  84 */       List<String> _pks = new ArrayList<>();
/*     */       
/*  86 */       while (pm.find()) {
/*  87 */         _pks.add(pm.group(1));
/*     */       }
/*     */       
/*  90 */       if (_pks.size() > 0) {
/*  91 */         PathAnalyzer _pr = PathAnalyzer.get(expr);
/*     */         
/*  93 */         pm = _pr.matcher(path2);
/*  94 */         if (pm.find()) {
/*  95 */           for (int i = 0, len = _pks.size(); i < len; i++) {
/*  96 */             _map.put(_pks.get(i), pm.group(i + 1));
/*     */           }
/*     */         }
/*     */       } 
/*     */     } 
/*     */     
/* 102 */     return _map;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\solon\cor\\util\PathUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */