package com.greencabbage.patch.util.billing.skulist.row;

/**
 * Provider for data that corresponds to a particular row
 */
public interface RowDataProvider {
    SkuRowData getData(int position);
}