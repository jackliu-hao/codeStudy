package freemarker.core;

import freemarker.template.MalformedTemplateNameException;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateScalarModel;
import freemarker.template.utility.StringUtil;
import java.io.IOException;

final class Include extends TemplateElement {
   private final Expression includedTemplateNameExp;
   private final Expression encodingExp;
   private final Expression parseExp;
   private final Expression ignoreMissingExp;
   private final String encoding;
   private final Boolean parse;
   private final Boolean ignoreMissingExpPrecalcedValue;

   Include(Template template, Expression includedTemplatePathExp, Expression encodingExp, Expression parseExp, Expression ignoreMissingExp) throws ParseException {
      this.includedTemplateNameExp = includedTemplatePathExp;
      this.encodingExp = encodingExp;
      if (encodingExp == null) {
         this.encoding = null;
      } else if (encodingExp.isLiteral()) {
         try {
            TemplateModel tm = encodingExp.eval((Environment)null);
            if (!(tm instanceof TemplateScalarModel)) {
               throw new ParseException("Expected a string as the value of the \"encoding\" argument", encodingExp);
            }

            this.encoding = ((TemplateScalarModel)tm).getAsString();
         } catch (TemplateException var11) {
            throw new BugException(var11);
         }
      } else {
         this.encoding = null;
      }

      this.parseExp = parseExp;
      if (parseExp == null) {
         this.parse = Boolean.TRUE;
      } else if (parseExp.isLiteral()) {
         try {
            if (parseExp instanceof StringLiteral) {
               this.parse = StringUtil.getYesNo(parseExp.evalAndCoerceToPlainText((Environment)null));
            } else {
               try {
                  this.parse = parseExp.evalToBoolean(template.getConfiguration());
               } catch (NonBooleanException var9) {
                  throw new ParseException("Expected a boolean or string as the value of the parse attribute", parseExp, var9);
               }
            }
         } catch (TemplateException var10) {
            throw new BugException(var10);
         }
      } else {
         this.parse = null;
      }

      this.ignoreMissingExp = ignoreMissingExp;
      if (ignoreMissingExp != null && ignoreMissingExp.isLiteral()) {
         try {
            try {
               this.ignoreMissingExpPrecalcedValue = ignoreMissingExp.evalToBoolean(template.getConfiguration());
            } catch (NonBooleanException var7) {
               throw new ParseException("Expected a boolean as the value of the \"ignore_missing\" attribute", ignoreMissingExp, var7);
            }
         } catch (TemplateException var8) {
            throw new BugException(var8);
         }
      } else {
         this.ignoreMissingExpPrecalcedValue = null;
      }

   }

   TemplateElement[] accept(Environment env) throws TemplateException, IOException {
      String includedTemplateName = this.includedTemplateNameExp.evalAndCoerceToPlainText(env);

      String fullIncludedTemplateName;
      try {
         fullIncludedTemplateName = env.toFullTemplateName(this.getTemplate().getName(), includedTemplateName);
      } catch (MalformedTemplateNameException var10) {
         throw new _MiscTemplateException(var10, env, new Object[]{"Malformed template name ", new _DelayedJQuote(var10.getTemplateName()), ":\n", var10.getMalformednessDescription()});
      }

      String encoding = this.encoding != null ? this.encoding : (this.encodingExp != null ? this.encodingExp.evalAndCoerceToPlainText(env) : null);
      boolean parse;
      if (this.parse != null) {
         parse = this.parse;
      } else {
         TemplateModel tm = this.parseExp.eval(env);
         if (tm instanceof TemplateScalarModel) {
            parse = this.getYesNo(this.parseExp, EvalUtil.modelToString((TemplateScalarModel)tm, this.parseExp, env));
         } else {
            parse = this.parseExp.modelToBoolean(tm, env);
         }
      }

      boolean ignoreMissing;
      if (this.ignoreMissingExpPrecalcedValue != null) {
         ignoreMissing = this.ignoreMissingExpPrecalcedValue;
      } else if (this.ignoreMissingExp != null) {
         ignoreMissing = this.ignoreMissingExp.evalToBoolean(env);
      } else {
         ignoreMissing = false;
      }

      Template includedTemplate;
      try {
         includedTemplate = env.getTemplateForInclusion(fullIncludedTemplateName, encoding, parse, ignoreMissing);
      } catch (IOException var9) {
         throw new _MiscTemplateException(var9, env, new Object[]{"Template inclusion failed (for parameter value ", new _DelayedJQuote(includedTemplateName), "):\n", new _DelayedGetMessage(var9)});
      }

      if (includedTemplate != null) {
         env.include(includedTemplate);
      }

      return null;
   }

   protected String dump(boolean canonical) {
      StringBuilder buf = new StringBuilder();
      if (canonical) {
         buf.append('<');
      }

      buf.append(this.getNodeTypeSymbol());
      buf.append(' ');
      buf.append(this.includedTemplateNameExp.getCanonicalForm());
      if (this.encodingExp != null) {
         buf.append(" encoding=").append(this.encodingExp.getCanonicalForm());
      }

      if (this.parseExp != null) {
         buf.append(" parse=").append(this.parseExp.getCanonicalForm());
      }

      if (this.ignoreMissingExp != null) {
         buf.append(" ignore_missing=").append(this.ignoreMissingExp.getCanonicalForm());
      }

      if (canonical) {
         buf.append("/>");
      }

      return buf.toString();
   }

   String getNodeTypeSymbol() {
      return "#include";
   }

   int getParameterCount() {
      return 4;
   }

   Object getParameterValue(int idx) {
      switch (idx) {
         case 0:
            return this.includedTemplateNameExp;
         case 1:
            return this.parseExp;
         case 2:
            return this.encodingExp;
         case 3:
            return this.ignoreMissingExp;
         default:
            throw new IndexOutOfBoundsException();
      }
   }

   ParameterRole getParameterRole(int idx) {
      switch (idx) {
         case 0:
            return ParameterRole.TEMPLATE_NAME;
         case 1:
            return ParameterRole.PARSE_PARAMETER;
         case 2:
            return ParameterRole.ENCODING_PARAMETER;
         case 3:
            return ParameterRole.IGNORE_MISSING_PARAMETER;
         default:
            throw new IndexOutOfBoundsException();
      }
   }

   boolean isNestedBlockRepeater() {
      return false;
   }

   private boolean getYesNo(Expression exp, String s) throws TemplateException {
      try {
         return StringUtil.getYesNo(s);
      } catch (IllegalArgumentException var4) {
         throw new _MiscTemplateException(exp, new Object[]{"Value must be boolean (or one of these strings: \"n\", \"no\", \"f\", \"false\", \"y\", \"yes\", \"t\", \"true\"), but it was ", new _DelayedJQuote(s), "."});
      }
   }

   boolean isShownInStackTrace() {
      return true;
   }
}
