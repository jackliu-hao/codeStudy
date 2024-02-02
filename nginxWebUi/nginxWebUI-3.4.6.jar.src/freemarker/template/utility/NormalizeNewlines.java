/*     */ package freemarker.template.utility;
/*     */ 
/*     */ import freemarker.template.TemplateTransformModel;
/*     */ import java.io.BufferedReader;
/*     */ import java.io.IOException;
/*     */ import java.io.PrintWriter;
/*     */ import java.io.Reader;
/*     */ import java.io.StringReader;
/*     */ import java.io.StringWriter;
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
/*     */ public class NormalizeNewlines
/*     */   implements TemplateTransformModel
/*     */ {
/*     */   public Writer getWriter(final Writer out, Map args) {
/*  69 */     final StringBuilder buf = new StringBuilder();
/*  70 */     return new Writer()
/*     */       {
/*     */         public void write(char[] cbuf, int off, int len) {
/*  73 */           buf.append(cbuf, off, len);
/*     */         }
/*     */ 
/*     */         
/*     */         public void flush() throws IOException {
/*  78 */           out.flush();
/*     */         }
/*     */ 
/*     */         
/*     */         public void close() throws IOException {
/*  83 */           StringReader sr = new StringReader(buf.toString());
/*  84 */           StringWriter sw = new StringWriter();
/*  85 */           NormalizeNewlines.this.transform(sr, sw);
/*  86 */           out.write(sw.toString());
/*     */         }
/*     */       };
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void transform(Reader in, Writer out) throws IOException {
/*  98 */     BufferedReader br = (in instanceof BufferedReader) ? (BufferedReader)in : new BufferedReader(in);
/*     */ 
/*     */     
/* 101 */     PrintWriter pw = (out instanceof PrintWriter) ? (PrintWriter)out : new PrintWriter(out);
/*     */ 
/*     */     
/* 104 */     String line = br.readLine();
/* 105 */     if (line != null && 
/* 106 */       line.length() > 0) {
/* 107 */       pw.println(line);
/*     */     }
/*     */     
/* 110 */     while ((line = br.readLine()) != null)
/* 111 */       pw.println(line); 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\templat\\utility\NormalizeNewlines.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */