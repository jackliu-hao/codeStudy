package freemarker.core;

import freemarker.template.TemplateModelException;
import java.io.IOException;
import java.io.Writer;

public abstract class CommonMarkupOutputFormat<MO extends CommonTemplateMarkupOutputModel> extends MarkupOutputFormat<MO> {
   protected CommonMarkupOutputFormat() {
   }

   public final MO fromPlainTextByEscaping(String textToEsc) throws TemplateModelException {
      return this.newTemplateMarkupOutputModel(textToEsc, (String)null);
   }

   public final MO fromMarkup(String markupText) throws TemplateModelException {
      return this.newTemplateMarkupOutputModel((String)null, markupText);
   }

   public final void output(MO mo, Writer out) throws IOException, TemplateModelException {
      String mc = mo.getMarkupContent();
      if (mc != null) {
         out.write(mc);
      } else {
         this.output(mo.getPlainTextContent(), out);
      }

   }

   public abstract void output(String var1, Writer var2) throws IOException, TemplateModelException;

   public final String getSourcePlainText(MO mo) throws TemplateModelException {
      return mo.getPlainTextContent();
   }

   public final String getMarkupString(MO mo) throws TemplateModelException {
      String mc = mo.getMarkupContent();
      if (mc != null) {
         return mc;
      } else {
         mc = this.escapePlainText(mo.getPlainTextContent());
         mo.setMarkupContent(mc);
         return mc;
      }
   }

   public final MO concat(MO mo1, MO mo2) throws TemplateModelException {
      String pc1 = mo1.getPlainTextContent();
      String mc1 = mo1.getMarkupContent();
      String pc2 = mo2.getPlainTextContent();
      String mc2 = mo2.getMarkupContent();
      String pc3 = pc1 != null && pc2 != null ? pc1 + pc2 : null;
      String mc3 = mc1 != null && mc2 != null ? mc1 + mc2 : null;
      if (pc3 == null && mc3 == null) {
         return pc1 != null ? this.newTemplateMarkupOutputModel((String)null, this.getMarkupString(mo1) + mc2) : this.newTemplateMarkupOutputModel((String)null, mc1 + this.getMarkupString(mo2));
      } else {
         return this.newTemplateMarkupOutputModel(pc3, mc3);
      }
   }

   public boolean isEmpty(MO mo) throws TemplateModelException {
      String s = mo.getPlainTextContent();
      if (s != null) {
         return s.length() == 0;
      } else {
         return mo.getMarkupContent().length() == 0;
      }
   }

   public boolean isOutputFormatMixingAllowed() {
      return false;
   }

   public boolean isAutoEscapedByDefault() {
      return true;
   }

   protected abstract MO newTemplateMarkupOutputModel(String var1, String var2) throws TemplateModelException;
}
