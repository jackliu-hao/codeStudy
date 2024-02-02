/*     */ package freemarker.ext.jsp;
/*     */ 
/*     */ import freemarker.log.Logger;
/*     */ import freemarker.template.TemplateModelException;
/*     */ import freemarker.template.TemplateTransformModel;
/*     */ import freemarker.template.TransformControl;
/*     */ import java.beans.IntrospectionException;
/*     */ import java.io.CharArrayReader;
/*     */ import java.io.CharArrayWriter;
/*     */ import java.io.IOException;
/*     */ import java.io.Reader;
/*     */ import java.io.Writer;
/*     */ import java.util.Map;
/*     */ import javax.servlet.jsp.JspException;
/*     */ import javax.servlet.jsp.JspWriter;
/*     */ import javax.servlet.jsp.tagext.BodyContent;
/*     */ import javax.servlet.jsp.tagext.BodyTag;
/*     */ import javax.servlet.jsp.tagext.IterationTag;
/*     */ import javax.servlet.jsp.tagext.Tag;
/*     */ import javax.servlet.jsp.tagext.TryCatchFinally;
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
/*     */ class TagTransformModel
/*     */   extends JspTagModelBase
/*     */   implements TemplateTransformModel
/*     */ {
/*  49 */   private static final Logger LOG = Logger.getLogger("freemarker.jsp");
/*     */   
/*     */   private final boolean isBodyTag;
/*     */   private final boolean isIterationTag;
/*     */   private final boolean isTryCatchFinally;
/*     */   
/*     */   public TagTransformModel(String tagName, Class<?> tagClass) throws IntrospectionException {
/*  56 */     super(tagName, tagClass);
/*  57 */     this.isIterationTag = IterationTag.class.isAssignableFrom(tagClass);
/*  58 */     this.isBodyTag = (this.isIterationTag && BodyTag.class.isAssignableFrom(tagClass));
/*  59 */     this.isTryCatchFinally = TryCatchFinally.class.isAssignableFrom(tagClass);
/*     */   }
/*     */   public Writer getWriter(Writer out, Map args) throws TemplateModelException {
/*     */     try {
/*     */       JspWriterAdapter jspWriterAdapter;
/*     */       boolean usesAdapter;
/*  65 */       Tag tag = (Tag)getTagInstance();
/*  66 */       FreeMarkerPageContext pageContext = PageContextFactory.getCurrentPageContext();
/*  67 */       Tag parentTag = (Tag)pageContext.peekTopTag(Tag.class);
/*  68 */       tag.setParent(parentTag);
/*  69 */       tag.setPageContext(pageContext);
/*  70 */       setupTag(tag, args, pageContext.getObjectWrapper());
/*     */ 
/*     */ 
/*     */       
/*  74 */       if (out instanceof JspWriter) {
/*     */ 
/*     */         
/*  77 */         if (out != pageContext.getOut()) {
/*  78 */           throw new TemplateModelException("out != pageContext.getOut(). Out is " + out + " pageContext.getOut() is " + pageContext
/*     */ 
/*     */               
/*  81 */               .getOut());
/*     */         }
/*  83 */         usesAdapter = false;
/*     */       } else {
/*  85 */         jspWriterAdapter = new JspWriterAdapter(out);
/*  86 */         pageContext.pushWriter(jspWriterAdapter);
/*  87 */         usesAdapter = true;
/*     */       } 
/*  89 */       TagWriter tagWriter = new TagWriter((Writer)jspWriterAdapter, tag, pageContext, usesAdapter);
/*  90 */       pageContext.pushTopTag(tag);
/*  91 */       pageContext.pushWriter((JspWriter)tagWriter);
/*  92 */       return (Writer)tagWriter;
/*  93 */     } catch (Exception e) {
/*  94 */       throw toTemplateModelExceptionOrRethrow(e);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   static class BodyContentImpl
/*     */     extends BodyContent
/*     */   {
/*     */     private CharArrayWriter buf;
/*     */     
/*     */     BodyContentImpl(JspWriter out, boolean buffer) {
/* 105 */       super(out);
/* 106 */       if (buffer) initBuffer(); 
/*     */     }
/*     */     
/*     */     void initBuffer() {
/* 110 */       this.buf = new CharArrayWriter();
/*     */     }
/*     */ 
/*     */     
/*     */     public void flush() throws IOException {
/* 115 */       if (this.buf == null) {
/* 116 */         getEnclosingWriter().flush();
/*     */       }
/*     */     }
/*     */ 
/*     */     
/*     */     public void clear() throws IOException {
/* 122 */       if (this.buf != null) {
/* 123 */         this.buf = new CharArrayWriter();
/*     */       } else {
/* 125 */         throw new IOException("Can't clear");
/*     */       } 
/*     */     }
/*     */ 
/*     */     
/*     */     public void clearBuffer() throws IOException {
/* 131 */       if (this.buf != null) {
/* 132 */         this.buf = new CharArrayWriter();
/*     */       } else {
/* 134 */         throw new IOException("Can't clear");
/*     */       } 
/*     */     }
/*     */ 
/*     */     
/*     */     public int getRemaining() {
/* 140 */       return Integer.MAX_VALUE;
/*     */     }
/*     */ 
/*     */     
/*     */     public void newLine() throws IOException {
/* 145 */       write(JspWriterAdapter.NEWLINE);
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public void close() throws IOException {}
/*     */ 
/*     */     
/*     */     public void print(boolean arg0) throws IOException {
/* 154 */       write(arg0 ? Boolean.TRUE.toString() : Boolean.FALSE.toString());
/*     */     }
/*     */ 
/*     */     
/*     */     public void print(char arg0) throws IOException {
/* 159 */       write(arg0);
/*     */     }
/*     */ 
/*     */     
/*     */     public void print(char[] arg0) throws IOException {
/* 164 */       write(arg0);
/*     */     }
/*     */ 
/*     */     
/*     */     public void print(double arg0) throws IOException {
/* 169 */       write(Double.toString(arg0));
/*     */     }
/*     */ 
/*     */     
/*     */     public void print(float arg0) throws IOException {
/* 174 */       write(Float.toString(arg0));
/*     */     }
/*     */ 
/*     */     
/*     */     public void print(int arg0) throws IOException {
/* 179 */       write(Integer.toString(arg0));
/*     */     }
/*     */ 
/*     */     
/*     */     public void print(long arg0) throws IOException {
/* 184 */       write(Long.toString(arg0));
/*     */     }
/*     */ 
/*     */     
/*     */     public void print(Object arg0) throws IOException {
/* 189 */       write((arg0 == null) ? "null" : arg0.toString());
/*     */     }
/*     */ 
/*     */     
/*     */     public void print(String arg0) throws IOException {
/* 194 */       write(arg0);
/*     */     }
/*     */ 
/*     */     
/*     */     public void println() throws IOException {
/* 199 */       newLine();
/*     */     }
/*     */ 
/*     */     
/*     */     public void println(boolean arg0) throws IOException {
/* 204 */       print(arg0);
/* 205 */       newLine();
/*     */     }
/*     */ 
/*     */     
/*     */     public void println(char arg0) throws IOException {
/* 210 */       print(arg0);
/* 211 */       newLine();
/*     */     }
/*     */ 
/*     */     
/*     */     public void println(char[] arg0) throws IOException {
/* 216 */       print(arg0);
/* 217 */       newLine();
/*     */     }
/*     */ 
/*     */     
/*     */     public void println(double arg0) throws IOException {
/* 222 */       print(arg0);
/* 223 */       newLine();
/*     */     }
/*     */ 
/*     */     
/*     */     public void println(float arg0) throws IOException {
/* 228 */       print(arg0);
/* 229 */       newLine();
/*     */     }
/*     */ 
/*     */     
/*     */     public void println(int arg0) throws IOException {
/* 234 */       print(arg0);
/* 235 */       newLine();
/*     */     }
/*     */ 
/*     */     
/*     */     public void println(long arg0) throws IOException {
/* 240 */       print(arg0);
/* 241 */       newLine();
/*     */     }
/*     */ 
/*     */     
/*     */     public void println(Object arg0) throws IOException {
/* 246 */       print(arg0);
/* 247 */       newLine();
/*     */     }
/*     */ 
/*     */     
/*     */     public void println(String arg0) throws IOException {
/* 252 */       print(arg0);
/* 253 */       newLine();
/*     */     }
/*     */ 
/*     */     
/*     */     public void write(int c) throws IOException {
/* 258 */       if (this.buf != null) {
/* 259 */         this.buf.write(c);
/*     */       } else {
/* 261 */         getEnclosingWriter().write(c);
/*     */       } 
/*     */     }
/*     */ 
/*     */     
/*     */     public void write(char[] cbuf, int off, int len) throws IOException {
/* 267 */       if (this.buf != null) {
/* 268 */         this.buf.write(cbuf, off, len);
/*     */       } else {
/* 270 */         getEnclosingWriter().write(cbuf, off, len);
/*     */       } 
/*     */     }
/*     */ 
/*     */     
/*     */     public String getString() {
/* 276 */       return this.buf.toString();
/*     */     }
/*     */ 
/*     */     
/*     */     public Reader getReader() {
/* 281 */       return new CharArrayReader(this.buf.toCharArray());
/*     */     }
/*     */ 
/*     */     
/*     */     public void writeOut(Writer out) throws IOException {
/* 286 */       this.buf.writeTo(out);
/*     */     }
/*     */   }
/*     */   
/*     */   class TagWriter
/*     */     extends BodyContentImpl implements TransformControl {
/*     */     private final Tag tag;
/*     */     private final FreeMarkerPageContext pageContext;
/*     */     private boolean needPop = true;
/*     */     private final boolean needDoublePop;
/*     */     private boolean closed = false;
/*     */     
/*     */     TagWriter(Writer out, Tag tag, FreeMarkerPageContext pageContext, boolean needDoublePop) {
/* 299 */       super((JspWriter)out, false);
/* 300 */       this.needDoublePop = needDoublePop;
/* 301 */       this.tag = tag;
/* 302 */       this.pageContext = pageContext;
/*     */     }
/*     */ 
/*     */     
/*     */     public String toString() {
/* 307 */       return "TagWriter for " + this.tag.getClass().getName() + " wrapping a " + getEnclosingWriter().toString();
/*     */     }
/*     */     
/*     */     Tag getTag() {
/* 311 */       return this.tag;
/*     */     }
/*     */     
/*     */     FreeMarkerPageContext getPageContext() {
/* 315 */       return this.pageContext;
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public int onStart() throws TemplateModelException {
/*     */       try {
/* 322 */         int dst = this.tag.doStartTag();
/* 323 */         switch (dst) {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */           
/*     */           case 0:
/*     */           case 6:
/* 331 */             endEvaluation();
/* 332 */             return 0;
/*     */           
/*     */           case 2:
/* 335 */             if (TagTransformModel.this.isBodyTag) {
/* 336 */               initBuffer();
/* 337 */               BodyTag btag = (BodyTag)this.tag;
/* 338 */               btag.setBodyContent(this);
/* 339 */               btag.doInitBody();
/*     */             } else {
/* 341 */               throw new TemplateModelException("Can't buffer body since " + this.tag.getClass().getName() + " does not implement BodyTag.");
/*     */             } 
/*     */ 
/*     */           
/*     */           case 1:
/* 346 */             return 1;
/*     */         } 
/*     */         
/* 349 */         throw new RuntimeException("Illegal return value " + dst + " from " + this.tag.getClass().getName() + ".doStartTag()");
/*     */       
/*     */       }
/* 352 */       catch (Exception e) {
/* 353 */         throw TagTransformModel.this.toTemplateModelExceptionOrRethrow(e);
/*     */       } 
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public int afterBody() throws TemplateModelException {
/*     */       try {
/* 361 */         if (TagTransformModel.this.isIterationTag) {
/* 362 */           int dab = ((IterationTag)this.tag).doAfterBody();
/* 363 */           switch (dab) {
/*     */             case 0:
/* 365 */               endEvaluation();
/* 366 */               return 1;
/*     */             case 2:
/* 368 */               return 0;
/*     */           } 
/* 370 */           throw new TemplateModelException("Unexpected return value " + dab + "from " + this.tag.getClass().getName() + ".doAfterBody()");
/*     */         } 
/*     */         
/* 373 */         endEvaluation();
/* 374 */         return 1;
/* 375 */       } catch (Exception e) {
/* 376 */         throw TagTransformModel.this.toTemplateModelExceptionOrRethrow(e);
/*     */       } 
/*     */     }
/*     */     
/*     */     private void endEvaluation() throws JspException {
/* 381 */       if (this.needPop) {
/* 382 */         this.pageContext.popWriter();
/* 383 */         this.needPop = false;
/*     */       } 
/* 385 */       if (this.tag.doEndTag() == 5) {
/* 386 */         TagTransformModel.LOG.warn("Tag.SKIP_PAGE was ignored from a " + this.tag.getClass().getName() + " tag.");
/*     */       }
/*     */     }
/*     */ 
/*     */     
/*     */     public void onError(Throwable t) throws Throwable {
/* 392 */       if (TagTransformModel.this.isTryCatchFinally) {
/* 393 */         ((TryCatchFinally)this.tag).doCatch(t);
/*     */       } else {
/* 395 */         throw t;
/*     */       } 
/*     */     }
/*     */ 
/*     */     
/*     */     public void close() {
/* 401 */       if (this.closed) {
/*     */         return;
/*     */       }
/* 404 */       this.closed = true;
/*     */       
/* 406 */       if (this.needPop) {
/* 407 */         this.pageContext.popWriter();
/*     */       }
/* 409 */       this.pageContext.popTopTag();
/*     */       try {
/* 411 */         if (TagTransformModel.this.isTryCatchFinally) {
/* 412 */           ((TryCatchFinally)this.tag).doFinally();
/*     */         }
/*     */         
/* 415 */         this.tag.release();
/*     */       } finally {
/* 417 */         if (this.needDoublePop)
/* 418 */           this.pageContext.popWriter(); 
/*     */       } 
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\ext\jsp\TagTransformModel.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */