/*     */ package org.wildfly.client.config;
/*     */ 
/*     */ import java.net.URI;
/*     */ import javax.xml.stream.Location;
/*     */ import javax.xml.stream.XMLStreamException;
/*     */ import javax.xml.stream.XMLStreamReader;
/*     */ import org.wildfly.client.config._private.ConfigMessages;
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
/*     */ 
/*     */ public class ConfigXMLParseException
/*     */   extends XMLStreamException
/*     */ {
/*     */   private static final long serialVersionUID = -1880381457871462141L;
/*     */   
/*     */   public ConfigXMLParseException() {}
/*     */   
/*     */   public ConfigXMLParseException(String msg) {
/*  49 */     super(msg);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ConfigXMLParseException(Throwable cause) {
/*  60 */     super(cause);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ConfigXMLParseException(String msg, Throwable cause) {
/*  70 */     super(msg, cause);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ConfigXMLParseException(Location location) {
/*  80 */     this(ConfigMessages.msg.parseError(), XMLLocation.toXMLLocation(location), 0);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ConfigXMLParseException(String msg, Location location) {
/*  90 */     this(msg, XMLLocation.toXMLLocation(location), 0);
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
/*     */   public ConfigXMLParseException(Throwable cause, Location location) {
/* 102 */     this(ConfigMessages.msg.parseError(), XMLLocation.toXMLLocation(location), cause, 0);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ConfigXMLParseException(String msg, Location location, Throwable cause) {
/* 112 */     this(msg, XMLLocation.toXMLLocation(location), cause, 0);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ConfigXMLParseException(XMLStreamReader reader) {
/* 122 */     this(ConfigMessages.msg.parseError(), XMLLocation.toXMLLocation(reader.getLocation()), 0);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ConfigXMLParseException(String msg, XMLStreamReader reader) {
/* 132 */     this(msg, XMLLocation.toXMLLocation(reader.getLocation()), 0);
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
/*     */   public ConfigXMLParseException(Throwable cause, XMLStreamReader reader) {
/* 144 */     this(ConfigMessages.msg.parseError(), XMLLocation.toXMLLocation(reader.getLocation()), cause, 0);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ConfigXMLParseException(String msg, XMLStreamReader reader, Throwable cause) {
/* 155 */     this(msg, XMLLocation.toXMLLocation(reader.getLocation()), cause, 0);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public XMLLocation getLocation() {
/* 164 */     return XMLLocation.toXMLLocation(super.getLocation());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void setLocation(XMLLocation location) {
/* 173 */     this.location = location;
/*     */   }
/*     */   static ConfigXMLParseException from(XMLStreamException exception) {
/*     */     ConfigXMLParseException parseException;
/* 177 */     if (exception instanceof ConfigXMLParseException) return (ConfigXMLParseException)exception; 
/* 178 */     StackTraceElement[] stackTrace = exception.getStackTrace();
/* 179 */     Throwable cause = exception.getCause();
/*     */     
/* 181 */     if (cause != null) {
/* 182 */       parseException = new ConfigXMLParseException(clean(exception.getMessage()), exception.getLocation(), cause);
/*     */     } else {
/* 184 */       parseException = new ConfigXMLParseException(clean(exception.getMessage()), exception.getLocation());
/*     */     } 
/* 186 */     parseException.setStackTrace(stackTrace);
/* 187 */     return parseException;
/*     */   }
/*     */   static ConfigXMLParseException from(XMLStreamException exception, URI uri) {
/*     */     ConfigXMLParseException parseException;
/* 191 */     if (exception instanceof ConfigXMLParseException) return (ConfigXMLParseException)exception; 
/* 192 */     StackTraceElement[] stackTrace = exception.getStackTrace();
/* 193 */     Throwable cause = exception.getCause();
/*     */     
/* 195 */     if (cause != null) {
/* 196 */       parseException = new ConfigXMLParseException(clean(exception.getMessage()), XMLLocation.toXMLLocation(uri, exception.getLocation()), cause);
/*     */     } else {
/* 198 */       parseException = new ConfigXMLParseException(clean(exception.getMessage()), XMLLocation.toXMLLocation(uri, exception.getLocation()));
/*     */     } 
/* 200 */     parseException.setStackTrace(stackTrace);
/* 201 */     return parseException;
/*     */   }
/*     */   static ConfigXMLParseException from(XMLStreamException exception, URI uri, XMLLocation includedFrom) {
/*     */     ConfigXMLParseException parseException;
/* 205 */     if (exception instanceof ConfigXMLParseException) return (ConfigXMLParseException)exception; 
/* 206 */     StackTraceElement[] stackTrace = exception.getStackTrace();
/* 207 */     Throwable cause = exception.getCause();
/*     */     
/* 209 */     if (cause != null) {
/* 210 */       parseException = new ConfigXMLParseException(clean(exception.getMessage()), XMLLocation.toXMLLocation(includedFrom, uri, exception.getLocation()), cause);
/*     */     } else {
/* 212 */       parseException = new ConfigXMLParseException(clean(exception.getMessage()), XMLLocation.toXMLLocation(includedFrom, uri, exception.getLocation()));
/*     */     } 
/* 214 */     parseException.setStackTrace(stackTrace);
/* 215 */     return parseException;
/*     */   }
/*     */   static ConfigXMLParseException from(Exception exception) {
/*     */     ConfigXMLParseException parseException;
/* 219 */     if (exception instanceof XMLStreamException) {
/* 220 */       return from((XMLStreamException)exception);
/*     */     }
/* 222 */     StackTraceElement[] stackTrace = exception.getStackTrace();
/* 223 */     Throwable cause = exception.getCause();
/*     */     
/* 225 */     if (cause != null) {
/* 226 */       parseException = new ConfigXMLParseException(clean(exception.getMessage()), XMLLocation.UNKNOWN, cause);
/*     */     } else {
/* 228 */       parseException = new ConfigXMLParseException(clean(exception.getMessage()), XMLLocation.UNKNOWN);
/*     */     } 
/* 230 */     parseException.setStackTrace(stackTrace);
/* 231 */     return parseException;
/*     */   }
/*     */   static ConfigXMLParseException from(Exception exception, URI uri) {
/*     */     ConfigXMLParseException parseException;
/* 235 */     if (exception instanceof XMLStreamException) {
/* 236 */       return from((XMLStreamException)exception, uri);
/*     */     }
/* 238 */     StackTraceElement[] stackTrace = exception.getStackTrace();
/* 239 */     Throwable cause = exception.getCause();
/*     */     
/* 241 */     if (cause != null) {
/* 242 */       parseException = new ConfigXMLParseException(clean(exception.getMessage()), XMLLocation.toXMLLocation(uri, new XMLLocation(uri)), cause);
/*     */     } else {
/* 244 */       parseException = new ConfigXMLParseException(clean(exception.getMessage()), XMLLocation.toXMLLocation(uri, new XMLLocation(uri)));
/*     */     } 
/* 246 */     parseException.setStackTrace(stackTrace);
/* 247 */     return parseException;
/*     */   }
/*     */   static ConfigXMLParseException from(Exception exception, URI uri, XMLLocation includedFrom) {
/*     */     ConfigXMLParseException parseException;
/* 251 */     if (exception instanceof XMLStreamException) {
/* 252 */       return from((XMLStreamException)exception, uri, includedFrom);
/*     */     }
/* 254 */     StackTraceElement[] stackTrace = exception.getStackTrace();
/* 255 */     Throwable cause = exception.getCause();
/*     */     
/* 257 */     if (cause != null) {
/* 258 */       parseException = new ConfigXMLParseException(clean(exception.getMessage()), XMLLocation.toXMLLocation(includedFrom, uri, new XMLLocation(uri)), cause);
/*     */     } else {
/* 260 */       parseException = new ConfigXMLParseException(clean(exception.getMessage()), XMLLocation.toXMLLocation(includedFrom, uri, new XMLLocation(uri)));
/*     */     } 
/* 262 */     parseException.setStackTrace(stackTrace);
/* 263 */     return parseException;
/*     */   }
/*     */   
/*     */   private static String clean(String original) {
/* 267 */     if (original.startsWith("ParseError at [row,col]:[")) {
/* 268 */       int idx = original.indexOf("Message: ");
/* 269 */       return (idx == -1) ? original : original.substring(idx + 9);
/*     */     } 
/* 271 */     return original;
/*     */   }
/*     */ 
/*     */   
/*     */   private ConfigXMLParseException(String msg, XMLLocation location, int ignored) {
/* 276 */     super(msg + location);
/* 277 */     this.location = location;
/*     */   }
/*     */   
/*     */   private ConfigXMLParseException(String msg, XMLLocation location, Throwable cause, int ignored) {
/* 281 */     super(msg + location, cause);
/* 282 */     this.location = location;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\wildfly\client\config\ConfigXMLParseException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */