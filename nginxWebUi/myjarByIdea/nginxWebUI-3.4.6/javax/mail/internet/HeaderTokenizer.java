package javax.mail.internet;

public class HeaderTokenizer {
   private String string;
   private boolean skipComments;
   private String delimiters;
   private int currentPos;
   private int maxPos;
   private int nextPos;
   private int peekPos;
   public static final String RFC822 = "()<>@,;:\\\"\t .[]";
   public static final String MIME = "()<>@,;:\\\"\t []/?=";
   private static final Token EOFToken = new Token(-4, (String)null);

   public HeaderTokenizer(String header, String delimiters, boolean skipComments) {
      this.string = header == null ? "" : header;
      this.skipComments = skipComments;
      this.delimiters = delimiters;
      this.currentPos = this.nextPos = this.peekPos = 0;
      this.maxPos = this.string.length();
   }

   public HeaderTokenizer(String header, String delimiters) {
      this(header, delimiters, true);
   }

   public HeaderTokenizer(String header) {
      this(header, "()<>@,;:\\\"\t .[]");
   }

   public Token next() throws ParseException {
      return this.next('\u0000', false);
   }

   Token next(char endOfAtom) throws ParseException {
      return this.next(endOfAtom, false);
   }

   Token next(char endOfAtom, boolean keepEscapes) throws ParseException {
      this.currentPos = this.nextPos;
      Token tk = this.getNext(endOfAtom, keepEscapes);
      this.nextPos = this.peekPos = this.currentPos;
      return tk;
   }

   public Token peek() throws ParseException {
      this.currentPos = this.peekPos;
      Token tk = this.getNext('\u0000', false);
      this.peekPos = this.currentPos;
      return tk;
   }

   public String getRemainder() {
      return this.string.substring(this.nextPos);
   }

   private Token getNext(char endOfAtom, boolean keepEscapes) throws ParseException {
      if (this.currentPos >= this.maxPos) {
         return EOFToken;
      } else if (this.skipWhiteSpace() == -4) {
         return EOFToken;
      } else {
         boolean filter = false;

         char c;
         int start;
         for(c = this.string.charAt(this.currentPos); c == '('; c = this.string.charAt(this.currentPos)) {
            start = ++this.currentPos;

            int nesting;
            for(nesting = 1; nesting > 0 && this.currentPos < this.maxPos; ++this.currentPos) {
               c = this.string.charAt(this.currentPos);
               if (c == '\\') {
                  ++this.currentPos;
                  filter = true;
               } else if (c == '\r') {
                  filter = true;
               } else if (c == '(') {
                  ++nesting;
               } else if (c == ')') {
                  --nesting;
               }
            }

            if (nesting != 0) {
               throw new ParseException("Unbalanced comments");
            }

            if (!this.skipComments) {
               String s;
               if (filter) {
                  s = filterToken(this.string, start, this.currentPos - 1, keepEscapes);
               } else {
                  s = this.string.substring(start, this.currentPos - 1);
               }

               return new Token(-3, s);
            }

            if (this.skipWhiteSpace() == -4) {
               return EOFToken;
            }
         }

         if (c == '"') {
            ++this.currentPos;
            return this.collectString('"', keepEscapes);
         } else if (c >= ' ' && c < 127 && this.delimiters.indexOf(c) < 0) {
            for(start = this.currentPos; this.currentPos < this.maxPos; ++this.currentPos) {
               c = this.string.charAt(this.currentPos);
               if (c < ' ' || c >= 127 || c == '(' || c == ' ' || c == '"' || this.delimiters.indexOf(c) >= 0) {
                  if (endOfAtom > 0 && c != endOfAtom) {
                     this.currentPos = start;
                     return this.collectString(endOfAtom, keepEscapes);
                  }
                  break;
               }
            }

            return new Token(-1, this.string.substring(start, this.currentPos));
         } else if (endOfAtom > 0 && c != endOfAtom) {
            return this.collectString(endOfAtom, keepEscapes);
         } else {
            ++this.currentPos;
            char[] ch = new char[]{c};
            return new Token(c, new String(ch));
         }
      }
   }

   private Token collectString(char eos, boolean keepEscapes) throws ParseException {
      boolean filter = false;

      int start;
      for(start = this.currentPos; this.currentPos < this.maxPos; ++this.currentPos) {
         char c = this.string.charAt(this.currentPos);
         if (c == '\\') {
            ++this.currentPos;
            filter = true;
         } else if (c == '\r') {
            filter = true;
         } else if (c == eos) {
            ++this.currentPos;
            String s;
            if (filter) {
               s = filterToken(this.string, start, this.currentPos - 1, keepEscapes);
            } else {
               s = this.string.substring(start, this.currentPos - 1);
            }

            if (c != '"') {
               s = trimWhiteSpace(s);
               --this.currentPos;
            }

            return new Token(-2, s);
         }
      }

      if (eos == '"') {
         throw new ParseException("Unbalanced quoted string");
      } else {
         String s;
         if (filter) {
            s = filterToken(this.string, start, this.currentPos, keepEscapes);
         } else {
            s = this.string.substring(start, this.currentPos);
         }

         s = trimWhiteSpace(s);
         return new Token(-2, s);
      }
   }

   private int skipWhiteSpace() {
      while(this.currentPos < this.maxPos) {
         char c;
         if ((c = this.string.charAt(this.currentPos)) != ' ' && c != '\t' && c != '\r' && c != '\n') {
            return this.currentPos;
         }

         ++this.currentPos;
      }

      return -4;
   }

   private static String trimWhiteSpace(String s) {
      char c;
      int i;
      for(i = s.length() - 1; i >= 0 && ((c = s.charAt(i)) == ' ' || c == '\t' || c == '\r' || c == '\n'); --i) {
      }

      return i <= 0 ? "" : s.substring(0, i + 1);
   }

   private static String filterToken(String s, int start, int end, boolean keepEscapes) {
      StringBuffer sb = new StringBuffer();
      boolean gotEscape = false;
      boolean gotCR = false;

      for(int i = start; i < end; ++i) {
         char c = s.charAt(i);
         if (c == '\n' && gotCR) {
            gotCR = false;
         } else {
            gotCR = false;
            if (!gotEscape) {
               if (c == '\\') {
                  gotEscape = true;
               } else if (c == '\r') {
                  gotCR = true;
               } else {
                  sb.append(c);
               }
            } else {
               if (keepEscapes) {
                  sb.append('\\');
               }

               sb.append(c);
               gotEscape = false;
            }
         }
      }

      return sb.toString();
   }

   public static class Token {
      private int type;
      private String value;
      public static final int ATOM = -1;
      public static final int QUOTEDSTRING = -2;
      public static final int COMMENT = -3;
      public static final int EOF = -4;

      public Token(int type, String value) {
         this.type = type;
         this.value = value;
      }

      public int getType() {
         return this.type;
      }

      public String getValue() {
         return this.value;
      }
   }
}
