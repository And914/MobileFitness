package it.polimi.jaa.mobilefitness.profile;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;

import it.polimi.jaa.mobilefitness.R;
import it.polimi.jaa.mobilefitness.utils.UserInfo;

/**
 * Created by andre on 30/03/15.
 */
public class ProfileUserFragment extends Fragment {

    SharedPreferences mSharedPreferences;
    private static final String PREFS = "prefs";
    private static final String PREF_NAME = "name";
    private static final String PREF_SURNAME = "surname";
    private static final String PREF_BIRTHDATE = "birthdate";
    private static final String PREF_EMAIL = "email";
    private static final String PREF_WEIGHT = "weight";
    private static final String PREF_HEIGHT = "height";
    private static final String PREF_AVATAR = "avatar";

    public ProfileUserFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_user_profile, container, false);
        UserInfo user = getUserInfo();
        setUserInfo(user, rootView);
        return rootView;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        mSharedPreferences = this.getActivity().getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        if(mSharedPreferences.getString(PREF_AVATAR,"").length()>0){
            setImage(Uri.parse(mSharedPreferences.getString(PREF_AVATAR,"")));
        }
        super.onViewCreated(view, savedInstanceState);

    }

    UserInfo getUserInfo (){
        UserInfo user = new UserInfo();
        // Access the device's key-value storage
        mSharedPreferences = this.getActivity().getSharedPreferences(PREFS, Context.MODE_PRIVATE);

        user.setName(mSharedPreferences.getString(PREF_NAME,""));
        user.setBirthDate(mSharedPreferences.getString(PREF_BIRTHDATE,""));
        user.setEmail(mSharedPreferences.getString(PREF_EMAIL, ""));
        user.setHeight(mSharedPreferences.getString(PREF_HEIGHT, ""));
        user.setSurname(mSharedPreferences.getString(PREF_SURNAME,""));
        user.setWeight(mSharedPreferences.getString(PREF_WEIGHT,""));

        return user;
    }

    void setUserInfo(UserInfo user, View view){
        TextView name = (TextView) view.findViewById(R.id.user_name);
        TextView surname = (TextView) view.findViewById(R.id.user_surname);
        TextView birthDate = (TextView) view.findViewById(R.id.user_birth_date);
        TextView height = (TextView) view.findViewById(R.id.user_height);
        TextView weight = (TextView) view.findViewById(R.id.user_weight);
        TextView email = (TextView) view.findViewById(R.id.user_email);
        email.setText("Email: " + user.getEmail());
        name.setText("Name: " + user.getName());
        surname.setText("Surname: " + user.getSurname());
        birthDate.setText("Birthdate: " + user.getBirthDate());
        height.setText("Height: " + user.getHeight());
        weight.setText("Weight: " + user.getWeight());
    }

    public void setImage(Uri uri){
        try {
            Bitmap bitmap = getCorrectlyOrientedImage(getActivity(),uri);

            ImageView imageView = (ImageView) getActivity().findViewById(R.id.img_profile);
            imageView.setImageBitmap(bitmap);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int getOrientation(Uri photoUri) {
    /* it's on the external media. */
        Cursor cursor = getActivity().getContentResolver().query(photoUri,
                new String[] { MediaStore.Images.ImageColumns.ORIENTATION }, null, null, null);

        if (cursor.getCount() != 1) {
            return -1;
        }

        cursor.moveToFirst();
        return cursor.getInt(0);
    }

    public Bitmap getCorrectlyOrientedImage(Context context, Uri photoUri) throws IOException {
        InputStream is = context.getContentResolver().openInputStream(photoUri);
        BitmapFactory.Options dbo = new BitmapFactory.Options();
        dbo.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(is, null, dbo);
        is.close();

        int rotatedWidth, rotatedHeight;
        int orientation = getOrientation(photoUri);

        if (orientation == 90 || orientation == 270) {
            rotatedWidth = dbo.outHeight;
            rotatedHeight = dbo.outWidth;
        } else {
            rotatedWidth = dbo.outWidth;
            rotatedHeight = dbo.outHeight;
        }

        Bitmap srcBitmap;
        is = context.getContentResolver().openInputStream(photoUri);
        if (rotatedWidth > 512 || rotatedHeight > 512) {
            float widthRatio = ((float) rotatedWidth) / ((float) 512);
            float heightRatio = ((float) rotatedHeight) / ((float) 512);
            float maxRatio = Math.max(widthRatio, heightRatio);

            // Create the bitmap from file
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = (int) maxRatio;
            srcBitmap = BitmapFactory.decodeStream(is, null, options);
        } else {
            srcBitmap = BitmapFactory.decodeStream(is);
        }
        is.close();

    /*
     * if the orientation is not 0 (or -1, which means we don't know), we
     * have to do a rotation.
     */
        if (orientation > 0) {
            Matrix matrix = new Matrix();
            matrix.postRotate(orientation);

            srcBitmap = Bitmap.createBitmap(srcBitmap, 0, 0, srcBitmap.getWidth(),
                    srcBitmap.getHeight(), matrix, true);
        }

        return srcBitmap;
    }
}
