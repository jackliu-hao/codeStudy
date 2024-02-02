package freemarker.core;

import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateException;
import freemarker.template.utility.StringUtil;

class NestedContentNotSupportedException extends TemplateException {
   public static void check(TemplateDirectiveBody body) throws NestedContentNotSupportedException {
      if (body != null) {
         if (body instanceof Environment.NestedElementTemplateDirectiveBody) {
            TemplateElement[] tes = ((Environment.NestedElementTemplateDirectiveBody)body).getChildrenBuffer();
            if (tes == null || tes.length == 0 || tes[0] instanceof ThreadInterruptionSupportTemplatePostProcessor.ThreadInterruptionCheck && (tes.length == 1 || tes[1] == null)) {
               return;
            }
         }

         throw new NestedContentNotSupportedException(Environment.getCurrentEnvironment());
      }
   }

   private NestedContentNotSupportedException(Environment env) {
      this((String)null, (Exception)null, env);
   }

   private NestedContentNotSupportedException(Exception cause, Environment env) {
      this((String)null, cause, env);
   }

   private NestedContentNotSupportedException(String description, Environment env) {
      this(description, (Exception)null, env);
   }

   private NestedContentNotSupportedException(String description, Exception cause, Environment env) {
      super("Nested content (body) not supported." + (description != null ? " " + StringUtil.jQuote(description) : ""), cause, env);
   }
}
