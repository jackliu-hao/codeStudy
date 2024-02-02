package org.codehaus.plexus.util.xml;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.codehaus.plexus.util.xml.pull.XmlSerializer;

public class Xpp3Dom {
   protected String name;
   protected String value;
   protected Map attributes;
   protected final List childList;
   protected final Map childMap;
   protected Xpp3Dom parent;
   private static final String[] EMPTY_STRING_ARRAY = new String[0];
   private static final Xpp3Dom[] EMPTY_DOM_ARRAY = new Xpp3Dom[0];
   public static final String CHILDREN_COMBINATION_MODE_ATTRIBUTE = "combine.children";
   public static final String CHILDREN_COMBINATION_MERGE = "merge";
   public static final String CHILDREN_COMBINATION_APPEND = "append";
   public static final String DEFAULT_CHILDREN_COMBINATION_MODE = "merge";
   public static final String SELF_COMBINATION_MODE_ATTRIBUTE = "combine.self";
   public static final String SELF_COMBINATION_OVERRIDE = "override";
   public static final String SELF_COMBINATION_MERGE = "merge";
   public static final String DEFAULT_SELF_COMBINATION_MODE = "merge";

   public Xpp3Dom(String name) {
      this.name = name;
      this.childList = new ArrayList();
      this.childMap = new HashMap();
   }

   public Xpp3Dom(Xpp3Dom src) {
      this(src, src.getName());
   }

   public Xpp3Dom(Xpp3Dom src, String name) {
      this.name = name;
      int childCount = src.getChildCount();
      this.childList = new ArrayList(childCount);
      this.childMap = new HashMap(childCount << 1);
      this.setValue(src.getValue());
      String[] attributeNames = src.getAttributeNames();

      int i;
      for(i = 0; i < attributeNames.length; ++i) {
         String attributeName = attributeNames[i];
         this.setAttribute(attributeName, src.getAttribute(attributeName));
      }

      for(i = 0; i < childCount; ++i) {
         this.addChild(new Xpp3Dom(src.getChild(i)));
      }

   }

   public String getName() {
      return this.name;
   }

   public String getValue() {
      return this.value;
   }

   public void setValue(String value) {
      this.value = value;
   }

   public String[] getAttributeNames() {
      return null != this.attributes && !this.attributes.isEmpty() ? (String[])this.attributes.keySet().toArray(new String[this.attributes.size()]) : EMPTY_STRING_ARRAY;
   }

   public String getAttribute(String name) {
      return null != this.attributes ? (String)this.attributes.get(name) : null;
   }

   public void setAttribute(String name, String value) {
      if (null == value) {
         throw new NullPointerException("Attribute value can not be null");
      } else if (null == name) {
         throw new NullPointerException("Attribute name can not be null");
      } else {
         if (null == this.attributes) {
            this.attributes = new HashMap();
         }

         this.attributes.put(name, value);
      }
   }

   public Xpp3Dom getChild(int i) {
      return (Xpp3Dom)this.childList.get(i);
   }

   public Xpp3Dom getChild(String name) {
      return (Xpp3Dom)this.childMap.get(name);
   }

   public void addChild(Xpp3Dom xpp3Dom) {
      xpp3Dom.setParent(this);
      this.childList.add(xpp3Dom);
      this.childMap.put(xpp3Dom.getName(), xpp3Dom);
   }

   public Xpp3Dom[] getChildren() {
      return null != this.childList && !this.childList.isEmpty() ? (Xpp3Dom[])this.childList.toArray(new Xpp3Dom[this.childList.size()]) : EMPTY_DOM_ARRAY;
   }

   public Xpp3Dom[] getChildren(String name) {
      if (null == this.childList) {
         return EMPTY_DOM_ARRAY;
      } else {
         ArrayList children = new ArrayList();
         int size = this.childList.size();

         for(int i = 0; i < size; ++i) {
            Xpp3Dom configuration = (Xpp3Dom)this.childList.get(i);
            if (name.equals(configuration.getName())) {
               children.add(configuration);
            }
         }

         return (Xpp3Dom[])children.toArray(new Xpp3Dom[children.size()]);
      }
   }

   public int getChildCount() {
      return null == this.childList ? 0 : this.childList.size();
   }

   public void removeChild(int i) {
      Xpp3Dom child = this.getChild(i);
      this.childMap.values().remove(child);
      this.childList.remove(i);
      child.setParent((Xpp3Dom)null);
   }

   public Xpp3Dom getParent() {
      return this.parent;
   }

   public void setParent(Xpp3Dom parent) {
      this.parent = parent;
   }

