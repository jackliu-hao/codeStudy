package com.github.jaiimageio.impl.plugins.tiff;

import com.github.jaiimageio.plugins.tiff.BaselineTIFFTagSet;
import com.github.jaiimageio.plugins.tiff.TIFFDirectory;
import com.github.jaiimageio.plugins.tiff.TIFFField;
import com.github.jaiimageio.plugins.tiff.TIFFTag;
import com.github.jaiimageio.plugins.tiff.TIFFTagSet;
import java.io.EOFException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Vector;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.ImageOutputStream;

public class TIFFIFD extends TIFFDirectory {
   private long stripOrTileByteCountsPosition;
   private long stripOrTileOffsetsPosition;
   private long lastPosition;

   public static TIFFTag getTag(int tagNumber, List tagSets) {
      Iterator iter = tagSets.iterator();

      TIFFTag tag;
      do {
         if (!iter.hasNext()) {
            return null;
         }

         TIFFTagSet tagSet = (TIFFTagSet)iter.next();
         tag = tagSet.getTag(tagNumber);
      } while(tag == null);

      return tag;
   }

   public static TIFFTag getTag(String tagName, List tagSets) {
      Iterator iter = tagSets.iterator();

      TIFFTag tag;
      do {
         if (!iter.hasNext()) {
            return null;
         }

         TIFFTagSet tagSet = (TIFFTagSet)iter.next();
         tag = tagSet.getTag(tagName);
      } while(tag == null);

      return tag;
   }

   private static void writeTIFFFieldToStream(TIFFField field, ImageOutputStream stream) throws IOException {
      int count = field.getCount();
      Object data = field.getData();
      int i;
      switch (field.getType()) {
         case 1:
         case 6:
         case 7:
            stream.write((byte[])((byte[])data));
            break;
         case 2:
            for(i = 0; i < count; ++i) {
               String s = ((String[])((String[])data))[i];
               int length = s.length();

               for(int j = 0; j < length; ++j) {
                  stream.writeByte(s.charAt(j) & 255);
               }

               stream.writeByte(0);
            }

            return;
         case 3:
            stream.writeChars((char[])((char[])data), 0, ((char[])((char[])data)).length);
            break;
         case 4:
            for(i = 0; i < count; ++i) {
               stream.writeInt((int)((long[])((long[])data))[i]);
            }

            return;
         case 5:
            for(i = 0; i < count; ++i) {
               long num = ((long[][])((long[][])data))[i][0];
               long den = ((long[][])((long[][])data))[i][1];
               stream.writeInt((int)num);
               stream.writeInt((int)den);
            }

            return;
         case 8:
            stream.writeShorts((short[])((short[])data), 0, ((short[])((short[])data)).length);
            break;
         case 9:
            stream.writeInts((int[])((int[])data), 0, ((int[])((int[])data)).length);
            break;
         case 10:
            for(i = 0; i < count; ++i) {
               stream.writeInt(((int[][])((int[][])data))[i][0]);
               stream.writeInt(((int[][])((int[][])data))[i][1]);
            }

            return;
         case 11:
            stream.writeFloats((float[])((float[])data), 0, ((float[])((float[])data)).length);
            break;
         case 12:
            stream.writeDoubles((double[])((double[])data), 0, ((double[])((double[])data)).length);
            break;
         case 13:
            stream.writeInt(0);
      }

   }

   public TIFFIFD(List tagSets, TIFFTag parentTag) {
      super((TIFFTagSet[])((TIFFTagSet[])tagSets.toArray(new TIFFTagSet[tagSets.size()])), parentTag);
      this.stripOrTileByteCountsPosition = -1L;
      this.stripOrTileOffsetsPosition = -1L;
      this.lastPosition = -1L;
   }

   public TIFFIFD(List tagSets) {
      this(tagSets, (TIFFTag)null);
   }

   public List getTagSetList() {
      return Arrays.asList(this.getTagSets());
   }

   public Iterator iterator() {
      return Arrays.asList(this.getTIFFFields()).iterator();
   }

   public void initialize(ImageInputStream stream, boolean ignoreUnknownFields) throws IOException {
      this.removeTIFFFields();
      List tagSetList = this.getTagSetList();
      int numEntries = stream.readUnsignedShort();

      for(int i = 0; i < numEntries; ++i) {
         int tag = stream.readUnsignedShort();
         int type = stream.readUnsignedShort();
         int count = (int)stream.readUnsignedInt();
         TIFFTag tiffTag = getTag(tag, tagSetList);
         if (ignoreUnknownFields && tiffTag == null) {
            stream.skipBytes(4);
         } else {
            long nextTagOffset = stream.getStreamPosition() + 4L;
            int sizeOfType = TIFFTag.getSizeOfType(type);
            if (count * sizeOfType > 4) {
               long value = stream.readUnsignedInt();
               stream.seek(value);
            }

            if (tag != 279 && tag != 325 && tag != 514) {
               if (tag == 273 || tag == 324 || tag == 513) {
                  this.stripOrTileOffsetsPosition = stream.getStreamPosition();
               }
            } else {
               this.stripOrTileByteCountsPosition = stream.getStreamPosition();
            }

            Object obj = null;

            try {
               int prevIndex;
               int index;
               int c;
               switch (type) {
                  case 1:
                  case 2:
                  case 6:
                  case 7:
                     byte[] bvalues = new byte[count];
                     stream.readFully(bvalues, 0, count);
                     if (type != 2) {
                        obj = bvalues;
                        break;
                     }

                     Vector v = new Vector();
                     boolean inString = false;
                     prevIndex = 0;

                     for(index = 0; index <= count; ++index) {
                        if (index < count && bvalues[index] != 0) {
                           if (!inString) {
                              prevIndex = index;
                              inString = true;
                           }
                        } else if (inString) {
                           String s = new String(bvalues, prevIndex, index - prevIndex);
                           v.add(s);
                           inString = false;
                        }
                     }

                     count = v.size();
                     String[] strings;
                     if (count != 0) {
                        strings = new String[count];

                        for(c = 0; c < count; ++c) {
                           strings[c] = (String)v.elementAt(c);
                        }
                     } else {
                        count = 1;
                        strings = new String[]{""};
                     }

                     obj = strings;
                     break;
                  case 3:
                     char[] cvalues = new char[count];

                     for(int j = 0; j < count; ++j) {
                        cvalues[j] = (char)stream.readUnsignedShort();
                     }

                     obj = cvalues;
                     break;
                  case 4:
                  case 13:
                     long[] lvalues = new long[count];

                     for(prevIndex = 0; prevIndex < count; ++prevIndex) {
                        lvalues[prevIndex] = stream.readUnsignedInt();
                     }

                     obj = lvalues;
                     break;
                  case 5:
                     long[][] llvalues = new long[count][2];

                     for(index = 0; index < count; ++index) {
                        llvalues[index][0] = stream.readUnsignedInt();
                        llvalues[index][1] = stream.readUnsignedInt();
                     }

                     obj = llvalues;
                     break;
                  case 8:
                     short[] svalues = new short[count];

                     for(c = 0; c < count; ++c) {
                        svalues[c] = stream.readShort();
                     }

                     obj = svalues;
                     break;
                  case 9:
                     int[] ivalues = new int[count];

                     for(int j = 0; j < count; ++j) {
                        ivalues[j] = stream.readInt();
                     }

                     obj = ivalues;
                     break;
                  case 10:
                     int[][] iivalues = new int[count][2];

                     for(int j = 0; j < count; ++j) {
                        iivalues[j][0] = stream.readInt();
                        iivalues[j][1] = stream.readInt();
                     }

                     obj = iivalues;
                     break;
                  case 11:
                     float[] fvalues = new float[count];

                     for(int j = 0; j < count; ++j) {
                        fvalues[j] = stream.readFloat();
                     }

                     obj = fvalues;
                     break;
                  case 12:
                     double[] dvalues = new double[count];

                     for(int j = 0; j < count; ++j) {
                        dvalues[j] = stream.readDouble();
                     }

                     obj = dvalues;
               }
            } catch (EOFException var24) {
               if (BaselineTIFFTagSet.getInstance().getTag(tag) == null) {
                  throw var24;
               }
            }

            if (tiffTag != null && tiffTag.isDataTypeOK(type) && tiffTag.isIFDPointer() && obj != null) {
               stream.mark();
               stream.seek(((long[])((long[])obj))[0]);
               List tagSets = new ArrayList(1);
               tagSets.add(tiffTag.getTagSet());
               TIFFIFD subIFD = new TIFFIFD(tagSets);
               subIFD.initialize(stream, ignoreUnknownFields);
               obj = subIFD;
               stream.reset();
            }

            if (tiffTag == null) {
               tiffTag = new TIFFTag((String)null, tag, 1 << type, (TIFFTagSet)null);
            }

            if (obj != null) {
               TIFFField f = new TIFFField(tiffTag, type, count, obj);
               this.addTIFFField(f);
            }

            stream.seek(nextTagOffset);
         }
      }

      this.lastPosition = stream.getStreamPosition();
   }

