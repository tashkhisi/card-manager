package com.digipay.cardmanager.transferhelper;

import java.util.List;

public interface NonBlockingCommunicationTemplate<T>{
    void doForEntity(final List<T> Items);

    void shutDown();
    void awaitTermination();

    List<T> shutDownNow();


}
