package ResourceTypeOrganiser.models;

import com.intellij.psi.xml.XmlTag;

import java.util.ArrayList;

/**
 * Created by zone2 on 12/16/16.
 */
public class Resource {
    private boolean hasId;
    private XmlTag xmlTag;
    private String trackId;
    private String idValue;
    private ArrayList<Resource> children;


    public String getTrackId() {
        return trackId;
    }

    public void setTrackId(String trackId) {
        this.trackId = trackId;
    }

    public boolean hasId() {
        return hasId;
    }

    public void setHasId(boolean hasId) {
        this.hasId = hasId;
    }

    public XmlTag getXmlTag() {
        return xmlTag;
    }

    public void setXmlTag(XmlTag xmlTag) {
        this.xmlTag = xmlTag;
    }

    public String getIdValue() {
        return idValue;
    }

    public void setIdValue(String idValue) {
        this.idValue = idValue;
    }

    public ArrayList<Resource> getChildren() {
        return children;
    }

    public void setChildren(ArrayList<Resource> children) {
        this.children = children;
    }
}
