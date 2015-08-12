package gal.xieiro.lembramo.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import gal.xieiro.lembramo.R;

public abstract class BaseActivity extends AppCompatActivity {

    protected final String TAG = this.getClass().getSimpleName();
    protected Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResource());
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        if (mToolbar != null) {
            setSupportActionBar(mToolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    // la subclase debe implementar este método para establecer la vista que se muestra
    protected abstract int getLayoutResource();

    protected void setNavigationIcon(int iconRes) {
        mToolbar.setNavigationIcon(iconRes);
    }

    protected void setToolbarTitle(int resId) {
        //si no ejecuta setSupportActionbar() no hace el setTitle()
        setSupportActionBar(mToolbar);
        mToolbar.setTitle(resId);
    }
}
