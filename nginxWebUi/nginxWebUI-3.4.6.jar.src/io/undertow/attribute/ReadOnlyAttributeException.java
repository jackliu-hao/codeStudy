/*    */ package io.undertow.attribute;
/*    */ 
/*    */ import io.undertow.UndertowMessages;
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
/*    */ 
/*    */ 
/*    */ public class ReadOnlyAttributeException
/*    */   extends Exception
/*    */ {
/*    */   public ReadOnlyAttributeException() {}
/*    */   
/*    */   public ReadOnlyAttributeException(String attributeName, String newValue) {
/* 34 */     super(UndertowMessages.MESSAGES.couldNotSetAttribute(attributeName, newValue));
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\attribute\ReadOnlyAttributeException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */