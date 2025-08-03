package com.mj.poker.ui;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.mj.poker.Const;
import com.mj.poker.R;
import com.mj.poker.service.HttpService;

public class PayActivity extends AppCompatActivity {
    public TextView priceText;
    public TextView tipText;
    public ImageView qrcodeImage;
    public Button payedButton;

    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay);
        priceText = findViewById(R.id.priceText);
        priceText.setText("请支付"+ Const.payType.getPrice()+"元");
        tipText = findViewById(R.id.tipText);
        qrcodeImage = findViewById(R.id.qrcodeImage);
        payedButton = findViewById(R.id.payedButton);
        payedButton.setOnClickListener(v -> {
            finish();
        });
        new Thread(() -> {
            Object[] object =  HttpService.pay(Const.payType.getPayType());
            if(object!=null && object.length==2 && object[1]!=null)
                runOnUiThread(() -> {
                    Const.payNo = String.valueOf(object[0]);
                    qrcodeImage.setImageBitmap((Bitmap) object[1]);
                    qrcodeImage.setVisibility(View.VISIBLE);
                    tipText.setText("请截图后进入支付宝扫码支付");
                });
            else
                runOnUiThread(() -> {
                    tipText.setTextColor(R.color.red);
                    tipText.setText("交易失败");
                });
        }).start();
    }

    @Override
    public void finish() {
        if(Const.payNo !=null) {
            new Thread(() -> {
                Object[] errnoAndResult = HttpService.query(Const.payNo);
                switch ((int) errnoAndResult[0]) {
                    case 0:
                        if((Boolean) errnoAndResult[1]) {
                            Const.user = HttpService.loginWithoutData(Const.user.getUsername(), Const.user.getPassword());
                            runOnUiThread(() -> {
                                //MainActivity.INSTANCE.reload();
                                Toast.makeText(this, "支付成功!", Toast.LENGTH_SHORT).show();
                            });
                        } else
                            runOnUiThread(() -> Toast.makeText(this, "未支付!", Toast.LENGTH_SHORT).show());
                        break;
                    case 1:
                        runOnUiThread(() -> Toast.makeText(this, "未登录!", Toast.LENGTH_SHORT).show());
                        break;
                    case 2:
                        runOnUiThread(() -> Toast.makeText(this, "订单号错误!", Toast.LENGTH_SHORT).show());
                        break;
                    default:
                        runOnUiThread(() -> Toast.makeText(this, "无法连接至服务器!", Toast.LENGTH_SHORT).show());
                }
                Const.payNo = null;
            }).start();
        } else {
            Toast.makeText(this, "您已取消订单", Toast.LENGTH_SHORT).show();
        }
        super.finish();
    }
}