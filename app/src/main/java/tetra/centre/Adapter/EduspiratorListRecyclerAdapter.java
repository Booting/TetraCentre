package tetra.centre.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import org.json.JSONArray;
import java.util.ArrayList;
import de.hdodenhof.circleimageview.CircleImageView;
import tetra.centre.EduspiratorDetailActivity;
import tetra.centre.R;
import tetra.centre.SupportClass.Config;
import tetra.centre.SupportClass.FontCache;

public class EduspiratorListRecyclerAdapter extends RecyclerView.Adapter {
    private final Context context;
    public JSONArray jArrEduspirator;

    private final String UserId          = "UserId";
    private final String Name            = "Name";
    private final String DateOfBirth     = "DateOfBirth";
    private final String Address         = "Address";
    private final String School          = "School";
    private final String PhoneNumber     = "PhoneNumber";
    private final String Email           = "Email";
    private final String TitleOfMaterial = "TitleOfMaterial";
    private final String Material        = "Material";
    private final String Photo           = "Photo";

    private ArrayList<String> arrayUserId = new ArrayList<String>(),
            arrayName            = new ArrayList<String>(),
            arrayDateOfBirth     = new ArrayList<String>(),
            arrayAddress         = new ArrayList<String>(),
            arraySchool          = new ArrayList<String>(),
            arrayPhoneNumber     = new ArrayList<String>(),
            arrayEmail           = new ArrayList<String>(),
            arrayTitleOfMaterial = new ArrayList<String>(),
            arrayMaterial        = new ArrayList<String>(),
            arrayPhoto           = new ArrayList<String>();

    public EduspiratorListRecyclerAdapter(Context context, JSONArray jArrEduspirator) {
        this.context         = context;
        this.jArrEduspirator = jArrEduspirator;

        for (int i = 0; i < this.jArrEduspirator.length(); i++) {
            arrayUserId.add(this.jArrEduspirator.optJSONObject(i).optString(UserId));
            arrayName.add(this.jArrEduspirator.optJSONObject(i).optString(Name));
            arrayDateOfBirth.add(this.jArrEduspirator.optJSONObject(i).optString(DateOfBirth));
            arrayAddress.add(this.jArrEduspirator.optJSONObject(i).optString(Address));
            arraySchool.add(this.jArrEduspirator.optJSONObject(i).optString(School));
            arrayAddress.add(this.jArrEduspirator.optJSONObject(i).optString(Address));
            arrayPhoneNumber.add(this.jArrEduspirator.optJSONObject(i).optString(PhoneNumber));
            arrayEmail.add(this.jArrEduspirator.optJSONObject(i).optString(Email));
            arrayTitleOfMaterial.add(this.jArrEduspirator.optJSONObject(i).optString(TitleOfMaterial));
            arrayMaterial.add(this.jArrEduspirator.optJSONObject(i).optString(Material));
            arrayPhoto.add(this.jArrEduspirator.optJSONObject(i).optString(Photo));
        }
    }

    @Override
    public int getItemCount() {
        return jArrEduspirator.length();
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder vh, final int position) {
        if (vh instanceof EduspiratorViewHolder) {
            ((EduspiratorViewHolder) vh).lblEduspirator.setText(arrayName.get(position));
            ((EduspiratorViewHolder) vh).relEduspiratorCell.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, EduspiratorDetailActivity.class);
                    intent.putExtra("UserId", arrayUserId.get(position));
                    intent.putExtra("Name", arrayName.get(position));
                    intent.putExtra("DateOfBirth", arrayDateOfBirth.get(position));
                    intent.putExtra("Address", arrayAddress.get(position));
                    intent.putExtra("School", arraySchool.get(position));
                    intent.putExtra("PhoneNumber", arrayPhoneNumber.get(position));
                    intent.putExtra("Email", arrayEmail.get(position));
                    intent.putExtra("TitleOfMaterial", arrayTitleOfMaterial.get(position));
                    intent.putExtra("Material", arrayMaterial.get(position));
                    intent.putExtra("Photo", arrayPhoto.get(position));
                    context.startActivity(intent);
                }
            });

            Glide.with(context).load(Config.URL_PICTURES + arrayPhoto.get(position)).placeholder(R.drawable.placeholder)
                    .centerCrop().diskCacheStrategy(DiskCacheStrategy.ALL).override(100, 100)
                    .dontAnimate().into(((EduspiratorViewHolder) vh).imgProfile);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        final RecyclerView.ViewHolder vh;

        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.eduspirator_list_cell, viewGroup, false);
        vh = new EduspiratorViewHolder(viewGroup.getContext(), itemView);

        return vh;
    }

    public static class EduspiratorViewHolder extends RecyclerView.ViewHolder {
        protected TextView lblEduspirator;
        protected CircleImageView imgProfile;
        private Typeface fontLatoRegular;
        private Typeface fontLatoBold;
        private Typeface fontLatoHeavy;
        private RelativeLayout relEduspiratorCell;

        public EduspiratorViewHolder(Context ctx, View view) {
            super(view);

            lblEduspirator     = (TextView) view.findViewById(R.id.lblEduspirator);
            fontLatoRegular    = FontCache.get(ctx, "Lato-Regular");
            fontLatoBold       = FontCache.get(ctx, "Lato-Bold");
            fontLatoHeavy      = FontCache.get(ctx, "Lato-Heavy");
            relEduspiratorCell = (RelativeLayout) view.findViewById(R.id.relEduspiratorCell);
            imgProfile         = (CircleImageView) view.findViewById(R.id.imgProfile);

            lblEduspirator.setTypeface(fontLatoRegular);
        }
    }
}
