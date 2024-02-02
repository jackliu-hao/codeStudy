/*     */ package javax.mail.util;
/*     */ 
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
/*     */ import javax.activation.DataSource;
/*     */ import javax.mail.internet.ContentType;
/*     */ import javax.mail.internet.MimeUtility;
/*     */ import javax.mail.internet.ParseException;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class ByteArrayDataSource
/*     */   implements DataSource
/*     */ {
/*     */   private byte[] data;
/*  59 */   private int len = -1;
/*     */   private String type;
/*  61 */   private String name = "";
/*     */   
/*     */   static class DSByteArrayOutputStream extends ByteArrayOutputStream {
/*     */     public byte[] getBuf() {
/*  65 */       return this.buf;
/*     */     }
/*     */     
/*     */     public int getCount() {
/*  69 */       return this.count;
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
/*     */   public ByteArrayDataSource(InputStream is, String type) throws IOException {
/*  84 */     DSByteArrayOutputStream os = new DSByteArrayOutputStream();
/*  85 */     byte[] buf = new byte[8192];
/*     */     int len;
/*  87 */     while ((len = is.read(buf)) > 0)
/*  88 */       os.write(buf, 0, len); 
/*  89 */     this.data = os.getBuf();
/*  90 */     this.len = os.getCount();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  99 */     if (this.data.length - this.len > 262144) {
/* 100 */       this.data = os.toByteArray();
/* 101 */       this.len = this.data.length;
/*     */     } 
/* 103 */     this.type = type;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ByteArrayDataSource(byte[] data, String type) {
/* 114 */     this.data = data;
/* 115 */     this.type = type;
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
/*     */   public ByteArrayDataSource(String data, String type) throws IOException {
/* 131 */     String charset = null;
/*     */     try {
/* 133 */       ContentType ct = new ContentType(type);
/* 134 */       charset = ct.getParameter("charset");
/* 135 */     } catch (ParseException pex) {}
/*     */ 
/*     */     
/* 138 */     charset = MimeUtility.javaCharset(charset);
/* 139 */     if (charset == null) {
/* 140 */       charset = MimeUtility.getDefaultJavaCharset();
/*     */     }
/* 142 */     this.data = data.getBytes(charset);
/* 143 */     this.type = type;
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
/*     */   public InputStream getInputStream() throws IOException {
/* 155 */     if (this.data == null)
/* 156 */       throw new IOException("no data"); 
/* 157 */     if (this.len < 0)
/* 158 */       this.len = this.data.length; 
/* 159 */     return new SharedByteArrayInputStream(this.data, 0, this.len);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public OutputStream getOutputStream() throws IOException {
/* 170 */     throw new IOException("cannot do this");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getContentType() {
/* 179 */     return this.type;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getName() {
/* 189 */     return this.name;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setName(String name) {
/* 198 */     this.name = name;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\javax\mai\\util\ByteArrayDataSource.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       1.1.3
 */