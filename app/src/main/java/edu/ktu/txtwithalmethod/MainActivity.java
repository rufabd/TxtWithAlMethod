package edu.ktu.txtwithalmethod;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class MainActivity extends AppCompatActivity {
    private Button saveBtn, loadBtn;
    private EditText input, load;
    String filename = "";
    String filepath = "";
    String fileContent = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        saveBtn = (Button) findViewById(R.id.saveBtn);
        loadBtn = (Button) findViewById(R.id.loadBtn);

        input = (EditText) findViewById(R.id.writeText);
        load = (EditText) findViewById(R.id.loadText);

        filename = "myFile.txt";
        filepath = "MyFileDir";

        if(!isExternalStorageAvailableForRW()) {
            saveBtn.setEnabled(false);
        }

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                load.setText("");
                fileContent = load.getText().toString().trim();

                // if file is not empty, then proceed
                if(!fileContent.equals("")) {
                    File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                    File myFile = new File(path, filename);
                    FileOutputStream fos = null;

                    try {
                        fos = new FileOutputStream(myFile);
                        fos.write(fileContent.getBytes());
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    load.setText("");
                    Toast.makeText(MainActivity.this, "Information Saved!", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(MainActivity.this, "Text field cannot be empty!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        loadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*FileReader fr = null;
                File myFile = new File(getExternalFilesDir(filepath), filename);
                StringBuilder stringBuilder = new StringBuilder();
                try {
                    fr = new FileReader(myFile);
                    BufferedReader br = new BufferedReader(fr);
                    String line = br.readLine();
                    while(line != null) {
                        stringBuilder.append(line).append('\n');
                        line = br.readLine();
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    String fileContents = "File Contents \n" + stringBuilder.toString();
                    load.setText(fileContents);
                }*/

                openFileDialog();

            }
        });
    }

    ActivityResultLauncher<Intent> sActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if(result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        Uri uri = data.getData();
                        byte[] bytes = getBytesFromUri(getApplicationContext(), uri);
                        load.setText(new String(bytes));
                    }
                }
            }
    );

    private void openFileDialog() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("*/*");
        intent = Intent.createChooser(intent, "Choose a file");
        sActivityResultLauncher.launch(intent);
    }

    byte[] getBytesFromUri (Context context, Uri uri) {
        InputStream iStream = null;
        try {
            iStream = context.getContentResolver().openInputStream(uri);
            ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();

            int bufferSize = 1024;
            byte[] buffer = new byte[bufferSize];
            int len = 0;
            while((len = iStream.read(buffer)) != -1) {
                byteBuffer.write(buffer, 0, len);
            }
            return byteBuffer.toByteArray();
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

//    private void createAndSaveFile() {
//        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
//        intent.addCategory(Intent.CATEGORY_OPENABLE);
//        intent.setType("text/plain");
//        intent.putExtra(Intent.EXTRA_TITLE, "testFile.txt");
//
//        startActivityForResult(intent, 1);
//    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        if(requestCode == 1) {
//            if(resultCode == RESULT_OK) {
//                Uri uri = data.getData();
//
//                OutputStream outputStream = getContentResolver().openOutputStream(uri);
//                try {
//                    outputStream.write("HIIIIII".getBytes());
//                    outputStream.close();
//
//                    Toast.makeText(this, "SUCCESS!", Toast.LENGTH_SHORT).show();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                    Toast.makeText(this, "FAILED!", Toast.LENGTH_SHORT).show();
//                }
//
//            }
//        }
//    }

    private boolean isExternalStorageAvailableForRW() {
        String extStorage = Environment.getExternalStorageState();

        // if can read and write to media
        if(extStorage.equals(Environment.MEDIA_MOUNTED)) {
            return true;
        }
        return false;
    }
}