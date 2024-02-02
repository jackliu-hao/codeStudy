/*    */ package ch.qos.logback.classic.sift;
/*    */ 
/*    */ import ch.qos.logback.core.joran.action.Action;
/*    */ import ch.qos.logback.core.joran.event.InPlayListener;
/*    */ import ch.qos.logback.core.joran.event.SaxEvent;
/*    */ import ch.qos.logback.core.joran.spi.ActionException;
/*    */ import ch.qos.logback.core.joran.spi.InterpretationContext;
/*    */ import ch.qos.logback.core.sift.AppenderFactory;
/*    */ import java.util.ArrayList;
/*    */ import java.util.List;
/*    */ import java.util.Map;
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
/*    */ public class SiftAction
/*    */   extends Action
/*    */   implements InPlayListener
/*    */ {
/*    */   List<SaxEvent> seList;
/*    */   
/*    */   public void begin(InterpretationContext ic, String name, Attributes attributes) throws ActionException {
/* 33 */     this.seList = new ArrayList<SaxEvent>();
/* 34 */     ic.addInPlayListener(this);
/*    */   }
/*    */ 
/*    */   
/*    */   public void end(InterpretationContext ic, String name) throws ActionException {
/* 39 */     ic.removeInPlayListener(this);
/* 40 */     Object o = ic.peekObject();
/* 41 */     if (o instanceof SiftingAppender) {
/* 42 */       SiftingAppender sa = (SiftingAppender)o;
/* 43 */       Map<String, String> propertyMap = ic.getCopyOfPropertyMap();
/* 44 */       AppenderFactoryUsingJoran appenderFactory = new AppenderFactoryUsingJoran(this.seList, sa.getDiscriminatorKey(), propertyMap);
/* 45 */       sa.setAppenderFactory((AppenderFactory)appenderFactory);
/*    */     } 
/*    */   }
/*    */   
/*    */   public void inPlay(SaxEvent event) {
/* 50 */     this.seList.add(event);
/*    */   }
/*    */   
/*    */   public List<SaxEvent> getSeList() {
/* 54 */     return this.seList;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\ch\qos\logback\classic\sift\SiftAction.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */