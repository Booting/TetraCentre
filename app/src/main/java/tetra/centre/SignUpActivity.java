package tetra.centre;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;
import tetra.centre.SupportClass.FontCache;

public class SignUpActivity extends Activity {
    private Typeface fontLatoBold, fontLatoRegular, fontLatoHeavy, fontLatoBlack, fontLatoItalic;
    private TextView lblSignUp, lblEduspirator, lblParticipants;
    private LinearLayout linEduspirator, linParticipants;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
        setContentView(R.layout.activity_sign_up);

        fontLatoBold    = FontCache.get(SignUpActivity.this, "Lato-Bold");
        fontLatoRegular = FontCache.get(SignUpActivity.this, "Lato-Regular");
        fontLatoHeavy   = FontCache.get(SignUpActivity.this, "Lato-Heavy");
        fontLatoBlack   = FontCache.get(SignUpActivity.this, "Lato-Black");
        fontLatoItalic  = FontCache.get(SignUpActivity.this, "Lato-Italic");
        lblSignUp       = (TextView) findViewById(R.id.lblSignUp);
        lblEduspirator  = (TextView) findViewById(R.id.lblEduspirator);
        lblParticipants = (TextView) findViewById(R.id.lblParticipants);
        linEduspirator  = (LinearLayout) findViewById(R.id.linEduspirator);
        linParticipants = (LinearLayout) findViewById(R.id.linParticipants);

        lblSignUp.setTypeface(fontLatoBold);
        lblEduspirator.setTypeface(fontLatoRegular);
        lblParticipants.setTypeface(fontLatoRegular);

        linEduspirator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignUpActivity.this, SignUpFormActivity.class);
                intent.putExtra("Category", "Eduspirator");
                intent.putExtra("IsProfile", "false");
                startActivity(intent);
            }
        });

        linParticipants.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignUpActivity.this, SignUpFormActivity.class);
                intent.putExtra("Category", "Participants");
                intent.putExtra("IsProfile", "false");
                startActivity(intent);
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
    }
}
