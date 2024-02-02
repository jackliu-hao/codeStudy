package io.undertow.servlet.compat.rewrite;

import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RewriteCond {
   protected String testString = null;
   protected String condPattern = null;
   protected boolean positive = true;
   protected Substitution test = null;
   protected ThreadLocal<Condition> condition = new ThreadLocal();
   public boolean nocase = false;
   public boolean ornext = false;

   public String getCondPattern() {
      return this.condPattern;
   }

   public void setCondPattern(String condPattern) {
      this.condPattern = condPattern;
   }

   public String getTestString() {
      return this.testString;
   }

   public void setTestString(String testString) {
      this.testString = testString;
   }

   public void parse(Map<String, RewriteMap> maps) {
      this.test = new Substitution();
      this.test.setSub(this.testString);
      this.test.parse(maps);
      if (this.condPattern.startsWith("!")) {
         this.positive = false;
         this.condPattern = this.condPattern.substring(1);
      }

   }

   public Matcher getMatcher() {
      Object condition = this.condition.get();
      return condition instanceof PatternCondition ? ((PatternCondition)condition).matcher : null;
   }

   public String toString() {
      return "RewriteCond " + this.testString + " " + this.condPattern;
   }

   public boolean evaluate(Matcher rule, Matcher cond, Resolver resolver) {
      String value = this.test.evaluate(rule, cond, resolver);
      if (this.nocase) {
         value = value.toLowerCase(Locale.ENGLISH);
      }

      Condition condition = (Condition)this.condition.get();
      if (condition == null) {
         LexicalCondition ncondition;
         if (this.condPattern.startsWith("<")) {
            ncondition = new LexicalCondition();
            ncondition.type = -1;
            ncondition.condition = this.condPattern.substring(1);
            condition = ncondition;
         } else if (this.condPattern.startsWith(">")) {
            ncondition = new LexicalCondition();
            ncondition.type = 1;
            ncondition.condition = this.condPattern.substring(1);
            condition = ncondition;
         } else if (this.condPattern.startsWith("=")) {
            ncondition = new LexicalCondition();
            ncondition.type = 0;
            ncondition.condition = this.condPattern.substring(1);
            condition = ncondition;
         } else {
            ResourceCondition ncondition;
            if (this.condPattern.equals("-d")) {
               ncondition = new ResourceCondition();
               ncondition.type = 0;
               condition = ncondition;
            } else if (this.condPattern.equals("-f")) {
               ncondition = new ResourceCondition();
               ncondition.type = 1;
               condition = ncondition;
            } else if (this.condPattern.equals("-s")) {
               ncondition = new ResourceCondition();
               ncondition.type = 2;
               condition = ncondition;
            } else {
               PatternCondition ncondition = new PatternCondition();
               int flags = 0;
               if (this.isNocase()) {
                  flags |= 2;
               }

               ncondition.pattern = Pattern.compile(this.condPattern, flags);
               condition = ncondition;
            }
         }

         this.condition.set(condition);
      }

      if (this.positive) {
         return ((Condition)condition).evaluate(value, resolver);
      } else {
         return !((Condition)condition).evaluate(value, resolver);
      }
   }

   public boolean isNocase() {
      return this.nocase;
   }

   public void setNocase(boolean nocase) {
      this.nocase = nocase;
   }

   public boolean isOrnext() {
      return this.ornext;
   }

   public void setOrnext(boolean ornext) {
      this.ornext = ornext;
   }

   public boolean isPositive() {
      return this.positive;
   }

   public void setPositive(boolean positive) {
      this.positive = positive;
   }

   public static class ResourceCondition extends Condition {
      public int type = 0;

      public boolean evaluate(String value, Resolver resolver) {
         switch (this.type) {
            case 0:
               return true;
            case 1:
               return true;
            case 2:
               return true;
            default:
               return false;
         }
      }
   }

   public static class LexicalCondition extends Condition {
      public int type = 0;
      public String condition;

      public boolean evaluate(String value, Resolver resolver) {
         int result = value.compareTo(this.condition);
         switch (this.type) {
            case -1:
               return result < 0;
            case 0:
               return result == 0;
            case 1:
               return result > 0;
            default:
               return false;
         }
      }
   }

   public static class PatternCondition extends Condition {
      public Pattern pattern;
      public Matcher matcher = null;

      public boolean evaluate(String value, Resolver resolver) {
         Matcher m = this.pattern.matcher(value);
         if (m.matches()) {
            this.matcher = m;
            return true;
         } else {
            return false;
         }
      }
   }

   public abstract static class Condition {
      public abstract boolean evaluate(String var1, Resolver var2);
   }
}
