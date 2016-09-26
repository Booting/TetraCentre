package tetra.centre;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import tetra.centre.SupportClass.Config;
import tetra.centre.SupportClass.FontCache;
import tetra.centre.SupportClass.SimpleFileDialog;
import tetra.centre.SupportClass.TypeFaceSpan;
import tetra.centre.SupportClass.callURL;

public class EduPaperAddActivity extends AppCompatActivity {
    private Typeface fontLatoBold, fontLatoRegular, fontLatoHeavy, fontLatoBlack, fontLatoItalic;
    private Toolbar toolbar;
    private TextView lblUploadFile, lblUploadFileKet, txtUploadFile;
    private EditText txtName, txtDescription;
    private Button btnSend;
    private RequestQueue queue;
    private ProgressDialog pDialog;
    private SharedPreferences appsPref;
    private String url = "";
    private HttpEntity resEntity;
    private String imagepathOne=null;
    private String pathOne="";
    private String m_chosen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
        setContentView(R.layout.activity_edupaper_add);

        initToolbar();

        appsPref 	     = getSharedPreferences(Config.PREF_NAME, Activity.MODE_PRIVATE);
        queue    	     = Volley.newRequestQueue(this);
        fontLatoBold     = FontCache.get(EduPaperAddActivity.this, "Lato-Bold");
        fontLatoRegular  = FontCache.get(EduPaperAddActivity.this, "Lato-Regular");
        fontLatoHeavy    = FontCache.get(EduPaperAddActivity.this, "Lato-Heavy");
        fontLatoBlack    = FontCache.get(EduPaperAddActivity.this, "Lato-Black");
        fontLatoItalic   = FontCache.get(EduPaperAddActivity.this, "Lato-Italic");
        lblUploadFile    = (TextView) findViewById(R.id.lblUploadFile);
        lblUploadFileKet = (TextView) findViewById(R.id.lblUploadFileKet);
        txtName          = (EditText) findViewById(R.id.txtName);
        txtDescription   = (EditText) findViewById(R.id.txtDescription);
        btnSend          = (Button) findViewById(R.id.btnSend);
        txtUploadFile    = (TextView) findViewById(R.id.txtUploadFile);

        lblUploadFile.setTypeface(fontLatoRegular);
        lblUploadFileKet.setTypeface(fontLatoRegular);
        txtName.setTypeface(fontLatoRegular);
        txtDescription.setTypeface(fontLatoRegular);
        btnSend.setTypeface(fontLatoHeavy);
        txtUploadFile.setTypeface(fontLatoRegular);

        lblUploadFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /////////////////////////////////////////////////////////////////////////////////////////////////
                //Create FileOpenDialog and register a callback
                /////////////////////////////////////////////////////////////////////////////////////////////////
                SimpleFileDialog FileOpenDialog =  new SimpleFileDialog(EduPaperAddActivity.this, "FileOpen",
                        new SimpleFileDialog.SimpleFileDialogListener() {
                            @Override
                            public void onChosenDir(String chosenDir) {
                                File file = new File(chosenDir);
                                if (file.getName().toLowerCase().endsWith("pdf")) {
                                    // The code in this function will be executed when the dialog OK button is pushed
                                    m_chosen = chosenDir;
                                    txtUploadFile.setVisibility(View.VISIBLE);
                                    txtUploadFile.setText(m_chosen);
                                } else {
                                    Toast.makeText(getApplicationContext(), "Format file tidak sesuai", Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                //You can change the default filename using the public variable "Default_File_Name"
                FileOpenDialog.Default_File_Name = "";
                FileOpenDialog.chooseFile_or_Dir();
                /////////////////////////////////////////////////////////////////////////////////////////////////
            }
        });

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pathOne==null) {
                    Toast.makeText(getApplicationContext(), "Paper is required!", Toast.LENGTH_SHORT).show();
                } else if (txtName.getText().length()==0) {
                    txtName.setError("Name is required!");
                } else if (txtDescription.getText().length()==0) {
                    txtDescription.setError("Description is required!");
                } else {
                    new Submit().execute();
                }
            }
        });

        pDialog = new ProgressDialog(EduPaperAddActivity.this);
        pDialog.setMessage("Working...");
        pDialog.setCancelable(false);
    }

    private void initToolbar() {
        SpannableString spanToolbar = new SpannableString("EduPaper - Add");
        spanToolbar.setSpan(new TypeFaceSpan(EduPaperAddActivity.this, "Lato-Bold"), 0, spanToolbar.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        //Initiate Toolbar/ActionBar
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(spanToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(false);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private class Submit extends AsyncTask<HttpEntity, Void, HttpEntity> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog.show();
        }
        @Override
        protected HttpEntity doInBackground(HttpEntity... params) {
            return doFileUploadMedia();
        }
        @Override
        protected void onPostExecute(HttpEntity result) {
            super.onPostExecute(result);
            if (result != null) {
                new addNewMedia().execute();
            } else {
                pDialog.dismiss();
            }
        }
    }

    private class addNewMedia extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog.show();
        }
        @Override
        protected String doInBackground(String... params) {
            String strName        = null;
            String strDescription = null;
            try {
                strName        = URLEncoder.encode(txtName.getText().toString().replace("\"", "'"), "utf-8");
                strDescription = URLEncoder.encode(txtDescription.getText().toString().replace("\"", "'"), "utf-8");
            } catch (UnsupportedEncodingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            url = Config.URL + "/service.php?ct=ADDEDUPAPER&UserId=" + appsPref.getString("UserId", "") +
                    "&Title=" + strName +
                    "&Description=" + strDescription +
                    "&Url=" + pathOne;

            String hasil = callURL.call(url);
            return hasil;
        }
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Toast.makeText(getApplicationContext(), "Add new EduPaper succesfully!", Toast.LENGTH_LONG).show();
            setResult(RESULT_OK);
            finish();
        }
    }

    private HttpEntity doFileUploadMedia() {
        try {
            HttpClient client = new DefaultHttpClient();
            HttpPost post = null;
            File file1    = null;
            FileBody bin1 = null;

            MultipartEntity reqEntity = new MultipartEntity();
            post    = new HttpPost("http://tetracentre.com/tetracenter_ws/UploadMaterial.php");
            file1   = new File(m_chosen);
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

    @Override
    public void onPause() {
        super.onPause();
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_menu:
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                break;
        }
        return true;
    }
}
