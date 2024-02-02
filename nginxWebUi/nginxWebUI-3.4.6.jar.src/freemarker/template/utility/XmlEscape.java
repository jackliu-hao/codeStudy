/*    */ package freemarker.template.utility;
/*    */ 
/*    */ import freemarker.template.TemplateTransformModel;
/*    */ import java.io.IOException;
/*    */ import java.io.Writer;
/*    */ import java.util.Map;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class XmlEscape
/*    */   implements TemplateTransformModel
/*    */ {
/* 37 */   private static final char[] LT = "&lt;".toCharArray();
/* 38 */   private static final char[] GT = "&gt;".toCharArray();
/* 39 */   private static final char[] AMP = "&amp;".toCharArray();
/* 40 */   private static final char[] QUOT = "&quot;".toCharArray();
/* 41 */   private static final char[] APOS = "&apos;".toCharArray();
/*    */ 
/*    */   
/*    */   public Writer getWriter(final Writer out, Map args) {
/* 45 */     return new Writer()
/*    */       {
/*    */         
/*    */         public void write(int c) throws IOException
/*    */         {
/* 50 */           switch (c) {
/*    */             case 60:
/* 52 */               out.write(XmlEscape.LT, 0, 4); return;
/* 53 */             case 62: out.write(XmlEscape.GT, 0, 4); return;
/* 54 */             case 38: out.write(XmlEscape.AMP, 0, 5); return;
/* 55 */             case 34: out.write(XmlEscape.QUOT, 0, 6); return;
/* 56 */             case 39: out.write(XmlEscape.APOS, 0, 6); return;
/* 57 */           }  out.write(c);
/*    */         }
/*    */ 
/*    */ 
/*    */ 
/*    */         
/*    */         public void write(char[] cbuf, int off, int len) throws IOException {
/* 64 */           int lastoff = off;
/* 65 */           int lastpos = off + len;
/* 66 */           for (int i = off; i < lastpos; i++) {
/* 67 */             switch (cbuf[i]) {
/*    */               case '<':
/* 69 */                 out.write(cbuf, lastoff, i - lastoff); out.write(XmlEscape.LT, 0, 4); lastoff = i + 1; break;
/* 70 */               case '>': out.write(cbuf, lastoff, i - lastoff); out.write(XmlEscape.GT, 0, 4); lastoff = i + 1; break;
/* 71 */               case '&': out.write(cbuf, lastoff, i - lastoff); out.write(XmlEscape.AMP, 0, 5); lastoff = i + 1; break;
/* 72 */               case '"': out.write(cbuf, lastoff, i - lastoff); out.write(XmlEscape.QUOT, 0, 6); lastoff = i + 1; break;
/* 73 */               case '\'': out.write(cbuf, lastoff, i - lastoff); out.write(XmlEscape.APOS, 0, 6); lastoff = i + 1; break;
/*    */             } 
/*    */           } 
/* 76 */           int remaining = lastpos - lastoff;
/* 77 */           if (remaining > 0) {
/* 78 */             out.write(cbuf, lastoff, remaining);
/*    */           }
/*    */         }
/*    */         
/*    */         public void flush() throws IOException {
/* 83 */           out.flush();
/*    */         }
/*    */         
/*    */         public void close() {}
/*    */       };
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\templat\\utility\XmlEscape.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */