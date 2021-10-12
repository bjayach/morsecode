package com.greencabbage.patch.util.billing;

public interface BillingProvider {
    BillingManager getBillingManager();

    boolean isPremiumPurchased();

    boolean isGoldMonthlySubscribed();

    boolean isTankFull();

    boolean isGoldYearlySubscribed();
}