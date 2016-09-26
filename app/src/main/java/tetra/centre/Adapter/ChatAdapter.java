package tetra.centre.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import org.json.JSONArray;
import org.json.JSONObject;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import de.hdodenhof.circleimageview.CircleImageView;
import tetra.centre.R;
import tetra.centre.SupportClass.Config;
import tetra.centre.SupportClass.FontCache;

public class ChatAdapter extends BaseAdapter {

    private final String ChatId         = "ChatId";
    private final String EduspiratorId  = "EduspiratorId";
    private final String ParticipantsId = "ParticipantsId";
    private final String SenderId       = "SenderId";
    private final String Message 	    = "Message";
    private final String Date           = "Datee";

    private ArrayList<String> arrayChatId = new ArrayList<String>(),
            arrayEduspiratorId  = new ArrayList<String>(),
            arrayParticipantsId = new ArrayList<String>(),
            arraySenderId       = new ArrayList<String>(),
            arrayMessage        = new ArrayList<String>(),
            arrayDate           = new ArrayList<String>();

    private final LayoutInflater mLayoutInflater;
    public JSONArray jsonChatList;
    private Context context;
    private Typeface fontLatoBold, fontLatoRegular;
    private String strUserId, strUserName, strPhoto;
    private SharedPreferences appsPref;

    public ChatAdapter(Context context, JSONArray jsonChatList, String strUserId, String strUserName, String strPhoto) {
        this.mLayoutInflater = LayoutInflater.from(context);
        this.jsonChatList    = jsonChatList;
        this.context         = context;
        fontLatoBold         = FontCache.get(context, "Lato-Bold");
        fontLatoRegular      = FontCache.get(context, "Lato-Regular");
        this.strUserId       = strUserId;
        this.strUserName     = strUserName;
        this.strPhoto        = strPhoto;
        appsPref 	         = context.getSharedPreferences(Config.PREF_NAME, Activity.MODE_PRIVATE);

        for (int i = 0; i < this.jsonChatList.length(); i++) {
            JSONObject jObjChat = this.jsonChatList.optJSONObject(i);
            arrayChatId.add(jObjChat.optString(ChatId));
            arrayEduspiratorId.add(jObjChat.optString(EduspiratorId));
            arrayParticipantsId.add(jObjChat.optString(ParticipantsId));
            arraySenderId.add(jObjChat.optString(SenderId));
            arrayMessage.add(jObjChat.optString(Message));
            arrayDate.add(jObjChat.optString(Date));
        }
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return jsonChatList.length();
    }

    @Override
    public Object getItem(int position) {
        return this.jsonChatList.optString(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        final ViewHolder vh;
        if (convertView == null) {
            convertView = mLayoutInflater.inflate(R.layout.chat_cell, parent, false);
            vh = new ViewHolder();

            vh.relItem	  = (LinearLayout) convertView.findViewById(R.id.relItem);
            vh.imgProfile = (CircleImageView) convertView.findViewById(R.id.imgProfile);
            vh.txtName    = (TextView) convertView.findViewById(R.id.txtName);
            vh.txtDate    = (TextView) convertView.findViewById(R.id.txtDate);
            vh.txtText    = (TextView) convertView.findViewById(R.id.txtText);

            vh.txtName.setTypeface(fontLatoBold);
            vh.txtDate.setTypeface(fontLatoRegular);
            vh.txtText.setTypeface(fontLatoRegular);

            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }

        // Set Data
        if (strUserId.equalsIgnoreCase(arraySenderId.get(position))) {
            vh.txtName.setText(strUserName);
            Glide.with(context).load(Config.URL_PICTURES + strPhoto).placeholder(R.drawable.placeholder)
                    .centerCrop().diskCacheStrategy(DiskCacheStrategy.ALL).override(300, 300)
                    .dontAnimate().into(vh.imgProfile);
        } else {
            vh.txtName.setText("You");
            Glide.with(context).load(Config.URL_PICTURES + appsPref.getString("Photo", "")).placeholder(R.drawable.placeholder)
                    .centerCrop().diskCacheStrategy(DiskCacheStrategy.ALL).override(300, 300)
                    .dontAnimate().into(vh.imgProfile);
        }
        vh.txtText.setText(arrayMessage.get(position));

        long lastUpdate     = Long.parseLong(arrayDate.get(position));
        long remainingDays  = Config.getRemainingDays(lastUpdate);
        Date dateLastUpdate = new Date(lastUpdate);

        if (remainingDays == 0) {
            Date dtLastUpdate1 = null;
            try {
                dtLastUpdate1 = Config.getSimpleDateFormatHours().parse(Config.getSimpleDateFormatHours().format(dateLastUpdate));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            long eventMillis1   = dtLastUpdate1.getTime();
            long diffMillis1    = Config.getCurrentMillis() - eventMillis1;
            long remainingHours = TimeUnit.MILLISECONDS.toHours(diffMillis1);

            if (remainingHours == 0) {
                long remainingMinutes = TimeUnit.MILLISECONDS.toMinutes(diffMillis1);
                vh.txtDate.setText(""+remainingMinutes+" minutes");
            } else {
                vh.txtDate.setText(""+remainingHours+" hours");
            }
        } else {
            vh.txtDate.setText(""+remainingDays+" days");
        }

        return convertView;
    }

    static class ViewHolder {
    	LinearLayout relItem;
        CircleImageView imgProfile;
        TextView txtName, txtDate, txtText;
    }

}