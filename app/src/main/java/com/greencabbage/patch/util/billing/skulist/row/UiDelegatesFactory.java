package com.greencabbage.patch.util.billing.skulist.row;
import com.android.billingclient.api.BillingClient.SkuType;
import com.greencabbage.patch.util.billing.BillingProvider;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This factory is responsible to finding the appropriate delegate for Ui rendering and calling
 * corresponding method on it.
 */
public class UiDelegatesFactory {
    private final Map<String, UiManagingDelegate> uiDelegates;

    public UiDelegatesFactory(BillingProvider provider) {
        uiDelegates = new HashMap<>();
        uiDelegates.put(SilverDelegate.SKU_ID, new SilverDelegate(provider));
        uiDelegates.put(GoldDelegate.SKU_ID, new GoldDelegate(provider));
        uiDelegates.put(PlatinumDelegate.SKU_ID, new PlatinumDelegate(provider));
        uiDelegates.put(CarbonDelegate.SKU_ID, new CarbonDelegate(provider));
    }

    /**
     * Returns the list of all SKUs for the billing type specified
     */
    public final List<String> getSkuList(@SkuType String billingType) {
        List<String> result = new ArrayList<>();
        for (String skuId : uiDelegates.keySet()) {
            UiManagingDelegate delegate = uiDelegates.get(skuId);
            if (delegate.getType().equals(billingType)) {
                result.add(skuId);
            }
        }
        return result;
    }

    public void onBindViewHolder(SkuRowData data, RowViewHolder holder) {
        uiDelegates.get(data.getSku()).onBindViewHolder(data, holder);
    }

    public void onButtonClicked(SkuRowData data) {
        uiDelegates.get(data.getSku()).onButtonClicked(data);
    }
}