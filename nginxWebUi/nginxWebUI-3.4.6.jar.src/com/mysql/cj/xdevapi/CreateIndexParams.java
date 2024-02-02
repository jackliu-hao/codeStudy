/*     */ package com.mysql.cj.xdevapi;
/*     */ 
/*     */ import com.mysql.cj.Messages;
/*     */ import com.mysql.cj.exceptions.AssertionFailedException;
/*     */ import java.io.IOException;
/*     */ import java.io.StringReader;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
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
/*     */ public class CreateIndexParams
/*     */ {
/*     */   public static final String INDEX = "INDEX";
/*     */   public static final String SPATIAL = "SPATIAL";
/*     */   public static final String GEOJSON = "GEOJSON";
/*     */   private String indexName;
/*  50 */   private String indexType = null;
/*  51 */   private List<IndexField> fields = new ArrayList<>();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public CreateIndexParams(String indexName, DbDoc indexDefinition) {
/*  62 */     init(indexName, indexDefinition);
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
/*     */   public CreateIndexParams(String indexName, String jsonIndexDefinition) {
/*  74 */     if (jsonIndexDefinition == null || jsonIndexDefinition.trim().length() == 0) {
/*  75 */       throw new XDevAPIError(Messages.getString("CreateIndexParams.0", new String[] { "jsonIndexDefinition" }));
/*     */     }
/*     */     try {
/*  78 */       init(indexName, JsonParser.parseDoc(new StringReader(jsonIndexDefinition)));
/*  79 */     } catch (IOException ex) {
/*  80 */       throw AssertionFailedException.shouldNotHappen(ex);
/*     */     } 
/*     */   }
/*     */   
/*     */   private void init(String idxName, DbDoc indexDefinition) {
/*  85 */     if (idxName == null || idxName.trim().length() == 0) {
/*  86 */       throw new XDevAPIError(Messages.getString("CreateIndexParams.0", new String[] { "indexName" }));
/*     */     }
/*  88 */     if (indexDefinition == null) {
/*  89 */       throw new XDevAPIError(Messages.getString("CreateIndexParams.0", new String[] { "indexDefinition" }));
/*     */     }
/*     */     
/*  92 */     this.indexName = idxName;
/*     */     
/*  94 */     for (String key : indexDefinition.keySet()) {
/*  95 */       if (!"type".equals(key) && !"fields".equals(key)) {
/*  96 */         throw new XDevAPIError("The '" + key + "' field is not allowed in indexDefinition.");
/*     */       }
/*     */     } 
/*     */     
/* 100 */     JsonValue val = indexDefinition.get("type");
/* 101 */     if (val != null) {
/* 102 */       if (val instanceof JsonString) {
/* 103 */         String type = ((JsonString)val).getString();
/* 104 */         if ("INDEX".equalsIgnoreCase(type) || "SPATIAL".equalsIgnoreCase(type)) {
/* 105 */           this.indexType = type;
/*     */         } else {
/* 107 */           throw new XDevAPIError("Wrong index type '" + type + "'. Must be 'INDEX' or 'SPATIAL'.");
/*     */         } 
/*     */       } else {
/* 110 */         throw new XDevAPIError("Index type must be a string.");
/*     */       } 
/*     */     }
/*     */     
/* 114 */     val = indexDefinition.get("fields");
/* 115 */     if (val != null) {
/* 116 */       if (val instanceof JsonArray) {
/* 117 */         for (JsonValue field : val) {
/* 118 */           if (field instanceof DbDoc) {
/* 119 */             this.fields.add(new IndexField((DbDoc)field)); continue;
/*     */           } 
/* 121 */           throw new XDevAPIError("Index field definition must be a JSON document.");
/*     */         }
/*     */       
/*     */       } else {
/*     */         
/* 126 */         throw new XDevAPIError("Index definition 'fields' member must be an array of index fields.");
/*     */       } 
/*     */     } else {
/* 129 */       throw new XDevAPIError("Index definition does not contain fields.");
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getIndexName() {
/* 139 */     return this.indexName;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getIndexType() {
/* 148 */     return this.indexType;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public List<IndexField> getFields() {
/* 157 */     return this.fields;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public static class IndexField
/*     */   {
/*     */     private static final String FIELD = "field";
/*     */ 
/*     */     
/*     */     private static final String TYPE = "type";
/*     */ 
/*     */     
/*     */     private static final String REQUIRED = "required";
/*     */     
/*     */     private static final String OPTIONS = "options";
/*     */     
/*     */     private static final String SRID = "srid";
/*     */     
/*     */     private static final String ARRAY = "array";
/*     */     
/*     */     private String field;
/*     */     
/*     */     private String type;
/*     */     
/* 182 */     private Boolean required = Boolean.FALSE;
/*     */ 
/*     */     
/* 185 */     private Integer options = null;
/*     */ 
/*     */     
/* 188 */     private Integer srid = null;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     private Boolean array;
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
/*     */     public IndexField(DbDoc indexField) {
/* 210 */       for (String key : indexField.keySet()) {
/* 211 */         if (!"type".equals(key) && !"field".equals(key) && !"required".equals(key) && !"options".equals(key) && !"srid".equals(key) && !"array".equals(key)) {
/* 212 */           throw new XDevAPIError("The '" + key + "' field is not allowed in indexField.");
/*     */         }
/*     */       } 
/*     */       
/* 216 */       JsonValue val = indexField.get("field");
/* 217 */       if (val != null) {
/* 218 */         if (val instanceof JsonString) {
/* 219 */           this.field = ((JsonString)val).getString();
/*     */         } else {
/* 221 */           throw new XDevAPIError("Index field 'field' member must be a string.");
/*     */         } 
/*     */       } else {
/* 224 */         throw new XDevAPIError("Index field definition has no document path.");
/*     */       } 
/*     */       
/* 227 */       val = indexField.get("type");
/* 228 */       if (val != null) {
/* 229 */         if (val instanceof JsonString) {
/* 230 */           this.type = ((JsonString)val).getString();
/*     */         
/*     */         }
/*     */         else {
/*     */ 
/*     */           
/* 236 */           throw new XDevAPIError("Index type must be a string.");
/*     */         } 
/*     */       } else {
/* 239 */         throw new XDevAPIError("Index field definition has no field type.");
/*     */       } 
/*     */       
/* 242 */       val = indexField.get("required");
/* 243 */       if (val != null) {
/* 244 */         if (val instanceof JsonLiteral && !JsonLiteral.NULL.equals(val)) {
/* 245 */           this.required = Boolean.valueOf(((JsonLiteral)val).value);
/*     */         } else {
/* 247 */           throw new XDevAPIError("Index field 'required' member must be boolean.");
/*     */         } 
/* 249 */       } else if ("GEOJSON".equalsIgnoreCase(this.type)) {
/* 250 */         this.required = Boolean.TRUE;
/*     */       } 
/*     */       
/* 253 */       val = indexField.get("options");
/* 254 */       if (val != null) {
/* 255 */         if ("GEOJSON".equalsIgnoreCase(this.type)) {
/* 256 */           if (val instanceof JsonNumber) {
/* 257 */             this.options = ((JsonNumber)val).getInteger();
/*     */           } else {
/* 259 */             throw new XDevAPIError("Index field 'options' member must be integer.");
/*     */           } 
/*     */         } else {
/* 262 */           throw new XDevAPIError("Index field 'options' member should not be used for field types other than GEOJSON.");
/*     */         } 
/*     */       }
/*     */       
/* 266 */       val = indexField.get("srid");
/* 267 */       if (val != null) {
/* 268 */         if ("GEOJSON".equalsIgnoreCase(this.type)) {
/* 269 */           if (val instanceof JsonNumber) {
/* 270 */             this.srid = ((JsonNumber)val).getInteger();
/*     */           } else {
/* 272 */             throw new XDevAPIError("Index field 'srid' member must be integer.");
/*     */           } 
/*     */         } else {
/* 275 */           throw new XDevAPIError("Index field 'srid' member should not be used for field types other than GEOJSON.");
/*     */         } 
/*     */       }
/*     */       
/* 279 */       val = indexField.get("array");
/* 280 */       if (val != null) {
/* 281 */         if (val instanceof JsonLiteral && !JsonLiteral.NULL.equals(val)) {
/* 282 */           this.array = Boolean.valueOf(((JsonLiteral)val).value);
/*     */         } else {
/* 284 */           throw new XDevAPIError("Index field 'array' member must be boolean.");
/*     */         } 
/*     */       }
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public String getField() {
/* 295 */       return this.field;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public String getType() {
/* 304 */       return this.type;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public Boolean isRequired() {
/* 313 */       return this.required;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public Integer getOptions() {
/* 322 */       return this.options;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public Integer getSrid() {
/* 331 */       return this.srid;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public Boolean isArray() {
/* 340 */       return this.array;
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\xdevapi\CreateIndexParams.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */