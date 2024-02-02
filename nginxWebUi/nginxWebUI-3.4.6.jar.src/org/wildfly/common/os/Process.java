/*    */ package org.wildfly.common.os;
/*    */ 
/*    */ import java.security.AccessController;
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
/*    */ public final class Process
/*    */ {
/*    */   private static final long processId;
/*    */   private static final String processName;
/*    */   
/*    */   static {
/* 33 */     Object[] array = AccessController.<Object[]>doPrivileged(new GetProcessInfoAction());
/* 34 */     processId = ((Long)array[0]).longValue();
/* 35 */     processName = (String)array[1];
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
/*    */   public static String getProcessName() {
/* 47 */     return processName;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static long getProcessId() {
/* 57 */     return processId;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\wildfly\common\os\Process.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */