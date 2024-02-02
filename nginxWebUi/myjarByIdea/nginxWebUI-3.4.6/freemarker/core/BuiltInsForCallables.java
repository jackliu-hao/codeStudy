package freemarker.core;

import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateDirectiveModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateHashModelEx;
import freemarker.template.TemplateHashModelEx2;
import freemarker.template.TemplateMethodModel;
import freemarker.template.TemplateMethodModelEx;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateScalarModel;
import freemarker.template.TemplateSequenceModel;
import freemarker.template.utility.TemplateModelUtils;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

class BuiltInsForCallables {
   static final class with_args_lastBI extends AbstractWithArgsBI {
      protected boolean isOrderLast() {
         return true;
      }
   }

   static final class with_argsBI extends AbstractWithArgsBI {
      protected boolean isOrderLast() {
         return false;
      }
   }

   abstract static class AbstractWithArgsBI extends BuiltIn {
      protected abstract boolean isOrderLast();

      TemplateModel _eval(Environment env) throws TemplateException {
         TemplateModel model = this.target.eval(env);
         if (model instanceof Macro) {
            return new BIMethodForMacroAndFunction((Macro)model);
         } else if (model instanceof TemplateDirectiveModel) {
            return new BIMethodForDirective((TemplateDirectiveModel)model);
         } else if (model instanceof TemplateMethodModel) {
            return new BIMethodForMethod((TemplateMethodModel)model);
         } else {
            throw new UnexpectedTypeException(this.target, model, "macro, function, directive, or method", new Class[]{Macro.class, TemplateDirectiveModel.class, TemplateMethodModel.class}, env);
         }
      }

      private class BIMethodForDirective implements TemplateMethodModelEx {
         private final TemplateDirectiveModel directive;

         public BIMethodForDirective(TemplateDirectiveModel directive) {
            this.directive = directive;
         }

         public Object exec(List args) throws TemplateModelException {
            AbstractWithArgsBI.this.checkMethodArgCount(args.size(), 1);
            TemplateModel argTM = (TemplateModel)args.get(0);
            if (argTM instanceof TemplateHashModelEx) {
               final TemplateHashModelEx withArgs = (TemplateHashModelEx)argTM;
               return new TemplateDirectiveModel() {
                  public void execute(Environment env, Map origArgs, TemplateModel[] loopVars, TemplateDirectiveBody body) throws TemplateException, IOException {
                     int withArgsSize = withArgs.size();
                     Map<String, TemplateModel> newArgs = new LinkedHashMap((withArgsSize + origArgs.size()) * 4 / 3, 1.0F);
                     TemplateHashModelEx2.KeyValuePairIterator withArgsIter = TemplateModelUtils.getKeyValuePairIterator(withArgs);
                     TemplateHashModelEx2.KeyValuePair withArgsKVP;
                     if (AbstractWithArgsBI.this.isOrderLast()) {
                        newArgs.putAll(origArgs);

                        while(withArgsIter.hasNext()) {
                           withArgsKVP = withArgsIter.next();
                           String argName = this.getArgumentName(withArgsKVP);
                           if (!newArgs.containsKey(argName)) {
                              newArgs.put(argName, withArgsKVP.getValue());
                           }
                        }
                     } else {
                        while(true) {
                           if (!withArgsIter.hasNext()) {
                              newArgs.putAll(origArgs);
                              break;
                           }

                           withArgsKVP = withArgsIter.next();
                           newArgs.put(this.getArgumentName(withArgsKVP), withArgsKVP.getValue());
                        }
                     }

                     BIMethodForDirective.this.directive.execute(env, newArgs, loopVars, body);
                  }

                  private String getArgumentName(TemplateHashModelEx2.KeyValuePair withArgsKVP) throws TemplateModelException {
                     TemplateModel argNameTM = withArgsKVP.getKey();
                     if (!(argNameTM instanceof TemplateScalarModel)) {
                        throw new _TemplateModelException(new Object[]{"Expected string keys in the ?", AbstractWithArgsBI.this.key, "(...) arguments, but one of the keys was ", new _DelayedAOrAn(new _DelayedFTLTypeDescription(argNameTM)), "."});
                     } else {
                        return EvalUtil.modelToString((TemplateScalarModel)argNameTM, (Expression)null, (Environment)null);
                     }
                  }
               };
            } else if (argTM instanceof TemplateSequenceModel) {
               throw new _TemplateModelException(new Object[]{"When applied on a directive, ?", AbstractWithArgsBI.this.key, "(...) can't have a sequence argument. Use a hash argument."});
            } else {
               throw _MessageUtil.newMethodArgMustBeExtendedHashOrSequnceException("?" + AbstractWithArgsBI.this.key, 0, argTM);
            }
         }
      }

      private class BIMethodForMethod implements TemplateMethodModelEx {
         private final TemplateMethodModel method;

         public BIMethodForMethod(TemplateMethodModel method) {
            this.method = method;
         }

         public Object exec(List args) throws TemplateModelException {
            AbstractWithArgsBI.this.checkMethodArgCount(args.size(), 1);
            TemplateModel argTM = (TemplateModel)args.get(0);
            if (argTM instanceof TemplateSequenceModel) {
               final TemplateSequenceModel withArgs = (TemplateSequenceModel)argTM;
               return this.method instanceof TemplateMethodModelEx ? new TemplateMethodModelEx() {
                  public Object exec(List origArgs) throws TemplateModelException {
                     int withArgsSize = withArgs.size();
                     List<TemplateModel> newArgs = new ArrayList(withArgsSize + origArgs.size());
                     if (AbstractWithArgsBI.this.isOrderLast()) {
                        newArgs.addAll(origArgs);
                     }

                     for(int i = 0; i < withArgsSize; ++i) {
                        newArgs.add(withArgs.get(i));
                     }

                     if (!AbstractWithArgsBI.this.isOrderLast()) {
                        newArgs.addAll(origArgs);
                     }

                     return BIMethodForMethod.this.method.exec(newArgs);
                  }
               } : new TemplateMethodModel() {
                  public Object exec(List origArgs) throws TemplateModelException {
                     int withArgsSize = withArgs.size();
                     List<String> newArgs = new ArrayList(withArgsSize + origArgs.size());
                     if (AbstractWithArgsBI.this.isOrderLast()) {
                        newArgs.addAll(origArgs);
                     }

                     for(int i = 0; i < withArgsSize; ++i) {
                        TemplateModel argVal = withArgs.get(i);
                        newArgs.add(this.argValueToString(argVal));
                     }

                     if (!AbstractWithArgsBI.this.isOrderLast()) {
                        newArgs.addAll(origArgs);
                     }

                     return BIMethodForMethod.this.method.exec(newArgs);
                  }

                  private String argValueToString(TemplateModel argVal) throws TemplateModelException {
                     String argValStr;
                     if (argVal instanceof TemplateScalarModel) {
                        argValStr = ((TemplateScalarModel)argVal).getAsString();
                     } else if (argVal == null) {
                        argValStr = null;
                     } else {
                        try {
                           argValStr = EvalUtil.coerceModelToPlainText(argVal, (Expression)null, (String)null, Environment.getCurrentEnvironment());
                        } catch (TemplateException var4) {
                           throw new _TemplateModelException(var4, new Object[]{"Failed to convert method argument to string. Argument type was: ", new _DelayedFTLTypeDescription(argVal)});
                        }
                     }

                     return argValStr;
                  }
               };
            } else if (argTM instanceof TemplateHashModelEx) {
               throw new _TemplateModelException(new Object[]{"When applied on a method, ?", AbstractWithArgsBI.this.key, " can't have a hash argument. Use a sequence argument."});
            } else {
               throw _MessageUtil.newMethodArgMustBeExtendedHashOrSequnceException("?" + AbstractWithArgsBI.this.key, 0, argTM);
            }
         }
      }

      private class BIMethodForMacroAndFunction implements TemplateMethodModelEx {
         private final Macro macroOrFunction;

         private BIMethodForMacroAndFunction(Macro macroOrFunction) {
            this.macroOrFunction = macroOrFunction;
         }

         public Object exec(List args) throws TemplateModelException {
            AbstractWithArgsBI.this.checkMethodArgCount(args.size(), 1);
            TemplateModel argTM = (TemplateModel)args.get(0);
            Macro.WithArgs withArgs;
            if (argTM instanceof TemplateSequenceModel) {
               withArgs = new Macro.WithArgs((TemplateSequenceModel)argTM, AbstractWithArgsBI.this.isOrderLast());
            } else {
               if (!(argTM instanceof TemplateHashModelEx)) {
                  throw _MessageUtil.newMethodArgMustBeExtendedHashOrSequnceException("?" + AbstractWithArgsBI.this.key, 0, argTM);
               }

               if (this.macroOrFunction.isFunction()) {
                  throw new _TemplateModelException(new Object[]{"When applied on a function, ?", AbstractWithArgsBI.this.key, " can't have a hash argument. Use a sequence argument."});
               }

               withArgs = new Macro.WithArgs((TemplateHashModelEx)argTM, AbstractWithArgsBI.this.isOrderLast());
            }

            return new Macro(this.macroOrFunction, withArgs);
         }

         // $FF: synthetic method
         BIMethodForMacroAndFunction(Macro x1, Object x2) {
            this(x1);
         }
      }
   }
}
