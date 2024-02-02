/*    */ package ch.qos.logback.solon;
/*    */ 
/*    */ import ch.qos.logback.core.joran.action.Action;
/*    */ import ch.qos.logback.core.joran.action.ActionUtil;
/*    */ import ch.qos.logback.core.joran.spi.ActionException;
/*    */ import ch.qos.logback.core.joran.spi.InterpretationContext;
/*    */ import ch.qos.logback.core.util.OptionHelper;
/*    */ import org.noear.solon.Solon;
/*    */ import org.xml.sax.Attributes;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class SolonPropertyAction
/*    */   extends Action
/*    */ {
/*    */   public void begin(InterpretationContext context, String s, Attributes attrs) throws ActionException {
/* 19 */     String name = attrs.getValue("name");
/* 20 */     String source = attrs.getValue("source");
/* 21 */     ActionUtil.Scope scope = ActionUtil.stringToScope(attrs.getValue("scope"));
/* 22 */     String defaultValue = attrs.getValue("defaultValue");
/* 23 */     if (OptionHelper.isEmpty(name) || OptionHelper.isEmpty(source)) {
/* 24 */       addError("The \"name\" and \"source\" attributes of <springProperty> must be set");
/*    */     }
/*    */     
/* 27 */     ActionUtil.setProperty(context, name, getValue(source, defaultValue), scope);
/*    */   }
/*    */   
/*    */   private String getValue(String source, String defaultValue) {
/* 31 */     String value = Solon.cfg().getProperty(source);
/* 32 */     if (value != null) {
/* 33 */       return value;
/*    */     }
/* 35 */     int lastDot = source.lastIndexOf('.');
/* 36 */     if (lastDot > 0) {
/* 37 */       String prefix = source.substring(0, lastDot + 1);
/* 38 */       return Solon.cfg().getProperty(prefix + source.substring(lastDot + 1), defaultValue);
/*    */     } 
/* 40 */     return defaultValue;
/*    */   }
/*    */   
/*    */   public void end(InterpretationContext interpretationContext, String s) throws ActionException {}
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\ch\qos\logback\solon\SolonPropertyAction.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */