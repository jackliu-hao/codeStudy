/*    */ package org.noear.snack.core.utils;
/*    */ 
/*    */ import java.io.Reader;
/*    */ import java.sql.Clob;
/*    */ import java.util.Map;
/*    */ import java.util.concurrent.ConcurrentHashMap;
/*    */ import org.noear.snack.exception.SnackException;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class BeanUtil
/*    */ {
/* 15 */   public static final Map<String, Class<?>> clzCached = new ConcurrentHashMap<>();
/*    */   
/*    */   public static Class<?> loadClass(String clzName) {
/*    */     try {
/* 19 */       Class<?> clz = clzCached.get(clzName);
/* 20 */       if (clz == null) {
/* 21 */         clz = Class.forName(clzName);
/* 22 */         clzCached.put(clzName, clz);
/*    */       } 
/*    */       
/* 25 */       return clz;
/* 26 */     } catch (RuntimeException e) {
/* 27 */       throw e;
/* 28 */     } catch (Throwable e) {
/* 29 */       throw new SnackException(e);
/*    */     } 
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static String clobToString(Clob clob) {
/* 41 */     Reader reader = null;
/* 42 */     StringBuilder buf = new StringBuilder();
/*    */     
/*    */     try {
/* 45 */       reader = clob.getCharacterStream();
/*    */       
/* 47 */       char[] chars = new char[2048];
/*    */       while (true) {
/* 49 */         int len = reader.read(chars, 0, chars.length);
/* 50 */         if (len < 0) {
/*    */           break;
/*    */         }
/* 53 */         buf.append(chars, 0, len);
/*    */       } 
/* 55 */     } catch (Throwable e) {
/* 56 */       throw new SnackException("read string from reader error", e);
/*    */     } 
/*    */     
/* 59 */     String text = buf.toString();
/*    */     
/* 61 */     if (reader != null) {
/*    */       try {
/* 63 */         reader.close();
/* 64 */       } catch (Throwable e) {
/* 65 */         throw new SnackException("read string from reader error", e);
/*    */       } 
/*    */     }
/*    */     
/* 69 */     return text;
/*    */   }
/*    */   
/*    */   public static Object newInstance(Class<?> clz) {
/*    */     try {
/* 74 */       if (clz.isInterface()) {
/* 75 */         return null;
/*    */       }
/* 77 */       return clz.newInstance();
/*    */     }
/* 79 */     catch (Throwable e) {
/* 80 */       throw new SnackException("create instance error, class " + clz.getName(), e);
/*    */     } 
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\snack\cor\\utils\BeanUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */