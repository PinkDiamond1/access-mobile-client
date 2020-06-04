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

package org.iota.access.delegation.preview;

import android.annotation.SuppressLint;
import androidx.databinding.DataBindingUtil;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.iota.access.R;
import org.iota.access.databinding.ItemJsonElementBinding;
import org.iota.access.utils.ui.DisplayUtil;
import org.iota.access.utils.ui.recursiverecyclerview.RecursiveRecyclerAdapter;

import java.util.List;

public class JsonAdapter extends RecursiveRecyclerAdapter<JsonAdapter.ViewHolder> {

    public JsonAdapter(List<JsonElement> items) {
        setItems(items);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        ItemJsonElementBinding binding = DataBindingUtil.inflate(LayoutInflater.from(viewGroup.getContext()), R.layout.item_json_element, viewGroup, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        Object item = getItem(position);
        if (item instanceof JsonElement) {
            holder.bind((JsonElement) item, getDepth(position), isExpended(position));
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView mKeyTextView;
        private final TextView mValueTextView;
        private final ImageView mArrowImageView;

        private ViewHolder(ItemJsonElementBinding binding) {
            super(binding.getRoot());
            mKeyTextView = binding.textKey;
            mValueTextView = binding.textValue;
            mArrowImageView = binding.imageArrow;
        }

        @SuppressLint("SetTextI18n")
        private void bind(JsonElement jsonElement, int depth, boolean expended) {
            mKeyTextView.setText(jsonElement.getKey() + ":");
            mValueTextView.setText(jsonElement.getValue());

            mKeyTextView.setPadding(
                    DisplayUtil.convertDensityPixelToPixel(itemView.getContext(), 5)
                            + depth * DisplayUtil.convertDensityPixelToPixel(itemView.getContext(), 20),
                    0, 0, 0);

            if (jsonElement.getChildren().size() == 0)
                mArrowImageView.setVisibility(View.GONE);
            else {
                if (expended)
                    mArrowImageView.setImageResource(R.drawable.ic_arrow_up);
                else
                    mArrowImageView.setImageResource(R.drawable.ic_arrow_down);
                mArrowImageView.setVisibility(View.VISIBLE);
            }


        }
    }
}
