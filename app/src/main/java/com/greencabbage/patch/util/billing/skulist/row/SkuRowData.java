package com.greencabbage.patch.util.billing.skulist.row;

import com.android.billingclient.api.BillingClient.SkuType;
import com.android.billingclient.api.SkuDetails;
import com.greencabbage.patch.util.billing.skulist.SkusAdapter;

/**
 * A model for SkusAdapter's row
 */
public class SkuRowData {
    private String sku, title, price, description;
    private @SkusAdapter.RowTypeDef
    int type;
    private @SkuType String billingType;

    public SkuRowData(SkuDetails details, @SkusAdapter.RowTypeDef int rowType,
                      @SkuType String billingType) {
        this.sku = details.getSku();
        this.title = details.getTitle();
        this.price = details.getPrice();
        this.description = details.getDescription();
        this.type = rowType;
        this.billingType = billingType;
    }

    public SkuRowData(String title) {
        this.title = title;
        this.type = SkusAdapter.TYPE_HEADER;
    }

    public String getSku() {
        return sku;
    }

    public String getTitle() {
        return title;
    }

    public String getPrice() {
        return price;
    }

    public String getDescription() {
        return description;
    }

    public @SkusAdapter.RowTypeDef
    int getRowType() {
        return type;
    }

    public @SkuType String getSkuType() {
        return billingType;
    }
}