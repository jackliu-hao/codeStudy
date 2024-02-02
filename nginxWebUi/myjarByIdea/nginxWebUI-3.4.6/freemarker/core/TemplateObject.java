package freemarker.core;

import freemarker.template.Template;

/** @deprecated */
@Deprecated
public abstract class TemplateObject {
   private Template template;
   int beginColumn;
   int beginLine;
   int endColumn;
   int endLine;
   static final int RUNTIME_EVAL_LINE_DISPLACEMENT = -1000000000;

   void copyFieldsFrom(TemplateObject that) {
      this.template = that.template;
      this.beginColumn = that.beginColumn;
      this.beginLine = that.beginLine;
      this.endColumn = that.endColumn;
      this.endLine = that.endLine;
   }

   final void setLocation(Template template, Token begin, Token end) {
      this.setLocation(template, begin.beginColumn, begin.beginLine, end.endColumn, end.endLine);
   }

   final void setLocation(Template template, Token tagBegin, Token tagEnd, TemplateElements children) {
      TemplateElement lastChild = children.getLast();
      if (lastChild != null) {
         this.setLocation(template, (Token)tagBegin, (TemplateObject)lastChild);
      } else {
         this.setLocation(template, tagBegin, tagEnd);
      }

   }

   final void setLocation(Template template, Token begin, TemplateObject end) {
      this.setLocation(template, begin.beginColumn, begin.beginLine, end.endColumn, end.endLine);
   }

   final void setLocation(Template template, TemplateObject begin, Token end) {
      this.setLocation(template, begin.beginColumn, begin.beginLine, end.endColumn, end.endLine);
   }

   final void setLocation(Template template, TemplateObject begin, TemplateObject end) {
      this.setLocation(template, begin.beginColumn, begin.beginLine, end.endColumn, end.endLine);
   }

   void setLocation(Template template, int beginColumn, int beginLine, int endColumn, int endLine) {
      this.template = template;
      this.beginColumn = beginColumn;
      this.beginLine = beginLine;
      this.endColumn = endColumn;
      this.endLine = endLine;
   }

   public final int getBeginColumn() {
      return this.beginColumn;
   }

   public final int getBeginLine() {
      return this.beginLine;
   }

   public final int getEndColumn() {
      return this.endColumn;
   }

   public final int getEndLine() {
      return this.endLine;
   }

   public String getStartLocation() {
      return _MessageUtil.formatLocationForEvaluationError(this.template, this.beginLine, this.beginColumn);
   }

   public String getStartLocationQuoted() {
      return this.getStartLocation();
   }

   public String getEndLocation() {
      return _MessageUtil.formatLocationForEvaluationError(this.template, this.endLine, this.endColumn);
   }

   public String getEndLocationQuoted() {
      return this.getEndLocation();
   }

   public final String getSource() {
      String s;
      if (this.template != null) {
         s = this.template.getSource(this.beginColumn, this.beginLine, this.endColumn, this.endLine);
      } else {
         s = null;
      }

      return s != null ? s : this.getCanonicalForm();
   }

   public String toString() {
      String s;
      try {
         s = this.getSource();
      } catch (Exception var3) {
         s = null;
      }

      return s != null ? s : this.getCanonicalForm();
   }

   public boolean contains(int column, int line) {
      if (line >= this.beginLine && line <= this.endLine) {
         if (line == this.beginLine && column < this.beginColumn) {
            return false;
         } else {
            return line != this.endLine || column <= this.endColumn;
         }
      } else {
         return false;
      }
   }

   public Template getTemplate() {
      return this.template;
   }

   TemplateObject copyLocationFrom(TemplateObject from) {
      this.template = from.template;
      this.beginColumn = from.beginColumn;
      this.beginLine = from.beginLine;
      this.endColumn = from.endColumn;
      this.endLine = from.endLine;
      return this;
   }

   public abstract String getCanonicalForm();

   abstract String getNodeTypeSymbol();

   abstract int getParameterCount();

   abstract Object getParameterValue(int var1);

   abstract ParameterRole getParameterRole(int var1);
}
