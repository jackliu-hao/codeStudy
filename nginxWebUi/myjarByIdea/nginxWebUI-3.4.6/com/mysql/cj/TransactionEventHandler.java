package com.mysql.cj;

public interface TransactionEventHandler {
   void transactionBegun();

   void transactionCompleted();
}
