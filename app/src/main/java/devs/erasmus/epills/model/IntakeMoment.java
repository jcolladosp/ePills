package devs.erasmus.epills.model;

import org.litepal.crud.DataSupport;
import java.util.Date;

/**
 * Created by jcolladosp on 28/10/2017.
 * Represents the intake of a specific medicine on a specific day in a week at a certain time. It may be repeated in the next week or it may not be repeated.
 */

public class IntakeMoment extends DataSupport {
    private long id;

    private Date startDate;
    private Date endDate;
    private Receipt receipt;
    private Medicine medicine;
    private int quantity;
    private int alarmRequestCode;
    private boolean switchState; //false is switch off, true switch on (notification disable, notification able)
    private long medicineId;
    private int isOnce; //0 is false, 1 is true

    public IntakeMoment(Date startDate, Date endDate, Receipt receipt, long medicineId, int quantity, int alarmRequestCode, int isOnce) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.receipt = receipt;
        this.medicineId = medicineId;
        this.quantity = quantity;
        this.alarmRequestCode = alarmRequestCode;
        this.isOnce = isOnce;

        this.switchState = true;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Date getStartDate() { return startDate; }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }


    public Receipt getReceipt() {
        return receipt;
    }

    public void setReceipt(Receipt receipt) {
        this.receipt = receipt;
    }

    public Medicine getMedicine() {
        return medicine;
    }

    public void setMedicine(Medicine medicineId) {
        this.medicine = medicineId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getAlarmRequestCode() {
        return alarmRequestCode;
    }

    public void setAlarmRequestCode(int alarmRequestCode) {
        this.alarmRequestCode = alarmRequestCode;
    }

    public long getMedicineId() {
        return medicineId;
    }

    public void setMedicineId(long medicineId) {
        this.medicineId = medicineId;
    }

    public int getIsOnce() {
        return isOnce;
    }

    public void setIsOnce(int once) {
        isOnce = once;
    }

    public boolean getSwitchState() {
        return switchState;
    }

    public void setSwitchState(boolean switchState) {
        this.switchState = switchState;
    }
}
