/*     */ package freemarker.core;
/*     */ 
/*     */ import freemarker.template.Template;
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
/*     */ @Deprecated
/*     */ public abstract class TemplateObject
/*     */ {
/*     */   private Template template;
/*     */   int beginColumn;
/*     */   int beginLine;
/*     */   int endColumn;
/*     */   int endLine;
/*     */   static final int RUNTIME_EVAL_LINE_DISPLACEMENT = -1000000000;
/*     */   
/*     */   void copyFieldsFrom(TemplateObject that) {
/*  48 */     this.template = that.template;
/*  49 */     this.beginColumn = that.beginColumn;
/*  50 */     this.beginLine = that.beginLine;
/*  51 */     this.endColumn = that.endColumn;
/*  52 */     this.endLine = that.endLine;
/*     */   }
/*     */   
/*     */   final void setLocation(Template template, Token begin, Token end) {
/*  56 */     setLocation(template, begin.beginColumn, begin.beginLine, end.endColumn, end.endLine);
/*     */   }
/*     */   
/*     */   final void setLocation(Template template, Token tagBegin, Token tagEnd, TemplateElements children) {
/*  60 */     TemplateElement lastChild = children.getLast();
/*  61 */     if (lastChild != null) {
/*     */       
/*  63 */       setLocation(template, tagBegin, lastChild);
/*     */     } else {
/*     */       
/*  66 */       setLocation(template, tagBegin, tagEnd);
/*     */     } 
/*     */   }
/*     */   
/*     */   final void setLocation(Template template, Token begin, TemplateObject end) {
/*  71 */     setLocation(template, begin.beginColumn, begin.beginLine, end.endColumn, end.endLine);
/*     */   }
/*     */   
/*     */   final void setLocation(Template template, TemplateObject begin, Token end) {
/*  75 */     setLocation(template, begin.beginColumn, begin.beginLine, end.endColumn, end.endLine);
/*     */   }
/*     */   
/*     */   final void setLocation(Template template, TemplateObject begin, TemplateObject end) {
/*  79 */     setLocation(template, begin.beginColumn, begin.beginLine, end.endColumn, end.endLine);
/*     */   }
/*     */   
/*     */   void setLocation(Template template, int beginColumn, int beginLine, int endColumn, int endLine) {
/*  83 */     this.template = template;
/*  84 */     this.beginColumn = beginColumn;
/*  85 */     this.beginLine = beginLine;
/*  86 */     this.endColumn = endColumn;
/*  87 */     this.endLine = endLine;
/*     */   }
/*     */   
/*     */   public final int getBeginColumn() {
/*  91 */     return this.beginColumn;
/*     */   }
/*     */   
/*     */   public final int getBeginLine() {
/*  95 */     return this.beginLine;
/*     */   }
/*     */   
/*     */   public final int getEndColumn() {
/*  99 */     return this.endColumn;
/*     */   }
/*     */   
/*     */   public final int getEndLine() {
/* 103 */     return this.endLine;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getStartLocation() {
/* 111 */     return _MessageUtil.formatLocationForEvaluationError(this.template, this.beginLine, this.beginColumn);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getStartLocationQuoted() {
/* 119 */     return getStartLocation();
/*     */   }
/*     */   
/*     */   public String getEndLocation() {
/* 123 */     return _MessageUtil.formatLocationForEvaluationError(this.template, this.endLine, this.endColumn);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getEndLocationQuoted() {
/* 131 */     return getEndLocation();
/*     */   }
/*     */   
/*     */   public final String getSource() {
/*     */     String s;
/* 136 */     if (this.template != null) {
/* 137 */       s = this.template.getSource(this.beginColumn, this.beginLine, this.endColumn, this.endLine);
/*     */     } else {
/* 139 */       s = null;
/*     */     } 
/*     */ 
/*     */     
/* 143 */     return (s != null) ? s : getCanonicalForm();
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/*     */     String s;
/*     */     try {
/* 150 */       s = getSource();
/* 151 */     } catch (Exception e) {
/* 152 */       s = null;
/*     */     } 
/* 154 */     return (s != null) ? s : getCanonicalForm();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean contains(int column, int line) {
/* 162 */     if (line < this.beginLine || line > this.endLine) {
/* 163 */       return false;
/*     */     }
/* 165 */     if (line == this.beginLine && 
/* 166 */       column < this.beginColumn) {
/* 167 */       return false;
/*     */     }
/*     */     
/* 170 */     if (line == this.endLine && 
/* 171 */       column > this.endColumn) {
/* 172 */       return false;
/*     */     }
/*     */     
/* 175 */     return true;
/*     */   }
/*     */   
/*     */   public Template getTemplate() {
/* 179 */     return this.template;
/*     */   }
/*     */   
/*     */   TemplateObject copyLocationFrom(TemplateObject from) {
/* 183 */     this.template = from.template;
/* 184 */     this.beginColumn = from.beginColumn;
/* 185 */     this.beginLine = from.beginLine;
/* 186 */     this.endColumn = from.endColumn;
/* 187 */     this.endLine = from.endLine;
/* 188 */     return this;
/*     */   }
/*     */   
/*     */   public abstract String getCanonicalForm();
/*     */   
/*     */   abstract String getNodeTypeSymbol();
/*     */   
/*     */   abstract int getParameterCount();
/*     */   
/*     */   abstract Object getParameterValue(int paramInt);
/*     */   
/*     */   abstract ParameterRole getParameterRole(int paramInt);
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\core\TemplateObject.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */