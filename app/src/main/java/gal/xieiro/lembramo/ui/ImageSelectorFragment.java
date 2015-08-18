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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.File;
import java.io.IOException;

import gal.xieiro.lembramo.R;
import gal.xieiro.lembramo.util.Utils;

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
    private String mCurrentImagePath;

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

    public void setImage(String uri) {
        if (mImageView != null && uri != null) {
            ImageLoader.getInstance().displayImage(uri, mImageView);
            mCurrentImagePath = uri;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mImageResource = getArguments().getInt(ARG_PARAM1);
        } else mImageResource = 0;

        mImageBitmap = null;
        mCurrentImagePath = null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.image_selector, container, false);

        //cambiar la imagen por defecto
        mImageView = (ImageView) mView.findViewById(R.id.imagen);

        if (savedInstanceState == null) {
            if (mImageResource != 0)
                mImageBitmap = BitmapFactory.decodeResource(getResources(), mImageResource);
        } else
            mImageBitmap = savedInstanceState.getParcelable("imageBitmap");

        if (mImageBitmap != null)
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
                Toast.makeText(getActivity(), "Pendiente ampliación imagen", Toast.LENGTH_LONG).show();
            }
        });
    }

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
                                        dispatchPickImageIntent();
                                        return true;

                                    case (R.id.action_take_photo):
                                        //sacar foto
                                        dispatchTakePhotoIntent();
                                        return true;

                                    case (R.id.action_delete_image):
                                        //borrar imagen
                                        dispatchDeleteImage();
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

    /**
     * Método de retorno del Intent
     *
     * @param requestCode Código de la acción
     * @param resultCode Código que indica si el resultado fue correcto
     * @param data Resultado
     */
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case IMAGE_PICK:
                    imageFromGallery(data);
                    break;
                case IMAGE_CAPTURE:
                    imageFromCamera();
                    break;
                default:
                    break;
            }
        }
    }

    // Crea un Intent para elegir una imagen de la galería
    private void dispatchPickImageIntent() {
        Intent galleryIntent = new Intent(
                Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, IMAGE_PICK);
    }

    // Crea un Intent para sacar una foto para usar como imagen
    private void dispatchTakePhotoIntent() {
        Intent photoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        try {
            File f = Utils.createImageFile(getString(R.string.album_name));
            Uri uri = Uri.fromFile(f);
            mCurrentImagePath = uri.toString();
            photoIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        } catch (IOException ioe) {
            Log.v(TAG, "Fallo al crear fichero para foto.");
            ioe.printStackTrace();
            mCurrentImagePath = null;
        }
        startActivityForResult(photoIntent, IMAGE_CAPTURE);
    }

    private void dispatchDeleteImage() {
        mImageResource = R.drawable.no_image;
        mImageBitmap = BitmapFactory.decodeResource(getResources(), mImageResource);
        mImageView.setImageBitmap(mImageBitmap);
        mCurrentImagePath = null;
    }

    private void imageFromCamera() {
        ImageLoader.getInstance().displayImage(mCurrentImagePath, mImageView);
        galleryAddPic();
    }

    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE");
        File f = new File(mCurrentImagePath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        getActivity().sendBroadcast(mediaScanIntent);
    }

    private void imageFromGallery(Intent data) {
        Uri selectedImage = data.getData();
        String[] filePathColumn = {MediaStore.Images.Media.DATA};

        Cursor cursor = getActivity().getContentResolver().query(selectedImage,
                filePathColumn, null, null, null);
        cursor.moveToFirst();
        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        String filePath = cursor.getString(columnIndex);
        cursor.close();

        mCurrentImagePath = Uri.fromFile(new File(filePath)).toString();
        ImageLoader.getInstance().displayImage(mCurrentImagePath, mImageView);
    }

    /**
     * Deshabilita las opciones del PopupMenu asociado a botón de selección de imagen
     * Si no hay un Intent que pueda resolver la acción se deshabilita
     *
     * @param popupMenu El PopupMenu al que deshabilitar opciones
     */
    private void checkOptionsAvailable(PopupMenu popupMenu) {
        Menu menu = popupMenu.getMenu();
        if (Utils.isIntentAvailable(getActivity(), Intent.ACTION_PICK))
            menu.findItem(R.id.action_select_image).setEnabled(true);
        else
            //deshabilitar la opción de escoger de galería si no hay intent que pueda resolver
            menu.findItem(R.id.action_select_image).setEnabled(false);

        if (Utils.isIntentAvailable(getActivity(), MediaStore.ACTION_IMAGE_CAPTURE))
            menu.findItem(R.id.action_take_photo).setEnabled(true);
        else
            //deshabilitar la opción de sacar foto si no hay posibilidad de cámara
            menu.findItem(R.id.action_take_photo).setEnabled(false);
    }

    // para recuperar la imagen cuando recree la vista desde cero
    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (mImageBitmap != null)
            outState.putParcelable("imageBitmap", mImageBitmap);
        super.onSaveInstanceState(outState);
    }

    public String getImagePath() {
        return mCurrentImagePath;
    }
}
