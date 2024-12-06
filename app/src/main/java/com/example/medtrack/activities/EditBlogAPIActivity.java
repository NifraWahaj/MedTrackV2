package com.example.medtrack.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;

import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.text.InputType;
import android.text.SpannableStringBuilder;
import android.text.style.ImageSpan;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.palette.graphics.Palette;

import com.example.medtrack.R;
import com.example.medtrack.models.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import jp.wasabeef.richeditor.RichEditor;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class EditBlogAPIActivity extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;
    private TextView tvusername;
    private RichEditor mEditor;
    private String blogId;
    private Bitmap bitmap;
    private ImageButton btnPost, btnGoBack;
    private String IMGBB_API_KEY = "40c7d1f33a00acd8ecd5ed77b2bff4a9";
    private boolean isEdit = false;
    private ImageButton btnUndo, btnRedo, btnBold, btnItalic, btnStrikeThrough, btnUnderline, btnDiv, btnLeftAlign,
            btnNumberingList, btnColorPicker, btnCenterAlign, btnRightAlign, btnBlockQuote, btnBulletList, btnAddImage, btnAddLink;

    private EditText etTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_blog_apiactivity);

        Intent intent = getIntent();
        // Default to false if not provided
        isEdit = intent.getBooleanExtra("isEdit", false);
        // Load a sample bitmap in the background
        // Bitmap is representation of the image in memory.
      //  BitmapFactory helps convert image data into a Bitmap object.
        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.color_palette);

        // Toolbar buttons
        setToolbarActions();

        mEditor.setEditorHeight(200);
        mEditor.setEditorFontSize(22);
        mEditor.setEditorFontColor(Color.BLACK);
        mEditor.setPadding(10, 10, 10, 10);
        mEditor.setPlaceholder("Insert text here...");

        tvusername.setText(User.getCurrentUserName(this));

        if (isEdit == true) {
            blogId = intent.getStringExtra("blogId");
            String blogTitle = intent.getStringExtra("blogTitle");
            String blogContent = intent.getStringExtra("blogContent");
            mEditor.setHtml(blogContent);
        }


     }

    private void setToolbarActions() {
        etTitle = findViewById(R.id.etBlogTitle);
        tvusername = findViewById(R.id.tvusername);
        mEditor = findViewById(R.id.etBlogContent);
        btnNumberingList = findViewById(R.id.btnNumberingList);
        btnGoBack = findViewById(R.id.btnGoBack);
        btnPost = findViewById(R.id.btnPost);
        btnUndo = findViewById(R.id.btnUndo);
        btnRedo = findViewById(R.id.btnRedo);
        btnBold = findViewById(R.id.btnBold);
        btnItalic = findViewById(R.id.btnItalic);
        btnStrikeThrough = findViewById(R.id.btnStrikeThrough);
        btnUnderline = findViewById(R.id.btnUnderline);
        btnDiv = findViewById(R.id.btnDiv);
        btnLeftAlign = findViewById(R.id.btnLeftAlign);
        btnCenterAlign = findViewById(R.id.btnCenterAlign);
        btnRightAlign = findViewById(R.id.btnRightAlign);
        btnBlockQuote = findViewById(R.id.btnBlockQuote);
        btnBulletList = findViewById(R.id.btnBulletList);
        btnAddImage = findViewById(R.id.btnAddImage);
        btnAddLink = findViewById(R.id.btnAddLink);
        btnColorPicker = findViewById(R.id.btnColorPicker);

        // Set onClickListeners for each button
        findViewById(R.id.btnNumberingList).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setNumbers();
            }
        });


        btnPost.setOnClickListener(v ->
        {
            if (mEditor.getHtml().isEmpty() || mEditor.getHtml() == null || etTitle.getText().toString().isEmpty() || etTitle.getText().toString() == null) {
                Toast.makeText(this, "Title or Content can't be empty", Toast.LENGTH_SHORT).show();

            }
            String title = etTitle.getText().toString().trim();
            // Check if the title is empty, starts with a digit, or doesn't contain alphanumeric characters
            if (title.isEmpty()) {
                // Title is empty
                etTitle.setError("Title cannot be empty");
            } else if (title.matches("^[0-9].*")) {
                // Title starts with a digit
                etTitle.setError("Title cannot start with a digit");
            } else if (title.matches(".*[a-zA-Z0-9].*")) {
                // Title doesn't contain any alphanumeric characters
                etTitle.setError("Title must not contain alphanumeric characters");
            } else {
                saveBlogToFirebase();
                finish();
            }
        });

         btnColorPicker.setOnClickListener(v ->
        {
            showColorPickerDialog(bitmap);
        });

        btnGoBack.setOnClickListener(v -> finish());
        btnUndo.setOnClickListener(v -> mEditor.undo());
        btnRedo.setOnClickListener(v -> mEditor.redo());
        btnBold.setOnClickListener(v -> mEditor.setBold());
        btnItalic.setOnClickListener(v -> mEditor.setItalic());
        btnStrikeThrough.setOnClickListener(v -> mEditor.setStrikeThrough());
        btnUnderline.setOnClickListener(v -> mEditor.setUnderline());
        btnDiv.setOnClickListener(this::showDivPopupMenu);
        btnLeftAlign.setOnClickListener(v -> mEditor.setAlignLeft());
        btnCenterAlign.setOnClickListener(v -> mEditor.setAlignCenter());
        btnRightAlign.setOnClickListener(v -> mEditor.setAlignRight());
        btnBlockQuote.setOnClickListener(v -> mEditor.setBlockquote());
        btnBulletList.setOnClickListener(v -> mEditor.setBullets());
        btnAddImage.setOnClickListener(v -> openImagePicker());
        btnAddLink.setOnClickListener(v -> showLinkDialog());
    }

    private void showDivPopupMenu(View v) {
        PopupMenu popupMenu = new PopupMenu(this, v);
        popupMenu.getMenuInflater().inflate(R.menu.divmenu, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(item -> {
            int id = item.getItemId();
            if (id == R.id.heading1) {
                mEditor.setHeading(1);
            } else if (id == R.id.heading2) {
                mEditor.setHeading(2);
            } else if (id == R.id.heading3) {
                mEditor.setHeading(3);
            } else if (id == R.id.heading4) {
                mEditor.setHeading(4);
            } else if (id == R.id.simple) {
                mEditor.setHeading(0); // Reset to simple text
            }
            return true;
        });
        popupMenu.show();
    }

    private void showColorPickerDialog(Bitmap bitmap) {
        if (bitmap == null) {
            Log.e("ColorPicker", "Bitmap is null!");
            return;  // Exit if bitmap is null
        }

        // Generate a palette asynchronously
        Palette.from(bitmap).generate(palette -> {
            if (palette != null) {
                // Create an AlertDialog
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Pick a Color");

                // Create a layout to display the colors
                LinearLayout colorLayout = new LinearLayout(this);
                colorLayout.setOrientation(LinearLayout.HORIZONTAL);
                colorLayout.setPadding(16, 16, 16, 16);

                // Extract colors from the palette : different color types
                int[] colors = {
                        palette.getVibrantColor(0),
                        palette.getLightVibrantColor(0),
                        palette.getDarkVibrantColor(0),
                        palette.getMutedColor(0),
                        palette.getLightMutedColor(0),
                        palette.getDarkMutedColor(0)
                };
                // Log to check the extracted colors
                for (int color : colors) {
                    Log.d("ColorPicker", "Extracted color: " + color);
                }

                // Add each color to the layout
                for (int color : colors) {
                    // Only add valid colors
                    if (color != 0) {
                        View colorView = new View(this);
                        colorView.setBackgroundColor(color);
                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(100, 100);
                        params.setMargins(16, 0, 16, 0);
                        colorView.setLayoutParams(params);

                        // Set click listener to select color
                        colorView.setOnClickListener(v -> {
                            if (mEditor != null) {
                                mEditor.setEditorFontColor(color);
                                if (builder != null && builder.create() != null) {
                                    builder.create().dismiss();
                                }
                            }
                        });
                        colorLayout.addView(colorView);
                    }
                }
                // Set the layout for the dialog
                builder.setView(colorLayout);
                builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
    }

    // Method to open the image picker
    // pick images from devices external storage
    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == PICK_IMAGE_REQUEST && data != null) {
            Uri selectedImageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);
                uploadImageToImgBB(bitmap); // Upload the image to ImgBB
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Failed to load image!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void uploadImageToImgBB(Bitmap bitmap) {
        String url = "https://api.imgbb.com/1/upload?key=" + IMGBB_API_KEY;

        // Convert Bitmap to Base64
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String base64Image = Base64.encodeToString(imageBytes, Base64.DEFAULT);

        // Build the request body
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("image", base64Image)
                .build();

        // Create and show progress bar
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Uploading image...");
        progressDialog.setCancelable(false); // Prevent dialog from being dismissed
        progressDialog.show();

        // Make the HTTP request
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> {
                    progressDialog.dismiss();
                    Toast.makeText(EditBlogAPIActivity.this, "Image upload failed!", Toast.LENGTH_SHORT).show();
                });
                Log.e("ImgBB", "Upload failed", e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    try {
                        // Parse the response in a background thread
                        String responseBody = response.body().string();
                        JSONObject jsonObject = new JSONObject(responseBody);
                        String imageUrl = jsonObject.getJSONObject("data").getString("url");

                        // Run on the UI thread to handle UI-related tasks
                        runOnUiThread(() -> {
                            // Dismiss the ProgressDialog after upload finishes
                            progressDialog.dismiss();

                            // Proceed to prompt image dimensions
                            promptImageDimensions(imageUrl);
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                        runOnUiThread(() -> {
                            progressDialog.dismiss();
                            Toast.makeText(EditBlogAPIActivity.this, "Failed to parse response!", Toast.LENGTH_SHORT).show();
                        });
                    }
                } else {
                    runOnUiThread(() -> {
                        progressDialog.dismiss();
                        Toast.makeText(EditBlogAPIActivity.this, "Server error!", Toast.LENGTH_SHORT).show();
                    });
                }
            }
        });
    }

    private void promptImageDimensions(String imageUrl) {
        // Create an AlertDialog to ask for width and height
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Set Image Dimensions");

        // Create a custom layout with input fields
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(50, 40, 50, 10);

        final EditText widthInput = new EditText(this);
        widthInput.setHint("Width (px)");
        widthInput.setInputType(InputType.TYPE_CLASS_NUMBER);
        layout.addView(widthInput);

        final EditText heightInput = new EditText(this);
        heightInput.setHint("Height (px)");
        heightInput.setInputType(InputType.TYPE_CLASS_NUMBER);
        layout.addView(heightInput);

        builder.setView(layout);

        // Set dialog buttons
        builder.setPositiveButton("Insert", (dialog, which) -> {
            String width = widthInput.getText().toString().trim();
            String height = heightInput.getText().toString().trim();

            // Validate input
            if (width.isEmpty() || height.isEmpty() || !isNumeric(width) || !isNumeric(height)) {
                Toast.makeText(this, "Invalid dimensions!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Insert image with dimensions
            insertImageInHtml(imageUrl, Integer.parseInt(width), Integer.parseInt(height));
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        builder.show();
    }


    private void insertImageInHtml(String imageUrl, int width, int height) {
        String imageHtml = "<img src='" + imageUrl + "' width='" + width + "' height='" + height + "' />";
        String currentHtmlContent = mEditor.getHtml();
        if (currentHtmlContent == null) {
            currentHtmlContent = " ";
        }
        String updatedHtmlContent = currentHtmlContent + imageHtml;
        mEditor.setHtml(updatedHtmlContent);
        Toast.makeText(this, "Image inserted!", Toast.LENGTH_SHORT).show();
    }

    // Utility method to check if a string is numeric
    private boolean isNumeric(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }


    // Method to show the dialog for adding a link
    private void showLinkDialog() {
        // Create a layout for the dialog inputs
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);

        // Create input fields for URL and label
        EditText linkInput = new EditText(this);
        linkInput.setHint("Enter URL (e.g., https://example.com)");
        layout.addView(linkInput);

        EditText labelInput = new EditText(this);
        labelInput.setHint("Enter link text");
        layout.addView(labelInput);

        // Build the AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Insert Link")
                .setView(layout)
                .setPositiveButton("Insert", (dialog, which) -> {
                    String linkUrl = linkInput.getText().toString().trim();
                    String linkText = labelInput.getText().toString().trim();

                    // Validate the input
                    if (linkUrl.isEmpty() || linkText.isEmpty()) {
                        Toast.makeText(this, "URL and text are required", Toast.LENGTH_SHORT).show();
                    } else {
                        // Insert the link into the editor
                        String htmlContent = mEditor.getHtml();
                        if (htmlContent == null) {
                            mEditor.setHtml(" ");
                        }
                        mEditor.insertLink(linkUrl, linkText);

                        // Optionally style the link color
                        String styledLinkHtml = "<a href='" + linkUrl + "' style='color:blue;'>" + linkText + "</a>";
                        mEditor.setHtml(mEditor.getHtml() + styledLinkHtml);

                    }
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .create()
                .show();
    }


    private void saveBlogToFirebase() {
        // Save the data to Firebase
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://medtrack-68ec9-default-rtdb.asia-southeast1.firebasedatabase.app");
        DatabaseReference blogsRef = database.getReference("blogs");

        if (isEdit == true) {
            DatabaseReference blogRef = blogsRef.child(blogId);
            Map<String, Object> blogData = new HashMap<>();
            blogData.put("title", etTitle.getText().toString());
            blogData.put("userEmail", User.getCurrentUserEmail(this));
            blogData.put("userName", User.getCurrentUserName(this));
            blogData.put("content", mEditor.getHtml()); // Store formatted text content as JSON
            blogData.put("isApproved", false);

            blogRef.updateChildren(blogData)
                    .addOnSuccessListener(aVoid -> Toast.makeText(this, "Blog updated successfully!", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e -> Toast.makeText(this, "Failed to update blog", Toast.LENGTH_SHORT).show());
        } else {
            Map<String, Object> blogData = new HashMap<>();
            blogData.put("title", etTitle.getText().toString());

            blogData.put("userId", User.getCurrentUserId(this));

            blogData.put("content", mEditor.getHtml()); // Store formatted text content as JSON
            blogData.put("isApproved", false);
            blogData.put("reviews", new HashMap<>());
            blogData.put("ratings", new HashMap<>());

            blogsRef.push().setValue(blogData)
                    .addOnSuccessListener(aVoid -> Toast.makeText(this, "Blog saved to Firebase!", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e -> Toast.makeText(this, "Failed to save blog", Toast.LENGTH_SHORT).show());
        }
    }

  /*  public void fetchFromDB() {
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://medtrack-68ec9-default-rtdb.asia-southeast1.firebasedatabase.app");
        DatabaseReference blogsRef = database.getReference("content");
        mEditor.setHtml("");  // Clear the editor

        blogsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot blogSnapshot : dataSnapshot.getChildren()) {
                    String title = blogSnapshot.child("title").getValue(String.class);
                    String content = blogSnapshot.child("content").getValue(String.class);
                    String userName = blogSnapshot.child("userName").getValue(String.class);
                    mEditor.setHtml(content);  // Populate the editor with the fetched content
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("EditBlogAPIActivity", "Error fetching blog data: " + databaseError.getMessage());
            }
        });
    }*/
}