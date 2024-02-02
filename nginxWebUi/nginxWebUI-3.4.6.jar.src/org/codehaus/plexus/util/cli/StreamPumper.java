/*     */ package org.codehaus.plexus.util.cli;
/*     */ 
/*     */ import java.io.BufferedReader;
/*     */ import java.io.InputStream;
/*     */ import java.io.InputStreamReader;
/*     */ import java.io.PrintWriter;
/*     */ import org.codehaus.plexus.util.IOUtil;
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
/*     */ public class StreamPumper
/*     */   extends Thread
/*     */ {
/*     */   private BufferedReader in;
/*  94 */   private StreamConsumer consumer = null;
/*     */   
/*  96 */   private PrintWriter out = null;
/*     */   
/*     */   private static final int SIZE = 1024;
/*     */   
/*     */   boolean done;
/*     */ 
/*     */   
/*     */   public StreamPumper(InputStream in) {
/* 104 */     this.in = new BufferedReader(new InputStreamReader(in), 1024);
/*     */   }
/*     */ 
/*     */   
/*     */   public StreamPumper(InputStream in, StreamConsumer consumer) {
/* 109 */     this(in);
/*     */     
/* 111 */     this.consumer = consumer;
/*     */   }
/*     */ 
/*     */   
/*     */   public StreamPumper(InputStream in, PrintWriter writer) {
/* 116 */     this(in);
/*     */     
/* 118 */     this.out = writer;
/*     */   }
/*     */ 
/*     */   
/*     */   public StreamPumper(InputStream in, PrintWriter writer, StreamConsumer consumer) {
/* 123 */     this(in);
/* 124 */     this.out = writer;
/* 125 */     this.consumer = consumer;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void run() {
/*     */     try {
/* 132 */       String s = this.in.readLine();
/*     */       
/* 134 */       while (s != null)
/*     */       {
/* 136 */         consumeLine(s);
/*     */         
/* 138 */         if (this.out != null) {
/*     */           
/* 140 */           this.out.println(s);
/*     */           
/* 142 */           this.out.flush();
/*     */         } 
/*     */         
/* 145 */         s = this.in.readLine();
/*     */       }
/*     */     
/* 148 */     } catch (Throwable e) {
/*     */ 
/*     */     
/*     */     }
/*     */     finally {
/*     */       
/* 154 */       IOUtil.close(this.in);
/*     */       
/* 156 */       synchronized (this) {
/*     */         
/* 158 */         this.done = true;
/*     */         
/* 160 */         notifyAll();
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void flush() {
/* 167 */     if (this.out != null)
/*     */     {
/* 169 */       this.out.flush();
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public void close() {
/* 175 */     IOUtil.close(this.out);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isDone() {
/* 180 */     return this.done;
/*     */   }
/*     */ 
/*     */   
/*     */   private void consumeLine(String line) {
/* 185 */     if (this.consumer != null)
/*     */     {
/* 187 */       this.consumer.consumeLine(line);
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\codehaus\plexu\\util\cli\StreamPumper.class
 * Java compiler version: 3 (47.0)
 * JD-Core Version:       1.1.3
 */