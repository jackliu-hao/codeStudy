/*     */ package cn.hutool.core.lang;
/*     */ 
/*     */ import cn.hutool.core.convert.Convert;
/*     */ import cn.hutool.core.util.StrUtil;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
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
/*     */ public class ConsoleTable
/*     */ {
/*     */   private static final char ROW_LINE = '－';
/*     */   private static final char COLUMN_LINE = '|';
/*     */   private static final char CORNER = '+';
/*     */   private static final char SPACE = '　';
/*     */   private static final char LF = '\n';
/*     */   private boolean isSBCMode = true;
/*     */   
/*     */   public static ConsoleTable create() {
/*  34 */     return new ConsoleTable();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  40 */   private final List<List<String>> headerList = new ArrayList<>();
/*     */ 
/*     */ 
/*     */   
/*  44 */   private final List<List<String>> bodyList = new ArrayList<>();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private List<Integer> columnCharNumber;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ConsoleTable setSBCMode(boolean isSBCMode) {
/*  59 */     this.isSBCMode = isSBCMode;
/*  60 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ConsoleTable addHeader(String... titles) {
/*  70 */     if (this.columnCharNumber == null) {
/*  71 */       this.columnCharNumber = new ArrayList<>(Collections.nCopies(titles.length, Integer.valueOf(0)));
/*     */     }
/*  73 */     List<String> l = new ArrayList<>();
/*  74 */     fillColumns(l, titles);
/*  75 */     this.headerList.add(l);
/*  76 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ConsoleTable addBody(String... values) {
/*  86 */     List<String> l = new ArrayList<>();
/*  87 */     this.bodyList.add(l);
/*  88 */     fillColumns(l, values);
/*  89 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void fillColumns(List<String> l, String[] columns) {
/*  99 */     for (int i = 0; i < columns.length; i++) {
/* 100 */       String column = columns[i];
/* 101 */       if (this.isSBCMode) {
/* 102 */         column = Convert.toSBC(column);
/*     */       }
/* 104 */       l.add(column);
/* 105 */       int width = column.length();
/* 106 */       if (width > ((Integer)this.columnCharNumber.get(i)).intValue()) {
/* 107 */         this.columnCharNumber.set(i, Integer.valueOf(width));
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
/*     */   public String toString() {
/* 119 */     StringBuilder sb = new StringBuilder();
/* 120 */     fillBorder(sb);
/* 121 */     fillRows(sb, this.headerList);
/* 122 */     fillBorder(sb);
/* 123 */     fillRows(sb, this.bodyList);
/* 124 */     fillBorder(sb);
/* 125 */     return sb.toString();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void fillRows(StringBuilder sb, List<List<String>> list) {
/* 135 */     for (List<String> row : list) {
/* 136 */       sb.append('|');
/* 137 */       fillRow(sb, row);
/* 138 */       sb.append('\n');
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void fillRow(StringBuilder sb, List<String> row) {
/* 149 */     int size = row.size();
/*     */     
/* 151 */     for (int i = 0; i < size; i++) {
/* 152 */       String value = row.get(i);
/* 153 */       sb.append('　');
/* 154 */       sb.append(value);
/* 155 */       int length = value.length();
/* 156 */       int sbcCount = sbcCount(value);
/* 157 */       if (sbcCount % 2 == 1) {
/* 158 */         sb.append(' ');
/*     */       }
/* 160 */       sb.append('　');
/* 161 */       int maxLength = ((Integer)this.columnCharNumber.get(i)).intValue();
/* 162 */       for (int j = 0; j < maxLength - length + sbcCount / 2; j++) {
/* 163 */         sb.append('　');
/*     */       }
/* 165 */       sb.append('|');
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void fillBorder(StringBuilder sb) {
/* 175 */     sb.append('+');
/* 176 */     for (Integer width : this.columnCharNumber) {
/* 177 */       sb.append(StrUtil.repeat('－', width.intValue() + 2));
/* 178 */       sb.append('+');
/*     */     } 
/* 180 */     sb.append('\n');
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void print() {
/* 187 */     Console.print(toString());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private int sbcCount(String value) {
/* 197 */     int count = 0;
/* 198 */     for (int i = 0; i < value.length(); i++) {
/* 199 */       if (value.charAt(i) < '') {
/* 200 */         count++;
/*     */       }
/*     */     } 
/*     */     
/* 204 */     return count;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\lang\ConsoleTable.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */