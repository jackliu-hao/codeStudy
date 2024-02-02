package freemarker.template.utility;

import freemarker.template.TemplateBooleanModel;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateNumberModel;
import freemarker.template.TemplateTransformModel;
import java.io.IOException;
import java.io.Writer;
import java.util.Map;

public class StandardCompress implements TemplateTransformModel {
   private static final String BUFFER_SIZE_KEY = "buffer_size";
   private static final String SINGLE_LINE_KEY = "single_line";
   private int defaultBufferSize;
   public static final StandardCompress INSTANCE = new StandardCompress();

   public StandardCompress() {
      this(2048);
   }

   public StandardCompress(int defaultBufferSize) {
      this.defaultBufferSize = defaultBufferSize;
   }

   public Writer getWriter(Writer out, Map args) throws TemplateModelException {
      int bufferSize = this.defaultBufferSize;
      boolean singleLine = false;
      if (args != null) {
         try {
            TemplateNumberModel num = (TemplateNumberModel)args.get("buffer_size");
            if (num != null) {
               bufferSize = num.getAsNumber().intValue();
            }
         } catch (ClassCastException var7) {
            throw new TemplateModelException("Expecting numerical argument to buffer_size");
         }

         try {
            TemplateBooleanModel flag = (TemplateBooleanModel)args.get("single_line");
            if (flag != null) {
               singleLine = flag.getAsBoolean();
            }
         } catch (ClassCastException var6) {
            throw new TemplateModelException("Expecting boolean argument to single_line");
         }
      }

      return new StandardCompressWriter(out, bufferSize, singleLine);
   }

   private static class StandardCompressWriter extends Writer {
      private static final int MAX_EOL_LENGTH = 2;
      private static final int AT_BEGINNING = 0;
      private static final int SINGLE_LINE = 1;
      private static final int INIT = 2;
      private static final int SAW_CR = 3;
      private static final int LINEBREAK_CR = 4;
      private static final int LINEBREAK_CRLF = 5;
      private static final int LINEBREAK_LF = 6;
      private final Writer out;
      private final char[] buf;
      private final boolean singleLine;
      private int pos = 0;
      private boolean inWhitespace = true;
      private int lineBreakState = 0;

      public StandardCompressWriter(Writer out, int bufSize, boolean singleLine) {
         this.out = out;
         this.singleLine = singleLine;
         this.buf = new char[bufSize];
      }

      public void write(char[] cbuf, int off, int len) throws IOException {
         while(true) {
            int room = this.buf.length - this.pos - 2;
            if (room >= len) {
               this.writeHelper(cbuf, off, len);
               return;
            }

            if (room <= 0) {
               this.flushInternal();
            } else {
               this.writeHelper(cbuf, off, room);
               this.flushInternal();
               off += room;
               len -= room;
            }
         }
      }

      private void writeHelper(char[] cbuf, int off, int len) {
         int i = off;

         for(int end = off + len; i < end; ++i) {
            char c = cbuf[i];
            if (Character.isWhitespace(c)) {
               this.inWhitespace = true;
               this.updateLineBreakState(c);
            } else if (this.inWhitespace) {
               this.inWhitespace = false;
               this.writeLineBreakOrSpace();
               this.buf[this.pos++] = c;
            } else {
               this.buf[this.pos++] = c;
            }
         }

      }

      private void updateLineBreakState(char c) {
         switch (this.lineBreakState) {
            case 2:
               if (c == '\r') {
                  this.lineBreakState = 3;
               } else if (c == '\n') {
                  this.lineBreakState = 6;
               }
               break;
            case 3:
               if (c == '\n') {
                  this.lineBreakState = 5;
               } else {
                  this.lineBreakState = 4;
               }
         }

      }

      private void writeLineBreakOrSpace() {
         switch (this.lineBreakState) {
            case 0:
            default:
               break;
            case 1:
            case 2:
               this.buf[this.pos++] = ' ';
               break;
            case 3:
            case 4:
               this.buf[this.pos++] = '\r';
               break;
            case 5:
               this.buf[this.pos++] = '\r';
            case 6:
               this.buf[this.pos++] = '\n';
         }

         this.lineBreakState = this.singleLine ? 1 : 2;
      }

      private void flushInternal() throws IOException {
         this.out.write(this.buf, 0, this.pos);
         this.pos = 0;
      }

      public void flush() throws IOException {
         this.flushInternal();
         this.out.flush();
      }

      public void close() throws IOException {
         this.flushInternal();
      }
   }
}
