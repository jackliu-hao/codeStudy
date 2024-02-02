/*     */ package org.codehaus.plexus.util.xml;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.io.FileNotFoundException;
/*     */ import java.io.FileOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.OutputStream;
/*     */ import java.io.OutputStreamWriter;
/*     */ import java.io.StringWriter;
/*     */ import java.io.Writer;
/*     */ import java.util.Locale;
/*     */ import java.util.regex.Matcher;
/*     */ import java.util.regex.Pattern;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class XmlStreamWriter
/*     */   extends Writer
/*     */ {
/*     */   private static final int BUFFER_SIZE = 4096;
/*  43 */   private StringWriter xmlPrologWriter = new StringWriter(4096);
/*     */   
/*     */   private OutputStream out;
/*     */   
/*     */   private Writer writer;
/*     */   
/*     */   private String encoding;
/*     */ 
/*     */   
/*     */   public XmlStreamWriter(OutputStream out) {
/*  53 */     this.out = out;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public XmlStreamWriter(File file) throws FileNotFoundException {
/*  59 */     this(new FileOutputStream(file));
/*     */   }
/*     */ 
/*     */   
/*     */   public String getEncoding() {
/*  64 */     return this.encoding;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void close() throws IOException {
/*  70 */     if (this.writer == null) {
/*     */       
/*  72 */       this.encoding = "UTF-8";
/*  73 */       this.writer = new OutputStreamWriter(this.out, this.encoding);
/*  74 */       this.writer.write(this.xmlPrologWriter.toString());
/*     */     } 
/*  76 */     this.writer.close();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void flush() throws IOException {
/*  82 */     if (this.writer != null)
/*     */     {
/*  84 */       this.writer.flush();
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private void detectEncoding(char[] cbuf, int off, int len) throws IOException {
/*  91 */     int size = len;
/*  92 */     StringBuffer xmlProlog = this.xmlPrologWriter.getBuffer();
/*  93 */     if (xmlProlog.length() + len > 4096)
/*     */     {
/*  95 */       size = 4096 - xmlProlog.length();
/*     */     }
/*  97 */     this.xmlPrologWriter.write(cbuf, off, size);
/*     */ 
/*     */     
/* 100 */     if (xmlProlog.length() >= 5) {
/*     */       
/* 102 */       if (xmlProlog.substring(0, 5).equals("<?xml")) {
/*     */ 
/*     */         
/* 105 */         int xmlPrologEnd = xmlProlog.indexOf("?>");
/* 106 */         if (xmlPrologEnd > 0)
/*     */         {
/*     */           
/* 109 */           Matcher m = ENCODING_PATTERN.matcher(xmlProlog.substring(0, xmlPrologEnd));
/* 110 */           if (m.find())
/*     */           {
/* 112 */             this.encoding = m.group(1).toUpperCase(Locale.ENGLISH);
/* 113 */             this.encoding = this.encoding.substring(1, this.encoding.length() - 1);
/*     */           
/*     */           }
/*     */           else
/*     */           {
/* 118 */             this.encoding = "UTF-8";
/*     */           
/*     */           }
/*     */         
/*     */         }
/* 123 */         else if (xmlProlog.length() >= 4096)
/*     */         {
/*     */           
/* 126 */           this.encoding = "UTF-8";
/*     */         
/*     */         }
/*     */       
/*     */       }
/*     */       else {
/*     */         
/* 133 */         this.encoding = "UTF-8";
/*     */       } 
/* 135 */       if (this.encoding != null) {
/*     */ 
/*     */         
/* 138 */         this.xmlPrologWriter = null;
/* 139 */         this.writer = new OutputStreamWriter(this.out, this.encoding);
/* 140 */         this.writer.write(xmlProlog.toString());
/* 141 */         if (len > size)
/*     */         {
/* 143 */           this.writer.write(cbuf, off + size, len - size);
/*     */         }
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void write(char[] cbuf, int off, int len) throws IOException {
/* 152 */     if (this.xmlPrologWriter != null) {
/*     */       
/* 154 */       detectEncoding(cbuf, off, len);
/*     */     }
/*     */     else {
/*     */       
/* 158 */       this.writer.write(cbuf, off, len);
/*     */     } 
/*     */   }
/*     */   
/* 162 */   static final Pattern ENCODING_PATTERN = XmlReader.ENCODING_PATTERN;
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\codehaus\plexu\\util\xml\XmlStreamWriter.class
 * Java compiler version: 3 (47.0)
 * JD-Core Version:       1.1.3
 */