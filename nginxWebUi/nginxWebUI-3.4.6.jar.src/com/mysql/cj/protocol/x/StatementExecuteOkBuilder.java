/*    */ package com.mysql.cj.protocol.x;
/*    */ 
/*    */ import com.mysql.cj.exceptions.ExceptionFactory;
/*    */ import com.mysql.cj.exceptions.WrongArgumentException;
/*    */ import com.mysql.cj.protocol.ProtocolEntity;
/*    */ import com.mysql.cj.protocol.ResultBuilder;
/*    */ import com.mysql.cj.protocol.Warning;
/*    */ import com.mysql.cj.x.protobuf.MysqlxDatatypes;
/*    */ import java.util.ArrayList;
/*    */ import java.util.Collections;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ public class StatementExecuteOkBuilder
/*    */   implements ResultBuilder<StatementExecuteOk>
/*    */ {
/* 50 */   private long rowsAffected = 0L;
/* 51 */   private Long lastInsertId = null;
/* 52 */   private List<String> generatedIds = Collections.emptyList();
/* 53 */   private List<Warning> warnings = new ArrayList<>();
/*    */ 
/*    */   
/*    */   public boolean addProtocolEntity(ProtocolEntity entity) {
/* 57 */     if (entity instanceof Notice) {
/* 58 */       addNotice((Notice)entity);
/* 59 */       return false;
/*    */     } 
/* 61 */     if (entity instanceof FetchDoneEntity) {
/* 62 */       return false;
/*    */     }
/* 64 */     if (entity instanceof StatementExecuteOk) {
/* 65 */       return true;
/*    */     }
/* 67 */     throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, "Unexpected protocol entity " + entity);
/*    */   }
/*    */   
/*    */   public StatementExecuteOk build() {
/* 71 */     return new StatementExecuteOk(this.rowsAffected, this.lastInsertId, this.generatedIds, this.warnings);
/*    */   }
/*    */   
/*    */   private void addNotice(Notice notice) {
/* 75 */     if (notice instanceof Notice.XWarning) {
/* 76 */       this.warnings.add((Notice.XWarning)notice);
/*    */     }
/* 78 */     else if (notice instanceof Notice.XSessionStateChanged) {
/* 79 */       switch (((Notice.XSessionStateChanged)notice).getParamType().intValue()) {
/*    */         case 3:
/* 81 */           this.lastInsertId = Long.valueOf(((Notice.XSessionStateChanged)notice).getValue().getVUnsignedInt());
/*    */           break;
/*    */         case 4:
/* 84 */           this.rowsAffected = ((Notice.XSessionStateChanged)notice).getValue().getVUnsignedInt();
/*    */           break;
/*    */         case 12:
/* 87 */           this
/* 88 */             .generatedIds = (List<String>)((Notice.XSessionStateChanged)notice).getValueList().stream().map(v -> v.getVOctets().getValue().toStringUtf8()).collect(Collectors.toList());
/*    */           break;
/*    */       } 
/*    */     } 
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\protocol\x\StatementExecuteOkBuilder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */