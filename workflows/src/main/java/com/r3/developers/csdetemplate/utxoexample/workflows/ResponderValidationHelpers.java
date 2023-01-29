package com.r3.developers.csdetemplate.utxoexample.workflows;

import com.r3.developers.csdetemplate.utxoexample.states.ChatState;
import net.corda.v5.base.annotations.Suspendable;
import net.corda.v5.base.types.MemberX500Name;

import java.util.Arrays;
import java.util.List;


// I think we should avoid static imports, it's an extra level of complexity we don't need to add.
// The Kotlin helper functions are more simple because Kotlin supports functions which are in the package but
// not in a class.

public final class ResponderValidationHelpers {
//    public final static List<String> bannedWords = Arrays.asList("banana", "apple", "pear");
//
//    @Suspendable
//    public static boolean checkForBannedWords(String str) {
//        return bannedWords.stream().anyMatch(str::contains);
//    }

//    @Suspendable
//    public static boolean checkMessageFromMatchesCounterparty(ChatState state, MemberX500Name otherMember) {
//        return state.getMessageFrom().equals(otherMember);
//    }

    // This class just introduces a scope for some helper functions and should not be instantiated.
//    private ResponderValidationHelpers() {}

}

