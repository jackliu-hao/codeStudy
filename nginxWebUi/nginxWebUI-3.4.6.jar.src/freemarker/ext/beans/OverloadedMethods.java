/*     */ package freemarker.ext.beans;
/*     */ 
/*     */ import freemarker.core._DelayedConversionToString;
/*     */ import freemarker.core._ErrorDescriptionBuilder;
/*     */ import freemarker.core._TemplateModelException;
/*     */ import freemarker.template.TemplateModel;
/*     */ import freemarker.template.TemplateModelException;
/*     */ import freemarker.template.utility.ClassUtil;
/*     */ import java.lang.reflect.Constructor;
/*     */ import java.lang.reflect.Method;
/*     */ import java.util.HashSet;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ final class OverloadedMethods
/*     */ {
/*     */   private final OverloadedMethodsSubset fixArgMethods;
/*     */   private OverloadedMethodsSubset varargMethods;
/*     */   private final boolean bugfixed;
/*     */   
/*     */   OverloadedMethods(boolean bugfixed) {
/*  51 */     this.bugfixed = bugfixed;
/*  52 */     this.fixArgMethods = new OverloadedFixArgsMethods(bugfixed);
/*     */   }
/*     */   
/*     */   void addMethod(Method method) {
/*  56 */     Class[] paramTypes = method.getParameterTypes();
/*  57 */     addCallableMemberDescriptor(new ReflectionCallableMemberDescriptor(method, paramTypes));
/*     */   }
/*     */   
/*     */   void addConstructor(Constructor constr) {
/*  61 */     Class[] paramTypes = constr.getParameterTypes();
/*  62 */     addCallableMemberDescriptor(new ReflectionCallableMemberDescriptor(constr, paramTypes));
/*     */   }
/*     */ 
/*     */   
/*     */   private void addCallableMemberDescriptor(ReflectionCallableMemberDescriptor memberDesc) {
/*  67 */     this.fixArgMethods.addCallableMemberDescriptor(memberDesc);
/*  68 */     if (memberDesc.isVarargs()) {
/*  69 */       if (this.varargMethods == null) {
/*  70 */         this.varargMethods = new OverloadedVarArgsMethods(this.bugfixed);
/*     */       }
/*  72 */       this.varargMethods.addCallableMemberDescriptor(memberDesc);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   MemberAndArguments getMemberAndArguments(List tmArgs, BeansWrapper unwrapper) throws TemplateModelException {
/*  79 */     MaybeEmptyMemberAndArguments varargsRes, fixArgsRes = this.fixArgMethods.getMemberAndArguments(tmArgs, unwrapper);
/*  80 */     if (fixArgsRes instanceof MemberAndArguments) {
/*  81 */       return (MemberAndArguments)fixArgsRes;
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*  86 */     if (this.varargMethods != null) {
/*  87 */       varargsRes = this.varargMethods.getMemberAndArguments(tmArgs, unwrapper);
/*  88 */       if (varargsRes instanceof MemberAndArguments) {
/*  89 */         return (MemberAndArguments)varargsRes;
/*     */       }
/*     */     } else {
/*  92 */       varargsRes = null;
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 101 */     _ErrorDescriptionBuilder edb = new _ErrorDescriptionBuilder(new Object[] { toCompositeErrorMessage((EmptyMemberAndArguments)fixArgsRes, (EmptyMemberAndArguments)varargsRes, tmArgs), "\nThe matching overload was searched among these members:\n", memberListToString() });
/* 102 */     if (!this.bugfixed) {
/* 103 */       edb.tip("You seem to use BeansWrapper with incompatibleImprovements set below 2.3.21. If you think this error is unfounded, enabling 2.3.21 fixes may helps. See version history for more.");
/*     */     }
/*     */     
/* 106 */     addMarkupBITipAfterNoNoMarchIfApplicable(edb, tmArgs);
/* 107 */     throw new _TemplateModelException(edb);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private Object[] toCompositeErrorMessage(EmptyMemberAndArguments fixArgsEmptyRes, EmptyMemberAndArguments varargsEmptyRes, List tmArgs) {
/*     */     Object[] argsErrorMsg;
/* 114 */     if (varargsEmptyRes != null) {
/* 115 */       if (fixArgsEmptyRes == null || fixArgsEmptyRes.isNumberOfArgumentsWrong()) {
/* 116 */         argsErrorMsg = toErrorMessage(varargsEmptyRes, tmArgs);
/*     */       
/*     */       }
/*     */       else {
/*     */ 
/*     */         
/* 122 */         argsErrorMsg = new Object[] { "When trying to call the non-varargs overloads:\n", toErrorMessage(fixArgsEmptyRes, tmArgs), "\nWhen trying to call the varargs overloads:\n", toErrorMessage(varargsEmptyRes, null) };
/*     */       } 
/*     */     } else {
/*     */       
/* 126 */       argsErrorMsg = toErrorMessage(fixArgsEmptyRes, tmArgs);
/*     */     } 
/* 128 */     return argsErrorMsg;
/*     */   }
/*     */   
/*     */   private Object[] toErrorMessage(EmptyMemberAndArguments res, List tmArgs) {
/* 132 */     Object[] unwrappedArgs = res.getUnwrappedArguments();
/* 133 */     (new Object[3])[0] = res
/* 134 */       .getErrorDescription(); (new Object[3])[0] = "\nThe FTL type of the argument values were: "; (new Object[3])[1] = 
/*     */ 
/*     */       
/* 137 */       getTMActualParameterTypes(tmArgs); (new Object[3])[2] = "."; (new Object[3])[1] = (tmArgs != null) ? new Object[3] : ""; (new Object[2])[0] = "\nThe Java type of the argument values were: "; (new Object[2])[1] = 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 142 */       getUnwrappedActualParameterTypes(unwrappedArgs) + "."; return new Object[] { null, null, (unwrappedArgs != null) ? new Object[2] : "" };
/*     */   }
/*     */ 
/*     */   
/*     */   private _DelayedConversionToString memberListToString() {
/* 147 */     return new _DelayedConversionToString(null)
/*     */       {
/*     */         protected String doConversion(Object obj)
/*     */         {
/* 151 */           Iterator<CallableMemberDescriptor> fixArgMethodsIter = OverloadedMethods.this.fixArgMethods.getMemberDescriptors();
/* 152 */           Iterator<CallableMemberDescriptor> varargMethodsIter = (OverloadedMethods.this.varargMethods != null) ? OverloadedMethods.this.varargMethods.getMemberDescriptors() : null;
/*     */           
/* 154 */           boolean hasMethods = (fixArgMethodsIter.hasNext() || (varargMethodsIter != null && varargMethodsIter.hasNext()));
/* 155 */           if (hasMethods) {
/* 156 */             StringBuilder sb = new StringBuilder();
/* 157 */             HashSet<CallableMemberDescriptor> fixArgMethods = new HashSet();
/* 158 */             while (fixArgMethodsIter.hasNext()) {
/* 159 */               if (sb.length() != 0) sb.append(",\n"); 
/* 160 */               sb.append("    ");
/* 161 */               CallableMemberDescriptor callableMemberDesc = fixArgMethodsIter.next();
/* 162 */               fixArgMethods.add(callableMemberDesc);
/* 163 */               sb.append(callableMemberDesc.getDeclaration());
/*     */             } 
/* 165 */             if (varargMethodsIter != null) {
/* 166 */               while (varargMethodsIter.hasNext()) {
/* 167 */                 CallableMemberDescriptor callableMemberDesc = varargMethodsIter.next();
/* 168 */                 if (!fixArgMethods.contains(callableMemberDesc)) {
/* 169 */                   if (sb.length() != 0) sb.append(",\n"); 
/* 170 */                   sb.append("    ");
/* 171 */                   sb.append(callableMemberDesc.getDeclaration());
/*     */                 } 
/*     */               } 
/*     */             }
/* 175 */             return sb.toString();
/*     */           } 
/* 177 */           return "No members";
/*     */         }
/*     */       };
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void addMarkupBITipAfterNoNoMarchIfApplicable(_ErrorDescriptionBuilder edb, List tmArgs) {
/* 190 */     for (int argIdx = 0; argIdx < tmArgs.size(); argIdx++) {
/* 191 */       Object tmArg = tmArgs.get(argIdx);
/* 192 */       if (tmArg instanceof freemarker.core.TemplateMarkupOutputModel) {
/* 193 */         for (Iterator<CallableMemberDescriptor> membDescs = this.fixArgMethods.getMemberDescriptors(); membDescs.hasNext(); ) {
/* 194 */           CallableMemberDescriptor membDesc = membDescs.next();
/* 195 */           Class[] paramTypes = membDesc.getParamTypes();
/*     */           
/* 197 */           Class<?> paramType = null;
/* 198 */           if (membDesc.isVarargs() && argIdx >= paramTypes.length - 1) {
/* 199 */             paramType = paramTypes[paramTypes.length - 1];
/* 200 */             if (paramType.isArray()) {
/* 201 */               paramType = paramType.getComponentType();
/*     */             }
/*     */           } 
/* 204 */           if (paramType == null && argIdx < paramTypes.length) {
/* 205 */             paramType = paramTypes[argIdx];
/*     */           }
/* 207 */           if (paramType != null && 
/* 208 */             paramType.isAssignableFrom(String.class) && !paramType.isAssignableFrom(tmArg.getClass())) {
/* 209 */             edb.tip("A markup output value can be converted to markup string like value?markup_string. But consider if the Java method whose argument it will be can handle markup strings properly.");
/*     */             return;
/*     */           } 
/*     */         } 
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   private _DelayedConversionToString getTMActualParameterTypes(List<TemplateModel> arguments) {
/* 219 */     String[] argumentTypeDescs = new String[arguments.size()];
/* 220 */     for (int i = 0; i < arguments.size(); i++) {
/* 221 */       argumentTypeDescs[i] = ClassUtil.getFTLTypeDescription(arguments.get(i));
/*     */     }
/*     */     
/* 224 */     return new DelayedCallSignatureToString((Object[])argumentTypeDescs)
/*     */       {
/*     */         String argumentToString(Object argType)
/*     */         {
/* 228 */           return (String)argType;
/*     */         }
/*     */       };
/*     */   }
/*     */ 
/*     */   
/*     */   private Object getUnwrappedActualParameterTypes(Object[] unwrappedArgs) {
/* 235 */     Class[] argumentTypes = new Class[unwrappedArgs.length];
/* 236 */     for (int i = 0; i < unwrappedArgs.length; i++) {
/* 237 */       Object unwrappedArg = unwrappedArgs[i];
/* 238 */       argumentTypes[i] = (unwrappedArg != null) ? unwrappedArg.getClass() : null;
/*     */     } 
/*     */     
/* 241 */     return new DelayedCallSignatureToString((Object[])argumentTypes)
/*     */       {
/*     */         String argumentToString(Object argType)
/*     */         {
/* 245 */           return (argType != null) ? 
/* 246 */             ClassUtil.getShortClassName((Class)argType) : 
/* 247 */             ClassUtil.getShortClassNameOfObject(null);
/*     */         }
/*     */       };
/*     */   }
/*     */   
/*     */   private abstract class DelayedCallSignatureToString
/*     */     extends _DelayedConversionToString
/*     */   {
/*     */     public DelayedCallSignatureToString(Object[] argTypeArray) {
/* 256 */       super(argTypeArray);
/*     */     }
/*     */ 
/*     */     
/*     */     protected String doConversion(Object obj) {
/* 261 */       Object[] argTypes = (Object[])obj;
/*     */       
/* 263 */       StringBuilder sb = new StringBuilder();
/* 264 */       for (int i = 0; i < argTypes.length; i++) {
/* 265 */         if (i != 0) sb.append(", "); 
/* 266 */         sb.append(argumentToString(argTypes[i]));
/*     */       } 
/*     */       
/* 269 */       return sb.toString();
/*     */     }
/*     */     
/*     */     abstract String argumentToString(Object param1Object);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\ext\beans\OverloadedMethods.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */