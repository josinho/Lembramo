package gal.xieiro.lembramo.ui;


import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

import gal.xieiro.lembramo.R;
import gal.xieiro.lembramo.ui.component.DividerItemDecoration;

public class ShapeFragment extends Fragment {

    private Context mContext;
    private RecyclerView mShapeList;
    private ShapeAdapter mShapeAdapter;


    public static ShapeFragment newInstance() {
        return new ShapeFragment();
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ShapeFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.shape_list, container, false);

        mShapeAdapter = new ShapeAdapter(getListDrawables());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);

        mShapeList = (RecyclerView) view.findViewById(R.id.shapeList);
        /*
        mShapeList.addItemDecoration(
                new DividerItemDecoration(getActivity(), DividerItemDecoration.HORIZONTAL_LIST)
        );
        */
        mShapeList.setLayoutManager(linearLayoutManager);
        mShapeList.setAdapter(mShapeAdapter);

        return view;
    }

    private List<Drawable> getListDrawables() {
        List<Drawable> list = new ArrayList<>();

        TypedArray icons = getResources().obtainTypedArray(R.array.drawables_list);
        for (int i = 0; i < icons.length(); i++) {
            list.add(icons.getDrawable(i));
        }
        icons.recycle();
        return list;
    }

    private class ShapeAdapter extends RecyclerView.Adapter<ShapeAdapter.ViewHolder> {

        private List<Drawable> mShapes;

        // View lookup cache
        public class ViewHolder extends RecyclerView.ViewHolder {
            ImageView shape;

            public ViewHolder(View itemView) {
                // Stores the itemView in a public final member variable that can be used
                // to access the context from any ViewHolder instance.
                super(itemView);
                shape = (ImageView) itemView.findViewById(R.id.shape);
            }
        }

        public ShapeAdapter(List<Drawable> shapes) {
            mShapes = shapes;
        }

        @Override
        public ShapeAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            Context context = parent.getContext();
            LayoutInflater inflater = LayoutInflater.from(context);

            // Inflate the custom layout
            View itemView = inflater.inflate(R.layout.shape_item, parent, false);

            // Return a new holder instance
            return new ViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(final ShapeAdapter.ViewHolder viewHolder, int position) {
            // Get the data model based on position

            Drawable normalDrawable = mShapes.get(position);
            Drawable wrapDrawable = DrawableCompat.wrap(normalDrawable);
            DrawableCompat.setTint(wrapDrawable, getResources().getColor(R.color.bpRed));
            viewHolder.shape.setImageDrawable(wrapDrawable);
        }

        // Returns the total count of items
        @Override
        public int getItemCount() {
            return mShapes.size();
        }
    }
}

