package org.yaml.snakeyaml.resolver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import org.yaml.snakeyaml.nodes.NodeId;
import org.yaml.snakeyaml.nodes.Tag;

public class Resolver {
   public static final Pattern BOOL = Pattern.compile("^(?:yes|Yes|YES|no|No|NO|true|True|TRUE|false|False|FALSE|on|On|ON|off|Off|OFF)$");
   public static final Pattern FLOAT = Pattern.compile("^([-+]?(\\.[0-9]+|[0-9_]+(\\.[0-9_]*)?)([eE][-+]?[0-9]+)?|[-+]?[0-9][0-9_]*(?::[0-5]?[0-9])+\\.[0-9_]*|[-+]?\\.(?:inf|Inf|INF)|\\.(?:nan|NaN|NAN))$");
   public static final Pattern INT = Pattern.compile("^(?:[-+]?0b[0-1_]+|[-+]?0[0-7_]+|[-+]?(?:0|[1-9][0-9_]*)|[-+]?0x[0-9a-fA-F_]+|[-+]?[1-9][0-9_]*(?::[0-5]?[0-9])+)$");
   public static final Pattern MERGE = Pattern.compile("^(?:<<)$");
   public static final Pattern NULL = Pattern.compile("^(?:~|null|Null|NULL| )$");
   public static final Pattern EMPTY = Pattern.compile("^$");
   public static final Pattern TIMESTAMP = Pattern.compile("^(?:[0-9][0-9][0-9][0-9]-[0-9][0-9]-[0-9][0-9]|[0-9][0-9][0-9][0-9]-[0-9][0-9]?-[0-9][0-9]?(?:[Tt]|[ \t]+)[0-9][0-9]?:[0-9][0-9]:[0-9][0-9](?:\\.[0-9]*)?(?:[ \t]*(?:Z|[-+][0-9][0-9]?(?::[0-9][0-9])?))?)$");
   public static final Pattern VALUE = Pattern.compile("^(?:=)$");
   public static final Pattern YAML = Pattern.compile("^(?:!|&|\\*)$");
   protected Map<Character, List<ResolverTuple>> yamlImplicitResolvers = new HashMap();

   protected void addImplicitResolvers() {
      this.addImplicitResolver(Tag.BOOL, BOOL, "yYnNtTfFoO");
      this.addImplicitResolver(Tag.INT, INT, "-+0123456789");
      this.addImplicitResolver(Tag.FLOAT, FLOAT, "-+0123456789.");
      this.addImplicitResolver(Tag.MERGE, MERGE, "<");
      this.addImplicitResolver(Tag.NULL, NULL, "~nN\u0000");
      this.addImplicitResolver(Tag.NULL, EMPTY, (String)null);
      this.addImplicitResolver(Tag.TIMESTAMP, TIMESTAMP, "0123456789");
      this.addImplicitResolver(Tag.YAML, YAML, "!&*");
   }

   public Resolver() {
      this.addImplicitResolvers();
   }

   public void addImplicitResolver(Tag tag, Pattern regexp, String first) {
      if (first == null) {
         List<ResolverTuple> curr = (List)this.yamlImplicitResolvers.get((Object)null);
         if (curr == null) {
            curr = new ArrayList();
            this.yamlImplicitResolvers.put((Object)null, curr);
         }

         ((List)curr).add(new ResolverTuple(tag, regexp));
      } else {
         char[] chrs = first.toCharArray();
         int i = 0;

         for(int j = chrs.length; i < j; ++i) {
            Character theC = chrs[i];
            if (theC == 0) {
               theC = null;
            }

            List<ResolverTuple> curr = (List)this.yamlImplicitResolvers.get(theC);
            if (curr == null) {
               curr = new ArrayList();
               this.yamlImplicitResolvers.put(theC, curr);
            }

            ((List)curr).add(new ResolverTuple(tag, regexp));
         }
      }

   }

   public Tag resolve(NodeId kind, String value, boolean implicit) {
      if (kind == NodeId.scalar && implicit) {
         List resolvers;
         if (value.length() == 0) {
            resolvers = (List)this.yamlImplicitResolvers.get('\u0000');
         } else {
            resolvers = (List)this.yamlImplicitResolvers.get(value.charAt(0));
         }

         Iterator i$;
         ResolverTuple v;
         Tag tag;
         Pattern regexp;
         if (resolvers != null) {
            i$ = resolvers.iterator();

            while(i$.hasNext()) {
               v = (ResolverTuple)i$.next();
               tag = v.getTag();
               regexp = v.getRegexp();
               if (regexp.matcher(value).matches()) {
                  return tag;
               }
            }
         }

         if (this.yamlImplicitResolvers.containsKey((Object)null)) {
            i$ = ((List)this.yamlImplicitResolvers.get((Object)null)).iterator();

            while(i$.hasNext()) {
               v = (ResolverTuple)i$.next();
               tag = v.getTag();
               regexp = v.getRegexp();
               if (regexp.matcher(value).matches()) {
                  return tag;
               }
            }
         }
      }

      switch (kind) {
         case scalar:
            return Tag.STR;
         case sequence:
            return Tag.SEQ;
         default:
            return Tag.MAP;
      }
   }
}
