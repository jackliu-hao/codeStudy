/*     */ package com.mysql.cj.jdbc;
/*     */ 
/*     */ import com.mysql.cj.Constants;
/*     */ import com.mysql.cj.Messages;
/*     */ import com.mysql.cj.exceptions.CJException;
/*     */ import com.mysql.cj.exceptions.ExceptionInterceptor;
/*     */ import com.mysql.cj.jdbc.exceptions.SQLError;
/*     */ import com.mysql.cj.jdbc.exceptions.SQLExceptionsMapping;
/*     */ import com.mysql.cj.jdbc.result.ResultSetInternalMethods;
/*     */ import com.mysql.cj.protocol.OutputStreamWatcher;
/*     */ import com.mysql.cj.protocol.WatchableOutputStream;
/*     */ import com.mysql.cj.protocol.WatchableStream;
/*     */ import java.io.ByteArrayInputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
/*     */ import java.sql.Blob;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class Blob
/*     */   implements Blob, OutputStreamWatcher
/*     */ {
/*  64 */   private byte[] binaryData = null;
/*     */ 
/*     */   
/*     */   private boolean isClosed = false;
/*     */ 
/*     */   
/*     */   private ExceptionInterceptor exceptionInterceptor;
/*     */ 
/*     */ 
/*     */   
/*     */   Blob(ExceptionInterceptor exceptionInterceptor) {
/*  75 */     setBinaryData(Constants.EMPTY_BYTE_ARRAY);
/*  76 */     this.exceptionInterceptor = exceptionInterceptor;
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
/*     */   public Blob(byte[] data, ExceptionInterceptor exceptionInterceptor) {
/*  88 */     setBinaryData(data);
/*  89 */     this.exceptionInterceptor = exceptionInterceptor;
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
/*     */   Blob(byte[] data, ResultSetInternalMethods creatorResultSetToSet, int columnIndexToSet) {
/* 103 */     setBinaryData(data);
/*     */   }
/*     */   
/*     */   private synchronized byte[] getBinaryData() {
/* 107 */     return this.binaryData;
/*     */   }
/*     */   
/*     */   public synchronized InputStream getBinaryStream() throws SQLException {
/*     */     
/* 112 */     try { checkClosed();
/*     */       
/* 114 */       return new ByteArrayInputStream(getBinaryData()); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, this.exceptionInterceptor); }
/*     */   
/*     */   }
/*     */   public synchronized byte[] getBytes(long pos, int length) throws SQLException {
/*     */     
/* 119 */     try { checkClosed();
/*     */       
/* 121 */       if (pos < 1L) {
/* 122 */         throw SQLError.createSQLException(Messages.getString("Blob.2"), "S1009", this.exceptionInterceptor);
/*     */       }
/*     */       
/* 125 */       pos--;
/*     */       
/* 127 */       if (pos > this.binaryData.length) {
/* 128 */         throw SQLError.createSQLException(Messages.getString("Blob.3"), "S1009", this.exceptionInterceptor);
/*     */       }
/*     */       
/* 131 */       if (pos + length > this.binaryData.length) {
/* 132 */         throw SQLError.createSQLException(Messages.getString("Blob.4"), "S1009", this.exceptionInterceptor);
/*     */       }
/*     */       
/* 135 */       byte[] newData = new byte[length];
/* 136 */       System.arraycopy(getBinaryData(), (int)pos, newData, 0, length);
/*     */       
/* 138 */       return newData; } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, this.exceptionInterceptor); }
/*     */   
/*     */   }
/*     */   public synchronized long length() throws SQLException {
/*     */     
/* 143 */     try { checkClosed();
/*     */       
/* 145 */       return (getBinaryData()).length; } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, this.exceptionInterceptor); }
/*     */   
/*     */   }
/*     */   public synchronized long position(byte[] pattern, long start) throws SQLException {
/*     */     
/* 150 */     try { throw SQLError.createSQLFeatureNotSupportedException(); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, this.exceptionInterceptor); }
/*     */   
/*     */   }
/*     */   public synchronized long position(Blob pattern, long start) throws SQLException {
/*     */     
/* 155 */     try { checkClosed();
/*     */       
/* 157 */       return position(pattern.getBytes(0L, (int)pattern.length()), start); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, this.exceptionInterceptor); }
/*     */   
/*     */   }
/*     */   private synchronized void setBinaryData(byte[] newBinaryData) {
/* 161 */     this.binaryData = newBinaryData;
/*     */   }
/*     */   
/*     */   public synchronized OutputStream setBinaryStream(long indexToWriteAt) throws SQLException {
/*     */     
/* 166 */     try { checkClosed();
/*     */       
/* 168 */       if (indexToWriteAt < 1L) {
/* 169 */         throw SQLError.createSQLException(Messages.getString("Blob.0"), "S1009", this.exceptionInterceptor);
/*     */       }
/*     */       
/* 172 */       WatchableOutputStream bytesOut = new WatchableOutputStream();
/* 173 */       bytesOut.setWatcher(this);
/*     */       
/* 175 */       if (indexToWriteAt > 0L) {
/* 176 */         bytesOut.write(this.binaryData, 0, (int)(indexToWriteAt - 1L));
/*     */       }
/*     */       
/* 179 */       return (OutputStream)bytesOut; } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, this.exceptionInterceptor); }
/*     */   
/*     */   }
/*     */   public synchronized int setBytes(long writeAt, byte[] bytes) throws SQLException {
/*     */     
/* 184 */     try { checkClosed();
/*     */       
/* 186 */       return setBytes(writeAt, bytes, 0, bytes.length); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, this.exceptionInterceptor); }
/*     */   
/*     */   }
/*     */   public synchronized int setBytes(long writeAt, byte[] bytes, int offset, int length) throws SQLException {
/*     */     
/* 191 */     try { checkClosed();
/*     */       
/* 193 */       OutputStream bytesOut = setBinaryStream(writeAt);
/*     */       
/*     */       try {
/* 196 */         bytesOut.write(bytes, offset, length);
/* 197 */       } catch (IOException ioEx) {
/* 198 */         SQLException sqlEx = SQLError.createSQLException(Messages.getString("Blob.1"), "S1000", this.exceptionInterceptor);
/*     */         
/* 200 */         sqlEx.initCause(ioEx);
/*     */         
/* 202 */         throw sqlEx;
/*     */       } finally {
/*     */         try {
/* 205 */           bytesOut.close();
/* 206 */         } catch (IOException iOException) {}
/*     */       } 
/*     */ 
/*     */ 
/*     */       
/* 211 */       return length; } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, this.exceptionInterceptor); }
/*     */   
/*     */   }
/*     */   public synchronized void streamClosed(byte[] byteData) {
/* 215 */     this.binaryData = byteData;
/*     */   }
/*     */ 
/*     */   
/*     */   public synchronized void streamClosed(WatchableStream out) {
/* 220 */     int streamSize = out.size();
/*     */     
/* 222 */     if (streamSize < this.binaryData.length) {
/* 223 */       out.write(this.binaryData, streamSize, this.binaryData.length - streamSize);
/*     */     }
/*     */     
/* 226 */     this.binaryData = out.toByteArray();
/*     */   }
/*     */   
/*     */   public synchronized void truncate(long len) throws SQLException {
/*     */     
/* 231 */     try { checkClosed();
/*     */       
/* 233 */       if (len < 0L) {
/* 234 */         throw SQLError.createSQLException(Messages.getString("Blob.5"), "S1009", this.exceptionInterceptor);
/*     */       }
/*     */       
/* 237 */       if (len > this.binaryData.length) {
/* 238 */         throw SQLError.createSQLException(Messages.getString("Blob.6"), "S1009", this.exceptionInterceptor);
/*     */       }
/*     */ 
/*     */ 
/*     */       
/* 243 */       byte[] newData = new byte[(int)len];
/* 244 */       System.arraycopy(getBinaryData(), 0, newData, 0, (int)len);
/* 245 */       this.binaryData = newData; return; }
/* 246 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, this.exceptionInterceptor); }
/*     */   
/*     */   } public synchronized void free() throws SQLException {
/*     */     
/* 250 */     try { this.binaryData = null;
/* 251 */       this.isClosed = true; return; }
/* 252 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, this.exceptionInterceptor); }
/*     */   
/*     */   } public synchronized InputStream getBinaryStream(long pos, long length) throws SQLException {
/*     */     
/* 256 */     try { checkClosed();
/*     */       
/* 258 */       if (pos < 1L) {
/* 259 */         throw SQLError.createSQLException(Messages.getString("Blob.2"), "S1009", this.exceptionInterceptor);
/*     */       }
/*     */       
/* 262 */       pos--;
/*     */       
/* 264 */       if (pos > this.binaryData.length) {
/* 265 */         throw SQLError.createSQLException(Messages.getString("Blob.6"), "S1009", this.exceptionInterceptor);
/*     */       }
/*     */       
/* 268 */       if (pos + length > this.binaryData.length) {
/* 269 */         throw SQLError.createSQLException(Messages.getString("Blob.4"), "S1009", this.exceptionInterceptor);
/*     */       }
/*     */       
/* 272 */       return new ByteArrayInputStream(getBinaryData(), (int)pos, (int)length); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, this.exceptionInterceptor); }
/*     */   
/*     */   }
/*     */   private synchronized void checkClosed() throws SQLException {
/* 276 */     if (this.isClosed)
/* 277 */       throw SQLError.createSQLException(Messages.getString("Blob.7"), "S1009", this.exceptionInterceptor); 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\jdbc\Blob.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */