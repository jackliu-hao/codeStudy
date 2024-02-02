package org.h2.command;

import org.h2.engine.CastDataProvider;
import org.h2.message.DbException;
import org.h2.util.StringUtils;
import org.h2.value.Value;
import org.h2.value.ValueBigint;
import org.h2.value.ValueInteger;
import org.h2.value.ValueVarbinary;
import org.h2.value.ValueVarchar;

public abstract class Token implements Cloneable {
   static final int PARAMETER = 92;
   static final int END_OF_INPUT = 93;
   static final int LITERAL = 94;
   static final int EQUAL = 95;
   static final int BIGGER_EQUAL = 96;
   static final int BIGGER = 97;
   static final int SMALLER = 98;
   static final int SMALLER_EQUAL = 99;
   static final int NOT_EQUAL = 100;
   static final int AT = 101;
   static final int MINUS_SIGN = 102;
   static final int PLUS_SIGN = 103;
   static final int CONCATENATION = 104;
   static final int OPEN_PAREN = 105;
   static final int CLOSE_PAREN = 106;
   static final int SPATIAL_INTERSECTS = 107;
   static final int ASTERISK = 108;
   static final int COMMA = 109;
   static final int DOT = 110;
   static final int OPEN_BRACE = 111;
   static final int CLOSE_BRACE = 112;
   static final int SLASH = 113;
   static final int PERCENT = 114;
   static final int SEMICOLON = 115;
   static final int COLON = 116;
   static final int OPEN_BRACKET = 117;
   static final int CLOSE_BRACKET = 118;
   static final int TILDE = 119;
   static final int COLON_COLON = 120;
   static final int COLON_EQ = 121;
   static final int NOT_TILDE = 122;
   static final String[] TOKENS = new String[]{null, null, null, "ALL", "AND", "ANY", "ARRAY", "AS", "ASYMMETRIC", "AUTHORIZATION", "BETWEEN", "CASE", "CAST", "CHECK", "CONSTRAINT", "CROSS", "CURRENT_CATALOG", "CURRENT_DATE", "CURRENT_PATH", "CURRENT_ROLE", "CURRENT_SCHEMA", "CURRENT_TIME", "CURRENT_TIMESTAMP", "CURRENT_USER", "DAY", "DEFAULT", "DISTINCT", "ELSE", "END", "EXCEPT", "EXISTS", "FALSE", "FETCH", "FOR", "FOREIGN", "FROM", "FULL", "GROUP", "HAVING", "HOUR", "IF", "IN", "INNER", "INTERSECT", "INTERVAL", "IS", "JOIN", "KEY", "LEFT", "LIKE", "LIMIT", "LOCALTIME", "LOCALTIMESTAMP", "MINUS", "MINUTE", "MONTH", "NATURAL", "NOT", "NULL", "OFFSET", "ON", "OR", "ORDER", "PRIMARY", "QUALIFY", "RIGHT", "ROW", "ROWNUM", "SECOND", "SELECT", "SESSION_USER", "SET", "SOME", "SYMMETRIC", "SYSTEM_USER", "TABLE", "TO", "TRUE", "UESCAPE", "UNION", "UNIQUE", "UNKNOWN", "USER", "USING", "VALUE", "VALUES", "WHEN", "WHERE", "WINDOW", "WITH", "YEAR", "_ROWID_", "?", null, null, "=", ">=", ">", "<", "<=", "<>", "@", "-", "+", "||", "(", ")", "&&", "*", ",", ".", "{", "}", "/", "%", ";", ":", "[", "]", "~", "::", ":=", "!~"};
   private int start;

   Token(int var1) {
      this.start = var1;
   }

   final int start() {
      return this.start;
   }

   final void setStart(int var1) {
      this.start = var1;
   }

   final void subtractFromStart(int var1) {
      this.start -= var1;
   }

   abstract int tokenType();

   String asIdentifier() {
      return null;
   }

   boolean isQuoted() {
      return false;
   }

   Value value(CastDataProvider var1) {
      return null;
   }

   boolean needsUnicodeConversion() {
      return false;
   }

   void convertUnicode(int var1) {
      throw DbException.getInternalError();
   }

   protected Token clone() {
      try {
         return (Token)super.clone();
      } catch (CloneNotSupportedException var2) {
         throw DbException.getInternalError();
      }
   }

