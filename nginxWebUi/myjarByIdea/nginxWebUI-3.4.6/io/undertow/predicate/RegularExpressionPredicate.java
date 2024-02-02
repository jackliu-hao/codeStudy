package io.undertow.predicate;

import io.undertow.UndertowLogger;
import io.undertow.attribute.ExchangeAttribute;
import io.undertow.attribute.ExchangeAttributes;
import io.undertow.server.HttpServerExchange;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegularExpressionPredicate implements Predicate {
   private final Pattern pattern;
   private final ExchangeAttribute matchAttribute;
   private final boolean requireFullMatch;
   private static final boolean traceEnabled;

   public RegularExpressionPredicate(String regex, ExchangeAttribute matchAttribute, boolean requireFullMatch, boolean caseSensitive) {
      this.requireFullMatch = requireFullMatch;
      this.pattern = Pattern.compile(regex, caseSensitive ? 0 : 2);
      this.matchAttribute = matchAttribute;
   }

   public RegularExpressionPredicate(String regex, ExchangeAttribute matchAttribute, boolean requireFullMatch) {
      this(regex, matchAttribute, requireFullMatch, true);
   }

   public RegularExpressionPredicate(String regex, ExchangeAttribute matchAttribute) {
      this(regex, matchAttribute, false);
   }

   public boolean resolve(HttpServerExchange value) {
      String input = this.matchAttribute.readAttribute(value);
      if (input == null) {
         return false;
      } else {
         Matcher matcher = this.pattern.matcher(input);
         boolean matches;
         if (this.requireFullMatch) {
            matches = matcher.matches();
         } else {
            matches = matcher.find();
         }

         if (traceEnabled) {
            UndertowLogger.PREDICATE_LOGGER.tracef("Regex pattern [%s] %s input [%s] for %s.", new Object[]{this.pattern.toString(), matches ? "MATCHES" : "DOES NOT MATCH", input, value});
         }

         if (matches) {
            Map<String, Object> context = (Map)value.getAttachment(PREDICATE_CONTEXT);
            if (context == null) {
               value.putAttachment(PREDICATE_CONTEXT, context = new TreeMap());
            }

            int count = matcher.groupCount();

            for(int i = 0; i <= count; ++i) {
               if (traceEnabled) {
                  UndertowLogger.PREDICATE_LOGGER.tracef("Storing regex match group [%s] as [%s] for %s.", i, matcher.group(i), value);
               }

               ((Map)context).put(Integer.toString(i), matcher.group(i));
            }
         }

         return matches;
      }
   }

   public String toString() {
      return "regex( pattern='" + this.pattern.toString() + "', value='" + this.matchAttribute.toString() + "', full-match='" + this.requireFullMatch + "', case-sensitive='" + (this.pattern.flags() & 2) == 2 + "' )";
   }

   static {
      traceEnabled = UndertowLogger.PREDICATE_LOGGER.isTraceEnabled();
   }

   public static class Builder implements PredicateBuilder {
      public String name() {
         return "regex";
      }

      public Map<String, Class<?>> parameters() {
         Map<String, Class<?>> params = new HashMap();
         params.put("pattern", String.class);
         params.put("value", ExchangeAttribute.class);
         params.put("full-match", Boolean.class);
         params.put("case-sensitive", Boolean.class);
         return params;
      }

      public Set<String> requiredParameters() {
         Set<String> params = new HashSet();
         params.add("pattern");
         return params;
      }

      public String defaultParameter() {
         return "pattern";
      }

      public Predicate build(Map<String, Object> config) {
         ExchangeAttribute value = (ExchangeAttribute)config.get("value");
         if (value == null) {
            value = ExchangeAttributes.relativePath();
         }

         Boolean fullMatch = (Boolean)config.get("full-match");
         Boolean caseSensitive = (Boolean)config.get("case-sensitive");
         String pattern = (String)config.get("pattern");
         return new RegularExpressionPredicate(pattern, value, fullMatch == null ? false : fullMatch, caseSensitive == null ? true : caseSensitive);
      }
   }
}
