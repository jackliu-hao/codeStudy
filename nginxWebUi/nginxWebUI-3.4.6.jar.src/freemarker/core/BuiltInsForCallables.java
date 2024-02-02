/*     */ package freemarker.core;
/*     */ 
/*     */ import freemarker.template.TemplateDirectiveBody;
/*     */ import freemarker.template.TemplateDirectiveModel;
/*     */ import freemarker.template.TemplateException;
/*     */ import freemarker.template.TemplateHashModelEx;
/*     */ import freemarker.template.TemplateHashModelEx2;
/*     */ import freemarker.template.TemplateMethodModel;
/*     */ import freemarker.template.TemplateMethodModelEx;
/*     */ import freemarker.template.TemplateModel;
/*     */ import freemarker.template.TemplateModelException;
/*     */ import freemarker.template.TemplateScalarModel;
/*     */ import freemarker.template.TemplateSequenceModel;
/*     */ import freemarker.template.utility.TemplateModelUtils;
/*     */ import java.io.IOException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.LinkedHashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ class BuiltInsForCallables
/*     */ {
/*     */   static abstract class AbstractWithArgsBI
/*     */     extends BuiltIn
/*     */   {
/*     */     protected abstract boolean isOrderLast();
/*     */     
/*     */     TemplateModel _eval(Environment env) throws TemplateException {
/*  49 */       TemplateModel model = this.target.eval(env);
/*  50 */       if (model instanceof Macro)
/*  51 */         return (TemplateModel)new BIMethodForMacroAndFunction((Macro)model); 
/*  52 */       if (model instanceof TemplateDirectiveModel)
/*  53 */         return (TemplateModel)new BIMethodForDirective((TemplateDirectiveModel)model); 
/*  54 */       if (model instanceof TemplateMethodModel) {
/*  55 */         return (TemplateModel)new BIMethodForMethod((TemplateMethodModel)model);
/*     */       }
/*  57 */       throw new UnexpectedTypeException(this.target, model, "macro, function, directive, or method", new Class[] { Macro.class, TemplateDirectiveModel.class, TemplateMethodModel.class }, env);
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     private class BIMethodForMacroAndFunction
/*     */       implements TemplateMethodModelEx
/*     */     {
/*     */       private final Macro macroOrFunction;
/*     */ 
/*     */ 
/*     */       
/*     */       private BIMethodForMacroAndFunction(Macro macroOrFunction) {
/*  70 */         this.macroOrFunction = macroOrFunction;
/*     */       }
/*     */       
/*     */       public Object exec(List<TemplateModel> args) throws TemplateModelException {
/*     */         Macro.WithArgs withArgs;
/*  75 */         BuiltInsForCallables.AbstractWithArgsBI.this.checkMethodArgCount(args.size(), 1);
/*  76 */         TemplateModel argTM = args.get(0);
/*     */ 
/*     */         
/*  79 */         if (argTM instanceof TemplateSequenceModel) {
/*  80 */           withArgs = new Macro.WithArgs((TemplateSequenceModel)argTM, BuiltInsForCallables.AbstractWithArgsBI.this.isOrderLast());
/*  81 */         } else if (argTM instanceof TemplateHashModelEx) {
/*  82 */           if (this.macroOrFunction.isFunction()) {
/*  83 */             throw new _TemplateModelException(new Object[] { "When applied on a function, ?", this.this$0.key, " can't have a hash argument. Use a sequence argument." });
/*     */           }
/*     */           
/*  86 */           withArgs = new Macro.WithArgs((TemplateHashModelEx)argTM, BuiltInsForCallables.AbstractWithArgsBI.this.isOrderLast());
/*     */         } else {
/*  88 */           throw _MessageUtil.newMethodArgMustBeExtendedHashOrSequnceException("?" + BuiltInsForCallables.AbstractWithArgsBI.this.key, 0, argTM);
/*     */         } 
/*     */         
/*  91 */         return new Macro(this.macroOrFunction, withArgs);
/*     */       }
/*     */     }
/*     */     
/*     */     private class BIMethodForMethod
/*     */       implements TemplateMethodModelEx
/*     */     {
/*     */       private final TemplateMethodModel method;
/*     */       
/*     */       public BIMethodForMethod(TemplateMethodModel method) {
/* 101 */         this.method = method;
/*     */       }
/*     */ 
/*     */       
/*     */       public Object exec(List<TemplateModel> args) throws TemplateModelException {
/* 106 */         BuiltInsForCallables.AbstractWithArgsBI.this.checkMethodArgCount(args.size(), 1);
/* 107 */         TemplateModel argTM = args.get(0);
/*     */         
/* 109 */         if (argTM instanceof TemplateSequenceModel) {
/* 110 */           final TemplateSequenceModel withArgs = (TemplateSequenceModel)argTM;
/* 111 */           if (this.method instanceof TemplateMethodModelEx) {
/* 112 */             return new TemplateMethodModelEx()
/*     */               {
/*     */                 public Object exec(List<? extends TemplateModel> origArgs) throws TemplateModelException {
/* 115 */                   int withArgsSize = withArgs.size();
/*     */                   
/* 117 */                   List<TemplateModel> newArgs = new ArrayList<>(withArgsSize + origArgs.size());
/*     */                   
/* 119 */                   if (BuiltInsForCallables.AbstractWithArgsBI.this.isOrderLast()) {
/* 120 */                     newArgs.addAll(origArgs);
/*     */                   }
/* 122 */                   for (int i = 0; i < withArgsSize; i++) {
/* 123 */                     newArgs.add(withArgs.get(i));
/*     */                   }
/* 125 */                   if (!BuiltInsForCallables.AbstractWithArgsBI.this.isOrderLast()) {
/* 126 */                     newArgs.addAll(origArgs);
/*     */                   }
/*     */                   
/* 129 */                   return BuiltInsForCallables.AbstractWithArgsBI.BIMethodForMethod.this.method.exec(newArgs);
/*     */                 }
/*     */               };
/*     */           }
/* 133 */           return new TemplateMethodModel()
/*     */             {
/*     */               public Object exec(List<? extends String> origArgs) throws TemplateModelException {
/* 136 */                 int withArgsSize = withArgs.size();
/*     */                 
/* 138 */                 List<String> newArgs = new ArrayList<>(withArgsSize + origArgs.size());
/*     */                 
/* 140 */                 if (BuiltInsForCallables.AbstractWithArgsBI.this.isOrderLast()) {
/* 141 */                   newArgs.addAll(origArgs);
/*     */                 }
/* 143 */                 for (int i = 0; i < withArgsSize; i++) {
/* 144 */                   TemplateModel argVal = withArgs.get(i);
/* 145 */                   newArgs.add(argValueToString(argVal));
/*     */                 } 
/* 147 */                 if (!BuiltInsForCallables.AbstractWithArgsBI.this.isOrderLast()) {
/* 148 */                   newArgs.addAll(origArgs);
/*     */                 }
/*     */                 
/* 151 */                 return BuiltInsForCallables.AbstractWithArgsBI.BIMethodForMethod.this.method.exec(newArgs);
/*     */               }
/*     */ 
/*     */ 
/*     */ 
/*     */               
/*     */               private String argValueToString(TemplateModel argVal) throws TemplateModelException {
/*     */                 String argValStr;
/* 159 */                 if (argVal instanceof TemplateScalarModel) {
/* 160 */                   argValStr = ((TemplateScalarModel)argVal).getAsString();
/* 161 */                 } else if (argVal == null) {
/* 162 */                   argValStr = null;
/*     */                 } else {
/*     */                   try {
/* 165 */                     argValStr = EvalUtil.coerceModelToPlainText(argVal, null, null, 
/* 166 */                         Environment.getCurrentEnvironment());
/* 167 */                   } catch (TemplateException e) {
/* 168 */                     throw new _TemplateModelException(e, new Object[] { "Failed to convert method argument to string. Argument type was: ", new _DelayedFTLTypeDescription(argVal) });
/*     */                   } 
/*     */                 } 
/*     */ 
/*     */                 
/* 173 */                 return argValStr;
/*     */               }
/*     */             };
/*     */         } 
/* 177 */         if (argTM instanceof TemplateHashModelEx) {
/* 178 */           throw new _TemplateModelException(new Object[] { "When applied on a method, ?", this.this$0.key, " can't have a hash argument. Use a sequence argument." });
/*     */         }
/*     */         
/* 181 */         throw _MessageUtil.newMethodArgMustBeExtendedHashOrSequnceException("?" + BuiltInsForCallables.AbstractWithArgsBI.this.key, 0, argTM);
/*     */       }
/*     */     }
/*     */ 
/*     */     
/*     */     private class BIMethodForDirective
/*     */       implements TemplateMethodModelEx
/*     */     {
/*     */       private final TemplateDirectiveModel directive;
/*     */       
/*     */       public BIMethodForDirective(TemplateDirectiveModel directive) {
/* 192 */         this.directive = directive;
/*     */       }
/*     */ 
/*     */       
/*     */       public Object exec(List<TemplateModel> args) throws TemplateModelException {
/* 197 */         BuiltInsForCallables.AbstractWithArgsBI.this.checkMethodArgCount(args.size(), 1);
/* 198 */         TemplateModel argTM = args.get(0);
/*     */         
/* 200 */         if (argTM instanceof TemplateHashModelEx) {
/* 201 */           final TemplateHashModelEx withArgs = (TemplateHashModelEx)argTM;
/* 202 */           return new TemplateDirectiveModel()
/*     */             {
/*     */               public void execute(Environment env, Map<? extends String, ? extends TemplateModel> origArgs, TemplateModel[] loopVars, TemplateDirectiveBody body) throws TemplateException, IOException
/*     */               {
/* 206 */                 int withArgsSize = withArgs.size();
/*     */ 
/*     */ 
/*     */                 
/* 210 */                 Map<String, TemplateModel> newArgs = new LinkedHashMap<>((withArgsSize + origArgs.size()) * 4 / 3, 1.0F);
/*     */ 
/*     */                 
/* 213 */                 TemplateHashModelEx2.KeyValuePairIterator withArgsIter = TemplateModelUtils.getKeyValuePairIterator(withArgs);
/* 214 */                 if (BuiltInsForCallables.AbstractWithArgsBI.this.isOrderLast()) {
/* 215 */                   newArgs.putAll(origArgs);
/* 216 */                   while (withArgsIter.hasNext()) {
/* 217 */                     TemplateHashModelEx2.KeyValuePair withArgsKVP = withArgsIter.next();
/* 218 */                     String argName = getArgumentName(withArgsKVP);
/* 219 */                     if (!newArgs.containsKey(argName)) {
/* 220 */                       newArgs.put(argName, withArgsKVP.getValue());
/*     */                     }
/*     */                   } 
/*     */                 } else {
/* 224 */                   while (withArgsIter.hasNext()) {
/* 225 */                     TemplateHashModelEx2.KeyValuePair withArgsKVP = withArgsIter.next();
/* 226 */                     newArgs.put(getArgumentName(withArgsKVP), withArgsKVP.getValue());
/*     */                   } 
/* 228 */                   newArgs.putAll(origArgs);
/*     */                 } 
/*     */                 
/* 231 */                 BuiltInsForCallables.AbstractWithArgsBI.BIMethodForDirective.this.directive.execute(env, newArgs, loopVars, body);
/*     */               }
/*     */ 
/*     */               
/*     */               private String getArgumentName(TemplateHashModelEx2.KeyValuePair withArgsKVP) throws TemplateModelException {
/* 236 */                 TemplateModel argNameTM = withArgsKVP.getKey();
/* 237 */                 if (!(argNameTM instanceof TemplateScalarModel)) {
/* 238 */                   throw new _TemplateModelException(new Object[] { "Expected string keys in the ?", this.this$1.this$0.key, "(...) arguments, but one of the keys was ", new _DelayedAOrAn(new _DelayedFTLTypeDescription(argNameTM)), "." });
/*     */                 }
/*     */ 
/*     */ 
/*     */                 
/* 243 */                 return EvalUtil.modelToString((TemplateScalarModel)argNameTM, null, null); }
/*     */             };
/*     */         } 
/* 246 */         if (argTM instanceof TemplateSequenceModel) {
/* 247 */           throw new _TemplateModelException(new Object[] { "When applied on a directive, ?", this.this$0.key, "(...) can't have a sequence argument. Use a hash argument." });
/*     */         }
/*     */         
/* 250 */         throw _MessageUtil.newMethodArgMustBeExtendedHashOrSequnceException("?" + BuiltInsForCallables.AbstractWithArgsBI.this.key, 0, argTM);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   static final class with_argsBI
/*     */     extends AbstractWithArgsBI
/*     */   {
/*     */     protected boolean isOrderLast() {
/* 261 */       return false;
/*     */     }
/*     */   }
/*     */   
/*     */   static final class with_args_lastBI
/*     */     extends AbstractWithArgsBI {
/*     */     protected boolean isOrderLast() {
/* 268 */       return true;
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\core\BuiltInsForCallables.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */