package com.r3.developers.csdetemplate.utilities;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public final class CorDappHelpers {
    public static <T> T findAndExpectExactlyOne(Collection<T> collection, Predicate<? super T> filterFn, String exceptionMsg)
    {
        Collection<T> results = collection.stream().filter(filterFn).collect(Collectors.toList());
        if(results.size() != 1){
            throw new RuntimeException(exceptionMsg);
        }
        return results.iterator().next();
    }

    public static <T> T findAndExpectExactlyOne(Collection<T> collection, String exceptionMsg) {
        return findAndExpectExactlyOne(collection, e -> true, exceptionMsg);
    }
}
