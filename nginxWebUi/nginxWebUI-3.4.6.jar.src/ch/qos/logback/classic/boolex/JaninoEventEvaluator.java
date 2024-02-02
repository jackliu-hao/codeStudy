/*     */ package ch.qos.logback.classic.boolex;
/*     */ 
/*     */ import ch.qos.logback.classic.Level;
/*     */ import ch.qos.logback.classic.spi.ILoggingEvent;
/*     */ import ch.qos.logback.classic.spi.IThrowableProxy;
/*     */ import ch.qos.logback.classic.spi.LoggerContextVO;
/*     */ import ch.qos.logback.classic.spi.ThrowableProxy;
/*     */ import ch.qos.logback.core.CoreConstants;
/*     */ import ch.qos.logback.core.boolex.JaninoEventEvaluatorBase;
/*     */ import ch.qos.logback.core.boolex.Matcher;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import org.slf4j.Marker;
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
/*     */ public class JaninoEventEvaluator
/*     */   extends JaninoEventEvaluatorBase<ILoggingEvent>
/*     */ {
/*     */   public static final String IMPORT_LEVEL = "import ch.qos.logback.classic.Level;\r\n";
/*  35 */   public static final List<String> DEFAULT_PARAM_NAME_LIST = new ArrayList<String>();
/*  36 */   public static final List<Class> DEFAULT_PARAM_TYPE_LIST = (List)new ArrayList<Class<?>>();
/*     */   
/*     */   static {
/*  39 */     DEFAULT_PARAM_NAME_LIST.add("DEBUG");
/*  40 */     DEFAULT_PARAM_NAME_LIST.add("INFO");
/*  41 */     DEFAULT_PARAM_NAME_LIST.add("WARN");
/*  42 */     DEFAULT_PARAM_NAME_LIST.add("ERROR");
/*     */     
/*  44 */     DEFAULT_PARAM_NAME_LIST.add("event");
/*  45 */     DEFAULT_PARAM_NAME_LIST.add("message");
/*     */     
/*  47 */     DEFAULT_PARAM_NAME_LIST.add("formattedMessage");
/*  48 */     DEFAULT_PARAM_NAME_LIST.add("logger");
/*  49 */     DEFAULT_PARAM_NAME_LIST.add("loggerContext");
/*  50 */     DEFAULT_PARAM_NAME_LIST.add("level");
/*  51 */     DEFAULT_PARAM_NAME_LIST.add("timeStamp");
/*  52 */     DEFAULT_PARAM_NAME_LIST.add("marker");
/*  53 */     DEFAULT_PARAM_NAME_LIST.add("mdc");
/*  54 */     DEFAULT_PARAM_NAME_LIST.add("throwableProxy");
/*  55 */     DEFAULT_PARAM_NAME_LIST.add("throwable");
/*     */     
/*  57 */     DEFAULT_PARAM_TYPE_LIST.add(int.class);
/*  58 */     DEFAULT_PARAM_TYPE_LIST.add(int.class);
/*  59 */     DEFAULT_PARAM_TYPE_LIST.add(int.class);
/*  60 */     DEFAULT_PARAM_TYPE_LIST.add(int.class);
/*     */     
/*  62 */     DEFAULT_PARAM_TYPE_LIST.add(ILoggingEvent.class);
/*  63 */     DEFAULT_PARAM_TYPE_LIST.add(String.class);
/*  64 */     DEFAULT_PARAM_TYPE_LIST.add(String.class);
/*  65 */     DEFAULT_PARAM_TYPE_LIST.add(String.class);
/*  66 */     DEFAULT_PARAM_TYPE_LIST.add(LoggerContextVO.class);
/*  67 */     DEFAULT_PARAM_TYPE_LIST.add(int.class);
/*  68 */     DEFAULT_PARAM_TYPE_LIST.add(long.class);
/*  69 */     DEFAULT_PARAM_TYPE_LIST.add(Marker.class);
/*  70 */     DEFAULT_PARAM_TYPE_LIST.add(Map.class);
/*  71 */     DEFAULT_PARAM_TYPE_LIST.add(IThrowableProxy.class);
/*  72 */     DEFAULT_PARAM_TYPE_LIST.add(Throwable.class);
/*     */   }
/*     */   
/*     */   protected String getDecoratedExpression() {
/*  76 */     String expression = getExpression();
/*  77 */     if (!expression.contains("return")) {
/*  78 */       expression = "return " + expression + ";";
/*  79 */       addInfo("Adding [return] prefix and a semicolon suffix. Expression becomes [" + expression + "]");
/*  80 */       addInfo("See also http://logback.qos.ch/codes.html#block");
/*     */     } 
/*     */     
/*  83 */     return "import ch.qos.logback.classic.Level;\r\n" + expression;
/*     */   }
/*     */   
/*     */   protected String[] getParameterNames() {
/*  87 */     List<String> fullNameList = new ArrayList<String>();
/*  88 */     fullNameList.addAll(DEFAULT_PARAM_NAME_LIST);
/*     */     
/*  90 */     for (int i = 0; i < this.matcherList.size(); i++) {
/*  91 */       Matcher m = this.matcherList.get(i);
/*  92 */       fullNameList.add(m.getName());
/*     */     } 
/*     */     
/*  95 */     return fullNameList.<String>toArray(CoreConstants.EMPTY_STRING_ARRAY);
/*     */   }
/*     */   
/*     */   protected Class[] getParameterTypes() {
/*  99 */     List<Class<?>> fullTypeList = new ArrayList<Class<?>>();
/* 100 */     fullTypeList.addAll((Collection)DEFAULT_PARAM_TYPE_LIST);
/* 101 */     for (int i = 0; i < this.matcherList.size(); i++) {
/* 102 */       fullTypeList.add(Matcher.class);
/*     */     }
/* 104 */     return (Class[])fullTypeList.<Class<?>[]>toArray((Class<?>[][])CoreConstants.EMPTY_CLASS_ARRAY);
/*     */   }
/*     */   
/*     */   protected Object[] getParameterValues(ILoggingEvent loggingEvent) {
/* 108 */     int matcherListSize = this.matcherList.size();
/*     */     
/* 110 */     int i = 0;
/* 111 */     Object[] values = new Object[DEFAULT_PARAM_NAME_LIST.size() + matcherListSize];
/*     */     
/* 113 */     values[i++] = Level.DEBUG_INTEGER;
/* 114 */     values[i++] = Level.INFO_INTEGER;
/* 115 */     values[i++] = Level.WARN_INTEGER;
/* 116 */     values[i++] = Level.ERROR_INTEGER;
/*     */     
/* 118 */     values[i++] = loggingEvent;
/* 119 */     values[i++] = loggingEvent.getMessage();
/* 120 */     values[i++] = loggingEvent.getFormattedMessage();
/* 121 */     values[i++] = loggingEvent.getLoggerName();
/* 122 */     values[i++] = loggingEvent.getLoggerContextVO();
/* 123 */     values[i++] = loggingEvent.getLevel().toInteger();
/* 124 */     values[i++] = Long.valueOf(loggingEvent.getTimeStamp());
/*     */ 
/*     */ 
/*     */     
/* 128 */     values[i++] = loggingEvent.getMarker();
/* 129 */     values[i++] = loggingEvent.getMDCPropertyMap();
/*     */     
/* 131 */     IThrowableProxy iThrowableProxy = loggingEvent.getThrowableProxy();
/*     */     
/* 133 */     if (iThrowableProxy != null) {
/* 134 */       values[i++] = iThrowableProxy;
/* 135 */       if (iThrowableProxy instanceof ThrowableProxy) {
/* 136 */         values[i++] = ((ThrowableProxy)iThrowableProxy).getThrowable();
/*     */       } else {
/* 138 */         values[i++] = null;
/*     */       } 
/*     */     } else {
/* 141 */       values[i++] = null;
/* 142 */       values[i++] = null;
/*     */     } 
/*     */     
/* 145 */     for (int j = 0; j < matcherListSize; j++) {
/* 146 */       values[i++] = this.matcherList.get(j);
/*     */     }
/*     */     
/* 149 */     return values;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\ch\qos\logback\classic\boolex\JaninoEventEvaluator.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */