/*    */ package META-INF.versions.9.org.wildfly.common.lock;
/*    */ 
/*    */ import java.security.AccessController;
/*    */ import java.security.PrivilegedAction;
/*    */ import sun.misc.Unsafe;
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
/*    */ 
/*    */ final class JDKSpecific
/*    */ {
/* 35 */   static final Unsafe unsafe = AccessController.<Unsafe>doPrivileged((PrivilegedAction<Unsafe>)new Object());
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
/*    */   static void onSpinWait() {
/* 51 */     Thread.onSpinWait();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\META-INF\versions\9\org\wildfly\common\lock\JDKSpecific.class
 * Java compiler version: 9 (53.0)
 * JD-Core Version:       1.1.3
 */