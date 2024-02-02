package org.codehaus.plexus.util.xml;

import java.io.IOException;
import org.codehaus.plexus.util.xml.pull.XmlSerializer;

public class Xpp3DomUtils {
   public static final String CHILDREN_COMBINATION_MODE_ATTRIBUTE = "combine.children";
   public static final String CHILDREN_COMBINATION_MERGE = "merge";
   public static final String CHILDREN_COMBINATION_APPEND = "append";
   public static final String DEFAULT_CHILDREN_COMBINATION_MODE = "merge";
   public static final String SELF_COMBINATION_MODE_ATTRIBUTE = "combine.self";
   public static final String SELF_COMBINATION_OVERRIDE = "override";
   public static final String SELF_COMBINATION_MERGE = "merge";
   public static final String DEFAULT_SELF_COMBINATION_MODE = "merge";

   public void writeToSerializer(String namespace, XmlSerializer serializer, Xpp3Dom dom) throws IOException {
      SerializerXMLWriter xmlWriter = new SerializerXMLWriter(namespace, serializer);
      Xpp3DomWriter.write((XMLWriter)xmlWriter, dom);
      if (xmlWriter.getExceptions().size() > 0) {
         throw (IOException)xmlWriter.getExceptions().get(0);
      }
   }

   private static void mergeIntoXpp3Dom(Xpp3Dom dominant, Xpp3Dom recessive, Boolean childMergeOverride) {
      if (recessive != null) {
         boolean mergeSelf = true;
         String selfMergeMode = dominant.getAttribute("combine.self");
         if (isNotEmpty(selfMergeMode) && "override".equals(selfMergeMode)) {
            mergeSelf = false;
         }

         if (mergeSelf) {
            if (isEmpty(dominant.getValue())) {
               dominant.setValue(recessive.getValue());
            }

            String[] recessiveAttrs = recessive.getAttributeNames();

            String childMergeMode;
            for(int i = 0; i < recessiveAttrs.length; ++i) {
               childMergeMode = recessiveAttrs[i];
               if (isEmpty(dominant.getAttribute(childMergeMode))) {
                  dominant.setAttribute(childMergeMode, recessive.getAttribute(childMergeMode));
               }
            }

            boolean mergeChildren = true;
            if (childMergeOverride != null) {
               mergeChildren = childMergeOverride;
            } else {
               childMergeMode = dominant.getAttribute("combine.children");
               if (isNotEmpty(childMergeMode) && "append".equals(childMergeMode)) {
                  mergeChildren = false;
               }
            }

            Xpp3Dom[] children = recessive.getChildren();

            for(int i = 0; i < children.length; ++i) {
               Xpp3Dom child = children[i];
               Xpp3Dom childDom = dominant.getChild(child.getName());
               if (mergeChildren && childDom != null) {
                  mergeIntoXpp3Dom(childDom, child, childMergeOverride);
               } else {
                  dominant.addChild(new Xpp3Dom(child));
               }
            }
         }

      }
   }

   public static Xpp3Dom mergeXpp3Dom(Xpp3Dom dominant, Xpp3Dom recessive, Boolean childMergeOverride) {
      if (dominant != null) {
         mergeIntoXpp3Dom(dominant, recessive, childMergeOverride);
         return dominant;
      } else {
         return recessive;
      }
   }

   public static Xpp3Dom mergeXpp3Dom(Xpp3Dom dominant, Xpp3Dom recessive) {
      if (dominant != null) {
         mergeIntoXpp3Dom(dominant, recessive, (Boolean)null);
         return dominant;
      } else {
         return recessive;
      }
   }

   public static boolean isNotEmpty(String str) {
      return str != null && str.length() > 0;
   }

   public static boolean isEmpty(String str) {
      return str == null || str.trim().length() == 0;
   }
}
