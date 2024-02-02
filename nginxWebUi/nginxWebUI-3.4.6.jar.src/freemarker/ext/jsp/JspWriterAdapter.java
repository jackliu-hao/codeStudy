/*     */ package freemarker.ext.jsp;
/*     */ 
/*     */ import freemarker.template.utility.SecurityUtilities;
/*     */ import java.io.IOException;
/*     */ import java.io.Writer;
/*     */ import javax.servlet.jsp.JspWriter;
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
/*     */ class JspWriterAdapter
/*     */   extends JspWriter
/*     */ {
/*  30 */   static final char[] NEWLINE = SecurityUtilities.getSystemProperty("line.separator", "\n").toCharArray();
/*     */   
/*     */   private final Writer out;
/*     */   
/*     */   JspWriterAdapter(Writer out) {
/*  35 */     super(0, true);
/*  36 */     this.out = out;
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/*  41 */     return "JspWriterAdapter wrapping a " + this.out.toString();
/*     */   }
/*     */ 
/*     */   
/*     */   public void clear() throws IOException {
/*  46 */     throw new IOException("Can't clear");
/*     */   }
/*     */ 
/*     */   
/*     */   public void clearBuffer() throws IOException {
/*  51 */     throw new IOException("Can't clear");
/*     */   }
/*     */ 
/*     */   
/*     */   public void close() throws IOException {
/*  56 */     throw new IOException("Close not permitted.");
/*     */   }
/*     */ 
/*     */   
/*     */   public void flush() throws IOException {
/*  61 */     this.out.flush();
/*     */   }
/*     */ 
/*     */   
/*     */   public int getRemaining() {
/*  66 */     return 0;
/*     */   }
/*     */ 
/*     */   
/*     */   public void newLine() throws IOException {
/*  71 */     this.out.write(NEWLINE);
/*     */   }
/*     */ 
/*     */   
/*     */   public void print(boolean arg0) throws IOException {
/*  76 */     this.out.write(arg0 ? Boolean.TRUE.toString() : Boolean.FALSE.toString());
/*     */   }
/*     */ 
/*     */   
/*     */   public void print(char arg0) throws IOException {
/*  81 */     this.out.write(arg0);
/*     */   }
/*     */ 
/*     */   
/*     */   public void print(char[] arg0) throws IOException {
/*  86 */     this.out.write(arg0);
/*     */   }
/*     */ 
/*     */   
/*     */   public void print(double arg0) throws IOException {
/*  91 */     this.out.write(Double.toString(arg0));
/*     */   }
/*     */ 
/*     */   
/*     */   public void print(float arg0) throws IOException {
/*  96 */     this.out.write(Float.toString(arg0));
/*     */   }
/*     */ 
/*     */   
/*     */   public void print(int arg0) throws IOException {
/* 101 */     this.out.write(Integer.toString(arg0));
/*     */   }
/*     */ 
/*     */   
/*     */   public void print(long arg0) throws IOException {
/* 106 */     this.out.write(Long.toString(arg0));
/*     */   }
/*     */ 
/*     */   
/*     */   public void print(Object arg0) throws IOException {
/* 111 */     this.out.write((arg0 == null) ? "null" : arg0.toString());
/*     */   }
/*     */ 
/*     */   
/*     */   public void print(String arg0) throws IOException {
/* 116 */     this.out.write(arg0);
/*     */   }
/*     */ 
/*     */   
/*     */   public void println() throws IOException {
/* 121 */     newLine();
/*     */   }
/*     */ 
/*     */   
/*     */   public void println(boolean arg0) throws IOException {
/* 126 */     print(arg0);
/* 127 */     newLine();
/*     */   }
/*     */ 
/*     */   
/*     */   public void println(char arg0) throws IOException {
/* 132 */     print(arg0);
/* 133 */     newLine();
/*     */   }
/*     */ 
/*     */   
/*     */   public void println(char[] arg0) throws IOException {
/* 138 */     print(arg0);
/* 139 */     newLine();
/*     */   }
/*     */ 
/*     */   
/*     */   public void println(double arg0) throws IOException {
/* 144 */     print(arg0);
/* 145 */     newLine();
/*     */   }
/*     */ 
/*     */   
/*     */   public void println(float arg0) throws IOException {
/* 150 */     print(arg0);
/* 151 */     newLine();
/*     */   }
/*     */ 
/*     */   
/*     */   public void println(int arg0) throws IOException {
/* 156 */     print(arg0);
/* 157 */     newLine();
/*     */   }
/*     */ 
/*     */   
/*     */   public void println(long arg0) throws IOException {
/* 162 */     print(arg0);
/* 163 */     newLine();
/*     */   }
/*     */ 
/*     */   
/*     */   public void println(Object arg0) throws IOException {
/* 168 */     print(arg0);
/* 169 */     newLine();
/*     */   }
/*     */ 
/*     */   
/*     */   public void println(String arg0) throws IOException {
/* 174 */     print(arg0);
/* 175 */     newLine();
/*     */   }
/*     */ 
/*     */   
/*     */   public void write(int c) throws IOException {
/* 180 */     this.out.write(c);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void write(char[] arg0, int arg1, int arg2) throws IOException {
/* 186 */     this.out.write(arg0, arg1, arg2);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\ext\jsp\JspWriterAdapter.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */