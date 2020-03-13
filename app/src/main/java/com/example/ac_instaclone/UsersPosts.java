package com.example.ac_instaclone;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.shashank.sony.fancytoastlib.FancyToast;

import java.util.List;

public class UsersPosts extends AppCompatActivity {

    private LinearLayout linearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users_posts);

        linearLayout = findViewById(R.id.linearLayout);

        //Get username from intent in onItemClick method of the UsersTab class and
        //initializes it with receiveUsernameFromIntent variable
        Intent getIntentObj = getIntent();
        final String receiveUsernameFromIntent = getIntentObj.getStringExtra("username"); //key name must be exactly the same
        FancyToast.makeText(this,
                receiveUsernameFromIntent,
                Toast.LENGTH_LONG, FancyToast.INFO,
                true).show();

        //Set class title
        setTitle(receiveUsernameFromIntent + "'s posts");

        //Query Photo table (or class)
        ParseQuery<ParseObject> parseQuery = new ParseQuery<>("Photo");
        parseQuery.whereEqualTo("username", receiveUsernameFromIntent);
        parseQuery.orderByDescending("createdAt");

        //Start a progressbar dialog
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("Loading...");
        dialog.show();

        //get data wrt the query
        parseQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (objects.size() > 0 && e == null) {

                    for (ParseObject post : objects) {

                        //This is how to set up UI components from code instead xml
                        final TextView postDescription = new TextView(UsersPosts.this);
                        postDescription.setText(post.get("image_des") + "");
                        ParseFile postPicture = (ParseFile) post.get("picture");
                        postPicture.getDataInBackground(new GetDataCallback() {
                            @Override
                            public void done(byte[] data, ParseException e) {

                                if (data != null && e == null) {

                                    Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);

                                    //setting up ImageView
                                    ImageView postImageView = new ImageView(UsersPosts.this);

                                    //UI component (ImageView) properties
                                    LinearLayout.LayoutParams imageView_params =
                                            new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                                                    ViewGroup.LayoutParams.WRAP_CONTENT);
                                    imageView_params.setMargins(5, 5, 5, 5);
                                    postImageView.setLayoutParams(imageView_params);
                                    postImageView.setScaleType(ImageView.ScaleType.FIT_CENTER);

                                    //set image(decoded bitmap) in the ImageView
                                    postImageView.setImageBitmap(bitmap);

                                    //UI component (TextView) properties
                                    LinearLayout.LayoutParams des_params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                                    des_params.setMargins(5, 5, 5, 15);
                                    postDescription.setLayoutParams(des_params);
                                    postDescription.setGravity(Gravity.CENTER);
                                    postDescription.setBackgroundColor(Color.RED);
                                    postDescription.setTextColor(Color.WHITE);
                                    postDescription.setTextSize(30f);

                                    //Displaying UI components
                                    linearLayout.addView(postImageView);
                                    linearLayout.addView(postDescription);

                                }

                            }
                        });


                    }



                } else {

                    FancyToast.makeText(UsersPosts.this, receiveUsernameFromIntent + " doesn't have any posts!", Toast.LENGTH_SHORT, FancyToast.INFO, true).show();
                    finish();
                }

                dialog.dismiss();
            }
        });
    }
}
