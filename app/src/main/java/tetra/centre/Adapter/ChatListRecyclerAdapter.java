package tetra.centre.Adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import de.hdodenhof.circleimageview.CircleImageView;
import tetra.centre.R;
import tetra.centre.SupportClass.Config;
import tetra.centre.SupportClass.FontCache;

public class ChatListRecyclerAdapter extends RecyclerView.Adapter {
    private final Context context;
    public JSONArray jArrChat;
    private ChatListRecyclerAdapterListener listener;
    private boolean blnCall = false;

    private final String UserId       = "UserId";
    private final String UserCategory = "UserCategory";
    private final String Name         = "Name";
    private final String Photo        = "Photo";

    private ArrayList<String> arrayUserId = new ArrayList<String>(),
            arrayUserCategory = new ArrayList<String>(),
            arrayName         = new ArrayList<String>(),
            arrayPhoto        = new ArrayList<String>();

    public ChatListRecyclerAdapter(Context context, JSONArray jArrChat, boolean blnCall, ChatListRecyclerAdapterListener listener) {
        this.context  = context;
        this.jArrChat = jArrChat;
        this.listener = listener;
        this.blnCall  = blnCall;

        for (int i = 0; i < this.jArrChat.length(); i++) {
            JSONObject jObjClass = this.jArrChat.optJSONObject(i);
            if (jObjClass.optString(UserCategory).equalsIgnoreCase("2")) {
                arrayUserId.add(jObjClass.optString(UserId));
                arrayName.add(jObjClass.optString(Name));
                arrayPhoto.add(jObjClass.optString(Photo));
            }
        }
    }

    @Override
    public int getItemCount() {
        return arrayUserId.size();
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder vh, final int position) {
        if (vh instanceof ClassViewHolder) {
            ((ClassViewHolder) vh).lblClass.setText(arrayName.get(position));
            ((ClassViewHolder) vh).btnApply.setVisibility(View.GONE);
            ((ClassViewHolder) vh).imgProfile.setVisibility(View.VISIBLE);
            Glide.with(context).load(Config.URL_PICTURES + arrayPhoto.get(position)).placeholder(R.drawable.placeholder)
                    .centerCrop().diskCacheStrategy(DiskCacheStrategy.ALL).override(300, 300)
                    .dontAnimate().into(((ClassViewHolder) vh).imgProfile);

            ((ClassViewHolder) vh).relChatCell.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onListClicked(arrayUserId.get(position), arrayName.get(position), arrayPhoto.get(position), blnCall);
                }
            });
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        final RecyclerView.ViewHolder vh;

        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.class_list_cell, viewGroup, false);
        vh = new ClassViewHolder(viewGroup.getContext(), itemView);

        return vh;
    }

    public static class ClassViewHolder extends RecyclerView.ViewHolder {
        protected TextView lblClass;
        private Typeface fontLatoRegular;
        private Typeface fontLatoBold;
        private Typeface fontLatoHeavy;
        private Button btnApply;
        private RelativeLayout relChatCell;
        private CircleImageView imgProfile;

        public ClassViewHolder(Context ctx, View view) {
            super(view);

            lblClass        = (TextView) view.findViewById(R.id.lblClass);
            fontLatoRegular = FontCache.get(ctx, "Lato-Regular");
            fontLatoBold    = FontCache.get(ctx, "Lato-Bold");
            fontLatoHeavy   = FontCache.get(ctx, "Lato-Heavy");
            btnApply        = (Button) view.findViewById(R.id.btnApply);
            relChatCell     = (RelativeLayout) view.findViewById(R.id.relChatCell);
            imgProfile      = (CircleImageView) view.findViewById(R.id.imgProfile);

            lblClass.setTypeface(fontLatoRegular);
            btnApply.setTypeface(fontLatoRegular);
        }
    }

    public interface ChatListRecyclerAdapterListener {
        public void onListClicked(String strUserId, String strName, String strPhoto, boolean blnCall);
    }
}
