package org.nervos.muta.service.asset;

import com.fasterxml.jackson.core.type.TypeReference;
import java.io.IOException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.nervos.muta.Muta;
import org.nervos.muta.service.asset.type.*;

@AllArgsConstructor
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
  private final Muta muta;

  public Asset createAsset(CreateAssetPayload createAssetPayload) throws IOException {
    Asset asset =
        muta.sendTransactionAndPollResult(
            SERVICE_NAME, METHOD_CREATE_ASSET, createAssetPayload, new TypeReference<Asset>() {});

    return asset;
  }

  public Asset getAsset(GetAssetPayload getAssetPayload) throws IOException {
    Asset ret =
        muta.queryService(
            SERVICE_NAME, METHOD_GET_ASSET, getAssetPayload, new TypeReference<Asset>() {});
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

  public void transfer(TransferPayload transferPayload) throws IOException {
    muta.sendTransactionAndPollResult(
        SERVICE_NAME, METHOD_TRANSFER, transferPayload, new TypeReference<Void>() {});
  }

  public void approve(TransferPayload transferPayload) throws IOException {
    muta.sendTransactionAndPollResult(
        SERVICE_NAME, METHOD_APPROVE, transferPayload, new TypeReference<Void>() {});
  }

  public void transfer_from(TransferFromPayload transferFromPayload) throws IOException {
    muta.sendTransactionAndPollResult(
        SERVICE_NAME, METHOD_TRANSFER_FROM, transferFromPayload, new TypeReference<Void>() {});
  }
}
