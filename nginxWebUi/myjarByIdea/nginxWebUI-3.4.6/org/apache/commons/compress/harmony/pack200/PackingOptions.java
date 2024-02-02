package org.apache.commons.compress.harmony.pack200;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.objectweb.asm.Attribute;

public class PackingOptions {
   public static final String STRIP = "strip";
   public static final String ERROR = "error";
   public static final String PASS = "pass";
   public static final String KEEP = "keep";
   private boolean gzip = true;
   private boolean stripDebug = false;
   private boolean keepFileOrder = true;
   private long segmentLimit = 1000000L;
   private int effort = 5;
   private String deflateHint = "keep";
   private String modificationTime = "keep";
   private List passFiles;
   private String unknownAttributeAction = "pass";
   private Map classAttributeActions;
   private Map fieldAttributeActions;
   private Map methodAttributeActions;
   private Map codeAttributeActions;
   private boolean verbose = false;
   private String logFile;
   private Attribute[] unknownAttributeTypes;

   public boolean isGzip() {
      return this.gzip;
   }

   public void setGzip(boolean gzip) {
      this.gzip = gzip;
   }

   public boolean isStripDebug() {
      return this.stripDebug;
   }

   public void setStripDebug(boolean stripDebug) {
      this.stripDebug = stripDebug;
   }

   public boolean isKeepFileOrder() {
      return this.keepFileOrder;
   }

   public void setKeepFileOrder(boolean keepFileOrder) {
      this.keepFileOrder = keepFileOrder;
   }

   public long getSegmentLimit() {
      return this.segmentLimit;
   }

   public void setSegmentLimit(long segmentLimit) {
      this.segmentLimit = segmentLimit;
   }

   public int getEffort() {
      return this.effort;
   }

   public void setEffort(int effort) {
      this.effort = effort;
   }

   public String getDeflateHint() {
      return this.deflateHint;
   }

   public boolean isKeepDeflateHint() {
      return "keep".equals(this.deflateHint);
   }

   public void setDeflateHint(String deflateHint) {
      if (!"keep".equals(deflateHint) && !"true".equals(deflateHint) && !"false".equals(deflateHint)) {
         throw new IllegalArgumentException("Bad argument: -H " + deflateHint + " ? deflate hint should be either true, false or keep (default)");
      } else {
         this.deflateHint = deflateHint;
      }
   }

   public String getModificationTime() {
      return this.modificationTime;
   }

   public void setModificationTime(String modificationTime) {
      if (!"keep".equals(modificationTime) && !"latest".equals(modificationTime)) {
         throw new IllegalArgumentException("Bad argument: -m " + modificationTime + " ? transmit modtimes should be either latest or keep (default)");
      } else {
         this.modificationTime = modificationTime;
      }
   }

   public boolean isPassFile(String passFileName) {
      if (this.passFiles != null) {
         Iterator iterator = this.passFiles.iterator();

         while(iterator.hasNext()) {
            String pass = (String)iterator.next();
            if (passFileName.equals(pass)) {
               return true;
            }

            if (!pass.endsWith(".class")) {
               if (!pass.endsWith("/")) {
                  pass = pass + "/";
               }

               return passFileName.startsWith(pass);
            }
         }
      }

      return false;
   }

   public void addPassFile(String passFileName) {
      if (this.passFiles == null) {
         this.passFiles = new ArrayList();
      }

      String fileSeparator = System.getProperty("file.separator");
      if (fileSeparator.equals("\\")) {
         fileSeparator = fileSeparator + "\\";
      }

      passFileName = passFileName.replaceAll(fileSeparator, "/");
      this.passFiles.add(passFileName);
   }

   public void removePassFile(String passFileName) {
      this.passFiles.remove(passFileName);
   }

   public String getUnknownAttributeAction() {
      return this.unknownAttributeAction;
   }

   public void setUnknownAttributeAction(String unknownAttributeAction) {
      this.unknownAttributeAction = unknownAttributeAction;
      if (!"pass".equals(unknownAttributeAction) && !"error".equals(unknownAttributeAction) && !"strip".equals(unknownAttributeAction)) {
         throw new RuntimeException("Incorrect option for -U, " + unknownAttributeAction);
      }
   }

   public void addClassAttributeAction(String attributeName, String action) {
      if (this.classAttributeActions == null) {
         this.classAttributeActions = new HashMap();
      }

      this.classAttributeActions.put(attributeName, action);
   }

   public void addFieldAttributeAction(String attributeName, String action) {
      if (this.fieldAttributeActions == null) {
         this.fieldAttributeActions = new HashMap();
      }

      this.fieldAttributeActions.put(attributeName, action);
   }

   public void addMethodAttributeAction(String attributeName, String action) {
      if (this.methodAttributeActions == null) {
         this.methodAttributeActions = new HashMap();
      }

      this.methodAttributeActions.put(attributeName, action);
   }

   public void addCodeAttributeAction(String attributeName, String action) {
      if (this.codeAttributeActions == null) {
         this.codeAttributeActions = new HashMap();
      }

      this.codeAttributeActions.put(attributeName, action);
   }

   public boolean isVerbose() {
      return this.verbose;
   }

   public void setVerbose(boolean verbose) {
      this.verbose = verbose;
   }

   public void setQuiet(boolean quiet) {
      this.verbose = !quiet;
   }

   public String getLogFile() {
      return this.logFile;
   }

   public void setLogFile(String logFile) {
      this.logFile = logFile;
   }

   private void addOrUpdateAttributeActions(List prototypes, Map attributeActions, int tag) {
      if (attributeActions != null && attributeActions.size() > 0) {
         Iterator iteratorI = attributeActions.keySet().iterator();

         while(iteratorI.hasNext()) {
            String name = (String)iteratorI.next();
            String action = (String)attributeActions.get(name);
            boolean prototypeExists = false;
            Iterator iteratorJ = prototypes.iterator();

            while(iteratorJ.hasNext()) {
               NewAttribute newAttribute = (NewAttribute)iteratorJ.next();
               if (newAttribute.type.equals(name)) {
                  newAttribute.addContext(tag);
                  prototypeExists = true;
                  break;
               }
            }

            if (!prototypeExists) {
               Object newAttribute;
               if ("error".equals(action)) {
                  newAttribute = new NewAttribute.ErrorAttribute(name, tag);
               } else if ("strip".equals(action)) {
                  newAttribute = new NewAttribute.StripAttribute(name, tag);
               } else if ("pass".equals(action)) {
                  newAttribute = new NewAttribute.PassAttribute(name, tag);
               } else {
                  newAttribute = new NewAttribute(name, action, tag);
               }

               prototypes.add(newAttribute);
            }
         }
      }

   }

   public Attribute[] getUnknownAttributePrototypes() {
      if (this.unknownAttributeTypes == null) {
         List prototypes = new ArrayList();
         this.addOrUpdateAttributeActions(prototypes, this.classAttributeActions, 0);
         this.addOrUpdateAttributeActions(prototypes, this.methodAttributeActions, 2);
         this.addOrUpdateAttributeActions(prototypes, this.fieldAttributeActions, 1);
         this.addOrUpdateAttributeActions(prototypes, this.codeAttributeActions, 3);
         this.unknownAttributeTypes = (Attribute[])((Attribute[])prototypes.toArray(new Attribute[0]));
      }

      return this.unknownAttributeTypes;
   }

   public String getUnknownClassAttributeAction(String type) {
      if (this.classAttributeActions == null) {
         return this.unknownAttributeAction;
      } else {
         String action = (String)this.classAttributeActions.get(type);
         if (action == null) {
            action = this.unknownAttributeAction;
         }

         return action;
      }
   }

   public String getUnknownMethodAttributeAction(String type) {
      if (this.methodAttributeActions == null) {
         return this.unknownAttributeAction;
      } else {
         String action = (String)this.methodAttributeActions.get(type);
         if (action == null) {
            action = this.unknownAttributeAction;
         }

         return action;
      }
   }

   public String getUnknownFieldAttributeAction(String type) {
      if (this.fieldAttributeActions == null) {
         return this.unknownAttributeAction;
      } else {
         String action = (String)this.fieldAttributeActions.get(type);
         if (action == null) {
            action = this.unknownAttributeAction;
         }

         return action;
      }
   }

   public String getUnknownCodeAttributeAction(String type) {
      if (this.codeAttributeActions == null) {
         return this.unknownAttributeAction;
      } else {
         String action = (String)this.codeAttributeActions.get(type);
         if (action == null) {
            action = this.unknownAttributeAction;
         }

         return action;
      }
   }
}
