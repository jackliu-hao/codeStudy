package io.undertow.server.handlers.builder;

import io.undertow.UndertowLogger;
import io.undertow.UndertowMessages;
import io.undertow.attribute.ExchangeAttribute;
import io.undertow.attribute.ExchangeAttributeParser;
import io.undertow.attribute.ExchangeAttributes;
import io.undertow.predicate.Predicate;
import io.undertow.predicate.PredicateBuilder;
import io.undertow.predicate.Predicates;
import io.undertow.predicate.PredicatesHandler;
import io.undertow.server.HandlerWrapper;
import io.undertow.util.FileUtils;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;

public class PredicatedHandlersParser {
   public static final String ELSE = "else";
   public static final String ARROW = "->";
   public static final String NOT = "not";
   public static final String OR = "or";
   public static final String AND = "and";
   public static final String TRUE = "true";
   public static final String FALSE = "false";

   public static List<PredicatedHandler> parse(File file, ClassLoader classLoader) {
      return parse(file.toPath(), classLoader);
   }

   public static List<PredicatedHandler> parse(Path file, ClassLoader classLoader) {
      try {
         return parse(new String(Files.readAllBytes(file), StandardCharsets.UTF_8), classLoader);
      } catch (IOException var3) {
         throw new RuntimeException(var3);
      }
   }

   public static List<PredicatedHandler> parse(InputStream inputStream, ClassLoader classLoader) {
      return parse(FileUtils.readFile(inputStream), classLoader);
   }

   public static List<PredicatedHandler> parse(String contents, ClassLoader classLoader) {
      Deque<Token> tokens = tokenize(contents);
      Node node = parse(contents, tokens);
      Map<String, PredicateBuilder> predicateBuilders = loadPredicateBuilders(classLoader);
      Map<String, HandlerBuilder> handlerBuilders = loadHandlerBuilders(classLoader);
      ExchangeAttributeParser attributeParser = ExchangeAttributes.parser(classLoader);
      return handleNode(contents, node, predicateBuilders, handlerBuilders, attributeParser);
   }

   public static Predicate parsePredicate(String string, ClassLoader classLoader) {
      Deque<Token> tokens = tokenize(string);
      Node node = parse(string, tokens);
      Map<String, PredicateBuilder> predicateBuilders = loadPredicateBuilders(classLoader);
      ExchangeAttributeParser attributeParser = ExchangeAttributes.parser(classLoader);
      return handlePredicateNode(string, node, predicateBuilders, attributeParser);
   }

   public static HandlerWrapper parseHandler(String string, ClassLoader classLoader) {
      Deque<Token> tokens = tokenize(string);
      Node node = parse(string, tokens);
      Map<String, HandlerBuilder> handlerBuilders = loadHandlerBuilders(classLoader);
      ExchangeAttributeParser attributeParser = ExchangeAttributes.parser(classLoader);
      return handleHandlerNode(string, (ExpressionNode)node, handlerBuilders, attributeParser);
   }

   private static List<PredicatedHandler> handleNode(String contents, Node node, Map<String, PredicateBuilder> predicateBuilders, Map<String, HandlerBuilder> handlerBuilders, ExchangeAttributeParser attributeParser) {
      if (node instanceof BlockNode) {
         return handleBlockNode(contents, (BlockNode)node, predicateBuilders, handlerBuilders, attributeParser);
      } else if (node instanceof ExpressionNode) {
         HandlerWrapper handler = handleHandlerNode(contents, (ExpressionNode)node, handlerBuilders, attributeParser);
         return Collections.singletonList(new PredicatedHandler(Predicates.truePredicate(), handler));
      } else if (node instanceof PredicateOperatorNode) {
         return Collections.singletonList(handlePredicateOperatorNode(contents, (PredicateOperatorNode)node, predicateBuilders, handlerBuilders, attributeParser));
      } else {
         throw error(contents, node.getToken().getPosition(), "unexpected token " + node.getToken());
      }
   }

   private static PredicatedHandler handlePredicateOperatorNode(String contents, PredicateOperatorNode node, Map<String, PredicateBuilder> predicateBuilders, Map<String, HandlerBuilder> handlerBuilders, ExchangeAttributeParser parser) {
      Predicate predicate = handlePredicateNode(contents, node.getLeft(), predicateBuilders, parser);
      HandlerWrapper ret = handlePredicatedAction(contents, node.getRight(), predicateBuilders, handlerBuilders, parser);
      HandlerWrapper elseBranch = null;
      if (node.getElseBranch() != null) {
         elseBranch = handlePredicatedAction(contents, node.getElseBranch(), predicateBuilders, handlerBuilders, parser);
      }

      return new PredicatedHandler(predicate, ret, elseBranch);
   }

   private static HandlerWrapper handlePredicatedAction(String contents, Node node, Map<String, PredicateBuilder> predicateBuilders, Map<String, HandlerBuilder> handlerBuilders, ExchangeAttributeParser parser) {
      if (node instanceof ExpressionNode) {
         return handleHandlerNode(contents, (ExpressionNode)node, handlerBuilders, parser);
      } else {
         List handlers;
         if (node instanceof BlockNode) {
            handlers = handleBlockNode(contents, (BlockNode)node, predicateBuilders, handlerBuilders, parser);
            return new PredicatesHandler.Wrapper(handlers, false);
         } else if (node instanceof PredicateOperatorNode) {
            handlers = Collections.singletonList(handlePredicateOperatorNode(contents, (PredicateOperatorNode)node, predicateBuilders, handlerBuilders, parser));
            return new PredicatesHandler.Wrapper(handlers, false);
         } else {
            throw error(contents, node.getToken().getPosition(), "unexpected token " + node.getToken());
         }
      }
   }

   private static List<PredicatedHandler> handleBlockNode(String contents, BlockNode node, Map<String, PredicateBuilder> predicateBuilders, Map<String, HandlerBuilder> handlerBuilders, ExchangeAttributeParser parser) {
      List<PredicatedHandler> ret = new ArrayList();
      Iterator var6 = node.getBlock().iterator();

      while(var6.hasNext()) {
         Node line = (Node)var6.next();
         ret.addAll(handleNode(contents, line, predicateBuilders, handlerBuilders, parser));
      }

      return ret;
   }

   private static HandlerWrapper handleHandlerNode(String contents, ExpressionNode node, Map<String, HandlerBuilder> handlerBuilders, ExchangeAttributeParser parser) {
      Token token = node.getToken();
      HandlerBuilder builder = (HandlerBuilder)handlerBuilders.get(token.getToken());
      if (builder == null) {
         throw error(contents, token.getPosition(), "no handler named " + token.getToken() + " known handlers are " + handlerBuilders.keySet());
      } else {
         Map<String, Object> parameters = new HashMap();
         Iterator var7 = node.getValues().entrySet().iterator();

         while(var7.hasNext()) {
            Map.Entry<String, Node> val = (Map.Entry)var7.next();
            String name = (String)val.getKey();
            if (name == null) {
               if (builder.defaultParameter() == null) {
                  throw error(contents, token.getPosition(), "default parameter not supported");
               }

               name = builder.defaultParameter();
            }

            Class<?> type = (Class)builder.parameters().get(name);
            if (type == null) {
               throw error(contents, ((Node)val.getValue()).getToken().getPosition(), "unknown parameter " + name);
            }

            if (val.getValue() instanceof ValueNode) {
               parameters.put(name, coerceToType(contents, ((Node)val.getValue()).getToken(), type, parser));
            } else {
               if (!(val.getValue() instanceof ArrayNode)) {
                  throw error(contents, ((Node)val.getValue()).getToken().getPosition(), "unexpected node " + val.getValue());
               }

               parameters.put(name, readArrayType(contents, name, (ArrayNode)val.getValue(), parser, type));
            }
         }

         return builder.build(parameters);
      }
   }

