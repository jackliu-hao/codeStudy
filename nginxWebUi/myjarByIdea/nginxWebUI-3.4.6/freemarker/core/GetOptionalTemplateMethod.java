package freemarker.core;

import freemarker.template.MalformedTemplateNameException;
import freemarker.template.SimpleHash;
import freemarker.template.Template;
import freemarker.template.TemplateBooleanModel;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateDirectiveModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateHashModelEx;
import freemarker.template.TemplateHashModelEx2;
import freemarker.template.TemplateMethodModelEx;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateScalarModel;
import freemarker.template._TemplateAPI;
import freemarker.template.utility.TemplateModelUtils;
import java.io.IOException;
import java.util.List;
import java.util.Map;

class GetOptionalTemplateMethod implements TemplateMethodModelEx {
   static final GetOptionalTemplateMethod INSTANCE = new GetOptionalTemplateMethod("get_optional_template");
   static final GetOptionalTemplateMethod INSTANCE_CC = new GetOptionalTemplateMethod("getOptionalTemplate");
   private static final String OPTION_ENCODING = "encoding";
   private static final String OPTION_PARSE = "parse";
   private static final String RESULT_INCLUDE = "include";
   private static final String RESULT_IMPORT = "import";
   private static final String RESULT_EXISTS = "exists";
   private final String methodName;

   private GetOptionalTemplateMethod(String builtInVarName) {
      this.methodName = "." + builtInVarName;
   }

   public Object exec(List args) throws TemplateModelException {
      int argCnt = args.size();
      if (argCnt >= 1 && argCnt <= 2) {
         final Environment env = Environment.getCurrentEnvironment();
         if (env == null) {
            throw new IllegalStateException("No freemarer.core.Environment is associated to the current thread.");
         } else {
            TemplateModel arg = (TemplateModel)args.get(0);
            if (!(arg instanceof TemplateScalarModel)) {
               throw _MessageUtil.newMethodArgMustBeStringException(this.methodName, 0, arg);
            } else {
               String encoding = EvalUtil.modelToString((TemplateScalarModel)arg, (Expression)null, env);

               String absTemplateName;
               try {
                  absTemplateName = env.toFullTemplateName(env.getCurrentTemplate().getName(), encoding);
               } catch (MalformedTemplateNameException var13) {
                  throw new _TemplateModelException(var13, "Failed to convert template path to full path; see cause exception.");
               }

               TemplateHashModelEx options;
               if (argCnt > 1) {
                  TemplateModel arg = (TemplateModel)args.get(1);
                  if (!(arg instanceof TemplateHashModelEx)) {
                     throw _MessageUtil.newMethodArgMustBeExtendedHashException(this.methodName, 1, arg);
                  }

                  options = (TemplateHashModelEx)arg;
               } else {
                  options = null;
               }

               encoding = null;
               boolean parse = true;
               if (options != null) {
                  TemplateHashModelEx2.KeyValuePairIterator kvpi = TemplateModelUtils.getKeyValuePairIterator(options);

                  while(kvpi.hasNext()) {
                     TemplateHashModelEx2.KeyValuePair kvp = kvpi.next();
                     TemplateModel optValue = kvp.getKey();
                     if (!(optValue instanceof TemplateScalarModel)) {
                        throw _MessageUtil.newMethodArgInvalidValueException(this.methodName, 1, "All keys in the options hash must be strings, but found ", new _DelayedAOrAn(new _DelayedFTLTypeDescription(optValue)));
                     }

                     String optName = ((TemplateScalarModel)optValue).getAsString();
                     optValue = kvp.getValue();
                     if ("encoding".equals(optName)) {
                        encoding = this.getStringOption("encoding", optValue);
                     } else {
                        if (!"parse".equals(optName)) {
                           throw _MessageUtil.newMethodArgInvalidValueException(this.methodName, 1, "Unsupported option ", new _DelayedJQuote(optName), "; valid names are: ", new _DelayedJQuote("encoding"), ", ", new _DelayedJQuote("parse"), ".");
                        }

                        parse = this.getBooleanOption("parse", optValue);
                     }
                  }
               }

               final Template template;
               try {
                  template = env.getTemplateForInclusion(absTemplateName, encoding, parse, true);
               } catch (IOException var12) {
                  throw new _TemplateModelException(var12, new Object[]{"I/O error when trying to load optional template ", new _DelayedJQuote(absTemplateName), "; see cause exception"});
               }

               SimpleHash result = new SimpleHash(_TemplateAPI.SAFE_OBJECT_WRAPPER);
               result.put("exists", template != null);
               if (template != null) {
                  result.put("include", new TemplateDirectiveModel() {
                     public void execute(Environment env, Map params, TemplateModel[] loopVars, TemplateDirectiveBody body) throws TemplateException, IOException {
                        if (!params.isEmpty()) {
                           throw new TemplateException("This directive supports no parameters.", env);
                        } else if (loopVars.length != 0) {
                           throw new TemplateException("This directive supports no loop variables.", env);
                        } else if (body != null) {
                           throw new TemplateException("This directive supports no nested content.", env);
                        } else {
                           env.include(template);
                        }
                     }
                  });
                  result.put("import", new TemplateMethodModelEx() {
                     public Object exec(List args) throws TemplateModelException {
                        if (!args.isEmpty()) {
                           throw new TemplateModelException("This method supports no parameters.");
                        } else {
                           try {
                              return env.importLib((Template)template, (String)null);
                           } catch (TemplateException | IOException var3) {
                              throw new _TemplateModelException(var3, "Failed to import loaded template; see cause exception");
                           }
                        }
                     }
                  });
               }

               return result;
            }
         }
      } else {
         throw _MessageUtil.newArgCntError(this.methodName, argCnt, 1, 2);
      }
   }

   private boolean getBooleanOption(String optionName, TemplateModel value) throws TemplateModelException {
      if (!(value instanceof TemplateBooleanModel)) {
         throw _MessageUtil.newMethodArgInvalidValueException(this.methodName, 1, "The value of the ", new _DelayedJQuote(optionName), " option must be a boolean, but it was ", new _DelayedAOrAn(new _DelayedFTLTypeDescription(value)), ".");
      } else {
         return ((TemplateBooleanModel)value).getAsBoolean();
      }
   }

   private String getStringOption(String optionName, TemplateModel value) throws TemplateModelException {
      if (!(value instanceof TemplateScalarModel)) {
         throw _MessageUtil.newMethodArgInvalidValueException(this.methodName, 1, "The value of the ", new _DelayedJQuote(optionName), " option must be a string, but it was ", new _DelayedAOrAn(new _DelayedFTLTypeDescription(value)), ".");
      } else {
         return EvalUtil.modelToString((TemplateScalarModel)value, (Expression)null, (Environment)null);
      }
   }
}
