package freemarker.core;

import freemarker.template.MalformedTemplateNameException;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import java.io.IOException;

/** @deprecated */
@Deprecated
public final class LibraryLoad extends TemplateElement {
   private Expression importedTemplateNameExp;
   private String targetNsVarName;

   LibraryLoad(Template template, Expression templateName, String targetNsVarName) {
      this.targetNsVarName = targetNsVarName;
      this.importedTemplateNameExp = templateName;
   }

   TemplateElement[] accept(Environment env) throws TemplateException, IOException {
      String importedTemplateName = this.importedTemplateNameExp.evalAndCoerceToPlainText(env);

      String fullImportedTemplateName;
      try {
         fullImportedTemplateName = env.toFullTemplateName(this.getTemplate().getName(), importedTemplateName);
      } catch (MalformedTemplateNameException var6) {
         throw new _MiscTemplateException(var6, env, new Object[]{"Malformed template name ", new _DelayedJQuote(var6.getTemplateName()), ":\n", var6.getMalformednessDescription()});
      }

      try {
         env.importLib(fullImportedTemplateName, this.targetNsVarName);
         return null;
      } catch (IOException var5) {
         throw new _MiscTemplateException(var5, env, new Object[]{"Template importing failed (for parameter value ", new _DelayedJQuote(importedTemplateName), "):\n", new _DelayedGetMessage(var5)});
      }
   }

   protected String dump(boolean canonical) {
      StringBuilder buf = new StringBuilder();
      if (canonical) {
         buf.append('<');
      }

      buf.append(this.getNodeTypeSymbol());
      buf.append(' ');
      buf.append(this.importedTemplateNameExp.getCanonicalForm());
      buf.append(" as ");
      buf.append(_CoreStringUtils.toFTLTopLevelTragetIdentifier(this.targetNsVarName));
      if (canonical) {
         buf.append("/>");
      }

      return buf.toString();
   }

   String getNodeTypeSymbol() {
      return "#import";
   }

   int getParameterCount() {
      return 2;
   }

   Object getParameterValue(int idx) {
      switch (idx) {
         case 0:
            return this.importedTemplateNameExp;
         case 1:
            return this.targetNsVarName;
         default:
            throw new IndexOutOfBoundsException();
      }
   }

   ParameterRole getParameterRole(int idx) {
      switch (idx) {
         case 0:
            return ParameterRole.TEMPLATE_NAME;
         case 1:
            return ParameterRole.NAMESPACE;
         default:
            throw new IndexOutOfBoundsException();
      }
   }

   public String getTemplateName() {
      return this.importedTemplateNameExp.toString();
   }

   boolean isNestedBlockRepeater() {
      return false;
   }

   boolean isShownInStackTrace() {
      return true;
   }
}
