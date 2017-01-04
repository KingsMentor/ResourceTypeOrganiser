package ResourceTypeOrganiser;

import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.psi.xml.XmlFile;
import com.intellij.psi.xml.XmlTag;
import com.intellij.ui.components.JBList;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.components.JBTabbedPane;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.util.*;

/**
 * Created by zone2 on 12/14/16.
 */
public class GenerateDialog extends DialogWrapper implements Observer {
    //    CollectionListModel<ResourceMap> collectionListModel;
    JPanel jPanel;
    Resource resource;
    EditSession editSession;

    protected GenerateDialog(@Nullable XmlFile xmlFile) {
        super(xmlFile.getProject());
        XmlTag xmlTag = xmlFile.getDocument().getRootTag();
        resource = new Resource();
        resourceMaps = new ArrayList<>();
        iterateTag(resource, new ArrayList<>(), xmlTag);


        setTitle("Organise Resource");
        jPanel = new JPanel(new BorderLayout());
        JBTabbedPane jtp = new JBTabbedPane();
        JPanel jp = new JPanel(new BorderLayout());


        JBList list = new JBList(resourceMaps);

        editSession = new EditSession(xmlFile.getProject(), resourceMaps);

        ItemRenderer itemRenderer = new ItemRenderer(editSession.getPreviewArea(), editSession.getNewIdField(), editSession.getFilteredComboBox(), resource, resourceMaps);


        list.setCellRenderer(itemRenderer);

        list.setPreferredSize(new Dimension(500, 1000));
        list.setSelectedIndex(0);


        list.setVisibleRowCount(4);
        JBScrollPane pane = new JBScrollPane(list);
        jp.add(pane, BorderLayout.CENTER);

        jp.add(editSession, BorderLayout.EAST);


        jtp.addTab("Ids", jp);
        jPanel.add(jtp, BorderLayout.CENTER);


        getContentPane().add(jPanel);


        init();
    }

    @Override
    public void update(Observable o, Object arg) {
        System.out.println("change has occured");
    }


    //    ArrayList<Resource> resources = new ArrayList<>();
    ArrayList<ResourceMap> resourceMaps;

    private void iterateTag(Resource resource, ArrayList<Resource> resources, XmlTag xmlTag) {
        XmlTag[] subTags = xmlTag.getSubTags();
        if (xmlTag.getAttribute("android:id") != null) {
            resource.setHasId(true);
            String content = xmlTag.getNode().getText().substring(0, xmlTag.getNode().getText().indexOf(">") + 1);
            String viewName = xmlTag.getName();
            String value = xmlTag.getAttribute("android:id").getValue();

            content = content.replace(value, "@id/" + value.replace("@+id/", "").replace("@id/", ""));

            if (!value.startsWith("@android:id/")) {
                resource.setIdValue(value);
                resource.setTrackId(generateTrackId());

                ResourceMap resourceMap = new ResourceMap(resource.getIdValue(), resource.getTrackId(), content, viewName);
                resourceMap.addObserver(this);
                resourceMaps.add(resourceMap);
            }
//            System.out.println(xmlTag.getAttribute("android:id").getValue());
        }
        resource.setXmlTag(xmlTag);
        if (subTags.length > 0) {

            ArrayList<Resource> resourceList = new ArrayList<>();
            for (XmlTag tag : subTags) {
                Resource children = new Resource();
                iterateTag(children, resourceList, tag);
            }
            resource.setChildren(resourceList);
            resources.add(resource);
        } else {
            resources.add(resource);
        }

    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        return jPanel;
    }

    public String getIdPath() {
        return editSession.getResDirField().getText();
    }

    public ArrayList<String> ids() {
        return editSession.getValues(getIdPath(), "id");
    }

    public Resource getResource() {
        return resource;
    }

    public ArrayList<ResourceMap> getResourceMaps() {
        return resourceMaps;
    }

    private String generateTrackId() {
        return UUID.randomUUID().toString();
    }
}
