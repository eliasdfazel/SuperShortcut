package net.geekstools.supershortcuts.PRO.Util.IAP.skulist;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.android.billingclient.api.BillingClient;

import net.geekstools.supershortcuts.PRO.R;
import net.geekstools.supershortcuts.PRO.Util.IAP.billing.BillingProvider;
import net.geekstools.supershortcuts.PRO.Util.IAP.skulist.row.RowViewHolder;
import net.geekstools.supershortcuts.PRO.Util.IAP.skulist.row.SkuRowData;

import java.util.List;

public class SkusAdapter extends RecyclerView.Adapter<RowViewHolder> implements RowViewHolder.OnButtonClickListener {

    Activity activity;

    List<SkuRowData> rowDataList;
    BillingProvider billingProvider;

    public SkusAdapter(BillingProvider billingProvider, Activity activity) {
        this.billingProvider = billingProvider;
        this.activity = activity;
    }

    public void updateData(List<SkuRowData> skuRowData) {
        rowDataList = skuRowData;

        notifyDataSetChanged();
    }

    @Override
    public RowViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.iap_sku_details_item, parent, false);
        return new RowViewHolder(inflate, this);
    }

    @Override
    public void onBindViewHolder(RowViewHolder rowViewHolder, int position) {
        SkuRowData skuRowData = getData(position);
        if (skuRowData != null) {
            rowViewHolder.purchaseItemName.setText(skuRowData.getTitle());
            rowViewHolder.purchaseItemDescription.setText(skuRowData.getDescription());
            rowViewHolder.purchaseItemPrice.setText(skuRowData.getPrice());
            rowViewHolder.purchaseItemButton.setEnabled(true);

            if (skuRowData.getBillingType().equals(BillingClient.SkuType.INAPP)) {
                rowViewHolder.purchaseItemInfo.setVisibility(View.VISIBLE);
            } else {
                rowViewHolder.purchaseItemInfo.setVisibility(View.INVISIBLE);
            }
        }
        switch (skuRowData.getSku()) {
            case "mix.shortcuts":
                rowViewHolder.purchaseItemIcon.setImageResource(R.drawable.draw_mix);
                rowViewHolder.purchaseItemButton.setText(activity.getString(R.string.purchase));

                break;
        }
    }

    @Override
    public int getItemCount() {
        return rowDataList == null ? 0 : rowDataList.size();
    }

    @Override
    public void onButtonClicked(int position) {
        SkuRowData skuRowData = getData(position);
        billingProvider.getBillingManager().startPurchaseFlow(skuRowData.getSkuDetails(), skuRowData.getSku(), skuRowData.getBillingType());

    }

    public SkuRowData getData(int position) {
        return rowDataList == null ? null : rowDataList.get(position);
    }
}

