package devs.erasmus.epills.model;

import java.util.ArrayList;

/**
 * Created by jcolladosp on 28/10/2017.
 */

public class Medicine {
    private int id;
    private String name;
    private String description;
    private ArrayList<MedicineQuantity> medicineQuantityList;
    private String image;

    public Medicine(int id, String name,String image) {
        this.id = id;
        this.name = name;
        medicineQuantityList = new ArrayList<>();
        this.image = image;

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void addMedicineQuantity(MedicineQuantity medicineQuantity) {
        medicineQuantityList.add(medicineQuantity);
    }
    public String getImage(){
        return this.image;
    }

    public void removeMedicineQuantity(MedicineQuantity o) {
        medicineQuantityList.remove(o);
    }
}
