/*    */ package cn.hutool.cron.task;
/*    */ 
/*    */ import cn.hutool.cron.pattern.CronPattern;
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
/*    */ public class CronTask
/*    */   implements Task
/*    */ {
/*    */   private final String id;
/*    */   private CronPattern pattern;
/*    */   private final Task task;
/*    */   
/*    */   public CronTask(String id, CronPattern pattern, Task task) {
/* 24 */     this.id = id;
/* 25 */     this.pattern = pattern;
/* 26 */     this.task = task;
/*    */   }
/*    */ 
/*    */   
/*    */   public void execute() {
/* 31 */     this.task.execute();
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public String getId() {
/* 40 */     return this.id;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public CronPattern getPattern() {
/* 49 */     return this.pattern;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public CronTask setPattern(CronPattern pattern) {
/* 58 */     this.pattern = pattern;
/* 59 */     return this;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public Task getRaw() {
/* 68 */     return this.task;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\cron\task\CronTask.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */