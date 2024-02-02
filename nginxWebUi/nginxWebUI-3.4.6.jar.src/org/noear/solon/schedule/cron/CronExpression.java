/*      */ package org.noear.solon.schedule.cron;
/*      */ import java.io.IOException;
/*      */ import java.io.ObjectInputStream;
/*      */ import java.io.Serializable;
/*      */ import java.text.ParseException;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Calendar;
/*      */ import java.util.Date;
/*      */ import java.util.GregorianCalendar;
/*      */ import java.util.HashMap;
/*      */ import java.util.Iterator;
/*      */ import java.util.Locale;
/*      */ import java.util.Map;
/*      */ import java.util.Set;
/*      */ import java.util.SortedSet;
/*      */ import java.util.StringTokenizer;
/*      */ import java.util.TimeZone;
/*      */ import java.util.TreeSet;
/*      */ 
/*      */ public class CronExpression implements Serializable, Cloneable {
/*      */   private static final long serialVersionUID = 12423409423L;
/*      */   protected static final int SECOND = 0;
/*      */   protected static final int MINUTE = 1;
/*      */   protected static final int HOUR = 2;
/*      */   protected static final int DAY_OF_MONTH = 3;
/*   26 */   protected static final Integer ALL_SPEC = Integer.valueOf(99); protected static final int MONTH = 4; protected static final int DAY_OF_WEEK = 5; protected static final int YEAR = 6; protected static final int ALL_SPEC_INT = 99; protected static final int NO_SPEC_INT = 98;
/*   27 */   protected static final Integer NO_SPEC = Integer.valueOf(98);
/*      */   
/*   29 */   protected static final Map<String, Integer> monthMap = new HashMap<>(20);
/*   30 */   protected static final Map<String, Integer> dayMap = new HashMap<>(60); private final String cronExpression;
/*      */   static {
/*   32 */     monthMap.put("JAN", Integer.valueOf(0));
/*   33 */     monthMap.put("FEB", Integer.valueOf(1));
/*   34 */     monthMap.put("MAR", Integer.valueOf(2));
/*   35 */     monthMap.put("APR", Integer.valueOf(3));
/*   36 */     monthMap.put("MAY", Integer.valueOf(4));
/*   37 */     monthMap.put("JUN", Integer.valueOf(5));
/*   38 */     monthMap.put("JUL", Integer.valueOf(6));
/*   39 */     monthMap.put("AUG", Integer.valueOf(7));
/*   40 */     monthMap.put("SEP", Integer.valueOf(8));
/*   41 */     monthMap.put("OCT", Integer.valueOf(9));
/*   42 */     monthMap.put("NOV", Integer.valueOf(10));
/*   43 */     monthMap.put("DEC", Integer.valueOf(11));
/*      */     
/*   45 */     dayMap.put("SUN", Integer.valueOf(1));
/*   46 */     dayMap.put("MON", Integer.valueOf(2));
/*   47 */     dayMap.put("TUE", Integer.valueOf(3));
/*   48 */     dayMap.put("WED", Integer.valueOf(4));
/*   49 */     dayMap.put("THU", Integer.valueOf(5));
/*   50 */     dayMap.put("FRI", Integer.valueOf(6));
/*   51 */     dayMap.put("SAT", Integer.valueOf(7));
/*      */   }
/*      */ 
/*      */   
/*   55 */   private TimeZone timeZone = null;
/*      */   
/*      */   protected transient TreeSet<Integer> seconds;
/*      */   protected transient TreeSet<Integer> minutes;
/*      */   protected transient TreeSet<Integer> hours;
/*      */   protected transient TreeSet<Integer> daysOfMonth;
/*      */   protected transient TreeSet<Integer> months;
/*      */   protected transient TreeSet<Integer> daysOfWeek;
/*      */   protected transient TreeSet<Integer> years;
/*      */   protected transient boolean lastdayOfWeek = false;
/*   65 */   protected transient int nthdayOfWeek = 0;
/*      */   protected transient boolean lastdayOfMonth = false;
/*      */   protected transient boolean nearestWeekday = false;
/*   68 */   protected transient int lastdayOffset = 0;
/*      */   
/*      */   protected transient boolean expressionParsed = false;
/*   71 */   public static final int MAX_YEAR = Calendar.getInstance().get(1) + 100;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public CronExpression(String cronExpression) throws ParseException {
/*   84 */     if (cronExpression == null) {
/*   85 */       throw new IllegalArgumentException("cronExpression cannot be null");
/*      */     }
/*      */     
/*   88 */     this.cronExpression = cronExpression.toUpperCase(Locale.US);
/*      */     
/*   90 */     buildExpression(this.cronExpression);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public CronExpression(CronExpression expression) {
/*  106 */     this.cronExpression = expression.getCronExpression();
/*      */     try {
/*  108 */       buildExpression(this.cronExpression);
/*  109 */     } catch (ParseException ex) {
/*  110 */       throw new AssertionError();
/*      */     } 
/*  112 */     if (expression.getTimeZone() != null) {
/*  113 */       setTimeZone((TimeZone)expression.getTimeZone().clone());
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean isSatisfiedBy(Date date) {
/*  127 */     Calendar testDateCal = Calendar.getInstance(getTimeZone());
/*  128 */     testDateCal.setTime(date);
/*  129 */     testDateCal.set(14, 0);
/*  130 */     Date originalDate = testDateCal.getTime();
/*      */     
/*  132 */     testDateCal.add(13, -1);
/*      */     
/*  134 */     Date timeAfter = getTimeAfter(testDateCal.getTime());
/*      */     
/*  136 */     return (timeAfter != null && timeAfter.equals(originalDate));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Date getNextValidTimeAfter(Date date) {
/*  148 */     return getTimeAfter(date);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Date getNextInvalidTimeAfter(Date date) {
/*  160 */     long difference = 1000L;
/*      */ 
/*      */     
/*  163 */     Calendar adjustCal = Calendar.getInstance(getTimeZone());
/*  164 */     adjustCal.setTime(date);
/*  165 */     adjustCal.set(14, 0);
/*  166 */     Date lastDate = adjustCal.getTime();
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  175 */     while (difference == 1000L) {
/*  176 */       Date newDate = getTimeAfter(lastDate);
/*  177 */       if (newDate == null) {
/*      */         break;
/*      */       }
/*  180 */       difference = newDate.getTime() - lastDate.getTime();
/*      */       
/*  182 */       if (difference == 1000L) {
/*  183 */         lastDate = newDate;
/*      */       }
/*      */     } 
/*      */     
/*  187 */     return new Date(lastDate.getTime() + 1000L);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public TimeZone getTimeZone() {
/*  195 */     if (this.timeZone == null) {
/*  196 */       this.timeZone = TimeZone.getDefault();
/*      */     }
/*      */     
/*  199 */     return this.timeZone;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setTimeZone(TimeZone timeZone) {
/*  207 */     this.timeZone = timeZone;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String toString() {
/*  217 */     return this.cronExpression;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean isValidExpression(String cronExpression) {
/*      */     try {
/*  231 */       new CronExpression(cronExpression);
/*  232 */     } catch (ParseException pe) {
/*  233 */       return false;
/*      */     } 
/*      */     
/*  236 */     return true;
/*      */   }
/*      */ 
/*      */   
/*      */   public static void validateExpression(String cronExpression) throws ParseException {
/*  241 */     new CronExpression(cronExpression);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected void buildExpression(String expression) throws ParseException {
/*  252 */     this.expressionParsed = true;
/*      */ 
/*      */     
/*      */     try {
/*  256 */       if (this.seconds == null) {
/*  257 */         this.seconds = new TreeSet<>();
/*      */       }
/*  259 */       if (this.minutes == null) {
/*  260 */         this.minutes = new TreeSet<>();
/*      */       }
/*  262 */       if (this.hours == null) {
/*  263 */         this.hours = new TreeSet<>();
/*      */       }
/*  265 */       if (this.daysOfMonth == null) {
/*  266 */         this.daysOfMonth = new TreeSet<>();
/*      */       }
/*  268 */       if (this.months == null) {
/*  269 */         this.months = new TreeSet<>();
/*      */       }
/*  271 */       if (this.daysOfWeek == null) {
/*  272 */         this.daysOfWeek = new TreeSet<>();
/*      */       }
/*  274 */       if (this.years == null) {
/*  275 */         this.years = new TreeSet<>();
/*      */       }
/*      */       
/*  278 */       int exprOn = 0;
/*      */       
/*  280 */       StringTokenizer exprsTok = new StringTokenizer(expression, " \t", false);
/*      */ 
/*      */       
/*  283 */       while (exprsTok.hasMoreTokens() && exprOn <= 6) {
/*  284 */         String expr = exprsTok.nextToken().trim();
/*      */ 
/*      */         
/*  287 */         if (exprOn == 3 && expr.indexOf('L') != -1 && expr.length() > 1 && expr.contains(",")) {
/*  288 */           throw new ParseException("Support for specifying 'L' and 'LW' with other days of the month is not implemented", -1);
/*      */         }
/*      */         
/*  291 */         if (exprOn == 5 && expr.indexOf('L') != -1 && expr.length() > 1 && expr.contains(",")) {
/*  292 */           throw new ParseException("Support for specifying 'L' with other days of the week is not implemented", -1);
/*      */         }
/*  294 */         if (exprOn == 5 && expr.indexOf('#') != -1 && expr.indexOf('#', expr.indexOf('#') + 1) != -1) {
/*  295 */           throw new ParseException("Support for specifying multiple \"nth\" days is not implemented.", -1);
/*      */         }
/*      */         
/*  298 */         StringTokenizer vTok = new StringTokenizer(expr, ",");
/*  299 */         while (vTok.hasMoreTokens()) {
/*  300 */           String v = vTok.nextToken();
/*  301 */           storeExpressionVals(0, v, exprOn);
/*      */         } 
/*      */         
/*  304 */         exprOn++;
/*      */       } 
/*      */       
/*  307 */       if (exprOn <= 5) {
/*  308 */         throw new ParseException("Unexpected end of expression.", expression
/*  309 */             .length());
/*      */       }
/*      */       
/*  312 */       if (exprOn <= 6) {
/*  313 */         storeExpressionVals(0, "*", 6);
/*      */       }
/*      */       
/*  316 */       TreeSet<Integer> dow = getSet(5);
/*  317 */       TreeSet<Integer> dom = getSet(3);
/*      */ 
/*      */       
/*  320 */       boolean dayOfMSpec = !dom.contains(NO_SPEC);
/*  321 */       boolean dayOfWSpec = !dow.contains(NO_SPEC);
/*      */       
/*  323 */       if ((!dayOfMSpec || dayOfWSpec) && (
/*  324 */         !dayOfWSpec || dayOfMSpec)) {
/*  325 */         throw new ParseException("Support for specifying both a day-of-week AND a day-of-month parameter is not implemented.", 0);
/*      */       
/*      */       }
/*      */     }
/*  329 */     catch (ParseException pe) {
/*  330 */       throw pe;
/*  331 */     } catch (Exception e) {
/*  332 */       throw new ParseException("Illegal cron expression format (" + e
/*  333 */           .toString() + ")", 0);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected int storeExpressionVals(int pos, String s, int type) throws ParseException {
/*  340 */     int incr = 0;
/*  341 */     int i = skipWhiteSpace(pos, s);
/*  342 */     if (i >= s.length()) {
/*  343 */       return i;
/*      */     }
/*  345 */     char c = s.charAt(i);
/*  346 */     if (c >= 'A' && c <= 'Z' && !s.equals("L") && !s.equals("LW") && !s.matches("^L-[0-9]*[W]?")) {
/*  347 */       String sub = s.substring(i, i + 3);
/*  348 */       int sval = -1;
/*  349 */       int eval = -1;
/*  350 */       if (type == 4) {
/*  351 */         sval = getMonthNumber(sub) + 1;
/*  352 */         if (sval <= 0) {
/*  353 */           throw new ParseException("Invalid Month value: '" + sub + "'", i);
/*      */         }
/*  355 */         if (s.length() > i + 3) {
/*  356 */           c = s.charAt(i + 3);
/*  357 */           if (c == '-') {
/*  358 */             i += 4;
/*  359 */             sub = s.substring(i, i + 3);
/*  360 */             eval = getMonthNumber(sub) + 1;
/*  361 */             if (eval <= 0) {
/*  362 */               throw new ParseException("Invalid Month value: '" + sub + "'", i);
/*      */             }
/*      */           } 
/*      */         } 
/*  366 */       } else if (type == 5) {
/*  367 */         sval = getDayOfWeekNumber(sub);
/*  368 */         if (sval < 0) {
/*  369 */           throw new ParseException("Invalid Day-of-Week value: '" + sub + "'", i);
/*      */         }
/*      */         
/*  372 */         if (s.length() > i + 3) {
/*  373 */           c = s.charAt(i + 3);
/*  374 */           if (c == '-') {
/*  375 */             i += 4;
/*  376 */             sub = s.substring(i, i + 3);
/*  377 */             eval = getDayOfWeekNumber(sub);
/*  378 */             if (eval < 0) {
/*  379 */               throw new ParseException("Invalid Day-of-Week value: '" + sub + "'", i);
/*      */             
/*      */             }
/*      */           }
/*  383 */           else if (c == '#') {
/*      */             try {
/*  385 */               i += 4;
/*  386 */               this.nthdayOfWeek = Integer.parseInt(s.substring(i));
/*  387 */               if (this.nthdayOfWeek < 1 || this.nthdayOfWeek > 5) {
/*  388 */                 throw new Exception();
/*      */               }
/*  390 */             } catch (Exception e) {
/*  391 */               throw new ParseException("A numeric value between 1 and 5 must follow the '#' option", i);
/*      */             }
/*      */           
/*      */           }
/*  395 */           else if (c == 'L') {
/*  396 */             this.lastdayOfWeek = true;
/*  397 */             i++;
/*      */           } 
/*      */         } 
/*      */       } else {
/*      */         
/*  402 */         throw new ParseException("Illegal characters for this position: '" + sub + "'", i);
/*      */       } 
/*      */ 
/*      */       
/*  406 */       if (eval != -1) {
/*  407 */         incr = 1;
/*      */       }
/*  409 */       addToSet(sval, eval, incr, type);
/*  410 */       return i + 3;
/*      */     } 
/*      */     
/*  413 */     if (c == '?') {
/*  414 */       i++;
/*  415 */       if (i + 1 < s.length() && s
/*  416 */         .charAt(i) != ' ' && s.charAt(i + 1) != '\t') {
/*  417 */         throw new ParseException("Illegal character after '?': " + s
/*  418 */             .charAt(i), i);
/*      */       }
/*  420 */       if (type != 5 && type != 3) {
/*  421 */         throw new ParseException("'?' can only be specified for Day-of-Month or Day-of-Week.", i);
/*      */       }
/*      */ 
/*      */       
/*  425 */       if (type == 5 && !this.lastdayOfMonth) {
/*  426 */         int val = ((Integer)this.daysOfMonth.last()).intValue();
/*  427 */         if (val == 98) {
/*  428 */           throw new ParseException("'?' can only be specified for Day-of-Month -OR- Day-of-Week.", i);
/*      */         }
/*      */       } 
/*      */ 
/*      */ 
/*      */       
/*  434 */       addToSet(98, -1, 0, type);
/*  435 */       return i;
/*      */     } 
/*      */     
/*  438 */     if (c == '*' || c == '/') {
/*  439 */       if (c == '*' && i + 1 >= s.length()) {
/*  440 */         addToSet(99, -1, incr, type);
/*  441 */         return i + 1;
/*  442 */       }  if (c == '/' && (i + 1 >= s
/*  443 */         .length() || s.charAt(i + 1) == ' ' || s
/*  444 */         .charAt(i + 1) == '\t'))
/*  445 */         throw new ParseException("'/' must be followed by an integer.", i); 
/*  446 */       if (c == '*') {
/*  447 */         i++;
/*      */       }
/*  449 */       c = s.charAt(i);
/*  450 */       if (c == '/') {
/*  451 */         i++;
/*  452 */         if (i >= s.length()) {
/*  453 */           throw new ParseException("Unexpected end of string.", i);
/*      */         }
/*      */         
/*  456 */         incr = getNumericValue(s, i);
/*      */         
/*  458 */         i++;
/*  459 */         if (incr > 10) {
/*  460 */           i++;
/*      */         }
/*  462 */         checkIncrementRange(incr, type, i);
/*      */       } else {
/*  464 */         incr = 1;
/*      */       } 
/*      */       
/*  467 */       addToSet(99, -1, incr, type);
/*  468 */       return i;
/*  469 */     }  if (c == 'L') {
/*  470 */       i++;
/*  471 */       if (type == 3) {
/*  472 */         this.lastdayOfMonth = true;
/*      */       }
/*  474 */       if (type == 5) {
/*  475 */         addToSet(7, 7, 0, type);
/*      */       }
/*  477 */       if (type == 3 && s.length() > i) {
/*  478 */         c = s.charAt(i);
/*  479 */         if (c == '-') {
/*  480 */           ValueSet vs = getValue(0, s, i + 1);
/*  481 */           this.lastdayOffset = vs.value;
/*  482 */           if (this.lastdayOffset > 30)
/*  483 */             throw new ParseException("Offset from last day must be <= 30", i + 1); 
/*  484 */           i = vs.pos;
/*      */         } 
/*  486 */         if (s.length() > i) {
/*  487 */           c = s.charAt(i);
/*  488 */           if (c == 'W') {
/*  489 */             this.nearestWeekday = true;
/*  490 */             i++;
/*      */           } 
/*      */         } 
/*      */       } 
/*  494 */       return i;
/*  495 */     }  if (c >= '0' && c <= '9') {
/*  496 */       int val = Integer.parseInt(String.valueOf(c));
/*  497 */       i++;
/*  498 */       if (i >= s.length()) {
/*  499 */         addToSet(val, -1, -1, type);
/*      */       } else {
/*  501 */         c = s.charAt(i);
/*  502 */         if (c >= '0' && c <= '9') {
/*  503 */           ValueSet vs = getValue(val, s, i);
/*  504 */           val = vs.value;
/*  505 */           i = vs.pos;
/*      */         } 
/*  507 */         i = checkNext(i, s, val, type);
/*  508 */         return i;
/*      */       } 
/*      */     } else {
/*  511 */       throw new ParseException("Unexpected character: " + c, i);
/*      */     } 
/*      */     
/*  514 */     return i;
/*      */   }
/*      */   
/*      */   private void checkIncrementRange(int incr, int type, int idxPos) throws ParseException {
/*  518 */     if (incr > 59 && (type == 0 || type == 1))
/*  519 */       throw new ParseException("Increment > 60 : " + incr, idxPos); 
/*  520 */     if (incr > 23 && type == 2)
/*  521 */       throw new ParseException("Increment > 24 : " + incr, idxPos); 
/*  522 */     if (incr > 31 && type == 3)
/*  523 */       throw new ParseException("Increment > 31 : " + incr, idxPos); 
/*  524 */     if (incr > 7 && type == 5)
/*  525 */       throw new ParseException("Increment > 7 : " + incr, idxPos); 
/*  526 */     if (incr > 12 && type == 4) {
/*  527 */       throw new ParseException("Increment > 12 : " + incr, idxPos);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected int checkNext(int pos, String s, int val, int type) throws ParseException {
/*  534 */     int end = -1;
/*  535 */     int i = pos;
/*      */     
/*  537 */     if (i >= s.length()) {
/*  538 */       addToSet(val, end, -1, type);
/*  539 */       return i;
/*      */     } 
/*      */     
/*  542 */     char c = s.charAt(pos);
/*      */     
/*  544 */     if (c == 'L') {
/*  545 */       if (type == 5) {
/*  546 */         if (val < 1 || val > 7)
/*  547 */           throw new ParseException("Day-of-Week values must be between 1 and 7", -1); 
/*  548 */         this.lastdayOfWeek = true;
/*      */       } else {
/*  550 */         throw new ParseException("'L' option is not valid here. (pos=" + i + ")", i);
/*      */       } 
/*  552 */       TreeSet<Integer> set = getSet(type);
/*  553 */       set.add(Integer.valueOf(val));
/*  554 */       i++;
/*  555 */       return i;
/*      */     } 
/*      */     
/*  558 */     if (c == 'W') {
/*  559 */       if (type == 3) {
/*  560 */         this.nearestWeekday = true;
/*      */       } else {
/*  562 */         throw new ParseException("'W' option is not valid here. (pos=" + i + ")", i);
/*      */       } 
/*  564 */       if (val > 31)
/*  565 */         throw new ParseException("The 'W' option does not make sense with values larger than 31 (max number of days in a month)", i); 
/*  566 */       TreeSet<Integer> set = getSet(type);
/*  567 */       set.add(Integer.valueOf(val));
/*  568 */       i++;
/*  569 */       return i;
/*      */     } 
/*      */     
/*  572 */     if (c == '#') {
/*  573 */       if (type != 5) {
/*  574 */         throw new ParseException("'#' option is not valid here. (pos=" + i + ")", i);
/*      */       }
/*  576 */       i++;
/*      */       try {
/*  578 */         this.nthdayOfWeek = Integer.parseInt(s.substring(i));
/*  579 */         if (this.nthdayOfWeek < 1 || this.nthdayOfWeek > 5) {
/*  580 */           throw new Exception();
/*      */         }
/*  582 */       } catch (Exception e) {
/*  583 */         throw new ParseException("A numeric value between 1 and 5 must follow the '#' option", i);
/*      */       } 
/*      */ 
/*      */ 
/*      */       
/*  588 */       TreeSet<Integer> set = getSet(type);
/*  589 */       set.add(Integer.valueOf(val));
/*  590 */       i++;
/*  591 */       return i;
/*      */     } 
/*      */     
/*  594 */     if (c == '-') {
/*  595 */       i++;
/*  596 */       c = s.charAt(i);
/*  597 */       int v = Integer.parseInt(String.valueOf(c));
/*  598 */       end = v;
/*  599 */       i++;
/*  600 */       if (i >= s.length()) {
/*  601 */         addToSet(val, end, 1, type);
/*  602 */         return i;
/*      */       } 
/*  604 */       c = s.charAt(i);
/*  605 */       if (c >= '0' && c <= '9') {
/*  606 */         ValueSet vs = getValue(v, s, i);
/*  607 */         end = vs.value;
/*  608 */         i = vs.pos;
/*      */       } 
/*  610 */       if (i < s.length() && (c = s.charAt(i)) == '/') {
/*  611 */         i++;
/*  612 */         c = s.charAt(i);
/*  613 */         int v2 = Integer.parseInt(String.valueOf(c));
/*  614 */         i++;
/*  615 */         if (i >= s.length()) {
/*  616 */           addToSet(val, end, v2, type);
/*  617 */           return i;
/*      */         } 
/*  619 */         c = s.charAt(i);
/*  620 */         if (c >= '0' && c <= '9') {
/*  621 */           ValueSet vs = getValue(v2, s, i);
/*  622 */           int v3 = vs.value;
/*  623 */           addToSet(val, end, v3, type);
/*  624 */           i = vs.pos;
/*  625 */           return i;
/*      */         } 
/*  627 */         addToSet(val, end, v2, type);
/*  628 */         return i;
/*      */       } 
/*      */       
/*  631 */       addToSet(val, end, 1, type);
/*  632 */       return i;
/*      */     } 
/*      */ 
/*      */     
/*  636 */     if (c == '/') {
/*  637 */       if (i + 1 >= s.length() || s.charAt(i + 1) == ' ' || s.charAt(i + 1) == '\t') {
/*  638 */         throw new ParseException("'/' must be followed by an integer.", i);
/*      */       }
/*      */       
/*  641 */       i++;
/*  642 */       c = s.charAt(i);
/*  643 */       int v2 = Integer.parseInt(String.valueOf(c));
/*  644 */       i++;
/*  645 */       if (i >= s.length()) {
/*  646 */         checkIncrementRange(v2, type, i);
/*  647 */         addToSet(val, end, v2, type);
/*  648 */         return i;
/*      */       } 
/*  650 */       c = s.charAt(i);
/*  651 */       if (c >= '0' && c <= '9') {
/*  652 */         ValueSet vs = getValue(v2, s, i);
/*  653 */         int v3 = vs.value;
/*  654 */         checkIncrementRange(v3, type, i);
/*  655 */         addToSet(val, end, v3, type);
/*  656 */         i = vs.pos;
/*  657 */         return i;
/*      */       } 
/*  659 */       throw new ParseException("Unexpected character '" + c + "' after '/'", i);
/*      */     } 
/*      */ 
/*      */     
/*  663 */     addToSet(val, end, 0, type);
/*  664 */     i++;
/*  665 */     return i;
/*      */   }
/*      */   
/*      */   public String getCronExpression() {
/*  669 */     return this.cronExpression;
/*      */   }
/*      */   
/*      */   public String getExpressionSummary() {
/*  673 */     StringBuilder buf = new StringBuilder();
/*      */     
/*  675 */     buf.append("seconds: ");
/*  676 */     buf.append(getExpressionSetSummary(this.seconds));
/*  677 */     buf.append("\n");
/*  678 */     buf.append("minutes: ");
/*  679 */     buf.append(getExpressionSetSummary(this.minutes));
/*  680 */     buf.append("\n");
/*  681 */     buf.append("hours: ");
/*  682 */     buf.append(getExpressionSetSummary(this.hours));
/*  683 */     buf.append("\n");
/*  684 */     buf.append("daysOfMonth: ");
/*  685 */     buf.append(getExpressionSetSummary(this.daysOfMonth));
/*  686 */     buf.append("\n");
/*  687 */     buf.append("months: ");
/*  688 */     buf.append(getExpressionSetSummary(this.months));
/*  689 */     buf.append("\n");
/*  690 */     buf.append("daysOfWeek: ");
/*  691 */     buf.append(getExpressionSetSummary(this.daysOfWeek));
/*  692 */     buf.append("\n");
/*  693 */     buf.append("lastdayOfWeek: ");
/*  694 */     buf.append(this.lastdayOfWeek);
/*  695 */     buf.append("\n");
/*  696 */     buf.append("nearestWeekday: ");
/*  697 */     buf.append(this.nearestWeekday);
/*  698 */     buf.append("\n");
/*  699 */     buf.append("NthDayOfWeek: ");
/*  700 */     buf.append(this.nthdayOfWeek);
/*  701 */     buf.append("\n");
/*  702 */     buf.append("lastdayOfMonth: ");
/*  703 */     buf.append(this.lastdayOfMonth);
/*  704 */     buf.append("\n");
/*  705 */     buf.append("years: ");
/*  706 */     buf.append(getExpressionSetSummary(this.years));
/*  707 */     buf.append("\n");
/*      */     
/*  709 */     return buf.toString();
/*      */   }
/*      */ 
/*      */   
/*      */   protected String getExpressionSetSummary(Set<Integer> set) {
/*  714 */     if (set.contains(NO_SPEC)) {
/*  715 */       return "?";
/*      */     }
/*  717 */     if (set.contains(ALL_SPEC)) {
/*  718 */       return "*";
/*      */     }
/*      */     
/*  721 */     StringBuilder buf = new StringBuilder();
/*      */     
/*  723 */     Iterator<Integer> itr = set.iterator();
/*  724 */     boolean first = true;
/*  725 */     while (itr.hasNext()) {
/*  726 */       Integer iVal = itr.next();
/*  727 */       String val = iVal.toString();
/*  728 */       if (!first) {
/*  729 */         buf.append(",");
/*      */       }
/*  731 */       buf.append(val);
/*  732 */       first = false;
/*      */     } 
/*      */     
/*  735 */     return buf.toString();
/*      */   }
/*      */ 
/*      */   
/*      */   protected String getExpressionSetSummary(ArrayList<Integer> list) {
/*  740 */     if (list.contains(NO_SPEC)) {
/*  741 */       return "?";
/*      */     }
/*  743 */     if (list.contains(ALL_SPEC)) {
/*  744 */       return "*";
/*      */     }
/*      */     
/*  747 */     StringBuilder buf = new StringBuilder();
/*      */     
/*  749 */     Iterator<Integer> itr = list.iterator();
/*  750 */     boolean first = true;
/*  751 */     while (itr.hasNext()) {
/*  752 */       Integer iVal = itr.next();
/*  753 */       String val = iVal.toString();
/*  754 */       if (!first) {
/*  755 */         buf.append(",");
/*      */       }
/*  757 */       buf.append(val);
/*  758 */       first = false;
/*      */     } 
/*      */     
/*  761 */     return buf.toString();
/*      */   }
/*      */   
/*      */   protected int skipWhiteSpace(int i, String s) {
/*  765 */     for (; i < s.length() && (s.charAt(i) == ' ' || s.charAt(i) == '\t'); i++);
/*      */ 
/*      */ 
/*      */     
/*  769 */     return i;
/*      */   }
/*      */   
/*      */   protected int findNextWhiteSpace(int i, String s) {
/*  773 */     for (; i < s.length() && (s.charAt(i) != ' ' || s.charAt(i) != '\t'); i++);
/*      */ 
/*      */ 
/*      */     
/*  777 */     return i;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected void addToSet(int val, int end, int incr, int type) throws ParseException {
/*  783 */     TreeSet<Integer> set = getSet(type);
/*      */     
/*  785 */     if (type == 0 || type == 1) {
/*  786 */       if ((val < 0 || val > 59 || end > 59) && val != 99) {
/*  787 */         throw new ParseException("Minute and Second values must be between 0 and 59", -1);
/*      */       
/*      */       }
/*      */     }
/*  791 */     else if (type == 2) {
/*  792 */       if ((val < 0 || val > 23 || end > 23) && val != 99) {
/*  793 */         throw new ParseException("Hour values must be between 0 and 23", -1);
/*      */       }
/*      */     }
/*  796 */     else if (type == 3) {
/*  797 */       if ((val < 1 || val > 31 || end > 31) && val != 99 && val != 98)
/*      */       {
/*  799 */         throw new ParseException("Day of month values must be between 1 and 31", -1);
/*      */       }
/*      */     }
/*  802 */     else if (type == 4) {
/*  803 */       if ((val < 1 || val > 12 || end > 12) && val != 99) {
/*  804 */         throw new ParseException("Month values must be between 1 and 12", -1);
/*      */       }
/*      */     }
/*  807 */     else if (type == 5 && (
/*  808 */       val == 0 || val > 7 || end > 7) && val != 99 && val != 98) {
/*      */       
/*  810 */       throw new ParseException("Day-of-Week values must be between 1 and 7", -1);
/*      */     } 
/*      */ 
/*      */ 
/*      */     
/*  815 */     if ((incr == 0 || incr == -1) && val != 99) {
/*  816 */       if (val != -1) {
/*  817 */         set.add(Integer.valueOf(val));
/*      */       } else {
/*  819 */         set.add(NO_SPEC);
/*      */       } 
/*      */       
/*      */       return;
/*      */     } 
/*      */     
/*  825 */     int startAt = val;
/*  826 */     int stopAt = end;
/*      */     
/*  828 */     if (val == 99 && incr <= 0) {
/*  829 */       incr = 1;
/*  830 */       set.add(ALL_SPEC);
/*      */     } 
/*      */     
/*  833 */     if (type == 0 || type == 1) {
/*  834 */       if (stopAt == -1) {
/*  835 */         stopAt = 59;
/*      */       }
/*  837 */       if (startAt == -1 || startAt == 99) {
/*  838 */         startAt = 0;
/*      */       }
/*  840 */     } else if (type == 2) {
/*  841 */       if (stopAt == -1) {
/*  842 */         stopAt = 23;
/*      */       }
/*  844 */       if (startAt == -1 || startAt == 99) {
/*  845 */         startAt = 0;
/*      */       }
/*  847 */     } else if (type == 3) {
/*  848 */       if (stopAt == -1) {
/*  849 */         stopAt = 31;
/*      */       }
/*  851 */       if (startAt == -1 || startAt == 99) {
/*  852 */         startAt = 1;
/*      */       }
/*  854 */     } else if (type == 4) {
/*  855 */       if (stopAt == -1) {
/*  856 */         stopAt = 12;
/*      */       }
/*  858 */       if (startAt == -1 || startAt == 99) {
/*  859 */         startAt = 1;
/*      */       }
/*  861 */     } else if (type == 5) {
/*  862 */       if (stopAt == -1) {
/*  863 */         stopAt = 7;
/*      */       }
/*  865 */       if (startAt == -1 || startAt == 99) {
/*  866 */         startAt = 1;
/*      */       }
/*  868 */     } else if (type == 6) {
/*  869 */       if (stopAt == -1) {
/*  870 */         stopAt = MAX_YEAR;
/*      */       }
/*  872 */       if (startAt == -1 || startAt == 99) {
/*  873 */         startAt = 1970;
/*      */       }
/*      */     } 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  880 */     int max = -1;
/*  881 */     if (stopAt < startAt) {
/*  882 */       switch (type) { case 0:
/*  883 */           max = 60; break;
/*  884 */         case 1: max = 60; break;
/*  885 */         case 2: max = 24; break;
/*  886 */         case 4: max = 12; break;
/*  887 */         case 5: max = 7; break;
/*  888 */         case 3: max = 31; break;
/*  889 */         case 6: throw new IllegalArgumentException("Start year must be less than stop year");
/*  890 */         default: throw new IllegalArgumentException("Unexpected type encountered"); }
/*      */       
/*  892 */       stopAt += max;
/*      */     } 
/*      */     int i;
/*  895 */     for (i = startAt; i <= stopAt; i += incr) {
/*  896 */       if (max == -1) {
/*      */         
/*  898 */         set.add(Integer.valueOf(i));
/*      */       } else {
/*      */         
/*  901 */         int i2 = i % max;
/*      */ 
/*      */         
/*  904 */         if (i2 == 0 && (type == 4 || type == 5 || type == 3)) {
/*  905 */           i2 = max;
/*      */         }
/*      */         
/*  908 */         set.add(Integer.valueOf(i2));
/*      */       } 
/*      */     } 
/*      */   }
/*      */   
/*      */   TreeSet<Integer> getSet(int type) {
/*  914 */     switch (type) {
/*      */       case 0:
/*  916 */         return this.seconds;
/*      */       case 1:
/*  918 */         return this.minutes;
/*      */       case 2:
/*  920 */         return this.hours;
/*      */       case 3:
/*  922 */         return this.daysOfMonth;
/*      */       case 4:
/*  924 */         return this.months;
/*      */       case 5:
/*  926 */         return this.daysOfWeek;
/*      */       case 6:
/*  928 */         return this.years;
/*      */     } 
/*  930 */     return null;
/*      */   }
/*      */ 
/*      */   
/*      */   protected ValueSet getValue(int v, String s, int i) {
/*  935 */     char c = s.charAt(i);
/*  936 */     StringBuilder s1 = new StringBuilder(String.valueOf(v));
/*  937 */     while (c >= '0' && c <= '9') {
/*  938 */       s1.append(c);
/*  939 */       i++;
/*  940 */       if (i >= s.length()) {
/*      */         break;
/*      */       }
/*  943 */       c = s.charAt(i);
/*      */     } 
/*  945 */     ValueSet val = new ValueSet();
/*      */     
/*  947 */     val.pos = (i < s.length()) ? i : (i + 1);
/*  948 */     val.value = Integer.parseInt(s1.toString());
/*  949 */     return val;
/*      */   }
/*      */   
/*      */   protected int getNumericValue(String s, int i) {
/*  953 */     int endOfVal = findNextWhiteSpace(i, s);
/*  954 */     String val = s.substring(i, endOfVal);
/*  955 */     return Integer.parseInt(val);
/*      */   }
/*      */   
/*      */   protected int getMonthNumber(String s) {
/*  959 */     Integer integer = monthMap.get(s);
/*      */     
/*  961 */     if (integer == null) {
/*  962 */       return -1;
/*      */     }
/*      */     
/*  965 */     return integer.intValue();
/*      */   }
/*      */   
/*      */   protected int getDayOfWeekNumber(String s) {
/*  969 */     Integer integer = dayMap.get(s);
/*      */     
/*  971 */     if (integer == null) {
/*  972 */       return -1;
/*      */     }
/*      */     
/*  975 */     return integer.intValue();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Date getTimeAfter(Date afterTime) {
/*  987 */     Calendar cl = new GregorianCalendar(getTimeZone());
/*      */ 
/*      */ 
/*      */     
/*  991 */     afterTime = new Date(afterTime.getTime() + 1000L);
/*      */     
/*  993 */     cl.setTime(afterTime);
/*  994 */     cl.set(14, 0);
/*      */     
/*  996 */     boolean gotOne = false;
/*      */     
/*  998 */     while (!gotOne) {
/*      */ 
/*      */       
/* 1001 */       if (cl.get(1) > 2999) {
/* 1002 */         return null;
/*      */       }
/*      */       
/* 1005 */       SortedSet<Integer> st = null;
/* 1006 */       int t = 0;
/*      */       
/* 1008 */       int sec = cl.get(13);
/* 1009 */       int min = cl.get(12);
/*      */ 
/*      */       
/* 1012 */       st = this.seconds.tailSet(Integer.valueOf(sec));
/* 1013 */       if (st != null && st.size() != 0) {
/* 1014 */         sec = ((Integer)st.first()).intValue();
/*      */       } else {
/* 1016 */         sec = ((Integer)this.seconds.first()).intValue();
/* 1017 */         min++;
/* 1018 */         cl.set(12, min);
/*      */       } 
/* 1020 */       cl.set(13, sec);
/*      */       
/* 1022 */       min = cl.get(12);
/* 1023 */       int hr = cl.get(11);
/* 1024 */       t = -1;
/*      */ 
/*      */       
/* 1027 */       st = this.minutes.tailSet(Integer.valueOf(min));
/* 1028 */       if (st != null && st.size() != 0) {
/* 1029 */         t = min;
/* 1030 */         min = ((Integer)st.first()).intValue();
/*      */       } else {
/* 1032 */         min = ((Integer)this.minutes.first()).intValue();
/* 1033 */         hr++;
/*      */       } 
/* 1035 */       if (min != t) {
/* 1036 */         cl.set(13, 0);
/* 1037 */         cl.set(12, min);
/* 1038 */         setCalendarHour(cl, hr);
/*      */         continue;
/*      */       } 
/* 1041 */       cl.set(12, min);
/*      */       
/* 1043 */       hr = cl.get(11);
/* 1044 */       int day = cl.get(5);
/* 1045 */       t = -1;
/*      */ 
/*      */       
/* 1048 */       st = this.hours.tailSet(Integer.valueOf(hr));
/* 1049 */       if (st != null && st.size() != 0) {
/* 1050 */         t = hr;
/* 1051 */         hr = ((Integer)st.first()).intValue();
/*      */       } else {
/* 1053 */         hr = ((Integer)this.hours.first()).intValue();
/* 1054 */         day++;
/*      */       } 
/* 1056 */       if (hr != t) {
/* 1057 */         cl.set(13, 0);
/* 1058 */         cl.set(12, 0);
/* 1059 */         cl.set(5, day);
/* 1060 */         setCalendarHour(cl, hr);
/*      */         continue;
/*      */       } 
/* 1063 */       cl.set(11, hr);
/*      */       
/* 1065 */       day = cl.get(5);
/* 1066 */       int mon = cl.get(2) + 1;
/*      */ 
/*      */       
/* 1069 */       t = -1;
/* 1070 */       int tmon = mon;
/*      */ 
/*      */       
/* 1073 */       boolean dayOfMSpec = !this.daysOfMonth.contains(NO_SPEC);
/* 1074 */       boolean dayOfWSpec = !this.daysOfWeek.contains(NO_SPEC);
/* 1075 */       if (dayOfMSpec && !dayOfWSpec) {
/* 1076 */         st = this.daysOfMonth.tailSet(Integer.valueOf(day));
/* 1077 */         if (this.lastdayOfMonth) {
/* 1078 */           if (!this.nearestWeekday) {
/* 1079 */             t = day;
/* 1080 */             day = getLastDayOfMonth(mon, cl.get(1));
/* 1081 */             day -= this.lastdayOffset;
/* 1082 */             if (t > day) {
/* 1083 */               mon++;
/* 1084 */               if (mon > 12) {
/* 1085 */                 mon = 1;
/* 1086 */                 tmon = 3333;
/* 1087 */                 cl.add(1, 1);
/*      */               } 
/* 1089 */               day = 1;
/*      */             } 
/*      */           } else {
/* 1092 */             t = day;
/* 1093 */             day = getLastDayOfMonth(mon, cl.get(1));
/* 1094 */             day -= this.lastdayOffset;
/*      */             
/* 1096 */             Calendar tcal = Calendar.getInstance(getTimeZone());
/* 1097 */             tcal.set(13, 0);
/* 1098 */             tcal.set(12, 0);
/* 1099 */             tcal.set(11, 0);
/* 1100 */             tcal.set(5, day);
/* 1101 */             tcal.set(2, mon - 1);
/* 1102 */             tcal.set(1, cl.get(1));
/*      */             
/* 1104 */             int ldom = getLastDayOfMonth(mon, cl.get(1));
/* 1105 */             int dow = tcal.get(7);
/*      */             
/* 1107 */             if (dow == 7 && day == 1) {
/* 1108 */               day += 2;
/* 1109 */             } else if (dow == 7) {
/* 1110 */               day--;
/* 1111 */             } else if (dow == 1 && day == ldom) {
/* 1112 */               day -= 2;
/* 1113 */             } else if (dow == 1) {
/* 1114 */               day++;
/*      */             } 
/*      */             
/* 1117 */             tcal.set(13, sec);
/* 1118 */             tcal.set(12, min);
/* 1119 */             tcal.set(11, hr);
/* 1120 */             tcal.set(5, day);
/* 1121 */             tcal.set(2, mon - 1);
/* 1122 */             Date nTime = tcal.getTime();
/* 1123 */             if (nTime.before(afterTime)) {
/* 1124 */               day = 1;
/* 1125 */               mon++;
/*      */             } 
/*      */           } 
/* 1128 */         } else if (this.nearestWeekday) {
/* 1129 */           t = day;
/* 1130 */           day = ((Integer)this.daysOfMonth.first()).intValue();
/*      */           
/* 1132 */           Calendar tcal = Calendar.getInstance(getTimeZone());
/* 1133 */           tcal.set(13, 0);
/* 1134 */           tcal.set(12, 0);
/* 1135 */           tcal.set(11, 0);
/* 1136 */           tcal.set(5, day);
/* 1137 */           tcal.set(2, mon - 1);
/* 1138 */           tcal.set(1, cl.get(1));
/*      */           
/* 1140 */           int ldom = getLastDayOfMonth(mon, cl.get(1));
/* 1141 */           int dow = tcal.get(7);
/*      */           
/* 1143 */           if (dow == 7 && day == 1) {
/* 1144 */             day += 2;
/* 1145 */           } else if (dow == 7) {
/* 1146 */             day--;
/* 1147 */           } else if (dow == 1 && day == ldom) {
/* 1148 */             day -= 2;
/* 1149 */           } else if (dow == 1) {
/* 1150 */             day++;
/*      */           } 
/*      */ 
/*      */           
/* 1154 */           tcal.set(13, sec);
/* 1155 */           tcal.set(12, min);
/* 1156 */           tcal.set(11, hr);
/* 1157 */           tcal.set(5, day);
/* 1158 */           tcal.set(2, mon - 1);
/* 1159 */           Date nTime = tcal.getTime();
/* 1160 */           if (nTime.before(afterTime)) {
/* 1161 */             day = ((Integer)this.daysOfMonth.first()).intValue();
/* 1162 */             mon++;
/*      */           } 
/* 1164 */         } else if (st != null && st.size() != 0) {
/* 1165 */           t = day;
/* 1166 */           day = ((Integer)st.first()).intValue();
/*      */           
/* 1168 */           int lastDay = getLastDayOfMonth(mon, cl.get(1));
/* 1169 */           if (day > lastDay) {
/* 1170 */             day = ((Integer)this.daysOfMonth.first()).intValue();
/* 1171 */             mon++;
/*      */           } 
/*      */         } else {
/* 1174 */           day = ((Integer)this.daysOfMonth.first()).intValue();
/* 1175 */           mon++;
/*      */         } 
/*      */         
/* 1178 */         if (day != t || mon != tmon) {
/* 1179 */           cl.set(13, 0);
/* 1180 */           cl.set(12, 0);
/* 1181 */           cl.set(11, 0);
/* 1182 */           cl.set(5, day);
/* 1183 */           cl.set(2, mon - 1);
/*      */ 
/*      */           
/*      */           continue;
/*      */         } 
/* 1188 */       } else if (dayOfWSpec && !dayOfMSpec) {
/* 1189 */         if (this.lastdayOfWeek) {
/*      */           
/* 1191 */           int dow = ((Integer)this.daysOfWeek.first()).intValue();
/*      */           
/* 1193 */           int cDow = cl.get(7);
/* 1194 */           int daysToAdd = 0;
/* 1195 */           if (cDow < dow) {
/* 1196 */             daysToAdd = dow - cDow;
/*      */           }
/* 1198 */           if (cDow > dow) {
/* 1199 */             daysToAdd = dow + 7 - cDow;
/*      */           }
/*      */           
/* 1202 */           int lDay = getLastDayOfMonth(mon, cl.get(1));
/*      */           
/* 1204 */           if (day + daysToAdd > lDay) {
/*      */             
/* 1206 */             cl.set(13, 0);
/* 1207 */             cl.set(12, 0);
/* 1208 */             cl.set(11, 0);
/* 1209 */             cl.set(5, 1);
/* 1210 */             cl.set(2, mon);
/*      */ 
/*      */             
/*      */             continue;
/*      */           } 
/*      */           
/* 1216 */           while (day + daysToAdd + 7 <= lDay) {
/* 1217 */             daysToAdd += 7;
/*      */           }
/*      */           
/* 1220 */           day += daysToAdd;
/*      */           
/* 1222 */           if (daysToAdd > 0) {
/* 1223 */             cl.set(13, 0);
/* 1224 */             cl.set(12, 0);
/* 1225 */             cl.set(11, 0);
/* 1226 */             cl.set(5, day);
/* 1227 */             cl.set(2, mon - 1);
/*      */ 
/*      */             
/*      */             continue;
/*      */           } 
/* 1232 */         } else if (this.nthdayOfWeek != 0) {
/*      */           
/* 1234 */           int dow = ((Integer)this.daysOfWeek.first()).intValue();
/*      */           
/* 1236 */           int cDow = cl.get(7);
/* 1237 */           int daysToAdd = 0;
/* 1238 */           if (cDow < dow) {
/* 1239 */             daysToAdd = dow - cDow;
/* 1240 */           } else if (cDow > dow) {
/* 1241 */             daysToAdd = dow + 7 - cDow;
/*      */           } 
/*      */           
/* 1244 */           boolean dayShifted = false;
/* 1245 */           if (daysToAdd > 0) {
/* 1246 */             dayShifted = true;
/*      */           }
/*      */           
/* 1249 */           day += daysToAdd;
/* 1250 */           int weekOfMonth = day / 7;
/* 1251 */           if (day % 7 > 0) {
/* 1252 */             weekOfMonth++;
/*      */           }
/*      */           
/* 1255 */           daysToAdd = (this.nthdayOfWeek - weekOfMonth) * 7;
/* 1256 */           day += daysToAdd;
/* 1257 */           if (daysToAdd < 0 || day > 
/* 1258 */             getLastDayOfMonth(mon, cl
/* 1259 */               .get(1))) {
/* 1260 */             cl.set(13, 0);
/* 1261 */             cl.set(12, 0);
/* 1262 */             cl.set(11, 0);
/* 1263 */             cl.set(5, 1);
/* 1264 */             cl.set(2, mon);
/*      */             continue;
/*      */           } 
/* 1267 */           if (daysToAdd > 0 || dayShifted) {
/* 1268 */             cl.set(13, 0);
/* 1269 */             cl.set(12, 0);
/* 1270 */             cl.set(11, 0);
/* 1271 */             cl.set(5, day);
/* 1272 */             cl.set(2, mon - 1);
/*      */             
/*      */             continue;
/*      */           } 
/*      */         } else {
/* 1277 */           int cDow = cl.get(7);
/* 1278 */           int dow = ((Integer)this.daysOfWeek.first()).intValue();
/*      */           
/* 1280 */           st = this.daysOfWeek.tailSet(Integer.valueOf(cDow));
/* 1281 */           if (st != null && st.size() > 0) {
/* 1282 */             dow = ((Integer)st.first()).intValue();
/*      */           }
/*      */           
/* 1285 */           int daysToAdd = 0;
/* 1286 */           if (cDow < dow) {
/* 1287 */             daysToAdd = dow - cDow;
/*      */           }
/* 1289 */           if (cDow > dow) {
/* 1290 */             daysToAdd = dow + 7 - cDow;
/*      */           }
/*      */           
/* 1293 */           int lDay = getLastDayOfMonth(mon, cl.get(1));
/*      */           
/* 1295 */           if (day + daysToAdd > lDay) {
/*      */             
/* 1297 */             cl.set(13, 0);
/* 1298 */             cl.set(12, 0);
/* 1299 */             cl.set(11, 0);
/* 1300 */             cl.set(5, 1);
/* 1301 */             cl.set(2, mon);
/*      */             continue;
/*      */           } 
/* 1304 */           if (daysToAdd > 0) {
/* 1305 */             cl.set(13, 0);
/* 1306 */             cl.set(12, 0);
/* 1307 */             cl.set(11, 0);
/* 1308 */             cl.set(5, day + daysToAdd);
/* 1309 */             cl.set(2, mon - 1);
/*      */ 
/*      */             
/*      */             continue;
/*      */           } 
/*      */         } 
/*      */       } else {
/* 1316 */         throw new UnsupportedOperationException("Support for specifying both a day-of-week AND a day-of-month parameter is not implemented.");
/*      */       } 
/*      */       
/* 1319 */       cl.set(5, day);
/*      */       
/* 1321 */       mon = cl.get(2) + 1;
/*      */ 
/*      */       
/* 1324 */       int year = cl.get(1);
/* 1325 */       t = -1;
/*      */ 
/*      */ 
/*      */       
/* 1329 */       if (year > MAX_YEAR) {
/* 1330 */         return null;
/*      */       }
/*      */ 
/*      */       
/* 1334 */       st = this.months.tailSet(Integer.valueOf(mon));
/* 1335 */       if (st != null && st.size() != 0) {
/* 1336 */         t = mon;
/* 1337 */         mon = ((Integer)st.first()).intValue();
/*      */       } else {
/* 1339 */         mon = ((Integer)this.months.first()).intValue();
/* 1340 */         year++;
/*      */       } 
/* 1342 */       if (mon != t) {
/* 1343 */         cl.set(13, 0);
/* 1344 */         cl.set(12, 0);
/* 1345 */         cl.set(11, 0);
/* 1346 */         cl.set(5, 1);
/* 1347 */         cl.set(2, mon - 1);
/*      */ 
/*      */         
/* 1350 */         cl.set(1, year);
/*      */         continue;
/*      */       } 
/* 1353 */       cl.set(2, mon - 1);
/*      */ 
/*      */ 
/*      */       
/* 1357 */       year = cl.get(1);
/* 1358 */       t = -1;
/*      */ 
/*      */       
/* 1361 */       st = this.years.tailSet(Integer.valueOf(year));
/* 1362 */       if (st != null && st.size() != 0) {
/* 1363 */         t = year;
/* 1364 */         year = ((Integer)st.first()).intValue();
/*      */       } else {
/* 1366 */         return null;
/*      */       } 
/*      */       
/* 1369 */       if (year != t) {
/* 1370 */         cl.set(13, 0);
/* 1371 */         cl.set(12, 0);
/* 1372 */         cl.set(11, 0);
/* 1373 */         cl.set(5, 1);
/* 1374 */         cl.set(2, 0);
/*      */ 
/*      */         
/* 1377 */         cl.set(1, year);
/*      */         continue;
/*      */       } 
/* 1380 */       cl.set(1, year);
/*      */       
/* 1382 */       gotOne = true;
/*      */     } 
/*      */     
/* 1385 */     return cl.getTime();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected void setCalendarHour(Calendar cal, int hour) {
/* 1396 */     cal.set(11, hour);
/* 1397 */     if (cal.get(11) != hour && hour != 24) {
/* 1398 */       cal.set(11, hour + 1);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Date getTimeBefore(Date endTime) {
/* 1408 */     return null;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Date getFinalFireTime() {
/* 1417 */     return null;
/*      */   }
/*      */   
/*      */   protected boolean isLeapYear(int year) {
/* 1421 */     return ((year % 4 == 0 && year % 100 != 0) || year % 400 == 0);
/*      */   }
/*      */ 
/*      */   
/*      */   protected int getLastDayOfMonth(int monthNum, int year) {
/* 1426 */     switch (monthNum) {
/*      */       case 1:
/* 1428 */         return 31;
/*      */       case 2:
/* 1430 */         return isLeapYear(year) ? 29 : 28;
/*      */       case 3:
/* 1432 */         return 31;
/*      */       case 4:
/* 1434 */         return 30;
/*      */       case 5:
/* 1436 */         return 31;
/*      */       case 6:
/* 1438 */         return 30;
/*      */       case 7:
/* 1440 */         return 31;
/*      */       case 8:
/* 1442 */         return 31;
/*      */       case 9:
/* 1444 */         return 30;
/*      */       case 10:
/* 1446 */         return 31;
/*      */       case 11:
/* 1448 */         return 30;
/*      */       case 12:
/* 1450 */         return 31;
/*      */     } 
/* 1452 */     throw new IllegalArgumentException("Illegal month number: " + monthNum);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
/* 1461 */     stream.defaultReadObject();
/*      */     try {
/* 1463 */       buildExpression(this.cronExpression);
/* 1464 */     } catch (Exception exception) {}
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public Object clone() {
/* 1471 */     return new CronExpression(this);
/*      */   }
/*      */   
/*      */   static class ValueSet {
/*      */     public int value;
/*      */     public int pos;
/*      */   }
/*      */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\solon\schedule\cron\CronExpression.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */