/*    */ package freemarker.ext.beans;
/*    */ 
/*    */ import freemarker.template.ObjectWrapperAndUnwrapper;
/*    */ import freemarker.template.TemplateModel;
/*    */ import freemarker.template.TemplateModelException;
/*    */ import java.util.Collections;
/*    */ import java.util.Iterator;
/*    */ import java.util.List;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ class OverloadedFixArgsMethods
/*    */   extends OverloadedMethodsSubset
/*    */ {
/*    */   OverloadedFixArgsMethods(boolean bugfixed) {
/* 35 */     super(bugfixed);
/*    */   }
/*    */ 
/*    */   
/*    */   Class[] preprocessParameterTypes(CallableMemberDescriptor memberDesc) {
/* 40 */     return memberDesc.getParamTypes();
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   void afterWideningUnwrappingHints(Class[] paramTypes, int[] paramNumericalTypes) {}
/*    */ 
/*    */ 
/*    */   
/*    */   MaybeEmptyMemberAndArguments getMemberAndArguments(List tmArgs, BeansWrapper unwrapper) throws TemplateModelException {
/* 51 */     if (tmArgs == null)
/*    */     {
/* 53 */       tmArgs = Collections.EMPTY_LIST;
/*    */     }
/* 55 */     int argCount = tmArgs.size();
/* 56 */     Class[][] unwrappingHintsByParamCount = getUnwrappingHintsByParamCount();
/* 57 */     if (unwrappingHintsByParamCount.length <= argCount) {
/* 58 */       return EmptyMemberAndArguments.WRONG_NUMBER_OF_ARGUMENTS;
/*    */     }
/* 60 */     Class[] unwarppingHints = unwrappingHintsByParamCount[argCount];
/* 61 */     if (unwarppingHints == null) {
/* 62 */       return EmptyMemberAndArguments.WRONG_NUMBER_OF_ARGUMENTS;
/*    */     }
/*    */     
/* 65 */     Object[] pojoArgs = new Object[argCount];
/*    */     
/* 67 */     int[] typeFlags = getTypeFlags(argCount);
/* 68 */     if (typeFlags == ALL_ZEROS_ARRAY) {
/* 69 */       typeFlags = null;
/*    */     }
/*    */     
/* 72 */     Iterator<TemplateModel> it = tmArgs.iterator();
/* 73 */     for (int i = 0; i < argCount; i++) {
/* 74 */       Object pojo = unwrapper.tryUnwrapTo(it
/* 75 */           .next(), unwarppingHints[i], (typeFlags != null) ? typeFlags[i] : 0);
/*    */ 
/*    */       
/* 78 */       if (pojo == ObjectWrapperAndUnwrapper.CANT_UNWRAP_TO_TARGET_CLASS) {
/* 79 */         return EmptyMemberAndArguments.noCompatibleOverload(i + 1);
/*    */       }
/* 81 */       pojoArgs[i] = pojo;
/*    */     } 
/*    */     
/* 84 */     MaybeEmptyCallableMemberDescriptor maybeEmtpyMemberDesc = getMemberDescriptorForArgs(pojoArgs, false);
/* 85 */     if (maybeEmtpyMemberDesc instanceof CallableMemberDescriptor) {
/* 86 */       CallableMemberDescriptor memberDesc = (CallableMemberDescriptor)maybeEmtpyMemberDesc;
/* 87 */       if (this.bugfixed) {
/* 88 */         if (typeFlags != null)
/*    */         {
/*    */ 
/*    */           
/* 92 */           forceNumberArgumentsToParameterTypes(pojoArgs, memberDesc.getParamTypes(), typeFlags);
/*    */         }
/*    */       } else {
/* 95 */         BeansWrapper.coerceBigDecimals(memberDesc.getParamTypes(), pojoArgs);
/*    */       } 
/* 97 */       return new MemberAndArguments(memberDesc, pojoArgs);
/*    */     } 
/* 99 */     return EmptyMemberAndArguments.from((EmptyCallableMemberDescriptor)maybeEmtpyMemberDesc, pojoArgs);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\ext\beans\OverloadedFixArgsMethods.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */