package ResourceTypeOrganiser.gui.cellAdapter;

import ResourceTypeOrganiser.gui.views.FilterComboBox;
import ResourceTypeOrganiser.models.Resource;
import ResourceTypeOrganiser.models.ResourceMap;
import ResourceTypeOrganiser.utils.Utils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class ListItemRenderer extends JPanel implements ListCellRenderer {

    private JTextArea previewArea;
    private JTextField idField;
    private FilterComboBox filterComboBox;
    private ArrayList<ResourceMap> resourceMaps;

    public ListItemRenderer(JTextArea previewArea, JTextField idField, FilterComboBox filterComboBox, ArrayList<ResourceMap> resourceMaps) {
        this.previewArea = previewArea;
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
        viewTypeLabel.setFont(new Font("Serif", Font.BOLD, 13));

        idDetail = new JLabel("<html> from <b>" + resourceMaps.get(index).getIdValue() + "</b> to <b>@id/" + Utils.standardizeId(resourceMaps.get(index).getTempValue()) + "</b> </html>");
        idDetail.setFont(new Font("Serif", Font.PLAIN, 14));
        itemPanel.setLayout(new GridLayout(2, 1));


        itemPanel.setBorder(new EmptyBorder(5, 5, 5, 5));

        itemPanel.add(idDetail, 0, 0);
        itemPanel.add(viewTypeLabel, 1, 0);

        filterComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String currentFilterItem = filterComboBox.getSelectedItem().toString();
                if (!lastFilterItem.equals(currentFilterItem)) {
                    updateValue(currentFilterItem);
                    list.revalidate();
                }
            }
        });
        if (isSelected) {
            currentIndex = index;
            itemPanel.setBackground(new Color(73, 157, 245));
            viewTypeLabel.setForeground(Color.WHITE);
            idDetail.setForeground(Color.WHITE);
            this.idField.setText(Utils.standardizeId(resourceMaps.get(index).getTempValue()));
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
                itemPanel.setBackground(new Color(221, 221, 221));
            }
        }


        return itemPanel;
    }


    private void updateValue(String text) {

        String content = resourceMaps.get(currentIndex).getContent();
        content = content.replace(resourceMaps.get(currentIndex).getTempValue(), "@id/" + Utils.standardizeId(text));
        previewArea.setText(content);

        resourceMaps.get(currentIndex).setTempValue("@id/" + Utils.standardizeId(text));
        resourceMaps.get(currentIndex).setContent(content);
        idDetail.setText("<html> from <b>" + resourceMaps.get(currentIndex).getIdValue() + "</b> to <b>@id/" + Utils.standardizeId(resourceMaps.get(currentIndex).getTempValue()) + "</b> </html>");


    }


}