   private static Predicate handlePredicateNode(String contents, Node node, Map<String, PredicateBuilder> handlerBuilders, ExchangeAttributeParser parser) {
      if (node instanceof AndNode) {
         AndNode andNode = (AndNode)node;
         return Predicates.and(handlePredicateNode(contents, andNode.getLeft(), handlerBuilders, parser), handlePredicateNode(contents, andNode.getRight(), handlerBuilders, parser));
      } else if (node instanceof OrNode) {
         OrNode orNode = (OrNode)node;
         return Predicates.or(handlePredicateNode(contents, orNode.getLeft(), handlerBuilders, parser), handlePredicateNode(contents, orNode.getRight(), handlerBuilders, parser));
      } else if (node instanceof NotNode) {
         NotNode orNode = (NotNode)node;
         return Predicates.not(handlePredicateNode(contents, orNode.getNode(), handlerBuilders, parser));
      } else if (node instanceof ExpressionNode) {
         return handlePredicateExpressionNode(contents, (ExpressionNode)node, handlerBuilders, parser);
      } else {
         if (node instanceof OperatorNode) {
            switch (node.getToken().getToken()) {
               case "true":
                  return Predicates.truePredicate();
               case "false":
                  return Predicates.falsePredicate();
            }
         }

         throw error(contents, node.getToken().getPosition(), "unexpected node " + node);
      }
   }

   private static Predicate handlePredicateExpressionNode(String contents, ExpressionNode node, Map<String, PredicateBuilder> handlerBuilders, ExchangeAttributeParser parser) {
      Token token = node.getToken();
      PredicateBuilder builder = (PredicateBuilder)handlerBuilders.get(token.getToken());
      if (builder == null) {
         throw error(contents, token.getPosition(), "no predicate named " + token.getToken() + " known predicates are " + handlerBuilders.keySet());
      } else {
         Map<String, Object> parameters = new HashMap();
         Iterator var7 = node.getValues().entrySet().iterator();

         while(var7.hasNext()) {
            Map.Entry<String, Node> val = (Map.Entry)var7.next();
            String name = (String)val.getKey();
            if (name == null) {
               if (builder.defaultParameter() == null) {
                  throw error(contents, token.getPosition(), "default parameter not supported");
               }

               name = builder.defaultParameter();
            }

            Class<?> type = (Class)builder.parameters().get(name);
            if (type == null) {
               throw error(contents, ((Node)val.getValue()).getToken().getPosition(), "unknown parameter " + name);
            }

            if (val.getValue() instanceof ValueNode) {
               parameters.put(name, coerceToType(contents, ((Node)val.getValue()).getToken(), type, parser));
            } else {
               if (!(val.getValue() instanceof ArrayNode)) {
                  throw error(contents, ((Node)val.getValue()).getToken().getPosition(), "unexpected node " + val.getValue());
               }

               parameters.put(name, readArrayType(contents, name, (ArrayNode)val.getValue(), parser, type));
            }
         }

         return builder.build(parameters);
      }
   }

   private static Object readArrayType(String string, String paramName, ArrayNode value, ExchangeAttributeParser parser, Class type) {
      if (!type.isArray()) {
         throw error(string, value.getToken().getPosition(), "parameter is not an array type " + paramName);
      } else {
         Class<?> componentType = type.getComponentType();
         List<Object> values = new ArrayList();
         Iterator var7 = value.getValues().iterator();

         while(var7.hasNext()) {
            Token token = (Token)var7.next();
            values.add(coerceToType(string, token, componentType, parser));
         }

         Object array = Array.newInstance(componentType, values.size());

         for(int i = 0; i < values.size(); ++i) {
            Array.set(array, i, values.get(i));
         }

         return array;
      }
   }

   private static Object coerceToType(String string, Token token, Class<?> type, ExchangeAttributeParser attributeParser) {
      if (type.isArray()) {
         Object array = Array.newInstance(type.getComponentType(), 1);
         Array.set(array, 0, coerceToType(string, token, type.getComponentType(), attributeParser));
         return array;
      } else if (type == String.class) {
         return token.getToken();
      } else if (!type.equals(Boolean.class) && !type.equals(Boolean.TYPE)) {
         if (!type.equals(Byte.class) && !type.equals(Byte.TYPE)) {
            if (!type.equals(Character.class) && !type.equals(Character.TYPE)) {
               if (!type.equals(Short.class) && !type.equals(Short.TYPE)) {
                  if (!type.equals(Integer.class) && !type.equals(Integer.TYPE)) {
                     if (!type.equals(Long.class) && !type.equals(Long.TYPE)) {
                        if (!type.equals(Float.class) && !type.equals(Float.TYPE)) {
                           if (!type.equals(Double.class) && !type.equals(Double.TYPE)) {
                              return type.equals(ExchangeAttribute.class) ? attributeParser.parse(token.getToken()) : token.getToken();
                           } else {
                              return Double.valueOf(token.getToken());
                           }
                        } else {
                           return Float.valueOf(token.getToken());
                        }
                     } else {
                        return Long.valueOf(token.getToken());
                     }
                  } else {
                     return Integer.valueOf(token.getToken());
                  }
               } else {
                  return Short.valueOf(token.getToken());
               }
            } else if (token.getToken().length() != 1) {
               throw error(string, token.getPosition(), "Cannot coerce " + token.getToken() + " to a Character");
            } else {
               return token.getToken().charAt(0);
            }
         } else {
            return Byte.valueOf(token.getToken());
         }
      } else {
         return Boolean.valueOf(token.getToken());
      }
   }

   private static Map<String, PredicateBuilder> loadPredicateBuilders(ClassLoader classLoader) {
      ServiceLoader<PredicateBuilder> loader = ServiceLoader.load(PredicateBuilder.class, classLoader);
      Map<String, PredicateBuilder> ret = new HashMap();
      Iterator var3 = loader.iterator();

      while(var3.hasNext()) {
         PredicateBuilder builder = (PredicateBuilder)var3.next();
         if (ret.containsKey(builder.name())) {
            if (((PredicateBuilder)ret.get(builder.name())).getClass() != builder.getClass()) {
               throw UndertowMessages.MESSAGES.moreThanOnePredicateWithName(builder.name(), builder.getClass(), ((PredicateBuilder)ret.get(builder.name())).getClass());
            }
         } else {
            ret.put(builder.name(), builder);
         }
      }

      return ret;
   }

   private static Map<String, HandlerBuilder> loadHandlerBuilders(ClassLoader classLoader) {
      ServiceLoader<HandlerBuilder> loader = ServiceLoader.load(HandlerBuilder.class, classLoader);
      Map<String, HandlerBuilder> ret = new HashMap();
      Iterator var3 = loader.iterator();

      while(var3.hasNext()) {
         HandlerBuilder builder = (HandlerBuilder)var3.next();
         if (ret.containsKey(builder.name())) {
            if (((HandlerBuilder)ret.get(builder.name())).getClass() != builder.getClass()) {
               throw UndertowMessages.MESSAGES.moreThanOneHandlerWithName(builder.name(), builder.getClass(), ((HandlerBuilder)ret.get(builder.name())).getClass());
            }
         } else {
            ret.put(builder.name(), builder);
         }
      }

      return ret;
   }

   static Node parse(String string, Deque<Token> tokens) {
      return parse(string, tokens, true);
   }

   static Node parse(String string, Deque<Token> tokens, boolean topLevel) {
      Deque<Token> operatorStack = new ArrayDeque();
      Deque<Node> output = new ArrayDeque();
      List<Node> blocks = new ArrayList();

      while(!tokens.isEmpty()) {
         Token token = (Token)tokens.poll();
         if (token.getToken().equals("{")) {
            output.push(parse(string, tokens, false));
         } else {
            if (token.getToken().equals("}")) {
               if (topLevel) {
                  throw error(string, token.getPosition(), "Unexpected token");
               }
               break;
            }

            if (!token.getToken().equals("\n") && !token.getToken().equals(";")) {
               if (isSpecialChar(token.getToken())) {
                  if (token.getToken().equals("(")) {
                     operatorStack.push(token);
                  } else if (token.getToken().equals(")")) {
                     while(true) {
                        Token op = (Token)operatorStack.pop();
                        if (op == null) {
                           throw error(string, token.getPosition(), "Unexpected end of input");
                        }

                        if (op.getToken().equals("(")) {
                           break;
                        }

                        output.push(new OperatorNode(op));
                     }
                  } else {
                     output.push(new OperatorNode(token));
                  }
               } else if (isOperator(token.getToken()) && !token.getToken().equals("else")) {
                  int prec = precedence(token.getToken());

                  for(Token top = (Token)operatorStack.peek(); top != null && !top.getToken().equals("("); top = (Token)operatorStack.peek()) {
                     int exitingPrec = precedence(top.getToken());
                     if (prec > exitingPrec) {
                        break;
                     }

                     output.push(new OperatorNode((Token)operatorStack.pop()));
                  }

                  operatorStack.push(token);
               } else {
                  output.push(parseExpression(string, token, tokens));
               }
            } else if (!token.getToken().equals(";") || tokens.peek() == null || !((Token)tokens.peek()).getToken().equals("else")) {
               handleLineEnd(string, operatorStack, output, blocks);
            }
         }
      }

      handleLineEnd(string, operatorStack, output, blocks);
      return (Node)(blocks.size() == 1 ? (Node)blocks.get(0) : new BlockNode(new Token("", 0), blocks));
   }

   private static void handleLineEnd(String string, Deque<Token> operatorStack, Deque<Node> output, List<Node> blocks) {
      while(!operatorStack.isEmpty()) {
         Token op = (Token)operatorStack.pop();
         if (op.getToken().equals(")")) {
            throw error(string, string.length(), "Mismatched parenthesis");
         }

         output.push(new OperatorNode(op));
      }

      if (!output.isEmpty()) {
         Node predicate = collapseOutput((Node)output.pop(), output);
         if (!output.isEmpty()) {
            throw error(string, ((Node)output.getFirst()).getToken().getPosition(), "Invalid expression");
         } else {
            blocks.add(predicate);
         }
      }
   }

   private static Node collapseOutput(Node token, Deque<Node> tokens) {
      if (token instanceof OperatorNode) {
         OperatorNode node = (OperatorNode)token;
         Node n1;
         Node n2;
         if (node.token.getToken().equals("and")) {
            n1 = collapseOutput((Node)tokens.pop(), tokens);
            n2 = collapseOutput((Node)tokens.pop(), tokens);
            return new AndNode(token.getToken(), n2, n1);
         } else if (node.token.getToken().equals("or")) {
            n1 = collapseOutput((Node)tokens.pop(), tokens);
            n2 = collapseOutput((Node)tokens.pop(), tokens);
            return new OrNode(token.getToken(), n2, n1);
         } else if (node.token.getToken().equals("not")) {
            n1 = collapseOutput((Node)tokens.pop(), tokens);
            return new NotNode(token.getToken(), n1);
         } else if (node.token.getToken().equals("->")) {
            n1 = collapseOutput((Node)tokens.pop(), tokens);
            n2 = null;
            Node elseBranch = null;
            Node popped = (Node)tokens.pop();
            if (popped.getToken().getToken().equals("else")) {
               elseBranch = n1;
               n1 = collapseOutput((Node)tokens.pop(), tokens);
               n2 = collapseOutput((Node)tokens.pop(), tokens);
            } else {
               n2 = collapseOutput(popped, tokens);
            }

            return new PredicateOperatorNode(token.getToken(), n2, n1, elseBranch);
         } else {
            return token;
         }
      } else {
         return token;
      }
   }

   private static Node parseExpression(String string, Token token, Deque<Token> tokens) {
      if (token.getToken().equals("true")) {
         return new OperatorNode(token);
      } else if (token.getToken().equals("false")) {
         return new OperatorNode(token);
      } else {
         Token next = (Token)tokens.peek();
         String endChar = ")";
         if (next != null && (next.getToken().equals("[") || next.getToken().equals("("))) {
            if (next.getToken().equals("[")) {
               endChar = "]";
               UndertowLogger.ROOT_LOGGER.oldStylePredicateSyntax(string);
            }

            Map<String, Node> values = new HashMap();
            tokens.poll();
            next = (Token)tokens.poll();
            if (next == null) {
               throw error(string, string.length(), "Unexpected end of input");
            } else if (next.getToken().equals("{")) {
               return handleSingleArrayValue(string, token, tokens, endChar);
            } else {
               while(!next.getToken().equals(endChar)) {
                  Token equals = (Token)tokens.poll();
                  if (equals == null) {
                     throw error(string, string.length(), "Unexpected end of input");
                  }

                  if (!equals.getToken().equals("=")) {
                     if (equals.getToken().equals(endChar) && values.isEmpty()) {
                        return handleSingleValue(token, next);
                     }

                     if (equals.getToken().equals(",")) {
                        tokens.push(equals);
                        tokens.push(next);
                        return handleSingleVarArgsValue(string, token, tokens, endChar);
                     }

                     throw error(string, equals.getPosition(), "Unexpected token");
                  }

                  Token value = (Token)tokens.poll();
                  if (value == null) {
                     throw error(string, string.length(), "Unexpected end of input");
                  }

                  if (value.getToken().equals("{")) {
                     values.put(next.getToken(), new ArrayNode(value, readArrayType(string, tokens, "}")));
                  } else {
                     if (isOperator(value.getToken()) || isSpecialChar(value.getToken())) {
                        throw error(string, value.getPosition(), "Unexpected token");
                     }

                     values.put(next.getToken(), new ValueNode(value));
                  }

                  next = (Token)tokens.poll();
                  if (next == null) {
                     throw error(string, string.length(), "Unexpected end of input");
                  }

                  if (!next.getToken().equals(endChar)) {
                     if (!next.getToken().equals(",")) {
                        throw error(string, string.length(), "Expecting , or " + endChar);
                     }

                     next = (Token)tokens.poll();
                     if (next == null) {
                        throw error(string, string.length(), "Unexpected end of input");
                     }
                  }
               }

               return new ExpressionNode(token, values);
            }
         } else if (next != null && isSpecialChar(next.getToken())) {
            throw error(string, next.getPosition(), "Unexpected character");
         } else {
            return new ExpressionNode(token, Collections.emptyMap());
         }
      }
   }

   private static Node handleSingleArrayValue(String string, Token builder, Deque<Token> tokens, String endChar) {
      List<Token> array = readArrayType(string, tokens, "}");
      Token close = (Token)tokens.poll();
      if (!close.getToken().equals(endChar)) {
         throw error(string, close.getPosition(), "expected " + endChar);
      } else {
         return new ExpressionNode(builder, Collections.singletonMap((Object)null, new ArrayNode(builder, array)));
      }
   }

   private static Node handleSingleVarArgsValue(String string, Token expressionName, Deque<Token> tokens, String endChar) {
      List<Token> array = readArrayType(string, tokens, endChar);
      return new ExpressionNode(expressionName, Collections.singletonMap((Object)null, new ArrayNode(expressionName, array)));
   }

   private static List<Token> readArrayType(String string, Deque<Token> tokens, String expectedEndToken) {
      List<Token> values = new ArrayList();
      Token token = (Token)tokens.poll();
      if (token.getToken().equals(expectedEndToken)) {
         return Collections.emptyList();
      } else {
         while(token != null) {
            Token commaOrEnd = (Token)tokens.poll();
            values.add(token);
            if (commaOrEnd.getToken().equals(expectedEndToken)) {
               return values;
            }

            if (!commaOrEnd.getToken().equals(",")) {
               throw error(string, commaOrEnd.getPosition(), "expected either , or " + expectedEndToken);
            }

            token = (Token)tokens.poll();
         }

         throw error(string, string.length(), "unexpected end of input in array");
      }
   }

   private static Node handleSingleValue(Token token, Token next) {
      return new ExpressionNode(token, Collections.singletonMap((Object)null, new ValueNode(next)));
   }

   private static int precedence(String operator) {
      if (operator.equals("not")) {
         return 3;
      } else if (operator.equals("and")) {
         return 2;
      } else if (operator.equals("or")) {
         return 1;
      } else if (operator.equals("->")) {
         return -1000;
      } else {
         throw new IllegalStateException();
      }
   }

   private static boolean isOperator(String op) {
      return op.equals("and") || op.equals("or") || op.equals("not") || op.equals("->");
   }

   private static boolean isSpecialChar(String token) {
      if (token.length() == 1) {
         char c = token.charAt(0);
         switch (c) {
            case '(':
            case ')':
            case ',':
            case '=':
            case '[':
            case ']':
               return true;
            default:
               return false;
         }
      } else {
         return false;
      }
   }

   public static Deque<Token> tokenize(String string) {
      char currentStringDelim = 0;
      boolean inVariable = false;
      int pos = 0;
      StringBuilder current = new StringBuilder();

      ArrayDeque ret;
      for(ret = new ArrayDeque(); pos < string.length(); ++pos) {
         char c = string.charAt(pos);
         if (currentStringDelim != 0) {
            if (c == currentStringDelim && current.charAt(current.length() - 1) != '\\') {
               ret.add(new Token(current.toString(), pos));
               current.setLength(0);
               currentStringDelim = 0;
            } else if (c != '\n' && c != '\r') {
               current.append(c);
            } else {
               ret.add(new Token(current.toString(), pos));
               current.setLength(0);
               currentStringDelim = 0;
               ret.add(new Token("\n", pos));
            }
         } else {
            switch (c) {
               case '\t':
               case ' ':
                  if (current.length() != 0) {
                     ret.add(new Token(current.toString(), pos));
                     current.setLength(0);
                  }
                  break;
               case '\n':
               case '\r':
                  if (current.length() != 0) {
                     ret.add(new Token(current.toString(), pos));
                     current.setLength(0);
                  }

                  ret.add(new Token("\n", pos));
                  break;
               case '"':
               case '\'':
                  if (current.length() != 0) {
                     throw error(string, pos, "Unexpected token");
                  }

                  currentStringDelim = c;
                  break;
               case '$':
               case '%':
                  current.append(c);
                  if (string.charAt(pos + 1) == '{') {
                     inVariable = true;
                  }
                  break;
               case '(':
               case ')':
               case ',':
               case ';':
               case '=':
               case '[':
               case ']':
               case '{':
               case '}':
                  if (inVariable) {
                     current.append(c);
                     if (c == '}') {
                        inVariable = false;
                     }
                  } else {
                     if (current.length() != 0) {
                        ret.add(new Token(current.toString(), pos));
                        current.setLength(0);
                     }

                     ret.add(new Token("" + c, pos));
                  }
                  break;
               case '-':
                  if (inVariable) {
                     current.append(c);
                  } else {
                     if (pos != string.length() && string.charAt(pos + 1) == '>') {
                        ++pos;
                        if (current.length() != 0) {
                           ret.add(new Token(current.toString(), pos));
                           current.setLength(0);
                        }

                        ret.add(new Token("->", pos));
                        continue;
                     }

                     current.append(c);
                  }
                  break;
               default:
                  current.append(c);
            }
         }
      }

      if (current.length() > 0) {
         ret.add(new Token(current.toString(), string.length()));
      }

      return ret;
   }

   private static IllegalStateException error(String string, int pos, String reason) {
      StringBuilder b = new StringBuilder();
      int linePos = 0;

      int i;
      for(i = 0; i < string.length(); ++i) {
         if (string.charAt(i) == '\n') {
            if (i >= pos) {
               break;
            }

            linePos = 0;
         } else if (i < pos) {
            ++linePos;
         }

         b.append(string.charAt(i));
      }

      b.append('\n');

      for(i = 0; i < linePos; ++i) {
         b.append(' ');
      }

      b.append('^');
      throw UndertowMessages.MESSAGES.errorParsingPredicateString(reason, b.toString());
   }

   static final class Token {
      private final String token;
      private final int position;

      Token(String token, int position) {
         this.token = token;
         this.position = position;
      }

      public String getToken() {
         return this.token;
      }

      public int getPosition() {
         return this.position;
      }

      public String toString() {
         return this.token + " <" + this.position + ">";
      }
   }

   static class BlockNode implements Node {
      private final Token token;
      private final List<Node> block;

      BlockNode(Token token, List<Node> block) {
         this.token = token;
         this.block = block;
      }

      public List<Node> getBlock() {
         return this.block;
      }

      public Token getToken() {
         return this.token;
      }
   }

   static class NotNode implements Node {
      private final Token token;
      private final Node node;

      NotNode(Token token, Node node) {
         this.token = token;
         this.node = node;
      }

      public Node getNode() {
         return this.node;
      }

      public Token getToken() {
         return this.token;
      }
   }

   static class PredicateOperatorNode implements Node {
      private final Token token;
      private final Node left;
      private final Node right;
      private final Node elseBranch;

      PredicateOperatorNode(Token token, Node left, Node right, Node elseBranch) {
         this.token = token;
         this.left = left;
         this.right = right;
         this.elseBranch = elseBranch;
      }

      public Node getLeft() {
         return this.left;
      }

      public Node getRight() {
         return this.right;
      }

      public Node getElseBranch() {
         return this.elseBranch;
      }

      public Token getToken() {
         return this.token;
      }
   }

   static class OrNode implements Node {
      private final Token token;
      private final Node left;
      private final Node right;

      OrNode(Token token, Node left, Node right) {
         this.token = token;
         this.left = left;
         this.right = right;
      }

      public Node getLeft() {
         return this.left;
      }

      public Node getRight() {
         return this.right;
      }

      public Token getToken() {
         return this.token;
      }
   }

   static class AndNode implements Node {
      private final Token token;
      private final Node left;
      private final Node right;

      AndNode(Token token, Node left, Node right) {
         this.token = token;
         this.left = left;
         this.right = right;
      }

      public Node getLeft() {
         return this.left;
      }

      public Node getRight() {
         return this.right;
      }

      public Token getToken() {
         return this.token;
      }
   }

   static class OperatorNode implements Node {
      private final Token token;

      private OperatorNode(Token token) {
         this.token = token;
      }

      public Token getToken() {
         return this.token;
      }

      // $FF: synthetic method
      OperatorNode(Token x0, Object x1) {
         this(x0);
      }
   }

   static class ValueNode implements Node {
      private final Token value;

      private ValueNode(Token value) {
         this.value = value;
      }

      public Token getValue() {
         return this.value;
      }

      public String toString() {
         return this.value.getToken();
      }

      public Token getToken() {
         return this.value;
      }

      // $FF: synthetic method
      ValueNode(Token x0, Object x1) {
         this(x0);
      }
   }

   static class ArrayNode implements Node {
      private final Token start;
      private final List<Token> values;

      private ArrayNode(Token start, List<Token> tokens) {
         this.start = start;
         this.values = tokens;
      }

      public List<Token> getValues() {
         return this.values;
      }

      public Token getToken() {
         return this.start;
      }

      // $FF: synthetic method
      ArrayNode(Token x0, List x1, Object x2) {
         this(x0, x1);
      }
   }

   static class ExpressionNode implements Node {
      private final Token token;
      private final Map<String, Node> values;

      private ExpressionNode(Token token, Map<String, Node> values) {
         this.token = token;
         this.values = values;
      }

      public Token getToken() {
         return this.token;
      }

      public Map<String, Node> getValues() {
         return this.values;
      }

      // $FF: synthetic method
      ExpressionNode(Token x0, Map x1, Object x2) {
         this(x0, x1);
      }
   }

   public interface Node {
      Token getToken();
   }
}
