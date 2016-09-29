package tetra.centre;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
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
import org.json.JSONArray;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import tetra.centre.Adapter.ClassChatAdapter;
import tetra.centre.SupportClass.Config;
import tetra.centre.SupportClass.FontCache;
import tetra.centre.SupportClass.SimpleFileDialog;
import tetra.centre.SupportClass.TypeFaceSpan;
import tetra.centre.SupportClass.callURL;

public class ClassDetailActivity extends AppCompatActivity implements ClassChatAdapter.ClassChatAdapterListener {
    private Typeface fontLatoBold, fontLatoRegular, fontLatoHeavy, fontLatoBlack, fontLatoItalic;
    private Toolbar toolbar;
    private WebView webView;
    private ProgressBar progressBusy;
    private RelativeLayout relWebview;
    private ListView rcChatList;
    private EditText txtMessage;
    private TextView txtSend;
    private ClassChatAdapter classChatAdapter;
    private ProgressDialog pDialog, pDialogg;
    private RequestQueue queue;
    private SharedPreferences appsPref;
    private ImageView imgAttachment;
    private HttpEntity resEntity;
    private String m_chosen, strFileName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
        setContentView(R.layout.activity_class_detail);

        initToolbar();

        appsPref 	    = getSharedPreferences(Config.PREF_NAME, Activity.MODE_PRIVATE);
        queue    	    = Volley.newRequestQueue(this);
        fontLatoBold    = FontCache.get(ClassDetailActivity.this, "Lato-Bold");
        fontLatoRegular = FontCache.get(ClassDetailActivity.this, "Lato-Regular");
        fontLatoHeavy   = FontCache.get(ClassDetailActivity.this, "Lato-Heavy");
        fontLatoBlack   = FontCache.get(ClassDetailActivity.this, "Lato-Black");
        fontLatoItalic  = FontCache.get(ClassDetailActivity.this, "Lato-Italic");
        rcChatList      = (ListView) findViewById(R.id.rcChatList);
        webView         = (WebView) findViewById(R.id.webView);
        progressBusy    = (ProgressBar) findViewById(R.id.progressBusy);
        relWebview      = (RelativeLayout) findViewById(R.id.relWebview);
        txtMessage      = (EditText) findViewById(R.id.txtMessage);
        txtSend         = (TextView) findViewById(R.id.txtSend);
        imgAttachment   = (ImageView) findViewById(R.id.imgAttachment);

        txtSend.setTypeface(fontLatoBold);
        txtMessage.setTypeface(fontLatoRegular);

        relWebview.setVisibility(View.VISIBLE);
        webView.setWebViewClient(new myWebClient());
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.loadUrl("http://docs.google.com/gview?embedded=true&url=" + Config.URL_CLASS + getIntent().getStringExtra("File"));

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

        pDialog = new ProgressDialog(ClassDetailActivity.this);
        pDialog.setMessage("Working...");
        pDialog.setCancelable(false);

        imgAttachment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /////////////////////////////////////////////////////////////////////////////////////////////////
                //Create FileOpenDialog and register a callback
                /////////////////////////////////////////////////////////////////////////////////////////////////
                SimpleFileDialog FileOpenDialog =  new SimpleFileDialog(ClassDetailActivity.this, "FileOpen",
                        new SimpleFileDialog.SimpleFileDialogListener() {
                            @Override
                            public void onChosenDir(String chosenDir) {
                                File file = new File(chosenDir);
                                if (file.getName().toLowerCase().endsWith("pdf") || file.getName().toLowerCase().endsWith("doc") ||
                                        file.getName().toLowerCase().endsWith("docs")) {
                                    // The code in this function will be executed when the dialog OK button is pushed
                                    m_chosen   = chosenDir;
                                    File file1 = new File(m_chosen);
                                    txtMessage.setText(file1.getName());
                                    new UploadAttachment().execute();
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

        getListChat();
    }

    public void getListChat() {
        pDialog.show();
        String url = Config.URL + "getClassChat.php?ClassId=" + getIntent().getStringExtra("ClassId");
        queue.getCache().clear();
        queue.getCache().remove(url);
        System.out.println("Response (getListChat) : "+url);
        JsonArrayRequest jsArrRequest = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                System.out.println("Response (getListChat) : "+response);
                classChatAdapter = new ClassChatAdapter(ClassDetailActivity.this, response, ClassDetailActivity.this);
                rcChatList.setAdapter(classChatAdapter);
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
    public void onDownloadClicked(String strFile) {
        new DownloadFileFromURL().execute(Config.URL + "Assignment/" + strFile.replaceAll(" ", "%20"));
    }

    /**
     * Background Async Task to download file
     * */
    class DownloadFileFromURL extends AsyncTask<String, String, String> {
        /**
         * Before starting background thread
         * Show Progress Bar Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialogg = new ProgressDialog(ClassDetailActivity.this);
            pDialogg.setMessage("Downloading file. Please wait...");
            pDialogg.setIndeterminate(false);
            pDialogg.setMax(100);
            pDialogg.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            pDialogg.setCancelable(true);
            pDialogg.show();
        }

        /**
         * Downloading file in background thread
         * */
        @Override
        protected String doInBackground(String... f_url) {
            int count;
            try {
                URL url = new URL(f_url[0]);
                URLConnection conection = url.openConnection();
                conection.connect();
                // this will be useful so that you can show a tipical 0-100% progress bar
                int lenghtOfFile = conection.getContentLength();

                // download the file
                InputStream input = new BufferedInputStream(url.openStream(), 8192);

                // Output stream
                strFileName = f_url[0].substring(f_url[0].lastIndexOf('/') + 1);
                OutputStream output = new FileOutputStream("/sdcard/"+strFileName);

                byte data[] = new byte[1024];

                long total = 0;

                while ((count = input.read(data)) != -1) {
                    total += count;
                    // publishing the progress....
                    // After this onProgressUpdate will be called
                    publishProgress(""+(int)((total*100)/lenghtOfFile));

                    // writing data to file
                    output.write(data, 0, count);
                }

                // flushing output
                output.flush();

                // closing streams
                output.close();
                input.close();

            } catch (Exception e) {
                e.printStackTrace();
                Log.e("Error: ", e.getMessage());
            }

            return null;
        }

        /**
         * Updating progress bar
         * */
        protected void onProgressUpdate(String... progress) {
            // setting progress percentage
            pDialogg.setProgress(Integer.parseInt(progress[0]));
        }

        /**
         * After completing background task
         * Dismiss the progress dialog
         * **/
        @Override
        protected void onPostExecute(String file_url) {
            // dismiss the dialog after the file was downloaded
            pDialogg.dismiss();

            // Displaying downloaded image into image view
            // Reading image path from sdcard
            String imagePath = Environment.getExternalStorageDirectory().toString() + "/" + strFileName;
            Toast.makeText(ClassDetailActivity.this, "Download Complete. Check file here : "+imagePath, Toast.LENGTH_LONG).show();
        }
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

            String strUrl = Config.URL + "/service.php?ct=ADDCLASSCHAT&ClassId=" + getIntent().getStringExtra("ClassId") +
                        "&SenderId=" + appsPref.getString("UserId", "") +
                        "&Message=" + strMessage +
                        "&Datee=" + timeInMilliseconds;

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

    public class myWebClient extends WebViewClient {
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            // TODO Auto-generated method stub
            super.onPageStarted(view, url, favicon);
            progressBusy.setVisibility(View.VISIBLE);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            // TODO Auto-generated method stub
            view.loadUrl(url);
            return true;

        }

        @Override
        public void onPageFinished(WebView view, String url) {
            // TODO Auto-generated method stub
            super.onPageFinished(view, url);
            progressBusy.setVisibility(View.GONE);
        }
    }

    private void initToolbar() {
        SpannableString spanToolbar = new SpannableString(getIntent().getStringExtra("Name"));
        spanToolbar.setSpan(new TypeFaceSpan(ClassDetailActivity.this, "Lato-Bold"), 0, spanToolbar.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

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

    private class UploadAttachment extends AsyncTask<HttpEntity, Void, HttpEntity> {
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
                new addNewChat().execute();
            } else {
                pDialog.dismiss();
            }
        }
    }

    private HttpEntity doFileUploadMedia() {
        try {
            HttpClient client = new DefaultHttpClient();
            HttpPost post = null;
            File file1    = null;
            FileBody bin1 = null;

            MultipartEntity reqEntity = new MultipartEntity();
            post  = new HttpPost("http://tetracentre.com/tetracenter_ws/UploadMaterial.php");
            file1 = new File(m_chosen);

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
