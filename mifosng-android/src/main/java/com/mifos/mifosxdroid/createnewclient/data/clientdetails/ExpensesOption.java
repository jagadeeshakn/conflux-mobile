package com.mifos.mifosxdroid.createnewclient.data.clientdetails;

/**
 * Created by jagadeeshakn on 11/26/2015.
 */
import java.util.HashMap;
import java.util.Map;

public class ExpensesOption {


    private Integer id;

    private String name;

    private Integer position;

    private String description;

    private Map<String, Object> expenses = new HashMap<String, Object>();


    public Integer getId() {
        return id;
    }


    public void setId(Integer id) {
        this.id = id;
    }


    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Map<String, Object> getExpenses() {
        return this.expenses;
    }
    public void setExpenses(String name, Object value) {
        this.expenses.put(name, value);
    }
    @Override
    public String toString() {
        return "ExpensesOption{" +
                "id=" + id +
                ", position=" + position +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                '}';
    }

}