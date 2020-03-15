package com.example.ac_instaclone;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.viewpager2.widget.ViewPager2;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.shashank.sony.fancytoastlib.FancyToast;

import java.io.ByteArrayOutputStream;

public class SocialMediaActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_social_media);

        //Adjust layout when keyboard pops up  (for activity.. check profileTab for fragment)
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        setTitle("Homepage");

        ViewPager2 viewPager2 = findViewById(R.id.viewPager2);
        viewPager2.setAdapter(new SocialMediaActivityAdapter(this));

        final TabLayout tabLayout = findViewById(R.id.tabLayout);
        TabLayoutMediator tabLayoutMediator = new TabLayoutMediator(
                tabLayout, viewPager2, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                switch (position) {
                    case 0:
                        tab.setText("User");
                        tab.setIcon(R.drawable.ic_user);
                        break;
                    case 1:
                        tab.setText("Profile");
                        tab.setIcon(R.drawable.ic_details);
                        break;
                    case 2:
                        tab.setText("Image");
                        tab.setIcon(R.drawable.ic_share_picture);
                        BadgeDrawable badgeDrawable = tab.getOrCreateBadge();
                        badgeDrawable.setBackgroundColor(
                                ContextCompat.getColor(getApplicationContext(), R.color.colorAccent)
                        );
                        badgeDrawable.setVisible(true);
                        badgeDrawable.setNumber(100);
                        badgeDrawable.setMaxCharacterCount(3);
                        break;
                        default:
                            break;

                }
            }
        }
        );

        tabLayoutMediator.attach();
        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                BadgeDrawable badgeDrawable = tabLayout.getTabAt(position).getOrCreateBadge();
                badgeDrawable.setVisible(false);
            }
        });
    }

    //This method inflates the menu items of my_menu so that they can be displayed on the actionbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.my_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }


    //This method handles the click event on each menu item...
    // in our case there are only two menu items (camera & logout)..
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == R.id.postImageItem) {

            if (android.os.Build.VERSION.SDK_INT >= 23 &&
                    checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED) {


                requestPermissions(new String[]
                                {Manifest.permission.READ_EXTERNAL_STORAGE},
                        3000);

            } else {

                captureImage();
            }

        } else if (item.getItemId() == R.id.logoutUserItem) {

            ParseUser.getCurrentUser().logOut();
            finish();
            Intent intent = new Intent(SocialMediaActivity.this, SignUpActivity.class);
            startActivity(intent);

        }
        
        return super.onOptionsItemSelected(item);
    }


    //This will run as soon as user grants or declines permission
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 3000) {

            if (grantResults.length > 0 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                captureImage();
            }

        }
    }

    private void captureImage() {
        Intent intent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, 4000);
    }

    //This will run as soon as the image is selected rather than waiting for an upload button to be clicked
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 4000 && resultCode == RESULT_OK && data != null) {

            try {

                Uri capturedImage = data.getData();
                Bitmap bitmap = MediaStore.Images.Media.
                        getBitmap(this.getContentResolver(),
                                capturedImage);
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
                byte[] bytes = byteArrayOutputStream.toByteArray();
                ParseFile parseFile = new ParseFile(ParseUser.getCurrentUser().getUsername() +"_"+ Math.random() +"_img.png_", bytes);


                ParseObject parseObject = new ParseObject("Photo");
                parseObject.put("picture", parseFile);
                parseObject.put("username", ParseUser.getCurrentUser().getUsername());
                final ProgressDialog dialog = new ProgressDialog(this);
                dialog.setMessage("Loading...");
                dialog.setCanceledOnTouchOutside(false);
                dialog.show();
                parseObject.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            FancyToast.makeText(SocialMediaActivity.this, "Pictured Uploaded!", Toast.LENGTH_SHORT, FancyToast.INFO, true).show();
                        } else {
                            FancyToast.makeText(SocialMediaActivity.this, "Unknown error: " + e.getMessage(), Toast.LENGTH_SHORT, FancyToast.ERROR, true).show();

                        }
                        dialog.dismiss();
                    }
                });


            } catch (Exception e) {

                e.printStackTrace();
            }

        }
    }
}
