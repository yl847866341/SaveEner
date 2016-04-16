package com.example.saveenergy;


import android.content.Intent;

import android.os.Bundle;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.data.MySharedPerferences;
import com.example.data.SaveEnergyDataBaseHelper;
import com.example.http.HttpConnection;
import com.example.params.SwitchStatusParams;
import com.example.tool.TimeTool;



import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class MainActivity extends AppCompatActivity implements View.OnClickListener,CompoundButton.OnCheckedChangeListener {

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    Button connectButton;
    ToggleButton toggleButton_one, toggleButton_two, toggleButton_three, toggleButton_four;
    AutoCompleteTextView keyEdit, didEdit;
    MySharedPerferences lastLoginFile, dataSettingFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        keyEdit = (AutoCompleteTextView) findViewById(R.id.key);
        didEdit = (AutoCompleteTextView) findViewById(R.id.did);
        connectButton = (Button) findViewById(R.id.connect);
        toggleButton_one = (ToggleButton) findViewById(R.id.switch_one);
        toggleButton_two = (ToggleButton) findViewById(R.id.switch_two);
        toggleButton_three = (ToggleButton) findViewById(R.id.switch_tree);
        toggleButton_four = (ToggleButton) findViewById(R.id.switch_four);
        //设置按钮记录
        getButtonStatus();
        toggleButton_one.setOnCheckedChangeListener(this);
        toggleButton_two.setOnCheckedChangeListener(this);
        toggleButton_three.setOnCheckedChangeListener(this);
        toggleButton_four.setOnCheckedChangeListener(this);
        connectButton.setOnClickListener(this);
        setSupportActionBar(toolbar);
    }


    @Override
    protected void onResume() {
        super.onResume();
        lastLoginFile = new MySharedPerferences(this, MySharedPerferences.LASTLOGIN);
        String tKey, tDid;
        tKey = lastLoginFile.getString(MySharedPerferences.LAST_COUNT, "");//获得最后一次登录账号
        tDid = lastLoginFile.getString(tKey, "");
        keyEdit.setText(tKey);
        didEdit.setText(tDid);
        String[] allKey = new String[lastLoginFile.getAllSize()];
        allKey = lastLoginFile.getALl().keySet().toArray(new String[0]);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, allKey);
        keyEdit.setAdapter(adapter);//设置自动提醒


    }


    private void getButtonStatus() {
        dataSettingFile = new MySharedPerferences(this, MySharedPerferences.DATASETTINGFILE);

        if (dataSettingFile.getBoolean("button1", false)) {
            toggleButton_one.setChecked(true);
        } else
            toggleButton_one.setChecked(false);

        if (dataSettingFile.getBoolean(MySharedPerferences.BUTTON2, false)) {
            toggleButton_two.setChecked(true);
        } else
            toggleButton_two.setChecked(false);

        if (dataSettingFile.getBoolean(MySharedPerferences.BUTTON3, false)) {
            toggleButton_three.setChecked(true);
        } else {
            toggleButton_three.setChecked(false);
        }

        if (dataSettingFile.getBoolean(MySharedPerferences.BUTTON4, false)) {
            toggleButton_four.setChecked(true);
        } else {
            toggleButton_four.setChecked(false);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, EnergyCalculateActivity.class);
            startActivity(intent);
            return true;
        }


        return super.onOptionsItemSelected(item);
    }

    public void connect() {
        connectButton.setText("断开");
        String key = keyEdit.getText().toString();
        String did = didEdit.getText().toString();
        SwitchStatusParams.key = key;
        SwitchStatusParams.did = did;
        MySharedPerferences lastLoginFile = new MySharedPerferences(this, MySharedPerferences.LASTLOGIN);
        lastLoginFile.setString(MySharedPerferences.LAST_COUNT, key);
        lastLoginFile.setString(key, did);
        didEdit.setEnabled(false);
        keyEdit.setEnabled(false);


    }


    public void disConnect(){
        connectButton.setText("连接");
        keyEdit.setEnabled(true);
        didEdit.setEnabled(true);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.connect:
                if(connectButton.getText().equals("连接")){
                    connect();
                }else{
                    disConnect();
                }
                break;

        }

    }

    private void controlSwitch(int whichOne, String command) {
        Map<String, String> paramsMap = new HashMap<String, String>();
        paramsMap.clear();


        Log.d("MainActivity", "case1");
        paramsMap.put("did", SwitchStatusParams.did);
        paramsMap.put("key", SwitchStatusParams.key);
        paramsMap.put("ctrl", "L"+whichOne + command);
        paramsMap.put("t", "2");
        HttpConnection.postMes(paramsMap);



    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch ((buttonView.getId())) {
            case R.id.switch_one:   setData(1, isChecked);
                                    if(isChecked){
                                        Log.d("switchOne","执行");
                                        dataSettingFile.setBoolean(MySharedPerferences.BUTTON1, true);
                                        dataSettingFile.setString(MySharedPerferences.BUTTON1_ON_TIME, TimeTool.getTime());
                                    }else {
                                        dataSettingFile.setBoolean(MySharedPerferences.BUTTON1, false);
                                    }
                                    break;

            case R.id.switch_two:  setData(2,isChecked);

                                   if(isChecked){
                                       Log.d("switchtwo","执行");
                                       dataSettingFile.setBoolean(MySharedPerferences.BUTTON2, true);
                                       dataSettingFile.setString(MySharedPerferences.BUTTON2_ON_TIME, TimeTool.getTime());
                                   }else {
                                       dataSettingFile.setBoolean(MySharedPerferences.BUTTON2, false);
                                   }
                                   break;

            case R.id.switch_tree:  setData(3,isChecked);

                if(isChecked){
                    dataSettingFile.setBoolean(MySharedPerferences.BUTTON3, true);
                    dataSettingFile.setString(MySharedPerferences.BUTTON3_ON_TIME, TimeTool.getTime());
                }else {
                    dataSettingFile.setBoolean(MySharedPerferences.BUTTON3, false);
                }
                break;


            case R.id.switch_four:  setData(4,isChecked);

                if(isChecked){
                    dataSettingFile.setBoolean(MySharedPerferences.BUTTON4, true);
                    dataSettingFile.setString(MySharedPerferences.BUTTON4_ON_TIME, TimeTool.getTime());
                }else {
                    dataSettingFile.setBoolean(MySharedPerferences.BUTTON4, false);
                }
                break;

        }
    }


    public void setData(int whichOne, boolean isChecked) {
        if (dataSettingFile == null) {
            dataSettingFile = new MySharedPerferences(this, MySharedPerferences.DATASETTINGFILE);
        }
        if (isChecked) {
            controlSwitch(whichOne, "ON");
        } else {
            controlSwitch(whichOne, "OFF");
        }
    }

}