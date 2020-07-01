package org.nervos.muta.service.asset;

import com.fasterxml.jackson.core.type.TypeReference;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.nervos.muta.Muta;
import org.nervos.muta.service.asset.type.*;
import org.nervos.muta.util.Util;

import java.io.IOException;

@AllArgsConstructor
@Getter
public class AssetService {
    private final Muta muta;

    public static final String SERVICE_NAME = "asset";
    public static final String METHOD_CREATE_ASSET = "create_asset";
    public static final String METHOD_GET_ASSET = "get_asset";
    public static final String METHOD_GET_BALANCE = "get_balance";
    public static final String METHOD_GET_ALLOWANCE = "get_allowance";
    public static final String METHOD_TRANSFER = "transfer";
    public static final String METHOD_APPROVE = "approve";
    public static final String METHOD_TRANSFER_FROM = "transfer_from";

    public Asset createAsset(String name, String symbol, long supply) throws IOException {
        Asset asset = muta.sendTransactionAndPollResult(SERVICE_NAME, METHOD_CREATE_ASSET,
                new CreateAssetPayload(
                        name,
                        symbol,
                        supply),
                new TypeReference<Asset>() {
                }
        );

        return asset;
    }

    public Asset getAsset(String id) throws IOException {
        Asset ret = muta.queryService(SERVICE_NAME, METHOD_GET_ASSET, new GetAssetPayload(
                id
        ), new TypeReference<Asset>() {
        });
        return ret;
    }

    public GetBalanceResponse getBalance(String asset_id, String user) throws IOException {
        GetBalanceResponse ret = muta.queryService(SERVICE_NAME, METHOD_GET_BALANCE, new GetBalancePayload(
                asset_id, user
        ), new TypeReference<GetBalanceResponse>() {
        });
        return ret;
    }

    public GetAllowanceResponse getAllowance(String asset_id, String grantor, String grantee) throws IOException {
        GetAllowanceResponse ret = muta.queryService(SERVICE_NAME, METHOD_GET_ALLOWANCE, new GetAllowancePayload(
                asset_id, grantor, grantee
        ), new TypeReference<GetAllowanceResponse>() {
        });
        return ret;
    }

    public void transfer(String asset_id, String to, long value) throws IOException {
        muta.sendTransactionAndPollResult(SERVICE_NAME, METHOD_TRANSFER,
                new TransferPayload(
                        asset_id, to, value
                ),
                new TypeReference<Void>() {
                }

        );
    }

    public void approve(String asset_id, String to, long value) throws IOException {
        muta.sendTransactionAndPollResult(SERVICE_NAME, METHOD_APPROVE,
                new TransferPayload(
                        asset_id, to, value
                ),
                new TypeReference<Void>() {
                }
        );
    }

    public void transfer_from(String asset_id, String sender, String recipient, long value) throws IOException {
        muta.sendTransactionAndPollResult(SERVICE_NAME, METHOD_TRANSFER_FROM,
                new TransferFromPayload(
                        asset_id, sender, recipient, value
                ), new TypeReference<Void>() {
                }
        );
    }


}
