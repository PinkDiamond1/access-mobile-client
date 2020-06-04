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

package org.iota.access.delegation;

import android.content.res.Resources;
import androidx.annotation.StringRes;

import org.iota.access.R;

public enum RuleLimitation {
    TIME,
    LOCATION,
    SPEED_LIMIT;

    @StringRes
    public int getNameResId() {
        switch (this) {
            case TIME:
                return R.string.rule_limitation_time;
            case LOCATION:
                return R.string.rule_limitation_location;
            case SPEED_LIMIT:
                return R.string.rule_limitation_speed_limit;
        }
        return 0;
    }

    public static RuleLimitation[] getAllValues() {
        return new RuleLimitation[]{
                TIME,
                LOCATION,
                SPEED_LIMIT};
    }

    public static String[] getAllStringValues(Resources resources) {
        return new String[]{
                resources.getString(RuleLimitation.TIME.getNameResId()),
                resources.getString(RuleLimitation.LOCATION.getNameResId()),
                resources.getString(RuleLimitation.SPEED_LIMIT.getNameResId())};
    }

    public enum TimeType {
        FROM,
        UNTIL
    }
}