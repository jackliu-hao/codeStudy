package freemarker.core;

import freemarker.template.SimpleScalar;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import java.io.IOException;
import java.io.StringWriter;

final class BlockAssignment extends TemplateElement {
   private final String varName;
   private final Expression namespaceExp;
   private final int scope;
   private final MarkupOutputFormat<?> markupOutputFormat;

   BlockAssignment(TemplateElements children, String varName, int scope, Expression namespaceExp, MarkupOutputFormat<?> markupOutputFormat) {
      this.setChildren(children);
      this.varName = varName;
      this.namespaceExp = namespaceExp;
      this.scope = scope;
      this.markupOutputFormat = markupOutputFormat;
   }

   TemplateElement[] accept(Environment env) throws TemplateException, IOException {
      TemplateElement[] children = this.getChildBuffer();
      TemplateModel value;
      if (children != null) {
         StringWriter out = new StringWriter();
         env.visit(children, out);
         value = this.capturedStringToModel(out.toString());
      } else {
         value = this.capturedStringToModel("");
      }

      if (this.namespaceExp != null) {
         TemplateModel uncheckedNamespace = this.namespaceExp.eval(env);

         Environment.Namespace namespace;
         try {
            namespace = (Environment.Namespace)uncheckedNamespace;
         } catch (ClassCastException var7) {
            throw new NonNamespaceException(this.namespaceExp, uncheckedNamespace, env);
         }

         if (namespace == null) {
            throw InvalidReferenceException.getInstance(this.namespaceExp, env);
         }

         namespace.put(this.varName, value);
      } else if (this.scope == 1) {
         env.setVariable(this.varName, value);
      } else if (this.scope == 3) {
         env.setGlobalVariable(this.varName, value);
      } else {
         if (this.scope != 2) {
            throw new BugException("Unhandled scope");
         }

         env.setLocalVariable(this.varName, value);
      }

      return null;
   }

   private TemplateModel capturedStringToModel(String s) throws TemplateModelException {
      return (TemplateModel)(this.markupOutputFormat == null ? new SimpleScalar(s) : this.markupOutputFormat.fromMarkup(s));
   }

   protected String dump(boolean canonical) {
      StringBuilder sb = new StringBuilder();
      if (canonical) {
         sb.append("<");
      }

      sb.append(this.getNodeTypeSymbol());
      sb.append(' ');
      sb.append(this.varName);
      if (this.namespaceExp != null) {
         sb.append(" in ");
         sb.append(this.namespaceExp.getCanonicalForm());
      }

      if (canonical) {
         sb.append('>');
         sb.append(this.getChildrenCanonicalForm());
         sb.append("</");
         sb.append(this.getNodeTypeSymbol());
         sb.append('>');
      } else {
         sb.append(" = .nested_output");
      }

      return sb.toString();
   }

   String getNodeTypeSymbol() {
      return Assignment.getDirectiveName(this.scope);
   }

   int getParameterCount() {
      return 3;
   }

   Object getParameterValue(int idx) {
      switch (idx) {
         case 0:
            return this.varName;
         case 1:
            return this.scope;
         case 2:
            return this.namespaceExp;
         default:
            throw new IndexOutOfBoundsException();
      }
   }

   ParameterRole getParameterRole(int idx) {
      switch (idx) {
         case 0:
            return ParameterRole.ASSIGNMENT_TARGET;
         case 1:
            return ParameterRole.VARIABLE_SCOPE;
         case 2:
            return ParameterRole.NAMESPACE;
         default:
            throw new IndexOutOfBoundsException();
      }
   }

   boolean isNestedBlockRepeater() {
      return false;
   }
}
