/*     */ package ch.qos.logback.core.joran.spi;
/*     */ 
/*     */ import java.io.IOException;
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
/*     */ public enum ConsoleTarget
/*     */ {
/*  29 */   SystemOut("System.out", new OutputStream()
/*     */     {
/*     */       public void write(int b) throws IOException {
/*  32 */         System.out.write(b);
/*     */       }
/*     */ 
/*     */       
/*     */       public void write(byte[] b) throws IOException {
/*  37 */         System.out.write(b);
/*     */       }
/*     */ 
/*     */       
/*     */       public void write(byte[] b, int off, int len) throws IOException {
/*  42 */         System.out.write(b, off, len);
/*     */       }
/*     */ 
/*     */       
/*     */       public void flush() throws IOException {
/*  47 */         System.out.flush();
/*     */       }
/*     */     }),
/*     */   
/*  51 */   SystemErr("System.err", new OutputStream()
/*     */     {
/*     */       public void write(int b) throws IOException {
/*  54 */         System.err.write(b);
/*     */       }
/*     */ 
/*     */       
/*     */       public void write(byte[] b) throws IOException {
/*  59 */         System.err.write(b);
/*     */       }
/*     */ 
/*     */       
/*     */       public void write(byte[] b, int off, int len) throws IOException {
/*  64 */         System.err.write(b, off, len);
/*     */       }
/*     */ 
/*     */       
/*     */       public void flush() throws IOException {
/*  69 */         System.err.flush();
/*     */       }
/*     */     });
/*     */   
/*     */   public static ConsoleTarget findByName(String name) {
/*  74 */     for (ConsoleTarget target : values()) {
/*  75 */       if (target.name.equalsIgnoreCase(name)) {
/*  76 */         return target;
/*     */       }
/*     */     } 
/*  79 */     return null;
/*     */   }
/*     */   
/*     */   private final String name;
/*     */   private final OutputStream stream;
/*     */   
/*     */   ConsoleTarget(String name, OutputStream stream) {
/*  86 */     this.name = name;
/*  87 */     this.stream = stream;
/*     */   }
/*     */   
/*     */   public String getName() {
/*  91 */     return this.name;
/*     */   }
/*     */   
/*     */   public OutputStream getStream() {
/*  95 */     return this.stream;
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 100 */     return this.name;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\ch\qos\logback\core\joran\spi\ConsoleTarget.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */