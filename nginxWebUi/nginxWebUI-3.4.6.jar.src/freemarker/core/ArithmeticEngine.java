/*     */ package freemarker.core;
/*     */ 
/*     */ import freemarker.template.TemplateException;
/*     */ import freemarker.template.utility.NumberUtil;
/*     */ import freemarker.template.utility.OptimizerUtil;
/*     */ import freemarker.template.utility.StringUtil;
/*     */ import java.math.BigDecimal;
/*     */ import java.math.BigInteger;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
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
/*     */ public abstract class ArithmeticEngine
/*     */ {
/*  44 */   public static final BigDecimalEngine BIGDECIMAL_ENGINE = new BigDecimalEngine();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  50 */   public static final ConservativeEngine CONSERVATIVE_ENGINE = new ConservativeEngine();
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
/*  68 */   protected int minScale = 12;
/*  69 */   protected int maxScale = 12;
/*  70 */   protected int roundingPolicy = 4;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setMinScale(int minScale) {
/*  77 */     if (minScale < 0) {
/*  78 */       throw new IllegalArgumentException("minScale < 0");
/*     */     }
/*  80 */     this.minScale = minScale;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setMaxScale(int maxScale) {
/*  88 */     if (maxScale < this.minScale) {
/*  89 */       throw new IllegalArgumentException("maxScale < minScale");
/*     */     }
/*  91 */     this.maxScale = maxScale;
/*     */   }
/*     */   
/*     */   public void setRoundingPolicy(int roundingPolicy) {
/*  95 */     if (roundingPolicy != 2 && roundingPolicy != 1 && roundingPolicy != 3 && roundingPolicy != 5 && roundingPolicy != 6 && roundingPolicy != 4 && roundingPolicy != 7 && roundingPolicy != 0)
/*     */     {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 103 */       throw new IllegalArgumentException("invalid rounding policy");
/*     */     }
/*     */     
/* 106 */     this.roundingPolicy = roundingPolicy;
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
/*     */   public static class BigDecimalEngine
/*     */     extends ArithmeticEngine
/*     */   {
/*     */     public int compareNumbers(Number first, Number second) {
/* 121 */       int firstSignum = NumberUtil.getSignum(first);
/* 122 */       int secondSignum = NumberUtil.getSignum(second);
/* 123 */       if (firstSignum != secondSignum)
/* 124 */         return (firstSignum < secondSignum) ? -1 : ((firstSignum > secondSignum) ? 1 : 0); 
/* 125 */       if (firstSignum == 0 && secondSignum == 0) {
/* 126 */         return 0;
/*     */       }
/*     */ 
/*     */ 
/*     */       
/* 131 */       if (first.getClass() == second.getClass()) {
/*     */         
/* 133 */         if (first instanceof BigDecimal) {
/* 134 */           return ((BigDecimal)first).compareTo((BigDecimal)second);
/*     */         }
/*     */         
/* 137 */         if (first instanceof Integer) {
/* 138 */           return ((Integer)first).compareTo((Integer)second);
/*     */         }
/* 140 */         if (first instanceof Long) {
/* 141 */           return ((Long)first).compareTo((Long)second);
/*     */         }
/* 143 */         if (first instanceof Double) {
/* 144 */           return ((Double)first).compareTo((Double)second);
/*     */         }
/* 146 */         if (first instanceof Float) {
/* 147 */           return ((Float)first).compareTo((Float)second);
/*     */         }
/* 149 */         if (first instanceof Byte) {
/* 150 */           return ((Byte)first).compareTo((Byte)second);
/*     */         }
/* 152 */         if (first instanceof Short) {
/* 153 */           return ((Short)first).compareTo((Short)second);
/*     */         }
/*     */       } 
/*     */ 
/*     */ 
/*     */       
/* 159 */       if (first instanceof Double) {
/* 160 */         double firstD = first.doubleValue();
/* 161 */         if (Double.isInfinite(firstD)) {
/* 162 */           if (NumberUtil.hasTypeThatIsKnownToNotSupportInfiniteAndNaN(second)) {
/* 163 */             return (firstD == Double.NEGATIVE_INFINITY) ? -1 : 1;
/*     */           }
/* 165 */           if (second instanceof Float) {
/* 166 */             return Double.compare(firstD, second.doubleValue());
/*     */           }
/*     */         } 
/*     */       } 
/* 170 */       if (first instanceof Float) {
/* 171 */         float firstF = first.floatValue();
/* 172 */         if (Float.isInfinite(firstF)) {
/* 173 */           if (NumberUtil.hasTypeThatIsKnownToNotSupportInfiniteAndNaN(second)) {
/* 174 */             return (firstF == Float.NEGATIVE_INFINITY) ? -1 : 1;
/*     */           }
/* 176 */           if (second instanceof Double) {
/* 177 */             return Double.compare(firstF, second.doubleValue());
/*     */           }
/*     */         } 
/*     */       } 
/* 181 */       if (second instanceof Double) {
/* 182 */         double secondD = second.doubleValue();
/* 183 */         if (Double.isInfinite(secondD)) {
/* 184 */           if (NumberUtil.hasTypeThatIsKnownToNotSupportInfiniteAndNaN(first)) {
/* 185 */             return (secondD == Double.NEGATIVE_INFINITY) ? 1 : -1;
/*     */           }
/* 187 */           if (first instanceof Float) {
/* 188 */             return Double.compare(first.doubleValue(), secondD);
/*     */           }
/*     */         } 
/*     */       } 
/* 192 */       if (second instanceof Float) {
/* 193 */         float secondF = second.floatValue();
/* 194 */         if (Float.isInfinite(secondF)) {
/* 195 */           if (NumberUtil.hasTypeThatIsKnownToNotSupportInfiniteAndNaN(first)) {
/* 196 */             return (secondF == Float.NEGATIVE_INFINITY) ? 1 : -1;
/*     */           }
/* 198 */           if (first instanceof Double) {
/* 199 */             return Double.compare(first.doubleValue(), secondF);
/*     */           }
/*     */         } 
/*     */       } 
/*     */       
/* 204 */       return ArithmeticEngine.toBigDecimal(first).compareTo(ArithmeticEngine.toBigDecimal(second));
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public Number add(Number first, Number second) {
/* 210 */       BigDecimal left = ArithmeticEngine.toBigDecimal(first);
/* 211 */       BigDecimal right = ArithmeticEngine.toBigDecimal(second);
/* 212 */       return left.add(right);
/*     */     }
/*     */ 
/*     */     
/*     */     public Number subtract(Number first, Number second) {
/* 217 */       BigDecimal left = ArithmeticEngine.toBigDecimal(first);
/* 218 */       BigDecimal right = ArithmeticEngine.toBigDecimal(second);
/* 219 */       return left.subtract(right);
/*     */     }
/*     */ 
/*     */     
/*     */     public Number multiply(Number first, Number second) {
/* 224 */       BigDecimal left = ArithmeticEngine.toBigDecimal(first);
/* 225 */       BigDecimal right = ArithmeticEngine.toBigDecimal(second);
/* 226 */       BigDecimal result = left.multiply(right);
/* 227 */       if (result.scale() > this.maxScale) {
/* 228 */         result = result.setScale(this.maxScale, this.roundingPolicy);
/*     */       }
/* 230 */       return result;
/*     */     }
/*     */ 
/*     */     
/*     */     public Number divide(Number first, Number second) {
/* 235 */       BigDecimal left = ArithmeticEngine.toBigDecimal(first);
/* 236 */       BigDecimal right = ArithmeticEngine.toBigDecimal(second);
/* 237 */       return divide(left, right);
/*     */     }
/*     */ 
/*     */     
/*     */     public Number modulus(Number first, Number second) {
/* 242 */       long left = first.longValue();
/* 243 */       long right = second.longValue();
/* 244 */       return Long.valueOf(left % right);
/*     */     }
/*     */ 
/*     */     
/*     */     public Number toNumber(String s) {
/* 249 */       return ArithmeticEngine.toBigDecimalOrDouble(s);
/*     */     }
/*     */     
/*     */     private BigDecimal divide(BigDecimal left, BigDecimal right) {
/* 253 */       int scale1 = left.scale();
/* 254 */       int scale2 = right.scale();
/* 255 */       int scale = Math.max(scale1, scale2);
/* 256 */       scale = Math.max(this.minScale, scale);
/* 257 */       return left.divide(right, scale, this.roundingPolicy);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static class ConservativeEngine
/*     */     extends ArithmeticEngine
/*     */   {
/*     */     private static final int INTEGER = 0;
/*     */ 
/*     */ 
/*     */     
/*     */     private static final int LONG = 1;
/*     */ 
/*     */ 
/*     */     
/*     */     private static final int FLOAT = 2;
/*     */ 
/*     */     
/*     */     private static final int DOUBLE = 3;
/*     */ 
/*     */     
/*     */     private static final int BIGINTEGER = 4;
/*     */ 
/*     */     
/*     */     private static final int BIGDECIMAL = 5;
/*     */ 
/*     */     
/* 287 */     private static final Map classCodes = createClassCodesMap(); public int compareNumbers(Number first, Number second) throws TemplateException { int i; long l1; float f1; double d1; BigInteger bigInteger1; BigDecimal n1; int j; float f2; BigInteger bigInteger2;
/*     */       BigDecimal n2;
/*     */       long l2;
/*     */       double d2;
/* 291 */       switch (getCommonClassCode(first, second)) {
/*     */         case 0:
/* 293 */           i = first.intValue();
/* 294 */           j = second.intValue();
/* 295 */           return (i < j) ? -1 : ((i == j) ? 0 : 1);
/*     */         
/*     */         case 1:
/* 298 */           l1 = first.longValue();
/* 299 */           l2 = second.longValue();
/* 300 */           return (l1 < l2) ? -1 : ((l1 == l2) ? 0 : 1);
/*     */         
/*     */         case 2:
/* 303 */           f1 = first.floatValue();
/* 304 */           f2 = second.floatValue();
/* 305 */           return (f1 < f2) ? -1 : ((f1 == f2) ? 0 : 1);
/*     */         
/*     */         case 3:
/* 308 */           d1 = first.doubleValue();
/* 309 */           d2 = second.doubleValue();
/* 310 */           return (d1 < d2) ? -1 : ((d1 == d2) ? 0 : 1);
/*     */         
/*     */         case 4:
/* 313 */           bigInteger1 = toBigInteger(first);
/* 314 */           bigInteger2 = toBigInteger(second);
/* 315 */           return bigInteger1.compareTo(bigInteger2);
/*     */         
/*     */         case 5:
/* 318 */           n1 = ArithmeticEngine.toBigDecimal(first);
/* 319 */           n2 = ArithmeticEngine.toBigDecimal(second);
/* 320 */           return n1.compareTo(n2);
/*     */       } 
/*     */ 
/*     */ 
/*     */       
/* 325 */       throw new Error(); } public Number add(Number first, Number second) throws TemplateException { int i; long l1; BigInteger bigInteger1; BigDecimal n1; int j; BigInteger bigInteger2;
/*     */       BigDecimal n2;
/*     */       int n;
/*     */       long l2;
/*     */       long l3;
/* 330 */       switch (getCommonClassCode(first, second)) {
/*     */         case 0:
/* 332 */           i = first.intValue();
/* 333 */           j = second.intValue();
/* 334 */           n = i + j;
/* 335 */           return 
/* 336 */             Long.valueOf(((n ^ i) < 0 && (n ^ j) < 0) ? 
/* 337 */               Long.valueOf(i + j).longValue() : 
/* 338 */               Integer.valueOf(n).intValue());
/*     */         
/*     */         case 1:
/* 341 */           l1 = first.longValue();
/* 342 */           l2 = second.longValue();
/* 343 */           l3 = l1 + l2;
/* 344 */           return ((l3 ^ l1) < 0L && (l3 ^ l2) < 0L) ? 
/*     */             
/* 346 */             toBigInteger(first).add(toBigInteger(second)) : 
/* 347 */             Long.valueOf(l3);
/*     */         
/*     */         case 2:
/* 350 */           return Float.valueOf(first.floatValue() + second.floatValue());
/*     */         
/*     */         case 3:
/* 353 */           return Double.valueOf(first.doubleValue() + second.doubleValue());
/*     */         
/*     */         case 4:
/* 356 */           bigInteger1 = toBigInteger(first);
/* 357 */           bigInteger2 = toBigInteger(second);
/* 358 */           return bigInteger1.add(bigInteger2);
/*     */         
/*     */         case 5:
/* 361 */           n1 = ArithmeticEngine.toBigDecimal(first);
/* 362 */           n2 = ArithmeticEngine.toBigDecimal(second);
/* 363 */           return n1.add(n2);
/*     */       } 
/*     */ 
/*     */ 
/*     */       
/* 368 */       throw new Error(); } public Number subtract(Number first, Number second) throws TemplateException { int i; long l1; BigInteger bigInteger1; BigDecimal n1; int j; BigInteger bigInteger2;
/*     */       BigDecimal n2;
/*     */       int n;
/*     */       long l2;
/*     */       long l3;
/* 373 */       switch (getCommonClassCode(first, second)) {
/*     */         case 0:
/* 375 */           i = first.intValue();
/* 376 */           j = second.intValue();
/* 377 */           n = i - j;
/* 378 */           return 
/* 379 */             Long.valueOf(((n ^ i) < 0 && (n ^ j ^ 0xFFFFFFFF) < 0) ? 
/* 380 */               Long.valueOf(i - j).longValue() : 
/* 381 */               Integer.valueOf(n).intValue());
/*     */         
/*     */         case 1:
/* 384 */           l1 = first.longValue();
/* 385 */           l2 = second.longValue();
/* 386 */           l3 = l1 - l2;
/* 387 */           return ((l3 ^ l1) < 0L && (l3 ^ l2 ^ 0xFFFFFFFFFFFFFFFFL) < 0L) ? 
/*     */             
/* 389 */             toBigInteger(first).subtract(toBigInteger(second)) : 
/* 390 */             Long.valueOf(l3);
/*     */         
/*     */         case 2:
/* 393 */           return Float.valueOf(first.floatValue() - second.floatValue());
/*     */         
/*     */         case 3:
/* 396 */           return Double.valueOf(first.doubleValue() - second.doubleValue());
/*     */         
/*     */         case 4:
/* 399 */           bigInteger1 = toBigInteger(first);
/* 400 */           bigInteger2 = toBigInteger(second);
/* 401 */           return bigInteger1.subtract(bigInteger2);
/*     */         
/*     */         case 5:
/* 404 */           n1 = ArithmeticEngine.toBigDecimal(first);
/* 405 */           n2 = ArithmeticEngine.toBigDecimal(second);
/* 406 */           return n1.subtract(n2);
/*     */       } 
/*     */ 
/*     */ 
/*     */       
/* 411 */       throw new Error(); } public Number multiply(Number first, Number second) throws TemplateException { int i; long l1; BigInteger bigInteger1; BigDecimal n1; int j; BigInteger bigInteger2; BigDecimal n2;
/*     */       int n;
/*     */       long l2;
/*     */       BigDecimal r;
/*     */       long l3;
/* 416 */       switch (getCommonClassCode(first, second)) {
/*     */         case 0:
/* 418 */           i = first.intValue();
/* 419 */           j = second.intValue();
/* 420 */           n = i * j;
/* 421 */           return 
/* 422 */             Long.valueOf((i == 0 || n / i == j) ? 
/* 423 */               Integer.valueOf(n).intValue() : 
/* 424 */               Long.valueOf(i * j).longValue());
/*     */         
/*     */         case 1:
/* 427 */           l1 = first.longValue();
/* 428 */           l2 = second.longValue();
/* 429 */           l3 = l1 * l2;
/* 430 */           return (l1 == 0L || l3 / l1 == l2) ? 
/*     */             
/* 432 */             Long.valueOf(l3) : 
/* 433 */             toBigInteger(first).multiply(toBigInteger(second));
/*     */         
/*     */         case 2:
/* 436 */           return Float.valueOf(first.floatValue() * second.floatValue());
/*     */         
/*     */         case 3:
/* 439 */           return Double.valueOf(first.doubleValue() * second.doubleValue());
/*     */         
/*     */         case 4:
/* 442 */           bigInteger1 = toBigInteger(first);
/* 443 */           bigInteger2 = toBigInteger(second);
/* 444 */           return bigInteger1.multiply(bigInteger2);
/*     */         
/*     */         case 5:
/* 447 */           n1 = ArithmeticEngine.toBigDecimal(first);
/* 448 */           n2 = ArithmeticEngine.toBigDecimal(second);
/* 449 */           r = n1.multiply(n2);
/* 450 */           return (r.scale() > this.maxScale) ? r.setScale(this.maxScale, this.roundingPolicy) : r;
/*     */       } 
/*     */ 
/*     */ 
/*     */       
/* 455 */       throw new Error(); } public Number divide(Number first, Number second) throws TemplateException { int i; long l1; BigInteger bigInteger1; BigDecimal n1; int j; BigInteger bigInteger2; BigDecimal n2; long l2; BigInteger[] divmod; int scale1;
/*     */       BigDecimal bd1;
/*     */       int scale2;
/*     */       BigDecimal bd2;
/*     */       int scale;
/* 460 */       switch (getCommonClassCode(first, second)) {
/*     */         case 0:
/* 462 */           i = first.intValue();
/* 463 */           j = second.intValue();
/* 464 */           if (i % j == 0) {
/* 465 */             return Integer.valueOf(i / j);
/*     */           }
/* 467 */           return Double.valueOf(i / j);
/*     */         
/*     */         case 1:
/* 470 */           l1 = first.longValue();
/* 471 */           l2 = second.longValue();
/* 472 */           if (l1 % l2 == 0L) {
/* 473 */             return Long.valueOf(l1 / l2);
/*     */           }
/* 475 */           return Double.valueOf(l1 / l2);
/*     */         
/*     */         case 2:
/* 478 */           return Float.valueOf(first.floatValue() / second.floatValue());
/*     */         
/*     */         case 3:
/* 481 */           return Double.valueOf(first.doubleValue() / second.doubleValue());
/*     */         
/*     */         case 4:
/* 484 */           bigInteger1 = toBigInteger(first);
/* 485 */           bigInteger2 = toBigInteger(second);
/* 486 */           divmod = bigInteger1.divideAndRemainder(bigInteger2);
/* 487 */           if (divmod[1].equals(BigInteger.ZERO)) {
/* 488 */             return divmod[0];
/*     */           }
/* 490 */           bd1 = new BigDecimal(bigInteger1);
/* 491 */           bd2 = new BigDecimal(bigInteger2);
/* 492 */           return bd1.divide(bd2, this.minScale, this.roundingPolicy);
/*     */ 
/*     */         
/*     */         case 5:
/* 496 */           n1 = ArithmeticEngine.toBigDecimal(first);
/* 497 */           n2 = ArithmeticEngine.toBigDecimal(second);
/* 498 */           scale1 = n1.scale();
/* 499 */           scale2 = n2.scale();
/* 500 */           scale = Math.max(scale1, scale2);
/* 501 */           scale = Math.max(this.minScale, scale);
/* 502 */           return n1.divide(n2, scale, this.roundingPolicy);
/*     */       } 
/*     */ 
/*     */ 
/*     */       
/* 507 */       throw new Error(); }
/*     */     
/*     */     public Number modulus(Number first, Number second) throws TemplateException {
/*     */       BigInteger n1;
/*     */       BigInteger n2;
/* 512 */       switch (getCommonClassCode(first, second)) {
/*     */         case 0:
/* 514 */           return Integer.valueOf(first.intValue() % second.intValue());
/*     */         
/*     */         case 1:
/* 517 */           return Long.valueOf(first.longValue() % second.longValue());
/*     */         
/*     */         case 2:
/* 520 */           return Float.valueOf(first.floatValue() % second.floatValue());
/*     */         
/*     */         case 3:
/* 523 */           return Double.valueOf(first.doubleValue() % second.doubleValue());
/*     */         
/*     */         case 4:
/* 526 */           n1 = toBigInteger(first);
/* 527 */           n2 = toBigInteger(second);
/* 528 */           return n1.mod(n2);
/*     */         
/*     */         case 5:
/* 531 */           throw new _MiscTemplateException("Can't calculate remainder on BigDecimals");
/*     */       } 
/*     */ 
/*     */ 
/*     */       
/* 536 */       throw new BugException();
/*     */     }
/*     */ 
/*     */     
/*     */     public Number toNumber(String s) {
/* 541 */       Number n = ArithmeticEngine.toBigDecimalOrDouble(s);
/* 542 */       return (n instanceof BigDecimal) ? OptimizerUtil.optimizeNumberRepresentation(n) : n;
/*     */     }
/*     */     
/*     */     private static Map createClassCodesMap() {
/* 546 */       Map<Object, Object> map = new HashMap<>(17);
/* 547 */       Integer intcode = Integer.valueOf(0);
/* 548 */       map.put(Byte.class, intcode);
/* 549 */       map.put(Short.class, intcode);
/* 550 */       map.put(Integer.class, intcode);
/* 551 */       map.put(Long.class, Integer.valueOf(1));
/* 552 */       map.put(Float.class, Integer.valueOf(2));
/* 553 */       map.put(Double.class, Integer.valueOf(3));
/* 554 */       map.put(BigInteger.class, Integer.valueOf(4));
/* 555 */       map.put(BigDecimal.class, Integer.valueOf(5));
/* 556 */       return map;
/*     */     }
/*     */     
/*     */     private static int getClassCode(Number num) throws TemplateException {
/*     */       try {
/* 561 */         return ((Integer)classCodes.get(num.getClass())).intValue();
/* 562 */       } catch (NullPointerException e) {
/* 563 */         if (num == null) {
/* 564 */           throw new _MiscTemplateException("The Number object was null.");
/*     */         }
/* 566 */         throw new _MiscTemplateException(new Object[] { "Unknown number type ", num.getClass().getName() });
/*     */       } 
/*     */     }
/*     */ 
/*     */     
/*     */     private static int getCommonClassCode(Number num1, Number num2) throws TemplateException {
/* 572 */       int min, c1 = getClassCode(num1);
/* 573 */       int c2 = getClassCode(num2);
/* 574 */       int c = (c1 > c2) ? c1 : c2;
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 579 */       switch (c) {
/*     */         case 2:
/* 581 */           if (((c1 < c2) ? c1 : c2) == true) {
/* 582 */             return 3;
/*     */           }
/*     */           break;
/*     */         
/*     */         case 4:
/* 587 */           min = (c1 < c2) ? c1 : c2;
/* 588 */           if (min == 3 || min == 2) {
/* 589 */             return 5;
/*     */           }
/*     */           break;
/*     */       } 
/*     */       
/* 594 */       return c;
/*     */     }
/*     */     
/*     */     private static BigInteger toBigInteger(Number num) {
/* 598 */       return (num instanceof BigInteger) ? (BigInteger)num : new BigInteger(num.toString());
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static BigDecimal toBigDecimal(Number num) {
/* 609 */     if (num instanceof BigDecimal) {
/* 610 */       return (BigDecimal)num;
/*     */     }
/* 612 */     if (num instanceof Integer || num instanceof Long || num instanceof Byte || num instanceof Short) {
/* 613 */       return BigDecimal.valueOf(num.longValue());
/*     */     }
/* 615 */     if (num instanceof BigInteger) {
/* 616 */       return new BigDecimal((BigInteger)num);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     try {
/* 623 */       return new BigDecimal(num.toString());
/* 624 */     } catch (NumberFormatException e) {
/* 625 */       if (NumberUtil.isInfinite(num)) {
/* 626 */         throw new NumberFormatException("It's impossible to convert an infinte value (" + num
/* 627 */             .getClass().getSimpleName() + " " + num + ") to BigDecimal.");
/*     */       }
/*     */       
/* 630 */       throw new NumberFormatException("Can't parse this as BigDecimal number: " + StringUtil.jQuote(num));
/*     */     } 
/*     */   }
/*     */   
/*     */   private static Number toBigDecimalOrDouble(String s) {
/* 635 */     if (s.length() > 2) {
/* 636 */       char c = s.charAt(0);
/* 637 */       if (c == 'I' && (s.equals("INF") || s.equals("Infinity")))
/* 638 */         return Double.valueOf(Double.POSITIVE_INFINITY); 
/* 639 */       if (c == 'N' && s.equals("NaN"))
/* 640 */         return Double.valueOf(Double.NaN); 
/* 641 */       if (c == '-' && s.charAt(1) == 'I' && (s.equals("-INF") || s.equals("-Infinity"))) {
/* 642 */         return Double.valueOf(Double.NEGATIVE_INFINITY);
/*     */       }
/*     */     } 
/* 645 */     return new BigDecimal(s);
/*     */   }
/*     */   
/*     */   public abstract int compareNumbers(Number paramNumber1, Number paramNumber2) throws TemplateException;
/*     */   
/*     */   public abstract Number add(Number paramNumber1, Number paramNumber2) throws TemplateException;
/*     */   
/*     */   public abstract Number subtract(Number paramNumber1, Number paramNumber2) throws TemplateException;
/*     */   
/*     */   public abstract Number multiply(Number paramNumber1, Number paramNumber2) throws TemplateException;
/*     */   
/*     */   public abstract Number divide(Number paramNumber1, Number paramNumber2) throws TemplateException;
/*     */   
/*     */   public abstract Number modulus(Number paramNumber1, Number paramNumber2) throws TemplateException;
/*     */   
/*     */   public abstract Number toNumber(String paramString);
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\core\ArithmeticEngine.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */