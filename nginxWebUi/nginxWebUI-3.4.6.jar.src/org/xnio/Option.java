/*     */ package org.xnio;
/*     */ 
/*     */ import java.io.InvalidObjectException;
/*     */ import java.io.ObjectStreamException;
/*     */ import java.io.Serializable;
/*     */ import java.lang.reflect.Field;
/*     */ import java.lang.reflect.Modifier;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.Collections;
/*     */ import java.util.HashMap;
/*     */ import java.util.LinkedHashSet;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
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
/*     */ 
/*     */ public abstract class Option<T>
/*     */   implements Serializable
/*     */ {
/*     */   private static final long serialVersionUID = -1564427329140182760L;
/*     */   private final Class<?> declClass;
/*     */   private final String name;
/*     */   private static final Map<Class<?>, ValueParser<?>> parsers;
/*     */   
/*     */   Option(Class<?> declClass, String name) {
/*  51 */     if (declClass == null) {
/*  52 */       throw Messages.msg.nullParameter("declClass");
/*     */     }
/*  54 */     if (name == null) {
/*  55 */       throw Messages.msg.nullParameter("name");
/*     */     }
/*  57 */     this.declClass = declClass;
/*  58 */     this.name = name;
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
/*     */   public static <T> Option<T> simple(Class<?> declClass, String name, Class<T> type) {
/*  71 */     return new SingleOption<>(declClass, name, type);
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
/*     */   public static <T> Option<Sequence<T>> sequence(Class<?> declClass, String name, Class<T> elementType) {
/*  84 */     return new SequenceOption<>(declClass, name, elementType);
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
/*     */   public static <T> Option<Class<? extends T>> type(Class<?> declClass, String name, Class<T> declType) {
/*  97 */     return new TypeOption<>(declClass, name, declType);
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
/*     */   public static <T> Option<Sequence<Class<? extends T>>> typeSequence(Class<?> declClass, String name, Class<T> elementDeclType) {
/* 110 */     return new TypeSequenceOption<>(declClass, name, elementDeclType);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getName() {
/* 119 */     return this.name;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String toString() {
/* 128 */     return this.declClass.getName() + "." + this.name;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Option<?> fromString(String name, ClassLoader classLoader) throws IllegalArgumentException {
/*     */     Class<?> clazz;
/*     */     Field field;
/*     */     Option<?> option;
/* 141 */     int lastDot = name.lastIndexOf('.');
/* 142 */     if (lastDot == -1) {
/* 143 */       throw Messages.msg.invalidOptionName(name);
/*     */     }
/* 145 */     String fieldName = name.substring(lastDot + 1);
/* 146 */     String className = name.substring(0, lastDot);
/*     */     
/*     */     try {
/* 149 */       clazz = Class.forName(className, true, classLoader);
/* 150 */     } catch (ClassNotFoundException e) {
/* 151 */       throw Messages.msg.optionClassNotFound(className, classLoader);
/*     */     } 
/*     */     
/*     */     try {
/* 155 */       field = clazz.getField(fieldName);
/* 156 */     } catch (NoSuchFieldException e) {
/* 157 */       throw Messages.msg.noField(fieldName, clazz);
/*     */     } 
/* 159 */     int modifiers = field.getModifiers();
/* 160 */     if (!Modifier.isPublic(modifiers)) {
/* 161 */       throw Messages.msg.fieldNotAccessible(fieldName, clazz);
/*     */     }
/* 163 */     if (!Modifier.isStatic(modifiers)) {
/* 164 */       throw Messages.msg.fieldNotStatic(fieldName, clazz);
/*     */     }
/*     */     
/*     */     try {
/* 168 */       option = (Option)field.get(null);
/* 169 */     } catch (IllegalAccessException e) {
/* 170 */       throw Messages.msg.fieldNotAccessible(fieldName, clazz);
/*     */     } 
/* 172 */     if (option == null) {
/* 173 */       throw Messages.msg.invalidNullOption(name);
/*     */     }
/* 175 */     return option;
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
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final T cast(Object o, T defaultVal) throws ClassCastException {
/* 198 */     return (o == null) ? defaultVal : cast(o);
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
/*     */   
/*     */   protected final Object readResolve() throws ObjectStreamException {
/*     */     try {
/* 219 */       Field field = this.declClass.getField(this.name);
/* 220 */       int modifiers = field.getModifiers();
/* 221 */       if (!Modifier.isPublic(modifiers)) {
/* 222 */         throw new InvalidObjectException("Invalid Option instance (the field is not public)");
/*     */       }
/* 224 */       if (!Modifier.isStatic(modifiers)) {
/* 225 */         throw new InvalidObjectException("Invalid Option instance (the field is not static)");
/*     */       }
/* 227 */       Option<?> option = (Option)field.get(null);
/* 228 */       if (option == null) {
/* 229 */         throw new InvalidObjectException("Invalid null Option");
/*     */       }
/* 231 */       return option;
/* 232 */     } catch (NoSuchFieldException e) {
/* 233 */       throw new InvalidObjectException("Invalid Option instance (no matching field)");
/* 234 */     } catch (IllegalAccessException e) {
/* 235 */       throw new InvalidObjectException("Invalid Option instance (Illegal access on field get)");
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static SetBuilder setBuilder() {
/* 245 */     return new SetBuilder();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public static class SetBuilder
/*     */   {
/* 252 */     private List<Option<?>> optionSet = new ArrayList<>();
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
/*     */     public SetBuilder add(Option<?> option) {
/* 264 */       if (option == null) {
/* 265 */         throw Messages.msg.nullParameter("option");
/*     */       }
/* 267 */       this.optionSet.add(option);
/* 268 */       return this;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public SetBuilder add(Option<?> option1, Option<?> option2) {
/* 279 */       if (option1 == null) {
/* 280 */         throw Messages.msg.nullParameter("option1");
/*     */       }
/* 282 */       if (option2 == null) {
/* 283 */         throw Messages.msg.nullParameter("option2");
/*     */       }
/* 285 */       this.optionSet.add(option1);
/* 286 */       this.optionSet.add(option2);
/* 287 */       return this;
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
/*     */     public SetBuilder add(Option<?> option1, Option<?> option2, Option<?> option3) {
/* 299 */       if (option1 == null) {
/* 300 */         throw Messages.msg.nullParameter("option1");
/*     */       }
/* 302 */       if (option2 == null) {
/* 303 */         throw Messages.msg.nullParameter("option2");
/*     */       }
/* 305 */       if (option3 == null) {
/* 306 */         throw Messages.msg.nullParameter("option3");
/*     */       }
/* 308 */       this.optionSet.add(option1);
/* 309 */       this.optionSet.add(option2);
/* 310 */       this.optionSet.add(option3);
/* 311 */       return this;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public SetBuilder add(Option<?>... options) {
/* 321 */       if (options == null) {
/* 322 */         throw Messages.msg.nullParameter("options");
/*     */       }
/* 324 */       for (Option<?> option : options) {
/* 325 */         add(option);
/*     */       }
/* 327 */       return this;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public SetBuilder addAll(Collection<Option<?>> options) {
/* 337 */       if (options == null) {
/* 338 */         throw Messages.msg.nullParameter("option");
/*     */       }
/* 340 */       for (Option<?> option : options) {
/* 341 */         add(option);
/*     */       }
/* 343 */       return this;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public Set<Option<?>> create() {
/* 352 */       return Collections.unmodifiableSet(new LinkedHashSet<>(this.optionSet));
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 362 */   private static final ValueParser<?> noParser = new ValueParser() {
/*     */       public Object parseValue(String string, ClassLoader classLoader) throws IllegalArgumentException {
/* 364 */         throw Messages.msg.noOptionParser();
/*     */       }
/*     */     };
/*     */   
/*     */   static {
/* 369 */     Map<Class<?>, ValueParser<?>> map = new HashMap<>();
/* 370 */     map.put(Byte.class, new ValueParser<Byte>() {
/*     */           public Byte parseValue(String string, ClassLoader classLoader) throws IllegalArgumentException {
/* 372 */             return Byte.decode(string.trim());
/*     */           }
/*     */         });
/* 375 */     map.put(Short.class, new ValueParser<Short>() {
/*     */           public Short parseValue(String string, ClassLoader classLoader) throws IllegalArgumentException {
/* 377 */             return Short.decode(string.trim());
/*     */           }
/*     */         });
/* 380 */     map.put(Integer.class, new ValueParser<Integer>() {
/*     */           public Integer parseValue(String string, ClassLoader classLoader) throws IllegalArgumentException {
/* 382 */             return Integer.decode(string.trim());
/*     */           }
/*     */         });
/* 385 */     map.put(Long.class, new ValueParser<Long>() {
/*     */           public Long parseValue(String string, ClassLoader classLoader) throws IllegalArgumentException {
/* 387 */             return Long.decode(string.trim());
/*     */           }
/*     */         });
/* 390 */     map.put(String.class, new ValueParser<String>() {
/*     */           public String parseValue(String string, ClassLoader classLoader) throws IllegalArgumentException {
/* 392 */             return string.trim();
/*     */           }
/*     */         });
/* 395 */     map.put(Boolean.class, new ValueParser<Boolean>() {
/*     */           public Boolean parseValue(String string, ClassLoader classLoader) throws IllegalArgumentException {
/* 397 */             return Boolean.valueOf(string.trim());
/*     */           }
/*     */         });
/* 400 */     map.put(Property.class, new ValueParser() {
/*     */           public Object parseValue(String string, ClassLoader classLoader) throws IllegalArgumentException {
/* 402 */             int idx = string.indexOf('=');
/* 403 */             if (idx == -1) {
/* 404 */               throw Messages.msg.invalidOptionPropertyFormat(string);
/*     */             }
/* 406 */             return Property.of(string.substring(0, idx), string.substring(idx + 1, string.length()));
/*     */           }
/*     */         });
/* 409 */     parsers = map;
/*     */   }
/*     */   
/*     */   static <T> ValueParser<Class<? extends T>> getClassParser(final Class<T> argType) {
/* 413 */     return (ValueParser)new ValueParser<Class<? extends Class<? extends T>>>() {
/*     */         public Class<? extends T> parseValue(String string, ClassLoader classLoader) throws IllegalArgumentException {
/*     */           try {
/* 416 */             return Class.forName(string, false, classLoader).asSubclass(argType);
/* 417 */           } catch (ClassNotFoundException e) {
/* 418 */             throw Messages.msg.classNotFound(string, e);
/* 419 */           } catch (ClassCastException e) {
/* 420 */             throw Messages.msg.classNotInstance(string, argType);
/*     */           } 
/*     */         }
/*     */       };
/*     */   }
/*     */   
/*     */   static <T, E extends Enum<E>> ValueParser<T> getEnumParser(final Class<T> enumType) {
/* 427 */     return new ValueParser<T>() {
/*     */         public T parseValue(String string, ClassLoader classLoader) throws IllegalArgumentException {
/* 429 */           return enumType.cast(Enum.valueOf((Class)Option.asEnum(enumType), string.trim()));
/*     */         }
/*     */       };
/*     */   }
/*     */ 
/*     */   
/*     */   private static <T, E extends Enum<E>> Class<E> asEnum(Class<T> enumType) {
/* 436 */     return enumType;
/*     */   }
/*     */ 
/*     */   
/*     */   static <T> ValueParser<T> getParser(Class<T> argType) {
/* 441 */     if (argType.isEnum()) {
/* 442 */       return getEnumParser(argType);
/*     */     }
/* 444 */     ValueParser<?> value = parsers.get(argType);
/* 445 */     return (value == null) ? (ValueParser)noParser : (ValueParser)value;
/*     */   }
/*     */   
/*     */   public abstract T cast(Object paramObject) throws ClassCastException;
/*     */   
/*     */   public abstract T parseValue(String paramString, ClassLoader paramClassLoader) throws IllegalArgumentException;
/*     */   
/*     */   static interface ValueParser<T> {
/*     */     T parseValue(String param1String, ClassLoader param1ClassLoader) throws IllegalArgumentException;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\xnio\Option.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */