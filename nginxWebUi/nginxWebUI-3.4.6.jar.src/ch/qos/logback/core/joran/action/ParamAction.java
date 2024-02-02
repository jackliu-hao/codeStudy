/*    */ package ch.qos.logback.core.joran.action;
/*    */ 
/*    */ import ch.qos.logback.core.joran.spi.InterpretationContext;
/*    */ import ch.qos.logback.core.joran.util.PropertySetter;
/*    */ import ch.qos.logback.core.joran.util.beans.BeanDescriptionCache;
/*    */ import org.xml.sax.Attributes;
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
/*    */ public class ParamAction
/*    */   extends Action
/*    */ {
/* 23 */   static String NO_NAME = "No name attribute in <param> element";
/* 24 */   static String NO_VALUE = "No value attribute in <param> element";
/*    */   boolean inError = false;
/*    */   private final BeanDescriptionCache beanDescriptionCache;
/*    */   
/*    */   public ParamAction(BeanDescriptionCache beanDescriptionCache) {
/* 29 */     this.beanDescriptionCache = beanDescriptionCache;
/*    */   }
/*    */   
/*    */   public void begin(InterpretationContext ec, String localName, Attributes attributes) {
/* 33 */     String name = attributes.getValue("name");
/* 34 */     String value = attributes.getValue("value");
/*    */     
/* 36 */     if (name == null) {
/* 37 */       this.inError = true;
/* 38 */       addError(NO_NAME);
/*    */       
/*    */       return;
/*    */     } 
/* 42 */     if (value == null) {
/* 43 */       this.inError = true;
/* 44 */       addError(NO_VALUE);
/*    */       
/*    */       return;
/*    */     } 
/*    */     
/* 49 */     value = value.trim();
/*    */     
/* 51 */     Object o = ec.peekObject();
/* 52 */     PropertySetter propSetter = new PropertySetter(this.beanDescriptionCache, o);
/* 53 */     propSetter.setContext(this.context);
/* 54 */     value = ec.subst(value);
/*    */ 
/*    */     
/* 57 */     name = ec.subst(name);
/*    */ 
/*    */ 
/*    */     
/* 61 */     propSetter.setProperty(name, value);
/*    */   }
/*    */   
/*    */   public void end(InterpretationContext ec, String localName) {}
/*    */   
/*    */   public void finish(InterpretationContext ec) {}
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\ch\qos\logback\core\joran\action\ParamAction.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */