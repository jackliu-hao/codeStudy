package org.apache.commons.compress.archivers.tar;

public interface TarConstants {
   int DEFAULT_RCDSIZE = 512;
   int DEFAULT_BLKSIZE = 10240;
   int FORMAT_OLDGNU = 2;
   int FORMAT_POSIX = 3;
   int FORMAT_XSTAR = 4;
   int NAMELEN = 100;
   int MODELEN = 8;
   int UIDLEN = 8;
   int GIDLEN = 8;
   long MAXID = 2097151L;
   int CHKSUMLEN = 8;
   int CHKSUM_OFFSET = 148;
   int SIZELEN = 12;
   long MAXSIZE = 8589934591L;
   int MAGIC_OFFSET = 257;
   int MAGICLEN = 6;
   int VERSION_OFFSET = 263;
   int VERSIONLEN = 2;
   int MODTIMELEN = 12;
   int UNAMELEN = 32;
   int GNAMELEN = 32;
   int DEVLEN = 8;
   int PREFIXLEN = 155;
   int ATIMELEN_GNU = 12;
   int CTIMELEN_GNU = 12;
   int OFFSETLEN_GNU = 12;
   int LONGNAMESLEN_GNU = 4;
   int PAD2LEN_GNU = 1;
   int SPARSELEN_GNU = 96;
   int ISEXTENDEDLEN_GNU = 1;
   int REALSIZELEN_GNU = 12;
   int SPARSE_OFFSET_LEN = 12;
   int SPARSE_NUMBYTES_LEN = 12;
   int SPARSE_HEADERS_IN_OLDGNU_HEADER = 4;
   int SPARSE_HEADERS_IN_EXTENSION_HEADER = 21;
   int SPARSELEN_GNU_SPARSE = 504;
   int ISEXTENDEDLEN_GNU_SPARSE = 1;
   byte LF_OLDNORM = 0;
   byte LF_NORMAL = 48;
   byte LF_LINK = 49;
   byte LF_SYMLINK = 50;
   byte LF_CHR = 51;
   byte LF_BLK = 52;
   byte LF_DIR = 53;
   byte LF_FIFO = 54;
   byte LF_CONTIG = 55;
   byte LF_GNUTYPE_LONGLINK = 75;
   byte LF_GNUTYPE_LONGNAME = 76;
   byte LF_GNUTYPE_SPARSE = 83;
   byte LF_PAX_EXTENDED_HEADER_LC = 120;
   byte LF_PAX_EXTENDED_HEADER_UC = 88;
   byte LF_PAX_GLOBAL_EXTENDED_HEADER = 103;
   String MAGIC_POSIX = "ustar\u0000";
   String VERSION_POSIX = "00";
   String MAGIC_GNU = "ustar ";
   String VERSION_GNU_SPACE = " \u0000";
   String VERSION_GNU_ZERO = "0\u0000";
   String MAGIC_ANT = "ustar\u0000";
   String VERSION_ANT = "\u0000\u0000";
   String GNU_LONGLINK = "././@LongLink";
   String MAGIC_XSTAR = "tar\u0000";
   int XSTAR_MAGIC_OFFSET = 508;
   int XSTAR_MAGIC_LEN = 4;
   int PREFIXLEN_XSTAR = 131;
   int ATIMELEN_XSTAR = 12;
   int CTIMELEN_XSTAR = 12;
}
