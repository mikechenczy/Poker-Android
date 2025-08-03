/*package com.mj.poker.ui;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.alipay.sdk.app.PayTask;
import com.mj.poker.Define;
import com.mj.poker.R;
import com.mj.poker.model.PayType;
import com.mj.poker.pay.PayResult;
import com.mj.poker.service.HttpService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChoosePayActivity extends AppCompatActivity{
    public PayType[] payTypes;
    public SimpleAdapter adapter;
    private ListView listView;

    private static final int SDK_PAY_FLAG = 1;

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @SuppressWarnings("unused")
        public void handleMessage(Message msg) {
            if (msg.what == SDK_PAY_FLAG) {
                @SuppressWarnings("unchecked")
                PayResult payResult = new PayResult((Map<String, String>) msg.obj);
                //对于支付结果，请商户依赖服务端的异步通知结果。同步通知结果，仅作为支付结束的通知。
                String resultInfo = payResult.getResult();// 同步返回需要验证的信息
                String resultStatus = payResult.getResultStatus();
                // 判断resultStatus 为9000则代表支付成功
                if(Define.payNo !=null) {
                    new Thread(() -> {
                        Object[] errnoAndResult = HttpService.query(Define.payNo);
                        switch ((int) errnoAndResult[0]) {
                            case 0:
                                if((Boolean) errnoAndResult[1]) {
                                    Define.user = HttpService.loginWithoutData(Define.user.getUsername(), Define.user.getPassword());
                                    runOnUiThread(() -> {
                                        MainActivity.INSTANCE.reload();
                                        Toast.makeText(ChoosePayActivity.this, "支付成功!", Toast.LENGTH_SHORT).show();
                                        ChoosePayActivity.this.finish();
                                    });
                                } else
                                    runOnUiThread(() -> Toast.makeText(ChoosePayActivity.this, "未支付!", Toast.LENGTH_SHORT).show());
                                break;
                            case 1:
                                runOnUiThread(() -> Toast.makeText(ChoosePayActivity.this, "未登录!", Toast.LENGTH_SHORT).show());
                                break;
                            case 2:
                                runOnUiThread(() -> Toast.makeText(ChoosePayActivity.this, "订单号错误!", Toast.LENGTH_SHORT).show());
                                break;
                            default:
                                runOnUiThread(() -> Toast.makeText(ChoosePayActivity.this, "无法连接至服务器!", Toast.LENGTH_SHORT).show());
                        }
                        Define.payNo = null;
                    }).start();
                } else {
                    Toast.makeText(ChoosePayActivity.this, "您已取消订单", Toast.LENGTH_SHORT).show();
                }
            }
        }
    };

    @Override
    protected void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_pay);
        ((Toolbar)findViewById(R.id.id_toolbar)).setNavigationOnClickListener(v -> finish());
        new Thread(() -> {
            payTypes = HttpService.getPayTypes();
            if(payTypes!=null) {
                runOnUiThread(() -> {
                    List<Map<String, Object>> lists = new ArrayList<>();
                    for (int i = 0; i < payTypes.length; i++) {
                        Map<String, Object> map = new HashMap<>();
                        map.put("image", R.mipmap.ic_launcher);
                        map.put("name", payTypes[i].getPrice()+"元"+payTypes[i].getTime());
                        lists.add(map);
                    }
                    adapter = new SimpleAdapter(this, lists, R.layout.list_item, new String[]{"image", "name"}, new int[]{R.id.locationImage, R.id.locationName}) {
                        @Override
                        public View getView(int position, View convertView, ViewGroup parent) {
                            View view = super.getView(position, convertView, parent);
                            view.setOnClickListener(v -> {
                                Define.payType = payTypes[position];
                                new Thread(() -> {
                                    Object[] orderInfos = HttpService.getOrderInfo(Define.payType.getPayType());
                                    if (orderInfos != null && orderInfos.length == 2 && orderInfos[1] != null) {
                                        Define.payNo = String.valueOf(orderInfos[0]);
                                        String orderInfo = (String) orderInfos[1];
                                        Runnable payRunnable = () -> {
                                            PayTask alipay = new PayTask(ChoosePayActivity.this);
                                            Map<String, String> result = alipay.payV2(orderInfo, true);
                                            Message msg = new Message();
                                            msg.what = SDK_PAY_FLAG;
                                            msg.obj = result;
                                            mHandler.sendMessage(msg);
                                        };
                                        // 必须异步调用
                                        Thread payThread = new Thread(payRunnable);
                                        payThread.start();
                                    } else {
                                        runOnUiThread(() -> Toast.makeText(ChoosePayActivity.this, "无法连接至服务器", Toast.LENGTH_SHORT).show());
                                    }
                                }).start();
                            });
                            //Intent intent = new Intent(ChoosePayActivity.this, PayActivity.class);
                            //startActivity(intent);
                            return view;
                        }
                    };
                    listView = findViewById(R.id.listview);
                    listView.setAdapter(adapter);
                });
            } else {
                runOnUiThread(() -> Toast.makeText(this, R.string.cannot_connect_to_server, Toast.LENGTH_SHORT).show());
                finish();
            }
        }).start();
    }
}*/