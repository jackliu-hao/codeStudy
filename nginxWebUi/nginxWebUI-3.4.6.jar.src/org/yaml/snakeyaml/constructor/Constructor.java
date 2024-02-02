/*     */ package org.yaml.snakeyaml.constructor;
/*     */ 
/*     */ import java.lang.reflect.Constructor;
/*     */ import java.math.BigDecimal;
/*     */ import java.math.BigInteger;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Calendar;
/*     */ import java.util.Collection;
/*     */ import java.util.Date;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import java.util.UUID;
/*     */ import org.yaml.snakeyaml.LoaderOptions;
/*     */ import org.yaml.snakeyaml.TypeDescription;
/*     */ import org.yaml.snakeyaml.error.YAMLException;
/*     */ import org.yaml.snakeyaml.introspector.Property;
/*     */ import org.yaml.snakeyaml.nodes.CollectionNode;
/*     */ import org.yaml.snakeyaml.nodes.MappingNode;
/*     */ import org.yaml.snakeyaml.nodes.Node;
/*     */ import org.yaml.snakeyaml.nodes.NodeId;
/*     */ import org.yaml.snakeyaml.nodes.NodeTuple;
/*     */ import org.yaml.snakeyaml.nodes.ScalarNode;
/*     */ import org.yaml.snakeyaml.nodes.SequenceNode;
/*     */ import org.yaml.snakeyaml.nodes.Tag;
/*     */ import org.yaml.snakeyaml.util.EnumUtils;
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
/*     */ public class Constructor
/*     */   extends SafeConstructor
/*     */ {
/*     */   public Constructor() {
/*  48 */     this(Object.class);
/*     */   }
/*     */   
/*     */   public Constructor(LoaderOptions loadingConfig) {
/*  52 */     this(Object.class, loadingConfig);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Constructor(Class<? extends Object> theRoot) {
/*  62 */     this(new TypeDescription(checkRoot(theRoot)));
/*     */   }
/*     */   
/*     */   public Constructor(Class<? extends Object> theRoot, LoaderOptions loadingConfig) {
/*  66 */     this(new TypeDescription(checkRoot(theRoot)), loadingConfig);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static Class<? extends Object> checkRoot(Class<? extends Object> theRoot) {
/*  73 */     if (theRoot == null) {
/*  74 */       throw new NullPointerException("Root class must be provided.");
/*     */     }
/*  76 */     return theRoot;
/*     */   }
/*     */   
/*     */   public Constructor(TypeDescription theRoot) {
/*  80 */     this(theRoot, (Collection<TypeDescription>)null, new LoaderOptions());
/*     */   }
/*     */   
/*     */   public Constructor(TypeDescription theRoot, LoaderOptions loadingConfig) {
/*  84 */     this(theRoot, (Collection<TypeDescription>)null, loadingConfig);
/*     */   }
/*     */   
/*     */   public Constructor(TypeDescription theRoot, Collection<TypeDescription> moreTDs) {
/*  88 */     this(theRoot, moreTDs, new LoaderOptions());
/*     */   }
/*     */   
/*     */   public Constructor(TypeDescription theRoot, Collection<TypeDescription> moreTDs, LoaderOptions loadingConfig) {
/*  92 */     super(loadingConfig);
/*  93 */     if (theRoot == null) {
/*  94 */       throw new NullPointerException("Root type must be provided.");
/*     */     }
/*  96 */     this.yamlConstructors.put(null, new ConstructYamlObject());
/*  97 */     if (!Object.class.equals(theRoot.getType())) {
/*  98 */       this.rootTag = new Tag(theRoot.getType());
/*     */     }
/* 100 */     this.yamlClassConstructors.put(NodeId.scalar, new ConstructScalar());
/* 101 */     this.yamlClassConstructors.put(NodeId.mapping, new ConstructMapping());
/* 102 */     this.yamlClassConstructors.put(NodeId.sequence, new ConstructSequence());
/* 103 */     addTypeDescription(theRoot);
/* 104 */     if (moreTDs != null) {
/* 105 */       for (TypeDescription td : moreTDs) {
/* 106 */         addTypeDescription(td);
/*     */       }
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
/*     */   
/*     */   public Constructor(String theRoot) throws ClassNotFoundException {
/* 121 */     this((Class)Class.forName(check(theRoot)));
/*     */   }
/*     */   
/*     */   public Constructor(String theRoot, LoaderOptions loadingConfig) throws ClassNotFoundException {
/* 125 */     this((Class)Class.forName(check(theRoot)), loadingConfig);
/*     */   }
/*     */   
/*     */   private static final String check(String s) {
/* 129 */     if (s == null) {
/* 130 */       throw new NullPointerException("Root type must be provided.");
/*     */     }
/* 132 */     if (s.trim().length() == 0) {
/* 133 */       throw new YAMLException("Root type must be provided.");
/*     */     }
/* 135 */     return s;
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
/*     */   protected class ConstructMapping
/*     */     implements Construct
/*     */   {
/*     */     public Object construct(Node node) {
/* 154 */       MappingNode mnode = (MappingNode)node;
/* 155 */       if (Map.class.isAssignableFrom(node.getType())) {
/* 156 */         if (node.isTwoStepsConstruction()) {
/* 157 */           return Constructor.this.newMap(mnode);
/*     */         }
/* 159 */         return Constructor.this.constructMapping(mnode);
/*     */       } 
/* 161 */       if (Collection.class.isAssignableFrom(node.getType())) {
/* 162 */         if (node.isTwoStepsConstruction()) {
/* 163 */           return Constructor.this.newSet((CollectionNode<?>)mnode);
/*     */         }
/* 165 */         return Constructor.this.constructSet(mnode);
/*     */       } 
/*     */       
/* 168 */       Object obj = Constructor.this.newInstance((Node)mnode);
/* 169 */       if (node.isTwoStepsConstruction()) {
/* 170 */         return obj;
/*     */       }
/* 172 */       return constructJavaBean2ndStep(mnode, obj);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public void construct2ndStep(Node node, Object object) {
/* 179 */       if (Map.class.isAssignableFrom(node.getType())) {
/* 180 */         Constructor.this.constructMapping2ndStep((MappingNode)node, (Map<Object, Object>)object);
/* 181 */       } else if (Set.class.isAssignableFrom(node.getType())) {
/* 182 */         Constructor.this.constructSet2ndStep((MappingNode)node, (Set<Object>)object);
/*     */       } else {
/* 184 */         constructJavaBean2ndStep((MappingNode)node, object);
/*     */       } 
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
/*     */     protected Object constructJavaBean2ndStep(MappingNode node, Object object) {
/* 213 */       Constructor.this.flattenMapping(node);
/* 214 */       Class<? extends Object> beanType = node.getType();
/* 215 */       List<NodeTuple> nodeValue = node.getValue();
/* 216 */       for (NodeTuple tuple : nodeValue) {
/*     */         ScalarNode keyNode;
/* 218 */         if (tuple.getKeyNode() instanceof ScalarNode) {
/*     */           
/* 220 */           keyNode = (ScalarNode)tuple.getKeyNode();
/*     */         } else {
/* 222 */           throw new YAMLException("Keys must be scalars but found: " + tuple.getKeyNode());
/*     */         } 
/*     */         
/* 225 */         Node valueNode = tuple.getValueNode();
/*     */         
/* 227 */         keyNode.setType(String.class);
/* 228 */         String key = (String)Constructor.this.constructObject((Node)keyNode);
/*     */         try {
/* 230 */           TypeDescription memberDescription = Constructor.this.typeDefinitions.get(beanType);
/* 231 */           Property property = (memberDescription == null) ? getProperty(beanType, key) : memberDescription.getProperty(key);
/*     */ 
/*     */           
/* 234 */           if (!property.isWritable()) {
/* 235 */             throw new YAMLException("No writable property '" + key + "' on class: " + beanType.getName());
/*     */           }
/*     */ 
/*     */           
/* 239 */           valueNode.setType(property.getType());
/* 240 */           boolean typeDetected = (memberDescription != null) ? memberDescription.setupPropertyType(key, valueNode) : false;
/*     */ 
/*     */           
/* 243 */           if (!typeDetected && valueNode.getNodeId() != NodeId.scalar) {
/*     */             
/* 245 */             Class<?>[] arguments = property.getActualTypeArguments();
/* 246 */             if (arguments != null && arguments.length > 0)
/*     */             {
/*     */               
/* 249 */               if (valueNode.getNodeId() == NodeId.sequence) {
/* 250 */                 Class<?> t = arguments[0];
/* 251 */                 SequenceNode snode = (SequenceNode)valueNode;
/* 252 */                 snode.setListType(t);
/* 253 */               } else if (Set.class.isAssignableFrom(valueNode.getType())) {
/* 254 */                 Class<?> t = arguments[0];
/* 255 */                 MappingNode mnode = (MappingNode)valueNode;
/* 256 */                 mnode.setOnlyKeyType(t);
/* 257 */                 mnode.setUseClassConstructor(Boolean.valueOf(true));
/* 258 */               } else if (Map.class.isAssignableFrom(valueNode.getType())) {
/* 259 */                 Class<?> keyType = arguments[0];
/* 260 */                 Class<?> valueType = arguments[1];
/* 261 */                 MappingNode mnode = (MappingNode)valueNode;
/* 262 */                 mnode.setTypes(keyType, valueType);
/* 263 */                 mnode.setUseClassConstructor(Boolean.valueOf(true));
/*     */               } 
/*     */             }
/*     */           } 
/*     */           
/* 268 */           Object value = (memberDescription != null) ? newInstance(memberDescription, key, valueNode) : Constructor.this.constructObject(valueNode);
/*     */ 
/*     */ 
/*     */ 
/*     */           
/* 273 */           if ((property.getType() == float.class || property.getType() == Float.class) && 
/* 274 */             value instanceof Double) {
/* 275 */             value = Float.valueOf(((Double)value).floatValue());
/*     */           }
/*     */ 
/*     */           
/* 279 */           if (property.getType() == String.class && Tag.BINARY.equals(valueNode.getTag()) && value instanceof byte[])
/*     */           {
/* 281 */             value = new String((byte[])value);
/*     */           }
/*     */           
/* 284 */           if (memberDescription == null || !memberDescription.setProperty(object, key, value))
/*     */           {
/* 286 */             property.set(object, value);
/*     */           }
/* 288 */         } catch (DuplicateKeyException e) {
/* 289 */           throw e;
/* 290 */         } catch (Exception e) {
/* 291 */           throw new ConstructorException("Cannot create property=" + key + " for JavaBean=" + object, node.getStartMark(), e.getMessage(), valueNode.getStartMark(), e);
/*     */         } 
/*     */       } 
/*     */ 
/*     */       
/* 296 */       return object;
/*     */     }
/*     */ 
/*     */     
/*     */     private Object newInstance(TypeDescription memberDescription, String propertyName, Node node) {
/* 301 */       Object newInstance = memberDescription.newInstance(propertyName, node);
/* 302 */       if (newInstance != null) {
/* 303 */         Constructor.this.constructedObjects.put(node, newInstance);
/* 304 */         return Constructor.this.constructObjectNoCheck(node);
/*     */       } 
/* 306 */       return Constructor.this.constructObject(node);
/*     */     }
/*     */     
/*     */     protected Property getProperty(Class<? extends Object> type, String name) {
/* 310 */       return Constructor.this.getPropertyUtils().getProperty(type, name);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected class ConstructYamlObject
/*     */     implements Construct
/*     */   {
/*     */     private Construct getConstructor(Node node) {
/* 323 */       Class<?> cl = Constructor.this.getClassForNode(node);
/* 324 */       node.setType(cl);
/*     */       
/* 326 */       Construct constructor = Constructor.this.yamlClassConstructors.get(node.getNodeId());
/* 327 */       return constructor;
/*     */     }
/*     */     
/*     */     public Object construct(Node node) {
/*     */       try {
/* 332 */         return getConstructor(node).construct(node);
/* 333 */       } catch (ConstructorException e) {
/* 334 */         throw e;
/* 335 */       } catch (Exception e) {
/* 336 */         throw new ConstructorException(null, null, "Can't construct a java object for " + node.getTag() + "; exception=" + e.getMessage(), node.getStartMark(), e);
/*     */       } 
/*     */     }
/*     */ 
/*     */     
/*     */     public void construct2ndStep(Node node, Object object) {
/*     */       try {
/* 343 */         getConstructor(node).construct2ndStep(node, object);
/* 344 */       } catch (Exception e) {
/* 345 */         throw new ConstructorException(null, null, "Can't construct a second step for a java object for " + node.getTag() + "; exception=" + e.getMessage(), node.getStartMark(), e);
/*     */       } 
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected class ConstructScalar
/*     */     extends AbstractConstruct
/*     */   {
/*     */     public Object construct(Node nnode) {
/* 359 */       ScalarNode node = (ScalarNode)nnode;
/* 360 */       Class<?> type = node.getType();
/*     */       
/*     */       try {
/* 363 */         return Constructor.this.newInstance(type, (Node)node, false);
/* 364 */       } catch (InstantiationException e1) {
/*     */         Object result;
/*     */ 
/*     */         
/* 368 */         if (type.isPrimitive() || type == String.class || Number.class.isAssignableFrom(type) || type == Boolean.class || Date.class.isAssignableFrom(type) || type == Character.class || type == BigInteger.class || type == BigDecimal.class || Enum.class.isAssignableFrom(type) || Tag.BINARY.equals(node.getTag()) || Calendar.class.isAssignableFrom(type) || type == UUID.class) {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */           
/* 375 */           result = constructStandardJavaInstance(type, node);
/*     */         } else {
/*     */           Object argument;
/* 378 */           Constructor[] arrayOfConstructor = (Constructor[])type.getDeclaredConstructors();
/*     */           
/* 380 */           int oneArgCount = 0;
/* 381 */           Constructor<?> javaConstructor = null;
/* 382 */           for (Constructor<?> c : arrayOfConstructor) {
/* 383 */             if ((c.getParameterTypes()).length == 1) {
/* 384 */               oneArgCount++;
/* 385 */               javaConstructor = c;
/*     */             } 
/*     */           } 
/*     */           
/* 389 */           if (javaConstructor == null) {
/*     */             try {
/* 391 */               return Constructor.this.newInstance(type, (Node)node, false);
/* 392 */             } catch (InstantiationException ie) {
/* 393 */               throw new YAMLException("No single argument constructor found for " + type + " : " + ie.getMessage());
/*     */             } 
/*     */           }
/* 396 */           if (oneArgCount == 1) {
/* 397 */             argument = constructStandardJavaInstance(javaConstructor.getParameterTypes()[0], node);
/*     */ 
/*     */ 
/*     */           
/*     */           }
/*     */           else {
/*     */ 
/*     */ 
/*     */             
/* 406 */             argument = Constructor.this.constructScalar(node);
/*     */             try {
/* 408 */               javaConstructor = type.getDeclaredConstructor(new Class[] { String.class });
/* 409 */             } catch (Exception e) {
/* 410 */               throw new YAMLException("Can't construct a java object for scalar " + node.getTag() + "; No String constructor found. Exception=" + e.getMessage(), e);
/*     */             } 
/*     */           } 
/*     */ 
/*     */           
/*     */           try {
/* 416 */             javaConstructor.setAccessible(true);
/* 417 */             result = javaConstructor.newInstance(new Object[] { argument });
/* 418 */           } catch (Exception e) {
/* 419 */             throw new ConstructorException(null, null, "Can't construct a java object for scalar " + node.getTag() + "; exception=" + e.getMessage(), node.getStartMark(), e);
/*     */           } 
/*     */         } 
/*     */ 
/*     */ 
/*     */         
/* 425 */         return result;
/*     */       } 
/*     */     }
/*     */ 
/*     */     
/*     */     private Object constructStandardJavaInstance(Class<String> type, ScalarNode node) {
/*     */       Object result;
/* 432 */       if (type == String.class) {
/* 433 */         Construct stringConstructor = Constructor.this.yamlConstructors.get(Tag.STR);
/* 434 */         result = stringConstructor.construct((Node)node);
/* 435 */       } else if (type == Boolean.class || type == boolean.class) {
/* 436 */         Construct boolConstructor = Constructor.this.yamlConstructors.get(Tag.BOOL);
/* 437 */         result = boolConstructor.construct((Node)node);
/* 438 */       } else if (type == Character.class || type == char.class) {
/* 439 */         Construct charConstructor = Constructor.this.yamlConstructors.get(Tag.STR);
/* 440 */         String ch = (String)charConstructor.construct((Node)node);
/* 441 */         if (ch.length() == 0)
/* 442 */         { result = null; }
/* 443 */         else { if (ch.length() != 1) {
/* 444 */             throw new YAMLException("Invalid node Character: '" + ch + "'; length: " + ch.length());
/*     */           }
/*     */           
/* 447 */           result = Character.valueOf(ch.charAt(0)); }
/*     */       
/* 449 */       } else if (Date.class.isAssignableFrom(type)) {
/* 450 */         Construct dateConstructor = Constructor.this.yamlConstructors.get(Tag.TIMESTAMP);
/* 451 */         Date date = (Date)dateConstructor.construct((Node)node);
/* 452 */         if (type == Date.class) {
/* 453 */           result = date;
/*     */         } else {
/*     */           try {
/* 456 */             Constructor<?> constr = type.getConstructor(new Class[] { long.class });
/* 457 */             result = constr.newInstance(new Object[] { Long.valueOf(date.getTime()) });
/* 458 */           } catch (RuntimeException e) {
/* 459 */             throw e;
/* 460 */           } catch (Exception e) {
/* 461 */             throw new YAMLException("Cannot construct: '" + type + "'");
/*     */           } 
/*     */         } 
/* 464 */       } else if (type == Float.class || type == Double.class || type == float.class || type == double.class || type == BigDecimal.class) {
/*     */         
/* 466 */         if (type == BigDecimal.class) {
/* 467 */           result = new BigDecimal(node.getValue());
/*     */         } else {
/* 469 */           Construct doubleConstructor = Constructor.this.yamlConstructors.get(Tag.FLOAT);
/* 470 */           result = doubleConstructor.construct((Node)node);
/* 471 */           if (type == Float.class || type == float.class) {
/* 472 */             result = Float.valueOf(((Double)result).floatValue());
/*     */           }
/*     */         } 
/* 475 */       } else if (type == Byte.class || type == Short.class || type == Integer.class || type == Long.class || type == BigInteger.class || type == byte.class || type == short.class || type == int.class || type == long.class) {
/*     */ 
/*     */         
/* 478 */         Construct intConstructor = Constructor.this.yamlConstructors.get(Tag.INT);
/* 479 */         result = intConstructor.construct((Node)node);
/* 480 */         if (type == Byte.class || type == byte.class) {
/* 481 */           result = Byte.valueOf(Integer.valueOf(result.toString()).byteValue());
/* 482 */         } else if (type == Short.class || type == short.class) {
/* 483 */           result = Short.valueOf(Integer.valueOf(result.toString()).shortValue());
/* 484 */         } else if (type == Integer.class || type == int.class) {
/* 485 */           result = Integer.valueOf(Integer.parseInt(result.toString()));
/* 486 */         } else if (type == Long.class || type == long.class) {
/* 487 */           result = Long.valueOf(result.toString());
/*     */         } else {
/*     */           
/* 490 */           result = new BigInteger(result.toString());
/*     */         } 
/* 492 */       } else if (Enum.class.isAssignableFrom(type)) {
/* 493 */         String enumValueName = node.getValue();
/*     */         try {
/* 495 */           if (Constructor.this.loadingConfig.isEnumCaseSensitive()) {
/* 496 */             result = Enum.valueOf(type, enumValueName);
/*     */           } else {
/* 498 */             result = EnumUtils.findEnumInsensitiveCase(type, enumValueName);
/*     */           } 
/* 500 */         } catch (Exception ex) {
/* 501 */           throw new YAMLException("Unable to find enum value '" + enumValueName + "' for enum class: " + type.getName());
/*     */         }
/*     */       
/* 504 */       } else if (Calendar.class.isAssignableFrom(type)) {
/* 505 */         SafeConstructor.ConstructYamlTimestamp contr = new SafeConstructor.ConstructYamlTimestamp();
/* 506 */         contr.construct((Node)node);
/* 507 */         result = contr.getCalendar();
/* 508 */       } else if (Number.class.isAssignableFrom(type)) {
/*     */         
/* 510 */         SafeConstructor.ConstructYamlFloat contr = new SafeConstructor.ConstructYamlFloat(Constructor.this);
/* 511 */         result = contr.construct((Node)node);
/* 512 */       } else if (UUID.class == type) {
/* 513 */         result = UUID.fromString(node.getValue());
/*     */       }
/* 515 */       else if (Constructor.this.yamlConstructors.containsKey(node.getTag())) {
/* 516 */         result = ((Construct)Constructor.this.yamlConstructors.get(node.getTag())).construct((Node)node);
/*     */       } else {
/* 518 */         throw new YAMLException("Unsupported class: " + type);
/*     */       } 
/*     */       
/* 521 */       return result;
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected class ConstructSequence
/*     */     implements Construct
/*     */   {
/*     */     public Object construct(Node node) {
/* 532 */       SequenceNode snode = (SequenceNode)node;
/* 533 */       if (Set.class.isAssignableFrom(node.getType())) {
/* 534 */         if (node.isTwoStepsConstruction()) {
/* 535 */           throw new YAMLException("Set cannot be recursive.");
/*     */         }
/* 537 */         return Constructor.this.constructSet(snode);
/*     */       } 
/* 539 */       if (Collection.class.isAssignableFrom(node.getType())) {
/* 540 */         if (node.isTwoStepsConstruction()) {
/* 541 */           return Constructor.this.newList(snode);
/*     */         }
/* 543 */         return Constructor.this.constructSequence(snode);
/*     */       } 
/* 545 */       if (node.getType().isArray()) {
/* 546 */         if (node.isTwoStepsConstruction()) {
/* 547 */           return Constructor.this.createArray(node.getType(), snode.getValue().size());
/*     */         }
/* 549 */         return Constructor.this.constructArray(snode);
/*     */       } 
/*     */ 
/*     */       
/* 553 */       List<Constructor<?>> possibleConstructors = new ArrayList<>(snode.getValue().size());
/*     */       
/* 555 */       for (Constructor<?> constructor : node.getType().getDeclaredConstructors()) {
/*     */         
/* 557 */         if (snode.getValue().size() == (constructor.getParameterTypes()).length) {
/* 558 */           possibleConstructors.add(constructor);
/*     */         }
/*     */       } 
/* 561 */       if (!possibleConstructors.isEmpty()) {
/* 562 */         if (possibleConstructors.size() == 1) {
/* 563 */           Object[] arrayOfObject = new Object[snode.getValue().size()];
/* 564 */           Constructor<?> c = possibleConstructors.get(0);
/* 565 */           int i = 0;
/* 566 */           for (Node argumentNode : snode.getValue()) {
/* 567 */             Class<?> type = c.getParameterTypes()[i];
/*     */             
/* 569 */             argumentNode.setType(type);
/* 570 */             arrayOfObject[i++] = Constructor.this.constructObject(argumentNode);
/*     */           } 
/*     */           
/*     */           try {
/* 574 */             c.setAccessible(true);
/* 575 */             return c.newInstance(arrayOfObject);
/* 576 */           } catch (Exception e) {
/* 577 */             throw new YAMLException(e);
/*     */           } 
/*     */         } 
/*     */ 
/*     */         
/* 582 */         List<Object> argumentList = (List)Constructor.this.constructSequence(snode);
/* 583 */         Class<?>[] parameterTypes = new Class[argumentList.size()];
/* 584 */         int index = 0;
/* 585 */         for (Object parameter : argumentList) {
/* 586 */           parameterTypes[index] = parameter.getClass();
/* 587 */           index++;
/*     */         } 
/*     */         
/* 590 */         for (Constructor<?> c : possibleConstructors) {
/* 591 */           Class<?>[] argTypes = c.getParameterTypes();
/* 592 */           boolean foundConstructor = true;
/* 593 */           for (int i = 0; i < argTypes.length; i++) {
/* 594 */             if (!wrapIfPrimitive(argTypes[i]).isAssignableFrom(parameterTypes[i])) {
/* 595 */               foundConstructor = false;
/*     */               break;
/*     */             } 
/*     */           } 
/* 599 */           if (foundConstructor) {
/*     */             try {
/* 601 */               c.setAccessible(true);
/* 602 */               return c.newInstance(argumentList.toArray());
/* 603 */             } catch (Exception e) {
/* 604 */               throw new YAMLException(e);
/*     */             } 
/*     */           }
/*     */         } 
/*     */       } 
/* 609 */       throw new YAMLException("No suitable constructor with " + String.valueOf(snode.getValue().size()) + " arguments found for " + node.getType());
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     private final Class<? extends Object> wrapIfPrimitive(Class<?> clazz) {
/* 617 */       if (!clazz.isPrimitive()) {
/* 618 */         return (Class)clazz;
/*     */       }
/* 620 */       if (clazz == int.class) {
/* 621 */         return (Class)Integer.class;
/*     */       }
/* 623 */       if (clazz == float.class) {
/* 624 */         return (Class)Float.class;
/*     */       }
/* 626 */       if (clazz == double.class) {
/* 627 */         return (Class)Double.class;
/*     */       }
/* 629 */       if (clazz == boolean.class) {
/* 630 */         return (Class)Boolean.class;
/*     */       }
/* 632 */       if (clazz == long.class) {
/* 633 */         return (Class)Long.class;
/*     */       }
/* 635 */       if (clazz == char.class) {
/* 636 */         return (Class)Character.class;
/*     */       }
/* 638 */       if (clazz == short.class) {
/* 639 */         return (Class)Short.class;
/*     */       }
/* 641 */       if (clazz == byte.class) {
/* 642 */         return (Class)Byte.class;
/*     */       }
/* 644 */       throw new YAMLException("Unexpected primitive " + clazz);
/*     */     }
/*     */ 
/*     */     
/*     */     public void construct2ndStep(Node node, Object object) {
/* 649 */       SequenceNode snode = (SequenceNode)node;
/* 650 */       if (List.class.isAssignableFrom(node.getType())) {
/* 651 */         List<Object> list = (List<Object>)object;
/* 652 */         Constructor.this.constructSequenceStep2(snode, list);
/* 653 */       } else if (node.getType().isArray()) {
/* 654 */         Constructor.this.constructArrayStep2(snode, object);
/*     */       } else {
/* 656 */         throw new YAMLException("Immutable objects cannot be recursive.");
/*     */       } 
/*     */     }
/*     */   }
/*     */   
/*     */   protected Class<?> getClassForNode(Node node) {
/* 662 */     Class<? extends Object> classForTag = this.typeTags.get(node.getTag());
/* 663 */     if (classForTag == null) {
/* 664 */       Class<?> cl; String name = node.getTag().getClassName();
/*     */       
/*     */       try {
/* 667 */         cl = getClassForName(name);
/* 668 */       } catch (ClassNotFoundException e) {
/* 669 */         throw new YAMLException("Class not found: " + name);
/*     */       } 
/* 671 */       this.typeTags.put(node.getTag(), cl);
/* 672 */       return cl;
/*     */     } 
/* 674 */     return classForTag;
/*     */   }
/*     */ 
/*     */   
/*     */   protected Class<?> getClassForName(String name) throws ClassNotFoundException {
/*     */     try {
/* 680 */       return Class.forName(name, true, Thread.currentThread().getContextClassLoader());
/* 681 */     } catch (ClassNotFoundException e) {
/* 682 */       return Class.forName(name);
/*     */     } 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\yaml\snakeyaml\constructor\Constructor.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */