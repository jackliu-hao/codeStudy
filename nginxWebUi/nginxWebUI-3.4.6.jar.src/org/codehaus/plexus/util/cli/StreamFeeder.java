/*     */ package org.codehaus.plexus.util.cli;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class StreamFeeder
/*     */   extends Thread
/*     */ {
/*     */   private InputStream input;
/*     */   private OutputStream output;
/*     */   private boolean done;
/*     */   
/*     */   public StreamFeeder(InputStream input, OutputStream output) {
/*  46 */     this.input = input;
/*     */     
/*  48 */     this.output = output;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void run() {
/*     */     try {
/*  59 */       feed();
/*     */     }
/*  61 */     catch (Throwable ex) {
/*     */ 
/*     */     
/*     */     }
/*     */     finally {
/*     */       
/*  67 */       close();
/*     */ 
/*     */       
/*  70 */       synchronized (this) {
/*     */         
/*  72 */         this.done = true;
/*     */         
/*  74 */         notifyAll();
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void close() {
/*  85 */     if (this.input != null)
/*     */     {
/*  87 */       synchronized (this.input) {
/*     */ 
/*     */         
/*     */         try {
/*  91 */           this.input.close();
/*     */         }
/*  93 */         catch (IOException ex) {}
/*     */ 
/*     */ 
/*     */ 
/*     */         
/*  98 */         this.input = null;
/*     */       } 
/*     */     }
/*     */     
/* 102 */     if (this.output != null)
/*     */     {
/* 104 */       synchronized (this.output) {
/*     */ 
/*     */         
/*     */         try {
/* 108 */           this.output.close();
/*     */         }
/* 110 */         catch (IOException ex) {}
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 115 */         this.output = null;
/*     */       } 
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isDone() {
/* 122 */     return this.done;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void feed() throws IOException {
/* 132 */     int data = this.input.read();
/*     */     
/* 134 */     while (!this.done && data != -1) {
/*     */       
/* 136 */       synchronized (this.output) {
/*     */         
/* 138 */         this.output.write(data);
/*     */         
/* 140 */         data = this.input.read();
/*     */       } 
/*     */     } 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\codehaus\plexu\\util\cli\StreamFeeder.class
 * Java compiler version: 3 (47.0)
 * JD-Core Version:       1.1.3
 */