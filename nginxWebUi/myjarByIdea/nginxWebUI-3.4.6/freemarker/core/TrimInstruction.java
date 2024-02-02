package freemarker.core;

final class TrimInstruction extends TemplateElement {
   static final int TYPE_T = 0;
   static final int TYPE_LT = 1;
   static final int TYPE_RT = 2;
   static final int TYPE_NT = 3;
   final boolean left;
   final boolean right;

   TrimInstruction(boolean left, boolean right) {
      this.left = left;
      this.right = right;
   }

   TemplateElement[] accept(Environment env) {
      return null;
   }

   protected String dump(boolean canonical) {
      StringBuilder sb = new StringBuilder();
      if (canonical) {
         sb.append('<');
      }

      sb.append(this.getNodeTypeSymbol());
      if (canonical) {
         sb.append("/>");
      }

      return sb.toString();
   }

   String getNodeTypeSymbol() {
      if (this.left && this.right) {
         return "#t";
      } else if (this.left) {
         return "#lt";
      } else {
         return this.right ? "#rt" : "#nt";
      }
   }

   boolean isIgnorable(boolean stripWhitespace) {
      return true;
   }

   int getParameterCount() {
      return 1;
   }

   Object getParameterValue(int idx) {
      if (idx != 0) {
         throw new IndexOutOfBoundsException();
      } else {
         byte type;
         if (this.left && this.right) {
            type = 0;
         } else if (this.left) {
            type = 1;
         } else if (this.right) {
            type = 2;
         } else {
            type = 3;
         }

         return Integer.valueOf(type);
      }
   }

   ParameterRole getParameterRole(int idx) {
      if (idx != 0) {
         throw new IndexOutOfBoundsException();
      } else {
         return ParameterRole.AST_NODE_SUBTYPE;
      }
   }

   boolean isOutputCacheable() {
      return true;
   }

   boolean isNestedBlockRepeater() {
      return false;
   }
}
