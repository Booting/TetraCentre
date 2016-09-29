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
import android.text.Spannable;
import android.text.SpannableString;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import tetra.centre.Adapter.EduVideoListRecyclerAdapter;
import tetra.centre.SupportClass.Config;
import tetra.centre.SupportClass.FontCache;
import tetra.centre.SupportClass.TypeFaceSpan;
import tetra.centre.SupportClass.callURL;

public class EduVideoListActivity extends AppCompatActivity implements EduVideoListRecyclerAdapter.EduVideoListRecyclerAdapterListener {
    private Typeface fontLatoBold, fontLatoRegular, fontLatoHeavy, fontLatoBlack, fontLatoItalic;
    private Toolbar toolbar;
    private RecyclerView rcEduVideoList;
    private FloatingActionButton fabAdd;
    private EduVideoListRecyclerAdapter eduVideoListRecyclerAdapter;
    private RequestQueue queue;
    private ProgressDialog pDialog;
    private SharedPreferences appsPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
        setContentView(R.layout.activity_eduvideo_list);

        initToolbar();

        appsPref 	    = getSharedPreferences(Config.PREF_NAME, Activity.MODE_PRIVATE);
        queue    	    = Volley.newRequestQueue(this);
        fontLatoBold    = FontCache.get(EduVideoListActivity.this, "Lato-Bold");
        fontLatoRegular = FontCache.get(EduVideoListActivity.this, "Lato-Regular");
        fontLatoHeavy   = FontCache.get(EduVideoListActivity.this, "Lato-Heavy");
        fontLatoBlack   = FontCache.get(EduVideoListActivity.this, "Lato-Black");
        fontLatoItalic  = FontCache.get(EduVideoListActivity.this, "Lato-Italic");
        rcEduVideoList  = (RecyclerView) findViewById(R.id.rcEduVideoList);
        fabAdd          = (FloatingActionButton) findViewById(R.id.fabAdd);

        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EduVideoListActivity.this, EduVideoAddActivity.class);
                startActivityForResult(intent, 1);
            }
        });

        pDialog = new ProgressDialog(EduVideoListActivity.this);
        pDialog.setMessage("Working...");
        pDialog.setCancelable(false);

        getEduVideo();
    }

    private void initToolbar() {
        SpannableString spanToolbar = new SpannableString("EduVideo");
        spanToolbar.setSpan(new TypeFaceSpan(EduVideoListActivity.this, "Lato-Bold"), 0, spanToolbar.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

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

    public void getEduVideo() {
        pDialog.show();
        String url = Config.URL + "getEduVideo.php";
        queue.getCache().clear();
        queue.getCache().remove(url);
        JsonArrayRequest jsArrRequest = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                GridLayoutManager rcInformationListLayoutManager = new GridLayoutManager(EduVideoListActivity.this, 1);
                rcEduVideoList.setLayoutManager(rcInformationListLayoutManager);
                eduVideoListRecyclerAdapter = new EduVideoListRecyclerAdapter(EduVideoListActivity.this, response, EduVideoListActivity.this);
                rcEduVideoList.setAdapter(eduVideoListRecyclerAdapter);
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
    public void onDeleteClicked(final String strVideoId, String strVideoName) {
        final Dialog dialog = new Dialog(EduVideoListActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_dialog);
        dialog.setCancelable(false);

        TextView lblTitle = (TextView) dialog.findViewById(R.id.lblTitle);
        Button btnNo      = (Button) dialog.findViewById(R.id.btnNo);
        Button btnYes     = (Button) dialog.findViewById(R.id.btnYes);

        lblTitle.setText("Are you sure to delete " + strVideoName + "?");
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
                new deleteVideo(strVideoId).execute();
            }
        });

        try { dialog.show(); } catch (Exception e) {}
    }

    private class deleteVideo extends AsyncTask<String, Void, String> {
        private String strVideoId;
        public deleteVideo(String mVideoId) {
            strVideoId = mVideoId;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog.show();
        }
        @Override
        protected String doInBackground(String... params) {
            String strUrl   = Config.URL + "/service.php?ct=DELETEEDUVIDEO&VideoId="+strVideoId;
            String strHasil = callURL.call(strUrl);
            return strHasil;
        }
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Toast.makeText(getApplicationContext(), "Delete video succesfully!", Toast.LENGTH_LONG).show();
            getEduVideo();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 1) {
                if (!isFinishing()) {
                    getEduVideo();
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
