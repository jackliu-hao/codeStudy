package freemarker.core;

import freemarker.debug.impl.DebuggerService;
import freemarker.template.TemplateException;
import java.io.IOException;

/** @deprecated */
@Deprecated
public class DebugBreak extends TemplateElement {
   public DebugBreak(TemplateElement nestedBlock) {
      this.addChild(nestedBlock);
      this.copyLocationFrom(nestedBlock);
   }

   protected TemplateElement[] accept(Environment env) throws TemplateException, IOException {
      if (!DebuggerService.suspendEnvironment(env, this.getTemplate().getSourceName(), this.getChild(0).getBeginLine())) {
         return this.getChild(0).accept(env);
      } else {
         throw new StopException(env, "Stopped by debugger");
      }
   }

   protected String dump(boolean canonical) {
      if (canonical) {
         StringBuilder sb = new StringBuilder();
         sb.append("<#-- ");
         sb.append("debug break");
         if (this.getChildCount() == 0) {
            sb.append(" /-->");
         } else {
            sb.append(" -->");
            sb.append(this.getChild(0).getCanonicalForm());
            sb.append("<#--/ debug break -->");
         }

         return sb.toString();
      } else {
         return "debug break";
      }
   }

   String getNodeTypeSymbol() {
      return "#debug_break";
   }

   int getParameterCount() {
      return 0;
   }

   Object getParameterValue(int idx) {
      throw new IndexOutOfBoundsException();
   }

   ParameterRole getParameterRole(int idx) {
      throw new IndexOutOfBoundsException();
   }

   boolean isNestedBlockRepeater() {
      return false;
   }
}
