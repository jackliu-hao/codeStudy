/*     */ package io.undertow.server.handlers.builder;
/*     */ 
/*     */ import io.undertow.UndertowLogger;
/*     */ import io.undertow.UndertowMessages;
/*     */ import io.undertow.attribute.ExchangeAttribute;
/*     */ import io.undertow.attribute.ExchangeAttributeParser;
/*     */ import io.undertow.attribute.ExchangeAttributes;
/*     */ import io.undertow.predicate.Predicate;
/*     */ import io.undertow.predicate.PredicateBuilder;
/*     */ import io.undertow.predicate.Predicates;
/*     */ import io.undertow.predicate.PredicatesHandler;
/*     */ import io.undertow.server.HandlerWrapper;
/*     */ import io.undertow.util.FileUtils;
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.lang.reflect.Array;
/*     */ import java.nio.charset.StandardCharsets;
/*     */ import java.nio.file.Files;
/*     */ import java.nio.file.Path;
/*     */ import java.util.ArrayDeque;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.Deque;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.ServiceLoader;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class PredicatedHandlersParser
/*     */ {
/*     */   public static final String ELSE = "else";
/*     */   public static final String ARROW = "->";
/*     */   public static final String NOT = "not";
/*     */   public static final String OR = "or";
/*     */   public static final String AND = "and";
/*     */   public static final String TRUE = "true";
/*     */   public static final String FALSE = "false";
/*     */   
/*     */   public static List<PredicatedHandler> parse(File file, ClassLoader classLoader) {
/*  68 */     return parse(file.toPath(), classLoader);
/*     */   }
/*     */   
/*     */   public static List<PredicatedHandler> parse(Path file, ClassLoader classLoader) {
/*     */     try {
/*  73 */       return parse(new String(Files.readAllBytes(file), StandardCharsets.UTF_8), classLoader);
/*  74 */     } catch (IOException e) {
/*  75 */       throw new RuntimeException(e);
/*     */     } 
/*     */   }
/*     */   
/*     */   public static List<PredicatedHandler> parse(InputStream inputStream, ClassLoader classLoader) {
/*  80 */     return parse(FileUtils.readFile(inputStream), classLoader);
/*     */   }
/*     */   
/*     */   public static List<PredicatedHandler> parse(String contents, ClassLoader classLoader) {
/*  84 */     Deque<Token> tokens = tokenize(contents);
/*     */     
/*  86 */     Node node = parse(contents, tokens);
/*  87 */     Map<String, PredicateBuilder> predicateBuilders = loadPredicateBuilders(classLoader);
/*  88 */     Map<String, HandlerBuilder> handlerBuilders = loadHandlerBuilders(classLoader);
/*     */     
/*  90 */     ExchangeAttributeParser attributeParser = ExchangeAttributes.parser(classLoader);
/*  91 */     return handleNode(contents, node, predicateBuilders, handlerBuilders, attributeParser);
/*     */   }
/*     */ 
/*     */   
/*     */   public static Predicate parsePredicate(String string, ClassLoader classLoader) {
/*  96 */     Deque<Token> tokens = tokenize(string);
/*  97 */     Node node = parse(string, tokens);
/*  98 */     Map<String, PredicateBuilder> predicateBuilders = loadPredicateBuilders(classLoader);
/*  99 */     ExchangeAttributeParser attributeParser = ExchangeAttributes.parser(classLoader);
/* 100 */     return handlePredicateNode(string, node, predicateBuilders, attributeParser);
/*     */   }
/*     */   
/*     */   public static HandlerWrapper parseHandler(String string, ClassLoader classLoader) {
/* 104 */     Deque<Token> tokens = tokenize(string);
/* 105 */     Node node = parse(string, tokens);
/* 106 */     Map<String, HandlerBuilder> handlerBuilders = loadHandlerBuilders(classLoader);
/* 107 */     ExchangeAttributeParser attributeParser = ExchangeAttributes.parser(classLoader);
/* 108 */     return handleHandlerNode(string, (ExpressionNode)node, handlerBuilders, attributeParser);
/*     */   }
/*     */   private static List<PredicatedHandler> handleNode(String contents, Node node, Map<String, PredicateBuilder> predicateBuilders, Map<String, HandlerBuilder> handlerBuilders, ExchangeAttributeParser attributeParser) {
/* 111 */     if (node instanceof BlockNode)
/* 112 */       return handleBlockNode(contents, (BlockNode)node, predicateBuilders, handlerBuilders, attributeParser); 
/* 113 */     if (node instanceof ExpressionNode) {
/* 114 */       HandlerWrapper handler = handleHandlerNode(contents, (ExpressionNode)node, handlerBuilders, attributeParser);
/* 115 */       return Collections.singletonList(new PredicatedHandler(Predicates.truePredicate(), handler));
/* 116 */     }  if (node instanceof PredicateOperatorNode) {
/* 117 */       return Collections.singletonList(handlePredicateOperatorNode(contents, (PredicateOperatorNode)node, predicateBuilders, handlerBuilders, attributeParser));
/*     */     }
/* 119 */     throw error(contents, node.getToken().getPosition(), "unexpected token " + node.getToken());
/*     */   }
/*     */ 
/*     */   
/*     */   private static PredicatedHandler handlePredicateOperatorNode(String contents, PredicateOperatorNode node, Map<String, PredicateBuilder> predicateBuilders, Map<String, HandlerBuilder> handlerBuilders, ExchangeAttributeParser parser) {
/* 124 */     Predicate predicate = handlePredicateNode(contents, node.getLeft(), predicateBuilders, parser);
/* 125 */     HandlerWrapper ret = handlePredicatedAction(contents, node.getRight(), predicateBuilders, handlerBuilders, parser);
/* 126 */     HandlerWrapper elseBranch = null;
/* 127 */     if (node.getElseBranch() != null) {
/* 128 */       elseBranch = handlePredicatedAction(contents, node.getElseBranch(), predicateBuilders, handlerBuilders, parser);
/*     */     }
/* 130 */     return new PredicatedHandler(predicate, ret, elseBranch);
/*     */   }
/*     */   
/*     */   private static HandlerWrapper handlePredicatedAction(String contents, Node node, Map<String, PredicateBuilder> predicateBuilders, Map<String, HandlerBuilder> handlerBuilders, ExchangeAttributeParser parser) {
/* 134 */     if (node instanceof ExpressionNode)
/* 135 */       return handleHandlerNode(contents, (ExpressionNode)node, handlerBuilders, parser); 
/* 136 */     if (node instanceof BlockNode) {
/* 137 */       List<PredicatedHandler> handlers = handleBlockNode(contents, (BlockNode)node, predicateBuilders, handlerBuilders, parser);
/* 138 */       return (HandlerWrapper)new PredicatesHandler.Wrapper(handlers, false);
/* 139 */     }  if (node instanceof PredicateOperatorNode) {
/* 140 */       List<PredicatedHandler> handlers = Collections.singletonList(handlePredicateOperatorNode(contents, (PredicateOperatorNode)node, predicateBuilders, handlerBuilders, parser));
/* 141 */       return (HandlerWrapper)new PredicatesHandler.Wrapper(handlers, false);
/*     */     } 
/* 143 */     throw error(contents, node.getToken().getPosition(), "unexpected token " + node.getToken());
/*     */   }
/*     */ 
/*     */   
/*     */   private static List<PredicatedHandler> handleBlockNode(String contents, BlockNode node, Map<String, PredicateBuilder> predicateBuilders, Map<String, HandlerBuilder> handlerBuilders, ExchangeAttributeParser parser) {
/* 148 */     List<PredicatedHandler> ret = new ArrayList<>();
/* 149 */     for (Node line : node.getBlock()) {
/* 150 */       ret.addAll(handleNode(contents, line, predicateBuilders, handlerBuilders, parser));
/*     */     }
/* 152 */     return ret;
/*     */   }
/*     */ 
/*     */   
/*     */   private static HandlerWrapper handleHandlerNode(String contents, ExpressionNode node, Map<String, HandlerBuilder> handlerBuilders, ExchangeAttributeParser parser) {
/* 157 */     Token token = node.getToken();
/* 158 */     HandlerBuilder builder = handlerBuilders.get(token.getToken());
/* 159 */     if (builder == null) {
/* 160 */       throw error(contents, token.getPosition(), "no handler named " + token.getToken() + " known handlers are " + handlerBuilders.keySet());
/*     */     }
/* 162 */     Map<String, Object> parameters = new HashMap<>();
/*     */     
/* 164 */     for (Map.Entry<String, Node> val : node.getValues().entrySet()) {
/* 165 */       String name = val.getKey();
/* 166 */       if (name == null) {
/* 167 */         if (builder.defaultParameter() == null) {
/* 168 */           throw error(contents, token.getPosition(), "default parameter not supported");
/*     */         }
/* 170 */         name = builder.defaultParameter();
/*     */       } 
/* 172 */       Class<?> type = builder.parameters().get(name);
/* 173 */       if (type == null) {
/* 174 */         throw error(contents, ((Node)val.getValue()).getToken().getPosition(), "unknown parameter " + name);
/*     */       }
/* 176 */       if (val.getValue() instanceof ValueNode) {
/* 177 */         parameters.put(name, coerceToType(contents, ((Node)val.getValue()).getToken(), type, parser)); continue;
/* 178 */       }  if (val.getValue() instanceof ArrayNode) {
/* 179 */         parameters.put(name, readArrayType(contents, name, (ArrayNode)val.getValue(), parser, type)); continue;
/*     */       } 
/* 181 */       throw error(contents, ((Node)val.getValue()).getToken().getPosition(), "unexpected node " + val.getValue());
/*     */     } 
/*     */     
/* 184 */     return builder.build(parameters);
/*     */   }
/*     */   
/*     */   private static Predicate handlePredicateNode(String contents, Node node, Map<String, PredicateBuilder> handlerBuilders, ExchangeAttributeParser parser) {
/* 188 */     if (node instanceof AndNode) {
/* 189 */       AndNode andNode = (AndNode)node;
/* 190 */       return Predicates.and(new Predicate[] { handlePredicateNode(contents, andNode.getLeft(), handlerBuilders, parser), handlePredicateNode(contents, andNode.getRight(), handlerBuilders, parser) });
/* 191 */     }  if (node instanceof OrNode) {
/* 192 */       OrNode orNode = (OrNode)node;
/* 193 */       return Predicates.or(new Predicate[] { handlePredicateNode(contents, orNode.getLeft(), handlerBuilders, parser), handlePredicateNode(contents, orNode.getRight(), handlerBuilders, parser) });
/* 194 */     }  if (node instanceof NotNode) {
/* 195 */       NotNode orNode = (NotNode)node;
/* 196 */       return Predicates.not(handlePredicateNode(contents, orNode.getNode(), handlerBuilders, parser));
/* 197 */     }  if (node instanceof ExpressionNode)
/* 198 */       return handlePredicateExpressionNode(contents, (ExpressionNode)node, handlerBuilders, parser); 
/* 199 */     if (node instanceof OperatorNode) {
/* 200 */       switch (node.getToken().getToken()) {
/*     */         case "true":
/* 202 */           return Predicates.truePredicate();
/*     */         
/*     */         case "false":
/* 205 */           return Predicates.falsePredicate();
/*     */       } 
/*     */     
/*     */     }
/* 209 */     throw error(contents, node.getToken().getPosition(), "unexpected node " + node);
/*     */   }
/*     */   
/*     */   private static Predicate handlePredicateExpressionNode(String contents, ExpressionNode node, Map<String, PredicateBuilder> handlerBuilders, ExchangeAttributeParser parser) {
/* 213 */     Token token = node.getToken();
/* 214 */     PredicateBuilder builder = handlerBuilders.get(token.getToken());
/* 215 */     if (builder == null) {
/* 216 */       throw error(contents, token.getPosition(), "no predicate named " + token.getToken() + " known predicates are " + handlerBuilders.keySet());
/*     */     }
/* 218 */     Map<String, Object> parameters = new HashMap<>();
/*     */     
/* 220 */     for (Map.Entry<String, Node> val : node.getValues().entrySet()) {
/* 221 */       String name = val.getKey();
/* 222 */       if (name == null) {
/* 223 */         if (builder.defaultParameter() == null) {
/* 224 */           throw error(contents, token.getPosition(), "default parameter not supported");
/*     */         }
/* 226 */         name = builder.defaultParameter();
/*     */       } 
/* 228 */       Class<?> type = (Class)builder.parameters().get(name);
/* 229 */       if (type == null) {
/* 230 */         throw error(contents, ((Node)val.getValue()).getToken().getPosition(), "unknown parameter " + name);
/*     */       }
/* 232 */       if (val.getValue() instanceof ValueNode) {
/* 233 */         parameters.put(name, coerceToType(contents, ((Node)val.getValue()).getToken(), type, parser)); continue;
/* 234 */       }  if (val.getValue() instanceof ArrayNode) {
/* 235 */         parameters.put(name, readArrayType(contents, name, (ArrayNode)val.getValue(), parser, type)); continue;
/*     */       } 
/* 237 */       throw error(contents, ((Node)val.getValue()).getToken().getPosition(), "unexpected node " + val.getValue());
/*     */     } 
/*     */     
/* 240 */     return builder.build(parameters);
/*     */   }
/*     */   
/*     */   private static Object readArrayType(String string, String paramName, ArrayNode value, ExchangeAttributeParser parser, Class type) {
/* 244 */     if (!type.isArray()) {
/* 245 */       throw error(string, value.getToken().getPosition(), "parameter is not an array type " + paramName);
/*     */     }
/*     */     
/* 248 */     Class<?> componentType = type.getComponentType();
/* 249 */     List<Object> values = new ArrayList();
/* 250 */     for (Token token : value.getValues()) {
/* 251 */       values.add(coerceToType(string, token, componentType, parser));
/*     */     }
/* 253 */     Object array = Array.newInstance(componentType, values.size());
/* 254 */     for (int i = 0; i < values.size(); i++) {
/* 255 */       Array.set(array, i, values.get(i));
/*     */     }
/* 257 */     return array;
/*     */   }
/*     */   
/*     */   private static Object coerceToType(String string, Token token, Class<?> type, ExchangeAttributeParser attributeParser) {
/* 261 */     if (type.isArray()) {
/* 262 */       Object array = Array.newInstance(type.getComponentType(), 1);
/* 263 */       Array.set(array, 0, coerceToType(string, token, type.getComponentType(), attributeParser));
/* 264 */       return array;
/*     */     } 
/*     */     
/* 267 */     if (type == String.class)
/* 268 */       return token.getToken(); 
/* 269 */     if (type.equals(Boolean.class) || type.equals(boolean.class))
/* 270 */       return Boolean.valueOf(token.getToken()); 
/* 271 */     if (type.equals(Byte.class) || type.equals(byte.class))
/* 272 */       return Byte.valueOf(token.getToken()); 
/* 273 */     if (type.equals(Character.class) || type.equals(char.class)) {
/* 274 */       if (token.getToken().length() != 1) {
/* 275 */         throw error(string, token.getPosition(), "Cannot coerce " + token.getToken() + " to a Character");
/*     */       }
/* 277 */       return Character.valueOf(token.getToken().charAt(0));
/* 278 */     }  if (type.equals(Short.class) || type.equals(short.class))
/* 279 */       return Short.valueOf(token.getToken()); 
/* 280 */     if (type.equals(Integer.class) || type.equals(int.class))
/* 281 */       return Integer.valueOf(token.getToken()); 
/* 282 */     if (type.equals(Long.class) || type.equals(long.class))
/* 283 */       return Long.valueOf(token.getToken()); 
/* 284 */     if (type.equals(Float.class) || type.equals(float.class))
/* 285 */       return Float.valueOf(token.getToken()); 
/* 286 */     if (type.equals(Double.class) || type.equals(double.class))
/* 287 */       return Double.valueOf(token.getToken()); 
/* 288 */     if (type.equals(ExchangeAttribute.class)) {
/* 289 */       return attributeParser.parse(token.getToken());
/*     */     }
/*     */     
/* 292 */     return token.getToken();
/*     */   }
/*     */   
/*     */   private static Map<String, PredicateBuilder> loadPredicateBuilders(ClassLoader classLoader) {
/* 296 */     ServiceLoader<PredicateBuilder> loader = ServiceLoader.load(PredicateBuilder.class, classLoader);
/* 297 */     Map<String, PredicateBuilder> ret = new HashMap<>();
/* 298 */     for (PredicateBuilder builder : loader) {
/* 299 */       if (ret.containsKey(builder.name())) {
/* 300 */         if (((PredicateBuilder)ret.get(builder.name())).getClass() != builder.getClass())
/* 301 */           throw UndertowMessages.MESSAGES.moreThanOnePredicateWithName(builder.name(), builder.getClass(), ((PredicateBuilder)ret.get(builder.name())).getClass()); 
/*     */         continue;
/*     */       } 
/* 304 */       ret.put(builder.name(), builder);
/*     */     } 
/*     */     
/* 307 */     return ret;
/*     */   }
/*     */   
/*     */   private static Map<String, HandlerBuilder> loadHandlerBuilders(ClassLoader classLoader) {
/* 311 */     ServiceLoader<HandlerBuilder> loader = ServiceLoader.load(HandlerBuilder.class, classLoader);
/* 312 */     Map<String, HandlerBuilder> ret = new HashMap<>();
/* 313 */     for (HandlerBuilder builder : loader) {
/* 314 */       if (ret.containsKey(builder.name())) {
/* 315 */         if (((HandlerBuilder)ret.get(builder.name())).getClass() != builder.getClass())
/* 316 */           throw UndertowMessages.MESSAGES.moreThanOneHandlerWithName(builder.name(), builder.getClass(), ((HandlerBuilder)ret.get(builder.name())).getClass()); 
/*     */         continue;
/*     */       } 
/* 319 */       ret.put(builder.name(), builder);
/*     */     } 
/*     */     
/* 322 */     return ret;
/*     */   }
/*     */   
/*     */   static Node parse(String string, Deque<Token> tokens) {
/* 326 */     return parse(string, tokens, true);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static Node parse(String string, Deque<Token> tokens, boolean topLevel) {
/* 333 */     Deque<Token> operatorStack = new ArrayDeque<>();
/*     */     
/* 335 */     Deque<Node> output = new ArrayDeque<>();
/* 336 */     List<Node> blocks = new ArrayList<>();
/*     */ 
/*     */     
/* 339 */     label53: while (!tokens.isEmpty()) {
/* 340 */       Token token = tokens.poll();
/* 341 */       if (token.getToken().equals("{")) {
/* 342 */         output.push(parse(string, tokens, false)); continue;
/* 343 */       }  if (token.getToken().equals("}")) {
/* 344 */         if (topLevel)
/* 345 */           throw error(string, token.getPosition(), "Unexpected token"); 
/*     */         break;
/*     */       } 
/* 348 */       if (token.getToken().equals("\n") || token.getToken().equals(";")) {
/* 349 */         if (token.getToken().equals(";") && tokens.peek() != null && ((Token)tokens.peek()).getToken().equals("else")) {
/*     */           continue;
/*     */         }
/*     */         
/* 353 */         handleLineEnd(string, operatorStack, output, blocks); continue;
/* 354 */       }  if (isSpecialChar(token.getToken())) {
/* 355 */         if (token.getToken().equals("(")) {
/* 356 */           operatorStack.push(token); continue;
/* 357 */         }  if (token.getToken().equals(")")) {
/*     */           while (true) {
/* 359 */             Token op = operatorStack.pop();
/* 360 */             if (op == null)
/* 361 */               throw error(string, token.getPosition(), "Unexpected end of input"); 
/* 362 */             if (op.getToken().equals("(")) {
/*     */               continue label53;
/*     */             }
/* 365 */             output.push(new OperatorNode(op));
/*     */           } 
/*     */         }
/*     */         
/* 369 */         output.push(new OperatorNode(token));
/*     */         continue;
/*     */       } 
/* 372 */       if (isOperator(token.getToken()) && !token.getToken().equals("else")) {
/* 373 */         int prec = precedence(token.getToken());
/* 374 */         Token top = operatorStack.peek();
/* 375 */         while (top != null && 
/* 376 */           !top.getToken().equals("(")) {
/*     */ 
/*     */           
/* 379 */           int exitingPrec = precedence(top.getToken());
/* 380 */           if (prec <= exitingPrec) {
/* 381 */             output.push(new OperatorNode(operatorStack.pop()));
/*     */ 
/*     */ 
/*     */             
/* 385 */             top = operatorStack.peek(); continue;
/*     */           }  break;
/* 387 */         }  operatorStack.push(token); continue;
/*     */       } 
/* 389 */       output.push(parseExpression(string, token, tokens));
/*     */     } 
/*     */ 
/*     */     
/* 393 */     handleLineEnd(string, operatorStack, output, blocks);
/* 394 */     if (blocks.size() == 1) {
/* 395 */       return blocks.get(0);
/*     */     }
/* 397 */     return new BlockNode(new Token("", 0), blocks);
/*     */   }
/*     */ 
/*     */   
/*     */   private static void handleLineEnd(String string, Deque<Token> operatorStack, Deque<Node> output, List<Node> blocks) {
/* 402 */     while (!operatorStack.isEmpty()) {
/* 403 */       Token op = operatorStack.pop();
/* 404 */       if (op.getToken().equals(")")) {
/* 405 */         throw error(string, string.length(), "Mismatched parenthesis");
/*     */       }
/* 407 */       output.push(new OperatorNode(op));
/*     */     } 
/* 409 */     if (output.isEmpty()) {
/*     */       return;
/*     */     }
/*     */     
/* 413 */     Node predicate = collapseOutput(output.pop(), output);
/* 414 */     if (!output.isEmpty()) {
/* 415 */       throw error(string, ((Node)output.getFirst()).getToken().getPosition(), "Invalid expression");
/*     */     }
/* 417 */     blocks.add(predicate);
/*     */   }
/*     */   
/*     */   private static Node collapseOutput(Node token, Deque<Node> tokens) {
/* 421 */     if (token instanceof OperatorNode) {
/* 422 */       OperatorNode node = (OperatorNode)token;
/* 423 */       if (node.token.getToken().equals("and")) {
/* 424 */         Node n1 = collapseOutput(tokens.pop(), tokens);
/* 425 */         Node n2 = collapseOutput(tokens.pop(), tokens);
/* 426 */         return new AndNode(token.getToken(), n2, n1);
/* 427 */       }  if (node.token.getToken().equals("or")) {
/* 428 */         Node n1 = collapseOutput(tokens.pop(), tokens);
/* 429 */         Node n2 = collapseOutput(tokens.pop(), tokens);
/* 430 */         return new OrNode(token.getToken(), n2, n1);
/* 431 */       }  if (node.token.getToken().equals("not")) {
/* 432 */         Node n1 = collapseOutput(tokens.pop(), tokens);
/* 433 */         return new NotNode(token.getToken(), n1);
/* 434 */       }  if (node.token.getToken().equals("->")) {
/* 435 */         Node n1 = collapseOutput(tokens.pop(), tokens);
/* 436 */         Node n2 = null;
/* 437 */         Node elseBranch = null;
/* 438 */         Node popped = tokens.pop();
/* 439 */         if (popped.getToken().getToken().equals("else")) {
/* 440 */           elseBranch = n1;
/* 441 */           n1 = collapseOutput(tokens.pop(), tokens);
/* 442 */           n2 = collapseOutput(tokens.pop(), tokens);
/*     */         } else {
/* 444 */           n2 = collapseOutput(popped, tokens);
/*     */         } 
/* 446 */         return new PredicateOperatorNode(token.getToken(), n2, n1, elseBranch);
/*     */       } 
/* 448 */       return token;
/*     */     } 
/*     */     
/* 451 */     return token;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private static Node parseExpression(String string, Token token, Deque<Token> tokens) {
/* 457 */     if (token.getToken().equals("true"))
/* 458 */       return new OperatorNode(token); 
/* 459 */     if (token.getToken().equals("false")) {
/* 460 */       return new OperatorNode(token);
/*     */     }
/* 462 */     Token next = tokens.peek();
/* 463 */     String endChar = ")";
/* 464 */     if (next != null && (next.getToken().equals("[") || next.getToken().equals("("))) {
/* 465 */       if (next.getToken().equals("[")) {
/* 466 */         endChar = "]";
/* 467 */         UndertowLogger.ROOT_LOGGER.oldStylePredicateSyntax(string);
/*     */       } 
/* 469 */       Map<String, Node> values = new HashMap<>();
/*     */       
/* 471 */       tokens.poll();
/* 472 */       next = tokens.poll();
/* 473 */       if (next == null) {
/* 474 */         throw error(string, string.length(), "Unexpected end of input");
/*     */       }
/* 476 */       if (next.getToken().equals("{")) {
/* 477 */         return handleSingleArrayValue(string, token, tokens, endChar);
/*     */       }
/* 479 */       while (!next.getToken().equals(endChar)) {
/* 480 */         Token equals = tokens.poll();
/* 481 */         if (equals == null) {
/* 482 */           throw error(string, string.length(), "Unexpected end of input");
/*     */         }
/* 484 */         if (!equals.getToken().equals("=")) {
/* 485 */           if (equals.getToken().equals(endChar) && values.isEmpty())
/*     */           {
/* 487 */             return handleSingleValue(token, next); } 
/* 488 */           if (equals.getToken().equals(",")) {
/* 489 */             tokens.push(equals);
/* 490 */             tokens.push(next);
/* 491 */             return handleSingleVarArgsValue(string, token, tokens, endChar);
/*     */           } 
/* 493 */           throw error(string, equals.getPosition(), "Unexpected token");
/*     */         } 
/* 495 */         Token value = tokens.poll();
/* 496 */         if (value == null) {
/* 497 */           throw error(string, string.length(), "Unexpected end of input");
/*     */         }
/* 499 */         if (value.getToken().equals("{")) {
/* 500 */           values.put(next.getToken(), new ArrayNode(value, readArrayType(string, tokens, "}")));
/*     */         } else {
/* 502 */           if (isOperator(value.getToken()) || isSpecialChar(value.getToken())) {
/* 503 */             throw error(string, value.getPosition(), "Unexpected token");
/*     */           }
/* 505 */           values.put(next.getToken(), new ValueNode(value));
/*     */         } 
/*     */         
/* 508 */         next = tokens.poll();
/* 509 */         if (next == null) {
/* 510 */           throw error(string, string.length(), "Unexpected end of input");
/*     */         }
/* 512 */         if (!next.getToken().equals(endChar)) {
/* 513 */           if (!next.getToken().equals(",")) {
/* 514 */             throw error(string, string.length(), "Expecting , or " + endChar);
/*     */           }
/* 516 */           next = tokens.poll();
/* 517 */           if (next == null) {
/* 518 */             throw error(string, string.length(), "Unexpected end of input");
/*     */           }
/*     */         } 
/*     */       } 
/* 522 */       return new ExpressionNode(token, values);
/*     */     } 
/*     */     
/* 525 */     if (next != null && isSpecialChar(next.getToken())) {
/* 526 */       throw error(string, next.getPosition(), "Unexpected character");
/*     */     }
/* 528 */     return new ExpressionNode(token, Collections.emptyMap());
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private static Node handleSingleArrayValue(String string, Token builder, Deque<Token> tokens, String endChar) {
/* 534 */     List<Token> array = readArrayType(string, tokens, "}");
/* 535 */     Token close = tokens.poll();
/* 536 */     if (!close.getToken().equals(endChar)) {
/* 537 */       throw error(string, close.getPosition(), "expected " + endChar);
/*     */     }
/* 539 */     return new ExpressionNode(builder, Collections.singletonMap(null, new ArrayNode(builder, array)));
/*     */   }
/*     */   
/*     */   private static Node handleSingleVarArgsValue(String string, Token expressionName, Deque<Token> tokens, String endChar) {
/* 543 */     List<Token> array = readArrayType(string, tokens, endChar);
/* 544 */     return new ExpressionNode(expressionName, Collections.singletonMap(null, new ArrayNode(expressionName, array)));
/*     */   }
/*     */   
/*     */   private static List<Token> readArrayType(String string, Deque<Token> tokens, String expectedEndToken) {
/* 548 */     List<Token> values = new ArrayList<>();
/* 549 */     Token token = tokens.poll();
/* 550 */     if (token.getToken().equals(expectedEndToken)) {
/* 551 */       return Collections.emptyList();
/*     */     }
/* 553 */     while (token != null) {
/* 554 */       Token commaOrEnd = tokens.poll();
/* 555 */       values.add(token);
/* 556 */       if (commaOrEnd.getToken().equals(expectedEndToken))
/* 557 */         return values; 
/* 558 */       if (!commaOrEnd.getToken().equals(",")) {
/* 559 */         throw error(string, commaOrEnd.getPosition(), "expected either , or " + expectedEndToken);
/*     */       }
/* 561 */       token = tokens.poll();
/*     */     } 
/* 563 */     throw error(string, string.length(), "unexpected end of input in array");
/*     */   }
/*     */ 
/*     */   
/*     */   private static Node handleSingleValue(Token token, Token next) {
/* 568 */     return new ExpressionNode(token, Collections.singletonMap(null, new ValueNode(next)));
/*     */   }
/*     */   
/*     */   private static int precedence(String operator) {
/* 572 */     if (operator.equals("not"))
/* 573 */       return 3; 
/* 574 */     if (operator.equals("and"))
/* 575 */       return 2; 
/* 576 */     if (operator.equals("or"))
/* 577 */       return 1; 
/* 578 */     if (operator.equals("->")) {
/* 579 */       return -1000;
/*     */     }
/* 581 */     throw new IllegalStateException();
/*     */   }
/*     */ 
/*     */   
/*     */   private static boolean isOperator(String op) {
/* 586 */     return (op.equals("and") || op.equals("or") || op.equals("not") || op.equals("->"));
/*     */   }
/*     */   
/*     */   private static boolean isSpecialChar(String token) {
/* 590 */     if (token.length() == 1) {
/* 591 */       char c = token.charAt(0);
/* 592 */       switch (c) {
/*     */         case '(':
/*     */         case ')':
/*     */         case ',':
/*     */         case '=':
/*     */         case '[':
/*     */         case ']':
/* 599 */           return true;
/*     */       } 
/* 601 */       return false;
/*     */     } 
/*     */     
/* 604 */     return false;
/*     */   }
/*     */   
/*     */   public static Deque<Token> tokenize(String string) {
/* 608 */     char currentStringDelim = Character.MIN_VALUE;
/* 609 */     boolean inVariable = false;
/*     */     
/* 611 */     int pos = 0;
/* 612 */     StringBuilder current = new StringBuilder();
/* 613 */     Deque<Token> ret = new ArrayDeque<>();
/* 614 */     while (pos < string.length()) {
/* 615 */       char c = string.charAt(pos);
/* 616 */       if (currentStringDelim != '\000') {
/* 617 */         if (c == currentStringDelim && current.charAt(current.length() - 1) != '\\') {
/* 618 */           ret.add(new Token(current.toString(), pos));
/* 619 */           current.setLength(0);
/* 620 */           currentStringDelim = Character.MIN_VALUE;
/* 621 */         } else if (c == '\n' || c == '\r') {
/* 622 */           ret.add(new Token(current.toString(), pos));
/* 623 */           current.setLength(0);
/* 624 */           currentStringDelim = Character.MIN_VALUE;
/* 625 */           ret.add(new Token("\n", pos));
/*     */         } else {
/* 627 */           current.append(c);
/*     */         } 
/*     */       } else {
/* 630 */         switch (c) {
/*     */           case '\t':
/*     */           case ' ':
/* 633 */             if (current.length() != 0) {
/* 634 */               ret.add(new Token(current.toString(), pos));
/* 635 */               current.setLength(0);
/*     */             } 
/*     */             break;
/*     */           
/*     */           case '\n':
/*     */           case '\r':
/* 641 */             if (current.length() != 0) {
/* 642 */               ret.add(new Token(current.toString(), pos));
/* 643 */               current.setLength(0);
/*     */             } 
/* 645 */             ret.add(new Token("\n", pos));
/*     */             break;
/*     */           
/*     */           case '(':
/*     */           case ')':
/*     */           case ',':
/*     */           case ';':
/*     */           case '=':
/*     */           case '[':
/*     */           case ']':
/*     */           case '{':
/*     */           case '}':
/* 657 */             if (inVariable) {
/* 658 */               current.append(c);
/* 659 */               if (c == '}')
/* 660 */                 inVariable = false; 
/*     */               break;
/*     */             } 
/* 663 */             if (current.length() != 0) {
/* 664 */               ret.add(new Token(current.toString(), pos));
/* 665 */               current.setLength(0);
/*     */             } 
/* 667 */             ret.add(new Token("" + c, pos));
/*     */             break;
/*     */ 
/*     */           
/*     */           case '"':
/*     */           case '\'':
/* 673 */             if (current.length() != 0) {
/* 674 */               throw error(string, pos, "Unexpected token");
/*     */             }
/* 676 */             currentStringDelim = c;
/*     */             break;
/*     */           
/*     */           case '$':
/*     */           case '%':
/* 681 */             current.append(c);
/* 682 */             if (string.charAt(pos + 1) == '{') {
/* 683 */               inVariable = true;
/*     */             }
/*     */             break;
/*     */           
/*     */           case '-':
/* 688 */             if (inVariable) {
/* 689 */               current.append(c); break;
/*     */             } 
/* 691 */             if (pos != string.length() && string.charAt(pos + 1) == '>') {
/* 692 */               pos++;
/* 693 */               if (current.length() != 0) {
/* 694 */                 ret.add(new Token(current.toString(), pos));
/* 695 */                 current.setLength(0);
/*     */               } 
/* 697 */               ret.add(new Token("->", pos)); break;
/*     */             } 
/* 699 */             current.append(c);
/*     */             break;
/*     */ 
/*     */           
/*     */           default:
/* 704 */             current.append(c); break;
/*     */         } 
/*     */       } 
/* 707 */       pos++;
/*     */     } 
/* 709 */     if (current.length() > 0) {
/* 710 */       ret.add(new Token(current.toString(), string.length()));
/*     */     }
/* 712 */     return ret;
/*     */   }
/*     */   
/*     */   private static IllegalStateException error(String string, int pos, String reason) {
/* 716 */     StringBuilder b = new StringBuilder();
/* 717 */     int linePos = 0; int i;
/* 718 */     for (i = 0; i < string.length(); i++) {
/* 719 */       if (string.charAt(i) == '\n') {
/* 720 */         if (i >= pos) {
/*     */           break;
/*     */         }
/*     */         
/* 724 */         linePos = 0;
/*     */       }
/* 726 */       else if (i < pos) {
/* 727 */         linePos++;
/*     */       } 
/* 729 */       b.append(string.charAt(i));
/*     */     } 
/* 731 */     b.append('\n');
/* 732 */     for (i = 0; i < linePos; i++) {
/* 733 */       b.append(' ');
/*     */     }
/* 735 */     b.append('^');
/* 736 */     throw UndertowMessages.MESSAGES.errorParsingPredicateString(reason, b.toString());
/*     */   }
/*     */ 
/*     */   
/*     */   public static interface Node
/*     */   {
/*     */     PredicatedHandlersParser.Token getToken();
/*     */   }
/*     */ 
/*     */   
/*     */   static class ExpressionNode
/*     */     implements Node
/*     */   {
/*     */     private final PredicatedHandlersParser.Token token;
/*     */     
/*     */     private final Map<String, PredicatedHandlersParser.Node> values;
/*     */ 
/*     */     
/*     */     private ExpressionNode(PredicatedHandlersParser.Token token, Map<String, PredicatedHandlersParser.Node> values) {
/* 755 */       this.token = token;
/* 756 */       this.values = values;
/*     */     }
/*     */     
/*     */     public PredicatedHandlersParser.Token getToken() {
/* 760 */       return this.token;
/*     */     }
/*     */     
/*     */     public Map<String, PredicatedHandlersParser.Node> getValues() {
/* 764 */       return this.values;
/*     */     }
/*     */   }
/*     */   
/*     */   static class ArrayNode implements Node {
/*     */     private final PredicatedHandlersParser.Token start;
/*     */     private final List<PredicatedHandlersParser.Token> values;
/*     */     
/*     */     private ArrayNode(PredicatedHandlersParser.Token start, List<PredicatedHandlersParser.Token> tokens) {
/* 773 */       this.start = start;
/* 774 */       this.values = tokens;
/*     */     }
/*     */     
/*     */     public List<PredicatedHandlersParser.Token> getValues() {
/* 778 */       return this.values;
/*     */     }
/*     */ 
/*     */     
/*     */     public PredicatedHandlersParser.Token getToken() {
/* 783 */       return this.start;
/*     */     }
/*     */   }
/*     */   
/*     */   static class ValueNode implements Node {
/*     */     private final PredicatedHandlersParser.Token value;
/*     */     
/*     */     private ValueNode(PredicatedHandlersParser.Token value) {
/* 791 */       this.value = value;
/*     */     }
/*     */     
/*     */     public PredicatedHandlersParser.Token getValue() {
/* 795 */       return this.value;
/*     */     }
/*     */ 
/*     */     
/*     */     public String toString() {
/* 800 */       return this.value.getToken();
/*     */     }
/*     */ 
/*     */     
/*     */     public PredicatedHandlersParser.Token getToken() {
/* 805 */       return this.value;
/*     */     }
/*     */   }
/*     */   
/*     */   static class OperatorNode
/*     */     implements Node {
/*     */     private final PredicatedHandlersParser.Token token;
/*     */     
/*     */     private OperatorNode(PredicatedHandlersParser.Token token) {
/* 814 */       this.token = token;
/*     */     }
/*     */     
/*     */     public PredicatedHandlersParser.Token getToken() {
/* 818 */       return this.token;
/*     */     }
/*     */   }
/*     */   
/*     */   static class AndNode
/*     */     implements Node {
/*     */     private final PredicatedHandlersParser.Token token;
/*     */     private final PredicatedHandlersParser.Node left;
/*     */     private final PredicatedHandlersParser.Node right;
/*     */     
/*     */     AndNode(PredicatedHandlersParser.Token token, PredicatedHandlersParser.Node left, PredicatedHandlersParser.Node right) {
/* 829 */       this.token = token;
/* 830 */       this.left = left;
/* 831 */       this.right = right;
/*     */     }
/*     */     
/*     */     public PredicatedHandlersParser.Node getLeft() {
/* 835 */       return this.left;
/*     */     }
/*     */     
/*     */     public PredicatedHandlersParser.Node getRight() {
/* 839 */       return this.right;
/*     */     }
/*     */     
/*     */     public PredicatedHandlersParser.Token getToken() {
/* 843 */       return this.token;
/*     */     }
/*     */   }
/*     */   
/*     */   static class OrNode implements Node {
/*     */     private final PredicatedHandlersParser.Token token;
/*     */     private final PredicatedHandlersParser.Node left;
/*     */     private final PredicatedHandlersParser.Node right;
/*     */     
/*     */     OrNode(PredicatedHandlersParser.Token token, PredicatedHandlersParser.Node left, PredicatedHandlersParser.Node right) {
/* 853 */       this.token = token;
/* 854 */       this.left = left;
/* 855 */       this.right = right;
/*     */     }
/*     */     
/*     */     public PredicatedHandlersParser.Node getLeft() {
/* 859 */       return this.left;
/*     */     }
/*     */     
/*     */     public PredicatedHandlersParser.Node getRight() {
/* 863 */       return this.right;
/*     */     }
/*     */     
/*     */     public PredicatedHandlersParser.Token getToken() {
/* 867 */       return this.token;
/*     */     }
/*     */   }
/*     */   
/*     */   static class PredicateOperatorNode
/*     */     implements Node {
/*     */     private final PredicatedHandlersParser.Token token;
/*     */     private final PredicatedHandlersParser.Node left;
/*     */     private final PredicatedHandlersParser.Node right;
/*     */     private final PredicatedHandlersParser.Node elseBranch;
/*     */     
/*     */     PredicateOperatorNode(PredicatedHandlersParser.Token token, PredicatedHandlersParser.Node left, PredicatedHandlersParser.Node right, PredicatedHandlersParser.Node elseBranch) {
/* 879 */       this.token = token;
/* 880 */       this.left = left;
/* 881 */       this.right = right;
/* 882 */       this.elseBranch = elseBranch;
/*     */     }
/*     */     
/*     */     public PredicatedHandlersParser.Node getLeft() {
/* 886 */       return this.left;
/*     */     }
/*     */     
/*     */     public PredicatedHandlersParser.Node getRight() {
/* 890 */       return this.right;
/*     */     }
/*     */     
/*     */     public PredicatedHandlersParser.Node getElseBranch() {
/* 894 */       return this.elseBranch;
/*     */     }
/*     */ 
/*     */     
/*     */     public PredicatedHandlersParser.Token getToken() {
/* 899 */       return this.token;
/*     */     }
/*     */   }
/*     */   
/*     */   static class NotNode
/*     */     implements Node {
/*     */     private final PredicatedHandlersParser.Token token;
/*     */     private final PredicatedHandlersParser.Node node;
/*     */     
/*     */     NotNode(PredicatedHandlersParser.Token token, PredicatedHandlersParser.Node node) {
/* 909 */       this.token = token;
/* 910 */       this.node = node;
/*     */     }
/*     */     
/*     */     public PredicatedHandlersParser.Node getNode() {
/* 914 */       return this.node;
/*     */     }
/*     */     
/*     */     public PredicatedHandlersParser.Token getToken() {
/* 918 */       return this.token;
/*     */     }
/*     */   }
/*     */   
/*     */   static class BlockNode implements Node {
/*     */     private final PredicatedHandlersParser.Token token;
/*     */     private final List<PredicatedHandlersParser.Node> block;
/*     */     
/*     */     BlockNode(PredicatedHandlersParser.Token token, List<PredicatedHandlersParser.Node> block) {
/* 927 */       this.token = token;
/* 928 */       this.block = block;
/*     */     }
/*     */     
/*     */     public List<PredicatedHandlersParser.Node> getBlock() {
/* 932 */       return this.block;
/*     */     }
/*     */ 
/*     */     
/*     */     public PredicatedHandlersParser.Token getToken() {
/* 937 */       return this.token;
/*     */     }
/*     */   }
/*     */   
/*     */   static final class Token
/*     */   {
/*     */     private final String token;
/*     */     private final int position;
/*     */     
/*     */     Token(String token, int position) {
/* 947 */       this.token = token;
/* 948 */       this.position = position;
/*     */     }
/*     */     
/*     */     public String getToken() {
/* 952 */       return this.token;
/*     */     }
/*     */     
/*     */     public int getPosition() {
/* 956 */       return this.position;
/*     */     }
/*     */ 
/*     */     
/*     */     public String toString() {
/* 961 */       return this.token + " <" + this.position + ">";
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\server\handlers\builder\PredicatedHandlersParser.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */