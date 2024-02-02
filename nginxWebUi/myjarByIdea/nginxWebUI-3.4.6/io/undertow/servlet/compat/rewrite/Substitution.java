package io.undertow.servlet.compat.rewrite;

import java.util.ArrayList;
import java.util.Map;
import java.util.regex.Matcher;

public class Substitution {
   protected SubstitutionElement[] elements = null;
   protected String sub = null;

   public String getSub() {
      return this.sub;
   }

   public void setSub(String sub) {
      this.sub = sub;
   }

   public void parse(Map<String, RewriteMap> maps) {
      ArrayList<SubstitutionElement> elements = new ArrayList();
      int pos = 0;
      int percentPos = false;
      int dollarPos = false;

      while(true) {
         while(pos < this.sub.length()) {
            int percentPos = this.sub.indexOf(37, pos);
            int dollarPos = this.sub.indexOf(36, pos);
            StaticElement newElement;
            if (percentPos != -1 || dollarPos != -1) {
               int open;
               int colon;
               int close;
               if (percentPos == -1 || dollarPos != -1 && dollarPos < percentPos) {
                  if (dollarPos + 1 == this.sub.length()) {
                     throw new IllegalArgumentException(this.sub);
                  }

                  if (pos < dollarPos) {
                     newElement = new StaticElement();
                     newElement.value = this.sub.substring(pos, dollarPos);
                     elements.add(newElement);
                  }

                  if (Character.isDigit(this.sub.charAt(dollarPos + 1))) {
                     RewriteRuleBackReferenceElement newElement = new RewriteRuleBackReferenceElement();
                     newElement.n = Character.digit(this.sub.charAt(dollarPos + 1), 10);
                     pos = dollarPos + 2;
                     elements.add(newElement);
                  } else {
                     MapElement newElement = new MapElement();
                     open = this.sub.indexOf(123, dollarPos);
                     colon = this.sub.indexOf(58, dollarPos);
                     close = this.sub.indexOf(124, dollarPos);
                     int close = this.sub.indexOf(125, dollarPos);
                     if (-1 >= open || open >= colon || colon >= close) {
                        throw new IllegalArgumentException(this.sub);
                     }

                     newElement.map = (RewriteMap)maps.get(this.sub.substring(open + 1, colon));
                     if (newElement.map == null) {
                        throw new IllegalArgumentException(this.sub + ": No map: " + this.sub.substring(open + 1, colon));
                     }

                     if (close > -1) {
                        if (colon >= close || close >= close) {
                           throw new IllegalArgumentException(this.sub);
                        }

                        newElement.key = this.sub.substring(colon + 1, close);
                        newElement.defaultValue = this.sub.substring(close + 1, close);
                     } else {
                        newElement.key = this.sub.substring(colon + 1, close);
                     }

                     pos = close + 1;
                     elements.add(newElement);
                  }
               } else {
                  if (percentPos + 1 == this.sub.length()) {
                     throw new IllegalArgumentException(this.sub);
                  }

                  if (pos < percentPos) {
                     newElement = new StaticElement();
                     newElement.value = this.sub.substring(pos, percentPos);
                     elements.add(newElement);
                  }

                  if (Character.isDigit(this.sub.charAt(percentPos + 1))) {
                     RewriteCondBackReferenceElement newElement = new RewriteCondBackReferenceElement();
                     newElement.n = Character.digit(this.sub.charAt(percentPos + 1), 10);
                     pos = percentPos + 2;
                     elements.add(newElement);
                  } else {
                     newElement = null;
                     open = this.sub.indexOf(123, percentPos);
                     colon = this.sub.indexOf(58, percentPos);
                     close = this.sub.indexOf(125, percentPos);
                     if (-1 >= open || open >= close) {
                        throw new IllegalArgumentException(this.sub);
                     }

                     Object newElement;
                     if (colon > -1) {
                        if (open >= colon || colon >= close) {
                           throw new IllegalArgumentException(this.sub);
                        }

                        String type = this.sub.substring(open + 1, colon);
                        if (type.equals("ENV")) {
                           newElement = new ServerVariableEnvElement();
                           ((ServerVariableEnvElement)newElement).key = this.sub.substring(colon + 1, close);
                        } else if (type.equals("SSL")) {
                           newElement = new ServerVariableSslElement();
                           ((ServerVariableSslElement)newElement).key = this.sub.substring(colon + 1, close);
                        } else {
                           if (!type.equals("HTTP")) {
                              throw new IllegalArgumentException(this.sub + ": Bad type: " + type);
                           }

                           newElement = new ServerVariableHttpElement();
                           ((ServerVariableHttpElement)newElement).key = this.sub.substring(colon + 1, close);
                        }
                     } else {
                        newElement = new ServerVariableElement();
                        ((ServerVariableElement)newElement).key = this.sub.substring(open + 1, close);
                     }

                     pos = close + 1;
                     elements.add(newElement);
                  }
               }
            } else {
               newElement = new StaticElement();
               newElement.value = this.sub.substring(pos, this.sub.length());
               pos = this.sub.length();
               elements.add(newElement);
            }
         }

         this.elements = (SubstitutionElement[])elements.toArray(new SubstitutionElement[0]);
         return;
      }
   }

   public String evaluate(Matcher rule, Matcher cond, Resolver resolver) {
      StringBuffer buf = new StringBuffer();

      for(int i = 0; i < this.elements.length; ++i) {
         buf.append(this.elements[i].evaluate(rule, cond, resolver));
      }

      return buf.toString();
   }

   public class MapElement extends SubstitutionElement {
      public RewriteMap map = null;
      public String key;
      public String defaultValue = null;

      public MapElement() {
         super();
      }

      public String evaluate(Matcher rule, Matcher cond, Resolver resolver) {
         String result = this.map.lookup(this.key);
         if (result == null) {
            result = this.defaultValue;
         }

         return result;
      }
   }

   public class ServerVariableHttpElement extends SubstitutionElement {
      public String key;

      public ServerVariableHttpElement() {
         super();
      }

      public String evaluate(Matcher rule, Matcher cond, Resolver resolver) {
         return resolver.resolveHttp(this.key);
      }
   }

   public class ServerVariableSslElement extends SubstitutionElement {
      public String key;

      public ServerVariableSslElement() {
         super();
      }

      public String evaluate(Matcher rule, Matcher cond, Resolver resolver) {
         return resolver.resolveSsl(this.key);
      }
   }

   public class ServerVariableEnvElement extends SubstitutionElement {
      public String key;

      public ServerVariableEnvElement() {
         super();
      }

      public String evaluate(Matcher rule, Matcher cond, Resolver resolver) {
         return resolver.resolveEnv(this.key);
      }
   }

   public class ServerVariableElement extends SubstitutionElement {
      public String key;

      public ServerVariableElement() {
         super();
      }

      public String evaluate(Matcher rule, Matcher cond, Resolver resolver) {
         return resolver.resolve(this.key);
      }
   }

   public class RewriteCondBackReferenceElement extends SubstitutionElement {
      public int n;

      public RewriteCondBackReferenceElement() {
         super();
      }

      public String evaluate(Matcher rule, Matcher cond, Resolver resolver) {
         return cond.group(this.n);
      }
   }

   public class RewriteRuleBackReferenceElement extends SubstitutionElement {
      public int n;

      public RewriteRuleBackReferenceElement() {
         super();
      }

      public String evaluate(Matcher rule, Matcher cond, Resolver resolver) {
         return rule.group(this.n);
      }
   }

   public class StaticElement extends SubstitutionElement {
      public String value;

      public StaticElement() {
         super();
      }

      public String evaluate(Matcher rule, Matcher cond, Resolver resolver) {
         return this.value;
      }
   }

   public abstract class SubstitutionElement {
      public abstract String evaluate(Matcher var1, Matcher var2, Resolver var3);
   }
}
