/*     */ package org.apache.http.impl.execchain;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
/*     */ import java.net.SocketException;
/*     */ import org.apache.http.HttpEntity;
/*     */ import org.apache.http.HttpResponse;
/*     */ import org.apache.http.conn.EofSensorInputStream;
/*     */ import org.apache.http.conn.EofSensorWatcher;
/*     */ import org.apache.http.entity.HttpEntityWrapper;
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
/*     */ class ResponseEntityProxy
/*     */   extends HttpEntityWrapper
/*     */   implements EofSensorWatcher
/*     */ {
/*     */   private final ConnectionHolder connHolder;
/*     */   
/*     */   public static void enchance(HttpResponse response, ConnectionHolder connHolder) {
/*  51 */     HttpEntity entity = response.getEntity();
/*  52 */     if (entity != null && entity.isStreaming() && connHolder != null) {
/*  53 */       response.setEntity((HttpEntity)new ResponseEntityProxy(entity, connHolder));
/*     */     }
/*     */   }
/*     */   
/*     */   ResponseEntityProxy(HttpEntity entity, ConnectionHolder connHolder) {
/*  58 */     super(entity);
/*  59 */     this.connHolder = connHolder;
/*     */   }
/*     */   
/*     */   private void cleanup() throws IOException {
/*  63 */     if (this.connHolder != null) {
/*  64 */       this.connHolder.close();
/*     */     }
/*     */   }
/*     */   
/*     */   private void abortConnection() {
/*  69 */     if (this.connHolder != null) {
/*  70 */       this.connHolder.abortConnection();
/*     */     }
/*     */   }
/*     */   
/*     */   public void releaseConnection() {
/*  75 */     if (this.connHolder != null) {
/*  76 */       this.connHolder.releaseConnection();
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isRepeatable() {
/*  82 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   public InputStream getContent() throws IOException {
/*  87 */     return (InputStream)new EofSensorInputStream(this.wrappedEntity.getContent(), this);
/*     */   }
/*     */ 
/*     */   
/*     */   public void consumeContent() throws IOException {
/*  92 */     releaseConnection();
/*     */   }
/*     */ 
/*     */   
/*     */   public void writeTo(OutputStream outStream) throws IOException {
/*     */     try {
/*  98 */       if (outStream != null) {
/*  99 */         this.wrappedEntity.writeTo(outStream);
/*     */       }
/* 101 */       releaseConnection();
/* 102 */     } catch (IOException ex) {
/* 103 */       abortConnection();
/* 104 */       throw ex;
/* 105 */     } catch (RuntimeException ex) {
/* 106 */       abortConnection();
/* 107 */       throw ex;
/*     */     } finally {
/* 109 */       cleanup();
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean eofDetected(InputStream wrapped) throws IOException {
/*     */     try {
/* 118 */       if (wrapped != null) {
/* 119 */         wrapped.close();
/*     */       }
/* 121 */       releaseConnection();
/* 122 */     } catch (IOException ex) {
/* 123 */       abortConnection();
/* 124 */       throw ex;
/* 125 */     } catch (RuntimeException ex) {
/* 126 */       abortConnection();
/* 127 */       throw ex;
/*     */     } finally {
/* 129 */       cleanup();
/*     */     } 
/* 131 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean streamClosed(InputStream wrapped) throws IOException {
/*     */     try {
/* 137 */       boolean open = (this.connHolder != null && !this.connHolder.isReleased());
/*     */ 
/*     */       
/*     */       try {
/* 141 */         if (wrapped != null) {
/* 142 */           wrapped.close();
/*     */         }
/* 144 */         releaseConnection();
/* 145 */       } catch (SocketException ex) {
/* 146 */         if (open) {
/* 147 */           throw ex;
/*     */         }
/*     */       } 
/* 150 */     } catch (IOException ex) {
/* 151 */       abortConnection();
/* 152 */       throw ex;
/* 153 */     } catch (RuntimeException ex) {
/* 154 */       abortConnection();
/* 155 */       throw ex;
/*     */     } finally {
/* 157 */       cleanup();
/*     */     } 
/* 159 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean streamAbort(InputStream wrapped) throws IOException {
/* 164 */     cleanup();
/* 165 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 170 */     StringBuilder sb = new StringBuilder("ResponseEntityProxy{");
/* 171 */     sb.append(this.wrappedEntity);
/* 172 */     sb.append('}');
/* 173 */     return sb.toString();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\impl\execchain\ResponseEntityProxy.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */