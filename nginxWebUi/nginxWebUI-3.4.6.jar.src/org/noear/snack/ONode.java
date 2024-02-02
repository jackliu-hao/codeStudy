/*      */ package org.noear.snack;
/*      */ 
/*      */ import java.lang.reflect.Type;
/*      */ import java.math.BigDecimal;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Arrays;
/*      */ import java.util.Collection;
/*      */ import java.util.Date;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import java.util.Objects;
/*      */ import java.util.function.BiConsumer;
/*      */ import java.util.function.Consumer;
/*      */ import org.noear.snack.core.Context;
/*      */ import org.noear.snack.core.DEFAULTS;
/*      */ import org.noear.snack.core.Feature;
/*      */ import org.noear.snack.core.Handler;
/*      */ import org.noear.snack.core.JsonPath;
/*      */ import org.noear.snack.core.Options;
/*      */ import org.noear.snack.from.Fromer;
/*      */ import org.noear.snack.to.Toer;
/*      */ 
/*      */ public class ONode {
/*      */   protected Options _o;
/*      */   protected ONodeData _d;
/*      */   
/*      */   public static String version() {
/*   28 */     return "3.2";
/*      */   }
/*      */   
/*      */   public ONode() {
/*   32 */     this._o = Options.def();
/*   33 */     this._d = new ONodeData(this);
/*      */   }
/*      */   
/*      */   public ONode(Options options) {
/*   37 */     this._d = new ONodeData(this);
/*      */     
/*   39 */     if (options == null) {
/*   40 */       this._o = Options.def();
/*      */     } else {
/*   42 */       this._o = options;
/*      */     } 
/*      */   }
/*      */   
/*      */   public static ONode newValue() {
/*   47 */     return (new ONode()).asValue();
/*      */   }
/*      */   
/*      */   public static ONode newObject() {
/*   51 */     return (new ONode()).asObject();
/*      */   }
/*      */   
/*      */   public static ONode newArray() {
/*   55 */     return (new ONode()).asArray();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ONode select(String jpath, boolean useStandard, boolean cacheJpath) {
/*   67 */     return JsonPath.eval(this, jpath, useStandard, cacheJpath);
/*      */   }
/*      */   
/*      */   public ONode select(String jpath, boolean useStandard) {
/*   71 */     return select(jpath, useStandard, true);
/*      */   }
/*      */   
/*      */   public ONode select(String jpath) {
/*   75 */     return select(jpath, false);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ONode asObject() {
/*   84 */     this._d.tryInitObject();
/*   85 */     return this;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ONode asArray() {
/*   94 */     this._d.tryInitArray();
/*   95 */     return this;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ONode asValue() {
/*  104 */     this._d.tryInitValue();
/*  105 */     return this;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ONode asNull() {
/*  114 */     this._d.tryInitNull();
/*  115 */     return this;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ONodeData nodeData() {
/*  125 */     return this._d;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ONodeType nodeType() {
/*  135 */     return this._d.nodeType;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ONode options(Options opts) {
/*  145 */     if (opts != null) {
/*  146 */       this._o = opts;
/*      */     }
/*  148 */     return this;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ONode options(Consumer<Options> custom) {
/*  155 */     custom.accept(this._o);
/*  156 */     return this;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Options options() {
/*  163 */     return this._o;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ONode build(Consumer<ONode> custom) {
/*  174 */     custom.accept(this);
/*  175 */     return this;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public OValue val() {
/*  191 */     return (asValue())._d.value;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ONode val(Object val) {
/*  201 */     if (val == null) {
/*  202 */       this._d.tryInitNull();
/*  203 */     } else if (val instanceof ONode) {
/*  204 */       this._d.tryInitNull();
/*  205 */       this._d = ((ONode)val)._d;
/*  206 */     } else if (val instanceof Map || val instanceof Collection) {
/*  207 */       this._d.tryInitNull();
/*  208 */       this._d = (buildVal(val))._d;
/*      */     } else {
/*  210 */       this._d.tryInitValue();
/*  211 */       this._d.value.set(val);
/*      */     } 
/*      */     
/*  214 */     return this;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String getString() {
/*  223 */     if (isValue()) {
/*  224 */       return this._d.value.getString();
/*      */     }
/*  226 */     if (isArray()) {
/*  227 */       return toJson();
/*      */     }
/*      */     
/*  230 */     if (isObject()) {
/*  231 */       return toJson();
/*      */     }
/*      */     
/*  234 */     if (this._o.hasFeature(Feature.StringNullAsEmpty)) {
/*  235 */       return "";
/*      */     }
/*  237 */     return null;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public short getShort() {
/*  247 */     if (isValue()) {
/*  248 */       return this._d.value.getShort();
/*      */     }
/*  250 */     return 0;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int getInt() {
/*  257 */     if (isValue()) {
/*  258 */       return this._d.value.getInt();
/*      */     }
/*  260 */     return 0;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public long getLong() {
/*  267 */     if (isValue()) {
/*  268 */       return this._d.value.getLong();
/*      */     }
/*  270 */     return 0L;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public float getFloat() {
/*  277 */     if (isValue()) {
/*  278 */       return this._d.value.getFloat();
/*      */     }
/*  280 */     return 0.0F;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public double getDouble() {
/*  287 */     if (isValue()) {
/*  288 */       return this._d.value.getDouble();
/*      */     }
/*  290 */     return 0.0D;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public double getDouble(int scale) {
/*  299 */     double temp = getDouble();
/*      */     
/*  301 */     if (temp == 0.0D) {
/*  302 */       return 0.0D;
/*      */     }
/*  304 */     return (new BigDecimal(temp))
/*  305 */       .setScale(scale, 4)
/*  306 */       .doubleValue();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean getBoolean() {
/*  314 */     if (isValue()) {
/*  315 */       return this._d.value.getBoolean();
/*      */     }
/*  317 */     return false;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Date getDate() {
/*  324 */     if (isValue()) {
/*  325 */       return this._d.value.getDate();
/*      */     }
/*  327 */     return null;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public char getChar() {
/*  334 */     if (isValue()) {
/*  335 */       return this._d.value.getChar();
/*      */     }
/*  337 */     return Character.MIN_VALUE;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String getRawString() {
/*  345 */     if (isValue()) {
/*  346 */       return this._d.value.getRawString();
/*      */     }
/*  348 */     return null;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Number getRawNumber() {
/*  357 */     if (isValue()) {
/*  358 */       return this._d.value.getRawNumber();
/*      */     }
/*  360 */     return null;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Boolean getRawBoolean() {
/*  368 */     if (isValue()) {
/*  369 */       return Boolean.valueOf(this._d.value.getRawBoolean());
/*      */     }
/*  371 */     return null;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Date getRawDate() {
/*  379 */     if (isValue()) {
/*  380 */       return this._d.value.getRawDate();
/*      */     }
/*  382 */     return null;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void clear() {
/*  395 */     if (isObject()) {
/*  396 */       this._d.object.clear();
/*  397 */     } else if (isArray()) {
/*  398 */       this._d.array.clear();
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int count() {
/*  406 */     if (isObject()) {
/*  407 */       return this._d.object.size();
/*      */     }
/*      */     
/*  410 */     if (isArray()) {
/*  411 */       return this._d.array.size();
/*      */     }
/*      */     
/*  414 */     return 0;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Map<String, ONode> obj() {
/*  429 */     return (asObject())._d.object;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean contains(String key) {
/*  436 */     if (isObject()) {
/*  437 */       return this._d.object.containsKey(key);
/*      */     }
/*  439 */     return false;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ONode rename(String key, String newKey) {
/*  447 */     if (isObject()) {
/*  448 */       rename_do(this, key, newKey);
/*  449 */     } else if (isArray()) {
/*  450 */       for (ONode n : this._d.array) {
/*  451 */         rename_do(n, key, newKey);
/*      */       }
/*      */     } 
/*      */     
/*  455 */     return this;
/*      */   }
/*      */   
/*      */   private static void rename_do(ONode n, String key, String newKey) {
/*  459 */     if (n.isObject()) {
/*  460 */       ONode tmp = n._d.object.get(key);
/*  461 */       if (tmp != null) {
/*  462 */         n._d.object.put(newKey, tmp);
/*  463 */         n._d.object.remove(key);
/*      */       } 
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ONode get(String key) {
/*  474 */     this._d.tryInitObject();
/*      */     
/*  476 */     ONode tmp = this._d.object.get(key);
/*  477 */     if (tmp == null) {
/*  478 */       return new ONode(this._o);
/*      */     }
/*  480 */     return tmp;
/*      */   }
/*      */ 
/*      */   
/*      */   public ONode getOrNew(String key) {
/*  485 */     this._d.tryInitObject();
/*      */     
/*  487 */     ONode tmp = this._d.object.get(key);
/*  488 */     if (tmp == null) {
/*  489 */       tmp = new ONode(this._o);
/*  490 */       this._d.object.put(key, tmp);
/*      */     } 
/*      */     
/*  493 */     return tmp;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ONode getOrNull(String key) {
/*  502 */     if (isObject()) {
/*  503 */       return this._d.object.get(key);
/*      */     }
/*  505 */     return null;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ONode getNew(String key) {
/*  515 */     ONode tmp = new ONode(this._o);
/*  516 */     this._d.object.put(key, tmp);
/*      */     
/*  518 */     return tmp;
/*      */   }
/*      */   
/*      */   private ONode buildVal(Object val) {
/*  522 */     if (val instanceof Map)
/*  523 */       return (new ONode(this._o)).setAll((Map<String, ?>)val); 
/*  524 */     if (val instanceof Collection) {
/*  525 */       return (new ONode(this._o)).addAll((Collection)val);
/*      */     }
/*      */ 
/*      */     
/*  529 */     if (val != null && val.getClass().isArray()) {
/*  530 */       return (new ONode(this._o)).addAll(Arrays.asList((Object[])val));
/*      */     }
/*  532 */     return (new ONode(this._o)).val(val);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ONode set(String key, Object val) {
/*  543 */     this._d.tryInitObject();
/*      */     
/*  545 */     if (val instanceof ONode) {
/*  546 */       this._d.object.put(key, (ONode)val);
/*      */     } else {
/*  548 */       this._d.object.put(key, buildVal(val));
/*      */     } 
/*      */     
/*  551 */     return this;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ONode setNode(String key, ONode val) {
/*  560 */     this._d.object.put(key, val);
/*  561 */     return this;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ONode setAll(ONode obj) {
/*  571 */     this._d.tryInitObject();
/*      */     
/*  573 */     if (obj != null && obj.isObject()) {
/*  574 */       this._d.object.putAll(obj._d.object);
/*      */     }
/*      */     
/*  577 */     return this;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public <T> ONode setAll(Map<String, T> map) {
/*  586 */     this._d.tryInitObject();
/*      */     
/*  588 */     if (map != null) {
/*  589 */       map.forEach((k, v) -> set(k, v));
/*      */     }
/*      */ 
/*      */     
/*  593 */     return this;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public <T> ONode setAll(Map<String, T> map, BiConsumer<ONode, T> handler) {
/*  602 */     this._d.tryInitObject();
/*      */     
/*  604 */     if (map != null) {
/*  605 */       map.forEach((k, v) -> handler.accept(get(k), v));
/*      */     }
/*      */ 
/*      */     
/*  609 */     return this;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void remove(String key) {
/*  616 */     if (isObject()) {
/*  617 */       this._d.object.remove(key);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public List<ONode> ary() {
/*  633 */     return (asArray())._d.array;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ONode get(int index) {
/*  642 */     this._d.tryInitArray();
/*      */     
/*  644 */     if (index >= 0 && this._d.array.size() > index) {
/*  645 */       return this._d.array.get(index);
/*      */     }
/*      */     
/*  648 */     return new ONode(this._o);
/*      */   }
/*      */   
/*      */   public ONode getOrNew(int index) {
/*  652 */     this._d.tryInitArray();
/*      */     
/*  654 */     if (this._d.array.size() > index) {
/*  655 */       return this._d.array.get(index);
/*      */     }
/*  657 */     ONode n = null;
/*  658 */     for (int i = this._d.array.size(); i <= index; i++) {
/*  659 */       n = new ONode(this._o);
/*  660 */       this._d.array.add(n);
/*      */     } 
/*  662 */     return n;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ONode getOrNull(int index) {
/*  672 */     if (isArray() && 
/*  673 */       index >= 0 && this._d.array.size() > index) {
/*  674 */       return this._d.array.get(index);
/*      */     }
/*      */ 
/*      */     
/*  678 */     return null;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void removeAt(int index) {
/*  685 */     if (isArray()) {
/*  686 */       this._d.array.remove(index);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ONode addNew() {
/*  696 */     this._d.tryInitArray();
/*  697 */     ONode n = new ONode(this._o);
/*  698 */     this._d.array.add(n);
/*  699 */     return n;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ONode add(Object val) {
/*  709 */     this._d.tryInitArray();
/*      */     
/*  711 */     if (val instanceof ONode) {
/*  712 */       this._d.array.add((ONode)val);
/*      */     } else {
/*  714 */       this._d.array.add(buildVal(val));
/*      */     } 
/*      */     
/*  717 */     return this;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ONode addNode(ONode val) {
/*  726 */     this._d.array.add(val);
/*  727 */     return this;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ONode addAll(ONode ary) {
/*  737 */     this._d.tryInitArray();
/*      */     
/*  739 */     if (ary != null && ary.isArray()) {
/*  740 */       this._d.array.addAll(ary._d.array);
/*      */     }
/*      */     
/*  743 */     return this;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public <T> ONode addAll(Collection<T> ary) {
/*  752 */     this._d.tryInitArray();
/*      */     
/*  754 */     if (ary != null) {
/*  755 */       ary.forEach(m -> add(m));
/*      */     }
/*  757 */     return this;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public <T> ONode addAll(Collection<T> ary, BiConsumer<ONode, T> handler) {
/*  766 */     this._d.tryInitArray();
/*      */     
/*  768 */     if (ary != null) {
/*  769 */       ary.forEach(m -> handler.accept(addNew(), m));
/*      */     }
/*  771 */     return this;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean isNull() {
/*  780 */     return (this._d.nodeType == ONodeType.Null || (isValue() && this._d.value.isNull()));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean isValue() {
/*  787 */     return (this._d.nodeType == ONodeType.Value);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean isObject() {
/*  794 */     return (this._d.nodeType == ONodeType.Object);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean isArray() {
/*  801 */     return (this._d.nodeType == ONodeType.Array);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ONode forEach(BiConsumer<String, ONode> consumer) {
/*  811 */     if (isObject()) {
/*  812 */       this._d.object.forEach(consumer);
/*      */     }
/*      */     
/*  815 */     return this;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ONode forEach(Consumer<ONode> consumer) {
/*  822 */     if (isArray()) {
/*  823 */       this._d.array.forEach(consumer);
/*      */     }
/*      */     
/*  826 */     return this;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String attrGet(String key) {
/*  839 */     return this._d.attrGet(key);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ONode attrSet(String key, String val) {
/*  846 */     this._d.attrSet(key, val);
/*  847 */     return this;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ONode attrForeach(BiConsumer<String, String> consumer) {
/*  854 */     if (this._d.attrs != null) {
/*  855 */       this._d.attrs.forEach(consumer);
/*      */     }
/*  857 */     return this;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String toString() {
/*  871 */     return to(DEFAULTS.DEF_STRING_TOER);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String toJson() {
/*  878 */     return to(DEFAULTS.DEF_JSON_TOER);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Object toData() {
/*  885 */     return to(DEFAULTS.DEF_OBJECT_TOER);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public <T> T toObject() {
/*  894 */     return toObject(Object.class);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public <T> T toObject(Type clz) {
/*  905 */     return to(DEFAULTS.DEF_OBJECT_TOER, clz);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public <T> List<T> toObjectList(Class<T> clz) {
/*  917 */     List<T> list = new ArrayList<>();
/*      */     
/*  919 */     for (ONode n : ary()) {
/*  920 */       list.add(n.toObject(clz));
/*      */     }
/*      */     
/*  923 */     return list;
/*      */   }
/*      */   
/*      */   @Deprecated
/*      */   public <T> List<T> toArray(Class<T> clz) {
/*  928 */     return toObjectList(clz);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public <T> T to(Toer toer, Type clz) {
/*  935 */     return (T)((new Context(this._o, this, clz)).handle((Handler)toer)).target;
/*      */   }
/*      */   public <T> T to(Toer toer) {
/*  938 */     return to(toer, null);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public <T> T bindTo(T target) {
/*  945 */     Context ctx = new Context(this._o, this, target.getClass());
/*  946 */     ctx.target = target;
/*  947 */     return (T)(ctx.handle((Handler)DEFAULTS.DEF_OBJECT_TOER)).target;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ONode fill(Object source) {
/*  958 */     val(doLoad(source, source instanceof String, this._o, null));
/*  959 */     return this;
/*      */   }
/*      */   
/*      */   public ONode fill(Object source, Feature... features) {
/*  963 */     val(doLoad(source, source instanceof String, Options.def().add(features), null));
/*  964 */     return this;
/*      */   }
/*      */   
/*      */   public ONode fillObj(Object source, Feature... features) {
/*  968 */     val(doLoad(source, false, Options.def().add(features), null));
/*  969 */     return this;
/*      */   }
/*      */   
/*      */   public ONode fillStr(String source, Feature... features) {
/*  973 */     val(doLoad(source, true, Options.def().add(features), null));
/*  974 */     return this;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static ONode load(Object source) {
/*  990 */     return load(source, null, null);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static ONode load(Object source, Feature... features) {
/*  997 */     return load(source, Options.def().add(features), null);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static ONode load(Object source, Options opts) {
/* 1004 */     return load(source, opts, null);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static ONode load(Object source, Options opts, Fromer fromer) {
/* 1013 */     return doLoad(source, source instanceof String, opts, fromer);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static ONode loadStr(String source) {
/* 1020 */     return doLoad(source, true, null, null);
/*      */   }
/*      */   
/*      */   public static ONode loadStr(String source, Options opts) {
/* 1024 */     return doLoad(source, true, opts, null);
/*      */   }
/*      */   
/*      */   public static ONode loadStr(String source, Feature... features) {
/* 1028 */     return doLoad(source, true, Options.def().add(features), null);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static ONode loadObj(Object source) {
/* 1035 */     return doLoad(source, false, null, null);
/*      */   }
/*      */ 
/*      */   
/*      */   public static ONode loadObj(Object source, Options opts) {
/* 1040 */     return doLoad(source, false, opts, null);
/*      */   }
/*      */   
/*      */   public static ONode loadObj(Object source, Feature... features) {
/* 1044 */     return doLoad(source, false, Options.def().add(features), null);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private static ONode doLoad(Object source, boolean isString, Options opts, Fromer fromer) {
/* 1050 */     if (fromer == null) {
/* 1051 */       if (isString) {
/* 1052 */         fromer = DEFAULTS.DEF_STRING_FROMER;
/*      */       } else {
/* 1054 */         fromer = DEFAULTS.DEF_OBJECT_FROMER;
/*      */       } 
/*      */     }
/*      */     
/* 1058 */     if (opts == null) {
/* 1059 */       opts = Options.def();
/*      */     }
/*      */     
/* 1062 */     return (ONode)((new Context(opts, source)).handle((Handler)fromer)).target;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String stringify(Object source) {
/* 1078 */     return stringify(source, Options.def());
/*      */   }
/*      */   
/*      */   public static String stringify(Object source, Feature... features) {
/* 1082 */     if (features.length > 0) {
/* 1083 */       return stringify(source, new Options(Feature.of(features)));
/*      */     }
/* 1085 */     return stringify(source, Options.def());
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String stringify(Object source, Options opts) {
/* 1098 */     return load(source, opts, DEFAULTS.DEF_OBJECT_FROMER).toString();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String serialize(Object source) {
/* 1115 */     return load(source, Options.serialize(), DEFAULTS.DEF_OBJECT_FROMER).toJson();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static <T> T deserialize(String source) {
/* 1125 */     return deserialize(source, Object.class);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static <T> T deserialize(String source, Type clz) {
/* 1136 */     return load(source, Options.serialize(), null).toObject(clz);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean equals(Object o) {
/* 1142 */     if (this == o) {
/* 1143 */       return true;
/*      */     }
/*      */     
/* 1146 */     if (o == null) {
/* 1147 */       return isNull();
/*      */     }
/*      */     
/* 1150 */     if (isArray()) {
/* 1151 */       if (o instanceof ONode) {
/* 1152 */         return Objects.equals(ary(), ((ONode)o).ary());
/*      */       }
/* 1154 */       return Objects.equals(ary(), o);
/*      */     } 
/*      */ 
/*      */     
/* 1158 */     if (isObject()) {
/* 1159 */       if (o instanceof ONode) {
/* 1160 */         return Objects.equals(obj(), ((ONode)o).obj());
/*      */       }
/* 1162 */       return Objects.equals(obj(), o);
/*      */     } 
/*      */ 
/*      */     
/* 1166 */     if (isValue()) {
/* 1167 */       if (o instanceof ONode) {
/* 1168 */         return Objects.equals(val(), ((ONode)o).val());
/*      */       }
/* 1170 */       return Objects.equals(val(), o);
/*      */     } 
/*      */ 
/*      */ 
/*      */     
/* 1175 */     if (o instanceof ONode) {
/* 1176 */       return ((ONode)o).isNull();
/*      */     }
/* 1178 */     return false;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public int hashCode() {
/* 1184 */     return this._d.hashCode();
/*      */   }
/*      */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\snack\ONode.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */