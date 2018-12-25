package com.example.iduma.tree_tracking.Model;

public class StatisticsModel {
    private String firstName, LastName, noTrees, location,treeType;

    public StatisticsModel() {
    }

    public StatisticsModel(String firstName, String lastName, String noTrees,
                           String location, String treeType) {
        this.firstName = firstName;
        LastName = lastName;
        this.noTrees = noTrees;
        this.location = location;
        this.treeType = treeType;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return LastName;
    }

    public void setLastName(String lastName) {
        LastName = lastName;
    }

    public String getNoTrees() {
        return noTrees;
    }

    public void setNoTrees(String noTrees) {
        this.noTrees = noTrees;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getTreeType() {
        return treeType;
    }

    public void setTreeType(String treeType) {
        this.treeType = treeType;
    }
}
