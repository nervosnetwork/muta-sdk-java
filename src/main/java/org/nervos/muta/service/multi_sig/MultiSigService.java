package org.nervos.muta.service.multi_sig;

import lombok.AllArgsConstructor;
import org.nervos.muta.Muta;
import org.nervos.muta.service.multi_sig.type.*;

import java.io.IOException;
import java.util.Vector;

@AllArgsConstructor
public class MultiSigService {
    public Muta muta;

    public static final String SERVICE_NAME = "multisig";
    public static final String METHOD_GENERATE_ACCOUNT = "generate_account";
    public static final String METHOD_GET_ACCOUNT_FROM_ADDRESS = "get_account_from_address";
    public static final String METHOD_VERIFY_SIGNATURE = "verify_signature";
    public static final String METHOD_CHANGE_OWNER = "change_owner";
    public static final String METHOD_CHANGE_MEMO = "change_memo";
    public static final String METHOD_ADD_ACCOUNT = "add_account";
    public static final String METHOD_REMOVE_ACCOUNT = "remove_account";
    public static final String METHOD_SET_ACCOUNT_WEIGHT = "set_account_weight";
    public static final String METHOD_SET_THRESHOLD = "set_threshold";


    public GenerateMultiSigAccountResponse generate_account(String owner, Vector<AddressWithWeight> addr_with_weight, int threshold, String memo) throws IOException {
        GenerateMultiSigAccountResponse ret = muta.sendTransactionAndPollResult(SERVICE_NAME, METHOD_GENERATE_ACCOUNT,
                new GenerateMultiSigAccountPayload(
                        owner,
                        addr_with_weight,
                        threshold,
                        memo),
                GenerateMultiSigAccountResponse.class
        );

        return ret;
    }

    public GenerateMultiSigAccountResponse get_account_from_address(String multi_sig_address) throws IOException {
        GenerateMultiSigAccountResponse ret = muta.queryService(SERVICE_NAME, METHOD_GET_ACCOUNT_FROM_ADDRESS, new GetMultiSigAccountPayload(
                multi_sig_address
        ), GenerateMultiSigAccountResponse.class);
        return ret;
    }

    public void get_account_from_address(SignedTransaction signedTransaction) throws IOException {
        muta.queryService(SERVICE_NAME, METHOD_VERIFY_SIGNATURE, signedTransaction, Void.class);
    }

    public void change_owner(ChangeOwnerPayload changeOwnerPayload) throws IOException {
        muta.sendTransactionAndPollResult(SERVICE_NAME, METHOD_CHANGE_OWNER,
                changeOwnerPayload,
                Void.class
        );
    }

    public void change_memo(ChangeMemoPayload changeMemoPayload) throws IOException {
        muta.sendTransactionAndPollResult(SERVICE_NAME, METHOD_CHANGE_MEMO,
                changeMemoPayload,
                Void.class
        );
    }

    public void add_account(AddAccountPayload addAccountPayload) throws IOException {
        muta.sendTransactionAndPollResult(SERVICE_NAME, METHOD_ADD_ACCOUNT,
                addAccountPayload,
                Void.class
        );
    }

    public Account remove_account(RemoveAccountPayload removeAccountPayload) throws IOException {
        Account ret = muta.sendTransactionAndPollResult(SERVICE_NAME, METHOD_REMOVE_ACCOUNT,
                removeAccountPayload,
                Account.class
        );
        return ret;
    }

    public void set_account_weight(SetAccountWeightPayload setAccountWeightPayload) throws IOException {
        muta.sendTransactionAndPollResult(SERVICE_NAME, METHOD_SET_ACCOUNT_WEIGHT,
                setAccountWeightPayload,
                Account.class
        );
    }

    public void set_threshold(SetThresholdPayload setThresholdPayload) throws IOException {
        muta.sendTransactionAndPollResult(SERVICE_NAME, METHOD_SET_THRESHOLD,
                setThresholdPayload,
                Account.class
        );
    }
}
