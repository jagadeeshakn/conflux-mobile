package com.mifos.objects;

import com.mifos.objects.client.Client;
import com.mifos.objects.client.ClientPageItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by conflux37 on 12/14/2015.
 */
public class ClientDetail {
    private Integer totalFilteredRecords;
    private List<Client> pageItems = new ArrayList<Client>();

    public Integer getTotalFilteredRecords() {
        return totalFilteredRecords;
    }

    public void setTotalFilteredRecords(Integer totalFilteredRecords) {
        this.totalFilteredRecords = totalFilteredRecords;
    }

    public List<Client> getPageItems() {
        return pageItems;
    }

    public void setPageItems(List<Client> pageItems) {
        this.pageItems = pageItems;
    }

    @Override
    public String toString() {
        return "ClientDetail{" +
                "totalFilteredRecords=" + totalFilteredRecords +
                ", pageItems=" + pageItems +
                '}';
    }
}
