/*    */ package cn.hutool.system;
/*    */ 
/*    */ import cn.hutool.core.io.FileUtil;
/*    */ import java.io.Serializable;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class RuntimeInfo
/*    */   implements Serializable
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/* 15 */   private final Runtime currentRuntime = Runtime.getRuntime();
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public final Runtime getRuntime() {
/* 23 */     return this.currentRuntime;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public final long getMaxMemory() {
/* 32 */     return this.currentRuntime.maxMemory();
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public final long getTotalMemory() {
/* 41 */     return this.currentRuntime.totalMemory();
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public final long getFreeMemory() {
/* 50 */     return this.currentRuntime.freeMemory();
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public final long getUsableMemory() {
/* 59 */     return this.currentRuntime.maxMemory() - this.currentRuntime.totalMemory() + this.currentRuntime.freeMemory();
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 64 */     StringBuilder builder = new StringBuilder();
/*    */     
/* 66 */     SystemUtil.append(builder, "Max Memory:    ", FileUtil.readableFileSize(getMaxMemory()));
/* 67 */     SystemUtil.append(builder, "Total Memory:     ", FileUtil.readableFileSize(getTotalMemory()));
/* 68 */     SystemUtil.append(builder, "Free Memory:     ", FileUtil.readableFileSize(getFreeMemory()));
/* 69 */     SystemUtil.append(builder, "Usable Memory:     ", FileUtil.readableFileSize(getUsableMemory()));
/*    */     
/* 71 */     return builder.toString();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\system\RuntimeInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */