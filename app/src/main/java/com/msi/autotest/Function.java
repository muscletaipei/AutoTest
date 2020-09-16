package com.msi.autotest;

public class Function {
    String name;
    int icon;
    int status = -1;

    public Function() {
    }

    public Function(String name) {
        this.name = name;
    }

    public Function(String name, int icon) {
        this.name = name;
        this.icon = icon;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getStatus() {
        return status;
    }
}
