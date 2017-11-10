package devs.erasmus.epills.model;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by jcolladosp on 28/10/2017.
 */

public class IntakeMoment extends DataSupport {

    private Date intakeDate;
    private Receipt receipt;
    private ArrayList<MedicineQuantity> medicineQuantityList;

    public IntakeMoment(Date intakeDate, MedicineQuantity medicineQuantity) {

        this.intakeDate = intakeDate;
        medicineQuantityList = new ArrayList<>();
        medicineQuantityList.add(medicineQuantity);
    }


    public Date getIntakeDate() {
        return intakeDate;
    }

    public void setIntakeDate(Date intakeDate) {
        this.intakeDate = intakeDate;
    }



    public Receipt getReceipt() {
        return receipt;
    }

    public void setReceipt(Receipt receipt) {
        this.receipt = receipt;
    }


    public void addMedicineQuantity(MedicineQuantity medicineQuantity) {
        medicineQuantityList.add(medicineQuantity);
    }

    public void removeMedicineQuantity(MedicineQuantity medicineQuantity) {
        medicineQuantityList.remove(medicineQuantity);
    }

    public ArrayList<MedicineQuantity> getMedicineQuantityList() {
        return medicineQuantityList;
    }
}
