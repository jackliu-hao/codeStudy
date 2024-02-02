/*     */ package freemarker.core;
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
/*     */ interface FMParserConstants
/*     */ {
/*     */   public static final int EOF = 0;
/*     */   public static final int BLANK = 1;
/*     */   public static final int START_TAG = 2;
/*     */   public static final int END_TAG = 3;
/*     */   public static final int CLOSE_TAG1 = 4;
/*     */   public static final int CLOSE_TAG2 = 5;
/*     */   public static final int ATTEMPT = 6;
/*     */   public static final int RECOVER = 7;
/*     */   public static final int IF = 8;
/*     */   public static final int ELSE_IF = 9;
/*     */   public static final int LIST = 10;
/*     */   public static final int ITEMS = 11;
/*     */   public static final int SEP = 12;
/*     */   public static final int FOREACH = 13;
/*     */   public static final int SWITCH = 14;
/*     */   public static final int CASE = 15;
/*     */   public static final int ASSIGN = 16;
/*     */   public static final int GLOBALASSIGN = 17;
/*     */   public static final int LOCALASSIGN = 18;
/*     */   public static final int _INCLUDE = 19;
/*     */   public static final int IMPORT = 20;
/*     */   public static final int FUNCTION = 21;
/*     */   public static final int MACRO = 22;
/*     */   public static final int TRANSFORM = 23;
/*     */   public static final int VISIT = 24;
/*     */   public static final int STOP = 25;
/*     */   public static final int RETURN = 26;
/*     */   public static final int CALL = 27;
/*     */   public static final int SETTING = 28;
/*     */   public static final int OUTPUTFORMAT = 29;
/*     */   public static final int AUTOESC = 30;
/*     */   public static final int NOAUTOESC = 31;
/*     */   public static final int COMPRESS = 32;
/*     */   public static final int COMMENT = 33;
/*     */   public static final int TERSE_COMMENT = 34;
/*     */   public static final int NOPARSE = 35;
/*     */   public static final int END_IF = 36;
/*     */   public static final int END_LIST = 37;
/*     */   public static final int END_ITEMS = 38;
/*     */   public static final int END_SEP = 39;
/*     */   public static final int END_RECOVER = 40;
/*     */   public static final int END_ATTEMPT = 41;
/*     */   public static final int END_FOREACH = 42;
/*     */   public static final int END_LOCAL = 43;
/*     */   public static final int END_GLOBAL = 44;
/*     */   public static final int END_ASSIGN = 45;
/*     */   public static final int END_FUNCTION = 46;
/*     */   public static final int END_MACRO = 47;
/*     */   public static final int END_OUTPUTFORMAT = 48;
/*     */   public static final int END_AUTOESC = 49;
/*     */   public static final int END_NOAUTOESC = 50;
/*     */   public static final int END_COMPRESS = 51;
/*     */   public static final int END_TRANSFORM = 52;
/*     */   public static final int END_SWITCH = 53;
/*     */   public static final int ELSE = 54;
/*     */   public static final int BREAK = 55;
/*     */   public static final int CONTINUE = 56;
/*     */   public static final int SIMPLE_RETURN = 57;
/*     */   public static final int HALT = 58;
/*     */   public static final int FLUSH = 59;
/*     */   public static final int TRIM = 60;
/*     */   public static final int LTRIM = 61;
/*     */   public static final int RTRIM = 62;
/*     */   public static final int NOTRIM = 63;
/*     */   public static final int DEFAUL = 64;
/*     */   public static final int SIMPLE_NESTED = 65;
/*     */   public static final int NESTED = 66;
/*     */   public static final int SIMPLE_RECURSE = 67;
/*     */   public static final int RECURSE = 68;
/*     */   public static final int FALLBACK = 69;
/*     */   public static final int ESCAPE = 70;
/*     */   public static final int END_ESCAPE = 71;
/*     */   public static final int NOESCAPE = 72;
/*     */   public static final int END_NOESCAPE = 73;
/*     */   public static final int UNIFIED_CALL = 74;
/*     */   public static final int UNIFIED_CALL_END = 75;
/*     */   public static final int FTL_HEADER = 76;
/*     */   public static final int TRIVIAL_FTL_HEADER = 77;
/*     */   public static final int UNKNOWN_DIRECTIVE = 78;
/*     */   public static final int STATIC_TEXT_WS = 79;
/*     */   public static final int STATIC_TEXT_NON_WS = 80;
/*     */   public static final int STATIC_TEXT_FALSE_ALARM = 81;
/*     */   public static final int DOLLAR_INTERPOLATION_OPENING = 82;
/*     */   public static final int HASH_INTERPOLATION_OPENING = 83;
/*     */   public static final int SQUARE_BRACKET_INTERPOLATION_OPENING = 84;
/*     */   public static final int ESCAPED_CHAR = 92;
/*     */   public static final int STRING_LITERAL = 93;
/*     */   public static final int RAW_STRING = 94;
/*     */   public static final int FALSE = 95;
/*     */   public static final int TRUE = 96;
/*     */   public static final int INTEGER = 97;
/*     */   public static final int DECIMAL = 98;
/*     */   public static final int DOT = 99;
/*     */   public static final int DOT_DOT = 100;
/*     */   public static final int DOT_DOT_LESS = 101;
/*     */   public static final int DOT_DOT_ASTERISK = 102;
/*     */   public static final int BUILT_IN = 103;
/*     */   public static final int EXISTS = 104;
/*     */   public static final int EQUALS = 105;
/*     */   public static final int DOUBLE_EQUALS = 106;
/*     */   public static final int NOT_EQUALS = 107;
/*     */   public static final int PLUS_EQUALS = 108;
/*     */   public static final int MINUS_EQUALS = 109;
/*     */   public static final int TIMES_EQUALS = 110;
/*     */   public static final int DIV_EQUALS = 111;
/*     */   public static final int MOD_EQUALS = 112;
/*     */   public static final int PLUS_PLUS = 113;
/*     */   public static final int MINUS_MINUS = 114;
/*     */   public static final int LESS_THAN = 115;
/*     */   public static final int LESS_THAN_EQUALS = 116;
/*     */   public static final int ESCAPED_GT = 117;
/*     */   public static final int ESCAPED_GTE = 118;
/*     */   public static final int LAMBDA_ARROW = 119;
/*     */   public static final int PLUS = 120;
/*     */   public static final int MINUS = 121;
/*     */   public static final int TIMES = 122;
/*     */   public static final int DOUBLE_STAR = 123;
/*     */   public static final int ELLIPSIS = 124;
/*     */   public static final int DIVIDE = 125;
/*     */   public static final int PERCENT = 126;
/*     */   public static final int AND = 127;
/*     */   public static final int OR = 128;
/*     */   public static final int EXCLAM = 129;
/*     */   public static final int COMMA = 130;
/*     */   public static final int SEMICOLON = 131;
/*     */   public static final int COLON = 132;
/*     */   public static final int OPEN_BRACKET = 133;
/*     */   public static final int CLOSE_BRACKET = 134;
/*     */   public static final int OPEN_PAREN = 135;
/*     */   public static final int CLOSE_PAREN = 136;
/*     */   public static final int OPENING_CURLY_BRACKET = 137;
/*     */   public static final int CLOSING_CURLY_BRACKET = 138;
/*     */   public static final int IN = 139;
/*     */   public static final int AS = 140;
/*     */   public static final int USING = 141;
/*     */   public static final int ID = 142;
/*     */   public static final int OPEN_MISPLACED_INTERPOLATION = 143;
/*     */   public static final int NON_ESCAPED_ID_START_CHAR = 144;
/*     */   public static final int ESCAPED_ID_CHAR = 145;
/*     */   public static final int ID_START_CHAR = 146;
/*     */   public static final int ASCII_DIGIT = 147;
/*     */   public static final int DIRECTIVE_END = 148;
/*     */   public static final int EMPTY_DIRECTIVE_END = 149;
/*     */   public static final int NATURAL_GT = 150;
/*     */   public static final int NATURAL_GTE = 151;
/*     */   public static final int TERMINATING_WHITESPACE = 152;
/*     */   public static final int TERMINATING_EXCLAM = 153;
/*     */   public static final int TERSE_COMMENT_END = 154;
/*     */   public static final int MAYBE_END = 155;
/*     */   public static final int KEEP_GOING = 156;
/*     */   public static final int LONE_LESS_THAN_OR_DASH = 157;
/*     */   public static final int DEFAULT = 0;
/*     */   public static final int NO_DIRECTIVE = 1;
/*     */   public static final int FM_EXPRESSION = 2;
/*     */   public static final int IN_PAREN = 3;
/*     */   public static final int NAMED_PARAMETER_EXPRESSION = 4;
/*     */   public static final int EXPRESSION_COMMENT = 5;
/*     */   public static final int NO_SPACE_EXPRESSION = 6;
/*     */   public static final int NO_PARSE = 7;
/* 332 */   public static final String[] tokenImage = new String[] { "<EOF>", "<BLANK>", "<START_TAG>", "<END_TAG>", "<CLOSE_TAG1>", "<CLOSE_TAG2>", "<ATTEMPT>", "<RECOVER>", "<IF>", "<ELSE_IF>", "<LIST>", "<ITEMS>", "<SEP>", "<FOREACH>", "<SWITCH>", "<CASE>", "<ASSIGN>", "<GLOBALASSIGN>", "<LOCALASSIGN>", "<_INCLUDE>", "<IMPORT>", "<FUNCTION>", "<MACRO>", "<TRANSFORM>", "<VISIT>", "<STOP>", "<RETURN>", "<CALL>", "<SETTING>", "<OUTPUTFORMAT>", "<AUTOESC>", "<NOAUTOESC>", "<COMPRESS>", "<COMMENT>", "<TERSE_COMMENT>", "<NOPARSE>", "<END_IF>", "<END_LIST>", "<END_ITEMS>", "<END_SEP>", "<END_RECOVER>", "<END_ATTEMPT>", "<END_FOREACH>", "<END_LOCAL>", "<END_GLOBAL>", "<END_ASSIGN>", "<END_FUNCTION>", "<END_MACRO>", "<END_OUTPUTFORMAT>", "<END_AUTOESC>", "<END_NOAUTOESC>", "<END_COMPRESS>", "<END_TRANSFORM>", "<END_SWITCH>", "<ELSE>", "<BREAK>", "<CONTINUE>", "<SIMPLE_RETURN>", "<HALT>", "<FLUSH>", "<TRIM>", "<LTRIM>", "<RTRIM>", "<NOTRIM>", "<DEFAUL>", "<SIMPLE_NESTED>", "<NESTED>", "<SIMPLE_RECURSE>", "<RECURSE>", "<FALLBACK>", "<ESCAPE>", "<END_ESCAPE>", "<NOESCAPE>", "<END_NOESCAPE>", "<UNIFIED_CALL>", "<UNIFIED_CALL_END>", "<FTL_HEADER>", "<TRIVIAL_FTL_HEADER>", "<UNKNOWN_DIRECTIVE>", "<STATIC_TEXT_WS>", "<STATIC_TEXT_NON_WS>", "<STATIC_TEXT_FALSE_ALARM>", "\"${\"", "\"#{\"", "\"[=\"", "<token of kind 85>", "<token of kind 86>", "<token of kind 87>", "\">\"", "\"]\"", "\"-\"", "<token of kind 91>", "<ESCAPED_CHAR>", "<STRING_LITERAL>", "<RAW_STRING>", "\"false\"", "\"true\"", "<INTEGER>", "<DECIMAL>", "\".\"", "\"..\"", "<DOT_DOT_LESS>", "\"..*\"", "\"?\"", "\"??\"", "\"=\"", "\"==\"", "\"!=\"", "\"+=\"", "\"-=\"", "\"*=\"", "\"/=\"", "\"%=\"", "\"++\"", "\"--\"", "<LESS_THAN>", "<LESS_THAN_EQUALS>", "<ESCAPED_GT>", "<ESCAPED_GTE>", "<LAMBDA_ARROW>", "\"+\"", "\"-\"", "\"*\"", "\"**\"", "\"...\"", "\"/\"", "\"%\"", "<AND>", "<OR>", "\"!\"", "\",\"", "\";\"", "\":\"", "\"[\"", "\"]\"", "\"(\"", "\")\"", "\"{\"", "\"}\"", "\"in\"", "\"as\"", "\"using\"", "<ID>", "<OPEN_MISPLACED_INTERPOLATION>", "<NON_ESCAPED_ID_START_CHAR>", "<ESCAPED_ID_CHAR>", "<ID_START_CHAR>", "<ASCII_DIGIT>", "\">\"", "<EMPTY_DIRECTIVE_END>", "\">\"", "\">=\"", "<TERMINATING_WHITESPACE>", "<TERMINATING_EXCLAM>", "<TERSE_COMMENT_END>", "<MAYBE_END>", "<KEEP_GOING>", "<LONE_LESS_THAN_OR_DASH>" };
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\core\FMParserConstants.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */