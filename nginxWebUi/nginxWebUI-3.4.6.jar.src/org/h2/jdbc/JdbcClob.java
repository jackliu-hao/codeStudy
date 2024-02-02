/*     */ package org.h2.jdbc;
/*     */ 
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
/*     */ import java.io.Reader;
/*     */ import java.io.StringReader;
/*     */ import java.io.StringWriter;
/*     */ import java.io.Writer;
/*     */ import java.sql.Clob;
/*     */ import java.sql.NClob;
/*     */ import java.sql.SQLException;
/*     */ import org.h2.message.DbException;
/*     */ import org.h2.store.RangeReader;
/*     */ import org.h2.util.IOUtils;
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
/*     */ public final class JdbcClob
/*     */   extends JdbcLob
/*     */   implements NClob
/*     */ {
/*     */   public JdbcClob(JdbcConnection paramJdbcConnection, Value paramValue, JdbcLob.State paramState, int paramInt) {
/*  38 */     super(paramJdbcConnection, paramValue, paramState, 10, paramInt);
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
/*  51 */       if (this.value.getValueType() == 3) {
/*  52 */         long l = this.value.getType().getPrecision();
/*  53 */         if (l > 0L) {
/*  54 */           return l;
/*     */         }
/*     */       } 
/*  57 */       return IOUtils.copyAndCloseInput(this.value.getReader(), null, Long.MAX_VALUE);
/*  58 */     } catch (Exception exception) {
/*  59 */       throw logAndConvert(exception);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void truncate(long paramLong) throws SQLException {
/*  68 */     throw unsupported("LOB update");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public InputStream getAsciiStream() throws SQLException {
/*     */     try {
/*  79 */       debugCodeCall("getAsciiStream");
/*  80 */       checkReadable();
/*  81 */       String str = this.value.getString();
/*  82 */       return IOUtils.getInputStreamFromString(str);
/*  83 */     } catch (Exception exception) {
/*  84 */       throw logAndConvert(exception);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public OutputStream setAsciiStream(long paramLong) throws SQLException {
/*  93 */     throw unsupported("LOB update");
/*     */   }
/*     */ 
/*     */   
/*     */   public Reader getCharacterStream() throws SQLException {
/*  98 */     return super.getCharacterStream();
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
/*     */   public Writer setCharacterStream(long paramLong) throws SQLException {
/*     */     try {
/* 114 */       if (isDebugEnabled()) {
/* 115 */         debugCodeCall("setCharacterStream", paramLong);
/*     */       }
/* 117 */       checkEditable();
/* 118 */       if (paramLong != 1L) {
/* 119 */         throw DbException.getInvalidValueException("pos", Long.valueOf(paramLong));
/*     */       }
/* 121 */       this.state = JdbcLob.State.SET_CALLED;
/* 122 */       return setCharacterStreamImpl();
/* 123 */     } catch (Exception exception) {
/* 124 */       throw logAndConvert(exception);
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
/*     */   public String getSubString(long paramLong, int paramInt) throws SQLException {
/*     */     try {
/* 138 */       if (isDebugEnabled()) {
/* 139 */         debugCode("getSubString(" + paramLong + ", " + paramInt + ')');
/*     */       }
/* 141 */       checkReadable();
/* 142 */       if (paramLong < 1L) {
/* 143 */         throw DbException.getInvalidValueException("pos", Long.valueOf(paramLong));
/*     */       }
/* 145 */       if (paramInt < 0) {
/* 146 */         throw DbException.getInvalidValueException("length", Integer.valueOf(paramInt));
/*     */       }
/*     */       
/* 149 */       StringWriter stringWriter = new StringWriter(Math.min(4096, paramInt));
/* 150 */       try (Reader null = this.value.getReader()) {
/* 151 */         IOUtils.skipFully(reader, paramLong - 1L);
/* 152 */         IOUtils.copyAndCloseInput(reader, stringWriter, paramInt);
/*     */       } 
/* 154 */       return stringWriter.toString();
/* 155 */     } catch (Exception exception) {
/* 156 */       throw logAndConvert(exception);
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
/*     */   public int setString(long paramLong, String paramString) throws SQLException {
/*     */     try {
/* 173 */       if (isDebugEnabled()) {
/* 174 */         debugCode("setString(" + paramLong + ", " + quote(paramString) + ')');
/*     */       }
/* 176 */       checkEditable();
/* 177 */       if (paramLong != 1L)
/* 178 */         throw DbException.getInvalidValueException("pos", Long.valueOf(paramLong)); 
/* 179 */       if (paramString == null) {
/* 180 */         throw DbException.getInvalidValueException("str", paramString);
/*     */       }
/* 182 */       completeWrite(this.conn.createClob(new StringReader(paramString), -1L));
/* 183 */       return paramString.length();
/* 184 */     } catch (Exception exception) {
/* 185 */       throw logAndConvert(exception);
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
/*     */   public int setString(long paramLong, String paramString, int paramInt1, int paramInt2) throws SQLException {
/*     */     try {
/* 204 */       if (isDebugEnabled()) {
/* 205 */         debugCode("setString(" + paramLong + ", " + quote(paramString) + ", " + paramInt1 + ", " + paramInt2 + ')');
/*     */       }
/* 207 */       checkEditable();
/* 208 */       if (paramLong != 1L)
/* 209 */         throw DbException.getInvalidValueException("pos", Long.valueOf(paramLong)); 
/* 210 */       if (paramString == null) {
/* 211 */         throw DbException.getInvalidValueException("str", paramString);
/*     */       }
/* 213 */       completeWrite(this.conn.createClob((Reader)new RangeReader(new StringReader(paramString), paramInt1, paramInt2), -1L));
/* 214 */       return (int)this.value.getType().getPrecision();
/* 215 */     } catch (Exception exception) {
/* 216 */       throw logAndConvert(exception);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public long position(String paramString, long paramLong) throws SQLException {
/* 225 */     throw unsupported("LOB search");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public long position(Clob paramClob, long paramLong) throws SQLException {
/* 233 */     throw unsupported("LOB search");
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
/*     */   public Reader getCharacterStream(long paramLong1, long paramLong2) throws SQLException {
/*     */     try {
/* 246 */       if (isDebugEnabled()) {
/* 247 */         debugCode("getCharacterStream(" + paramLong1 + ", " + paramLong2 + ')');
/*     */       }
/* 249 */       checkReadable();
/* 250 */       if (this.state == JdbcLob.State.NEW) {
/* 251 */         if (paramLong1 != 1L) {
/* 252 */           throw DbException.getInvalidValueException("pos", Long.valueOf(paramLong1));
/*     */         }
/* 254 */         if (paramLong2 != 0L) {
/* 255 */           throw DbException.getInvalidValueException("length", Long.valueOf(paramLong1));
/*     */         }
/*     */       } 
/* 258 */       return this.value.getReader(paramLong1, paramLong2);
/* 259 */     } catch (Exception exception) {
/* 260 */       throw logAndConvert(exception);
/*     */     } 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\jdbc\JdbcClob.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */