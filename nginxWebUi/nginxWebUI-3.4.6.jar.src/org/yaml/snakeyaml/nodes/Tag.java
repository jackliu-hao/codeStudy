/*     */ package org.yaml.snakeyaml.nodes;
/*     */ 
/*     */ import java.math.BigDecimal;
/*     */ import java.math.BigInteger;
/*     */ import java.net.URI;
/*     */ import java.util.Date;
/*     */ import java.util.HashMap;
/*     */ import java.util.HashSet;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import org.yaml.snakeyaml.error.YAMLException;
/*     */ import org.yaml.snakeyaml.util.UriEncoder;
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
/*     */ public final class Tag
/*     */ {
/*     */   public static final String PREFIX = "tag:yaml.org,2002:";
/*  32 */   public static final Tag YAML = new Tag("tag:yaml.org,2002:yaml");
/*  33 */   public static final Tag MERGE = new Tag("tag:yaml.org,2002:merge");
/*  34 */   public static final Tag SET = new Tag("tag:yaml.org,2002:set");
/*  35 */   public static final Tag PAIRS = new Tag("tag:yaml.org,2002:pairs");
/*  36 */   public static final Tag OMAP = new Tag("tag:yaml.org,2002:omap");
/*  37 */   public static final Tag BINARY = new Tag("tag:yaml.org,2002:binary");
/*  38 */   public static final Tag INT = new Tag("tag:yaml.org,2002:int");
/*  39 */   public static final Tag FLOAT = new Tag("tag:yaml.org,2002:float");
/*  40 */   public static final Tag TIMESTAMP = new Tag("tag:yaml.org,2002:timestamp");
/*  41 */   public static final Tag BOOL = new Tag("tag:yaml.org,2002:bool");
/*  42 */   public static final Tag NULL = new Tag("tag:yaml.org,2002:null");
/*  43 */   public static final Tag STR = new Tag("tag:yaml.org,2002:str");
/*  44 */   public static final Tag SEQ = new Tag("tag:yaml.org,2002:seq");
/*  45 */   public static final Tag MAP = new Tag("tag:yaml.org,2002:map");
/*     */   
/*  47 */   public static final Tag COMMENT = new Tag("tag:yaml.org,2002:comment");
/*     */ 
/*     */   
/*  50 */   protected static final Map<Tag, Set<Class<?>>> COMPATIBILITY_MAP = new HashMap<>(); static {
/*  51 */     Set<Class<?>> floatSet = new HashSet<>();
/*  52 */     floatSet.add(Double.class);
/*  53 */     floatSet.add(Float.class);
/*  54 */     floatSet.add(BigDecimal.class);
/*  55 */     COMPATIBILITY_MAP.put(FLOAT, floatSet);
/*     */     
/*  57 */     Set<Class<?>> intSet = new HashSet<>();
/*  58 */     intSet.add(Integer.class);
/*  59 */     intSet.add(Long.class);
/*  60 */     intSet.add(BigInteger.class);
/*  61 */     COMPATIBILITY_MAP.put(INT, intSet);
/*     */     
/*  63 */     Set<Class<?>> timestampSet = new HashSet<>();
/*  64 */     timestampSet.add(Date.class);
/*     */ 
/*     */     
/*     */     try {
/*  68 */       timestampSet.add(Class.forName("java.sql.Date"));
/*  69 */       timestampSet.add(Class.forName("java.sql.Timestamp"));
/*  70 */     } catch (ClassNotFoundException ignored) {}
/*     */ 
/*     */ 
/*     */     
/*  74 */     COMPATIBILITY_MAP.put(TIMESTAMP, timestampSet);
/*     */   }
/*     */   
/*     */   private final String value;
/*     */   private boolean secondary = false;
/*     */   
/*     */   public Tag(String tag) {
/*  81 */     if (tag == null)
/*  82 */       throw new NullPointerException("Tag must be provided."); 
/*  83 */     if (tag.length() == 0)
/*  84 */       throw new IllegalArgumentException("Tag must not be empty."); 
/*  85 */     if (tag.trim().length() != tag.length()) {
/*  86 */       throw new IllegalArgumentException("Tag must not contain leading or trailing spaces.");
/*     */     }
/*  88 */     this.value = UriEncoder.encode(tag);
/*  89 */     this.secondary = !tag.startsWith("tag:yaml.org,2002:");
/*     */   }
/*     */   
/*     */   public Tag(Class<? extends Object> clazz) {
/*  93 */     if (clazz == null) {
/*  94 */       throw new NullPointerException("Class for tag must be provided.");
/*     */     }
/*  96 */     this.value = "tag:yaml.org,2002:" + UriEncoder.encode(clazz.getName());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Tag(URI uri) {
/* 105 */     if (uri == null) {
/* 106 */       throw new NullPointerException("URI for tag must be provided.");
/*     */     }
/* 108 */     this.value = uri.toASCIIString();
/*     */   }
/*     */   
/*     */   public boolean isSecondary() {
/* 112 */     return this.secondary;
/*     */   }
/*     */   
/*     */   public String getValue() {
/* 116 */     return this.value;
/*     */   }
/*     */   
/*     */   public boolean startsWith(String prefix) {
/* 120 */     return this.value.startsWith(prefix);
/*     */   }
/*     */   
/*     */   public String getClassName() {
/* 124 */     if (!this.value.startsWith("tag:yaml.org,2002:")) {
/* 125 */       throw new YAMLException("Invalid tag: " + this.value);
/*     */     }
/* 127 */     return UriEncoder.decode(this.value.substring("tag:yaml.org,2002:".length()));
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 132 */     return this.value;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean equals(Object obj) {
/* 137 */     if (obj instanceof Tag) {
/* 138 */       return this.value.equals(((Tag)obj).getValue());
/*     */     }
/* 140 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 145 */     return this.value.hashCode();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isCompatible(Class<?> clazz) {
/* 156 */     Set<Class<?>> set = COMPATIBILITY_MAP.get(this);
/* 157 */     if (set != null) {
/* 158 */       return set.contains(clazz);
/*     */     }
/* 160 */     return false;
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
/*     */   public boolean matches(Class<? extends Object> clazz) {
/* 172 */     return this.value.equals("tag:yaml.org,2002:" + clazz.getName());
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\yaml\snakeyaml\nodes\Tag.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */