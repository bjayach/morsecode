package com.greencabbage.patch.util.billing.skulist.row;

import android.widget.Toast;
import com.android.billingclient.api.BillingClient.SkuType;
import com.greencabbage.patch.util.ResourceUtils;
import com.greencabbage.patch.util.billing.BillingProvider;


/**
 * Handles Ui specific to "silver" - consumable in-app purchase row
 */
public class SilverDelegate extends UiManagingDelegate {
    public static final String SKU_ID = "2_dollars";

    @Override
    public @SkuType String getType() {
        return SkuType.INAPP;
    }

    public SilverDelegate(BillingProvider billingProvider) {
        super(billingProvider);
    }

    @Override
    public void onBindViewHolder(SkuRowData data, RowViewHolder holder) {
        super.onBindViewHolder(data, holder);
        if (mBillingProvider.isTankFull()) {
            holder.button.setText(ResourceUtils.R_string_button_full);
        } else {
            holder.button.setText(ResourceUtils.R_string_button_buy);
        }

        //holder.skuIcon.setImageResource(R.drawable.gas_icon);
    }

    @Override
    public void onButtonClicked(SkuRowData data) {
        if (mBillingProvider.isTankFull()) {
            Toast.makeText(mBillingProvider.getBillingManager().getContext(),
                    ResourceUtils.R_string_toast_full, Toast.LENGTH_SHORT).show();
        } else {
            super.onButtonClicked(data);
        }
    }
}