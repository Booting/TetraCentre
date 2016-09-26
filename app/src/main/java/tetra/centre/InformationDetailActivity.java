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
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import tetra.centre.SupportClass.Config;
import tetra.centre.SupportClass.FontCache;
import tetra.centre.SupportClass.TypeFaceSpan;

public class InformationDetailActivity extends AppCompatActivity {
    private Typeface fontLatoBold, fontLatoRegular, fontLatoHeavy, fontLatoBlack, fontLatoItalic;
    private Toolbar toolbar;
    private ImageView imgInfo;
    private TextView txtInfoDescription, lblInfoDescription;
    private WebView webView;
    private ProgressBar progressBusy;
    private RelativeLayout relWebview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
        setContentView(R.layout.activity_information_detail);

        initToolbar();

        fontLatoBold       = FontCache.get(InformationDetailActivity.this, "Lato-Bold");
        fontLatoRegular    = FontCache.get(InformationDetailActivity.this, "Lato-Regular");
        fontLatoHeavy      = FontCache.get(InformationDetailActivity.this, "Lato-Heavy");
        fontLatoBlack      = FontCache.get(InformationDetailActivity.this, "Lato-Black");
        fontLatoItalic     = FontCache.get(InformationDetailActivity.this, "Lato-Italic");
        imgInfo            = (ImageView) findViewById(R.id.imgInfo);
        txtInfoDescription = (TextView) findViewById(R.id.txtInfoDescription);
        webView            = (WebView) findViewById(R.id.webView);
        progressBusy       = (ProgressBar) findViewById(R.id.progressBusy);
        relWebview         = (RelativeLayout) findViewById(R.id.relWebview);
        lblInfoDescription = (TextView) findViewById(R.id.lblInfoDescription);

        lblInfoDescription.setTypeface(fontLatoHeavy);
        txtInfoDescription.setTypeface(fontLatoRegular);
        txtInfoDescription.setText(getIntent().getStringExtra("Description"));

        if (getIntent().getStringExtra("FileName").contains("pdf")) {
            relWebview.setVisibility(View.VISIBLE);
            imgInfo.setVisibility(View.GONE);
            webView.setWebViewClient(new myWebClient());
            webView.getSettings().setJavaScriptEnabled(true);
            webView.getSettings().setBuiltInZoomControls(true);
            webView.loadUrl("http://docs.google.com/gview?embedded=true&url=" + Config.URL_MATERIALS + getIntent().getStringExtra("FileName"));
        } else {
            relWebview.setVisibility(View.GONE);
            imgInfo.setVisibility(View.VISIBLE);
            Glide.with(this).load(Config.URL_MATERIALS + getIntent().getStringExtra("FileName"))
                    .centerCrop().diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.drawable.placeholder).dontAnimate().into(imgInfo);
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
        SpannableString spanToolbar = new SpannableString("Information - "+getIntent().getStringExtra("Title"));
        spanToolbar.setSpan(new TypeFaceSpan(InformationDetailActivity.this, "Lato-Bold"), 0, spanToolbar.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

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
