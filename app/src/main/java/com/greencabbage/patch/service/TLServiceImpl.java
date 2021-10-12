package com.greencabbage.patch.service;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.Purchase;
import com.greencabbage.patch.util.billing.BillingManager;

import java.util.List;

public class TLServiceImpl implements TLService {
    private BillingManager mBillingManager;
    Context _context;

    private final UpdateListener mUpdateListener;
    private static final String TAG = "TLServiceImpl";

    private static boolean _isInit = false;
    private static TLServiceImpl _tlServiceImpl;

    private TLServiceImpl(Context aContext) {
        mUpdateListener = new UpdateListener();
        this._context = aContext;
    }

    public TLServiceImpl getTLSserviceImpl(Context aContext) {
        if (_isInit) {
            return _tlServiceImpl;
        } else {
            _tlServiceImpl = new TLServiceImpl(aContext);
            initService();
            _isInit = true;
            return _tlServiceImpl;
        }
    }

    public BillingManager getBillingManager() {
        return mBillingManager;
    }

    public void initService() {
        mBillingManager = new BillingManager((Activity)this._context, this.getUpdateListener());
    }

    public UpdateListener getUpdateListener() {
        return mUpdateListener;
    }

    /**
     * Handler to billing updates
     */
    private class UpdateListener implements BillingManager.BillingUpdatesListener {
        @Override
        public void onBillingClientSetupFinished() {
            // mActivity.onBillingManagerSetupFinished();
        }

        @Override
        public void onConsumeFinished(String token, @BillingClient.BillingResponse int result) {
            Log.d(TAG, "Consumption finished. Purchase token: " + token + ", result: " + result);

            // Note: We know this is the SKU_GAS, because it's the only one we consume, so we don't
            // check if token corresponding to the expected sku was consumed.
            // If you have more than one sku, you probably need to validate that the token matches
            // the SKU you expect.
            // It could be done by maintaining a map (updating it every time you call consumeAsync)
            // of all tokens into SKUs which were scheduled to be consumed and then looking through
            // it here to check which SKU corresponds to a consumed token.
            if (result == BillingClient.BillingResponse.OK) {
                // Successfully consumed, so we apply the effects of the item in our
                // game world's logic, which in our case means filling the gas tank a bit
                Log.d(TAG, "Consumption successful. Provisioning.");
                //mTank = mTank == TANK_MAX ? TANK_MAX : mTank + 1;
                //saveData();
                // mActivity.alert(R.string.alert_fill_gas, mTank);
            } else {
                //mActivity.alert(R.string.alert_error_consuming, result);
            }

            //mActivity.showRefreshedUi();
            Log.d(TAG, "End consumption flow.");
        }

        @Override
        public void onPurchasesUpdated(List<Purchase> purchaseList) {
            //mGoldMonthly = false;
            //mGoldYearly = false;

            for (Purchase purchase : purchaseList) {
                switch (purchase.getSku()) {
                    /*case PremiumDelegate.SKU_ID:
                        Log.d(TAG, "You are Premium! Congratulations!!!");
                        mIsPremium = true;
                        break;
                    case SilverDelegate.SKU_ID:
                        Log.d(TAG, "We have gas. Consuming it.");
                        // We should consume the purchase and fill up the tank once it was consumed
                        mActivity.getBillingManager().consumeAsync(purchase.getPurchaseToken());
                        break;
                    case GoldMonthlyDelegate.SKU_ID:
                        mGoldMonthly = true;
                        break;
                    case GoldYearlyDelegate.SKU_ID:
                        mGoldYearly = true;
                        break;*/
                }
            }

            //mActivity.showRefreshedUi();
        }
    }
}

