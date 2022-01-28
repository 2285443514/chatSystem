package uschat;

import java.util.Vector;

public class Group {
    private Vector<String> members;
    private String name;
    private String account;

    public Group(String name, String account) {
        this.name = name;
        this.account = account;
        this.members = new Vector<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public Vector<String> getMembers() {
        return members;
    }

    public void setMembers(Vector<String> members) {
        this.members = members;
    }
}
