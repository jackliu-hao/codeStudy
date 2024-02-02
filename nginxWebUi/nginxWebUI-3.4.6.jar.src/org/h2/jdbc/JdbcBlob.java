/*     */ package org.h2.jdbc;
/*     */ 
/*     */ import java.io.BufferedOutputStream;
/*     */ import java.io.ByteArrayInputStream;
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
/*     */ import java.io.PipedInputStream;
/*     */ import java.sql.Blob;
/*     */ import java.sql.SQLException;
/*     */ import org.h2.message.DbException;
/*     */ import org.h2.util.IOUtils;
/*     */ import org.h2.util.Task;
/*     */ import org.h2.value.Value;
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
/*     */ public final class JdbcBlob
/*     */   extends JdbcLob
/*     */   implements Blob
/*     */ {
/*     */   public JdbcBlob(JdbcConnection paramJdbcConnection, Value paramValue, JdbcLob.State paramState, int paramInt) {
/*  38 */     super(paramJdbcConnection, paramValue, paramState, 9, paramInt);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public long length() throws SQLException {
/*     */     try {
/*  49 */       debugCodeCall("length");
/*  50 */       checkReadable();
/*  51 */       if (this.value.getValueType() == 7) {
/*  52 */         long l = this.value.getType().getPrecision();
/*  53 */         if (l > 0L) {
/*  54 */           return l;
/*     */         }
/*     */       } 
/*  57 */       return IOUtils.copyAndCloseInput(this.value.getInputStream(), null);
/*  58 */     } catch (Exception exception) {
/*  59 */       throw logAndConvert(exception);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void truncate(long paramLong) throws SQLException {
/*  70 */     throw unsupported("LOB update");
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
/*     */   public byte[] getBytes(long paramLong, int paramInt) throws SQLException {
/*     */     try {
/*  83 */       if (isDebugEnabled()) {
/*  84 */         debugCode("getBytes(" + paramLong + ", " + paramInt + ')');
/*     */       }
/*  86 */       checkReadable();
/*  87 */       ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
/*  88 */       try (InputStream null = this.value.getInputStream()) {
/*  89 */         IOUtils.skipFully(inputStream, paramLong - 1L);
/*  90 */         IOUtils.copy(inputStream, byteArrayOutputStream, paramInt);
/*     */       } 
/*  92 */       return byteArrayOutputStream.toByteArray();
/*  93 */     } catch (Exception exception) {
/*  94 */       throw logAndConvert(exception);
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
/*     */   public int setBytes(long paramLong, byte[] paramArrayOfbyte) throws SQLException {
/* 109 */     if (paramArrayOfbyte == null) {
/* 110 */       throw new NullPointerException();
/*     */     }
/*     */     try {
/* 113 */       if (isDebugEnabled()) {
/* 114 */         debugCode("setBytes(" + paramLong + ", " + quoteBytes(paramArrayOfbyte) + ')');
/*     */       }
/* 116 */       checkEditable();
/* 117 */       if (paramLong != 1L) {
/* 118 */         throw DbException.getInvalidValueException("pos", Long.valueOf(paramLong));
/*     */       }
/* 120 */       completeWrite(this.conn.createBlob(new ByteArrayInputStream(paramArrayOfbyte), -1L));
/* 121 */       return paramArrayOfbyte.length;
/* 122 */     } catch (Exception exception) {
/* 123 */       throw logAndConvert(exception);
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
/*     */   public int setBytes(long paramLong, byte[] paramArrayOfbyte, int paramInt1, int paramInt2) throws SQLException {
/* 139 */     if (paramArrayOfbyte == null) {
/* 140 */       throw new NullPointerException();
/*     */     }
/*     */     try {
/* 143 */       if (isDebugEnabled()) {
/* 144 */         debugCode("setBytes(" + paramLong + ", " + quoteBytes(paramArrayOfbyte) + ", " + paramInt1 + ", " + paramInt2 + ')');
/*     */       }
/* 146 */       checkEditable();
/* 147 */       if (paramLong != 1L) {
/* 148 */         throw DbException.getInvalidValueException("pos", Long.valueOf(paramLong));
/*     */       }
/* 150 */       completeWrite(this.conn.createBlob(new ByteArrayInputStream(paramArrayOfbyte, paramInt1, paramInt2), -1L));
/* 151 */       return (int)this.value.getType().getPrecision();
/* 152 */     } catch (Exception exception) {
/* 153 */       throw logAndConvert(exception);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public InputStream getBinaryStream() throws SQLException {
/* 159 */     return super.getBinaryStream();
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
/*     */   public OutputStream setBinaryStream(long paramLong) throws SQLException {
/*     */     try {
/* 175 */       if (isDebugEnabled()) {
/* 176 */         debugCodeCall("setBinaryStream", paramLong);
/*     */       }
/* 178 */       checkEditable();
/* 179 */       if (paramLong != 1L) {
/* 180 */         throw DbException.getInvalidValueException("pos", Long.valueOf(paramLong));
/*     */       }
/* 182 */       final PipedInputStream in = new PipedInputStream();
/* 183 */       Task task = new Task()
/*     */         {
/*     */           public void call() {
/* 186 */             JdbcBlob.this.completeWrite(JdbcBlob.this.conn.createBlob(in, -1L));
/*     */           }
/*     */         };
/* 189 */       JdbcLob.LobPipedOutputStream lobPipedOutputStream = new JdbcLob.LobPipedOutputStream(pipedInputStream, task);
/* 190 */       task.execute();
/* 191 */       this.state = JdbcLob.State.SET_CALLED;
/* 192 */       return new BufferedOutputStream(lobPipedOutputStream);
/* 193 */     } catch (Exception exception) {
/* 194 */       throw logAndConvert(exception);
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
/*     */   public long position(byte[] paramArrayOfbyte, long paramLong) throws SQLException {
/* 207 */     if (isDebugEnabled()) {
/* 208 */       debugCode("position(" + quoteBytes(paramArrayOfbyte) + ", " + paramLong + ')');
/*     */     }
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
/* 250 */     throw unsupported("LOB search");
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
/*     */   public long position(Blob paramBlob, long paramLong) throws SQLException {
/* 262 */     if (isDebugEnabled()) {
/* 263 */       debugCode("position(blobPattern, " + paramLong + ')');
/*     */     }
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
/* 285 */     throw unsupported("LOB subset");
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
/*     */   public InputStream getBinaryStream(long paramLong1, long paramLong2) throws SQLException {
/*     */     try {
/* 298 */       if (isDebugEnabled()) {
/* 299 */         debugCode("getBinaryStream(" + paramLong1 + ", " + paramLong2 + ')');
/*     */       }
/* 301 */       checkReadable();
/* 302 */       if (this.state == JdbcLob.State.NEW) {
/* 303 */         if (paramLong1 != 1L) {
/* 304 */           throw DbException.getInvalidValueException("pos", Long.valueOf(paramLong1));
/*     */         }
/* 306 */         if (paramLong2 != 0L) {
/* 307 */           throw DbException.getInvalidValueException("length", Long.valueOf(paramLong1));
/*     */         }
/*     */       } 
/* 310 */       return this.value.getInputStream(paramLong1, paramLong2);
/* 311 */     } catch (Exception exception) {
/* 312 */       throw logAndConvert(exception);
/*     */     } 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\jdbc\JdbcBlob.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */