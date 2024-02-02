/*    */ package ch.qos.logback.solon;
/*    */ 
/*    */ import ch.qos.logback.classic.joran.JoranConfigurator;
/*    */ import ch.qos.logback.core.joran.spi.ElementSelector;
/*    */ import ch.qos.logback.core.joran.spi.RuleStore;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class SolonConfigurator
/*    */   extends JoranConfigurator
/*    */ {
/*    */   public void addInstanceRules(RuleStore rs) {
/* 14 */     super.addInstanceRules(rs);
/* 15 */     rs.addRule(new ElementSelector("configuration/solonProperty"), new SolonPropertyAction());
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\ch\qos\logback\solon\SolonConfigurator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */