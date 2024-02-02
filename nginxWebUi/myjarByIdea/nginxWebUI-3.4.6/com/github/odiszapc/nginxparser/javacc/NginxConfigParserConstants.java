package com.github.odiszapc.nginxparser.javacc;

public interface NginxConfigParserConstants {
   int EOF = 0;
   int LPAREN = 5;
   int RPAREN = 6;
   int LBRACE = 7;
   int RBRACE = 8;
   int SEMICOLON = 9;
   int IF = 10;
   int IF_BODY = 11;
   int NUMBER = 12;
   int STRING = 13;
   int QUOTED_STRING = 14;
   int SINGLE_QUOTED_STRING = 15;
   int SINGLE_LINE_COMMENT = 16;
   int COMMENT_SIGN = 17;
   int COMMENT_BODY = 18;
   int DEFAULT = 0;
   String[] tokenImage = new String[]{"<EOF>", "\" \"", "\"\\t\"", "\"\\r\"", "\"\\n\"", "\"(\"", "\")\"", "\"{\"", "\"}\"", "\";\"", "\"if\"", "<IF_BODY>", "<NUMBER>", "<STRING>", "<QUOTED_STRING>", "<SINGLE_QUOTED_STRING>", "<SINGLE_LINE_COMMENT>", "\"#\"", "<COMMENT_BODY>"};
}
