package freemarker.core;

interface FMParserConstants {
   int EOF = 0;
   int BLANK = 1;
   int START_TAG = 2;
   int END_TAG = 3;
   int CLOSE_TAG1 = 4;
   int CLOSE_TAG2 = 5;
   int ATTEMPT = 6;
   int RECOVER = 7;
   int IF = 8;
   int ELSE_IF = 9;
   int LIST = 10;
   int ITEMS = 11;
   int SEP = 12;
   int FOREACH = 13;
   int SWITCH = 14;
   int CASE = 15;
   int ASSIGN = 16;
   int GLOBALASSIGN = 17;
   int LOCALASSIGN = 18;
   int _INCLUDE = 19;
   int IMPORT = 20;
   int FUNCTION = 21;
   int MACRO = 22;
   int TRANSFORM = 23;
   int VISIT = 24;
   int STOP = 25;
   int RETURN = 26;
   int CALL = 27;
   int SETTING = 28;
   int OUTPUTFORMAT = 29;
   int AUTOESC = 30;
   int NOAUTOESC = 31;
   int COMPRESS = 32;
   int COMMENT = 33;
   int TERSE_COMMENT = 34;
   int NOPARSE = 35;
   int END_IF = 36;
   int END_LIST = 37;
   int END_ITEMS = 38;
   int END_SEP = 39;
   int END_RECOVER = 40;
   int END_ATTEMPT = 41;
   int END_FOREACH = 42;
   int END_LOCAL = 43;
   int END_GLOBAL = 44;
   int END_ASSIGN = 45;
   int END_FUNCTION = 46;
   int END_MACRO = 47;
   int END_OUTPUTFORMAT = 48;
   int END_AUTOESC = 49;
   int END_NOAUTOESC = 50;
   int END_COMPRESS = 51;
   int END_TRANSFORM = 52;
   int END_SWITCH = 53;
   int ELSE = 54;
   int BREAK = 55;
   int CONTINUE = 56;
   int SIMPLE_RETURN = 57;
   int HALT = 58;
   int FLUSH = 59;
   int TRIM = 60;
   int LTRIM = 61;
   int RTRIM = 62;
   int NOTRIM = 63;
   int DEFAUL = 64;
   int SIMPLE_NESTED = 65;
   int NESTED = 66;
   int SIMPLE_RECURSE = 67;
   int RECURSE = 68;
   int FALLBACK = 69;
   int ESCAPE = 70;
   int END_ESCAPE = 71;
   int NOESCAPE = 72;
   int END_NOESCAPE = 73;
   int UNIFIED_CALL = 74;
   int UNIFIED_CALL_END = 75;
   int FTL_HEADER = 76;
   int TRIVIAL_FTL_HEADER = 77;
   int UNKNOWN_DIRECTIVE = 78;
   int STATIC_TEXT_WS = 79;
   int STATIC_TEXT_NON_WS = 80;
   int STATIC_TEXT_FALSE_ALARM = 81;
   int DOLLAR_INTERPOLATION_OPENING = 82;
   int HASH_INTERPOLATION_OPENING = 83;
   int SQUARE_BRACKET_INTERPOLATION_OPENING = 84;
   int ESCAPED_CHAR = 92;
   int STRING_LITERAL = 93;
   int RAW_STRING = 94;
   int FALSE = 95;
   int TRUE = 96;
   int INTEGER = 97;
   int DECIMAL = 98;
   int DOT = 99;
   int DOT_DOT = 100;
   int DOT_DOT_LESS = 101;
   int DOT_DOT_ASTERISK = 102;
   int BUILT_IN = 103;
   int EXISTS = 104;
   int EQUALS = 105;
   int DOUBLE_EQUALS = 106;
   int NOT_EQUALS = 107;
   int PLUS_EQUALS = 108;
   int MINUS_EQUALS = 109;
   int TIMES_EQUALS = 110;
   int DIV_EQUALS = 111;
   int MOD_EQUALS = 112;
   int PLUS_PLUS = 113;
   int MINUS_MINUS = 114;
   int LESS_THAN = 115;
   int LESS_THAN_EQUALS = 116;
   int ESCAPED_GT = 117;
   int ESCAPED_GTE = 118;
   int LAMBDA_ARROW = 119;
   int PLUS = 120;
   int MINUS = 121;
   int TIMES = 122;
   int DOUBLE_STAR = 123;
   int ELLIPSIS = 124;
   int DIVIDE = 125;
   int PERCENT = 126;
   int AND = 127;
   int OR = 128;
   int EXCLAM = 129;
   int COMMA = 130;
   int SEMICOLON = 131;
   int COLON = 132;
   int OPEN_BRACKET = 133;
   int CLOSE_BRACKET = 134;
   int OPEN_PAREN = 135;
   int CLOSE_PAREN = 136;
   int OPENING_CURLY_BRACKET = 137;
   int CLOSING_CURLY_BRACKET = 138;
   int IN = 139;
   int AS = 140;
   int USING = 141;
   int ID = 142;
   int OPEN_MISPLACED_INTERPOLATION = 143;
   int NON_ESCAPED_ID_START_CHAR = 144;
   int ESCAPED_ID_CHAR = 145;
   int ID_START_CHAR = 146;
   int ASCII_DIGIT = 147;
   int DIRECTIVE_END = 148;
   int EMPTY_DIRECTIVE_END = 149;
   int NATURAL_GT = 150;
   int NATURAL_GTE = 151;
   int TERMINATING_WHITESPACE = 152;
   int TERMINATING_EXCLAM = 153;
   int TERSE_COMMENT_END = 154;
   int MAYBE_END = 155;
   int KEEP_GOING = 156;
   int LONE_LESS_THAN_OR_DASH = 157;
   int DEFAULT = 0;
   int NO_DIRECTIVE = 1;
   int FM_EXPRESSION = 2;
   int IN_PAREN = 3;
   int NAMED_PARAMETER_EXPRESSION = 4;
   int EXPRESSION_COMMENT = 5;
   int NO_SPACE_EXPRESSION = 6;
   int NO_PARSE = 7;
   String[] tokenImage = new String[]{"<EOF>", "<BLANK>", "<START_TAG>", "<END_TAG>", "<CLOSE_TAG1>", "<CLOSE_TAG2>", "<ATTEMPT>", "<RECOVER>", "<IF>", "<ELSE_IF>", "<LIST>", "<ITEMS>", "<SEP>", "<FOREACH>", "<SWITCH>", "<CASE>", "<ASSIGN>", "<GLOBALASSIGN>", "<LOCALASSIGN>", "<_INCLUDE>", "<IMPORT>", "<FUNCTION>", "<MACRO>", "<TRANSFORM>", "<VISIT>", "<STOP>", "<RETURN>", "<CALL>", "<SETTING>", "<OUTPUTFORMAT>", "<AUTOESC>", "<NOAUTOESC>", "<COMPRESS>", "<COMMENT>", "<TERSE_COMMENT>", "<NOPARSE>", "<END_IF>", "<END_LIST>", "<END_ITEMS>", "<END_SEP>", "<END_RECOVER>", "<END_ATTEMPT>", "<END_FOREACH>", "<END_LOCAL>", "<END_GLOBAL>", "<END_ASSIGN>", "<END_FUNCTION>", "<END_MACRO>", "<END_OUTPUTFORMAT>", "<END_AUTOESC>", "<END_NOAUTOESC>", "<END_COMPRESS>", "<END_TRANSFORM>", "<END_SWITCH>", "<ELSE>", "<BREAK>", "<CONTINUE>", "<SIMPLE_RETURN>", "<HALT>", "<FLUSH>", "<TRIM>", "<LTRIM>", "<RTRIM>", "<NOTRIM>", "<DEFAUL>", "<SIMPLE_NESTED>", "<NESTED>", "<SIMPLE_RECURSE>", "<RECURSE>", "<FALLBACK>", "<ESCAPE>", "<END_ESCAPE>", "<NOESCAPE>", "<END_NOESCAPE>", "<UNIFIED_CALL>", "<UNIFIED_CALL_END>", "<FTL_HEADER>", "<TRIVIAL_FTL_HEADER>", "<UNKNOWN_DIRECTIVE>", "<STATIC_TEXT_WS>", "<STATIC_TEXT_NON_WS>", "<STATIC_TEXT_FALSE_ALARM>", "\"${\"", "\"#{\"", "\"[=\"", "<token of kind 85>", "<token of kind 86>", "<token of kind 87>", "\">\"", "\"]\"", "\"-\"", "<token of kind 91>", "<ESCAPED_CHAR>", "<STRING_LITERAL>", "<RAW_STRING>", "\"false\"", "\"true\"", "<INTEGER>", "<DECIMAL>", "\".\"", "\"..\"", "<DOT_DOT_LESS>", "\"..*\"", "\"?\"", "\"??\"", "\"=\"", "\"==\"", "\"!=\"", "\"+=\"", "\"-=\"", "\"*=\"", "\"/=\"", "\"%=\"", "\"++\"", "\"--\"", "<LESS_THAN>", "<LESS_THAN_EQUALS>", "<ESCAPED_GT>", "<ESCAPED_GTE>", "<LAMBDA_ARROW>", "\"+\"", "\"-\"", "\"*\"", "\"**\"", "\"...\"", "\"/\"", "\"%\"", "<AND>", "<OR>", "\"!\"", "\",\"", "\";\"", "\":\"", "\"[\"", "\"]\"", "\"(\"", "\")\"", "\"{\"", "\"}\"", "\"in\"", "\"as\"", "\"using\"", "<ID>", "<OPEN_MISPLACED_INTERPOLATION>", "<NON_ESCAPED_ID_START_CHAR>", "<ESCAPED_ID_CHAR>", "<ID_START_CHAR>", "<ASCII_DIGIT>", "\">\"", "<EMPTY_DIRECTIVE_END>", "\">\"", "\">=\"", "<TERMINATING_WHITESPACE>", "<TERMINATING_EXCLAM>", "<TERSE_COMMENT_END>", "<MAYBE_END>", "<KEEP_GOING>", "<LONE_LESS_THAN_OR_DASH>"};
}
