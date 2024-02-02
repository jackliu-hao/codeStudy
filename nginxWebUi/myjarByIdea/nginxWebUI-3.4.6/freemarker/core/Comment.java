package freemarker.core;

import freemarker.template.utility.StringUtil;

/** @deprecated */
@Deprecated
public final class Comment extends TemplateElement {
   private final String text;

   Comment(String text) {
      this.text = text;
   }

   TemplateElement[] accept(Environment env) {
      return null;
   }

   protected String dump(boolean canonical) {
      return canonical ? "<#--" + this.text + "-->" : "comment " + StringUtil.jQuote(this.text.trim());
   }

   String getNodeTypeSymbol() {
      return "#--...--";
   }

   int getParameterCount() {
      return 1;
   }

   Object getParameterValue(int idx) {
      if (idx != 0) {
         throw new IndexOutOfBoundsException();
      } else {
         return this.text;
      }
   }

   ParameterRole getParameterRole(int idx) {
      if (idx != 0) {
         throw new IndexOutOfBoundsException();
      } else {
         return ParameterRole.CONTENT;
      }
   }

   public String getText() {
      return this.text;
   }

   boolean isOutputCacheable() {
      return true;
   }

   boolean isNestedBlockRepeater() {
      return false;
   }
}
