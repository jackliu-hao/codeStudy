/*     */ package ch.qos.logback.core.pattern.parser;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ class Token
/*     */ {
/*     */   static final int PERCENT = 37;
/*     */   static final int RIGHT_PARENTHESIS = 41;
/*     */   static final int MINUS = 45;
/*     */   static final int DOT = 46;
/*     */   static final int CURLY_LEFT = 123;
/*     */   static final int CURLY_RIGHT = 125;
/*     */   static final int LITERAL = 1000;
/*     */   static final int FORMAT_MODIFIER = 1002;
/*     */   static final int SIMPLE_KEYWORD = 1004;
/*     */   static final int COMPOSITE_KEYWORD = 1005;
/*     */   static final int OPTION = 1006;
/*     */   static final int EOF = 2147483647;
/*  34 */   static Token EOF_TOKEN = new Token(2147483647, "EOF");
/*  35 */   static Token RIGHT_PARENTHESIS_TOKEN = new Token(41);
/*  36 */   static Token BARE_COMPOSITE_KEYWORD_TOKEN = new Token(1005, "BARE");
/*  37 */   static Token PERCENT_TOKEN = new Token(37);
/*     */   
/*     */   private final int type;
/*     */   private final Object value;
/*     */   
/*     */   public Token(int type) {
/*  43 */     this(type, null);
/*     */   }
/*     */   
/*     */   public Token(int type, Object value) {
/*  47 */     this.type = type;
/*  48 */     this.value = value;
/*     */   }
/*     */   
/*     */   public int getType() {
/*  52 */     return this.type;
/*     */   }
/*     */   
/*     */   public Object getValue() {
/*  56 */     return this.value;
/*     */   }
/*     */   
/*     */   public String toString() {
/*  60 */     String typeStr = null;
/*  61 */     switch (this.type) {
/*     */       
/*     */       case 37:
/*  64 */         typeStr = "%";
/*     */         break;
/*     */       case 1002:
/*  67 */         typeStr = "FormatModifier";
/*     */         break;
/*     */       case 1000:
/*  70 */         typeStr = "LITERAL";
/*     */         break;
/*     */       case 1006:
/*  73 */         typeStr = "OPTION";
/*     */         break;
/*     */       case 1004:
/*  76 */         typeStr = "SIMPLE_KEYWORD";
/*     */         break;
/*     */       case 1005:
/*  79 */         typeStr = "COMPOSITE_KEYWORD";
/*     */         break;
/*     */       case 41:
/*  82 */         typeStr = "RIGHT_PARENTHESIS";
/*     */         break;
/*     */       default:
/*  85 */         typeStr = "UNKNOWN"; break;
/*     */     } 
/*  87 */     if (this.value == null) {
/*  88 */       return "Token(" + typeStr + ")";
/*     */     }
/*     */     
/*  91 */     return "Token(" + typeStr + ", \"" + this.value + "\")";
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public int hashCode() {
/*  97 */     int result = this.type;
/*  98 */     result = 29 * result + ((this.value != null) ? this.value.hashCode() : 0);
/*  99 */     return result;
/*     */   }
/*     */   
/*     */   public boolean equals(Object o) {
/* 103 */     if (this == o)
/* 104 */       return true; 
/* 105 */     if (!(o instanceof Token)) {
/* 106 */       return false;
/*     */     }
/* 108 */     Token token = (Token)o;
/*     */     
/* 110 */     if (this.type != token.type)
/* 111 */       return false; 
/* 112 */     if ((this.value != null) ? !this.value.equals(token.value) : (token.value != null)) {
/* 113 */       return false;
/*     */     }
/* 115 */     return true;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\ch\qos\logback\core\pattern\parser\Token.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */