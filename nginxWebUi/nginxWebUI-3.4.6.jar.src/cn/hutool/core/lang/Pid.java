/*    */ package cn.hutool.core.lang;
/*    */ 
/*    */ import cn.hutool.core.exceptions.UtilException;
/*    */ import cn.hutool.core.util.StrUtil;
/*    */ import java.lang.management.ManagementFactory;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public enum Pid
/*    */ {
/* 16 */   INSTANCE;
/*    */   
/*    */   private final int pid;
/*    */   
/*    */   Pid() {
/* 21 */     this.pid = getPid();
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public int get() {
/* 30 */     return this.pid;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   private static int getPid() throws UtilException {
/* 40 */     String processName = ManagementFactory.getRuntimeMXBean().getName();
/* 41 */     if (StrUtil.isBlank(processName)) {
/* 42 */       throw new UtilException("Process name is blank!");
/*    */     }
/* 44 */     int atIndex = processName.indexOf('@');
/* 45 */     if (atIndex > 0) {
/* 46 */       return Integer.parseInt(processName.substring(0, atIndex));
/*    */     }
/* 48 */     return processName.hashCode();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\lang\Pid.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */