package freemarker.core;

import freemarker.ext.beans.BeanModel;
import freemarker.ext.beans.OverloadedMethodsModel;
import freemarker.ext.beans.SimpleMethodModel;
import freemarker.ext.beans._BeansAPI;
import freemarker.template.SimpleDate;
import freemarker.template.SimpleNumber;
import freemarker.template.SimpleScalar;
import freemarker.template.TemplateBooleanModel;
import freemarker.template.TemplateCollectionModel;
import freemarker.template.TemplateCollectionModelEx;
import freemarker.template.TemplateDateModel;
import freemarker.template.TemplateDirectiveModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateHashModel;
import freemarker.template.TemplateHashModelEx;
import freemarker.template.TemplateMethodModel;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateModelIterator;
import freemarker.template.TemplateModelWithAPISupport;
import freemarker.template.TemplateNodeModel;
import freemarker.template.TemplateNumberModel;
import freemarker.template.TemplateScalarModel;
import freemarker.template.TemplateSequenceModel;
import freemarker.template.TemplateTransformModel;
import freemarker.template._TemplateAPI;
import freemarker.template.utility.NumberUtil;
import java.util.Date;
import java.util.List;

class BuiltInsForMultipleTypes {
   private BuiltInsForMultipleTypes() {
   }

   abstract static class AbstractCBI extends BuiltIn {
      TemplateModel _eval(Environment env) throws TemplateException {
         TemplateModel model = this.target.eval(env);
         if (model instanceof TemplateNumberModel) {
            return this.formatNumber(env, model);
         } else if (model instanceof TemplateBooleanModel) {
            return new SimpleScalar(((TemplateBooleanModel)model).getAsBoolean() ? "true" : "false");
         } else {
            throw new UnexpectedTypeException(this.target, model, "number or boolean", new Class[]{TemplateNumberModel.class, TemplateBooleanModel.class}, env);
         }
      }

      protected abstract TemplateModel formatNumber(Environment var1, TemplateModel var2) throws TemplateModelException;
   }

   static class stringBI extends BuiltIn {
      TemplateModel _eval(Environment env) throws TemplateException {
         TemplateModel model = this.target.eval(env);
         if (model instanceof TemplateNumberModel) {
            return new NumberFormatter((TemplateNumberModel)model, env);
         } else if (model instanceof TemplateDateModel) {
            TemplateDateModel dm = (TemplateDateModel)model;
            return new DateFormatter(dm, env);
         } else if (model instanceof SimpleScalar) {
            return model;
         } else if (model instanceof TemplateBooleanModel) {
            return new BooleanFormatter((TemplateBooleanModel)model, env);
         } else if (model instanceof TemplateScalarModel) {
            return new SimpleScalar(((TemplateScalarModel)model).getAsString());
         } else if (env.isClassicCompatible() && model instanceof BeanModel) {
            return new SimpleScalar(_BeansAPI.getAsClassicCompatibleString((BeanModel)model));
         } else {
            throw new UnexpectedTypeException(this.target, model, "number, date, boolean or string", new Class[]{TemplateNumberModel.class, TemplateDateModel.class, TemplateBooleanModel.class, TemplateScalarModel.class}, env);
         }
      }

      private class NumberFormatter implements TemplateScalarModel, TemplateHashModel, TemplateMethodModel {
         private final TemplateNumberModel numberModel;
         private final Number number;
         private final Environment env;
         private final TemplateNumberFormat defaultFormat;
         private String cachedValue;

         NumberFormatter(TemplateNumberModel numberModel, Environment env) throws TemplateException {
            this.env = env;
            this.numberModel = numberModel;
            this.number = EvalUtil.modelToNumber(numberModel, stringBI.this.target);

            try {
               this.defaultFormat = env.getTemplateNumberFormat((Expression)stringBI.this, true);
            } catch (TemplateException var5) {
               throw _CoreAPI.ensureIsTemplateModelException("Failed to get default number format", var5);
            }
         }

         public Object exec(List args) throws TemplateModelException {
            stringBI.this.checkMethodArgCount(args, 1);
            return this.get((String)args.get(0));
         }

         public TemplateModel get(String key) throws TemplateModelException {
            TemplateNumberFormat format;
            try {
               format = this.env.getTemplateNumberFormat(key, stringBI.this, true);
            } catch (TemplateException var6) {
               throw _CoreAPI.ensureIsTemplateModelException("Failed to get number format", var6);
            }

            String result;
            try {
               if (format instanceof BackwardCompatibleTemplateNumberFormat) {
                  result = this.env.formatNumberToPlainText(this.number, (BackwardCompatibleTemplateNumberFormat)format, stringBI.this.target);
               } else {
                  result = this.env.formatNumberToPlainText(this.numberModel, format, stringBI.this.target, true);
               }
            } catch (TemplateException var5) {
               throw _CoreAPI.ensureIsTemplateModelException("Failed to format number", var5);
            }

            return new SimpleScalar(result);
         }

         public String getAsString() throws TemplateModelException {
            if (this.cachedValue == null) {
               try {
                  if (this.defaultFormat instanceof BackwardCompatibleTemplateNumberFormat) {
                     this.cachedValue = this.env.formatNumberToPlainText(this.number, (BackwardCompatibleTemplateNumberFormat)this.defaultFormat, stringBI.this.target);
                  } else {
                     this.cachedValue = this.env.formatNumberToPlainText(this.numberModel, this.defaultFormat, stringBI.this.target, true);
                  }
               } catch (TemplateException var2) {
                  throw _CoreAPI.ensureIsTemplateModelException("Failed to format number", var2);
               }
            }

            return this.cachedValue;
         }

         public boolean isEmpty() {
            return false;
         }
      }

      private class DateFormatter implements TemplateScalarModel, TemplateHashModel, TemplateMethodModel {
         private final TemplateDateModel dateModel;
         private final Environment env;
         private final TemplateDateFormat defaultFormat;
         private String cachedValue;

         DateFormatter(TemplateDateModel dateModel, Environment env) throws TemplateException {
            this.dateModel = dateModel;
            this.env = env;
            int dateType = dateModel.getDateType();
            this.defaultFormat = dateType == 0 ? null : env.getTemplateDateFormat(dateType, EvalUtil.modelToDate(dateModel, stringBI.this.target).getClass(), stringBI.this.target, true);
         }

         public Object exec(List args) throws TemplateModelException {
            stringBI.this.checkMethodArgCount(args, 1);
            return this.formatWith((String)args.get(0));
         }

         public TemplateModel get(String key) throws TemplateModelException {
            return this.formatWith(key);
         }

         private TemplateModel formatWith(String key) throws TemplateModelException {
            try {
               return new SimpleScalar(this.env.formatDateToPlainText(this.dateModel, key, stringBI.this.target, stringBI.this, true));
            } catch (TemplateException var3) {
               throw _CoreAPI.ensureIsTemplateModelException("Failed to format value", var3);
            }
         }

         public String getAsString() throws TemplateModelException {
            if (this.cachedValue == null) {
               if (this.defaultFormat == null) {
                  if (this.dateModel.getDateType() == 0) {
                     throw _MessageUtil.newCantFormatUnknownTypeDateException(stringBI.this.target, (UnknownDateTypeFormattingUnsupportedException)null);
                  }

                  throw new BugException();
               }

               try {
                  this.cachedValue = EvalUtil.assertFormatResultNotNull(this.defaultFormat.formatToPlainText(this.dateModel));
               } catch (TemplateValueFormatException var4) {
                  TemplateValueFormatException e = var4;

                  try {
                     throw _MessageUtil.newCantFormatDateException(this.defaultFormat, stringBI.this.target, e, true);
                  } catch (TemplateException var3) {
                     throw _CoreAPI.ensureIsTemplateModelException("Failed to format date/time/datetime", var3);
                  }
               }
            }

            return this.cachedValue;
         }

         public boolean isEmpty() {
            return false;
         }
      }

      private class BooleanFormatter implements TemplateScalarModel, TemplateMethodModel {
         private final TemplateBooleanModel bool;
         private final Environment env;

         BooleanFormatter(TemplateBooleanModel bool, Environment env) {
            this.bool = bool;
            this.env = env;
         }

         public Object exec(List args) throws TemplateModelException {
            stringBI.this.checkMethodArgCount(args, 2);
            return new SimpleScalar((String)args.get(this.bool.getAsBoolean() ? 0 : 1));
         }

         public String getAsString() throws TemplateModelException {
            if (this.bool instanceof TemplateScalarModel) {
               return ((TemplateScalarModel)this.bool).getAsString();
            } else {
               try {
                  return this.env.formatBoolean(this.bool.getAsBoolean(), true);
               } catch (TemplateException var2) {
                  throw new TemplateModelException(var2);
               }
            }
         }
      }
   }

   static class sizeBI extends BuiltIn {
      private int countingLimit;

      protected void setTarget(Expression target) {
         super.setTarget(target);
         target.enableLazilyGeneratedResult();
      }

      TemplateModel _eval(Environment env) throws TemplateException {
         TemplateModel model = this.target.eval(env);
         int size;
         if (this.countingLimit == 1 && model instanceof TemplateCollectionModelEx) {
            size = ((TemplateCollectionModelEx)model).isEmpty() ? 0 : 1;
         } else if (model instanceof TemplateSequenceModel) {
            size = ((TemplateSequenceModel)model).size();
         } else if (model instanceof TemplateCollectionModelEx) {
            size = ((TemplateCollectionModelEx)model).size();
         } else if (model instanceof TemplateHashModelEx) {
            size = ((TemplateHashModelEx)model).size();
         } else {
            if (!(model instanceof LazilyGeneratedCollectionModel) || !((LazilyGeneratedCollectionModel)model).isSequence()) {
               throw new UnexpectedTypeException(this.target, model, "extended-hash or sequence or extended collection", new Class[]{TemplateHashModelEx.class, TemplateSequenceModel.class, TemplateCollectionModelEx.class}, env);
            }

            TemplateModelIterator iterator = ((LazilyGeneratedCollectionModel)model).iterator();
            int counter = 0;

            while(iterator.hasNext()) {
               ++counter;
               if (counter == this.countingLimit) {
                  break;
               }

               iterator.next();
            }

            size = counter;
         }

         return new SimpleNumber(size);
      }

      void setCountingLimit(int cmpOperator, NumberLiteral rightOperand) {
         int cmpInt;
         try {
            cmpInt = NumberUtil.toIntExact(rightOperand.getAsNumber());
         } catch (ArithmeticException var5) {
            return;
         }

         switch (cmpOperator) {
            case 1:
               this.countingLimit = cmpInt + 1;
               break;
            case 2:
               this.countingLimit = cmpInt + 1;
               break;
            case 3:
               this.countingLimit = cmpInt;
               break;
            case 4:
               this.countingLimit = cmpInt + 1;
               break;
            case 5:
               this.countingLimit = cmpInt + 1;
               break;
            case 6:
               this.countingLimit = cmpInt;
               break;
            default:
               throw new BugException("Unsupported comparator operator code: " + cmpOperator);
         }

      }
   }

   static class namespaceBI extends BuiltIn {
      TemplateModel _eval(Environment env) throws TemplateException {
         TemplateModel tm = this.target.eval(env);
         if (!(tm instanceof Macro)) {
            throw new UnexpectedTypeException(this.target, tm, "macro or function", new Class[]{Macro.class}, env);
         } else {
            return env.getMacroNamespace((Macro)tm);
         }
      }
   }

   static class is_transformBI extends BuiltIn {
      TemplateModel _eval(Environment env) throws TemplateException {
         TemplateModel tm = this.target.eval(env);
         this.target.assertNonNull(tm, env);
         return tm instanceof TemplateTransformModel ? TemplateBooleanModel.TRUE : TemplateBooleanModel.FALSE;
      }
   }

   static class is_stringBI extends BuiltIn {
      TemplateModel _eval(Environment env) throws TemplateException {
         TemplateModel tm = this.target.eval(env);
         this.target.assertNonNull(tm, env);
         return tm instanceof TemplateScalarModel ? TemplateBooleanModel.TRUE : TemplateBooleanModel.FALSE;
      }
   }

   static class is_sequenceBI extends BuiltIn {
      TemplateModel _eval(Environment env) throws TemplateException {
         TemplateModel tm = this.target.eval(env);
         this.target.assertNonNull(tm, env);
         return !(tm instanceof TemplateSequenceModel) || (tm instanceof OverloadedMethodsModel || tm instanceof SimpleMethodModel) && env.isIcI2324OrLater() ? TemplateBooleanModel.FALSE : TemplateBooleanModel.TRUE;
      }
   }

   static class is_numberBI extends BuiltIn {
      TemplateModel _eval(Environment env) throws TemplateException {
         TemplateModel tm = this.target.eval(env);
         this.target.assertNonNull(tm, env);
         return tm instanceof TemplateNumberModel ? TemplateBooleanModel.TRUE : TemplateBooleanModel.FALSE;
      }
   }

   static class is_nodeBI extends BuiltIn {
      TemplateModel _eval(Environment env) throws TemplateException {
         TemplateModel tm = this.target.eval(env);
         this.target.assertNonNull(tm, env);
         return tm instanceof TemplateNodeModel ? TemplateBooleanModel.TRUE : TemplateBooleanModel.FALSE;
      }
   }

   static class is_methodBI extends BuiltIn {
      TemplateModel _eval(Environment env) throws TemplateException {
         TemplateModel tm = this.target.eval(env);
         this.target.assertNonNull(tm, env);
         return tm instanceof TemplateMethodModel ? TemplateBooleanModel.TRUE : TemplateBooleanModel.FALSE;
      }
   }

   static class is_markup_outputBI extends BuiltIn {
      TemplateModel _eval(Environment env) throws TemplateException {
         TemplateModel tm = this.target.eval(env);
         this.target.assertNonNull(tm, env);
         return tm instanceof TemplateMarkupOutputModel ? TemplateBooleanModel.TRUE : TemplateBooleanModel.FALSE;
      }
   }

   static class is_macroBI extends BuiltIn {
      TemplateModel _eval(Environment env) throws TemplateException {
         TemplateModel tm = this.target.eval(env);
         this.target.assertNonNull(tm, env);
         return tm instanceof Macro ? TemplateBooleanModel.TRUE : TemplateBooleanModel.FALSE;
      }
   }

   static class is_indexableBI extends BuiltIn {
      TemplateModel _eval(Environment env) throws TemplateException {
         TemplateModel tm = this.target.eval(env);
         this.target.assertNonNull(tm, env);
         return tm instanceof TemplateSequenceModel ? TemplateBooleanModel.TRUE : TemplateBooleanModel.FALSE;
      }
   }

   static class is_hashBI extends BuiltIn {
      TemplateModel _eval(Environment env) throws TemplateException {
         TemplateModel tm = this.target.eval(env);
         this.target.assertNonNull(tm, env);
         return tm instanceof TemplateHashModel ? TemplateBooleanModel.TRUE : TemplateBooleanModel.FALSE;
      }
   }

   static class is_hash_exBI extends BuiltIn {
      TemplateModel _eval(Environment env) throws TemplateException {
         TemplateModel tm = this.target.eval(env);
         this.target.assertNonNull(tm, env);
         return tm instanceof TemplateHashModelEx ? TemplateBooleanModel.TRUE : TemplateBooleanModel.FALSE;
      }
   }

   static class is_enumerableBI extends BuiltIn {
      TemplateModel _eval(Environment env) throws TemplateException {
         TemplateModel tm = this.target.eval(env);
         this.target.assertNonNull(tm, env);
         return !(tm instanceof TemplateSequenceModel) && !(tm instanceof TemplateCollectionModel) || _TemplateAPI.getTemplateLanguageVersionAsInt((TemplateObject)this) >= _TemplateAPI.VERSION_INT_2_3_21 && (tm instanceof SimpleMethodModel || tm instanceof OverloadedMethodsModel) ? TemplateBooleanModel.FALSE : TemplateBooleanModel.TRUE;
      }
   }

   static class is_directiveBI extends BuiltIn {
      TemplateModel _eval(Environment env) throws TemplateException {
         TemplateModel tm = this.target.eval(env);
         this.target.assertNonNull(tm, env);
         return !(tm instanceof TemplateTransformModel) && !(tm instanceof Macro) && !(tm instanceof TemplateDirectiveModel) ? TemplateBooleanModel.FALSE : TemplateBooleanModel.TRUE;
      }
   }

   static class is_dateOfTypeBI extends BuiltIn {
      private final int dateType;

      is_dateOfTypeBI(int dateType) {
         this.dateType = dateType;
      }

      TemplateModel _eval(Environment env) throws TemplateException {
         TemplateModel tm = this.target.eval(env);
         this.target.assertNonNull(tm, env);
         return tm instanceof TemplateDateModel && ((TemplateDateModel)tm).getDateType() == this.dateType ? TemplateBooleanModel.TRUE : TemplateBooleanModel.FALSE;
      }
   }

   static class is_dateLikeBI extends BuiltIn {
      TemplateModel _eval(Environment env) throws TemplateException {
         TemplateModel tm = this.target.eval(env);
         this.target.assertNonNull(tm, env);
         return tm instanceof TemplateDateModel ? TemplateBooleanModel.TRUE : TemplateBooleanModel.FALSE;
      }
   }

   static class is_collection_exBI extends BuiltIn {
      TemplateModel _eval(Environment env) throws TemplateException {
         TemplateModel tm = this.target.eval(env);
         this.target.assertNonNull(tm, env);
         return tm instanceof TemplateCollectionModelEx ? TemplateBooleanModel.TRUE : TemplateBooleanModel.FALSE;
      }
   }

   static class is_collectionBI extends BuiltIn {
      TemplateModel _eval(Environment env) throws TemplateException {
         TemplateModel tm = this.target.eval(env);
         this.target.assertNonNull(tm, env);
         return tm instanceof TemplateCollectionModel ? TemplateBooleanModel.TRUE : TemplateBooleanModel.FALSE;
      }
   }

   static class is_booleanBI extends BuiltIn {
      TemplateModel _eval(Environment env) throws TemplateException {
         TemplateModel tm = this.target.eval(env);
         this.target.assertNonNull(tm, env);
         return tm instanceof TemplateBooleanModel ? TemplateBooleanModel.TRUE : TemplateBooleanModel.FALSE;
      }
   }

   static class has_apiBI extends BuiltIn {
      TemplateModel _eval(Environment env) throws TemplateException {
         TemplateModel tm = this.target.eval(env);
         this.target.assertNonNull(tm, env);
         return tm instanceof TemplateModelWithAPISupport ? TemplateBooleanModel.TRUE : TemplateBooleanModel.FALSE;
      }
   }

   static class apiBI extends BuiltIn {
      TemplateModel _eval(Environment env) throws TemplateException {
         if (!env.isAPIBuiltinEnabled()) {
            throw new _MiscTemplateException(this, new Object[]{"Can't use ?api, because the \"", "api_builtin_enabled", "\" configuration setting is false. Think twice before you set it to true though. Especially, it shouldn't abused for modifying Map-s and Collection-s."});
         } else {
            TemplateModel tm = this.target.eval(env);
            if (!(tm instanceof TemplateModelWithAPISupport)) {
               this.target.assertNonNull(tm, env);
               throw new APINotSupportedTemplateException(env, this.target, tm);
            } else {
               return ((TemplateModelWithAPISupport)tm).getAPI();
            }
         }
      }
   }

   static class dateBI extends BuiltIn {
      private final int dateType;

      dateBI(int dateType) {
         this.dateType = dateType;
      }

      TemplateModel _eval(Environment env) throws TemplateException {
         TemplateModel model = this.target.eval(env);
         if (model instanceof TemplateDateModel) {
            TemplateDateModel dmodel = (TemplateDateModel)model;
            int dtype = dmodel.getDateType();
            if (this.dateType == dtype) {
               return model;
            } else if (dtype != 0 && dtype != 3) {
               throw new _MiscTemplateException(this, new Object[]{"Cannot convert ", TemplateDateModel.TYPE_NAMES.get(dtype), " to ", TemplateDateModel.TYPE_NAMES.get(this.dateType)});
            } else {
               return new SimpleDate(dmodel.getAsDate(), this.dateType);
            }
         } else {
            String s = this.target.evalAndCoerceToPlainText(env);
            return new DateParser(s, env);
         }
      }

      private class DateParser implements TemplateDateModel, TemplateMethodModel, TemplateHashModel {
         private final String text;
         private final Environment env;
         private final TemplateDateFormat defaultFormat;
         private TemplateDateModel cachedValue;

         DateParser(String text, Environment env) throws TemplateException {
            this.text = text;
            this.env = env;
            this.defaultFormat = env.getTemplateDateFormat(dateBI.this.dateType, Date.class, dateBI.this.target, false);
         }

         public Object exec(List args) throws TemplateModelException {
            dateBI.this.checkMethodArgCount(args, 0, 1);
            return args.size() == 0 ? this.getAsDateModel() : this.get((String)args.get(0));
         }

         public TemplateModel get(String pattern) throws TemplateModelException {
            TemplateDateFormat format;
            try {
               format = this.env.getTemplateDateFormat(pattern, dateBI.this.dateType, Date.class, dateBI.this.target, dateBI.this, true);
            } catch (TemplateException var4) {
               throw _CoreAPI.ensureIsTemplateModelException("Failed to get format", var4);
            }

            return this.toTemplateDateModel(this.parse(format));
         }

         private TemplateDateModel toTemplateDateModel(Object date) throws _TemplateModelException {
            if (date instanceof Date) {
               return new SimpleDate((Date)date, dateBI.this.dateType);
            } else {
               TemplateDateModel tm = (TemplateDateModel)date;
               if (tm.getDateType() != dateBI.this.dateType) {
                  throw new _TemplateModelException("The result of the parsing was of the wrong date type.");
               } else {
                  return tm;
               }
            }
         }

         private TemplateDateModel getAsDateModel() throws TemplateModelException {
            if (this.cachedValue == null) {
               this.cachedValue = this.toTemplateDateModel(this.parse(this.defaultFormat));
            }

            return this.cachedValue;
         }

         public Date getAsDate() throws TemplateModelException {
            return this.getAsDateModel().getAsDate();
         }

         public int getDateType() {
            return dateBI.this.dateType;
         }

         public boolean isEmpty() {
            return false;
         }

         private Object parse(TemplateDateFormat df) throws TemplateModelException {
            try {
               return df.parse(this.text, dateBI.this.dateType);
            } catch (TemplateValueFormatException var3) {
               throw new _TemplateModelException(var3, new Object[]{"The string doesn't match the expected date/time/date-time format. The string to parse was: ", new _DelayedJQuote(this.text), ". ", "The expected format was: ", new _DelayedJQuote(df.getDescription()), ".", var3.getMessage() != null ? "\nThe nested reason given follows:\n" : "", var3.getMessage() != null ? var3.getMessage() : ""});
            }
         }
      }
   }

   static class cBI extends AbstractCBI implements ICIChainMember {
      private final BIBeforeICI2d3d21 prevICIObj = new BIBeforeICI2d3d21();

      TemplateModel _eval(Environment env) throws TemplateException {
         TemplateModel model = this.target.eval(env);
         if (model instanceof TemplateNumberModel) {
            return this.formatNumber(env, model);
         } else if (model instanceof TemplateBooleanModel) {
            return new SimpleScalar(((TemplateBooleanModel)model).getAsBoolean() ? "true" : "false");
         } else {
            throw new UnexpectedTypeException(this.target, model, "number or boolean", new Class[]{TemplateNumberModel.class, TemplateBooleanModel.class}, env);
         }
      }

      protected TemplateModel formatNumber(Environment env, TemplateModel model) throws TemplateModelException {
         Number num = EvalUtil.modelToNumber((TemplateNumberModel)model, this.target);
         if (!(num instanceof Integer) && !(num instanceof Long)) {
            if (num instanceof Double) {
               double n = num.doubleValue();
               if (n == Double.POSITIVE_INFINITY) {
                  return new SimpleScalar("INF");
               }

               if (n == Double.NEGATIVE_INFINITY) {
                  return new SimpleScalar("-INF");
               }

               if (Double.isNaN(n)) {
                  return new SimpleScalar("NaN");
               }
            } else if (num instanceof Float) {
               float n = num.floatValue();
               if (n == Float.POSITIVE_INFINITY) {
                  return new SimpleScalar("INF");
               }

               if (n == Float.NEGATIVE_INFINITY) {
                  return new SimpleScalar("-INF");
               }

               if (Float.isNaN(n)) {
                  return new SimpleScalar("NaN");
               }
            }

            return new SimpleScalar(env.getCNumberFormat().format(num));
         } else {
            return new SimpleScalar(num.toString());
         }
      }

      public int getMinimumICIVersion() {
         return _TemplateAPI.VERSION_INT_2_3_21;
      }

      public Object getPreviousICIChainMember() {
         return this.prevICIObj;
      }

      static class BIBeforeICI2d3d21 extends AbstractCBI {
         protected TemplateModel formatNumber(Environment env, TemplateModel model) throws TemplateModelException {
            Number num = EvalUtil.modelToNumber((TemplateNumberModel)model, this.target);
            return !(num instanceof Integer) && !(num instanceof Long) ? new SimpleScalar(env.getCNumberFormat().format(num)) : new SimpleScalar(num.toString());
         }
      }
   }
}
