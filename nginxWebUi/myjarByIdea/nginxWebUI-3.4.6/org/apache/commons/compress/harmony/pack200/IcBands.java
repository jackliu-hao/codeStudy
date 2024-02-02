package org.apache.commons.compress.harmony.pack200;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class IcBands extends BandSet {
   private final Set innerClasses = new TreeSet();
   private final CpBands cpBands;
   private int bit16Count = 0;
   private final Map outerToInner = new HashMap();

   public IcBands(SegmentHeader segmentHeader, CpBands cpBands, int effort) {
      super(effort, segmentHeader);
      this.cpBands = cpBands;
   }

   public void finaliseBands() {
      this.segmentHeader.setIc_count(this.innerClasses.size());
   }

   public void pack(OutputStream out) throws IOException, Pack200Exception {
      PackingUtils.log("Writing internal class bands...");
      int[] ic_this_class = new int[this.innerClasses.size()];
      int[] ic_flags = new int[this.innerClasses.size()];
      int[] ic_outer_class = new int[this.bit16Count];
      int[] ic_name = new int[this.bit16Count];
      int index2 = 0;
      List innerClassesList = new ArrayList(this.innerClasses);

      for(int i = 0; i < ic_this_class.length; ++i) {
         IcTuple icTuple = (IcTuple)innerClassesList.get(i);
         ic_this_class[i] = icTuple.C.getIndex();
         ic_flags[i] = icTuple.F;
         if ((icTuple.F & 65536) != 0) {
            ic_outer_class[index2] = icTuple.C2 == null ? 0 : icTuple.C2.getIndex() + 1;
            ic_name[index2] = icTuple.N == null ? 0 : icTuple.N.getIndex() + 1;
            ++index2;
         }
      }

      byte[] encodedBand = this.encodeBandInt("ic_this_class", ic_this_class, Codec.UDELTA5);
      out.write(encodedBand);
      PackingUtils.log("Wrote " + encodedBand.length + " bytes from ic_this_class[" + ic_this_class.length + "]");
      encodedBand = this.encodeBandInt("ic_flags", ic_flags, Codec.UNSIGNED5);
      out.write(encodedBand);
      PackingUtils.log("Wrote " + encodedBand.length + " bytes from ic_flags[" + ic_flags.length + "]");
      encodedBand = this.encodeBandInt("ic_outer_class", ic_outer_class, Codec.DELTA5);
      out.write(encodedBand);
      PackingUtils.log("Wrote " + encodedBand.length + " bytes from ic_outer_class[" + ic_outer_class.length + "]");
      encodedBand = this.encodeBandInt("ic_name", ic_name, Codec.DELTA5);
      out.write(encodedBand);
      PackingUtils.log("Wrote " + encodedBand.length + " bytes from ic_name[" + ic_name.length + "]");
   }

   public void addInnerClass(String name, String outerName, String innerName, int flags) {
      IcTuple icTuple;
      if (outerName == null && innerName == null) {
         icTuple = new IcTuple(this.cpBands.getCPClass(name), flags, (CPClass)null, (CPUTF8)null);
         this.addToMap(this.getOuter(name), icTuple);
         this.innerClasses.add(icTuple);
      } else if (this.namesArePredictable(name, outerName, innerName)) {
         icTuple = new IcTuple(this.cpBands.getCPClass(name), flags, (CPClass)null, (CPUTF8)null);
         this.addToMap(outerName, icTuple);
         this.innerClasses.add(icTuple);
      } else {
         flags |= 65536;
         icTuple = new IcTuple(this.cpBands.getCPClass(name), flags, this.cpBands.getCPClass(outerName), this.cpBands.getCPUtf8(innerName));
         boolean added = this.innerClasses.add(icTuple);
         if (added) {
            ++this.bit16Count;
            this.addToMap(outerName, icTuple);
         }
      }

   }

   public List getInnerClassesForOuter(String outerClassName) {
      return (List)this.outerToInner.get(outerClassName);
   }

   private String getOuter(String name) {
      return name.substring(0, name.lastIndexOf(36));
   }

   private void addToMap(String outerName, IcTuple icTuple) {
      List tuples = (List)this.outerToInner.get(outerName);
      if (tuples == null) {
         List tuples = new ArrayList();
         this.outerToInner.put(outerName, tuples);
         tuples.add(icTuple);
      } else {
         Iterator iterator = tuples.iterator();

         while(iterator.hasNext()) {
            IcTuple icT = (IcTuple)iterator.next();
            if (icTuple.equals(icT)) {
               return;
            }
         }

         tuples.add(icTuple);
      }

   }

   private boolean namesArePredictable(String name, String outerName, String innerName) {
      return name.equals(outerName + '$' + innerName) && innerName.indexOf(36) == -1;
   }

   public IcTuple getIcTuple(CPClass inner) {
      Iterator iterator = this.innerClasses.iterator();

      IcTuple icTuple;
      do {
         if (!iterator.hasNext()) {
            return null;
         }

         icTuple = (IcTuple)iterator.next();
      } while(!icTuple.C.equals(inner));

      return icTuple;
   }

   class IcTuple implements Comparable {
      protected CPClass C;
      protected int F;
      protected CPClass C2;
      protected CPUTF8 N;

      public IcTuple(CPClass C, int F, CPClass C2, CPUTF8 N) {
         this.C = C;
         this.F = F;
         this.C2 = C2;
         this.N = N;
      }

      public boolean equals(Object o) {
         if (!(o instanceof IcTuple)) {
            return false;
         } else {
            boolean var10000;
            label48: {
               IcTuple icT = (IcTuple)o;
               if (this.C.equals(icT.C) && this.F == icT.F) {
                  label42: {
                     if (this.C2 != null) {
                        if (!this.C2.equals(icT.C2)) {
                           break label42;
                        }
                     } else if (icT.C2 != null) {
                        break label42;
                     }

                     if (this.N != null) {
                        if (this.N.equals(icT.N)) {
                           break label48;
                        }
                     } else if (icT.N == null) {
                        break label48;
                     }
                  }
               }

               var10000 = false;
               return var10000;
            }

            var10000 = true;
            return var10000;
         }
      }

      public String toString() {
         return this.C.toString();
      }

      public int compareTo(Object arg0) {
         return this.C.compareTo(((IcTuple)arg0).C);
      }

      public boolean isAnonymous() {
         String className = this.C.toString();
         String innerName = className.substring(className.lastIndexOf(36) + 1);
         return Character.isDigit(innerName.charAt(0));
      }
   }
}
