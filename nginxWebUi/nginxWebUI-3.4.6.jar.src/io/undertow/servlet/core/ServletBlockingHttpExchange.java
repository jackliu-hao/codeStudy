/*     */ package io.undertow.servlet.core;
/*     */ 
/*     */ import io.undertow.io.BlockingReceiverImpl;
/*     */ import io.undertow.io.BlockingSenderImpl;
/*     */ import io.undertow.io.Receiver;
/*     */ import io.undertow.io.Sender;
/*     */ import io.undertow.server.BlockingHttpExchange;
/*     */ import io.undertow.server.HttpServerExchange;
/*     */ import io.undertow.servlet.handlers.ServletRequestContext;
/*     */ import io.undertow.servlet.spec.HttpServletRequestImpl;
/*     */ import io.undertow.servlet.spec.HttpServletResponseImpl;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
/*     */ import javax.servlet.ServletRequest;
/*     */ import javax.servlet.ServletResponse;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class ServletBlockingHttpExchange
/*     */   implements BlockingHttpExchange
/*     */ {
/*     */   private final HttpServerExchange exchange;
/*     */   
/*     */   public ServletBlockingHttpExchange(HttpServerExchange exchange) {
/*  46 */     this.exchange = exchange;
/*     */   }
/*     */ 
/*     */   
/*     */   public InputStream getInputStream() {
/*  51 */     ServletRequest request = ((ServletRequestContext)this.exchange.getAttachment(ServletRequestContext.ATTACHMENT_KEY)).getServletRequest();
/*     */     try {
/*  53 */       return (InputStream)request.getInputStream();
/*  54 */     } catch (IOException e) {
/*  55 */       throw new RuntimeException(e);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public OutputStream getOutputStream() {
/*  61 */     ServletResponse response = ((ServletRequestContext)this.exchange.getAttachment(ServletRequestContext.ATTACHMENT_KEY)).getServletResponse();
/*     */     try {
/*  63 */       return (OutputStream)response.getOutputStream();
/*  64 */     } catch (IOException e) {
/*  65 */       throw new RuntimeException(e);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public Sender getSender() {
/*     */     try {
/*  72 */       return (Sender)new BlockingSenderImpl(this.exchange, getOutputStream());
/*  73 */     } catch (IllegalStateException e) {
/*  74 */       ServletResponse response = ((ServletRequestContext)this.exchange.getAttachment(ServletRequestContext.ATTACHMENT_KEY)).getServletResponse();
/*     */       try {
/*  76 */         return new BlockingWriterSenderImpl(this.exchange, response.getWriter(), response.getCharacterEncoding());
/*  77 */       } catch (IOException e1) {
/*  78 */         throw new RuntimeException(e1);
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void close() throws IOException {
/*  85 */     ServletRequestContext servletRequestContext = (ServletRequestContext)this.exchange.getAttachment(ServletRequestContext.ATTACHMENT_KEY);
/*  86 */     if (!this.exchange.isComplete()) {
/*     */       try {
/*  88 */         HttpServletRequestImpl request = servletRequestContext.getOriginalRequest();
/*  89 */         request.closeAndDrainRequest();
/*     */       } finally {
/*  91 */         HttpServletResponseImpl response = servletRequestContext.getOriginalResponse();
/*  92 */         response.closeStreamAndWriter();
/*     */       } 
/*     */     } else {
/*     */       try {
/*  96 */         HttpServletRequestImpl request = servletRequestContext.getOriginalRequest();
/*  97 */         request.freeResources();
/*     */       } finally {
/*  99 */         HttpServletResponseImpl response = servletRequestContext.getOriginalResponse();
/* 100 */         response.freeResources();
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public Receiver getReceiver() {
/* 107 */     return (Receiver)new BlockingReceiverImpl(this.exchange, getInputStream());
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\servlet\core\ServletBlockingHttpExchange.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */