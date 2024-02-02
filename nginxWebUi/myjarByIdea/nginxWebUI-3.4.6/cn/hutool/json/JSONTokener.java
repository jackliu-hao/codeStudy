package cn.hutool.json;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.StrUtil;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;

public class JSONTokener {
   private long character;
   private boolean eof;
   private long index;
   private long line;
   private char previous;
   private boolean usePrevious;
   private final Reader reader;
   private final JSONConfig config;

   public JSONTokener(Reader reader, JSONConfig config) {
      this.reader = (Reader)(reader.markSupported() ? reader : new BufferedReader(reader));
      this.eof = false;
      this.usePrevious = false;
      this.previous = 0;
      this.index = 0L;
      this.character = 1L;
      this.line = 1L;
      this.config = config;
   }

   public JSONTokener(InputStream inputStream, JSONConfig config) throws JSONException {
      this((Reader)IoUtil.getUtf8Reader(inputStream), config);
   }

   public JSONTokener(CharSequence s, JSONConfig config) {
      this((Reader)(new StringReader(StrUtil.str(s))), config);
   }

   public void back() throws JSONException {
      if (!this.usePrevious && this.index > 0L) {
         --this.index;
         --this.character;
         this.usePrevious = true;
         this.eof = false;
      } else {
         throw new JSONException("Stepping back two steps is not supported");
      }
   }

   public boolean end() {
      return this.eof && !this.usePrevious;
   }

   public boolean more() throws JSONException {
      this.next();
      if (this.end()) {
         return false;
      } else {
         this.back();
         return true;
      }
   }

   public char next() throws JSONException {
      int c;
      if (this.usePrevious) {
         this.usePrevious = false;
         c = this.previous;
      } else {
         try {
            c = this.reader.read();
         } catch (IOException var3) {
            throw new JSONException(var3);
         }

         if (c <= 0) {
            this.eof = true;
            c = 0;
         }
      }

      ++this.index;
      if (this.previous == '\r') {
         ++this.line;
         this.character = c == 10 ? 0L : 1L;
      } else if (c == 10) {
         ++this.line;
         this.character = 0L;
      } else {
         ++this.character;
      }

      this.previous = (char)c;
      return this.previous;
   }

   public char next(char c) throws JSONException {
      char n = this.next();
      if (n != c) {
         throw this.syntaxError("Expected '" + c + "' and instead saw '" + n + "'");
      } else {
         return n;
      }
   }

   public String next(int n) throws JSONException {
      if (n == 0) {
         return "";
      } else {
         char[] chars = new char[n];

         for(int pos = 0; pos < n; ++pos) {
            chars[pos] = this.next();
            if (this.end()) {
               throw this.syntaxError("Substring bounds error");
            }
         }

         return new String(chars);
      }
   }

   public char nextClean() throws JSONException {
      char c;
      do {
         c = this.next();
      } while(c != 0 && c <= ' ');

      return c;
   }

   public String nextString(char quote) throws JSONException {
      StringBuilder sb = new StringBuilder();

      while(true) {
         char c = this.next();
         switch (c) {
            case '\u0000':
            case '\n':
            case '\r':
               throw this.syntaxError("Unterminated string");
            case '\\':
               c = this.next();
               switch (c) {
                  case '"':
                  case '\'':
                  case '/':
                  case '\\':
                     sb.append(c);
                     continue;
                  case 'b':
                     sb.append('\b');
                     continue;
                  case 'f':
                     sb.append('\f');
                     continue;
                  case 'n':
                     sb.append('\n');
                     continue;
                  case 'r':
                     sb.append('\r');
                     continue;
                  case 't':
                     sb.append('\t');
                     continue;
                  case 'u':
                     sb.append((char)Integer.parseInt(this.next((int)4), 16));
                     continue;
                  default:
                     throw this.syntaxError("Illegal escape.");
               }
            default:
               if (c == quote) {
                  return sb.toString();
               }

               sb.append(c);
         }
      }
   }

   public String nextTo(char delimiter) throws JSONException {
      StringBuilder sb = new StringBuilder();

      while(true) {
         char c = this.next();
         if (c == delimiter || c == 0 || c == '\n' || c == '\r') {
            if (c != 0) {
               this.back();
            }

            return sb.toString().trim();
         }

         sb.append(c);
      }
   }

   public String nextTo(String delimiters) throws JSONException {
      StringBuilder sb = new StringBuilder();

      while(true) {
         char c = this.next();
         if (delimiters.indexOf(c) >= 0 || c == 0 || c == '\n' || c == '\r') {
            if (c != 0) {
               this.back();
            }

            return sb.toString().trim();
         }

         sb.append(c);
      }
   }

   public Object nextValue() throws JSONException {
      char c = this.nextClean();
      switch (c) {
         case '"':
         case '\'':
            return this.nextString(c);
         case '[':
            this.back();
            return new JSONArray(this, this.config);
         case '{':
            this.back();
            return new JSONObject(this, this.config);
         default:
            StringBuilder sb;
            for(sb = new StringBuilder(); c >= ' ' && ",:]}/\\\"[{;=#".indexOf(c) < 0; c = this.next()) {
               sb.append(c);
            }

            this.back();
            String string = sb.toString().trim();
            if (0 == string.length()) {
               throw this.syntaxError("Missing value");
            } else {
               return InternalJSONUtil.stringToValue(string);
            }
      }
   }

   public char skipTo(char to) throws JSONException {
      char c;
      try {
         long startIndex = this.index;
         long startCharacter = this.character;
         long startLine = this.line;
         this.reader.mark(1000000);

         do {
            c = this.next();
            if (c == 0) {
               this.reader.reset();
               this.index = startIndex;
               this.character = startCharacter;
               this.line = startLine;
               return c;
            }
         } while(c != to);
      } catch (IOException var9) {
         throw new JSONException(var9);
      }

      this.back();
      return c;
   }

   public JSONException syntaxError(String message) {
      return new JSONException(message + this);
   }

   public JSONArray toJSONArray() {
      JSONArray jsonArray = new JSONArray(this.config);
      if (this.nextClean() != '[') {
         throw this.syntaxError("A JSONArray text must start with '['");
      } else if (this.nextClean() != ']') {
         this.back();

         while(true) {
            if (this.nextClean() == ',') {
               this.back();
               jsonArray.add(JSONNull.NULL);
            } else {
               this.back();
               jsonArray.add(this.nextValue());
            }

            switch (this.nextClean()) {
               case ',':
                  if (this.nextClean() == ']') {
                     return jsonArray;
                  }

                  this.back();
                  break;
               case ']':
                  return jsonArray;
               default:
                  throw this.syntaxError("Expected a ',' or ']'");
            }
         }
      } else {
         return jsonArray;
      }
   }

   public String toString() {
      return " at " + this.index + " [character " + this.character + " line " + this.line + "]";
   }
}
