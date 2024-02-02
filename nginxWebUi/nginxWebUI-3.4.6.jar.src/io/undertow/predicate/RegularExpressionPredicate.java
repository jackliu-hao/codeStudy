/*     */ package io.undertow.predicate;
/*     */ 
/*     */ import io.undertow.UndertowLogger;
/*     */ import io.undertow.attribute.ExchangeAttribute;
/*     */ import io.undertow.attribute.ExchangeAttributes;
/*     */ import io.undertow.server.HttpServerExchange;
/*     */ import java.util.HashMap;
/*     */ import java.util.HashSet;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import java.util.TreeMap;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class RegularExpressionPredicate
/*     */   implements Predicate
/*     */ {
/*     */   private final Pattern pattern;
/*     */   private final ExchangeAttribute matchAttribute;
/*     */   private final boolean requireFullMatch;
/*  51 */   private static final boolean traceEnabled = UndertowLogger.PREDICATE_LOGGER.isTraceEnabled();
/*     */ 
/*     */   
/*     */   public RegularExpressionPredicate(String regex, ExchangeAttribute matchAttribute, boolean requireFullMatch, boolean caseSensitive) {
/*  55 */     this.requireFullMatch = requireFullMatch;
/*  56 */     this.pattern = Pattern.compile(regex, caseSensitive ? 0 : 2);
/*  57 */     this.matchAttribute = matchAttribute;
/*     */   }
/*     */   
/*     */   public RegularExpressionPredicate(String regex, ExchangeAttribute matchAttribute, boolean requireFullMatch) {
/*  61 */     this(regex, matchAttribute, requireFullMatch, true);
/*     */   }
/*     */   
/*     */   public RegularExpressionPredicate(String regex, ExchangeAttribute matchAttribute) {
/*  65 */     this(regex, matchAttribute, false);
/*     */   }
/*     */   
/*     */   public boolean resolve(HttpServerExchange value) {
/*     */     boolean matches;
/*  70 */     String input = this.matchAttribute.readAttribute(value);
/*  71 */     if (input == null) {
/*  72 */       return false;
/*     */     }
/*  74 */     Matcher matcher = this.pattern.matcher(input);
/*     */     
/*  76 */     if (this.requireFullMatch) {
/*  77 */       matches = matcher.matches();
/*     */     } else {
/*  79 */       matches = matcher.find();
/*     */     } 
/*  81 */     if (traceEnabled) {
/*  82 */       UndertowLogger.PREDICATE_LOGGER.tracef("Regex pattern [%s] %s input [%s] for %s.", new Object[] { this.pattern.toString(), matches ? "MATCHES" : "DOES NOT MATCH", input, value });
/*     */     }
/*  84 */     if (matches) {
/*  85 */       Map<String, Object> context = (Map<String, Object>)value.getAttachment(PREDICATE_CONTEXT);
/*  86 */       if (context == null) {
/*  87 */         value.putAttachment(PREDICATE_CONTEXT, context = new TreeMap<>());
/*     */       }
/*  89 */       int count = matcher.groupCount();
/*  90 */       for (int i = 0; i <= count; i++) {
/*  91 */         if (traceEnabled) {
/*  92 */           UndertowLogger.PREDICATE_LOGGER.tracef("Storing regex match group [%s] as [%s] for %s.", i, matcher.group(i), value);
/*     */         }
/*  94 */         context.put(Integer.toString(i), matcher.group(i));
/*     */       } 
/*     */     } 
/*  97 */     return matches;
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 102 */     return "regex( pattern='" + this.pattern.toString() + "', value='" + this.matchAttribute.toString() + "', full-match='" + Boolean.valueOf(this.requireFullMatch) + "', case-sensitive='" + Boolean.valueOf(((this.pattern.flags() & 0x2) == 2)) + "' )";
/*     */   }
/*     */   
/*     */   public static class Builder
/*     */     implements PredicateBuilder
/*     */   {
/*     */     public String name() {
/* 109 */       return "regex";
/*     */     }
/*     */ 
/*     */     
/*     */     public Map<String, Class<?>> parameters() {
/* 114 */       Map<String, Class<?>> params = new HashMap<>();
/* 115 */       params.put("pattern", String.class);
/* 116 */       params.put("value", ExchangeAttribute.class);
/* 117 */       params.put("full-match", Boolean.class);
/* 118 */       params.put("case-sensitive", Boolean.class);
/* 119 */       return params;
/*     */     }
/*     */ 
/*     */     
/*     */     public Set<String> requiredParameters() {
/* 124 */       Set<String> params = new HashSet<>();
/* 125 */       params.add("pattern");
/* 126 */       return params;
/*     */     }
/*     */ 
/*     */     
/*     */     public String defaultParameter() {
/* 131 */       return "pattern";
/*     */     }
/*     */ 
/*     */     
/*     */     public Predicate build(Map<String, Object> config) {
/* 136 */       ExchangeAttribute value = (ExchangeAttribute)config.get("value");
/* 137 */       if (value == null) {
/* 138 */         value = ExchangeAttributes.relativePath();
/*     */       }
/* 140 */       Boolean fullMatch = (Boolean)config.get("full-match");
/* 141 */       Boolean caseSensitive = (Boolean)config.get("case-sensitive");
/* 142 */       String pattern = (String)config.get("pattern");
/* 143 */       return new RegularExpressionPredicate(pattern, value, (fullMatch == null) ? false : fullMatch.booleanValue(), (caseSensitive == null) ? true : caseSensitive.booleanValue());
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\predicate\RegularExpressionPredicate.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */