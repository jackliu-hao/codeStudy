/*     */ package cn.hutool.core.bean;
/*     */ 
/*     */ import cn.hutool.core.clone.CloneSupport;
/*     */ import cn.hutool.core.lang.Assert;
/*     */ import cn.hutool.core.util.ClassUtil;
/*     */ import cn.hutool.core.util.ReflectUtil;
/*     */ import java.io.Serializable;
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
/*     */ public class DynaBean
/*     */   extends CloneSupport<DynaBean>
/*     */   implements Serializable
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*     */   private final Class<?> beanClass;
/*     */   private final Object bean;
/*     */   
/*     */   public static DynaBean create(Object bean) {
/*  31 */     return new DynaBean(bean);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static DynaBean create(Class<?> beanClass) {
/*  41 */     return new DynaBean(beanClass);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static DynaBean create(Class<?> beanClass, Object... params) {
/*  53 */     return new DynaBean(beanClass, params);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public DynaBean(Class<?> beanClass, Object... params) {
/*  65 */     this(ReflectUtil.newInstance(beanClass, params));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public DynaBean(Class<?> beanClass) {
/*  74 */     this(ReflectUtil.newInstance(beanClass, new Object[0]));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public DynaBean(Object bean) {
/*  83 */     Assert.notNull(bean);
/*  84 */     if (bean instanceof DynaBean) {
/*  85 */       bean = ((DynaBean)bean).getBean();
/*     */     }
/*  87 */     this.bean = bean;
/*  88 */     this.beanClass = ClassUtil.getClass(bean);
/*     */   }
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
/*     */   public <T> T get(String fieldName) throws BeanException {
/* 102 */     if (Map.class.isAssignableFrom(this.beanClass)) {
/* 103 */       return (T)((Map)this.bean).get(fieldName);
/*     */     }
/* 105 */     PropDesc prop = BeanUtil.getBeanDesc(this.beanClass).getProp(fieldName);
/* 106 */     if (null == prop) {
/* 107 */       throw new BeanException("No public field or get method for {}", new Object[] { fieldName });
/*     */     }
/* 109 */     return (T)prop.getValue(this.bean);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean containsProp(String fieldName) {
/* 121 */     if (Map.class.isAssignableFrom(this.beanClass)) {
/* 122 */       return ((Map)this.bean).containsKey(fieldName);
/*     */     }
/* 124 */     return (null != BeanUtil.getBeanDesc(this.beanClass).getProp(fieldName));
/*     */   }
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
/*     */   public <T> T safeGet(String fieldName) {
/*     */     try {
/* 138 */       return get(fieldName);
/* 139 */     } catch (Exception e) {
/* 140 */       return null;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void set(String fieldName, Object value) throws BeanException {
/* 153 */     if (Map.class.isAssignableFrom(this.beanClass)) {
/* 154 */       ((Map<String, Object>)this.bean).put(fieldName, value);
/*     */     } else {
/* 156 */       PropDesc prop = BeanUtil.getBeanDesc(this.beanClass).getProp(fieldName);
/* 157 */       if (null == prop) {
/* 158 */         throw new BeanException("No public field or set method for {}", new Object[] { fieldName });
/*     */       }
/* 160 */       prop.setValue(this.bean, value);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Object invoke(String methodName, Object... params) {
/* 172 */     return ReflectUtil.invoke(this.bean, methodName, params);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public <T> T getBean() {
/* 183 */     return (T)this.bean;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public <T> Class<T> getBeanClass() {
/* 194 */     return (Class)this.beanClass;
/*     */   }
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 199 */     int prime = 31;
/* 200 */     int result = 1;
/* 201 */     result = 31 * result + ((this.bean == null) ? 0 : this.bean.hashCode());
/* 202 */     return result;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean equals(Object obj) {
/* 207 */     if (this == obj) {
/* 208 */       return true;
/*     */     }
/* 210 */     if (obj == null) {
/* 211 */       return false;
/*     */     }
/* 213 */     if (getClass() != obj.getClass()) {
/* 214 */       return false;
/*     */     }
/* 216 */     DynaBean other = (DynaBean)obj;
/* 217 */     if (this.bean == null)
/* 218 */       return (other.bean == null); 
/* 219 */     return this.bean.equals(other.bean);
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 224 */     return this.bean.toString();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\bean\DynaBean.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */