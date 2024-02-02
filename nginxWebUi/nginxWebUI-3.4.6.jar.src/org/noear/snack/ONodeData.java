/*     */ package org.noear.snack;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.LinkedHashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import org.noear.snack.core.Feature;
/*     */ 
/*     */ 
/*     */ 
/*     */ public class ONodeData
/*     */ {
/*  14 */   public OValue value = null;
/*     */   
/*  16 */   public Map<String, ONode> object = null;
/*     */   
/*  18 */   public List<ONode> array = null;
/*     */ 
/*     */   
/*  21 */   public ONodeType nodeType = ONodeType.Null;
/*     */ 
/*     */   
/*     */   protected ONode _n;
/*     */   
/*     */   public Map<String, String> attrs;
/*     */ 
/*     */   
/*     */   public Map<String, ONode> object() {
/*  30 */     tryInitObject();
/*  31 */     return this.object;
/*     */   }
/*     */   
/*     */   public List<ONode> array() {
/*  35 */     tryInitArray();
/*  36 */     return this.array;
/*     */   }
/*     */   
/*     */   public OValue value() {
/*  40 */     tryInitValue();
/*  41 */     return this.value;
/*     */   }
/*     */ 
/*     */   
/*     */   protected void tryInitNull() {
/*  46 */     if (this.nodeType != ONodeType.Null) {
/*  47 */       this.nodeType = ONodeType.Null;
/*     */       
/*  49 */       if (this.object != null) {
/*  50 */         this.object.clear();
/*  51 */         this.object = null;
/*     */       } 
/*     */       
/*  54 */       if (this.array != null) {
/*  55 */         this.array.clear();
/*  56 */         this.array = null;
/*     */       } 
/*     */       
/*  59 */       this.value = null;
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   protected void tryInitValue() {
/*  65 */     if (this.nodeType != ONodeType.Value) {
/*  66 */       this.nodeType = ONodeType.Value;
/*     */       
/*  68 */       if (this.value == null) {
/*  69 */         this.value = new OValue(this._n);
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   protected void tryInitObject() {
/*  76 */     if (this.nodeType != ONodeType.Object) {
/*  77 */       this.nodeType = ONodeType.Object;
/*     */       
/*  79 */       if (this.object == null) {
/*  80 */         if (this._n._o.hasFeature(Feature.OrderedField)) {
/*  81 */           this.object = new ONodeLinkedObject();
/*     */         } else {
/*  83 */           this.object = new ONodeObject();
/*     */         } 
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   protected void tryInitArray() {
/*  91 */     if (this.nodeType != ONodeType.Array) {
/*  92 */       this.nodeType = ONodeType.Array;
/*     */       
/*  94 */       if (this.array == null) {
/*  95 */         this.array = new ONodeArray();
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   protected void shiftToArray() {
/* 102 */     tryInitArray();
/*     */     
/* 104 */     if (this.object != null) {
/* 105 */       for (ONode n1 : this.object.values()) {
/* 106 */         this.array.add(n1);
/*     */       }
/*     */       
/* 109 */       this.object.clear();
/* 110 */       this.object = null;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public ONodeData(ONode n) {
/* 117 */     this.attrs = null;
/*     */     this._n = n; } public String attrGet(String key) {
/* 119 */     if (this.attrs != null) {
/* 120 */       return this.attrs.get(key);
/*     */     }
/* 122 */     return null;
/*     */   }
/*     */   
/*     */   public void attrSet(String key, String val) {
/* 126 */     if (this.attrs == null) {
/* 127 */       this.attrs = new LinkedHashMap<>();
/*     */     }
/*     */     
/* 130 */     this.attrs.put(key, val);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean equals(Object o) {
/* 136 */     if (this == o) {
/* 137 */       return true;
/*     */     }
/*     */     
/* 140 */     if (o == null) {
/* 141 */       return false;
/*     */     }
/*     */     
/* 144 */     return (hashCode() == o.hashCode());
/*     */   }
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 149 */     if (this.nodeType == ONodeType.Object) {
/* 150 */       return this.object.hashCode();
/*     */     }
/*     */     
/* 153 */     if (this.nodeType == ONodeType.Array) {
/* 154 */       return this.array.hashCode();
/*     */     }
/*     */     
/* 157 */     if (this.nodeType == ONodeType.Value) {
/* 158 */       return this.value.hashCode();
/*     */     }
/*     */     
/* 161 */     return 0;
/*     */   }
/*     */   
/*     */   class ONodeArray
/*     */     extends ArrayList<ONode> {
/*     */     public int indexOf(Object o) {
/* 167 */       for (int i = 0; i < size(); i++) {
/* 168 */         if (get(i).equals(o))
/* 169 */           return i; 
/*     */       } 
/* 171 */       return -1;
/*     */     }
/*     */   }
/*     */   
/*     */   class ONodeObject
/*     */     extends HashMap<String, ONode> {
/*     */     public boolean containsValue(Object value) {
/* 178 */       for (ONode n : values()) {
/* 179 */         if (n.equals(value)) {
/* 180 */           return true;
/*     */         }
/*     */       } 
/* 183 */       return false;
/*     */     }
/*     */   }
/*     */   
/*     */   class ONodeLinkedObject
/*     */     extends LinkedHashMap<String, ONode> {
/*     */     public boolean containsValue(Object value) {
/* 190 */       for (ONode n : values()) {
/* 191 */         if (n.equals(value)) {
/* 192 */           return true;
/*     */         }
/*     */       } 
/* 195 */       return false;
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\snack\ONodeData.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */