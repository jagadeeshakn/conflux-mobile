package com.mifos.objects;

/**
 * Created by conflux37 on 11/25/2015.
 */
public class ClientMember {
    private Integer id;
    private String attendance;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getAttendance() {
        return attendance;
    }

    public void setAttendance(String attendance) {
        this.attendance = attendance;
    }

    @Override
    public String toString() {
        return "ClientMember{" +
                "id=" + id +
                ", attendance='" + attendance + '\'' +
                '}';
    }
}
