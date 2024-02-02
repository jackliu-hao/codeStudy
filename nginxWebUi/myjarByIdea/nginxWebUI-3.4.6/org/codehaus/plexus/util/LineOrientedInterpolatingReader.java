package org.codehaus.plexus.util;

import java.io.FilterReader;
import java.io.IOException;
import java.io.PushbackReader;
import java.io.Reader;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import org.codehaus.plexus.util.reflection.Reflector;
import org.codehaus.plexus.util.reflection.ReflectorException;

public class LineOrientedInterpolatingReader extends FilterReader {
   public static final String DEFAULT_START_DELIM = "${";
   public static final String DEFAULT_END_DELIM = "}";
   public static final String DEFAULT_ESCAPE_SEQ = "\\";
   private static final char CARRIAGE_RETURN_CHAR = '\r';
   private static final char NEWLINE_CHAR = '\n';
   private final PushbackReader pushbackReader;
   private final Map context;
   private final String startDelim;
   private final String endDelim;
   private final String escapeSeq;
   private final int minExpressionSize;
   private final Reflector reflector;
   private int lineIdx;
   private String line;

   public LineOrientedInterpolatingReader(Reader reader, Map context, String startDelim, String endDelim, String escapeSeq) {
      super(reader);
      this.lineIdx = -1;
      this.startDelim = startDelim;
      this.endDelim = endDelim;
      this.escapeSeq = escapeSeq;
      this.minExpressionSize = startDelim.length() + endDelim.length() + 1;
      this.context = Collections.unmodifiableMap(context);
      this.reflector = new Reflector();
      if (reader instanceof PushbackReader) {
         this.pushbackReader = (PushbackReader)reader;
      } else {
         this.pushbackReader = new PushbackReader(reader, 1);
      }

   }

   public LineOrientedInterpolatingReader(Reader reader, Map context, String startDelim, String endDelim) {
      this(reader, context, startDelim, endDelim, "\\");
   }

   public LineOrientedInterpolatingReader(Reader reader, Map context) {
      this(reader, context, "${", "}", "\\");
   }

   public int read() throws IOException {
      if (this.line == null || this.lineIdx >= this.line.length()) {
         this.readAndInterpolateLine();
      }

      int next = -1;
      if (this.line != null && this.lineIdx < this.line.length()) {
         next = this.line.charAt(this.lineIdx++);
      }

      return next;
   }

   public int read(char[] cbuf, int off, int len) throws IOException {
      int fillCount = 0;

      for(int i = off; i < off + len; ++i) {
         int next = this.read();
         if (next <= -1) {
            break;
         }

         cbuf[i] = (char)next;
         ++fillCount;
      }

      if (fillCount == 0) {
         fillCount = -1;
      }

      return fillCount;
   }

   public long skip(long n) throws IOException {
      long skipCount = 0L;

      for(long i = 0L; i < n; ++i) {
         int next = this.read();
         if (next < 0) {
            break;
         }

         ++skipCount;
      }

      return skipCount;
   }

   private void readAndInterpolateLine() throws IOException {
      String rawLine = this.readLine();
      if (rawLine != null) {
         Set expressions = this.parseForExpressions(rawLine);
         Map evaluatedExpressions = this.evaluateExpressions(expressions);
         String interpolated = this.replaceWithInterpolatedValues(rawLine, evaluatedExpressions);
         if (interpolated != null && interpolated.length() > 0) {
            this.line = interpolated;
            this.lineIdx = 0;
         }
      } else {
         this.line = null;
         this.lineIdx = -1;
      }

   }

   private String readLine() throws IOException {
      StringBuffer lineBuffer = new StringBuffer(40);
      int next = true;
      boolean lastWasCR = false;

      int next;
      while((next = this.pushbackReader.read()) > -1) {
         char c = (char)next;
         if (c == '\r') {
            lastWasCR = true;
            lineBuffer.append(c);
         } else {
            if (c == '\n') {
               lineBuffer.append(c);
               break;
            }

            if (lastWasCR) {
               this.pushbackReader.unread(c);
               break;
            }

            lineBuffer.append(c);
         }
      }

      return lineBuffer.length() < 1 ? null : lineBuffer.toString();
   }

   private String replaceWithInterpolatedValues(String rawLine, Map evaluatedExpressions) {
      String result = rawLine;

      String expression;
      String value;
      for(Iterator it = evaluatedExpressions.entrySet().iterator(); it.hasNext(); result = this.findAndReplaceUnlessEscaped(result, expression, value)) {
         Map.Entry entry = (Map.Entry)it.next();
         expression = (String)entry.getKey();
         value = String.valueOf(entry.getValue());
      }

      return result;
   }

   private Map evaluateExpressions(Set expressions) {
      Map evaluated = new TreeMap();
      Iterator it = expressions.iterator();

      while(true) {
         String rawExpression;
         String[] parts;
         Object value;
         do {
            do {
               if (!it.hasNext()) {
                  return evaluated;
               }

               rawExpression = (String)it.next();
               String realExpression = rawExpression.substring(this.startDelim.length(), rawExpression.length() - this.endDelim.length());
               parts = realExpression.split("\\.");
            } while(parts.length <= 0);

            value = this.context.get(parts[0]);
         } while(value == null);

         for(int i = 1; i < parts.length; ++i) {
            try {
               value = this.reflector.getObjectProperty(value, parts[i]);
               if (value == null) {
                  break;
               }
            } catch (ReflectorException var10) {
               var10.printStackTrace();
               break;
            }
         }

         evaluated.put(rawExpression, value);
      }
   }

   private Set parseForExpressions(String rawLine) {
      Set expressions = new HashSet();
      if (rawLine != null) {
         int placeholder = -1;

         do {
            int start = this.findDelimiter(rawLine, this.startDelim, placeholder);
            if (start < 0) {
               break;
            }

            int end = this.findDelimiter(rawLine, this.endDelim, start + 1);
            if (end < 0) {
               break;
            }

            expressions.add(rawLine.substring(start, end + this.endDelim.length()));
            placeholder = end + 1;
         } while(placeholder < rawLine.length() - this.minExpressionSize);
      }

      return expressions;
   }

   private int findDelimiter(String rawLine, String delimiter, int lastPos) {
      int placeholder = lastPos;
      int position = true;

      int position;
      do {
         position = rawLine.indexOf(delimiter, placeholder);
         if (position < 0) {
            break;
         }

         int escEndIdx = rawLine.indexOf(this.escapeSeq, placeholder) + this.escapeSeq.length();
         if (escEndIdx > this.escapeSeq.length() - 1 && escEndIdx == position) {
            placeholder = position + 1;
            position = -1;
         }
      } while(position < 0 && placeholder < rawLine.length() - this.endDelim.length());

      return position;
   }

   private String findAndReplaceUnlessEscaped(String rawLine, String search, String replace) {
      StringBuffer lineBuffer = new StringBuffer((int)((double)rawLine.length() * 1.5));
      int lastReplacement = -1;

      do {
         int nextReplacement = rawLine.indexOf(search, lastReplacement + 1);
         if (nextReplacement <= -1) {
            break;
         }

         if (lastReplacement < 0) {
            lastReplacement = 0;
         }

         lineBuffer.append(rawLine.substring(lastReplacement, nextReplacement));
         int escIdx = rawLine.indexOf(this.escapeSeq, lastReplacement + 1);
         if (escIdx > -1 && escIdx + this.escapeSeq.length() == nextReplacement) {
            lineBuffer.setLength(lineBuffer.length() - this.escapeSeq.length());
            lineBuffer.append(search);
         } else {
            lineBuffer.append(replace);
         }

         lastReplacement = nextReplacement + search.length();
      } while(lastReplacement > -1);

      if (lastReplacement < rawLine.length()) {
         lineBuffer.append(rawLine.substring(lastReplacement));
      }

      return lineBuffer.toString();
   }
}
