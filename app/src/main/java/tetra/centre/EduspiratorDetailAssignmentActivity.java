package tetra.centre;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

import tetra.centre.Adapter.AssignmentStudentListRecyclerAdapter;
import tetra.centre.SupportClass.Config;
import tetra.centre.SupportClass.FontCache;
import tetra.centre.SupportClass.TypeFaceSpan;
import tetra.centre.SupportClass.callURL;

public class EduspiratorDetailAssignmentActivity extends AppCompatActivity implements AssignmentStudentListRecyclerAdapter.AssignmentStudentListRecyclerAdapterListener {
    private Typeface fontLatoBold, fontLatoRegular, fontLatoHeavy, fontLatoBlack, fontLatoItalic;
    private Toolbar toolbar;
    private TextView lblName, lblScore, lblAction;
    private RecyclerView rcStudentAssignmentList;
    private AssignmentStudentListRecyclerAdapter assignmentStudentListRecyclerAdapter;
    private RequestQueue queue;
    private ProgressDialog pDialog, pDialogg;
    private SharedPreferences appsPref;
    private Bundle bundle;
    private String strFileName, strAssignmentDetailId;
    private Dialog dialog;
    private EditText txtNameOfAssigment;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
        setContentView(R.layout.activity_eduspirator_detail_assignment);

        initToolbar();

        appsPref 	            = getSharedPreferences(Config.PREF_NAME, Activity.MODE_PRIVATE);
        queue    	            = Volley.newRequestQueue(this);
        fontLatoBold            = FontCache.get(EduspiratorDetailAssignmentActivity.this, "Lato-Bold");
        fontLatoRegular         = FontCache.get(EduspiratorDetailAssignmentActivity.this, "Lato-Regular");
        fontLatoHeavy           = FontCache.get(EduspiratorDetailAssignmentActivity.this, "Lato-Heavy");
        fontLatoBlack           = FontCache.get(EduspiratorDetailAssignmentActivity.this, "Lato-Black");
        fontLatoItalic          = FontCache.get(EduspiratorDetailAssignmentActivity.this, "Lato-Italic");
        lblName                 = (TextView) findViewById(R.id.lblName);
        lblScore                = (TextView) findViewById(R.id.lblScore);
        lblAction               = (TextView) findViewById(R.id.lblAction);
        rcStudentAssignmentList = (RecyclerView) findViewById(R.id.rcStudentAssignmentList);

        lblName.setTypeface(fontLatoHeavy);
        lblScore.setTypeface(fontLatoHeavy);
        lblAction.setTypeface(fontLatoHeavy);

        pDialog = new ProgressDialog(EduspiratorDetailAssignmentActivity.this);
        pDialog.setMessage("Working...");
        pDialog.setCancelable(false);

        bundle = getIntent().getExtras();
        getEduspiratorAssignmentStudent();
    }

    private void initToolbar() {
        SpannableString spanToolbar = new SpannableString(getIntent().getStringExtra("AssignmentName"));
        spanToolbar.setSpan(new TypeFaceSpan(EduspiratorDetailAssignmentActivity.this, "Lato-Bold"), 0, spanToolbar.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

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

    public void getEduspiratorAssignmentStudent() {
        pDialog.show();
        String url = Config.URL + "getEduspiratorAssignmentStudent.php?AssignmentId=" + bundle.getString("AssignmentId");
        queue.getCache().clear();
        queue.getCache().remove(url);
        System.out.println("Response (getEduspiratorAssignmentStudent) : "+url);
        JsonArrayRequest jsArrRequest = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                System.out.println("Response (getEduspiratorAssignmentStudent) : "+response);
                LinearLayoutManager layoutManager = new LinearLayoutManager(EduspiratorDetailAssignmentActivity.this);
                layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                rcStudentAssignmentList.setLayoutManager(layoutManager);
                assignmentStudentListRecyclerAdapter = new AssignmentStudentListRecyclerAdapter(EduspiratorDetailAssignmentActivity.this,
                        response, EduspiratorDetailAssignmentActivity.this);
                rcStudentAssignmentList.setAdapter(assignmentStudentListRecyclerAdapter);
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

    @Override
    public void onDownloadClicked(String strFile) {
        new DownloadFileFromURL().execute(Config.URL + "Assignment/" + strFile);
    }

    @Override
    public void onScoreClicked(String strAssignmentDetailIdd, String strScore) {
        strAssignmentDetailId = strAssignmentDetailIdd;
        showDialogAddScore(strScore);
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
            pDialogg = new ProgressDialog(EduspiratorDetailAssignmentActivity.this);
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
            Toast.makeText(EduspiratorDetailAssignmentActivity.this, "Download Complete. Check file here : "+imagePath, Toast.LENGTH_LONG).show();
        }
    }

    private void showDialogAddScore(final String strScore) {
        dialog = new Dialog(EduspiratorDetailAssignmentActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_dialog_add_assignment);

        TextView lblAddNewAssignment   = (TextView) dialog.findViewById(R.id.lblAddNewAssignment);
        Button btnChooseAssignmentFile = (Button) dialog.findViewById(R.id.btnChooseAssignmentFile);
        txtNameOfAssigment             = (EditText) dialog.findViewById(R.id.txtNameOfAssigment);
        TextView lblSubmit             = (TextView) dialog.findViewById(R.id.lblSubmit);
        TextView lblCancel             = (TextView) dialog.findViewById(R.id.lblCancel);
        progressBar                    = (ProgressBar) dialog.findViewById(R.id.progressBar);

        lblAddNewAssignment.setTypeface(fontLatoBold);
        btnChooseAssignmentFile.setTypeface(fontLatoRegular);
        txtNameOfAssigment.setTypeface(fontLatoRegular);
        lblSubmit.setTypeface(fontLatoBold);
        lblCancel.setTypeface(fontLatoBold);

        lblAddNewAssignment.setText("Score");
        if (!strScore.equalsIgnoreCase("-")) {
            txtNameOfAssigment.setText(strScore);
        }
        txtNameOfAssigment.setHint("");
        txtNameOfAssigment.setInputType(InputType.TYPE_CLASS_NUMBER);
        btnChooseAssignmentFile.setVisibility(View.GONE);

        /*** Handle Bottom Bar Button Action ***/
        lblCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        lblSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (txtNameOfAssigment.getText().length() == 0) {
                    txtNameOfAssigment.setError("Name of Assignment is required!");
                } else {
                    new updateStudentScore().execute();
                }
            }
        });

        /*** Last, show dialog Report a Problem ***/
        if (!isFinishing()) {
            dialog.show();
        }
    }

    private class updateStudentScore extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
        }
        @Override
        protected String doInBackground(String... params) {
            String strUrl = Config.URL + "/service.php?ct=UPDATESTUDENTSCORE&Score=" + txtNameOfAssigment.getText().toString() +
                    "&AssignmentDetailId=" + strAssignmentDetailId;

            System.out.println("updateStudentScore : " + strUrl);
            String hasil = callURL.call(strUrl);
            return hasil;
        }
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Toast.makeText(getApplicationContext(), "Update score succesfully!", Toast.LENGTH_LONG).show();
            progressBar.setVisibility(View.GONE);
            dialog.dismiss();
            getEduspiratorAssignmentStudent();
        }
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
