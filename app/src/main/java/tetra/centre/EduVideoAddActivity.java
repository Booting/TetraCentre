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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import tetra.centre.SupportClass.Config;
import tetra.centre.SupportClass.FontCache;
import tetra.centre.SupportClass.TypeFaceSpan;
import tetra.centre.SupportClass.callURL;

public class EduVideoAddActivity extends AppCompatActivity {
    private Typeface fontLatoBold, fontLatoRegular, fontLatoHeavy, fontLatoBlack, fontLatoItalic;
    private Toolbar toolbar;
    private EditText txtName, txtUrl;
    private Button btnSend;
    private ProgressDialog pDialog;
    private SharedPreferences appsPref;
    private String url = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
        setContentView(R.layout.activity_eduvideo_add);

        initToolbar();

        appsPref 	    = getSharedPreferences(Config.PREF_NAME, Activity.MODE_PRIVATE);
        fontLatoBold    = FontCache.get(EduVideoAddActivity.this, "Lato-Bold");
        fontLatoRegular = FontCache.get(EduVideoAddActivity.this, "Lato-Regular");
        fontLatoHeavy   = FontCache.get(EduVideoAddActivity.this, "Lato-Heavy");
        fontLatoBlack   = FontCache.get(EduVideoAddActivity.this, "Lato-Black");
        fontLatoItalic  = FontCache.get(EduVideoAddActivity.this, "Lato-Italic");
        txtName         = (EditText) findViewById(R.id.txtName);
        txtUrl          = (EditText) findViewById(R.id.txtUrl);
        btnSend         = (Button) findViewById(R.id.btnSend);

        txtName.setTypeface(fontLatoRegular);
        txtUrl.setTypeface(fontLatoRegular);
        btnSend.setTypeface(fontLatoBold);

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (txtName.getText().length()==0) {
                    txtName.setError("Title is required!");
                } else if (txtUrl.getText().length()==0) {
                    txtUrl.setError("Url is required!");
                } else {
                    new addNewEduVideo().execute();
                }
            }
        });

        pDialog = new ProgressDialog(EduVideoAddActivity.this);
        pDialog.setMessage("Working...");
        pDialog.setCancelable(false);
    }

    private void initToolbar() {
        SpannableString spanToolbar = new SpannableString("EduVideo - Add");
        spanToolbar.setSpan(new TypeFaceSpan(EduVideoAddActivity.this, "Lato-Bold"), 0, spanToolbar.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

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

    private class addNewEduVideo extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog.show();
        }
        @Override
        protected String doInBackground(String... params) {
            String strName = null;
            String strUrl  = null;
            try {
                strName = URLEncoder.encode(txtName.getText().toString().replace("\"", "'"), "utf-8");
                strUrl  = URLEncoder.encode(txtUrl.getText().toString().replace("\"", "'"), "utf-8");
            } catch (UnsupportedEncodingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            url = Config.URL + "/service.php?ct=ADDEDUVIDEO&UserId=" + appsPref.getString("UserId", "") +
                    "&Title=" + strName +
                    "&Url=" + strUrl;

            String hasil = callURL.call(url);
            return hasil;
        }
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Toast.makeText(getApplicationContext(), "Add new EduVideo succesfully!", Toast.LENGTH_LONG).show();
            setResult(RESULT_OK);
            finish();
        }
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
