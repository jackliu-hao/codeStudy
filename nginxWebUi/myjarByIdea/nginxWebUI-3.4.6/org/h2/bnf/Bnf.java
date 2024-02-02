package org.h2.bnf;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.StringTokenizer;
import org.h2.bnf.context.DbContextRule;
import org.h2.command.dml.Help;
import org.h2.tools.Csv;
import org.h2.util.StringUtils;
import org.h2.util.Utils;

public class Bnf {
   private final HashMap<String, RuleHead> ruleMap = new HashMap();
   private String syntax;
   private String currentToken;
   private String[] tokens;
   private char firstChar;
   private int index;
   private Rule lastRepeat;
   private ArrayList<RuleHead> statements;
   private String currentTopic;

   public static Bnf getInstance(Reader var0) throws SQLException, IOException {
      Bnf var1 = new Bnf();
      if (var0 == null) {
         byte[] var2 = Utils.getResource("/org/h2/res/help.csv");
         var0 = new InputStreamReader(new ByteArrayInputStream(var2));
      }

      var1.parse((Reader)var0);
      return var1;
   }

   public void addAlias(String var1, String var2) {
      RuleHead var3 = (RuleHead)this.ruleMap.get(var2);
      this.ruleMap.put(var1, var3);
   }

   private void addFixedRule(String var1, int var2) {
      RuleFixed var3 = new RuleFixed(var2);
      this.addRule(var1, "Fixed", var3);
   }

   private RuleHead addRule(String var1, String var2, Rule var3) {
      RuleHead var4 = new RuleHead(var2, var1, var3);
      String var5 = StringUtils.toLowerEnglish(var1.trim().replace(' ', '_'));
      if (this.ruleMap.putIfAbsent(var5, var4) != null) {
         throw new AssertionError("already exists: " + var1);
      } else {
         return var4;
      }
   }

   private void parse(Reader var1) throws SQLException, IOException {
      Object var2 = null;
      this.statements = new ArrayList();
      Csv var3 = new Csv();
      var3.setLineCommentCharacter('#');
      ResultSet var4 = var3.read(var1, (String[])null);

      while(var4.next()) {
         String var5 = var4.getString("SECTION").trim();
         if (!var5.startsWith("System")) {
            String var6 = var4.getString("TOPIC");
            this.syntax = Help.stripAnnotationsFromSyntax(var4.getString("SYNTAX"));
            this.currentTopic = var5;
            this.tokens = this.tokenize();
            this.index = 0;
            Object var7 = this.parseRule();
            if (var5.startsWith("Command")) {
               var7 = new RuleList((Rule)var7, new RuleElement(";\n\n", this.currentTopic), false);
            }

            RuleHead var8 = this.addRule(var6, var5, (Rule)var7);
            if (var5.startsWith("Function")) {
               if (var2 == null) {
                  var2 = var7;
               } else {
                  var2 = new RuleList((Rule)var7, (Rule)var2, true);
               }
            } else if (var5.startsWith("Commands")) {
               this.statements.add(var8);
            }
         }
      }

      this.addRule("@func@", "Function", (Rule)var2);
      this.addFixedRule("@ymd@", 0);
      this.addFixedRule("@hms@", 1);
      this.addFixedRule("@nanos@", 2);
      this.addFixedRule("anything_except_single_quote", 3);
      this.addFixedRule("single_character", 3);
      this.addFixedRule("anything_except_double_quote", 4);
      this.addFixedRule("anything_until_end_of_line", 5);
      this.addFixedRule("anything_until_comment_start_or_end", 6);
      this.addFixedRule("anything_except_two_dollar_signs", 8);
      this.addFixedRule("anything", 7);
      this.addFixedRule("@hex_start@", 10);
      this.addFixedRule("@concat@", 11);
      this.addFixedRule("@az_@", 12);
      this.addFixedRule("@af@", 13);
      this.addFixedRule("@digit@", 14);
      this.addFixedRule("@open_bracket@", 15);
      this.addFixedRule("@close_bracket@", 16);
      this.addFixedRule("json_text", 17);
   }

