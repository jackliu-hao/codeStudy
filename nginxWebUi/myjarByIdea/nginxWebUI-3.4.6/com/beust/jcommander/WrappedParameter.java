package com.beust.jcommander;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class WrappedParameter {
   private Parameter m_parameter;
   private DynamicParameter m_dynamicParameter;

   public WrappedParameter(Parameter p) {
      this.m_parameter = p;
   }

   public WrappedParameter(DynamicParameter p) {
      this.m_dynamicParameter = p;
   }

   public Parameter getParameter() {
      return this.m_parameter;
   }

   public DynamicParameter getDynamicParameter() {
      return this.m_dynamicParameter;
   }

   public int arity() {
      return this.m_parameter != null ? this.m_parameter.arity() : 1;
   }

   public boolean hidden() {
      return this.m_parameter != null ? this.m_parameter.hidden() : this.m_dynamicParameter.hidden();
   }

   public boolean required() {
      return this.m_parameter != null ? this.m_parameter.required() : this.m_dynamicParameter.required();
   }

   public boolean password() {
      return this.m_parameter != null ? this.m_parameter.password() : false;
   }

   public String[] names() {
      return this.m_parameter != null ? this.m_parameter.names() : this.m_dynamicParameter.names();
   }

   public boolean variableArity() {
      return this.m_parameter != null ? this.m_parameter.variableArity() : false;
   }

   public Class<? extends IParameterValidator> validateWith() {
      return this.m_parameter != null ? this.m_parameter.validateWith() : this.m_dynamicParameter.validateWith();
   }

   public Class<? extends IValueValidator> validateValueWith() {
      return this.m_parameter != null ? this.m_parameter.validateValueWith() : this.m_dynamicParameter.validateValueWith();
   }

   public boolean echoInput() {
      return this.m_parameter != null ? this.m_parameter.echoInput() : false;
   }

   public void addValue(Parameterized parameterized, Object object, Object value) {
      if (this.m_parameter != null) {
         parameterized.set(object, value);
      } else {
         String a = this.m_dynamicParameter.assignment();
         String sv = value.toString();
         int aInd = sv.indexOf(a);
         if (aInd == -1) {
            throw new ParameterException("Dynamic parameter expected a value of the form a" + a + "b" + " but got:" + sv);
         }

         this.callPut(object, parameterized, sv.substring(0, aInd), sv.substring(aInd + 1));
      }

   }

   private void callPut(Object object, Parameterized parameterized, String key, String value) {
      try {
         Method m = this.findPut(parameterized.getType());
         m.invoke(parameterized.get(object), key, value);
      } catch (SecurityException var6) {
         var6.printStackTrace();
      } catch (IllegalAccessException var7) {
         var7.printStackTrace();
      } catch (InvocationTargetException var8) {
         var8.printStackTrace();
      } catch (NoSuchMethodException var9) {
         var9.printStackTrace();
      }

   }

   private Method findPut(Class<?> cls) throws SecurityException, NoSuchMethodException {
      return cls.getMethod("put", Object.class, Object.class);
   }

   public String getAssignment() {
      return this.m_dynamicParameter != null ? this.m_dynamicParameter.assignment() : "";
   }

   public boolean isHelp() {
      return this.m_parameter != null && this.m_parameter.help();
   }

   public boolean isNonOverwritableForced() {
      return this.m_parameter != null && this.m_parameter.forceNonOverwritable();
   }
}
