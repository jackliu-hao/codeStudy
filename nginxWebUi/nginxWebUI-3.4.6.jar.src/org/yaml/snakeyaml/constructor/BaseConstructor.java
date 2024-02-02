/*     */ package org.yaml.snakeyaml.constructor;
/*     */ 
/*     */ import java.lang.reflect.Array;
/*     */ import java.lang.reflect.Constructor;
/*     */ import java.lang.reflect.Modifier;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.EnumMap;
/*     */ import java.util.HashMap;
/*     */ import java.util.HashSet;
/*     */ import java.util.LinkedHashMap;
/*     */ import java.util.LinkedHashSet;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.NoSuchElementException;
/*     */ import java.util.Set;
/*     */ import java.util.SortedMap;
/*     */ import java.util.SortedSet;
/*     */ import java.util.TreeMap;
/*     */ import java.util.TreeSet;
/*     */ import org.yaml.snakeyaml.LoaderOptions;
/*     */ import org.yaml.snakeyaml.TypeDescription;
/*     */ import org.yaml.snakeyaml.composer.Composer;
/*     */ import org.yaml.snakeyaml.error.YAMLException;
/*     */ import org.yaml.snakeyaml.introspector.PropertyUtils;
/*     */ import org.yaml.snakeyaml.nodes.CollectionNode;
/*     */ import org.yaml.snakeyaml.nodes.MappingNode;
/*     */ import org.yaml.snakeyaml.nodes.Node;
/*     */ import org.yaml.snakeyaml.nodes.NodeId;
/*     */ import org.yaml.snakeyaml.nodes.NodeTuple;
/*     */ import org.yaml.snakeyaml.nodes.ScalarNode;
/*     */ import org.yaml.snakeyaml.nodes.SequenceNode;
/*     */ import org.yaml.snakeyaml.nodes.Tag;
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
/*     */ public abstract class BaseConstructor
/*     */ {
/*  56 */   protected final Map<NodeId, Construct> yamlClassConstructors = new EnumMap<>(NodeId.class);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  65 */   protected final Map<Tag, Construct> yamlConstructors = new HashMap<>();
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  70 */   protected final Map<String, Construct> yamlMultiConstructors = new HashMap<>();
/*     */   
/*     */   protected Composer composer;
/*     */   
/*     */   final Map<Node, Object> constructedObjects;
/*     */   
/*     */   private final Set<Node> recursiveObjects;
/*     */   
/*     */   private final ArrayList<RecursiveTuple<Map<Object, Object>, RecursiveTuple<Object, Object>>> maps2fill;
/*     */   
/*     */   private final ArrayList<RecursiveTuple<Set<Object>, Object>> sets2fill;
/*     */   protected Tag rootTag;
/*     */   private PropertyUtils propertyUtils;
/*     */   private boolean explicitPropertyUtils;
/*     */   private boolean allowDuplicateKeys = true;
/*     */   private boolean wrappedToRootException = false;
/*     */   private boolean enumCaseSensitive = false;
/*     */   protected final Map<Class<? extends Object>, TypeDescription> typeDefinitions;
/*     */   protected final Map<Tag, Class<? extends Object>> typeTags;
/*     */   protected LoaderOptions loadingConfig;
/*     */   
/*     */   public BaseConstructor() {
/*  92 */     this(new LoaderOptions());
/*     */   }
/*     */   
/*     */   public BaseConstructor(LoaderOptions loadingConfig) {
/*  96 */     this.constructedObjects = new HashMap<>();
/*  97 */     this.recursiveObjects = new HashSet<>();
/*  98 */     this.maps2fill = new ArrayList<>();
/*  99 */     this.sets2fill = new ArrayList<>();
/* 100 */     this.typeDefinitions = new HashMap<>();
/* 101 */     this.typeTags = new HashMap<>();
/*     */     
/* 103 */     this.rootTag = null;
/* 104 */     this.explicitPropertyUtils = false;
/*     */     
/* 106 */     this.typeDefinitions.put(SortedMap.class, new TypeDescription(SortedMap.class, Tag.OMAP, TreeMap.class));
/*     */     
/* 108 */     this.typeDefinitions.put(SortedSet.class, new TypeDescription(SortedSet.class, Tag.SET, TreeSet.class));
/*     */     
/* 110 */     this.loadingConfig = loadingConfig;
/*     */   }
/*     */   
/*     */   public void setComposer(Composer composer) {
/* 114 */     this.composer = composer;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean checkData() {
/* 124 */     return this.composer.checkNode();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Object getData() throws NoSuchElementException {
/* 134 */     if (!this.composer.checkNode()) throw new NoSuchElementException("No document is available."); 
/* 135 */     Node node = this.composer.getNode();
/* 136 */     if (this.rootTag != null) {
/* 137 */       node.setTag(this.rootTag);
/*     */     }
/* 139 */     return constructDocument(node);
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
/*     */   public Object getSingleData(Class<?> type) {
/* 151 */     Node node = this.composer.getSingleNode();
/* 152 */     if (node != null && !Tag.NULL.equals(node.getTag())) {
/* 153 */       if (Object.class != type) {
/* 154 */         node.setTag(new Tag(type));
/* 155 */       } else if (this.rootTag != null) {
/* 156 */         node.setTag(this.rootTag);
/*     */       } 
/* 158 */       return constructDocument(node);
/*     */     } 
/* 160 */     Construct construct = this.yamlConstructors.get(Tag.NULL);
/* 161 */     return construct.construct(node);
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
/*     */   protected final Object constructDocument(Node node) {
/*     */     try {
/* 174 */       Object data = constructObject(node);
/* 175 */       fillRecursive();
/* 176 */       return data;
/* 177 */     } catch (RuntimeException e) {
/* 178 */       if (this.wrappedToRootException && !(e instanceof YAMLException)) {
/* 179 */         throw new YAMLException(e);
/*     */       }
/* 181 */       throw e;
/*     */     }
/*     */     finally {
/*     */       
/* 185 */       this.constructedObjects.clear();
/* 186 */       this.recursiveObjects.clear();
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void fillRecursive() {
/* 194 */     if (!this.maps2fill.isEmpty()) {
/* 195 */       for (RecursiveTuple<Map<Object, Object>, RecursiveTuple<Object, Object>> entry : this.maps2fill) {
/* 196 */         RecursiveTuple<Object, Object> key_value = entry._2();
/* 197 */         ((Map)entry._1()).put(key_value._1(), key_value._2());
/*     */       } 
/* 199 */       this.maps2fill.clear();
/*     */     } 
/* 201 */     if (!this.sets2fill.isEmpty()) {
/* 202 */       for (RecursiveTuple<Set<Object>, Object> value : this.sets2fill) {
/* 203 */         ((Set)value._1()).add(value._2());
/*     */       }
/* 205 */       this.sets2fill.clear();
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
/*     */   protected Object constructObject(Node node) {
/* 217 */     if (this.constructedObjects.containsKey(node)) {
/* 218 */       return this.constructedObjects.get(node);
/*     */     }
/* 220 */     return constructObjectNoCheck(node);
/*     */   }
/*     */   
/*     */   protected Object constructObjectNoCheck(Node node) {
/* 224 */     if (this.recursiveObjects.contains(node)) {
/* 225 */       throw new ConstructorException(null, null, "found unconstructable recursive node", node.getStartMark());
/*     */     }
/*     */     
/* 228 */     this.recursiveObjects.add(node);
/* 229 */     Construct constructor = getConstructor(node);
/* 230 */     Object data = this.constructedObjects.containsKey(node) ? this.constructedObjects.get(node) : constructor.construct(node);
/*     */ 
/*     */     
/* 233 */     finalizeConstruction(node, data);
/* 234 */     this.constructedObjects.put(node, data);
/* 235 */     this.recursiveObjects.remove(node);
/* 236 */     if (node.isTwoStepsConstruction()) {
/* 237 */       constructor.construct2ndStep(node, data);
/*     */     }
/* 239 */     return data;
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
/*     */   protected Construct getConstructor(Node node) {
/* 251 */     if (node.useClassConstructor()) {
/* 252 */       return this.yamlClassConstructors.get(node.getNodeId());
/*     */     }
/* 254 */     Construct constructor = this.yamlConstructors.get(node.getTag());
/* 255 */     if (constructor == null) {
/* 256 */       for (String prefix : this.yamlMultiConstructors.keySet()) {
/* 257 */         if (node.getTag().startsWith(prefix)) {
/* 258 */           return this.yamlMultiConstructors.get(prefix);
/*     */         }
/*     */       } 
/* 261 */       return this.yamlConstructors.get(null);
/*     */     } 
/* 263 */     return constructor;
/*     */   }
/*     */ 
/*     */   
/*     */   protected String constructScalar(ScalarNode node) {
/* 268 */     return node.getValue();
/*     */   }
/*     */ 
/*     */   
/*     */   protected List<Object> createDefaultList(int initSize) {
/* 273 */     return new ArrayList(initSize);
/*     */   }
/*     */   
/*     */   protected Set<Object> createDefaultSet(int initSize) {
/* 277 */     return new LinkedHashSet(initSize);
/*     */   }
/*     */ 
/*     */   
/*     */   protected Map<Object, Object> createDefaultMap(int initSize) {
/* 282 */     return new LinkedHashMap<>(initSize);
/*     */   }
/*     */   
/*     */   protected Object createArray(Class<?> type, int size) {
/* 286 */     return Array.newInstance(type.getComponentType(), size);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   protected Object finalizeConstruction(Node node, Object data) {
/* 292 */     Class<? extends Object> type = node.getType();
/* 293 */     if (this.typeDefinitions.containsKey(type)) {
/* 294 */       return ((TypeDescription)this.typeDefinitions.get(type)).finalizeConstruction(data);
/*     */     }
/* 296 */     return data;
/*     */   }
/*     */ 
/*     */   
/*     */   protected Object newInstance(Node node) {
/*     */     try {
/* 302 */       return newInstance(Object.class, node);
/* 303 */     } catch (InstantiationException e) {
/* 304 */       throw new YAMLException(e);
/*     */     } 
/*     */   }
/*     */   
/*     */   protected final Object newInstance(Class<?> ancestor, Node node) throws InstantiationException {
/* 309 */     return newInstance(ancestor, node, true);
/*     */   }
/*     */ 
/*     */   
/*     */   protected Object newInstance(Class<?> ancestor, Node node, boolean tryDefault) throws InstantiationException {
/* 314 */     Class<? extends Object> type = node.getType();
/* 315 */     if (this.typeDefinitions.containsKey(type)) {
/* 316 */       TypeDescription td = this.typeDefinitions.get(type);
/* 317 */       Object instance = td.newInstance(node);
/* 318 */       if (instance != null) {
/* 319 */         return instance;
/*     */       }
/*     */     } 
/* 322 */     if (tryDefault)
/*     */     {
/*     */ 
/*     */ 
/*     */       
/* 327 */       if (ancestor.isAssignableFrom(type) && !Modifier.isAbstract(type.getModifiers())) {
/*     */         try {
/* 329 */           Constructor<?> c = type.getDeclaredConstructor(new Class[0]);
/* 330 */           c.setAccessible(true);
/* 331 */           return c.newInstance(new Object[0]);
/* 332 */         } catch (NoSuchMethodException e) {
/* 333 */           throw new InstantiationException("NoSuchMethodException:" + e.getLocalizedMessage());
/*     */         }
/* 335 */         catch (Exception e) {
/* 336 */           throw new YAMLException(e);
/*     */         } 
/*     */       }
/*     */     }
/* 340 */     throw new InstantiationException();
/*     */   }
/*     */ 
/*     */   
/*     */   protected Set<Object> newSet(CollectionNode<?> node) {
/*     */     try {
/* 346 */       return (Set<Object>)newInstance(Set.class, (Node)node);
/* 347 */     } catch (InstantiationException e) {
/* 348 */       return createDefaultSet(node.getValue().size());
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   protected List<Object> newList(SequenceNode node) {
/*     */     try {
/* 355 */       return (List<Object>)newInstance(List.class, (Node)node);
/* 356 */     } catch (InstantiationException e) {
/* 357 */       return createDefaultList(node.getValue().size());
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   protected Map<Object, Object> newMap(MappingNode node) {
/*     */     try {
/* 364 */       return (Map<Object, Object>)newInstance(Map.class, (Node)node);
/* 365 */     } catch (InstantiationException e) {
/* 366 */       return createDefaultMap(node.getValue().size());
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected List<? extends Object> constructSequence(SequenceNode node) {
/* 374 */     List<Object> result = newList(node);
/* 375 */     constructSequenceStep2(node, result);
/* 376 */     return result;
/*     */   }
/*     */   
/*     */   protected Set<? extends Object> constructSet(SequenceNode node) {
/* 380 */     Set<Object> result = newSet((CollectionNode<?>)node);
/* 381 */     constructSequenceStep2(node, result);
/* 382 */     return result;
/*     */   }
/*     */   
/*     */   protected Object constructArray(SequenceNode node) {
/* 386 */     return constructArrayStep2(node, createArray(node.getType(), node.getValue().size()));
/*     */   }
/*     */   
/*     */   protected void constructSequenceStep2(SequenceNode node, Collection<Object> collection) {
/* 390 */     for (Node child : node.getValue()) {
/* 391 */       collection.add(constructObject(child));
/*     */     }
/*     */   }
/*     */   
/*     */   protected Object constructArrayStep2(SequenceNode node, Object array) {
/* 396 */     Class<?> componentType = node.getType().getComponentType();
/*     */     
/* 398 */     int index = 0;
/* 399 */     for (Node child : node.getValue()) {
/*     */       
/* 401 */       if (child.getType() == Object.class) {
/* 402 */         child.setType(componentType);
/*     */       }
/*     */       
/* 405 */       Object value = constructObject(child);
/*     */       
/* 407 */       if (componentType.isPrimitive()) {
/*     */         
/* 409 */         if (value == null) {
/* 410 */           throw new NullPointerException("Unable to construct element value for " + child);
/*     */         }
/*     */ 
/*     */ 
/*     */         
/* 415 */         if (byte.class.equals(componentType)) {
/* 416 */           Array.setByte(array, index, ((Number)value).byteValue());
/*     */         }
/* 418 */         else if (short.class.equals(componentType)) {
/* 419 */           Array.setShort(array, index, ((Number)value).shortValue());
/*     */         }
/* 421 */         else if (int.class.equals(componentType)) {
/* 422 */           Array.setInt(array, index, ((Number)value).intValue());
/*     */         }
/* 424 */         else if (long.class.equals(componentType)) {
/* 425 */           Array.setLong(array, index, ((Number)value).longValue());
/*     */         }
/* 427 */         else if (float.class.equals(componentType)) {
/* 428 */           Array.setFloat(array, index, ((Number)value).floatValue());
/*     */         }
/* 430 */         else if (double.class.equals(componentType)) {
/* 431 */           Array.setDouble(array, index, ((Number)value).doubleValue());
/*     */         }
/* 433 */         else if (char.class.equals(componentType)) {
/* 434 */           Array.setChar(array, index, ((Character)value).charValue());
/*     */         }
/* 436 */         else if (boolean.class.equals(componentType)) {
/* 437 */           Array.setBoolean(array, index, ((Boolean)value).booleanValue());
/*     */         } else {
/*     */           
/* 440 */           throw new YAMLException("unexpected primitive type");
/*     */         }
/*     */       
/*     */       } else {
/*     */         
/* 445 */         Array.set(array, index, value);
/*     */       } 
/*     */       
/* 448 */       index++;
/*     */     } 
/* 450 */     return array;
/*     */   }
/*     */   
/*     */   protected Set<Object> constructSet(MappingNode node) {
/* 454 */     Set<Object> set = newSet((CollectionNode<?>)node);
/* 455 */     constructSet2ndStep(node, set);
/* 456 */     return set;
/*     */   }
/*     */   
/*     */   protected Map<Object, Object> constructMapping(MappingNode node) {
/* 460 */     Map<Object, Object> mapping = newMap(node);
/* 461 */     constructMapping2ndStep(node, mapping);
/* 462 */     return mapping;
/*     */   }
/*     */   
/*     */   protected void constructMapping2ndStep(MappingNode node, Map<Object, Object> mapping) {
/* 466 */     List<NodeTuple> nodeValue = node.getValue();
/* 467 */     for (NodeTuple tuple : nodeValue) {
/* 468 */       Node keyNode = tuple.getKeyNode();
/* 469 */       Node valueNode = tuple.getValueNode();
/* 470 */       Object key = constructObject(keyNode);
/* 471 */       if (key != null) {
/*     */         try {
/* 473 */           key.hashCode();
/* 474 */         } catch (Exception e) {
/* 475 */           throw new ConstructorException("while constructing a mapping", node.getStartMark(), "found unacceptable key " + key, tuple.getKeyNode().getStartMark(), e);
/*     */         } 
/*     */       }
/*     */ 
/*     */       
/* 480 */       Object value = constructObject(valueNode);
/* 481 */       if (keyNode.isTwoStepsConstruction()) {
/* 482 */         if (this.loadingConfig.getAllowRecursiveKeys()) {
/* 483 */           postponeMapFilling(mapping, key, value); continue;
/*     */         } 
/* 485 */         throw new YAMLException("Recursive key for mapping is detected but it is not configured to be allowed.");
/*     */       } 
/*     */       
/* 488 */       mapping.put(key, value);
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
/*     */   protected void postponeMapFilling(Map<Object, Object> mapping, Object key, Object value) {
/* 500 */     this.maps2fill.add(0, new RecursiveTuple<>(mapping, new RecursiveTuple<>(key, value)));
/*     */   }
/*     */   
/*     */   protected void constructSet2ndStep(MappingNode node, Set<Object> set) {
/* 504 */     List<NodeTuple> nodeValue = node.getValue();
/* 505 */     for (NodeTuple tuple : nodeValue) {
/* 506 */       Node keyNode = tuple.getKeyNode();
/* 507 */       Object key = constructObject(keyNode);
/* 508 */       if (key != null) {
/*     */         try {
/* 510 */           key.hashCode();
/* 511 */         } catch (Exception e) {
/* 512 */           throw new ConstructorException("while constructing a Set", node.getStartMark(), "found unacceptable key " + key, tuple.getKeyNode().getStartMark(), e);
/*     */         } 
/*     */       }
/*     */       
/* 516 */       if (keyNode.isTwoStepsConstruction()) {
/* 517 */         postponeSetFilling(set, key); continue;
/*     */       } 
/* 519 */       set.add(key);
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
/*     */   protected void postponeSetFilling(Set<Object> set, Object key) {
/* 531 */     this.sets2fill.add(0, new RecursiveTuple<>(set, key));
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
/*     */   public void setPropertyUtils(PropertyUtils propertyUtils) {
/* 549 */     this.propertyUtils = propertyUtils;
/* 550 */     this.explicitPropertyUtils = true;
/* 551 */     Collection<TypeDescription> tds = this.typeDefinitions.values();
/* 552 */     for (TypeDescription typeDescription : tds) {
/* 553 */       typeDescription.setPropertyUtils(propertyUtils);
/*     */     }
/*     */   }
/*     */   
/*     */   public final PropertyUtils getPropertyUtils() {
/* 558 */     if (this.propertyUtils == null) {
/* 559 */       this.propertyUtils = new PropertyUtils();
/*     */     }
/* 561 */     return this.propertyUtils;
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
/*     */   public TypeDescription addTypeDescription(TypeDescription definition) {
/* 574 */     if (definition == null) {
/* 575 */       throw new NullPointerException("TypeDescription is required.");
/*     */     }
/* 577 */     Tag tag = definition.getTag();
/* 578 */     this.typeTags.put(tag, definition.getType());
/* 579 */     definition.setPropertyUtils(getPropertyUtils());
/* 580 */     return this.typeDefinitions.put(definition.getType(), definition);
/*     */   }
/*     */   
/*     */   private static class RecursiveTuple<T, K> {
/*     */     private final T _1;
/*     */     private final K _2;
/*     */     
/*     */     public RecursiveTuple(T _1, K _2) {
/* 588 */       this._1 = _1;
/* 589 */       this._2 = _2;
/*     */     }
/*     */     
/*     */     public K _2() {
/* 593 */       return this._2;
/*     */     }
/*     */     
/*     */     public T _1() {
/* 597 */       return this._1;
/*     */     }
/*     */   }
/*     */   
/*     */   public final boolean isExplicitPropertyUtils() {
/* 602 */     return this.explicitPropertyUtils;
/*     */   }
/*     */   
/*     */   public boolean isAllowDuplicateKeys() {
/* 606 */     return this.allowDuplicateKeys;
/*     */   }
/*     */   
/*     */   public void setAllowDuplicateKeys(boolean allowDuplicateKeys) {
/* 610 */     this.allowDuplicateKeys = allowDuplicateKeys;
/*     */   }
/*     */   
/*     */   public boolean isWrappedToRootException() {
/* 614 */     return this.wrappedToRootException;
/*     */   }
/*     */   
/*     */   public void setWrappedToRootException(boolean wrappedToRootException) {
/* 618 */     this.wrappedToRootException = wrappedToRootException;
/*     */   }
/*     */   
/*     */   public boolean isEnumCaseSensitive() {
/* 622 */     return this.enumCaseSensitive;
/*     */   }
/*     */   
/*     */   public void setEnumCaseSensitive(boolean enumCaseSensitive) {
/* 626 */     this.enumCaseSensitive = enumCaseSensitive;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\yaml\snakeyaml\constructor\BaseConstructor.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */