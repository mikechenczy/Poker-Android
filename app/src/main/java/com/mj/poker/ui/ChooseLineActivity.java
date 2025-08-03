/*package com.mj.poker.newUi;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.mj.poker.Define;
import com.mj.poker.R;
import com.mj.poker.model.Location;
import com.mj.poker.service.HttpService;
import com.mj.poker.util.DataUtil;
import com.mj.poker.util.ImageUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChooseLineActivity extends AppCompatActivity{
    public List<Location> locations;
    public SimpleAdapter adapter;
    public ListView listView;

    @Override
    protected void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_line);
        ((Toolbar)findViewById(R.id.id_toolbar)).setNavigationOnClickListener(v -> finish());
        locations = new ArrayList<>();
        new Thread(() -> {
            locations = HttpService.getLocations();
            if(locations!=null) {
                runOnUiThread(() -> {
                    List<Map<String, Object>> lists = new ArrayList<>();
                    int[] images = ImageUtils.getFlagsFromLocation(locations);
                    for (int i = 0; i < locations.size(); i++) {
                        Map<String, Object> map = new HashMap<>();
                        map.put("image", images[i]);
                        map.put("name", locations.get(i).toString());
                        lists.add(map);
                    }
                    adapter = new SimpleAdapter(this, lists, R.layout.list_item, new String[]{"image", "name"}, new int[]{R.id.locationImage, R.id.locationName}) {
                        @Override
                        public View getView(int position, View convertView, ViewGroup parent) {
                            View view = super.getView(position, convertView, parent);
                            view.setOnClickListener(v -> {
                                Define.currentLocation = locations.get(position);
                                MainActivity.INSTANCE.currentLineText.setText(getString(R.string.currentLocation) + Define.currentLocation);
                                Toast.makeText(ChooseLineActivity.this, getString(R.string.chose) + Define.currentLocation, Toast.LENGTH_SHORT).show();
                                new Thread(DataUtils::save).start();
                                finish();
                            });
                            return view;
                        }
                    };
                    listView = findViewById(R.id.listview);
                    listView.setAdapter(adapter);
                });
            } else
                Toast.makeText(this, R.string.cannot_connect_to_server, Toast.LENGTH_SHORT).show();
        }).start();
    }
}*/