   public void visit(BnfVisitor var1, String var2) {
      this.syntax = var2;
      this.tokens = this.tokenize();
      this.index = 0;
      Rule var3 = this.parseRule();
      var3.setLinks(this.ruleMap);
      var3.accept(var1);
   }

   public static boolean startWithSpace(String var0) {
      return var0.length() > 0 && Character.isWhitespace(var0.charAt(0));
   }

   public static String getRuleMapKey(String var0) {
      StringBuilder var1 = new StringBuilder();
      char[] var2 = var0.toCharArray();
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         char var5 = var2[var4];
         if (Character.isUpperCase(var5)) {
            var1.append('_').append(Character.toLowerCase(var5));
         } else {
            var1.append(var5);
         }
      }

      return var1.toString();
   }

   public RuleHead getRuleHead(String var1) {
      return (RuleHead)this.ruleMap.get(var1);
   }

   private Rule parseRule() {
      this.read();
      return this.parseOr();
   }

   private Rule parseOr() {
      Object var1 = this.parseList();
      if (this.firstChar == '|') {
         this.read();
         var1 = new RuleList((Rule)var1, this.parseOr(), true);
      }

      this.lastRepeat = (Rule)var1;
      return (Rule)var1;
   }

   private Rule parseList() {
      Object var1 = this.parseToken();
      if (this.firstChar != '|' && this.firstChar != ']' && this.firstChar != '}' && this.firstChar != 0) {
         var1 = new RuleList((Rule)var1, this.parseList(), false);
      }

      this.lastRepeat = (Rule)var1;
      return (Rule)var1;
   }

   private RuleExtension parseExtension(boolean var1) {
      this.read();
      Object var3;
      if (this.firstChar == '[') {
         this.read();
         Rule var2 = this.parseOr();
         var3 = new RuleOptional(var2);
         if (this.firstChar != ']') {
            throw new AssertionError("expected ], got " + this.currentToken + " syntax:" + this.syntax);
         }
      } else if (this.firstChar == '{') {
         this.read();
         var3 = this.parseOr();
         if (this.firstChar != '}') {
            throw new AssertionError("expected }, got " + this.currentToken + " syntax:" + this.syntax);
         }
      } else {
         var3 = this.parseOr();
      }

      return new RuleExtension((Rule)var3, var1);
   }

   private Rule parseToken() {
      Object var2;
      if (this.firstChar >= 'A' && this.firstChar <= 'Z' || this.firstChar >= 'a' && this.firstChar <= 'z') {
         var2 = new RuleElement(this.currentToken, this.currentTopic);
      } else if (this.firstChar == '[') {
         this.read();
         Rule var1 = this.parseOr();
         var2 = new RuleOptional(var1);
         if (this.firstChar != ']') {
            throw new AssertionError("expected ], got " + this.currentToken + " syntax:" + this.syntax);
         }
      } else if (this.firstChar == '{') {
         this.read();
         var2 = this.parseOr();
         if (this.firstChar != '}') {
            throw new AssertionError("expected }, got " + this.currentToken + " syntax:" + this.syntax);
         }
      } else if (this.firstChar == '@') {
         if ("@commaDots@".equals(this.currentToken)) {
            RuleList var3 = new RuleList(new RuleElement(",", this.currentTopic), this.lastRepeat, false);
            var2 = new RuleRepeat(var3, true);
         } else if ("@dots@".equals(this.currentToken)) {
            var2 = new RuleRepeat(this.lastRepeat, false);
         } else if ("@c@".equals(this.currentToken)) {
            var2 = this.parseExtension(true);
         } else if ("@h2@".equals(this.currentToken)) {
            var2 = this.parseExtension(false);
         } else {
            var2 = new RuleElement(this.currentToken, this.currentTopic);
         }
      } else {
         var2 = new RuleElement(this.currentToken, this.currentTopic);
      }

      this.lastRepeat = (Rule)var2;
      this.read();
      return (Rule)var2;
   }

   private void read() {
      if (this.index < this.tokens.length) {
         this.currentToken = this.tokens[this.index++];
         this.firstChar = this.currentToken.charAt(0);
      } else {
         this.currentToken = "";
         this.firstChar = 0;
      }

   }

   public String toString() {
      StringBuilder var1 = new StringBuilder();

      int var2;
      for(var2 = 0; var2 < this.index; ++var2) {
         var1.append(this.tokens[var2]).append(' ');
      }

      var1.append("[*]");

      for(var2 = this.index; var2 < this.tokens.length; ++var2) {
         var1.append(' ').append(this.tokens[var2]);
      }

      return var1.toString();
   }

   private String[] tokenize() {
      ArrayList var1 = new ArrayList();
      this.syntax = StringUtils.replaceAll(this.syntax, "yyyy-MM-dd", "@ymd@");
      this.syntax = StringUtils.replaceAll(this.syntax, "hh:mm:ss", "@hms@");
      this.syntax = StringUtils.replaceAll(this.syntax, "hh:mm", "@hms@");
      this.syntax = StringUtils.replaceAll(this.syntax, "mm:ss", "@hms@");
      this.syntax = StringUtils.replaceAll(this.syntax, "nnnnnnnnn", "@nanos@");
      this.syntax = StringUtils.replaceAll(this.syntax, "function", "@func@");
      this.syntax = StringUtils.replaceAll(this.syntax, "0x", "@hexStart@");
      this.syntax = StringUtils.replaceAll(this.syntax, ",...", "@commaDots@");
      this.syntax = StringUtils.replaceAll(this.syntax, "...", "@dots@");
      this.syntax = StringUtils.replaceAll(this.syntax, "||", "@concat@");
      this.syntax = StringUtils.replaceAll(this.syntax, "a-z|_", "@az_@");
      this.syntax = StringUtils.replaceAll(this.syntax, "A-Z|_", "@az_@");
      this.syntax = StringUtils.replaceAll(this.syntax, "A-F", "@af@");
      this.syntax = StringUtils.replaceAll(this.syntax, "0-9", "@digit@");
      this.syntax = StringUtils.replaceAll(this.syntax, "'['", "@openBracket@");
      this.syntax = StringUtils.replaceAll(this.syntax, "']'", "@closeBracket@");
      StringTokenizer var2 = getTokenizer(this.syntax);

      while(true) {
         String var3;
         do {
            if (!var2.hasMoreTokens()) {
               return (String[])var1.toArray(new String[0]);
            }

            var3 = var2.nextToken();
            var3 = StringUtils.cache(var3);
         } while(var3.length() == 1 && " \r\n".indexOf(var3.charAt(0)) >= 0);

         var1.add(var3);
      }
   }

   public HashMap<String, String> getNextTokenList(String var1) {
      Sentence var2 = new Sentence();
      var2.setQuery(var1);

      try {
         Iterator var3 = this.statements.iterator();

         while(var3.hasNext()) {
            RuleHead var4 = (RuleHead)var3.next();
            if (var4.getSection().startsWith("Commands")) {
               var2.start();
               if (var4.getRule().autoComplete(var2)) {
                  break;
               }
            }
         }
      } catch (IllegalStateException var5) {
      }

      return var2.getNext();
   }

   public void linkStatements() {
      Iterator var1 = this.ruleMap.values().iterator();

      while(var1.hasNext()) {
         RuleHead var2 = (RuleHead)var1.next();
         var2.getRule().setLinks(this.ruleMap);
      }

   }

   public void updateTopic(String var1, DbContextRule var2) {
      var1 = StringUtils.toLowerEnglish(var1);
      RuleHead var3 = (RuleHead)this.ruleMap.get(var1);
      if (var3 == null) {
         var3 = new RuleHead("db", var1, var2);
         this.ruleMap.put(var1, var3);
         this.statements.add(var3);
      } else {
         var3.setRule(var2);
      }

   }

   public ArrayList<RuleHead> getStatements() {
      return this.statements;
   }

   public static StringTokenizer getTokenizer(String var0) {
      return new StringTokenizer(var0, " [](){}|.,\r\n<>:-+*/=\"!'$", true);
   }
}
