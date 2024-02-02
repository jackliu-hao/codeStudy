/*    */ package com.github.odiszapc.nginxparser.javacc;
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
/*    */ public interface NginxConfigParserConstants
/*    */ {
/*    */   public static final int EOF = 0;
/*    */   public static final int LPAREN = 5;
/*    */   public static final int RPAREN = 6;
/*    */   public static final int LBRACE = 7;
/*    */   public static final int RBRACE = 8;
/*    */   public static final int SEMICOLON = 9;
/*    */   public static final int IF = 10;
/*    */   public static final int IF_BODY = 11;
/*    */   public static final int NUMBER = 12;
/*    */   public static final int STRING = 13;
/*    */   public static final int QUOTED_STRING = 14;
/*    */   public static final int SINGLE_QUOTED_STRING = 15;
/*    */   public static final int SINGLE_LINE_COMMENT = 16;
/*    */   public static final int COMMENT_SIGN = 17;
/*    */   public static final int COMMENT_BODY = 18;
/*    */   public static final int DEFAULT = 0;
/* 46 */   public static final String[] tokenImage = new String[] { "<EOF>", "\" \"", "\"\\t\"", "\"\\r\"", "\"\\n\"", "\"(\"", "\")\"", "\"{\"", "\"}\"", "\";\"", "\"if\"", "<IF_BODY>", "<NUMBER>", "<STRING>", "<QUOTED_STRING>", "<SINGLE_QUOTED_STRING>", "<SINGLE_LINE_COMMENT>", "\"#\"", "<COMMENT_BODY>" };
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\github\odiszapc\nginxparser\javacc\NginxConfigParserConstants.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */