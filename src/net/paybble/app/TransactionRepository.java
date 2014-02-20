package net.paybble.app;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Semaphore;

public class TransactionRepository {
    volatile Set<String> trxs = new HashSet<String>();
    private static final TransactionRepository INSTANCE = new TransactionRepository();
    Semaphore mutex = new Semaphore(1);

    public static TransactionRepository getInstance() {
        return INSTANCE;
    }

    boolean contains(String trx) {
        return trxs.contains(trx);
    }

    synchronized void addTrx(String trx) {
        trxs.add(trx);
    }

    synchronized void removeTrx(String trx) {
        trxs.remove(trx);
    }

}
