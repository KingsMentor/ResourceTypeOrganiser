package ResourceTypeOrganiser;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

class ItemRenderer extends JPanel implements ListCellRenderer {

    private JTextArea previewArea;
    private JTextField idField;
    private Resource resource;
    private FilterComboBox filterComboBox;
    private ArrayList<ResourceMap> resourceMaps;

    public ItemRenderer(JTextArea previewArea, JTextField idField, FilterComboBox filterComboBox, Resource resource, ArrayList<ResourceMap> resourceMaps) {
        this.previewArea = previewArea;
        this.resource = resource;
        this.resourceMaps = resourceMaps;
        this.idField = idField;
        this.filterComboBox = filterComboBox;

    }

    JLabel idDetail;
    static int currentIndex = 0;

    private String lastFilterItem = "";
    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {

        JPanel itemPanel = new JPanel();


        JLabel viewTypeLabel = new JLabel(resourceMaps.get(index).getViewName());
        viewTypeLabel.setFont(new Font(null, Font.BOLD, 14));

        idDetail = new JLabel("from " + resourceMaps.get(index).getIdValue() + " to @id/" + standardizeId(resourceMaps.get(index).getTempValue()) + "");
        itemPanel.setLayout(new GridLayout(2, 1));


        itemPanel.setBorder(new EmptyBorder(5, 5, 5, 5));

        itemPanel.add(idDetail, 0, 0);
        itemPanel.add(viewTypeLabel, 1, 0);

        filterComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String currentFilterItem = filterComboBox.getSelectedItem().toString();
                if(!lastFilterItem.equals(currentFilterItem)){
                    updateValue(currentFilterItem);
                    list.revalidate();
                }
            }
        });
        if (isSelected) {
            currentIndex = index;
            itemPanel.setBackground(Color.BLUE);
            viewTypeLabel.setForeground(Color.WHITE);
            idDetail.setForeground(Color.WHITE);
            this.idField.setText(standardizeId(resourceMaps.get(index).getTempValue()));
            this.idField.getDocument().addDocumentListener(new DocumentListener() {
                @Override
                public void insertUpdate(DocumentEvent e) {
                    updateValue(idField.getText());
                    list.revalidate();
                }

                @Override
                public void removeUpdate(DocumentEvent e) {
                    updateValue(idField.getText());
                    list.revalidate();

                }

                @Override
                public void changedUpdate(DocumentEvent e) {
                    updateValue(idField.getText());
                }
            });
        } else {
            viewTypeLabel.setForeground(Color.BLACK);
            idDetail.setForeground(Color.BLACK);
            if (index % 2 == 0) {
                itemPanel.setBackground(Color.WHITE);
            } else {
                itemPanel.setBackground(Color.LIGHT_GRAY);
            }
        }


        return itemPanel;
    }


    private void updateValue(String text) {

        String content = resourceMaps.get(currentIndex).getContent();
        content = content.replace(resourceMaps.get(currentIndex).getTempValue(), "@id/" + standardizeId(text));
        previewArea.setText(content);

        resourceMaps.get(currentIndex).setTempValue("@id/" + standardizeId(text));
        resourceMaps.get(currentIndex).setContent(content);
        idDetail.setText("from " + resourceMaps.get(currentIndex).getIdValue() + " to @id/" + standardizeId(resourceMaps.get(currentIndex).getTempValue()) + "");


    }

    public static String standardizeId(String id) {
        return id.replace("@+id/", "").replace("@id/", "");
    }
}