package org.nervos.muta.service.asset;

import com.fasterxml.jackson.core.type.TypeReference;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import lombok.Getter;
import org.nervos.muta.EventRegisterEntry;
import org.nervos.muta.Muta;
import org.nervos.muta.client.type.ParsedEvent;
import org.nervos.muta.service.asset.type.*;

@Getter
public class AssetService {
    public static final String SERVICE_NAME = "asset";
    public static final String METHOD_CREATE_ASSET = "create_asset";
    public static final String METHOD_GET_ASSET = "get_asset";
    public static final String METHOD_GET_BALANCE = "get_balance";
    public static final String METHOD_GET_ALLOWANCE = "get_allowance";
    public static final String METHOD_TRANSFER = "transfer";
    public static final String METHOD_APPROVE = "approve";
    public static final String METHOD_TRANSFER_FROM = "transfer_from";

    public static final String EVENT_CREATE_ASSET = "CreateAsset";
    public static final String EVENT_TRANSFER_ASSET = "TransferAsset";
    public static final String EVENT_APPROVE_ASSET = "ApproveAsset";
    public static final String EVENT_TRANSFER_FROM = "TransferFrom";

    public static final List<EventRegisterEntry<?>> eventRegistry;

    static {
        eventRegistry =
                Arrays.asList(
                        new EventRegisterEntry<>(EVENT_CREATE_ASSET, new TypeReference<Asset>() {}),
                        new EventRegisterEntry<>(
                                EVENT_TRANSFER_ASSET, new TypeReference<TransferEvent>() {}),
                        new EventRegisterEntry<>(
                                EVENT_APPROVE_ASSET, new TypeReference<ApproveEvent>() {}),
                        new EventRegisterEntry<>(
                                EVENT_TRANSFER_FROM, new TypeReference<TransferFromEvent>() {}));
    }

    private final Muta muta;

    public AssetService(Muta muta) {
        this.muta = muta;
        muta.register(eventRegistry);
    }

    public Asset createAsset(CreateAssetPayload createAssetPayload, List<ParsedEvent<?>> events)
            throws IOException {
        Asset asset =
                muta.sendTransactionAndPollResult(
                        SERVICE_NAME,
                        METHOD_CREATE_ASSET,
                        createAssetPayload,
                        new TypeReference<Asset>() {},
                        events);

        return asset;
    }

    public Asset getAsset(GetAssetPayload getAssetPayload) throws IOException {
        Asset ret =
                muta.queryService(
                        SERVICE_NAME,
                        METHOD_GET_ASSET,
                        getAssetPayload,
                        new TypeReference<Asset>() {});
        return ret;
    }

    public GetBalanceResponse getBalance(GetBalancePayload getBalancePayload) throws IOException {
        GetBalanceResponse ret =
                muta.queryService(
                        SERVICE_NAME,
                        METHOD_GET_BALANCE,
                        getBalancePayload,
                        new TypeReference<GetBalanceResponse>() {});
        return ret;
    }

    public GetAllowanceResponse getAllowance(GetAllowancePayload getAllowancePayload)
            throws IOException {
        GetAllowanceResponse ret =
                muta.queryService(
                        SERVICE_NAME,
                        METHOD_GET_ALLOWANCE,
                        getAllowancePayload,
                        new TypeReference<GetAllowanceResponse>() {});
        return ret;
    }

    public void transfer(TransferPayload transferPayload, List<ParsedEvent<?>> events)
            throws IOException {

        muta.sendTransactionAndPollResult(
                SERVICE_NAME,
                METHOD_TRANSFER,
                transferPayload,
                new TypeReference<Void>() {},
                events);
    }

    public void approve(TransferPayload transferPayload, List<ParsedEvent<?>> events)
            throws IOException {

        muta.sendTransactionAndPollResult(
                SERVICE_NAME,
                METHOD_APPROVE,
                transferPayload,
                new TypeReference<Void>() {},
                events);
    }

    public void transfer_from(TransferFromPayload transferFromPayload, List<ParsedEvent<?>> events)
            throws IOException {

        muta.sendTransactionAndPollResult(
                SERVICE_NAME,
                METHOD_TRANSFER_FROM,
                transferFromPayload,
                new TypeReference<Void>() {},
                events);
    }
}
