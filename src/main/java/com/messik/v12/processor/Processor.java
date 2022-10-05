package com.messik.v12.processor;

public interface Processor<D> {

    void process(D data);
    Processor<D> mutate();
    Processor<D> copy();

}
