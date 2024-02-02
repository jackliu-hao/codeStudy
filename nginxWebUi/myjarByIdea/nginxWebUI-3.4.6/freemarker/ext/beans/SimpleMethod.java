package freemarker.ext.beans;

import freemarker.core.TemplateMarkupOutputModel;
import freemarker.core._DelayedFTLTypeDescription;
import freemarker.core._DelayedOrdinal;
import freemarker.core._ErrorDescriptionBuilder;
import freemarker.core._TemplateModelException;
import freemarker.template.ObjectWrapperAndUnwrapper;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.utility.ClassUtil;
import java.lang.reflect.Array;
import java.lang.reflect.Member;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

class SimpleMethod {
   static final String MARKUP_OUTPUT_TO_STRING_TIP = "A markup output value can be converted to markup string like value?markup_string. But consider if the Java method whose argument it will be can handle markup strings properly.";
   private final Member member;
   private final Class[] argTypes;

   protected SimpleMethod(Member member, Class[] argTypes) {
      this.member = member;
      this.argTypes = argTypes;
   }

   Object[] unwrapArguments(List arguments, BeansWrapper wrapper) throws TemplateModelException {
      if (arguments == null) {
         arguments = Collections.EMPTY_LIST;
      }

      boolean isVarArg = _MethodUtil.isVarargs(this.member);
      int typesLen = this.argTypes.length;
      if (isVarArg) {
         if (typesLen - 1 > arguments.size()) {
            throw new _TemplateModelException(new Object[]{_MethodUtil.invocationErrorMessageStart(this.member), " takes at least ", typesLen - 1, typesLen - 1 == 1 ? " argument" : " arguments", ", but ", arguments.size(), " was given."});
         }
      } else if (typesLen != arguments.size()) {
         throw new _TemplateModelException(new Object[]{_MethodUtil.invocationErrorMessageStart(this.member), " takes ", typesLen, typesLen == 1 ? " argument" : " arguments", ", but ", arguments.size(), " was given."});
      }

      Object[] args = this.unwrapArguments(arguments, this.argTypes, isVarArg, wrapper);
      return args;
   }

   private Object[] unwrapArguments(List args, Class[] argTypes, boolean isVarargs, BeansWrapper w) throws TemplateModelException {
      if (args == null) {
         return null;
      } else {
         int typesLen = argTypes.length;
         int argsLen = args.size();
         Object[] unwrappedArgs = new Object[typesLen];
         Iterator it = args.iterator();
         int normalArgCnt = isVarargs ? typesLen - 1 : typesLen;

         int argIdx;
         Class varargType;
         Object unwrappedArgVal;
         for(argIdx = 0; argIdx < normalArgCnt; unwrappedArgs[argIdx++] = unwrappedArgVal) {
            varargType = argTypes[argIdx];
            TemplateModel argVal = (TemplateModel)it.next();
            unwrappedArgVal = w.tryUnwrapTo(argVal, varargType);
            if (unwrappedArgVal == ObjectWrapperAndUnwrapper.CANT_UNWRAP_TO_TARGET_CLASS) {
               throw this.createArgumentTypeMismarchException(argIdx, argVal, varargType);
            }

            if (unwrappedArgVal == null && varargType.isPrimitive()) {
               throw this.createNullToPrimitiveArgumentException(argIdx, varargType);
            }
         }

         if (isVarargs) {
            varargType = argTypes[typesLen - 1];
            Class varargItemType = varargType.getComponentType();
            if (!it.hasNext()) {
               unwrappedArgs[argIdx++] = Array.newInstance(varargItemType, 0);
            } else {
               TemplateModel argVal = (TemplateModel)it.next();
               Object unwrappedArgVal;
               if (argsLen - argIdx == 1 && (unwrappedArgVal = w.tryUnwrapTo(argVal, varargType)) != ObjectWrapperAndUnwrapper.CANT_UNWRAP_TO_TARGET_CLASS) {
                  unwrappedArgs[argIdx++] = unwrappedArgVal;
               } else {
                  int varargArrayLen = argsLen - argIdx;
                  Object varargArray = Array.newInstance(varargItemType, varargArrayLen);

                  for(int varargIdx = 0; varargIdx < varargArrayLen; ++varargIdx) {
                     TemplateModel varargVal = (TemplateModel)((TemplateModel)(varargIdx == 0 ? argVal : it.next()));
                     Object unwrappedVarargVal = w.tryUnwrapTo(varargVal, varargItemType);
                     if (unwrappedVarargVal == ObjectWrapperAndUnwrapper.CANT_UNWRAP_TO_TARGET_CLASS) {
                        throw this.createArgumentTypeMismarchException(argIdx + varargIdx, varargVal, varargItemType);
                     }

                     if (unwrappedVarargVal == null && varargItemType.isPrimitive()) {
                        throw this.createNullToPrimitiveArgumentException(argIdx + varargIdx, varargItemType);
                     }

                     Array.set(varargArray, varargIdx, unwrappedVarargVal);
                  }

                  unwrappedArgs[argIdx++] = varargArray;
               }
            }
         }

         return unwrappedArgs;
      }
   }

   private TemplateModelException createArgumentTypeMismarchException(int argIdx, TemplateModel argVal, Class targetType) {
      _ErrorDescriptionBuilder desc = new _ErrorDescriptionBuilder(new Object[]{_MethodUtil.invocationErrorMessageStart(this.member), " couldn't be called: Can't convert the ", new _DelayedOrdinal(argIdx + 1), " argument's value to the target Java type, ", ClassUtil.getShortClassName(targetType), ". The type of the actual value was: ", new _DelayedFTLTypeDescription(argVal)});
      if (argVal instanceof TemplateMarkupOutputModel && targetType.isAssignableFrom(String.class)) {
         desc.tip("A markup output value can be converted to markup string like value?markup_string. But consider if the Java method whose argument it will be can handle markup strings properly.");
      }

      return new _TemplateModelException(desc);
   }

   private TemplateModelException createNullToPrimitiveArgumentException(int argIdx, Class targetType) {
      return new _TemplateModelException(new Object[]{_MethodUtil.invocationErrorMessageStart(this.member), " couldn't be called: The value of the ", new _DelayedOrdinal(argIdx + 1), " argument was null, but the target Java parameter type (", ClassUtil.getShortClassName(targetType), ") is primitive and so can't store null."});
   }

   protected Member getMember() {
      return this.member;
   }
}
