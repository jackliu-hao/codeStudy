/*     */ package com.google.protobuf;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
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
/*     */ public class TextFormatParseInfoTree
/*     */ {
/*     */   private Map<Descriptors.FieldDescriptor, List<TextFormatParseLocation>> locationsFromField;
/*     */   Map<Descriptors.FieldDescriptor, List<TextFormatParseInfoTree>> subtreesFromField;
/*     */   
/*     */   private TextFormatParseInfoTree(Map<Descriptors.FieldDescriptor, List<TextFormatParseLocation>> locationsFromField, Map<Descriptors.FieldDescriptor, List<Builder>> subtreeBuildersFromField) {
/*  71 */     Map<Descriptors.FieldDescriptor, List<TextFormatParseLocation>> locs = new HashMap<>();
/*     */     
/*  73 */     for (Map.Entry<Descriptors.FieldDescriptor, List<TextFormatParseLocation>> kv : locationsFromField.entrySet()) {
/*  74 */       locs.put(kv.getKey(), Collections.unmodifiableList(kv.getValue()));
/*     */     }
/*  76 */     this.locationsFromField = Collections.unmodifiableMap(locs);
/*     */     
/*  78 */     Map<Descriptors.FieldDescriptor, List<TextFormatParseInfoTree>> subs = new HashMap<>();
/*     */     
/*  80 */     for (Map.Entry<Descriptors.FieldDescriptor, List<Builder>> kv : subtreeBuildersFromField.entrySet()) {
/*  81 */       List<TextFormatParseInfoTree> submessagesOfField = new ArrayList<>();
/*  82 */       for (Builder subBuilder : kv.getValue()) {
/*  83 */         submessagesOfField.add(subBuilder.build());
/*     */       }
/*  85 */       subs.put(kv.getKey(), Collections.unmodifiableList(submessagesOfField));
/*     */     } 
/*  87 */     this.subtreesFromField = Collections.unmodifiableMap(subs);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public List<TextFormatParseLocation> getLocations(Descriptors.FieldDescriptor fieldDescriptor) {
/*  98 */     List<TextFormatParseLocation> result = this.locationsFromField.get(fieldDescriptor);
/*  99 */     return (result == null) ? Collections.<TextFormatParseLocation>emptyList() : result;
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
/*     */   public TextFormatParseLocation getLocation(Descriptors.FieldDescriptor fieldDescriptor, int index) {
/* 114 */     return getFromList(getLocations(fieldDescriptor), index, fieldDescriptor);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public List<TextFormatParseInfoTree> getNestedTrees(Descriptors.FieldDescriptor fieldDescriptor) {
/* 124 */     List<TextFormatParseInfoTree> result = this.subtreesFromField.get(fieldDescriptor);
/* 125 */     return (result == null) ? Collections.<TextFormatParseInfoTree>emptyList() : result;
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
/*     */   public TextFormatParseInfoTree getNestedTree(Descriptors.FieldDescriptor fieldDescriptor, int index) {
/* 138 */     return getFromList(getNestedTrees(fieldDescriptor), index, fieldDescriptor);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Builder builder() {
/* 147 */     return new Builder();
/*     */   }
/*     */   
/*     */   private static <T> T getFromList(List<T> list, int index, Descriptors.FieldDescriptor fieldDescriptor) {
/* 151 */     if (index >= list.size() || index < 0)
/* 152 */       throw new IllegalArgumentException(
/* 153 */           String.format("Illegal index field: %s, index %d", new Object[] {
/*     */               
/* 155 */               (fieldDescriptor == null) ? "<null>" : fieldDescriptor.getName(), Integer.valueOf(index)
/*     */             })); 
/* 157 */     return list.get(index);
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
/*     */   public static class Builder
/*     */   {
/* 171 */     private Map<Descriptors.FieldDescriptor, List<TextFormatParseLocation>> locationsFromField = new HashMap<>();
/* 172 */     private Map<Descriptors.FieldDescriptor, List<Builder>> subtreeBuildersFromField = new HashMap<>();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public Builder setLocation(Descriptors.FieldDescriptor fieldDescriptor, TextFormatParseLocation location) {
/* 183 */       List<TextFormatParseLocation> fieldLocations = this.locationsFromField.get(fieldDescriptor);
/* 184 */       if (fieldLocations == null) {
/* 185 */         fieldLocations = new ArrayList<>();
/* 186 */         this.locationsFromField.put(fieldDescriptor, fieldLocations);
/*     */       } 
/* 188 */       fieldLocations.add(location);
/* 189 */       return this;
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
/*     */     public Builder getBuilderForSubMessageField(Descriptors.FieldDescriptor fieldDescriptor) {
/* 202 */       List<Builder> submessageBuilders = this.subtreeBuildersFromField.get(fieldDescriptor);
/* 203 */       if (submessageBuilders == null) {
/* 204 */         submessageBuilders = new ArrayList<>();
/* 205 */         this.subtreeBuildersFromField.put(fieldDescriptor, submessageBuilders);
/*     */       } 
/* 207 */       Builder subtreeBuilder = new Builder();
/* 208 */       submessageBuilders.add(subtreeBuilder);
/* 209 */       return subtreeBuilder;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public TextFormatParseInfoTree build() {
/* 218 */       return new TextFormatParseInfoTree(this.locationsFromField, this.subtreeBuildersFromField);
/*     */     }
/*     */     
/*     */     private Builder() {}
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\protobuf\TextFormatParseInfoTree.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */