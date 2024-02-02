package freemarker.core;

import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;

abstract class BuiltInForLoopVariable extends SpecialBuiltIn {
   private String loopVarName;

   void bindToLoopVariable(String loopVarName) {
      this.loopVarName = loopVarName;
   }

   TemplateModel _eval(Environment env) throws TemplateException {
      IteratorBlock.IterationContext iterCtx = env.findEnclosingIterationContextWithVisibleVariable(this.loopVarName);
      if (iterCtx == null) {
         throw new _MiscTemplateException(this, env, new Object[]{"There's no iteration in context that uses loop variable ", new _DelayedJQuote(this.loopVarName), "."});
      } else {
         return this.calculateResult(iterCtx, env);
      }
   }

   abstract TemplateModel calculateResult(IteratorBlock.IterationContext var1, Environment var2) throws TemplateException;
}
