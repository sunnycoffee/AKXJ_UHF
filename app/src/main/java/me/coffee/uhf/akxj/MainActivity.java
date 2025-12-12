package me.coffee.uhf.akxj;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        UHFManager.INSTANCE.init(this);
        UHFManager.INSTANCE.setSoundID(R.raw.barcodebeep);
        findViewById(R.id.start_btn).setOnClickListener(v -> {
            UHFManager.INSTANCE.start(code -> {
                Log.d("EPC", code);
            });
        });

        findViewById(R.id.stop_btn).setOnClickListener(v -> {
            UHFManager.INSTANCE.stop();
        });
    }

}
