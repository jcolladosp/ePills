package devs.erasmus.epills.widget;

import android.content.Context;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import org.litepal.crud.DataSupport;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import devs.erasmus.epills.R;
import devs.erasmus.epills.model.Medicine;
import devs.erasmus.epills.utils.LitePalManageUtil;

/**
 * Created by Jonas on 22.11.2017.
 */

public class PillBoxAdapter extends RecyclerView.Adapter <PillBoxAdapter.ViewHolder> {
    private List<Medicine> medicines;
    private Context context;
    private View view;
    public PillBoxAdapter(Context context) {
        this.medicines = DataSupport.findAll(Medicine.class);
        this.context = context;

        Collections.sort(medicines, new Comparator<Medicine>() {
            @Override
            public int compare(Medicine o1, Medicine o2) {
                return o1.getName().compareTo(o2.getName()) ;
            }
        });
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.pill_box_item,parent, false);
        ViewHolder vh = new ViewHolder(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Medicine med = medicines.get(position);

        holder.medicineName.setText(med.getName());
        if (TextUtils.isEmpty(med.getDescription())) {
            holder.medicineDescription.setText(context.getString(R.string.empty_description));
        } else {
            holder.medicineDescription.setText(med.getDescription());
        }

        Glide.with(context)
                .load(med.getImage())
                .apply(RequestOptions.circleCropTransform().fallback(R.mipmap.ic_pill_placeholder).error(R.mipmap.ic_pill_placeholder))
                .into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return medicines.size();
    }

    public void delete(int position) {
        LitePalManageUtil.cancelMedicineFromDatabase(context, medicines.get(position));

        medicines.remove(position);
        notifyDataSetChanged();

        Snackbar mySnackbar = Snackbar.make(view,
                R.string.pill_deleted_success, Snackbar.LENGTH_SHORT);
        mySnackbar.show();

        //medicines.remove(position);
        //TODO: Delete existing DB instances and alarms.
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements ImageButton.OnClickListener{
        @BindView(R.id.imageView)
        ImageView imageView;
        @BindView(R.id.medicine_name)
        TextView medicineName;
        @BindView(R.id.medicine_description)
        TextView medicineDescription;
        @BindView(R.id.deletebutton)
        ImageButton deleteButton;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);

        }

        @OnClick(R.id.deletebutton)
        public void onClick(View v) {
            delete(getLayoutPosition());
        }
    }
}

