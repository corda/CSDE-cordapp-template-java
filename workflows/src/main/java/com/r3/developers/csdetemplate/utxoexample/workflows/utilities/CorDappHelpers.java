package com.r3.developers.csdetemplate.utxoexample.workflows.utilities;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public final class CorDappHelpers {
    public static <T> T findAndExpectExactlyOne(Collection<T> collection, Predicate<? super T> filterFn, String exceptionMsg) throws IllegalArgumentException
    {
        Collection<T> results = collection.stream().filter(filterFn).collect(Collectors.toList());
        if(results.size() != 1){
            throw new IllegalArgumentException(exceptionMsg);
        }
        return results.iterator().next();
    }

    public static <T> T findAndExpectExactlyOne(Collection<T> collection, String exceptionMsg) throws IllegalArgumentException {
        return findAndExpectExactlyOne(collection, e -> true, exceptionMsg);
    }
}
