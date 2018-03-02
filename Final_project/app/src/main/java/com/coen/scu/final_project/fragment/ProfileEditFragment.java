package com.coen.scu.final_project.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.coen.scu.final_project.R;
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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

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
    private EditText mUserName;
    private CircleImageView mImage;
    private Button mUpdateBtn;
    private boolean mChangeImage = false;
    private boolean mFirstTime = false;
    private String mNewUserEmail;
    private ProgressDialog  mProgressDialog;
    private ArrayAdapter<CharSequence> mAdapter;
    private final int PICK_IMAGE_REQUEST = 999;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRef = FirebaseDatabase.getInstance().getReference();
        mStorageRef = FirebaseStorage.getInstance().getReference();
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            int i = bundle.getInt("key",0);
            if(i == 1) {
                mFirstTime = true;
                mNewUserEmail = bundle.getString("email");
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
        mUserName = view.findViewById(R.id.profile_userName);
        mImage = view.findViewById(R.id.profile_image);
        mUpdateBtn = view.findViewById(R.id.btn_updateProfile);
        displayPreUserInfo(mUser.getUid());
        return view;
    }


    private void displayPreUserInfo(final String uid) {
        mRef.child("users").getRef().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String userName =  dataSnapshot.child(uid).child("name").getValue(String.class);
                String carType =  dataSnapshot.child(uid).child("car_type").getValue(String.class);
                //display
                if(userName!= null){
                    mUserName.setText(userName);
                }
                if(carType!= null){
                    int position = mAdapter.getPosition(carType);
                    mCarType.setSelection(position);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        mAdapter = ArrayAdapter.createFromResource(getContext(),
                R.array.myCarType, android.R.layout.simple_spinner_item);
        mAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mCarType.setAdapter(mAdapter);
        displayPreUserPic(mUser.getUid());
        mImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_PICK);
                startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_IMAGE_REQUEST);
                mChangeImage = true;
            }
        });
        mUpdateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mProgressDialog = ProgressDialog.show(getActivity(), "Upload",
                        "Please wait...", true);
                if (mUser!= null) {
                    String carText= mCarType.getSelectedItem().toString();
                    String userNameText = mUserName.getText().toString();
                    String uid = mUser.getUid();
                    if(userNameText != null) {
                        mRef.child("users").child(uid).child("name").setValue(userNameText);
                    }

                    if(carText!= null) {
                        mRef.child("users").child(uid).child("car_type").setValue(carText);
                    }
                    if(mFirstTime) {
                        mRef.child("users").child(uid).child("email").setValue(mNewUserEmail);
                    }
                    if(mChangeImage){
                        uploadPicture(mUser);
                    }

                }
            }
        });
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            mUri= data.getData();
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


    private void displayPreUserPic(String uid) {
        try {
            final File localFile = File.createTempFile("portrait", "jpg");
            mProgressDialog = ProgressDialog.show(getActivity(), "Retrieving Data",
                    "Loading...", true);
            mStorageRef.child("Portrait").child(uid).getFile(localFile)
                    .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                            // Successfully downloaded data to local file
                            Bitmap bMap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                            mImage.setImageBitmap(bMap);
                            mProgressDialog.dismiss();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    Snackbar.make(getView(), "load pic failed", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    // Handle failed download
                    // ...
                }
            });
        }catch (IOException e){

        }
    }

    public void uploadPicture(FirebaseUser firebaseUser){
        byte[] data = new byte[0];
        if(mUri!= null) {
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
                    mProgressDialog.dismiss();
 //                   Toast.makeText(getContext(), "Upload successful", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getContext(), "Upload Failed -> " + e, Toast.LENGTH_SHORT).show();
                }
            });
        }
        else {
            Toast.makeText(getContext(), "Select an image", Toast.LENGTH_SHORT).show();
        }
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

}
