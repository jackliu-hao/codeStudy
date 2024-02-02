package cn.hutool.core.exceptions;

import cn.hutool.core.lang.func.Func;
import cn.hutool.core.lang.func.Func0;
import cn.hutool.core.lang.func.Func1;
import cn.hutool.core.lang.func.Supplier1;
import cn.hutool.core.lang.func.VoidFunc;
import cn.hutool.core.lang.func.VoidFunc0;
import cn.hutool.core.lang.func.VoidFunc1;
import java.lang.invoke.SerializedLambda;
import java.util.Objects;

public class CheckedUtil {
   public static <P, R> FuncRt<P, R> uncheck(Func<P, R> expression) {
      return uncheck(expression, RuntimeException::new);
   }

   public static <R> Func0Rt<R> uncheck(Func0<R> expression) {
      return uncheck(expression, RuntimeException::new);
   }

   public static <P, R> Func1Rt<P, R> uncheck(Func1<P, R> expression) {
      return uncheck(expression, RuntimeException::new);
   }

   public static <P> VoidFuncRt<P> uncheck(VoidFunc<P> expression) {
      return uncheck(expression, RuntimeException::new);
   }

   public static VoidFunc0Rt uncheck(VoidFunc0 expression) {
      return uncheck(expression, RuntimeException::new);
   }

   public static <P> VoidFunc1Rt<P> uncheck(VoidFunc1<P> expression) {
      return uncheck(expression, RuntimeException::new);
   }

   public static <P, R> FuncRt<P, R> uncheck(Func<P, R> expression, Supplier1<RuntimeException, Exception> rteSupplier) {
      Objects.requireNonNull(expression, "expression can not be null");
      return (t) -> {
         try {
            return expression.call(t);
         } catch (Exception var4) {
            if (rteSupplier == null) {
               throw new RuntimeException(var4);
            } else {
               throw (RuntimeException)rteSupplier.get(var4);
            }
         }
      };
   }

   public static <R> Func0Rt<R> uncheck(Func0<R> expression, Supplier1<RuntimeException, Exception> rteSupplier) {
      Objects.requireNonNull(expression, "expression can not be null");
      return () -> {
         try {
            return expression.call();
         } catch (Exception var3) {
            if (rteSupplier == null) {
               throw new RuntimeException(var3);
            } else {
               throw (RuntimeException)rteSupplier.get(var3);
            }
         }
      };
   }

   public static <P, R> Func1Rt<P, R> uncheck(Func1<P, R> expression, Supplier1<RuntimeException, Exception> rteSupplier) {
      Objects.requireNonNull(expression, "expression can not be null");
      return (t) -> {
         try {
            return expression.call(t);
         } catch (Exception var4) {
            if (rteSupplier == null) {
               throw new RuntimeException(var4);
            } else {
               throw (RuntimeException)rteSupplier.get(var4);
            }
         }
      };
   }

   public static <P> VoidFuncRt<P> uncheck(VoidFunc<P> expression, Supplier1<RuntimeException, Exception> rteSupplier) {
      Objects.requireNonNull(expression, "expression can not be null");
      return (t) -> {
         try {
            expression.call(t);
         } catch (Exception var4) {
            if (rteSupplier == null) {
               throw new RuntimeException(var4);
            } else {
               throw (RuntimeException)rteSupplier.get(var4);
            }
         }
      };
   }

   public static VoidFunc0Rt uncheck(VoidFunc0 expression, RuntimeException rte) {
      Objects.requireNonNull(expression, "expression can not be null");
      return () -> {
         try {
            expression.call();
         } catch (Exception var3) {
            if (rte == null) {
               throw new RuntimeException(var3);
            } else {
               rte.initCause(var3);
               throw rte;
            }
         }
      };
   }

   public static VoidFunc0Rt uncheck(VoidFunc0 expression, Supplier1<RuntimeException, Exception> rteSupplier) {
      Objects.requireNonNull(expression, "expression can not be null");
      return () -> {
         try {
            expression.call();
         } catch (Exception var3) {
            if (rteSupplier == null) {
               throw new RuntimeException(var3);
            } else {
               throw (RuntimeException)rteSupplier.get(var3);
            }
         }
      };
   }

   public static <P> VoidFunc1Rt<P> uncheck(VoidFunc1<P> expression, Supplier1<RuntimeException, Exception> rteSupplier) {
      Objects.requireNonNull(expression, "expression can not be null");
      return (t) -> {
         try {
            expression.call(t);
         } catch (Exception var4) {
            if (rteSupplier == null) {
               throw new RuntimeException(var4);
            } else {
               throw (RuntimeException)rteSupplier.get(var4);
            }
         }
      };
   }

   // $FF: synthetic method
   private static Object $deserializeLambda$(SerializedLambda lambda) {
      switch (lambda.getImplMethodName()) {
         case "lambda$uncheck$5732f3b9$1":
            if (lambda.getImplMethodKind() == 6 && lambda.getFunctionalInterfaceClass().equals("cn/hutool/core/exceptions/CheckedUtil$Func1Rt") && lambda.getFunctionalInterfaceMethodName().equals("call") && lambda.getFunctionalInterfaceMethodSignature().equals("(Ljava/lang/Object;)Ljava/lang/Object;") && lambda.getImplClass().equals("cn/hutool/core/exceptions/CheckedUtil") && lambda.getImplMethodSignature().equals("(Lcn/hutool/core/lang/func/Func1;Lcn/hutool/core/lang/func/Supplier1;Ljava/lang/Object;)Ljava/lang/Object;")) {
               return (t) -> {
                  try {
                     return expression.call(t);
                  } catch (Exception var4) {
                     if (rteSupplier == null) {
                        throw new RuntimeException(var4);
                     } else {
                        throw (RuntimeException)rteSupplier.get(var4);
                     }
                  }
               };
            }
            break;
         case "lambda$uncheck$e9066ec4$1":
            if (lambda.getImplMethodKind() == 6 && lambda.getFunctionalInterfaceClass().equals("cn/hutool/core/exceptions/CheckedUtil$Func0Rt") && lambda.getFunctionalInterfaceMethodName().equals("call") && lambda.getFunctionalInterfaceMethodSignature().equals("()Ljava/lang/Object;") && lambda.getImplClass().equals("cn/hutool/core/exceptions/CheckedUtil") && lambda.getImplMethodSignature().equals("(Lcn/hutool/core/lang/func/Func0;Lcn/hutool/core/lang/func/Supplier1;)Ljava/lang/Object;")) {
               return () -> {
                  try {
                     return expression.call();
                  } catch (Exception var3) {
                     if (rteSupplier == null) {
                        throw new RuntimeException(var3);
                     } else {
                        throw (RuntimeException)rteSupplier.get(var3);
                     }
                  }
               };
            }
            break;
         case "lambda$uncheck$2300d7df$1":
            if (lambda.getImplMethodKind() == 6 && lambda.getFunctionalInterfaceClass().equals("cn/hutool/core/exceptions/CheckedUtil$VoidFunc0Rt") && lambda.getFunctionalInterfaceMethodName().equals("call") && lambda.getFunctionalInterfaceMethodSignature().equals("()V") && lambda.getImplClass().equals("cn/hutool/core/exceptions/CheckedUtil") && lambda.getImplMethodSignature().equals("(Lcn/hutool/core/lang/func/VoidFunc0;Lcn/hutool/core/lang/func/Supplier1;)V")) {
               return () -> {
                  try {
                     expression.call();
                  } catch (Exception var3) {
                     if (rteSupplier == null) {
                        throw new RuntimeException(var3);
                     } else {
                        throw (RuntimeException)rteSupplier.get(var3);
                     }
                  }
               };
            }
            break;
         case "lambda$uncheck$ad313ebc$1":
            if (lambda.getImplMethodKind() == 6 && lambda.getFunctionalInterfaceClass().equals("cn/hutool/core/exceptions/CheckedUtil$VoidFunc1Rt") && lambda.getFunctionalInterfaceMethodName().equals("call") && lambda.getFunctionalInterfaceMethodSignature().equals("(Ljava/lang/Object;)V") && lambda.getImplClass().equals("cn/hutool/core/exceptions/CheckedUtil") && lambda.getImplMethodSignature().equals("(Lcn/hutool/core/lang/func/VoidFunc1;Lcn/hutool/core/lang/func/Supplier1;Ljava/lang/Object;)V")) {
               return (t) -> {
                  try {
                     expression.call(t);
                  } catch (Exception var4) {
                     if (rteSupplier == null) {
                        throw new RuntimeException(var4);
                     } else {
                        throw (RuntimeException)rteSupplier.get(var4);
                     }
                  }
               };
            }
            break;
         case "lambda$uncheck$6c25eb8b$1":
            if (lambda.getImplMethodKind() == 6 && lambda.getFunctionalInterfaceClass().equals("cn/hutool/core/exceptions/CheckedUtil$FuncRt") && lambda.getFunctionalInterfaceMethodName().equals("call") && lambda.getFunctionalInterfaceMethodSignature().equals("([Ljava/lang/Object;)Ljava/lang/Object;") && lambda.getImplClass().equals("cn/hutool/core/exceptions/CheckedUtil") && lambda.getImplMethodSignature().equals("(Lcn/hutool/core/lang/func/Func;Lcn/hutool/core/lang/func/Supplier1;[Ljava/lang/Object;)Ljava/lang/Object;")) {
               return (t) -> {
                  try {
                     return expression.call(t);
                  } catch (Exception var4) {
                     if (rteSupplier == null) {
                        throw new RuntimeException(var4);
                     } else {
                        throw (RuntimeException)rteSupplier.get(var4);
                     }
                  }
               };
            }
            break;
         case "lambda$uncheck$5b7ace8e$1":
            if (lambda.getImplMethodKind() == 6 && lambda.getFunctionalInterfaceClass().equals("cn/hutool/core/exceptions/CheckedUtil$VoidFuncRt") && lambda.getFunctionalInterfaceMethodName().equals("call") && lambda.getFunctionalInterfaceMethodSignature().equals("([Ljava/lang/Object;)V") && lambda.getImplClass().equals("cn/hutool/core/exceptions/CheckedUtil") && lambda.getImplMethodSignature().equals("(Lcn/hutool/core/lang/func/VoidFunc;Lcn/hutool/core/lang/func/Supplier1;[Ljava/lang/Object;)V")) {
               return (t) -> {
                  try {
                     expression.call(t);
                  } catch (Exception var4) {
                     if (rteSupplier == null) {
                        throw new RuntimeException(var4);
                     } else {
                        throw (RuntimeException)rteSupplier.get(var4);
                     }
                  }
               };
            }
            break;
         case "lambda$uncheck$a3c5d001$1":
            if (lambda.getImplMethodKind() == 6 && lambda.getFunctionalInterfaceClass().equals("cn/hutool/core/exceptions/CheckedUtil$VoidFunc0Rt") && lambda.getFunctionalInterfaceMethodName().equals("call") && lambda.getFunctionalInterfaceMethodSignature().equals("()V") && lambda.getImplClass().equals("cn/hutool/core/exceptions/CheckedUtil") && lambda.getImplMethodSignature().equals("(Lcn/hutool/core/lang/func/VoidFunc0;Ljava/lang/RuntimeException;)V")) {
               return () -> {
                  try {
                     expression.call();
                  } catch (Exception var3) {
                     if (rte == null) {
                        throw new RuntimeException(var3);
                     } else {
                        rte.initCause(var3);
                        throw rte;
                     }
                  }
               };
            }
      }

      throw new IllegalArgumentException("Invalid lambda deserialization");
   }

   public interface VoidFunc1Rt<P> extends VoidFunc1<P> {
      void call(P var1) throws RuntimeException;
   }

   public interface VoidFunc0Rt extends VoidFunc0 {
      void call() throws RuntimeException;
   }

   public interface VoidFuncRt<P> extends VoidFunc<P> {
      void call(P... var1) throws RuntimeException;
   }

   public interface Func1Rt<P, R> extends Func1<P, R> {
      R call(P var1) throws RuntimeException;
   }

   public interface Func0Rt<R> extends Func0<R> {
      R call() throws RuntimeException;
   }

   public interface FuncRt<P, R> extends Func<P, R> {
      R call(P... var1) throws RuntimeException;
   }
}
