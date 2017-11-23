package devs.erasmus.epills.model;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;

/**
 * Created by jcolladosp on 28/10/2017.
 * Represents all pills, medicines blah.
 */

public class Medicine extends DataSupport {

    private String name;
    private String description;
    private String image;

    public Medicine(String name, String description, String image) {

        this.name = name;
        this.image = image;
        this.description = description;

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

    public String getImage(){
        return this.image;
    }
}
