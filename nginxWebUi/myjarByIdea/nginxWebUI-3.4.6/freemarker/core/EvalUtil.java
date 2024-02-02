package freemarker.core;

import freemarker.ext.beans.BeanModel;
import freemarker.ext.beans._BeansAPI;
import freemarker.template.TemplateBooleanModel;
import freemarker.template.TemplateCollectionModel;
import freemarker.template.TemplateDateModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateNumberModel;
import freemarker.template.TemplateScalarModel;
import freemarker.template.TemplateSequenceModel;
import freemarker.template._TemplateAPI;
import java.lang.reflect.InvocationTargetException;
import java.util.Date;

class EvalUtil {
   static final int CMP_OP_EQUALS = 1;
   static final int CMP_OP_NOT_EQUALS = 2;
   static final int CMP_OP_LESS_THAN = 3;
   static final int CMP_OP_GREATER_THAN = 4;
   static final int CMP_OP_LESS_THAN_EQUALS = 5;
   static final int CMP_OP_GREATER_THAN_EQUALS = 6;
   private static final String VALUE_OF_THE_COMPARISON_IS_UNKNOWN_DATE_LIKE = "value of the comparison is a date-like value where it's not known if it's a date (no time part), time, or date-time, and thus can't be used in a comparison.";

   private EvalUtil() {
   }

   static String modelToString(TemplateScalarModel model, Expression expr, Environment env) throws TemplateModelException {
      String value = model.getAsString();
      if (value == null) {
         if (env == null) {
            env = Environment.getCurrentEnvironment();
         }

         if (env != null && env.isClassicCompatible()) {
            return "";
         } else {
            throw newModelHasStoredNullException(String.class, model, expr);
         }
      } else {
         return value;
      }
   }

   static Number modelToNumber(TemplateNumberModel model, Expression expr) throws TemplateModelException {
      Number value = model.getAsNumber();
      if (value == null) {
         throw newModelHasStoredNullException(Number.class, model, expr);
      } else {
         return value;
      }
   }

   static Date modelToDate(TemplateDateModel model, Expression expr) throws TemplateModelException {
      Date value = model.getAsDate();
      if (value == null) {
         throw newModelHasStoredNullException(Date.class, model, expr);
      } else {
         return value;
      }
   }

   static TemplateModelException newModelHasStoredNullException(Class expected, TemplateModel model, Expression expr) {
      return new _TemplateModelException(expr, _TemplateModelException.modelHasStoredNullDescription(expected, model));
   }

   static boolean compare(Expression leftExp, int operator, String operatorString, Expression rightExp, Expression defaultBlamed, Environment env) throws TemplateException {
      TemplateModel ltm = leftExp.eval(env);
      TemplateModel rtm = rightExp.eval(env);
      return compare(ltm, leftExp, operator, operatorString, rtm, rightExp, defaultBlamed, false, false, false, false, env);
   }

   static boolean compare(TemplateModel leftValue, int operator, TemplateModel rightValue, Environment env) throws TemplateException {
      return compare(leftValue, (Expression)null, operator, (String)null, rightValue, (Expression)null, (Expression)null, false, false, false, false, env);
   }

   static boolean compareLenient(TemplateModel leftValue, int operator, TemplateModel rightValue, Environment env) throws TemplateException {
      return compare(leftValue, (Expression)null, operator, (String)null, rightValue, (Expression)null, (Expression)null, false, true, false, false, env);
   }

   static boolean compare(TemplateModel leftValue, Expression leftExp, int operator, String operatorString, TemplateModel rightValue, Expression rightExp, Expression defaultBlamed, boolean quoteOperandsInErrors, boolean typeMismatchMeansNotEqual, boolean leftNullReturnsFalse, boolean rightNullReturnsFalse, Environment env) throws TemplateException {
      if (leftValue == null) {
         if (env == null || !env.isClassicCompatible()) {
            if (leftNullReturnsFalse) {
               return false;
            } else if (leftExp != null) {
               throw InvalidReferenceException.getInstance(leftExp, env);
            } else {
               throw new _MiscTemplateException(defaultBlamed, env, "The left operand of the comparison was undefined or null.");
            }
         }

         leftValue = TemplateScalarModel.EMPTY_STRING;
      }

      if (rightValue == null) {
         if (env == null || !env.isClassicCompatible()) {
            if (rightNullReturnsFalse) {
               return false;
            }

            if (rightExp != null) {
               throw InvalidReferenceException.getInstance(rightExp, env);
            }

            throw new _MiscTemplateException(defaultBlamed, env, "The right operand of the comparison was undefined or null.");
         }

         rightValue = TemplateScalarModel.EMPTY_STRING;
      }

      int cmpResult;
      if (leftValue instanceof TemplateNumberModel && rightValue instanceof TemplateNumberModel) {
         Number leftNum = modelToNumber((TemplateNumberModel)leftValue, leftExp);
         Number rightNum = modelToNumber((TemplateNumberModel)rightValue, rightExp);
         ArithmeticEngine ae = env != null ? env.getArithmeticEngine() : (leftExp != null ? leftExp.getTemplate().getArithmeticEngine() : ArithmeticEngine.BIGDECIMAL_ENGINE);

         try {
            cmpResult = ((ArithmeticEngine)ae).compareNumbers(leftNum, rightNum);
         } catch (RuntimeException var19) {
            throw new _MiscTemplateException(defaultBlamed, var19, env, new Object[]{"Unexpected error while comparing two numbers: ", var19});
         }
      } else if (leftValue instanceof TemplateDateModel && rightValue instanceof TemplateDateModel) {
         TemplateDateModel leftDateModel = (TemplateDateModel)leftValue;
         TemplateDateModel rightDateModel = (TemplateDateModel)rightValue;
         int leftDateType = leftDateModel.getDateType();
         int rightDateType = rightDateModel.getDateType();
         if (leftDateType == 0 || rightDateType == 0) {
            String sideName;
            Expression sideExp;
            if (leftDateType == 0) {
               sideName = "left";
               sideExp = leftExp;
            } else {
               sideName = "right";
               sideExp = rightExp;
            }

            throw new _MiscTemplateException(sideExp != null ? sideExp : defaultBlamed, env, new Object[]{"The ", sideName, " ", "value of the comparison is a date-like value where it's not known if it's a date (no time part), time, or date-time, and thus can't be used in a comparison."});
         }

         if (leftDateType != rightDateType) {
            throw new _MiscTemplateException(defaultBlamed, env, new Object[]{"Can't compare dates of different types. Left date type is ", TemplateDateModel.TYPE_NAMES.get(leftDateType), ", right date type is ", TemplateDateModel.TYPE_NAMES.get(rightDateType), "."});
         }

         Date leftDate = modelToDate(leftDateModel, leftExp);
         Date rightDate = modelToDate(rightDateModel, rightExp);
         cmpResult = leftDate.compareTo(rightDate);
      } else {
         String leftSting;
         String rightString;
         if (leftValue instanceof TemplateScalarModel && rightValue instanceof TemplateScalarModel) {
            if (operator != 1 && operator != 2) {
               throw new _MiscTemplateException(defaultBlamed, env, new Object[]{"Can't use operator \"", cmpOpToString(operator, operatorString), "\" on string values."});
            }

            leftSting = modelToString((TemplateScalarModel)leftValue, leftExp, env);
            rightString = modelToString((TemplateScalarModel)rightValue, rightExp, env);
            cmpResult = env.getCollator().compare(leftSting, rightString);
         } else if (leftValue instanceof TemplateBooleanModel && rightValue instanceof TemplateBooleanModel) {
            if (operator != 1 && operator != 2) {
               throw new _MiscTemplateException(defaultBlamed, env, new Object[]{"Can't use operator \"", cmpOpToString(operator, operatorString), "\" on boolean values."});
            }

            boolean leftBool = ((TemplateBooleanModel)leftValue).getAsBoolean();
            boolean rightBool = ((TemplateBooleanModel)rightValue).getAsBoolean();
            cmpResult = (leftBool ? 1 : 0) - (rightBool ? 1 : 0);
         } else {
            if (!env.isClassicCompatible()) {
               if (typeMismatchMeansNotEqual) {
                  if (operator == 1) {
                     return false;
                  }

                  if (operator == 2) {
                     return true;
                  }
               }

               throw new _MiscTemplateException(defaultBlamed, env, new Object[]{"Can't compare values of these types. ", "Allowed comparisons are between two numbers, two strings, two dates, or two booleans.\n", "Left hand operand ", quoteOperandsInErrors && leftExp != null ? new Object[]{"(", new _DelayedGetCanonicalForm(leftExp), ") value "} : "", "is ", new _DelayedAOrAn(new _DelayedFTLTypeDescription(leftValue)), ".\n", "Right hand operand ", quoteOperandsInErrors && rightExp != null ? new Object[]{"(", new _DelayedGetCanonicalForm(rightExp), ") value "} : "", "is ", new _DelayedAOrAn(new _DelayedFTLTypeDescription(rightValue)), "."});
            }

            leftSting = leftExp.evalAndCoerceToPlainText(env);
            rightString = rightExp.evalAndCoerceToPlainText(env);
            cmpResult = env.getCollator().compare(leftSting, rightString);
         }
      }

      switch (operator) {
         case 1:
            return cmpResult == 0;
         case 2:
            return cmpResult != 0;
         case 3:
            return cmpResult < 0;
         case 4:
            return cmpResult > 0;
         case 5:
            return cmpResult <= 0;
         case 6:
            return cmpResult >= 0;
         default:
            throw new BugException("Unsupported comparator operator code: " + operator);
      }
   }

   private static String cmpOpToString(int operator, String operatorString) {
      if (operatorString != null) {
         return operatorString;
      } else {
         switch (operator) {
            case 1:
               return "equals";
            case 2:
               return "not-equals";
            case 3:
               return "less-than";
            case 4:
               return "greater-than";
            case 5:
               return "less-than-equals";
            case 6:
               return "greater-than-equals";
            default:
               return "???";
         }
      }
   }

   static int mirrorCmpOperator(int operator) {
      switch (operator) {
         case 1:
            return 1;
         case 2:
            return 2;
         case 3:
            return 4;
         case 4:
            return 3;
         case 5:
            return 6;
         case 6:
            return 5;
         default:
            throw new BugException("Unsupported comparator operator code: " + operator);
      }
   }

   static Object coerceModelToStringOrMarkup(TemplateModel tm, Expression exp, String seqTip, Environment env) throws TemplateException {
      return coerceModelToStringOrMarkup(tm, exp, false, seqTip, env);
   }

   static Object coerceModelToStringOrMarkup(TemplateModel tm, Expression exp, boolean returnNullOnNonCoercableType, String seqTip, Environment env) throws TemplateException {
      if (tm instanceof TemplateNumberModel) {
         TemplateNumberModel tnm = (TemplateNumberModel)tm;
         TemplateNumberFormat format = env.getTemplateNumberFormat(exp, false);

         try {
            return assertFormatResultNotNull(format.format(tnm));
         } catch (TemplateValueFormatException var8) {
            throw _MessageUtil.newCantFormatNumberException(format, exp, var8, false);
         }
      } else if (tm instanceof TemplateDateModel) {
         TemplateDateModel tdm = (TemplateDateModel)tm;
         TemplateDateFormat format = env.getTemplateDateFormat(tdm, exp, false);

         try {
            return assertFormatResultNotNull(format.format(tdm));
         } catch (TemplateValueFormatException var9) {
            throw _MessageUtil.newCantFormatDateException(format, exp, var9, false);
         }
      } else {
         return tm instanceof TemplateMarkupOutputModel ? tm : coerceModelToTextualCommon(tm, exp, seqTip, true, returnNullOnNonCoercableType, env);
      }
   }

   static String coerceModelToStringOrUnsupportedMarkup(TemplateModel tm, Expression exp, String seqTip, Environment env) throws TemplateException {
      if (tm instanceof TemplateNumberModel) {
         TemplateNumberModel tnm = (TemplateNumberModel)tm;
         TemplateNumberFormat format = env.getTemplateNumberFormat(exp, false);

         try {
            return ensureFormatResultString(format.format(tnm), exp, env);
         } catch (TemplateValueFormatException var7) {
            throw _MessageUtil.newCantFormatNumberException(format, exp, var7, false);
         }
      } else if (tm instanceof TemplateDateModel) {
         TemplateDateModel tdm = (TemplateDateModel)tm;
         TemplateDateFormat format = env.getTemplateDateFormat(tdm, exp, false);

         try {
            return ensureFormatResultString(format.format(tdm), exp, env);
         } catch (TemplateValueFormatException var8) {
            throw _MessageUtil.newCantFormatDateException(format, exp, var8, false);
         }
      } else {
         return coerceModelToTextualCommon(tm, exp, seqTip, false, false, env);
      }
   }

   static String coerceModelToPlainText(TemplateModel tm, Expression exp, String seqTip, Environment env) throws TemplateException {
      if (tm instanceof TemplateNumberModel) {
         return assertFormatResultNotNull(env.formatNumberToPlainText((TemplateNumberModel)tm, exp, false));
      } else {
         return tm instanceof TemplateDateModel ? assertFormatResultNotNull(env.formatDateToPlainText((TemplateDateModel)tm, exp, false)) : coerceModelToTextualCommon(tm, exp, seqTip, false, false, env);
      }
   }

   private static String coerceModelToTextualCommon(TemplateModel tm, Expression exp, String seqHint, boolean supportsTOM, boolean returnNullOnNonCoercableType, Environment env) throws TemplateModelException, InvalidReferenceException, TemplateException, NonStringOrTemplateOutputException, NonStringException {
      if (tm instanceof TemplateScalarModel) {
         return modelToString((TemplateScalarModel)tm, exp, env);
      } else if (tm == null) {
         if (env.isClassicCompatible()) {
            return "";
         } else if (exp != null) {
            throw InvalidReferenceException.getInstance(exp, env);
         } else {
            throw new InvalidReferenceException("Null/missing value (no more informatoin avilable)", env);
         }
      } else if (tm instanceof TemplateBooleanModel) {
         boolean booleanValue = ((TemplateBooleanModel)tm).getAsBoolean();
         int compatMode = env.getClassicCompatibleAsInt();
         if (compatMode == 0) {
            return env.formatBoolean(booleanValue, false);
         } else if (compatMode == 1) {
            return booleanValue ? "true" : "";
         } else if (compatMode == 2) {
            if (tm instanceof BeanModel) {
               return _BeansAPI.getAsClassicCompatibleString((BeanModel)tm);
            } else {
               return booleanValue ? "true" : "";
            }
         } else {
            throw new BugException("Unsupported classic_compatible variation: " + compatMode);
         }
      } else if (env.isClassicCompatible() && tm instanceof BeanModel) {
         return _BeansAPI.getAsClassicCompatibleString((BeanModel)tm);
      } else if (returnNullOnNonCoercableType) {
         return null;
      } else if (seqHint == null || !(tm instanceof TemplateSequenceModel) && !(tm instanceof TemplateCollectionModel)) {
         if (supportsTOM) {
            throw new NonStringOrTemplateOutputException(exp, tm, env);
         } else {
            throw new NonStringException(exp, tm, env);
         }
      } else if (supportsTOM) {
         throw new NonStringOrTemplateOutputException(exp, tm, seqHint, env);
      } else {
         throw new NonStringException(exp, tm, seqHint, env);
      }
   }

   private static String ensureFormatResultString(Object formatResult, Expression exp, Environment env) throws NonStringException {
      if (formatResult instanceof String) {
         return (String)formatResult;
      } else {
         assertFormatResultNotNull(formatResult);
         TemplateMarkupOutputModel mo = (TemplateMarkupOutputModel)formatResult;
         _ErrorDescriptionBuilder desc = (new _ErrorDescriptionBuilder(new Object[]{"Value was formatted to convert it to string, but the result was markup of ouput format ", new _DelayedJQuote(mo.getOutputFormat()), "."})).tip("Use value?string to force formatting to plain text.").blame(exp);
         throw new NonStringException((Environment)null, desc);
      }
   }

   static String assertFormatResultNotNull(String r) {
      if (r != null) {
         return r;
      } else {
         throw new NullPointerException("TemplateValueFormatter result can't be null");
      }
   }

   static Object assertFormatResultNotNull(Object r) {
      if (r != null) {
         return r;
      } else {
         throw new NullPointerException("TemplateValueFormatter result can't be null");
      }
   }

   static TemplateMarkupOutputModel concatMarkupOutputs(TemplateObject parent, TemplateMarkupOutputModel leftMO, TemplateMarkupOutputModel rightMO) throws TemplateException {
      MarkupOutputFormat leftOF = leftMO.getOutputFormat();
      MarkupOutputFormat rightOF = rightMO.getOutputFormat();
      if (rightOF != leftOF) {
         String rightPT;
         if ((rightPT = rightOF.getSourcePlainText(rightMO)) != null) {
            return leftOF.concat(leftMO, leftOF.fromPlainTextByEscaping(rightPT));
         } else {
            String leftPT;
            if ((leftPT = leftOF.getSourcePlainText(leftMO)) != null) {
               return rightOF.concat(rightOF.fromPlainTextByEscaping(leftPT), rightMO);
            } else {
               Object[] message = new Object[]{"Concatenation left hand operand is in ", new _DelayedToString(leftOF), " format, while the right hand operand is in ", new _DelayedToString(rightOF), ". Conversion to common format wasn't possible."};
               if (parent instanceof Expression) {
                  throw new _MiscTemplateException((Expression)parent, message);
               } else {
                  throw new _MiscTemplateException(message);
               }
            }
         }
      } else {
         return leftOF.concat(leftMO, rightMO);
      }
   }

   static ArithmeticEngine getArithmeticEngine(Environment env, TemplateObject tObj) {
      return env != null ? env.getArithmeticEngine() : tObj.getTemplate().getParserConfiguration().getArithmeticEngine();
   }

   static boolean shouldWrapUncheckedException(Throwable e, Environment env) {
      if (FlowControlException.class.isInstance(e)) {
         return false;
      } else if (env.getWrapUncheckedExceptions()) {
         return true;
      } else if (env.getConfiguration().getIncompatibleImprovements().intValue() < _TemplateAPI.VERSION_INT_2_3_27) {
         return false;
      } else {
         Class<? extends Throwable> c = e.getClass();
         return c == NullPointerException.class || c == ClassCastException.class || c == IndexOutOfBoundsException.class || c == InvocationTargetException.class;
      }
   }
}
