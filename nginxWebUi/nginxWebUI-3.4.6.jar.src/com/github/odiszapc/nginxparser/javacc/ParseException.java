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
/*     */ public class ParseException
/*     */   extends Exception
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*     */   public Token currentToken;
/*     */   public int[][] expectedTokenSequences;
/*     */   public String[] tokenImage;
/*     */   protected String eol;
/*     */   
/*     */   public ParseException(Token currentTokenVal, int[][] expectedTokenSequencesVal, String[] tokenImageVal) {
/*  50 */     super(initialise(currentTokenVal, expectedTokenSequencesVal, tokenImageVal));
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
/* 150 */     this.eol = System.getProperty("line.separator", "\n"); this.currentToken = currentTokenVal; this.expectedTokenSequences = expectedTokenSequencesVal; this.tokenImage = tokenImageVal; } public ParseException() { this.eol = System.getProperty("line.separator", "\n"); } public ParseException(String message) { super(message); this.eol = System.getProperty("line.separator", "\n"); }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static String add_escapes(String str) {
/* 158 */     StringBuffer retval = new StringBuffer();
/*     */     
/* 160 */     for (int i = 0; i < str.length(); i++) {
/* 161 */       char ch; switch (str.charAt(i)) {
/*     */         case '\000':
/*     */           break;
/*     */         
/*     */         case '\b':
/* 166 */           retval.append("\\b");
/*     */           break;
/*     */         case '\t':
/* 169 */           retval.append("\\t");
/*     */           break;
/*     */         case '\n':
/* 172 */           retval.append("\\n");
/*     */           break;
/*     */         case '\f':
/* 175 */           retval.append("\\f");
/*     */           break;
/*     */         case '\r':
/* 178 */           retval.append("\\r");
/*     */           break;
/*     */         case '"':
/* 181 */           retval.append("\\\"");
/*     */           break;
/*     */         case '\'':
/* 184 */           retval.append("\\'");
/*     */           break;
/*     */         case '\\':
/* 187 */           retval.append("\\\\");
/*     */           break;
/*     */         default:
/* 190 */           if ((ch = str.charAt(i)) < ' ' || ch > '~') {
/* 191 */             String s = "0000" + Integer.toString(ch, 16);
/* 192 */             retval.append("\\u" + s.substring(s.length() - 4, s.length())); break;
/*     */           } 
/* 194 */           retval.append(ch);
/*     */           break;
/*     */       } 
/*     */     
/*     */     } 
/* 199 */     return retval.toString();
/*     */   }
/*     */   
/*     */   private static String initialise(Token currentToken, int[][] expectedTokenSequences, String[] tokenImage) {
/*     */     String eol = System.getProperty("line.separator", "\n");
/*     */     StringBuffer expected = new StringBuffer();
/*     */     int maxSize = 0;
/*     */     for (int i = 0; i < expectedTokenSequences.length; i++) {
/*     */       if (maxSize < (expectedTokenSequences[i]).length)
/*     */         maxSize = (expectedTokenSequences[i]).length; 
/*     */       for (int k = 0; k < (expectedTokenSequences[i]).length; k++)
/*     */         expected.append(tokenImage[expectedTokenSequences[i][k]]).append(' '); 
/*     */       if (expectedTokenSequences[i][(expectedTokenSequences[i]).length - 1] != 0)
/*     */         expected.append("..."); 
/*     */       expected.append(eol).append("    ");
/*     */     } 
/*     */     String retval = "Encountered \"";
/*     */     Token tok = currentToken.next;
/*     */     for (int j = 0; j < maxSize; j++) {
/*     */       if (j != 0)
/*     */         retval = retval + " "; 
/*     */       if (tok.kind == 0) {
/*     */         retval = retval + tokenImage[0];
/*     */         break;
/*     */       } 
/*     */       retval = retval + " " + tokenImage[tok.kind];
/*     */       retval = retval + " \"";
/*     */       retval = retval + add_escapes(tok.image);
/*     */       retval = retval + " \"";
/*     */       tok = tok.next;
/*     */     } 
/*     */     retval = retval + "\" at line " + currentToken.next.beginLine + ", column " + currentToken.next.beginColumn;
/*     */     retval = retval + "." + eol;
/*     */     if (expectedTokenSequences.length == 1) {
/*     */       retval = retval + "Was expecting:" + eol + "    ";
/*     */     } else {
/*     */       retval = retval + "Was expecting one of:" + eol + "    ";
/*     */     } 
/*     */     retval = retval + expected.toString();
/*     */     return retval;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\github\odiszapc\nginxparser\javacc\ParseException.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */