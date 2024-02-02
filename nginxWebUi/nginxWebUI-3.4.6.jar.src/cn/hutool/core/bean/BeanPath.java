/*     */ package cn.hutool.core.bean;
/*     */ 
/*     */ import cn.hutool.core.collection.CollUtil;
/*     */ import cn.hutool.core.collection.ListUtil;
/*     */ import cn.hutool.core.convert.Convert;
/*     */ import cn.hutool.core.map.MapUtil;
/*     */ import cn.hutool.core.util.ArrayUtil;
/*     */ import cn.hutool.core.util.StrUtil;
/*     */ import java.io.Serializable;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class BeanPath
/*     */   implements Serializable
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*  43 */   private static final char[] EXP_CHARS = new char[] { '.', '[', ']' };
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private boolean isStartWith = false;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected List<String> patternParts;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static BeanPath create(String expression) {
/*  71 */     return new BeanPath(expression);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public BeanPath(String expression) {
/*  80 */     init(expression);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public List<String> getPatternParts() {
/*  89 */     return this.patternParts;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Object get(Object bean) {
/*  99 */     return get(this.patternParts, bean, false);
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
/*     */ 
/*     */ 
/*     */   
/*     */   public void set(Object bean, Object value) {
/* 116 */     set(bean, this.patternParts, value);
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 121 */     return this.patternParts.toString();
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void set(Object bean, List<String> patternParts, Object value) {
/* 140 */     Object subBean = get(patternParts, bean, true);
/* 141 */     if (null == subBean) {
/* 142 */       set(bean, patternParts.subList(0, patternParts.size() - 1), new HashMap<>());
/*     */       
/* 144 */       subBean = get(patternParts, bean, true);
/*     */     } 
/* 146 */     BeanUtil.setFieldValue(subBean, patternParts.get(patternParts.size() - 1), value);
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
/*     */   private Object get(List<String> patternParts, Object bean, boolean ignoreLast) {
/* 158 */     int length = patternParts.size();
/* 159 */     if (ignoreLast) {
/* 160 */       length--;
/*     */     }
/* 162 */     Object subBean = bean;
/* 163 */     boolean isFirst = true;
/*     */     
/* 165 */     for (int i = 0; i < length; i++) {
/* 166 */       String patternPart = patternParts.get(i);
/* 167 */       subBean = getFieldValue(subBean, patternPart);
/* 168 */       if (null == subBean)
/*     */       {
/* 170 */         if (isFirst && false == this.isStartWith && BeanUtil.isMatchName(bean, patternPart, true)) {
/* 171 */           subBean = bean;
/* 172 */           isFirst = false;
/*     */         } else {
/* 174 */           return null;
/*     */         } 
/*     */       }
/*     */     } 
/* 178 */     return subBean;
/*     */   }
/*     */ 
/*     */   
/*     */   private static Object getFieldValue(Object bean, String expression) {
/* 183 */     if (StrUtil.isBlank(expression)) {
/* 184 */       return null;
/*     */     }
/*     */     
/* 187 */     if (StrUtil.contains(expression, ':')) {
/*     */       
/* 189 */       List<String> parts = StrUtil.splitTrim(expression, ':');
/* 190 */       int start = Integer.parseInt(parts.get(0));
/* 191 */       int end = Integer.parseInt(parts.get(1));
/* 192 */       int step = 1;
/* 193 */       if (3 == parts.size()) {
/* 194 */         step = Integer.parseInt(parts.get(2));
/*     */       }
/* 196 */       if (bean instanceof Collection)
/* 197 */         return CollUtil.sub((Collection)bean, start, end, step); 
/* 198 */       if (ArrayUtil.isArray(bean))
/* 199 */         return ArrayUtil.sub(bean, start, end, step); 
/*     */     } else {
/* 201 */       if (StrUtil.contains(expression, ',')) {
/*     */         
/* 203 */         List<String> keys = StrUtil.splitTrim(expression, ',');
/* 204 */         if (bean instanceof Collection)
/* 205 */           return CollUtil.getAny((Collection)bean, (int[])Convert.convert(int[].class, keys)); 
/* 206 */         if (ArrayUtil.isArray(bean)) {
/* 207 */           return ArrayUtil.getAny(bean, (int[])Convert.convert(int[].class, keys));
/*     */         }
/* 209 */         String[] unWrappedKeys = new String[keys.size()];
/* 210 */         for (int i = 0; i < unWrappedKeys.length; i++) {
/* 211 */           unWrappedKeys[i] = StrUtil.unWrap(keys.get(i), '\'');
/*     */         }
/* 213 */         if (bean instanceof Map)
/*     */         {
/* 215 */           return MapUtil.getAny((Map)bean, (Object[])unWrappedKeys);
/*     */         }
/* 217 */         Map<String, Object> map = BeanUtil.beanToMap(bean, new String[0]);
/* 218 */         return MapUtil.getAny(map, (Object[])unWrappedKeys);
/*     */       } 
/*     */ 
/*     */ 
/*     */       
/* 223 */       return BeanUtil.getFieldValue(bean, expression);
/*     */     } 
/*     */     
/* 226 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void init(String expression) {
/* 235 */     List<String> localPatternParts = new ArrayList<>();
/* 236 */     int length = expression.length();
/*     */     
/* 238 */     StringBuilder builder = new StringBuilder();
/*     */     
/* 240 */     boolean isNumStart = false;
/* 241 */     boolean isInWrap = false;
/* 242 */     for (int i = 0; i < length; i++) {
/* 243 */       char c = expression.charAt(i);
/* 244 */       if (0 == i && '$' == c) {
/*     */         
/* 246 */         this.isStartWith = true;
/*     */ 
/*     */       
/*     */       }
/* 250 */       else if ('\'' == c) {
/*     */         
/* 252 */         isInWrap = (false == isInWrap);
/*     */ 
/*     */       
/*     */       }
/* 256 */       else if (false == isInWrap && ArrayUtil.contains(EXP_CHARS, c)) {
/*     */         
/* 258 */         if (']' == c) {
/*     */           
/* 260 */           if (false == isNumStart) {
/* 261 */             throw new IllegalArgumentException(StrUtil.format("Bad expression '{}':{}, we find ']' but no '[' !", new Object[] { expression, Integer.valueOf(i) }));
/*     */           }
/* 263 */           isNumStart = false;
/*     */         } else {
/*     */           
/* 266 */           if (isNumStart)
/*     */           {
/* 268 */             throw new IllegalArgumentException(StrUtil.format("Bad expression '{}':{}, we find '[' but no ']' !", new Object[] { expression, Integer.valueOf(i) })); } 
/* 269 */           if ('[' == c)
/*     */           {
/* 271 */             isNumStart = true;
/*     */           }
/*     */         } 
/*     */         
/* 275 */         if (builder.length() > 0) {
/* 276 */           localPatternParts.add(builder.toString());
/*     */         }
/* 278 */         builder.setLength(0);
/*     */       } else {
/*     */         
/* 281 */         builder.append(c);
/*     */       } 
/*     */     } 
/*     */ 
/*     */     
/* 286 */     if (isNumStart) {
/* 287 */       throw new IllegalArgumentException(StrUtil.format("Bad expression '{}':{}, we find '[' but no ']' !", new Object[] { expression, Integer.valueOf(length - 1) }));
/*     */     }
/* 289 */     if (builder.length() > 0) {
/* 290 */       localPatternParts.add(builder.toString());
/*     */     }
/*     */ 
/*     */ 
/*     */     
/* 295 */     this.patternParts = ListUtil.unmodifiable(localPatternParts);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\bean\BeanPath.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */