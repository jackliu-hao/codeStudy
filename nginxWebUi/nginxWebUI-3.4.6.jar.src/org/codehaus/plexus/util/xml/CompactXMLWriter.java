/*    */ package org.codehaus.plexus.util.xml;
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
/*    */ public class CompactXMLWriter
/*    */   extends PrettyPrintXMLWriter
/*    */ {
/*    */   public CompactXMLWriter(PrintWriter writer) {
/* 31 */     super(writer);
/*    */   }
/*    */ 
/*    */   
/*    */   public CompactXMLWriter(Writer writer) {
/* 36 */     super(writer);
/*    */   }
/*    */   
/*    */   protected void endOfLine() {}
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\codehaus\plexu\\util\xml\CompactXMLWriter.class
 * Java compiler version: 3 (47.0)
 * JD-Core Version:       1.1.3
 */