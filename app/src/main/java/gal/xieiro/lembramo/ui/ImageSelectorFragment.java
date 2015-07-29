package gal.xieiro.lembramo.ui;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.widget.PopupMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import gal.xieiro.lembramo.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * { link ImageSelectorFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ImageSelectorFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ImageSelectorFragment extends Fragment {
    private static final String TAG = "ImageSelectorFragment";
    private static final String ARG_PARAM1 = "imageResource"; //imagen inicial
    private static final int IMAGE_PICK = 1;
    private static final int IMAGE_CAPTURE = 2;

    private int mImageResource;
    private View mView; //root view of layout


    //private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param imageResource Id de fichero de imagen.
     * @return A new instance of fragment ImageSelectorFragment.
     */
    public static ImageSelectorFragment newInstance(int imageResource) {
        ImageSelectorFragment fragment = new ImageSelectorFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM1, imageResource);

        fragment.setArguments(args);
        return fragment;
    }

    public ImageSelectorFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mImageResource = getArguments().getInt(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.image_selector, container, false);

        //cambiar la imagen por defecto
        ((ImageView) mView.findViewById(R.id.imagen)).setImageResource(mImageResource);

        //manejador para al pinchar el botón sobre la imagen
        setPopupMenu();

        return mView;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    /*
    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }
    *
    */

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        /*
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
        */
    }

    @Override
    public void onDetach() {
        super.onDetach();
        //mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    /*
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }
*/
    private void setPopupMenu() {
        mView.findViewById(R.id.editPhoto).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //mostrar menú popup con las opciones de imágen
                        PopupMenu popup = new PopupMenu(getActivity(), v);
                        popup.inflate(R.menu.menu_image_selector);
                        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                switch (item.getItemId()) {
                                    case (R.id.action_select_image):
                                        //coger imagen de la galería
                                        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                                                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                        galleryIntent.setType("image/*");
                                        startActivityForResult(galleryIntent, IMAGE_PICK);
                                        return true;

                                    case (R.id.action_take_photo):
                                        //sacar foto
                                        Intent photoIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                                        startActivityForResult(photoIntent, IMAGE_CAPTURE);
                                        return true;

                                    case (R.id.action_delete_image):
                                        ((ImageView) mView.findViewById(R.id.imagen)).setImageResource(R.drawable.no_image);
                                        return true;

                                    default:
                                        return false;
                                }
                            }
                        });
                        popup.show();
                    }
                });
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case IMAGE_PICK:
                    this.imageFromGallery(resultCode, data);
                    break;
                case IMAGE_CAPTURE:
                    this.imageFromCamera(resultCode, data);
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * Image result from camera
     * * @param resultCode
     * * @param data
     */
    private void imageFromCamera(int resultCode, Intent data) {
        ImageView imagen = (ImageView) mView.findViewById(R.id.imagen);
        imagen.setImageBitmap((Bitmap) data.getExtras().get("data"));
    }

    /**
     * Image result from gallery
     * * @param resultCode
     * * @param data
     */
    private void imageFromGallery(int resultCode, Intent data) {
        ImageView imagen = (ImageView) mView.findViewById(R.id.imagen);
        Uri selectedImage = data.getData();
        String[] filePathColumn = {MediaStore.Images.Media.DATA};
        Cursor cursor = getActivity().getContentResolver().query(selectedImage, filePathColumn, null, null, null);
        cursor.moveToFirst();
        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        String filePath = cursor.getString(columnIndex);
        cursor.close();
        imagen.setImageBitmap(BitmapFactory.decodeFile(filePath));
    }
}
