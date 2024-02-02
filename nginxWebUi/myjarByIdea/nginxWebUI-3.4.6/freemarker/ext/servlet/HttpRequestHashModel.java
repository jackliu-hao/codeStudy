package freemarker.ext.servlet;

import freemarker.template.ObjectWrapper;
import freemarker.template.SimpleCollection;
import freemarker.template.TemplateCollectionModel;
import freemarker.template.TemplateHashModelEx;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import java.util.ArrayList;
import java.util.Enumeration;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public final class HttpRequestHashModel implements TemplateHashModelEx {
   private final HttpServletRequest request;
   private final HttpServletResponse response;
   private final ObjectWrapper wrapper;

   public HttpRequestHashModel(HttpServletRequest request, ObjectWrapper wrapper) {
      this(request, (HttpServletResponse)null, wrapper);
   }

   public HttpRequestHashModel(HttpServletRequest request, HttpServletResponse response, ObjectWrapper wrapper) {
      this.request = request;
      this.response = response;
      this.wrapper = wrapper;
   }

   public TemplateModel get(String key) throws TemplateModelException {
      return this.wrapper.wrap(this.request.getAttribute(key));
   }

   public boolean isEmpty() {
      return !this.request.getAttributeNames().hasMoreElements();
   }

   public int size() {
      int result = 0;

      for(Enumeration enumeration = this.request.getAttributeNames(); enumeration.hasMoreElements(); ++result) {
         enumeration.nextElement();
      }

      return result;
   }

   public TemplateCollectionModel keys() {
      ArrayList keys = new ArrayList();
      Enumeration enumeration = this.request.getAttributeNames();

      while(enumeration.hasMoreElements()) {
         keys.add(enumeration.nextElement());
      }

      return new SimpleCollection(keys.iterator());
   }

   public TemplateCollectionModel values() {
      ArrayList values = new ArrayList();
      Enumeration enumeration = this.request.getAttributeNames();

      while(enumeration.hasMoreElements()) {
         values.add(this.request.getAttribute((String)enumeration.nextElement()));
      }

      return new SimpleCollection(values.iterator(), this.wrapper);
   }

   public HttpServletRequest getRequest() {
      return this.request;
   }

   public HttpServletResponse getResponse() {
      return this.response;
   }

   public ObjectWrapper getObjectWrapper() {
      return this.wrapper;
   }
}
