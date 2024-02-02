package freemarker.core;

import freemarker.template.Configuration;
import freemarker.template.SimpleDate;
import freemarker.template.SimpleScalar;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateHashModel;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateScalarModel;
import freemarker.template._TemplateAPI;
import freemarker.template.utility.StringUtil;
import java.util.Arrays;
import java.util.Date;

final class BuiltinVariable extends Expression {
   static final String TEMPLATE_NAME_CC = "templateName";
   static final String TEMPLATE_NAME = "template_name";
   static final String MAIN_TEMPLATE_NAME_CC = "mainTemplateName";
   static final String MAIN_TEMPLATE_NAME = "main_template_name";
   static final String CURRENT_TEMPLATE_NAME_CC = "currentTemplateName";
   static final String CURRENT_TEMPLATE_NAME = "current_template_name";
   static final String NAMESPACE = "namespace";
   static final String MAIN = "main";
   static final String GLOBALS = "globals";
   static final String LOCALS = "locals";
   static final String DATA_MODEL_CC = "dataModel";
   static final String DATA_MODEL = "data_model";
   static final String LANG = "lang";
   static final String LOCALE = "locale";
   static final String LOCALE_OBJECT_CC = "localeObject";
   static final String LOCALE_OBJECT = "locale_object";
   static final String TIME_ZONE_CC = "timeZone";
   static final String TIME_ZONE = "time_zone";
   static final String CURRENT_NODE_CC = "currentNode";
   static final String CURRENT_NODE = "current_node";
   static final String NODE = "node";
   static final String PASS = "pass";
   static final String VARS = "vars";
   static final String VERSION = "version";
   static final String INCOMPATIBLE_IMPROVEMENTS_CC = "incompatibleImprovements";
   static final String INCOMPATIBLE_IMPROVEMENTS = "incompatible_improvements";
   static final String ERROR = "error";
   static final String OUTPUT_ENCODING_CC = "outputEncoding";
   static final String OUTPUT_ENCODING = "output_encoding";
   static final String OUTPUT_FORMAT_CC = "outputFormat";
   static final String OUTPUT_FORMAT = "output_format";
   static final String AUTO_ESC_CC = "autoEsc";
   static final String AUTO_ESC = "auto_esc";
   static final String URL_ESCAPING_CHARSET_CC = "urlEscapingCharset";
   static final String URL_ESCAPING_CHARSET = "url_escaping_charset";
   static final String NOW = "now";
   static final String GET_OPTIONAL_TEMPLATE = "get_optional_template";
   static final String GET_OPTIONAL_TEMPLATE_CC = "getOptionalTemplate";
   static final String CALLER_TEMPLATE_NAME = "caller_template_name";
   static final String CALLER_TEMPLATE_NAME_CC = "callerTemplateName";
   static final String ARGS = "args";
   static final String[] SPEC_VAR_NAMES = new String[]{"args", "autoEsc", "auto_esc", "callerTemplateName", "caller_template_name", "currentNode", "currentTemplateName", "current_node", "current_template_name", "dataModel", "data_model", "error", "getOptionalTemplate", "get_optional_template", "globals", "incompatibleImprovements", "incompatible_improvements", "lang", "locale", "localeObject", "locale_object", "locals", "main", "mainTemplateName", "main_template_name", "namespace", "node", "now", "outputEncoding", "outputFormat", "output_encoding", "output_format", "pass", "templateName", "template_name", "timeZone", "time_zone", "urlEscapingCharset", "url_escaping_charset", "vars", "version"};
   private final String name;
   private final TemplateModel parseTimeValue;

   BuiltinVariable(Token nameTk, FMParserTokenManager tokenManager, TemplateModel parseTimeValue) throws ParseException {
      String name = nameTk.image;
      this.parseTimeValue = parseTimeValue;
      if (Arrays.binarySearch(SPEC_VAR_NAMES, name) < 0) {
         StringBuilder sb = new StringBuilder();
         sb.append("Unknown special variable name: ");
         sb.append(StringUtil.jQuote(name)).append(".");
         int namingConvention = tokenManager.namingConvention;
         int shownNamingConvention = namingConvention != 10 ? namingConvention : 11;
         String correctName;
         if (!name.equals("auto_escape") && !name.equals("auto_escaping") && !name.equals("autoesc")) {
            if (!name.equals("autoEscape") && !name.equals("autoEscaping")) {
               correctName = null;
            } else {
               correctName = "autoEsc";
            }
         } else {
            correctName = "auto_esc";
         }

         if (correctName != null) {
            sb.append(" You may meant: ");
            sb.append(StringUtil.jQuote(correctName)).append(".");
         }

         sb.append("\nThe allowed special variable names are: ");
         boolean first = true;

         for(int i = 0; i < SPEC_VAR_NAMES.length; ++i) {
            String correctName = SPEC_VAR_NAMES[i];
            int correctNameNamingConvetion = _CoreStringUtils.getIdentifierNamingConvention(correctName);
            if (shownNamingConvention == 12) {
               if (correctNameNamingConvetion == 11) {
                  continue;
               }
            } else if (correctNameNamingConvetion == 12) {
               continue;
            }

            if (first) {
               first = false;
            } else {
               sb.append(", ");
            }

            sb.append(correctName);
         }

         throw new ParseException(sb.toString(), (Template)null, nameTk);
      } else {
         this.name = name.intern();
      }
   }

   TemplateModel _eval(Environment env) throws TemplateException {
      if (this.parseTimeValue != null) {
         return this.parseTimeValue;
      } else if (this.name == "namespace") {
         return env.getCurrentNamespace();
      } else if (this.name == "main") {
         return env.getMainNamespace();
      } else if (this.name == "globals") {
         return env.getGlobalVariables();
      } else if (this.name == "locals") {
         Macro.Context ctx = env.getCurrentMacroContext();
         return ctx == null ? null : ctx.getLocals();
      } else if (this.name != "data_model" && this.name != "dataModel") {
         if (this.name == "vars") {
            return new VarsHash(env);
         } else if (this.name == "locale") {
            return new SimpleScalar(env.getLocale().toString());
         } else if (this.name != "locale_object" && this.name != "localeObject") {
            if (this.name == "lang") {
               return new SimpleScalar(env.getLocale().getLanguage());
            } else if (this.name != "current_node" && this.name != "node" && this.name != "currentNode") {
               if (this.name != "template_name" && this.name != "templateName") {
                  if (this.name != "main_template_name" && this.name != "mainTemplateName") {
                     if (this.name != "current_template_name" && this.name != "currentTemplateName") {
                        if (this.name == "pass") {
                           return Macro.DO_NOTHING_MACRO;
                        } else {
                           String s;
                           if (this.name != "output_encoding" && this.name != "outputEncoding") {
                              if (this.name != "url_escaping_charset" && this.name != "urlEscapingCharset") {
                                 if (this.name == "error") {
                                    return new SimpleScalar(env.getCurrentRecoveredErrorMessage());
                                 } else if (this.name == "now") {
                                    return new SimpleDate(new Date(), 3);
                                 } else if (this.name == "version") {
                                    return new SimpleScalar(Configuration.getVersionNumber());
                                 } else if (this.name != "incompatible_improvements" && this.name != "incompatibleImprovements") {
                                    if (this.name == "get_optional_template") {
                                       return GetOptionalTemplateMethod.INSTANCE;
                                    } else if (this.name == "getOptionalTemplate") {
                                       return GetOptionalTemplateMethod.INSTANCE_CC;
                                    } else if (this.name != "caller_template_name" && this.name != "callerTemplateName") {
                                       if (this.name == "args") {
                                          TemplateModel args = this.getRequiredMacroContext(env).getArgsSpecialVariableValue();
                                          if (args == null) {
                                             throw new _MiscTemplateException(this, new Object[]{"The \"", "args", "\" special variable wasn't initialized.", this.name});
                                          } else {
                                             return args;
                                          }
                                       } else if (this.name != "time_zone" && this.name != "timeZone") {
                                          throw new _MiscTemplateException(this, new Object[]{"Invalid special variable: ", this.name});
                                       } else {
                                          return new SimpleScalar(env.getTimeZone().getID());
                                       }
                                    } else {
                                       TemplateObject callPlace = this.getRequiredMacroContext(env).callPlace;
                                       String name = callPlace != null ? callPlace.getTemplate().getName() : null;
                                       return (TemplateModel)(name != null ? new SimpleScalar(name) : TemplateScalarModel.EMPTY_STRING);
                                    }
                                 } else {
                                    return new SimpleScalar(env.getConfiguration().getIncompatibleImprovements().toString());
                                 }
                              } else {
                                 s = env.getURLEscapingCharset();
                                 return SimpleScalar.newInstanceOrNull(s);
                              }
                           } else {
                              s = env.getOutputEncoding();
                              return SimpleScalar.newInstanceOrNull(s);
                           }
                        }
                     } else {
                        return SimpleScalar.newInstanceOrNull(env.getCurrentTemplate().getName());
                     }
                  } else {
                     return SimpleScalar.newInstanceOrNull(env.getMainTemplate().getName());
                  }
               } else {
                  return env.getConfiguration().getIncompatibleImprovements().intValue() >= _TemplateAPI.VERSION_INT_2_3_23 ? new SimpleScalar(env.getTemplate230().getName()) : new SimpleScalar(env.getTemplate().getName());
               }
            } else {
               return env.getCurrentVisitorNode();
            }
         } else {
            return env.getObjectWrapper().wrap(env.getLocale());
         }
      } else {
         return env.getDataModel();
      }
   }

   private Macro.Context getRequiredMacroContext(Environment env) throws TemplateException {
      Macro.Context ctx = env.getCurrentMacroContext();
      if (ctx == null) {
         throw new TemplateException("Can't get ." + this.name + " here, as there's no macro or function (that's implemented in the template) call in context.", env);
      } else {
         return ctx;
      }
   }

   public String toString() {
      return "." + this.name;
   }

   public String getCanonicalForm() {
      return "." + this.name;
   }

   String getNodeTypeSymbol() {
      return this.getCanonicalForm();
   }

   boolean isLiteral() {
      return false;
   }

   protected Expression deepCloneWithIdentifierReplaced_inner(String replacedIdentifier, Expression replacement, Expression.ReplacemenetState replacementState) {
      return this;
   }

   int getParameterCount() {
      return 0;
   }

   Object getParameterValue(int idx) {
      throw new IndexOutOfBoundsException();
   }

   ParameterRole getParameterRole(int idx) {
      throw new IndexOutOfBoundsException();
   }

   static class VarsHash implements TemplateHashModel {
      Environment env;

      VarsHash(Environment env) {
         this.env = env;
      }

      public TemplateModel get(String key) throws TemplateModelException {
         return this.env.getVariable(key);
      }

      public boolean isEmpty() {
         return false;
      }
   }
}
