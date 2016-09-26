package tetra.centre.Adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import org.json.JSONArray;
import org.json.JSONObject;
import de.hdodenhof.circleimageview.CircleImageView;
import tetra.centre.R;
import tetra.centre.SupportClass.Config;
import tetra.centre.SupportClass.FontCache;

public class EduVideoListRecyclerAdapter extends RecyclerView.Adapter {
    private final Context context;
    public JSONArray jArrVideo;

    public EduVideoListRecyclerAdapter(Context context, JSONArray jArrVideo) {
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
            JSONObject jObj = jArrVideo.optJSONObject(position);
            ((EduVideoViewHolder) vh).lblVideoName.setText(jObj.optString("Title"));
            ((EduVideoViewHolder) vh).txtName.setText(jObj.optString("Name"));
            Glide.with(context).load(Config.URL_PICTURES + jObj.optString("Photo")).placeholder(R.drawable.placeholder)
                    .centerCrop().diskCacheStrategy(DiskCacheStrategy.ALL).override(300, 300)
                    .dontAnimate().into(((EduVideoViewHolder) vh).imgProfile);
            String frameVideo = "<html><body style='margin: 0; padding: 0'><iframe width='100%' height='100%' src='" + jObj.optString("Url") + "' frameborder='0' allowfullscreen></iframe></body></html>";
            ((EduVideoViewHolder) vh).webview.setWebViewClient(new WebViewClient(){
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    return false;
                }
            });
            WebSettings webSettings = ((EduVideoViewHolder) vh).webview.getSettings();
            webSettings.setJavaScriptEnabled(true);
            ((EduVideoViewHolder) vh).webview.loadData(frameVideo, "text/html", "utf-8");
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
        private Typeface fontLatoRegular;
        private Typeface fontLatoBold;
        private Typeface fontLatoHeavy;

        public EduVideoViewHolder(Context ctx, View view) {
            super(view);

            webview         = (WebView) view.findViewById(R.id.webview);
            lblVideoName    = (TextView) view.findViewById(R.id.lblVideoName);
            imgProfile      = (CircleImageView) view.findViewById(R.id.imgProfile);
            txtName         = (TextView) view.findViewById(R.id.txtName);
            fontLatoRegular = FontCache.get(ctx, "Lato-Regular");
            fontLatoBold    = FontCache.get(ctx, "Lato-Bold");
            fontLatoHeavy   = FontCache.get(ctx, "Lato-Heavy");

            lblVideoName.setTypeface(fontLatoBold);
            txtName.setTypeface(fontLatoRegular);
        }
    }
}
