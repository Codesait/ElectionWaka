package com.example.iduma.tree_tracking.Model;

public class DeforestationModel {

    private String reporterName, treeCoordinates,typeOfTrees, noOfTrees;

    public DeforestationModel() {
    }

    public DeforestationModel(String reporterName, String treeCoordinates, String typeOfTrees,
                              String noOfTrees) {
        this.reporterName = reporterName;
        this.treeCoordinates = treeCoordinates;
        this.typeOfTrees = typeOfTrees;
        this.noOfTrees = noOfTrees;
    }

    public String getReporterName() {
        return reporterName;
    }

    public void setReporterName(String reporterName) {
        this.reporterName = reporterName;
    }

    public String getTreeCoordinates() {
        return treeCoordinates;
    }

    public void setTreeCoordinates(String treeCoordinates) {
        this.treeCoordinates = treeCoordinates;
    }

    public String getTypeOfTrees() {
        return typeOfTrees;
    }

    public void setTypeOfTrees(String typeOfTrees) {
        this.typeOfTrees = typeOfTrees;
    }

    public String getNoOfTrees() {
        return noOfTrees;
    }

    public void setNoOfTrees(String noOfTrees) {
        this.noOfTrees = noOfTrees;
    }
}
