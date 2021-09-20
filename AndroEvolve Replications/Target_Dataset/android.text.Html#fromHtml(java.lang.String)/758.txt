package com.androidadvance.opensourcebitcoinwallet.fragments;

import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.androidadvance.opensourcebitcoinwallet.R;
import java.util.List;

public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.ViewHolder> {
  private List<String> values;

  public class ViewHolder extends RecyclerView.ViewHolder {
    public TextView row_tx_date;
    public TextView row_tx_amount;
    public TextView row_tx_balance;

    public View layout;

    public ViewHolder(View v) {
      super(v);
      layout = v;
      row_tx_date = v.findViewById(R.id.row_tx_date);
      row_tx_amount = v.findViewById(R.id.row_tx_amount);
      row_tx_balance = v.findViewById(R.id.row_tx_balance);
    }
  }

  public void add(int position, String item) {
    values.add(position, item);
    notifyItemInserted(position);
  }

  public void remove(int position) {
    values.remove(position);
    notifyItemRemoved(position);
  }

  public HomeAdapter(List<String> myDataset) {
    values = myDataset;
  }

  @Override public HomeAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    LayoutInflater inflater = LayoutInflater.from(parent.getContext());
    View v = inflater.inflate(R.layout.row_home, parent, false);
    ViewHolder vh = new ViewHolder(v);
    return vh;
  }

  @Override public void onBindViewHolder(ViewHolder holder, final int position) {
    final String[] txInfo = values.get(position).split("###");
    String date = txInfo[0];
    String amount = txInfo[1];
    String balance = txInfo[2];
    holder.row_tx_date.setText(Html.fromHtml(date));
    if(amount.contains("-")){
      holder.row_tx_amount.setText(Html.fromHtml("<font color='#ff4444'>" + amount+"</font>"));
    }else {
      holder.row_tx_amount.setText(Html.fromHtml("<font color='#669900'>" + amount+"</font>"));
    }
    holder.row_tx_balance.setText(Html.fromHtml(balance));
  }

  @Override public int getItemCount() {
    return values.size();
  }
}