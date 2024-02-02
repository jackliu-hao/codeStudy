package org.apache.http.params;

import java.util.Set;

/** @deprecated */
@Deprecated
public abstract class AbstractHttpParams implements HttpParams, HttpParamsNames {
   protected AbstractHttpParams() {
   }

   public long getLongParameter(String name, long defaultValue) {
      Object param = this.getParameter(name);
      return param == null ? defaultValue : (Long)param;
   }

   public HttpParams setLongParameter(String name, long value) {
      this.setParameter(name, value);
      return this;
   }

   public int getIntParameter(String name, int defaultValue) {
      Object param = this.getParameter(name);
      return param == null ? defaultValue : (Integer)param;
   }

   public HttpParams setIntParameter(String name, int value) {
      this.setParameter(name, value);
      return this;
   }

   public double getDoubleParameter(String name, double defaultValue) {
      Object param = this.getParameter(name);
      return param == null ? defaultValue : (Double)param;
   }

   public HttpParams setDoubleParameter(String name, double value) {
      this.setParameter(name, value);
      return this;
   }

   public boolean getBooleanParameter(String name, boolean defaultValue) {
      Object param = this.getParameter(name);
      return param == null ? defaultValue : (Boolean)param;
   }

   public HttpParams setBooleanParameter(String name, boolean value) {
      this.setParameter(name, value ? Boolean.TRUE : Boolean.FALSE);
      return this;
   }

   public boolean isParameterTrue(String name) {
      return this.getBooleanParameter(name, false);
   }

   public boolean isParameterFalse(String name) {
      return !this.getBooleanParameter(name, false);
   }

   public Set<String> getNames() {
      throw new UnsupportedOperationException();
   }
}
