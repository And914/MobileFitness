package it.polimi.jaa.mobilefitness.profile;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.login.widget.ProfilePictureView;
import com.parse.ParseUser;

import java.io.IOException;
import java.io.InputStream;

import it.polimi.jaa.mobilefitness.R;
import it.polimi.jaa.mobilefitness.utils.UserInfo;
import it.polimi.jaa.mobilefitness.utils.Utils;

/**
 * Created by andre on 30/03/15.
 */
public class ProfileUserFragment extends Fragment implements View.OnClickListener{

    SharedPreferences mSharedPreferences;

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
        mSharedPreferences = this.getActivity().getSharedPreferences(Utils.PREFS, Context.MODE_PRIVATE);
        if(mSharedPreferences.getString(Utils.PREF_AVATAR,"").length()>0){
            setImage(Uri.parse(mSharedPreferences.getString(Utils.PREF_AVATAR,"")));
        }
        else if(ParseUser.getCurrentUser().has("fbId")){
            ProfilePictureView profilePictureView = (ProfilePictureView) view.findViewById(R.id.friend_profile_image_large);
            profilePictureView.setProfileId(ParseUser.getCurrentUser().getString("fbId"));
            profilePictureView.setPresetSize(ProfilePictureView.LARGE);

        }
        else {
            ImageView imageView = (ImageView) getActivity().findViewById(R.id.img_profile);
            imageView.setImageDrawable(getResources().getDrawable(R.drawable.avatar));
            imageView.setVisibility(View.VISIBLE);
        }
        super.onViewCreated(view, savedInstanceState);

    }

    UserInfo getUserInfo (){
        UserInfo user = new UserInfo();
        // Access the device's key-value storage
        mSharedPreferences = this.getActivity().getSharedPreferences(Utils.PREFS, Context.MODE_PRIVATE);

        user.setName(mSharedPreferences.getString(Utils.PREF_NAME,""));
        user.setBirthDate(mSharedPreferences.getString(Utils.PREF_BIRTHDATE,""));
        user.setEmail(mSharedPreferences.getString(Utils.PREF_EMAIL, ""));
        user.setHeight(mSharedPreferences.getString(Utils.PREF_HEIGHT, ""));
        user.setSurname(mSharedPreferences.getString(Utils.PREF_SURNAME,""));
        user.setWeight(mSharedPreferences.getString(Utils.PREF_WEIGHT,""));

        return user;
    }

    void setUserInfo(UserInfo user, View view){
        TextView name = (TextView) view.findViewById(R.id.user_name);
        TextView surname = (TextView) view.findViewById(R.id.user_surname);
        TextView birthDate = (TextView) view.findViewById(R.id.user_birth_date);
        TextView height = (TextView) view.findViewById(R.id.user_height);
        TextView weight = (TextView) view.findViewById(R.id.user_weight);
        TextView email = (TextView) view.findViewById(R.id.user_email);
        final ImageButton nameButton = (ImageButton) view.findViewById(R.id.user_name_imagebtn);
        ImageButton surnameButton = (ImageButton) view.findViewById(R.id.user_surname_imagebtn);
        ImageButton birthDateButton = (ImageButton) view.findViewById(R.id.user_birth_date_imagebtn);
        ImageButton heightButton = (ImageButton) view.findViewById(R.id.user_height_imagebtn);
        ImageButton weightButton = (ImageButton) view.findViewById(R.id.user_weight_imagebtn);

        email.setText(user.getEmail());
        name.setText(user.getName());
        surname.setText(user.getSurname());
        birthDate.setText(user.getBirthDate());
        height.setText(user.getHeight());
        weight.setText(user.getWeight());
        nameButton.setImageResource(R.drawable.edit_profile);
        surnameButton.setImageResource(R.drawable.edit_profile);
        birthDateButton.setImageResource(R.drawable.edit_profile);
        heightButton.setImageResource(R.drawable.edit_profile);
        weightButton.setImageResource(R.drawable.edit_profile);

        nameButton.setOnClickListener(this);
        surnameButton.setOnClickListener(this);
        birthDateButton.setOnClickListener(this);
        heightButton.setOnClickListener(this);
        weightButton.setOnClickListener(this);


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
        int result = cursor.getInt(0);
        cursor.close();
        return result;
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

    @Override
    public void onClick(View v) {
        FragmentManager fragmentManager = ((FragmentActivity) v.getContext()).getSupportFragmentManager();
        Bundle bundle = new Bundle();
        EditProfileFragment editProfileFragment = new EditProfileFragment();
        switch (v.getId()) {
            case R.id.user_name_imagebtn:
                bundle.putString("value",Utils.PREF_NAME);
                break;
            case R.id.user_surname_imagebtn:
                bundle.putString("value",Utils.PREF_SURNAME);
                break;
            case R.id.user_birth_date_imagebtn:
                bundle.putString("value",Utils.PREF_BIRTHDATE);
                break;
            case R.id.user_height_imagebtn:
                bundle.putString("value",Utils.PREF_HEIGHT);
                break;
            case R.id.user_weight_imagebtn:
                bundle.putString("value",Utils.PREF_WEIGHT);
                break;
            default:
                break;
        }
        editProfileFragment.setArguments(bundle);
        fragmentManager.beginTransaction().replace(R.id.container, editProfileFragment).addToBackStack("tag").commit();

    }
}