   static final class EndOfInputToken extends Token {
      EndOfInputToken(int var1) {
         super(var1);
      }

      int tokenType() {
         return 93;
      }
   }

   static final class ParameterToken extends Token {
      int index;

      ParameterToken(int var1, int var2) {
         super(var1);
         this.index = var2;
      }

      int tokenType() {
         return 92;
      }

      String asIdentifier() {
         return "?";
      }

      int index() {
         return this.index;
      }

      public String toString() {
         return this.index == 0 ? "?" : "?" + this.index;
      }
   }

   static final class ValueToken extends LiteralToken {
      ValueToken(int var1, Value var2) {
         super(var1);
         this.value = var2;
      }

      Value value(CastDataProvider var1) {
         return this.value;
      }
   }

   static final class BigintToken extends LiteralToken {
      private final long number;

      BigintToken(int var1, long var2) {
         super(var1);
         this.number = var2;
      }

      Value value(CastDataProvider var1) {
         if (this.value == null) {
            this.value = ValueBigint.get(this.number);
         }

         return this.value;
      }
   }

   static final class IntegerToken extends LiteralToken {
      private final int number;

      IntegerToken(int var1, int var2) {
         super(var1);
         this.number = var2;
      }

      Value value(CastDataProvider var1) {
         if (this.value == null) {
            this.value = ValueInteger.get(this.number);
         }

         return this.value;
      }
   }

   static final class CharacterStringToken extends LiteralToken {
      String string;
      private boolean unicode;

      CharacterStringToken(int var1, String var2, boolean var3) {
         super(var1);
         this.string = var2;
         this.unicode = var3;
      }

      Value value(CastDataProvider var1) {
         if (this.value == null) {
            this.value = ValueVarchar.get(this.string, var1);
         }

         return this.value;
      }

      boolean needsUnicodeConversion() {
         return this.unicode;
      }

      void convertUnicode(int var1) {
         if (this.unicode) {
            this.string = StringUtils.decodeUnicodeStringSQL(this.string, var1);
            this.unicode = false;
         } else {
            throw DbException.getInternalError();
         }
      }
   }

   static final class BinaryStringToken extends LiteralToken {
      private final byte[] string;

      BinaryStringToken(int var1, byte[] var2) {
         super(var1);
         this.string = var2;
      }

      Value value(CastDataProvider var1) {
         if (this.value == null) {
            this.value = ValueVarbinary.getNoCopy(this.string);
         }

         return this.value;
      }
   }

   abstract static class LiteralToken extends Token {
      Value value;

      LiteralToken(int var1) {
         super(var1);
      }

      final int tokenType() {
         return 94;
      }

      public final String toString() {
         return this.value((CastDataProvider)null).getTraceSQL();
      }
   }

   static final class KeywordOrIdentifierToken extends Token {
      private final int type;
      private final String identifier;

      KeywordOrIdentifierToken(int var1, int var2, String var3) {
         super(var1);
         this.type = var2;
         this.identifier = var3;
      }

      int tokenType() {
         return this.type;
      }

      String asIdentifier() {
         return this.identifier;
      }

      public String toString() {
         return this.identifier;
      }
   }

   static final class KeywordToken extends Token {
      private final int type;

      KeywordToken(int var1, int var2) {
         super(var1);
         this.type = var2;
      }

      int tokenType() {
         return this.type;
      }

      String asIdentifier() {
         return TOKENS[this.type];
      }

      public String toString() {
         return TOKENS[this.type];
      }
   }

   static class IdentifierToken extends Token {
      private String identifier;
      private final boolean quoted;
      private boolean unicode;

      IdentifierToken(int var1, String var2, boolean var3, boolean var4) {
         super(var1);
         this.identifier = var2;
         this.quoted = var3;
         this.unicode = var4;
      }

      int tokenType() {
         return 2;
      }

      String asIdentifier() {
         return this.identifier;
      }

      boolean isQuoted() {
         return this.quoted;
      }

      boolean needsUnicodeConversion() {
         return this.unicode;
      }

      void convertUnicode(int var1) {
         if (this.unicode) {
            this.identifier = StringUtils.decodeUnicodeStringSQL(this.identifier, var1);
            this.unicode = false;
         } else {
            throw DbException.getInternalError();
         }
      }

      public String toString() {
         return this.quoted ? StringUtils.quoteIdentifier(this.identifier) : this.identifier;
      }
   }
}
