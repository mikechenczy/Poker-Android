package com.mj.poker.ui.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mj.poker.Const;
import com.mj.poker.R;
import com.mj.poker.service.HttpService;

import java.util.ArrayList;
import java.util.List;

public class RoomListAdapter extends BaseAdapter{
    public List<Integer> roomIds;
    public List<String> titles;
    public List<String> descriptions;
    public List<String> playerCounts;
    public List<Boolean> noPasswords;
    private Activity activity;
    private LayoutInflater mInflater;
    private int selectIndex = -1;

    public RoomListAdapter(Activity context, JSONArray data){
        this.activity = context;
        setData(data);
        mInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);//LayoutInflater.from(mContext);
    }
    @Override
    public int getCount() {
        return roomIds.size();
    }
    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        int roomId = roomIds.get(position);
        String title = titles.get(position);
        String description = descriptions.get(position);
        String playerCount = "人数" + playerCounts.get(position);
        boolean noPassword = noPasswords.get(position);
        if(convertView==null){
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.list_room, null);
            holder.title = convertView.findViewById(R.id.title_item);
            holder.description = convertView.findViewById(R.id.description_item);
            holder.playerCount = convertView.findViewById(R.id.player_count_item);
            holder.noPassword = convertView.findViewById(R.id.no_password_item);
            convertView.setTag(holder);
            Button enterButton = convertView.findViewById(R.id.enter_button);
            enterButton.setOnClickListener(v -> {
                if(enterButton.isEnabled()) {
                    enterButton.setEnabled(false);
                    if (noPassword) {
                        new Thread(() -> {
                            Const.roomTitle = title;
                            Const.roomDescription = description;
                            Const.roomId = roomId;
                            JSONObject object = HttpService.enterRoom(Const.roomId, null);
                            activity.runOnUiThread(() -> {
                                if (object.getInteger("errno") == 0) {
                                    enterButton.setEnabled(true);
                                } else {
                                    Toast.makeText(activity, object.getString("errMsg"), Toast.LENGTH_SHORT).show();
                                    enterButton.setEnabled(true);
                                }
                            });
                        }).start();
                    } else {
                        enterButton.setEnabled(true);
                        Const.roomTitle = title;
                        Const.roomDescription = description;
                        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                        builder.setTitle(R.string.enter_password); //设置对话框标题
                        builder.setIcon(R.drawable.password); //设置对话框标题前的图标
                        final EditText passwordText = new EditText(activity);
                        builder.setView(passwordText);
                        builder.setPositiveButton(R.string.btn_ok, (dialog, which) -> {
                            if(passwordText.getText().toString().equals("")) {
                                Toast.makeText(activity, R.string.password_cannot_be_null, Toast.LENGTH_SHORT).show();
                            } else {
                                new Thread(() -> {
                                    JSONObject object = HttpService.enterRoom(roomId, passwordText.getText().toString());
                                    switch (object.getInteger("errno")) {
                                        case 0:
                                            break;
                                        case -1:
                                            activity.runOnUiThread(() -> Toast.makeText(activity, R.string.cannot_connect_to_server, Toast.LENGTH_SHORT).show());
                                            break;
                                        default:
                                            activity.runOnUiThread(() -> Toast.makeText(activity, object.getString("errMsg"), Toast.LENGTH_SHORT).show());
                                            break;
                                    }
                                }).start();
                            }
                        });
                        builder.setNegativeButton("取消", (dialog, which) -> {});
                        builder.show();
                        //enterPasswordDialog = new EnterPasswordDialog(this, (int)room["roomId"]);
                        //enterPasswordDialog.Show();
                    }
                }
            });
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        convertView.setSelected(position == selectIndex);
        holder.title.setText(title);
        holder.description.setText(description);
        holder.playerCount.setText(playerCount);
        holder.noPassword.setText(noPassword?"没有密码":"");
        return convertView;
    }

    public void setData(JSONArray rooms) {
        List<Integer> roomIds = new ArrayList<>();
        List<String> titles = new ArrayList<>();
        List<String> descriptions = new ArrayList<>();
        List<String> playerCounts = new ArrayList<>();
        List<Boolean> noPasswords = new ArrayList<>();
        if(rooms!=null) {
            for (int i = 0; i < rooms.size(); i++) {
                roomIds.add(rooms.getJSONObject(i).getInteger("roomId"));
                titles.add(rooms.getJSONObject(i).getString("title"));
                descriptions.add(rooms.getJSONObject(i).getString("description"));
                playerCounts.add(rooms.getJSONObject(i).getString("playerCount"));
                noPasswords.add(rooms.getJSONObject(i).getBoolean("noPassword"));
            }
        }
        setData(roomIds, titles, descriptions, playerCounts, noPasswords);
    }

    public void setData(List<Integer> roomIds, List<String> titles, List<String> descriptions, List<String> playerCounts, List<Boolean> noPasswords) {
        this.roomIds = roomIds;
        this.titles = titles;
        this.descriptions = descriptions;
        this.playerCounts = playerCounts;
        this.noPasswords = noPasswords;
    }

    private static class ViewHolder {
        private TextView title;
        private TextView description;
        private TextView playerCount;
        private TextView noPassword;
    }
    public void setSelectIndex(int i){
        selectIndex = i;
    }
}