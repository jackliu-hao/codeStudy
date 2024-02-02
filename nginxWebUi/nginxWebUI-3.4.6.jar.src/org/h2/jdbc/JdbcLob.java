/*     */ package org.h2.jdbc;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.PipedInputStream;
/*     */ import java.io.PipedOutputStream;
/*     */ import java.io.Reader;
/*     */ import java.io.Writer;
/*     */ import java.sql.SQLException;
/*     */ import org.h2.message.DbException;
/*     */ import org.h2.message.TraceObject;
/*     */ import org.h2.mvstore.DataUtils;
/*     */ import org.h2.util.IOUtils;
/*     */ import org.h2.util.Task;
/*     */ import org.h2.value.Value;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public abstract class JdbcLob
/*     */   extends TraceObject
/*     */ {
/*     */   final JdbcConnection conn;
/*     */   Value value;
/*     */   State state;
/*     */   
/*     */   static final class LobPipedOutputStream
/*     */     extends PipedOutputStream
/*     */   {
/*     */     private final Task task;
/*     */     
/*     */     LobPipedOutputStream(PipedInputStream param1PipedInputStream, Task param1Task) throws IOException {
/*  33 */       super(param1PipedInputStream);
/*  34 */       this.task = param1Task;
/*     */     }
/*     */ 
/*     */     
/*     */     public void close() throws IOException {
/*  39 */       super.close();
/*     */       try {
/*  41 */         this.task.get();
/*  42 */       } catch (Exception exception) {
/*  43 */         throw DataUtils.convertToIOException(exception);
/*     */       } 
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public enum State
/*     */   {
/*  55 */     NEW,
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  60 */     SET_CALLED,
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  65 */     WITH_VALUE,
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  70 */     CLOSED;
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
/*     */   JdbcLob(JdbcConnection paramJdbcConnection, Value paramValue, State paramState, int paramInt1, int paramInt2) {
/*  89 */     setTrace(paramJdbcConnection.getSession().getTrace(), paramInt1, paramInt2);
/*  90 */     this.conn = paramJdbcConnection;
/*  91 */     this.value = paramValue;
/*  92 */     this.state = paramState;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   void checkClosed() {
/* 100 */     this.conn.checkClosed();
/* 101 */     if (this.state == State.CLOSED) {
/* 102 */       throw DbException.get(90007);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   void checkEditable() {
/* 111 */     checkClosed();
/* 112 */     if (this.state != State.NEW) {
/* 113 */       throw DbException.getUnsupportedException("Allocate a new object to set its value.");
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
/*     */   void checkReadable() throws SQLException, IOException {
/* 125 */     checkClosed();
/* 126 */     if (this.state == State.SET_CALLED) {
/* 127 */       throw DbException.getUnsupportedException("Stream setter is not yet closed.");
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   void completeWrite(Value paramValue) {
/* 136 */     checkClosed();
/* 137 */     this.state = State.WITH_VALUE;
/* 138 */     this.value = paramValue;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void free() {
/* 145 */     debugCodeCall("free");
/* 146 */     this.state = State.CLOSED;
/* 147 */     this.value = null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   InputStream getBinaryStream() throws SQLException {
/*     */     try {
/* 158 */       debugCodeCall("getBinaryStream");
/* 159 */       checkReadable();
/* 160 */       return this.value.getInputStream();
/* 161 */     } catch (Exception exception) {
/* 162 */       throw logAndConvert(exception);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   Reader getCharacterStream() throws SQLException {
/*     */     try {
/* 174 */       debugCodeCall("getCharacterStream");
/* 175 */       checkReadable();
/* 176 */       return this.value.getReader();
/* 177 */     } catch (Exception exception) {
/* 178 */       throw logAndConvert(exception);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   Writer setCharacterStreamImpl() throws IOException {
/* 189 */     return IOUtils.getBufferedWriter(setClobOutputStreamImpl());
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
/*     */   LobPipedOutputStream setClobOutputStreamImpl() throws IOException {
/* 202 */     final PipedInputStream in = new PipedInputStream();
/* 203 */     Task task = new Task()
/*     */       {
/*     */         public void call() {
/* 206 */           JdbcLob.this.completeWrite(JdbcLob.this.conn.createClob(IOUtils.getReader(in), -1L));
/*     */         }
/*     */       };
/* 209 */     LobPipedOutputStream lobPipedOutputStream = new LobPipedOutputStream(pipedInputStream, task);
/* 210 */     task.execute();
/* 211 */     return lobPipedOutputStream;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String toString() {
/* 219 */     StringBuilder stringBuilder = (new StringBuilder()).append(getTraceObjectName()).append(": ");
/* 220 */     if (this.state == State.SET_CALLED) {
/* 221 */       stringBuilder.append("<setter_in_progress>");
/* 222 */     } else if (this.state == State.CLOSED) {
/* 223 */       stringBuilder.append("<closed>");
/*     */     } else {
/* 225 */       stringBuilder.append(this.value.getTraceSQL());
/*     */     } 
/* 227 */     return stringBuilder.toString();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\jdbc\JdbcLob.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */