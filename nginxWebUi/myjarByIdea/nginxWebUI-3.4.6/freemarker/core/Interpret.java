package freemarker.core;

import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateScalarModel;
import freemarker.template.TemplateSequenceModel;
import freemarker.template.TemplateTransformModel;
import freemarker.template._TemplateAPI;
import java.io.IOException;
import java.io.StringReader;
import java.io.Writer;
import java.util.Map;

class Interpret extends OutputFormatBoundBuiltIn {
   protected TemplateModel calculateResult(Environment env) throws TemplateException {
      TemplateModel model = this.target.eval(env);
      Expression sourceExpr = null;
      String id = "anonymous_interpreted";
      if (model instanceof TemplateSequenceModel) {
         sourceExpr = (Expression)(new DynamicKeyName(this.target, new NumberLiteral(0))).copyLocationFrom(this.target);
         if (((TemplateSequenceModel)model).size() > 1) {
            id = ((Expression)(new DynamicKeyName(this.target, new NumberLiteral(1))).copyLocationFrom(this.target)).evalAndCoerceToPlainText(env);
         }
      } else {
         if (!(model instanceof TemplateScalarModel)) {
            throw new UnexpectedTypeException(this.target, model, "sequence or string", new Class[]{TemplateSequenceModel.class, TemplateScalarModel.class}, env);
         }

         sourceExpr = this.target;
      }

      String templateSource = sourceExpr.evalAndCoerceToPlainText(env);
      Template parentTemplate = env.getConfiguration().getIncompatibleImprovements().intValue() >= _TemplateAPI.VERSION_INT_2_3_26 ? env.getCurrentTemplate() : env.getTemplate();

      Template interpretedTemplate;
      try {
         ParserConfiguration pCfg = parentTemplate.getParserConfiguration();
         if (((ParserConfiguration)pCfg).getOutputFormat() != this.outputFormat) {
            pCfg = new _ParserConfigurationWithInheritedFormat((ParserConfiguration)pCfg, this.outputFormat, this.autoEscapingPolicy);
         }

         interpretedTemplate = new Template((parentTemplate.getName() != null ? parentTemplate.getName() : "nameless_template") + "->" + id, (String)null, new StringReader(templateSource), parentTemplate.getConfiguration(), (ParserConfiguration)pCfg, (String)null);
      } catch (IOException var9) {
         throw new _MiscTemplateException(this, var9, env, new Object[]{"Template parsing with \"?", this.key, "\" has failed with this error:\n\n", "---begin-message---\n", new _DelayedGetMessage(var9), "\n---end-message---", "\n\nThe failed expression:"});
      }

      interpretedTemplate.setLocale(env.getLocale());
      return new TemplateProcessorModel(interpretedTemplate);
   }

   private class TemplateProcessorModel implements TemplateTransformModel {
      private final Template template;

      TemplateProcessorModel(Template template) {
         this.template = template;
      }

      public Writer getWriter(final Writer out, Map args) throws TemplateModelException, IOException {
         try {
            Environment env = Environment.getCurrentEnvironment();
            boolean lastFIRE = env.setFastInvalidReferenceExceptions(false);

            try {
               env.include(this.template);
            } finally {
               env.setFastInvalidReferenceExceptions(lastFIRE);
            }
         } catch (Exception var9) {
            throw new _TemplateModelException(var9, new Object[]{"Template created with \"?", Interpret.this.key, "\" has stopped with this error:\n\n", "---begin-message---\n", new _DelayedGetMessage(var9), "\n---end-message---"});
         }

         return new Writer(out) {
            public void close() {
            }

            public void flush() throws IOException {
               out.flush();
            }

            public void write(char[] cbuf, int off, int len) throws IOException {
               out.write(cbuf, off, len);
            }
         };
      }
   }
}
