/*     */ package com.mysql.cj.jdbc;
/*     */ 
/*     */ import com.mysql.cj.Messages;
/*     */ import com.mysql.cj.exceptions.CJException;
/*     */ import com.mysql.cj.jdbc.exceptions.SQLExceptionsMapping;
/*     */ import com.mysql.cj.log.Log;
/*     */ import com.mysql.cj.util.StringUtils;
/*     */ import java.sql.Connection;
/*     */ import java.sql.ResultSet;
/*     */ import java.sql.SQLException;
/*     */ import java.sql.Statement;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import javax.sql.XAConnection;
/*     */ import javax.transaction.xa.XAException;
/*     */ import javax.transaction.xa.XAResource;
/*     */ import javax.transaction.xa.Xid;
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
/*     */ public class MysqlXAConnection
/*     */   extends MysqlPooledConnection
/*     */   implements XAConnection, XAResource
/*     */ {
/*     */   private static final int MAX_COMMAND_LENGTH = 300;
/*     */   private JdbcConnection underlyingConnection;
/*     */   private static final Map<Integer, Integer> MYSQL_ERROR_CODES_TO_XA_ERROR_CODES;
/*     */   private Log log;
/*     */   protected boolean logXaCommands;
/*     */   
/*     */   static {
/*  64 */     HashMap<Integer, Integer> temp = new HashMap<>();
/*     */     
/*  66 */     temp.put(Integer.valueOf(1397), Integer.valueOf(-4));
/*  67 */     temp.put(Integer.valueOf(1398), Integer.valueOf(-5));
/*  68 */     temp.put(Integer.valueOf(1399), Integer.valueOf(-7));
/*  69 */     temp.put(Integer.valueOf(1400), Integer.valueOf(-9));
/*  70 */     temp.put(Integer.valueOf(1401), Integer.valueOf(-3));
/*  71 */     temp.put(Integer.valueOf(1402), Integer.valueOf(100));
/*  72 */     temp.put(Integer.valueOf(1440), Integer.valueOf(-8));
/*  73 */     temp.put(Integer.valueOf(1613), Integer.valueOf(106));
/*  74 */     temp.put(Integer.valueOf(1614), Integer.valueOf(102));
/*     */     
/*  76 */     MYSQL_ERROR_CODES_TO_XA_ERROR_CODES = Collections.unmodifiableMap(temp);
/*     */   }
/*     */   
/*     */   protected static MysqlXAConnection getInstance(JdbcConnection mysqlConnection, boolean logXaCommands) throws SQLException {
/*  80 */     return new MysqlXAConnection(mysqlConnection, logXaCommands);
/*     */   }
/*     */   
/*     */   public MysqlXAConnection(JdbcConnection connection, boolean logXaCommands) {
/*  84 */     super(connection);
/*  85 */     this.underlyingConnection = connection;
/*  86 */     this.log = connection.getSession().getLog();
/*  87 */     this.logXaCommands = logXaCommands;
/*     */   }
/*     */   
/*     */   public XAResource getXAResource() throws SQLException {
/*     */     
/*  92 */     try { return this; } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException); }
/*     */   
/*     */   }
/*     */   
/*     */   public int getTransactionTimeout() throws XAException {
/*  97 */     return 0;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean setTransactionTimeout(int arg0) throws XAException {
/* 102 */     return false;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isSameRM(XAResource xares) throws XAException {
/* 108 */     if (xares instanceof MysqlXAConnection) {
/* 109 */       return this.underlyingConnection.isSameResource(((MysqlXAConnection)xares).underlyingConnection);
/*     */     }
/*     */     
/* 112 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   public Xid[] recover(int flag) throws XAException {
/* 117 */     return recover(this.underlyingConnection, flag);
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected static Xid[] recover(Connection c, int flag) throws XAException {
/* 142 */     boolean startRscan = ((flag & 0x1000000) > 0);
/* 143 */     boolean endRscan = ((flag & 0x800000) > 0);
/*     */     
/* 145 */     if (!startRscan && !endRscan && flag != 0) {
/* 146 */       throw new MysqlXAException(-5, Messages.getString("MysqlXAConnection.001"), null);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 155 */     if (!startRscan) {
/* 156 */       return new Xid[0];
/*     */     }
/*     */     
/* 159 */     ResultSet rs = null;
/* 160 */     Statement stmt = null;
/*     */     
/* 162 */     List<MysqlXid> recoveredXidList = new ArrayList<>();
/*     */ 
/*     */     
/*     */     try {
/* 166 */       stmt = c.createStatement();
/*     */       
/* 168 */       rs = stmt.executeQuery("XA RECOVER");
/*     */       
/* 170 */       while (rs.next()) {
/* 171 */         int formatId = rs.getInt(1);
/* 172 */         int gtridLength = rs.getInt(2);
/* 173 */         int bqualLength = rs.getInt(3);
/* 174 */         byte[] gtridAndBqual = rs.getBytes(4);
/*     */         
/* 176 */         byte[] gtrid = new byte[gtridLength];
/* 177 */         byte[] bqual = new byte[bqualLength];
/*     */         
/* 179 */         if (gtridAndBqual.length != gtridLength + bqualLength) {
/* 180 */           throw new MysqlXAException(105, Messages.getString("MysqlXAConnection.002"), null);
/*     */         }
/*     */         
/* 183 */         System.arraycopy(gtridAndBqual, 0, gtrid, 0, gtridLength);
/* 184 */         System.arraycopy(gtridAndBqual, gtridLength, bqual, 0, bqualLength);
/*     */         
/* 186 */         recoveredXidList.add(new MysqlXid(gtrid, bqual, formatId));
/*     */       } 
/* 188 */     } catch (SQLException sqlEx) {
/* 189 */       throw mapXAExceptionFromSQLException(sqlEx);
/*     */     } finally {
/* 191 */       if (rs != null) {
/*     */         try {
/* 193 */           rs.close();
/* 194 */         } catch (SQLException sqlEx) {
/* 195 */           throw mapXAExceptionFromSQLException(sqlEx);
/*     */         } 
/*     */       }
/*     */       
/* 199 */       if (stmt != null) {
/*     */         try {
/* 201 */           stmt.close();
/* 202 */         } catch (SQLException sqlEx) {
/* 203 */           throw mapXAExceptionFromSQLException(sqlEx);
/*     */         } 
/*     */       }
/*     */     } 
/*     */     
/* 208 */     int numXids = recoveredXidList.size();
/*     */     
/* 210 */     Xid[] asXids = new Xid[numXids];
/* 211 */     Object[] asObjects = recoveredXidList.toArray();
/*     */     
/* 213 */     for (int i = 0; i < numXids; i++) {
/* 214 */       asXids[i] = (Xid)asObjects[i];
/*     */     }
/*     */     
/* 217 */     return asXids;
/*     */   }
/*     */ 
/*     */   
/*     */   public int prepare(Xid xid) throws XAException {
/* 222 */     StringBuilder commandBuf = new StringBuilder(300);
/* 223 */     commandBuf.append("XA PREPARE ");
/* 224 */     appendXid(commandBuf, xid);
/*     */     
/* 226 */     dispatchCommand(commandBuf.toString());
/*     */     
/* 228 */     return 0;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void forget(Xid xid) throws XAException {}
/*     */ 
/*     */ 
/*     */   
/*     */   public void rollback(Xid xid) throws XAException {
/* 238 */     StringBuilder commandBuf = new StringBuilder(300);
/* 239 */     commandBuf.append("XA ROLLBACK ");
/* 240 */     appendXid(commandBuf, xid);
/*     */     
/*     */     try {
/* 243 */       dispatchCommand(commandBuf.toString());
/*     */     } finally {
/* 245 */       this.underlyingConnection.setInGlobalTx(false);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void end(Xid xid, int flags) throws XAException {
/* 251 */     StringBuilder commandBuf = new StringBuilder(300);
/* 252 */     commandBuf.append("XA END ");
/* 253 */     appendXid(commandBuf, xid);
/*     */     
/* 255 */     switch (flags) {
/*     */       case 67108864:
/*     */         break;
/*     */       case 33554432:
/* 259 */         commandBuf.append(" SUSPEND");
/*     */         break;
/*     */       case 536870912:
/*     */         break;
/*     */       default:
/* 264 */         throw new XAException(-5);
/*     */     } 
/*     */     
/* 267 */     dispatchCommand(commandBuf.toString());
/*     */   }
/*     */ 
/*     */   
/*     */   public void start(Xid xid, int flags) throws XAException {
/* 272 */     StringBuilder commandBuf = new StringBuilder(300);
/* 273 */     commandBuf.append("XA START ");
/* 274 */     appendXid(commandBuf, xid);
/*     */     
/* 276 */     switch (flags) {
/*     */       case 2097152:
/* 278 */         commandBuf.append(" JOIN");
/*     */         break;
/*     */       case 134217728:
/* 281 */         commandBuf.append(" RESUME");
/*     */         break;
/*     */       
/*     */       case 0:
/*     */         break;
/*     */       default:
/* 287 */         throw new XAException(-5);
/*     */     } 
/*     */     
/* 290 */     dispatchCommand(commandBuf.toString());
/*     */     
/* 292 */     this.underlyingConnection.setInGlobalTx(true);
/*     */   }
/*     */ 
/*     */   
/*     */   public void commit(Xid xid, boolean onePhase) throws XAException {
/* 297 */     StringBuilder commandBuf = new StringBuilder(300);
/* 298 */     commandBuf.append("XA COMMIT ");
/* 299 */     appendXid(commandBuf, xid);
/*     */     
/* 301 */     if (onePhase) {
/* 302 */       commandBuf.append(" ONE PHASE");
/*     */     }
/*     */     
/*     */     try {
/* 306 */       dispatchCommand(commandBuf.toString());
/*     */     } finally {
/* 308 */       this.underlyingConnection.setInGlobalTx(false);
/*     */     } 
/*     */   }
/*     */   
/*     */   private ResultSet dispatchCommand(String command) throws XAException {
/* 313 */     Statement stmt = null;
/*     */     
/*     */     try {
/* 316 */       if (this.logXaCommands) {
/* 317 */         this.log.logDebug("Executing XA statement: " + command);
/*     */       }
/*     */ 
/*     */       
/* 321 */       stmt = this.underlyingConnection.createStatement();
/*     */       
/* 323 */       stmt.execute(command);
/*     */       
/* 325 */       ResultSet rs = stmt.getResultSet();
/*     */       
/* 327 */       return rs;
/* 328 */     } catch (SQLException sqlEx) {
/* 329 */       throw mapXAExceptionFromSQLException(sqlEx);
/*     */     } finally {
/* 331 */       if (stmt != null) {
/*     */         try {
/* 333 */           stmt.close();
/* 334 */         } catch (SQLException sQLException) {}
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   protected static XAException mapXAExceptionFromSQLException(SQLException sqlEx) {
/* 341 */     Integer xaCode = MYSQL_ERROR_CODES_TO_XA_ERROR_CODES.get(Integer.valueOf(sqlEx.getErrorCode()));
/*     */     
/* 343 */     if (xaCode != null) {
/* 344 */       return (XAException)(new MysqlXAException(xaCode.intValue(), sqlEx.getMessage(), null)).initCause(sqlEx);
/*     */     }
/*     */     
/* 347 */     return (XAException)(new MysqlXAException(-7, Messages.getString("MysqlXAConnection.003"), null)).initCause(sqlEx);
/*     */   }
/*     */   
/*     */   private static void appendXid(StringBuilder builder, Xid xid) {
/* 351 */     byte[] gtrid = xid.getGlobalTransactionId();
/* 352 */     byte[] btrid = xid.getBranchQualifier();
/*     */     
/* 354 */     if (gtrid != null) {
/* 355 */       StringUtils.appendAsHex(builder, gtrid);
/*     */     }
/*     */     
/* 358 */     builder.append(',');
/* 359 */     if (btrid != null) {
/* 360 */       StringUtils.appendAsHex(builder, btrid);
/*     */     }
/*     */     
/* 363 */     builder.append(',');
/* 364 */     StringUtils.appendAsHex(builder, xid.getFormatId());
/*     */   }
/*     */   
/*     */   public synchronized Connection getConnection() throws SQLException {
/*     */     
/* 369 */     try { Connection connToWrap = getConnection(false, true);
/*     */       
/* 371 */       return connToWrap; } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException); }
/*     */   
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\jdbc\MysqlXAConnection.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */