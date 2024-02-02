/*    */ package cn.hutool.poi.excel.sax;
/*    */ 
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
/*    */ public enum AttributeName
/*    */ {
/* 16 */   r,
/*    */ 
/*    */ 
/*    */   
/* 20 */   s,
/*    */ 
/*    */ 
/*    */   
/* 24 */   t;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public boolean match(String attributeName) {
/* 33 */     return name().equals(attributeName);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public String getValue(Attributes attributes) {
/* 43 */     return attributes.getValue(name());
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\poi\excel\sax\AttributeName.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */