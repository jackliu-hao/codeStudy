/*    */ package freemarker.template.utility;
/*    */ 
/*    */ import freemarker.log.Logger;
/*    */ import java.security.AccessControlException;
/*    */ import java.security.AccessController;
/*    */ import java.security.PrivilegedAction;
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
/*    */ public class SecurityUtilities
/*    */ {
/* 31 */   private static final Logger LOG = Logger.getLogger("freemarker.security");
/*    */ 
/*    */ 
/*    */   
/*    */   public static String getSystemProperty(final String key) {
/* 36 */     return AccessController.<String>doPrivileged(new PrivilegedAction<String>()
/*    */         {
/*    */           
/*    */           public Object run()
/*    */           {
/* 41 */             return System.getProperty(key);
/*    */           }
/*    */         });
/*    */   }
/*    */   
/*    */   public static String getSystemProperty(final String key, final String defValue) {
/*    */     try {
/* 48 */       return AccessController.<String>doPrivileged(new PrivilegedAction<String>()
/*    */           {
/*    */             
/*    */             public Object run()
/*    */             {
/* 53 */               return System.getProperty(key, defValue);
/*    */             }
/*    */           });
/* 56 */     } catch (AccessControlException e) {
/* 57 */       LOG.warn("Insufficient permissions to read system property " + 
/* 58 */           StringUtil.jQuoteNoXSS(key) + ", using default value " + 
/* 59 */           StringUtil.jQuoteNoXSS(defValue));
/* 60 */       return defValue;
/*    */     } 
/*    */   }
/*    */   
/*    */   public static Integer getSystemProperty(final String key, final int defValue) {
/*    */     try {
/* 66 */       return AccessController.<Integer>doPrivileged(new PrivilegedAction<Integer>()
/*    */           {
/*    */             
/*    */             public Object run()
/*    */             {
/* 71 */               return Integer.getInteger(key, defValue);
/*    */             }
/*    */           });
/* 74 */     } catch (AccessControlException e) {
/* 75 */       LOG.warn("Insufficient permissions to read system property " + 
/* 76 */           StringUtil.jQuote(key) + ", using default value " + defValue);
/* 77 */       return Integer.valueOf(defValue);
/*    */     } 
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\templat\\utility\SecurityUtilities.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */