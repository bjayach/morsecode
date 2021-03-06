package com.greencabbage.patch.util.billing.skulist.row;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.greencabbage.patch.util.ResourceUtils;
import com.greencabbage.patch.util.billing.BillingProvider;
import com.greencabbage.patch.util.billing.skulist.SkusAdapter;

/**
 * Renders the UI for a particular row by delegating specifics to corresponding handlers
 */
public class UiManager implements RowViewHolder.OnButtonClickListener {
    private final RowDataProvider mRowDataProvider;
    private final UiDelegatesFactory mDelegatesFactory;

    public UiManager(RowDataProvider rowDataProvider, BillingProvider billingProvider) {
        mRowDataProvider = rowDataProvider;
        mDelegatesFactory = new UiDelegatesFactory(billingProvider);
    }

    public UiDelegatesFactory getDelegatesFactory() {
        return mDelegatesFactory;
    }

    public final RowViewHolder onCreateViewHolder(ViewGroup parent, @SkusAdapter.RowTypeDef int viewType) {
        // Selecting a flat layout for header rows
        if (viewType == SkusAdapter.TYPE_HEADER) {
            View item = LayoutInflater.from(parent.getContext())
                    .inflate(ResourceUtils.R_layout_sku_details_row_header, parent, false);
            return new RowViewHolder(item, this);
        } else {
            View item = LayoutInflater.from(parent.getContext())
                    .inflate(ResourceUtils.R_layout_sku_details_row, parent, false);
            return new RowViewHolder(item, this);
        }
    }

    public void onBindViewHolder(SkuRowData data, RowViewHolder holder) {
        if (data != null) {
            holder.title.setText(data.getTitle());
            // For non-header rows we need to feel other data and init button's state
            if (data.getRowType() != SkusAdapter.TYPE_HEADER) {
                mDelegatesFactory.onBindViewHolder(data, holder);
            }
        }
    }

    public void onButtonClicked(int position) {
        SkuRowData data = mRowDataProvider.getData(position);
        if (data != null) {
            mDelegatesFactory.onButtonClicked(data);
        }
    }
}