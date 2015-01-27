package uk.ac.aber.chh57.helloworld;

/**
 * Created by Christian on 26/01/2015.
 */
public class Record {

    private String plantName;
    private String abundance;
    private String comments;
    private String plantPhotoPath;
    private String scenePhotoPath;
    private String reserveViewed;

    public String getPlantName(){
        return plantName;
    }

    public void setPlantName(String newName){
        plantName = newName;
    }

    public String getAbundance(){
        return abundance;
    }

    public void setAbundance(String newAbundance){
        abundance = newAbundance;
    }

    public String getComments(){
        return comments;
    }

    public void setComments(String newComments){
        comments = newComments;
    }

    public String getPlantPhotoPath(){
        return plantPhotoPath;
    }

    public void setPlantPhotoPath(String newPlantPhotoPath){
        plantPhotoPath = newPlantPhotoPath;
    }

    public String getScenePhotoPath(){
        return scenePhotoPath;
    }

    public void setScenePhotoPath(String newScenePhotoPath){
        scenePhotoPath = newScenePhotoPath;
    }

    public String getReserveViewed(){
        return reserveViewed;
    }

    public void setReserveViewed(String newReserveViewed){
        reserveViewed = newReserveViewed;
    }






}
