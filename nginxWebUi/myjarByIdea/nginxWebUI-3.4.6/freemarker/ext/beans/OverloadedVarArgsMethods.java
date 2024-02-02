package freemarker.ext.beans;

import freemarker.core.BugException;
import freemarker.template.ObjectWrapperAndUnwrapper;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import java.lang.reflect.Array;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

class OverloadedVarArgsMethods extends OverloadedMethodsSubset {
   OverloadedVarArgsMethods(boolean bugfixed) {
      super(bugfixed);
   }

   Class[] preprocessParameterTypes(CallableMemberDescriptor memberDesc) {
      Class[] preprocessedParamTypes = (Class[])memberDesc.getParamTypes().clone();
      int ln = preprocessedParamTypes.length;
      Class varArgsCompType = preprocessedParamTypes[ln - 1].getComponentType();
      if (varArgsCompType == null) {
         throw new BugException("Only varargs methods should be handled here");
      } else {
         preprocessedParamTypes[ln - 1] = varArgsCompType;
         return preprocessedParamTypes;
      }
   }

   void afterWideningUnwrappingHints(Class[] paramTypes, int[] paramNumericalTypes) {
      int paramCount = paramTypes.length;
      Class[][] unwrappingHintsByParamCount = this.getUnwrappingHintsByParamCount();

      int i;
      for(i = paramCount - 1; i >= 0; --i) {
         Class[] previousHints = unwrappingHintsByParamCount[i];
         if (previousHints != null) {
            this.widenHintsToCommonSupertypes(paramCount, previousHints, this.getTypeFlags(i));
            break;
         }
      }

      if (paramCount + 1 < unwrappingHintsByParamCount.length) {
         Class[] oneLongerHints = unwrappingHintsByParamCount[paramCount + 1];
         if (oneLongerHints != null) {
            this.widenHintsToCommonSupertypes(paramCount, oneLongerHints, this.getTypeFlags(paramCount + 1));
         }
      }

      for(i = paramCount + 1; i < unwrappingHintsByParamCount.length; ++i) {
         this.widenHintsToCommonSupertypes(i, paramTypes, paramNumericalTypes);
      }

      if (paramCount > 0) {
         this.widenHintsToCommonSupertypes(paramCount - 1, paramTypes, paramNumericalTypes);
      }

   }

   private void widenHintsToCommonSupertypes(int paramCountOfWidened, Class[] wideningTypes, int[] wideningTypeFlags) {
      Class[] typesToWiden = this.getUnwrappingHintsByParamCount()[paramCountOfWidened];
      if (typesToWiden != null) {
         int typesToWidenLen = typesToWiden.length;
         int wideningTypesLen = wideningTypes.length;
         int min = Math.min(wideningTypesLen, typesToWidenLen);

         for(int i = 0; i < min; ++i) {
            typesToWiden[i] = this.getCommonSupertypeForUnwrappingHint(typesToWiden[i], wideningTypes[i]);
         }

         if (typesToWidenLen > wideningTypesLen) {
            Class varargsComponentType = wideningTypes[wideningTypesLen - 1];

            for(int i = wideningTypesLen; i < typesToWidenLen; ++i) {
               typesToWiden[i] = this.getCommonSupertypeForUnwrappingHint(typesToWiden[i], varargsComponentType);
            }
         }

         if (this.bugfixed) {
            this.mergeInTypesFlags(paramCountOfWidened, wideningTypeFlags);
         }

      }
   }

   MaybeEmptyMemberAndArguments getMemberAndArguments(List tmArgs, BeansWrapper unwrapper) throws TemplateModelException {
      if (tmArgs == null) {
         tmArgs = Collections.EMPTY_LIST;
      }

      int argsLen = tmArgs.size();
      Class[][] unwrappingHintsByParamCount = this.getUnwrappingHintsByParamCount();
      Object[] pojoArgs = new Object[argsLen];
      int[] typesFlags = null;

      label64:
      for(int paramCount = Math.min(argsLen + 1, unwrappingHintsByParamCount.length - 1); paramCount >= 0; --paramCount) {
         Class[] unwarappingHints = unwrappingHintsByParamCount[paramCount];
         if (unwarappingHints == null) {
            if (paramCount == 0) {
               return EmptyMemberAndArguments.WRONG_NUMBER_OF_ARGUMENTS;
            }
         } else {
            typesFlags = this.getTypeFlags(paramCount);
            if (typesFlags == ALL_ZEROS_ARRAY) {
               typesFlags = null;
            }

            Iterator it = tmArgs.iterator();
            int i = 0;

            while(true) {
               if (i >= argsLen) {
                  break label64;
               }

               int paramIdx = i < paramCount ? i : paramCount - 1;
               Object pojo = unwrapper.tryUnwrapTo((TemplateModel)it.next(), unwarappingHints[paramIdx], typesFlags != null ? typesFlags[paramIdx] : 0);
               if (pojo == ObjectWrapperAndUnwrapper.CANT_UNWRAP_TO_TARGET_CLASS) {
                  break;
               }

               pojoArgs[i] = pojo;
               ++i;
            }
         }
      }

      MaybeEmptyCallableMemberDescriptor maybeEmtpyMemberDesc = this.getMemberDescriptorForArgs(pojoArgs, true);
      if (maybeEmtpyMemberDesc instanceof CallableMemberDescriptor) {
         CallableMemberDescriptor memberDesc = (CallableMemberDescriptor)maybeEmtpyMemberDesc;
         Object argsOrErrorIdx = this.replaceVarargsSectionWithArray(pojoArgs, tmArgs, memberDesc, unwrapper);
         if (argsOrErrorIdx instanceof Object[]) {
            Object[] pojoArgsWithArray = (Object[])((Object[])argsOrErrorIdx);
            if (this.bugfixed) {
               if (typesFlags != null) {
                  this.forceNumberArgumentsToParameterTypes(pojoArgsWithArray, memberDesc.getParamTypes(), typesFlags);
               }
            } else {
               BeansWrapper.coerceBigDecimals(memberDesc.getParamTypes(), pojoArgsWithArray);
            }

            return new MemberAndArguments(memberDesc, pojoArgsWithArray);
         } else {
            return EmptyMemberAndArguments.noCompatibleOverload((Integer)argsOrErrorIdx);
         }
      } else {
         return EmptyMemberAndArguments.from((EmptyCallableMemberDescriptor)maybeEmtpyMemberDesc, pojoArgs);
      }
   }

   private Object replaceVarargsSectionWithArray(Object[] args, List modelArgs, CallableMemberDescriptor memberDesc, BeansWrapper unwrapper) throws TemplateModelException {
      Class[] paramTypes = memberDesc.getParamTypes();
      int paramCount = paramTypes.length;
      Class varArgsCompType = paramTypes[paramCount - 1].getComponentType();
      int totalArgCount = args.length;
      int fixArgCount = paramCount - 1;
      Object varargs;
      if (args.length != paramCount) {
         Object[] packedArgs = new Object[paramCount];
         System.arraycopy(args, 0, packedArgs, 0, fixArgCount);
         varargs = Array.newInstance(varArgsCompType, totalArgCount - fixArgCount);

         for(int i = fixArgCount; i < totalArgCount; ++i) {
            Object val = unwrapper.tryUnwrapTo((TemplateModel)modelArgs.get(i), varArgsCompType);
            if (val == ObjectWrapperAndUnwrapper.CANT_UNWRAP_TO_TARGET_CLASS) {
               return i + 1;
            }

            Array.set(varargs, i - fixArgCount, val);
         }

         packedArgs[fixArgCount] = varargs;
         return packedArgs;
      } else {
         Object val = unwrapper.tryUnwrapTo((TemplateModel)modelArgs.get(fixArgCount), varArgsCompType);
         if (val == ObjectWrapperAndUnwrapper.CANT_UNWRAP_TO_TARGET_CLASS) {
            return fixArgCount + 1;
         } else {
            varargs = Array.newInstance(varArgsCompType, 1);
            Array.set(varargs, 0, val);
            args[fixArgCount] = varargs;
            return args;
         }
      }
   }
}
