package tetra.centre;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Locale;
import tetra.centre.Adapter.EduMediaListRecyclerAdapter;
import tetra.centre.Model.Media;
import tetra.centre.SupportClass.Config;
import tetra.centre.SupportClass.FontCache;
import tetra.centre.SupportClass.TypeFaceSpan;
import tetra.centre.SupportClass.callURL;

public class EduMediaListActivity extends AppCompatActivity implements EduMediaListRecyclerAdapter.EduMediaListRecyclerAdapterListener {
    private Typeface fontLatoBold, fontLatoRegular, fontLatoHeavy, fontLatoBlack, fontLatoItalic;
    private Toolbar toolbar;
    private RecyclerView rcEduMediaList;
    private FloatingActionButton fabAdd;
    private EduMediaListRecyclerAdapter eduMediaListRecyclerAdapter;
    private RequestQueue queue;
    private ProgressDialog pDialog;
    private SharedPreferences appsPref;
    private EditText txtFilter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
        setContentView(R.layout.activity_edumedia_list);

        initToolbar();

        appsPref 	    = getSharedPreferences(Config.PREF_NAME, Activity.MODE_PRIVATE);
        queue    	    = Volley.newRequestQueue(this);
        fontLatoBold    = FontCache.get(EduMediaListActivity.this, "Lato-Bold");
        fontLatoRegular = FontCache.get(EduMediaListActivity.this, "Lato-Regular");
        fontLatoHeavy   = FontCache.get(EduMediaListActivity.this, "Lato-Heavy");
        fontLatoBlack   = FontCache.get(EduMediaListActivity.this, "Lato-Black");
        fontLatoItalic  = FontCache.get(EduMediaListActivity.this, "Lato-Italic");
        rcEduMediaList  = (RecyclerView) findViewById(R.id.rcEduMediaList);
        fabAdd          = (FloatingActionButton) findViewById(R.id.fabAdd);
        txtFilter       = (EditText) findViewById(R.id.txtFilter);
        txtFilter.setTypeface(fontLatoRegular);

        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EduMediaListActivity.this, EduMediaAddActivity.class);
                startActivityForResult(intent, 1);
            }
        });

        pDialog = new ProgressDialog(EduMediaListActivity.this);
        pDialog.setMessage("Working...");
        pDialog.setCancelable(false);

        txtFilter.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                String text = txtFilter.getText().toString().toLowerCase(Locale.getDefault());
                eduMediaListRecyclerAdapter.filter(text);
            }
        });

        getEduMedia();
    }

    private void initToolbar() {
        SpannableString spanToolbar = new SpannableString("EduMedia");
        spanToolbar.setSpan(new TypeFaceSpan(EduMediaListActivity.this, "Lato-Bold"), 0, spanToolbar.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

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

    public void getEduMedia() {
        pDialog.show();
        String url = Config.URL + "getEduMedia.php";
        queue.getCache().clear();
        queue.getCache().remove(url);
        JsonArrayRequest jsArrRequest = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                ArrayList<Media> mValues = new ArrayList<>();
                for (int i=0; i<response.length(); i++) {
                    JSONObject jObj = response.optJSONObject(i);
                    Media media     = new Media(jObj.optString("MediaId"), jObj.optString("UserId"), jObj.optString("Name"),
                            jObj.optString("Photo"), jObj.optString("Title"), jObj.optString("Description"), jObj.optString("Url"));
                    mValues.add(media);
                }

                GridLayoutManager rcInformationListLayoutManager = new GridLayoutManager(EduMediaListActivity.this, 2);
                rcEduMediaList.setLayoutManager(rcInformationListLayoutManager);
                eduMediaListRecyclerAdapter = new EduMediaListRecyclerAdapter(EduMediaListActivity.this, mValues, EduMediaListActivity.this);
                rcEduMediaList.setAdapter(eduMediaListRecyclerAdapter);
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
    public void onDeleteClicked(final String strMediaId, String strMediaName) {
        final Dialog dialog = new Dialog(EduMediaListActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_dialog);
        dialog.setCancelable(false);

        TextView lblTitle = (TextView) dialog.findViewById(R.id.lblTitle);
        Button btnNo      = (Button) dialog.findViewById(R.id.btnNo);
        Button btnYes     = (Button) dialog.findViewById(R.id.btnYes);

        lblTitle.setText("Are you sure to delete " + strMediaName + "?");
        lblTitle.setTypeface(fontLatoRegular);
        btnNo.setTypeface(fontLatoBold);
        btnYes.setTypeface(fontLatoBold);

        btnNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        btnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                new deleteMedia(strMediaId).execute();
            }
        });

        try { dialog.show(); } catch (Exception e) {}
    }

    private class deleteMedia extends AsyncTask<String, Void, String> {
        private String strMediaId;
        public deleteMedia(String mMediaId) {
            strMediaId = mMediaId;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog.show();
        }
        @Override
        protected String doInBackground(String... params) {
            String strUrl   = Config.URL + "/service.php?ct=DELETEEDUMEDIA&MediaId="+strMediaId;
            String strHasil = callURL.call(strUrl);
            return strHasil;
        }
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Toast.makeText(getApplicationContext(), "Delete media succesfully!", Toast.LENGTH_LONG).show();
            getEduMedia();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 1) {
                if (!isFinishing()) {
                    getEduMedia();
                }
            }
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
