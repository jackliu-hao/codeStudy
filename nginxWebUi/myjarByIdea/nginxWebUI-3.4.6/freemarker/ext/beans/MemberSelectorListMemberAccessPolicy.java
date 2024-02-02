package freemarker.ext.beans;

import freemarker.template.utility.ClassUtil;
import freemarker.template.utility.NullArgumentException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

public abstract class MemberSelectorListMemberAccessPolicy implements MemberAccessPolicy {
   private final ListType listType;
   private final MethodMatcher methodMatcher;
   private final ConstructorMatcher constructorMatcher;
   private final FieldMatcher fieldMatcher;
   private final Class<? extends Annotation> matchAnnotation;

   MemberSelectorListMemberAccessPolicy(Collection<? extends MemberSelector> memberSelectors, ListType listType, Class<? extends Annotation> matchAnnotation) {
      this.listType = listType;
      this.matchAnnotation = matchAnnotation;
      this.methodMatcher = new MethodMatcher();
      this.constructorMatcher = new ConstructorMatcher();
      this.fieldMatcher = new FieldMatcher();
      Iterator var4 = memberSelectors.iterator();

      while(var4.hasNext()) {
         MemberSelector memberSelector = (MemberSelector)var4.next();
         Class<?> upperBoundClass = memberSelector.upperBoundType;
         if (memberSelector.constructor != null) {
            this.constructorMatcher.addMatching(upperBoundClass, memberSelector.constructor);
         } else if (memberSelector.method != null) {
            this.methodMatcher.addMatching(upperBoundClass, memberSelector.method);
         } else {
            if (memberSelector.field == null) {
               throw new AssertionError();
            }

            this.fieldMatcher.addMatching(upperBoundClass, memberSelector.field);
         }
      }

   }

   public final ClassMemberAccessPolicy forClass(final Class<?> contextClass) {
      return new ClassMemberAccessPolicy() {
         public boolean isMethodExposed(Method method) {
            return MemberSelectorListMemberAccessPolicy.this.matchResultToIsExposedResult(MemberSelectorListMemberAccessPolicy.this.methodMatcher.matches(contextClass, method) || MemberSelectorListMemberAccessPolicy.this.matchAnnotation != null && _MethodUtil.getInheritableAnnotation(contextClass, method, MemberSelectorListMemberAccessPolicy.this.matchAnnotation) != null);
         }

         public boolean isConstructorExposed(Constructor<?> constructor) {
            return MemberSelectorListMemberAccessPolicy.this.matchResultToIsExposedResult(MemberSelectorListMemberAccessPolicy.this.constructorMatcher.matches(contextClass, constructor) || MemberSelectorListMemberAccessPolicy.this.matchAnnotation != null && _MethodUtil.getInheritableAnnotation(contextClass, constructor, MemberSelectorListMemberAccessPolicy.this.matchAnnotation) != null);
         }

         public boolean isFieldExposed(Field field) {
            return MemberSelectorListMemberAccessPolicy.this.matchResultToIsExposedResult(MemberSelectorListMemberAccessPolicy.this.fieldMatcher.matches(contextClass, field) || MemberSelectorListMemberAccessPolicy.this.matchAnnotation != null && _MethodUtil.getInheritableAnnotation(contextClass, field, MemberSelectorListMemberAccessPolicy.this.matchAnnotation) != null);
         }
      };
   }

   private boolean matchResultToIsExposedResult(boolean matches) {
      if (this.listType == MemberSelectorListMemberAccessPolicy.ListType.WHITELIST) {
         return matches;
      } else if (this.listType == MemberSelectorListMemberAccessPolicy.ListType.BLACKLIST) {
         return !matches;
      } else {
         throw new AssertionError();
      }
   }

   private static boolean isWellFormedClassName(String s) {
      if (s.length() == 0) {
         return false;
      } else {
         int identifierStartIdx = 0;

         for(int i = 0; i < s.length(); ++i) {
            char c = s.charAt(i);
            if (i == identifierStartIdx) {
               if (!Character.isJavaIdentifierStart(c)) {
                  return false;
               }
            } else if (c == '.' && i != s.length() - 1) {
               identifierStartIdx = i + 1;
            } else if (!Character.isJavaIdentifierPart(c)) {
               return false;
            }
         }

         return true;
      }
   }

   private static boolean isWellFormedJavaIdentifier(String s) {
      if (s.length() == 0) {
         return false;
      } else if (!Character.isJavaIdentifierStart(s.charAt(0))) {
         return false;
      } else {
         for(int i = 1; i < s.length(); ++i) {
            if (!Character.isJavaIdentifierPart(s.charAt(i))) {
               return false;
            }
         }

         return true;
      }
   }

   public static final class MemberSelector {
      private final Class<?> upperBoundType;
      private final Method method;
      private final Constructor<?> constructor;
      private final Field field;

      public MemberSelector(Class<?> upperBoundType, Method method) {
         NullArgumentException.check("upperBoundType", upperBoundType);
         NullArgumentException.check("method", method);
         this.upperBoundType = upperBoundType;
         this.method = method;
         this.constructor = null;
         this.field = null;
      }

      public MemberSelector(Class<?> upperBoundType, Constructor<?> constructor) {
         NullArgumentException.check("upperBoundType", upperBoundType);
         NullArgumentException.check("constructor", constructor);
         this.upperBoundType = upperBoundType;
         this.method = null;
         this.constructor = constructor;
         this.field = null;
      }

      public MemberSelector(Class<?> upperBoundType, Field field) {
         NullArgumentException.check("upperBoundType", upperBoundType);
         NullArgumentException.check("field", field);
         this.upperBoundType = upperBoundType;
         this.method = null;
         this.constructor = null;
         this.field = field;
      }

      public Class<?> getUpperBoundType() {
         return this.upperBoundType;
      }

      public Method getMethod() {
         return this.method;
      }

      public Constructor<?> getConstructor() {
         return this.constructor;
      }

      public Field getField() {
         return this.field;
      }

      public static MemberSelector parse(String memberSelectorString, ClassLoader classLoader) throws ClassNotFoundException, NoSuchMethodException, NoSuchFieldException {
         if (!memberSelectorString.contains("<") && !memberSelectorString.contains(">") && !memberSelectorString.contains("...") && !memberSelectorString.contains(";")) {
            String cleanedStr = memberSelectorString.trim().replaceAll("\\s*([\\.,\\(\\)\\[\\]])\\s*", "$1");
            int postClassDotIdx = cleanedStr.indexOf(40);
            boolean hasArgList = postClassDotIdx != -1;
            int postMemberNameIdx = hasArgList ? postClassDotIdx : cleanedStr.length();
            postClassDotIdx = cleanedStr.lastIndexOf(46, postMemberNameIdx);
            if (postClassDotIdx == -1) {
               throw new IllegalArgumentException("Malformed whitelist entry (missing dot): " + memberSelectorString);
            } else {
               String upperBoundClassStr = cleanedStr.substring(0, postClassDotIdx);
               if (!MemberSelectorListMemberAccessPolicy.isWellFormedClassName(upperBoundClassStr)) {
                  throw new IllegalArgumentException("Malformed whitelist entry (malformed upper bound class name): " + memberSelectorString);
               } else {
                  Class<?> upperBoundClass = classLoader.loadClass(upperBoundClassStr);
                  String memberName = cleanedStr.substring(postClassDotIdx + 1, postMemberNameIdx);
                  if (!MemberSelectorListMemberAccessPolicy.isWellFormedJavaIdentifier(memberName)) {
                     throw new IllegalArgumentException("Malformed whitelist entry (malformed member name): " + memberSelectorString);
                  } else if (!hasArgList) {
                     return new MemberSelector(upperBoundClass, upperBoundClass.getField(memberName));
                  } else if (cleanedStr.charAt(cleanedStr.length() - 1) != ')') {
                     throw new IllegalArgumentException("Malformed whitelist entry (should end with ')'): " + memberSelectorString);
                  } else {
                     String argsSpec = cleanedStr.substring(postMemberNameIdx + 1, cleanedStr.length() - 1);
                     StringTokenizer tok = new StringTokenizer(argsSpec, ",");
                     int argCount = tok.countTokens();
                     Class<?>[] argTypes = new Class[argCount];

                     for(int i = 0; i < argCount; ++i) {
                        String argClassName = tok.nextToken();

                        int arrayDimensions;
                        for(arrayDimensions = 0; argClassName.endsWith("[]"); argClassName = argClassName.substring(0, argClassName.length() - 2)) {
                           ++arrayDimensions;
                        }

                        Class<?> primArgClass = ClassUtil.resolveIfPrimitiveTypeName(argClassName);
                        Class argClass;
                        if (primArgClass != null) {
                           argClass = primArgClass;
                        } else {
                           if (!MemberSelectorListMemberAccessPolicy.isWellFormedClassName(argClassName)) {
                              throw new IllegalArgumentException("Malformed whitelist entry (malformed argument class name): " + memberSelectorString);
                           }

                           argClass = classLoader.loadClass(argClassName);
                        }

                        argTypes[i] = ClassUtil.getArrayClass(argClass, arrayDimensions);
                     }

                     return memberName.equals(upperBoundClass.getSimpleName()) ? new MemberSelector(upperBoundClass, upperBoundClass.getConstructor(argTypes)) : new MemberSelector(upperBoundClass, upperBoundClass.getMethod(memberName, argTypes));
                  }
               }
            }
         } else {
            throw new IllegalArgumentException("Malformed whitelist entry (shouldn't contain \"<\", \">\", \"...\", or \";\"): " + memberSelectorString);
         }
      }

      public static List<MemberSelector> parse(Collection<String> memberSelectors, boolean ignoreMissingClassOrMember, ClassLoader classLoader) throws ClassNotFoundException, NoSuchMethodException, NoSuchFieldException {
         List<MemberSelector> parsedMemberSelectors = new ArrayList(memberSelectors.size());
         Iterator var4 = memberSelectors.iterator();

         while(true) {
            String memberSelector;
            do {
               if (!var4.hasNext()) {
                  return parsedMemberSelectors;
               }

               memberSelector = (String)var4.next();
            } while(isIgnoredLine(memberSelector));

            try {
               parsedMemberSelectors.add(parse(memberSelector, classLoader));
            } catch (NoSuchFieldException | NoSuchMethodException | ClassNotFoundException var7) {
               if (!ignoreMissingClassOrMember) {
                  throw var7;
               }
            }
         }
      }

      public static boolean isIgnoredLine(String line) {
         String trimmedLine = line.trim();
         return trimmedLine.length() == 0 || trimmedLine.startsWith("#") || trimmedLine.startsWith("//");
      }
   }

   static enum ListType {
      WHITELIST,
      BLACKLIST;
   }
}
