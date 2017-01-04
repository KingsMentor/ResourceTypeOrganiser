package ResourceTypeOrganiser.models;

import java.util.Observable;

/**
 * Created by zone2 on 12/16/16.
 */
public class ResourceMap extends Observable {
    private String idValue, resourceTrackId, content, viewName = "", tempValue = "";


    public ResourceMap(String idValue, String resourceTrackId, String content, String viewName) {
        this.idValue = idValue;
        this.tempValue = "@id/" + idValue.replace("@+id/", "").replace("@id/", "");
        this.resourceTrackId = resourceTrackId;
        this.content = content;
        this.viewName = viewName;
    }

    public String getTempValue() {
        return tempValue;
    }

    public void setTempValue(String tempValue) {
        this.tempValue = tempValue;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
        notifyObservers();
    }

    public String getViewName() {
        return viewName;
    }

    public void setViewName(String viewName) {
        this.viewName = viewName;
        notifyObservers();
    }

    public String getIdValue() {
        return idValue;
    }

    public void setIdValue(String idValue) {
        this.idValue = idValue;
        notifyObservers();
    }

    public String getResourceTrackId() {
        return resourceTrackId;
    }

    public void setResourceTrackId(String resourceTrackId) {
        this.resourceTrackId = resourceTrackId;
        notifyObservers();
    }
}
