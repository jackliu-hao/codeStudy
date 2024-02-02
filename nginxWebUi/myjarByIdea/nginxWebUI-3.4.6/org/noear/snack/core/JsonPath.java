package org.noear.snack.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.regex.Pattern;
import org.noear.snack.ONode;
import org.noear.snack.OValue;
import org.noear.snack.OValueType;
import org.noear.snack.core.exts.CharBuffer;
import org.noear.snack.core.exts.CharReader;
import org.noear.snack.core.exts.ThData;
import org.noear.snack.core.exts.TmpCache;
import org.noear.snack.core.utils.StringUtil;

public class JsonPath {
   private static int _cacheSize = 1024;
   private static Map<String, JsonPath> _jpathCache = new HashMap(128);
   private static final ThData<CharBuffer> tlBuilder = new ThData(() -> {
      return new CharBuffer();
   });
   private static final ThData<TmpCache> tlCache = new ThData(() -> {
      return new TmpCache();
   });
   private List<Segment> segments = new ArrayList();
   private static Map<String, Pattern> _regexLib = new HashMap();
   private static Resolver handler_$ = (s, root, tmp, usd) -> {
      return tmp;
   };
   private static Resolver handler_xx = (s, root, tmp, usd) -> {
      if (s.name.length() > 0) {
         ONode tmp2 = (new ONode(root.options())).asArray();
         if ("*".equals(s.name)) {
            scanByAll(s.name, tmp, true, tmp2.ary());
         } else {
            scanByName(s.name, tmp, tmp2.ary());
         }

         if (tmp2.count() > 0) {
            return tmp2;
         }
      }

      return null;
   };
   private static Resolver handler_x = (s, root, tmp, usd) -> {
      ONode tmp2 = null;
      if (tmp.count() > 0) {
         tmp2 = (new ONode(tmp.options())).asArray();
         if (tmp.isObject()) {
            tmp2.addAll(tmp.obj().values());
         } else {
            tmp2.addAll((Collection)tmp.ary());
         }
      }

      return tmp2;
   };
   private static Resolver handler_prop = (s, root, tmp, usd) -> {
      if (tmp.isObject()) {
         return tmp.getOrNull(s.cmd);
      } else if (tmp.isArray()) {
         ONode tmp2 = (new ONode(tmp.options())).asArray();
         Iterator var5 = tmp.ary().iterator();

         while(var5.hasNext()) {
            ONode n1 = (ONode)var5.next();
            if (n1.isObject()) {
               ONode n2 = (ONode)n1.nodeData().object.get(s.cmd);
               if (n2 != null) {
                  tmp2.add(n2);
               }
            }
         }

         return tmp2;
      } else {
         return null;
      }
   };
   private static Resolver handler_fun = (s, root, tmp, usd) -> {
      double sum;
      Iterator var7;
      ONode max_n;
      ONode n1;
      switch (s.cmd) {
         case "size()":
            return (new ONode(tmp.options())).val(tmp.count());
         case "length()":
            if (tmp.isValue()) {
               return (new ONode(tmp.options())).val(tmp.getString().length());
            }

            return (new ONode(tmp.options())).val(tmp.count());
         case "min()":
            if (tmp.isArray()) {
               max_n = null;
               var7 = tmp.ary().iterator();

               while(var7.hasNext()) {
                  n1 = (ONode)var7.next();
                  if (n1.isValue()) {
                     if (max_n == null) {
                        max_n = n1;
                     } else if (n1.getDouble() < max_n.getDouble()) {
                        max_n = n1;
                     }
                  }
               }

               return max_n;
            }

            return null;
         case "max()":
            if (tmp.isArray()) {
               max_n = null;
               var7 = tmp.ary().iterator();

               while(var7.hasNext()) {
                  n1 = (ONode)var7.next();
                  if (n1.isValue()) {
                     if (max_n == null) {
                        max_n = n1;
                     } else if (n1.getDouble() > max_n.getDouble()) {
                        max_n = n1;
                     }
                  }
               }

               return max_n;
            }

            return null;
         case "avg()":
            if (tmp.isArray()) {
               sum = 0.0;
               int num = 0;
               Iterator var14 = tmp.ary().iterator();

               while(var14.hasNext()) {
                  ONode n1xx = (ONode)var14.next();
                  if (n1xx.isValue()) {
                     sum += n1xx.getDouble();
                     ++num;
                  }
               }

               if (num > 0) {
                  return (new ONode(tmp.options())).val(sum / (double)num);
               }
            }

            return null;
         case "sum()":
            if (!tmp.isArray()) {
               return null;
            }

            sum = 0.0;

            ONode n1x;
            for(Iterator var8 = tmp.ary().iterator(); var8.hasNext(); sum += n1x.getDouble()) {
               n1x = (ONode)var8.next();
            }

            return (new ONode(tmp.options())).val(sum);
         default:
            return null;
      }
   };
   private static Resolver handler_ary_x = (s, root, tmp, usd) -> {
      ONode tmp2 = null;
      if (tmp.isArray()) {
         tmp2 = tmp;
      }

      if (tmp.isObject()) {
         tmp2 = (new ONode(tmp.options())).asArray();
         tmp2.addAll(tmp.obj().values());
      }

      return tmp2;
   };
   private static Resolver handler_ary_exp = (s, root, tmp, usd) -> {
      ONode tmp2 = tmp;
      Iterator var5;
      ONode n1;
      if (s.op == null) {
         if (tmp.isObject()) {
            if (do_get(tmp, s.left, true, usd).isNull()) {
               return null;
            }
         } else if (tmp.isArray()) {
            tmp2 = (new ONode(tmp.options())).asArray();
            var5 = tmp.ary().iterator();

            while(var5.hasNext()) {
               n1 = (ONode)var5.next();
               if (!do_get(n1, s.left, true, usd).isNull()) {
                  tmp2.nodeData().array.add(n1);
               }
            }
         }
      } else if (tmp.isObject()) {
         if ("@".equals(s.left)) {
            return null;
         }

         ONode leftOx = do_get(tmp, s.left, true, usd);
         if (!compare(root, tmp, leftOx, s.op, s.right, usd)) {
            return null;
         }
      } else if (tmp.isArray()) {
         tmp2 = (new ONode(tmp.options())).asArray();
         if ("@".equals(s.left)) {
            var5 = tmp.ary().iterator();

            while(var5.hasNext()) {
               n1 = (ONode)var5.next();
               if (compare(root, n1, n1, s.op, s.right, usd)) {
                  tmp2.addNode(n1);
               }
            }
         } else {
            var5 = tmp.ary().iterator();

            while(var5.hasNext()) {
               n1 = (ONode)var5.next();
               ONode leftO = do_get(n1, s.left, true, usd);
               if (compare(root, n1, leftO, s.op, s.right, usd)) {
                  tmp2.addNode(n1);
               }
            }
         }
      } else if (tmp.isValue() && "@".equals(s.left) && !compare(root, tmp, tmp, s.op, s.right, usd)) {
         return null;
      }

      return tmp2;
   };
   private static Resolver handler_ary_ref = (s, root, tmp, usd) -> {
      ONode tmp2 = null;
      if (tmp.isObject()) {
         if (s.cmdAry.startsWith("$")) {
            tmp2 = do_get(root, s.cmdAry, true, usd);
         } else {
            tmp2 = do_get(tmp, s.cmdAry, true, usd);
         }

         if (tmp2.isValue()) {
            tmp2 = tmp.get(tmp2.getString());
         } else {
            tmp2 = null;
         }
      }

      return tmp2;
   };
   private static Resolver handler_ary_multi = (s, root, tmp, usd) -> {
      ONode tmp2 = null;
      Iterator var13;
      if (s.cmdAry.indexOf("'") >= 0) {
         Iterator var5;
         if (tmp.isObject()) {
            var5 = s.nameS.iterator();

            while(var5.hasNext()) {
               String k = (String)var5.next();
               ONode n1 = (ONode)tmp.obj().get(k);
               if (n1 != null) {
                  if (tmp2 == null) {
                     tmp2 = (new ONode(tmp.options())).asArray();
                  }

                  tmp2.addNode(n1);
               }
            }
         }

         if (tmp.isArray()) {
            tmp2 = (new ONode(tmp.options())).asArray();
            var5 = tmp.ary().iterator();

            while(true) {
               ONode tmp1;
               do {
                  if (!var5.hasNext()) {
                     return tmp2;
                  }

                  tmp1 = (ONode)var5.next();
               } while(!tmp1.isObject());

               var13 = s.nameS.iterator();

               while(var13.hasNext()) {
                  String kx = (String)var13.next();
                  ONode n1x = (ONode)tmp1.obj().get(kx);
                  if (n1x != null) {
                     tmp2.addNode(n1x);
                  }
               }
            }
         }
      } else if (tmp.isArray()) {
         List<ONode> list2 = tmp.nodeData().array;
         int len2 = list2.size();
         var13 = s.indexS.iterator();

         while(var13.hasNext()) {
            int idx = (Integer)var13.next();
            if (idx >= 0 && idx < len2) {
               if (tmp2 == null) {
                  tmp2 = (new ONode(tmp.options())).asArray();
               }

               tmp2.addNode((ONode)list2.get(idx));
            }
         }
      }

      return tmp2;
   };
   private static Resolver handler_ary_range = (s, root, tmp, usd) -> {
      if (tmp.isArray()) {
         int count = tmp.count();
         int start = s.start;
         int end = s.end;
         if (start < 0) {
            start += count;
         }

         if (end == 0) {
            end = count;
         }

         if (end < 0) {
            end += count;
         }

         if (start < 0) {
            start = 0;
         }

         if (end > count) {
            end = count;
         }

         return (new ONode(tmp.options())).addAll((Collection)tmp.ary().subList(start, end));
      } else {
         return null;
      }
   };
   private static Resolver handler_ary_prop = (s, root, tmp, usd) -> {
      if (s.cmdHasQuote) {
         if (tmp.isObject()) {
            return tmp.getOrNull(s.name);
         } else if (tmp.isArray()) {
            ONode tmp2 = (new ONode(tmp.options())).asArray();
            Iterator var5 = tmp.ary().iterator();

            while(var5.hasNext()) {
               ONode n1 = (ONode)var5.next();
               if (n1.isObject()) {
                  ONode n2 = (ONode)n1.nodeData().object.get(s.name);
                  if (n2 != null) {
                     tmp2.add(n2);
                  }
               }
            }

            return tmp2;
         } else {
            return null;
         }
      } else {
         return s.start < 0 ? tmp.getOrNull(tmp.count() + s.start) : tmp.getOrNull(s.start);
      }
   };

   public static ONode eval(ONode source, String jpath, boolean useStandard, boolean cacheJpath) {
      ((TmpCache)tlCache.get()).clear();
      return do_get(source, jpath, cacheJpath, useStandard);
   }

   private static ONode do_get(ONode source, String jpath, boolean cacheJpath, boolean useStandard) {
      JsonPath jsonPath = null;
      if (cacheJpath) {
         jsonPath = (JsonPath)_jpathCache.get(jpath);
         if (jsonPath == null) {
            synchronized(jpath.intern()) {
               jsonPath = (JsonPath)_jpathCache.get(jpath);
               if (jsonPath == null) {
                  jsonPath = compile(jpath);
                  if (_jpathCache.size() < _cacheSize) {
                     _jpathCache.put(jpath, jsonPath);
                  }
               }
            }
         }
      } else {
         jsonPath = compile(jpath);
      }

      return exec(jsonPath, source, useStandard);
   }

   private JsonPath() {
   }

   private static JsonPath compile(String jpath) {
      String jpath2 = jpath.replace("..", "._");
      JsonPath jsonPath = new JsonPath();
      char token = 0;
      char c = false;
      CharBuffer buffer = (CharBuffer)tlBuilder.get();
      buffer.setLength(0);
      CharReader reader = new CharReader(jpath2);

      while(true) {
         while(true) {
            while(true) {
               char c = reader.next();
               if (c == 0) {
                  if (buffer.length() > 0) {
                     jsonPath.segments.add(new Segment(buffer.toString()));
                     buffer.clear();
                  }

                  return jsonPath;
               }

               switch (c) {
                  case '(':
                     if (token == '[') {
                        token = c;
                     }

                     buffer.append(c);
                     break;
                  case ')':
                     if (token == '(') {
                        token = c;
                     }

                     buffer.append(c);
                     break;
                  case '.':
                     if (token > 0) {
                        buffer.append(c);
                     } else if (buffer.length() > 0) {
                        jsonPath.segments.add(new Segment(buffer.toString()));
                        buffer.clear();
                     }
                     break;
                  case '[':
                     if (token == 0) {
                        token = c;
                        if (buffer.length() > 0) {
                           jsonPath.segments.add(new Segment(buffer.toString()));
                           buffer.clear();
                        }
                     } else {
                        buffer.append(c);
                     }
                     break;
                  case ']':
                     if (token != '[' && token != ')') {
                        buffer.append(c);
                     } else {
                        token = 0;
                        buffer.append(c);
                        if (buffer.length() > 0) {
                           jsonPath.segments.add(new Segment(buffer.toString()));
                           buffer.clear();
                        }
                     }
                     break;
                  default:
                     buffer.append(c);
               }
            }
         }
      }
   }

   private static ONode exec(JsonPath jsonPath, ONode source, boolean useStandard) {
      ONode tmp = source;
      boolean branch_do = false;
      Iterator var5 = jsonPath.segments.iterator();

      while(var5.hasNext()) {
         Segment s = (Segment)var5.next();
         if (tmp == null) {
            break;
         }

         if (!branch_do || !useStandard && s.cmdAry == null) {
            tmp = s.handler.run(s, source, tmp, useStandard);
            branch_do = s.cmdHasUnline;
         } else {
            ONode tmp2 = (new ONode(source.options())).asArray();
            Consumer<ONode> act1 = (n1) -> {
               ONode n2 = s.handler.run(s, source, n1, useStandard);
               if (n2 != null) {
                  if (s.cmdAry != null) {
                     if (n2.isArray()) {
                        tmp2.addAll((Collection)n2.ary());
                     } else {
                        tmp2.addNode(n2);
                     }
                  } else {
                     tmp2.addNode(n2);
                  }
               }

            };
            tmp.ary().forEach(act1);
            tmp = tmp2;
            if (!useStandard) {
               branch_do = false;
            }
         }
      }

      return tmp == null ? new ONode(source.options()) : tmp;
   }

   private static void scanByName(String name, ONode source, List<ONode> target) {
      Iterator var3;
      if (source.isObject()) {
         Map.Entry kv;
         for(var3 = source.obj().entrySet().iterator(); var3.hasNext(); scanByName(name, (ONode)kv.getValue(), target)) {
            kv = (Map.Entry)var3.next();
            if (name.equals(kv.getKey())) {
               target.add(kv.getValue());
            }
         }

      } else if (source.isArray()) {
         var3 = source.ary().iterator();

         while(var3.hasNext()) {
            ONode n1 = (ONode)var3.next();
            scanByName(name, n1, target);
         }

      }
   }

   private static void scanByAll(String name, ONode source, boolean isRoot, List<ONode> target) {
      if (!isRoot) {
         target.add(source);
      }

      Iterator var4;
      if (source.isObject()) {
         var4 = source.obj().entrySet().iterator();

         while(var4.hasNext()) {
            Map.Entry<String, ONode> kv = (Map.Entry)var4.next();
            scanByAll(name, (ONode)kv.getValue(), false, target);
         }

      } else if (source.isArray()) {
         var4 = source.ary().iterator();

         while(var4.hasNext()) {
            ONode n1 = (ONode)var4.next();
            scanByAll(name, n1, false, target);
         }

      }
   }

   private static boolean compare(ONode root, ONode parent, ONode leftO, String op, String right, boolean useStandard) {
      if (leftO == null) {
         return false;
      } else if (leftO.isValue() && !leftO.val().isNull()) {
         OValue left = leftO.val();
         ONode rightO = null;
         if (right.startsWith("$")) {
            rightO = (ONode)((TmpCache)tlCache.get()).get(right);
            if (rightO == null) {
               rightO = do_get(root, right, true, useStandard);
               ((TmpCache)tlCache.get()).put(right, rightO);
            }
         }

         if (right.startsWith("@")) {
            rightO = do_get(parent, right, true, useStandard);
         }

         if (rightO != null) {
            if (rightO.isValue()) {
               if (rightO.val().type() == OValueType.String) {
                  right = "'" + rightO.getString() + "'";
               } else {
                  right = rightO.getDouble() + "";
               }
            } else {
               right = null;
            }
         }

         Object val;
         Iterator var11;
         ONode n1;
         switch (op) {
            case "==":
               if (right == null) {
                  return false;
               } else {
                  if (right.startsWith("'")) {
                     return left.getString().equals(right.substring(1, right.length() - 1));
                  }

                  return left.getDouble() == Double.parseDouble(right);
               }
            case "!=":
               if (right == null) {
                  return false;
               } else {
                  if (right.startsWith("'")) {
                     return !left.getString().equals(right.substring(1, right.length() - 1));
                  }

                  return left.getDouble() != Double.parseDouble(right);
               }
            case "<":
               if (right == null) {
                  return false;
               }

               return left.getDouble() < Double.parseDouble(right);
            case "<=":
               if (right == null) {
                  return false;
               }

               return left.getDouble() <= Double.parseDouble(right);
            case ">":
               if (right == null) {
                  return false;
               }

               return left.getDouble() > Double.parseDouble(right);
            case ">=":
               if (right == null) {
                  return false;
               }

               return left.getDouble() >= Double.parseDouble(right);
            case "=~":
               if (right == null) {
                  return false;
               }

               int end = right.lastIndexOf(47);
               String exp = right.substring(1, end);
               return regex(right, exp).matcher(left.getString()).find();
            case "in":
               if (right == null) {
                  val = left.getRaw();
                  var11 = rightO.ary().iterator();

                  do {
                     if (!var11.hasNext()) {
                        return false;
                     }

                     n1 = (ONode)var11.next();
                  } while(!n1.val().getRaw().equals(val));

                  return true;
               } else {
                  if (right.indexOf("'") > 0) {
                     return getStringAry(right).contains(left.getString());
                  }

                  return getDoubleAry(right).contains(left.getDouble());
               }
            case "nin":
               if (right == null) {
                  val = left.getRaw();
                  var11 = rightO.ary().iterator();

                  do {
                     if (!var11.hasNext()) {
                        return true;
                     }

                     n1 = (ONode)var11.next();
                  } while(!n1.val().getRaw().equals(val));

                  return false;
               } else {
                  if (right.indexOf("'") > 0) {
                     return !getStringAry(right).contains(left.getString());
                  }

                  return !getDoubleAry(right).contains(left.getDouble());
               }
            default:
               return false;
         }
      } else {
         return false;
      }
   }

   private static List<String> getStringAry(String text) {
      List<String> ary = new ArrayList();
      String test2 = text.substring(1, text.length() - 1);
      String[] ss = test2.split(",");
      String[] var4 = ss;
      int var5 = ss.length;

      for(int var6 = 0; var6 < var5; ++var6) {
         String s = var4[var6];
         ary.add(s.substring(1, s.length() - 1));
      }

      return ary;
   }

   private static List<Double> getDoubleAry(String text) {
      List<Double> ary = new ArrayList();
      String test2 = text.substring(1, text.length() - 1);
      String[] ss = test2.split(",");
      String[] var4 = ss;
      int var5 = ss.length;

      for(int var6 = 0; var6 < var5; ++var6) {
         String s = var4[var6];
         ary.add(Double.parseDouble(s));
      }

      return ary;
   }

   private static Pattern regex(String exprFull, String expr) {
      Pattern p = (Pattern)_regexLib.get(exprFull);
      if (p == null) {
         synchronized(exprFull.intern()) {
            if (p == null) {
               if (exprFull.endsWith("i")) {
                  p = Pattern.compile(expr, 2);
               } else {
                  p = Pattern.compile(expr);
               }

               _regexLib.put(exprFull, p);
            }
         }
      }

      return p;
   }

   private static class Segment {
      public String cmd;
      public String cmdAry;
      public boolean cmdHasQuote;
      public boolean cmdHasUnline;
      public List<Integer> indexS;
      public List<String> nameS;
      public String name;
      public int start = 0;
      public int end = 0;
      public String left;
      public String op;
      public String right;
      public Resolver handler;

