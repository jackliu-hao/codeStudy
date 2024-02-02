/*     */ package com.zaxxer.hikari.util;
/*     */ 
/*     */ import com.zaxxer.hikari.HikariConfig;
/*     */ import java.lang.reflect.Method;
/*     */ import java.util.Arrays;
/*     */ import java.util.HashSet;
/*     */ import java.util.List;
/*     */ import java.util.Locale;
/*     */ import java.util.Properties;
/*     */ import java.util.Set;
/*     */ import java.util.regex.Matcher;
/*     */ import java.util.regex.Pattern;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
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
/*     */ public final class PropertyElf
/*     */ {
/*  41 */   private static final Pattern GETTER_PATTERN = Pattern.compile("(get|is)[A-Z].+");
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void setTargetFromProperties(Object target, Properties properties) {
/*  49 */     if (target == null || properties == null) {
/*     */       return;
/*     */     }
/*     */     
/*  53 */     List<Method> methods = Arrays.asList(target.getClass().getMethods());
/*  54 */     properties.forEach((key, value) -> {
/*     */           if (target instanceof HikariConfig && key.toString().startsWith("dataSource.")) {
/*     */             ((HikariConfig)target).addDataSourceProperty(key.toString().substring("dataSource.".length()), value);
/*     */           } else {
/*     */             setProperty(target, key.toString(), value, methods);
/*     */           } 
/*     */         });
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
/*     */   public static Set<String> getPropertyNames(Class<?> targetClass) {
/*  72 */     HashSet<String> set = new HashSet<>();
/*  73 */     Matcher matcher = GETTER_PATTERN.matcher("");
/*  74 */     for (Method method : targetClass.getMethods()) {
/*  75 */       String name = method.getName();
/*  76 */       if ((method.getParameterTypes()).length == 0 && matcher.reset(name).matches()) {
/*  77 */         name = name.replaceFirst("(get|is)", "");
/*     */         try {
/*  79 */           if (targetClass.getMethod("set" + name, new Class[] { method.getReturnType() }) != null) {
/*  80 */             name = Character.toLowerCase(name.charAt(0)) + name.substring(1);
/*  81 */             set.add(name);
/*     */           }
/*     */         
/*  84 */         } catch (Exception exception) {}
/*     */       } 
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/*  90 */     return set;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public static Object getProperty(String propName, Object target) {
/*     */     try {
/*  97 */       String capitalized = "get" + propName.substring(0, 1).toUpperCase(Locale.ENGLISH) + propName.substring(1);
/*  98 */       Method method = target.getClass().getMethod(capitalized, new Class[0]);
/*  99 */       return method.invoke(target, new Object[0]);
/*     */     }
/* 101 */     catch (Exception e) {
/*     */       try {
/* 103 */         String capitalized = "is" + propName.substring(0, 1).toUpperCase(Locale.ENGLISH) + propName.substring(1);
/* 104 */         Method method = target.getClass().getMethod(capitalized, new Class[0]);
/* 105 */         return method.invoke(target, new Object[0]);
/*     */       }
/* 107 */       catch (Exception e2) {
/* 108 */         return null;
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public static Properties copyProperties(Properties props) {
/* 115 */     Properties copy = new Properties();
/* 116 */     props.forEach((key, value) -> copy.setProperty(key.toString(), value.toString()));
/* 117 */     return copy;
/*     */   }
/*     */ 
/*     */   
/*     */   private static void setProperty(Object target, String propName, Object propValue, List<Method> methods) {
/* 122 */     Logger logger = LoggerFactory.getLogger(PropertyElf.class);
/*     */ 
/*     */     
/* 125 */     String methodName = "set" + propName.substring(0, 1).toUpperCase(Locale.ENGLISH) + propName.substring(1);
/* 126 */     Method writeMethod = methods.stream().filter(m -> (m.getName().equals(methodName) && m.getParameterCount() == 1)).findFirst().orElse(null);
/*     */     
/* 128 */     if (writeMethod == null) {
/* 129 */       String methodName2 = "set" + propName.toUpperCase(Locale.ENGLISH);
/* 130 */       writeMethod = methods.stream().filter(m -> (m.getName().equals(methodName2) && m.getParameterCount() == 1)).findFirst().orElse(null);
/*     */     } 
/*     */     
/* 133 */     if (writeMethod == null) {
/* 134 */       logger.error("Property {} does not exist on target {}", propName, target.getClass());
/* 135 */       throw new RuntimeException(String.format("Property %s does not exist on target %s", new Object[] { propName, target.getClass() }));
/*     */     } 
/*     */     
/*     */     try {
/* 139 */       Class<?> paramClass = writeMethod.getParameterTypes()[0];
/* 140 */       if (paramClass == int.class) {
/* 141 */         writeMethod.invoke(target, new Object[] { Integer.valueOf(Integer.parseInt(propValue.toString())) });
/*     */       }
/* 143 */       else if (paramClass == long.class) {
/* 144 */         writeMethod.invoke(target, new Object[] { Long.valueOf(Long.parseLong(propValue.toString())) });
/*     */       }
/* 146 */       else if (paramClass == short.class) {
/* 147 */         writeMethod.invoke(target, new Object[] { Short.valueOf(Short.parseShort(propValue.toString())) });
/*     */       }
/* 149 */       else if (paramClass == boolean.class || paramClass == Boolean.class) {
/* 150 */         writeMethod.invoke(target, new Object[] { Boolean.valueOf(Boolean.parseBoolean(propValue.toString())) });
/*     */       }
/* 152 */       else if (paramClass == String.class) {
/* 153 */         writeMethod.invoke(target, new Object[] { propValue.toString() });
/*     */       } else {
/*     */         
/*     */         try {
/* 157 */           logger.debug("Try to create a new instance of \"{}\"", propValue);
/* 158 */           writeMethod.invoke(target, new Object[] { Class.forName(propValue.toString()).getDeclaredConstructor(new Class[0]).newInstance(new Object[0]) });
/*     */         }
/* 160 */         catch (InstantiationException|ClassNotFoundException e) {
/* 161 */           logger.debug("Class \"{}\" not found or could not instantiate it (Default constructor)", propValue);
/* 162 */           writeMethod.invoke(target, new Object[] { propValue });
/*     */         }
/*     */       
/*     */       } 
/* 166 */     } catch (Exception e) {
/* 167 */       logger.error("Failed to set property {} on target {}", new Object[] { propName, target.getClass(), e });
/* 168 */       throw new RuntimeException(e);
/*     */     } 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\zaxxer\hikar\\util\PropertyElf.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */