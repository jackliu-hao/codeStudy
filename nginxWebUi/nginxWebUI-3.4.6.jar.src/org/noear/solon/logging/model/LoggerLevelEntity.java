/*    */ package org.noear.solon.logging.model;
/*    */ 
/*    */ import java.util.Objects;
/*    */ import org.noear.solon.logging.event.Level;
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
/*    */ public class LoggerLevelEntity
/*    */ {
/*    */   private final String loggerExpr;
/*    */   private final Level level;
/*    */   
/*    */   public Level getLevel() {
/* 22 */     return this.level;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public String getLoggerExpr() {
/* 29 */     return this.loggerExpr;
/*    */   }
/*    */   
/*    */   public LoggerLevelEntity(String loggerExpr, Level level) {
/* 33 */     this.loggerExpr = loggerExpr;
/* 34 */     this.level = level;
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean equals(Object o) {
/* 39 */     if (this == o) return true; 
/* 40 */     if (!(o instanceof LoggerLevelEntity)) return false; 
/* 41 */     LoggerLevelEntity that = (LoggerLevelEntity)o;
/* 42 */     return Objects.equals(this.loggerExpr, that.loggerExpr);
/*    */   }
/*    */ 
/*    */   
/*    */   public int hashCode() {
/* 47 */     return Objects.hash(new Object[] { this.loggerExpr });
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\solon\logging\model\LoggerLevelEntity.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */