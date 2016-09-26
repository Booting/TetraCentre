package tetra.centre;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import tetra.centre.SupportClass.Config;
import tetra.centre.SupportClass.FontCache;
import tetra.centre.SupportClass.TypeFaceSpan;

public class MainActivity extends AppCompatActivity {
    private Typeface fontLatoBold, fontLatoRegular, fontLatoHeavy, fontLatoBlack, fontLatoItalic;
    private Toolbar toolbar;
    private TextView lblInformation, lblEduShare, lblEduShop;
    private RelativeLayout relInformation, relEduShare, relEduShop;
    private SharedPreferences appsPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
        setContentView(R.layout.activity_main);

        initToolbar();

        appsPref 	    = getSharedPreferences(Config.PREF_NAME, Activity.MODE_PRIVATE);
        fontLatoBold    = FontCache.get(MainActivity.this, "Lato-Bold");
        fontLatoRegular = FontCache.get(MainActivity.this, "Lato-Regular");
        fontLatoHeavy   = FontCache.get(MainActivity.this, "Lato-Heavy");
        fontLatoBlack   = FontCache.get(MainActivity.this, "Lato-Black");
        fontLatoItalic  = FontCache.get(MainActivity.this, "Lato-Italic");
        lblInformation  = (TextView) findViewById(R.id.lblInformation);
        lblEduShare     = (TextView) findViewById(R.id.lblEduShare);
        lblEduShop      = (TextView) findViewById(R.id.lblEduShop);
        relInformation  = (RelativeLayout) findViewById(R.id.relInformation);
        relEduShare     = (RelativeLayout) findViewById(R.id.relEduShare);
        relEduShop      = (RelativeLayout) findViewById(R.id.relEduShop);

        lblInformation.setTypeface(fontLatoHeavy);
        lblEduShare.setTypeface(fontLatoHeavy);
        lblEduShop.setTypeface(fontLatoHeavy);

        relInformation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, InformationListActivity.class);
                startActivity(intent);
            }
        });

        relEduShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, EduShareActivity.class);
                startActivity(intent);
            }
        });

        relEduShop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, EdushopActivity.class);
                startActivity(intent);
            }
        });
    }

    private void initToolbar() {
        SpannableString spanToolbar = new SpannableString("TETRA CENTRE");
        spanToolbar.setSpan(new TypeFaceSpan(MainActivity.this, "Lato-Bold"), 0, spanToolbar.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        //Initiate Toolbar/ActionBar
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(spanToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setHomeButtonEnabled(false);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_menu:
                Intent intent = new Intent(MainActivity.this, SignUpFormActivity.class);
                if (appsPref.getString("UserCategory", "").equalsIgnoreCase("1")) {
                    intent.putExtra("Category", "Eduspirator");
                } else {
                    intent.putExtra("Category", "Participants");
                }
                intent.putExtra("IsProfile", "true");
                startActivity(intent);
                break;
        }
        return true;
    }

    @Override
    public void onPause() {
        super.onPause();
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
    }
}
