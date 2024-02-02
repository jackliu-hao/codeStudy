package com.sun.jna.platform.linux;

public interface Fcntl {
   int O_RDONLY = 0;
   int O_WRONLY = 1;
   int O_RDWR = 2;
   int O_CREAT = 64;
   int O_EXCL = 128;
   int O_TRUNC = 512;
   int S_IRUSR = 256;
   int S_IWUSR = 128;
   int S_IXUSR = 64;
   int S_IRWXU = 448;
   int S_IRGRP = 32;
   int S_IWGRP = 16;
   int S_IXGRP = 8;
   int S_IRWXG = 56;
   int S_IROTH = 4;
   int S_IWOTH = 2;
   int S_IXOTH = 1;
   int S_IRWXO = 7;
   int S_ISUID = 2048;
   int S_ISGID = 1024;
   int S_ISVTX = 512;
}
