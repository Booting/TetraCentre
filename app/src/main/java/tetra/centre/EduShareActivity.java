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
import android.widget.LinearLayout;
import android.widget.TextView;
import tetra.centre.SupportClass.FontCache;
import tetra.centre.SupportClass.TypeFaceSpan;

public class EduShareActivity extends AppCompatActivity {
    private Typeface fontLatoBold, fontLatoRegular, fontLatoHeavy, fontLatoBlack, fontLatoItalic;
    private Toolbar toolbar;
    private TextView lblEduClass, lblEduVideo, lblEduPaper, lblEduMedia;
    private LinearLayout linEduClass, linEduVideo, linEduMedia, linEduPaper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
        setContentView(R.layout.activity_edushare);

        initToolbar();

        fontLatoBold    = FontCache.get(EduShareActivity.this, "Lato-Bold");
        fontLatoRegular = FontCache.get(EduShareActivity.this, "Lato-Regular");
        fontLatoHeavy   = FontCache.get(EduShareActivity.this, "Lato-Heavy");
        fontLatoBlack   = FontCache.get(EduShareActivity.this, "Lato-Black");
        fontLatoItalic  = FontCache.get(EduShareActivity.this, "Lato-Italic");
        lblEduClass     = (TextView) findViewById(R.id.lblEduClass);
        lblEduVideo     = (TextView) findViewById(R.id.lblEduVideo);
        lblEduPaper     = (TextView) findViewById(R.id.lblEduPaper);
        lblEduMedia     = (TextView) findViewById(R.id.lblEduMedia);
        linEduClass     = (LinearLayout) findViewById(R.id.linEduClass);
        linEduVideo     = (LinearLayout) findViewById(R.id.linEduVideo);
        linEduMedia     = (LinearLayout) findViewById(R.id.linEduMedia);
        linEduPaper     = (LinearLayout) findViewById(R.id.linEduPaper);

        lblEduClass.setTypeface(fontLatoRegular);
        lblEduVideo.setTypeface(fontLatoRegular);
        lblEduPaper.setTypeface(fontLatoRegular);
        lblEduMedia.setTypeface(fontLatoRegular);

        linEduClass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EduShareActivity.this, EduspiratorListActivity.class);
                startActivity(intent);
            }
        });
        linEduVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EduShareActivity.this, EduVideoListActivity.class);
                startActivity(intent);
            }
        });
        linEduMedia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EduShareActivity.this, EduMediaListActivity.class);
                startActivity(intent);
            }
        });
        linEduPaper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EduShareActivity.this, EduPaperListActivity.class);
                startActivity(intent);
            }
        });
    }

    private void initToolbar() {
        SpannableString spanToolbar = new SpannableString("EduShare");
        spanToolbar.setSpan(new TypeFaceSpan(EduShareActivity.this, "Lato-Bold"), 0, spanToolbar.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

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
