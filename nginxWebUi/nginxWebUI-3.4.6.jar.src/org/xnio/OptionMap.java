/*     */ package org.xnio;
/*     */ 
/*     */ import java.io.Serializable;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.IdentityHashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Properties;
/*     */ import org.xnio._private.Messages;
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
/*     */ public final class OptionMap
/*     */   implements Iterable<Option<?>>, Serializable
/*     */ {
/*     */   private static final long serialVersionUID = 3632842565346928132L;
/*     */   private final Map<Option<?>, Object> value;
/*     */   
/*     */   private OptionMap(Map<Option<?>, Object> value) {
/*  43 */     this.value = value;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean contains(Option<?> option) {
/*  53 */     return this.value.containsKey(option);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public <T> T get(Option<T> option) {
/*  64 */     return option.cast(this.value.get(option));
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
/*     */   public <T> T get(Option<T> option, T defaultValue) {
/*  76 */     Object o = this.value.get(option);
/*  77 */     return (o == null) ? defaultValue : option.cast(o);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean get(Option<Boolean> option, boolean defaultValue) {
/*  88 */     Object o = this.value.get(option);
/*  89 */     return (o == null) ? defaultValue : ((Boolean)option.cast(o)).booleanValue();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int get(Option<Integer> option, int defaultValue) {
/* 100 */     Object o = this.value.get(option);
/* 101 */     return (o == null) ? defaultValue : ((Integer)option.cast(o)).intValue();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public long get(Option<Long> option, long defaultValue) {
/* 112 */     Object o = this.value.get(option);
/* 113 */     return (o == null) ? defaultValue : ((Long)option.cast(o)).longValue();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Iterator<Option<?>> iterator() {
/* 122 */     return Collections.<Option<?>>unmodifiableCollection(this.value.keySet()).iterator();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int size() {
/* 131 */     return this.value.size();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 137 */   public static final OptionMap EMPTY = new OptionMap(Collections.emptyMap());
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Builder builder() {
/* 145 */     return new Builder();
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
/*     */   public static <T> OptionMap create(Option<T> option, T value) {
/* 159 */     if (option == null) {
/* 160 */       throw Messages.msg.nullParameter("option");
/*     */     }
/* 162 */     if (value == null) {
/* 163 */       throw Messages.msg.nullParameter("value");
/*     */     }
/* 165 */     return new OptionMap(Collections.singletonMap(option, option.cast(value)));
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
/*     */   public static <T1, T2> OptionMap create(Option<T1> option1, T1 value1, Option<T2> option2, T2 value2) {
/* 183 */     if (option1 == null) {
/* 184 */       throw Messages.msg.nullParameter("option1");
/*     */     }
/* 186 */     if (value1 == null) {
/* 187 */       throw Messages.msg.nullParameter("value1");
/*     */     }
/* 189 */     if (option2 == null) {
/* 190 */       throw Messages.msg.nullParameter("option2");
/*     */     }
/* 192 */     if (value2 == null) {
/* 193 */       throw Messages.msg.nullParameter("value2");
/*     */     }
/* 195 */     if (option1 == option2) {
/* 196 */       return create(option2, value2);
/*     */     }
/* 198 */     IdentityHashMap<Option<?>, Object> map = new IdentityHashMap<>(2);
/* 199 */     map.put(option1, value1);
/* 200 */     map.put(option2, value2);
/* 201 */     return new OptionMap(map);
/*     */   }
/*     */   
/*     */   public String toString() {
/* 205 */     StringBuilder builder = new StringBuilder();
/* 206 */     builder.append('{');
/* 207 */     Iterator<Map.Entry<Option<?>, Object>> iterator = this.value.entrySet().iterator();
/* 208 */     while (iterator.hasNext()) {
/* 209 */       Map.Entry<Option<?>, Object> entry = iterator.next();
/* 210 */       builder.append(entry.getKey()).append("=>").append(entry.getValue());
/* 211 */       if (iterator.hasNext()) {
/* 212 */         builder.append(',');
/*     */       }
/*     */     } 
/* 215 */     builder.append('}');
/* 216 */     return builder.toString();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean equals(Object other) {
/* 226 */     return (other instanceof OptionMap && equals((OptionMap)other));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean equals(OptionMap other) {
/* 236 */     return (this == other || (other != null && this.value.equals(other.value)));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 245 */     return this.value.hashCode();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public static final class Builder
/*     */   {
/*     */     private static class OVPair<T>
/*     */     {
/*     */       Option<T> option;
/*     */ 
/*     */       
/*     */       T value;
/*     */ 
/*     */       
/*     */       private OVPair(Option<T> option, T value) {
/* 261 */         this.option = option;
/* 262 */         this.value = value;
/*     */       }
/*     */     }
/*     */     
/* 266 */     private List<OVPair<?>> list = new ArrayList<>();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public <T> Builder parse(Option<T> key, String stringValue) {
/* 277 */       set(key, key.parseValue(stringValue, key.getClass().getClassLoader()));
/* 278 */       return this;
/*     */     }
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
/*     */     public <T> Builder parse(Option<T> key, String stringValue, ClassLoader classLoader) {
/* 291 */       set(key, key.parseValue(stringValue, classLoader));
/* 292 */       return this;
/*     */     }
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
/*     */     public Builder parseAll(Properties props, String prefix, ClassLoader optionClassLoader) {
/* 306 */       if (!prefix.endsWith(".")) {
/* 307 */         prefix = prefix + ".";
/*     */       }
/* 309 */       for (String name : props.stringPropertyNames()) {
/* 310 */         if (name.startsWith(prefix)) {
/* 311 */           String optionName = name.substring(prefix.length());
/*     */           try {
/* 313 */             Option<?> option = Option.fromString(optionName, optionClassLoader);
/* 314 */             parse(option, props.getProperty(name), optionClassLoader);
/* 315 */           } catch (IllegalArgumentException e) {
/* 316 */             Messages.optionParseMsg.invalidOptionInProperty(optionName, name, e);
/*     */           } 
/*     */         } 
/*     */       } 
/* 320 */       return this;
/*     */     }
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
/*     */     public Builder parseAll(Properties props, String prefix) {
/* 333 */       if (!prefix.endsWith(".")) {
/* 334 */         prefix = prefix + ".";
/*     */       }
/* 336 */       for (String name : props.stringPropertyNames()) {
/* 337 */         if (name.startsWith(prefix)) {
/* 338 */           String optionName = name.substring(prefix.length());
/*     */           try {
/* 340 */             Option<?> option = Option.fromString(optionName, getClass().getClassLoader());
/* 341 */             parse(option, props.getProperty(name));
/* 342 */           } catch (IllegalArgumentException e) {
/* 343 */             Messages.optionParseMsg.invalidOptionInProperty(optionName, name, e);
/*     */           } 
/*     */         } 
/*     */       } 
/* 347 */       return this;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public <T> Builder set(Option<T> key, T value) {
/* 359 */       if (key == null) {
/* 360 */         throw Messages.msg.nullParameter("key");
/*     */       }
/* 362 */       if (value == null) {
/* 363 */         throw Messages.msg.nullParameter("value");
/*     */       }
/* 365 */       this.list.add(new OVPair(key, value));
/* 366 */       return this;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public Builder set(Option<Integer> key, int value) {
/* 377 */       if (key == null) {
/* 378 */         throw Messages.msg.nullParameter("key");
/*     */       }
/* 380 */       this.list.add(new OVPair(key, Integer.valueOf(value)));
/* 381 */       return this;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public Builder setSequence(Option<Sequence<Integer>> key, int... values) {
/* 392 */       if (key == null) {
/* 393 */         throw Messages.msg.nullParameter("key");
/*     */       }
/* 395 */       Integer[] a = new Integer[values.length];
/* 396 */       for (int i = 0; i < values.length; i++) {
/* 397 */         a[i] = Integer.valueOf(values[i]);
/*     */       }
/* 399 */       this.list.add(new OVPair(key, Sequence.of(a)));
/* 400 */       return this;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public Builder set(Option<Long> key, long value) {
/* 411 */       if (key == null) {
/* 412 */         throw Messages.msg.nullParameter("key");
/*     */       }
/* 414 */       this.list.add(new OVPair(key, Long.valueOf(value)));
/* 415 */       return this;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public Builder setSequence(Option<Sequence<Long>> key, long... values) {
/* 426 */       if (key == null) {
/* 427 */         throw Messages.msg.nullParameter("key");
/*     */       }
/* 429 */       Long[] a = new Long[values.length];
/* 430 */       for (int i = 0; i < values.length; i++) {
/* 431 */         a[i] = Long.valueOf(values[i]);
/*     */       }
/* 433 */       this.list.add(new OVPair(key, Sequence.of(a)));
/* 434 */       return this;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public Builder set(Option<Boolean> key, boolean value) {
/* 445 */       if (key == null) {
/* 446 */         throw Messages.msg.nullParameter("key");
/*     */       }
/* 448 */       this.list.add(new OVPair(key, Boolean.valueOf(value)));
/* 449 */       return this;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public Builder setSequence(Option<Sequence<Boolean>> key, boolean... values) {
/* 461 */       if (key == null) {
/* 462 */         throw Messages.msg.nullParameter("key");
/*     */       }
/* 464 */       Boolean[] a = new Boolean[values.length];
/* 465 */       for (int i = 0; i < values.length; i++) {
/* 466 */         a[i] = Boolean.valueOf(values[i]);
/*     */       }
/* 468 */       this.list.add(new OVPair(key, Sequence.of(a)));
/* 469 */       return this;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public <T> Builder setSequence(Option<Sequence<T>> key, T... values) {
/* 481 */       if (key == null) {
/* 482 */         throw Messages.msg.nullParameter("key");
/*     */       }
/* 484 */       this.list.add(new OVPair(key, Sequence.of(values)));
/* 485 */       return this;
/*     */     }
/*     */     
/*     */     private <T> void copy(Map<?, ?> map, Option<T> option) {
/* 489 */       set(option, option.cast(map.get(option)));
/*     */     }
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
/*     */     public Builder add(Map<?, ?> map) throws ClassCastException {
/* 502 */       for (Object key : map.keySet()) {
/* 503 */         Option<?> option = Option.class.cast(key);
/* 504 */         copy(map, option);
/*     */       } 
/* 506 */       return this;
/*     */     }
/*     */     
/*     */     private <T> void copy(OptionMap optionMap, Option<T> option) {
/* 510 */       set(option, optionMap.get(option));
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public Builder addAll(OptionMap optionMap) {
/* 521 */       for (Option<?> option : (Iterable<Option<?>>)optionMap) {
/* 522 */         copy(optionMap, option);
/*     */       }
/* 524 */       return this;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public OptionMap getMap() {
/* 533 */       List<OVPair<?>> list = this.list;
/* 534 */       if (list.size() == 0)
/* 535 */         return OptionMap.EMPTY; 
/* 536 */       if (list.size() == 1) {
/* 537 */         OVPair<?> pair = list.get(0);
/* 538 */         return new OptionMap(Collections.singletonMap(pair.option, pair.value));
/*     */       } 
/* 540 */       Map<Option<?>, Object> map = new IdentityHashMap<>();
/* 541 */       for (OVPair<?> ovPair : list) {
/* 542 */         map.put(ovPair.option, ovPair.value);
/*     */       }
/* 544 */       return new OptionMap(map);
/*     */     }
/*     */     
/*     */     private Builder() {}
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\xnio\OptionMap.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */