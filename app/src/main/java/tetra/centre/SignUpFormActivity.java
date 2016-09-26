package tetra.centre;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import tetra.centre.SupportClass.Config;
import tetra.centre.SupportClass.FontCache;
import tetra.centre.SupportClass.ImageFilePath;
import tetra.centre.SupportClass.SimpleFileDialog;
import tetra.centre.SupportClass.callURL;

public class SignUpFormActivity extends Activity {
    private Typeface fontLatoBold, fontLatoRegular, fontLatoHeavy, fontLatoBlack, fontLatoItalic;
    private TextView lblPhoto, lblEduForm, lblEduspirator, lblTitleOfMaterial;
    private ImageView imgPhoto;
    private EditText txtName, txtDateOfBirth, txtAddress, txtSchool, txtPhoneNumber, txtEmail,
            txtTitleOfMaterial, txtUsername, txtPassword;
    private Button btnUploadOfMaterial, btnSubmit, btnLogout;
    private LinearLayout linTitleOfMaterial, linUsernamePassword;
    private View viewTitleOfMaterial;
    private String url = "";
    private HttpEntity resEntity;
    private String imagepathOne=null, imagepathTwo=null;
    private String pathOne="", pathTwo="";
    private ProgressBar progressBar;
    private int intUserCategory;
    private int year, month, day;
    private static final int TAKE_PHOTO = 1;
    private static final int TAKE_PHOTO_GALLERY = 11;
    private static final int PICKFILE_RESULT_CODE = 2;
    private String m_chosen;
    private RequestQueue queue;
    private ProgressDialog pDialog;
    private SharedPreferences appsPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
        setContentView(R.layout.activity_sign_up_form);

        appsPref 	        = getSharedPreferences(Config.PREF_NAME, Activity.MODE_PRIVATE);
        queue    	        = Volley.newRequestQueue(this);
        fontLatoBold        = FontCache.get(SignUpFormActivity.this, "Lato-Bold");
        fontLatoRegular     = FontCache.get(SignUpFormActivity.this, "Lato-Regular");
        fontLatoHeavy       = FontCache.get(SignUpFormActivity.this, "Lato-Heavy");
        fontLatoBlack       = FontCache.get(SignUpFormActivity.this, "Lato-Black");
        fontLatoItalic      = FontCache.get(SignUpFormActivity.this, "Lato-Italic");
        lblPhoto            = (TextView) findViewById(R.id.lblPhoto);
        imgPhoto            = (ImageView) findViewById(R.id.imgPhoto);
        lblEduForm          = (TextView) findViewById(R.id.lblEduForm);
        lblEduspirator      = (TextView) findViewById(R.id.lblEduspirator);
        txtName             = (EditText) findViewById(R.id.txtName);
        txtDateOfBirth      = (EditText) findViewById(R.id.txtDateOfBirth);
        txtAddress          = (EditText) findViewById(R.id.txtAddress);
        txtSchool           = (EditText) findViewById(R.id.txtSchool);
        txtPhoneNumber      = (EditText) findViewById(R.id.txtPhoneNumber);
        txtEmail            = (EditText) findViewById(R.id.txtEmail);
        txtTitleOfMaterial  = (EditText) findViewById(R.id.txtTitleOfMaterial);
        btnUploadOfMaterial = (Button) findViewById(R.id.btnUploadOfMaterial);
        btnSubmit           = (Button) findViewById(R.id.btnSubmit);
        linTitleOfMaterial  = (LinearLayout) findViewById(R.id.linTitleOfMaterial);
        viewTitleOfMaterial = (View) findViewById(R.id.viewTitleOfMaterial);
        progressBar         = (ProgressBar) findViewById(R.id.progressBar);
        txtUsername         = (EditText) findViewById(R.id.txtUsername);
        txtPassword         = (EditText) findViewById(R.id.txtPassword);
        lblTitleOfMaterial  = (TextView) findViewById(R.id.lblTitleOfMaterial);
        linUsernamePassword = (LinearLayout) findViewById(R.id.linUsernamePassword);
        btnLogout           = (Button) findViewById(R.id.btnLogout);

        lblPhoto.setTypeface(fontLatoRegular);
        lblEduForm.setTypeface(fontLatoBold);
        lblEduspirator.setTypeface(fontLatoRegular);
        txtUsername.setTypeface(fontLatoRegular);
        txtPassword.setTypeface(fontLatoRegular);
        txtName.setTypeface(fontLatoRegular);
        txtDateOfBirth.setTypeface(fontLatoRegular);
        txtAddress.setTypeface(fontLatoRegular);
        txtSchool.setTypeface(fontLatoRegular);
        txtPhoneNumber.setTypeface(fontLatoRegular);
        txtEmail.setTypeface(fontLatoRegular);
        txtTitleOfMaterial.setTypeface(fontLatoRegular);
        btnUploadOfMaterial.setTypeface(fontLatoRegular);
        btnSubmit.setTypeface(fontLatoHeavy);
        lblTitleOfMaterial.setTypeface(fontLatoRegular);
        btnLogout.setTypeface(fontLatoHeavy);

        if (getIntent().getStringExtra("Category").equalsIgnoreCase("Eduspirator")) {
            intUserCategory = 1;
            lblEduspirator.setText("EDUSPIRATOR");
            btnUploadOfMaterial.setVisibility(View.VISIBLE);
            linTitleOfMaterial.setVisibility(View.VISIBLE);
            viewTitleOfMaterial.setVisibility(View.VISIBLE);
        } else {
            intUserCategory = 2;
            lblEduspirator.setText("PARTICIPANTS");
            btnUploadOfMaterial.setVisibility(View.GONE);
            linTitleOfMaterial.setVisibility(View.GONE);
            viewTitleOfMaterial.setVisibility(View.GONE);
        }

        final Calendar c = Calendar.getInstance();
        year  = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day   = c.get(Calendar.DAY_OF_MONTH);