      public Segment(String test) {
         this.cmd = test.trim();
         this.cmdHasQuote = this.cmd.indexOf("'") >= 0;
         this.cmdHasUnline = this.cmd.startsWith("_");
         if (this.cmdHasUnline) {
            this.name = this.cmd.substring(1);
         }

         if (this.cmd.endsWith("]")) {
            this.cmdAry = this.cmd.substring(0, this.cmd.length() - 1).trim();
            String[] ss2;
            if (this.cmdAry.startsWith("?")) {
               String s2 = this.cmdAry.substring(2, this.cmdAry.length() - 1);
               ss2 = s2.split(" ");
               this.left = ss2[0];
               if (ss2.length == 3) {
                  this.op = ss2[1];
                  this.right = ss2[2];
               }
            } else {
               String[] iAry;
               if (this.cmdAry.indexOf(":") >= 0) {
                  iAry = this.cmdAry.split(":", -1);
                  this.start = 0;
                  if (iAry[0].length() > 0) {
                     this.start = Integer.parseInt(iAry[0]);
                  }

                  this.end = 0;
                  if (iAry[1].length() > 0) {
                     this.end = Integer.parseInt(iAry[1]);
                  }
               } else if (this.cmdAry.indexOf(",") > 0) {
                  int var4;
                  int var5;
                  String i1;
                  if (this.cmdAry.indexOf("'") >= 0) {
                     this.nameS = new ArrayList();
                     iAry = this.cmdAry.split(",");
                     ss2 = iAry;
                     var4 = iAry.length;

                     for(var5 = 0; var5 < var4; ++var5) {
                        i1 = ss2[var5];
                        i1 = i1.trim();
                        this.nameS.add(i1.substring(1, i1.length() - 1));
                     }
                  } else {
                     this.indexS = new ArrayList();
                     iAry = this.cmdAry.split(",");
                     ss2 = iAry;
                     var4 = iAry.length;

                     for(var5 = 0; var5 < var4; ++var5) {
                        i1 = ss2[var5];
                        i1 = i1.trim();
                        this.indexS.add(Integer.parseInt(i1));
                     }
                  }
               } else if (this.cmdAry.indexOf("'") >= 0) {
                  this.name = this.cmdAry.substring(1, this.cmdAry.length() - 1);
               } else if (StringUtil.isInteger(this.cmdAry)) {
                  this.start = Integer.parseInt(this.cmdAry);
               }
            }
         }

         if (!"$".equals(this.cmd) && !"@".equals(this.cmd)) {
            if (this.cmd.startsWith("_")) {
               this.handler = JsonPath.handler_xx;
            } else if ("*".equals(this.cmd)) {
               this.handler = JsonPath.handler_x;
            } else {
               if (this.cmd.endsWith("]")) {
                  if ("*".equals(this.cmdAry)) {
                     this.handler = JsonPath.handler_ary_x;
                     return;
                  }

                  if (this.cmd.startsWith("?")) {
                     this.handler = JsonPath.handler_ary_exp;
                  } else if (this.cmdAry.indexOf(",") > 0) {
                     this.handler = JsonPath.handler_ary_multi;
                  } else if (this.cmdAry.indexOf(":") >= 0) {
                     this.handler = JsonPath.handler_ary_range;
                  } else if (!this.cmdAry.startsWith("$.") && !this.cmdAry.startsWith("@.")) {
                     this.handler = JsonPath.handler_ary_prop;
                  } else {
                     this.handler = JsonPath.handler_ary_ref;
                  }
               } else if (this.cmd.endsWith(")")) {
                  this.handler = JsonPath.handler_fun;
               } else {
                  this.handler = JsonPath.handler_prop;
               }

            }
         } else {
            this.handler = JsonPath.handler_$;
         }
      }

      public int length() {
         return this.cmd.length();
      }

      public String toString() {
         return this.cmd;
      }
   }

   @FunctionalInterface
   private interface Resolver {
      ONode run(Segment s, ONode root, ONode tmp, Boolean usd);
   }
}
