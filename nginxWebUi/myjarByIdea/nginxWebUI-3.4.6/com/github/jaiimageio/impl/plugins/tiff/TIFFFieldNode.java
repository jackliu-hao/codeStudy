package com.github.jaiimageio.impl.plugins.tiff;

import com.github.jaiimageio.plugins.tiff.TIFFDirectory;
import com.github.jaiimageio.plugins.tiff.TIFFField;
import com.github.jaiimageio.plugins.tiff.TIFFTag;
import com.github.jaiimageio.plugins.tiff.TIFFTagSet;
import java.util.Arrays;
import java.util.List;
import javax.imageio.metadata.IIOMetadataNode;
import org.w3c.dom.Node;

public class TIFFFieldNode extends IIOMetadataNode {
   private boolean isIFD;
   private Boolean isInitialized;
   private TIFFField field;

   private static String getNodeName(TIFFField f) {
      return f.getData() instanceof TIFFDirectory ? "TIFFIFD" : "TIFFField";
   }

   public TIFFFieldNode(TIFFField field) {
      super(getNodeName(field));
      this.isInitialized = Boolean.FALSE;
      this.isIFD = field.getData() instanceof TIFFDirectory;
      this.field = field;
      TIFFTag tag = field.getTag();
      int tagNumber = tag.getNumber();
      String tagName = tag.getName();
      if (this.isIFD) {
         if (tagNumber != 0) {
            this.setAttribute("parentTagNumber", Integer.toString(tagNumber));
         }

         if (tagName != null) {
            this.setAttribute("parentTagName", tagName);
         }

         TIFFDirectory dir = (TIFFDirectory)field.getData();
         TIFFTagSet[] tagSets = dir.getTagSets();
         if (tagSets != null) {
            String tagSetNames = "";

            for(int i = 0; i < tagSets.length; ++i) {
               tagSetNames = tagSetNames + tagSets[i].getClass().getName();
               if (i != tagSets.length - 1) {
                  tagSetNames = tagSetNames + ",";
               }
            }

            this.setAttribute("tagSets", tagSetNames);
         }
      } else {
         this.setAttribute("number", Integer.toString(tagNumber));
         this.setAttribute("name", tagName);
      }

   }

   private synchronized void initialize() {
      if (this.isInitialized != Boolean.TRUE) {
         int i;
         int value;
         if (this.isIFD) {
            TIFFDirectory dir = (TIFFDirectory)this.field.getData();
            TIFFField[] fields = dir.getTIFFFields();
            if (fields != null) {
               TIFFTagSet[] tagSets = dir.getTagSets();
               List tagSetList = Arrays.asList(tagSets);
               i = fields.length;

               for(value = 0; value < i; ++value) {
                  TIFFField f = fields[value];
                  int tagNumber = f.getTagNumber();
                  TIFFIFD.getTag(tagNumber, tagSetList);
                  Node node = f.getAsNativeNode();
                  if (node != null) {
                     this.appendChild(node);
                  }
               }
            }
         } else {
            int count = this.field.getCount();
            IIOMetadataNode child;
            if (this.field.getType() != 7) {
               StringBuilder var10002 = (new StringBuilder()).append("TIFF");
               TIFFField var10003 = this.field;
               child = new IIOMetadataNode(var10002.append(TIFFField.getTypeName(this.field.getType())).append("s").toString());
               TIFFTag tag = this.field.getTag();

               for(int i = 0; i < count; ++i) {
                  var10002 = (new StringBuilder()).append("TIFF");
                  var10003 = this.field;
                  IIOMetadataNode cchild = new IIOMetadataNode(var10002.append(TIFFField.getTypeName(this.field.getType())).toString());
                  cchild.setAttribute("value", this.field.getValueAsString(i));
                  if (tag.hasValueNames() && this.field.isIntegral()) {
                     value = this.field.getAsInt(i);
                     String name = tag.getValueName(value);
                     if (name != null) {
                        cchild.setAttribute("description", name);
                     }
                  }

                  child.appendChild(cchild);
               }
            } else {
               child = new IIOMetadataNode("TIFFUndefined");
               byte[] data = this.field.getAsBytes();
               StringBuffer sb = new StringBuffer();

               for(i = 0; i < count; ++i) {
                  sb.append(Integer.toString(data[i] & 255));
                  if (i < count - 1) {
                     sb.append(",");
                  }
               }

               child.setAttribute("value", sb.toString());
            }

            this.appendChild(child);
         }

         this.isInitialized = Boolean.TRUE;
      }
   }

   public Node appendChild(Node newChild) {
      if (newChild == null) {
         throw new IllegalArgumentException("newChild == null!");
      } else {
         return super.insertBefore(newChild, (Node)null);
      }
   }

   public boolean hasChildNodes() {
      this.initialize();
      return super.hasChildNodes();
   }

   public int getLength() {
      this.initialize();
      return super.getLength();
   }

   public Node getFirstChild() {
      this.initialize();
      return super.getFirstChild();
   }

   public Node getLastChild() {
      this.initialize();
      return super.getLastChild();
   }

   public Node getPreviousSibling() {
      this.initialize();
      return super.getPreviousSibling();
   }

   public Node getNextSibling() {
      this.initialize();
      return super.getNextSibling();
   }

   public Node insertBefore(Node newChild, Node refChild) {
      this.initialize();
      return super.insertBefore(newChild, refChild);
   }

   public Node replaceChild(Node newChild, Node oldChild) {
      this.initialize();
      return super.replaceChild(newChild, oldChild);
   }

   public Node removeChild(Node oldChild) {
      this.initialize();
      return super.removeChild(oldChild);
   }

   public Node cloneNode(boolean deep) {
      this.initialize();
      return super.cloneNode(deep);
   }
}
