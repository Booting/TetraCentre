package tetra.centre.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import org.json.JSONArray;
import org.json.JSONObject;
import de.hdodenhof.circleimageview.CircleImageView;
import tetra.centre.EduMediaDetailActivity;
import tetra.centre.R;
import tetra.centre.SupportClass.Config;
import tetra.centre.SupportClass.FontCache;

public class EduMediaListRecyclerAdapter extends RecyclerView.Adapter {
    private final Context context;
    public JSONArray jArrVideo;

    public EduMediaListRecyclerAdapter(Context context, JSONArray jArrVideo) {
        this.context   = context;
        this.jArrVideo = jArrVideo;
    }

    @Override
    public int getItemCount() {
        return jArrVideo.length();
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder vh, final int position) {
        if (vh instanceof EduVideoViewHolder) {
            final JSONObject jObj = jArrVideo.optJSONObject(position);
            ((EduVideoViewHolder) vh).lblMediaName.setText(jObj.optString("Title"));
            ((EduVideoViewHolder) vh).txtName.setText(jObj.optString("Name"));
            Glide.with(context).load(Config.URL_PICTURES + jObj.optString("Photo")).placeholder(R.drawable.placeholder)
                    .centerCrop().diskCacheStrategy(DiskCacheStrategy.ALL).override(300, 300)
                    .dontAnimate().into(((EduVideoViewHolder) vh).imgProfile);
            Glide.with(context).load(Config.URL_PICTURES + jObj.optString("Url")).placeholder(R.drawable.placeholder)
                    .centerCrop().diskCacheStrategy(DiskCacheStrategy.ALL).override(300, 300)
                    .dontAnimate().into(((EduVideoViewHolder) vh).imgMedia);

            ((EduVideoViewHolder) vh).cardMediaListCell.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, EduMediaDetailActivity.class);
                    intent.putExtra("Title", jObj.optString("Title"));
                    intent.putExtra("Description", jObj.optString("Description"));
                    intent.putExtra("Url", jObj.optString("Url"));
                    context.startActivity(intent);
                }
            });
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        final RecyclerView.ViewHolder vh;

        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.media_list_cell, viewGroup, false);
        vh = new EduVideoViewHolder(viewGroup.getContext(), itemView);

        return vh;
    }

    public static class EduVideoViewHolder extends RecyclerView.ViewHolder {
        protected CardView cardMediaListCell;
        protected ImageView imgMedia;
        protected TextView lblMediaName, txtName;
        protected CircleImageView imgProfile;
        private Typeface fontLatoRegular;
        private Typeface fontLatoBold;
        private Typeface fontLatoHeavy;

        public EduVideoViewHolder(Context ctx, View view) {
            super(view);

            cardMediaListCell = (CardView) view.findViewById(R.id.cardMediaListCell);
            imgMedia          = (ImageView) view.findViewById(R.id.imgMedia);
            lblMediaName      = (TextView) view.findViewById(R.id.lblMediaName);
            imgProfile        = (CircleImageView) view.findViewById(R.id.imgProfile);
            txtName           = (TextView) view.findViewById(R.id.txtName);
            fontLatoRegular   = FontCache.get(ctx, "Lato-Regular");
            fontLatoBold      = FontCache.get(ctx, "Lato-Bold");
            fontLatoHeavy     = FontCache.get(ctx, "Lato-Heavy");

            lblMediaName.setTypeface(fontLatoBold);
            txtName.setTypeface(fontLatoRegular);
        }
    }
}
