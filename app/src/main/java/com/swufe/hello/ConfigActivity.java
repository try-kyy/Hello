package com.swufe.hello;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class ConfigActivity extends AppCompatActivity {
    public final String Tag = "ConfigActivity";
    EditText dollarText;
    EditText euroText;
    EditText wonText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config);
        Intent intent = getIntent();
        float dollar2 = intent.getFloatExtra("dollar_rate_key", 0.0f);
        float euro2 = intent.getFloatExtra("euro_rate_key", 0.0f);
        float won2 = intent.getFloatExtra("won_rate_key", 0.0f);
        Log.i(Tag, "onCreate:dollar2=" + dollar2);
        Log.i(Tag, "onCreate:euro2=" + euro2);
        Log.i(Tag, "onCreate:won2=" + won2);

        dollarText = findViewById(R.id.dollar_rate);
        euroText = findViewById(R.id.euro_rate);
        wonText = findViewById(R.id.won_rate);

        dollarText.setText(String.valueOf(dollar2));
        euroText.setText(String.valueOf(euro2));
        wonText.setText(String.valueOf(won2));
    }

    public void save(View btn) {
        Log.i(Tag, "save: ");
        //获取新的输入数据
        float newDollar = Float.parseFloat(dollarText.getText().toString());
        float newEuro = Float.parseFloat(euroText.getText().toString());
        float newWon = Float.parseFloat(wonText.getText().toString());
        Log.i(Tag, "save:获取到新的值");
        Log.i(Tag, "onCreate:newDollar=" + newDollar);
        Log.i(Tag, "onCreate:newEuro=" + newEuro);
        Log.i(Tag, "onCreate:newWon=" + newWon);
        //保存到Bundle或放到Extra
        Bundle bdl = new Bundle();
        Intent intent = getIntent();
        bdl.putFloat("key_dollar", newDollar);
        bdl.putFloat("key_euro", newEuro);
        bdl.putFloat("key_won", newWon);
        intent.putExtras(bdl);
        setResult(2, intent);
        //返回到调用页面
        finish();
    }

}
