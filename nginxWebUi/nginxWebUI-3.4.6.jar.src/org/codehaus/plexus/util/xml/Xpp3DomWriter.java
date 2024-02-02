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
/*    */ public class Xpp3DomWriter
/*    */ {
/*    */   public static void write(Writer writer, Xpp3Dom dom) {
/* 29 */     write(new PrettyPrintXMLWriter(writer), dom);
/*    */   }
/*    */ 
/*    */   
/*    */   public static void write(PrintWriter writer, Xpp3Dom dom) {
/* 34 */     write(new PrettyPrintXMLWriter(writer), dom);
/*    */   }
/*    */ 
/*    */   
/*    */   public static void write(XMLWriter xmlWriter, Xpp3Dom dom) {
/* 39 */     write(xmlWriter, dom, true);
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public static void write(XMLWriter xmlWriter, Xpp3Dom dom, boolean escape) {
/* 45 */     xmlWriter.startElement(dom.getName());
/* 46 */     String[] attributeNames = dom.getAttributeNames();
/* 47 */     for (int i = 0; i < attributeNames.length; i++) {
/*    */       
/* 49 */       String attributeName = attributeNames[i];
/* 50 */       xmlWriter.addAttribute(attributeName, dom.getAttribute(attributeName));
/*    */     } 
/* 52 */     Xpp3Dom[] children = dom.getChildren();
/* 53 */     for (int j = 0; j < children.length; j++)
/*    */     {
/* 55 */       write(xmlWriter, children[j], escape);
/*    */     }
/*    */     
/* 58 */     String value = dom.getValue();
/* 59 */     if (value != null)
/*    */     {
/* 61 */       if (escape) {
/*    */         
/* 63 */         xmlWriter.writeText(value);
/*    */       }
/*    */       else {
/*    */         
/* 67 */         xmlWriter.writeMarkup(value);
/*    */       } 
/*    */     }
/*    */     
/* 71 */     xmlWriter.endElement();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\codehaus\plexu\\util\xml\Xpp3DomWriter.class
 * Java compiler version: 3 (47.0)
 * JD-Core Version:       1.1.3
 */