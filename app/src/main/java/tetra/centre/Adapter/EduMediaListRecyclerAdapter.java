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
import java.util.ArrayList;
import java.util.Locale;
import de.hdodenhof.circleimageview.CircleImageView;
import tetra.centre.EduMediaDetailActivity;
import tetra.centre.Model.Media;
import tetra.centre.R;
import tetra.centre.SupportClass.Config;
import tetra.centre.SupportClass.FontCache;

public class EduMediaListRecyclerAdapter extends RecyclerView.Adapter {
    private final Context context;
    private EduMediaListRecyclerAdapterListener listener;
    private SharedPreferences appsPref;
    private ArrayList<Media> mDataset;
    private ArrayList<Media> mCleanCopyDataset;

    public EduMediaListRecyclerAdapter(Context context, ArrayList<Media> dataset, EduMediaListRecyclerAdapterListener mListener) {
        this.context      = context;
        listener          = mListener;
        this.appsPref     = context.getSharedPreferences(Config.PREF_NAME, Activity.MODE_PRIVATE);
        mDataset          = dataset;
        mCleanCopyDataset = mDataset;
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder vh, final int position) {
        if (vh instanceof EduVideoViewHolder) {
            final Media media = mDataset.get(position);
            ((EduVideoViewHolder) vh).lblMediaName.setText(media.getTitle());
            ((EduVideoViewHolder) vh).txtName.setText(media.getName());
            Glide.with(context).load(Config.URL_PICTURES + media.getPhoto()).placeholder(R.drawable.placeholder)
                    .centerCrop().diskCacheStrategy(DiskCacheStrategy.ALL).override(300, 300)
                    .dontAnimate().into(((EduVideoViewHolder) vh).imgProfile);
            Glide.with(context).load(Config.URL_PICTURES + media.getUrl()).placeholder(R.drawable.placeholder)
                    .centerCrop().diskCacheStrategy(DiskCacheStrategy.ALL).override(300, 300)
                    .dontAnimate().into(((EduVideoViewHolder) vh).imgMedia);

            ((EduVideoViewHolder) vh).cardMediaListCell.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, EduMediaDetailActivity.class);
                    intent.putExtra("Title", media.getTitle());
                    intent.putExtra("Description", media.getDescription());
                    intent.putExtra("Url", media.getUrl());
                    context.startActivity(intent);
                }
            });

            ((EduVideoViewHolder) vh).relDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onDeleteClicked(media.getMediaId(), media.getTitle());
                }
            });

            if (media.getUserId().equalsIgnoreCase(appsPref.getString("UserId", ""))) {
                ((EduVideoViewHolder) vh).relDelete.setVisibility(View.VISIBLE);
            } else {
                ((EduVideoViewHolder) vh).relDelete.setVisibility(View.GONE);
            }
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
        protected RelativeLayout relDelete;
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
            relDelete         = (RelativeLayout) view.findViewById(R.id.relDelete);
            fontLatoRegular   = FontCache.get(ctx, "Lato-Regular");
            fontLatoBold      = FontCache.get(ctx, "Lato-Bold");
            fontLatoHeavy     = FontCache.get(ctx, "Lato-Heavy");

            lblMediaName.setTypeface(fontLatoBold);
            txtName.setTypeface(fontLatoRegular);
        }
    }

    public interface EduMediaListRecyclerAdapterListener {
        public void onDeleteClicked(String strMediaId, String strMediaName);
    }

    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        mDataset = new ArrayList<Media>();
        if (charText.length() == 0) {
            mDataset.addAll(mCleanCopyDataset);
        } else {
            for (Media item : mCleanCopyDataset) {
                if (item.getTitle().toLowerCase(Locale.getDefault()).contains(charText)) {
                    mDataset.add(item);
                }
            }
        }
        notifyDataSetChanged();
    }
}
