/*     */ package javax.servlet;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.PrintWriter;
/*     */ import java.util.Locale;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class ServletResponseWrapper
/*     */   implements ServletResponse
/*     */ {
/*     */   private ServletResponse response;
/*     */   
/*     */   public ServletResponseWrapper(ServletResponse response) {
/*  49 */     if (response == null) {
/*  50 */       throw new IllegalArgumentException("Response cannot be null");
/*     */     }
/*  52 */     this.response = response;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ServletResponse getResponse() {
/*  62 */     return this.response;
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
/*     */   public void setResponse(ServletResponse response) {
/*  74 */     if (response == null) {
/*  75 */       throw new IllegalArgumentException("Response cannot be null");
/*     */     }
/*  77 */     this.response = response;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setCharacterEncoding(String charset) {
/*  88 */     this.response.setCharacterEncoding(charset);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getCharacterEncoding() {
/*  97 */     return this.response.getCharacterEncoding();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ServletOutputStream getOutputStream() throws IOException {
/* 107 */     return this.response.getOutputStream();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public PrintWriter getWriter() throws IOException {
/* 117 */     return this.response.getWriter();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setContentLength(int len) {
/* 126 */     this.response.setContentLength(len);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setContentLengthLong(long len) {
/* 135 */     this.response.setContentLengthLong(len);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setContentType(String type) {
/* 144 */     this.response.setContentType(type);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getContentType() {
/* 155 */     return this.response.getContentType();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setBufferSize(int size) {
/* 163 */     this.response.setBufferSize(size);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getBufferSize() {
/* 171 */     return this.response.getBufferSize();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void flushBuffer() throws IOException {
/* 180 */     this.response.flushBuffer();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isCommitted() {
/* 188 */     return this.response.isCommitted();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void reset() {
/* 197 */     this.response.reset();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void resetBuffer() {
/* 206 */     this.response.resetBuffer();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setLocale(Locale loc) {
/* 215 */     this.response.setLocale(loc);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Locale getLocale() {
/* 223 */     return this.response.getLocale();
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
/*     */   public boolean isWrapperFor(ServletResponse wrapped) {
/* 239 */     if (this.response == wrapped)
/* 240 */       return true; 
/* 241 */     if (this.response instanceof ServletResponseWrapper) {
/* 242 */       return ((ServletResponseWrapper)this.response).isWrapperFor(wrapped);
/*     */     }
/* 244 */     return false;
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
/*     */   public boolean isWrapperFor(Class<?> wrappedType) {
/* 265 */     if (!ServletResponse.class.isAssignableFrom(wrappedType)) {
/* 266 */       throw new IllegalArgumentException("Given class " + wrappedType
/* 267 */           .getName() + " not a subinterface of " + ServletResponse.class
/* 268 */           .getName());
/*     */     }
/* 270 */     if (wrappedType.isAssignableFrom(this.response.getClass()))
/* 271 */       return true; 
/* 272 */     if (this.response instanceof ServletResponseWrapper) {
/* 273 */       return ((ServletResponseWrapper)this.response).isWrapperFor(wrappedType);
/*     */     }
/* 275 */     return false;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\javax\servlet\ServletResponseWrapper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */