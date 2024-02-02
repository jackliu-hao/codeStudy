package freemarker.core;

import freemarker.template.SimpleDate;
import freemarker.template.SimpleNumber;
import freemarker.template.SimpleScalar;
import freemarker.template.TemplateBooleanModel;
import freemarker.template.TemplateDateModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateNumberModel;
import freemarker.template.utility.NumberUtil;
import freemarker.template.utility.StringUtil;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

class BuiltInsForNumbers {
   private static final BigDecimal BIG_DECIMAL_ONE = new BigDecimal("1");
   private static final BigDecimal BIG_DECIMAL_LONG_MIN = BigDecimal.valueOf(Long.MIN_VALUE);
   private static final BigDecimal BIG_DECIMAL_LONG_MAX = BigDecimal.valueOf(Long.MAX_VALUE);
   private static final BigInteger BIG_INTEGER_LONG_MIN = BigInteger.valueOf(Long.MIN_VALUE);
   private static final BigInteger BIG_INTEGER_LONG_MAX = BigInteger.valueOf(Long.MAX_VALUE);

   private static final long safeToLong(Number num) throws TemplateModelException {
      if (num instanceof Double) {
         double d = (double)Math.round(num.doubleValue());
         if (!(d > 9.223372036854776E18) && !(d < -9.223372036854776E18)) {
            return (long)d;
         } else {
            throw new _TemplateModelException(new Object[]{"Number doesn't fit into a 64 bit signed integer (long): ", d});
         }
      } else if (num instanceof Float) {
         float f = (float)Math.round(num.floatValue());
         if (!(f > 9.223372E18F) && !(f < -9.223372E18F)) {
            return (long)f;
         } else {
            throw new _TemplateModelException(new Object[]{"Number doesn't fit into a 64 bit signed integer (long): ", f});
         }
      } else if (num instanceof BigDecimal) {
         BigDecimal bd = ((BigDecimal)num).setScale(0, 4);
         if (bd.compareTo(BIG_DECIMAL_LONG_MAX) <= 0 && bd.compareTo(BIG_DECIMAL_LONG_MIN) >= 0) {
            return bd.longValue();
         } else {
            throw new _TemplateModelException(new Object[]{"Number doesn't fit into a 64 bit signed integer (long): ", bd});
         }
      } else if (num instanceof BigInteger) {
         BigInteger bi = (BigInteger)num;
         if (bi.compareTo(BIG_INTEGER_LONG_MAX) <= 0 && bi.compareTo(BIG_INTEGER_LONG_MIN) >= 0) {
            return bi.longValue();
         } else {
            throw new _TemplateModelException(new Object[]{"Number doesn't fit into a 64 bit signed integer (long): ", bi});
         }
      } else if (!(num instanceof Long) && !(num instanceof Integer) && !(num instanceof Byte) && !(num instanceof Short)) {
         throw new _TemplateModelException(new Object[]{"Unsupported number type: ", num.getClass()});
      } else {
         return num.longValue();
      }
   }

   private BuiltInsForNumbers() {
   }

   static class shortBI extends BuiltInForNumber {
      TemplateModel calculateResult(Number num, TemplateModel model) {
         return (TemplateModel)(num instanceof Short ? model : new SimpleNumber(num.shortValue()));
      }
   }

   static class roundBI extends BuiltInForNumber {
      private static final BigDecimal half = new BigDecimal("0.5");

      TemplateModel calculateResult(Number num, TemplateModel model) {
         return new SimpleNumber((new BigDecimal(num.doubleValue())).add(half).divide(BuiltInsForNumbers.BIG_DECIMAL_ONE, 0, 3));
      }
   }

   static class number_to_dateBI extends BuiltInForNumber {
      private final int dateType;

      number_to_dateBI(int dateType) {
         this.dateType = dateType;
      }

      TemplateModel calculateResult(Number num, TemplateModel model) throws TemplateModelException {
         return new SimpleDate(new Date(BuiltInsForNumbers.safeToLong(num)), this.dateType);
      }
   }

   static class longBI extends BuiltIn {
      TemplateModel _eval(Environment env) throws TemplateException {
         TemplateModel model = this.target.eval(env);
         if (!(model instanceof TemplateNumberModel) && model instanceof TemplateDateModel) {
            Date date = EvalUtil.modelToDate((TemplateDateModel)model, this.target);
            return new SimpleNumber(date.getTime());
         } else {
            Number num = this.target.modelToNumber(model, env);
            return (TemplateModel)(num instanceof Long ? model : new SimpleNumber(num.longValue()));
         }
      }
   }

   static class is_nanBI extends BuiltInForNumber {
      TemplateModel calculateResult(Number num, TemplateModel model) throws TemplateModelException {
         return NumberUtil.isNaN(num) ? TemplateBooleanModel.TRUE : TemplateBooleanModel.FALSE;
      }
   }

   static class is_infiniteBI extends BuiltInForNumber {
      TemplateModel calculateResult(Number num, TemplateModel model) throws TemplateModelException {
         return NumberUtil.isInfinite(num) ? TemplateBooleanModel.TRUE : TemplateBooleanModel.FALSE;
      }
   }

   static class intBI extends BuiltInForNumber {
      TemplateModel calculateResult(Number num, TemplateModel model) {
         return (TemplateModel)(num instanceof Integer ? model : new SimpleNumber(num.intValue()));
      }
   }

   static class floorBI extends BuiltInForNumber {
      TemplateModel calculateResult(Number num, TemplateModel model) {
         return new SimpleNumber((new BigDecimal(num.doubleValue())).divide(BuiltInsForNumbers.BIG_DECIMAL_ONE, 0, 3));
      }
   }

   static class floatBI extends BuiltInForNumber {
      TemplateModel calculateResult(Number num, TemplateModel model) {
         return (TemplateModel)(num instanceof Float ? model : new SimpleNumber(num.floatValue()));
      }
   }

   static class doubleBI extends BuiltInForNumber {
      TemplateModel calculateResult(Number num, TemplateModel model) {
         return (TemplateModel)(num instanceof Double ? model : new SimpleNumber(num.doubleValue()));
      }
   }

   static class ceilingBI extends BuiltInForNumber {
      TemplateModel calculateResult(Number num, TemplateModel model) {
         return new SimpleNumber((new BigDecimal(num.doubleValue())).divide(BuiltInsForNumbers.BIG_DECIMAL_ONE, 0, 2));
      }
   }

   static class byteBI extends BuiltInForNumber {
      TemplateModel calculateResult(Number num, TemplateModel model) {
         return (TemplateModel)(num instanceof Byte ? model : new SimpleNumber(num.byteValue()));
      }
   }

   static class absBI extends BuiltInForNumber {
      TemplateModel calculateResult(Number num, TemplateModel model) throws TemplateModelException {
         int n;
         if (num instanceof Integer) {
            n = num.intValue();
            return (TemplateModel)(n < 0 ? new SimpleNumber(-n) : model);
         } else if (num instanceof BigDecimal) {
            BigDecimal n = (BigDecimal)num;
            return (TemplateModel)(n.signum() < 0 ? new SimpleNumber(n.negate()) : model);
         } else if (num instanceof Double) {
            double n = num.doubleValue();
            return (TemplateModel)(n < 0.0 ? new SimpleNumber(-n) : model);
         } else if (num instanceof Float) {
            float n = num.floatValue();
            return (TemplateModel)(n < 0.0F ? new SimpleNumber(-n) : model);
         } else if (num instanceof Long) {
            long n = num.longValue();
            return (TemplateModel)(n < 0L ? new SimpleNumber(-n) : model);
         } else if (num instanceof Short) {
            n = num.shortValue();
            return (TemplateModel)(n < 0 ? new SimpleNumber(-n) : model);
         } else if (num instanceof Byte) {
            n = num.byteValue();
            return (TemplateModel)(n < 0 ? new SimpleNumber(-n) : model);
         } else if (num instanceof BigInteger) {
            BigInteger n = (BigInteger)num;
            return (TemplateModel)(n.signum() < 0 ? new SimpleNumber(n.negate()) : model);
         } else {
            throw new _TemplateModelException(new Object[]{"Unsupported number class: ", num.getClass()});
         }
      }
   }

   static class upper_abcBI extends abcBI {
      upper_abcBI() {
         super(null);
      }

      protected String toABC(int n) {
         return StringUtil.toUpperABC(n);
      }
   }

   static class lower_abcBI extends abcBI {
      lower_abcBI() {
         super(null);
      }

      protected String toABC(int n) {
         return StringUtil.toLowerABC(n);
      }
   }

   private abstract static class abcBI extends BuiltInForNumber {
      private abcBI() {
      }

      TemplateModel calculateResult(Number num, TemplateModel model) throws TemplateModelException {
         int n;
         try {
            n = NumberUtil.toIntExact(num);
         } catch (ArithmeticException var5) {
            throw new _TemplateModelException(this.target, new Object[]{"The left side operand value isn't compatible with ?", this.key, ": ", var5.getMessage()});
         }

         if (n <= 0) {
            throw new _TemplateModelException(this.target, new Object[]{"The left side operand of to ?", this.key, " must be at least 1, but was ", n, "."});
         } else {
            return new SimpleScalar(this.toABC(n));
         }
      }

      protected abstract String toABC(int var1);

      // $FF: synthetic method
      abcBI(Object x0) {
         this();
      }
   }
}