   public void writeToSerializer(String namespace, XmlSerializer serializer) throws IOException {
      SerializerXMLWriter xmlWriter = new SerializerXMLWriter(namespace, serializer);
      Xpp3DomWriter.write((XMLWriter)xmlWriter, this);
      if (xmlWriter.getExceptions().size() > 0) {
         throw (IOException)xmlWriter.getExceptions().get(0);
      }
   }

   private static void mergeIntoXpp3Dom(Xpp3Dom dominant, Xpp3Dom recessive, Boolean childMergeOverride) {
      if (recessive != null) {
         boolean mergeSelf = true;
         String selfMergeMode = dominant.getAttribute("combine.self");
         if ("override".equals(selfMergeMode)) {
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

            if (recessive.getChildCount() > 0) {
               boolean mergeChildren = true;
               if (childMergeOverride != null) {
                  mergeChildren = childMergeOverride;
               } else {
                  childMergeMode = dominant.getAttribute("combine.children");
                  if ("append".equals(childMergeMode)) {
                     mergeChildren = false;
                  }
               }

               int i;
               int recessiveChildCount;
               Xpp3Dom recessiveChild;
               if (!mergeChildren) {
                  Xpp3Dom[] dominantChildren = dominant.getChildren();
                  dominant.childList.clear();
                  i = 0;

                  for(recessiveChildCount = recessive.getChildCount(); i < recessiveChildCount; ++i) {
                     recessiveChild = recessive.getChild(i);
                     dominant.addChild(new Xpp3Dom(recessiveChild));
                  }

                  for(i = 0; i < dominantChildren.length; ++i) {
                     dominant.addChild(dominantChildren[i]);
                  }
               } else {
                  Map commonChildren = new HashMap();
                  Iterator it = recessive.childMap.keySet().iterator();

                  while(it.hasNext()) {
                     String childName = (String)it.next();
                     Xpp3Dom[] dominantChildren = dominant.getChildren(childName);
                     if (dominantChildren.length > 0) {
                        commonChildren.put(childName, Arrays.asList(dominantChildren).iterator());
                     }
                  }

                  i = 0;

                  for(recessiveChildCount = recessive.getChildCount(); i < recessiveChildCount; ++i) {
                     recessiveChild = recessive.getChild(i);
                     Iterator it = (Iterator)commonChildren.get(recessiveChild.getName());
                     if (it == null) {
                        dominant.addChild(new Xpp3Dom(recessiveChild));
                     } else if (it.hasNext()) {
                        Xpp3Dom dominantChild = (Xpp3Dom)it.next();
                        mergeIntoXpp3Dom(dominantChild, recessiveChild, childMergeOverride);
                     }
                  }
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

   public boolean equals(Object obj) {
      if (obj == this) {
         return true;
      } else if (!(obj instanceof Xpp3Dom)) {
         return false;
      } else {
         Xpp3Dom dom;
         label56: {
            dom = (Xpp3Dom)obj;
            if (this.name == null) {
               if (dom.name == null) {
                  break label56;
               }
            } else if (this.name.equals(dom.name)) {
               break label56;
            }

            return false;
         }

         label49: {
            if (this.value == null) {
               if (dom.value == null) {
                  break label49;
               }
            } else if (this.value.equals(dom.value)) {
               break label49;
            }

            return false;
         }

         if (this.attributes == null) {
            if (dom.attributes != null) {
               return false;
            }
         } else if (!this.attributes.equals(dom.attributes)) {
            return false;
         }

         if (this.childList == null) {
            if (dom.childList != null) {
               return false;
            }
         } else if (!this.childList.equals(dom.childList)) {
            return false;
         }

         return true;
      }
   }

   public int hashCode() {
      int result = 17;
      result = 37 * result + (this.name != null ? this.name.hashCode() : 0);
      result = 37 * result + (this.value != null ? this.value.hashCode() : 0);
      result = 37 * result + (this.attributes != null ? this.attributes.hashCode() : 0);
      result = 37 * result + (this.childList != null ? this.childList.hashCode() : 0);
      return result;
   }

   public String toString() {
      StringWriter writer = new StringWriter();
      XMLWriter xmlWriter = new PrettyPrintXMLWriter(writer, "UTF-8", (String)null);
      Xpp3DomWriter.write((XMLWriter)xmlWriter, this);
      return writer.toString();
   }

   public String toUnescapedString() {
      StringWriter writer = new StringWriter();
      XMLWriter xmlWriter = new PrettyPrintXMLWriter(writer, "UTF-8", (String)null);
      Xpp3DomWriter.write(xmlWriter, this, false);
      return writer.toString();
   }

   public static boolean isNotEmpty(String str) {
      return str != null && str.length() > 0;
   }

   public static boolean isEmpty(String str) {
      return str == null || str.trim().length() == 0;
   }
}
