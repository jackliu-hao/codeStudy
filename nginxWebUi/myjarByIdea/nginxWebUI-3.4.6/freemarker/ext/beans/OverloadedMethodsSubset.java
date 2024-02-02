package freemarker.ext.beans;

import freemarker.template.TemplateModelException;
import freemarker.template.utility.ClassUtil;
import freemarker.template.utility.NullArgumentException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

abstract class OverloadedMethodsSubset {
   static final int[] ALL_ZEROS_ARRAY = new int[0];
   private static final int[][] ZERO_PARAM_COUNT_TYPE_FLAGS_ARRAY = new int[1][];
   private Class[][] unwrappingHintsByParamCount;
   private int[][] typeFlagsByParamCount;
   private final Map argTypesToMemberDescCache = new ConcurrentHashMap(6, 0.75F, 1);
   private final List memberDescs = new LinkedList();
   protected final boolean bugfixed;

   OverloadedMethodsSubset(boolean bugfixed) {
      this.bugfixed = bugfixed;
   }

   void addCallableMemberDescriptor(ReflectionCallableMemberDescriptor memberDesc) {
      this.memberDescs.add(memberDesc);
      Class[] prepedParamTypes = this.preprocessParameterTypes(memberDesc);
      int paramCount = prepedParamTypes.length;
      int paramIdx;
      if (this.unwrappingHintsByParamCount == null) {
         this.unwrappingHintsByParamCount = new Class[paramCount + 1][];
         this.unwrappingHintsByParamCount[paramCount] = (Class[])prepedParamTypes.clone();
      } else if (this.unwrappingHintsByParamCount.length <= paramCount) {
         Class[][] newUnwrappingHintsByParamCount = new Class[paramCount + 1][];
         System.arraycopy(this.unwrappingHintsByParamCount, 0, newUnwrappingHintsByParamCount, 0, this.unwrappingHintsByParamCount.length);
         this.unwrappingHintsByParamCount = newUnwrappingHintsByParamCount;
         this.unwrappingHintsByParamCount[paramCount] = (Class[])prepedParamTypes.clone();
      } else {
         Class[] unwrappingHints = this.unwrappingHintsByParamCount[paramCount];
         if (unwrappingHints == null) {
            this.unwrappingHintsByParamCount[paramCount] = (Class[])prepedParamTypes.clone();
         } else {
            for(paramIdx = 0; paramIdx < prepedParamTypes.length; ++paramIdx) {
               unwrappingHints[paramIdx] = this.getCommonSupertypeForUnwrappingHint(unwrappingHints[paramIdx], prepedParamTypes[paramIdx]);
            }
         }
      }

      int[] typeFlagsByParamIdx = ALL_ZEROS_ARRAY;
      if (this.bugfixed) {
         for(paramIdx = 0; paramIdx < paramCount; ++paramIdx) {
            int typeFlags = TypeFlags.classToTypeFlags(prepedParamTypes[paramIdx]);
            if (typeFlags != 0) {
               if (typeFlagsByParamIdx == ALL_ZEROS_ARRAY) {
                  typeFlagsByParamIdx = new int[paramCount];
               }

               typeFlagsByParamIdx[paramIdx] = typeFlags;
            }
         }

         this.mergeInTypesFlags(paramCount, typeFlagsByParamIdx);
      }

      this.afterWideningUnwrappingHints(this.bugfixed ? prepedParamTypes : this.unwrappingHintsByParamCount[paramCount], typeFlagsByParamIdx);
   }

   Class[][] getUnwrappingHintsByParamCount() {
      return this.unwrappingHintsByParamCount;
   }

   final MaybeEmptyCallableMemberDescriptor getMemberDescriptorForArgs(Object[] args, boolean varArg) {
      ArgumentTypes argTypes = new ArgumentTypes(args, this.bugfixed);
      MaybeEmptyCallableMemberDescriptor memberDesc = (MaybeEmptyCallableMemberDescriptor)this.argTypesToMemberDescCache.get(argTypes);
      if (memberDesc == null) {
         synchronized(this.argTypesToMemberDescCache) {
            memberDesc = (MaybeEmptyCallableMemberDescriptor)this.argTypesToMemberDescCache.get(argTypes);
            if (memberDesc == null) {
               memberDesc = argTypes.getMostSpecific(this.memberDescs, varArg);
               this.argTypesToMemberDescCache.put(argTypes, memberDesc);
            }
         }
      }

      return memberDesc;
   }

   Iterator getMemberDescriptors() {
      return this.memberDescs.iterator();
   }

   abstract Class[] preprocessParameterTypes(CallableMemberDescriptor var1);

   abstract void afterWideningUnwrappingHints(Class[] var1, int[] var2);

   abstract MaybeEmptyMemberAndArguments getMemberAndArguments(List var1, BeansWrapper var2) throws TemplateModelException;

   protected Class getCommonSupertypeForUnwrappingHint(Class c1, Class c2) {
      if (c1 == c2) {
         return c1;
      } else {
         if (this.bugfixed) {
            boolean c1WasPrim;
            if (c1.isPrimitive()) {
               c1 = ClassUtil.primitiveClassToBoxingClass(c1);
               c1WasPrim = true;
            } else {
               c1WasPrim = false;
            }

            boolean c2WasPrim;
            if (c2.isPrimitive()) {
               c2 = ClassUtil.primitiveClassToBoxingClass(c2);
               c2WasPrim = true;
            } else {
               c2WasPrim = false;
            }

            if (c1 == c2) {
               return c1;
            }

            if (Number.class.isAssignableFrom(c1) && Number.class.isAssignableFrom(c2)) {
               return Number.class;
            }

            if (c1WasPrim || c2WasPrim) {
               return Object.class;
            }
         } else if (c2.isPrimitive()) {
            if (c2 == Byte.TYPE) {
               c2 = Byte.class;
            } else if (c2 == Short.TYPE) {
               c2 = Short.class;
            } else if (c2 == Character.TYPE) {
               c2 = Character.class;
            } else if (c2 == Integer.TYPE) {
               c2 = Integer.class;
            } else if (c2 == Float.TYPE) {
               c2 = Float.class;
            } else if (c2 == Long.TYPE) {
               c2 = Long.class;
            } else if (c2 == Double.TYPE) {
               c2 = Double.class;
            }
         }

         Set commonTypes = _MethodUtil.getAssignables(c1, c2);
         commonTypes.retainAll(_MethodUtil.getAssignables(c2, c1));
         if (commonTypes.isEmpty()) {
            return Object.class;
         } else {
            List max = new ArrayList();
            Iterator it = commonTypes.iterator();

            while(true) {
               Class maxCl;
               label106:
               while(it.hasNext()) {
                  maxCl = (Class)it.next();
                  Iterator maxIter = max.iterator();

                  while(maxIter.hasNext()) {
                     Class maxClazz = (Class)maxIter.next();
                     if (_MethodUtil.isMoreOrSameSpecificParameterType(maxClazz, maxCl, false, 0) != 0) {
                        continue label106;
                     }

                     if (_MethodUtil.isMoreOrSameSpecificParameterType(maxCl, maxClazz, false, 0) != 0) {
                        maxIter.remove();
                     }
                  }

                  max.add(maxCl);
               }

               if (max.size() > 1) {
                  if (!this.bugfixed) {
                     return Object.class;
                  }

                  it = max.iterator();

                  while(it.hasNext()) {
                     maxCl = (Class)it.next();
                     if (!maxCl.isInterface()) {
                        if (maxCl != Object.class) {
                           return maxCl;
                        }

                        it.remove();
                     }
                  }

                  max.remove(Cloneable.class);
                  if (max.size() > 1) {
                     max.remove(Serializable.class);
                     if (max.size() > 1) {
                        max.remove(Comparable.class);
                        if (max.size() > 1) {
                           return Object.class;
                        }
                     }
                  }
               }

               return (Class)max.get(0);
            }
         }
      }
   }

   protected final int[] getTypeFlags(int paramCount) {
      return this.typeFlagsByParamCount != null && this.typeFlagsByParamCount.length > paramCount ? this.typeFlagsByParamCount[paramCount] : null;
   }

   protected final void mergeInTypesFlags(int dstParamCount, int[] srcTypeFlagsByParamIdx) {
      NullArgumentException.check("srcTypesFlagsByParamIdx", srcTypeFlagsByParamIdx);
      if (dstParamCount == 0) {
         if (this.typeFlagsByParamCount == null) {
            this.typeFlagsByParamCount = ZERO_PARAM_COUNT_TYPE_FLAGS_ARRAY;
         } else if (this.typeFlagsByParamCount != ZERO_PARAM_COUNT_TYPE_FLAGS_ARRAY) {
            this.typeFlagsByParamCount[0] = ALL_ZEROS_ARRAY;
         }

      } else {
         if (this.typeFlagsByParamCount == null) {
            this.typeFlagsByParamCount = new int[dstParamCount + 1][];
         } else if (this.typeFlagsByParamCount.length <= dstParamCount) {
            int[][] newTypeFlagsByParamCount = new int[dstParamCount + 1][];
            System.arraycopy(this.typeFlagsByParamCount, 0, newTypeFlagsByParamCount, 0, this.typeFlagsByParamCount.length);
            this.typeFlagsByParamCount = newTypeFlagsByParamCount;
         }

         int[] dstTypeFlagsByParamIdx = this.typeFlagsByParamCount[dstParamCount];
         int paramIdx;
         int srcParamTypeFlags;
         if (dstTypeFlagsByParamIdx == null) {
            if (srcTypeFlagsByParamIdx != ALL_ZEROS_ARRAY) {
               paramIdx = srcTypeFlagsByParamIdx.length;
               dstTypeFlagsByParamIdx = new int[dstParamCount];

               for(srcParamTypeFlags = 0; srcParamTypeFlags < dstParamCount; ++srcParamTypeFlags) {
                  dstTypeFlagsByParamIdx[srcParamTypeFlags] = srcTypeFlagsByParamIdx[srcParamTypeFlags < paramIdx ? srcParamTypeFlags : paramIdx - 1];
               }
            } else {
               dstTypeFlagsByParamIdx = ALL_ZEROS_ARRAY;
            }

            this.typeFlagsByParamCount[dstParamCount] = dstTypeFlagsByParamIdx;
         } else {
            if (srcTypeFlagsByParamIdx == dstTypeFlagsByParamIdx) {
               return;
            }

            if (dstTypeFlagsByParamIdx == ALL_ZEROS_ARRAY && dstParamCount > 0) {
               dstTypeFlagsByParamIdx = new int[dstParamCount];
               this.typeFlagsByParamCount[dstParamCount] = dstTypeFlagsByParamIdx;
            }

            for(paramIdx = 0; paramIdx < dstParamCount; ++paramIdx) {
               int dstParamTypesFlags;
               if (srcTypeFlagsByParamIdx != ALL_ZEROS_ARRAY) {
                  dstParamTypesFlags = srcTypeFlagsByParamIdx.length;
                  srcParamTypeFlags = srcTypeFlagsByParamIdx[paramIdx < dstParamTypesFlags ? paramIdx : dstParamTypesFlags - 1];
               } else {
                  srcParamTypeFlags = 0;
               }

               dstParamTypesFlags = dstTypeFlagsByParamIdx[paramIdx];
               if (dstParamTypesFlags != srcParamTypeFlags) {
                  int mergedTypeFlags = dstParamTypesFlags | srcParamTypeFlags;
                  if ((mergedTypeFlags & 2044) != 0) {
                     mergedTypeFlags |= 1;
                  }

                  dstTypeFlagsByParamIdx[paramIdx] = mergedTypeFlags;
               }
            }
         }

      }
   }

   protected void forceNumberArgumentsToParameterTypes(Object[] args, Class[] paramTypes, int[] typeFlagsByParamIndex) {
      int paramTypesLen = paramTypes.length;
      int argsLen = args.length;

      for(int argIdx = 0; argIdx < argsLen; ++argIdx) {
         int paramTypeIdx = argIdx < paramTypesLen ? argIdx : paramTypesLen - 1;
         int typeFlags = typeFlagsByParamIndex[paramTypeIdx];
         if ((typeFlags & 1) != 0) {
            Object arg = args[argIdx];
            if (arg instanceof Number) {
               Class targetType = paramTypes[paramTypeIdx];
               Number convertedArg = BeansWrapper.forceUnwrappedNumberToType((Number)arg, targetType, this.bugfixed);
               if (convertedArg != null) {
                  args[argIdx] = convertedArg;
               }
            }
         }
      }

   }

   static {
      ZERO_PARAM_COUNT_TYPE_FLAGS_ARRAY[0] = ALL_ZEROS_ARRAY;
   }
}
