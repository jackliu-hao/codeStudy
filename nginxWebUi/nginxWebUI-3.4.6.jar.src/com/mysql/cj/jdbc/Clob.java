/*     */ package com.mysql.cj.jdbc;
/*     */ 
/*     */ import com.mysql.cj.Messages;
/*     */ import com.mysql.cj.exceptions.CJException;
/*     */ import com.mysql.cj.exceptions.ExceptionInterceptor;
/*     */ import com.mysql.cj.jdbc.exceptions.SQLError;
/*     */ import com.mysql.cj.jdbc.exceptions.SQLExceptionsMapping;
/*     */ import com.mysql.cj.protocol.OutputStreamWatcher;
/*     */ import com.mysql.cj.protocol.WatchableOutputStream;
/*     */ import com.mysql.cj.protocol.WatchableStream;
/*     */ import com.mysql.cj.protocol.WatchableWriter;
/*     */ import com.mysql.cj.protocol.WriterWatcher;
/*     */ import com.mysql.cj.util.StringUtils;
/*     */ import java.io.ByteArrayInputStream;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
/*     */ import java.io.Reader;
/*     */ import java.io.StringReader;
/*     */ import java.io.Writer;
/*     */ import java.sql.Clob;
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
/*     */ public class Clob
/*     */   implements Clob, OutputStreamWatcher, WriterWatcher
/*     */ {
/*     */   private String charData;
/*     */   private ExceptionInterceptor exceptionInterceptor;
/*     */   
/*     */   Clob(ExceptionInterceptor exceptionInterceptor) {
/*  59 */     this.charData = "";
/*  60 */     this.exceptionInterceptor = exceptionInterceptor;
/*     */   }
/*     */   
/*     */   public Clob(String charDataInit, ExceptionInterceptor exceptionInterceptor) {
/*  64 */     this.charData = charDataInit;
/*  65 */     this.exceptionInterceptor = exceptionInterceptor;
/*     */   }
/*     */   
/*     */   public InputStream getAsciiStream() throws SQLException {
/*     */     
/*  70 */     try { if (this.charData != null) {
/*  71 */         return new ByteArrayInputStream(StringUtils.getBytes(this.charData));
/*     */       }
/*     */       
/*  74 */       return null; } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, this.exceptionInterceptor); }
/*     */   
/*     */   }
/*     */   public Reader getCharacterStream() throws SQLException {
/*     */     
/*  79 */     try { if (this.charData != null) {
/*  80 */         return new StringReader(this.charData);
/*     */       }
/*     */       
/*  83 */       return null; } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, this.exceptionInterceptor); }
/*     */   
/*     */   }
/*     */   public String getSubString(long startPos, int length) throws SQLException {
/*     */     
/*  88 */     try { if (startPos < 1L) {
/*  89 */         throw SQLError.createSQLException(Messages.getString("Clob.6"), "S1009", this.exceptionInterceptor);
/*     */       }
/*     */       
/*  92 */       int adjustedStartPos = (int)startPos - 1;
/*  93 */       int adjustedEndIndex = adjustedStartPos + length;
/*     */       
/*  95 */       if (this.charData != null) {
/*  96 */         if (adjustedEndIndex > this.charData.length()) {
/*  97 */           throw SQLError.createSQLException(Messages.getString("Clob.7"), "S1009", this.exceptionInterceptor);
/*     */         }
/*     */         
/* 100 */         return this.charData.substring(adjustedStartPos, adjustedEndIndex);
/*     */       } 
/*     */       
/* 103 */       return null; } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, this.exceptionInterceptor); }
/*     */   
/*     */   }
/*     */   public long length() throws SQLException {
/*     */     
/* 108 */     try { if (this.charData != null) {
/* 109 */         return this.charData.length();
/*     */       }
/*     */       
/* 112 */       return 0L; } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, this.exceptionInterceptor); }
/*     */   
/*     */   }
/*     */   public long position(Clob arg0, long arg1) throws SQLException {
/*     */     
/* 117 */     try { return position(arg0.getSubString(1L, (int)arg0.length()), arg1); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, this.exceptionInterceptor); }
/*     */   
/*     */   }
/*     */   public long position(String stringToFind, long startPos) throws SQLException {
/*     */     
/* 122 */     try { if (startPos < 1L) {
/* 123 */         throw SQLError.createSQLException(Messages.getString("Clob.8", new Object[] { Long.valueOf(startPos) }), "S1009", this.exceptionInterceptor);
/*     */       }
/*     */ 
/*     */       
/* 127 */       if (this.charData != null) {
/* 128 */         if (startPos - 1L > this.charData.length()) {
/* 129 */           throw SQLError.createSQLException(Messages.getString("Clob.10"), "S1009", this.exceptionInterceptor);
/*     */         }
/*     */         
/* 132 */         int pos = this.charData.indexOf(stringToFind, (int)(startPos - 1L));
/*     */         
/* 134 */         return (pos == -1) ? -1L : (pos + 1);
/*     */       } 
/*     */       
/* 137 */       return -1L; } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, this.exceptionInterceptor); }
/*     */   
/*     */   }
/*     */   public OutputStream setAsciiStream(long indexToWriteAt) throws SQLException {
/*     */     
/* 142 */     try { if (indexToWriteAt < 1L) {
/* 143 */         throw SQLError.createSQLException(Messages.getString("Clob.0"), "S1009", this.exceptionInterceptor);
/*     */       }
/*     */       
/* 146 */       WatchableOutputStream bytesOut = new WatchableOutputStream();
/* 147 */       bytesOut.setWatcher(this);
/*     */       
/* 149 */       if (indexToWriteAt > 0L) {
/* 150 */         bytesOut.write(StringUtils.getBytes(this.charData), 0, (int)(indexToWriteAt - 1L));
/*     */       }
/*     */       
/* 153 */       return (OutputStream)bytesOut; } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, this.exceptionInterceptor); }
/*     */   
/*     */   }
/*     */   public Writer setCharacterStream(long indexToWriteAt) throws SQLException {
/*     */     
/* 158 */     try { if (indexToWriteAt < 1L) {
/* 159 */         throw SQLError.createSQLException(Messages.getString("Clob.1"), "S1009", this.exceptionInterceptor);
/*     */       }
/*     */       
/* 162 */       WatchableWriter writer = new WatchableWriter();
/* 163 */       writer.setWatcher(this);
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 168 */       if (indexToWriteAt > 1L) {
/* 169 */         writer.write(this.charData, 0, (int)(indexToWriteAt - 1L));
/*     */       }
/*     */       
/* 172 */       return (Writer)writer; } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, this.exceptionInterceptor); }
/*     */   
/*     */   }
/*     */   public int setString(long pos, String str) throws SQLException {
/*     */     
/* 177 */     try { if (pos < 1L) {
/* 178 */         throw SQLError.createSQLException(Messages.getString("Clob.2"), "S1009", this.exceptionInterceptor);
/*     */       }
/*     */       
/* 181 */       if (str == null) {
/* 182 */         throw SQLError.createSQLException(Messages.getString("Clob.3"), "S1009", this.exceptionInterceptor);
/*     */       }
/*     */       
/* 185 */       StringBuilder charBuf = new StringBuilder(this.charData);
/*     */       
/* 187 */       pos--;
/*     */       
/* 189 */       int strLength = str.length();
/*     */       
/* 191 */       charBuf.replace((int)pos, (int)(pos + strLength), str);
/*     */       
/* 193 */       this.charData = charBuf.toString();
/*     */       
/* 195 */       return strLength; } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, this.exceptionInterceptor); }
/*     */   
/*     */   }
/*     */   public int setString(long pos, String str, int offset, int len) throws SQLException {
/*     */     
/* 200 */     try { if (pos < 1L) {
/* 201 */         throw SQLError.createSQLException(Messages.getString("Clob.4"), "S1009", this.exceptionInterceptor);
/*     */       }
/*     */       
/* 204 */       if (str == null) {
/* 205 */         throw SQLError.createSQLException(Messages.getString("Clob.5"), "S1009", this.exceptionInterceptor);
/*     */       }
/*     */       
/* 208 */       StringBuilder charBuf = new StringBuilder(this.charData);
/*     */       
/* 210 */       pos--;
/*     */       
/*     */       try {
/* 213 */         String replaceString = str.substring(offset, offset + len);
/*     */         
/* 215 */         charBuf.replace((int)pos, (int)(pos + replaceString.length()), replaceString);
/* 216 */       } catch (StringIndexOutOfBoundsException e) {
/* 217 */         throw SQLError.createSQLException(e.getMessage(), "S1009", e, this.exceptionInterceptor);
/*     */       } 
/*     */       
/* 220 */       this.charData = charBuf.toString();
/*     */       
/* 222 */       return len; } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, this.exceptionInterceptor); }
/*     */   
/*     */   }
/*     */   
/*     */   public void streamClosed(WatchableStream out) {
/* 227 */     int streamSize = out.size();
/*     */     
/* 229 */     if (streamSize < this.charData.length()) {
/* 230 */       out.write(StringUtils.getBytes(this.charData), streamSize, this.charData.length() - streamSize);
/*     */     }
/*     */     
/* 233 */     this.charData = StringUtils.toAsciiString(out.toByteArray());
/*     */   }
/*     */   
/*     */   public void truncate(long length) throws SQLException {
/*     */     
/* 238 */     try { if (length > this.charData.length()) {
/* 239 */         throw SQLError.createSQLException(
/* 240 */             Messages.getString("Clob.11") + this.charData.length() + Messages.getString("Clob.12") + length + Messages.getString("Clob.13"), this.exceptionInterceptor);
/*     */       }
/*     */ 
/*     */       
/* 244 */       this.charData = this.charData.substring(0, (int)length); return; }
/* 245 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, this.exceptionInterceptor); }
/*     */   
/*     */   } public void writerClosed(char[] charDataBeingWritten) {
/* 248 */     this.charData = new String(charDataBeingWritten);
/*     */   }
/*     */ 
/*     */   
/*     */   public void writerClosed(WatchableWriter out) {
/* 253 */     int dataLength = out.size();
/*     */     
/* 255 */     if (dataLength < this.charData.length()) {
/* 256 */       out.write(this.charData, dataLength, this.charData.length() - dataLength);
/*     */     }
/*     */     
/* 259 */     this.charData = out.toString();
/*     */   }
/*     */   
/*     */   public void free() throws SQLException {
/*     */     
/* 264 */     try { this.charData = null; return; }
/* 265 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, this.exceptionInterceptor); }
/*     */   
/*     */   } public Reader getCharacterStream(long pos, long length) throws SQLException {
/*     */     
/* 269 */     try { return new StringReader(getSubString(pos, (int)length)); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, this.exceptionInterceptor); }
/*     */   
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\jdbc\Clob.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */