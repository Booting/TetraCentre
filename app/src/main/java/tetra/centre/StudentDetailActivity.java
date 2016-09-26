package tetra.centre;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;

import de.hdodenhof.circleimageview.CircleImageView;
import tetra.centre.SupportClass.FontCache;
import tetra.centre.SupportClass.TypeFaceSpan;

public class StudentDetailActivity extends AppCompatActivity {
    private Typeface fontLatoBold, fontLatoRegular, fontLatoHeavy, fontLatoBlack, fontLatoItalic;
    private Toolbar toolbar;
    private CircleImageView imgProfile;
    private TextView lblName, txtName, lblDateOfBirth, txtDateOfBirth, lblSchool, txtSchool,
            lblNumberOfPhone, txtNumberOfPhone, lblEmail, txtEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
        setContentView(R.layout.activity_student_detail);

        initToolbar();

        fontLatoBold      = FontCache.get(StudentDetailActivity.this, "Lato-Bold");
        fontLatoRegular   = FontCache.get(StudentDetailActivity.this, "Lato-Regular");
        fontLatoHeavy     = FontCache.get(StudentDetailActivity.this, "Lato-Heavy");
        fontLatoBlack     = FontCache.get(StudentDetailActivity.this, "Lato-Black");
        fontLatoItalic    = FontCache.get(StudentDetailActivity.this, "Lato-Italic");
        imgProfile        = (CircleImageView) findViewById(R.id.imgProfile);
        lblName           = (TextView) findViewById(R.id.lblName);
        txtName           = (TextView) findViewById(R.id.txtName);
        lblDateOfBirth    = (TextView) findViewById(R.id.lblDateOfBirth);
        txtDateOfBirth    = (TextView) findViewById(R.id.txtDateOfBirth);
        lblSchool         = (TextView) findViewById(R.id.lblSchool);
        txtSchool         = (TextView) findViewById(R.id.txtSchool);
        lblNumberOfPhone  = (TextView) findViewById(R.id.lblNumberOfPhone);
        txtNumberOfPhone  = (TextView) findViewById(R.id.txtNumberOfPhone);
        lblEmail          = (TextView) findViewById(R.id.lblEmail);
        txtEmail          = (TextView) findViewById(R.id.txtEmail);

        lblName.setTypeface(fontLatoBold);
        txtName.setTypeface(fontLatoRegular);
        lblDateOfBirth.setTypeface(fontLatoBold);
        txtDateOfBirth.setTypeface(fontLatoRegular);
        lblSchool.setTypeface(fontLatoBold);
        txtSchool.setTypeface(fontLatoRegular);
        lblNumberOfPhone.setTypeface(fontLatoBold);
        txtNumberOfPhone.setTypeface(fontLatoRegular);
        lblEmail.setTypeface(fontLatoBold);
        txtEmail.setTypeface(fontLatoRegular);
    }

    private void initToolbar() {
        SpannableString spanToolbar = new SpannableString(getIntent().getStringExtra("StudentName"));
        spanToolbar.setSpan(new TypeFaceSpan(StudentDetailActivity.this, "Lato-Bold"), 0, spanToolbar.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

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
}
