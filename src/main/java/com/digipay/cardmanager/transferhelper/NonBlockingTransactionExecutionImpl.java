package com.digipay.cardmanager.transferhelper;

import com.digipay.cardmanager.model.TransactionItem;
import com.digipay.cardmanager.model.TransactionResponse;
import com.digipay.cardmanager.service.ResponseHandler;

import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class NonBlockingTransactionExecutionImpl implements NonBlockingCommunicationTemplate<TransactionItem> {

    private ResponseHandler responseHandler;
    private ExecutorService executor;

    public NonBlockingTransactionExecutionImpl(ResponseHandler responseHandler, ExecutorService executor){
        this.responseHandler = responseHandler;
        this.executor = executor;
    }

    @Override
    public void doForEntity(final List<TransactionItem> transferTransactionItems) {
        if(transferTransactionItems == null)
            return;
        for (TransactionItem transaction : transferTransactionItems) {
            Runnable worker = new WorkerThread(transaction, responseHandler);
            try{
                executor.execute(worker);
            }catch (Exception ex){
                System.out.println("asdfsdf");
            }
        }
    }


    @Override
    public void shutDown() {
        executor.shutdown();
    }

    public List<TransactionItem> shutDownNow(){
        return executor.shutdownNow().stream().map(
                a -> ((WorkerThread)a).transaction).collect(Collectors.toList());
    }

    @Override
    public void awaitTermination() {
        try {
            boolean terminate = false;
            while(terminate)
                terminate = executor.awaitTermination(1000, TimeUnit.DAYS);
        } catch (InterruptedException e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
    }


    private class WorkerThread implements Runnable {

        private final TransactionItem transaction;
        private final ResponseHandler responseHandler;

        public WorkerThread(final TransactionItem transaction,final  ResponseHandler responseHandler){
            this.transaction = transaction;
            this.responseHandler = responseHandler;
        }

        @Override
        public void run() {
            System.out.println(Thread.currentThread().getName()+" Start. Command = "+transaction);
            processCommand();
            System.out.println(Thread.currentThread().getName()+" End.");
        }

        private void processCommand() {
            try {
                Thread.sleep(200);
                Random random = new Random();
                if(random.nextInt(50) % 9 == 0)
                    responseHandler.onFailed((new TransactionResponse(new Date(), transaction)));
                else
                    responseHandler.onSuccess(new TransactionResponse(new Date(), transaction));

            } catch (InterruptedException e) {
                e.printStackTrace();
                responseHandler.onInterrupt((new TransactionResponse(new Date(), transaction)));
            }
        }

        @Override
        public String toString(){
            return this.transaction.toString();
        }
    }
}
