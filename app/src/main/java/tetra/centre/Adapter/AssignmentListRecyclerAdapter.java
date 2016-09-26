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
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;

import tetra.centre.EduspiratorDetailAssignmentActivity;
import tetra.centre.R;
import tetra.centre.SupportClass.Config;
import tetra.centre.SupportClass.FontCache;

public class AssignmentListRecyclerAdapter extends RecyclerView.Adapter {
    private final Context context;
    public JSONArray jArrAssignment;
    public JSONArray jArrAssignmentScore;
    private SharedPreferences appsPref;
    private AssignmentListRecyclerAdapterListener listener;
    private String strUserId;

    private final String AssignmentId  = "AssignmentId";
    private final String EduspiratorId = "EduspiratorId";
    private final String Name          = "Name";
    private final String File          = "File";
    private final String Score         = "Score";

    private ArrayList<String> arrayAssignmentId = new ArrayList<String>(),
            arrayEduspiratorId = new ArrayList<String>(),
            arrayName          = new ArrayList<String>(),
            arrayFile          = new ArrayList<String>(),
            arrayScore         = new ArrayList<String>();

    public AssignmentListRecyclerAdapter(Context context, JSONArray jArrAssignment, JSONArray jArrAssignmentScore,
                                         String strUserId, AssignmentListRecyclerAdapterListener listener) {
        this.context             = context;
        this.jArrAssignment      = jArrAssignment;
        this.jArrAssignmentScore = jArrAssignmentScore;
        this.appsPref 	         = context.getSharedPreferences(Config.PREF_NAME, Activity.MODE_PRIVATE);
        this.listener            = listener;
        this.strUserId           = strUserId;

        for (int i = 0; i < this.jArrAssignment.length(); i++) {
            JSONObject jObjClass = this.jArrAssignment.optJSONObject(i);
            arrayAssignmentId.add(jObjClass.optString(AssignmentId));
            arrayEduspiratorId.add(jObjClass.optString(EduspiratorId));
            arrayName.add(jObjClass.optString(Name));
            arrayFile.add(jObjClass.optString(File));
            arrayScore.add("x");
        }

        for (int j = 0; j < this.jArrAssignment.length(); j++) {
            for (int i = 0; i < this.jArrAssignmentScore.length(); i++) {
                JSONObject jObjClass = this.jArrAssignmentScore.optJSONObject(i);
                if (this.jArrAssignment.optJSONObject(j).optString(AssignmentId).equalsIgnoreCase(jObjClass.optString(AssignmentId))) {
                    if (jObjClass.optString("ParticipantsId").equalsIgnoreCase(appsPref.getString("UserId", ""))) {
                        arrayScore.set(j, jObjClass.optString(Score));
                    }
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        return jArrAssignment.length();
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder vh, final int position) {
        if (vh instanceof ClassViewHolder) {
            ((ClassViewHolder) vh).lblClass.setText(arrayName.get(position));
            ((ClassViewHolder) vh).relAssignmentCell.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (appsPref.getString("UserCategory", "").equalsIgnoreCase("1")) {
                        if (strUserId.equalsIgnoreCase(appsPref.getString("UserId", ""))) {
                            Intent intent = new Intent(context, EduspiratorDetailAssignmentActivity.class);
                            intent.putExtra("AssignmentId", arrayAssignmentId.get(position));
                            intent.putExtra("AssignmentName", arrayName.get(position));
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(intent);
                        }
                    }
                }
            });

            if (appsPref.getString("UserCategory", "").equalsIgnoreCase("1")) {
                ((ClassViewHolder) vh).btnDownload.setVisibility(View.GONE);
                ((ClassViewHolder) vh).btnUpload.setVisibility(View.GONE);
            } else {
                ((ClassViewHolder) vh).btnDownload.setVisibility(View.VISIBLE);
                ((ClassViewHolder) vh).btnUpload.setVisibility(View.VISIBLE);
                if (!arrayScore.get(position).equalsIgnoreCase("x")) {
                    ((ClassViewHolder) vh).btnDownload.setVisibility(View.GONE);
                    ((ClassViewHolder) vh).btnUpload.setVisibility(View.GONE);
                    ((ClassViewHolder) vh).linScore.setVisibility(View.VISIBLE);
                    ((ClassViewHolder) vh).txtScore.setText(arrayScore.get(position));
                }
            }

            ((ClassViewHolder) vh).btnDownload.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onDownloadClicked(arrayFile.get(position));
                }
            });
            ((ClassViewHolder) vh).btnUpload.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onUploadClicked(arrayAssignmentId.get(position));
                }
            });
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        final RecyclerView.ViewHolder vh;

        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.assignment_list_cell, viewGroup, false);
        vh = new ClassViewHolder(viewGroup.getContext(), itemView);

        return vh;
    }

    public static class ClassViewHolder extends RecyclerView.ViewHolder {
        protected LinearLayout linScore;
        protected RelativeLayout relAssignmentCell;
        protected TextView lblClass, lblScore, txtScore;
        protected Button btnDownload, btnUpload;
        private Typeface fontLatoRegular;
        private Typeface fontLatoBold;
        private Typeface fontLatoHeavy;

        public ClassViewHolder(Context ctx, View view) {
            super(view);

            linScore          = (LinearLayout) view.findViewById(R.id.linScore);
            relAssignmentCell = (RelativeLayout) view.findViewById(R.id.relAssignmentCell);
            lblClass          = (TextView) view.findViewById(R.id.lblClass);
            btnDownload       = (Button) view.findViewById(R.id.btnDownload);
            btnUpload         = (Button) view.findViewById(R.id.btnUpload);
            lblScore          = (TextView) view.findViewById(R.id.lblScore);
            txtScore          = (TextView) view.findViewById(R.id.txtScore);
            fontLatoRegular   = FontCache.get(ctx, "Lato-Regular");
            fontLatoBold      = FontCache.get(ctx, "Lato-Bold");
            fontLatoHeavy     = FontCache.get(ctx, "Lato-Heavy");

            lblClass.setTypeface(fontLatoRegular);
            btnDownload.setTypeface(fontLatoRegular);
            btnUpload.setTypeface(fontLatoRegular);
            lblScore.setTypeface(fontLatoRegular);
            txtScore.setTypeface(fontLatoBold);
        }
    }

    public interface AssignmentListRecyclerAdapterListener {
        public void onDownloadClicked(String strFile);
        public void onUploadClicked(String strAssignmentId);
    }
}
