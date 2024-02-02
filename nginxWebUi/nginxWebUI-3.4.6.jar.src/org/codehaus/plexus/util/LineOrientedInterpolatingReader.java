/*     */ package org.codehaus.plexus.util;
/*     */ 
/*     */ import java.io.FilterReader;
/*     */ import java.io.IOException;
/*     */ import java.io.PushbackReader;
/*     */ import java.io.Reader;
/*     */ import java.util.Collections;
/*     */ import java.util.HashSet;
/*     */ import java.util.Iterator;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import java.util.TreeMap;
/*     */ import org.codehaus.plexus.util.reflection.Reflector;
/*     */ import org.codehaus.plexus.util.reflection.ReflectorException;
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
/*     */ public class LineOrientedInterpolatingReader
/*     */   extends FilterReader
/*     */ {
/*     */   public static final String DEFAULT_START_DELIM = "${";
/*     */   public static final String DEFAULT_END_DELIM = "}";
/*     */   public static final String DEFAULT_ESCAPE_SEQ = "\\";
/*     */   private static final char CARRIAGE_RETURN_CHAR = '\r';
/*     */   private static final char NEWLINE_CHAR = '\n';
/*     */   private final PushbackReader pushbackReader;
/*     */   private final Map context;
/*     */   private final String startDelim;
/*     */   private final String endDelim;
/*     */   private final String escapeSeq;
/*     */   private final int minExpressionSize;
/*     */   private final Reflector reflector;
/*  63 */   private int lineIdx = -1;
/*     */ 
/*     */   
/*     */   private String line;
/*     */ 
/*     */   
/*     */   public LineOrientedInterpolatingReader(Reader reader, Map context, String startDelim, String endDelim, String escapeSeq) {
/*  70 */     super(reader);
/*     */     
/*  72 */     this.startDelim = startDelim;
/*     */     
/*  74 */     this.endDelim = endDelim;
/*     */     
/*  76 */     this.escapeSeq = escapeSeq;
/*     */ 
/*     */     
/*  79 */     this.minExpressionSize = startDelim.length() + endDelim.length() + 1;
/*     */     
/*  81 */     this.context = Collections.unmodifiableMap(context);
/*     */     
/*  83 */     this.reflector = new Reflector();
/*     */     
/*  85 */     if (reader instanceof PushbackReader) {
/*     */       
/*  87 */       this.pushbackReader = (PushbackReader)reader;
/*     */     }
/*     */     else {
/*     */       
/*  91 */       this.pushbackReader = new PushbackReader(reader, 1);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public LineOrientedInterpolatingReader(Reader reader, Map context, String startDelim, String endDelim) {
/*  97 */     this(reader, context, startDelim, endDelim, "\\");
/*     */   }
/*     */ 
/*     */   
/*     */   public LineOrientedInterpolatingReader(Reader reader, Map context) {
/* 102 */     this(reader, context, "${", "}", "\\");
/*     */   }
/*     */ 
/*     */   
/*     */   public int read() throws IOException {
/* 107 */     if (this.line == null || this.lineIdx >= this.line.length())
/*     */     {
/* 109 */       readAndInterpolateLine();
/*     */     }
/*     */     
/* 112 */     int next = -1;
/*     */     
/* 114 */     if (this.line != null && this.lineIdx < this.line.length())
/*     */     {
/* 116 */       next = this.line.charAt(this.lineIdx++);
/*     */     }
/*     */     
/* 119 */     return next;
/*     */   }
/*     */ 
/*     */   
/*     */   public int read(char[] cbuf, int off, int len) throws IOException {
/* 124 */     int fillCount = 0;
/*     */     
/* 126 */     for (int i = off; i < off + len; ) {
/*     */       
/* 128 */       int next = read();
/* 129 */       if (next > -1) {
/*     */         
/* 131 */         cbuf[i] = (char)next;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 138 */         fillCount++; i++;
/*     */       } 
/*     */     } 
/* 141 */     if (fillCount == 0)
/*     */     {
/* 143 */       fillCount = -1;
/*     */     }
/*     */     
/* 146 */     return fillCount;
/*     */   }
/*     */ 
/*     */   
/*     */   public long skip(long n) throws IOException {
/* 151 */     long skipCount = 0L;
/*     */     long i;
/* 153 */     for (i = 0L; i < n; i++) {
/*     */       
/* 155 */       int next = read();
/*     */       
/* 157 */       if (next < 0) {
/*     */         break;
/*     */       }
/*     */ 
/*     */       
/* 162 */       skipCount++;
/*     */     } 
/*     */     
/* 165 */     return skipCount;
/*     */   }
/*     */ 
/*     */   
/*     */   private void readAndInterpolateLine() throws IOException {
/* 170 */     String rawLine = readLine();
/*     */     
/* 172 */     if (rawLine != null) {
/*     */       
/* 174 */       Set expressions = parseForExpressions(rawLine);
/*     */       
/* 176 */       Map evaluatedExpressions = evaluateExpressions(expressions);
/*     */       
/* 178 */       String interpolated = replaceWithInterpolatedValues(rawLine, evaluatedExpressions);
/*     */       
/* 180 */       if (interpolated != null && interpolated.length() > 0)
/*     */       {
/* 182 */         this.line = interpolated;
/* 183 */         this.lineIdx = 0;
/*     */       }
/*     */     
/*     */     } else {
/*     */       
/* 188 */       this.line = null;
/* 189 */       this.lineIdx = -1;
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   private String readLine() throws IOException {
/* 195 */     StringBuffer lineBuffer = new StringBuffer(40);
/* 196 */     int next = -1;
/*     */     
/* 198 */     boolean lastWasCR = false;
/* 199 */     while ((next = this.pushbackReader.read()) > -1) {
/*     */       
/* 201 */       char c = (char)next;
/*     */       
/* 203 */       if (c == '\r') {
/*     */         
/* 205 */         lastWasCR = true;
/* 206 */         lineBuffer.append(c); continue;
/*     */       } 
/* 208 */       if (c == '\n') {
/*     */         
/* 210 */         lineBuffer.append(c);
/*     */         break;
/*     */       } 
/* 213 */       if (lastWasCR) {
/*     */         
/* 215 */         this.pushbackReader.unread(c);
/*     */         
/*     */         break;
/*     */       } 
/*     */       
/* 220 */       lineBuffer.append(c);
/*     */     } 
/*     */ 
/*     */     
/* 224 */     if (lineBuffer.length() < 1)
/*     */     {
/* 226 */       return null;
/*     */     }
/*     */ 
/*     */     
/* 230 */     return lineBuffer.toString();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private String replaceWithInterpolatedValues(String rawLine, Map evaluatedExpressions) {
/* 236 */     String result = rawLine;
/*     */     
/* 238 */     for (Iterator it = evaluatedExpressions.entrySet().iterator(); it.hasNext(); ) {
/*     */       
/* 240 */       Map.Entry entry = it.next();
/*     */       
/* 242 */       String expression = (String)entry.getKey();
/*     */       
/* 244 */       String value = String.valueOf(entry.getValue());
/*     */       
/* 246 */       result = findAndReplaceUnlessEscaped(result, expression, value);
/*     */     } 
/*     */     
/* 249 */     return result;
/*     */   }
/*     */ 
/*     */   
/*     */   private Map evaluateExpressions(Set expressions) {
/* 254 */     Map evaluated = new TreeMap();
/*     */     
/* 256 */     for (Iterator it = expressions.iterator(); it.hasNext(); ) {
/*     */       
/* 258 */       String rawExpression = it.next();
/*     */       
/* 260 */       String realExpression = rawExpression.substring(this.startDelim.length(), rawExpression.length() - this.endDelim.length());
/*     */ 
/*     */       
/* 263 */       String[] parts = realExpression.split("\\.");
/* 264 */       if (parts.length > 0) {
/*     */         
/* 266 */         Object value = this.context.get(parts[0]);
/*     */         
/* 268 */         if (value != null) {
/*     */           
/* 270 */           for (int i = 1; i < parts.length; i++) {
/*     */ 
/*     */             
/*     */             try {
/* 274 */               value = this.reflector.getObjectProperty(value, parts[i]);
/*     */               
/* 276 */               if (value == null)
/*     */               {
/*     */                 break;
/*     */               }
/*     */             }
/* 281 */             catch (ReflectorException e) {
/*     */ 
/*     */               
/* 284 */               e.printStackTrace();
/*     */               
/*     */               break;
/*     */             } 
/*     */           } 
/*     */           
/* 290 */           evaluated.put(rawExpression, value);
/*     */         } 
/*     */       } 
/*     */     } 
/*     */     
/* 295 */     return evaluated;
/*     */   }
/*     */ 
/*     */   
/*     */   private Set parseForExpressions(String rawLine) {
/* 300 */     Set expressions = new HashSet();
/*     */     
/* 302 */     if (rawLine != null) {
/*     */       
/* 304 */       int placeholder = -1;
/*     */ 
/*     */ 
/*     */       
/*     */       do {
/* 309 */         int start = findDelimiter(rawLine, this.startDelim, placeholder);
/*     */ 
/*     */         
/* 312 */         if (start < 0) {
/*     */           break;
/*     */         }
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 319 */         int end = findDelimiter(rawLine, this.endDelim, start + 1);
/*     */ 
/*     */         
/* 322 */         if (end < 0) {
/*     */           break;
/*     */         }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 331 */         expressions.add(rawLine.substring(start, end + this.endDelim.length()));
/*     */ 
/*     */         
/* 334 */         placeholder = end + 1;
/* 335 */       } while (placeholder < rawLine.length() - this.minExpressionSize);
/*     */     } 
/*     */     
/* 338 */     return expressions;
/*     */   }
/*     */ 
/*     */   
/*     */   private int findDelimiter(String rawLine, String delimiter, int lastPos) {
/* 343 */     int placeholder = lastPos;
/*     */     
/* 345 */     int position = -1;
/*     */     
/*     */     do {
/* 348 */       position = rawLine.indexOf(delimiter, placeholder);
/*     */       
/* 350 */       if (position < 0) {
/*     */         break;
/*     */       }
/*     */ 
/*     */ 
/*     */       
/* 356 */       int escEndIdx = rawLine.indexOf(this.escapeSeq, placeholder) + this.escapeSeq.length();
/*     */       
/* 358 */       if (escEndIdx <= this.escapeSeq.length() - 1 || escEndIdx != position)
/*     */         continue; 
/* 360 */       placeholder = position + 1;
/* 361 */       position = -1;
/*     */ 
/*     */     
/*     */     }
/* 365 */     while (position < 0 && placeholder < rawLine.length() - this.endDelim.length());
/*     */ 
/*     */ 
/*     */     
/* 369 */     return position;
/*     */   }
/*     */ 
/*     */   
/*     */   private String findAndReplaceUnlessEscaped(String rawLine, String search, String replace) {
/* 374 */     StringBuffer lineBuffer = new StringBuffer((int)(rawLine.length() * 1.5D));
/*     */     
/* 376 */     int lastReplacement = -1;
/*     */ 
/*     */     
/*     */     while (true) {
/* 380 */       int nextReplacement = rawLine.indexOf(search, lastReplacement + 1);
/* 381 */       if (nextReplacement > -1)
/*     */       
/* 383 */       { if (lastReplacement < 0)
/*     */         {
/* 385 */           lastReplacement = 0;
/*     */         }
/*     */         
/* 388 */         lineBuffer.append(rawLine.substring(lastReplacement, nextReplacement));
/*     */         
/* 390 */         int escIdx = rawLine.indexOf(this.escapeSeq, lastReplacement + 1);
/* 391 */         if (escIdx > -1 && escIdx + this.escapeSeq.length() == nextReplacement) {
/*     */           
/* 393 */           lineBuffer.setLength(lineBuffer.length() - this.escapeSeq.length());
/* 394 */           lineBuffer.append(search);
/*     */         }
/*     */         else {
/*     */           
/* 398 */           lineBuffer.append(replace);
/*     */         } 
/*     */         
/* 401 */         lastReplacement = nextReplacement + search.length();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 408 */         if (lastReplacement <= -1)
/*     */           break;  continue; }  break;
/* 410 */     }  if (lastReplacement < rawLine.length())
/*     */     {
/* 412 */       lineBuffer.append(rawLine.substring(lastReplacement));
/*     */     }
/*     */     
/* 415 */     return lineBuffer.toString();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\codehaus\plexu\\util\LineOrientedInterpolatingReader.class
 * Java compiler version: 3 (47.0)
 * JD-Core Version:       1.1.3
 */