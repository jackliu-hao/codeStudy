/*    */ package cn.hutool.log.dialect.slf4j;
/*    */ 
/*    */ import cn.hutool.log.Log;
/*    */ import cn.hutool.log.LogFactory;
/*    */ import java.io.OutputStream;
/*    */ import java.io.PrintStream;
/*    */ import java.io.UnsupportedEncodingException;
/*    */ import org.slf4j.LoggerFactory;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class Slf4jLogFactory
/*    */   extends LogFactory
/*    */ {
/*    */   public Slf4jLogFactory() {
/* 23 */     this(true);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public Slf4jLogFactory(boolean failIfNOP) {
/* 32 */     super("Slf4j");
/* 33 */     checkLogExist(LoggerFactory.class);
/* 34 */     if (false == failIfNOP) {
/*    */       return;
/*    */     }
/*    */ 
/*    */ 
/*    */     
/* 40 */     final StringBuilder buf = new StringBuilder();
/* 41 */     PrintStream err = System.err;
/*    */     try {
/* 43 */       System.setErr(new PrintStream(new OutputStream()
/*    */             {
/*    */               public void write(int b) {
/* 46 */                 buf.append((char)b);
/*    */               }
/*    */             },  true, "US-ASCII"));
/* 49 */     } catch (UnsupportedEncodingException e) {
/* 50 */       throw new Error(e);
/*    */     } 
/*    */     
/*    */     try {
/* 54 */       if (LoggerFactory.getILoggerFactory() instanceof org.slf4j.helpers.NOPLoggerFactory) {
/* 55 */         throw new NoClassDefFoundError(buf.toString());
/*    */       }
/* 57 */       err.print(buf);
/* 58 */       err.flush();
/*    */     } finally {
/*    */       
/* 61 */       System.setErr(err);
/*    */     } 
/*    */   }
/*    */ 
/*    */   
/*    */   public Log createLog(String name) {
/* 67 */     return (Log)new Slf4jLog(name);
/*    */   }
/*    */ 
/*    */   
/*    */   public Log createLog(Class<?> clazz) {
/* 72 */     return (Log)new Slf4jLog(clazz);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\log\dialect\slf4j\Slf4jLogFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */