package org.noear.solon.core.wrap;

import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import org.noear.solon.Utils;
import org.noear.solon.annotation.Body;
import org.noear.solon.annotation.Header;
import org.noear.solon.annotation.Param;

public class ParamWrap {
   private final Parameter parameter;
   private String name;
   private String defaultValue;
   private boolean required;
   private boolean requireBody;
   private boolean requireHeader;
   private ParameterizedType genericType;

   public ParamWrap(Parameter parameter) {
      this.parameter = parameter;
      this.name = parameter.getName();
      Param paramAnno = (Param)parameter.getAnnotation(Param.class);
      Header headerAnno = (Header)parameter.getAnnotation(Header.class);
      Body bodyAnno = (Body)parameter.getAnnotation(Body.class);
      String name2;
      if (paramAnno != null) {
         name2 = Utils.annoAlias(paramAnno.value(), paramAnno.name());
         if (Utils.isNotEmpty(name2)) {
            this.name = name2;
         }

         if (!"\n\t\t\n\t\t\n\ue000\ue001\ue002\n\t\t\t\t\n".equals(paramAnno.defaultValue())) {
            this.defaultValue = paramAnno.defaultValue();
         }

         this.required = paramAnno.required();
      }

      if (headerAnno != null) {
         name2 = Utils.annoAlias(headerAnno.value(), headerAnno.name());
         if (Utils.isNotEmpty(name2)) {
            this.name = name2;
         }

         if (!"\n\t\t\n\t\t\n\ue000\ue001\ue002\n\t\t\t\t\n".equals(headerAnno.defaultValue())) {
            this.defaultValue = headerAnno.defaultValue();
         }

         this.required = headerAnno.required();
         this.requireHeader = true;
      }

      if (bodyAnno != null) {
         this.requireBody = true;
      }

      Type tmp = parameter.getParameterizedType();
      if (tmp instanceof ParameterizedType) {
         this.genericType = (ParameterizedType)tmp;
      } else {
         this.genericType = null;
      }

   }

   public Parameter getParameter() {
      return this.parameter;
   }

   public String getName() {
      return this.name;
   }

   public ParameterizedType getGenericType() {
      return this.genericType;
   }

   public Class<?> getType() {
      return this.parameter.getType();
   }

   public boolean required() {
      return this.required;
   }

   public boolean requireBody() {
      return this.requireBody;
   }

   public boolean requireHeader() {
      return this.requireHeader;
   }

   public String defaultValue() {
      return this.defaultValue;
   }
}
