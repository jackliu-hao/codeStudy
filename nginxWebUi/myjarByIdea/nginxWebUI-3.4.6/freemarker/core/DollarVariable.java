package freemarker.core;

import freemarker.template.TemplateException;
import freemarker.template.utility.StringUtil;
import java.io.IOException;
import java.io.Writer;

final class DollarVariable extends Interpolation {
   private final Expression expression;
   private final Expression escapedExpression;
   private final OutputFormat outputFormat;
   private final MarkupOutputFormat markupOutputFormat;
   private final boolean autoEscape;

   DollarVariable(Expression expression, Expression escapedExpression, OutputFormat outputFormat, boolean autoEscape) {
      this.expression = expression;
      this.escapedExpression = escapedExpression;
      this.outputFormat = outputFormat;
      this.markupOutputFormat = (MarkupOutputFormat)((MarkupOutputFormat)(outputFormat instanceof MarkupOutputFormat ? outputFormat : null));
      this.autoEscape = autoEscape;
   }

   TemplateElement[] accept(Environment env) throws TemplateException, IOException {
      Object moOrStr = this.calculateInterpolatedStringOrMarkup(env);
      Writer out = env.getOut();
      if (moOrStr instanceof String) {
         String s = (String)moOrStr;
         if (this.autoEscape) {
            this.markupOutputFormat.output(s, out);
         } else {
            out.write(s);
         }
      } else {
         TemplateMarkupOutputModel mo = (TemplateMarkupOutputModel)moOrStr;
         MarkupOutputFormat moOF = mo.getOutputFormat();
         if (moOF != this.outputFormat && !this.outputFormat.isOutputFormatMixingAllowed()) {
            String srcPlainText = moOF.getSourcePlainText(mo);
            if (srcPlainText == null) {
               throw new _TemplateModelException(this.escapedExpression, new Object[]{"The value to print is in ", new _DelayedToString(moOF), " format, which differs from the current output format, ", new _DelayedToString(this.outputFormat), ". Format conversion wasn't possible."});
            }

            if (this.outputFormat instanceof MarkupOutputFormat) {
               ((MarkupOutputFormat)this.outputFormat).output(srcPlainText, out);
            } else {
               out.write(srcPlainText);
            }
         } else {
            moOF.output(mo, out);
         }
      }

      return null;
   }

   protected Object calculateInterpolatedStringOrMarkup(Environment env) throws TemplateException {
      return EvalUtil.coerceModelToStringOrMarkup(this.escapedExpression.eval(env), this.escapedExpression, (String)null, env);
   }

   protected String dump(boolean canonical, boolean inStringLiteral) {
      StringBuilder sb = new StringBuilder();
      int syntax = this.getTemplate().getInterpolationSyntax();
      sb.append(syntax != 22 ? "${" : "[=");
      String exprCF = this.expression.getCanonicalForm();
      sb.append(inStringLiteral ? StringUtil.FTLStringLiteralEnc(exprCF, '"') : exprCF);
      sb.append(syntax != 22 ? "}" : "]");
      if (!canonical && this.expression != this.escapedExpression) {
         sb.append(" auto-escaped");
      }

      return sb.toString();
   }

   String getNodeTypeSymbol() {
      return "${...}";
   }

   boolean heedsOpeningWhitespace() {
      return true;
   }

   boolean heedsTrailingWhitespace() {
      return true;
   }

   int getParameterCount() {
      return 1;
   }

   Object getParameterValue(int idx) {
      if (idx != 0) {
         throw new IndexOutOfBoundsException();
      } else {
         return this.expression;
      }
   }

   ParameterRole getParameterRole(int idx) {
      if (idx != 0) {
         throw new IndexOutOfBoundsException();
      } else {
         return ParameterRole.CONTENT;
      }
   }

   boolean isNestedBlockRepeater() {
      return false;
   }
}
