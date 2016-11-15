package com.bomeans.irreader;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.bomeans.IRKit.BIRReader;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

public class TestActivity extends AppCompatActivity {

    private BIRReader mIrReader;
    private TextView mResultText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        // enable the back button on the action bar
        ActionBar actionBar = getSupportActionBar();
        if (null != actionBar) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        mResultText = (TextView) findViewById(R.id.test_result);

        Button startButton = (Button) findViewById(R.id.start_button);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startTest();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        mIrReader = ((BomeansIrReaderApp) getApplication()).getIrReader();
    }

    private void startTest() {

        TestData testData = new TestData();
        Map<String, byte[]> dataMap = testData.getAllData();

        Iterator it = dataMap.entrySet().iterator();
        ArrayList<BIRReader.ReaderMatchResult> result;
        String info = "";
        while (it.hasNext()) {
            Map.Entry<String, byte[]> pair = (Map.Entry)it.next();

            if (mIrReader.load(pair.getValue(), false, true)) {
                //mIrReader.loadLearningData(pair.getValue());

                result = mIrReader.getBestMatches();
                if (null != result && result.size() > 0) {
                    info += String.format("%s: %s", pair.getKey(), result.get(0).formatId);
                    if (result.get(0).isAc()) {
                        info += "\n";
                    } else {
                        info += String.format("C:0x%X, K:0x%X\n", result.get(0).customCode, result.get(0).keyCode);
                    }
                } else {
                    info += String.format("%s: NG\n", pair.getKey());
                }
            }

            it.remove(); // avoids a ConcurrentModificationException

            mResultText.setText(info);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case android.R.id.home:
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
