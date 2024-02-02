package freemarker.core;

import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

final class BodyInstruction extends TemplateElement {
   private List bodyParameters;

   BodyInstruction(List bodyParameters) {
      this.bodyParameters = bodyParameters;
   }

   List getBodyParameters() {
      return this.bodyParameters;
   }

   TemplateElement[] accept(Environment env) throws IOException, TemplateException {
      Context bodyContext = new Context(env);
      env.invokeNestedContent(bodyContext);
      return null;
   }

   protected String dump(boolean canonical) {
      StringBuilder sb = new StringBuilder();
      if (canonical) {
         sb.append('<');
      }

      sb.append(this.getNodeTypeSymbol());
      if (this.bodyParameters != null) {
         for(int i = 0; i < this.bodyParameters.size(); ++i) {
            sb.append(' ');
            sb.append(((Expression)this.bodyParameters.get(i)).getCanonicalForm());
         }
      }

      if (canonical) {
         sb.append('>');
      }

      return sb.toString();
   }

   String getNodeTypeSymbol() {
      return "#nested";
   }

   int getParameterCount() {
      return this.bodyParameters != null ? this.bodyParameters.size() : 0;
   }

   Object getParameterValue(int idx) {
      this.checkIndex(idx);
      return this.bodyParameters.get(idx);
   }

   ParameterRole getParameterRole(int idx) {
      this.checkIndex(idx);
      return ParameterRole.PASSED_VALUE;
   }

   private void checkIndex(int idx) {
      if (this.bodyParameters == null || idx >= this.bodyParameters.size()) {
         throw new IndexOutOfBoundsException();
      }
   }

   boolean isShownInStackTrace() {
      return true;
   }

   boolean isNestedBlockRepeater() {
      return false;
   }

   class Context implements LocalContext {
      Macro.Context invokingMacroContext;
      Environment.Namespace bodyVars;

      Context(Environment env) throws TemplateException {
         this.invokingMacroContext = env.getCurrentMacroContext();
         List bodyParameterNames = this.invokingMacroContext.nestedContentParameterNames;
         if (BodyInstruction.this.bodyParameters != null) {
            for(int i = 0; i < BodyInstruction.this.bodyParameters.size(); ++i) {
               Expression exp = (Expression)BodyInstruction.this.bodyParameters.get(i);
               TemplateModel tm = exp.eval(env);
               if (bodyParameterNames != null && i < bodyParameterNames.size()) {
                  String bodyParameterName = (String)bodyParameterNames.get(i);
                  if (this.bodyVars == null) {
                     this.bodyVars = env.new Namespace();
                  }

                  this.bodyVars.put(bodyParameterName, tm != null ? tm : (BodyInstruction.this.getTemplate().getConfiguration().getFallbackOnNullLoopVariable() ? null : TemplateNullModel.INSTANCE));
               }
            }
         }

      }

      public TemplateModel getLocalVariable(String name) throws TemplateModelException {
         return this.bodyVars == null ? null : this.bodyVars.get(name);
      }

      public Collection getLocalVariableNames() {
         List bodyParameterNames = this.invokingMacroContext.nestedContentParameterNames;
         return bodyParameterNames == null ? Collections.EMPTY_LIST : bodyParameterNames;
      }
   }
}
