/*     */ package freemarker.ext.jsp;
/*     */ 
/*     */ import freemarker.core._DelayedJQuote;
/*     */ import freemarker.core._DelayedShortClassName;
/*     */ import freemarker.core._ErrorDescriptionBuilder;
/*     */ import freemarker.core._TemplateModelException;
/*     */ import freemarker.ext.beans.BeansWrapper;
/*     */ import freemarker.template.ObjectWrapper;
/*     */ import freemarker.template.ObjectWrapperAndUnwrapper;
/*     */ import freemarker.template.TemplateModel;
/*     */ import freemarker.template.TemplateModelException;
/*     */ import freemarker.template.utility.StringUtil;
/*     */ import java.beans.BeanInfo;
/*     */ import java.beans.IntrospectionException;
/*     */ import java.beans.Introspector;
/*     */ import java.beans.PropertyDescriptor;
/*     */ import java.lang.reflect.InvocationTargetException;
/*     */ import java.lang.reflect.Method;
/*     */ import java.math.BigDecimal;
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.Map;
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
/*     */ class JspTagModelBase
/*     */ {
/*     */   protected final String tagName;
/*     */   private final Class tagClass;
/*     */   private final Method dynaSetter;
/*     */   private final Map propertySetters;
/*     */   
/*     */   protected JspTagModelBase(String tagName, Class<?> tagClass) throws IntrospectionException {
/*     */     Method dynaSetter;
/*  51 */     this.propertySetters = new HashMap<>();
/*     */ 
/*     */     
/*  54 */     this.tagName = tagName;
/*  55 */     this.tagClass = tagClass;
/*  56 */     BeanInfo bi = Introspector.getBeanInfo(tagClass);
/*  57 */     PropertyDescriptor[] pda = bi.getPropertyDescriptors();
/*  58 */     for (int i = 0; i < pda.length; i++) {
/*  59 */       PropertyDescriptor pd = pda[i];
/*  60 */       Method m = pd.getWriteMethod();
/*  61 */       if (m != null) {
/*  62 */         this.propertySetters.put(pd.getName(), m);
/*     */       }
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/*     */     try {
/*  69 */       dynaSetter = tagClass.getMethod("setDynamicAttribute", new Class[] { String.class, String.class, Object.class });
/*     */     }
/*  71 */     catch (NoSuchMethodException nsme) {
/*  72 */       dynaSetter = null;
/*     */     } 
/*  74 */     this.dynaSetter = dynaSetter;
/*     */   }
/*     */   
/*     */   Object getTagInstance() throws IllegalAccessException, InstantiationException {
/*  78 */     return this.tagClass.newInstance();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   void setupTag(Object tag, Map args, ObjectWrapper wrapper) throws TemplateModelException, InvocationTargetException, IllegalAccessException {
/*  85 */     if (args != null && !args.isEmpty()) {
/*     */ 
/*     */       
/*  88 */       ObjectWrapperAndUnwrapper unwrapper = (wrapper instanceof ObjectWrapperAndUnwrapper) ? (ObjectWrapperAndUnwrapper)wrapper : (ObjectWrapperAndUnwrapper)BeansWrapper.getDefaultInstance();
/*  89 */       Object[] argArray = new Object[1];
/*  90 */       for (Iterator<Map.Entry> iter = args.entrySet().iterator(); iter.hasNext(); ) {
/*  91 */         Map.Entry entry = iter.next();
/*  92 */         Object arg = unwrapper.unwrap((TemplateModel)entry.getValue());
/*  93 */         argArray[0] = arg;
/*  94 */         Object paramName = entry.getKey();
/*  95 */         Method setterMethod = (Method)this.propertySetters.get(paramName);
/*  96 */         if (setterMethod == null) {
/*  97 */           if (this.dynaSetter == null) {
/*  98 */             throw new TemplateModelException("Unknown property " + 
/*  99 */                 StringUtil.jQuote(paramName.toString()) + " on instance of " + this.tagClass
/* 100 */                 .getName());
/*     */           }
/* 102 */           this.dynaSetter.invoke(tag, new Object[] { null, paramName, argArray[0] });
/*     */           continue;
/*     */         } 
/* 105 */         if (arg instanceof BigDecimal) {
/* 106 */           argArray[0] = BeansWrapper.coerceBigDecimal((BigDecimal)arg, setterMethod
/* 107 */               .getParameterTypes()[0]);
/*     */         }
/*     */         try {
/* 110 */           setterMethod.invoke(tag, argArray);
/* 111 */         } catch (Exception e) {
/* 112 */           Class<?> setterType = setterMethod.getParameterTypes()[0];
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */           
/* 118 */           _ErrorDescriptionBuilder desc = new _ErrorDescriptionBuilder(new Object[] { "Failed to set JSP tag parameter ", new _DelayedJQuote(paramName), " (declared type: ", new _DelayedShortClassName(setterType) + ", actual value's type: ", (argArray[0] != null) ? new _DelayedShortClassName(argArray[0].getClass()) : "Null", "). See cause exception for the more specific cause..." });
/*     */           
/* 120 */           if (e instanceof IllegalArgumentException && !setterType.isAssignableFrom(String.class) && argArray[0] != null && argArray[0] instanceof String)
/*     */           {
/* 122 */             desc.tip(new Object[] { "This problem is often caused by unnecessary parameter quotation. Paramters aren't quoted in FTL, similarly as they aren't quoted in most languages. For example, these parameter assignments are wrong: ", "<@my.tag p1=\"true\" p2=\"10\" p3=\"${someVariable}\" p4=\"${x+1}\" />", ". The correct form is: ", "<@my.tag p1=true p2=10 p3=someVariable p4=x+1 />", ". Only string literals are quoted (regardless of where they occur): ", "<@my.box style=\"info\" message=\"Hello ${name}!\" width=200 />", "." });
/*     */           }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */           
/* 132 */           throw new _TemplateModelException(e, null, desc);
/*     */         } 
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   protected final TemplateModelException toTemplateModelExceptionOrRethrow(Exception e) throws TemplateModelException {
/* 140 */     if (e instanceof RuntimeException && !isCommonRuntimeException((RuntimeException)e)) {
/* 141 */       throw (RuntimeException)e;
/*     */     }
/* 143 */     if (e instanceof TemplateModelException) {
/* 144 */       throw (TemplateModelException)e;
/*     */     }
/* 146 */     if (e instanceof SimpleTagDirectiveModel.TemplateExceptionWrapperJspException) {
/* 147 */       return toTemplateModelExceptionOrRethrow((Exception)((SimpleTagDirectiveModel.TemplateExceptionWrapperJspException)e).getCause());
/*     */     }
/* 149 */     return new TemplateModelException("Error while invoking the " + 
/* 150 */         StringUtil.jQuote(this.tagName) + " JSP custom tag; see cause exception", e instanceof freemarker.template.TemplateException, e);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private boolean isCommonRuntimeException(RuntimeException e) {
/* 160 */     Class<?> eClass = e.getClass();
/*     */ 
/*     */     
/* 163 */     return (eClass == NullPointerException.class || eClass == IllegalArgumentException.class || eClass == ClassCastException.class || eClass == IndexOutOfBoundsException.class);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\ext\jsp\JspTagModelBase.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */