package org.nervos.muta.service.multi_sig;

import com.fasterxml.jackson.core.type.TypeReference;
import java.io.IOException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.nervos.muta.Muta;
import org.nervos.muta.client.type.primitive.SignedTransaction;
import org.nervos.muta.service.multi_sig.type.*;

@AllArgsConstructor
@Getter
public class MultiSigService {
    public static final String SERVICE_NAME = "multi_signature";
    public static final String METHOD_GENERATE_ACCOUNT = "generate_account";
    public static final String METHOD_GET_ACCOUNT_FROM_ADDRESS = "get_account_from_address";
    public static final String METHOD_VERIFY_SIGNATURE = "verify_signature";
    public static final String METHOD_UPDATE_ACCOUNT = "update_account";
    public static final String METHOD_CHANGE_OWNER = "change_owner";
    public static final String METHOD_CHANGE_MEMO = "change_memo";
    public static final String METHOD_ADD_ACCOUNT = "add_account";
    public static final String METHOD_REMOVE_ACCOUNT = "remove_account";
    public static final String METHOD_SET_ACCOUNT_WEIGHT = "set_account_weight";
    public static final String METHOD_SET_THRESHOLD = "set_threshold";
    private final Muta muta;

    public GenerateMultiSigAccountResponse generate_account(
            GenerateMultiSigAccountPayload generateMultiSigAccountPayload) throws IOException {
        GenerateMultiSigAccountResponse ret =
                muta.sendTransactionAndPollResult(
                        SERVICE_NAME,
                        METHOD_GENERATE_ACCOUNT,
                        generateMultiSigAccountPayload,
                        new TypeReference<GenerateMultiSigAccountResponse>() {});

        return ret;
    }

    public GetMultiSigAccountResponse get_account_from_address(
            GetMultiSigAccountPayload getMultiSigAccountPayload) throws IOException {
        GetMultiSigAccountResponse ret =
                muta.queryService(
                        SERVICE_NAME,
                        METHOD_GET_ACCOUNT_FROM_ADDRESS,
                        getMultiSigAccountPayload,
                        new TypeReference<GetMultiSigAccountResponse>() {});
        return ret;
    }

    public void verify_signature(SignedTransaction signedTransaction) throws IOException {
        muta.queryService(
                SERVICE_NAME,
                METHOD_VERIFY_SIGNATURE,
                signedTransaction,
                new TypeReference<Void>() {});
    }

    public void update_account(UpdateAccountPayload updateAccountPayload) throws IOException {
        muta.sendTransactionAndPollResult(
                SERVICE_NAME,
                METHOD_UPDATE_ACCOUNT,
                updateAccountPayload,
                new TypeReference<Void>() {});
    }

    public void change_owner(ChangeOwnerPayload changeOwnerPayload) throws IOException {
        muta.sendTransactionAndPollResult(
                SERVICE_NAME,
                METHOD_CHANGE_OWNER,
                changeOwnerPayload,
                new TypeReference<Void>() {});
    }

    public void change_memo(ChangeMemoPayload changeMemoPayload) throws IOException {
        muta.sendTransactionAndPollResult(
                SERVICE_NAME, METHOD_CHANGE_MEMO, changeMemoPayload, new TypeReference<Void>() {});
    }

    public void add_account(AddAccountPayload addAccountPayload) throws IOException {
        muta.sendTransactionAndPollResult(
                SERVICE_NAME, METHOD_ADD_ACCOUNT, addAccountPayload, new TypeReference<Void>() {});
    }

    public Account remove_account(RemoveAccountPayload removeAccountPayload) throws IOException {
        Account ret =
                muta.sendTransactionAndPollResult(
                        SERVICE_NAME,
                        METHOD_REMOVE_ACCOUNT,
                        removeAccountPayload,
                        new TypeReference<Account>() {});
        return ret;
    }

    public void set_account_weight(SetAccountWeightPayload setAccountWeightPayload)
            throws IOException {
        muta.sendTransactionAndPollResult(
                SERVICE_NAME,
                METHOD_SET_ACCOUNT_WEIGHT,
                setAccountWeightPayload,
                new TypeReference<Account>() {});
    }

    public void set_threshold(SetThresholdPayload setThresholdPayload) throws IOException {
        muta.sendTransactionAndPollResult(
                SERVICE_NAME,
                METHOD_SET_THRESHOLD,
                setThresholdPayload,
                new TypeReference<Account>() {});
    }
}
