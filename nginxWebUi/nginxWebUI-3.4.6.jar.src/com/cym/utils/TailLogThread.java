/*    */ package com.cym.utils;
/*    */ 
/*    */ import java.io.BufferedReader;
/*    */ import java.io.IOException;
/*    */ import java.io.InputStream;
/*    */ import java.io.InputStreamReader;
/*    */ import org.noear.solon.core.message.Session;
/*    */ import org.slf4j.Logger;
/*    */ import org.slf4j.LoggerFactory;
/*    */ 
/*    */ public class TailLogThread
/*    */   extends Thread
/*    */ {
/* 14 */   static Logger logger = LoggerFactory.getLogger(TailLogThread.class);
/*    */   
/*    */   private BufferedReader reader;
/*    */   private Session session;
/*    */   
/*    */   public TailLogThread(InputStream in, Session session) {
/* 20 */     this.reader = new BufferedReader(new InputStreamReader(in));
/* 21 */     this.session = session;
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public void run() {
/*    */     try {
/*    */       String line;
/* 29 */       while ((line = this.reader.readLine()) != null)
/*    */       {
/* 31 */         this.session.send(line + "<br>");
/*    */       }
/* 33 */     } catch (IOException e) {
/* 34 */       logger.error(e.getMessage(), e);
/*    */     } 
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\cy\\utils\TailLogThread.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */