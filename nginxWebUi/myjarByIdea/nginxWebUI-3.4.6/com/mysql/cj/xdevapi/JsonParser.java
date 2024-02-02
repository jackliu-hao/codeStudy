package com.mysql.cj.xdevapi;

import com.mysql.cj.Messages;
import com.mysql.cj.exceptions.AssertionFailedException;
import com.mysql.cj.exceptions.ExceptionFactory;
import com.mysql.cj.exceptions.WrongArgumentException;
import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class JsonParser {
   static Set<Character> whitespaceChars = new HashSet();
   static HashMap<Character, Character> unescapeChars = new HashMap();

   private static boolean isValidEndOfValue(char ch) {
      return JsonParser.StructuralToken.COMMA.CHAR == ch || JsonParser.StructuralToken.RCRBRACKET.CHAR == ch || JsonParser.StructuralToken.RSQBRACKET.CHAR == ch;
   }

   public static DbDoc parseDoc(String jsonString) {
      try {
         return parseDoc(new StringReader(jsonString));
      } catch (IOException var2) {
         throw AssertionFailedException.shouldNotHappen((Exception)var2);
      }
   }

   public static DbDoc parseDoc(StringReader reader) throws IOException {
      DbDoc doc = new DbDocImpl();
      int leftBrackets = 0;
      int rightBrackets = 0;

      int intch;
      while((intch = reader.read()) != -1) {
         String key = null;
         char ch = (char)intch;
         if (ch != JsonParser.StructuralToken.LCRBRACKET.CHAR && ch != JsonParser.StructuralToken.COMMA.CHAR) {
            if (ch == JsonParser.StructuralToken.RCRBRACKET.CHAR) {
               ++rightBrackets;
               break;
            }

            if (!whitespaceChars.contains(ch)) {
               throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, Messages.getString("JsonParser.1", new Character[]{ch}));
            }
         } else {
            if (ch == JsonParser.StructuralToken.LCRBRACKET.CHAR) {
               ++leftBrackets;
            }

            if ((key = nextKey(reader)) != null) {
               try {
                  JsonValue val;
                  if ((val = nextValue(reader)) != null) {
                     doc.put(key, val);
                  } else {
                     reader.reset();
                  }
               } catch (WrongArgumentException var9) {
                  throw (WrongArgumentException)ExceptionFactory.createException((Class)WrongArgumentException.class, (String)Messages.getString("JsonParser.0", new String[]{key}), (Throwable)var9);
               }
            } else {
               reader.reset();
            }
         }
      }

      if (leftBrackets == 0) {
         throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, Messages.getString("JsonParser.2"));
      } else if (leftBrackets > rightBrackets) {
         throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, Messages.getString("JsonParser.3", new Character[]{JsonParser.StructuralToken.RCRBRACKET.CHAR}));
      } else {
         return doc;
      }
   }

   public static JsonArray parseArray(StringReader reader) throws IOException {
      JsonArray arr = new JsonArray();
      int openings = 0;

      int intch;
      while((intch = reader.read()) != -1) {
         char ch = (char)intch;
         if (ch != JsonParser.StructuralToken.LSQBRACKET.CHAR && ch != JsonParser.StructuralToken.COMMA.CHAR) {
            if (ch == JsonParser.StructuralToken.RSQBRACKET.CHAR) {
               --openings;
               break;
            }

            if (!whitespaceChars.contains(ch)) {
               throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, Messages.getString("JsonParser.1", new Character[]{ch}));
            }
         } else {
            if (ch == JsonParser.StructuralToken.LSQBRACKET.CHAR) {
               ++openings;
            }

            JsonValue val;
            if ((val = nextValue(reader)) != null) {
               arr.add(val);
            } else {
               reader.reset();
            }
         }
      }

      if (openings > 0) {
         throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, Messages.getString("JsonParser.3", new Character[]{JsonParser.StructuralToken.RSQBRACKET.CHAR}));
      } else {
         return arr;
      }
   }

   private static String nextKey(StringReader reader) throws IOException {
      reader.mark(1);
      JsonString val = parseString(reader);
      if (val == null) {
         reader.reset();
      }

      char ch = ' ';

      int intch;
      while((intch = reader.read()) != -1) {
         ch = (char)intch;
         if (ch == JsonParser.StructuralToken.COLON.CHAR || ch == JsonParser.StructuralToken.RCRBRACKET.CHAR) {
            break;
         }

         if (!whitespaceChars.contains(ch)) {
            throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, Messages.getString("JsonParser.1", new Character[]{ch}));
         }
      }

      if (ch != JsonParser.StructuralToken.COLON.CHAR && val != null && val.getString().length() > 0) {
         throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, Messages.getString("JsonParser.4", new String[]{val.getString()}));
      } else {
         return val != null ? val.getString() : null;
      }
   }

   private static JsonValue nextValue(StringReader reader) throws IOException {
      reader.mark(1);

      int intch;
      while((intch = reader.read()) != -1) {
         char ch = (char)intch;
         if (ch == JsonParser.EscapeChar.QUOTE.CHAR) {
            reader.reset();
            return parseString(reader);
         }

         if (ch == JsonParser.StructuralToken.LSQBRACKET.CHAR) {
            reader.reset();
            return parseArray(reader);
         }

         if (ch == JsonParser.StructuralToken.LCRBRACKET.CHAR) {
            reader.reset();
            return parseDoc(reader);
         }

         if (ch == '-' || ch >= '0' && ch <= '9') {
            reader.reset();
            return parseNumber(reader);
         }

         if (ch == JsonLiteral.TRUE.value.charAt(0)) {
            reader.reset();
            return parseLiteral(reader);
         }

         if (ch == JsonLiteral.FALSE.value.charAt(0)) {
            reader.reset();
            return parseLiteral(reader);
         }

         if (ch == JsonLiteral.NULL.value.charAt(0)) {
            reader.reset();
            return parseLiteral(reader);
         }

         if (ch == JsonParser.StructuralToken.RSQBRACKET.CHAR) {
            return null;
         }

         if (!whitespaceChars.contains(ch)) {
            throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, Messages.getString("JsonParser.1", new Character[]{ch}));
         }

         reader.mark(1);
      }

      throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, Messages.getString("JsonParser.5"));
   }

   private static void appendChar(StringBuilder sb, char ch) {
      if (sb == null) {
         if (!whitespaceChars.contains(ch)) {
            throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, Messages.getString("JsonParser.6", new Character[]{ch}));
         }
      } else {
         sb.append(ch);
      }

   }

   static JsonString parseString(StringReader reader) throws IOException {
      int quotes = 0;
      boolean escapeNextChar = false;
      StringBuilder sb = null;

      int intch;
      while((intch = reader.read()) != -1) {
         char ch = (char)intch;
         if (escapeNextChar) {
            if (!unescapeChars.containsKey(ch)) {
               throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, Messages.getString("JsonParser.7", new Character[]{ch}));
            }

            appendChar(sb, (Character)unescapeChars.get(ch));
            escapeNextChar = false;
         } else if (ch == JsonParser.EscapeChar.QUOTE.CHAR) {
            if (sb != null) {
               --quotes;
               break;
            }

            sb = new StringBuilder();
            ++quotes;
         } else {
            if (quotes == 0 && ch == JsonParser.StructuralToken.RCRBRACKET.CHAR) {
               break;
            }

            if (ch == JsonParser.EscapeChar.RSOLIDUS.CHAR) {
               escapeNextChar = true;
            } else {
               appendChar(sb, ch);
            }
         }
      }

      if (quotes > 0) {
         throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, Messages.getString("JsonParser.3", new Character[]{JsonParser.EscapeChar.QUOTE.CHAR}));
      } else {
         return sb == null ? null : (new JsonString()).setValue(sb.toString());
      }
   }

   static JsonNumber parseNumber(StringReader reader) throws IOException {
      StringBuilder sb = null;
      char lastChar = ' ';
      boolean hasFractionalPart = false;
      boolean hasExponent = false;

      int intch;
      while((intch = reader.read()) != -1) {
         char ch = (char)intch;
         if (sb == null) {
            if (ch == '-') {
               sb = new StringBuilder();
               sb.append(ch);
            } else if (ch >= '0' && ch <= '9') {
               sb = new StringBuilder();
               sb.append(ch);
            } else if (!whitespaceChars.contains(ch)) {
               throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, Messages.getString("JsonParser.1", new Character[]{ch}));
            }
         } else if (ch == '-') {
            if (lastChar != 'E' && lastChar != 'e') {
               throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, Messages.getString("JsonParser.8", new Object[]{ch, sb.toString()}));
            }

            sb.append(ch);
         } else if (ch >= '0' && ch <= '9') {
            sb.append(ch);
         } else if (ch != 'E' && ch != 'e') {
            if (ch == '.') {
               if (hasFractionalPart) {
                  throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, Messages.getString("JsonParser.10", new Object[]{ch, sb.toString()}));
               }

               if (hasExponent) {
                  throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, Messages.getString("JsonParser.11"));
               }

               if (lastChar < '0' || lastChar > '9') {
                  throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, Messages.getString("JsonParser.8", new Object[]{ch, sb.toString()}));
               }

               hasFractionalPart = true;
               sb.append(ch);
            } else {
               if (ch != '+') {
                  if (!whitespaceChars.contains(ch) && !isValidEndOfValue(ch)) {
                     throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, Messages.getString("JsonParser.1", new Character[]{ch}));
                  }

                  reader.reset();
                  break;
               }

               if (lastChar != 'E' && lastChar != 'e') {
                  throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, Messages.getString("JsonParser.8", new Object[]{ch, sb.toString()}));
               }

               sb.append(ch);
            }
         } else {
            if (lastChar < '0' || lastChar > '9') {
               throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, Messages.getString("JsonParser.8", new Object[]{ch, sb.toString()}));
            }

            hasExponent = true;
            sb.append(ch);
         }

         lastChar = ch;
         reader.mark(1);
      }

      if (sb != null && sb.length() != 0) {
         return (new JsonNumber()).setValue(sb.toString());
      } else {
         throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, Messages.getString("JsonParser.5"));
      }
   }

   static JsonLiteral parseLiteral(StringReader reader) throws IOException {
      StringBuilder sb = null;
      JsonLiteral res = null;

      int literalIndex;
      int intch;
      for(literalIndex = 0; (intch = reader.read()) != -1; reader.mark(1)) {
         char ch = (char)intch;
         if (sb == null) {
            if (ch == JsonLiteral.TRUE.value.charAt(0)) {
               res = JsonLiteral.TRUE;
               sb = new StringBuilder();
               sb.append(ch);
               ++literalIndex;
            } else if (ch == JsonLiteral.FALSE.value.charAt(0)) {
               res = JsonLiteral.FALSE;
               sb = new StringBuilder();
               sb.append(ch);
               ++literalIndex;
            } else if (ch == JsonLiteral.NULL.value.charAt(0)) {
               res = JsonLiteral.NULL;
               sb = new StringBuilder();
               sb.append(ch);
               ++literalIndex;
            } else if (!whitespaceChars.contains(ch)) {
               throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, Messages.getString("JsonParser.1", new Character[]{ch}));
            }
         } else {
            if (literalIndex >= res.value.length() || ch != res.value.charAt(literalIndex)) {
               if (!whitespaceChars.contains(ch) && !isValidEndOfValue(ch)) {
                  throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, Messages.getString("JsonParser.1", new Character[]{ch}));
               }

               reader.reset();
               break;
            }

            sb.append(ch);
            ++literalIndex;
         }
      }

      if (sb == null) {
         throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, Messages.getString("JsonParser.5"));
      } else if (literalIndex == res.value.length()) {
         return res;
      } else {
         throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, Messages.getString("JsonParser.12", new String[]{sb.toString()}));
      }
   }

   static {
      EscapeChar[] var0 = JsonParser.EscapeChar.values();
      int var1 = var0.length;

      int var2;
      for(var2 = 0; var2 < var1; ++var2) {
         EscapeChar ec = var0[var2];
         unescapeChars.put(ec.ESCAPED.charAt(1), ec.CHAR);
      }

      Whitespace[] var4 = JsonParser.Whitespace.values();
      var1 = var4.length;

      for(var2 = 0; var2 < var1; ++var2) {
         Whitespace ws = var4[var2];
         whitespaceChars.add(ws.CHAR);
      }

   }

   static enum EscapeChar {
      QUOTE('"', "\\\""),
      RSOLIDUS('\\', "\\\\"),
      SOLIDUS('/', "\\/"),
      BACKSPACE('\b', "\\b"),
      FF('\f', "\\f"),
      LF('\n', "\\n"),
      CR('\r', "\\r"),
      TAB('\t', "\\t");

      public final char CHAR;
      public final String ESCAPED;

      private EscapeChar(char character, String escaped) {
         this.CHAR = character;
         this.ESCAPED = escaped;
      }
   }

   static enum StructuralToken {
      LSQBRACKET('['),
      RSQBRACKET(']'),
      LCRBRACKET('{'),
      RCRBRACKET('}'),
      COLON(':'),
      COMMA(',');

      public final char CHAR;

      private StructuralToken(char character) {
         this.CHAR = character;
      }
   }

   static enum Whitespace {
      TAB('\t'),
      LF('\n'),
      CR('\r'),
      SPACE(' ');

      public final char CHAR;

      private Whitespace(char character) {
         this.CHAR = character;
      }
   }
}
