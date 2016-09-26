package tetra.centre;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import tetra.centre.SupportClass.Config;
import tetra.centre.SupportClass.FontCache;

public class SignInActivity extends Activity {
    private Typeface fontLatoBold, fontLatoRegular, fontLatoHeavy, fontLatoBlack, fontLatoItalic;
    private TextView lblSignIn, lblForgetPassword, lblSignUp;
    private EditText txtUsername, txtPassword;
    private Button btnSignIn;
    private RequestQueue queue;
    private ProgressDialog pDialog;
    private SharedPreferences appsPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_sign_in);

        appsPref 	       = getSharedPreferences(Config.PREF_NAME, Activity.MODE_PRIVATE);
        queue    	       = Volley.newRequestQueue(this);
        fontLatoBold       = FontCache.get(SignInActivity.this, "Lato-Bold");
        fontLatoRegular    = FontCache.get(SignInActivity.this, "Lato-Regular");
        fontLatoHeavy      = FontCache.get(SignInActivity.this, "Lato-Heavy");
        fontLatoBlack      = FontCache.get(SignInActivity.this, "Lato-Black");
        fontLatoItalic     = FontCache.get(SignInActivity.this, "Lato-Italic");
        lblSignIn          = (TextView) findViewById(R.id.lblSignIn);
        txtUsername        = (EditText) findViewById(R.id.txtUsername);
        txtPassword        = (EditText) findViewById(R.id.txtPassword);
        lblForgetPassword  = (TextView) findViewById(R.id.lblForgetPassword);
        lblSignUp          = (TextView) findViewById(R.id.lblSignUp);
        btnSignIn          = (Button) findViewById(R.id.btnSignIn);

        lblSignIn.setTypeface(fontLatoBold);
        txtUsername.setTypeface(fontLatoRegular);
        txtPassword.setTypeface(fontLatoRegular);
        lblForgetPassword.setTypeface(fontLatoRegular);
        lblSignUp.setTypeface(fontLatoRegular);
        btnSignIn.setTypeface(fontLatoHeavy);

        lblSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignInActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
        });

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (txtUsername.getText().length()==0) {
                    txtUsername.setError("Username is required!");
                } else if (txtPassword.getText().length()==0) {
                    txtPassword.setError("Password is required!");
                } else {
                    getLogin();
                }
            }
        });

        pDialog = new ProgressDialog(SignInActivity.this);
        pDialog.setMessage("Working...");
        pDialog.setCancelable(false);
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
}
