/*    */ package com.mysql.cj.protocol.x;
/*    */ 
/*    */ import com.mysql.cj.protocol.ProtocolEntity;
/*    */ import com.mysql.cj.protocol.Warning;
/*    */ import com.mysql.cj.xdevapi.Result;
/*    */ import com.mysql.cj.xdevapi.Warning;
/*    */ import com.mysql.cj.xdevapi.WarningImpl;
/*    */ import java.util.ArrayList;
/*    */ import java.util.Collections;
/*    */ import java.util.Iterator;
/*    */ import java.util.List;
/*    */ import java.util.stream.Collectors;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class StatementExecuteOk
/*    */   implements ProtocolEntity, Result
/*    */ {
/* 47 */   private long rowsAffected = 0L;
/* 48 */   private Long lastInsertId = null;
/*    */   private List<String> generatedIds;
/*    */   private List<Warning> warnings;
/*    */   
/*    */   public StatementExecuteOk() {
/* 53 */     this.generatedIds = Collections.emptyList();
/* 54 */     this.warnings = new ArrayList<>();
/*    */   }
/*    */   
/*    */   public StatementExecuteOk(long rowsAffected, Long lastInsertId, List<String> generatedIds, List<Warning> warnings) {
/* 58 */     this.rowsAffected = rowsAffected;
/* 59 */     this.lastInsertId = lastInsertId;
/* 60 */     this.generatedIds = Collections.unmodifiableList(generatedIds);
/* 61 */     this.warnings = warnings;
/*    */   }
/*    */   
/*    */   public long getAffectedItemsCount() {
/* 65 */     return this.rowsAffected;
/*    */   }
/*    */   
/*    */   public Long getLastInsertId() {
/* 69 */     return this.lastInsertId;
/*    */   }
/*    */   
/*    */   public List<String> getGeneratedIds() {
/* 73 */     return this.generatedIds;
/*    */   }
/*    */ 
/*    */   
/*    */   public int getWarningsCount() {
/* 78 */     return this.warnings.size();
/*    */   }
/*    */ 
/*    */   
/*    */   public Iterator<Warning> getWarnings() {
/* 83 */     return ((List<Warning>)this.warnings.stream().map(w -> new WarningImpl(w)).collect(Collectors.toList())).iterator();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\protocol\x\StatementExecuteOk.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */