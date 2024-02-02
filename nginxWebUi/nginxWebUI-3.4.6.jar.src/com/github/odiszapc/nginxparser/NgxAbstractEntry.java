/*    */ package com.github.odiszapc.nginxparser;
/*    */ 
/*    */ import java.util.ArrayList;
/*    */ import java.util.Collection;
/*    */ import java.util.Iterator;
/*    */ import java.util.List;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public abstract class NgxAbstractEntry
/*    */   implements NgxEntry
/*    */ {
/* 25 */   private Collection<NgxToken> tokens = new ArrayList<NgxToken>();
/*    */   
/*    */   public NgxAbstractEntry(String... rawValues) {
/* 28 */     for (String val : rawValues) {
/* 29 */       this.tokens.add(new NgxToken(val));
/*    */     }
/*    */   }
/*    */   
/*    */   public Collection<NgxToken> getTokens() {
/* 34 */     return this.tokens;
/*    */   }
/*    */   
/*    */   public void addValue(NgxToken token) {
/* 38 */     this.tokens.add(token);
/*    */   }
/*    */   
/*    */   public void addValue(String value) {
/* 42 */     addValue(new NgxToken(value));
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 47 */     StringBuilder builder = new StringBuilder();
/* 48 */     for (NgxToken value : this.tokens) {
/* 49 */       builder.append(value).append(" ");
/*    */     }
/* 51 */     String s = builder.toString();
/* 52 */     return s.substring(0, s.length() - 1);
/*    */   }
/*    */   
/*    */   public String getName() {
/* 56 */     if (getTokens().isEmpty()) {
/* 57 */       return null;
/*    */     }
/* 59 */     return ((NgxToken)getTokens().iterator().next()).toString();
/*    */   }
/*    */   
/*    */   public List<String> getValues() {
/* 63 */     ArrayList<String> values = new ArrayList<String>();
/* 64 */     if (getTokens().size() < 2) {
/* 65 */       return values;
/*    */     }
/* 67 */     Iterator<NgxToken> it = getTokens().iterator();
/* 68 */     it.next();
/* 69 */     while (it.hasNext()) {
/* 70 */       values.add(((NgxToken)it.next()).toString());
/*    */     }
/* 72 */     return values;
/*    */   }
/*    */   
/*    */   public String getValue() {
/* 76 */     Iterator<String> iterator = getValues().iterator();
/* 77 */     StringBuilder builder = new StringBuilder();
/* 78 */     while (iterator.hasNext()) {
/* 79 */       builder.append(iterator.next());
/* 80 */       if (iterator.hasNext()) {
/* 81 */         builder.append(' ');
/*    */       }
/*    */     } 
/* 84 */     return builder.toString();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\github\odiszapc\nginxparser\NgxAbstractEntry.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */