/*     */ package freemarker.core;
/*     */ 
/*     */ import freemarker.template.Template;
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
/*     */ 
/*     */ public class TokenMgrError
/*     */   extends Error
/*     */ {
/*     */   static final int LEXICAL_ERROR = 0;
/*     */   static final int STATIC_LEXER_ERROR = 1;
/*     */   static final int INVALID_LEXICAL_STATE = 2;
/*     */   static final int LOOP_DETECTED = 3;
/*     */   int errorCode;
/*     */   private String detail;
/*     */   private Integer lineNumber;
/*     */   private Integer columnNumber;
/*     */   private Integer endLineNumber;
/*     */   private Integer endColumnNumber;
/*     */   
/*     */   protected static final String addEscapes(String str) {
/*  74 */     StringBuilder retval = new StringBuilder();
/*     */     
/*  76 */     for (int i = 0; i < str.length(); i++) {
/*  77 */       char ch; switch (str.charAt(i)) {
/*     */         case '\000':
/*     */           break;
/*     */         
/*     */         case '\b':
/*  82 */           retval.append("\\b");
/*     */           break;
/*     */         case '\t':
/*  85 */           retval.append("\\t");
/*     */           break;
/*     */         case '\n':
/*  88 */           retval.append("\\n");
/*     */           break;
/*     */         case '\f':
/*  91 */           retval.append("\\f");
/*     */           break;
/*     */         case '\r':
/*  94 */           retval.append("\\r");
/*     */           break;
/*     */         case '"':
/*  97 */           retval.append("\\\"");
/*     */           break;
/*     */         case '\'':
/* 100 */           retval.append("\\'");
/*     */           break;
/*     */         case '\\':
/* 103 */           retval.append("\\\\");
/*     */           break;
/*     */         default:
/* 106 */           if ((ch = str.charAt(i)) < ' ' || ch > '~') {
/* 107 */             String s = "0000" + Integer.toString(ch, 16);
/* 108 */             retval.append("\\u" + s.substring(s.length() - 4, s.length())); break;
/*     */           } 
/* 110 */           retval.append(ch);
/*     */           break;
/*     */       } 
/*     */     
/*     */     } 
/* 115 */     return retval.toString();
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
/* 131 */     return "Lexical error: encountered " + (EOFSeen ? "<EOF> " : ("\"" + 
/* 132 */       addEscapes(String.valueOf(curChar)) + "\"" + " (" + curChar + "), ")) + "after \"" + 
/* 133 */       addEscapes(errorAfter) + "\".";
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
/*     */   public String getMessage() {
/* 147 */     return super.getMessage();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public TokenMgrError() {}
/*     */ 
/*     */ 
/*     */   
/*     */   public TokenMgrError(String detail, int reason) {
/* 158 */     super(detail);
/* 159 */     this.detail = detail;
/* 160 */     this.errorCode = reason;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public TokenMgrError(String detail, int reason, int errorLine, int errorColumn) {
/* 170 */     this(detail, reason, errorLine, errorColumn, 0, 0);
/* 171 */     this.endLineNumber = null;
/* 172 */     this.endColumnNumber = null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public TokenMgrError(String detail, int reason, int errorLine, int errorColumn, int endLineNumber, int endColumnNumber) {
/* 181 */     super(detail);
/* 182 */     this.detail = detail;
/* 183 */     this.errorCode = reason;
/*     */     
/* 185 */     this.lineNumber = Integer.valueOf(errorLine);
/* 186 */     this.columnNumber = Integer.valueOf(errorColumn);
/* 187 */     this.endLineNumber = Integer.valueOf(endLineNumber);
/* 188 */     this.endColumnNumber = Integer.valueOf(endColumnNumber);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   TokenMgrError(boolean EOFSeen, int lexState, int errorLine, int errorColumn, String errorAfter, int curChar, int reason) {
/* 197 */     this(EOFSeen, lexState, errorLine, errorColumn, errorAfter, (char)curChar, reason);
/*     */   }
/*     */   
/*     */   public TokenMgrError(boolean EOFSeen, int lexState, int errorLine, int errorColumn, String errorAfter, char curChar, int reason) {
/* 201 */     this(LexicalError(EOFSeen, lexState, errorLine, errorColumn, errorAfter, curChar), reason);
/*     */     
/* 203 */     this.lineNumber = Integer.valueOf(errorLine);
/* 204 */     this.columnNumber = Integer.valueOf(errorColumn);
/*     */     
/* 206 */     this.endLineNumber = this.lineNumber;
/* 207 */     this.endColumnNumber = this.columnNumber;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Integer getLineNumber() {
/* 216 */     return this.lineNumber;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Integer getColumnNumber() {
/* 225 */     return this.columnNumber;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Integer getEndLineNumber() {
/* 236 */     return this.endLineNumber;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Integer getEndColumnNumber() {
/* 247 */     return this.endColumnNumber;
/*     */   }
/*     */   
/*     */   public String getDetail() {
/* 251 */     return this.detail;
/*     */   }
/*     */   
/*     */   public ParseException toParseException(Template template) {
/* 255 */     return new ParseException(getDetail(), template, 
/*     */         
/* 257 */         (getLineNumber() != null) ? getLineNumber().intValue() : 0, 
/* 258 */         (getColumnNumber() != null) ? getColumnNumber().intValue() : 0, 
/* 259 */         (getEndLineNumber() != null) ? getEndLineNumber().intValue() : 0, 
/* 260 */         (getEndColumnNumber() != null) ? getEndColumnNumber().intValue() : 0);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\core\TokenMgrError.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */