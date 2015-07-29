package gal.xieiro.lembramo;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;


public class MainActivity extends BaseActivity {

    static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Log.v(TAG,"onCreate MainActivity OK");
        super.onCreate(savedInstanceState);
        setNavigationIcon(R.mipmap.ic_launcher);
        /*
        DBAdapter db = new DBAdapter(this);
        db.open();
        db.close();
        */
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_main;
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public void editMedicamentos(View view) {
        //cargar la pantalla para editar medicamentos
        Intent intent = new Intent(this, ListMedicinesActivity.class);
        startActivity(intent);
    }
}
