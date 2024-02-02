package org.noear.solon.core.handle;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

public class ModelAndView implements Serializable {
   private String view;
   private Map<String, Object> model;

   public ModelAndView() {
      this.model = new LinkedHashMap();
   }

   public ModelAndView(String view) {
      this();
      this.view = view;
   }

   public ModelAndView(String view, Map<String, ?> model) {
      this(view);
      if (model != null) {
         this.model.putAll(model);
      }

   }

   public String view() {
      return this.view;
   }

   public ModelAndView view(String view) {
      this.view = view;
      return this;
   }

   public Map<String, Object> model() {
      return this.model;
   }

   public void put(String key, Object value) {
      this.model.put(key, value);
   }

   public void putIfAbsent(String key, Object value) {
      this.model.putIfAbsent(key, value);
   }

   public void putAll(Map<String, ?> keyValues) {
      this.model.putAll(keyValues);
   }

   public void clear() {
      this.model.clear();
      this.view = null;
   }

   public boolean isEmpty() {
      return this.view == null && this.model.size() == 0;
   }
}
