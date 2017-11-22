package devs.erasmus.epills.widget;

import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import devs.erasmus.epills.R;
import devs.erasmus.epills.controller.MedicineBoxActivity;
import devs.erasmus.epills.model.Medicine;

/**
 * Created by Jonas on 22.11.2017.
 */

public class PillBoxAdapter extends RecyclerView.Adapter <PillBoxAdapter.ViewHolder> {
    private List<Medicine> medicines;
    private MedicineBoxActivity medicineBoxActivity;

    public PillBoxAdapter(MedicineBoxActivity medicineBoxActivity) {
        this.medicines = DataSupport.findAll(Medicine.class);
        this.medicineBoxActivity = medicineBoxActivity;

        Collections.sort(medicines, new Comparator<Medicine>() {
            @Override
            public int compare(Medicine o1, Medicine o2) {
                return o1.getName().compareTo(o2.getName()) ;
            }
        });
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.pill_box_item,parent, false);
        ViewHolder vh = new ViewHolder(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Medicine med = medicines.get(position);

        holder.medicineName.setText(med.getName());
        if (TextUtils.isEmpty(med.getDescription())) {
            holder.medicineDescription.setText(medicineBoxActivity.getString(R.string.empty_description));
        } else {
            holder.medicineDescription.setText(med.getDescription());
        }

        Glide.with(medicineBoxActivity)
                .load(med.getImage())
                .apply(RequestOptions.circleCropTransform().fallback(R.mipmap.ic_picture_round).error(R.mipmap.ic_picture_round))
                .into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return medicines.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.imageView)
        ImageView imageView;
        @BindView(R.id.medicine_name)
        TextView medicineName;
        @BindView(R.id.medicine_description)
        TextView medicineDescription;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
