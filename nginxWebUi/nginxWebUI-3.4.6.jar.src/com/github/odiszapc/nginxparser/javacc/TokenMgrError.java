/*     */ package com.github.odiszapc.nginxparser.javacc;
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
/*     */ public class TokenMgrError
/*     */   extends Error
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*     */   static final int LEXICAL_ERROR = 0;
/*     */   static final int STATIC_LEXER_ERROR = 1;
/*     */   static final int INVALID_LEXICAL_STATE = 2;
/*     */   static final int LOOP_DETECTED = 3;
/*     */   int errorCode;
/*     */   
/*     */   protected static final String addEscapes(String str) {
/*  67 */     StringBuffer retval = new StringBuffer();
/*     */     
/*  69 */     for (int i = 0; i < str.length(); i++) {
/*  70 */       char ch; switch (str.charAt(i)) {
/*     */         case '\000':
/*     */           break;
/*     */         
/*     */         case '\b':
/*  75 */           retval.append("\\b");
/*     */           break;
/*     */         case '\t':
/*  78 */           retval.append("\\t");
/*     */           break;
/*     */         case '\n':
/*  81 */           retval.append("\\n");
/*     */           break;
/*     */         case '\f':
/*  84 */           retval.append("\\f");
/*     */           break;
/*     */         case '\r':
/*  87 */           retval.append("\\r");
/*     */           break;
/*     */         case '"':
/*  90 */           retval.append("\\\"");
/*     */           break;
/*     */         case '\'':
/*  93 */           retval.append("\\'");
/*     */           break;
/*     */         case '\\':
/*  96 */           retval.append("\\\\");
/*     */           break;
/*     */         default:
/*  99 */           if ((ch = str.charAt(i)) < ' ' || ch > '~') {
/* 100 */             String s = "0000" + Integer.toString(ch, 16);
/* 101 */             retval.append("\\u" + s.substring(s.length() - 4, s.length())); break;
/*     */           } 
/* 103 */           retval.append(ch);
/*     */           break;
/*     */       } 
/*     */     
/*     */     } 
/* 108 */     return retval.toString();
/*     */   }
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
/*     */   protected static String LexicalError(boolean EOFSeen, int lexState, int errorLine, int errorColumn, String errorAfter, char curChar) {
/* 124 */     return "Lexical error at line " + errorLine + ", column " + errorColumn + ".  Encountered: " + (EOFSeen ? "<EOF> " : ("\"" + addEscapes(String.valueOf(curChar)) + "\"" + " (" + curChar + "), ")) + "after : \"" + addEscapes(errorAfter) + "\"";
/*     */   }
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
/*     */   public String getMessage() {
/* 141 */     return super.getMessage();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public TokenMgrError() {}
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public TokenMgrError(String message, int reason) {
/* 154 */     super(message);
/* 155 */     this.errorCode = reason;
/*     */   }
/*     */ 
/*     */   
/*     */   public TokenMgrError(boolean EOFSeen, int lexState, int errorLine, int errorColumn, String errorAfter, char curChar, int reason) {
/* 160 */     this(LexicalError(EOFSeen, lexState, errorLine, errorColumn, errorAfter, curChar), reason);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\github\odiszapc\nginxparser\javacc\TokenMgrError.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */