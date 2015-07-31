package gal.xieiro.lembramo.ui;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.widget.PopupMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.List;

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
    private static final String ARG_PARAM1 = "imageResource"; //parámetro imagen inicial

    //request codes para startActivityForResult()
    private static final int IMAGE_PICK = 1;
    private static final int IMAGE_CAPTURE = 2;


    private int mImageResource;
    private View mView; //root view of layout
    private ImageView mImageView;
    private Bitmap mImageBitmap;
    //private OnFragmentInteractionListener mListener;

    public ImageSelectorFragment() {
        // Required empty public constructor
    }

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

    /**
     * Indicates whether the specified action can be used as an intent. This
     * method queries the package manager for installed packages that can
     * respond to an intent with the specified action. If no suitable package is
     * found, this method returns false.
     * http://android-developers.blogspot.com/2009/01/can-i-use-this-intent.html
     *
     * @param context The application's environment.
     * @param action  The Intent action to check for availability.
     * @return True if an Intent with the specified action can be sent and
     * responded to, false otherwise.
     */
    public static boolean isIntentAvailable(Context context, String action) {
        final PackageManager packageManager = context.getPackageManager();
        final Intent intent = new Intent(action);
        List<ResolveInfo> list = packageManager.queryIntentActivities(intent,
                PackageManager.MATCH_DEFAULT_ONLY);
        return list.size() > 0;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mImageResource = getArguments().getInt(ARG_PARAM1);
        }
        mImageBitmap = null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.image_selector, container, false);

        //cambiar la imagen por defecto
        mImageView = (ImageView) mView.findViewById(R.id.imagen);

        if (savedInstanceState == null)
            mImageBitmap = BitmapFactory.decodeResource(getResources(), mImageResource);
        else
            mImageBitmap = savedInstanceState.getParcelable("imageBitmap");

        mImageView.setImageBitmap(mImageBitmap);

        //manejador para al pinchar el botón sobre la imagen
        setPopupMenu();

        //setOnclickView();
        return mView;
    }

    private void setOnclickView() {
        mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            // TODO: meter aquí la ampliación de la imagen a toda pantalla
            public void onClick(View v) {
                Toast.makeText(getActivity(), "Id: " + mImageView.getId(), Toast.LENGTH_LONG).show();
            }
        });
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
    public void onStart() {
        super.onStart();
    }

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
        //Log.v(TAG,"onDetach()");
        //mImageBitmap.recycle();
        //mImageBitmap = null;
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
                        //mostrar menú popup con las opciones de imagen
                        PopupMenu popup = new PopupMenu(getActivity(), v);
                        popup.inflate(R.menu.menu_image_selector);
                        checkOptionsAvailable(popup);

                        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                switch (item.getItemId()) {
                                    case (R.id.action_select_image):
                                        //coger imagen de la galería
                                        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                                                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                        galleryIntent.setType("image/*");
                                        startActivityForResult(galleryIntent, IMAGE_PICK);
                                        return true;

                                    case (R.id.action_take_photo):
                                        //sacar foto
                                        Intent photoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                        startActivityForResult(photoIntent, IMAGE_CAPTURE);
                                        return true;

                                    case (R.id.action_delete_image):
                                        mImageBitmap = BitmapFactory.decodeResource(getResources(),
                                                R.drawable.no_image);
                                        mImageView.setImageBitmap(mImageBitmap);
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
                    this.imageFromGallery(data);
                    break;
                case IMAGE_CAPTURE:
                    this.imageFromCamera(data);
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
    private void imageFromCamera(Intent data) {
        //TODO: hacer que funcione con setImage()
        //TODO: hasSystemFeature(PackageManager.FEATURE_CAMERA).
        mImageView.setImageBitmap((Bitmap) data.getExtras().get("data"));
    }

    /**
     * Image result from gallery
     * * @param resultCode
     * * @param data
     */
    private void imageFromGallery(Intent data) {
        Uri selectedImage = data.getData();
        String[] filePathColumn = {MediaStore.Images.Media.DATA};

        Cursor cursor = getActivity().getContentResolver().query(selectedImage,
                filePathColumn, null, null, null);
        cursor.moveToFirst();
        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        String filePath = cursor.getString(columnIndex);
        cursor.close();

        setImage(mImageView, filePath);
    }

    /**
     * Deshabilita las opciones del PopupMenu asociado a botón de selección de imagen
     * Si no hay un Intent que pueda resolver la acción se deshabilita
     *
     * @param popupMenu El PopupMenu al que deshabilitar opciones
     */
    private void checkOptionsAvailable(PopupMenu popupMenu) {
        Menu menu = popupMenu.getMenu();
        if (isIntentAvailable(getActivity(), Intent.ACTION_PICK))
            menu.findItem(R.id.action_select_image).setEnabled(true);
        else
            //deshabilitar la opción de escoger de galería si no hay intent que pueda resolver
            menu.findItem(R.id.action_select_image).setEnabled(false);

        if (isIntentAvailable(getActivity(), MediaStore.ACTION_IMAGE_CAPTURE))
            menu.findItem(R.id.action_take_photo).setEnabled(true);
        else
            //deshabilitar la opción de sacar foto si no hay posibilidad de cámara
            menu.findItem(R.id.action_take_photo).setEnabled(false);
    }


    /**
     * Fija una imagen del sistema en el ImageView especificado escalando según sea necesario
     *
     * @param imageView El ImageView de destino donde mostrar la imagen
     * @param imagePath La ruta de origen de la imagen
     */
    private void setImage(ImageView imageView, String imagePath) {

		/* There isn't enough memory to open up more than a couple camera photos */
        /* So pre-scale the target bitmap into which the file is decoded */

		/* Get the size of the ImageView */
        int targetW = imageView.getWidth();
        int targetH = imageView.getHeight();

		/* Get the size of the image */
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imagePath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

		/* Figure out which way needs to be reduced less */
        int scaleFactor = 1;
        if ((targetW > 0) || (targetH > 0)) {
            scaleFactor = Math.min(photoW / targetW, photoH / targetH);
        }

		/* Set bitmap options to scale the image decode target */
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;
/*
        Log.v(TAG,"photoW="+photoW+" photoH="+photoH);
        Log.v(TAG,"targetW="+targetW+" targetH="+targetH);
        Log.v(TAG,"scaleFactor: " + scaleFactor);
*/
        if (mImageBitmap != null) mImageBitmap.recycle();

        /* Decode the JPEG file into a Bitmap */
        mImageBitmap = BitmapFactory.decodeFile(imagePath, bmOptions);

		/* Associate the Bitmap to the ImageView */

        imageView.setImageBitmap(mImageBitmap);
    }

    // para recuperar la imagen cuando recree la vista desde cero
    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (mImageBitmap != null)
            outState.putParcelable("imageBitmap", mImageBitmap);

        //Log.v(TAG,"onSaveInstanceState()");
        super.onSaveInstanceState(outState);
    }
}
