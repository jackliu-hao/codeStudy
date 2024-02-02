/*     */ package ch.qos.logback.classic.spi;
/*     */ 
/*     */ import ch.qos.logback.classic.Level;
/*     */ import java.io.IOException;
/*     */ import java.io.ObjectInputStream;
/*     */ import java.io.ObjectOutputStream;
/*     */ import java.io.Serializable;
/*     */ import java.util.Map;
/*     */ import org.slf4j.Marker;
/*     */ import org.slf4j.helpers.MessageFormatter;
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
/*     */ 
/*     */ public class LoggingEventVO
/*     */   implements ILoggingEvent, Serializable
/*     */ {
/*     */   private static final long serialVersionUID = 6553722650255690312L;
/*     */   private static final int NULL_ARGUMENT_ARRAY = -1;
/*     */   private static final String NULL_ARGUMENT_ARRAY_ELEMENT = "NULL_ARGUMENT_ARRAY_ELEMENT";
/*     */   private String threadName;
/*     */   private String loggerName;
/*     */   private LoggerContextVO loggerContextVO;
/*     */   private transient Level level;
/*     */   private String message;
/*     */   private transient String formattedMessage;
/*     */   private transient Object[] argumentArray;
/*     */   private ThrowableProxyVO throwableProxy;
/*     */   private StackTraceElement[] callerDataArray;
/*     */   private Marker marker;
/*     */   private Map<String, String> mdcPropertyMap;
/*     */   private long timeStamp;
/*     */   
/*     */   public static LoggingEventVO build(ILoggingEvent le) {
/*  63 */     LoggingEventVO ledo = new LoggingEventVO();
/*  64 */     ledo.loggerName = le.getLoggerName();
/*  65 */     ledo.loggerContextVO = le.getLoggerContextVO();
/*  66 */     ledo.threadName = le.getThreadName();
/*  67 */     ledo.level = le.getLevel();
/*  68 */     ledo.message = le.getMessage();
/*  69 */     ledo.argumentArray = le.getArgumentArray();
/*  70 */     ledo.marker = le.getMarker();
/*  71 */     ledo.mdcPropertyMap = le.getMDCPropertyMap();
/*  72 */     ledo.timeStamp = le.getTimeStamp();
/*  73 */     ledo.throwableProxy = ThrowableProxyVO.build(le.getThrowableProxy());
/*     */ 
/*     */     
/*  76 */     if (le.hasCallerData()) {
/*  77 */       ledo.callerDataArray = le.getCallerData();
/*     */     }
/*  79 */     return ledo;
/*     */   }
/*     */   
/*     */   public String getThreadName() {
/*  83 */     return this.threadName;
/*     */   }
/*     */   
/*     */   public LoggerContextVO getLoggerContextVO() {
/*  87 */     return this.loggerContextVO;
/*     */   }
/*     */   
/*     */   public String getLoggerName() {
/*  91 */     return this.loggerName;
/*     */   }
/*     */   
/*     */   public Level getLevel() {
/*  95 */     return this.level;
/*     */   }
/*     */   
/*     */   public String getMessage() {
/*  99 */     return this.message;
/*     */   }
/*     */   
/*     */   public String getFormattedMessage() {
/* 103 */     if (this.formattedMessage != null) {
/* 104 */       return this.formattedMessage;
/*     */     }
/*     */     
/* 107 */     if (this.argumentArray != null) {
/* 108 */       this.formattedMessage = MessageFormatter.arrayFormat(this.message, this.argumentArray).getMessage();
/*     */     } else {
/* 110 */       this.formattedMessage = this.message;
/*     */     } 
/*     */     
/* 113 */     return this.formattedMessage;
/*     */   }
/*     */   
/*     */   public Object[] getArgumentArray() {
/* 117 */     return this.argumentArray;
/*     */   }
/*     */   
/*     */   public IThrowableProxy getThrowableProxy() {
/* 121 */     return this.throwableProxy;
/*     */   }
/*     */   
/*     */   public StackTraceElement[] getCallerData() {
/* 125 */     return this.callerDataArray;
/*     */   }
/*     */   
/*     */   public boolean hasCallerData() {
/* 129 */     return (this.callerDataArray != null);
/*     */   }
/*     */   
/*     */   public Marker getMarker() {
/* 133 */     return this.marker;
/*     */   }
/*     */   
/*     */   public long getTimeStamp() {
/* 137 */     return this.timeStamp;
/*     */   }
/*     */   
/*     */   public long getContextBirthTime() {
/* 141 */     return this.loggerContextVO.getBirthTime();
/*     */   }
/*     */   
/*     */   public LoggerContextVO getContextLoggerRemoteView() {
/* 145 */     return this.loggerContextVO;
/*     */   }
/*     */   
/*     */   public Map<String, String> getMDCPropertyMap() {
/* 149 */     return this.mdcPropertyMap;
/*     */   }
/*     */   
/*     */   public Map<String, String> getMdc() {
/* 153 */     return this.mdcPropertyMap;
/*     */   }
/*     */ 
/*     */   
/*     */   public void prepareForDeferredProcessing() {}
/*     */   
/*     */   private void writeObject(ObjectOutputStream out) throws IOException {
/* 160 */     out.defaultWriteObject();
/* 161 */     out.writeInt(this.level.levelInt);
/* 162 */     if (this.argumentArray != null) {
/* 163 */       int len = this.argumentArray.length;
/* 164 */       out.writeInt(len);
/* 165 */       for (int i = 0; i < this.argumentArray.length; i++) {
/* 166 */         if (this.argumentArray[i] != null) {
/* 167 */           out.writeObject(this.argumentArray[i].toString());
/*     */         } else {
/* 169 */           out.writeObject("NULL_ARGUMENT_ARRAY_ELEMENT");
/*     */         } 
/*     */       } 
/*     */     } else {
/* 173 */       out.writeInt(-1);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
/* 179 */     in.defaultReadObject();
/* 180 */     int levelInt = in.readInt();
/* 181 */     this.level = Level.toLevel(levelInt);
/*     */     
/* 183 */     int argArrayLen = in.readInt();
/* 184 */     if (argArrayLen != -1) {
/* 185 */       this.argumentArray = (Object[])new String[argArrayLen];
/* 186 */       for (int i = 0; i < argArrayLen; i++) {
/* 187 */         Object val = in.readObject();
/* 188 */         if (!"NULL_ARGUMENT_ARRAY_ELEMENT".equals(val)) {
/* 189 */           this.argumentArray[i] = val;
/*     */         }
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 197 */     int prime = 31;
/* 198 */     int result = 1;
/* 199 */     result = 31 * result + ((this.message == null) ? 0 : this.message.hashCode());
/* 200 */     result = 31 * result + ((this.threadName == null) ? 0 : this.threadName.hashCode());
/* 201 */     result = 31 * result + (int)(this.timeStamp ^ this.timeStamp >>> 32L);
/* 202 */     return result;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean equals(Object obj) {
/* 207 */     if (this == obj)
/* 208 */       return true; 
/* 209 */     if (obj == null)
/* 210 */       return false; 
/* 211 */     if (getClass() != obj.getClass())
/* 212 */       return false; 
/* 213 */     LoggingEventVO other = (LoggingEventVO)obj;
/* 214 */     if (this.message == null) {
/* 215 */       if (other.message != null)
/* 216 */         return false; 
/* 217 */     } else if (!this.message.equals(other.message)) {
/* 218 */       return false;
/*     */     } 
/* 220 */     if (this.loggerName == null) {
/* 221 */       if (other.loggerName != null)
/* 222 */         return false; 
/* 223 */     } else if (!this.loggerName.equals(other.loggerName)) {
/* 224 */       return false;
/*     */     } 
/* 226 */     if (this.threadName == null) {
/* 227 */       if (other.threadName != null)
/* 228 */         return false; 
/* 229 */     } else if (!this.threadName.equals(other.threadName)) {
/* 230 */       return false;
/* 231 */     }  if (this.timeStamp != other.timeStamp) {
/* 232 */       return false;
/*     */     }
/* 234 */     if (this.marker == null) {
/* 235 */       if (other.marker != null)
/* 236 */         return false; 
/* 237 */     } else if (!this.marker.equals(other.marker)) {
/* 238 */       return false;
/*     */     } 
/* 240 */     if (this.mdcPropertyMap == null) {
/* 241 */       if (other.mdcPropertyMap != null)
/* 242 */         return false; 
/* 243 */     } else if (!this.mdcPropertyMap.equals(other.mdcPropertyMap)) {
/* 244 */       return false;
/* 245 */     }  return true;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\ch\qos\logback\classic\spi\LoggingEventVO.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */