/*     */ package freemarker.template.utility;
/*     */ 
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
/*     */ public class HtmlEscape
/*     */   implements TemplateTransformModel
/*     */ {
/*  57 */   private static final char[] LT = "&lt;".toCharArray();
/*  58 */   private static final char[] GT = "&gt;".toCharArray();
/*  59 */   private static final char[] AMP = "&amp;".toCharArray();
/*  60 */   private static final char[] QUOT = "&quot;".toCharArray();
/*     */ 
/*     */   
/*     */   public Writer getWriter(final Writer out, Map args) {
/*  64 */     return new Writer()
/*     */       {
/*     */         
/*     */         public void write(int c) throws IOException
/*     */         {
/*  69 */           switch (c) {
/*     */             case 60:
/*  71 */               out.write(HtmlEscape.LT, 0, 4); return;
/*  72 */             case 62: out.write(HtmlEscape.GT, 0, 4); return;
/*  73 */             case 38: out.write(HtmlEscape.AMP, 0, 5); return;
/*  74 */             case 34: out.write(HtmlEscape.QUOT, 0, 6); return;
/*  75 */           }  out.write(c);
/*     */         }
/*     */ 
/*     */ 
/*     */ 
/*     */         
/*     */         public void write(char[] cbuf, int off, int len) throws IOException {
/*  82 */           int lastoff = off;
/*  83 */           int lastpos = off + len;
/*  84 */           for (int i = off; i < lastpos; i++) {
/*  85 */             switch (cbuf[i]) {
/*     */               case '<':
/*  87 */                 out.write(cbuf, lastoff, i - lastoff); out.write(HtmlEscape.LT, 0, 4); lastoff = i + 1; break;
/*  88 */               case '>': out.write(cbuf, lastoff, i - lastoff); out.write(HtmlEscape.GT, 0, 4); lastoff = i + 1; break;
/*  89 */               case '&': out.write(cbuf, lastoff, i - lastoff); out.write(HtmlEscape.AMP, 0, 5); lastoff = i + 1; break;
/*  90 */               case '"': out.write(cbuf, lastoff, i - lastoff); out.write(HtmlEscape.QUOT, 0, 6); lastoff = i + 1; break;
/*     */             } 
/*     */           } 
/*  93 */           int remaining = lastpos - lastoff;
/*  94 */           if (remaining > 0) {
/*  95 */             out.write(cbuf, lastoff, remaining);
/*     */           }
/*     */         }
/*     */         
/*     */         public void flush() throws IOException {
/* 100 */           out.flush();
/*     */         }
/*     */         
/*     */         public void close() {}
/*     */       };
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\templat\\utility\HtmlEscape.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */