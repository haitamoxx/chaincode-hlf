/*
 * SPDX-License-Identifier: Apache-2.0
 */

package org.hyperledger.fabric.samples.assettransfer;

import java.util.Objects;

import org.hyperledger.fabric.contract.annotation.DataType;
import org.hyperledger.fabric.contract.annotation.Property;

import com.owlike.genson.annotation.JsonProperty;

@DataType()
public final class Asset {

    @Property()
    private final String hash;

    @Property()
    private final String pubKey;

    @Property()
    private final String timestamp;

    public String getHash() {
        return hash;
    }

    public String getPubKey() {
        return pubKey;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public Asset(@JsonProperty("hash") final String hash, @JsonProperty("pubKey") final String pubKey,
                 @JsonProperty("timestamp") final String timestamp) {
        this.hash = hash;
        this.pubKey = pubKey;
        this.timestamp = timestamp;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }

        if ((obj == null) || (getClass() != obj.getClass())) {
            return false;
        }

        Asset other = (Asset) obj;

        return Objects.deepEquals(
                new String[] {getHash(), getPubKey(), getTimestamp()},
                new String[] {other.getHash(), other.getPubKey(), other.getTimestamp()});
    }

    @Override
    public int hashCode() {
        return Objects.hash(getHash(), getPubKey(), getTimestamp());
    }


    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "@" + Integer.toHexString(hashCode()) + " [document hash=" + hash + ", public Key="
                + pubKey + ", timestamp=" + timestamp + "]";
    }
}
