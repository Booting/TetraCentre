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
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;

import tetra.centre.R;
import tetra.centre.SupportClass.FontCache;

public class AssignmentStudentListRecyclerAdapter extends RecyclerView.Adapter {
    private final Context context;
    public JSONArray jArrAssignmentStudent;
    private AssignmentStudentListRecyclerAdapterListener listener;

    private final String AssignmentDetailId = "AssignmentDetailId";
    private final String AssignmentId       = "AssignmentId";
    private final String ParticipantsId     = "ParticipantsId";
    private final String File               = "File";
    private final String Score              = "Score";
    private final String Name               = "Name";

    private ArrayList<String> arrayAssignmentDetailId = new ArrayList<String>(),
            arrayAssignmentId   = new ArrayList<String>(),
            arrayParticipantsId = new ArrayList<String>(),
            arrayFile           = new ArrayList<String>(),
            arrayScore          = new ArrayList<String>(),
            arrayName           = new ArrayList<String>();

    public AssignmentStudentListRecyclerAdapter(Context context, JSONArray jArrAssignmentStudent, AssignmentStudentListRecyclerAdapterListener listener) {
        this.context = context;
        this.jArrAssignmentStudent = jArrAssignmentStudent;
        this.listener = listener;

        for (int i = 0; i < this.jArrAssignmentStudent.length(); i++) {
            JSONObject jObjClass = this.jArrAssignmentStudent.optJSONObject(i);
            arrayAssignmentDetailId.add(jObjClass.optString(AssignmentDetailId));
            arrayAssignmentId.add(jObjClass.optString(AssignmentId));
            arrayParticipantsId.add(jObjClass.optString(ParticipantsId));
            arrayFile.add(jObjClass.optString(File));
            arrayScore.add(jObjClass.optString(Score));
            arrayName.add(jObjClass.optString(Name));
        }
    }

    @Override
    public int getItemCount() {
        return jArrAssignmentStudent.length();
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder vh, final int position) {
        if (vh instanceof ClassViewHolder) {
            ((ClassViewHolder) vh).lblName.setText(arrayName.get(position));
            ((ClassViewHolder) vh).lblScore.setText(arrayScore.get(position));

            ((ClassViewHolder) vh).btnDownload.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onDownloadClicked(arrayFile.get(position));
                }
            });
            ((ClassViewHolder) vh).btnScore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onScoreClicked(arrayAssignmentDetailId.get(position), arrayScore.get(position));
                }
            });
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        final RecyclerView.ViewHolder vh;

        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.assignment_student_list_cell, viewGroup, false);
        vh = new ClassViewHolder(viewGroup.getContext(), itemView);

        return vh;
    }

    public static class ClassViewHolder extends RecyclerView.ViewHolder {
        protected RelativeLayout relAssignmentStudentCell;
        protected TextView lblName, lblScore;
        protected Button btnDownload, btnScore;
        private Typeface fontLatoRegular;
        private Typeface fontLatoBold;
        private Typeface fontLatoHeavy;

        public ClassViewHolder(Context ctx, View view) {
            super(view);

            relAssignmentStudentCell = (RelativeLayout) view.findViewById(R.id.relAssignmentStudentCell);
            lblName                  = (TextView) view.findViewById(R.id.lblName);
            btnDownload              = (Button) view.findViewById(R.id.btnDownload);
            btnScore                 = (Button) view.findViewById(R.id.btnScore);
            lblScore                 = (TextView) view.findViewById(R.id.lblScore);
            fontLatoRegular          = FontCache.get(ctx, "Lato-Regular");
            fontLatoBold             = FontCache.get(ctx, "Lato-Bold");
            fontLatoHeavy            = FontCache.get(ctx, "Lato-Heavy");

            lblName.setTypeface(fontLatoRegular);
            btnDownload.setTypeface(fontLatoRegular);
            btnScore.setTypeface(fontLatoRegular);
            lblScore.setTypeface(fontLatoRegular);
        }
    }

    public interface AssignmentStudentListRecyclerAdapterListener {
        public void onDownloadClicked(String strFile);
        public void onScoreClicked(String strAssignmentDetailId, String strScore);
    }
}
