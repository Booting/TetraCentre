package tetra.centre;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
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
import android.widget.Toast;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;

import tetra.centre.Adapter.InformationListRecyclerAdapter;
import tetra.centre.SupportClass.Config;
import tetra.centre.SupportClass.FontCache;
import tetra.centre.SupportClass.TypeFaceSpan;

public class InformationListActivity extends AppCompatActivity {
    private Typeface fontLatoBold, fontLatoRegular, fontLatoHeavy, fontLatoBlack, fontLatoItalic;
    private Toolbar toolbar;
    private RecyclerView rcInformationList;
    private FloatingActionButton fabAdd;
    private InformationListRecyclerAdapter informationListRecyclerAdapter;
    private RequestQueue queue;
    private ProgressDialog pDialog;
    private SharedPreferences appsPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
        setContentView(R.layout.activity_information_list);

        initToolbar();

        appsPref 	       = getSharedPreferences(Config.PREF_NAME, Activity.MODE_PRIVATE);
        queue    	       = Volley.newRequestQueue(this);
        fontLatoBold       = FontCache.get(InformationListActivity.this, "Lato-Bold");
        fontLatoRegular    = FontCache.get(InformationListActivity.this, "Lato-Regular");
        fontLatoHeavy      = FontCache.get(InformationListActivity.this, "Lato-Heavy");
        fontLatoBlack      = FontCache.get(InformationListActivity.this, "Lato-Black");
        fontLatoItalic     = FontCache.get(InformationListActivity.this, "Lato-Italic");
        rcInformationList  = (RecyclerView) findViewById(R.id.rcInformationList);
        fabAdd             = (FloatingActionButton) findViewById(R.id.fabAdd);

        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(InformationListActivity.this, InformationAddActivity.class);
                startActivityForResult(intent, 1);
            }
        });

        pDialog = new ProgressDialog(InformationListActivity.this);
        pDialog.setMessage("Working...");
        pDialog.setCancelable(false);

        getListInformation();
    }

    private void initToolbar() {
        SpannableString spanToolbar = new SpannableString("Information");
        spanToolbar.setSpan(new TypeFaceSpan(InformationListActivity.this, "Lato-Bold"), 0, spanToolbar.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

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

    public void getListInformation() {
        pDialog.show();
        String url = Config.URL + "getInformation.php";
        queue.getCache().clear();
        queue.getCache().remove(url);
        JsonArrayRequest jsArrRequest = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                GridLayoutManager rcInformationListLayoutManager = new GridLayoutManager(InformationListActivity.this, 3);
                rcInformationList.setLayoutManager(rcInformationListLayoutManager);
                informationListRecyclerAdapter = new InformationListRecyclerAdapter(InformationListActivity.this, response);
                rcInformationList.setAdapter(informationListRecyclerAdapter);
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 1) {
                if (!isFinishing()) {
                    getListInformation();
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
