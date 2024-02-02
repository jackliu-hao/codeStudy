package freemarker.core;

import freemarker.template.SimpleSequence;
import freemarker.template.TemplateException;
import freemarker.template.TemplateHashModel;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateNodeModel;
import freemarker.template.TemplateScalarModel;
import freemarker.template.TemplateSequenceModel;
import freemarker.template._TemplateAPI;
import java.io.IOException;

final class RecurseNode extends TemplateElement {
   Expression targetNode;
   Expression namespaces;

   RecurseNode(Expression targetNode, Expression namespaces) {
      this.targetNode = targetNode;
      this.namespaces = namespaces;
   }

   TemplateElement[] accept(Environment env) throws IOException, TemplateException {
      TemplateModel node = this.targetNode == null ? null : this.targetNode.eval(env);
      if (node != null && !(node instanceof TemplateNodeModel)) {
         throw new NonNodeException(this.targetNode, node, "node", env);
      } else {
         TemplateModel nss = this.namespaces == null ? null : this.namespaces.eval(env);
         if (this.namespaces instanceof StringLiteral) {
            nss = env.importLib((String)((TemplateScalarModel)nss).getAsString(), (String)null);
         } else if (this.namespaces instanceof ListLiteral) {
            nss = ((ListLiteral)this.namespaces).evaluateStringsToNamespaces(env);
         }

         if (nss != null) {
            if (nss instanceof TemplateHashModel) {
               SimpleSequence ss = new SimpleSequence(1, _TemplateAPI.SAFE_OBJECT_WRAPPER);
               ss.add(nss);
               nss = ss;
            } else if (!(nss instanceof TemplateSequenceModel)) {
               if (this.namespaces != null) {
                  throw new NonSequenceException(this.namespaces, (TemplateModel)nss, env);
               }

               throw new _MiscTemplateException(env, "Expecting a sequence of namespaces after \"using\"");
            }
         }

         env.recurse((TemplateNodeModel)node, (TemplateSequenceModel)nss);
         return null;
      }
   }

   protected String dump(boolean canonical) {
      StringBuilder sb = new StringBuilder();
      if (canonical) {
         sb.append('<');
      }

      sb.append(this.getNodeTypeSymbol());
      if (this.targetNode != null) {
         sb.append(' ');
         sb.append(this.targetNode.getCanonicalForm());
      }

      if (this.namespaces != null) {
         sb.append(" using ");
         sb.append(this.namespaces.getCanonicalForm());
      }

      if (canonical) {
         sb.append("/>");
      }

      return sb.toString();
   }

   String getNodeTypeSymbol() {
      return "#recurse";
   }

   int getParameterCount() {
      return 2;
   }

   Object getParameterValue(int idx) {
      switch (idx) {
         case 0:
            return this.targetNode;
         case 1:
            return this.namespaces;
         default:
            throw new IndexOutOfBoundsException();
      }
   }

   ParameterRole getParameterRole(int idx) {
      switch (idx) {
         case 0:
            return ParameterRole.NODE;
         case 1:
            return ParameterRole.NAMESPACE;
         default:
            throw new IndexOutOfBoundsException();
      }
   }

   boolean isNestedBlockRepeater() {
      return false;
   }

   boolean isShownInStackTrace() {
      return true;
   }
}
