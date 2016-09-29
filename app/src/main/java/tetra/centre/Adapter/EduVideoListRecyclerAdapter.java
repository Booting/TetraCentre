package tetra.centre.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import java.util.ArrayList;
import java.util.Locale;
import de.hdodenhof.circleimageview.CircleImageView;
import tetra.centre.Model.Video;
import tetra.centre.R;
import tetra.centre.SupportClass.Config;
import tetra.centre.SupportClass.FontCache;

public class EduVideoListRecyclerAdapter extends RecyclerView.Adapter {
    private final Context context;
    private EduVideoListRecyclerAdapterListener listener;
    private SharedPreferences appsPref;
    private ArrayList<Video> mDataset;
    private ArrayList<Video> mCleanCopyDataset;

    public EduVideoListRecyclerAdapter(Context context, ArrayList<Video> dataset, EduVideoListRecyclerAdapterListener mListener) {
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
            final Video video = mDataset.get(position);
            ((EduVideoViewHolder) vh).lblVideoName.setText(video.getTitle());
            ((EduVideoViewHolder) vh).txtName.setText(video.getName());
            Glide.with(context).load(Config.URL_PICTURES + video.getPhoto()).placeholder(R.drawable.placeholder)
                    .centerCrop().diskCacheStrategy(DiskCacheStrategy.ALL).override(300, 300)
                    .dontAnimate().into(((EduVideoViewHolder) vh).imgProfile);
            String frameVideo = "<html><body style='margin: 0; padding: 0'><iframe width='100%' height='100%' src='" + video.getUrl() + "' frameborder='0' allowfullscreen></iframe></body></html>";
            ((EduVideoViewHolder) vh).webview.setWebViewClient(new WebViewClient(){
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    return false;
                }
            });
            WebSettings webSettings = ((EduVideoViewHolder) vh).webview.getSettings();
            webSettings.setJavaScriptEnabled(true);
            ((EduVideoViewHolder) vh).webview.loadData(frameVideo, "text/html", "utf-8");

            ((EduVideoViewHolder) vh).relDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onDeleteClicked(video.getVideoId(), video.getTitle());
                }
            });

            if (video.getUserId().equalsIgnoreCase(appsPref.getString("UserId", ""))) {
                ((EduVideoViewHolder) vh).relDelete.setVisibility(View.VISIBLE);
            } else {
                ((EduVideoViewHolder) vh).relDelete.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        final RecyclerView.ViewHolder vh;

        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.video_list_cell, viewGroup, false);
        vh = new EduVideoViewHolder(viewGroup.getContext(), itemView);

        return vh;
    }

    public static class EduVideoViewHolder extends RecyclerView.ViewHolder {
        protected WebView webview;
        protected TextView lblVideoName, txtName;
        protected CircleImageView imgProfile;
        protected RelativeLayout relDelete;
        private Typeface fontLatoRegular;
        private Typeface fontLatoBold;
        private Typeface fontLatoHeavy;

        public EduVideoViewHolder(Context ctx, View view) {
            super(view);

            webview         = (WebView) view.findViewById(R.id.webview);
            lblVideoName    = (TextView) view.findViewById(R.id.lblVideoName);
            imgProfile      = (CircleImageView) view.findViewById(R.id.imgProfile);
            txtName         = (TextView) view.findViewById(R.id.txtName);
            relDelete       = (RelativeLayout) view.findViewById(R.id.relDelete);
            fontLatoRegular = FontCache.get(ctx, "Lato-Regular");
            fontLatoBold    = FontCache.get(ctx, "Lato-Bold");
            fontLatoHeavy   = FontCache.get(ctx, "Lato-Heavy");

            lblVideoName.setTypeface(fontLatoBold);
            txtName.setTypeface(fontLatoRegular);
        }
    }

    public interface EduVideoListRecyclerAdapterListener {
        public void onDeleteClicked(String strVideoId, String strVideoName);
    }

    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        mDataset = new ArrayList<Video>();
        if (charText.length() == 0) {
            mDataset.addAll(mCleanCopyDataset);
        } else {
            for (Video item : mCleanCopyDataset) {
                if (item.getTitle().toLowerCase(Locale.getDefault()).contains(charText)) {
                    mDataset.add(item);
                }
            }
        }
        notifyDataSetChanged();
    }
}
