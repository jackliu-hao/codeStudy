package freemarker.core;

import freemarker.template.TemplateException;
import java.io.IOException;

class EscapeBlock extends TemplateElement {
   private final String variable;
   private final Expression expr;
   private Expression escapedExpr;

   EscapeBlock(String variable, Expression expr, Expression escapedExpr) {
      this.variable = variable;
      this.expr = expr;
      this.escapedExpr = escapedExpr;
   }

   void setContent(TemplateElements children) {
      this.setChildren(children);
      this.escapedExpr = null;
   }

   TemplateElement[] accept(Environment env) throws TemplateException, IOException {
      return this.getChildBuffer();
   }

   Expression doEscape(Expression expression) throws ParseException {
      try {
         return this.escapedExpr.deepCloneWithIdentifierReplaced(this.variable, expression, new Expression.ReplacemenetState());
      } catch (UncheckedParseException var3) {
         throw var3.getParseException();
      }
   }

   protected String dump(boolean canonical) {
      StringBuilder sb = new StringBuilder();
      if (canonical) {
         sb.append('<');
      }

      sb.append(this.getNodeTypeSymbol()).append(' ').append(_CoreStringUtils.toFTLTopLevelIdentifierReference(this.variable)).append(" as ").append(this.expr.getCanonicalForm());
      if (canonical) {
         sb.append('>');
         sb.append(this.getChildrenCanonicalForm());
         sb.append("</").append(this.getNodeTypeSymbol()).append('>');
      }

      return sb.toString();
   }

   String getNodeTypeSymbol() {
      return "#escape";
   }

   int getParameterCount() {
      return 2;
   }

   Object getParameterValue(int idx) {
      switch (idx) {
         case 0:
            return this.variable;
         case 1:
            return this.expr;
         default:
            throw new IndexOutOfBoundsException();
      }
   }

   ParameterRole getParameterRole(int idx) {
      switch (idx) {
         case 0:
            return ParameterRole.PLACEHOLDER_VARIABLE;
         case 1:
            return ParameterRole.EXPRESSION_TEMPLATE;
         default:
            throw new IndexOutOfBoundsException();
      }
   }

   boolean isOutputCacheable() {
      return true;
   }

   boolean isNestedBlockRepeater() {
      return false;
   }
}
