/*     */ package org.codehaus.plexus.util.xml;
/*     */ 
/*     */ import org.codehaus.plexus.util.StringUtils;
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
/*     */ public class XmlWriterUtil
/*     */ {
/*  30 */   public static final String LS = System.getProperty("line.separator");
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static final int DEFAULT_INDENTATION_SIZE = 2;
/*     */ 
/*     */ 
/*     */   
/*     */   public static final int DEFAULT_COLUMN_LINE = 80;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void writeLineBreak(XMLWriter writer) {
/*  45 */     writeLineBreak(writer, 1);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void writeLineBreak(XMLWriter writer, int repeat) {
/*  56 */     for (int i = 0; i < repeat; i++)
/*     */     {
/*  58 */       writer.writeMarkup(LS);
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
/*     */   
/*     */   public static void writeLineBreak(XMLWriter writer, int repeat, int indent) {
/*  73 */     writeLineBreak(writer, repeat, indent, 2);
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
/*     */   public static void writeLineBreak(XMLWriter writer, int repeat, int indent, int indentSize) {
/*  86 */     writeLineBreak(writer, repeat);
/*     */     
/*  88 */     if (indent < 0)
/*     */     {
/*  90 */       indent = 0;
/*     */     }
/*     */     
/*  93 */     if (indentSize < 0)
/*     */     {
/*  95 */       indentSize = 0;
/*     */     }
/*     */     
/*  98 */     writer.writeText(StringUtils.repeat(" ", indent * indentSize));
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
/*     */   public static void writeCommentLineBreak(XMLWriter writer) {
/* 110 */     writeCommentLineBreak(writer, 80);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void writeCommentLineBreak(XMLWriter writer, int columnSize) {
/* 121 */     if (columnSize < 10)
/*     */     {
/* 123 */       columnSize = 80;
/*     */     }
/*     */     
/* 126 */     writer.writeMarkup("<!-- " + StringUtils.repeat("=", columnSize - 10) + " -->" + LS);
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
/*     */   public static void writeComment(XMLWriter writer, String comment) {
/* 139 */     writeComment(writer, comment, 0, 2);
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
/*     */   public static void writeComment(XMLWriter writer, String comment, int indent) {
/* 154 */     writeComment(writer, comment, indent, 2);
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
/*     */   public static void writeComment(XMLWriter writer, String comment, int indent, int indentSize) {
/* 170 */     writeComment(writer, comment, indent, indentSize, 80);
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
/*     */   public static void writeComment(XMLWriter writer, String comment, int indent, int indentSize, int columnSize) {
/* 184 */     if (comment == null)
/*     */     {
/* 186 */       comment = "null";
/*     */     }
/*     */     
/* 189 */     if (indent < 0)
/*     */     {
/* 191 */       indent = 0;
/*     */     }
/*     */     
/* 194 */     if (indentSize < 0)
/*     */     {
/* 196 */       indentSize = 0;
/*     */     }
/*     */     
/* 199 */     if (columnSize < 0)
/*     */     {
/* 201 */       columnSize = 80;
/*     */     }
/*     */     
/* 204 */     String indentation = StringUtils.repeat(" ", indent * indentSize);
/* 205 */     int magicNumber = indentation.length() + columnSize - "-->".length() - 1;
/* 206 */     String[] sentences = StringUtils.split(comment, LS);
/*     */     
/* 208 */     StringBuffer line = new StringBuffer(indentation + "<!-- ");
/* 209 */     for (int i = 0; i < sentences.length; i++) {
/*     */       
/* 211 */       String sentence = sentences[i];
/* 212 */       String[] words = StringUtils.split(sentence, " ");
/* 213 */       for (int j = 0; j < words.length; j++) {
/*     */         
/* 215 */         StringBuffer sentenceTmp = new StringBuffer(line.toString());
/* 216 */         sentenceTmp.append(words[j]).append(' ');
/* 217 */         if (sentenceTmp.length() > magicNumber) {
/*     */           
/* 219 */           if (line.length() != indentation.length() + "<!-- ".length()) {
/*     */             
/* 221 */             if (magicNumber - line.length() > 0)
/*     */             {
/* 223 */               line.append(StringUtils.repeat(" ", magicNumber - line.length()));
/*     */             }
/*     */             
/* 226 */             line.append("-->").append(LS);
/* 227 */             writer.writeMarkup(line.toString());
/*     */           } 
/* 229 */           line = new StringBuffer(indentation + "<!-- ");
/* 230 */           line.append(words[j]).append(' ');
/*     */         }
/*     */         else {
/*     */           
/* 234 */           line.append(words[j]).append(' ');
/*     */         } 
/*     */       } 
/*     */       
/* 238 */       if (magicNumber - line.length() > 0)
/*     */       {
/* 240 */         line.append(StringUtils.repeat(" ", magicNumber - line.length()));
/*     */       }
/*     */     } 
/*     */     
/* 244 */     if (line.length() <= magicNumber)
/*     */     {
/* 246 */       line.append(StringUtils.repeat(" ", magicNumber - line.length()));
/*     */     }
/*     */     
/* 249 */     line.append("-->").append(LS);
/*     */     
/* 251 */     writer.writeMarkup(line.toString());
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
/*     */   public static void writeCommentText(XMLWriter writer, String comment) {
/* 265 */     writeCommentText(writer, comment, 0, 2);
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
/*     */   public static void writeCommentText(XMLWriter writer, String comment, int indent) {
/* 281 */     writeCommentText(writer, comment, indent, 2);
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
/*     */   public static void writeCommentText(XMLWriter writer, String comment, int indent, int indentSize) {
/* 297 */     writeCommentText(writer, comment, indent, indentSize, 80);
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
/*     */   public static void writeCommentText(XMLWriter writer, String comment, int indent, int indentSize, int columnSize) {
/* 313 */     if (indent < 0)
/*     */     {
/* 315 */       indent = 0;
/*     */     }
/*     */     
/* 318 */     if (indentSize < 0)
/*     */     {
/* 320 */       indentSize = 0;
/*     */     }
/*     */     
/* 323 */     if (columnSize < 0)
/*     */     {
/* 325 */       columnSize = 80;
/*     */     }
/*     */     
/* 328 */     writeLineBreak(writer, 1);
/*     */     
/* 330 */     writer.writeMarkup(StringUtils.repeat(" ", indent * indentSize));
/* 331 */     writeCommentLineBreak(writer, columnSize);
/*     */     
/* 333 */     writeComment(writer, comment, indent, indentSize, columnSize);
/*     */     
/* 335 */     writer.writeMarkup(StringUtils.repeat(" ", indent * indentSize));
/* 336 */     writeCommentLineBreak(writer, columnSize);
/*     */     
/* 338 */     writeLineBreak(writer, 1, indent, indentSize);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\codehaus\plexu\\util\xml\XmlWriterUtil.class
 * Java compiler version: 3 (47.0)
 * JD-Core Version:       1.1.3
 */