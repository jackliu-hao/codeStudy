/*    */ package ch.qos.logback.core.subst;
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
/*    */ public class Token
/*    */ {
/* 18 */   public static final Token START_TOKEN = new Token(Type.START, null);
/* 19 */   public static final Token CURLY_LEFT_TOKEN = new Token(Type.CURLY_LEFT, null);
/* 20 */   public static final Token CURLY_RIGHT_TOKEN = new Token(Type.CURLY_RIGHT, null); Type type;
/* 21 */   public static final Token DEFAULT_SEP_TOKEN = new Token(Type.DEFAULT, null);
/*    */   String payload;
/*    */   
/* 24 */   public enum Type { LITERAL, START, CURLY_LEFT, CURLY_RIGHT, DEFAULT; }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public Token(Type type, String payload) {
/* 31 */     this.type = type;
/* 32 */     this.payload = payload;
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean equals(Object o) {
/* 37 */     if (this == o)
/* 38 */       return true; 
/* 39 */     if (o == null || getClass() != o.getClass()) {
/* 40 */       return false;
/*    */     }
/* 42 */     Token token = (Token)o;
/*    */     
/* 44 */     if (this.type != token.type)
/* 45 */       return false; 
/* 46 */     if ((this.payload != null) ? !this.payload.equals(token.payload) : (token.payload != null)) {
/* 47 */       return false;
/*    */     }
/* 49 */     return true;
/*    */   }
/*    */ 
/*    */   
/*    */   public int hashCode() {
/* 54 */     int result = (this.type != null) ? this.type.hashCode() : 0;
/* 55 */     result = 31 * result + ((this.payload != null) ? this.payload.hashCode() : 0);
/* 56 */     return result;
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 61 */     String result = "Token{type=" + this.type;
/* 62 */     if (this.payload != null) {
/* 63 */       result = result + ", payload='" + this.payload + '\'';
/*    */     }
/* 65 */     result = result + '}';
/* 66 */     return result;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\ch\qos\logback\core\subst\Token.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */