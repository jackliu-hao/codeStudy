/*    */ package org.codehaus.plexus.util.cli;
/*    */ 
/*    */ import java.io.PrintWriter;
/*    */ import java.io.Writer;
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
/*    */ 
/*    */ 
/*    */ public class WriterStreamConsumer
/*    */   implements StreamConsumer
/*    */ {
/*    */   private PrintWriter writer;
/*    */   
/*    */   public WriterStreamConsumer(Writer writer) {
/* 33 */     this.writer = new PrintWriter(writer);
/*    */   }
/*    */ 
/*    */   
/*    */   public void consumeLine(String line) {
/* 38 */     this.writer.println(line);
/*    */     
/* 40 */     this.writer.flush();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\codehaus\plexu\\util\cli\WriterStreamConsumer.class
 * Java compiler version: 3 (47.0)
 * JD-Core Version:       1.1.3
 */