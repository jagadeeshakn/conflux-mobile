package com.mifos.objects.client;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by conflux37 on 12/10/2015.
 */
public class Changes {

    private List<String> clientMembers = new ArrayList<String>();

    public List<String> getClientMembers() {
        return clientMembers;
    }

    public void setClientMembers(List<String> clientMembers) {
        this.clientMembers = clientMembers;
    }
}
