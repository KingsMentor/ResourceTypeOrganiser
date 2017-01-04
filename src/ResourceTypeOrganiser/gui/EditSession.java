package ResourceTypeOrganiser.gui;

import ResourceTypeOrganiser.gui.views.FilterComboBox;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.TextBrowseFolderListener;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.openapi.util.JDOMUtil;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.components.panels.HorizontalLayout;
import com.intellij.ui.components.panels.VerticalLayout;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class EditSession extends JPanel {

    public EditSession(Project project, ArrayList items) {
//            setSize(3500, 3500);

        add(resDir(project, items));
    }

    public JPanel resDir(Project project, ArrayList items) {

        JPanel parentPanel = new JPanel(new VerticalLayout(10));
//        resDirectoryPanel(parentPanel);
//        resValueIdPanel(parentPanel, new ArrayList<>());
        parentPanel.add(resDirectoryPanel(project));

        ArrayList values = getValues(project.getBasePath() + DEFAULT_RES_DIR, "id");
        Collections.sort(values);
        parentPanel.add(resValueIdPanel(values));

        JPanel vbox = new JPanel(new VerticalLayout(10));
        vbox.add(previewPanel());
        vbox.add(repoPanel());
        parentPanel.add(vbox);


        return parentPanel;


    }

    private static final String DEFAULT_RES_DIR = "/app/src/main/res/values/ids.xml";
    private TextFieldWithBrowseButton resDirField;

    private JPanel resDirectoryPanel(Project project) {
        resDirField = new TextFieldWithBrowseButton();
        resDirField.setEditable(false);
        resDirField.setText(project.getBasePath() + DEFAULT_RES_DIR);
        resDirField.addBrowseFolderListener(new TextBrowseFolderListener(
                new FileChooserDescriptor(true, false, false, false, false, false), project));
//        resDirField.setPreferredSize(new Dimension(350, 20));
        resDirField.getTextField().getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                ArrayList values = getValues(resDirField.getText(), "id");
                Collections.sort(values);
                populateFilteredItems(values);

            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                ArrayList values = getValues(resDirField.getText(), "id");
                Collections.sort(values);
                populateFilteredItems(values);
            }

            @Override
            public void changedUpdate(DocumentEvent e) {

            }
        });
        JPanel resDirectoryPanel = new JPanel(new HorizontalLayout(110));
        resDirectoryPanel.add(new JLabel("Res Diretory : "));
        resDirField.setPreferredSize(new Dimension(340, (int) resDirField.getPreferredSize().getHeight()));
        resDirectoryPanel.add(resDirField);
        return resDirectoryPanel;
    }

    private FilterComboBox filteredComboBox;

    private JPanel resValueIdPanel(ArrayList<String> items) {
        JPanel resValueIdPanel = new JPanel(new HorizontalLayout(60));
        resValueIdPanel.add(new JLabel("Res-Value id (@id/) : "));
        filteredComboBox = new FilterComboBox();
        filteredComboBox.setPreferredSize(new Dimension(170, (int) filteredComboBox.getPreferredSize().getHeight()));
        populateFilteredItems(items);

//
//        newIdField.setPreferredSize(new Dimension(240, 20));
//        filteredComboBox.setPreferredSize(new Dimension(100, 20));

        resValueIdPanel.add(filteredComboBox);
        JPanel hbox = new JPanel(new HorizontalLayout(5));
        hbox.add(resValueIdPanel);
        newIdField.setPreferredSize(new Dimension(170, (int) newIdField.getPreferredSize().getHeight()));
        hbox.add(newIdField);
//        resValueIdPanel.add(newIdField);

        return hbox;
    }

    private void populateFilteredItems(ArrayList<String> items) {
        filteredComboBox.removeAllItems();
        for (String item : items) {
            filteredComboBox.addItem(item);
        }
    }

    private JPanel previewPanel() {


        JPanel previewPanel = new JPanel(new HorizontalLayout(100));
        previewPanel.add(new JLabel("View Content : "));


        previewArea.setFont(new Font(null, Font.PLAIN, 12));
        previewArea.setEditable(false);
        previewArea.setLineWrap(false);
        previewArea.setPreferredSize(new Dimension(350, 150));
        previewArea.setBorder(new EmptyBorder(5, 5, 5, 5));

        JBScrollPane jbScrollPane = new JBScrollPane(previewArea);
        jbScrollPane.setPreferredSize(new Dimension(350, 150));
        previewPanel.add(jbScrollPane);

        return previewPanel;
    }

    private JTextArea previewArea = new JTextArea();
    private JTextField newIdField = new JTextField();


    public TextFieldWithBrowseButton getResDirField() {
        return resDirField;
    }

    public FilterComboBox getFilteredComboBox() {
        return filteredComboBox;
    }

    public JTextArea getPreviewArea() {
        return previewArea;
    }

    public JTextField getNewIdField() {
        return newIdField;
    }

    private JPanel repoPanel() {
        JPanel repoPanel = new JPanel(new VerticalLayout(5));
        repoPanel.add(new JSeparator());
        JBLabel gitRepo = new JBLabel("<html>Contribute on <a href=\"http://google.github.io/material-design-icons/\">github</a></html>");
        repoPanel.add(gitRepo);

        JBLabel aboutMe = new JBLabel("<html>release <b>version 1.0-alpha</b> developed by <a href=\"http://belvi.xyz/\">Nosakhare Belvi</a></html>");
        repoPanel.add(aboutMe);
        initLabelLink(gitRepo, "");
        initLabelLink(aboutMe, "http://belvi.xyz");
        return repoPanel;
    }

    public ArrayList getValues(String filePath, String type) {
        ArrayList ids = new ArrayList();

        if (filePath.endsWith("/ids.xml")) {
            try {
                Document doc = JDOMUtil.loadDocument(new File(filePath));
                List<Element> elements = doc.getRootElement().getChildren();
                for (org.jdom.Element element : elements) {
                    String key = element.getAttributeValue("type");
                    if (key.equals(type)) {
                        ids.add(element.getAttributeValue("name"));
                    }

//                comboBoxColor.addItem(key);
                }

            } catch (JDOMException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return ids;
    }

    private void initLabelLink(JBLabel label, final String url) {
        label.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        label.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent event) {
                if (event.getClickCount() > 0) {
                    if (Desktop.isDesktopSupported()) {
                        Desktop desktop = Desktop.getDesktop();
                        try {
                            URI uri = new URI(url);
                            desktop.browse(uri);
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (URISyntaxException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
    }
}