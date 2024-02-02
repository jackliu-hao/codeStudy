/*     */ package cn.hutool.http;
/*     */ 
/*     */ import cn.hutool.core.convert.Convert;
/*     */ import cn.hutool.core.io.IORuntimeException;
/*     */ import cn.hutool.core.io.IoUtil;
/*     */ import cn.hutool.core.io.resource.Resource;
/*     */ import cn.hutool.core.io.resource.StringResource;
/*     */ import cn.hutool.core.util.StrUtil;
/*     */ import java.io.IOException;
/*     */ import java.io.OutputStream;
/*     */ import java.nio.charset.Charset;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class MultipartOutputStream
/*     */   extends OutputStream
/*     */ {
/*     */   private static final String CONTENT_DISPOSITION_TEMPLATE = "Content-Disposition: form-data; name=\"{}\"\r\n";
/*     */   private static final String CONTENT_DISPOSITION_FILE_TEMPLATE = "Content-Disposition: form-data; name=\"{}\"; filename=\"{}\"\r\n";
/*     */   private static final String CONTENT_TYPE_FILE_TEMPLATE = "Content-Type: {}\r\n";
/*     */   private final OutputStream out;
/*     */   private final Charset charset;
/*     */   private final String boundary;
/*     */   private boolean isFinish;
/*     */   
/*     */   public MultipartOutputStream(OutputStream out, Charset charset) {
/*  42 */     this(out, charset, HttpGlobalConfig.getBoundary());
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
/*     */   public MultipartOutputStream(OutputStream out, Charset charset, String boundary) {
/*  54 */     this.out = out;
/*  55 */     this.charset = charset;
/*  56 */     this.boundary = boundary;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public MultipartOutputStream write(String formFieldName, Object value) throws IORuntimeException {
/*  85 */     if (value instanceof cn.hutool.core.io.resource.MultiResource) {
/*  86 */       for (Resource subResource : value) {
/*  87 */         write(formFieldName, subResource);
/*     */       }
/*  89 */       return this;
/*     */     } 
/*     */ 
/*     */     
/*  93 */     beginPart();
/*     */     
/*  95 */     if (value instanceof Resource) {
/*  96 */       appendResource(formFieldName, (Resource)value);
/*     */     } else {
/*  98 */       appendResource(formFieldName, (Resource)new StringResource(
/*  99 */             Convert.toStr(value), null, this.charset));
/*     */     } 
/*     */     
/* 102 */     write(new Object[] { "\r\n" });
/* 103 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public void write(int b) throws IOException {
/* 108 */     this.out.write(b);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void finish() throws IORuntimeException {
/* 117 */     if (false == this.isFinish) {
/* 118 */       write(new Object[] { StrUtil.format("--{}--\r\n", new Object[] { this.boundary }) });
/* 119 */       this.isFinish = true;
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void close() {
/* 125 */     finish();
/* 126 */     IoUtil.close(this.out);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void appendResource(String formFieldName, Resource resource) throws IORuntimeException {
/* 137 */     String fileName = resource.getName();
/*     */ 
/*     */     
/* 140 */     if (null == fileName) {
/*     */       
/* 142 */       write(new Object[] { StrUtil.format("Content-Disposition: form-data; name=\"{}\"\r\n", new Object[] { formFieldName }) });
/*     */     } else {
/*     */       
/* 145 */       write(new Object[] { StrUtil.format("Content-Disposition: form-data; name=\"{}\"; filename=\"{}\"\r\n", new Object[] { formFieldName, fileName }) });
/*     */     } 
/*     */ 
/*     */     
/* 149 */     if (resource instanceof HttpResource) {
/* 150 */       String contentType = ((HttpResource)resource).getContentType();
/* 151 */       if (StrUtil.isNotBlank(contentType))
/*     */       {
/* 153 */         write(new Object[] { StrUtil.format("Content-Type: {}\r\n", new Object[] { contentType }) });
/*     */       }
/* 155 */     } else if (StrUtil.isNotEmpty(fileName)) {
/*     */       
/* 157 */       write(new Object[] { StrUtil.format("Content-Type: {}\r\n", new Object[] {
/* 158 */                 HttpUtil.getMimeType(fileName, ContentType.OCTET_STREAM.getValue())
/*     */               }) });
/*     */     } 
/*     */     
/* 162 */     write(new Object[] { "\r\n" });
/* 163 */     resource.writeTo(this);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void beginPart() {
/* 174 */     write(new Object[] { "--", this.boundary, "\r\n" });
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void write(Object... objs) {
/* 183 */     IoUtil.write(this, this.charset, false, objs);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\http\MultipartOutputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */