/*     */ package io.undertow.servlet.spec;
/*     */ 
/*     */ import io.undertow.server.handlers.form.FormData;
/*     */ import io.undertow.servlet.UndertowServletMessages;
/*     */ import io.undertow.util.HeaderValues;
/*     */ import io.undertow.util.Headers;
/*     */ import io.undertow.util.HttpString;
/*     */ import java.io.ByteArrayInputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.nio.file.Path;
/*     */ import java.nio.file.Paths;
/*     */ import java.util.Collection;
/*     */ import java.util.Collections;
/*     */ import java.util.HashSet;
/*     */ import java.util.Set;
/*     */ import javax.servlet.MultipartConfigElement;
/*     */ import javax.servlet.http.Part;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class PartImpl
/*     */   implements Part
/*     */ {
/*     */   private final String name;
/*     */   private final FormData.FormValue formValue;
/*     */   private final MultipartConfigElement config;
/*     */   private final ServletContextImpl servletContext;
/*     */   private final HttpServletRequestImpl servletRequest;
/*     */   
/*     */   public PartImpl(String name, FormData.FormValue formValue, MultipartConfigElement config, ServletContextImpl servletContext, HttpServletRequestImpl servletRequest) {
/*  52 */     this.name = name;
/*  53 */     this.formValue = formValue;
/*  54 */     this.config = config;
/*  55 */     this.servletContext = servletContext;
/*  56 */     this.servletRequest = servletRequest;
/*     */   }
/*     */ 
/*     */   
/*     */   public InputStream getInputStream() throws IOException {
/*     */     String charset;
/*  62 */     if (this.formValue.isFileItem()) {
/*  63 */       return this.formValue.getFileItem().getInputStream();
/*     */     }
/*     */     
/*  66 */     if (this.formValue.getCharset() != null) {
/*  67 */       charset = this.formValue.getCharset();
/*  68 */     } else if (this.servletRequest.getCharacterEncoding() != null) {
/*  69 */       charset = this.servletRequest.getCharacterEncoding();
/*     */     } else {
/*  71 */       charset = this.servletContext.getDeployment().getDefaultRequestCharset().name();
/*     */     } 
/*     */     
/*  74 */     return new ByteArrayInputStream(this.formValue.getValue().getBytes(charset));
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public String getContentType() {
/*  80 */     return this.formValue.getHeaders().getFirst(Headers.CONTENT_TYPE);
/*     */   }
/*     */ 
/*     */   
/*     */   public String getName() {
/*  85 */     return this.name;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getSubmittedFileName() {
/*  90 */     return this.formValue.getFileName();
/*     */   }
/*     */ 
/*     */   
/*     */   public long getSize() {
/*     */     try {
/*  96 */       if (this.formValue.isFileItem())
/*  97 */         return this.formValue.getFileItem().getFileSize(); 
/*  98 */       if (this.formValue.getCharset() != null) {
/*  99 */         return (this.formValue.getValue().getBytes(this.formValue.getCharset())).length;
/*     */       }
/* 101 */       return this.formValue.getValue().length();
/*     */     }
/* 103 */     catch (IOException e) {
/* 104 */       throw new RuntimeException(e);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void write(String fileName) throws IOException {
/* 110 */     Path target = Paths.get(fileName, new String[0]);
/* 111 */     if (!target.isAbsolute()) {
/* 112 */       if (this.config.getLocation().isEmpty()) {
/* 113 */         target = this.servletContext.getDeployment().getDeploymentInfo().getTempPath().resolve(fileName);
/*     */       } else {
/* 115 */         target = Paths.get(this.config.getLocation(), new String[] { fileName });
/*     */       } 
/*     */     }
/* 118 */     if (this.formValue.isFileItem()) {
/* 119 */       this.formValue.getFileItem().write(target);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public void delete() throws IOException {
/* 125 */     if (this.formValue.isFileItem()) {
/*     */       try {
/* 127 */         this.formValue.getFileItem().delete();
/* 128 */       } catch (IOException e) {
/* 129 */         throw UndertowServletMessages.MESSAGES.deleteFailed(this.formValue.getPath());
/*     */       } 
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public String getHeader(String name) {
/* 136 */     return this.formValue.getHeaders().getFirst(new HttpString(name));
/*     */   }
/*     */ 
/*     */   
/*     */   public Collection<String> getHeaders(String name) {
/* 141 */     HeaderValues values = this.formValue.getHeaders().get(new HttpString(name));
/* 142 */     return (values == null) ? Collections.<String>emptyList() : (Collection<String>)values;
/*     */   }
/*     */ 
/*     */   
/*     */   public Collection<String> getHeaderNames() {
/* 147 */     Set<String> ret = new HashSet<>();
/* 148 */     for (HttpString i : this.formValue.getHeaders().getHeaderNames()) {
/* 149 */       ret.add(i.toString());
/*     */     }
/* 151 */     return ret;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\servlet\spec\PartImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */