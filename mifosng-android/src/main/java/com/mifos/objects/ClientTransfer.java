package com.mifos.objects;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by conflux37 on 12/17/2015.
 */
public class ClientTransfer {
    private Boolean inheritDestinationGroupLoanOfficer;
    private String locale;
    private List<Clients> clients = new ArrayList<Clients>();
    private Integer destinationGroupId;

    public Boolean getInheritDestinationGroupLoanOfficer() {
        return inheritDestinationGroupLoanOfficer;
    }


    public void setInheritDestinationGroupLoanOfficer(Boolean inheritDestinationGroupLoanOfficer) {
        this.inheritDestinationGroupLoanOfficer = inheritDestinationGroupLoanOfficer;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public List<Clients> getClients() {
        return clients;
    }

    public void setClients(List<Clients> clients) {
        this.clients = clients;
    }

    public Integer getDestinationGroupId() {
        return destinationGroupId;
    }


    public void setDestinationGroupId(Integer destinationGroupId) {
        this.destinationGroupId = destinationGroupId;
    }



}
