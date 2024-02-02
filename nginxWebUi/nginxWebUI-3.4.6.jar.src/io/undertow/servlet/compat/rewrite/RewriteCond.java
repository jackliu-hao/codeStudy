/*     */ package io.undertow.servlet.compat.rewrite;
/*     */ 
/*     */ import java.util.Locale;
/*     */ import java.util.Map;
/*     */ import java.util.regex.Matcher;
/*     */ import java.util.regex.Pattern;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class RewriteCond
/*     */ {
/*     */   public static abstract class Condition
/*     */   {
/*     */     public abstract boolean evaluate(String param1String, Resolver param1Resolver);
/*     */   }
/*     */   
/*     */   public static class PatternCondition
/*     */     extends Condition
/*     */   {
/*     */     public Pattern pattern;
/*  37 */     public Matcher matcher = null;
/*     */     
/*     */     public boolean evaluate(String value, Resolver resolver) {
/*  40 */       Matcher m = this.pattern.matcher(value);
/*  41 */       if (m.matches()) {
/*  42 */         this.matcher = m;
/*  43 */         return true;
/*     */       } 
/*  45 */       return false;
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static class LexicalCondition
/*     */     extends Condition
/*     */   {
/*  56 */     public int type = 0;
/*     */     public String condition;
/*     */     
/*     */     public boolean evaluate(String value, Resolver resolver) {
/*  60 */       int result = value.compareTo(this.condition);
/*  61 */       switch (this.type) {
/*     */         case -1:
/*  63 */           return (result < 0);
/*     */         case 0:
/*  65 */           return (result == 0);
/*     */         case 1:
/*  67 */           return (result > 0);
/*     */       } 
/*  69 */       return false;
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static class ResourceCondition
/*     */     extends Condition
/*     */   {
/*  81 */     public int type = 0;
/*     */     
/*     */     public boolean evaluate(String value, Resolver resolver) {
/*  84 */       switch (this.type) {
/*     */         case 0:
/*  86 */           return true;
/*     */         case 1:
/*  88 */           return true;
/*     */         case 2:
/*  90 */           return true;
/*     */       } 
/*  92 */       return false;
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*  98 */   protected String testString = null;
/*  99 */   protected String condPattern = null;
/*     */   
/*     */   public String getCondPattern() {
/* 102 */     return this.condPattern;
/*     */   }
/*     */   
/*     */   public void setCondPattern(String condPattern) {
/* 106 */     this.condPattern = condPattern;
/*     */   }
/*     */   
/*     */   public String getTestString() {
/* 110 */     return this.testString;
/*     */   }
/*     */   
/*     */   public void setTestString(String testString) {
/* 114 */     this.testString = testString;
/*     */   }
/*     */   
/*     */   public void parse(Map<String, RewriteMap> maps) {
/* 118 */     this.test = new Substitution();
/* 119 */     this.test.setSub(this.testString);
/* 120 */     this.test.parse(maps);
/* 121 */     if (this.condPattern.startsWith("!")) {
/* 122 */       this.positive = false;
/* 123 */       this.condPattern = this.condPattern.substring(1);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Matcher getMatcher() {
/* 159 */     Object condition = this.condition.get();
/* 160 */     if (condition instanceof PatternCondition) {
/* 161 */       return ((PatternCondition)condition).matcher;
/*     */     }
/* 163 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String toString() {
/* 171 */     return "RewriteCond " + this.testString + " " + this.condPattern;
/*     */   }
/*     */ 
/*     */   
/*     */   protected boolean positive = true;
/*     */   
/* 177 */   protected Substitution test = null;
/*     */   
/* 179 */   protected ThreadLocal<Condition> condition = new ThreadLocal<>();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean nocase = false;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean ornext = false;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean evaluate(Matcher rule, Matcher cond, Resolver resolver) {
/* 202 */     String value = this.test.evaluate(rule, cond, resolver);
/* 203 */     if (this.nocase) {
/* 204 */       value = value.toLowerCase(Locale.ENGLISH);
/*     */     }
/* 206 */     Condition condition = this.condition.get();
/* 207 */     if (condition == null) {
/* 208 */       if (this.condPattern.startsWith("<")) {
/* 209 */         LexicalCondition ncondition = new LexicalCondition();
/* 210 */         ncondition.type = -1;
/* 211 */         ncondition.condition = this.condPattern.substring(1);
/* 212 */         condition = ncondition;
/* 213 */       } else if (this.condPattern.startsWith(">")) {
/* 214 */         LexicalCondition ncondition = new LexicalCondition();
/* 215 */         ncondition.type = 1;
/* 216 */         ncondition.condition = this.condPattern.substring(1);
/* 217 */         condition = ncondition;
/* 218 */       } else if (this.condPattern.startsWith("=")) {
/* 219 */         LexicalCondition ncondition = new LexicalCondition();
/* 220 */         ncondition.type = 0;
/* 221 */         ncondition.condition = this.condPattern.substring(1);
/* 222 */         condition = ncondition;
/* 223 */       } else if (this.condPattern.equals("-d")) {
/* 224 */         ResourceCondition ncondition = new ResourceCondition();
/* 225 */         ncondition.type = 0;
/* 226 */         condition = ncondition;
/* 227 */       } else if (this.condPattern.equals("-f")) {
/* 228 */         ResourceCondition ncondition = new ResourceCondition();
/* 229 */         ncondition.type = 1;
/* 230 */         condition = ncondition;
/* 231 */       } else if (this.condPattern.equals("-s")) {
/* 232 */         ResourceCondition ncondition = new ResourceCondition();
/* 233 */         ncondition.type = 2;
/* 234 */         condition = ncondition;
/*     */       } else {
/* 236 */         PatternCondition ncondition = new PatternCondition();
/* 237 */         int flags = 0;
/* 238 */         if (isNocase()) {
/* 239 */           flags |= 0x2;
/*     */         }
/* 241 */         ncondition.pattern = Pattern.compile(this.condPattern, flags);
/* 242 */         condition = ncondition;
/*     */       } 
/* 244 */       this.condition.set(condition);
/*     */     } 
/* 246 */     if (this.positive) {
/* 247 */       return condition.evaluate(value, resolver);
/*     */     }
/* 249 */     return !condition.evaluate(value, resolver);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isNocase() {
/* 254 */     return this.nocase;
/*     */   }
/*     */   
/*     */   public void setNocase(boolean nocase) {
/* 258 */     this.nocase = nocase;
/*     */   }
/*     */   
/*     */   public boolean isOrnext() {
/* 262 */     return this.ornext;
/*     */   }
/*     */   
/*     */   public void setOrnext(boolean ornext) {
/* 266 */     this.ornext = ornext;
/*     */   }
/*     */   
/*     */   public boolean isPositive() {
/* 270 */     return this.positive;
/*     */   }
/*     */   
/*     */   public void setPositive(boolean positive) {
/* 274 */     this.positive = positive;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\servlet\compat\rewrite\RewriteCond.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */