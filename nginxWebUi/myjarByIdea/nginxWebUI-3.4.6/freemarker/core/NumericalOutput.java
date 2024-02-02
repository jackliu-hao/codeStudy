package freemarker.core;

import freemarker.template.TemplateException;
import freemarker.template.utility.StringUtil;
import java.io.IOException;
import java.io.Writer;
import java.text.NumberFormat;
import java.util.Locale;

final class NumericalOutput extends Interpolation {
   private final Expression expression;
   private final boolean hasFormat;
   private final int minFracDigits;
   private final int maxFracDigits;
   private final MarkupOutputFormat autoEscapeOutputFormat;
   private volatile FormatHolder formatCache;

   NumericalOutput(Expression expression, MarkupOutputFormat autoEscapeOutputFormat) {
      this.expression = expression;
      this.hasFormat = false;
      this.minFracDigits = 0;
      this.maxFracDigits = 0;
      this.autoEscapeOutputFormat = autoEscapeOutputFormat;
   }

   NumericalOutput(Expression expression, int minFracDigits, int maxFracDigits, MarkupOutputFormat autoEscapeOutputFormat) {
      this.expression = expression;
      this.hasFormat = true;
      this.minFracDigits = minFracDigits;
      this.maxFracDigits = maxFracDigits;
      this.autoEscapeOutputFormat = autoEscapeOutputFormat;
   }

   TemplateElement[] accept(Environment env) throws TemplateException, IOException {
      String s = this.calculateInterpolatedStringOrMarkup(env);
      Writer out = env.getOut();
      if (this.autoEscapeOutputFormat != null) {
         this.autoEscapeOutputFormat.output(s, out);
      } else {
         out.write(s);
      }

      return null;
   }

   protected String calculateInterpolatedStringOrMarkup(Environment env) throws TemplateException {
      Number num = this.expression.evalToNumber(env);
      FormatHolder fmth = this.formatCache;
      if (fmth == null || !fmth.locale.equals(env.getLocale())) {
         synchronized(this) {
            fmth = this.formatCache;
            if (fmth == null || !fmth.locale.equals(env.getLocale())) {
               NumberFormat fmt = NumberFormat.getNumberInstance(env.getLocale());
               if (this.hasFormat) {
                  fmt.setMinimumFractionDigits(this.minFracDigits);
                  fmt.setMaximumFractionDigits(this.maxFracDigits);
               } else {
                  fmt.setMinimumFractionDigits(0);
                  fmt.setMaximumFractionDigits(50);
               }

               fmt.setGroupingUsed(false);
               this.formatCache = new FormatHolder(fmt, env.getLocale());
               fmth = this.formatCache;
            }
         }
      }

      String s = fmth.format.format(num);
      return s;
   }

   protected String dump(boolean canonical, boolean inStringLiteral) {
      StringBuilder buf = new StringBuilder("#{");
      String exprCF = this.expression.getCanonicalForm();
      buf.append(inStringLiteral ? StringUtil.FTLStringLiteralEnc(exprCF, '"') : exprCF);
      if (this.hasFormat) {
         buf.append(" ; ");
         buf.append("m");
         buf.append(this.minFracDigits);
         buf.append("M");
         buf.append(this.maxFracDigits);
      }

      buf.append("}");
      return buf.toString();
   }

   String getNodeTypeSymbol() {
      return "#{...}";
   }

   boolean heedsOpeningWhitespace() {
      return true;
   }

   boolean heedsTrailingWhitespace() {
      return true;
   }

   int getParameterCount() {
      return 3;
   }

   Object getParameterValue(int idx) {
      switch (idx) {
         case 0:
            return this.expression;
         case 1:
            return this.hasFormat ? this.minFracDigits : null;
         case 2:
            return this.hasFormat ? this.maxFracDigits : null;
         default:
            throw new IndexOutOfBoundsException();
      }
   }

   ParameterRole getParameterRole(int idx) {
      switch (idx) {
         case 0:
            return ParameterRole.CONTENT;
         case 1:
            return ParameterRole.MINIMUM_DECIMALS;
         case 2:
            return ParameterRole.MAXIMUM_DECIMALS;
         default:
            throw new IndexOutOfBoundsException();
      }
   }

   boolean isNestedBlockRepeater() {
      return false;
   }

   private static class FormatHolder {
      final NumberFormat format;
      final Locale locale;

      FormatHolder(NumberFormat format, Locale locale) {
         this.format = format;
         this.locale = locale;
      }
   }
}
