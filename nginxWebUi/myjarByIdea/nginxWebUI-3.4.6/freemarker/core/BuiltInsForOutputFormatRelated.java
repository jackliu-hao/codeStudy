package freemarker.core;

import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;

class BuiltInsForOutputFormatRelated {
   abstract static class AbstractConverterBI extends MarkupOutputFormatBoundBuiltIn {
      protected TemplateModel calculateResult(Environment env) throws TemplateException {
         TemplateModel lhoTM = this.target.eval(env);
         Object lhoMOOrStr = EvalUtil.coerceModelToStringOrMarkup(lhoTM, this.target, (String)null, env);
         MarkupOutputFormat contextOF = this.outputFormat;
         if (lhoMOOrStr instanceof String) {
            return this.calculateResult((String)lhoMOOrStr, contextOF, env);
         } else {
            TemplateMarkupOutputModel lhoMO = (TemplateMarkupOutputModel)lhoMOOrStr;
            MarkupOutputFormat lhoOF = lhoMO.getOutputFormat();
            if (lhoOF != contextOF && !contextOF.isOutputFormatMixingAllowed()) {
               String lhoPlainTtext = lhoOF.getSourcePlainText(lhoMO);
               if (lhoPlainTtext == null) {
                  throw new _TemplateModelException(this.target, new Object[]{"The left side operand of ?", this.key, " is in ", new _DelayedToString(lhoOF), " format, which differs from the current output format, ", new _DelayedToString(contextOF), ". Conversion wasn't possible."});
               } else {
                  return contextOF.fromPlainTextByEscaping(lhoPlainTtext);
               }
            } else {
               return lhoMO;
            }
         }
      }

      protected abstract TemplateModel calculateResult(String var1, MarkupOutputFormat var2, Environment var3) throws TemplateException;
   }

   static class escBI extends AbstractConverterBI {
      protected TemplateModel calculateResult(String lho, MarkupOutputFormat outputFormat, Environment env) throws TemplateException {
         return outputFormat.fromPlainTextByEscaping(lho);
      }
   }

   static class no_escBI extends AbstractConverterBI {
      protected TemplateModel calculateResult(String lho, MarkupOutputFormat outputFormat, Environment env) throws TemplateException {
         return outputFormat.fromMarkup(lho);
      }
   }
}
