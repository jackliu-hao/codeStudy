/*     */ package com.mysql.cj.jdbc.result;
/*     */ 
/*     */ import com.mysql.cj.exceptions.ExceptionFactory;
/*     */ import com.mysql.cj.exceptions.WrongArgumentException;
/*     */ import com.mysql.cj.jdbc.JdbcConnection;
/*     */ import com.mysql.cj.jdbc.StatementImpl;
/*     */ import com.mysql.cj.protocol.ProtocolEntity;
/*     */ import com.mysql.cj.protocol.ProtocolEntityFactory;
/*     */ import com.mysql.cj.protocol.Resultset;
/*     */ import com.mysql.cj.protocol.ResultsetRows;
/*     */ import com.mysql.cj.protocol.a.NativePacketPayload;
/*     */ import com.mysql.cj.protocol.a.result.OkPacket;
/*     */ import java.sql.SQLException;
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
/*     */ public class ResultSetFactory
/*     */   implements ProtocolEntityFactory<ResultSetImpl, NativePacketPayload>
/*     */ {
/*     */   private JdbcConnection conn;
/*     */   private StatementImpl stmt;
/*  53 */   private Resultset.Type type = Resultset.Type.FORWARD_ONLY;
/*  54 */   private Resultset.Concurrency concurrency = Resultset.Concurrency.READ_ONLY;
/*     */   
/*     */   public ResultSetFactory(JdbcConnection connection, StatementImpl creatorStmt) throws SQLException {
/*  57 */     this.conn = connection;
/*  58 */     this.stmt = creatorStmt;
/*     */     
/*  60 */     if (creatorStmt != null) {
/*  61 */       this.type = Resultset.Type.fromValue(creatorStmt.getResultSetType(), Resultset.Type.FORWARD_ONLY);
/*  62 */       this.concurrency = Resultset.Concurrency.fromValue(creatorStmt.getResultSetConcurrency(), Resultset.Concurrency.READ_ONLY);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public Resultset.Type getResultSetType() {
/*  68 */     return this.type;
/*     */   }
/*     */ 
/*     */   
/*     */   public Resultset.Concurrency getResultSetConcurrency() {
/*  73 */     return this.concurrency;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getFetchSize() {
/*     */     try {
/*  79 */       return this.stmt.getFetchSize();
/*  80 */     } catch (SQLException ex) {
/*  81 */       throw ExceptionFactory.createException(ex.getMessage(), ex);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public ResultSetImpl createFromProtocolEntity(ProtocolEntity protocolEntity) {
/*     */     try {
/*  88 */       if (protocolEntity instanceof OkPacket) {
/*  89 */         return new ResultSetImpl((OkPacket)protocolEntity, this.conn, this.stmt);
/*     */       }
/*  91 */       if (protocolEntity instanceof ResultsetRows) {
/*  92 */         int resultSetConcurrency = getResultSetConcurrency().getIntValue();
/*  93 */         int resultSetType = getResultSetType().getIntValue();
/*     */         
/*  95 */         return createFromResultsetRows(resultSetConcurrency, resultSetType, (ResultsetRows)protocolEntity);
/*     */       } 
/*     */       
/*  98 */       throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, "Unknown ProtocolEntity class " + protocolEntity);
/*     */     }
/* 100 */     catch (SQLException ex) {
/* 101 */       throw ExceptionFactory.createException(ex.getMessage(), ex);
/*     */     } 
/*     */   }
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
/*     */   public ResultSetImpl createFromResultsetRows(int resultSetConcurrency, int resultSetType, ResultsetRows rows) throws SQLException {
/*     */     ResultSetImpl rs;
/* 122 */     StatementImpl st = this.stmt;
/*     */     
/* 124 */     if (rows.getOwner() != null) {
/* 125 */       st = ((ResultSetImpl)rows.getOwner()).getOwningStatement();
/*     */     }
/*     */     
/* 128 */     switch (resultSetConcurrency) {
/*     */       case 1008:
/* 130 */         rs = new UpdatableResultSet(rows, this.conn, st);
/*     */         break;
/*     */ 
/*     */       
/*     */       default:
/* 135 */         rs = new ResultSetImpl(rows, this.conn, st);
/*     */         break;
/*     */     } 
/*     */     
/* 139 */     rs.setResultSetType(resultSetType);
/* 140 */     rs.setResultSetConcurrency(resultSetConcurrency);
/*     */     
/* 142 */     if (rows instanceof com.mysql.cj.protocol.a.result.ResultsetRowsCursor && st != null) {
/* 143 */       rs.setFetchSize(st.getFetchSize());
/*     */     }
/* 145 */     return rs;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\jdbc\result\ResultSetFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */