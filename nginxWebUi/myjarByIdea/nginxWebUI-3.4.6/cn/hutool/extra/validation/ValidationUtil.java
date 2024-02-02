package cn.hutool.extra.validation;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.util.Iterator;
import java.util.Set;

public class ValidationUtil {
   private static final Validator validator;

   public static Validator getValidator() {
      return validator;
   }

   public static <T> Set<ConstraintViolation<T>> validate(T bean, Class<?>... groups) {
      return validator.validate(bean, groups);
   }

   public static <T> Set<ConstraintViolation<T>> validateProperty(T bean, String propertyName, Class<?>... groups) {
      return validator.validateProperty(bean, propertyName, groups);
   }

   public static <T> BeanValidationResult warpValidate(T bean, Class<?>... groups) {
      return warpBeanValidationResult(validate(bean, groups));
   }

   public static <T> BeanValidationResult warpValidateProperty(T bean, String propertyName, Class<?>... groups) {
      return warpBeanValidationResult(validateProperty(bean, propertyName, groups));
   }

   private static <T> BeanValidationResult warpBeanValidationResult(Set<ConstraintViolation<T>> constraintViolations) {
      BeanValidationResult result = new BeanValidationResult(constraintViolations.isEmpty());
      Iterator var2 = constraintViolations.iterator();

      while(var2.hasNext()) {
         ConstraintViolation<T> constraintViolation = (ConstraintViolation)var2.next();
         BeanValidationResult.ErrorMessage errorMessage = new BeanValidationResult.ErrorMessage();
         errorMessage.setPropertyName(constraintViolation.getPropertyPath().toString());
         errorMessage.setMessage(constraintViolation.getMessage());
         errorMessage.setValue(constraintViolation.getInvalidValue());
         result.addErrorMessage(errorMessage);
      }

      return result;
   }

   static {
      ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
      Throwable var1 = null;

      try {
         validator = factory.getValidator();
      } catch (Throwable var10) {
         var1 = var10;
         throw var10;
      } finally {
         if (factory != null) {
            if (var1 != null) {
               try {
                  factory.close();
               } catch (Throwable var9) {
                  var1.addSuppressed(var9);
               }
            } else {
               factory.close();
            }
         }

      }

   }
}
