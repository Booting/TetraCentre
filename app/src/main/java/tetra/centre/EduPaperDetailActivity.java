package tetra.centre;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;
import tetra.centre.SupportClass.Config;
import tetra.centre.SupportClass.FontCache;
import tetra.centre.SupportClass.TypeFaceSpan;

public class EduPaperDetailActivity extends AppCompatActivity {
    private Typeface fontLatoBold, fontLatoRegular, fontLatoHeavy, fontLatoBlack, fontLatoItalic;
    private Toolbar toolbar;
    private WebView webView;
    private TextView txtDescription;
    private ProgressBar progressBusy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
        setContentView(R.layout.activity_edupaper_detail);

        initToolbar();

        fontLatoBold    = FontCache.get(EduPaperDetailActivity.this, "Lato-Bold");
        fontLatoRegular = FontCache.get(EduPaperDetailActivity.this, "Lato-Regular");
        fontLatoHeavy   = FontCache.get(EduPaperDetailActivity.this, "Lato-Heavy");
        fontLatoBlack   = FontCache.get(EduPaperDetailActivity.this, "Lato-Black");
        fontLatoItalic  = FontCache.get(EduPaperDetailActivity.this, "Lato-Italic");
        webView         = (WebView) findViewById(R.id.webView);
        txtDescription  = (TextView) findViewById(R.id.txtDescription);
        progressBusy    = (ProgressBar) findViewById(R.id.progressBusy);

        txtDescription.setTypeface(fontLatoRegular);
        txtDescription.setText(getIntent().getStringExtra("Description"));

        webView.setWebViewClient(new myWebClient());
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.loadUrl("http://docs.google.com/gview?embedded=true&url=" + Config.URL_MATERIALS + getIntent().getStringExtra("Url"));
        System.out.println("HALO : "+Config.URL_MATERIALS + getIntent().getStringExtra("Url"));
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
        SpannableString spanToolbar = new SpannableString("EduPaper - "+getIntent().getStringExtra("Title"));
        spanToolbar.setSpan(new TypeFaceSpan(EduPaperDetailActivity.this, "Lato-Bold"), 0, spanToolbar.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

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
