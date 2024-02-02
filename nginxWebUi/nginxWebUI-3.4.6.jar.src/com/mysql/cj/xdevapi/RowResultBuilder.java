/*     */ package com.mysql.cj.xdevapi;
/*     */ 
/*     */ import com.mysql.cj.MysqlxSession;
/*     */ import com.mysql.cj.conf.PropertySet;
/*     */ import com.mysql.cj.exceptions.ExceptionFactory;
/*     */ import com.mysql.cj.exceptions.WrongArgumentException;
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
/*     */ public class RowResultBuilder
/*     */   implements ResultBuilder<RowResult>
/*     */ {
/*  56 */   private ArrayList<Field> fields = new ArrayList<>();
/*     */   private ColumnDefinition metadata;
/*  58 */   private List<Row> rows = new ArrayList<>();
/*     */   
/*     */   private RowResult result;
/*     */   TimeZone defaultTimeZone;
/*     */   PropertySet pset;
/*  63 */   private StatementExecuteOkBuilder statementExecuteOkBuilder = new StatementExecuteOkBuilder();
/*     */   
/*     */   public RowResultBuilder(MysqlxSession sess) {
/*  66 */     this.defaultTimeZone = sess.getServerSession().getDefaultTimeZone();
/*  67 */     this.pset = sess.getPropertySet();
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean addProtocolEntity(ProtocolEntity entity) {
/*  72 */     if (entity instanceof Field) {
/*  73 */       this.fields.add((Field)entity);
/*  74 */       return false;
/*     */     } 
/*  76 */     if (entity instanceof Row) {
/*  77 */       if (this.metadata == null) {
/*  78 */         this.metadata = (ColumnDefinition)new DefaultColumnDefinition(this.fields.<Field>toArray(new Field[0]));
/*     */       }
/*  80 */       this.rows.add(((Row)entity).setMetadata(this.metadata));
/*  81 */       return false;
/*     */     } 
/*  83 */     if (entity instanceof com.mysql.cj.protocol.x.Notice) {
/*  84 */       this.statementExecuteOkBuilder.addProtocolEntity(entity);
/*  85 */       return false;
/*     */     } 
/*  87 */     if (entity instanceof com.mysql.cj.protocol.x.FetchDoneEntity) {
/*  88 */       return false;
/*     */     }
/*  90 */     if (entity instanceof com.mysql.cj.protocol.x.StatementExecuteOk) {
/*  91 */       return true;
/*     */     }
/*  93 */     throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, "Unexpected protocol entity " + entity);
/*     */   }
/*     */ 
/*     */   
/*     */   public RowResult build() {
/*  98 */     if (this.metadata == null) {
/*  99 */       this.metadata = (ColumnDefinition)new DefaultColumnDefinition(this.fields.<Field>toArray(new Field[0]));
/*     */     }
/* 101 */     this.result = new RowResultImpl(this.metadata, this.defaultTimeZone, (RowList)new BufferedRowList(this.rows), () -> this.statementExecuteOkBuilder.build(), this.pset);
/*     */     
/* 103 */     return this.result;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\xdevapi\RowResultBuilder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */