package com.example.lms_system;

public class ModuleData {

    static String moduleName;

    public ModuleData(){}

    public ModuleData(String moduleName) {
        this.moduleName = moduleName;
    }

    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }
}