   public void writeToStream(ImageOutputStream stream) throws IOException {
      int numFields = this.getNumTIFFFields();
      stream.writeShort(numFields);
      long nextSpace = stream.getStreamPosition() + (long)(12 * numFields) + 4L;

      for(Iterator iter = this.iterator(); iter.hasNext(); stream.reset()) {
         TIFFField f = (TIFFField)iter.next();
         TIFFTag tag = f.getTag();
         int type = f.getType();
         int count = f.getCount();
         if (type == 0) {
            type = 7;
         }

         int size = count * TIFFTag.getSizeOfType(type);
         int tagNumber;
         if (type == 2) {
            tagNumber = 0;

            for(int i = 0; i < count; ++i) {
               tagNumber += f.getAsString(i).length() + 1;
            }

            count = tagNumber;
            size = tagNumber;
         }

         tagNumber = f.getTagNumber();
         stream.writeShort(tagNumber);
         stream.writeShort(type);
         stream.writeInt(count);
         stream.writeInt(0);
         stream.mark();
         stream.skipBytes(-4);
         long pos;
         if (size <= 4 && !tag.isIFDPointer()) {
            pos = stream.getStreamPosition();
            writeTIFFFieldToStream(f, stream);
         } else {
            nextSpace = nextSpace + 3L & -4L;
            stream.writeInt((int)nextSpace);
            stream.seek(nextSpace);
            pos = nextSpace;
            if (tag.isIFDPointer()) {
               TIFFIFD subIFD = (TIFFIFD)f.getData();
               subIFD.writeToStream(stream);
               nextSpace = subIFD.lastPosition;
            } else {
               writeTIFFFieldToStream(f, stream);
               nextSpace = stream.getStreamPosition();
            }
         }

         if (tagNumber != 279 && tagNumber != 325 && tagNumber != 514) {
            if (tagNumber == 273 || tagNumber == 324 || tagNumber == 513) {
               this.stripOrTileOffsetsPosition = pos;
            }
         } else {
            this.stripOrTileByteCountsPosition = pos;
         }
      }

      this.lastPosition = nextSpace;
   }

   public long getStripOrTileByteCountsPosition() {
      return this.stripOrTileByteCountsPosition;
   }

   public long getStripOrTileOffsetsPosition() {
      return this.stripOrTileOffsetsPosition;
   }

   public long getLastPosition() {
      return this.lastPosition;
   }

   void setPositions(long stripOrTileOffsetsPosition, long stripOrTileByteCountsPosition, long lastPosition) {
      this.stripOrTileOffsetsPosition = stripOrTileOffsetsPosition;
      this.stripOrTileByteCountsPosition = stripOrTileByteCountsPosition;
      this.lastPosition = lastPosition;
   }

   public TIFFIFD getShallowClone() {
      TIFFTagSet baselineTagSet = BaselineTIFFTagSet.getInstance();
      List tagSetList = this.getTagSetList();
      if (!tagSetList.contains(baselineTagSet)) {
         return this;
      } else {
         TIFFIFD shallowClone = new TIFFIFD(tagSetList, this.getParentTag());
         Set baselineTagNumbers = baselineTagSet.getTagNumbers();

         TIFFField fieldClone;
         for(Iterator fields = this.iterator(); fields.hasNext(); shallowClone.addTIFFField(fieldClone)) {
            TIFFField field = (TIFFField)fields.next();
            Integer tagNumber = new Integer(field.getTagNumber());
            if (baselineTagNumbers.contains(tagNumber)) {
               Object fieldData = field.getData();
               int fieldType = field.getType();

               try {
                  switch (fieldType) {
                     case 1:
                     case 6:
                     case 7:
                        fieldData = ((byte[])((byte[])fieldData)).clone();
                        break;
                     case 2:
                        fieldData = ((String[])((String[])fieldData)).clone();
                        break;
                     case 3:
                        fieldData = ((char[])((char[])fieldData)).clone();
                        break;
                     case 4:
                     case 13:
                        fieldData = ((long[])((long[])fieldData)).clone();
                        break;
                     case 5:
                        fieldData = ((long[][])((long[][])fieldData)).clone();
                        break;
                     case 8:
                        fieldData = ((short[])((short[])fieldData)).clone();
                        break;
                     case 9:
                        fieldData = ((int[])((int[])fieldData)).clone();
                        break;
                     case 10:
                        fieldData = ((int[][])((int[][])fieldData)).clone();
                        break;
                     case 11:
                        fieldData = ((float[])((float[])fieldData)).clone();
                        break;
                     case 12:
                        fieldData = ((double[])((double[])fieldData)).clone();
                  }
               } catch (Exception var12) {
               }

               fieldClone = new TIFFField(field.getTag(), fieldType, field.getCount(), fieldData);
            } else {
               fieldClone = field;
            }
         }

         shallowClone.setPositions(this.stripOrTileOffsetsPosition, this.stripOrTileByteCountsPosition, this.lastPosition);
         return shallowClone;
      }
   }
}
