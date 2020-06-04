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

public class TSSendResponse implements Serializable {

    @SerializedName("receiver")
    @Expose
    @Nullable
    private String mReceiver;

    @SerializedName("amount")
    @Expose
    @Nullable
    private Float mAmount;

    public TSSendResponse() {
    }

    @Nullable
    public String getReceiver() {
        return mReceiver;
    }

    public void setReceiver(@Nullable String receiver) {
        mReceiver = receiver;
    }

    @Nullable
    public Float getAmount() {
        return mAmount;
    }

    public void setAmount(@Nullable Float amount) {
        mAmount = amount;
    }
}