package freemarker.ext.servlet;

import freemarker.core.Environment;
import freemarker.core._DelayedFTLTypeDescription;
import freemarker.core._MiscTemplateException;
import freemarker.template.TemplateBooleanModel;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateDirectiveModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateScalarModel;
import freemarker.template.utility.DeepUnwrap;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

public class IncludePage implements TemplateDirectiveModel {
   private final HttpServletRequest request;
   private final HttpServletResponse response;

   public IncludePage(HttpServletRequest request, HttpServletResponse response) {
      this.request = request;
      this.response = response;
   }

   public void execute(Environment env, Map params, TemplateModel[] loopVars, TemplateDirectiveBody body) throws TemplateException, IOException {
      TemplateModel path = (TemplateModel)params.get("path");
      if (path == null) {
         throw new _MiscTemplateException(env, "Missing required parameter \"path\"");
      } else if (!(path instanceof TemplateScalarModel)) {
         throw new _MiscTemplateException(env, new Object[]{"Expected a scalar model. \"path\" is instead ", new _DelayedFTLTypeDescription(path)});
      } else {
         String strPath = ((TemplateScalarModel)path).getAsString();
         if (strPath == null) {
            throw new _MiscTemplateException(env, "String value of \"path\" parameter is null");
         } else {
            Writer envOut = env.getOut();
            Object wrappedResponse;
            if (envOut == this.response.getWriter()) {
               wrappedResponse = this.response;
            } else {
               final PrintWriter printWriter = envOut instanceof PrintWriter ? (PrintWriter)envOut : new PrintWriter(envOut);
               wrappedResponse = new HttpServletResponseWrapper(this.response) {
                  public PrintWriter getWriter() {
                     return printWriter;
                  }
               };
            }

            TemplateModel inheritParamsModel = (TemplateModel)params.get("inherit_params");
            boolean inheritParams;
            if (inheritParamsModel == null) {
               inheritParams = true;
            } else {
               if (!(inheritParamsModel instanceof TemplateBooleanModel)) {
                  throw new _MiscTemplateException(env, new Object[]{"\"inherit_params\" should be a boolean but it's a(n) ", inheritParamsModel.getClass().getName(), " instead"});
               }

               inheritParams = ((TemplateBooleanModel)inheritParamsModel).getAsBoolean();
            }

            TemplateModel paramsModel = (TemplateModel)params.get("params");
            Object wrappedRequest;
            if (paramsModel == null && inheritParams) {
               wrappedRequest = this.request;
            } else {
               Map paramsMap;
               if (paramsModel != null) {
                  Object unwrapped = DeepUnwrap.unwrap(paramsModel);
                  if (!(unwrapped instanceof Map)) {
                     throw new _MiscTemplateException(env, new Object[]{"Expected \"params\" to unwrap into a java.util.Map. It unwrapped into ", unwrapped.getClass().getName(), " instead."});
                  }

                  paramsMap = (Map)unwrapped;
               } else {
                  paramsMap = Collections.EMPTY_MAP;
               }

               wrappedRequest = new CustomParamsRequest(this.request, paramsMap, inheritParams);
            }

            try {
               this.request.getRequestDispatcher(strPath).include((ServletRequest)wrappedRequest, (ServletResponse)wrappedResponse);
            } catch (ServletException var15) {
               throw new _MiscTemplateException(var15, env);
            }
         }
      }
   }

   private static final class CustomParamsRequest extends HttpServletRequestWrapper {
      private final HashMap paramsMap;

      private CustomParamsRequest(HttpServletRequest request, Map paramMap, boolean inheritParams) {
         super(request);
         this.paramsMap = inheritParams ? new HashMap(request.getParameterMap()) : new HashMap();
         Iterator it = paramMap.entrySet().iterator();

         while(it.hasNext()) {
            Map.Entry entry = (Map.Entry)it.next();
            String name = String.valueOf(entry.getKey());
            Object value = entry.getValue();
            String[] valueArray;
            int i;
            if (value == null) {
               valueArray = new String[]{null};
            } else if (value instanceof String[]) {
               valueArray = (String[])((String[])value);
            } else if (value instanceof Collection) {
               Collection col = (Collection)value;
               valueArray = new String[col.size()];
               i = 0;

               for(Iterator it2 = col.iterator(); it2.hasNext(); valueArray[i++] = String.valueOf(it2.next())) {
               }
            } else if (value.getClass().isArray()) {
               int len = Array.getLength(value);
               valueArray = new String[len];

               for(i = 0; i < len; ++i) {
                  valueArray[i] = String.valueOf(Array.get(value, i));
               }
            } else {
               valueArray = new String[]{String.valueOf(value)};
            }

            String[] existingParams = (String[])((String[])this.paramsMap.get(name));
            i = existingParams == null ? 0 : existingParams.length;
            if (i == 0) {
               this.paramsMap.put(name, valueArray);
            } else {
               int vl = valueArray.length;
               if (vl > 0) {
                  String[] newValueArray = new String[i + vl];
                  System.arraycopy(valueArray, 0, newValueArray, 0, vl);
                  System.arraycopy(existingParams, 0, newValueArray, vl, i);
                  this.paramsMap.put(name, newValueArray);
               }
            }
         }

      }

      public String[] getParameterValues(String name) {
         String[] value = (String[])((String[])this.paramsMap.get(name));
         return value != null ? (String[])value.clone() : null;
      }

      public String getParameter(String name) {
         String[] values = (String[])((String[])this.paramsMap.get(name));
         return values != null && values.length > 0 ? values[0] : null;
      }

      public Enumeration getParameterNames() {
         return Collections.enumeration(this.paramsMap.keySet());
      }

      public Map getParameterMap() {
         HashMap clone = (HashMap)this.paramsMap.clone();
         Iterator it = clone.entrySet().iterator();

         while(it.hasNext()) {
            Map.Entry entry = (Map.Entry)it.next();
            entry.setValue(((String[])((String[])entry.getValue())).clone());
         }

         return Collections.unmodifiableMap(clone);
      }

      // $FF: synthetic method
      CustomParamsRequest(HttpServletRequest x0, Map x1, boolean x2, Object x3) {
         this(x0, x1, x2);
      }
   }
}
