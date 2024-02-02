/*     */ package org.wildfly.common.expression;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.EnumSet;
/*     */ import java.util.HashSet;
/*     */ import java.util.List;
/*     */ import java.util.Objects;
/*     */ import java.util.Set;
/*     */ import java.util.function.BiConsumer;
/*     */ import org.wildfly.common.Assert;
/*     */ import org.wildfly.common._private.CommonMessages;
/*     */ import org.wildfly.common.function.ExceptionBiConsumer;
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
/*     */ public final class Expression
/*     */ {
/*     */   private final Node content;
/*     */   private final Set<String> referencedStrings;
/*     */   
/*     */   Expression(Node content) {
/*  44 */     this.content = content;
/*  45 */     HashSet<String> strings = new HashSet<>();
/*  46 */     content.catalog(strings);
/*  47 */     this.referencedStrings = strings.isEmpty() ? Collections.<String>emptySet() : ((strings.size() == 1) ? Collections.<String>singleton(strings.iterator().next()) : Collections.<String>unmodifiableSet(strings));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Set<String> getReferencedStrings() {
/*  58 */     return this.referencedStrings;
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
/*     */   public <E extends Exception> String evaluateException(ExceptionBiConsumer<ResolveContext<E>, StringBuilder, E> expandFunction) throws E {
/*  73 */     Assert.checkNotNullParam("expandFunction", expandFunction);
/*  74 */     StringBuilder b = new StringBuilder();
/*  75 */     this.content.emit(new ResolveContext<>(expandFunction, b), expandFunction);
/*  76 */     return b.toString();
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
/*     */   public String evaluate(BiConsumer<ResolveContext<RuntimeException>, StringBuilder> expandFunction) {
/*  89 */     Objects.requireNonNull(expandFunction); return evaluateException(expandFunction::accept);
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
/*     */   public String evaluateWithPropertiesAndEnvironment(boolean failOnNoDefault) {
/* 102 */     return evaluate((c, b) -> {
/*     */           String key = c.getKey();
/*     */           if (key.startsWith("env.")) {
/*     */             String env = key.substring(4);
/*     */             String val = System.getenv(env);
/*     */             if (val == null) {
/*     */               if (failOnNoDefault && !c.hasDefault()) {
/*     */                 throw CommonMessages.msg.unresolvedEnvironmentProperty(env);
/*     */               }
/*     */               c.expandDefault();
/*     */             } else {
/*     */               b.append(val);
/*     */             } 
/*     */           } else {
/*     */             String val = System.getProperty(key);
/*     */             if (val == null) {
/*     */               if (failOnNoDefault && !c.hasDefault()) {
/*     */                 throw CommonMessages.msg.unresolvedSystemProperty(key);
/*     */               }
/*     */               c.expandDefault();
/*     */             } else {
/*     */               b.append(val);
/*     */             } 
/*     */           } 
/*     */         });
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
/*     */   public String evaluateWithProperties(boolean failOnNoDefault) {
/* 138 */     return evaluate((c, b) -> {
/*     */           String key = c.getKey();
/*     */           String val = System.getProperty(key);
/*     */           if (val == null) {
/*     */             if (failOnNoDefault && !c.hasDefault()) {
/*     */               throw CommonMessages.msg.unresolvedSystemProperty(key);
/*     */             }
/*     */             c.expandDefault();
/*     */           } else {
/*     */             b.append(val);
/*     */           } 
/*     */         });
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
/*     */   public String evaluateWithEnvironment(boolean failOnNoDefault) {
/* 161 */     return evaluate((c, b) -> {
/*     */           String key = c.getKey();
/*     */           String val = System.getenv(key);
/*     */           if (val == null) {
/*     */             if (failOnNoDefault && !c.hasDefault()) {
/*     */               throw CommonMessages.msg.unresolvedEnvironmentProperty(key);
/*     */             }
/*     */             c.expandDefault();
/*     */           } else {
/*     */             b.append(val);
/*     */           } 
/*     */         });
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Expression compile(String string, Flag... flags) {
/* 183 */     return compile(string, (flags == null || flags.length == 0) ? NO_FLAGS : EnumSet.<Flag>of(flags[0], flags));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Expression compile(String string, EnumSet<Flag> flags) {
/*     */     Itr itr;
/* 194 */     Assert.checkNotNullParam("string", string);
/* 195 */     Assert.checkNotNullParam("flags", flags);
/*     */ 
/*     */     
/* 198 */     if (flags.contains(Flag.NO_TRIM)) {
/* 199 */       itr = new Itr(string);
/*     */     } else {
/* 201 */       itr = new Itr(string.trim());
/*     */     } 
/* 203 */     Node content = parseString(itr, true, false, false, flags);
/* 204 */     return (content == Node.NULL) ? EMPTY : new Expression(content);
/*     */   }
/*     */   
/* 207 */   private static final Expression EMPTY = new Expression(Node.NULL);
/*     */   
/*     */   static final class Itr {
/*     */     private final String str;
/*     */     private int idx;
/*     */     
/*     */     Itr(String str) {
/* 214 */       this.str = str;
/*     */     }
/*     */     
/*     */     boolean hasNext() {
/* 218 */       return (this.idx < this.str.length());
/*     */     }
/*     */     
/*     */     int next() {
/* 222 */       int idx = this.idx;
/*     */       try {
/* 224 */         return this.str.codePointAt(idx);
/*     */       } finally {
/* 226 */         this.idx = this.str.offsetByCodePoints(idx, 1);
/*     */       } 
/*     */     }
/*     */     
/*     */     int prev() {
/* 231 */       int idx = this.idx;
/*     */       try {
/* 233 */         return this.str.codePointBefore(idx);
/*     */       } finally {
/* 235 */         this.idx = this.str.offsetByCodePoints(idx, -1);
/*     */       } 
/*     */     }
/*     */     
/*     */     int getNextIdx() {
/* 240 */       return this.idx;
/*     */     }
/*     */     
/*     */     int getPrevIdx() {
/* 244 */       return this.str.offsetByCodePoints(this.idx, -1);
/*     */     }
/*     */     
/*     */     String getStr() {
/* 248 */       return this.str;
/*     */     }
/*     */     
/*     */     int peekNext() {
/* 252 */       return this.str.codePointAt(this.idx);
/*     */     }
/*     */     
/*     */     int peekPrev() {
/* 256 */       return this.str.codePointBefore(this.idx);
/*     */     }
/*     */     
/*     */     void rewind(int newNext) {
/* 260 */       this.idx = newNext;
/*     */     }
/*     */   }
/*     */   
/*     */   private static Node parseString(Itr itr, boolean allowExpr, boolean endOnBrace, boolean endOnColon, EnumSet<Flag> flags) {
/* 265 */     int ignoreBraceLevel = 0;
/* 266 */     List<Node> list = new ArrayList<>();
/* 267 */     int start = itr.getNextIdx();
/* 268 */     while (itr.hasNext()) {
/*     */       boolean general; Node keyNode;
/* 270 */       int idx = itr.getNextIdx();
/* 271 */       int ch = itr.next();
/* 272 */       switch (ch) {
/*     */         case 36:
/* 274 */           if (!allowExpr) {
/*     */             continue;
/*     */           }
/*     */ 
/*     */ 
/*     */           
/* 280 */           if (!itr.hasNext()) {
/* 281 */             if (!flags.contains(Flag.LENIENT_SYNTAX))
/*     */             {
/* 283 */               throw invalidExpressionSyntax(itr.getStr(), idx);
/*     */             }
/*     */             
/* 286 */             list.add(new LiteralNode(itr.getStr(), start, itr.getNextIdx()));
/* 287 */             start = itr.getNextIdx();
/*     */             
/*     */             continue;
/*     */           } 
/* 291 */           if (idx > start)
/*     */           {
/* 293 */             list.add(new LiteralNode(itr.getStr(), start, idx));
/*     */           }
/*     */           
/* 296 */           idx = itr.getNextIdx();
/* 297 */           ch = itr.next();
/* 298 */           switch (ch) {
/*     */             
/*     */             case 123:
/* 301 */               general = (flags.contains(Flag.GENERAL_EXPANSION) && itr.hasNext() && itr.peekNext() == 123);
/*     */               
/* 303 */               if (general) itr.next();
/*     */               
/* 305 */               start = itr.getNextIdx();
/*     */               
/* 307 */               keyNode = parseString(itr, !flags.contains(Flag.NO_RECURSE_KEY), true, true, flags);
/* 308 */               if (!itr.hasNext()) {
/* 309 */                 if (!flags.contains(Flag.LENIENT_SYNTAX))
/*     */                 {
/* 311 */                   throw invalidExpressionSyntax(itr.getStr(), itr.getNextIdx());
/*     */                 }
/*     */ 
/*     */                 
/* 315 */                 list.add(new ExpressionNode(general, keyNode, Node.NULL));
/* 316 */                 start = itr.getNextIdx(); continue;
/*     */               } 
/* 318 */               if (itr.peekNext() == 58) {
/* 319 */                 if (flags.contains(Flag.DOUBLE_COLON) && itr.hasNext() && itr.peekNext() == 58) {
/*     */ 
/*     */ 
/*     */                   
/* 323 */                   itr.rewind(start);
/* 324 */                   keyNode = parseString(itr, !flags.contains(Flag.NO_RECURSE_KEY), true, false, flags);
/* 325 */                   list.add(new ExpressionNode(general, keyNode, Node.NULL));
/*     */                 } else {
/*     */                   
/* 328 */                   itr.next();
/* 329 */                   Node defaultValueNode = parseString(itr, !flags.contains(Flag.NO_RECURSE_DEFAULT), true, false, flags);
/* 330 */                   list.add(new ExpressionNode(general, keyNode, defaultValueNode));
/*     */                 } 
/*     */                 
/* 333 */                 if (!itr.hasNext()) {
/* 334 */                   if (!flags.contains(Flag.LENIENT_SYNTAX))
/*     */                   {
/* 336 */                     throw invalidExpressionSyntax(itr.getStr(), itr.getNextIdx());
/*     */                   }
/*     */ 
/*     */                   
/* 340 */                   start = itr.getNextIdx();
/*     */                   
/*     */                   continue;
/*     */                 } 
/* 344 */                 assert itr.peekNext() == 125;
/* 345 */                 itr.next();
/* 346 */                 if (general) {
/* 347 */                   if (!itr.hasNext()) {
/* 348 */                     if (!flags.contains(Flag.LENIENT_SYNTAX))
/*     */                     {
/* 350 */                       throw invalidExpressionSyntax(itr.getStr(), itr.getNextIdx());
/*     */                     }
/*     */ 
/*     */                     
/* 354 */                     start = itr.getNextIdx();
/*     */                     continue;
/*     */                   } 
/* 357 */                   if (itr.peekNext() == 125) {
/* 358 */                     itr.next();
/*     */                     
/* 360 */                     start = itr.getNextIdx();
/*     */                     continue;
/*     */                   } 
/* 363 */                   if (!flags.contains(Flag.LENIENT_SYNTAX))
/*     */                   {
/* 365 */                     throw invalidExpressionSyntax(itr.getStr(), itr.getNextIdx());
/*     */                   }
/*     */                   
/* 368 */                   start = itr.getNextIdx();
/*     */                   
/*     */                   continue;
/*     */                 } 
/*     */                 
/* 373 */                 start = itr.getNextIdx();
/*     */ 
/*     */                 
/*     */                 continue;
/*     */               } 
/*     */ 
/*     */               
/* 380 */               assert itr.peekNext() == 125;
/* 381 */               itr.next();
/* 382 */               list.add(new ExpressionNode(general, keyNode, Node.NULL));
/* 383 */               if (general) {
/* 384 */                 if (!itr.hasNext()) {
/* 385 */                   if (!flags.contains(Flag.LENIENT_SYNTAX))
/*     */                   {
/* 387 */                     throw invalidExpressionSyntax(itr.getStr(), itr.getNextIdx());
/*     */                   }
/*     */ 
/*     */                   
/* 391 */                   start = itr.getNextIdx();
/*     */                   continue;
/*     */                 } 
/* 394 */                 if (itr.peekNext() == 125) {
/* 395 */                   itr.next();
/*     */                   
/* 397 */                   start = itr.getNextIdx();
/*     */                   continue;
/*     */                 } 
/* 400 */                 if (!flags.contains(Flag.LENIENT_SYNTAX))
/*     */                 {
/* 402 */                   throw invalidExpressionSyntax(itr.getStr(), itr.getNextIdx());
/*     */                 }
/*     */                 
/* 405 */                 start = itr.getNextIdx();
/*     */                 
/*     */                 continue;
/*     */               } 
/*     */               
/* 410 */               start = itr.getNextIdx();
/*     */               continue;
/*     */ 
/*     */ 
/*     */ 
/*     */             
/*     */             case 36:
/* 417 */               if (flags.contains(Flag.MINI_EXPRS)) {
/*     */                 
/* 419 */                 list.add(new ExpressionNode(false, LiteralNode.DOLLAR, Node.NULL));
/*     */               }
/*     */               else {
/*     */                 
/* 423 */                 list.add(LiteralNode.DOLLAR);
/*     */               } 
/* 425 */               start = itr.getNextIdx();
/*     */               continue;
/*     */ 
/*     */             
/*     */             case 125:
/* 430 */               if (flags.contains(Flag.MINI_EXPRS)) {
/*     */                 
/* 432 */                 list.add(new ExpressionNode(false, LiteralNode.CLOSE_BRACE, Node.NULL));
/* 433 */                 start = itr.getNextIdx(); continue;
/*     */               } 
/* 435 */               if (endOnBrace) {
/* 436 */                 if (flags.contains(Flag.LENIENT_SYNTAX)) {
/*     */ 
/*     */                   
/* 439 */                   list.add(LiteralNode.DOLLAR);
/* 440 */                   itr.prev();
/* 441 */                   return Node.fromList(list);
/*     */                 } 
/*     */                 
/* 444 */                 throw invalidExpressionSyntax(itr.getStr(), idx);
/*     */               } 
/*     */               
/* 447 */               if (flags.contains(Flag.LENIENT_SYNTAX)) {
/*     */ 
/*     */                 
/* 450 */                 list.add(LiteralNode.DOLLAR);
/* 451 */                 list.add(LiteralNode.CLOSE_BRACE);
/* 452 */                 start = itr.getNextIdx();
/*     */                 
/*     */                 continue;
/*     */               } 
/* 456 */               throw invalidExpressionSyntax(itr.getStr(), idx);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */             
/*     */             case 58:
/* 463 */               if (flags.contains(Flag.MINI_EXPRS)) {
/*     */ 
/*     */                 
/* 466 */                 list.add(new ExpressionNode(false, LiteralNode.COLON, Node.NULL));
/* 467 */                 start = itr.getNextIdx(); continue;
/*     */               } 
/* 469 */               if (endOnColon) {
/* 470 */                 if (flags.contains(Flag.LENIENT_SYNTAX)) {
/*     */ 
/*     */                   
/* 473 */                   itr.prev();
/* 474 */                   list.add(LiteralNode.DOLLAR);
/* 475 */                   return Node.fromList(list);
/*     */                 } 
/*     */                 
/* 478 */                 throw invalidExpressionSyntax(itr.getStr(), idx);
/*     */               } 
/*     */               
/* 481 */               if (flags.contains(Flag.LENIENT_SYNTAX)) {
/*     */ 
/*     */                 
/* 484 */                 list.add(LiteralNode.DOLLAR);
/* 485 */                 list.add(LiteralNode.COLON);
/* 486 */                 start = itr.getNextIdx();
/*     */                 
/*     */                 continue;
/*     */               } 
/* 490 */               throw invalidExpressionSyntax(itr.getStr(), idx);
/*     */           } 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */           
/* 497 */           if (flags.contains(Flag.MINI_EXPRS)) {
/*     */             
/* 499 */             list.add(new ExpressionNode(false, new LiteralNode(itr.getStr(), idx, itr.getNextIdx()), Node.NULL));
/* 500 */             start = itr.getNextIdx(); continue;
/*     */           } 
/* 502 */           if (flags.contains(Flag.LENIENT_SYNTAX)) {
/*     */ 
/*     */             
/* 505 */             start = itr.getPrevIdx() - 1;
/*     */             
/*     */             continue;
/*     */           } 
/* 509 */           throw invalidExpressionSyntax(itr.getStr(), idx);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */         
/*     */         case 58:
/* 517 */           if (endOnColon) {
/*     */             
/* 519 */             itr.prev();
/* 520 */             if (idx > start) {
/* 521 */               list.add(new LiteralNode(itr.getStr(), start, idx));
/*     */             }
/* 523 */             return Node.fromList(list);
/*     */           } 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */         
/*     */         case 123:
/* 532 */           if (!flags.contains(Flag.NO_SMART_BRACES))
/*     */           {
/* 534 */             ignoreBraceLevel++;
/*     */           }
/*     */ 
/*     */ 
/*     */         
/*     */         case 125:
/* 540 */           if (!flags.contains(Flag.NO_SMART_BRACES) && ignoreBraceLevel > 0) {
/*     */             
/* 542 */             ignoreBraceLevel--; continue;
/*     */           } 
/* 544 */           if (endOnBrace) {
/*     */             
/* 546 */             itr.prev();
/*     */             
/* 548 */             if (idx >= start) {
/* 549 */               list.add(new LiteralNode(itr.getStr(), start, idx));
/*     */             }
/* 551 */             return Node.fromList(list);
/*     */           } 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */         
/*     */         case 92:
/* 560 */           if (flags.contains(Flag.ESCAPES)) {
/* 561 */             LiteralNode node; if (idx > start) {
/* 562 */               list.add(new LiteralNode(itr.getStr(), start, idx));
/* 563 */               start = idx;
/*     */             } 
/* 565 */             if (!itr.hasNext()) {
/* 566 */               if (flags.contains(Flag.LENIENT_SYNTAX)) {
/*     */                 continue;
/*     */               }
/*     */ 
/*     */ 
/*     */               
/* 572 */               throw invalidExpressionSyntax(itr.getStr(), idx);
/*     */             } 
/*     */             
/* 575 */             ch = itr.next();
/*     */             
/* 577 */             switch (ch) {
/*     */               
/*     */               case 110:
/* 580 */                 node = LiteralNode.NEWLINE;
/*     */                 break;
/*     */ 
/*     */               
/*     */               case 114:
/* 585 */                 node = LiteralNode.CARRIAGE_RETURN;
/*     */                 break;
/*     */ 
/*     */               
/*     */               case 116:
/* 590 */                 node = LiteralNode.TAB;
/*     */                 break;
/*     */ 
/*     */               
/*     */               case 98:
/* 595 */                 node = LiteralNode.BACKSPACE;
/*     */                 break;
/*     */ 
/*     */               
/*     */               case 102:
/* 600 */                 node = LiteralNode.FORM_FEED;
/*     */                 break;
/*     */ 
/*     */               
/*     */               case 92:
/* 605 */                 node = LiteralNode.BACKSLASH;
/*     */                 break;
/*     */               
/*     */               default:
/* 609 */                 if (flags.contains(Flag.LENIENT_SYNTAX)) {
/*     */ 
/*     */                   
/* 612 */                   start = itr.getPrevIdx();
/*     */                   
/*     */                   continue;
/*     */                 } 
/* 616 */                 throw invalidExpressionSyntax(itr.getStr(), idx);
/*     */             } 
/*     */             
/* 619 */             list.add(node);
/* 620 */             start = itr.getNextIdx();
/*     */           } 
/*     */       } 
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
/*     */     } 
/* 637 */     int length = itr.getStr().length();
/* 638 */     if (length > start)
/*     */     {
/* 640 */       list.add(new LiteralNode(itr.getStr(), start, length));
/*     */     }
/* 642 */     return Node.fromList(list);
/*     */   }
/*     */   
/*     */   private static IllegalArgumentException invalidExpressionSyntax(String string, int index) {
/* 646 */     String msg = CommonMessages.msg.invalidExpressionSyntax(index);
/* 647 */     StringBuilder b = new StringBuilder(msg.length() + string.length() + string.length() + 5);
/* 648 */     b.append(msg);
/* 649 */     b.append('\n').append('\t').append(string);
/* 650 */     b.append('\n').append('\t'); int i;
/* 651 */     for (i = 0; i < index; i = string.offsetByCodePoints(i, 1)) {
/* 652 */       int cp = string.codePointAt(i);
/* 653 */       if (Character.isWhitespace(cp)) {
/* 654 */         b.append(cp);
/* 655 */       } else if (Character.isValidCodePoint(cp) && !Character.isISOControl(cp)) {
/* 656 */         b.append(' ');
/*     */       } 
/*     */     } 
/* 659 */     b.append('^');
/* 660 */     return new IllegalArgumentException(b.toString());
/*     */   }
/*     */   
/* 663 */   private static final EnumSet<Flag> NO_FLAGS = EnumSet.noneOf(Flag.class);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public enum Flag
/*     */   {
/* 672 */     NO_TRIM,
/*     */ 
/*     */ 
/*     */     
/* 676 */     LENIENT_SYNTAX,
/*     */ 
/*     */ 
/*     */     
/* 680 */     MINI_EXPRS,
/*     */ 
/*     */ 
/*     */     
/* 684 */     NO_RECURSE_KEY,
/*     */ 
/*     */ 
/*     */     
/* 688 */     NO_RECURSE_DEFAULT,
/*     */ 
/*     */ 
/*     */     
/* 692 */     NO_SMART_BRACES,
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 697 */     GENERAL_EXPANSION,
/*     */ 
/*     */ 
/*     */     
/* 701 */     ESCAPES,
/*     */ 
/*     */ 
/*     */     
/* 705 */     DOUBLE_COLON;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\wildfly\common\expression\Expression.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */