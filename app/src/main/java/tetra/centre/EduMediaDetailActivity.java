package tetra.centre;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import tetra.centre.SupportClass.Config;
import tetra.centre.SupportClass.FontCache;
import tetra.centre.SupportClass.TypeFaceSpan;

public class EduMediaDetailActivity extends AppCompatActivity {
    private Typeface fontLatoBold, fontLatoRegular, fontLatoHeavy, fontLatoBlack, fontLatoItalic;
    private Toolbar toolbar;
    private ImageView imgMedia;
    private TextView txtDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
        setContentView(R.layout.activity_edumedia_detail);

        initToolbar();

        fontLatoBold    = FontCache.get(EduMediaDetailActivity.this, "Lato-Bold");
        fontLatoRegular = FontCache.get(EduMediaDetailActivity.this, "Lato-Regular");
        fontLatoHeavy   = FontCache.get(EduMediaDetailActivity.this, "Lato-Heavy");
        fontLatoBlack   = FontCache.get(EduMediaDetailActivity.this, "Lato-Black");
        fontLatoItalic  = FontCache.get(EduMediaDetailActivity.this, "Lato-Italic");
        imgMedia        = (ImageView) findViewById(R.id.imgMedia);
        txtDescription  = (TextView) findViewById(R.id.txtDescription);

        txtDescription.setTypeface(fontLatoRegular);
        txtDescription.setText(getIntent().getStringExtra("Description"));

        Glide.with(this).load(Config.URL_PICTURES + getIntent().getStringExtra("Url"))
                .centerCrop().diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.drawable.placeholder).dontAnimate().into(imgMedia);
    }

    private void initToolbar() {
        SpannableString spanToolbar = new SpannableString("EduMedia - "+getIntent().getStringExtra("Title"));
        spanToolbar.setSpan(new TypeFaceSpan(EduMediaDetailActivity.this, "Lato-Bold"), 0, spanToolbar.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

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
