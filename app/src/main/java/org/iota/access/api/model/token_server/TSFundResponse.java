/*
 *  This file is part of the IOTA Access distribution
 *  (https://github.com/iotaledger/access)
 *
 *  Copyright (c) 2020 IOTA Stiftung.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.iota.access.api.model.token_server;

import androidx.annotation.Nullable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class TSFundResponse implements Serializable {

    @SerializedName("amount")
    @Expose
    @Nullable
    private String mAmount;

    @SerializedName("address")
    @Expose
    @Nullable
    private String mAddress;

    @SerializedName("master")
    @Expose
    @Nullable
    private String mMaster;

    public TSFundResponse() {
    }

    @Nullable
    public String getAmount() {
        return mAmount;
    }

    @Nullable
    public String getAddress() {
        return mAddress;
    }

    @Nullable
    public String getMaster() {
        return mMaster;
    }
}
