/*     */ package com.mysql.cj.xdevapi;
/*     */ 
/*     */ import com.mysql.cj.x.protobuf.MysqlxDatatypes;
/*     */ import com.mysql.cj.x.protobuf.MysqlxExpr;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashSet;
/*     */ import java.util.List;
/*     */ import java.util.Set;
/*     */ import java.util.stream.Collectors;
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
/*     */ public class ExprUnparser
/*     */ {
/*  55 */   static Set<String> infixOperators = new HashSet<>();
/*     */   
/*     */   static {
/*  58 */     infixOperators.add("and");
/*  59 */     infixOperators.add("or");
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static String scalarToString(MysqlxDatatypes.Scalar e) {
/*  82 */     switch (e.getType()) {
/*     */       case LITERAL:
/*  84 */         return "" + e.getVSignedInt();
/*     */       case IDENT:
/*  86 */         return "\"" + escapeLiteral(e.getVOctets().getValue().toStringUtf8()) + "\"";
/*     */       case FUNC_CALL:
/*  88 */         return "\"" + escapeLiteral(e.getVString().getValue().toStringUtf8()) + "\"";
/*     */       case OPERATOR:
/*  90 */         return "" + e.getVDouble();
/*     */       case PLACEHOLDER:
/*  92 */         return e.getVBool() ? "TRUE" : "FALSE";
/*     */       case ARRAY:
/*  94 */         return "NULL";
/*     */     } 
/*  96 */     throw new IllegalArgumentException("Unknown type tag: " + e.getType());
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
/*     */   static String documentPathToString(List<MysqlxExpr.DocumentPathItem> items) {
/* 108 */     StringBuilder docPathString = new StringBuilder();
/* 109 */     for (MysqlxExpr.DocumentPathItem item : items) {
/* 110 */       switch (item.getType()) {
/*     */         case LITERAL:
/* 112 */           docPathString.append(".").append(quoteDocumentPathMember(item.getValue()));
/*     */         
/*     */         case IDENT:
/* 115 */           docPathString.append(".*");
/*     */         
/*     */         case FUNC_CALL:
/* 118 */           docPathString.append("[").append("" + Integer.toUnsignedLong(item.getIndex())).append("]");
/*     */         
/*     */         case OPERATOR:
/* 121 */           docPathString.append("[*]");
/*     */         
/*     */         case PLACEHOLDER:
/* 124 */           docPathString.append("**");
/*     */       } 
/*     */     
/*     */     } 
/* 128 */     return docPathString.toString();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static String columnIdentifierToString(MysqlxExpr.ColumnIdentifier e) {
/* 139 */     if (e.hasName()) {
/* 140 */       String s = quoteIdentifier(e.getName());
/* 141 */       if (e.hasTableName()) {
/* 142 */         s = quoteIdentifier(e.getTableName()) + "." + s;
/*     */       }
/* 144 */       if (e.hasSchemaName()) {
/* 145 */         s = quoteIdentifier(e.getSchemaName()) + "." + s;
/*     */       }
/* 147 */       if (e.getDocumentPathCount() > 0) {
/* 148 */         s = s + "->$" + documentPathToString(e.getDocumentPathList());
/*     */       }
/* 150 */       return s;
/*     */     } 
/* 152 */     return "$" + documentPathToString(e.getDocumentPathList());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static String functionCallToString(MysqlxExpr.FunctionCall e) {
/* 163 */     MysqlxExpr.Identifier i = e.getName();
/* 164 */     String s = quoteIdentifier(i.getName());
/* 165 */     if (i.hasSchemaName()) {
/* 166 */       s = quoteIdentifier(i.getSchemaName()) + "." + s;
/*     */     }
/* 168 */     s = s + "(";
/* 169 */     for (MysqlxExpr.Expr p : e.getParamList()) {
/* 170 */       s = s + exprToString(p) + ", ";
/*     */     }
/* 172 */     s = s.replaceAll(", $", "");
/* 173 */     s = s + ")";
/* 174 */     return s;
/*     */   }
/*     */   
/*     */   static String arrayToString(MysqlxExpr.Array e) {
/* 178 */     String s = "[";
/*     */     
/* 180 */     for (MysqlxExpr.Expr v : e.getValueList()) {
/* 181 */       s = s + exprToString(v) + ", ";
/*     */     }
/* 183 */     s = s.replaceAll(", $", "");
/* 184 */     s = s + "]";
/*     */     
/* 186 */     return s;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static String paramListToString(List<String> params) {
/* 197 */     String s = "(";
/* 198 */     boolean first = true;
/* 199 */     for (String param : params) {
/* 200 */       if (!first) {
/* 201 */         s = s + ", ";
/*     */       }
/* 203 */       first = false;
/* 204 */       s = s + param;
/*     */     } 
/* 206 */     return s + ")";
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static String operatorToString(MysqlxExpr.Operator e) {
/* 217 */     String name = e.getName();
/* 218 */     List<String> params = new ArrayList<>();
/* 219 */     for (MysqlxExpr.Expr p : e.getParamList()) {
/* 220 */       params.add(exprToString(p));
/*     */     }
/* 222 */     if ("between".equals(name) || "not_between".equals(name)) {
/* 223 */       name = name.replaceAll("not_between", "not between");
/* 224 */       return String.format("(%s %s %s AND %s)", new Object[] { params.get(0), name, params.get(1), params.get(2) });
/* 225 */     }  if ("in".equals(name) || "not_in".equals(name)) {
/* 226 */       name = name.replaceAll("not_in", "not in");
/* 227 */       return String.format("%s %s%s", new Object[] { params.get(0), name, paramListToString(params.subList(1, params.size())) });
/* 228 */     }  if ("like".equals(name) || "not_like".equals(name)) {
/* 229 */       name = name.replaceAll("not_like", "not like");
/* 230 */       String s = String.format("%s %s %s", new Object[] { params.get(0), name, params.get(1) });
/* 231 */       if (params.size() == 3) {
/* 232 */         s = s + " ESCAPE " + (String)params.get(2);
/*     */       }
/* 234 */       return s;
/* 235 */     }  if ("overlaps".equals(name) || "not_overlaps".equals(name)) {
/* 236 */       name = name.replaceAll("not_overlaps", "not overlaps");
/* 237 */       return String.format("%s %s %s", new Object[] { params.get(0), name, params.get(1) });
/* 238 */     }  if ("regexp".equals(name) || "not_regexp".equals("name")) {
/* 239 */       name = name.replaceAll("not_regexp", "not regexp");
/* 240 */       return String.format("(%s %s %s)", new Object[] { params.get(0), name, params.get(1) });
/* 241 */     }  if ("cast".equals(name))
/* 242 */       return String.format("cast(%s AS %s)", new Object[] { params.get(0), ((String)params.get(1)).replaceAll("\"", "") }); 
/* 243 */     if ((name.length() < 3 || infixOperators.contains(name)) && params.size() == 2)
/* 244 */       return String.format("(%s %s %s)", new Object[] { params.get(0), name, params.get(1) }); 
/* 245 */     if ("sign_minus".equals(name)) {
/* 246 */       name = name.replaceAll("sign_minus", "-");
/* 247 */       return String.format("%s%s", new Object[] { name, params.get(0) });
/* 248 */     }  if ("sign_plus".equals(name)) {
/* 249 */       name = name.replaceAll("sign_plus", "+");
/* 250 */       return String.format("%s%s", new Object[] { name, params.get(0) });
/* 251 */     }  if (params.size() == 1)
/* 252 */       return String.format("%s%s", new Object[] { name, params.get(0) }); 
/* 253 */     if (params.size() == 0) {
/* 254 */       return name;
/*     */     }
/* 256 */     return name + paramListToString(params);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static String objectToString(MysqlxExpr.Object o) {
/* 263 */     String fields = o.getFldList().stream().map(f -> "'" + quoteJsonKey(f.getKey()) + "'" + ":" + exprToString(f.getValue())).collect(Collectors.joining(", "));
/* 264 */     return "{" + fields + "}";
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String escapeLiteral(String s) {
/* 275 */     return s.replaceAll("\"", "\"\"");
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
/*     */   public static String quoteIdentifier(String ident) {
/* 287 */     if (ident.contains("`") || ident.contains("\"") || ident.contains("'") || ident.contains("$") || ident.contains(".") || ident.contains("-")) {
/* 288 */       return "`" + ident.replaceAll("`", "``") + "`";
/*     */     }
/* 290 */     return ident;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String quoteJsonKey(String key) {
/* 301 */     return key.replaceAll("'", "\\\\'");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String quoteDocumentPathMember(String member) {
/* 312 */     if (!member.matches("[a-zA-Z0-9_]*")) {
/* 313 */       return "\"" + member.replaceAll("\"", "\\\\\"") + "\"";
/*     */     }
/* 315 */     return member;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String exprToString(MysqlxExpr.Expr e) {
/* 326 */     switch (e.getType()) {
/*     */       case LITERAL:
/* 328 */         return scalarToString(e.getLiteral());
/*     */       case IDENT:
/* 330 */         return columnIdentifierToString(e.getIdentifier());
/*     */       case FUNC_CALL:
/* 332 */         return functionCallToString(e.getFunctionCall());
/*     */       case OPERATOR:
/* 334 */         return operatorToString(e.getOperator());
/*     */       case PLACEHOLDER:
/* 336 */         return ":" + Integer.toUnsignedLong(e.getPosition());
/*     */       case ARRAY:
/* 338 */         return arrayToString(e.getArray());
/*     */       case OBJECT:
/* 340 */         return objectToString(e.getObject());
/*     */     } 
/* 342 */     throw new IllegalArgumentException("Unknown type tag: " + e.getType());
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\xdevapi\ExprUnparser.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */