/*     */ package org.apache.http.entity;
/*     */ 
/*     */ import java.io.ByteArrayInputStream;
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.ObjectOutputStream;
/*     */ import java.io.OutputStream;
/*     */ import java.io.Serializable;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class SerializableEntity
/*     */   extends AbstractHttpEntity
/*     */ {
/*     */   private byte[] objSer;
/*     */   private Serializable objRef;
/*     */   
/*     */   public SerializableEntity(Serializable ser, boolean bufferize) throws IOException {
/*  64 */     Args.notNull(ser, "Source object");
/*  65 */     if (bufferize) {
/*  66 */       createBytes(ser);
/*     */     } else {
/*  68 */       this.objRef = ser;
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
/*     */   public SerializableEntity(Serializable serializable) {
/*  81 */     Args.notNull(serializable, "Source object");
/*  82 */     this.objRef = serializable;
/*     */   }
/*     */   
/*     */   private void createBytes(Serializable ser) throws IOException {
/*  86 */     ByteArrayOutputStream baos = new ByteArrayOutputStream();
/*  87 */     ObjectOutputStream out = new ObjectOutputStream(baos);
/*  88 */     out.writeObject(ser);
/*  89 */     out.flush();
/*  90 */     this.objSer = baos.toByteArray();
/*     */   }
/*     */ 
/*     */   
/*     */   public InputStream getContent() throws IOException, IllegalStateException {
/*  95 */     if (this.objSer == null) {
/*  96 */       createBytes(this.objRef);
/*     */     }
/*  98 */     return new ByteArrayInputStream(this.objSer);
/*     */   }
/*     */ 
/*     */   
/*     */   public long getContentLength() {
/* 103 */     return (this.objSer == null) ? -1L : this.objSer.length;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isRepeatable() {
/* 108 */     return true;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isStreaming() {
/* 113 */     return (this.objSer == null);
/*     */   }
/*     */ 
/*     */   
/*     */   public void writeTo(OutputStream outStream) throws IOException {
/* 118 */     Args.notNull(outStream, "Output stream");
/* 119 */     if (this.objSer == null) {
/* 120 */       ObjectOutputStream out = new ObjectOutputStream(outStream);
/* 121 */       out.writeObject(this.objRef);
/* 122 */       out.flush();
/*     */     } else {
/* 124 */       outStream.write(this.objSer);
/* 125 */       outStream.flush();
/*     */     } 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\entity\SerializableEntity.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */