package io.undertow.servlet.compat.rewrite;

import io.undertow.servlet.UndertowServletLogger;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

public class RewriteConfigFactory {
   public static RewriteConfig build(InputStream inputStream) {
      BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));

      RewriteConfig var2;
      try {
         var2 = parse(reader);
      } finally {
         try {
            reader.close();
         } catch (IOException var9) {
         }

      }

      return var2;
   }

   private static RewriteConfig parse(BufferedReader reader) {
      ArrayList<RewriteRule> rules = new ArrayList();
      ArrayList<RewriteCond> conditions = new ArrayList();
      Map<String, RewriteMap> maps = new HashMap();

      while(true) {
         try {
            String line = reader.readLine();
            if (line == null) {
               break;
            }

            Object result = parse(line);
            if (result instanceof RewriteRule) {
               RewriteRule rule = (RewriteRule)result;
               if (UndertowServletLogger.ROOT_LOGGER.isDebugEnabled()) {
                  UndertowServletLogger.ROOT_LOGGER.debug("Add rule with pattern " + rule.getPatternString() + " and substitution " + rule.getSubstitutionString());
               }

               int i;
               for(i = conditions.size() - 1; i > 0; --i) {
                  if (((RewriteCond)conditions.get(i - 1)).isOrnext()) {
                     ((RewriteCond)conditions.get(i)).setOrnext(true);
                  }
               }

               for(i = 0; i < conditions.size(); ++i) {
                  if (UndertowServletLogger.ROOT_LOGGER.isDebugEnabled()) {
                     RewriteCond cond = (RewriteCond)conditions.get(i);
                     UndertowServletLogger.ROOT_LOGGER.debug("Add condition " + cond.getCondPattern() + " test " + cond.getTestString() + " to rule with pattern " + rule.getPatternString() + " and substitution " + rule.getSubstitutionString() + (cond.isOrnext() ? " [OR]" : "") + (cond.isNocase() ? " [NC]" : ""));
                  }

                  rule.addCondition((RewriteCond)conditions.get(i));
               }

               conditions.clear();
               rules.add(rule);
            } else if (result instanceof RewriteCond) {
               conditions.add((RewriteCond)result);
            } else if (result instanceof Object[]) {
               String mapName = (String)((Object[])((Object[])result))[0];
               RewriteMap map = (RewriteMap)((Object[])((Object[])result))[1];
               maps.put(mapName, map);
            }
         } catch (IOException var9) {
            UndertowServletLogger.ROOT_LOGGER.errorReadingRewriteConfiguration(var9);
         }
      }

      RewriteRule[] rulesArray = (RewriteRule[])rules.toArray(new RewriteRule[0]);

      for(int i = 0; i < rulesArray.length; ++i) {
         rulesArray[i].parse(maps);
      }

      return new RewriteConfig(rulesArray, maps);
   }

   private static Object parse(String line) {
      StringTokenizer tokenizer = new StringTokenizer(line);
      if (tokenizer.hasMoreTokens()) {
         String token = tokenizer.nextToken();
         String rewriteMapClassName;
         StringTokenizer flagsTokenizer;
         if (token.equals("RewriteCond")) {
            RewriteCond condition = new RewriteCond();
            if (tokenizer.countTokens() < 2) {
               throw UndertowServletLogger.ROOT_LOGGER.invalidRewriteConfiguration(line);
            }

            condition.setTestString(tokenizer.nextToken());
            condition.setCondPattern(tokenizer.nextToken());
            if (tokenizer.hasMoreTokens()) {
               rewriteMapClassName = tokenizer.nextToken();
               if (rewriteMapClassName.startsWith("[") && rewriteMapClassName.endsWith("]")) {
                  rewriteMapClassName = rewriteMapClassName.substring(1, rewriteMapClassName.length() - 1);
               }

               flagsTokenizer = new StringTokenizer(rewriteMapClassName, ",");

               while(flagsTokenizer.hasMoreElements()) {
                  parseCondFlag(line, condition, flagsTokenizer.nextToken());
               }
            }

            return condition;
         }

         if (token.equals("RewriteRule")) {
            RewriteRule rule = new RewriteRule();
            if (tokenizer.countTokens() < 2) {
               throw UndertowServletLogger.ROOT_LOGGER.invalidRewriteConfiguration(line);
            }

            rule.setPatternString(tokenizer.nextToken());
            rule.setSubstitutionString(tokenizer.nextToken());
            if (tokenizer.hasMoreTokens()) {
               rewriteMapClassName = tokenizer.nextToken();
               if (rewriteMapClassName.startsWith("[") && rewriteMapClassName.endsWith("]")) {
                  rewriteMapClassName = rewriteMapClassName.substring(1, rewriteMapClassName.length() - 1);
               }

               flagsTokenizer = new StringTokenizer(rewriteMapClassName, ",");

               while(flagsTokenizer.hasMoreElements()) {
                  parseRuleFlag(line, rule, flagsTokenizer.nextToken());
               }
            }

            return rule;
         }

         if (token.equals("RewriteMap")) {
            if (tokenizer.countTokens() < 2) {
               throw UndertowServletLogger.ROOT_LOGGER.invalidRewriteConfiguration(line);
            }

            String name = tokenizer.nextToken();
            rewriteMapClassName = tokenizer.nextToken();
            RewriteMap map = null;

            try {
               map = (RewriteMap)((RewriteMap)Class.forName(rewriteMapClassName).newInstance());
            } catch (Exception var7) {
               throw UndertowServletLogger.ROOT_LOGGER.invalidRewriteMap(rewriteMapClassName);
            }

            if (tokenizer.hasMoreTokens()) {
               map.setParameters(tokenizer.nextToken());
            }

            Object[] result = new Object[]{name, map};
            return result;
         }

         if (!token.startsWith("#")) {
            throw UndertowServletLogger.ROOT_LOGGER.invalidRewriteConfiguration(line);
         }
      }

      return null;
   }

   protected static void parseCondFlag(String line, RewriteCond condition, String flag) {
      if (!flag.equals("NC") && !flag.equals("nocase")) {
         if (!flag.equals("OR") && !flag.equals("ornext")) {
            throw UndertowServletLogger.ROOT_LOGGER.invalidRewriteFlags(line, flag);
         }

         condition.setOrnext(true);
      } else {
         condition.setNocase(true);
      }

   }

   protected static void parseRuleFlag(String line, RewriteRule rule, String flag) {
      if (!flag.equals("chain") && !flag.equals("C")) {
         if (!flag.startsWith("cookie=") && !flag.startsWith("CO=")) {
            if (!flag.startsWith("env=") && !flag.startsWith("E=")) {
               if (!flag.startsWith("forbidden") && !flag.startsWith("F")) {
                  if (!flag.startsWith("gone") && !flag.startsWith("G")) {
                     if (!flag.startsWith("host") && !flag.startsWith("H")) {
                        if (!flag.startsWith("last") && !flag.startsWith("L")) {
                           if (!flag.startsWith("next") && !flag.startsWith("N")) {
                              if (!flag.startsWith("nocase") && !flag.startsWith("NC")) {
                                 if (!flag.startsWith("noescape") && !flag.startsWith("NE")) {
                                    if (!flag.startsWith("qsappend") && !flag.startsWith("QSA")) {
                                       if (!flag.startsWith("redirect") && !flag.startsWith("R")) {
                                          if (!flag.startsWith("skip") && !flag.startsWith("S")) {
                                             if (!flag.startsWith("type") && !flag.startsWith("T")) {
                                                throw UndertowServletLogger.ROOT_LOGGER.invalidRewriteFlags(line, flag);
                                             }

                                             if (flag.startsWith("type=")) {
                                                flag = flag.substring("type=".length());
                                             } else if (flag.startsWith("T=")) {
                                                flag = flag.substring("T=".length());
                                             }

                                             rule.setType(true);
                                             rule.setTypeValue(flag);
                                          } else {
                                             if (flag.startsWith("skip=")) {
                                                flag = flag.substring("skip=".length());
                                             } else if (flag.startsWith("S=")) {
                                                flag = flag.substring("S=".length());
                                             }

                                             rule.setSkip(Integer.parseInt(flag));
                                          }
                                       } else if (flag.startsWith("redirect=")) {
                                          flag = flag.substring("redirect=".length());
                                          rule.setRedirect(true);
                                          rule.setRedirectCode(Integer.parseInt(flag));
                                       } else if (flag.startsWith("R=")) {
                                          flag = flag.substring("R=".length());
                                          rule.setRedirect(true);
                                          rule.setRedirectCode(Integer.parseInt(flag));
                                       } else {
                                          rule.setRedirect(true);
                                          rule.setRedirectCode(302);
                                       }
                                    } else {
                                       rule.setQsappend(true);
                                    }
                                 } else {
                                    rule.setNoescape(true);
                                 }
                              } else {
                                 rule.setNocase(true);
                              }
                           } else {
                              rule.setNext(true);
                           }
                        } else {
                           rule.setLast(true);
                        }
                     } else {
                        rule.setHost(true);
                     }
                  } else {
                     rule.setGone(true);
                  }
               } else {
                  rule.setForbidden(true);
               }
            } else {
               rule.setEnv(true);
               if (flag.startsWith("env=")) {
                  flag = flag.substring("env=".length());
               } else if (flag.startsWith("E=")) {
                  flag = flag.substring("E=".length());
               }

               int pos = flag.indexOf(58);
               if (pos == -1 || pos + 1 == flag.length()) {
                  throw UndertowServletLogger.ROOT_LOGGER.invalidRewriteFlags(line);
               }

               rule.addEnvName(flag.substring(0, pos));
               rule.addEnvValue(flag.substring(pos + 1));
            }
         } else {
            rule.setCookie(true);
            if (flag.startsWith("cookie")) {
               flag = flag.substring("cookie=".length());
            } else if (flag.startsWith("CO=")) {
               flag = flag.substring("CO=".length());
            }

            StringTokenizer tokenizer = new StringTokenizer(flag, ":");
            if (tokenizer.countTokens() < 2) {
               throw UndertowServletLogger.ROOT_LOGGER.invalidRewriteFlags(line);
            }

            rule.setCookieName(tokenizer.nextToken());
            rule.setCookieValue(tokenizer.nextToken());
            if (tokenizer.hasMoreTokens()) {
               rule.setCookieDomain(tokenizer.nextToken());
            }

            if (tokenizer.hasMoreTokens()) {
               try {
                  rule.setCookieLifetime(Integer.parseInt(tokenizer.nextToken()));
               } catch (NumberFormatException var5) {
                  throw UndertowServletLogger.ROOT_LOGGER.invalidRewriteFlags(line);
               }
            }

            if (tokenizer.hasMoreTokens()) {
               rule.setCookiePath(tokenizer.nextToken());
            }

            if (tokenizer.hasMoreTokens()) {
               rule.setCookieSecure(Boolean.parseBoolean(tokenizer.nextToken()));
            }

            if (tokenizer.hasMoreTokens()) {
               rule.setCookieHttpOnly(Boolean.parseBoolean(tokenizer.nextToken()));
            }
         }
      } else {
         rule.setChain(true);
      }

   }
}
