/*     */ package com.mysql.cj;
/*     */ 
/*     */ import com.mysql.cj.protocol.ColumnDefinition;
/*     */ import com.mysql.cj.protocol.ProtocolEntityFactory;
/*     */ import com.mysql.cj.protocol.a.NativePacketPayload;
/*     */ import com.mysql.cj.util.TestUtils;
/*     */ import java.io.IOException;
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
/*     */ public class ServerPreparedQueryTestcaseGenerator
/*     */   extends ServerPreparedQuery
/*     */ {
/*     */   public ServerPreparedQueryTestcaseGenerator(NativeSession sess) {
/*  45 */     super(sess);
/*     */   }
/*     */ 
/*     */   
/*     */   public void closeQuery() {
/*  50 */     dumpCloseForTestcase();
/*  51 */     super.closeQuery();
/*     */   }
/*     */   
/*     */   private void dumpCloseForTestcase() {
/*  55 */     StringBuilder buf = new StringBuilder();
/*  56 */     this.session.getProtocol().generateQueryCommentBlock(buf);
/*  57 */     buf.append("DEALLOCATE PREPARE debug_stmt_");
/*  58 */     buf.append(this.statementId);
/*  59 */     buf.append(";\n");
/*     */     
/*  61 */     TestUtils.dumpTestcaseQuery(buf.toString());
/*     */   }
/*     */ 
/*     */   
/*     */   public void serverPrepare(String sql) throws IOException {
/*  66 */     dumpPrepareForTestcase();
/*  67 */     super.serverPrepare(sql);
/*     */   }
/*     */   
/*     */   private void dumpPrepareForTestcase() {
/*  71 */     StringBuilder buf = new StringBuilder(getOriginalSql().length() + 64);
/*     */     
/*  73 */     this.session.getProtocol().generateQueryCommentBlock(buf);
/*     */     
/*  75 */     buf.append("PREPARE debug_stmt_");
/*  76 */     buf.append(this.statementId);
/*  77 */     buf.append(" FROM \"");
/*  78 */     buf.append(getOriginalSql());
/*  79 */     buf.append("\";\n");
/*     */     
/*  81 */     TestUtils.dumpTestcaseQuery(buf.toString());
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public <T extends com.mysql.cj.protocol.Resultset> T serverExecute(int maxRowsToRetrieve, boolean createStreamingResultSet, ColumnDefinition metadata, ProtocolEntityFactory<T, NativePacketPayload> resultSetFactory) {
/*  87 */     dumpExecuteForTestcase();
/*  88 */     return super.serverExecute(maxRowsToRetrieve, createStreamingResultSet, metadata, resultSetFactory);
/*     */   }
/*     */   
/*     */   private void dumpExecuteForTestcase() {
/*  92 */     StringBuilder buf = new StringBuilder();
/*     */     int i;
/*  94 */     for (i = 0; i < getParameterCount(); i++) {
/*  95 */       this.session.getProtocol().generateQueryCommentBlock(buf);
/*     */       
/*  97 */       buf.append("SET @debug_stmt_param");
/*  98 */       buf.append(this.statementId);
/*  99 */       buf.append("_");
/* 100 */       buf.append(i);
/* 101 */       buf.append("=");
/*     */       
/* 103 */       ServerPreparedQueryBindValue bv = this.queryBindings.getBindValues()[i];
/* 104 */       buf.append(bv.isNull() ? "NULL" : bv.toString(true));
/*     */       
/* 106 */       buf.append(";\n");
/*     */     } 
/*     */     
/* 109 */     this.session.getProtocol().generateQueryCommentBlock(buf);
/*     */     
/* 111 */     buf.append("EXECUTE debug_stmt_");
/* 112 */     buf.append(this.statementId);
/*     */     
/* 114 */     if (getParameterCount() > 0) {
/* 115 */       buf.append(" USING ");
/* 116 */       for (i = 0; i < getParameterCount(); i++) {
/* 117 */         if (i > 0) {
/* 118 */           buf.append(", ");
/*     */         }
/*     */         
/* 121 */         buf.append("@debug_stmt_param");
/* 122 */         buf.append(this.statementId);
/* 123 */         buf.append("_");
/* 124 */         buf.append(i);
/*     */       } 
/*     */     } 
/*     */ 
/*     */     
/* 129 */     buf.append(";\n");
/*     */     
/* 131 */     TestUtils.dumpTestcaseQuery(buf.toString());
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\ServerPreparedQueryTestcaseGenerator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */