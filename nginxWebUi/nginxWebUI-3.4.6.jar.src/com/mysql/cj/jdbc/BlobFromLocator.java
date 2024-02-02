/*     */ package com.mysql.cj.jdbc;
/*     */ 
/*     */ import com.mysql.cj.Messages;
/*     */ import com.mysql.cj.conf.PropertyKey;
/*     */ import com.mysql.cj.exceptions.CJException;
/*     */ import com.mysql.cj.exceptions.ExceptionInterceptor;
/*     */ import com.mysql.cj.jdbc.exceptions.SQLError;
/*     */ import com.mysql.cj.jdbc.exceptions.SQLExceptionsMapping;
/*     */ import com.mysql.cj.jdbc.result.ResultSetImpl;
/*     */ import com.mysql.cj.result.Field;
/*     */ import java.io.BufferedInputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
/*     */ import java.sql.Blob;
/*     */ import java.sql.PreparedStatement;
/*     */ import java.sql.ResultSet;
/*     */ import java.sql.SQLException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
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
/*     */ public class BlobFromLocator
/*     */   implements Blob
/*     */ {
/*  57 */   private List<String> primaryKeyColumns = null;
/*     */   
/*  59 */   private List<String> primaryKeyValues = null;
/*     */ 
/*     */   
/*     */   private ResultSetImpl creatorResultSet;
/*     */   
/*  64 */   private String blobColumnName = null;
/*     */   
/*  66 */   private String tableName = null;
/*     */   
/*  68 */   private int numColsInResultSet = 0;
/*     */   
/*  70 */   private int numPrimaryKeys = 0;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private String quotedId;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private ExceptionInterceptor exceptionInterceptor;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public BlobFromLocator(ResultSetImpl creatorResultSetToSet, int blobColumnIndex, ExceptionInterceptor exceptionInterceptor) throws SQLException {
/*  89 */     this.exceptionInterceptor = exceptionInterceptor;
/*  90 */     this.creatorResultSet = creatorResultSetToSet;
/*     */     
/*  92 */     Field[] fields = this.creatorResultSet.getMetadata().getFields();
/*  93 */     this.numColsInResultSet = fields.length;
/*  94 */     this.quotedId = this.creatorResultSet.getSession().getIdentifierQuoteString();
/*     */     
/*  96 */     if (this.numColsInResultSet > 1) {
/*  97 */       this.primaryKeyColumns = new ArrayList<>();
/*  98 */       this.primaryKeyValues = new ArrayList<>();
/*     */       
/* 100 */       for (int i = 0; i < this.numColsInResultSet; i++) {
/* 101 */         if (fields[i].isPrimaryKey()) {
/* 102 */           StringBuilder keyName = new StringBuilder();
/* 103 */           keyName.append(this.quotedId);
/*     */           
/* 105 */           String originalColumnName = fields[i].getOriginalName();
/*     */           
/* 107 */           if (originalColumnName != null && originalColumnName.length() > 0) {
/* 108 */             keyName.append(originalColumnName);
/*     */           } else {
/* 110 */             keyName.append(fields[i].getName());
/*     */           } 
/*     */           
/* 113 */           keyName.append(this.quotedId);
/*     */           
/* 115 */           this.primaryKeyColumns.add(keyName.toString());
/* 116 */           this.primaryKeyValues.add(this.creatorResultSet.getString(i + 1));
/*     */         } 
/*     */       } 
/*     */     } else {
/* 120 */       notEnoughInformationInQuery();
/*     */     } 
/*     */     
/* 123 */     this.numPrimaryKeys = this.primaryKeyColumns.size();
/*     */     
/* 125 */     if (this.numPrimaryKeys == 0) {
/* 126 */       notEnoughInformationInQuery();
/*     */     }
/*     */     
/* 129 */     if (fields[0].getOriginalTableName() != null) {
/* 130 */       StringBuilder tableNameBuffer = new StringBuilder();
/*     */       
/* 132 */       String databaseName = fields[0].getDatabaseName();
/*     */       
/* 134 */       if (databaseName != null && databaseName.length() > 0) {
/* 135 */         tableNameBuffer.append(this.quotedId);
/* 136 */         tableNameBuffer.append(databaseName);
/* 137 */         tableNameBuffer.append(this.quotedId);
/* 138 */         tableNameBuffer.append('.');
/*     */       } 
/*     */       
/* 141 */       tableNameBuffer.append(this.quotedId);
/* 142 */       tableNameBuffer.append(fields[0].getOriginalTableName());
/* 143 */       tableNameBuffer.append(this.quotedId);
/*     */       
/* 145 */       this.tableName = tableNameBuffer.toString();
/*     */     } else {
/* 147 */       StringBuilder tableNameBuffer = new StringBuilder();
/*     */       
/* 149 */       tableNameBuffer.append(this.quotedId);
/* 150 */       tableNameBuffer.append(fields[0].getTableName());
/* 151 */       tableNameBuffer.append(this.quotedId);
/*     */       
/* 153 */       this.tableName = tableNameBuffer.toString();
/*     */     } 
/*     */     
/* 156 */     this.blobColumnName = this.quotedId + this.creatorResultSet.getString(blobColumnIndex) + this.quotedId;
/*     */   }
/*     */   
/*     */   private void notEnoughInformationInQuery() throws SQLException {
/* 160 */     throw SQLError.createSQLException(Messages.getString("Blob.8"), "S1000", this.exceptionInterceptor);
/*     */   }
/*     */   
/*     */   public OutputStream setBinaryStream(long indexToWriteAt) throws SQLException {
/*     */     
/* 165 */     try { throw SQLError.createSQLFeatureNotSupportedException(); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, this.exceptionInterceptor); }
/*     */   
/*     */   }
/*     */   
/*     */   public InputStream getBinaryStream() throws SQLException {
/*     */     try {
/* 171 */       return new BufferedInputStream(new LocatorInputStream(), ((Integer)this.creatorResultSet
/* 172 */           .getSession().getPropertySet().getMemorySizeProperty(PropertyKey.locatorFetchBufferSize).getValue()).intValue());
/*     */     } catch (CJException cJException) {
/*     */       throw SQLExceptionsMapping.translateException(cJException, this.exceptionInterceptor);
/*     */     }  } public int setBytes(long writeAt, byte[] bytes, int offset, int length) throws SQLException {
/*     */     
/* 177 */     try { PreparedStatement pStmt = null;
/*     */       
/* 179 */       if (offset + length > bytes.length) {
/* 180 */         length = bytes.length - offset;
/*     */       }
/*     */       
/* 183 */       byte[] bytesToWrite = new byte[length];
/* 184 */       System.arraycopy(bytes, offset, bytesToWrite, 0, length);
/*     */ 
/*     */       
/* 187 */       StringBuilder query = new StringBuilder("UPDATE ");
/* 188 */       query.append(this.tableName);
/* 189 */       query.append(" SET ");
/* 190 */       query.append(this.blobColumnName);
/* 191 */       query.append(" = INSERT(");
/* 192 */       query.append(this.blobColumnName);
/* 193 */       query.append(", ");
/* 194 */       query.append(writeAt);
/* 195 */       query.append(", ");
/* 196 */       query.append(length);
/* 197 */       query.append(", ?) WHERE ");
/*     */       
/* 199 */       query.append(this.primaryKeyColumns.get(0));
/* 200 */       query.append(" = ?");
/*     */       int i;
/* 202 */       for (i = 1; i < this.numPrimaryKeys; i++) {
/* 203 */         query.append(" AND ");
/* 204 */         query.append(this.primaryKeyColumns.get(i));
/* 205 */         query.append(" = ?");
/*     */       } 
/*     */ 
/*     */       
/*     */       try {
/* 210 */         pStmt = this.creatorResultSet.getConnection().prepareStatement(query.toString());
/*     */         
/* 212 */         pStmt.setBytes(1, bytesToWrite);
/*     */         
/* 214 */         for (i = 0; i < this.numPrimaryKeys; i++) {
/* 215 */           pStmt.setString(i + 2, this.primaryKeyValues.get(i));
/*     */         }
/*     */         
/* 218 */         int rowsUpdated = pStmt.executeUpdate();
/*     */         
/* 220 */         if (rowsUpdated != 1) {
/* 221 */           throw SQLError.createSQLException(Messages.getString("Blob.9"), "S1000", this.exceptionInterceptor);
/*     */         }
/*     */       } finally {
/* 224 */         if (pStmt != null) {
/*     */           try {
/* 226 */             pStmt.close();
/* 227 */           } catch (SQLException sQLException) {}
/*     */ 
/*     */ 
/*     */           
/* 231 */           pStmt = null;
/*     */         } 
/*     */       } 
/*     */       
/* 235 */       return (int)length(); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, this.exceptionInterceptor); }
/*     */   
/*     */   }
/*     */   public int setBytes(long writeAt, byte[] bytes) throws SQLException {
/*     */     
/* 240 */     try { return setBytes(writeAt, bytes, 0, bytes.length); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, this.exceptionInterceptor); }
/*     */   
/*     */   }
/*     */   public byte[] getBytes(long pos, int length) throws SQLException {
/*     */     
/* 245 */     try { PreparedStatement pStmt = null;
/*     */ 
/*     */       
/*     */       try {
/* 249 */         pStmt = createGetBytesStatement();
/*     */         
/* 251 */         return getBytesInternal(pStmt, pos, length);
/*     */       } finally {
/* 253 */         if (pStmt != null)
/*     */         { try {
/* 255 */             pStmt.close();
/* 256 */           } catch (SQLException sQLException) {}
/*     */ 
/*     */ 
/*     */           
/* 260 */           pStmt = null; } 
/*     */       }  }
/* 262 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, this.exceptionInterceptor); }
/*     */   
/*     */   }
/*     */   public long length() throws SQLException {
/*     */     
/* 267 */     try { ResultSet blobRs = null;
/* 268 */       PreparedStatement pStmt = null;
/*     */ 
/*     */       
/* 271 */       StringBuilder query = new StringBuilder("SELECT LENGTH(");
/* 272 */       query.append(this.blobColumnName);
/* 273 */       query.append(") FROM ");
/* 274 */       query.append(this.tableName);
/* 275 */       query.append(" WHERE ");
/*     */       
/* 277 */       query.append(this.primaryKeyColumns.get(0));
/* 278 */       query.append(" = ?");
/*     */       int i;
/* 280 */       for (i = 1; i < this.numPrimaryKeys; i++) {
/* 281 */         query.append(" AND ");
/* 282 */         query.append(this.primaryKeyColumns.get(i));
/* 283 */         query.append(" = ?");
/*     */       } 
/*     */ 
/*     */       
/*     */       try {
/* 288 */         pStmt = this.creatorResultSet.getConnection().prepareStatement(query.toString());
/*     */         
/* 290 */         for (i = 0; i < this.numPrimaryKeys; i++) {
/* 291 */           pStmt.setString(i + 1, this.primaryKeyValues.get(i));
/*     */         }
/*     */         
/* 294 */         blobRs = pStmt.executeQuery();
/*     */         
/* 296 */         if (blobRs.next()) {
/* 297 */           return blobRs.getLong(1);
/*     */         }
/*     */         
/* 300 */         throw SQLError.createSQLException(Messages.getString("Blob.9"), "S1000", this.exceptionInterceptor);
/*     */       } finally {
/* 302 */         if (blobRs != null) {
/*     */           try {
/* 304 */             blobRs.close();
/* 305 */           } catch (SQLException sQLException) {}
/*     */ 
/*     */ 
/*     */           
/* 309 */           blobRs = null;
/*     */         } 
/*     */         
/* 312 */         if (pStmt != null)
/*     */         { try {
/* 314 */             pStmt.close();
/* 315 */           } catch (SQLException sQLException) {}
/*     */ 
/*     */ 
/*     */           
/* 319 */           pStmt = null; } 
/*     */       }  }
/* 321 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, this.exceptionInterceptor); }
/*     */   
/*     */   }
/*     */   public long position(Blob pattern, long start) throws SQLException {
/*     */     
/* 326 */     try { return position(pattern.getBytes(0L, (int)pattern.length()), start); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, this.exceptionInterceptor); }
/*     */   
/*     */   }
/*     */   public long position(byte[] pattern, long start) throws SQLException {
/*     */     
/* 331 */     try { ResultSet blobRs = null;
/* 332 */       PreparedStatement pStmt = null;
/*     */ 
/*     */       
/* 335 */       StringBuilder query = new StringBuilder("SELECT LOCATE(");
/* 336 */       query.append("?, ");
/* 337 */       query.append(this.blobColumnName);
/* 338 */       query.append(", ");
/* 339 */       query.append(start);
/* 340 */       query.append(") FROM ");
/* 341 */       query.append(this.tableName);
/* 342 */       query.append(" WHERE ");
/*     */       
/* 344 */       query.append(this.primaryKeyColumns.get(0));
/* 345 */       query.append(" = ?");
/*     */       int i;
/* 347 */       for (i = 1; i < this.numPrimaryKeys; i++) {
/* 348 */         query.append(" AND ");
/* 349 */         query.append(this.primaryKeyColumns.get(i));
/* 350 */         query.append(" = ?");
/*     */       } 
/*     */ 
/*     */       
/*     */       try {
/* 355 */         pStmt = this.creatorResultSet.getConnection().prepareStatement(query.toString());
/* 356 */         pStmt.setBytes(1, pattern);
/*     */         
/* 358 */         for (i = 0; i < this.numPrimaryKeys; i++) {
/* 359 */           pStmt.setString(i + 2, this.primaryKeyValues.get(i));
/*     */         }
/*     */         
/* 362 */         blobRs = pStmt.executeQuery();
/*     */         
/* 364 */         if (blobRs.next()) {
/* 365 */           return blobRs.getLong(1);
/*     */         }
/*     */         
/* 368 */         throw SQLError.createSQLException(Messages.getString("Blob.9"), "S1000", this.exceptionInterceptor);
/*     */       } finally {
/* 370 */         if (blobRs != null) {
/*     */           try {
/* 372 */             blobRs.close();
/* 373 */           } catch (SQLException sQLException) {}
/*     */ 
/*     */ 
/*     */           
/* 377 */           blobRs = null;
/*     */         } 
/*     */         
/* 380 */         if (pStmt != null)
/*     */         { try {
/* 382 */             pStmt.close();
/* 383 */           } catch (SQLException sQLException) {}
/*     */ 
/*     */ 
/*     */           
/* 387 */           pStmt = null; } 
/*     */       }  }
/* 389 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, this.exceptionInterceptor); }
/*     */   
/*     */   }
/*     */   public void truncate(long length) throws SQLException {
/*     */     
/* 394 */     try { PreparedStatement pStmt = null;
/*     */ 
/*     */       
/* 397 */       StringBuilder query = new StringBuilder("UPDATE ");
/* 398 */       query.append(this.tableName);
/* 399 */       query.append(" SET ");
/* 400 */       query.append(this.blobColumnName);
/* 401 */       query.append(" = LEFT(");
/* 402 */       query.append(this.blobColumnName);
/* 403 */       query.append(", ");
/* 404 */       query.append(length);
/* 405 */       query.append(") WHERE ");
/*     */       
/* 407 */       query.append(this.primaryKeyColumns.get(0));
/* 408 */       query.append(" = ?");
/*     */       int i;
/* 410 */       for (i = 1; i < this.numPrimaryKeys; i++) {
/* 411 */         query.append(" AND ");
/* 412 */         query.append(this.primaryKeyColumns.get(i));
/* 413 */         query.append(" = ?");
/*     */       } 
/*     */ 
/*     */       
/*     */       try {
/* 418 */         pStmt = this.creatorResultSet.getConnection().prepareStatement(query.toString());
/*     */         
/* 420 */         for (i = 0; i < this.numPrimaryKeys; i++) {
/* 421 */           pStmt.setString(i + 1, this.primaryKeyValues.get(i));
/*     */         }
/*     */         
/* 424 */         int rowsUpdated = pStmt.executeUpdate();
/*     */         
/* 426 */         if (rowsUpdated != 1) {
/* 427 */           throw SQLError.createSQLException(Messages.getString("Blob.9"), "S1000", this.exceptionInterceptor);
/*     */         }
/*     */       } finally {
/* 430 */         if (pStmt != null) {
/*     */           try {
/* 432 */             pStmt.close();
/* 433 */           } catch (SQLException sQLException) {}
/*     */ 
/*     */ 
/*     */           
/* 437 */           pStmt = null;
/*     */         } 
/*     */       }  return; }
/* 440 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, this.exceptionInterceptor); }
/*     */   
/*     */   } PreparedStatement createGetBytesStatement() throws SQLException {
/* 443 */     StringBuilder query = new StringBuilder("SELECT SUBSTRING(");
/*     */     
/* 445 */     query.append(this.blobColumnName);
/* 446 */     query.append(", ");
/* 447 */     query.append("?");
/* 448 */     query.append(", ");
/* 449 */     query.append("?");
/* 450 */     query.append(") FROM ");
/* 451 */     query.append(this.tableName);
/* 452 */     query.append(" WHERE ");
/*     */     
/* 454 */     query.append(this.primaryKeyColumns.get(0));
/* 455 */     query.append(" = ?");
/*     */     
/* 457 */     for (int i = 1; i < this.numPrimaryKeys; i++) {
/* 458 */       query.append(" AND ");
/* 459 */       query.append(this.primaryKeyColumns.get(i));
/* 460 */       query.append(" = ?");
/*     */     } 
/*     */     
/* 463 */     return this.creatorResultSet.getConnection().prepareStatement(query.toString());
/*     */   }
/*     */ 
/*     */   
/*     */   byte[] getBytesInternal(PreparedStatement pStmt, long pos, int length) throws SQLException {
/* 468 */     ResultSet blobRs = null;
/*     */ 
/*     */     
/*     */     try {
/* 472 */       pStmt.setLong(1, pos);
/* 473 */       pStmt.setInt(2, length);
/*     */       
/* 475 */       for (int i = 0; i < this.numPrimaryKeys; i++) {
/* 476 */         pStmt.setString(i + 3, this.primaryKeyValues.get(i));
/*     */       }
/*     */       
/* 479 */       blobRs = pStmt.executeQuery();
/*     */       
/* 481 */       if (blobRs.next()) {
/* 482 */         return blobRs.getBytes(1);
/*     */       }
/*     */       
/* 485 */       throw SQLError.createSQLException(Messages.getString("Blob.9"), "S1000", this.exceptionInterceptor);
/*     */     } finally {
/* 487 */       if (blobRs != null) {
/*     */         try {
/* 489 */           blobRs.close();
/* 490 */         } catch (SQLException sQLException) {}
/*     */ 
/*     */ 
/*     */         
/* 494 */         blobRs = null;
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   class LocatorInputStream extends InputStream {
/* 500 */     long currentPositionInBlob = 0L;
/*     */     
/* 502 */     long length = 0L;
/*     */     
/* 504 */     PreparedStatement pStmt = null;
/*     */     
/*     */     LocatorInputStream() throws SQLException {
/* 507 */       this.length = BlobFromLocator.this.length();
/* 508 */       this.pStmt = BlobFromLocator.this.createGetBytesStatement();
/*     */     }
/*     */ 
/*     */     
/*     */     LocatorInputStream(long pos, long len) throws SQLException {
/* 513 */       this.length = pos + len;
/* 514 */       this.currentPositionInBlob = pos;
/* 515 */       long blobLength = BlobFromLocator.this.length();
/*     */       
/* 517 */       if (pos + len > blobLength) {
/* 518 */         throw SQLError.createSQLException(
/* 519 */             Messages.getString("Blob.invalidStreamLength", new Object[] { Long.valueOf(blobLength), Long.valueOf(pos), Long.valueOf(len) }), "S1009", BlobFromLocator.this
/* 520 */             .exceptionInterceptor);
/*     */       }
/*     */       
/* 523 */       if (pos < 1L) {
/* 524 */         throw SQLError.createSQLException(Messages.getString("Blob.invalidStreamPos"), "S1009", BlobFromLocator.this
/* 525 */             .exceptionInterceptor);
/*     */       }
/*     */       
/* 528 */       if (pos > blobLength) {
/* 529 */         throw SQLError.createSQLException(Messages.getString("Blob.invalidStreamPos"), "S1009", BlobFromLocator.this
/* 530 */             .exceptionInterceptor);
/*     */       }
/*     */     }
/*     */ 
/*     */     
/*     */     public int read() throws IOException {
/* 536 */       if (this.currentPositionInBlob + 1L > this.length) {
/* 537 */         return -1;
/*     */       }
/*     */       
/*     */       try {
/* 541 */         byte[] asBytes = BlobFromLocator.this.getBytesInternal(this.pStmt, this.currentPositionInBlob++ + 1L, 1);
/*     */         
/* 543 */         if (asBytes == null) {
/* 544 */           return -1;
/*     */         }
/*     */         
/* 547 */         return asBytes[0];
/* 548 */       } catch (SQLException sqlEx) {
/* 549 */         throw new IOException(sqlEx.toString());
/*     */       } 
/*     */     }
/*     */ 
/*     */     
/*     */     public int read(byte[] b, int off, int len) throws IOException {
/* 555 */       if (this.currentPositionInBlob + 1L > this.length) {
/* 556 */         return -1;
/*     */       }
/*     */       
/*     */       try {
/* 560 */         byte[] asBytes = BlobFromLocator.this.getBytesInternal(this.pStmt, this.currentPositionInBlob + 1L, len);
/*     */         
/* 562 */         if (asBytes == null) {
/* 563 */           return -1;
/*     */         }
/*     */         
/* 566 */         System.arraycopy(asBytes, 0, b, off, asBytes.length);
/*     */         
/* 568 */         this.currentPositionInBlob += asBytes.length;
/*     */         
/* 570 */         return asBytes.length;
/* 571 */       } catch (SQLException sqlEx) {
/* 572 */         throw new IOException(sqlEx.toString());
/*     */       } 
/*     */     }
/*     */ 
/*     */     
/*     */     public int read(byte[] b) throws IOException {
/* 578 */       if (this.currentPositionInBlob + 1L > this.length) {
/* 579 */         return -1;
/*     */       }
/*     */       
/*     */       try {
/* 583 */         byte[] asBytes = BlobFromLocator.this.getBytesInternal(this.pStmt, this.currentPositionInBlob + 1L, b.length);
/*     */         
/* 585 */         if (asBytes == null) {
/* 586 */           return -1;
/*     */         }
/*     */         
/* 589 */         System.arraycopy(asBytes, 0, b, 0, asBytes.length);
/*     */         
/* 591 */         this.currentPositionInBlob += asBytes.length;
/*     */         
/* 593 */         return asBytes.length;
/* 594 */       } catch (SQLException sqlEx) {
/* 595 */         throw new IOException(sqlEx.toString());
/*     */       } 
/*     */     }
/*     */ 
/*     */     
/*     */     public void close() throws IOException {
/* 601 */       if (this.pStmt != null) {
/*     */         try {
/* 603 */           this.pStmt.close();
/* 604 */         } catch (SQLException sqlEx) {
/* 605 */           throw new IOException(sqlEx.toString());
/*     */         } 
/*     */       }
/*     */       
/* 609 */       super.close();
/*     */     }
/*     */   }
/*     */   
/*     */   public void free() throws SQLException {
/*     */     
/* 615 */     try { this.creatorResultSet = null;
/* 616 */       this.primaryKeyColumns = null;
/* 617 */       this.primaryKeyValues = null; return; }
/* 618 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, this.exceptionInterceptor); }
/*     */   
/*     */   } public InputStream getBinaryStream(long pos, long length) throws SQLException {
/*     */     
/* 622 */     try { return new LocatorInputStream(pos, length); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, this.exceptionInterceptor); }
/*     */   
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\jdbc\BlobFromLocator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */