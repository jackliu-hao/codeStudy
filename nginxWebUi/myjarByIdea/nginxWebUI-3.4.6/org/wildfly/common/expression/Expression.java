package org.wildfly.common.expression;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiConsumer;
import org.wildfly.common.Assert;
import org.wildfly.common._private.CommonMessages;
import org.wildfly.common.function.ExceptionBiConsumer;

public final class Expression {
   private final Node content;
   private final Set<String> referencedStrings;
   private static final Expression EMPTY;
   private static final EnumSet<Flag> NO_FLAGS;

   Expression(Node content) {
      this.content = content;
      HashSet<String> strings = new HashSet();
      content.catalog(strings);
      this.referencedStrings = strings.isEmpty() ? Collections.emptySet() : (strings.size() == 1 ? Collections.singleton((String)strings.iterator().next()) : Collections.unmodifiableSet(strings));
   }

   public Set<String> getReferencedStrings() {
      return this.referencedStrings;
   }

   public <E extends Exception> String evaluateException(ExceptionBiConsumer<ResolveContext<E>, StringBuilder, E> expandFunction) throws E {
      Assert.checkNotNullParam("expandFunction", expandFunction);
      StringBuilder b = new StringBuilder();
      this.content.emit(new ResolveContext(expandFunction, b), expandFunction);
      return b.toString();
   }

   public String evaluate(BiConsumer<ResolveContext<RuntimeException>, StringBuilder> expandFunction) {
      Objects.requireNonNull(expandFunction);
      return this.evaluateException(expandFunction::accept);
   }

   public String evaluateWithPropertiesAndEnvironment(boolean failOnNoDefault) {
      return this.evaluate((c, b) -> {
         String key = c.getKey();
         String env;
         if (key.startsWith("env.")) {
            env = key.substring(4);
            String val = System.getenv(env);
            if (val == null) {
               if (failOnNoDefault && !c.hasDefault()) {
                  throw CommonMessages.msg.unresolvedEnvironmentProperty(env);
               }

               c.expandDefault();
            } else {
               b.append(val);
            }
         } else {
            env = System.getProperty(key);
            if (env == null) {
               if (failOnNoDefault && !c.hasDefault()) {
                  throw CommonMessages.msg.unresolvedSystemProperty(key);
               }

               c.expandDefault();
            } else {
               b.append(env);
            }
         }

      });
   }

   public String evaluateWithProperties(boolean failOnNoDefault) {
      return this.evaluate((c, b) -> {
         String key = c.getKey();
         String val = System.getProperty(key);
         if (val == null) {
            if (failOnNoDefault && !c.hasDefault()) {
               throw CommonMessages.msg.unresolvedSystemProperty(key);
            }

            c.expandDefault();
         } else {
            b.append(val);
         }

      });
   }

   public String evaluateWithEnvironment(boolean failOnNoDefault) {
      return this.evaluate((c, b) -> {
         String key = c.getKey();
         String val = System.getenv(key);
         if (val == null) {
            if (failOnNoDefault && !c.hasDefault()) {
               throw CommonMessages.msg.unresolvedEnvironmentProperty(key);
            }

            c.expandDefault();
         } else {
            b.append(val);
         }

      });
   }

   public static Expression compile(String string, Flag... flags) {
      return compile(string, flags != null && flags.length != 0 ? EnumSet.of(flags[0], flags) : NO_FLAGS);
   }

   public static Expression compile(String string, EnumSet<Flag> flags) {
      Assert.checkNotNullParam("string", string);
      Assert.checkNotNullParam("flags", flags);
      Itr itr;
      if (flags.contains(Expression.Flag.NO_TRIM)) {
         itr = new Itr(string);
      } else {
         itr = new Itr(string.trim());
      }

      Node content = parseString(itr, true, false, false, flags);
      return content == Node.NULL ? EMPTY : new Expression(content);
   }

