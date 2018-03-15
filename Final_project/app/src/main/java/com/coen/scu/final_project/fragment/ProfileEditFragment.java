package com.coen.scu.final_project.fragment;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.coen.scu.final_project.BuildConfig;
import com.coen.scu.final_project.R;
import com.coen.scu.final_project.activity.MainActivity;
import com.coen.scu.final_project.java.Transportation;
import com.coen.scu.final_project.java.UserProfile;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.zxing.common.StringUtils;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileEditFragment extends Fragment {
    private Uri mUri;
    private FirebaseUser mUser;
    private DatabaseReference mRef;
    private StorageReference mStorageRef;
    private Spinner mCarType;
    private Spinner mDietType;
    private EditText mUserName;
    private CircleImageView mImage;
    private Button mUpdateBtn;
    private ProgressDialog mUpdateDialog;
    private boolean mChangeImage = false;
    private boolean mFirstTime = false;
    private ArrayAdapter<CharSequence> mAdapter;
    private ArrayAdapter<CharSequence> mDietAdapter;
    private final int PICK_IMAGE_REQUEST = 999;
    protected final int CAMERA_REQUEST = 0;
    protected final int GALLERY_PICTURE = 1;

    public static String toTitleCase(String str) {

        if (str == null) {
            return null;
        }

        boolean space = true;
        StringBuilder builder = new StringBuilder(str);
        final int len = builder.length();

        for (int i = 0; i < len; ++i) {
            char c = builder.charAt(i);
            if (space) {
                if (!Character.isWhitespace(c)) {
                    // Convert to title case and switch out of whitespace mode.
                    builder.setCharAt(i, Character.toTitleCase(c));
                    space = false;
                }
            } else if (Character.isWhitespace(c)) {
                space = true;
            } else {
                builder.setCharAt(i, Character.toLowerCase(c));
            }
        }

        return builder.toString();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            startCameraIntent();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRef = FirebaseDatabase.getInstance().getReference();
        mStorageRef = FirebaseStorage.getInstance().getReference();
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            int i = bundle.getInt("key", 0);
            if (i == 1) {
                mFirstTime = true;
            }
        }


        //Log.i(TAG, FirebaseAuth.getInstance().getCurrentUser().getClass().toString());
    }

    public ProfileEditFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile_edit, container, false);
        mCarType = view.findViewById(R.id.profile_carType);
        mDietType = view.findViewById(R.id.profile_dietType);
        mUserName = view.findViewById(R.id.profile_userName);
        mImage = view.findViewById(R.id.profile_image);
        mUpdateBtn = view.findViewById(R.id.btn_updateProfile);
        displayPreUserInfo(mUser.getUid());
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        return view;
    }


    private void displayPreUserInfo(final String uid) {
        mRef.child("users").getRef().addListenerForSingleValueEvent(new ValueEventListener() {
            Context ctx = getActivity();
            ProgressDialog progressDialog = ProgressDialog.show(new ContextThemeWrapper(ctx, R.style.DialogCustom), "Getting Data",
                    "Loading...", true);

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String userName = dataSnapshot.child(uid).child("name").getValue(String.class);
                String carType = dataSnapshot.child(uid).child("car_type").getValue(String.class);
                String url = dataSnapshot.child(uid).child("image").getValue(String.class);
                String dietType = dataSnapshot.child(uid).child("diet_type").getValue(String.class);
                //display
                if (userName != null) {
                    mUserName.setText(userName);
                }
                if (dietType!= null) {
                    String diet = "No Beef";
                    if(dietType.equals("NO_BEEF")){
                        diet = "No Beef";
                    }
//                    if(dietType.equals("MEAT_HEAVY")){
//                        diet = "Meat Heavy";
//                    }
                    if(dietType.equals("BALANCED")){
                        diet = "Balanced";
                    }
                    if(dietType.equals("VEGETARIAN")){
                        diet = "Vegetarian";
                    }
                    if(dietType.equals("VEGAN")){
                        diet = "Vegan";
                    }
                    int position = mDietAdapter.getPosition("No beef");
                    mDietType.setSelection(position);
                }


                String output = "Unknown";
                if (carType != null) {

                        if(carType.equals("MOTORCYCLE")){
                           output = "Motorcycle";
                        }
                        if(carType.equals("SMALL_CAR")){
                            output = "Small Car";
                        }
                        if(carType.equals("MID_CAR")){
                            output = "Mid Car";
                        }
                        if(carType.equals("LARGE_CAR")){
                            output = "Large Car";
                        }
                        if(carType.equals("SMALL_TRUCK")){
                            output = "Small Truck";
                        }
                        if(carType.equals("LARGE_TRUCK")){
                            output = "Large Truck";
                        }
                        if(carType.equals("SUV")){
                            output = "SUV";
                        }
                        if(carType.equals("HYBRID_CAR")){
                            output = "Hybrid Car";
                        }
                        if(carType.equals("HYBRID_TRUCK")){
                            output = "Hybrid Truck";
                        }
                        if(carType.equals("ELECTRIC_CAR")){
                            output = "Electric Car";
                        }
                        if(carType.equals("ELECTRIC_TRUCK")){
                            output = "Electric Truck";
                        }
                        if(carType.equals("UNKNOWN")){
                            output = "Unknown";
                        }
                    int position = mAdapter.getPosition(output);
                    mCarType.setSelection(position);
                }
                if (dietType != null) {
                    dietType.replace("_", " ");
                    dietType = toTitleCase(dietType);
                    int position = mDietAdapter.getPosition(dietType);
                    mDietType.setSelection(position);
                }

                if (!mFirstTime && url != null) {
                    Picasso.with(getContext())
                            .load(url)
                            .resize(100, 100)
                            .into(mImage, new com.squareup.picasso.Callback() {
                                @Override
                                public void onSuccess() {
                                    progressDialog.dismiss();

                                }

                                @Override
                                public void onError() {

                                }
                            });
                } else {
                    progressDialog.dismiss();

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mAdapter = ArrayAdapter.createFromResource(getContext(),
                R.array.myCarType, android.R.layout.simple_spinner_item);
        mAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mDietAdapter = ArrayAdapter.createFromResource(getContext(),
                R.array.myDietType, android.R.layout.simple_spinner_item);
        mDietAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        if (savedInstanceState == null) {
            savedInstanceState = new Bundle();
        }
        mCarType.setAdapter(mAdapter);
        mDietType.setAdapter(mDietAdapter);
        mImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startDialog();
//                Intent intent = new Intent();
//                intent.setType("image/*");
//                intent.setAction(Intent.ACTION_PICK);
//                startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_IMAGE_REQUEST);
                mChangeImage = true;
            }
        });
        mUpdateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context ctx = getActivity();
                mUpdateDialog = ProgressDialog.show(new ContextThemeWrapper(ctx, R.style.DialogCustom), "Update",
                        "Please wait...", true);
                if (mUser != null) {
                    String carText = mCarType.getSelectedItem().toString();
                    String dietText = mDietType.getSelectedItem().toString();
                    String userNameText = mUserName.getText().toString();
                    String uid = mUser.getUid();
                    DatabaseReference userRef = mRef.child("users").child(uid);
                    if (userNameText != null) {
                        userRef.child("name").setValue(userNameText);
                    }
                    if (carText != null) {
                        String carItemText = carText.toUpperCase()
                                .replace(" ", "_");
                        Transportation.CarType selectedCarType = Transportation.CarType.fromValue(carItemText);
                        userRef.child("car_type").setValue(carItemText);
                    }
                    if (dietText != null) {
                        String dietItemText = dietText.toUpperCase()
                                .replace(" ", "_").toUpperCase();
                        userRef.child("diet_type").setValue(dietItemText);
                    }
                    if (mChangeImage) {
                        uploadPicture(mUser);
                    } else {
                        mUpdateDialog.dismiss();
//                        Fragment fragment = new ProfileFragment();
//                        getFragmentManager()
//                                .beginTransaction()
//                                .replace(R.id.flContent, fragment)
//                                .addToBackStack(null)
//                                .commit();
                        Toast.makeText(getContext(), "Update successful", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    private void startDialog() {
        AlertDialog.Builder myAlertDialog= new AlertDialog.Builder(getContext(), R.style.AppCompatAlertDialogStyle);
        myAlertDialog.setTitle("Upload Pictures Option");
        myAlertDialog.setMessage("How do you want to set your picture?");

        myAlertDialog.setPositiveButton("Gallery",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        Intent pictureActionIntent = null;

                        pictureActionIntent = new Intent(
                                Intent.ACTION_PICK,
                                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(
                                pictureActionIntent,
                                GALLERY_PICTURE);

                    }
                });

        myAlertDialog.setNegativeButton("Camera",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        checkCameraPermission();
//                        Intent intent = new Intent(
//                                MediaStore.ACTION_IMAGE_CAPTURE);
//                        File f = new File(android.os.Environment
//                                .getExternalStorageDirectory(), "temp.jpg");
//                        intent.putExtra(MediaStore.EXTRA_OUTPUT,
//                        FileProvider.getUriForFile(getContext(),
//                                BuildConfig.APPLICATION_ID + ".provider",
//                                f));
//
//                        startActivityForResult(intent,
//                                CAMERA_REQUEST);

                    }
                });
        myAlertDialog.show();
    }

    private void checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, 1);
        } else {
            startCameraIntent();
        }

    }

    private void startCameraIntent() {
        Intent intent = new Intent(
                MediaStore.ACTION_IMAGE_CAPTURE);
        File f = new File(android.os.Environment
                .getExternalStorageDirectory(), "temp.jpg");
        intent.putExtra(MediaStore.EXTRA_OUTPUT,
                FileProvider.getUriForFile(getContext(),
                        BuildConfig.APPLICATION_ID + ".provider",
                        f));
        startActivityForResult(intent,
                CAMERA_REQUEST);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == CAMERA_REQUEST) {
            File f = new File(Environment.getExternalStorageDirectory()
                    .toString());
            for (File temp : f.listFiles()) {
                if (temp.getName().equals("temp.jpg")) {
                    f = temp;
                    break;
                }
            }
            if (!f.exists()) {
                Toast.makeText(getContext(),
                        "Error while capturing image", Toast.LENGTH_LONG)
                        .show();

                return;

            }

            try {
                Bitmap bitmap = BitmapFactory.decodeFile(f.getAbsolutePath());

                bitmap = Bitmap.createScaledBitmap(bitmap, 400, 400, true);

                int rotate = 0;
                try {
                    ExifInterface exif = new ExifInterface(f.getAbsolutePath());
                    int orientation = exif.getAttributeInt(
                            ExifInterface.TAG_ORIENTATION,
                            ExifInterface.ORIENTATION_NORMAL);

                    switch (orientation) {
                        case ExifInterface.ORIENTATION_ROTATE_270:
                            rotate = 270;
                            break;
                        case ExifInterface.ORIENTATION_ROTATE_180:
                            rotate = 180;
                            break;
                        case ExifInterface.ORIENTATION_ROTATE_90:
                            rotate = 90;
                            break;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Matrix matrix = new Matrix();
                matrix.postRotate(rotate);
                bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
                        bitmap.getHeight(), matrix, true);


                mImage.setImageBitmap(bitmap);
                //storeImageTosdCard(bitmap);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        } else if (resultCode == RESULT_OK && requestCode == GALLERY_PICTURE) {
            mUri = data.getData();
            if (mUri != null) {
                try {
                    //getting image from gallery
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), mUri);
                    //Setting image to ImageView
                    mImage.setImageBitmap(bitmap);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }


    public void uploadPicture(final FirebaseUser firebaseUser) {
        byte[] data = new byte[0];
        if (mUri != null) {
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), mUri);
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 30, out);
                data = out.toByteArray();
            } catch (IOException e) {
                e.printStackTrace();
            }
            StorageReference childRef = mStorageRef.child("Portrait").child(firebaseUser.getUid());

            //uploading the image
            UploadTask uploadTask = childRef.putBytes(data);

            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Uri imageUrl = taskSnapshot.getDownloadUrl();
                    mRef.child("users").child(firebaseUser.getUid()).child("image").setValue(imageUrl.toString());
                    mUpdateDialog.dismiss();
                    Toast.makeText(getContext(), "Update successful", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getContext(), "Upload Failed -> " + e, Toast.LENGTH_SHORT).show();
                }
            });
        } else {
//            Toast.makeText(getContext(), "Select an image", Toast.LENGTH_SHORT).show();
            mUpdateDialog.dismiss();
        }
    }
}
