package com.usr.usrsimplebleassistent;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MyBaseActivity extends AppCompatActivity {
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        getWindow().setBackgroundDrawable(null);
    }
    protected void bindToolBar(){
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.mipmap.ic_arrow_back_white_36dp);
        if (Build.VERSION.SDK_INT >= 23) {
            toolbar.setTitleTextColor(getColor(android.R.color.white));
        } else {
            toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_usr, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_share:
                 share();
                break;
            case R.id.menu_usr:
                System.out.println("--------------->检查更新");
                Intent intent = new Intent(this, UpdateActivity.class);
                startActivity(intent);
                //Uri uri = Uri.parse("http://www.baidu.com");
                //Intent it = new Intent(Intent.ACTION_VIEW, uri);
                //startActivity(it);
                break;
            case android.R.id.home:
                System.out.println("--------------->home");
                menuHomeClick();
                break;
        }
        return super.onOptionsItemSelected(item);
    }



    public void share(){
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_TITLE, getString(R.string.app_name));
        shareIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_text));
        shareIntent.setType("text/plain");

        Intent chooserIntent = Intent.createChooser(shareIntent, getString(R.string.share_title));
        if (chooserIntent == null) {
            return;
        }
        try {
            startActivity(chooserIntent);
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(this, R.string.share_ex, Toast.LENGTH_SHORT).show();
        }
    }


    protected void menuHomeClick(){
        //默认返回上一层
        finish();
        overridePendingTransition(0,R.anim.slide_top_to_bottom);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(0,R.anim.slide_top_to_bottom);
    }

}