   private static Node parseString(Itr itr, boolean allowExpr, boolean endOnBrace, boolean endOnColon, EnumSet<Flag> flags) {
      int ignoreBraceLevel = 0;
      List<Node> list = new ArrayList();
      int start = itr.getNextIdx();

      while(true) {
         while(true) {
            int idx;
            while(itr.hasNext()) {
               idx = itr.getNextIdx();
               int ch = itr.next();
               switch (ch) {
                  case 36:
                     if (!allowExpr) {
                        break;
                     }

                     if (!itr.hasNext()) {
                        if (!flags.contains(Expression.Flag.LENIENT_SYNTAX)) {
                           throw invalidExpressionSyntax(itr.getStr(), idx);
                        }

                        list.add(new LiteralNode(itr.getStr(), start, itr.getNextIdx()));
                        start = itr.getNextIdx();
                     } else {
                        if (idx > start) {
                           list.add(new LiteralNode(itr.getStr(), start, idx));
                        }

                        idx = itr.getNextIdx();
                        ch = itr.next();
                        switch (ch) {
                           case 36:
                              if (flags.contains(Expression.Flag.MINI_EXPRS)) {
                                 list.add(new ExpressionNode(false, LiteralNode.DOLLAR, Node.NULL));
                              } else {
                                 list.add(LiteralNode.DOLLAR);
                              }

                              start = itr.getNextIdx();
                              continue;
                           case 58:
                              if (flags.contains(Expression.Flag.MINI_EXPRS)) {
                                 list.add(new ExpressionNode(false, LiteralNode.COLON, Node.NULL));
                                 start = itr.getNextIdx();
                              } else {
                                 if (endOnColon) {
                                    if (flags.contains(Expression.Flag.LENIENT_SYNTAX)) {
                                       itr.prev();
                                       list.add(LiteralNode.DOLLAR);
                                       return Node.fromList(list);
                                    }

                                    throw invalidExpressionSyntax(itr.getStr(), idx);
                                 }

                                 if (!flags.contains(Expression.Flag.LENIENT_SYNTAX)) {
                                    throw invalidExpressionSyntax(itr.getStr(), idx);
                                 }

                                 list.add(LiteralNode.DOLLAR);
                                 list.add(LiteralNode.COLON);
                                 start = itr.getNextIdx();
                              }
                              continue;
                           case 123:
                              boolean general = flags.contains(Expression.Flag.GENERAL_EXPANSION) && itr.hasNext() && itr.peekNext() == 123;
                              if (general) {
                                 itr.next();
                              }

                              start = itr.getNextIdx();
                              Node keyNode = parseString(itr, !flags.contains(Expression.Flag.NO_RECURSE_KEY), true, true, flags);
                              if (!itr.hasNext()) {
                                 if (!flags.contains(Expression.Flag.LENIENT_SYNTAX)) {
                                    throw invalidExpressionSyntax(itr.getStr(), itr.getNextIdx());
                                 }

                                 list.add(new ExpressionNode(general, keyNode, Node.NULL));
                                 start = itr.getNextIdx();
                              } else if (itr.peekNext() != 58) {
                                 assert itr.peekNext() == 125;

                                 itr.next();
                                 list.add(new ExpressionNode(general, keyNode, Node.NULL));
                                 if (general) {
                                    if (!itr.hasNext()) {
                                       if (!flags.contains(Expression.Flag.LENIENT_SYNTAX)) {
                                          throw invalidExpressionSyntax(itr.getStr(), itr.getNextIdx());
                                       }

                                       start = itr.getNextIdx();
                                    } else if (itr.peekNext() == 125) {
                                       itr.next();
                                       start = itr.getNextIdx();
                                    } else {
                                       if (!flags.contains(Expression.Flag.LENIENT_SYNTAX)) {
                                          throw invalidExpressionSyntax(itr.getStr(), itr.getNextIdx());
                                       }

                                       start = itr.getNextIdx();
                                    }
                                 } else {
                                    start = itr.getNextIdx();
                                 }
                              } else {
                                 if (flags.contains(Expression.Flag.DOUBLE_COLON) && itr.hasNext() && itr.peekNext() == 58) {
                                    itr.rewind(start);
                                    keyNode = parseString(itr, !flags.contains(Expression.Flag.NO_RECURSE_KEY), true, false, flags);
                                    list.add(new ExpressionNode(general, keyNode, Node.NULL));
                                 } else {
                                    itr.next();
                                    Node defaultValueNode = parseString(itr, !flags.contains(Expression.Flag.NO_RECURSE_DEFAULT), true, false, flags);
                                    list.add(new ExpressionNode(general, keyNode, defaultValueNode));
                                 }

                                 if (!itr.hasNext()) {
                                    if (!flags.contains(Expression.Flag.LENIENT_SYNTAX)) {
                                       throw invalidExpressionSyntax(itr.getStr(), itr.getNextIdx());
                                    }

                                    start = itr.getNextIdx();
                                 } else {
                                    assert itr.peekNext() == 125;

                                    itr.next();
                                    if (general) {
                                       if (!itr.hasNext()) {
                                          if (!flags.contains(Expression.Flag.LENIENT_SYNTAX)) {
                                             throw invalidExpressionSyntax(itr.getStr(), itr.getNextIdx());
                                          }

                                          start = itr.getNextIdx();
                                       } else if (itr.peekNext() == 125) {
                                          itr.next();
                                          start = itr.getNextIdx();
                                       } else {
                                          if (!flags.contains(Expression.Flag.LENIENT_SYNTAX)) {
                                             throw invalidExpressionSyntax(itr.getStr(), itr.getNextIdx());
                                          }

                                          start = itr.getNextIdx();
                                       }
                                    } else {
                                       start = itr.getNextIdx();
                                    }
                                 }
                              }
                              continue;
                           case 125:
                              if (flags.contains(Expression.Flag.MINI_EXPRS)) {
                                 list.add(new ExpressionNode(false, LiteralNode.CLOSE_BRACE, Node.NULL));
                                 start = itr.getNextIdx();
                              } else {
                                 if (endOnBrace) {
                                    if (flags.contains(Expression.Flag.LENIENT_SYNTAX)) {
                                       list.add(LiteralNode.DOLLAR);
                                       itr.prev();
                                       return Node.fromList(list);
                                    }

                                    throw invalidExpressionSyntax(itr.getStr(), idx);
                                 }

                                 if (!flags.contains(Expression.Flag.LENIENT_SYNTAX)) {
                                    throw invalidExpressionSyntax(itr.getStr(), idx);
                                 }

                                 list.add(LiteralNode.DOLLAR);
                                 list.add(LiteralNode.CLOSE_BRACE);
                                 start = itr.getNextIdx();
                              }
                              continue;
                           default:
                              if (flags.contains(Expression.Flag.MINI_EXPRS)) {
                                 list.add(new ExpressionNode(false, new LiteralNode(itr.getStr(), idx, itr.getNextIdx()), Node.NULL));
                                 start = itr.getNextIdx();
                              } else {
                                 if (!flags.contains(Expression.Flag.LENIENT_SYNTAX)) {
                                    throw invalidExpressionSyntax(itr.getStr(), idx);
                                 }

                                 start = itr.getPrevIdx() - 1;
                              }
                        }
                     }
                     break;
                  case 58:
                     if (endOnColon) {
                        itr.prev();
                        if (idx > start) {
                           list.add(new LiteralNode(itr.getStr(), start, idx));
                        }

                        return Node.fromList(list);
                     }
                     break;
                  case 92:
                     if (!flags.contains(Expression.Flag.ESCAPES)) {
                        break;
                     }

                     if (idx > start) {
                        list.add(new LiteralNode(itr.getStr(), start, idx));
                        start = idx;
                     }

                     if (!itr.hasNext()) {
                        if (!flags.contains(Expression.Flag.LENIENT_SYNTAX)) {
                           throw invalidExpressionSyntax(itr.getStr(), idx);
                        }
                     } else {
                        ch = itr.next();
                        LiteralNode node;
                        switch (ch) {
                           case 92:
                              node = LiteralNode.BACKSLASH;
                              break;
                           case 98:
                              node = LiteralNode.BACKSPACE;
                              break;
                           case 102:
                              node = LiteralNode.FORM_FEED;
                              break;
                           case 110:
                              node = LiteralNode.NEWLINE;
                              break;
                           case 114:
                              node = LiteralNode.CARRIAGE_RETURN;
                              break;
                           case 116:
                              node = LiteralNode.TAB;
                              break;
                           default:
                              if (!flags.contains(Expression.Flag.LENIENT_SYNTAX)) {
                                 throw invalidExpressionSyntax(itr.getStr(), idx);
                              }

                              start = itr.getPrevIdx();
                              continue;
                        }

                        list.add(node);
                        start = itr.getNextIdx();
                     }
                     break;
                  case 123:
                     if (!flags.contains(Expression.Flag.NO_SMART_BRACES)) {
                        ++ignoreBraceLevel;
                     }
                     break;
                  case 125:
                     if (!flags.contains(Expression.Flag.NO_SMART_BRACES) && ignoreBraceLevel > 0) {
                        --ignoreBraceLevel;
                     } else if (endOnBrace) {
                        itr.prev();
                        if (idx >= start) {
                           list.add(new LiteralNode(itr.getStr(), start, idx));
                        }

                        return Node.fromList(list);
                     }
               }
            }

            idx = itr.getStr().length();
            if (idx > start) {
               list.add(new LiteralNode(itr.getStr(), start, idx));
            }

            return Node.fromList(list);
         }
      }
   }

   private static IllegalArgumentException invalidExpressionSyntax(String string, int index) {
      String msg = CommonMessages.msg.invalidExpressionSyntax(index);
      StringBuilder b = new StringBuilder(msg.length() + string.length() + string.length() + 5);
      b.append(msg);
      b.append('\n').append('\t').append(string);
      b.append('\n').append('\t');

      for(int i = 0; i < index; i = string.offsetByCodePoints(i, 1)) {
         int cp = string.codePointAt(i);
         if (Character.isWhitespace(cp)) {
            b.append(cp);
         } else if (Character.isValidCodePoint(cp) && !Character.isISOControl(cp)) {
            b.append(' ');
         }
      }

      b.append('^');
      return new IllegalArgumentException(b.toString());
   }

   static {
      EMPTY = new Expression(Node.NULL);
      NO_FLAGS = EnumSet.noneOf(Flag.class);
   }

   public static enum Flag {
      NO_TRIM,
      LENIENT_SYNTAX,
      MINI_EXPRS,
      NO_RECURSE_KEY,
      NO_RECURSE_DEFAULT,
      NO_SMART_BRACES,
      GENERAL_EXPANSION,
      ESCAPES,
      DOUBLE_COLON;
   }

   static final class Itr {
      private final String str;
      private int idx;

      Itr(String str) {
         this.str = str;
      }

      boolean hasNext() {
         return this.idx < this.str.length();
      }

      int next() {
         int idx = this.idx;

         int var2;
         try {
            var2 = this.str.codePointAt(idx);
         } finally {
            this.idx = this.str.offsetByCodePoints(idx, 1);
         }

         return var2;
      }

      int prev() {
         int idx = this.idx;

         int var2;
         try {
            var2 = this.str.codePointBefore(idx);
         } finally {
            this.idx = this.str.offsetByCodePoints(idx, -1);
         }

         return var2;
      }

      int getNextIdx() {
         return this.idx;
      }

      int getPrevIdx() {
         return this.str.offsetByCodePoints(this.idx, -1);
      }

      String getStr() {
         return this.str;
      }

      int peekNext() {
         return this.str.codePointAt(this.idx);
      }

      int peekPrev() {
         return this.str.codePointBefore(this.idx);
      }

      void rewind(int newNext) {
         this.idx = newNext;
      }
   }
}
