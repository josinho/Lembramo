package gal.xieiro.lembramo.ui;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import gal.xieiro.lembramo.R;

public class CommentFragment extends Fragment {

    public static CommentFragment newInstance() {
        return new CommentFragment();
    }

    public CommentFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_comments, container, false);
        return v;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

    }
}