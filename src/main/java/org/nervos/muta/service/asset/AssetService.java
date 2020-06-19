package org.nervos.muta.service.asset;

import lombok.AllArgsConstructor;
import org.nervos.muta.Muta;
import org.nervos.muta.service.asset.type.Asset;
import org.nervos.muta.service.asset.type.CreateAssetPayload;

import java.io.IOException;

@AllArgsConstructor
public class AssetService {
    public Muta muta;

    public static final String SERVICE_NAME = "asset";
    public static final String METHOD_CREATE_ASSET = "create_asset";

    public String createAsset(String name, String symbol, long supply) throws IOException {
        String ret = muta.sendTransaction(SERVICE_NAME, METHOD_CREATE_ASSET,
                new CreateAssetPayload(
                        name,
                        symbol,
                        supply)
                );
        return ret;
    }

    public static void main(String[] args) throws IOException {
        Muta muta = Muta.defaultMuta();
        AssetService assetService = new AssetService(muta);

        String ret = assetService.createAsset("hamster","HAM",100);
        System.out.println(ret);
    }
}
