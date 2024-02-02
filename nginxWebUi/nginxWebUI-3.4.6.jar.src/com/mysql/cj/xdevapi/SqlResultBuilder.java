/*     */ package com.mysql.cj.xdevapi;
/*     */ 
/*     */ import com.mysql.cj.MysqlxSession;
/*     */ import com.mysql.cj.conf.PropertySet;
/*     */ import com.mysql.cj.protocol.ColumnDefinition;
/*     */ import com.mysql.cj.protocol.ProtocolEntity;
/*     */ import com.mysql.cj.protocol.ResultBuilder;
/*     */ import com.mysql.cj.protocol.x.StatementExecuteOkBuilder;
/*     */ import com.mysql.cj.result.BufferedRowList;
/*     */ import com.mysql.cj.result.DefaultColumnDefinition;
/*     */ import com.mysql.cj.result.Field;
/*     */ import com.mysql.cj.result.Row;
/*     */ import com.mysql.cj.result.RowList;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import java.util.TimeZone;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class SqlResultBuilder
/*     */   implements ResultBuilder<SqlResult>
/*     */ {
/*  55 */   private ArrayList<Field> fields = new ArrayList<>();
/*     */   private ColumnDefinition metadata;
/*  57 */   private List<Row> rows = new ArrayList<>();
/*     */   
/*     */   TimeZone defaultTimeZone;
/*     */   
/*     */   PropertySet pset;
/*     */   boolean isRowResult = false;
/*  63 */   List<SqlSingleResult> resultSets = new ArrayList<>();
/*     */   
/*  65 */   private ProtocolEntity prevEntity = null;
/*  66 */   private StatementExecuteOkBuilder statementExecuteOkBuilder = new StatementExecuteOkBuilder();
/*     */   
/*     */   public SqlResultBuilder(TimeZone defaultTimeZone, PropertySet pset) {
/*  69 */     this.defaultTimeZone = defaultTimeZone;
/*  70 */     this.pset = pset;
/*     */   }
/*     */   
/*     */   public SqlResultBuilder(MysqlxSession sess) {
/*  74 */     this.defaultTimeZone = sess.getServerSession().getDefaultTimeZone();
/*  75 */     this.pset = sess.getPropertySet();
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean addProtocolEntity(ProtocolEntity entity) {
/*  80 */     if (entity instanceof Field) {
/*  81 */       this.fields.add((Field)entity);
/*  82 */       if (!this.isRowResult) {
/*  83 */         this.isRowResult = true;
/*     */       }
/*  85 */       this.prevEntity = entity;
/*  86 */       return false;
/*     */     } 
/*  88 */     if (entity instanceof com.mysql.cj.protocol.x.Notice) {
/*  89 */       this.statementExecuteOkBuilder.addProtocolEntity(entity);
/*  90 */       return false;
/*     */     } 
/*     */     
/*  93 */     if (this.isRowResult && this.metadata == null) {
/*  94 */       this.metadata = (ColumnDefinition)new DefaultColumnDefinition(this.fields.<Field>toArray(new Field[0]));
/*     */     }
/*     */     
/*  97 */     if (entity instanceof Row) {
/*  98 */       this.rows.add(((Row)entity).setMetadata(this.metadata));
/*     */     }
/* 100 */     else if (entity instanceof com.mysql.cj.protocol.x.FetchDoneMoreResults) {
/* 101 */       this.resultSets.add(new SqlSingleResult(this.metadata, this.defaultTimeZone, (RowList)new BufferedRowList(this.rows), () -> this.statementExecuteOkBuilder.build(), this.pset));
/*     */ 
/*     */       
/* 104 */       this.fields = new ArrayList<>();
/* 105 */       this.metadata = null;
/* 106 */       this.rows = new ArrayList<>();
/* 107 */       this.statementExecuteOkBuilder = new StatementExecuteOkBuilder();
/*     */     }
/* 109 */     else if (entity instanceof com.mysql.cj.protocol.x.FetchDoneEntity) {
/* 110 */       if (!(this.prevEntity instanceof com.mysql.cj.protocol.x.FetchDoneMoreResults))
/*     */       {
/*     */         
/* 113 */         this.resultSets.add(new SqlSingleResult(this.metadata, this.defaultTimeZone, (RowList)new BufferedRowList(this.rows), () -> this.statementExecuteOkBuilder.build(), this.pset));
/*     */       
/*     */       }
/*     */     }
/* 117 */     else if (entity instanceof com.mysql.cj.protocol.x.StatementExecuteOk) {
/* 118 */       return true;
/*     */     } 
/* 120 */     this.prevEntity = entity;
/* 121 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   public SqlResult build() {
/* 126 */     return this.isRowResult ? new SqlMultiResult(() -> (this.resultSets.size() > 0) ? this.resultSets.remove(0) : null) : new SqlUpdateResult(this.statementExecuteOkBuilder
/*     */         
/* 128 */         .build());
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\xdevapi\SqlResultBuilder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */