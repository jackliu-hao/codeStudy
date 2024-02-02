/*    */ package ch.qos.logback.core.joran.event;
/*    */ 
/*    */ import ch.qos.logback.core.joran.spi.ElementPath;
/*    */ import org.xml.sax.Attributes;
/*    */ import org.xml.sax.Locator;
/*    */ import org.xml.sax.helpers.AttributesImpl;
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
/*    */ public class StartEvent
/*    */   extends SaxEvent
/*    */ {
/*    */   public final Attributes attributes;
/*    */   public final ElementPath elementPath;
/*    */   
/*    */   StartEvent(ElementPath elementPath, String namespaceURI, String localName, String qName, Attributes attributes, Locator locator) {
/* 27 */     super(namespaceURI, localName, qName, locator);
/*    */     
/* 29 */     this.attributes = new AttributesImpl(attributes);
/* 30 */     this.elementPath = elementPath;
/*    */   }
/*    */   
/*    */   public Attributes getAttributes() {
/* 34 */     return this.attributes;
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 39 */     StringBuilder b = new StringBuilder("StartEvent(");
/* 40 */     b.append(getQName());
/* 41 */     if (this.attributes != null) {
/* 42 */       for (int i = 0; i < this.attributes.getLength(); i++) {
/* 43 */         if (i > 0)
/* 44 */           b.append(' '); 
/* 45 */         b.append(this.attributes.getLocalName(i)).append("=\"").append(this.attributes.getValue(i)).append("\"");
/*    */       } 
/*    */     }
/* 48 */     b.append(")  [");
/* 49 */     b.append(this.locator.getLineNumber());
/* 50 */     b.append(",");
/* 51 */     b.append(this.locator.getColumnNumber());
/* 52 */     b.append("]");
/* 53 */     return b.toString();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\ch\qos\logback\core\joran\event\StartEvent.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */