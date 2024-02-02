package freemarker.core;

import freemarker.template.Template;
import freemarker.template.TemplateException;
import java.io.IOException;

class ThreadInterruptionSupportTemplatePostProcessor extends TemplatePostProcessor {
   public void postProcess(Template t) throws TemplatePostProcessorException {
      TemplateElement te = t.getRootTreeNode();
      this.addInterruptionChecks(te);
   }

   private void addInterruptionChecks(TemplateElement te) throws TemplatePostProcessorException {
      if (te != null) {
         int childCount = te.getChildCount();

         for(int i = 0; i < childCount; ++i) {
            this.addInterruptionChecks(te.getChild(i));
         }

         if (te.isNestedBlockRepeater()) {
            try {
               te.addChild(0, new ThreadInterruptionCheck(te));
            } catch (ParseException var4) {
               throw new TemplatePostProcessorException("Unexpected error; see cause", var4);
            }
         }

      }
   }

   static class TemplateProcessingThreadInterruptedException extends FlowControlException {
      TemplateProcessingThreadInterruptedException() {
         super("Template processing thread \"interrupted\" flag was set.");
      }
   }

   static class ThreadInterruptionCheck extends TemplateElement {
      private ThreadInterruptionCheck(TemplateElement te) throws ParseException {
         this.setLocation(te.getTemplate(), te.beginColumn, te.beginLine, te.beginColumn, te.beginLine);
      }

      TemplateElement[] accept(Environment env) throws TemplateException, IOException {
         if (Thread.currentThread().isInterrupted()) {
            throw new TemplateProcessingThreadInterruptedException();
         } else {
            return null;
         }
      }

      protected String dump(boolean canonical) {
         return canonical ? "" : "<#--" + this.getNodeTypeSymbol() + "--#>";
      }

      String getNodeTypeSymbol() {
         return "##threadInterruptionCheck";
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

      // $FF: synthetic method
      ThreadInterruptionCheck(TemplateElement x0, Object x1) throws ParseException {
         this(x0);
      }
   }
}
