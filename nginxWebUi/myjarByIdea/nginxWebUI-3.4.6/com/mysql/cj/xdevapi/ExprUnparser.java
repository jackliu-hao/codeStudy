package com.mysql.cj.xdevapi;

import com.mysql.cj.x.protobuf.MysqlxDatatypes;
import com.mysql.cj.x.protobuf.MysqlxExpr;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ExprUnparser {
   static Set<String> infixOperators = new HashSet();

   static String scalarToString(MysqlxDatatypes.Scalar e) {
      switch (e.getType()) {
         case V_SINT:
            return "" + e.getVSignedInt();
         case V_OCTETS:
            return "\"" + escapeLiteral(e.getVOctets().getValue().toStringUtf8()) + "\"";
         case V_STRING:
            return "\"" + escapeLiteral(e.getVString().getValue().toStringUtf8()) + "\"";
         case V_DOUBLE:
            return "" + e.getVDouble();
         case V_BOOL:
            return e.getVBool() ? "TRUE" : "FALSE";
         case V_NULL:
            return "NULL";
         default:
            throw new IllegalArgumentException("Unknown type tag: " + e.getType());
      }
   }

   static String documentPathToString(List<MysqlxExpr.DocumentPathItem> items) {
      StringBuilder docPathString = new StringBuilder();
      Iterator var2 = items.iterator();

      while(var2.hasNext()) {
         MysqlxExpr.DocumentPathItem item = (MysqlxExpr.DocumentPathItem)var2.next();
         switch (item.getType()) {
            case MEMBER:
               docPathString.append(".").append(quoteDocumentPathMember(item.getValue()));
               break;
            case MEMBER_ASTERISK:
               docPathString.append(".*");
               break;
            case ARRAY_INDEX:
               docPathString.append("[").append("" + Integer.toUnsignedLong(item.getIndex())).append("]");
               break;
            case ARRAY_INDEX_ASTERISK:
               docPathString.append("[*]");
               break;
            case DOUBLE_ASTERISK:
               docPathString.append("**");
         }
      }

      return docPathString.toString();
   }

   static String columnIdentifierToString(MysqlxExpr.ColumnIdentifier e) {
      if (e.hasName()) {
         String s = quoteIdentifier(e.getName());
         if (e.hasTableName()) {
            s = quoteIdentifier(e.getTableName()) + "." + s;
         }

         if (e.hasSchemaName()) {
            s = quoteIdentifier(e.getSchemaName()) + "." + s;
         }

         if (e.getDocumentPathCount() > 0) {
            s = s + "->$" + documentPathToString(e.getDocumentPathList());
         }

         return s;
      } else {
         return "$" + documentPathToString(e.getDocumentPathList());
      }
   }

   static String functionCallToString(MysqlxExpr.FunctionCall e) {
      MysqlxExpr.Identifier i = e.getName();
      String s = quoteIdentifier(i.getName());
      if (i.hasSchemaName()) {
         s = quoteIdentifier(i.getSchemaName()) + "." + s;
      }

      s = s + "(";

      MysqlxExpr.Expr p;
      for(Iterator var3 = e.getParamList().iterator(); var3.hasNext(); s = s + exprToString(p) + ", ") {
         p = (MysqlxExpr.Expr)var3.next();
      }

      s = s.replaceAll(", $", "");
      s = s + ")";
      return s;
   }

   static String arrayToString(MysqlxExpr.Array e) {
      String s = "[";

      MysqlxExpr.Expr v;
      for(Iterator var2 = e.getValueList().iterator(); var2.hasNext(); s = s + exprToString(v) + ", ") {
         v = (MysqlxExpr.Expr)var2.next();
      }

      s = s.replaceAll(", $", "");
      s = s + "]";
      return s;
   }

   static String paramListToString(List<String> params) {
      String s = "(";
      boolean first = true;

      String param;
      for(Iterator var3 = params.iterator(); var3.hasNext(); s = s + param) {
         param = (String)var3.next();
         if (!first) {
            s = s + ", ";
         }

         first = false;
      }

      return s + ")";
   }

   static String operatorToString(MysqlxExpr.Operator e) {
      String name = e.getName();
      List<String> params = new ArrayList();
      Iterator var3 = e.getParamList().iterator();

      while(var3.hasNext()) {
         MysqlxExpr.Expr p = (MysqlxExpr.Expr)var3.next();
         params.add(exprToString(p));
      }

      if (!"between".equals(name) && !"not_between".equals(name)) {
         if (!"in".equals(name) && !"not_in".equals(name)) {
            if (!"like".equals(name) && !"not_like".equals(name)) {
               if (!"overlaps".equals(name) && !"not_overlaps".equals(name)) {
                  if (!"regexp".equals(name) && !"not_regexp".equals("name")) {
                     if ("cast".equals(name)) {
                        return String.format("cast(%s AS %s)", params.get(0), ((String)params.get(1)).replaceAll("\"", ""));
                     } else if ((name.length() < 3 || infixOperators.contains(name)) && params.size() == 2) {
                        return String.format("(%s %s %s)", params.get(0), name, params.get(1));
                     } else if ("sign_minus".equals(name)) {
                        name = name.replaceAll("sign_minus", "-");
                        return String.format("%s%s", name, params.get(0));
                     } else if ("sign_plus".equals(name)) {
                        name = name.replaceAll("sign_plus", "+");
                        return String.format("%s%s", name, params.get(0));
                     } else if (params.size() == 1) {
                        return String.format("%s%s", name, params.get(0));
                     } else {
                        return params.size() == 0 ? name : name + paramListToString(params);
                     }
                  } else {
                     name = name.replaceAll("not_regexp", "not regexp");
                     return String.format("(%s %s %s)", params.get(0), name, params.get(1));
                  }
               } else {
                  name = name.replaceAll("not_overlaps", "not overlaps");
                  return String.format("%s %s %s", params.get(0), name, params.get(1));
               }
            } else {
               name = name.replaceAll("not_like", "not like");
               String s = String.format("%s %s %s", params.get(0), name, params.get(1));
               if (params.size() == 3) {
                  s = s + " ESCAPE " + (String)params.get(2);
               }

               return s;
            }
         } else {
            name = name.replaceAll("not_in", "not in");
            return String.format("%s %s%s", params.get(0), name, paramListToString(params.subList(1, params.size())));
         }
      } else {
         name = name.replaceAll("not_between", "not between");
         return String.format("(%s %s %s AND %s)", params.get(0), name, params.get(1), params.get(2));
      }
   }

   static String objectToString(MysqlxExpr.Object o) {
      String fields = (String)o.getFldList().stream().map((f) -> {
         return "'" + quoteJsonKey(f.getKey()) + "'" + ":" + exprToString(f.getValue());
      }).collect(Collectors.joining(", "));
      return "{" + fields + "}";
   }

   public static String escapeLiteral(String s) {
      return s.replaceAll("\"", "\"\"");
   }

   public static String quoteIdentifier(String ident) {
      return !ident.contains("`") && !ident.contains("\"") && !ident.contains("'") && !ident.contains("$") && !ident.contains(".") && !ident.contains("-") ? ident : "`" + ident.replaceAll("`", "``") + "`";
   }

   public static String quoteJsonKey(String key) {
      return key.replaceAll("'", "\\\\'");
   }

   public static String quoteDocumentPathMember(String member) {
      return !member.matches("[a-zA-Z0-9_]*") ? "\"" + member.replaceAll("\"", "\\\\\"") + "\"" : member;
   }

   public static String exprToString(MysqlxExpr.Expr e) {
      switch (e.getType()) {
         case LITERAL:
            return scalarToString(e.getLiteral());
         case IDENT:
            return columnIdentifierToString(e.getIdentifier());
         case FUNC_CALL:
            return functionCallToString(e.getFunctionCall());
         case OPERATOR:
            return operatorToString(e.getOperator());
         case PLACEHOLDER:
            return ":" + Integer.toUnsignedLong(e.getPosition());
         case ARRAY:
            return arrayToString(e.getArray());
         case OBJECT:
            return objectToString(e.getObject());
         default:
            throw new IllegalArgumentException("Unknown type tag: " + e.getType());
      }
   }

   static {
      infixOperators.add("and");
      infixOperators.add("or");
   }
}
