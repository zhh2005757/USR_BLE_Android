package com.usr.usrsimplebleassistent;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.bluetooth.BluetoothGattCharacteristic;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateInterpolator;
import android.widget.AdapterView;
import android.widget.ListView;

import com.usr.usrsimplebleassistent.Utils.GattAttributes;
import com.usr.usrsimplebleassistent.adapter.CharacteristicsAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import butterknife.BindView;

public class CharacteristicsActivity extends MyBaseActivity {
    @BindView(R.id.view_filter)
    View filterView;
    @BindView(R.id.lv_characteristics)
    ListView lvCharacteristics;
    @BindView(R.id.view_shadow)
    View viewShadow;

    private final List<BluetoothGattCharacteristic> list = new ArrayList<>();
    private CharacteristicsAdapter adapter;

    private MyApplication myApplication;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_characteristics);
        bindToolBar();

        myApplication = (MyApplication) getApplication();

        List<BluetoothGattCharacteristic> characteristics = ((MyApplication)getApplication()).getCharacteristics();
        list.addAll(characteristics);

        //we create a virtual BluetoothGattCharacteristic just for debug
        if (getIntent().getBooleanExtra("is_usr_service",false)){
            BluetoothGattCharacteristic usrVirtualCharacteristic =
                    new BluetoothGattCharacteristic(UUID.fromString(GattAttributes.USR_SERVICE),-1,-1);
            list.add(usrVirtualCharacteristic);
        }


        adapter = new CharacteristicsAdapter(this,list);
        lvCharacteristics.setAdapter(adapter);

        lvCharacteristics.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                myApplication.setCharacteristic(list.get(position));
                Intent intent = new Intent(CharacteristicsActivity.this,GattDetailActivity.class);
                startActivity(intent);
                overridePendingTransition(0,0);
            }
        });


        if (savedInstanceState == null) {
            filterView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    filterView.getViewTreeObserver().removeOnPreDrawListener(this);
                    startEndAnimation();
                    return true;
                }
            });
        }
    }
    private void startEndAnimation() {
        filterView.setAlpha(0.0f);
        filterView.setVisibility(View.VISIBLE);
        filterView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        filterView.animate()
                .alpha(0.6f)
                .setDuration(200)
                .setInterpolator(new AccelerateInterpolator())
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        viewShadow.setVisibility(View.VISIBLE);
                        lvCharacteristics.setVisibility(View.VISIBLE);
                        animate2();
                    }
                })
                .start();

    }

    private void animate2(){
        filterView.animate()
                .alpha(0.0f)
                .setDuration(200)
                .setInterpolator(new AccelerateInterpolator())
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        filterView.setLayerType(View.LAYER_TYPE_NONE, null);
                        filterView.setVisibility(View.GONE);
                    }
                })
                .start();
    }

}
