package tetra.centre;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
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
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import de.hdodenhof.circleimageview.CircleImageView;
import tetra.centre.Adapter.AssignmentListRecyclerAdapter;
import tetra.centre.Adapter.ChatListRecyclerAdapter;
import tetra.centre.Adapter.ClassListRecyclerAdapter;
import tetra.centre.SupportClass.Config;
import tetra.centre.SupportClass.FontCache;
import tetra.centre.SupportClass.SimpleFileDialog;
import tetra.centre.SupportClass.TypeFaceSpan;
import tetra.centre.Adapter.ClassListRecyclerAdapter.ClassListRecyclerAdapterListener;
import tetra.centre.Adapter.ChatListRecyclerAdapter.ChatListRecyclerAdapterListener;
import tetra.centre.Adapter.AssignmentListRecyclerAdapter.AssignmentListRecyclerAdapterListener;
import tetra.centre.SupportClass.callURL;

public class EduspiratorDetailActivity extends AppCompatActivity implements ClassListRecyclerAdapterListener,
        ChatListRecyclerAdapterListener, AssignmentListRecyclerAdapterListener {
    private Typeface fontLatoBold, fontLatoRegular, fontLatoHeavy, fontLatoBlack, fontLatoItalic;
    private Toolbar toolbar;
    private CircleImageView imgProfile;
    private TextView lblEduspirator, lblClass, lblAssignment, lblChat, lblCall;
    private RecyclerView rcClassList, rcAssignmentList, rcChatList, rcCallList;
    private ClassListRecyclerAdapter classListRecyclerAdapter;
    private AssignmentListRecyclerAdapter assignmentListRecyclerAdapter;
    private ChatListRecyclerAdapter chatListRecyclerAdapter, callListRecyclerAdapter;
    private RelativeLayout relClass, relAssignment, relChat, relCall;
    private RequestQueue queue;
    private ProgressDialog pDialog, pDialogg;
    private SharedPreferences appsPref;
    private Bundle bundle;
    private ImageView imgChatRight, imgCallRight;
    private LinearLayout linClass, linAssignment;
    private Button btnAddNewClass, btnAddNewAssignment;
    private String m_chosen, pathTwo="";
    private ProgressBar progressBar;
    private HttpEntity resEntity;
    private EditText txtNameOfAssigment;
    private Dialog dialog;
    private String strFileName, strAssignmentId;
    private View viewOne;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
        setContentView(R.layout.activity_eduspirator_detail);

        initToolbar();

        appsPref 	        = getSharedPreferences(Config.PREF_NAME, Activity.MODE_PRIVATE);
        queue    	        = Volley.newRequestQueue(this);
        fontLatoBold        = FontCache.get(EduspiratorDetailActivity.this, "Lato-Bold");
        fontLatoRegular     = FontCache.get(EduspiratorDetailActivity.this, "Lato-Regular");
        fontLatoHeavy       = FontCache.get(EduspiratorDetailActivity.this, "Lato-Heavy");
        fontLatoBlack       = FontCache.get(EduspiratorDetailActivity.this, "Lato-Black");
        fontLatoItalic      = FontCache.get(EduspiratorDetailActivity.this, "Lato-Italic");
        imgProfile          = (CircleImageView) findViewById(R.id.imgProfile);
        lblEduspirator      = (TextView) findViewById(R.id.lblEduspirator);
        lblClass            = (TextView) findViewById(R.id.lblClass);
        lblAssignment       = (TextView) findViewById(R.id.lblAssignment);
        lblChat             = (TextView) findViewById(R.id.lblChat);
        lblCall             = (TextView) findViewById(R.id.lblCall);
        rcClassList         = (RecyclerView) findViewById(R.id.rcClassList);
        rcAssignmentList    = (RecyclerView) findViewById(R.id.rcAssignmentList);
        relClass            = (RelativeLayout) findViewById(R.id.relClass);
        relAssignment       = (RelativeLayout) findViewById(R.id.relAssignment);
        relChat             = (RelativeLayout) findViewById(R.id.relChat);
        rcChatList          = (RecyclerView) findViewById(R.id.rcChatList);
        imgChatRight        = (ImageView) findViewById(R.id.imgChatRight);
        linClass            = (LinearLayout) findViewById(R.id.linClass);
        linAssignment       = (LinearLayout) findViewById(R.id.linAssignment);
        btnAddNewClass      = (Button) findViewById(R.id.btnAddNewClass);
        btnAddNewAssignment = (Button) findViewById(R.id.btnAddNewAssignment);
        bundle              = getIntent().getExtras();
        relCall             = (RelativeLayout) findViewById(R.id.relCall);
        rcCallList          = (RecyclerView) findViewById(R.id.rcCallList);
        imgCallRight        = (ImageView) findViewById(R.id.imgCallRight);
        viewOne             = (View) findViewById(R.id.viewOne);

        lblEduspirator.setTypeface(fontLatoBold);
        lblEduspirator.setText(getIntent().getStringExtra("Name"));
        Glide.with(this).load(Config.URL_PICTURES + getIntent().getStringExtra("Photo")).placeholder(R.drawable.placeholder)
                .centerCrop().diskCacheStrategy(DiskCacheStrategy.ALL).override(300, 300)
                .dontAnimate().into(imgProfile);

        lblClass.setTypeface(fontLatoHeavy);
        lblAssignment.setTypeface(fontLatoHeavy);
        lblChat.setTypeface(fontLatoHeavy);
        lblCall.setTypeface(fontLatoHeavy);
        btnAddNewClass.setTypeface(fontLatoBold);
        btnAddNewAssignment.setTypeface(fontLatoBold);

        relClass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (linClass.getVisibility()==View.VISIBLE) {
                    linClass.setVisibility(View.GONE);
                } else {
                    linClass.setVisibility(View.VISIBLE);
                }
            }
        });

        relAssignment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (linAssignment.getVisibility()==View.VISIBLE) {
                    linAssignment.setVisibility(View.GONE);
                } else {
                    linAssignment.setVisibility(View.VISIBLE);
                }
            }
        });

        if (appsPref.getString("UserCategory", "").equalsIgnoreCase("1")) {
            imgChatRight.setVisibility(View.VISIBLE);
            imgCallRight.setVisibility(View.VISIBLE);
            if (bundle.getString("UserId").equalsIgnoreCase(appsPref.getString("UserId", ""))) {
                btnAddNewClass.setVisibility(View.VISIBLE);
                btnAddNewAssignment.setVisibility(View.VISIBLE);
            } else {
                btnAddNewClass.setVisibility(View.GONE);
                btnAddNewAssignment.setVisibility(View.GONE);
            }
        } else {
            imgChatRight.setVisibility(View.GONE);
            imgCallRight.setVisibility(View.GONE);
            btnAddNewClass.setVisibility(View.GONE);
            btnAddNewAssignment.setVisibility(View.GONE);
        }
        relChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (appsPref.getString("UserCategory", "").equalsIgnoreCase("1")) {
                    if (rcChatList.getVisibility()==View.VISIBLE) {
                        rcChatList.setVisibility(View.GONE);
                    } else {
                        rcChatList.setVisibility(View.VISIBLE);
                    }
                } else {
                    Intent intent = new Intent(EduspiratorDetailActivity.this, ChatDetailActivity.class);
                    intent.putExtra("UserId", bundle.getString("UserId"));
                    intent.putExtra("Name", bundle.getString("Name"));
                    intent.putExtra("Photo", bundle.getString("Photo"));
                    startActivity(intent);
                }
            }
        });

        relCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (appsPref.getString("UserCategory", "").equalsIgnoreCase("1")) {
                    if (rcCallList.getVisibility()==View.VISIBLE) {
                        rcCallList.setVisibility(View.GONE);
                        viewOne.setVisibility(View.GONE);
                    } else {
                        rcCallList.setVisibility(View.VISIBLE);
                        viewOne.setVisibility(View.VISIBLE);
                    }
                } else {
                    if (bundle.getString("PhoneNumber").equalsIgnoreCase("")) {
                        Toast.makeText(getBaseContext(), "No. Telp is Required!", Toast.LENGTH_SHORT).show();
                    } else {
                        showCustomDialog(getIntent().getStringExtra("Name"));
                    }
                }
            }
        });

        pDialog = new ProgressDialog(EduspiratorDetailActivity.this);
        pDialog.setMessage("Working...");
        pDialog.setCancelable(false);

        getEduspiratorClass(bundle.getString("UserId"));
        getEduspiratorAssignment(bundle.getString("UserId"));
        getEduspiratorChat(bundle.getString("UserId"));
        getEduspiratorCall(bundle.getString("UserId"));

        btnAddNewAssignment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogAddNewAssignment("", false);
            }
        });
        btnAddNewClass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogAddNewAssignment("", true);
            }
        });
    }

    private void initToolbar() {
        SpannableString spanToolbar = new SpannableString(getIntent().getStringExtra("Name"));
        spanToolbar.setSpan(new TypeFaceSpan(EduspiratorDetailActivity.this, "Lato-Bold"), 0, spanToolbar.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

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

    public void getStudentClass(final JSONArray jArrClass) {
        String url = Config.URL + "getStudentClass.php?StudentId=" + appsPref.getString("UserId", "");
        queue.getCache().clear();
        queue.getCache().remove(url);
        JsonArrayRequest jsArrRequest = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                LinearLayoutManager layoutManager = new LinearLayoutManager(EduspiratorDetailActivity.this);
                layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                rcClassList.setLayoutManager(layoutManager);
                classListRecyclerAdapter = new ClassListRecyclerAdapter(EduspiratorDetailActivity.this,
                        jArrClass, response, bundle.getString("UserId"), EduspiratorDetailActivity.this);
                rcClassList.setAdapter(classListRecyclerAdapter);
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

    public void getEduspiratorClass(String strUserId) {
        pDialog.show();
        String url = Config.URL + "getEduspiratorClass.php?UserId="+strUserId;
        queue.getCache().clear();
        queue.getCache().remove(url);
        JsonArrayRequest jsArrRequest = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                getStudentClass(response);
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

    public void getEduspiratorAssignment(String strUserId) {
        pDialog.show();
        String url = Config.URL + "getEduspiratorAssignment.php?EduspiratorId="+strUserId;
        queue.getCache().clear();
        queue.getCache().remove(url);
        JsonArrayRequest jsArrRequest = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                System.out.println("Response (getEduspiratorAssignment) : "+response);
                getEduspiratorAssignmentScore(response);
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

    public void getEduspiratorAssignmentScore(final JSONArray jArrResponse) {
        String url = Config.URL + "getEduspiratorAssignmentScore.php";
        queue.getCache().clear();
        queue.getCache().remove(url);
        JsonArrayRequest jsArrRequest = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                System.out.println("Response (getEduspiratorAssignmentScore) : "+response);
                LinearLayoutManager layoutManager = new LinearLayoutManager(EduspiratorDetailActivity.this);
                layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                rcAssignmentList.setLayoutManager(layoutManager);
                assignmentListRecyclerAdapter = new AssignmentListRecyclerAdapter(EduspiratorDetailActivity.this,
                        jArrResponse, response, bundle.getString("UserId"), EduspiratorDetailActivity.this);
                rcAssignmentList.setAdapter(assignmentListRecyclerAdapter);
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

    public void getEduspiratorChat(String strUserId) {
        pDialog.show();
        String url = Config.URL + "getEduspiratorChat.php?EduspiratorId="+strUserId;
        queue.getCache().clear();
        queue.getCache().remove(url);
        JsonArrayRequest jsArrRequest = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                System.out.println("Response (getEduspiratorChat) : "+response);
                LinearLayoutManager layoutManager = new LinearLayoutManager(EduspiratorDetailActivity.this);
                layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                rcChatList.setLayoutManager(layoutManager);
                chatListRecyclerAdapter = new ChatListRecyclerAdapter(EduspiratorDetailActivity.this, response, false, EduspiratorDetailActivity.this);
                rcChatList.setAdapter(chatListRecyclerAdapter);
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

    public void getEduspiratorCall(String strUserId) {
        pDialog.show();
        String url = Config.URL + "getEduspiratorCall.php?UserId="+strUserId;
        queue.getCache().clear();
        queue.getCache().remove(url);
        JsonArrayRequest jsArrRequest = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                System.out.println("Response (getEduspiratorCall) : "+response);
                GridLayoutManager gridLayoutManager = new GridLayoutManager(EduspiratorDetailActivity.this, 1);
                rcCallList.setLayoutManager(gridLayoutManager);
                callListRecyclerAdapter = new ChatListRecyclerAdapter(EduspiratorDetailActivity.this, response, true, EduspiratorDetailActivity.this);
                rcCallList.setAdapter(callListRecyclerAdapter);
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
    public void onApplyClicked(String strClassId) {
        new addStudentClass(strClassId).execute();
    }

    @Override
    public void onListClicked(String strUserId, String strName, String strPhoto, boolean blnCall) {
        if (bundle.getString("UserId").equalsIgnoreCase(appsPref.getString("UserId", ""))) {
            if (blnCall) {
                showCustomDialog(strName);
            } else {
                Intent intent = new Intent(EduspiratorDetailActivity.this, ChatDetailActivity.class);
                intent.putExtra("UserId", strUserId);
                intent.putExtra("Name", strName);
                intent.putExtra("Photo", strPhoto);
                startActivity(intent);
            }
        }
    }

    @Override
    public void onDownloadClicked(String strFile) {
        new DownloadFileFromURL().execute(Config.URL + "Assignment/" + strFile.replaceAll(" ", "%20"));
    }

    @Override
    public void onUploadClicked(String strAssignmentIdd) {
        strAssignmentId = strAssignmentIdd;
        showDialogAddNewAssignment(strAssignmentIdd, false);
    }

    private class addStudentClass extends AsyncTask<String, Void, String> {
        String strClassId;
        private addStudentClass(String strClassId) {
            this.strClassId = strClassId;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog.show();
        }
        @Override
        protected String doInBackground(String... params) {
            String url = Config.URL + "/service.php?ct=ADDSTUDENTCLASS&StudentId=" + appsPref.getString("UserId", "") +
                    "&ClassId=" + strClassId;
            String hasil = callURL.call(url);
            return hasil;
        }
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            pDialog.dismiss();
            getEduspiratorClass(bundle.getString("UserId"));
        }
    }

    private void showDialogAddNewAssignment(final String strAssignmentId, final boolean blnClass) {
        dialog = new Dialog(EduspiratorDetailActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_dialog_add_assignment);

        TextView lblAddNewAssignment         = (TextView) dialog.findViewById(R.id.lblAddNewAssignment);
        final Button btnChooseAssignmentFile = (Button) dialog.findViewById(R.id.btnChooseAssignmentFile);
        txtNameOfAssigment                   = (EditText) dialog.findViewById(R.id.txtNameOfAssigment);
        TextView lblSubmit                   = (TextView) dialog.findViewById(R.id.lblSubmit);
        TextView lblCancel                   = (TextView) dialog.findViewById(R.id.lblCancel);
        progressBar                          = (ProgressBar) dialog.findViewById(R.id.progressBar);

        lblAddNewAssignment.setTypeface(fontLatoBold);
        btnChooseAssignmentFile.setTypeface(fontLatoRegular);
        txtNameOfAssigment.setTypeface(fontLatoRegular);
        lblSubmit.setTypeface(fontLatoBold);
        lblCancel.setTypeface(fontLatoBold);

        if (!strAssignmentId.equalsIgnoreCase("")) {
            lblAddNewAssignment.setText("Upload Assignment");
            txtNameOfAssigment.setVisibility(View.GONE);
        }

        if (blnClass) {
            lblAddNewAssignment.setText("Add New Class");
            btnChooseAssignmentFile.setText("Choose Class File");
            txtNameOfAssigment.setHint("Name of Class");
        }

        btnChooseAssignmentFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /////////////////////////////////////////////////////////////////////////////////////////////////
                //Create FileOpenDialog and register a callback
                /////////////////////////////////////////////////////////////////////////////////////////////////
                SimpleFileDialog FileOpenDialog =  new SimpleFileDialog(EduspiratorDetailActivity.this, "FileOpen",
                        new SimpleFileDialog.SimpleFileDialogListener() {
                            @Override
                            public void onChosenDir(String chosenDir) {
                                // The code in this function will be executed when the dialog OK button is pushed
                                m_chosen = chosenDir;
                                btnChooseAssignmentFile.setText(m_chosen);
                            }
                        });
                //You can change the default filename using the public variable "Default_File_Name"
                FileOpenDialog.Default_File_Name = "";
                FileOpenDialog.chooseFile_or_Dir();
                /////////////////////////////////////////////////////////////////////////////////////////////////
            }
        });

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
                if (!strAssignmentId.equalsIgnoreCase("")) {
                    if (m_chosen == null) {
                        Toast.makeText(EduspiratorDetailActivity.this, "File of Assignment is required!", Toast.LENGTH_LONG).show();
                    } else {
                        new SubmitStudentAssignment().execute();
                    }
                } else {
                    if (m_chosen == null) {
                        if (blnClass) {
                            Toast.makeText(EduspiratorDetailActivity.this, "File of Class is required!", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(EduspiratorDetailActivity.this, "File of Assignment is required!", Toast.LENGTH_LONG).show();
                        }
                    } else if (txtNameOfAssigment.getText().length() == 0) {
                        if (blnClass) {
                            txtNameOfAssigment.setError("Name of Class is required!");
                        } else {
                            txtNameOfAssigment.setError("Name of Assignment is required!");
                        }
                    } else {
                        if (blnClass) {
                            new SubmitNewClass().execute();
                        } else {
                            new SubmitNewAssignment().execute();
                        }
                    }
                }
            }
        });

        /*** Last, show dialog Report a Problem ***/
        if (!isFinishing()) {
            dialog.show();
        }
    }

    private class SubmitNewAssignment extends AsyncTask<HttpEntity, Void, HttpEntity> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
        }
        @Override
        protected HttpEntity doInBackground(HttpEntity... params) {
            return doFileUploadAssignment();
        }
        @Override
        protected void onPostExecute(HttpEntity result) {
            super.onPostExecute(result);
            if (result != null) {
                new addNewAssignment().execute();
            } else {
                progressBar.setVisibility(View.GONE);
            }
        }
    }

    private class SubmitNewClass extends AsyncTask<HttpEntity, Void, HttpEntity> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
        }
        @Override
        protected HttpEntity doInBackground(HttpEntity... params) {
            return doFileUploadClass();
        }
        @Override
        protected void onPostExecute(HttpEntity result) {
            super.onPostExecute(result);
            if (result != null) {
                new addNewClass().execute();
            } else {
                progressBar.setVisibility(View.GONE);
            }
        }
    }

    private class SubmitStudentAssignment extends AsyncTask<HttpEntity, Void, HttpEntity> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
        }
        @Override
        protected HttpEntity doInBackground(HttpEntity... params) {
            return doFileUploadAssignment();
        }
        @Override
        protected void onPostExecute(HttpEntity result) {
            super.onPostExecute(result);
            if (result != null) {
                new addNewStudentAssignment().execute();
            } else {
                progressBar.setVisibility(View.GONE);
            }
        }
    }

    private HttpEntity doFileUploadAssignment() {
        try {
            HttpClient client = new DefaultHttpClient();
            HttpPost post = null;
            File file1    = null;
            FileBody bin1 = null;

            MultipartEntity reqEntity = new MultipartEntity();
            post    = new HttpPost("http://tetracentre.com/tetracenter_ws/UploadAssignment.php");
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

    private HttpEntity doFileUploadClass() {
        try {
            HttpClient client = new DefaultHttpClient();
            HttpPost post = null;
            File file1    = null;
            FileBody bin1 = null;

            MultipartEntity reqEntity = new MultipartEntity();
            post    = new HttpPost("http://tetracentre.com/tetracenter_ws/UploadClass.php");
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

    private class addNewAssignment extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        protected String doInBackground(String... params) {
            String strNameOfAssignment = null;
            String strFileOfAssignment = null;
            try {
                strNameOfAssignment = URLEncoder.encode(txtNameOfAssigment.getText().toString().replace("\"", "'"), "utf-8");
                strFileOfAssignment = URLEncoder.encode(pathTwo.replace("\"", "'"), "utf-8");
            } catch (UnsupportedEncodingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            String strUrl = Config.URL + "/service.php?ct=ADDASSIGNMENT&EduspiratorId=" + appsPref.getString("UserId", "") +
                    "&Name=" + strNameOfAssignment + "&File=" + strFileOfAssignment;

            System.out.println("addNewAssignment : " + strUrl);
            String strHasil = callURL.call(strUrl);
            return strHasil;
        }
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Toast.makeText(getApplicationContext(), "Add new assigment succesfully!", Toast.LENGTH_LONG).show();
            progressBar.setVisibility(View.GONE);
            dialog.dismiss();
            m_chosen = null;
            pathTwo  = "";
            getEduspiratorAssignment(bundle.getString("UserId"));
        }
    }

    private class addNewClass extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        protected String doInBackground(String... params) {
            String strNameOfAssignment = null;
            String strFileOfAssignment = null;
            try {
                strNameOfAssignment = URLEncoder.encode(txtNameOfAssigment.getText().toString().replace("\"", "'"), "utf-8");
                strFileOfAssignment = URLEncoder.encode(pathTwo.replace("\"", "'"), "utf-8");
            } catch (UnsupportedEncodingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            String strUrl = Config.URL + "/service.php?ct=ADDCLASS&UserId=" + appsPref.getString("UserId", "") +
                    "&Name=" + strNameOfAssignment + "&File=" + strFileOfAssignment;

            System.out.println("addNewClass : " + strUrl);
            String strHasil = callURL.call(strUrl);
            return strHasil;
        }
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Toast.makeText(getApplicationContext(), "Add new class succesfully!", Toast.LENGTH_LONG).show();
            progressBar.setVisibility(View.GONE);
            dialog.dismiss();
            m_chosen = null;
            pathTwo  = "";
            getEduspiratorClass(bundle.getString("UserId"));
        }
    }

    private class addNewStudentAssignment extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        protected String doInBackground(String... params) {
            String strFileOfAssignment = null;
            try {
                strFileOfAssignment = URLEncoder.encode(pathTwo.replace("\"", "'"), "utf-8");
            } catch (UnsupportedEncodingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            String strUrl = Config.URL + "/service.php?ct=ADDSTUDENTASSIGNMENT&AssignmentId=" + strAssignmentId +
                    "&ParticipantsId=" + appsPref.getString("UserId", "") + "&File=" + strFileOfAssignment + "&Score=-";

            System.out.println("addNewAssignment : " + strUrl);
            String strHasil = callURL.call(strUrl);
            return strHasil;
        }
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Toast.makeText(getApplicationContext(), "Add assigment succesfully!", Toast.LENGTH_LONG).show();
            progressBar.setVisibility(View.GONE);
            dialog.dismiss();
            getEduspiratorAssignment(bundle.getString("UserId"));
        }
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
            pDialogg = new ProgressDialog(EduspiratorDetailActivity.this);
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
            Toast.makeText(EduspiratorDetailActivity.this, "Download Complete. Check file here : "+imagePath, Toast.LENGTH_LONG).show();
        }
    }

    public void showCustomDialog(String strName) {
        final Dialog dialog = new Dialog(EduspiratorDetailActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_dialog);
        dialog.setCancelable(false);

        TextView lblTitle = (TextView) dialog.findViewById(R.id.lblTitle);
        Button btnNo      = (Button) dialog.findViewById(R.id.btnNo);
        Button btnYes     = (Button) dialog.findViewById(R.id.btnYes);

        lblTitle.setText("Are you sure to call " + strName + "?");
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
                String uri = "tel:" + bundle.getString("PhoneNumber").replace("-","").replace(" ","");
                Intent intent = new Intent(Intent.ACTION_CALL);
                intent.setData(Uri.parse(uri));
                startActivity(intent);
            }
        });

        try {
            dialog.show();
        } catch (Exception e) {}
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
