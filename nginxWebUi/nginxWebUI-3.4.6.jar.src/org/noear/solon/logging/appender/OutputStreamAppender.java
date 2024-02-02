/*     */ package org.noear.solon.logging.appender;
/*     */ 
/*     */ import java.io.OutputStream;
/*     */ import java.io.PrintWriter;
/*     */ import org.noear.snack.ONode;
/*     */ import org.noear.solon.core.util.PrintUtil;
/*     */ import org.noear.solon.logging.event.Level;
/*     */ import org.noear.solon.logging.event.LogEvent;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public abstract class OutputStreamAppender
/*     */   extends AppenderSimple
/*     */ {
/*  19 */   protected PrintWriter out = null;
/*     */   
/*     */   protected void setOutput(OutputStream stream) {
/*  22 */     if (stream == null) {
/*     */       return;
/*     */     }
/*     */     
/*  26 */     setOutput(new PrintWriter(stream, true));
/*     */   }
/*     */   
/*     */   protected void setOutput(PrintWriter writer) {
/*  30 */     if (writer == null) {
/*     */       return;
/*     */     }
/*     */ 
/*     */     
/*  35 */     PrintWriter outOld = this.out;
/*     */ 
/*     */     
/*  38 */     this.out = writer;
/*     */ 
/*     */     
/*  41 */     if (outOld != null) {
/*  42 */       outOld.flush();
/*  43 */       outOld.close();
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void append(LogEvent logEvent) {
/*  49 */     if (this.out == null) {
/*     */       return;
/*     */     }
/*     */     
/*  53 */     super.append(logEvent);
/*     */   }
/*     */ 
/*     */   
/*     */   protected void appendDo(Level level, String title, Object content) {
/*  58 */     synchronized (this.out) {
/*     */ 
/*     */       
/*  61 */       switch (level) {
/*     */         case ERROR:
/*  63 */           redln(title);
/*     */           break;
/*     */         case WARN:
/*  66 */           yellowln(title);
/*     */           break;
/*     */         case DEBUG:
/*  69 */           blueln(title);
/*     */           break;
/*     */         case TRACE:
/*  72 */           purpleln(title);
/*     */           break;
/*     */         default:
/*  75 */           greenln(title);
/*     */           break;
/*     */       } 
/*     */ 
/*     */ 
/*     */       
/*  81 */       if (content instanceof String) {
/*  82 */         this.out.println(content);
/*     */       } else {
/*  84 */         this.out.println(ONode.stringify(content));
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   protected void greenln(Object txt) {
/*  90 */     colorln("\033[32m", txt);
/*     */   }
/*     */   
/*     */   protected void blueln(Object txt) {
/*  94 */     colorln("\033[34m", txt);
/*     */   }
/*     */   
/*     */   protected void redln(String txt) {
/*  98 */     colorln("\033[31m", txt);
/*     */   }
/*     */   
/*     */   protected void yellowln(Object txt) {
/* 102 */     colorln("\033[33m", txt);
/*     */   }
/*     */   
/*     */   protected void purpleln(Object txt) {
/* 106 */     colorln("\033[35m", txt);
/*     */   }
/*     */   
/*     */   protected void colorln(String color, Object s) {
/* 110 */     if (PrintUtil.IS_WINDOWS) {
/* 111 */       this.out.println(s);
/*     */     } else {
/* 113 */       this.out.println(color + s);
/* 114 */       this.out.print("\033[0m");
/*     */     } 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\solon\logging\appender\OutputStreamAppender.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */