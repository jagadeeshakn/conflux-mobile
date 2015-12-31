package com.mifos.objects.client;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by conflux37 on 12/11/2015.
 */
public class ClientIdObject {
        private List<Integer> clientMembers = new ArrayList<Integer>();

        public List<Integer> getClientMembers() {
            return clientMembers;
        }
    public void setClientMembers(List<Integer> clientMembers) {
        this.clientMembers = clientMembers;
    }

}

