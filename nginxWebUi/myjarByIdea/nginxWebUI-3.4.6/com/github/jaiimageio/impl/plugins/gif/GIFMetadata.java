package com.github.jaiimageio.impl.plugins.gif;

import javax.imageio.metadata.IIOInvalidTreeException;
import javax.imageio.metadata.IIOMetadata;
import org.w3c.dom.Node;

abstract class GIFMetadata extends IIOMetadata {
   static final int UNDEFINED_INTEGER_VALUE = -1;

   protected static void fatal(Node node, String reason) throws IIOInvalidTreeException {
      throw new IIOInvalidTreeException(reason, node);
   }

   protected static String getStringAttribute(Node node, String name, String defaultValue, boolean required, String[] range) throws IIOInvalidTreeException {
      Node attr = node.getAttributes().getNamedItem(name);
      if (attr == null) {
         if (!required) {
            return defaultValue;
         }

         fatal(node, "Required attribute " + name + " not present!");
      }

      String value = attr.getNodeValue();
      if (range != null) {
         if (value == null) {
            fatal(node, "Null value for " + node.getNodeName() + " attribute " + name + "!");
         }

         boolean validValue = false;
         int len = range.length;

         for(int i = 0; i < len; ++i) {
            if (value.equals(range[i])) {
               validValue = true;
               break;
            }
         }

         if (!validValue) {
            fatal(node, "Bad value for " + node.getNodeName() + " attribute " + name + "!");
         }
      }

      return value;
   }

   protected static int getIntAttribute(Node node, String name, int defaultValue, boolean required, boolean bounded, int min, int max) throws IIOInvalidTreeException {
      String value = getStringAttribute(node, name, (String)null, required, (String[])null);
      if (value != null && !"".equals(value)) {
         int intValue = defaultValue;

         try {
            intValue = Integer.parseInt(value);
         } catch (NumberFormatException var10) {
            fatal(node, "Bad value for " + node.getNodeName() + " attribute " + name + "!");
         }

         if (bounded && (intValue < min || intValue > max)) {
            fatal(node, "Bad value for " + node.getNodeName() + " attribute " + name + "!");
         }

         return intValue;
      } else {
         return defaultValue;
      }
   }

   protected static float getFloatAttribute(Node node, String name, float defaultValue, boolean required) throws IIOInvalidTreeException {
      String value = getStringAttribute(node, name, (String)null, required, (String[])null);
      return value == null ? defaultValue : Float.parseFloat(value);
   }

   protected static int getIntAttribute(Node node, String name, boolean bounded, int min, int max) throws IIOInvalidTreeException {
      return getIntAttribute(node, name, -1, true, bounded, min, max);
   }

   protected static float getFloatAttribute(Node node, String name) throws IIOInvalidTreeException {
      return getFloatAttribute(node, name, -1.0F, true);
   }

   protected static boolean getBooleanAttribute(Node node, String name, boolean defaultValue, boolean required) throws IIOInvalidTreeException {
      Node attr = node.getAttributes().getNamedItem(name);
      if (attr == null) {
         if (!required) {
            return defaultValue;
         }

         fatal(node, "Required attribute " + name + " not present!");
      }

      String value = attr.getNodeValue();
      if (value.equalsIgnoreCase("TRUE")) {
         return true;
      } else if (value.equalsIgnoreCase("FALSE")) {
         return false;
      } else {
         fatal(node, "Attribute " + name + " must be 'TRUE' or 'FALSE'!");
         return false;
      }
   }

   protected static boolean getBooleanAttribute(Node node, String name) throws IIOInvalidTreeException {
      return getBooleanAttribute(node, name, false, true);
   }

   protected static int getEnumeratedAttribute(Node node, String name, String[] legalNames, int defaultValue, boolean required) throws IIOInvalidTreeException {
      Node attr = node.getAttributes().getNamedItem(name);
      if (attr == null) {
         if (!required) {
            return defaultValue;
         }

         fatal(node, "Required attribute " + name + " not present!");
      }

      String value = attr.getNodeValue();

      for(int i = 0; i < legalNames.length; ++i) {
         if (value.equals(legalNames[i])) {
            return i;
         }
      }

      fatal(node, "Illegal value for attribute " + name + "!");
      return -1;
   }

   protected static int getEnumeratedAttribute(Node node, String name, String[] legalNames) throws IIOInvalidTreeException {
      return getEnumeratedAttribute(node, name, legalNames, -1, true);
   }

   protected static String getAttribute(Node node, String name, String defaultValue, boolean required) throws IIOInvalidTreeException {
      Node attr = node.getAttributes().getNamedItem(name);
      if (attr == null) {
         if (!required) {
            return defaultValue;
         }

         fatal(node, "Required attribute " + name + " not present!");
      }

      return attr.getNodeValue();
   }

   protected static String getAttribute(Node node, String name) throws IIOInvalidTreeException {
      return getAttribute(node, name, (String)null, true);
   }

   protected GIFMetadata(boolean standardMetadataFormatSupported, String nativeMetadataFormatName, String nativeMetadataFormatClassName, String[] extraMetadataFormatNames, String[] extraMetadataFormatClassNames) {
      super(standardMetadataFormatSupported, nativeMetadataFormatName, nativeMetadataFormatClassName, extraMetadataFormatNames, extraMetadataFormatClassNames);
   }

   public void mergeTree(String formatName, Node root) throws IIOInvalidTreeException {
      if (formatName.equals(this.nativeMetadataFormatName)) {
         if (root == null) {
            throw new IllegalArgumentException("root == null!");
         }

         this.mergeNativeTree(root);
      } else {
         if (!formatName.equals("javax_imageio_1.0")) {
            throw new IllegalArgumentException("Not a recognized format!");
         }

         if (root == null) {
            throw new IllegalArgumentException("root == null!");
         }

         this.mergeStandardTree(root);
      }

   }

   protected byte[] getColorTable(Node colorTableNode, String entryNodeName, boolean lengthExpected, int expectedLength) throws IIOInvalidTreeException {
      byte[] red = new byte[256];
      byte[] green = new byte[256];
      byte[] blue = new byte[256];
      int maxIndex = -1;
      Node entry = colorTableNode.getFirstChild();
      if (entry == null) {
         fatal(colorTableNode, "Palette has no entries!");
      }

      int numEntries;
      while(entry != null) {
         if (!entry.getNodeName().equals(entryNodeName)) {
            fatal(colorTableNode, "Only a " + entryNodeName + " may be a child of a " + entry.getNodeName() + "!");
         }

         numEntries = getIntAttribute(entry, "index", true, 0, 255);
         if (numEntries > maxIndex) {
            maxIndex = numEntries;
         }

         red[numEntries] = (byte)getIntAttribute(entry, "red", true, 0, 255);
         green[numEntries] = (byte)getIntAttribute(entry, "green", true, 0, 255);
         blue[numEntries] = (byte)getIntAttribute(entry, "blue", true, 0, 255);
         entry = entry.getNextSibling();
      }

      numEntries = maxIndex + 1;
      if (lengthExpected && numEntries != expectedLength) {
         fatal(colorTableNode, "Unexpected length for palette!");
      }

      byte[] colorTable = new byte[3 * numEntries];
      int i = 0;

      for(int j = 0; i < numEntries; ++i) {
         colorTable[j++] = red[i];
         colorTable[j++] = green[i];
         colorTable[j++] = blue[i];
      }

      return colorTable;
   }

   protected abstract void mergeNativeTree(Node var1) throws IIOInvalidTreeException;

   protected abstract void mergeStandardTree(Node var1) throws IIOInvalidTreeException;
}
