/*     */ package ch.qos.logback.classic;
/*     */ 
/*     */ import ch.qos.logback.classic.spi.ILoggingEvent;
/*     */ import ch.qos.logback.classic.spi.LoggingEvent;
/*     */ import ch.qos.logback.classic.util.LoggerNameUtil;
/*     */ import ch.qos.logback.core.Appender;
/*     */ import ch.qos.logback.core.spi.AppenderAttachable;
/*     */ import ch.qos.logback.core.spi.AppenderAttachableImpl;
/*     */ import ch.qos.logback.core.spi.FilterReply;
/*     */ import java.io.ObjectStreamException;
/*     */ import java.io.Serializable;
/*     */ import java.util.Collections;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.concurrent.CopyOnWriteArrayList;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ import org.slf4j.Marker;
/*     */ import org.slf4j.event.LoggingEvent;
/*     */ import org.slf4j.spi.LocationAwareLogger;
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
/*     */ public final class Logger
/*     */   implements Logger, LocationAwareLogger, AppenderAttachable<ILoggingEvent>, Serializable
/*     */ {
/*     */   private static final long serialVersionUID = 5454405123156820674L;
/*  44 */   public static final String FQCN = Logger.class.getName();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private String name;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private transient Level level;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private transient int effectiveLevelInt;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private transient Logger parent;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private transient List<Logger> childrenList;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private transient AppenderAttachableImpl<ILoggingEvent> aai;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private transient boolean additive = true;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   final transient LoggerContext loggerContext;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   Logger(String name, Logger parent, LoggerContext loggerContext) {
/* 103 */     this.name = name;
/* 104 */     this.parent = parent;
/* 105 */     this.loggerContext = loggerContext;
/*     */   }
/*     */   
/*     */   public Level getEffectiveLevel() {
/* 109 */     return Level.toLevel(this.effectiveLevelInt);
/*     */   }
/*     */   
/*     */   int getEffectiveLevelInt() {
/* 113 */     return this.effectiveLevelInt;
/*     */   }
/*     */   
/*     */   public Level getLevel() {
/* 117 */     return this.level;
/*     */   }
/*     */   
/*     */   public String getName() {
/* 121 */     return this.name;
/*     */   }
/*     */ 
/*     */   
/*     */   private boolean isRootLogger() {
/* 126 */     return (this.parent == null);
/*     */   }
/*     */   
/*     */   Logger getChildByName(String childName) {
/* 130 */     if (this.childrenList == null) {
/* 131 */       return null;
/*     */     }
/* 133 */     int len = this.childrenList.size();
/* 134 */     for (int i = 0; i < len; i++) {
/* 135 */       Logger childLogger_i = this.childrenList.get(i);
/* 136 */       String childName_i = childLogger_i.getName();
/*     */       
/* 138 */       if (childName.equals(childName_i)) {
/* 139 */         return childLogger_i;
/*     */       }
/*     */     } 
/*     */     
/* 143 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   public synchronized void setLevel(Level newLevel) {
/* 148 */     if (this.level == newLevel) {
/*     */       return;
/*     */     }
/*     */     
/* 152 */     if (newLevel == null && isRootLogger()) {
/* 153 */       throw new IllegalArgumentException("The level of the root logger cannot be set to null");
/*     */     }
/*     */     
/* 156 */     this.level = newLevel;
/* 157 */     if (newLevel == null) {
/* 158 */       this.effectiveLevelInt = this.parent.effectiveLevelInt;
/* 159 */       newLevel = this.parent.getEffectiveLevel();
/*     */     } else {
/* 161 */       this.effectiveLevelInt = newLevel.levelInt;
/*     */     } 
/*     */     
/* 164 */     if (this.childrenList != null) {
/* 165 */       int len = this.childrenList.size();
/* 166 */       for (int i = 0; i < len; i++) {
/* 167 */         Logger child = this.childrenList.get(i);
/*     */         
/* 169 */         child.handleParentLevelChange(this.effectiveLevelInt);
/*     */       } 
/*     */     } 
/*     */     
/* 173 */     this.loggerContext.fireOnLevelChange(this, newLevel);
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
/*     */   private synchronized void handleParentLevelChange(int newParentLevelInt) {
/* 185 */     if (this.level == null) {
/* 186 */       this.effectiveLevelInt = newParentLevelInt;
/*     */ 
/*     */       
/* 189 */       if (this.childrenList != null) {
/* 190 */         int len = this.childrenList.size();
/* 191 */         for (int i = 0; i < len; i++) {
/* 192 */           Logger child = this.childrenList.get(i);
/* 193 */           child.handleParentLevelChange(newParentLevelInt);
/*     */         } 
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void detachAndStopAllAppenders() {
/* 205 */     if (this.aai != null) {
/* 206 */       this.aai.detachAndStopAllAppenders();
/*     */     }
/*     */   }
/*     */   
/*     */   public boolean detachAppender(String name) {
/* 211 */     if (this.aai == null) {
/* 212 */       return false;
/*     */     }
/* 214 */     return this.aai.detachAppender(name);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public synchronized void addAppender(Appender<ILoggingEvent> newAppender) {
/* 220 */     if (this.aai == null) {
/* 221 */       this.aai = new AppenderAttachableImpl();
/*     */     }
/* 223 */     this.aai.addAppender(newAppender);
/*     */   }
/*     */   
/*     */   public boolean isAttached(Appender<ILoggingEvent> appender) {
/* 227 */     if (this.aai == null) {
/* 228 */       return false;
/*     */     }
/* 230 */     return this.aai.isAttached(appender);
/*     */   }
/*     */ 
/*     */   
/*     */   public Iterator<Appender<ILoggingEvent>> iteratorForAppenders() {
/* 235 */     if (this.aai == null) {
/* 236 */       return Collections.EMPTY_LIST.iterator();
/*     */     }
/* 238 */     return this.aai.iteratorForAppenders();
/*     */   }
/*     */   
/*     */   public Appender<ILoggingEvent> getAppender(String name) {
/* 242 */     if (this.aai == null) {
/* 243 */       return null;
/*     */     }
/* 245 */     return this.aai.getAppender(name);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void callAppenders(ILoggingEvent event) {
/* 255 */     int writes = 0;
/* 256 */     for (Logger l = this; l != null; l = l.parent) {
/* 257 */       writes += l.appendLoopOnAppenders(event);
/* 258 */       if (!l.additive) {
/*     */         break;
/*     */       }
/*     */     } 
/*     */     
/* 263 */     if (writes == 0) {
/* 264 */       this.loggerContext.noAppenderDefinedWarning(this);
/*     */     }
/*     */   }
/*     */   
/*     */   private int appendLoopOnAppenders(ILoggingEvent event) {
/* 269 */     if (this.aai != null) {
/* 270 */       return this.aai.appendLoopOnAppenders(event);
/*     */     }
/* 272 */     return 0;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean detachAppender(Appender<ILoggingEvent> appender) {
/* 280 */     if (this.aai == null) {
/* 281 */       return false;
/*     */     }
/* 283 */     return this.aai.detachAppender(appender);
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
/*     */ 
/*     */ 
/*     */   
/*     */   Logger createChildByLastNamePart(String lastPart) {
/*     */     Logger childLogger;
/* 302 */     int i_index = LoggerNameUtil.getFirstSeparatorIndexOf(lastPart);
/* 303 */     if (i_index != -1) {
/* 304 */       throw new IllegalArgumentException("Child name [" + lastPart + " passed as parameter, may not include [" + '.' + "]");
/*     */     }
/*     */     
/* 307 */     if (this.childrenList == null) {
/* 308 */       this.childrenList = new CopyOnWriteArrayList<Logger>();
/*     */     }
/*     */     
/* 311 */     if (isRootLogger()) {
/* 312 */       childLogger = new Logger(lastPart, this, this.loggerContext);
/*     */     } else {
/* 314 */       childLogger = new Logger(this.name + '.' + lastPart, this, this.loggerContext);
/*     */     } 
/* 316 */     this.childrenList.add(childLogger);
/* 317 */     childLogger.effectiveLevelInt = this.effectiveLevelInt;
/* 318 */     return childLogger;
/*     */   }
/*     */   
/*     */   private void localLevelReset() {
/* 322 */     this.effectiveLevelInt = 10000;
/* 323 */     if (isRootLogger()) {
/* 324 */       this.level = Level.DEBUG;
/*     */     } else {
/* 326 */       this.level = null;
/*     */     } 
/*     */   }
/*     */   
/*     */   void recursiveReset() {
/* 331 */     detachAndStopAllAppenders();
/* 332 */     localLevelReset();
/* 333 */     this.additive = true;
/* 334 */     if (this.childrenList == null) {
/*     */       return;
/*     */     }
/* 337 */     for (Logger childLogger : this.childrenList) {
/* 338 */       childLogger.recursiveReset();
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   Logger createChildByName(String childName) {
/* 348 */     int i_index = LoggerNameUtil.getSeparatorIndexOf(childName, this.name.length() + 1);
/* 349 */     if (i_index != -1) {
/* 350 */       throw new IllegalArgumentException("For logger [" + this.name + "] child name [" + childName + " passed as parameter, may not include '.' after index" + (this.name
/* 351 */           .length() + 1));
/*     */     }
/*     */     
/* 354 */     if (this.childrenList == null) {
/* 355 */       this.childrenList = new CopyOnWriteArrayList<Logger>();
/*     */     }
/*     */     
/* 358 */     Logger childLogger = new Logger(childName, this, this.loggerContext);
/* 359 */     this.childrenList.add(childLogger);
/* 360 */     childLogger.effectiveLevelInt = this.effectiveLevelInt;
/* 361 */     return childLogger;
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
/*     */   private void filterAndLog_0_Or3Plus(String localFQCN, Marker marker, Level level, String msg, Object[] params, Throwable t) {
/* 373 */     FilterReply decision = this.loggerContext.getTurboFilterChainDecision_0_3OrMore(marker, this, level, msg, params, t);
/*     */     
/* 375 */     if (decision == FilterReply.NEUTRAL) {
/* 376 */       if (this.effectiveLevelInt > level.levelInt) {
/*     */         return;
/*     */       }
/* 379 */     } else if (decision == FilterReply.DENY) {
/*     */       return;
/*     */     } 
/*     */     
/* 383 */     buildLoggingEventAndAppend(localFQCN, marker, level, msg, params, t);
/*     */   }
/*     */ 
/*     */   
/*     */   private void filterAndLog_1(String localFQCN, Marker marker, Level level, String msg, Object param, Throwable t) {
/* 388 */     FilterReply decision = this.loggerContext.getTurboFilterChainDecision_1(marker, this, level, msg, param, t);
/*     */     
/* 390 */     if (decision == FilterReply.NEUTRAL) {
/* 391 */       if (this.effectiveLevelInt > level.levelInt) {
/*     */         return;
/*     */       }
/* 394 */     } else if (decision == FilterReply.DENY) {
/*     */       return;
/*     */     } 
/*     */     
/* 398 */     buildLoggingEventAndAppend(localFQCN, marker, level, msg, new Object[] { param }, t);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private void filterAndLog_2(String localFQCN, Marker marker, Level level, String msg, Object param1, Object param2, Throwable t) {
/* 404 */     FilterReply decision = this.loggerContext.getTurboFilterChainDecision_2(marker, this, level, msg, param1, param2, t);
/*     */     
/* 406 */     if (decision == FilterReply.NEUTRAL) {
/* 407 */       if (this.effectiveLevelInt > level.levelInt) {
/*     */         return;
/*     */       }
/* 410 */     } else if (decision == FilterReply.DENY) {
/*     */       return;
/*     */     } 
/*     */     
/* 414 */     buildLoggingEventAndAppend(localFQCN, marker, level, msg, new Object[] { param1, param2 }, t);
/*     */   }
/*     */ 
/*     */   
/*     */   private void buildLoggingEventAndAppend(String localFQCN, Marker marker, Level level, String msg, Object[] params, Throwable t) {
/* 419 */     LoggingEvent le = new LoggingEvent(localFQCN, this, level, msg, t, params);
/* 420 */     le.setMarker(marker);
/* 421 */     callAppenders((ILoggingEvent)le);
/*     */   }
/*     */   
/*     */   public void trace(String msg) {
/* 425 */     filterAndLog_0_Or3Plus(FQCN, null, Level.TRACE, msg, null, null);
/*     */   }
/*     */   
/*     */   public void trace(String format, Object arg) {
/* 429 */     filterAndLog_1(FQCN, null, Level.TRACE, format, arg, null);
/*     */   }
/*     */   
/*     */   public void trace(String format, Object arg1, Object arg2) {
/* 433 */     filterAndLog_2(FQCN, null, Level.TRACE, format, arg1, arg2, null);
/*     */   }
/*     */   
/*     */   public void trace(String format, Object... argArray) {
/* 437 */     filterAndLog_0_Or3Plus(FQCN, null, Level.TRACE, format, argArray, null);
/*     */   }
/*     */   
/*     */   public void trace(String msg, Throwable t) {
/* 441 */     filterAndLog_0_Or3Plus(FQCN, null, Level.TRACE, msg, null, t);
/*     */   }
/*     */   
/*     */   public void trace(Marker marker, String msg) {
/* 445 */     filterAndLog_0_Or3Plus(FQCN, marker, Level.TRACE, msg, null, null);
/*     */   }
/*     */   
/*     */   public void trace(Marker marker, String format, Object arg) {
/* 449 */     filterAndLog_1(FQCN, marker, Level.TRACE, format, arg, null);
/*     */   }
/*     */   
/*     */   public void trace(Marker marker, String format, Object arg1, Object arg2) {
/* 453 */     filterAndLog_2(FQCN, marker, Level.TRACE, format, arg1, arg2, null);
/*     */   }
/*     */   
/*     */   public void trace(Marker marker, String format, Object... argArray) {
/* 457 */     filterAndLog_0_Or3Plus(FQCN, marker, Level.TRACE, format, argArray, null);
/*     */   }
/*     */   
/*     */   public void trace(Marker marker, String msg, Throwable t) {
/* 461 */     filterAndLog_0_Or3Plus(FQCN, marker, Level.TRACE, msg, null, t);
/*     */   }
/*     */   
/*     */   public boolean isDebugEnabled() {
/* 465 */     return isDebugEnabled(null);
/*     */   }
/*     */   
/*     */   public boolean isDebugEnabled(Marker marker) {
/* 469 */     FilterReply decision = callTurboFilters(marker, Level.DEBUG);
/* 470 */     if (decision == FilterReply.NEUTRAL)
/* 471 */       return (this.effectiveLevelInt <= 10000); 
/* 472 */     if (decision == FilterReply.DENY)
/* 473 */       return false; 
/* 474 */     if (decision == FilterReply.ACCEPT) {
/* 475 */       return true;
/*     */     }
/* 477 */     throw new IllegalStateException("Unknown FilterReply value: " + decision);
/*     */   }
/*     */ 
/*     */   
/*     */   public void debug(String msg) {
/* 482 */     filterAndLog_0_Or3Plus(FQCN, null, Level.DEBUG, msg, null, null);
/*     */   }
/*     */   
/*     */   public void debug(String format, Object arg) {
/* 486 */     filterAndLog_1(FQCN, null, Level.DEBUG, format, arg, null);
/*     */   }
/*     */   
/*     */   public void debug(String format, Object arg1, Object arg2) {
/* 490 */     filterAndLog_2(FQCN, null, Level.DEBUG, format, arg1, arg2, null);
/*     */   }
/*     */   
/*     */   public void debug(String format, Object... argArray) {
/* 494 */     filterAndLog_0_Or3Plus(FQCN, null, Level.DEBUG, format, argArray, null);
/*     */   }
/*     */   
/*     */   public void debug(String msg, Throwable t) {
/* 498 */     filterAndLog_0_Or3Plus(FQCN, null, Level.DEBUG, msg, null, t);
/*     */   }
/*     */   
/*     */   public void debug(Marker marker, String msg) {
/* 502 */     filterAndLog_0_Or3Plus(FQCN, marker, Level.DEBUG, msg, null, null);
/*     */   }
/*     */   
/*     */   public void debug(Marker marker, String format, Object arg) {
/* 506 */     filterAndLog_1(FQCN, marker, Level.DEBUG, format, arg, null);
/*     */   }
/*     */   
/*     */   public void debug(Marker marker, String format, Object arg1, Object arg2) {
/* 510 */     filterAndLog_2(FQCN, marker, Level.DEBUG, format, arg1, arg2, null);
/*     */   }
/*     */   
/*     */   public void debug(Marker marker, String format, Object... argArray) {
/* 514 */     filterAndLog_0_Or3Plus(FQCN, marker, Level.DEBUG, format, argArray, null);
/*     */   }
/*     */   
/*     */   public void debug(Marker marker, String msg, Throwable t) {
/* 518 */     filterAndLog_0_Or3Plus(FQCN, marker, Level.DEBUG, msg, null, t);
/*     */   }
/*     */   
/*     */   public void error(String msg) {
/* 522 */     filterAndLog_0_Or3Plus(FQCN, null, Level.ERROR, msg, null, null);
/*     */   }
/*     */   
/*     */   public void error(String format, Object arg) {
/* 526 */     filterAndLog_1(FQCN, null, Level.ERROR, format, arg, null);
/*     */   }
/*     */   
/*     */   public void error(String format, Object arg1, Object arg2) {
/* 530 */     filterAndLog_2(FQCN, null, Level.ERROR, format, arg1, arg2, null);
/*     */   }
/*     */   
/*     */   public void error(String format, Object... argArray) {
/* 534 */     filterAndLog_0_Or3Plus(FQCN, null, Level.ERROR, format, argArray, null);
/*     */   }
/*     */   
/*     */   public void error(String msg, Throwable t) {
/* 538 */     filterAndLog_0_Or3Plus(FQCN, null, Level.ERROR, msg, null, t);
/*     */   }
/*     */   
/*     */   public void error(Marker marker, String msg) {
/* 542 */     filterAndLog_0_Or3Plus(FQCN, marker, Level.ERROR, msg, null, null);
/*     */   }
/*     */   
/*     */   public void error(Marker marker, String format, Object arg) {
/* 546 */     filterAndLog_1(FQCN, marker, Level.ERROR, format, arg, null);
/*     */   }
/*     */   
/*     */   public void error(Marker marker, String format, Object arg1, Object arg2) {
/* 550 */     filterAndLog_2(FQCN, marker, Level.ERROR, format, arg1, arg2, null);
/*     */   }
/*     */   
/*     */   public void error(Marker marker, String format, Object... argArray) {
/* 554 */     filterAndLog_0_Or3Plus(FQCN, marker, Level.ERROR, format, argArray, null);
/*     */   }
/*     */   
/*     */   public void error(Marker marker, String msg, Throwable t) {
/* 558 */     filterAndLog_0_Or3Plus(FQCN, marker, Level.ERROR, msg, null, t);
/*     */   }
/*     */   
/*     */   public boolean isInfoEnabled() {
/* 562 */     return isInfoEnabled(null);
/*     */   }
/*     */   
/*     */   public boolean isInfoEnabled(Marker marker) {
/* 566 */     FilterReply decision = callTurboFilters(marker, Level.INFO);
/* 567 */     if (decision == FilterReply.NEUTRAL)
/* 568 */       return (this.effectiveLevelInt <= 20000); 
/* 569 */     if (decision == FilterReply.DENY)
/* 570 */       return false; 
/* 571 */     if (decision == FilterReply.ACCEPT) {
/* 572 */       return true;
/*     */     }
/* 574 */     throw new IllegalStateException("Unknown FilterReply value: " + decision);
/*     */   }
/*     */ 
/*     */   
/*     */   public void info(String msg) {
/* 579 */     filterAndLog_0_Or3Plus(FQCN, null, Level.INFO, msg, null, null);
/*     */   }
/*     */   
/*     */   public void info(String format, Object arg) {
/* 583 */     filterAndLog_1(FQCN, null, Level.INFO, format, arg, null);
/*     */   }
/*     */   
/*     */   public void info(String format, Object arg1, Object arg2) {
/* 587 */     filterAndLog_2(FQCN, null, Level.INFO, format, arg1, arg2, null);
/*     */   }
/*     */   
/*     */   public void info(String format, Object... argArray) {
/* 591 */     filterAndLog_0_Or3Plus(FQCN, null, Level.INFO, format, argArray, null);
/*     */   }
/*     */   
/*     */   public void info(String msg, Throwable t) {
/* 595 */     filterAndLog_0_Or3Plus(FQCN, null, Level.INFO, msg, null, t);
/*     */   }
/*     */   
/*     */   public void info(Marker marker, String msg) {
/* 599 */     filterAndLog_0_Or3Plus(FQCN, marker, Level.INFO, msg, null, null);
/*     */   }
/*     */   
/*     */   public void info(Marker marker, String format, Object arg) {
/* 603 */     filterAndLog_1(FQCN, marker, Level.INFO, format, arg, null);
/*     */   }
/*     */   
/*     */   public void info(Marker marker, String format, Object arg1, Object arg2) {
/* 607 */     filterAndLog_2(FQCN, marker, Level.INFO, format, arg1, arg2, null);
/*     */   }
/*     */   
/*     */   public void info(Marker marker, String format, Object... argArray) {
/* 611 */     filterAndLog_0_Or3Plus(FQCN, marker, Level.INFO, format, argArray, null);
/*     */   }
/*     */   
/*     */   public void info(Marker marker, String msg, Throwable t) {
/* 615 */     filterAndLog_0_Or3Plus(FQCN, marker, Level.INFO, msg, null, t);
/*     */   }
/*     */   
/*     */   public boolean isTraceEnabled() {
/* 619 */     return isTraceEnabled(null);
/*     */   }
/*     */   
/*     */   public boolean isTraceEnabled(Marker marker) {
/* 623 */     FilterReply decision = callTurboFilters(marker, Level.TRACE);
/* 624 */     if (decision == FilterReply.NEUTRAL)
/* 625 */       return (this.effectiveLevelInt <= 5000); 
/* 626 */     if (decision == FilterReply.DENY)
/* 627 */       return false; 
/* 628 */     if (decision == FilterReply.ACCEPT) {
/* 629 */       return true;
/*     */     }
/* 631 */     throw new IllegalStateException("Unknown FilterReply value: " + decision);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isErrorEnabled() {
/* 636 */     return isErrorEnabled(null);
/*     */   }
/*     */   
/*     */   public boolean isErrorEnabled(Marker marker) {
/* 640 */     FilterReply decision = callTurboFilters(marker, Level.ERROR);
/* 641 */     if (decision == FilterReply.NEUTRAL)
/* 642 */       return (this.effectiveLevelInt <= 40000); 
/* 643 */     if (decision == FilterReply.DENY)
/* 644 */       return false; 
/* 645 */     if (decision == FilterReply.ACCEPT) {
/* 646 */       return true;
/*     */     }
/* 648 */     throw new IllegalStateException("Unknown FilterReply value: " + decision);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isWarnEnabled() {
/* 653 */     return isWarnEnabled(null);
/*     */   }
/*     */   
/*     */   public boolean isWarnEnabled(Marker marker) {
/* 657 */     FilterReply decision = callTurboFilters(marker, Level.WARN);
/* 658 */     if (decision == FilterReply.NEUTRAL)
/* 659 */       return (this.effectiveLevelInt <= 30000); 
/* 660 */     if (decision == FilterReply.DENY)
/* 661 */       return false; 
/* 662 */     if (decision == FilterReply.ACCEPT) {
/* 663 */       return true;
/*     */     }
/* 665 */     throw new IllegalStateException("Unknown FilterReply value: " + decision);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isEnabledFor(Marker marker, Level level) {
/* 671 */     FilterReply decision = callTurboFilters(marker, level);
/* 672 */     if (decision == FilterReply.NEUTRAL)
/* 673 */       return (this.effectiveLevelInt <= level.levelInt); 
/* 674 */     if (decision == FilterReply.DENY)
/* 675 */       return false; 
/* 676 */     if (decision == FilterReply.ACCEPT) {
/* 677 */       return true;
/*     */     }
/* 679 */     throw new IllegalStateException("Unknown FilterReply value: " + decision);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isEnabledFor(Level level) {
/* 684 */     return isEnabledFor(null, level);
/*     */   }
/*     */   
/*     */   public void warn(String msg) {
/* 688 */     filterAndLog_0_Or3Plus(FQCN, null, Level.WARN, msg, null, null);
/*     */   }
/*     */   
/*     */   public void warn(String msg, Throwable t) {
/* 692 */     filterAndLog_0_Or3Plus(FQCN, null, Level.WARN, msg, null, t);
/*     */   }
/*     */   
/*     */   public void warn(String format, Object arg) {
/* 696 */     filterAndLog_1(FQCN, null, Level.WARN, format, arg, null);
/*     */   }
/*     */   
/*     */   public void warn(String format, Object arg1, Object arg2) {
/* 700 */     filterAndLog_2(FQCN, null, Level.WARN, format, arg1, arg2, null);
/*     */   }
/*     */   
/*     */   public void warn(String format, Object... argArray) {
/* 704 */     filterAndLog_0_Or3Plus(FQCN, null, Level.WARN, format, argArray, null);
/*     */   }
/*     */   
/*     */   public void warn(Marker marker, String msg) {
/* 708 */     filterAndLog_0_Or3Plus(FQCN, marker, Level.WARN, msg, null, null);
/*     */   }
/*     */   
/*     */   public void warn(Marker marker, String format, Object arg) {
/* 712 */     filterAndLog_1(FQCN, marker, Level.WARN, format, arg, null);
/*     */   }
/*     */   
/*     */   public void warn(Marker marker, String format, Object... argArray) {
/* 716 */     filterAndLog_0_Or3Plus(FQCN, marker, Level.WARN, format, argArray, null);
/*     */   }
/*     */   
/*     */   public void warn(Marker marker, String format, Object arg1, Object arg2) {
/* 720 */     filterAndLog_2(FQCN, marker, Level.WARN, format, arg1, arg2, null);
/*     */   }
/*     */   
/*     */   public void warn(Marker marker, String msg, Throwable t) {
/* 724 */     filterAndLog_0_Or3Plus(FQCN, marker, Level.WARN, msg, null, t);
/*     */   }
/*     */   
/*     */   public boolean isAdditive() {
/* 728 */     return this.additive;
/*     */   }
/*     */   
/*     */   public void setAdditive(boolean additive) {
/* 732 */     this.additive = additive;
/*     */   }
/*     */   
/*     */   public String toString() {
/* 736 */     return "Logger[" + this.name + "]";
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
/*     */   private FilterReply callTurboFilters(Marker marker, Level level) {
/* 751 */     return this.loggerContext.getTurboFilterChainDecision_0_3OrMore(marker, this, level, null, null, null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public LoggerContext getLoggerContext() {
/* 760 */     return this.loggerContext;
/*     */   }
/*     */   
/*     */   public void log(Marker marker, String fqcn, int levelInt, String message, Object[] argArray, Throwable t) {
/* 764 */     Level level = Level.fromLocationAwareLoggerInteger(levelInt);
/* 765 */     filterAndLog_0_Or3Plus(fqcn, marker, level, message, argArray, t);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void log(LoggingEvent slf4jEvent) {
/* 774 */     Level level = Level.fromLocationAwareLoggerInteger(slf4jEvent.getLevel().toInt());
/* 775 */     filterAndLog_0_Or3Plus(FQCN, slf4jEvent.getMarker(), level, slf4jEvent.getMessage(), slf4jEvent.getArgumentArray(), slf4jEvent.getThrowable());
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
/*     */   protected Object readResolve() throws ObjectStreamException {
/* 787 */     return LoggerFactory.getLogger(getName());
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\ch\qos\logback\classic\Logger.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */