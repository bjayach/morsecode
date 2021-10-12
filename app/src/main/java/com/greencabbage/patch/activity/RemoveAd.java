package com.greencabbage.patch.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.greencabbage.patch.controllers.RemoveAdViewController;
import com.greencabbage.patch.util.ResourceUtils;
import com.greencabbage.patch.util.billing.BillingManager;
import com.greencabbage.patch.util.billing.BillingProvider;
import com.greencabbage.patch.util.billing.skulist.AcquireFragment;

import static com.greencabbage.patch.util.billing.BillingManager.BILLING_MANAGER_NOT_INITIALIZED;

public class RemoveAd extends FragmentActivity implements BillingProvider {

    TextView tvRemoveAd;
    TextView tvSilver;
    TextView tvGold;
    TextView tvPlatinum;
    TextView tvCarbon;
    Button ib2Dllr;
    Button ib5Dllr;
    Button ib10Dllr;
    Button ib100Dllr;

    private static final String TAG = "RemoveAd";
    private RemoveAdViewController mViewController;
    private BillingManager mBillingManager;
    private AcquireFragment mAcquireFragment;

    private static final String DIALOG_TAG = "dialog";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(ResourceUtils.remove_ad);

        // Update text on launch of RemoveAd activity
        //tvRemoveAd = (TextView) findViewById(ResourceUtils.tvRemoveAd);
        //tvRemoveAd.setText(getString(ResourceUtils.disclaimer_text));

        //silver
        tvSilver = (TextView) findViewById(ResourceUtils.tvSilver);
        tvSilver.setText(getString(ResourceUtils.silver_star));

        //gold
        tvGold = (TextView) findViewById(ResourceUtils.tvGold);
        tvGold.setText(getString(ResourceUtils.gold_star));

        //platinum
        tvPlatinum = (TextView) findViewById(ResourceUtils.tvPlatinum);
        tvPlatinum.setText(getString(ResourceUtils.platinum_star));

        //carbon
        tvCarbon = (TextView) findViewById(ResourceUtils.tvCarbon);
        tvCarbon.setText(getString(ResourceUtils.carbon_star));

        //inApp
        // Start the controller and load game data
        mViewController = new RemoveAdViewController(this);

        // Create and initialize BillingManager which talks to BillingLibrary
        mBillingManager = new BillingManager(this, mViewController.getUpdateListener());
        //End inApp

        //button
        ib2Dllr = (Button) findViewById(ResourceUtils.ib2Dllr);
        // Setup listener
        ib2Dllr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "**CLicked 2 Dllrs.");
                onPurchaseButtonClicked(v, "2_dollars");

            }
        });
        //button
        ib5Dllr = (Button) findViewById(ResourceUtils.ib5Dllr);
        // Setup listener
        ib5Dllr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "**CLicked 5 Dllrs.");
                onPurchaseButtonClicked(v, "5_dollars");

            }
        });
        //button
        ib10Dllr = (Button) findViewById(ResourceUtils.ib10Dllr);
        // Setup listener
        ib10Dllr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "**CLicked 12 Dllrs.");
                onPurchaseButtonClicked(v, "12_dollars");

            }
        });
        //button
        ib100Dllr = (Button) findViewById(ResourceUtils.ib100Dllr);
        // Setup listener
        ib100Dllr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "**CLicked 100 Dllrs.");
                onPurchaseButtonClicked(v, "100_dollars");

            }
        });

    }

    //@todo
    public void onBillingManagerSetupFinished() {

    }

    public void onPurchaseButtonClicked(final View v, String skuId) {
        Log.d(TAG, "Purchase button clicked. view ID"+v.getId());

        if (mAcquireFragment == null) {
            mAcquireFragment = new AcquireFragment();
        }

        if (!isAcquireFragmentShown()) {
            mAcquireFragment.show(getSupportFragmentManager(), DIALOG_TAG);

            if (mBillingManager != null
                    && mBillingManager.getBillingClientResponseCode() > BILLING_MANAGER_NOT_INITIALIZED) {
                mAcquireFragment.onManagerReady(this, skuId);
            }
        }
    }

    public boolean isAcquireFragmentShown() {
        return mAcquireFragment != null && mAcquireFragment.isVisible();
    }

    @Override
    public BillingManager getBillingManager() {
        return mBillingManager;
    }

    @Override
    public boolean isPremiumPurchased() {
        return false;
    }

    @Override
    public boolean isGoldMonthlySubscribed() {
        return false;
    }

    @Override
    public boolean isTankFull() {
        return false;
    }

    @Override
    public boolean isGoldYearlySubscribed() {
        return false;
    }
}
