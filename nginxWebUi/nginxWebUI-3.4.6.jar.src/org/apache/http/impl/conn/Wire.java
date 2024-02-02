/*     */ package org.apache.http.impl.conn;
/*     */ 
/*     */ import java.io.ByteArrayInputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import org.apache.commons.logging.Log;
/*     */ import org.apache.http.annotation.Contract;
/*     */ import org.apache.http.annotation.ThreadingBehavior;
/*     */ import org.apache.http.util.Args;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ @Contract(threading = ThreadingBehavior.IMMUTABLE)
/*     */ public class Wire
/*     */ {
/*     */   private final Log log;
/*     */   private final String id;
/*     */   
/*     */   public Wire(Log log, String id) {
/*  54 */     this.log = log;
/*  55 */     this.id = id;
/*     */   }
/*     */   
/*     */   public Wire(Log log) {
/*  59 */     this(log, "");
/*     */   }
/*     */ 
/*     */   
/*     */   private void wire(String header, InputStream inStream) throws IOException {
/*  64 */     StringBuilder buffer = new StringBuilder();
/*     */     int ch;
/*  66 */     while ((ch = inStream.read()) != -1) {
/*  67 */       if (ch == 13) {
/*  68 */         buffer.append("[\\r]"); continue;
/*  69 */       }  if (ch == 10) {
/*  70 */         buffer.append("[\\n]\"");
/*  71 */         buffer.insert(0, "\"");
/*  72 */         buffer.insert(0, header);
/*  73 */         this.log.debug(this.id + " " + buffer.toString());
/*  74 */         buffer.setLength(0); continue;
/*  75 */       }  if (ch < 32 || ch > 127) {
/*  76 */         buffer.append("[0x");
/*  77 */         buffer.append(Integer.toHexString(ch));
/*  78 */         buffer.append("]"); continue;
/*     */       } 
/*  80 */       buffer.append((char)ch);
/*     */     } 
/*     */     
/*  83 */     if (buffer.length() > 0) {
/*  84 */       buffer.append('"');
/*  85 */       buffer.insert(0, '"');
/*  86 */       buffer.insert(0, header);
/*  87 */       this.log.debug(this.id + " " + buffer.toString());
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean enabled() {
/*  93 */     return this.log.isDebugEnabled();
/*     */   }
/*     */ 
/*     */   
/*     */   public void output(InputStream outStream) throws IOException {
/*  98 */     Args.notNull(outStream, "Output");
/*  99 */     wire(">> ", outStream);
/*     */   }
/*     */ 
/*     */   
/*     */   public void input(InputStream inStream) throws IOException {
/* 104 */     Args.notNull(inStream, "Input");
/* 105 */     wire("<< ", inStream);
/*     */   }
/*     */ 
/*     */   
/*     */   public void output(byte[] b, int off, int len) throws IOException {
/* 110 */     Args.notNull(b, "Output");
/* 111 */     wire(">> ", new ByteArrayInputStream(b, off, len));
/*     */   }
/*     */ 
/*     */   
/*     */   public void input(byte[] b, int off, int len) throws IOException {
/* 116 */     Args.notNull(b, "Input");
/* 117 */     wire("<< ", new ByteArrayInputStream(b, off, len));
/*     */   }
/*     */ 
/*     */   
/*     */   public void output(byte[] b) throws IOException {
/* 122 */     Args.notNull(b, "Output");
/* 123 */     wire(">> ", new ByteArrayInputStream(b));
/*     */   }
/*     */ 
/*     */   
/*     */   public void input(byte[] b) throws IOException {
/* 128 */     Args.notNull(b, "Input");
/* 129 */     wire("<< ", new ByteArrayInputStream(b));
/*     */   }
/*     */ 
/*     */   
/*     */   public void output(int b) throws IOException {
/* 134 */     output(new byte[] { (byte)b });
/*     */   }
/*     */ 
/*     */   
/*     */   public void input(int b) throws IOException {
/* 139 */     input(new byte[] { (byte)b });
/*     */   }
/*     */ 
/*     */   
/*     */   public void output(String s) throws IOException {
/* 144 */     Args.notNull(s, "Output");
/* 145 */     output(s.getBytes());
/*     */   }
/*     */ 
/*     */   
/*     */   public void input(String s) throws IOException {
/* 150 */     Args.notNull(s, "Input");
/* 151 */     input(s.getBytes());
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\impl\conn\Wire.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */