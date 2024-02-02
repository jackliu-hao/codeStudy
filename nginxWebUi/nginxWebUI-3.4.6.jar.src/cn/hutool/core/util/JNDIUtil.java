/*    */ package cn.hutool.core.util;
/*    */ 
/*    */ import cn.hutool.core.convert.Convert;
/*    */ import cn.hutool.core.exceptions.UtilException;
/*    */ import cn.hutool.core.map.MapUtil;
/*    */ import java.util.Hashtable;
/*    */ import java.util.Map;
/*    */ import javax.naming.InitialContext;
/*    */ import javax.naming.NamingException;
/*    */ import javax.naming.directory.Attributes;
/*    */ import javax.naming.directory.InitialDirContext;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class JNDIUtil
/*    */ {
/*    */   public static InitialDirContext createInitialDirContext(Map<String, String> environment) {
/*    */     try {
/* 36 */       if (MapUtil.isEmpty(environment)) {
/* 37 */         return new InitialDirContext();
/*    */       }
/* 39 */       return new InitialDirContext((Hashtable<?, ?>)Convert.convert(Hashtable.class, environment));
/* 40 */     } catch (NamingException e) {
/* 41 */       throw new UtilException(e);
/*    */     } 
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static InitialContext createInitialContext(Map<String, String> environment) {
/*    */     try {
/* 53 */       if (MapUtil.isEmpty(environment)) {
/* 54 */         return new InitialContext();
/*    */       }
/* 56 */       return new InitialContext((Hashtable<?, ?>)Convert.convert(Hashtable.class, environment));
/* 57 */     } catch (NamingException e) {
/* 58 */       throw new UtilException(e);
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
/*    */   
/*    */   public static Attributes getAttributes(String uri, String... attrIds) {
/*    */     try {
/* 72 */       return createInitialDirContext(null).getAttributes(uri, attrIds);
/* 73 */     } catch (NamingException e) {
/* 74 */       throw new UtilException(e);
/*    */     } 
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\cor\\util\JNDIUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */