/*     */ package freemarker.template.utility;
/*     */ 
/*     */ import freemarker.template.TemplateBooleanModel;
/*     */ import freemarker.template.TemplateModelException;
/*     */ import freemarker.template.TemplateNumberModel;
/*     */ import freemarker.template.TemplateTransformModel;
/*     */ import java.io.IOException;
/*     */ import java.io.Writer;
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
/*     */ public class StandardCompress
/*     */   implements TemplateTransformModel
/*     */ {
/*     */   private static final String BUFFER_SIZE_KEY = "buffer_size";
/*     */   private static final String SINGLE_LINE_KEY = "single_line";
/*     */   private int defaultBufferSize;
/*  77 */   public static final StandardCompress INSTANCE = new StandardCompress();
/*     */   
/*     */   public StandardCompress() {
/*  80 */     this(2048);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public StandardCompress(int defaultBufferSize) {
/*  87 */     this.defaultBufferSize = defaultBufferSize;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public Writer getWriter(Writer out, Map args) throws TemplateModelException {
/*  93 */     int bufferSize = this.defaultBufferSize;
/*  94 */     boolean singleLine = false;
/*  95 */     if (args != null) {
/*     */       try {
/*  97 */         TemplateNumberModel num = (TemplateNumberModel)args.get("buffer_size");
/*  98 */         if (num != null)
/*  99 */           bufferSize = num.getAsNumber().intValue(); 
/* 100 */       } catch (ClassCastException e) {
/* 101 */         throw new TemplateModelException("Expecting numerical argument to buffer_size");
/*     */       } 
/*     */       try {
/* 104 */         TemplateBooleanModel flag = (TemplateBooleanModel)args.get("single_line");
/* 105 */         if (flag != null)
/* 106 */           singleLine = flag.getAsBoolean(); 
/* 107 */       } catch (ClassCastException e) {
/* 108 */         throw new TemplateModelException("Expecting boolean argument to single_line");
/*     */       } 
/*     */     } 
/* 111 */     return new StandardCompressWriter(out, bufferSize, singleLine);
/*     */   }
/*     */ 
/*     */   
/*     */   private static class StandardCompressWriter
/*     */     extends Writer
/*     */   {
/*     */     private static final int MAX_EOL_LENGTH = 2;
/*     */     private static final int AT_BEGINNING = 0;
/*     */     private static final int SINGLE_LINE = 1;
/*     */     private static final int INIT = 2;
/*     */     private static final int SAW_CR = 3;
/*     */     private static final int LINEBREAK_CR = 4;
/*     */     private static final int LINEBREAK_CRLF = 5;
/*     */     private static final int LINEBREAK_LF = 6;
/*     */     private final Writer out;
/*     */     private final char[] buf;
/*     */     private final boolean singleLine;
/* 129 */     private int pos = 0;
/*     */     private boolean inWhitespace = true;
/* 131 */     private int lineBreakState = 0;
/*     */     
/*     */     public StandardCompressWriter(Writer out, int bufSize, boolean singleLine) {
/* 134 */       this.out = out;
/* 135 */       this.singleLine = singleLine;
/* 136 */       this.buf = new char[bufSize];
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public void write(char[] cbuf, int off, int len) throws IOException {
/*     */       while (true) {
/* 143 */         int room = this.buf.length - this.pos - 2;
/* 144 */         if (room >= len) {
/* 145 */           writeHelper(cbuf, off, len); break;
/*     */         } 
/* 147 */         if (room <= 0) {
/* 148 */           flushInternal(); continue;
/*     */         } 
/* 150 */         writeHelper(cbuf, off, room);
/* 151 */         flushInternal();
/* 152 */         off += room;
/* 153 */         len -= room;
/*     */       } 
/*     */     }
/*     */ 
/*     */     
/*     */     private void writeHelper(char[] cbuf, int off, int len) {
/* 159 */       for (int i = off, end = off + len; i < end; i++) {
/* 160 */         char c = cbuf[i];
/* 161 */         if (Character.isWhitespace(c)) {
/* 162 */           this.inWhitespace = true;
/* 163 */           updateLineBreakState(c);
/* 164 */         } else if (this.inWhitespace) {
/* 165 */           this.inWhitespace = false;
/* 166 */           writeLineBreakOrSpace();
/* 167 */           this.buf[this.pos++] = c;
/*     */         } else {
/* 169 */           this.buf[this.pos++] = c;
/*     */         } 
/*     */       } 
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     private void updateLineBreakState(char c) {
/* 182 */       switch (this.lineBreakState) {
/*     */         case 2:
/* 184 */           if (c == '\r') {
/* 185 */             this.lineBreakState = 3; break;
/* 186 */           }  if (c == '\n') {
/* 187 */             this.lineBreakState = 6;
/*     */           }
/*     */           break;
/*     */         case 3:
/* 191 */           if (c == '\n') {
/* 192 */             this.lineBreakState = 5; break;
/*     */           } 
/* 194 */           this.lineBreakState = 4;
/*     */           break;
/*     */       } 
/*     */     }
/*     */     
/*     */     private void writeLineBreakOrSpace() {
/* 200 */       switch (this.lineBreakState) {
/*     */         
/*     */         case 3:
/*     */         case 4:
/* 204 */           this.buf[this.pos++] = '\r';
/*     */           break;
/*     */         case 5:
/* 207 */           this.buf[this.pos++] = '\r';
/*     */         
/*     */         case 6:
/* 210 */           this.buf[this.pos++] = '\n';
/*     */           break;
/*     */ 
/*     */ 
/*     */         
/*     */         case 1:
/*     */         case 2:
/* 217 */           this.buf[this.pos++] = ' '; break;
/*     */       } 
/* 219 */       this.lineBreakState = this.singleLine ? 1 : 2;
/*     */     }
/*     */     
/*     */     private void flushInternal() throws IOException {
/* 223 */       this.out.write(this.buf, 0, this.pos);
/* 224 */       this.pos = 0;
/*     */     }
/*     */ 
/*     */     
/*     */     public void flush() throws IOException {
/* 229 */       flushInternal();
/* 230 */       this.out.flush();
/*     */     }
/*     */ 
/*     */     
/*     */     public void close() throws IOException {
/* 235 */       flushInternal();
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\templat\\utility\StandardCompress.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */