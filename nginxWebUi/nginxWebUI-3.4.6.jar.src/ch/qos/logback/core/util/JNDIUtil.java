/*    */ package ch.qos.logback.core.util;
/*    */ 
/*    */ import java.util.Hashtable;
/*    */ import javax.naming.Context;
/*    */ import javax.naming.InitialContext;
/*    */ import javax.naming.NamingException;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class JNDIUtil
/*    */ {
/*    */   static final String RESTRICTION_MSG = "JNDI name must start with java: but was ";
/*    */   
/*    */   public static Context getInitialContext() throws NamingException {
/* 38 */     return new InitialContext();
/*    */   }
/*    */   
/*    */   public static Context getInitialContext(Hashtable<?, ?> props) throws NamingException {
/* 42 */     return new InitialContext(props);
/*    */   }
/*    */   
/*    */   public static Object lookupObject(Context ctx, String name) throws NamingException {
/* 46 */     if (ctx == null) {
/* 47 */       return null;
/*    */     }
/*    */     
/* 50 */     if (OptionHelper.isEmpty(name)) {
/* 51 */       return null;
/*    */     }
/*    */     
/* 54 */     jndiNameSecurityCheck(name);
/*    */     
/* 56 */     Object lookup = ctx.lookup(name);
/* 57 */     return lookup;
/*    */   }
/*    */   
/*    */   private static void jndiNameSecurityCheck(String name) throws NamingException {
/* 61 */     if (!name.startsWith("java:")) {
/* 62 */       throw new NamingException("JNDI name must start with java: but was " + name);
/*    */     }
/*    */   }
/*    */   
/*    */   public static String lookupString(Context ctx, String name) throws NamingException {
/* 67 */     Object lookup = lookupObject(ctx, name);
/* 68 */     return (String)lookup;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\ch\qos\logback\cor\\util\JNDIUtil.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */