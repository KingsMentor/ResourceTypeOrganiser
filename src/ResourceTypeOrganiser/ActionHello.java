package ResourceTypeOrganiser;

import ResourceTypeOrganiser.gui.GenerateDialog;
import ResourceTypeOrganiser.gui.cellAdapter.ListItemRenderer;
import ResourceTypeOrganiser.models.Resource;
import ResourceTypeOrganiser.models.ResourceMap;
import ResourceTypeOrganiser.utils.Utils;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.util.JDOMUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.psi.xml.XmlFile;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.output.XMLOutputter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by zone2 on 10/17/16.
 */
public class ActionHello extends AnAction {

    private String layoutXml = "";

    @Override
    public void actionPerformed(AnActionEvent e) {

        XmlFile xmlFile = getXmlFileFromContext(e);
        GenerateDialog generateDialog = new GenerateDialog(xmlFile);
        generateDialog.show();
        if (generateDialog.isOK()) {
            layoutXml = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n";
            Resource resource = generateDialog.getResource();
            ArrayList<ResourceMap> resourceMaps = generateDialog.getResourceMaps();
            generateNewLayoutXml(resource, resourceMaps);
            ApplicationManager.getApplication().runWriteAction(new Runnable() {
                @Override
                public void run() {
                    try {
                        writeToLayout(xmlFile);
                        writeToResourceXml(generateDialog, resourceMaps);
                    } catch (FileNotFoundException e1) {
                        e1.printStackTrace();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    } catch (JDOMException e1) {
                        e1.printStackTrace();
                    }
                }
            });


        }
    }

    @Override
    public void update(AnActionEvent e) {
        e.getPresentation().setEnabled(getXmlFileFromContext(e) != null);

    }

    private XmlFile getXmlFileFromContext(AnActionEvent e) {
        PsiFile psiFile = e.getData(LangDataKeys.PSI_FILE);
        try {
            XmlFile xmlFile = ((XmlFile) psiFile);
            return xmlFile;
        } catch (Exception ex) {
            return null;
        }

    }


    private void generateNewLayoutXml(Resource resource, ArrayList<ResourceMap> resourceMaps) {
        if (resource != null) {
            if (resource.getChildren() == null) {
                String content = getUpdatedResourceTypeContent(resource, resourceMaps, false);
                layoutXml += content + "\n";
            } else {
                String content = getUpdatedResourceTypeContent(resource, resourceMaps, true);
                layoutXml += content + "\n";
                for (int x = 0; x < resource.getChildren().size(); x++) {
                    generateNewLayoutXml(resource.getChildren().get(x), resourceMaps);
                    if (x + 1 == resource.getChildren().size()) {
                        String viewName = resource.getXmlTag().getName();
                        layoutXml += "</" + viewName + ">\n";
                    }
                }
            }
        }
    }

    private String getUpdatedResourceTypeContent(Resource resource, ArrayList<ResourceMap> resourceMaps, boolean hasChildren) {
        for (ResourceMap resourceMap : resourceMaps) {
            if (resource.getTrackId() != null)
                if (resource.getTrackId().equals(resourceMap.getResourceTrackId())) {
                    return resourceMap.getContent();
                }
        }
        if (hasChildren)
            return resource.getXmlTag().getNode().getText().substring(0, resource.getXmlTag().getNode().getText().indexOf(">") + 1);
        return resource.getXmlTag().getNode().getText();
    }


    private boolean isIdResourceMapped(ResourceMap resourceMap, ArrayList<String> ids) {
        return ids.contains(Utils.standardizeId(resourceMap.getTempValue()));
    }

    private void writeToLayout(XmlFile xmlFile) throws IOException {
        VirtualFile virtualFile = xmlFile.getVirtualFile();
        FileOutputStream fileOutputStream = new FileOutputStream(new File(virtualFile.getPath()));
        fileOutputStream.write(layoutXml.getBytes());
        fileOutputStream.close();


    }

    private void writeToResourceXml(GenerateDialog generateDialog, ArrayList<ResourceMap> resourceMaps) throws JDOMException, IOException {
        ArrayList<String> ids = generateDialog.ids();
        org.jdom.Document doc = JDOMUtil.loadDocument(new File(generateDialog.getIdPath()));
        for (ResourceMap resourceMap : resourceMaps) {
            if (!isIdResourceMapped(resourceMap, ids)) {
                Element element = new Element("item");
                element.setAttribute("name", Utils.standardizeId(resourceMap.getTempValue()));
                element.setAttribute("type", "id");

                doc.getRootElement().addContent(element);
            }

        }
        FileOutputStream fileOutputStream = new FileOutputStream(new File(generateDialog.getIdPath()));
        fileOutputStream.write(new XMLOutputter().outputString(doc).getBytes());
        fileOutputStream.close();
    }
}
