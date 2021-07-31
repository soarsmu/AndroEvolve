package com.bian.dan.blr.adapter.warehouse;

import android.app.Activity;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bian.dan.blr.R;
import com.zxdc.utils.library.bean.SellingOutBound;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SellingAdapter extends BaseAdapter {

    private Activity activity;
    private List<SellingOutBound.ListBean> list;

    public SellingAdapter(Activity activity, List<SellingOutBound.ListBean> list) {
        super();
        this.activity = activity;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list == null ? 0 : list.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    ViewHolder holder = null;

    public View getView(int position, View view, ViewGroup parent) {
        if (view == null) {
            view = LayoutInflater.from(activity).inflate(R.layout.item_selling, null);
            holder = new ViewHolder(view);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        SellingOutBound.ListBean listBean = list.get(position);
        holder.tvName.setText(Html.fromHtml("申请人：<font color=\"#000000\">" + listBean.getSellName() + "</font>"));
        holder.tvTime.setText(listBean.getCreateDate());
        if(listBean.getStatus()==0){
            holder.tvStatus.setText(Html.fromHtml("出售状态：<font color=\"#FE8E2C\">未出售</font>"));
        }else{
            holder.tvStatus.setText(Html.fromHtml("出售状态：<font color=\"#70DF5D\">已出售</font>"));
        }
        switch (listBean.getState()){
            case 0:
                 holder.tvState.setText(Html.fromHtml("审批状态：<font color=\"#FE8E2C\">未审批</font>"));
                 break;
            case 1:
                holder.tvState.setText(Html.fromHtml("审批状态：<font color=\"#70DF5D\">审批通过</font>"));
                break;
            case 2:
                holder.tvState.setText(Html.fromHtml("审批状态：<font color=\"#FF4B4C\">审批未通过</font>"));
                break;
            default:
                break;
        }
        return view;
    }


    static
    class ViewHolder {
        @BindView(R.id.tv_name)
        TextView tvName;
        @BindView(R.id.tv_state)
        TextView tvState;
        @BindView(R.id.tv_status)
        TextView tvStatus;
        @BindView(R.id.tv_time)
        TextView tvTime;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}