        txtDateOfBirth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                showDialog(999);
            }
        });

        imgPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent(TAKE_PHOTO);
            }
        });

        btnUploadOfMaterial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /////////////////////////////////////////////////////////////////////////////////////////////////
                //Create FileOpenDialog and register a callback
                /////////////////////////////////////////////////////////////////////////////////////////////////
                SimpleFileDialog FileOpenDialog =  new SimpleFileDialog(SignUpFormActivity.this, "FileOpen",
                        new SimpleFileDialog.SimpleFileDialogListener() {
                            @Override
                            public void onChosenDir(String chosenDir) {
                                // The code in this function will be executed when the dialog OK button is pushed
                                m_chosen = chosenDir;
                                lblTitleOfMaterial.setVisibility(View.VISIBLE);
                                lblTitleOfMaterial.setText(m_chosen);
                            }
                        });
                //You can change the default filename using the public variable "Default_File_Name"
                FileOpenDialog.Default_File_Name = "";
                FileOpenDialog.chooseFile_or_Dir();
                /////////////////////////////////////////////////////////////////////////////////////////////////
            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getIntent().getStringExtra("Category").equalsIgnoreCase("Eduspirator")) {
                    if (txtUsername.getText().length()==0) {
                        txtUsername.setError("Username is required!");
                    } else if (txtPassword.getText().length()==0) {
                        txtPassword.setError("Password is required!");
                    } else if (txtName.getText().length()==0) {
                        txtName.setError("Name is Required!");
                    } else if (txtDateOfBirth.getText().length()==0) {
                        txtDateOfBirth.setError("Date of Birth is required!");
                    } else if (txtAddress.getText().length()==0) {
                        txtAddress.setError("Address is required!");
                    } else if (txtSchool.getText().length()==0) {
                        txtSchool.setError("School is required!");
                    } else if (txtPhoneNumber.getText().length()==0) {
                        txtPhoneNumber.setError("Phone Number is required!");
                    } else if (txtEmail.getText().length()==0) {
                        txtEmail.setError("Email is required!");
                    } else if (txtTitleOfMaterial.getText().length()==0) {
                        txtTitleOfMaterial.setError("Title of Material is required!");
                    } else if (btnSubmit.getText().toString().equalsIgnoreCase("UPDATE")) {
                        if (imagepathOne==null && m_chosen==null) {
                            new UpdateUser().execute();
                        } else if (imagepathOne!=null && m_chosen!=null) {
                            new SubmitEduspirator().execute();
                        } else if (imagepathOne==null && m_chosen!=null) {
                            new SubmitTwo().execute();
                        } else if (imagepathOne!=null && m_chosen==null) {
                            new SubmitParticipants().execute();
                        }
                    } else if (imagepathOne==null) {
                        Toast.makeText(getApplicationContext(), "Photo is required!", Toast.LENGTH_SHORT).show();
                    } else if (m_chosen==null) {
                        Toast.makeText(getApplicationContext(), "Material is required!", Toast.LENGTH_SHORT).show();
                    } else {
                        new SubmitEduspirator().execute();
                    }
                } else {
                    if (txtUsername.getText().length()==0) {
                        txtUsername.setError("Username is required!");
                    } else if (txtPassword.getText().length()==0) {
                        txtPassword.setError("Password is required!");
                    } else if (txtName.getText().length()==0) {
                        txtName.setError("Name is Required!");
                    } else if (txtDateOfBirth.getText().length()==0) {
                        txtDateOfBirth.setError("Date of Birth is required!");
                    } else if (txtAddress.getText().length()==0) {
                        txtAddress.setError("Address is required!");
                    } else if (txtSchool.getText().length()==0) {
                        txtSchool.setError("School is required!");
                    } else if (txtPhoneNumber.getText().length()==0) {
                        txtPhoneNumber.setError("Phone Number is required!");
                    } else if (txtEmail.getText().length()==0) {
                        txtEmail.setError("Email is required!");
                    } else if (btnSubmit.getText().toString().equalsIgnoreCase("UPDATE")) {
                        if (imagepathOne==null) {
                            new UpdateUser().execute();
                        } else {
                            new SubmitParticipants().execute();
                        }
                    } else if (imagepathOne==null) {
                        Toast.makeText(getApplicationContext(), "Photo is required!", Toast.LENGTH_SHORT).show();
                    } else {
                        new SubmitParticipants().execute();
                    }
                }
            }
        });

        pDialog = new ProgressDialog(SignUpFormActivity.this);
        pDialog.setMessage("Working...");
        pDialog.setCancelable(false);

        if (getIntent().getStringExtra("IsProfile").equalsIgnoreCase("true")) {
            linUsernamePassword.setVisibility(View.VISIBLE);
            btnLogout.setVisibility(View.VISIBLE);
            btnSubmit.setText("UPDATE");
            getProfile();
        } else {
            linUsernamePassword.setVisibility(View.VISIBLE);
            btnLogout.setVisibility(View.GONE);
            btnSubmit.setText("SUBMIT");
        }

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = appsPref.edit();
                editor.putString("UserId", "");
                editor.putString("UserCategory", "");
                editor.putString("Name", "");
                editor.putString("PhoneNumber", "");
                editor.putString("Email", "");
                editor.putString("Photo", "");
                editor.commit();

                Intent intent = new Intent(getApplicationContext(), SignInActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
    }

    public void dispatchTakePictureIntent(final int intPosition) {
        final CharSequence[] items = { "Take Photo", "Choose from Library" };
        AlertDialog.Builder builder = new AlertDialog.Builder(SignUpFormActivity.this);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("Take Photo")) {
                    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    // Ensure that there's a camera activity to handle the intent
                    if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                        // Create the File where the photo should go
                        File photoFile = null;
                        try {
                            photoFile = createImageFile();
                        } catch (IOException ex) {
                            // Error occurred while creating the File
                        }
                        // Continue only if the File was successfully created
                        if (photoFile != null) {
                            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                            startActivityForResult(takePictureIntent, intPosition);
                        }
                    }
                } else if (items[item].equals("Choose from Library")) {
                    Intent intent = new Intent();
                    intent.setType("*/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    if (intPosition==TAKE_PHOTO) {
                        startActivityForResult(Intent.createChooser(intent, "Select File"), TAKE_PHOTO_GALLERY);
                    }
                }
            }
        });
        builder.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == TAKE_PHOTO && resultCode == RESULT_OK) {
            Glide.with(SignUpFormActivity.this).load(imagepathOne).asBitmap().into(imgPhoto);
        } else if (requestCode == TAKE_PHOTO_GALLERY && resultCode == RESULT_OK) {
            Uri selectedImageUri = data.getData();
            imagepathOne = ImageFilePath.getPath(getApplicationContext(), selectedImageUri);
            Glide.with(SignUpFormActivity.this).load(imagepathOne).asBitmap().into(imgPhoto);
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        imagepathOne = image.getAbsolutePath();
        return image;
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        // TODO Auto-generated method stub
        if (id == 999) {
            return new DatePickerDialog(this, myDateListener, year, month, day);
        }
        return null;
    }

    private DatePickerDialog.OnDateSetListener myDateListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker arg0, int arg1, int arg2, int arg3) {
            // TODO Auto-generated method stub
            txtDateOfBirth.setText(new StringBuilder().append(arg3).append(" ").append(formatMonth(arg2 + 1)).append(" ").append(arg1));
        }
    };

    public String formatMonth(int month) {
        DateFormat formatter = new SimpleDateFormat("MMMM", Locale.US);
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.MONTH, month-1);
        return formatter.format(calendar.getTime());
    }

    private class SubmitEduspirator extends AsyncTask<HttpEntity, Void, HttpEntity> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
        }
        @Override
        protected HttpEntity doInBackground(HttpEntity... params) {
            return doFileUploadPhotos();
        }
        @Override
        protected void onPostExecute(HttpEntity result) {
            super.onPostExecute(result);
            if (result != null) {
                new SubmitTwo().execute();
            } else {
                progressBar.setVisibility(View.GONE);
            }
        }
    }

    private class SubmitParticipants extends AsyncTask<HttpEntity, Void, HttpEntity> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
        }
        @Override
        protected HttpEntity doInBackground(HttpEntity... params) {
            return doFileUploadPhotos();
        }
        @Override
        protected void onPostExecute(HttpEntity result) {
            super.onPostExecute(result);
            if (result != null) {
                if (btnSubmit.getText().toString().equalsIgnoreCase("SUBMIT")) {
                    new addNewData().execute();
                } else {
                    new updateData().execute();
                }
            } else {
                progressBar.setVisibility(View.GONE);
            }
        }
    }

    private class UpdateUser extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
        }
        @Override
        protected String doInBackground(String... params) {
            String strUsername        = null;
            String strName            = null;
            String strDateOfBirth     = null;
            String strAddress         = null;
            String strSchool          = null;
            String strEmail           = null;
            String strTitleOfMaterial = null;
            try {
                strUsername        = URLEncoder.encode(txtUsername.getText().toString().replace("\"", "'"), "utf-8");
                strName            = URLEncoder.encode(txtName.getText().toString().replace("\"", "'"), "utf-8");
                strDateOfBirth     = URLEncoder.encode(txtDateOfBirth.getText().toString().replace("\"", "'"), "utf-8");
                strAddress         = URLEncoder.encode(txtAddress.getText().toString().replace("\"", "'"), "utf-8");
                strSchool          = URLEncoder.encode(txtSchool.getText().toString().replace("\"", "'"), "utf-8");
                strEmail           = URLEncoder.encode(txtEmail.getText().toString().replace("\"", "'"), "utf-8");
                strTitleOfMaterial = URLEncoder.encode(txtTitleOfMaterial.getText().toString().replace("\"", "'"), "utf-8");
            } catch (UnsupportedEncodingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            url = Config.URL + "/service.php?ct=UPDATEUSER&UserCategory=" + intUserCategory +
                    "&Username=" + strUsername +
                    "&Password=" + txtPassword.getText().toString() +
                    "&Name=" + strName +
                    "&DateOfBirth=" + strDateOfBirth +
                    "&Address=" + strAddress +
                    "&School=" + strSchool +
                    "&PhoneNumber=" + txtPhoneNumber.getText().toString() +
                    "&Email=" + strEmail +
                    "&TitleOfMaterial=" + strTitleOfMaterial +
                    "&Material=" + pathTwo +
                    "&Photo=" + pathOne +
                    "&UserId=" + appsPref.getString("UserId", "");

            System.out.println("UpdateUser : "+url);
            String hasil = callURL.call(url);
            return hasil;
        }
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            progressBar.setVisibility(View.GONE);
            Toast.makeText(getApplicationContext(), "Update profile succesfully!", Toast.LENGTH_LONG).show();
            getProfile();
        }
    }

    private class SubmitTwo extends AsyncTask<HttpEntity, Void, HttpEntity> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
        }
        @Override
        protected HttpEntity doInBackground(HttpEntity... params) {
            return doFileUploadMaterials();
        }
        @Override
        protected void onPostExecute(HttpEntity result) {
            super.onPostExecute(result);
            if (result != null) {
                if (btnSubmit.getText().toString().equalsIgnoreCase("SUBMIT")) {
                    new addNewData().execute();
                } else {
                    new updateData().execute();
                }
            } else {
                progressBar.setVisibility(View.GONE);
            }
        }
    }

    private HttpEntity doFileUploadPhotos() {
        try {
            HttpClient client = new DefaultHttpClient();
            HttpPost post = null;
            File file1    = null;
            FileBody bin1 = null;

            MultipartEntity reqEntity = new MultipartEntity();
            post    = new HttpPost("http://tetracentre.com/tetracenter_ws/UploadPhoto.php");
            file1   = new File(imagepathOne);
            pathOne = file1.getName();

            bin1 = new FileBody(file1);
            reqEntity.addPart("uploadedfile1", bin1);

            reqEntity.addPart("user", new StringBody("User"));
            post.setEntity(reqEntity);

            HttpResponse response = client.execute(post);
            resEntity = response.getEntity();
            @SuppressWarnings("unused")
            final String response_str = EntityUtils.toString(resEntity);
        } catch (Exception ex) {
            Log.e("Debug", "error: " + ex.getMessage(), ex);
        }

        return resEntity;
    }

    private HttpEntity doFileUploadMaterials() {
        try {
            HttpClient client = new DefaultHttpClient();
            HttpPost post = null;
            File file1    = null;
            FileBody bin1 = null;

            MultipartEntity reqEntity = new MultipartEntity();
            post    = new HttpPost("http://tetracentre.com/tetracenter_ws/UploadMaterial.php");
            file1   = new File(m_chosen);
            pathTwo = file1.getName();

            bin1 = new FileBody(file1);
            reqEntity.addPart("uploadedfile1", bin1);

            reqEntity.addPart("user", new StringBody("User"));
            post.setEntity(reqEntity);

            HttpResponse response = client.execute(post);
            resEntity = response.getEntity();
            @SuppressWarnings("unused")
            final String response_str = EntityUtils.toString(resEntity);
        } catch (Exception ex) {
            Log.e("Debug", "error: " + ex.getMessage(), ex);
        }

        return resEntity;
    }

    private class addNewData extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
        }
        @Override
        protected String doInBackground(String... params) {
            String strUsername        = null;
            String strName            = null;
            String strDateOfBirth     = null;
            String strAddress         = null;
            String strSchool          = null;
            String strEmail           = null;
            String strTitleOfMaterial = null;
            try {
                strUsername        = URLEncoder.encode(txtUsername.getText().toString().replace("\"", "'"), "utf-8");
                strName            = URLEncoder.encode(txtName.getText().toString().replace("\"", "'"), "utf-8");
                strDateOfBirth     = URLEncoder.encode(txtDateOfBirth.getText().toString().replace("\"", "'"), "utf-8");
                strAddress         = URLEncoder.encode(txtAddress.getText().toString().replace("\"", "'"), "utf-8");
                strSchool          = URLEncoder.encode(txtSchool.getText().toString().replace("\"", "'"), "utf-8");
                strEmail           = URLEncoder.encode(txtEmail.getText().toString().replace("\"", "'"), "utf-8");
                strTitleOfMaterial = URLEncoder.encode(txtTitleOfMaterial.getText().toString().replace("\"", "'"), "utf-8");
            } catch (UnsupportedEncodingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            url = Config.URL + "/service.php?ct=REGISTER&UserCategory=" + intUserCategory +
                    "&Username=" + strUsername +
                    "&Password=" + txtPassword.getText().toString() +
                    "&Name=" + strName +
                    "&DateOfBirth=" + strDateOfBirth +
                    "&Address=" + strAddress +
                    "&School=" + strSchool +
                    "&PhoneNumber=" + txtPhoneNumber.getText().toString() +
                    "&Email=" + strEmail +
                    "&TitleOfMaterial=" + strTitleOfMaterial +
                    "&Material=" + pathTwo +
                    "&Photo=" + pathOne;

            System.out.println("addNewData : "+url);
            String hasil = callURL.call(url);
            return hasil;
        }
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Toast.makeText(getApplicationContext(), "Register succesfully!", Toast.LENGTH_LONG).show();

            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    progressBar.setVisibility(View.GONE);
                    getLogin();
                }
            }, 2000);
        }
    }

    private class updateData extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
        }
        @Override
        protected String doInBackground(String... params) {
            String strUsername        = null;
            String strName            = null;
            String strDateOfBirth     = null;
            String strAddress         = null;
            String strSchool          = null;
            String strEmail           = null;
            String strTitleOfMaterial = null;
            try {
                strUsername        = URLEncoder.encode(txtUsername.getText().toString().replace("\"", "'"), "utf-8");
                strName            = URLEncoder.encode(txtName.getText().toString().replace("\"", "'"), "utf-8");
                strDateOfBirth     = URLEncoder.encode(txtDateOfBirth.getText().toString().replace("\"", "'"), "utf-8");
                strAddress         = URLEncoder.encode(txtAddress.getText().toString().replace("\"", "'"), "utf-8");
                strSchool          = URLEncoder.encode(txtSchool.getText().toString().replace("\"", "'"), "utf-8");
                strEmail           = URLEncoder.encode(txtEmail.getText().toString().replace("\"", "'"), "utf-8");
                strTitleOfMaterial = URLEncoder.encode(txtTitleOfMaterial.getText().toString().replace("\"", "'"), "utf-8");
            } catch (UnsupportedEncodingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            url = Config.URL + "/service.php?ct=UPDATEUSER&UserCategory=" + intUserCategory +
                    "&Username=" + strUsername +
                    "&Password=" + txtPassword.getText().toString() +
                    "&Name=" + strName +
                    "&DateOfBirth=" + strDateOfBirth +
                    "&Address=" + strAddress +
                    "&School=" + strSchool +
                    "&PhoneNumber=" + txtPhoneNumber.getText().toString() +
                    "&Email=" + strEmail +
                    "&TitleOfMaterial=" + strTitleOfMaterial +
                    "&Material=" + pathTwo +
                    "&Photo=" + pathOne +
                    "&UserId=" + appsPref.getString("UserId", "");

            System.out.println("updateData : "+url);
            String hasil = callURL.call(url);
            return hasil;
        }
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Toast.makeText(getApplicationContext(), "Update profile succesfully!", Toast.LENGTH_LONG).show();
            progressBar.setVisibility(View.GONE);
            getProfile();
        }
    }

    public void getLogin() {
        pDialog.show();
        String url = Config.URL+"/login.php?Username="+txtUsername.getText().toString()+"&Password="+txtPassword.getText().toString();
        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray jArrLogin  = response.getJSONArray("statuslogin");

                    for (int i = 0; i<jArrLogin.length(); i++){
                        JSONObject jObjLogin = jArrLogin.getJSONObject(i);

                        if (jObjLogin.getString("st").equalsIgnoreCase("ok")) {
                            SharedPreferences.Editor editor = appsPref.edit();
                            editor.putString("UserId", jObjLogin.optString("UserId"));
                            editor.putString("UserCategory", jObjLogin.optString("UserCategory"));
                            editor.putString("Name", jObjLogin.optString("Name"));
                            editor.putString("PhoneNumber", jObjLogin.optString("PhoneNumber"));
                            editor.putString("Email", jObjLogin.optString("Email"));
                            editor.putString("Photo", jObjLogin.optString("Photo"));
                            editor.commit();

                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        }
                        Toast.makeText(getApplicationContext(), jObjLogin.getString("hasil"), Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                pDialog.dismiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                pDialog.dismiss();
                Toast.makeText(getBaseContext(), error.toString(), Toast.LENGTH_SHORT).show();
            }
        });
        queue.add(jsObjRequest);
    }

    public void getProfile() {
        pDialog.show();
        String url = Config.URL + "getProfile.php?UserId=" + appsPref.getString("UserId", "");
        queue.getCache().clear();
        queue.getCache().remove(url);
        JsonArrayRequest jsArrRequest = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                System.out.println("Response (getProfile) : "+response);
                try {
                    JSONObject jObjResponse = response.getJSONObject(0);
                    txtUsername.setText(jObjResponse.getString("Username"));
                    txtPassword.setText(jObjResponse.getString("Password"));
                    txtName.setText(jObjResponse.getString("Name"));
                    txtDateOfBirth.setText(jObjResponse.getString("DateOfBirth"));
                    txtAddress.setText(jObjResponse.getString("Address"));
                    txtSchool.setText(jObjResponse.getString("School"));
                    txtPhoneNumber.setText(jObjResponse.getString("PhoneNumber"));
                    txtEmail.setText(jObjResponse.getString("Email"));
                    txtTitleOfMaterial.setText(jObjResponse.getString("TitleOfMaterial"));
                    lblTitleOfMaterial.setText(jObjResponse.getString("Material"));
                    if (!jObjResponse.getString("Material").equalsIgnoreCase("")) {
                        lblTitleOfMaterial.setVisibility(View.VISIBLE);
                    } else {
                        lblTitleOfMaterial.setVisibility(View.GONE);
                    }
                    Glide.with(SignUpFormActivity.this).load(Config.URL_PICTURES + jObjResponse.getString("Photo"))
                            .placeholder(R.drawable.placeholder).centerCrop().diskCacheStrategy(DiskCacheStrategy.ALL).override(500, 500)
                            .dontAnimate().into(imgPhoto);

                    pathOne = jObjResponse.getString("Photo");
                    pathTwo = jObjResponse.getString("Material");
                    SimpleDateFormat parser = new SimpleDateFormat("dd MMMM yyyy");
                    Date yourDate = null;
                    try {
                        yourDate = parser.parse(jObjResponse.getString("DateOfBirth"));
                        Calendar c = Calendar.getInstance();
                        c.setTime(yourDate);
                        year  = c.get(Calendar.YEAR);
                        month = c.get(Calendar.MONTH);
                        day   = c.get(Calendar.DAY_OF_MONTH);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                pDialog.dismiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                pDialog.dismiss();
                Toast.makeText(getBaseContext(), error.toString(), Toast.LENGTH_SHORT).show();
            }
        });
        queue.add(jsArrRequest);
    }

    @Override
    public void onPause() {
        super.onPause();
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
    }
}
