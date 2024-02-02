/*    */ package freemarker.ext.beans;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ final class CharacterOrString
/*    */ {
/*    */   private final String stringValue;
/*    */   
/*    */   CharacterOrString(String stringValue) {
/* 34 */     this.stringValue = stringValue;
/*    */   }
/*    */   
/*    */   String getAsString() {
/* 38 */     return this.stringValue;
/*    */   }
/*    */   
/*    */   char getAsChar() {
/* 42 */     return this.stringValue.charAt(0);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\ext\beans\CharacterOrString.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */