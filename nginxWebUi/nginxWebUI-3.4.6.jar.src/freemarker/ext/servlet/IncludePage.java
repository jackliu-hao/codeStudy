/*     */ package freemarker.ext.servlet;
/*     */ 
/*     */ import freemarker.core.Environment;
/*     */ import freemarker.core._DelayedFTLTypeDescription;
/*     */ import freemarker.core._MiscTemplateException;
/*     */ import freemarker.template.TemplateBooleanModel;
/*     */ import freemarker.template.TemplateDirectiveBody;
/*     */ import freemarker.template.TemplateDirectiveModel;
/*     */ import freemarker.template.TemplateException;
/*     */ import freemarker.template.TemplateModel;
/*     */ import freemarker.template.TemplateScalarModel;
/*     */ import freemarker.template.utility.DeepUnwrap;
/*     */ import java.io.IOException;
/*     */ import java.io.PrintWriter;
/*     */ import java.io.Writer;
/*     */ import java.lang.reflect.Array;
/*     */ import java.util.Collection;
/*     */ import java.util.Collections;
/*     */ import java.util.Enumeration;
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.Map;
/*     */ import javax.servlet.ServletException;
/*     */ import javax.servlet.ServletRequest;
/*     */ import javax.servlet.ServletResponse;
/*     */ import javax.servlet.http.HttpServletRequest;
/*     */ import javax.servlet.http.HttpServletRequestWrapper;
/*     */ import javax.servlet.http.HttpServletResponse;
/*     */ import javax.servlet.http.HttpServletResponseWrapper;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class IncludePage
/*     */   implements TemplateDirectiveModel
/*     */ {
/*     */   private final HttpServletRequest request;
/*     */   private final HttpServletResponse response;
/*     */   
/*     */   public IncludePage(HttpServletRequest request, HttpServletResponse response) {
/*  67 */     this.request = request;
/*  68 */     this.response = response;
/*     */   }
/*     */ 
/*     */   
/*     */   public void execute(Environment env, Map params, TemplateModel[] loopVars, TemplateDirectiveBody body) throws TemplateException, IOException {
/*     */     HttpServletResponseWrapper httpServletResponseWrapper;
/*     */     boolean inheritParams;
/*     */     CustomParamsRequest customParamsRequest;
/*  76 */     TemplateModel path = (TemplateModel)params.get("path");
/*  77 */     if (path == null) {
/*  78 */       throw new _MiscTemplateException(env, "Missing required parameter \"path\"");
/*     */     }
/*  80 */     if (!(path instanceof TemplateScalarModel)) {
/*  81 */       throw new _MiscTemplateException(env, new Object[] { "Expected a scalar model. \"path\" is instead ", new _DelayedFTLTypeDescription(path) });
/*     */     }
/*     */     
/*  84 */     String strPath = ((TemplateScalarModel)path).getAsString();
/*  85 */     if (strPath == null) {
/*  86 */       throw new _MiscTemplateException(env, "String value of \"path\" parameter is null");
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  93 */     Writer envOut = env.getOut();
/*     */     
/*  95 */     if (envOut == this.response.getWriter()) {
/*     */ 
/*     */       
/*  98 */       HttpServletResponse wrappedResponse = this.response;
/*     */     } else {
/* 100 */       final PrintWriter printWriter = (envOut instanceof PrintWriter) ? (PrintWriter)envOut : new PrintWriter(envOut);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 106 */       httpServletResponseWrapper = new HttpServletResponseWrapper(this.response)
/*     */         {
/*     */           public PrintWriter getWriter() {
/* 109 */             return printWriter;
/*     */           }
/*     */         };
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/* 116 */     TemplateModel inheritParamsModel = (TemplateModel)params.get("inherit_params");
/* 117 */     if (inheritParamsModel == null) {
/*     */       
/* 119 */       inheritParams = true;
/*     */     } else {
/* 121 */       if (!(inheritParamsModel instanceof TemplateBooleanModel)) {
/* 122 */         throw new _MiscTemplateException(env, new Object[] { "\"inherit_params\" should be a boolean but it's a(n) ", inheritParamsModel
/*     */               
/* 124 */               .getClass().getName(), " instead" });
/*     */       }
/* 126 */       inheritParams = ((TemplateBooleanModel)inheritParamsModel).getAsBoolean();
/*     */     } 
/*     */ 
/*     */     
/* 130 */     TemplateModel paramsModel = (TemplateModel)params.get("params");
/*     */ 
/*     */ 
/*     */     
/* 134 */     if (paramsModel == null && inheritParams) {
/*     */ 
/*     */       
/* 137 */       HttpServletRequest wrappedRequest = this.request;
/*     */     } else {
/*     */       Map paramsMap;
/*     */       
/* 141 */       if (paramsModel != null) {
/*     */         
/* 143 */         Object unwrapped = DeepUnwrap.unwrap(paramsModel);
/* 144 */         if (!(unwrapped instanceof Map)) {
/* 145 */           throw new _MiscTemplateException(env, new Object[] { "Expected \"params\" to unwrap into a java.util.Map. It unwrapped into ", unwrapped
/*     */                 
/* 147 */                 .getClass().getName(), " instead." });
/*     */         }
/* 149 */         paramsMap = (Map)unwrapped;
/*     */       } else {
/* 151 */         paramsMap = Collections.EMPTY_MAP;
/*     */       } 
/* 153 */       customParamsRequest = new CustomParamsRequest(this.request, paramsMap, inheritParams);
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/*     */     try {
/* 159 */       this.request.getRequestDispatcher(strPath).include((ServletRequest)customParamsRequest, (ServletResponse)httpServletResponseWrapper);
/*     */     }
/* 161 */     catch (ServletException e) {
/* 162 */       throw new _MiscTemplateException(e, env);
/*     */     } 
/*     */   }
/*     */   
/*     */   private static final class CustomParamsRequest
/*     */     extends HttpServletRequestWrapper {
/*     */     private final HashMap paramsMap;
/*     */     
/*     */     private CustomParamsRequest(HttpServletRequest request, Map paramMap, boolean inheritParams) {
/* 171 */       super(request);
/* 172 */       this.paramsMap = inheritParams ? new HashMap<>(request.getParameterMap()) : new HashMap<>();
/* 173 */       for (Iterator<Map.Entry> it = paramMap.entrySet().iterator(); it.hasNext(); ) {
/* 174 */         String[] valueArray; Map.Entry entry = it.next();
/* 175 */         String name = String.valueOf(entry.getKey());
/* 176 */         Object value = entry.getValue();
/*     */         
/* 178 */         if (value == null) {
/*     */ 
/*     */           
/* 181 */           valueArray = new String[] { null };
/* 182 */         } else if (value instanceof String[]) {
/*     */           
/* 184 */           valueArray = (String[])value;
/* 185 */         } else if (value instanceof Collection) {
/*     */ 
/*     */           
/* 188 */           Collection col = (Collection)value;
/* 189 */           valueArray = new String[col.size()];
/* 190 */           int i = 0;
/* 191 */           for (Iterator it2 = col.iterator(); it2.hasNext();) {
/* 192 */             valueArray[i++] = String.valueOf(it2.next());
/*     */           }
/* 194 */         } else if (value.getClass().isArray()) {
/*     */ 
/*     */           
/* 197 */           int len = Array.getLength(value);
/* 198 */           valueArray = new String[len];
/* 199 */           for (int i = 0; i < len; i++) {
/* 200 */             valueArray[i] = String.valueOf(Array.get(value, i));
/*     */           
/*     */           }
/*     */         }
/*     */         else {
/*     */           
/* 206 */           valueArray = new String[] { String.valueOf(value) };
/*     */         } 
/* 208 */         String[] existingParams = (String[])this.paramsMap.get(name);
/* 209 */         int el = (existingParams == null) ? 0 : existingParams.length;
/* 210 */         if (el == 0) {
/*     */           
/* 212 */           this.paramsMap.put(name, valueArray); continue;
/*     */         } 
/* 214 */         int vl = valueArray.length;
/* 215 */         if (vl > 0) {
/*     */ 
/*     */           
/* 218 */           String[] newValueArray = new String[el + vl];
/* 219 */           System.arraycopy(valueArray, 0, newValueArray, 0, vl);
/* 220 */           System.arraycopy(existingParams, 0, newValueArray, vl, el);
/* 221 */           this.paramsMap.put(name, newValueArray);
/*     */         } 
/*     */       } 
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public String[] getParameterValues(String name) {
/* 229 */       String[] value = (String[])this.paramsMap.get(name);
/* 230 */       return (value != null) ? (String[])value.clone() : null;
/*     */     }
/*     */ 
/*     */     
/*     */     public String getParameter(String name) {
/* 235 */       String[] values = (String[])this.paramsMap.get(name);
/* 236 */       return (values != null && values.length > 0) ? values[0] : null;
/*     */     }
/*     */ 
/*     */     
/*     */     public Enumeration getParameterNames() {
/* 241 */       return Collections.enumeration(this.paramsMap.keySet());
/*     */     }
/*     */ 
/*     */     
/*     */     public Map getParameterMap() {
/* 246 */       HashMap<?, ?> clone = (HashMap)this.paramsMap.clone();
/* 247 */       for (Iterator<Map.Entry> it = clone.entrySet().iterator(); it.hasNext(); ) {
/* 248 */         Map.Entry entry = it.next();
/* 249 */         entry.setValue(((String[])entry.getValue()).clone());
/*     */       } 
/* 251 */       return Collections.unmodifiableMap(clone);
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\ext\servlet\IncludePage.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */