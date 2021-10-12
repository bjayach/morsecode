package com.greencabbage.patch.util.billing.skulist.row;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.greencabbage.patch.util.ResourceUtils;


/**
 * ViewHolder for quick access to row's views
 */
public final class RowViewHolder extends RecyclerView.ViewHolder {
    public TextView title, description, price;
    public Button button;
    public ImageView skuIcon;

    /**
     * Handler for a button click on particular row
     */
    public interface OnButtonClickListener {
        void onButtonClicked(int position);
    }

    public RowViewHolder(final View itemView, final OnButtonClickListener clickListener) {
        super(itemView);
        title = (TextView) itemView.findViewById(ResourceUtils.R_id_title);
        price = (TextView) itemView.findViewById(ResourceUtils.R_id_price);
        description = (TextView) itemView.findViewById(ResourceUtils.R_id_description);
        skuIcon = (ImageView) itemView.findViewById(ResourceUtils.R_id_sku_icon);
        button = (Button) itemView.findViewById(ResourceUtils.R_id_state_button);
        if (button != null) {
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    clickListener.onButtonClicked(getAdapterPosition());
                }
            });
        }
    }
}