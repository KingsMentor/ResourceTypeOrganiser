package ResourceTypeOrganiser.gui;

import ResourceTypeOrganiser.gui.cellAdapter.ListItemRenderer;
import ResourceTypeOrganiser.models.Resource;
import ResourceTypeOrganiser.models.ResourceMap;
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
    private Resource resource;
    private EditSession editSession;
    private ArrayList<ResourceMap> resourceMaps;

    public GenerateDialog(@Nullable XmlFile xmlFile) {
        super(xmlFile.getProject());

        setTitle("Organise Resource");

        resource = new Resource();
        resourceMaps = new ArrayList<>();

        // iterate through xml file to get resource
        iterateTag(resource, new ArrayList<>(), xmlFile.getDocument().getRootTag());


        // handle GUI
        initGui(xmlFile);
        getContentPane().add(mainContainer);
        init();
    }


    @Override
    public void update(Observable o, Object arg) {
//        System.out.println("change has occured");
    }

    JPanel mainContainer;

    private void initGui(XmlFile xmlFile) {
        mainContainer = new JPanel(new BorderLayout());
        JBTabbedPane resourceTypeTab = new JBTabbedPane();

        editSession = new EditSession(xmlFile.getProject(), resourceMaps);
        JBList resourceTypesListView = new JBList(resourceMaps);

        ListItemRenderer itemRenderer = new ListItemRenderer(editSession.getPreviewArea(), editSession.getNewIdField(), editSession.getFilteredComboBox(), resourceMaps);
        resourceTypesListView.setCellRenderer(itemRenderer);
        resourceTypesListView.setPreferredSize(new Dimension(500, 1000));
        resourceTypesListView.setSelectedIndex(0);
        resourceTypesListView.setVisibleRowCount(10);


        JBScrollPane resourceTypesListViewScrollContainer = new JBScrollPane(resourceTypesListView);

        JPanel uiParentPanel = new JPanel(new BorderLayout());
        uiParentPanel.add(resourceTypesListViewScrollContainer, BorderLayout.CENTER);

        uiParentPanel.add(editSession, BorderLayout.EAST);


        resourceTypeTab.addTab("Ids", uiParentPanel);

        mainContainer.add(resourceTypeTab, BorderLayout.CENTER);
    }


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
        return mainContainer;
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
