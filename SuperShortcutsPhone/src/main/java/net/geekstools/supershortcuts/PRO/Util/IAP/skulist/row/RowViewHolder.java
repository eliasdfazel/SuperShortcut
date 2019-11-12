/*
 * Copyright © 2019 By Geeks Empire.
 *
 * Created by Elias Fazel on 11/11/19 7:22 PM
 * Last modified 11/11/19 7:21 PM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */
package net.geekstools.supershortcuts.PRO.Util.IAP.skulist.row;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;

import net.geekstools.supershortcuts.PRO.R;

/**
 * ViewHolder for quick access to row's views
 */
public final class RowViewHolder extends RecyclerView.ViewHolder {
    public TextView purchaseItemName, purchaseItemDescription, purchaseItemPrice, purchaseItemInfo;
    public MaterialButton purchaseItemButton;
    public ImageView purchaseItemIcon;

    /**
     * Handler for a button click on particular row
     */
    public interface OnButtonClickListener {
        void onButtonClicked(int position);
    }

    public RowViewHolder(final View itemView, final OnButtonClickListener clickListener) {
        super(itemView);
        purchaseItemName = (TextView) itemView.findViewById(R.id.purchaseItemName);
        purchaseItemPrice = (TextView) itemView.findViewById(R.id.purchaseItemPrice);
        purchaseItemDescription = (TextView) itemView.findViewById(R.id.purchaseItemDescription);
        purchaseItemIcon = (ImageView) itemView.findViewById(R.id.purchaseItemIcon);
        purchaseItemButton = (MaterialButton) itemView.findViewById(R.id.purchaseItemButton);
        purchaseItemInfo = (TextView) itemView.findViewById(R.id.purchaseItemInfo);
        if (purchaseItemButton != null) {
            purchaseItemButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    clickListener.onButtonClicked(getAdapterPosition());
                }
            });
        }
    }
}
