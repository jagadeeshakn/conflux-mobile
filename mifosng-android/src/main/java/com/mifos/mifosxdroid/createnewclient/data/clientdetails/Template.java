package com.mifos.mifosxdroid.createnewclient.data.clientdetails;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Template implements Serializable {


    private List<GenderOption> genderOptions = new ArrayList<GenderOption>();

    private List<ParentOptions> parentOptions = new ArrayList<ParentOptions>();

    private List<DependentsOption> dependentsOptions = new ArrayList<DependentsOption>();

    private List<SourceOfIncomeOption> sourceOfIncomeOptions = new ArrayList<SourceOfIncomeOption>();

    private List<IncomeTypeOption> incomeTypeOptions = new ArrayList<IncomeTypeOption>();

    private List<ExpensesOption> expensesOptions = new ArrayList<ExpensesOption>();
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    public List<GenderOption> getGenderOptions() {
        return genderOptions;
    }

    public void setGenderOptions(List<GenderOption> genderOptions) {
        this.genderOptions = genderOptions;
    }
    public List<ParentOptions> getParentOptions() {
        return parentOptions;
    }

    public void setParentOptions(List<ParentOptions> parentOptions) {
        this.parentOptions = parentOptions;
    }

    public List<DependentsOption> getDependentsOptions() {
        return dependentsOptions;
    }


    public void setDependentsOptions(List<DependentsOption> dependentsOptions) {
        this.dependentsOptions = dependentsOptions;
    }


    public List<SourceOfIncomeOption> getSourceOfIncomeOptions() {
        return sourceOfIncomeOptions;
    }

    public void setSourceOfIncomeOptions(List<SourceOfIncomeOption> sourceOfIncomeOptions) {
        this.sourceOfIncomeOptions = sourceOfIncomeOptions;
    }

    public List<IncomeTypeOption> getIncomeTypeOptions() {
        return incomeTypeOptions;
    }

    public void setIncomeTypeOptions(List<IncomeTypeOption> incomeTypeOptions) {
        this.incomeTypeOptions = incomeTypeOptions;
    }

    public List<ExpensesOption> getExpensesOptions() {
        return expensesOptions;
    }

    public void setExpensesOptions(List<ExpensesOption> expensesOptions) {
        this.expensesOptions = expensesOptions;
    }


    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }


    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}