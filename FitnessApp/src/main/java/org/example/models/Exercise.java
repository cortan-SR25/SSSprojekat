package org.example.models;

public class Exercise {
    public int id;
    public String name;
    public String note;
    public String videoUrl;
    public Integer equipmentId;
    public Integer machineId;
    public int trainerId;

    public Exercise(int id, String name, String note, String videoUrl, Integer equipmentId, Integer machineId, int trainerId) {
        this.id = id;
        this.name = name;
        this.note = note;
        this.videoUrl = videoUrl;
        this.equipmentId = equipmentId;
        this.machineId = machineId;
        this.trainerId = trainerId;
    }

    public Exercise(){
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public Integer getEquipmentId() {
        return equipmentId;
    }

    public void setEquipmentId(Integer equipmentId) {
        this.equipmentId = equipmentId;
    }

    public Integer getMachineId() {
        return machineId;
    }

    public void setMachineId(Integer machineId) {
        this.machineId = machineId;
    }

    public int getTrainerId() {
        return trainerId;
    }

    public void setTrainerId(int trainerId) {
        this.trainerId = trainerId;
    }
}
