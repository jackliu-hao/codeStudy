package freemarker.core;

import freemarker.ext.beans.BeanModel;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateBooleanModel;
import freemarker.template.TemplateCollectionModel;
import freemarker.template.TemplateDateModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateHashModel;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateNumberModel;
import freemarker.template.TemplateScalarModel;
import freemarker.template.TemplateSequenceModel;
import freemarker.template.utility.UndeclaredThrowableException;

/** @deprecated */
@Deprecated
public abstract class Expression extends TemplateObject {
   TemplateModel constantValue;

   abstract TemplateModel _eval(Environment var1) throws TemplateException;

   abstract boolean isLiteral();

   final void setLocation(Template template, int beginColumn, int beginLine, int endColumn, int endLine) {
      super.setLocation(template, beginColumn, beginLine, endColumn, endLine);
      if (this.isLiteral()) {
         try {
            this.constantValue = this._eval((Environment)null);
         } catch (Exception var7) {
         }
      }

   }

   /** @deprecated */
   @Deprecated
   public final TemplateModel getAsTemplateModel(Environment env) throws TemplateException {
      return this.eval(env);
   }

   void enableLazilyGeneratedResult() {
   }

   final TemplateModel eval(Environment env) throws TemplateException {
      try {
         return this.constantValue != null ? this.constantValue : this._eval(env);
      } catch (TemplateException | FlowControlException var3) {
         throw var3;
      } catch (Exception var4) {
         if (env != null && EvalUtil.shouldWrapUncheckedException(var4, env)) {
            throw new _MiscTemplateException(this, var4, env, "Expression has thrown an unchecked exception; see the cause exception.");
         } else if (var4 instanceof RuntimeException) {
            throw (RuntimeException)var4;
         } else {
            throw new UndeclaredThrowableException(var4);
         }
      }
   }

   String evalAndCoerceToPlainText(Environment env) throws TemplateException {
      return EvalUtil.coerceModelToPlainText(this.eval(env), this, (String)null, env);
   }

   String evalAndCoerceToPlainText(Environment env, String seqTip) throws TemplateException {
      return EvalUtil.coerceModelToPlainText(this.eval(env), this, seqTip, env);
   }

   Object evalAndCoerceToStringOrMarkup(Environment env) throws TemplateException {
      return EvalUtil.coerceModelToStringOrMarkup(this.eval(env), this, (String)null, env);
   }

   Object evalAndCoerceToStringOrMarkup(Environment env, String seqTip) throws TemplateException {
      return EvalUtil.coerceModelToStringOrMarkup(this.eval(env), this, seqTip, env);
   }

   String evalAndCoerceToStringOrUnsupportedMarkup(Environment env) throws TemplateException {
      return EvalUtil.coerceModelToStringOrUnsupportedMarkup(this.eval(env), this, (String)null, env);
   }

   String evalAndCoerceToStringOrUnsupportedMarkup(Environment env, String seqTip) throws TemplateException {
      return EvalUtil.coerceModelToStringOrUnsupportedMarkup(this.eval(env), this, seqTip, env);
   }

   Number evalToNumber(Environment env) throws TemplateException {
      TemplateModel model = this.eval(env);
      return this.modelToNumber(model, env);
   }

   final Number modelToNumber(TemplateModel model, Environment env) throws TemplateException {
      if (model instanceof TemplateNumberModel) {
         return EvalUtil.modelToNumber((TemplateNumberModel)model, this);
      } else {
         throw new NonNumericalException(this, model, env);
      }
   }

   boolean evalToBoolean(Environment env) throws TemplateException {
      return this.evalToBoolean(env, (Configuration)null);
   }

   boolean evalToBoolean(Configuration cfg) throws TemplateException {
      return this.evalToBoolean((Environment)null, cfg);
   }

   final TemplateModel evalToNonMissing(Environment env) throws TemplateException {
      TemplateModel result = this.eval(env);
      this.assertNonNull(result, env);
      return result;
   }

   private boolean evalToBoolean(Environment env, Configuration cfg) throws TemplateException {
      TemplateModel model = this.eval(env);
      return this.modelToBoolean(model, env, cfg);
   }

   final boolean modelToBoolean(TemplateModel model, Environment env) throws TemplateException {
      return this.modelToBoolean(model, env, (Configuration)null);
   }

   final boolean modelToBoolean(TemplateModel model, Configuration cfg) throws TemplateException {
      return this.modelToBoolean(model, (Environment)null, cfg);
   }

   private boolean modelToBoolean(TemplateModel model, Environment env, Configuration cfg) throws TemplateException {
      if (model instanceof TemplateBooleanModel) {
         return ((TemplateBooleanModel)model).getAsBoolean();
      } else {
         if (env != null) {
            if (env.isClassicCompatible()) {
               return model != null && !isEmpty(model);
            }
         } else if (cfg.isClassicCompatible()) {
            return model != null && !isEmpty(model);
         }

         throw new NonBooleanException(this, model, env);
      }
   }

   final Expression deepCloneWithIdentifierReplaced(String replacedIdentifier, Expression replacement, ReplacemenetState replacementState) {
      Expression clone = this.deepCloneWithIdentifierReplaced_inner(replacedIdentifier, replacement, replacementState);
      if (clone.beginLine == 0) {
         clone.copyLocationFrom(this);
      }

      return clone;
   }

   protected abstract Expression deepCloneWithIdentifierReplaced_inner(String var1, Expression var2, ReplacemenetState var3);

   static boolean isEmpty(TemplateModel model) throws TemplateModelException {
      if (model instanceof BeanModel) {
         return ((BeanModel)model).isEmpty();
      } else if (model instanceof TemplateSequenceModel) {
         return ((TemplateSequenceModel)model).size() == 0;
      } else if (!(model instanceof TemplateScalarModel)) {
         if (model == null) {
            return true;
         } else if (model instanceof TemplateMarkupOutputModel) {
            TemplateMarkupOutputModel mo = (TemplateMarkupOutputModel)model;
            return mo.getOutputFormat().isEmpty(mo);
         } else if (model instanceof TemplateCollectionModel) {
            return !((TemplateCollectionModel)model).iterator().hasNext();
         } else if (model instanceof TemplateHashModel) {
            return ((TemplateHashModel)model).isEmpty();
         } else {
            return !(model instanceof TemplateNumberModel) && !(model instanceof TemplateDateModel) && !(model instanceof TemplateBooleanModel);
         }
      } else {
         String s = ((TemplateScalarModel)model).getAsString();
         return s == null || s.length() == 0;
      }
   }

   final void assertNonNull(TemplateModel model, Environment env) throws InvalidReferenceException {
      if (model == null) {
         throw InvalidReferenceException.getInstance(this, env);
      }
   }

   static class ReplacemenetState {
      boolean replacementAlreadyInUse;
   }
}
