/*    */ package org.wildfly.client.config;
/*    */ 
/*    */ import java.util.NoSuchElementException;
/*    */ import javax.xml.stream.XMLStreamException;
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
/*    */ final class ScopedXMLStreamReader
/*    */   extends AbstractDelegatingXMLStreamReader
/*    */ {
/*    */   private int level;
/*    */   
/*    */   ScopedXMLStreamReader(boolean closeDelegate, ConfigurationXMLStreamReader delegate) {
/* 32 */     super(closeDelegate, delegate);
/*    */   }
/*    */   
/*    */   public boolean hasNext() throws ConfigXMLParseException {
/*    */     try {
/* 37 */       return (this.level >= 0 && getDelegate().hasNext());
/* 38 */     } catch (XMLStreamException e) {
/* 39 */       throw ConfigXMLParseException.from(e);
/*    */     } 
/*    */   }
/*    */   public int next() throws ConfigXMLParseException {
/*    */     int next;
/* 44 */     if (!hasNext()) {
/* 45 */       throw new NoSuchElementException();
/*    */     }
/*    */     
/*    */     try {
/* 49 */       next = getDelegate().next();
/* 50 */     } catch (XMLStreamException e) {
/* 51 */       throw ConfigXMLParseException.from(e);
/*    */     } 
/* 53 */     switch (next) {
/*    */       case 1:
/* 55 */         this.level++;
/*    */         break;
/*    */       
/*    */       case 2:
/* 59 */         this.level--;
/*    */         break;
/*    */     } 
/*    */     
/* 63 */     return next;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\wildfly\client\config\ScopedXMLStreamReader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */