package freemarker.core;

import freemarker.template.Template;

public class TokenMgrError extends Error {
   static final int LEXICAL_ERROR = 0;
   static final int STATIC_LEXER_ERROR = 1;
   static final int INVALID_LEXICAL_STATE = 2;
   static final int LOOP_DETECTED = 3;
   int errorCode;
   private String detail;
   private Integer lineNumber;
   private Integer columnNumber;
   private Integer endLineNumber;
   private Integer endColumnNumber;

   protected static final String addEscapes(String str) {
      StringBuilder retval = new StringBuilder();

      for(int i = 0; i < str.length(); ++i) {
         switch (str.charAt(i)) {
            case '\u0000':
               break;
            case '\b':
               retval.append("\\b");
               break;
            case '\t':
               retval.append("\\t");
               break;
            case '\n':
               retval.append("\\n");
               break;
            case '\f':
               retval.append("\\f");
               break;
            case '\r':
               retval.append("\\r");
               break;
            case '"':
               retval.append("\\\"");
               break;
            case '\'':
               retval.append("\\'");
               break;
            case '\\':
               retval.append("\\\\");
               break;
            default:
               char ch;
               if ((ch = str.charAt(i)) >= ' ' && ch <= '~') {
                  retval.append(ch);
               } else {
                  String s = "0000" + Integer.toString(ch, 16);
                  retval.append("\\u" + s.substring(s.length() - 4, s.length()));
               }
         }
      }

      return retval.toString();
   }

   protected static String LexicalError(boolean EOFSeen, int lexState, int errorLine, int errorColumn, String errorAfter, char curChar) {
      return "Lexical error: encountered " + (EOFSeen ? "<EOF> " : "\"" + addEscapes(String.valueOf(curChar)) + "\"" + " (" + curChar + "), ") + "after \"" + addEscapes(errorAfter) + "\".";
   }

   public String getMessage() {
      return super.getMessage();
   }

   public TokenMgrError() {
   }

   public TokenMgrError(String detail, int reason) {
      super(detail);
      this.detail = detail;
      this.errorCode = reason;
   }

   /** @deprecated */
   @Deprecated
   public TokenMgrError(String detail, int reason, int errorLine, int errorColumn) {
      this(detail, reason, errorLine, errorColumn, 0, 0);
      this.endLineNumber = null;
      this.endColumnNumber = null;
   }

   public TokenMgrError(String detail, int reason, int errorLine, int errorColumn, int endLineNumber, int endColumnNumber) {
      super(detail);
      this.detail = detail;
      this.errorCode = reason;
      this.lineNumber = errorLine;
      this.columnNumber = errorColumn;
      this.endLineNumber = endLineNumber;
      this.endColumnNumber = endColumnNumber;
   }

   TokenMgrError(boolean EOFSeen, int lexState, int errorLine, int errorColumn, String errorAfter, int curChar, int reason) {
      this(EOFSeen, lexState, errorLine, errorColumn, errorAfter, (char)curChar, reason);
   }

   public TokenMgrError(boolean EOFSeen, int lexState, int errorLine, int errorColumn, String errorAfter, char curChar, int reason) {
      this(LexicalError(EOFSeen, lexState, errorLine, errorColumn, errorAfter, curChar), reason);
      this.lineNumber = errorLine;
      this.columnNumber = errorColumn;
      this.endLineNumber = this.lineNumber;
      this.endColumnNumber = this.columnNumber;
   }

   public Integer getLineNumber() {
      return this.lineNumber;
   }

   public Integer getColumnNumber() {
      return this.columnNumber;
   }

   public Integer getEndLineNumber() {
      return this.endLineNumber;
   }

   public Integer getEndColumnNumber() {
      return this.endColumnNumber;
   }

   public String getDetail() {
      return this.detail;
   }

   public ParseException toParseException(Template template) {
      return new ParseException(this.getDetail(), template, this.getLineNumber() != null ? this.getLineNumber() : 0, this.getColumnNumber() != null ? this.getColumnNumber() : 0, this.getEndLineNumber() != null ? this.getEndLineNumber() : 0, this.getEndColumnNumber() != null ? this.getEndColumnNumber() : 0);
   }
}
