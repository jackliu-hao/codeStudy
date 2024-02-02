package freemarker.core;

final class Case extends TemplateElement {
   static final int TYPE_CASE = 0;
   static final int TYPE_DEFAULT = 1;
   Expression condition;

   Case(Expression matchingValue, TemplateElements children) {
      this.condition = matchingValue;
      this.setChildren(children);
   }

   TemplateElement[] accept(Environment env) {
      return this.getChildBuffer();
   }

   protected String dump(boolean canonical) {
      StringBuilder sb = new StringBuilder();
      if (canonical) {
         sb.append('<');
      }

      sb.append(this.getNodeTypeSymbol());
      if (this.condition != null) {
         sb.append(' ');
         sb.append(this.condition.getCanonicalForm());
      }

      if (canonical) {
         sb.append('>');
         sb.append(this.getChildrenCanonicalForm());
      }

      return sb.toString();
   }

   String getNodeTypeSymbol() {
      return this.condition != null ? "#case" : "#default";
   }

   int getParameterCount() {
      return 2;
   }

   Object getParameterValue(int idx) {
      switch (idx) {
         case 0:
            return this.condition;
         case 1:
            return this.condition != null ? 0 : 1;
         default:
            throw new IndexOutOfBoundsException();
      }
   }

   ParameterRole getParameterRole(int idx) {
      switch (idx) {
         case 0:
            return ParameterRole.CONDITION;
         case 1:
            return ParameterRole.AST_NODE_SUBTYPE;
         default:
            throw new IndexOutOfBoundsException();
      }
   }

   boolean isNestedBlockRepeater() {
      return false;
   }
}
