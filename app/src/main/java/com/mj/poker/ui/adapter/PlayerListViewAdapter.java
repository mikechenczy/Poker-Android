package com.mj.poker.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.mj.poker.Const;
import com.mj.poker.R;

import java.util.ArrayList;
import java.util.List;

public class PlayerListViewAdapter extends BaseAdapter{
    private List<String> names;
    private List<String> states;
    private List<String> moneys;
    private Context mContext;
    private LayoutInflater mInflater;
    private int selectIndex = -1;

    public PlayerListViewAdapter(Context context, List<String> names, List<String> states, List<String> moneys){
        this.mContext = context;
        this.names = names;
        this.states = states;
        this.moneys = moneys;
        mInflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);//LayoutInflater.from(mContext);
    }
    @Override
    public int getCount() {
        return names.size();
    }
    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public String currentName;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;
        String name = names.get(position);
        String state = states.get(position);
        String money = moneys.get(position) + "个豆子";
        if(convertView==null){
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.horizontal_list_item, null);
            holder.name = convertView.findViewById(R.id.text_list_item);
            holder.state = convertView.findViewById(R.id.state_item);
            holder.money = convertView.findViewById(R.id.money_item);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        if(name.equals(currentName)) {
            if(name.equals(Const.user.getUsername())) {
                convertView.setBackground(mContext.getResources().getDrawable(R.drawable.me_select_background));
            } else {
                convertView.setBackground(mContext.getResources().getDrawable(R.drawable.select_background));
            }
        } else if(name.equals(Const.user.getUsername())) {
            convertView.setBackground(mContext.getResources().getDrawable(R.drawable.me_background));
        } else {
            convertView.setBackground(mContext.getResources().getDrawable(R.drawable.item_background));
        }
        if(position == selectIndex){
            convertView.setSelected(true);
        }else{
            convertView.setSelected(false);
        }
        holder.name.setText(name);
        holder.state.setText(state);
        holder.money.setText(money);
        //iconBitmap = getPropThumnail(mIconIDs[position]);
        //holder.mImage.setImageBitmap(iconBitmap);

        return convertView;
    }

    public void setData(JSONArray players) {
        List<String> names = new ArrayList<>();
        List<String> states = new ArrayList<>();
        List<String> moneys = new ArrayList<>();
        for(int i=0;i<players.size();i++) {
            names.add(players.getJSONObject(i).getString("name"));
            states.add(players.getJSONObject(i).getString("state"));
            moneys.add(players.getJSONObject(i).getString("money"));
        }
        setData(names, states, moneys);
    }

    public void setData(List<String> names, List<String> states, List<String> moneys) {
        this.names = names;
        this.states = states;
        this.moneys = moneys;
    }

    private static class ViewHolder {
        private TextView name;
        private TextView state;
        private TextView money;
    }
    public void setSelectIndex(int i){
        selectIndex = i;
    }
    public void setCurrentName(String name) {
        currentName = name;
    }
}