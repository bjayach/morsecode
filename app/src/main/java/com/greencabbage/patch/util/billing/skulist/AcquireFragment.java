package com.greencabbage.patch.util.billing.skulist;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.billingclient.api.BillingClient.SkuType;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsResponseListener;
import com.greencabbage.patch.util.billing.BillingProvider;
import com.greencabbage.patch.util.billing.skulist.row.SkuRowData;
import com.greencabbage.patch.util.billing.skulist.row.UiManager;

import com.greencabbage.patch.util.ResourceUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.android.billingclient.api.BillingClient.BillingResponse;

/**
 * Displays a screen with various in-app purchase and subscription options
 */
public class AcquireFragment extends DialogFragment {
    private static final String TAG = "AcquireFragment";

    private RecyclerView mRecyclerView;
    private SkusAdapter mAdapter;
    private View mLoadingView;
    private TextView mErrorTextView;
    private BillingProvider mBillingProvider;
    private String _skuId;
    private boolean mWasRetryServiceConnection;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, ResourceUtils.R_style_AppTheme);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(ResourceUtils.R_layout_acquire_fragment, container, false);
        mErrorTextView = root.findViewById(ResourceUtils.R_id_error_textview);
        mRecyclerView = root.findViewById(ResourceUtils.R_id_list);
        mLoadingView = root.findViewById(ResourceUtils.R_id_screen_wait);
        if (mBillingProvider != null) {
            handleManagerAndUiReady();
        }
        // Setup a toolbar for this fragment
        Toolbar toolbar = root.findViewById(ResourceUtils.R_id_toolbar);
        toolbar.setNavigationIcon(ResourceUtils.R_drawable_ic_button);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        toolbar.setTitle(ResourceUtils.R_string_button_purchase);
        return root;
    }

    /**
     * Refreshes this fragment's UI
     */
    public void refreshUI() {
        Log.d(TAG, "Looks like purchases list might have been updated - refreshing the UI");
        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }
    }

    /**
     * Notifies the fragment that billing manager is ready and provides a BillingProviders
     * instance to access it
     */
    public void onManagerReady(BillingProvider billingProvider, String skuId) {
        mBillingProvider = billingProvider;
        _skuId = skuId;
        if (mRecyclerView != null) {
            handleManagerAndUiReady();
        }
    }

    /**
     * Enables or disables "please wait" screen.
     */
    private void setWaitScreen(boolean set) {
        mRecyclerView.setVisibility(set ? View.GONE : View.VISIBLE);
        mLoadingView.setVisibility(set ? View.VISIBLE : View.GONE);
    }

    /**
     * Executes query for SKU details at the background thread
     */
    private void handleManagerAndUiReady() {
        // If Billing Manager was successfully initialized - start querying for SKUs
        Log.i(TAG, "***handleManagerAndUiReady(skuId):"+_skuId);
        setWaitScreen(true);
//        querySkuDetails();
        if (_skuId != null) {
            initiatePurchase(_skuId.trim());
        } else {
            querySkuDetails();
        }

    }

    private void displayAnErrorIfNeeded() {
        if (getActivity() == null || getActivity().isFinishing()) {
            Log.i(TAG, "No need to show an error - activity is finishing already");
            return;
        }

        mLoadingView.setVisibility(View.GONE);
        mErrorTextView.setVisibility(View.VISIBLE);
        int billingResponseCode = mBillingProvider.getBillingManager()
                .getBillingClientResponseCode();

        switch (billingResponseCode) {
            case BillingResponse.OK:
                // If manager was connected successfully, then show no SKUs error
                mErrorTextView.setText(getText(ResourceUtils.R_string_error_no_skus));
                break;
            case BillingResponse.BILLING_UNAVAILABLE:
                mErrorTextView.setText(getText(ResourceUtils.R_string_error_billing_unavailable));
                break;
            default:
                mErrorTextView.setText(getText(ResourceUtils.R_string_error_billing_default));
        }

    }

    /**
     * Queries for in-app and subscriptions SKU details and updates an adapter with new data
     */
    private void querySkuDetails() {
        long startTime = System.currentTimeMillis();

        Log.d(TAG, "querySkuDetails() got subscriptions and inApp SKU details lists for: "
                + (System.currentTimeMillis() - startTime) + "ms");

        if (getActivity() != null && !getActivity().isFinishing()) {
            final List<SkuRowData> dataList = new ArrayList<>();
            mAdapter = new SkusAdapter();
            final UiManager uiManager = createUiManager(mAdapter, mBillingProvider);
            mAdapter.setUiManager(uiManager);
            // Filling the list with all the data to render subscription rows
            List<String> subscriptionsSkus = uiManager.getDelegatesFactory()
                    .getSkuList(SkuType.SUBS);
            Log.w(TAG, "****querySkuDetails adding SUBS SKUS ");
            addSkuRows(dataList, subscriptionsSkus, SkuType.SUBS, new Runnable() {
                @Override
                public void run() {
                    // Once we added all the subscription items, fill the in-app items rows below
                    Log.w(TAG, "****querySkuDetails adding INAPP SKUS ");
                    List<String> inAppSkus = uiManager.getDelegatesFactory()
                            .getSkuList(SkuType.INAPP);
                    addSkuRows(dataList, inAppSkus, SkuType.INAPP, null);
                }
            });
        }
    }

    private void initiatePurchase(String skuId) {
        long startTime = System.currentTimeMillis();

        Log.d(TAG, "querySkuDetails() got subscriptions and inApp SKU details lists for: "
                + (System.currentTimeMillis() - startTime) + "ms");

        if (getActivity() != null && !getActivity().isFinishing()) {
            final List<SkuRowData> dataList = new ArrayList<>();
            mAdapter = new SkusAdapter();
            final UiManager uiManager = createUiManager(mAdapter, mBillingProvider);
            mAdapter.setUiManager(uiManager);
            // Filling the list with all the data to render subscription rows
            Log.w(TAG, "****querySkuDetails adding INAPP SKU:"+skuId + ":");
            List<String> inAppSkus = Collections.singletonList(skuId);
            addSkuRows(dataList, inAppSkus, SkuType.INAPP, null);
        }
    }

    private void addSkuRows(final List<SkuRowData> inList, List<String> skusList,
                            final @SkuType String billingType, final Runnable executeWhenFinished) {

        Log.w(TAG, "****mBillingProvider " + mBillingProvider);
        Log.w(TAG, "****mBillingProvider.getBillingManager() " + mBillingProvider.getBillingManager());
        mBillingProvider.getBillingManager().querySkuDetailsAsync(billingType, skusList,
                new SkuDetailsResponseListener() {
                    @Override
                    public void onSkuDetailsResponse(int responseCode, List<SkuDetails> skuDetailsList) {

                        if (responseCode != BillingResponse.OK) {
                            Log.w(TAG, "Unsuccessful query for type: " + billingType
                                    + ". Error code: " + responseCode);
                        } else if (skuDetailsList != null
                                && skuDetailsList.size() > 0) {
                            Log.w(TAG, "****skuDetailsList.size() non zero: " + skuDetailsList.size());
                            // If we successfully got SKUs, add a header in front of the row
                            @StringRes int stringRes = SkuType.INAPP.equals(billingType)
                                    ? ResourceUtils.R_string_header_inapp : ResourceUtils.R_string_header_subscriptions;
                            inList.add(new SkuRowData(getString(stringRes)));
                            // Then fill all the other rows
                            for (SkuDetails details : skuDetailsList) {
                                Log.i(TAG, "Adding sku: " + details);
                                inList.add(new SkuRowData(details, SkusAdapter.TYPE_NORMAL,
                                        billingType));
                            }

                            if (inList.size() == 0) {
                                displayAnErrorIfNeeded();
                            } else {
                                if (mRecyclerView.getAdapter() == null) {
                                    mRecyclerView.setAdapter(mAdapter);
                                    Resources res = getContext().getResources();
                                    mRecyclerView.addItemDecoration(new CardsWithHeadersDecoration(
                                            mAdapter, (int) res.getDimension(ResourceUtils.R_dimen_header_gap),
                                            (int) res.getDimension(ResourceUtils.R_dimen_row_gap)));
                                    mRecyclerView.setLayoutManager(
                                            new LinearLayoutManager(getContext()));
                                }

                                mAdapter.updateData(inList);
                                setWaitScreen(false);
                            }
                        } else {
                            // Handle empty state
                            displayAnErrorIfNeeded();
                        }

                        if (executeWhenFinished != null) {
                            executeWhenFinished.run();
                        }
                    }
                });
    }

    protected UiManager createUiManager(SkusAdapter adapter, BillingProvider provider) {
        return new UiManager(adapter, provider);
    }
}