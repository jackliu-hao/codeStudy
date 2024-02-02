package org.apache.http.message;

import java.util.BitSet;
import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.util.CharArrayBuffer;

@Contract(
   threading = ThreadingBehavior.IMMUTABLE
)
public class TokenParser {
   public static final char CR = '\r';
   public static final char LF = '\n';
   public static final char SP = ' ';
   public static final char HT = '\t';
   public static final char DQUOTE = '"';
   public static final char ESCAPE = '\\';
   public static final TokenParser INSTANCE = new TokenParser();

   public static BitSet INIT_BITSET(int... b) {
      BitSet bitset = new BitSet();
      int[] arr$ = b;
      int len$ = b.length;

      for(int i$ = 0; i$ < len$; ++i$) {
         int aB = arr$[i$];
         bitset.set(aB);
      }

      return bitset;
   }

   public static boolean isWhitespace(char ch) {
      return ch == ' ' || ch == '\t' || ch == '\r' || ch == '\n';
   }

   public String parseToken(CharArrayBuffer buf, ParserCursor cursor, BitSet delimiters) {
      StringBuilder dst = new StringBuilder();
      boolean whitespace = false;

      while(!cursor.atEnd()) {
         char current = buf.charAt(cursor.getPos());
         if (delimiters != null && delimiters.get(current)) {
            break;
         }

         if (isWhitespace(current)) {
            this.skipWhiteSpace(buf, cursor);
            whitespace = true;
         } else {
            if (whitespace && dst.length() > 0) {
               dst.append(' ');
            }

            this.copyContent(buf, cursor, delimiters, dst);
            whitespace = false;
         }
      }

      return dst.toString();
   }

   public String parseValue(CharArrayBuffer buf, ParserCursor cursor, BitSet delimiters) {
      StringBuilder dst = new StringBuilder();
      boolean whitespace = false;

      while(!cursor.atEnd()) {
         char current = buf.charAt(cursor.getPos());
         if (delimiters != null && delimiters.get(current)) {
            break;
         }

         if (isWhitespace(current)) {
            this.skipWhiteSpace(buf, cursor);
            whitespace = true;
         } else if (current == '"') {
            if (whitespace && dst.length() > 0) {
               dst.append(' ');
            }

            this.copyQuotedContent(buf, cursor, dst);
            whitespace = false;
         } else {
            if (whitespace && dst.length() > 0) {
               dst.append(' ');
            }

            this.copyUnquotedContent(buf, cursor, delimiters, dst);
            whitespace = false;
         }
      }

      return dst.toString();
   }

   public void skipWhiteSpace(CharArrayBuffer buf, ParserCursor cursor) {
      int pos = cursor.getPos();
      int indexFrom = cursor.getPos();
      int indexTo = cursor.getUpperBound();

      for(int i = indexFrom; i < indexTo; ++i) {
         char current = buf.charAt(i);
         if (!isWhitespace(current)) {
            break;
         }

         ++pos;
      }

      cursor.updatePos(pos);
   }

   public void copyContent(CharArrayBuffer buf, ParserCursor cursor, BitSet delimiters, StringBuilder dst) {
      int pos = cursor.getPos();
      int indexFrom = cursor.getPos();
      int indexTo = cursor.getUpperBound();

      for(int i = indexFrom; i < indexTo; ++i) {
         char current = buf.charAt(i);
         if (delimiters != null && delimiters.get(current) || isWhitespace(current)) {
            break;
         }

         ++pos;
         dst.append(current);
      }

      cursor.updatePos(pos);
   }

   public void copyUnquotedContent(CharArrayBuffer buf, ParserCursor cursor, BitSet delimiters, StringBuilder dst) {
      int pos = cursor.getPos();
      int indexFrom = cursor.getPos();
      int indexTo = cursor.getUpperBound();

      for(int i = indexFrom; i < indexTo; ++i) {
         char current = buf.charAt(i);
         if (delimiters != null && delimiters.get(current) || isWhitespace(current) || current == '"') {
            break;
         }

         ++pos;
         dst.append(current);
      }

      cursor.updatePos(pos);
   }

   public void copyQuotedContent(CharArrayBuffer buf, ParserCursor cursor, StringBuilder dst) {
      if (!cursor.atEnd()) {
         int pos = cursor.getPos();
         int indexFrom = cursor.getPos();
         int indexTo = cursor.getUpperBound();
         char current = buf.charAt(pos);
         if (current == '"') {
            ++pos;
            ++indexFrom;
            boolean escaped = false;

            for(int i = indexFrom; i < indexTo; ++pos) {
               current = buf.charAt(i);
               if (escaped) {
                  if (current != '"' && current != '\\') {
                     dst.append('\\');
                  }

                  dst.append(current);
                  escaped = false;
               } else {
                  if (current == '"') {
                     ++pos;
                     break;
                  }

                  if (current == '\\') {
                     escaped = true;
                  } else if (current != '\r' && current != '\n') {
                     dst.append(current);
                  }
               }

               ++i;
            }

            cursor.updatePos(pos);
         }
      }
   }
}
