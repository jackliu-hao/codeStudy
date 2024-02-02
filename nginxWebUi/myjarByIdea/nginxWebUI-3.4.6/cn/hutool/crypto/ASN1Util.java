package cn.hutool.crypto;

import cn.hutool.core.io.FastByteArrayOutputStream;
import cn.hutool.core.io.IORuntimeException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.BERSequence;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DLSequence;
import org.bouncycastle.asn1.util.ASN1Dump;

public class ASN1Util {
   public static byte[] encodeDer(ASN1Encodable... elements) {
      return encode("DER", elements);
   }

   public static byte[] encode(String asn1Encoding, ASN1Encodable... elements) {
      FastByteArrayOutputStream out = new FastByteArrayOutputStream();
      encodeTo(asn1Encoding, out, elements);
      return out.toByteArray();
   }

   public static void encodeTo(String asn1Encoding, OutputStream out, ASN1Encodable... elements) {
      Object sequence;
      switch (asn1Encoding) {
         case "DER":
            sequence = new DERSequence(elements);
            break;
         case "BER":
            sequence = new BERSequence(elements);
            break;
         case "DL":
            sequence = new DLSequence(elements);
            break;
         default:
            throw new CryptoException("Unsupported ASN1 encoding: {}", new Object[]{asn1Encoding});
      }

      try {
         ((ASN1Sequence)sequence).encodeTo(out);
      } catch (IOException var6) {
         throw new IORuntimeException(var6);
      }
   }

   public static ASN1Object decode(InputStream in) {
      ASN1InputStream asn1In = new ASN1InputStream(in);

      try {
         return asn1In.readObject();
      } catch (IOException var3) {
         throw new IORuntimeException(var3);
      }
   }

   public static String getDumpStr(InputStream in) {
      return ASN1Dump.dumpAsString(decode(in));
   }
}
