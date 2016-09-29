package tetra.centre.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import org.json.JSONArray;
import org.json.JSONObject;
import de.hdodenhof.circleimageview.CircleImageView;
import tetra.centre.EduPaperDetailActivity;
import tetra.centre.R;
import tetra.centre.SupportClass.Config;
import tetra.centre.SupportClass.FontCache;

public class EduPaperListRecyclerAdapter extends RecyclerView.Adapter {
    private final Context context;
    private EduPaperListRecyclerAdapterListener listener;
    private SharedPreferences appsPref;
    public JSONArray jArrVideo;

    public EduPaperListRecyclerAdapter(Context context, JSONArray jArrVideo, EduPaperListRecyclerAdapterListener mListener) {
        this.context   = context;
        this.jArrVideo = jArrVideo;
        listener       = mListener;
        this.appsPref  = context.getSharedPreferences(Config.PREF_NAME, Activity.MODE_PRIVATE);
    }

    @Override
    public int getItemCount() {
        return jArrVideo.length();
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder vh, final int position) {
        if (vh instanceof EduVideoViewHolder) {
            final JSONObject jObj = jArrVideo.optJSONObject(position);
            ((EduVideoViewHolder) vh).lblPaperName.setText(jObj.optString("Title"));
            ((EduVideoViewHolder) vh).txtName.setText(jObj.optString("Name"));
            Glide.with(context).load(Config.URL_PICTURES + jObj.optString("Photo")).placeholder(R.drawable.placeholder)
                    .centerCrop().diskCacheStrategy(DiskCacheStrategy.ALL).override(300, 300)
                    .dontAnimate().into(((EduVideoViewHolder) vh).imgProfile);

            ((EduVideoViewHolder) vh).cardPaperListCell.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, EduPaperDetailActivity.class);
                    intent.putExtra("Title", jObj.optString("Title"));
                    intent.putExtra("Description", jObj.optString("Description"));
                    intent.putExtra("Url", jObj.optString("Url"));
                    context.startActivity(intent);
                }
            });

            ((EduVideoViewHolder) vh).relDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onDeleteClicked(jObj.optString("PaperId"), jObj.optString("Title"));
                }
            });

            if (jObj.optString("UserId").equalsIgnoreCase(appsPref.getString("UserId", ""))) {
                ((EduVideoViewHolder) vh).relDelete.setVisibility(View.VISIBLE);
            } else {
                ((EduVideoViewHolder) vh).relDelete.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        final RecyclerView.ViewHolder vh;

        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.paper_list_cell, viewGroup, false);
        vh = new EduVideoViewHolder(viewGroup.getContext(), itemView);

        return vh;
    }

    public static class EduVideoViewHolder extends RecyclerView.ViewHolder {
        protected CardView cardPaperListCell;
        protected ImageView imgPaper;
        protected TextView lblPaperName, txtName;
        protected CircleImageView imgProfile;
        protected RelativeLayout relDelete;
        private Typeface fontLatoRegular;
        private Typeface fontLatoBold;
        private Typeface fontLatoHeavy;

        public EduVideoViewHolder(Context ctx, View view) {
            super(view);

            cardPaperListCell = (CardView) view.findViewById(R.id.cardPaperListCell);
            imgPaper          = (ImageView) view.findViewById(R.id.imgPaper);
            lblPaperName      = (TextView) view.findViewById(R.id.lblPaperName);
            imgProfile        = (CircleImageView) view.findViewById(R.id.imgProfile);
            txtName           = (TextView) view.findViewById(R.id.txtName);
            relDelete         = (RelativeLayout) view.findViewById(R.id.relDelete);
            fontLatoRegular   = FontCache.get(ctx, "Lato-Regular");
            fontLatoBold      = FontCache.get(ctx, "Lato-Bold");
            fontLatoHeavy     = FontCache.get(ctx, "Lato-Heavy");

            lblPaperName.setTypeface(fontLatoBold);
            txtName.setTypeface(fontLatoRegular);
        }
    }

    public interface EduPaperListRecyclerAdapterListener {
        public void onDeleteClicked(String strPaperId, String strPaperName);
    }
}
