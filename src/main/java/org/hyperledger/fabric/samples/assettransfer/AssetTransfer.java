/*
 * SPDX-License-Identifier: Apache-2.0
 */

package org.hyperledger.fabric.samples.assettransfer;



import org.hyperledger.fabric.contract.Context;
import org.hyperledger.fabric.contract.ContractInterface;
import org.hyperledger.fabric.contract.annotation.Contract;
import org.hyperledger.fabric.contract.annotation.Default;
import org.hyperledger.fabric.contract.annotation.Info;
import org.hyperledger.fabric.contract.annotation.Transaction;
import org.hyperledger.fabric.shim.ChaincodeException;
import org.hyperledger.fabric.shim.ChaincodeStub;

import com.owlike.genson.Genson;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Contract(
        name = "hashTimestamping",
        info = @Info(
                title = "Hash Timestamping",
                description = "Horodatage et signature de documents",
                version = "0.0.1-SNAPSHOT"
                ))
@Default
public final class AssetTransfer implements ContractInterface {

    private final Genson genson = new Genson();

    private enum AssetTransferErrors {
        ASSET_NOT_FOUND,
        ASSET_ALREADY_EXISTS
    }


    /**
     * Creates a new asset on the ledger.
     *
     * @param ctx            the transaction context
     * @param hash        the hash of the new asset
     * @param pubKey          the public key of the signer
     * @return the created asset
     */
    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public Asset anchorHash(final Context ctx, final String hash, final String pubKey) {
        ChaincodeStub stub = ctx.getStub();

        if (HashExists(ctx, hash)) {
            String errorMessage = String.format("Asset %s already exists", hash);
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, AssetTransferErrors.ASSET_ALREADY_EXISTS.toString());
        }

        LocalDateTime dateObj = LocalDateTime.now();
        DateTimeFormatter formatObj = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        String timestamp = dateObj.format(formatObj);

        Asset asset = new Asset(hash, pubKey, timestamp);
        String assetJSON = genson.serialize(asset);
        stub.putStringState(hash, assetJSON);

        return asset;
    }

    /**
     * Retrieves an asset with the specified hash from the ledger.
     *
     * @param ctx     the transaction context
     * @param hash the hash of the asset
     * @return the asset found on the ledger if there was one
     */
    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public Asset ReadAsset(final Context ctx, final String hash) {
        ChaincodeStub stub = ctx.getStub();
        String assetJSON = stub.getStringState(hash);

        if (assetJSON == null || assetJSON.isEmpty()) {
            String errorMessage = String.format("Asset %s does not exist", hash);
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, AssetTransferErrors.ASSET_NOT_FOUND.toString());
        }

        Asset asset = genson.deserialize(assetJSON, Asset.class);
        return asset;
    }

    /**
     * Checks the existence of the asset on the ledger
     *
     * @param ctx     the transaction context
     * @param hash the hash of the asset
     * @return boolean indicating the existence of the asset
     */
    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public boolean HashExists(final Context ctx, final String hash) {
        ChaincodeStub stub = ctx.getStub();
        String assetJSON = stub.getStringState(hash);

        return (assetJSON != null && !assetJSON.isEmpty());
    }
}