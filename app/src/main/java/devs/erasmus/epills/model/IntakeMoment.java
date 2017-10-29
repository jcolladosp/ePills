package devs.erasmus.epills.model;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by jcolladosp on 28/10/2017.
 */

public class IntakeMoment {
    private int id;
    private Date intakeDate;
    private User user;
    private Receipt receipt;
    private ArrayList<MedicineQuantity> medicineQuantityList;

    public IntakeMoment(int id, Date intakeDate, User user, MedicineQuantity medicineQuantity) {
        this.id = id;
        this.intakeDate = intakeDate;
        this.user = user;
        medicineQuantityList = new ArrayList<>();
        medicineQuantityList.add(medicineQuantity);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getIntakeDate() {
        return intakeDate;
    }

    public void setIntakeDate(Date intakeDate) {
        this.intakeDate = intakeDate;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
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
}
