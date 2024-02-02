/*     */ package io.undertow.protocols.http2;
/*     */ 
/*     */ import io.undertow.UndertowMessages;
/*     */ import java.io.IOException;
/*     */ import java.nio.ByteBuffer;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public abstract class Http2PushBackParser
/*     */ {
/*     */   private byte[] pushedBackData;
/*     */   private boolean finished;
/*     */   private int remainingData;
/*     */   private final int frameLength;
/*     */   int cnt;
/*     */   
/*     */   public Http2PushBackParser(int frameLength) {
/*  41 */     this.remainingData = frameLength;
/*  42 */     this.frameLength = frameLength;
/*     */   }
/*     */   
/*     */   public void parse(ByteBuffer data, Http2FrameHeaderParser headerParser) throws IOException {
/*  46 */     int used = 0;
/*  47 */     ByteBuffer dataToParse = data;
/*  48 */     int oldLimit = data.limit();
/*  49 */     Throwable original = null;
/*     */     try {
/*  51 */       if (this.pushedBackData != null) {
/*  52 */         int toCopy = Math.min(this.remainingData - this.pushedBackData.length, data.remaining());
/*  53 */         dataToParse = ByteBuffer.wrap(new byte[this.pushedBackData.length + toCopy]);
/*  54 */         dataToParse.put(this.pushedBackData);
/*  55 */         data.limit(data.position() + toCopy);
/*  56 */         dataToParse.put(data);
/*  57 */         dataToParse.flip();
/*     */       } 
/*  59 */       if (dataToParse.remaining() > this.remainingData) {
/*  60 */         dataToParse.limit(dataToParse.position() + this.remainingData);
/*     */       }
/*  62 */       int rem = dataToParse.remaining();
/*  63 */       handleData(dataToParse, headerParser);
/*  64 */       used = rem - dataToParse.remaining();
/*  65 */       if (!isFinished() && this.remainingData > 0 && used == 0 && dataToParse.remaining() >= this.remainingData && 
/*  66 */         this.cnt++ == 100) {
/*  67 */         original = UndertowMessages.MESSAGES.parserDidNotMakeProgress();
/*     */       }
/*     */     }
/*  70 */     catch (Throwable t) {
/*  71 */       original = t;
/*     */     } finally {
/*     */ 
/*     */       
/*     */       try {
/*  76 */         if (this.finished) {
/*  77 */           data.limit(oldLimit);
/*     */         } else {
/*  79 */           int leftOver = dataToParse.remaining();
/*  80 */           if (leftOver > 0) {
/*  81 */             this.pushedBackData = new byte[leftOver];
/*  82 */             dataToParse.get(this.pushedBackData);
/*     */           } else {
/*  84 */             this.pushedBackData = null;
/*     */           } 
/*  86 */           data.limit(oldLimit);
/*  87 */           this.remainingData -= used;
/*  88 */           if (this.remainingData == 0) {
/*  89 */             this.finished = true;
/*     */           }
/*     */         } 
/*  92 */       } catch (Throwable t) {
/*  93 */         if (original != null) {
/*  94 */           original.addSuppressed(t);
/*     */         } else {
/*  96 */           original = t;
/*     */         } 
/*     */       } 
/*  99 */       if (original != null) {
/* 100 */         if (original instanceof RuntimeException) {
/* 101 */           throw (RuntimeException)original;
/*     */         }
/* 103 */         if (original instanceof Error) {
/* 104 */           throw (Error)original;
/*     */         }
/* 106 */         if (original instanceof IOException) {
/* 107 */           throw (IOException)original;
/*     */         }
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   protected abstract void handleData(ByteBuffer paramByteBuffer, Http2FrameHeaderParser paramHttp2FrameHeaderParser) throws IOException;
/*     */   
/*     */   public boolean isFinished() {
/* 116 */     if (this.pushedBackData != null && this.remainingData == this.pushedBackData.length) {
/* 117 */       return true;
/*     */     }
/* 119 */     return this.finished;
/*     */   }
/*     */   
/*     */   protected void finish() {
/* 123 */     this.finished = true;
/*     */   }
/*     */   
/*     */   protected void moreData(int data) {
/* 127 */     this.finished = false;
/* 128 */     this.remainingData += data;
/*     */   }
/*     */   
/*     */   public int getFrameLength() {
/* 132 */     return this.frameLength;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\protocols\http2\Http2PushBackParser.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */