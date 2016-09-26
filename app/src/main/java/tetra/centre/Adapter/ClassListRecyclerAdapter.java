package tetra.centre.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;

import tetra.centre.ClassDetailActivity;
import tetra.centre.R;
import tetra.centre.SupportClass.Config;
import tetra.centre.SupportClass.FontCache;

public class ClassListRecyclerAdapter extends RecyclerView.Adapter {
    private final Context context;
    public JSONArray jArrClass, jArrStudentClass;
    private ClassListRecyclerAdapterListener listener;
    private SharedPreferences appsPref;
    private String strUserId;

    private final String ClassId = "ClassId";
    private final String UserId  = "UserId";
    private final String Name    = "Name";
    private final String File    = "File";

    private ArrayList<String> arrayClassId = new ArrayList<String>(),
            arrayUserId   = new ArrayList<String>(),
            arrayName     = new ArrayList<String>(),
            arrayBlnApply = new ArrayList<String>(),
            arrayFile     = new ArrayList<String>();

    public ClassListRecyclerAdapter(Context context, JSONArray jArrClass, JSONArray jArrStudentClass,
                                    String strUserId, ClassListRecyclerAdapterListener listener) {
        this.context          = context;
        this.jArrClass        = jArrClass;
        this.jArrStudentClass = jArrStudentClass;
        this.listener         = listener;
        this.appsPref 	      = context.getSharedPreferences(Config.PREF_NAME, Activity.MODE_PRIVATE);
        this.strUserId        = strUserId;

        for (int i = 0; i < this.jArrClass.length(); i++) {
            JSONObject jObjClass = this.jArrClass.optJSONObject(i);
            arrayClassId.add(jObjClass.optString(ClassId));
            arrayUserId.add(jObjClass.optString(UserId));
            arrayName.add(jObjClass.optString(Name));
            arrayFile.add(jObjClass.optString(File));

            boolean blnApply = false;
            for (int j=0; j<this.jArrStudentClass.length(); j++) {
                JSONObject jObjStudentClass = this.jArrStudentClass.optJSONObject(j);
                if (jObjStudentClass.optString(ClassId).equalsIgnoreCase(jObjClass.optString(ClassId))) {
                    blnApply = true;
                }
            }

            if (blnApply) {
                arrayBlnApply.add("true");
            } else {
                arrayBlnApply.add("false");
            }
        }
    }

    @Override
    public int getItemCount() {
        return jArrClass.length();
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder vh, final int position) {
        if (vh instanceof ClassViewHolder) {
            ((ClassViewHolder) vh).lblClass.setText(arrayName.get(position));

            if (arrayBlnApply.get(position).equalsIgnoreCase("true")) {
                ((ClassViewHolder) vh).btnApply.setVisibility(View.GONE);
            } else {
                if (appsPref.getString("UserCategory", "").equalsIgnoreCase("1")) {
                    ((ClassViewHolder) vh).btnApply.setVisibility(View.GONE);
                } else {
                    ((ClassViewHolder) vh).btnApply.setVisibility(View.VISIBLE);
                }
            }

            ((ClassViewHolder) vh).btnApply.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onApplyClicked(arrayClassId.get(position));
                }
            });

            ((ClassViewHolder) vh).relChatCell.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (arrayBlnApply.get(position).equalsIgnoreCase("true") ||
                            appsPref.getString("UserCategory", "").equalsIgnoreCase("1")) {
                        if (appsPref.getString("UserCategory", "").equalsIgnoreCase("1")) {
                            if (strUserId.equalsIgnoreCase(appsPref.getString("UserId", ""))) {
                                Intent intent = new Intent(context, ClassDetailActivity.class);
                                intent.putExtra("ClassId", arrayClassId.get(position));
                                intent.putExtra("Name", arrayName.get(position));
                                intent.putExtra("File", arrayFile.get(position));
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                context.startActivity(intent);
                            }
                        } else {
                            Intent intent = new Intent(context, ClassDetailActivity.class);
                            intent.putExtra("ClassId", arrayClassId.get(position));
                            intent.putExtra("Name", arrayName.get(position));
                            intent.putExtra("File", arrayFile.get(position));
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(intent);
                        }
                    }
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
        protected RelativeLayout relChatCell;
        protected TextView lblClass;
        private Typeface fontLatoRegular;
        private Typeface fontLatoBold;
        private Typeface fontLatoHeavy;
        private Button btnApply;

        public ClassViewHolder(Context ctx, View view) {
            super(view);

            relChatCell     = (RelativeLayout) view.findViewById(R.id.relChatCell);
            lblClass        = (TextView) view.findViewById(R.id.lblClass);
            fontLatoRegular = FontCache.get(ctx, "Lato-Regular");
            fontLatoBold    = FontCache.get(ctx, "Lato-Bold");
            fontLatoHeavy   = FontCache.get(ctx, "Lato-Heavy");
            btnApply        = (Button) view.findViewById(R.id.btnApply);

            lblClass.setTypeface(fontLatoRegular);
            btnApply.setTypeface(fontLatoRegular);
        }
    }

    public interface ClassListRecyclerAdapterListener {
        public void onApplyClicked(String strClassId);
    }
}
