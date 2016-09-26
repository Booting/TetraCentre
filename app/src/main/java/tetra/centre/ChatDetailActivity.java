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
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import tetra.centre.Adapter.ChatAdapter;
import tetra.centre.SupportClass.Config;
import tetra.centre.SupportClass.FontCache;
import tetra.centre.SupportClass.TypeFaceSpan;
import tetra.centre.SupportClass.callURL;

public class ChatDetailActivity extends AppCompatActivity {
    private Typeface fontLatoBold, fontLatoRegular, fontLatoHeavy, fontLatoBlack, fontLatoItalic;
    private Toolbar toolbar;
    private ListView rcChatList;
    private ChatAdapter chatAdapter;
    private RequestQueue queue;
    private ProgressDialog pDialog;
    private SharedPreferences appsPref;
    private Bundle bundle;
    private EditText txtMessage;
    private TextView txtSend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
        setContentView(R.layout.activity_chat_detail);

        initToolbar();

        appsPref 	    = getSharedPreferences(Config.PREF_NAME, Activity.MODE_PRIVATE);
        queue    	    = Volley.newRequestQueue(this);
        fontLatoBold    = FontCache.get(ChatDetailActivity.this, "Lato-Bold");
        fontLatoRegular = FontCache.get(ChatDetailActivity.this, "Lato-Regular");
        fontLatoHeavy   = FontCache.get(ChatDetailActivity.this, "Lato-Heavy");
        fontLatoBlack   = FontCache.get(ChatDetailActivity.this, "Lato-Black");
        fontLatoItalic  = FontCache.get(ChatDetailActivity.this, "Lato-Italic");
        rcChatList      = (ListView) findViewById(R.id.rcChatList);
        txtMessage      = (EditText) findViewById(R.id.txtMessage);
        txtSend         = (TextView) findViewById(R.id.txtSend);

        bundle  = getIntent().getExtras();
        pDialog = new ProgressDialog(ChatDetailActivity.this);
        pDialog.setMessage("Working...");
        pDialog.setCancelable(false);

        txtMessage.setTypeface(fontLatoRegular);
        txtSend.setTypeface(fontLatoBold);

        txtSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (txtMessage.getText().length()==0) {
                    txtMessage.setError("Message is required!");
                } else {
                    new addNewChat().execute();
                }
            }
        });

        getListChat();
    }

    private void initToolbar() {
        SpannableString spanToolbar = new SpannableString("Chat");
        spanToolbar.setSpan(new TypeFaceSpan(ChatDetailActivity.this, "Lato-Bold"), 0, spanToolbar.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

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

    public void getListChat() {
        pDialog.show();
        String url;
        if (appsPref.getString("UserCategory", "").equalsIgnoreCase("1")) {
            url = Config.URL + "getDetailChat.php?EduspiratorId=" + appsPref.getString("UserId", "") +
                    "&ParticipantsId=" + bundle.getString("UserId");
        } else {
            url = Config.URL + "getDetailChat.php?EduspiratorId=" + bundle.getString("UserId") +
                    "&ParticipantsId=" + appsPref.getString("UserId", "");
        }
        queue.getCache().clear();
        queue.getCache().remove(url);
        System.out.println("Response (getListChat) : "+url);
        JsonArrayRequest jsArrRequest = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                System.out.println("Response (getListChat) : "+response);
                chatAdapter = new ChatAdapter(ChatDetailActivity.this, response, bundle.getString("UserId"),
                        bundle.getString("Name"), bundle.getString("Photo"));
                rcChatList.setAdapter(chatAdapter);
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

    private class addNewChat extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog.show();
        }
        @Override
        protected String doInBackground(String... params) {
            String strMessage = null;
            try {
                strMessage = URLEncoder.encode(txtMessage.getText().toString().replace("\"", "'"), "utf-8");
            } catch (UnsupportedEncodingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            Calendar c = Calendar.getInstance();
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String formattedDate = df.format(c.getTime());
            Date mDate;
            long timeInMilliseconds = 0;
            try {
                mDate = df.parse(formattedDate);
                timeInMilliseconds = mDate.getTime();
            } catch (ParseException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }

            String strUrl;
            if (appsPref.getString("UserCategory", "").equalsIgnoreCase("1")) {
                strUrl = Config.URL + "/service.php?ct=ADDCHAT&EduspiratorId=" + appsPref.getString("UserId", "") +
                        "&ParticipantsId=" + bundle.getString("UserId") +
                        "&SenderId=" + appsPref.getString("UserId", "") +
                        "&Message=" + strMessage +
                        "&Datee=" + timeInMilliseconds;
            } else {
                strUrl = Config.URL + "/service.php?ct=ADDCHAT&EduspiratorId=" + bundle.getString("UserId") +
                        "&ParticipantsId=" + appsPref.getString("UserId", "") +
                        "&SenderId=" + appsPref.getString("UserId", "") +
                        "&Message=" + strMessage +
                        "&Datee=" + timeInMilliseconds;
            }

            String strHasil = callURL.call(strUrl);
            return strHasil;
        }
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            txtMessage.setText("");
            getListChat();
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
