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
import java.util.ArrayList;

import tetra.centre.InformationDetailActivity;
import tetra.centre.R;
import tetra.centre.SupportClass.Config;
import tetra.centre.SupportClass.FontCache;

public class InformationListRecyclerAdapter extends RecyclerView.Adapter {
    private final Context context;
    public JSONArray jArrInformation;

    private final String UserId      = "UserId";
    private final String Name        = "Name";
    private final String Description = "Description";
    private final String FileName    = "FileName";

    private ArrayList<String> arrayUserId = new ArrayList<String>(),
            arrayName        = new ArrayList<String>(),
            arrayDescription = new ArrayList<String>(),
            arrayFileName    = new ArrayList<String>();

    public InformationListRecyclerAdapter(Context context, JSONArray jArrInformation) {
        this.context         = context;
        this.jArrInformation = jArrInformation;

        for (int i = 0; i < this.jArrInformation.length(); i++) {
            arrayUserId.add(this.jArrInformation.optJSONObject(i).optString(UserId));
            arrayName.add(this.jArrInformation.optJSONObject(i).optString(Name));
            arrayDescription.add(this.jArrInformation.optJSONObject(i).optString(Description));
            arrayFileName.add(this.jArrInformation.optJSONObject(i).optString(FileName));
        }
    }

    @Override
    public int getItemCount() {
        return jArrInformation.length();
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder vh, final int position) {
        if (vh instanceof InformationViewHolder) {
            if (arrayFileName.get(position).contains("pdf")) {
                Glide.with(context).load("").placeholder(R.drawable.pdf).
                        dontAnimate().into(((InformationViewHolder) vh).imgInformation);
            } else {
                Glide.with(context).load(Config.URL_MATERIALS + arrayFileName.get(position)).placeholder(R.drawable.placeholder)
                        .centerCrop().diskCacheStrategy(DiskCacheStrategy.ALL).override(300, 300)
                        .dontAnimate().into(((InformationViewHolder) vh).imgInformation);
            }
            ((InformationViewHolder) vh).lblInformationName.setText(arrayName.get(position));

            ((InformationViewHolder) vh).cardInformationListCell.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, InformationDetailActivity.class);
                    intent.putExtra("Title", ((InformationViewHolder) vh).lblInformationName.getText().toString());
                    intent.putExtra("Description", arrayDescription.get(position));
                    intent.putExtra("FileName", arrayFileName.get(position));
                    context.startActivity(intent);
                }
            });
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        final RecyclerView.ViewHolder vh;

        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.information_list_cell, viewGroup, false);
        vh = new InformationViewHolder(viewGroup.getContext(), itemView);

        return vh;
    }

    public static class InformationViewHolder extends RecyclerView.ViewHolder {
        protected CardView cardInformationListCell;
        protected ImageView imgInformation;
        protected TextView lblInformationName;
        private Typeface fontLatoRegular;
        private Typeface fontLatoBold;
        private Typeface fontLatoHeavy;

        public InformationViewHolder(Context ctx, View view) {
            super(view);

            cardInformationListCell = (CardView) view.findViewById(R.id.cardInformationListCell);
            imgInformation          = (ImageView) view.findViewById(R.id.imgInformation);
            lblInformationName      = (TextView) view.findViewById(R.id.lblInformationName);
            fontLatoRegular         = FontCache.get(ctx, "Lato-Regular");
            fontLatoBold            = FontCache.get(ctx, "Lato-Bold");
            fontLatoHeavy           = FontCache.get(ctx, "Lato-Heavy");

            lblInformationName.setTypeface(fontLatoHeavy);
        }
    }
}
