package gal.xieiro.lembramo.ui;

import android.support.v4.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;

import gal.xieiro.lembramo.R;


public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setNavigationIcon(R.mipmap.ic_launcher);
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_main;
    }

    public void editMedicamentos(View view) {
        //cargar la pantalla para editar medicamentos
        Intent intent = new Intent(this, ListMedicinesActivity.class);
        startActivity(intent);
    }
}
