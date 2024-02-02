/*     */ package org.h2.command;
/*     */ 
/*     */ import org.h2.engine.CastDataProvider;
/*     */ import org.h2.message.DbException;
/*     */ import org.h2.util.StringUtils;
/*     */ import org.h2.value.Value;
/*     */ import org.h2.value.ValueBigint;
/*     */ import org.h2.value.ValueInteger;
/*     */ import org.h2.value.ValueVarbinary;
/*     */ import org.h2.value.ValueVarchar;
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
/*     */ public abstract class Token
/*     */   implements Cloneable
/*     */ {
/*     */   static final int PARAMETER = 92;
/*     */   static final int END_OF_INPUT = 93;
/*     */   static final int LITERAL = 94;
/*     */   static final int EQUAL = 95;
/*     */   static final int BIGGER_EQUAL = 96;
/*     */   static final int BIGGER = 97;
/*     */   static final int SMALLER = 98;
/*     */   static final int SMALLER_EQUAL = 99;
/*     */   static final int NOT_EQUAL = 100;
/*     */   static final int AT = 101;
/*     */   static final int MINUS_SIGN = 102;
/*     */   static final int PLUS_SIGN = 103;
/*     */   static final int CONCATENATION = 104;
/*     */   static final int OPEN_PAREN = 105;
/*     */   static final int CLOSE_PAREN = 106;
/*     */   static final int SPATIAL_INTERSECTS = 107;
/*     */   static final int ASTERISK = 108;
/*     */   static final int COMMA = 109;
/*     */   static final int DOT = 110;
/*     */   static final int OPEN_BRACE = 111;
/*     */   static final int CLOSE_BRACE = 112;
/*     */   static final int SLASH = 113;
/*     */   static final int PERCENT = 114;
/*     */   static final int SEMICOLON = 115;
/*     */   static final int COLON = 116;
/*     */   static final int OPEN_BRACKET = 117;
/*     */   static final int CLOSE_BRACKET = 118;
/*     */   static final int TILDE = 119;
/*     */   static final int COLON_COLON = 120;
/*     */   static final int COLON_EQ = 121;
/*     */   static final int NOT_TILDE = 122;
/* 180 */   static final String[] TOKENS = new String[] { null, null, null, "ALL", "AND", "ANY", "ARRAY", "AS", "ASYMMETRIC", "AUTHORIZATION", "BETWEEN", "CASE", "CAST", "CHECK", "CONSTRAINT", "CROSS", "CURRENT_CATALOG", "CURRENT_DATE", "CURRENT_PATH", "CURRENT_ROLE", "CURRENT_SCHEMA", "CURRENT_TIME", "CURRENT_TIMESTAMP", "CURRENT_USER", "DAY", "DEFAULT", "DISTINCT", "ELSE", "END", "EXCEPT", "EXISTS", "FALSE", "FETCH", "FOR", "FOREIGN", "FROM", "FULL", "GROUP", "HAVING", "HOUR", "IF", "IN", "INNER", "INTERSECT", "INTERVAL", "IS", "JOIN", "KEY", "LEFT", "LIKE", "LIMIT", "LOCALTIME", "LOCALTIMESTAMP", "MINUS", "MINUTE", "MONTH", "NATURAL", "NOT", "NULL", "OFFSET", "ON", "OR", "ORDER", "PRIMARY", "QUALIFY", "RIGHT", "ROW", "ROWNUM", "SECOND", "SELECT", "SESSION_USER", "SET", "SOME", "SYMMETRIC", "SYSTEM_USER", "TABLE", "TO", "TRUE", "UESCAPE", "UNION", "UNIQUE", "UNKNOWN", "USER", "USING", "VALUE", "VALUES", "WHEN", "WHERE", "WINDOW", "WITH", "YEAR", "_ROWID_", "?", null, null, "=", ">=", ">", "<", "<=", "<>", "@", "-", "+", "||", "(", ")", "&&", "*", ",", ".", "{", "}", "/", "%", ";", ":", "[", "]", "~", "::", ":=", "!~" };
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
/*     */   private int start;
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
/*     */   static class IdentifierToken
/*     */     extends Token
/*     */   {
/*     */     private String identifier;
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
/*     */     private final boolean quoted;
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
/*     */     private boolean unicode;
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
/*     */     IdentifierToken(int param1Int, String param1String, boolean param1Boolean1, boolean param1Boolean2) {
/* 439 */       super(param1Int);
/* 440 */       this.identifier = param1String;
/* 441 */       this.quoted = param1Boolean1;
/* 442 */       this.unicode = param1Boolean2;
/*     */     }
/*     */ 
/*     */     
/*     */     int tokenType() {
/* 447 */       return 2;
/*     */     }
/*     */ 
/*     */     
/*     */     String asIdentifier() {
/* 452 */       return this.identifier;
/*     */     }
/*     */ 
/*     */     
/*     */     boolean isQuoted() {
/* 457 */       return this.quoted;
/*     */     }
/*     */ 
/*     */     
/*     */     boolean needsUnicodeConversion() {
/* 462 */       return this.unicode;
/*     */     }
/*     */ 
/*     */     
/*     */     void convertUnicode(int param1Int) {
/* 467 */       if (this.unicode) {
/* 468 */         this.identifier = StringUtils.decodeUnicodeStringSQL(this.identifier, param1Int);
/* 469 */         this.unicode = false;
/*     */       } else {
/* 471 */         throw DbException.getInternalError();
/*     */       } 
/*     */     }
/*     */ 
/*     */     
/*     */     public String toString() {
/* 477 */       return this.quoted ? StringUtils.quoteIdentifier(this.identifier) : this.identifier;
/*     */     }
/*     */   }
/*     */   
/*     */   static final class KeywordToken
/*     */     extends Token
/*     */   {
/*     */     private final int type;
/*     */     
/*     */     KeywordToken(int param1Int1, int param1Int2) {
/* 487 */       super(param1Int1);
/* 488 */       this.type = param1Int2;
/*     */     }
/*     */ 
/*     */     
/*     */     int tokenType() {
/* 493 */       return this.type;
/*     */     }
/*     */ 
/*     */     
/*     */     String asIdentifier() {
/* 498 */       return TOKENS[this.type];
/*     */     }
/*     */ 
/*     */     
/*     */     public String toString() {
/* 503 */       return TOKENS[this.type];
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   static final class KeywordOrIdentifierToken
/*     */     extends Token
/*     */   {
/*     */     private final int type;
/*     */     private final String identifier;
/*     */     
/*     */     KeywordOrIdentifierToken(int param1Int1, int param1Int2, String param1String) {
/* 515 */       super(param1Int1);
/* 516 */       this.type = param1Int2;
/* 517 */       this.identifier = param1String;
/*     */     }
/*     */ 
/*     */     
/*     */     int tokenType() {
/* 522 */       return this.type;
/*     */     }
/*     */ 
/*     */     
/*     */     String asIdentifier() {
/* 527 */       return this.identifier;
/*     */     }
/*     */ 
/*     */     
/*     */     public String toString() {
/* 532 */       return this.identifier;
/*     */     }
/*     */   }
/*     */   
/*     */   static abstract class LiteralToken
/*     */     extends Token
/*     */   {
/*     */     Value value;
/*     */     
/*     */     LiteralToken(int param1Int) {
/* 542 */       super(param1Int);
/*     */     }
/*     */ 
/*     */     
/*     */     final int tokenType() {
/* 547 */       return 94;
/*     */     }
/*     */ 
/*     */     
/*     */     public final String toString() {
/* 552 */       return value(null).getTraceSQL();
/*     */     }
/*     */   }
/*     */   
/*     */   static final class BinaryStringToken
/*     */     extends LiteralToken
/*     */   {
/*     */     private final byte[] string;
/*     */     
/*     */     BinaryStringToken(int param1Int, byte[] param1ArrayOfbyte) {
/* 562 */       super(param1Int);
/* 563 */       this.string = param1ArrayOfbyte;
/*     */     }
/*     */ 
/*     */     
/*     */     Value value(CastDataProvider param1CastDataProvider) {
/* 568 */       if (this.value == null) {
/* 569 */         this.value = (Value)ValueVarbinary.getNoCopy(this.string);
/*     */       }
/* 571 */       return this.value;
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   static final class CharacterStringToken
/*     */     extends LiteralToken
/*     */   {
/*     */     String string;
/*     */     private boolean unicode;
/*     */     
/*     */     CharacterStringToken(int param1Int, String param1String, boolean param1Boolean) {
/* 583 */       super(param1Int);
/* 584 */       this.string = param1String;
/* 585 */       this.unicode = param1Boolean;
/*     */     }
/*     */ 
/*     */     
/*     */     Value value(CastDataProvider param1CastDataProvider) {
/* 590 */       if (this.value == null) {
/* 591 */         this.value = ValueVarchar.get(this.string, param1CastDataProvider);
/*     */       }
/* 593 */       return this.value;
/*     */     }
/*     */ 
/*     */     
/*     */     boolean needsUnicodeConversion() {
/* 598 */       return this.unicode;
/*     */     }
/*     */ 
/*     */     
/*     */     void convertUnicode(int param1Int) {
/* 603 */       if (this.unicode) {
/* 604 */         this.string = StringUtils.decodeUnicodeStringSQL(this.string, param1Int);
/* 605 */         this.unicode = false;
/*     */       } else {
/* 607 */         throw DbException.getInternalError();
/*     */       } 
/*     */     }
/*     */   }
/*     */   
/*     */   static final class IntegerToken
/*     */     extends LiteralToken
/*     */   {
/*     */     private final int number;
/*     */     
/*     */     IntegerToken(int param1Int1, int param1Int2) {
/* 618 */       super(param1Int1);
/* 619 */       this.number = param1Int2;
/*     */     }
/*     */ 
/*     */     
/*     */     Value value(CastDataProvider param1CastDataProvider) {
/* 624 */       if (this.value == null) {
/* 625 */         this.value = (Value)ValueInteger.get(this.number);
/*     */       }
/* 627 */       return this.value;
/*     */     }
/*     */   }
/*     */   
/*     */   static final class BigintToken
/*     */     extends LiteralToken
/*     */   {
/*     */     private final long number;
/*     */     
/*     */     BigintToken(int param1Int, long param1Long) {
/* 637 */       super(param1Int);
/* 638 */       this.number = param1Long;
/*     */     }
/*     */ 
/*     */     
/*     */     Value value(CastDataProvider param1CastDataProvider) {
/* 643 */       if (this.value == null) {
/* 644 */         this.value = (Value)ValueBigint.get(this.number);
/*     */       }
/* 646 */       return this.value;
/*     */     }
/*     */   }
/*     */   
/*     */   static final class ValueToken
/*     */     extends LiteralToken
/*     */   {
/*     */     ValueToken(int param1Int, Value param1Value) {
/* 654 */       super(param1Int);
/* 655 */       this.value = param1Value;
/*     */     }
/*     */ 
/*     */     
/*     */     Value value(CastDataProvider param1CastDataProvider) {
/* 660 */       return this.value;
/*     */     }
/*     */   }
/*     */   
/*     */   static final class ParameterToken
/*     */     extends Token
/*     */   {
/*     */     int index;
/*     */     
/*     */     ParameterToken(int param1Int1, int param1Int2) {
/* 670 */       super(param1Int1);
/* 671 */       this.index = param1Int2;
/*     */     }
/*     */ 
/*     */     
/*     */     int tokenType() {
/* 676 */       return 92;
/*     */     }
/*     */ 
/*     */     
/*     */     String asIdentifier() {
/* 681 */       return "?";
/*     */     }
/*     */     
/*     */     int index() {
/* 685 */       return this.index;
/*     */     }
/*     */ 
/*     */     
/*     */     public String toString() {
/* 690 */       return (this.index == 0) ? "?" : ("?" + this.index);
/*     */     }
/*     */   }
/*     */   
/*     */   static final class EndOfInputToken
/*     */     extends Token
/*     */   {
/*     */     EndOfInputToken(int param1Int) {
/* 698 */       super(param1Int);
/*     */     }
/*     */ 
/*     */     
/*     */     int tokenType() {
/* 703 */       return 93;
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   Token(int paramInt) {
/* 711 */     this.start = paramInt;
/*     */   }
/*     */   
/*     */   final int start() {
/* 715 */     return this.start;
/*     */   }
/*     */   
/*     */   final void setStart(int paramInt) {
/* 719 */     this.start = paramInt;
/*     */   }
/*     */   
/*     */   final void subtractFromStart(int paramInt) {
/* 723 */     this.start -= paramInt;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   String asIdentifier() {
/* 729 */     return null;
/*     */   }
/*     */   
/*     */   boolean isQuoted() {
/* 733 */     return false;
/*     */   }
/*     */   
/*     */   Value value(CastDataProvider paramCastDataProvider) {
/* 737 */     return null;
/*     */   }
/*     */   
/*     */   boolean needsUnicodeConversion() {
/* 741 */     return false;
/*     */   }
/*     */   
/*     */   void convertUnicode(int paramInt) {
/* 745 */     throw DbException.getInternalError();
/*     */   }
/*     */ 
/*     */   
/*     */   protected Token clone() {
/*     */     try {
/* 751 */       return (Token)super.clone();
/* 752 */     } catch (CloneNotSupportedException cloneNotSupportedException) {
/* 753 */       throw DbException.getInternalError();
/*     */     } 
/*     */   }
/*     */   
/*     */   abstract int tokenType();
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\command\Token.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */