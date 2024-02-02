/*     */ package com.sun.mail.handlers;
/*     */ 
/*     */ import java.awt.datatransfer.DataFlavor;
/*     */ import java.io.FilterOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStreamReader;
/*     */ import java.io.OutputStream;
/*     */ import java.io.OutputStreamWriter;
/*     */ import java.io.UnsupportedEncodingException;
/*     */ import javax.activation.ActivationDataFlavor;
/*     */ import javax.activation.DataContentHandler;
/*     */ import javax.activation.DataSource;
/*     */ import javax.mail.internet.ContentType;
/*     */ import javax.mail.internet.MimeUtility;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class text_plain
/*     */   implements DataContentHandler
/*     */ {
/*  53 */   private static ActivationDataFlavor myDF = new ActivationDataFlavor(String.class, "text/plain", "Text String");
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static class NoCloseOutputStream
/*     */     extends FilterOutputStream
/*     */   {
/*     */     public NoCloseOutputStream(OutputStream os) {
/*  63 */       super(os);
/*     */     }
/*     */ 
/*     */     
/*     */     public void close() {}
/*     */   }
/*     */ 
/*     */   
/*     */   protected ActivationDataFlavor getDF() {
/*  72 */     return myDF;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public DataFlavor[] getTransferDataFlavors() {
/*  81 */     return new DataFlavor[] { (DataFlavor)getDF() };
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
/*     */   public Object getTransferData(DataFlavor df, DataSource ds) throws IOException {
/*  95 */     if (getDF().equals(df)) {
/*  96 */       return getContent(ds);
/*     */     }
/*  98 */     return null;
/*     */   }
/*     */   
/*     */   public Object getContent(DataSource ds) throws IOException {
/* 102 */     String enc = null;
/* 103 */     InputStreamReader is = null;
/*     */     
/*     */     try {
/* 106 */       enc = getCharset(ds.getContentType());
/* 107 */       is = new InputStreamReader(ds.getInputStream(), enc);
/* 108 */     } catch (IllegalArgumentException iex) {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 117 */       throw new UnsupportedEncodingException(enc);
/*     */     } 
/*     */     
/*     */     try {
/* 121 */       int pos = 0;
/*     */       
/* 123 */       char[] buf = new char[1024];
/*     */       int count;
/* 125 */       while ((count = is.read(buf, pos, buf.length - pos)) != -1) {
/* 126 */         pos += count;
/* 127 */         if (pos >= buf.length) {
/* 128 */           int size = buf.length;
/* 129 */           if (size < 262144) {
/* 130 */             size += size;
/*     */           } else {
/* 132 */             size += 262144;
/* 133 */           }  char[] tbuf = new char[size];
/* 134 */           System.arraycopy(buf, 0, tbuf, 0, pos);
/* 135 */           buf = tbuf;
/*     */         } 
/*     */       } 
/* 138 */       return new String(buf, 0, pos);
/*     */     } finally {
/*     */       try {
/* 141 */         is.close();
/* 142 */       } catch (IOException ex) {}
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void writeTo(Object obj, String type, OutputStream os) throws IOException {
/* 151 */     if (!(obj instanceof String)) {
/* 152 */       throw new IOException("\"" + getDF().getMimeType() + "\" DataContentHandler requires String object, " + "was given object of type " + obj.getClass().toString());
/*     */     }
/*     */ 
/*     */     
/* 156 */     String enc = null;
/* 157 */     OutputStreamWriter osw = null;
/*     */     
/*     */     try {
/* 160 */       enc = getCharset(type);
/* 161 */       osw = new OutputStreamWriter(new NoCloseOutputStream(os), enc);
/* 162 */     } catch (IllegalArgumentException iex) {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 171 */       throw new UnsupportedEncodingException(enc);
/*     */     } 
/*     */     
/* 174 */     String s = (String)obj;
/* 175 */     osw.write(s, 0, s.length());
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 183 */     osw.close();
/*     */   }
/*     */   
/*     */   private String getCharset(String type) {
/*     */     try {
/* 188 */       ContentType ct = new ContentType(type);
/* 189 */       String charset = ct.getParameter("charset");
/* 190 */       if (charset == null)
/*     */       {
/* 192 */         charset = "us-ascii"; } 
/* 193 */       return MimeUtility.javaCharset(charset);
/* 194 */     } catch (Exception ex) {
/* 195 */       return null;
/*     */     } 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\sun\mail\handlers\text_plain.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       1.1.3
 */