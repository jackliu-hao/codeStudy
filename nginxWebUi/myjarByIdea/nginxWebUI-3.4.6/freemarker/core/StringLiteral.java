package freemarker.core;

import freemarker.template.SimpleScalar;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateScalarModel;
import freemarker.template.utility.StringUtil;
import java.io.StringReader;
import java.util.Iterator;
import java.util.List;

final class StringLiteral extends Expression implements TemplateScalarModel {
   private final String value;
   private List<Object> dynamicValue;

   StringLiteral(String value) {
      this.value = value;
   }

   void parseValue(FMParser parentParser, OutputFormat outputFormat) throws ParseException {
      Template parentTemplate = this.getTemplate();
      ParserConfiguration pcfg = parentTemplate.getParserConfiguration();
      int intSyn = pcfg.getInterpolationSyntax();
      if (this.value.length() > 3 && ((intSyn == 20 || intSyn == 21) && (this.value.indexOf("${") != -1 || intSyn == 20 && this.value.indexOf("#{") != -1) || intSyn == 22 && this.value.indexOf("[=") != -1)) {
         try {
            SimpleCharStream simpleCharacterStream = new SimpleCharStream(new StringReader(this.value), this.beginLine, this.beginColumn + 1, this.value.length());
            simpleCharacterStream.setTabSize(pcfg.getTabSize());
            FMParserTokenManager tkMan = new FMParserTokenManager(simpleCharacterStream);
            FMParser parser = new FMParser(parentTemplate, false, tkMan, pcfg);
            parser.setupStringLiteralMode(parentParser, outputFormat);

            try {
               this.dynamicValue = parser.StaticTextAndInterpolations();
            } finally {
               parser.tearDownStringLiteralMode(parentParser);
            }
         } catch (ParseException var13) {
            var13.setTemplateName(parentTemplate.getSourceName());
            throw var13;
         }

         this.constantValue = null;
      }

   }

   TemplateModel _eval(Environment env) throws TemplateException {
      if (this.dynamicValue == null) {
         return new SimpleScalar(this.value);
      } else {
         StringBuilder plainTextResult = null;
         TemplateMarkupOutputModel<?> markupResult = null;
         Iterator var4 = this.dynamicValue.iterator();

         while(var4.hasNext()) {
            Object part = var4.next();
            Object calcedPart = part instanceof String ? part : ((Interpolation)part).calculateInterpolatedStringOrMarkup(env);
            TemplateMarkupOutputModel moPart;
            if (markupResult != null) {
               moPart = calcedPart instanceof String ? markupResult.getOutputFormat().fromPlainTextByEscaping((String)calcedPart) : (TemplateMarkupOutputModel)calcedPart;
               markupResult = EvalUtil.concatMarkupOutputs(this, markupResult, moPart);
            } else if (calcedPart instanceof String) {
               String partStr = (String)calcedPart;
               if (plainTextResult == null) {
                  plainTextResult = new StringBuilder(partStr);
               } else {
                  plainTextResult.append(partStr);
               }
            } else {
               moPart = (TemplateMarkupOutputModel)calcedPart;
               if (plainTextResult != null) {
                  TemplateMarkupOutputModel<?> leftHandMO = moPart.getOutputFormat().fromPlainTextByEscaping(plainTextResult.toString());
                  markupResult = EvalUtil.concatMarkupOutputs(this, leftHandMO, moPart);
                  plainTextResult = null;
               } else {
                  markupResult = moPart;
               }
            }
         }

         return (TemplateModel)(markupResult != null ? markupResult : (plainTextResult != null ? new SimpleScalar(plainTextResult.toString()) : SimpleScalar.EMPTY_STRING));
      }
   }

   public String getAsString() {
      return this.value;
   }

   boolean isSingleInterpolationLiteral() {
      return this.dynamicValue != null && this.dynamicValue.size() == 1 && this.dynamicValue.get(0) instanceof Interpolation;
   }

   public String getCanonicalForm() {
      if (this.dynamicValue == null) {
         return StringUtil.ftlQuote(this.value);
      } else {
         StringBuilder sb = new StringBuilder();
         sb.append('"');
         Iterator var2 = this.dynamicValue.iterator();

         while(var2.hasNext()) {
            Object child = var2.next();
            if (child instanceof Interpolation) {
               sb.append(((Interpolation)child).getCanonicalFormInStringLiteral());
            } else {
               sb.append(StringUtil.FTLStringLiteralEnc((String)child, '"'));
            }
         }

         sb.append('"');
         return sb.toString();
      }
   }

   String getNodeTypeSymbol() {
      return this.dynamicValue == null ? this.getCanonicalForm() : "dynamic \"...\"";
   }

   boolean isLiteral() {
      return this.dynamicValue == null;
   }

   protected Expression deepCloneWithIdentifierReplaced_inner(String replacedIdentifier, Expression replacement, Expression.ReplacemenetState replacementState) {
      StringLiteral cloned = new StringLiteral(this.value);
      cloned.dynamicValue = this.dynamicValue;
      return cloned;
   }

   int getParameterCount() {
      return this.dynamicValue == null ? 0 : this.dynamicValue.size();
   }

   Object getParameterValue(int idx) {
      this.checkIndex(idx);
      return this.dynamicValue.get(idx);
   }

   private void checkIndex(int idx) {
      if (this.dynamicValue == null || idx >= this.dynamicValue.size()) {
         throw new IndexOutOfBoundsException();
      }
   }

   ParameterRole getParameterRole(int idx) {
      this.checkIndex(idx);
      return ParameterRole.VALUE_PART;
   }
}
