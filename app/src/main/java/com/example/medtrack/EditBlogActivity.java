package com.example.medtrack;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.Html;
import android.text.InputType;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.BulletSpan;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StrikethroughSpan;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;
import android.util.Base64;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class EditBlogActivity extends AppCompatActivity {
    private static final int PICK_IMAGE = 1;
    private static final int SAVE_STATE_INTERVAL = 500;  // 500ms interval for saving state

    private EditText etBlogContent,etTitle;
    private SpannableStringBuilder spannableStringBuilder;
    private UndoManager undoManager;

    private boolean isBoldActive = false;
    private boolean isItalicActive = false;
    private boolean isUnderLine = false;
    private boolean isStrikeThrough = false;
    private boolean isBulletList = false;
    private boolean isNumberedList = false;
    private boolean isMajorHeading =false;
    private boolean isHeading =false;
    private boolean isSubHeading =false;
    private boolean isMinorHeading =false;
    private boolean isSimpleText =false;
    private boolean isBloqQuote=false;

    private String lastSavedText = "";  // Track last saved state of the text
    private Handler handler = new Handler();
    private Runnable saveStateRunnable;  // Runnable to periodically save state
    private Stack<CharSequence> undoStack = new Stack<>();
    private Stack<CharSequence> redoStack = new Stack<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_blog);

        etBlogContent = findViewById(R.id.etBlogContent);
        etTitle= findViewById(R.id.etBlogTitle);
        spannableStringBuilder = new SpannableStringBuilder(etBlogContent.getText());
        undoManager = new UndoManager();
        etBlogContent.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);  // Set a base size for the text in SP

        etBlogContent.setText(spannableStringBuilder);
        etBlogContent.setMovementMethod(LinkMovementMethod.getInstance()); // for adding links

        // TextWatcher to apply formatting to newly typed text when bold/italic is active
        etBlogContent.addTextChangedListener(new TextWatcher() {
            private int lastStart = 0;
            private String lastText = "";  // Variable to hold the previous text state

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                lastStart = start; // Save the cursor position before text change
                lastText = s.toString();  // Save the current state of the text before it changes
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                int start = lastStart;
                int end = etBlogContent.getSelectionStart(); // Adjust end based on current cursor
                String currentText = s.toString();

                // Only save the new state if the text has changed and the interval has passed
                if (!currentText.equals(lastText)) {
                    lastText = currentText;
                    saveTextState(currentText);  // Save new state after change
                }

                // Apply bold or italic style to newly inserted text
                if (isBoldActive && end > start) {
                    applyStyleToRange(Typeface.BOLD, start, end);
                }
                if (isItalicActive && end > start) {
                    applyStyleToRange(Typeface.ITALIC, start, end);
                }
                if(isUnderLine && end >start){
                    applyUnderline(start, end);  // Apply underline to the range
                }
                if (isStrikeThrough && end > start) {
                    applyStrikethrough(start, end);  // Apply strikethrough to the range
                }
                if (isBulletList && end > start) {
                    applyBulletList(start, end);  // Apply bullet list to the range
                }

                // Heading formatting
                if (isMajorHeading && end > start) {
                    applyMajorHeading(start, end);  // Example size for Major Heading
                }
                else  if (isHeading && end > start) {
                    applyHeading(start, end);  // Example size for Heading
                }
                else if (isSubHeading && end > start) {
                    applySubHeading(start, end);  // Example size for Sub Heading
                }
                else if (isMinorHeading && end > start) {
                    applyMinorHeading(start, end);  // Example size for Minor Heading
                }
                else if(isSimpleText && end>start){
                    applySimpleText(start,end);
                }
                if(isBloqQuote && end>start){
                    applyBlockquote(start,end);
                }
            }
        });

        setupButtons();
    }

    private void setupButtons() {
        findViewById(R.id.btnBold).setOnClickListener(v -> toggleBold());
        findViewById(R.id.btnItalic).setOnClickListener(v -> toggleItalic());
        findViewById(R.id.btnUnderline).setOnClickListener(v -> {
            toggleUnderLine();  // First function call
            applyUnderline();  // Second function call
        });
        findViewById(R.id.btnStrikeThrough).setOnClickListener(v -> {
            toggleStrikeThrough();  // First function call
            applyStrikethrough();  // Second function call
        });
        // findViewById(R.id.btnNumberingList).setOnClickListener(v->{//toggleNumberedList();});
        findViewById(R.id.btnBulletList).setOnClickListener(v -> toggleBulletList());
        findViewById(R.id.btnAddLink).setOnClickListener(v->{
            showLinkDialog();
            saveFormattedText();
            restoreFormattedText();
        });

        findViewById(R.id.btnDiv).setOnClickListener(v -> {
            // Create a PopupMenu instance
            PopupMenu popupMenu = new PopupMenu(EditBlogActivity.this, v);

            // Inflate the menu from XML resource
            popupMenu.getMenuInflater().inflate(R.menu.divmenu, popupMenu.getMenu());

            // Access the submenu items by their IDs
            MenuItem heading1 = popupMenu.getMenu().findItem(R.id.heading1);
            MenuItem heading2 = popupMenu.getMenu().findItem(R.id.heading2);
            MenuItem heading3 = popupMenu.getMenu().findItem(R.id.heading3);
            MenuItem heading4 = popupMenu.getMenu().findItem(R.id.heading4);
            MenuItem simpleText = popupMenu.getMenu().findItem(R.id.simple);

            int start = etBlogContent.getSelectionStart();
            int end = etBlogContent.getSelectionEnd();

            // Set a listener for menu item clicks
            popupMenu.setOnMenuItemClickListener(item -> {
                // Handle each submenu item click here
                String itemTitle=item.getTitle().toString();
                switch ( itemTitle) {
                    case "Major Heading":
                        applyMajorHeading(start, end);
                        resetHeadingFlags();

                        isMajorHeading=true;
                        Toast.makeText(EditBlogActivity.this, "Major Heading Selected", Toast.LENGTH_SHORT).show();
                        return true;
                    case "Heading":
                        applyHeading(start,end);
                        resetHeadingFlags();

                        isHeading=true;

                        Toast.makeText(EditBlogActivity.this, "Heading Selected", Toast.LENGTH_SHORT).show();
                        return true;
                    case "Sub Heading":
                        resetHeadingFlags();

                        isSubHeading=true;

                        applySubHeading(start,end);
                        Toast.makeText(EditBlogActivity.this, "Sub Heading Selected", Toast.LENGTH_SHORT).show();
                        return true;
                    case "Minor Heading":
                        resetHeadingFlags();

                        isMinorHeading=true;

                        applyMinorHeading(start,end);
                        Toast.makeText(EditBlogActivity.this, "Minor Heading Selected", Toast.LENGTH_SHORT).show();
                        return true;
                    case "Simple":
                        resetHeadingFlags();

                        isSimpleText=true;

                        applySimpleText(start,end);
                        Toast.makeText(EditBlogActivity.this, "Simple Text Selected", Toast.LENGTH_SHORT).show();
                        return true;
                    default:
                        return false;
                }
            });

            // Show the PopupMenu
            popupMenu.show();
        });

        findViewById(R.id.btnBlockQuote).setOnClickListener(v->{toggleBloqQuote();});
        findViewById(R.id.btnLeftAlign).setOnClickListener(v -> applyLeftAlign());
        findViewById(R.id.btnCenterAlign).setOnClickListener(v -> applyCenterAlign());
        findViewById(R.id.btnRightAlign).setOnClickListener(v -> applyRightAlign());
        findViewById(R.id.btnJustify).setOnClickListener(v -> applyJustify());
        findViewById(R.id.btnUndo).setOnClickListener(v -> undoText());
        findViewById(R.id.btnRedo).setOnClickListener(v -> redoText());
        findViewById(R.id.btnAddImage).setOnClickListener(v -> openImagePicker());
    }
    private void toggleBloqQuote(){

        isBloqQuote=!isBloqQuote;
        int start = etBlogContent.getSelectionStart();
        int end = etBlogContent.getSelectionEnd();

        if (start != end) { // Only apply blockquote if there is text selected
            applyBlockquote(start,end);
        }
    }
    private void applyBlockquote(int start, int end) {
        Spannable spannableText = etBlogContent.getText();
        clearPreviousStyles(spannableText, start, end);  // Clear previous styles


        // Apply a leading margin (indentation) for blockquote
        int indentSize = 40; // Adjust this value to control the indentation level
        spannableText.setSpan(new android.text.style.LeadingMarginSpan.Standard(indentSize, 0), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        // Optionally, apply a color to the text to make it stand out (usually grey or another subtle color)
        spannableText.setSpan(new ForegroundColorSpan(Color.GRAY), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        // Optionally, apply an italic style to make blockquote text stand out more
        spannableText.setSpan(new StyleSpan(Typeface.ITALIC), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
    }

    private void resetHeadingFlags(){
        isMajorHeading =false;
        isHeading =false;
        isSubHeading =false;
        isMinorHeading =false;
        isSimpleText =false;
    }
    private void applyMajorHeading(int start, int end) {
        Spannable spannableText = etBlogContent.getText();
        clearPreviousStyles(spannableText, start, end); // Clear previous styles
        spannableText.setSpan(new StyleSpan(Typeface.BOLD), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);  // Bold for Major Heading
        spannableText.setSpan(new android.text.style.RelativeSizeSpan(2.0f), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);  // Larger size

        // Apply italic if needed
        if (isItalicActive) {
            spannableText.setSpan(new StyleSpan(Typeface.ITALIC), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);  // Italic
        }
    }

    private void applyHeading(int start, int end) {
        Spannable spannableText = etBlogContent.getText();
        clearPreviousStyles(spannableText, start, end); // Clear previous styles
        spannableText.setSpan(new StyleSpan(Typeface.BOLD), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);  // Bold for Heading
        spannableText.setSpan(new android.text.style.RelativeSizeSpan(1.6f), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);  // Slightly smaller size than Major Heading

        // Apply italic if needed
        if (isItalicActive) {
            spannableText.setSpan(new StyleSpan(Typeface.ITALIC), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);  // Italic
        }
    }

    private void applySubHeading(int start, int end) {
        Spannable spannableText = etBlogContent.getText();
        clearPreviousStyles(spannableText, start, end); // Clear previous styles
        spannableText.setSpan(new StyleSpan(Typeface.BOLD), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);  // Bold for Sub Heading
        spannableText.setSpan(new android.text.style.RelativeSizeSpan(1.3f), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);  // Smaller size than Heading

        // Apply italic if needed
        if (isItalicActive) {
            spannableText.setSpan(new StyleSpan(Typeface.ITALIC), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);  // Italic
        }
    }

    private void applyMinorHeading(int start, int end) {
        Spannable spannableText = etBlogContent.getText();
        clearPreviousStyles(spannableText, start, end); // Clear previous styles
        spannableText.setSpan(new StyleSpan(Typeface.BOLD), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);  // Bold for Minor Heading
        spannableText.setSpan(new android.text.style.RelativeSizeSpan(1.1f), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);  // Slightly larger than normal text

        // Apply italic if needed
        if (isItalicActive) {
            spannableText.setSpan(new StyleSpan(Typeface.ITALIC), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);  // Italic
        }
    }

    private void applySimpleText(int start, int end) {
        Spannable spannableText = etBlogContent.getText();
        clearPreviousStyles(spannableText, start, end); // Clear previous styles
        spannableText.setSpan(new android.text.style.RelativeSizeSpan(1.0f), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);  // Normal text size

        // Apply italic if needed
        if (isItalicActive) {
            spannableText.setSpan(new StyleSpan(Typeface.ITALIC), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);  // Italic
        }
    }

    // Helper method to clear any previous styles in the specified range
    private void clearPreviousStyles(Spannable spannableText, int start, int end) {
        // Remove all StyleSpans and RelativeSizeSpans in the range
        StyleSpan[] styleSpans = spannableText.getSpans(start, end, StyleSpan.class);
        for (StyleSpan span : styleSpans) {
            spannableText.removeSpan(span);
        }

        RelativeSizeSpan[] sizeSpans = spannableText.getSpans(start, end, RelativeSizeSpan.class);
        for (RelativeSizeSpan span : sizeSpans) {
            spannableText.removeSpan(span);
        }
    }


    private void toggleBold() {
        isBoldActive = !isBoldActive;
        int start = etBlogContent.getSelectionStart();
        int end = etBlogContent.getSelectionEnd();
        if (start < end) {
            applyStyleToRange(Typeface.BOLD, start, end);
        }
    }
    private void toggleUnderLine(){
        isUnderLine=!isUnderLine;
    }
    private  void toggleStrikeThrough(){
        isStrikeThrough=!isStrikeThrough;
    }
    private void applyStyleToRange(int style, int start, int end) {
        if (start < 0 || end > etBlogContent.length() || start >= end) return;
        Spannable spannableText = etBlogContent.getText();
        spannableText.setSpan(new StyleSpan(style), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
    }

    private void toggleItalic() {
        isItalicActive = !isItalicActive;
        int start = etBlogContent.getSelectionStart();
        int end = etBlogContent.getSelectionEnd();
        if (start < end) {
            applyStyleToRange(Typeface.ITALIC, start, end);
        }
    }
    private void applyUnderline(int start, int end) {
        if (start == end) return;  // Do nothing if there's no selection

        Spannable spannableText = etBlogContent.getText();

        // Remove existing underline spans in the selected range
        UnderlineSpan[] spans = spannableText.getSpans(start, end, UnderlineSpan.class);
        for (UnderlineSpan span : spans) {
            spannableText.removeSpan(span);
        }

        // Apply underline to the selected range
        spannableText.setSpan(new UnderlineSpan(), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
    }
    private void applyStrikethrough(int start, int end) {
        if (start == end) return;  // Do nothing if there's no selection

        Spannable spannableText = etBlogContent.getText();

        // Remove existing strikethrough spans in the selected range
        StrikethroughSpan[] spans = spannableText.getSpans(start, end, StrikethroughSpan.class);
        for (StrikethroughSpan span : spans) {
            spannableText.removeSpan(span);
        }

        // Apply strikethrough to the selected range
        spannableText.setSpan(new StrikethroughSpan(), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
    }
    private void toggleBulletList() {
        isBulletList = !isBulletList;
        int start = etBlogContent.getSelectionStart();
        int end = etBlogContent.getSelectionEnd();
        if (start < end) {
            applyBulletList(start, end);  // Apply bullet points to the selected text
        }
    }



    private void applyBulletList(int start, int end) {
        if (start == end) return;  // Do nothing if there's no selection

        Spannable spannableText = etBlogContent.getText();

        // Remove any existing bullet points in the selected range
        BulletSpan[] spans = spannableText.getSpans(start, end, BulletSpan.class);
        for (BulletSpan span : spans) {
            spannableText.removeSpan(span);
        }

        // Apply bullet point to the selected range
        spannableText.setSpan(new BulletSpan(10), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
    }

    private void applyUnderline() {
        int start = etBlogContent.getSelectionStart();
        int end = etBlogContent.getSelectionEnd();
        if (start == end) return;

        Spannable spannableText = etBlogContent.getText();
        UnderlineSpan[] spans = spannableText.getSpans(start, end, UnderlineSpan.class);
        boolean isUnderlined = false;

        for (UnderlineSpan span : spans) {
            spannableText.removeSpan(span);
            isUnderlined = true;
        }

        if (!isUnderlined) {
            spannableText.setSpan(new UnderlineSpan(), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
    }

    private void applyStrikethrough() {
        int start = etBlogContent.getSelectionStart();
        int end = etBlogContent.getSelectionEnd();
        if (start == end) return;

        Spannable spannableText = etBlogContent.getText();
        StrikethroughSpan[] spans = spannableText.getSpans(start, end, StrikethroughSpan.class);
        boolean isStrikethrough = false;

        for (StrikethroughSpan span : spans) {
            spannableText.removeSpan(span);
            isStrikethrough = true;
        }

        if (!isStrikethrough) {
            spannableText.setSpan(new StrikethroughSpan(), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
    }

    private void showLinkDialog() {
        // Create an AlertDialog with an EditText input
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter URL");

        // Create an EditText for URL input
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_URI); // Only allow valid URL input
        builder.setView(input);

        builder.setPositiveButton("OK", (dialog, which) -> {
            String url = input.getText().toString().trim();
            if (!url.isEmpty()) {
                temp(url);
                // insertLinkIntoText(url);
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> {
            dialog.cancel(); // Dismiss the dialog on Cancel
        });

        builder.show(); // Show the dialog
    }
    public void temp(String url) {
        // Create Spannable text from the URL itself
        SpannableString spannableString = new SpannableString(url);

        // Create ClickableSpan
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                // Open the URL in Chrome explicitly
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                intent.setPackage("com.android.chrome");  // Target Chrome

                // If Chrome is installed, use it; otherwise, fallback to default browser
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                } else {
                    // If Chrome is not installed, open the default browser
                    Intent defaultIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(defaultIntent);
                }
            }

            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(getResources().getColor(android.R.color.holo_blue_dark));  // Set the color to blue
                ds.setUnderlineText(true);  // Set the text to be underlined
            }
        };

        // Apply the clickable span to the entire URL
        spannableString.setSpan(clickableSpan, 0, url.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        // Set the spannable text to EditText
        etBlogContent.setText(spannableString);

        // Enable clickable links in EditText
        etBlogContent.setMovementMethod(LinkMovementMethod.getInstance());
    }


    private void insertLinkIntoText(String url) {
        Editable spannableText = etBlogContent.getText();
        int start = etBlogContent.getSelectionStart();
        int end = etBlogContent.getSelectionEnd();

        // If there's no selection, append the URL at the cursor position
        if (start == end) {
            start = end = spannableText.length(); // Append at the end
        }

        // Create a clickable span for the URL with blue and underlined text style
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                // When the link is clicked, open the URL in the browser
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(intent);
            }

            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                super.updateDrawState(ds);
                // Set the color of the link to blue
                ds.setColor(Color.BLUE);
                // Underline the text
                ds.setUnderlineText(true);
            }
        };

        // Set the clickable span in the selected text or insert it at the cursor position
        spannableText.setSpan(clickableSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        // Set the URL as plain text (you can customize the URL styling if needed)
        spannableText.replace(start, end, url);

        // Update the EditText with the new content
        etBlogContent.setText(spannableText);

        // Make the text clickable
        etBlogContent.setMovementMethod(LinkMovementMethod.getInstance());
    }


    private void applyLeftAlign() {
        etBlogContent.setGravity(Gravity.START);
    }

    private void applyCenterAlign() {
        etBlogContent.setGravity(Gravity.CENTER);
    }

    private void applyRightAlign() {
        etBlogContent.setGravity(Gravity.END);
    }

    private void applyJustify() {
        etBlogContent.setGravity(Gravity.START | Gravity.END);
    }

    private void saveTextState(String newText) {
        String currentText = etBlogContent.getText().toString();
        if (!newText.equals(lastSavedText)) {
            lastSavedText = newText;
            undoManager.addEdit(currentText, newText);  // Only add to undo manager if new text is different
        }
    }

    private void undoText() {
        String currentText = etBlogContent.getText().toString();
        String undoneText = undoManager.undo(currentText);  // Get the text after undo
        etBlogContent.setText(undoneText);  // Set the text after undo
        etBlogContent.setSelection(undoneText.length());  // Move cursor to the end
        Toast.makeText(this, "Undone", Toast.LENGTH_SHORT).show();
    }

    private void redoText() {
        String currentText = etBlogContent.getText().toString();
        String redoneText = undoManager.redo(currentText);  // Get the text after redo
        etBlogContent.setText(redoneText);  // Set the text after redo
        etBlogContent.setSelection(redoneText.length());  // Move cursor to the end
        Toast.makeText(this, "Redone", Toast.LENGTH_SHORT).show();
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == PICK_IMAGE && data != null) {
            Uri selectedImageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);
                insertImageInEditText(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    private String bitmapToBase64(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
        byte[] byteArray = outputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

    private Bitmap base64ToBitmap(String base64String) {
        byte[] decodedString = Base64.decode(base64String, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
    }

    private void insertImageInEditText(Bitmap bitmap) {
        // Convert the image to Base64 and store it in FormattedText
        String imageBase64 = bitmapToBase64(bitmap);

        FormattedText formattedText = new FormattedText();
        formattedText.setImageBase64(imageBase64);

        Drawable drawable = new BitmapDrawable(getResources(), bitmap);
        drawable.setBounds(0, 0, 400, 400);
        ImageSpan imageSpan = new ImageSpan(drawable);

        int start = etBlogContent.getSelectionStart();
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(etBlogContent.getText());
        spannableStringBuilder.insert(start, " "); // Insert a space to avoid text replacement
        spannableStringBuilder.setSpan(imageSpan, start, start + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        etBlogContent.setText(spannableStringBuilder);
        etBlogContent.setSelection(start + 1);
        etBlogContent.requestFocus();
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(saveStateRunnable);  // Remove any pending save task
    }
    class UndoManager {
        private static final int MAX_UNDO_HISTORY = 30;  // Limit to 30 undo states
        private Stack<String> undoStack = new Stack<>();
        private Stack<String> redoStack = new Stack<>();

        public void addEdit(String oldText, String newText) {
            // If the undo stack exceeds the maximum size, remove the oldest item
            if (undoStack.size() >= MAX_UNDO_HISTORY) {
                undoStack.remove(0);  // Remove the oldest entry if we exceed the limit
            }
            undoStack.push(oldText);
            // redoStack.clear();  // Clear redo stack after a new change
        }

        public String undo(String currentText) {

            if (undoStack.isEmpty()) return currentText;  // No more undos
            String previousText = undoStack.pop();
            redoStack.push(currentText);  // Push current text to redo stack
            return previousText;
        }

        public String redo(String currentText) {
            Toast.makeText(getApplicationContext(),"  redo stack size" + redoStack.size(), Toast.LENGTH_SHORT).show();

            if (redoStack.isEmpty()) return currentText;  // No more redos
            String nextText = redoStack.pop();
            undoStack.push(currentText);  // Push current text to undo stack
            return nextText;
        }
    }
    private void saveFormattedText() {
        Spannable spannableText = etBlogContent.getText();
        List<FormattedText> formattedTexts = new ArrayList<>();

        for (int i = 0; i < spannableText.length(); i++) {
            char c = spannableText.charAt(i);

            FormattedText formattedText = new FormattedText();
            int start = i;
            int end = i + 1;

            // Set regular text or image
            ImageSpan[] imageSpans = spannableText.getSpans(start, end, ImageSpan.class);
            if (imageSpans.length > 0) {
                Drawable drawable = imageSpans[0].getDrawable();
                if (drawable instanceof BitmapDrawable) {
                    Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
                    String imageBase64 = bitmapToBase64(bitmap);
                    formattedText.setImageBase64(imageBase64);  // Set image in Base64
                }
            } else {
                formattedText.setText(String.valueOf(c));
            }

            // Apply formatting
            StyleSpan[] styleSpans = spannableText.getSpans(start, end, StyleSpan.class);
            for (StyleSpan span : styleSpans) {
                if (span.getStyle() == Typeface.BOLD) {
                    formattedText.setBold(true);
                }
                if (span.getStyle() == Typeface.ITALIC) {
                    formattedText.setItalic(true);
                }
            }

            StrikethroughSpan[] strikethroughSpans = spannableText.getSpans(start, end, StrikethroughSpan.class);
            if (strikethroughSpans.length > 0) {
                formattedText.setStrikethrough(true);
            }

            RelativeSizeSpan[] sizeSpans = spannableText.getSpans(start, end, RelativeSizeSpan.class);
            for (RelativeSizeSpan sizeSpan : sizeSpans) {
                formattedText.setRelativeSize(sizeSpan.getSizeChange());
            }

            // Heading levels
            if (formattedText.getRelativeSize() >= 2.0f) {
                formattedText.setHeadingLevel(1);
            } else if (formattedText.getRelativeSize() >= 1.5f) {
                formattedText.setHeadingLevel(2);
            } else {
                formattedText.setHeadingLevel(3);
            }

            // Alignment
            int alignment = etBlogContent.getGravity();
            formattedText.setAlignment(alignment);

            formattedTexts.add(formattedText);
        }

        Gson gson = new Gson();
        String json = gson.toJson(formattedTexts);

        SharedPreferences prefs = getSharedPreferences("BlogPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("blog_content", json);
        editor.apply();
    }

    private void restoreFormattedText() {
        SharedPreferences prefs = getSharedPreferences("BlogPrefs", MODE_PRIVATE);
        String json = prefs.getString("blog_content", "");

        Gson gson = new Gson();
        Type listType = new TypeToken<List<FormattedText>>() {}.getType();
        List<FormattedText> formattedTexts = gson.fromJson(json, listType);

        SpannableStringBuilder spannableText = new SpannableStringBuilder();

        for (FormattedText formattedText : formattedTexts) {
            Spannable spanText;

            if (formattedText.getImageBase64() != null) {
                // Restore image
                Bitmap bitmap = base64ToBitmap(formattedText.getImageBase64());
                Drawable drawable = new BitmapDrawable(getResources(), bitmap);
                drawable.setBounds(0, 0, 400, 400);
                ImageSpan imageSpan = new ImageSpan(drawable);

                spanText = new SpannableString(" ");
                spanText.setSpan(imageSpan, 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            } else {
                spanText = new SpannableString(formattedText.getText());

                // Apply formatting
                if (formattedText.isBold()) {
                    spanText.setSpan(new StyleSpan(Typeface.BOLD), 0, formattedText.getText().length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
                if (formattedText.isItalic()) {
                    spanText.setSpan(new StyleSpan(Typeface.ITALIC), 0, formattedText.getText().length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
                if (formattedText.isStrikethrough()) {
                    spanText.setSpan(new StrikethroughSpan(), 0, formattedText.getText().length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
                if (formattedText.getRelativeSize() > 1.0f) {
                    spanText.setSpan(new RelativeSizeSpan(formattedText.getRelativeSize()), 0, formattedText.getText().length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
                if (formattedText.getHeadingLevel() > 0) {
                    if (formattedText.getHeadingLevel() == 1) {
                        spanText.setSpan(new RelativeSizeSpan(2.0f), 0, formattedText.getText().length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    } else if (formattedText.getHeadingLevel() == 2) {
                        spanText.setSpan(new RelativeSizeSpan(1.6f), 0, formattedText.getText().length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }
                }
            }

            spannableText.append(spanText);
        }

        etBlogContent.setText(spannableText);

        int alignment = formattedTexts.get(0).getAlignment();
        etBlogContent.setGravity(alignment);
        etTitle.setText("done");
    }


}
