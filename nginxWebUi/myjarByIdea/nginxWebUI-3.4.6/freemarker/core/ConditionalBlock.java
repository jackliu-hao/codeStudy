package freemarker.core;

import freemarker.template.TemplateException;
import java.io.IOException;

final class ConditionalBlock extends TemplateElement {
   static final int TYPE_IF = 0;
   static final int TYPE_ELSE = 1;
   static final int TYPE_ELSE_IF = 2;
   final Expression condition;
   private final int type;

   ConditionalBlock(Expression condition, TemplateElements children, int type) {
      this.condition = condition;
      this.setChildren(children);
      this.type = type;
   }

   TemplateElement[] accept(Environment env) throws TemplateException, IOException {
      return this.condition != null && !this.condition.evalToBoolean(env) ? null : this.getChildBuffer();
   }

   protected String dump(boolean canonical) {
      StringBuilder buf = new StringBuilder();
      if (canonical) {
         buf.append('<');
      }

      buf.append(this.getNodeTypeSymbol());
      if (this.condition != null) {
         buf.append(' ');
         buf.append(this.condition.getCanonicalForm());
      }

      if (canonical) {
         buf.append(">");
         buf.append(this.getChildrenCanonicalForm());
         if (!(this.getParentElement() instanceof IfBlock)) {
            buf.append("</#if>");
         }
      }

      return buf.toString();
   }

   String getNodeTypeSymbol() {
      if (this.type == 1) {
         return "#else";
      } else if (this.type == 0) {
         return "#if";
      } else if (this.type == 2) {
         return "#elseif";
      } else {
         throw new BugException("Unknown type");
      }
   }

   int getParameterCount() {
      return 2;
   }

   Object getParameterValue(int idx) {
      switch (idx) {
         case 0:
            return this.condition;
         case 1:
            return this.type;
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
