package freemarker.core;

import freemarker.template.MalformedTemplateNameException;
import freemarker.template.SimpleNumber;
import freemarker.template.Template;
import freemarker.template.TemplateBooleanModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateMethodModelEx;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateScalarModel;
import java.io.StringReader;
import java.util.List;

class BuiltInsForStringsMisc {
   private BuiltInsForStringsMisc() {
   }

   static class absolute_template_nameBI extends BuiltInForString {
      TemplateModel calculateResult(String s, Environment env) throws TemplateException {
         return new AbsoluteTemplateNameResult(s, env);
      }

      private class AbsoluteTemplateNameResult implements TemplateScalarModel, TemplateMethodModelEx {
         private final String pathToResolve;
         private final Environment env;

         public AbsoluteTemplateNameResult(String pathToResolve, Environment env) {
            this.pathToResolve = pathToResolve;
            this.env = env;
         }

         public Object exec(List args) throws TemplateModelException {
            absolute_template_nameBI.this.checkMethodArgCount(args, 1);
            return this.resolvePath(absolute_template_nameBI.this.getStringMethodArg(args, 0));
         }

         public String getAsString() throws TemplateModelException {
            return this.resolvePath(absolute_template_nameBI.this.getTemplate().getName());
         }

         private String resolvePath(String basePath) throws TemplateModelException {
            try {
               return this.env.rootBasedToAbsoluteTemplateName(this.env.toFullTemplateName(basePath, this.pathToResolve));
            } catch (MalformedTemplateNameException var3) {
               throw new _TemplateModelException(var3, new Object[]{"Can't resolve ", new _DelayedJQuote(this.pathToResolve), "to absolute template name using base ", new _DelayedJQuote(basePath), "; see cause exception"});
            }
         }
      }
   }

   static class numberBI extends BuiltInForString {
      TemplateModel calculateResult(String s, Environment env) throws TemplateException {
         try {
            return new SimpleNumber(env.getArithmeticEngine().toNumber(s));
         } catch (NumberFormatException var4) {
            throw NonNumericalException.newMalformedNumberException(this, s, env);
         }
      }
   }

   static class evalJsonBI extends BuiltInForString {
      TemplateModel calculateResult(String s, Environment env) throws TemplateException {
         try {
            return JSONParser.parse(s);
         } catch (JSONParser.JSONParseException var4) {
            throw new _MiscTemplateException(this, env, new Object[]{"Failed to \"?", this.key, "\" string with this error:\n\n", "---begin-message---\n", new _DelayedGetMessage(var4), "\n---end-message---", "\n\nThe failing expression:"});
         }
      }
   }

   static class evalBI extends OutputFormatBoundBuiltIn {
      protected TemplateModel calculateResult(Environment env) throws TemplateException {
         return this.calculateResult(BuiltInForString.getTargetString(this.target, env), env);
      }

      TemplateModel calculateResult(String s, Environment env) throws TemplateException {
         Template parentTemplate = this.getTemplate();
         Expression exp = null;

         try {
            try {
               ParserConfiguration pCfg = parentTemplate.getParserConfiguration();
               SimpleCharStream simpleCharStream = new SimpleCharStream(new StringReader("(" + s + ")"), -1000000000, 1, s.length() + 2);
               simpleCharStream.setTabSize(((ParserConfiguration)pCfg).getTabSize());
               FMParserTokenManager tkMan = new FMParserTokenManager(simpleCharStream);
               tkMan.SwitchTo(2);
               if (((ParserConfiguration)pCfg).getOutputFormat() != this.outputFormat) {
                  pCfg = new _ParserConfigurationWithInheritedFormat((ParserConfiguration)pCfg, this.outputFormat, this.autoEscapingPolicy);
               }

               FMParser parser = new FMParser(parentTemplate, false, tkMan, (ParserConfiguration)pCfg);
               exp = parser.Expression();
            } catch (TokenMgrError var10) {
               throw var10.toParseException(parentTemplate);
            }
         } catch (ParseException var11) {
            throw new _MiscTemplateException(this, env, new Object[]{"Failed to \"?", this.key, "\" string with this error:\n\n", "---begin-message---\n", new _DelayedGetMessage(var11), "\n---end-message---", "\n\nThe failing expression:"});
         }

         try {
            return exp.eval(env);
         } catch (TemplateException var9) {
            throw new _MiscTemplateException(var9, new Object[]{this, env, "Failed to \"?", this.key, "\" string with this error:\n\n", "---begin-message---\n", new _DelayedGetMessageWithoutStackTop(var9), "\n---end-message---", "\n\nThe failing expression:"});
         }
      }
   }

   static class booleanBI extends BuiltInForString {
      TemplateModel calculateResult(String s, Environment env) throws TemplateException {
         boolean b;
         if (s.equals("true")) {
            b = true;
         } else if (s.equals("false")) {
            b = false;
         } else if (s.equals(env.getTrueStringValue())) {
            b = true;
         } else {
            if (!s.equals(env.getFalseStringValue())) {
               throw new _MiscTemplateException(this, env, new Object[]{"Can't convert this string to boolean: ", new _DelayedJQuote(s)});
            }

            b = false;
         }

         return b ? TemplateBooleanModel.TRUE : TemplateBooleanModel.FALSE;
      }
   }
}
