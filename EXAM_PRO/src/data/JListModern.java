package data;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

public class JListModern extends JLabel implements ListCellRenderer<Object> {

	public JListModern() {
        setOpaque(true);
    }

    public Component getListCellRendererComponent(JList<?> list,
                                                  Object value,
                                                  int index,
                                                  boolean isSelected,
                                                  boolean cellHasFocus) {

        setText((index+1) +". "+value.toString());
        setFont(new Font("Calibri",Font.BOLD,20));
        Color background;
        Color foreground;

        // check if this cell represents the current DnD drop location
        JList.DropLocation dropLocation = list.getDropLocation();
        if (dropLocation != null
                && !dropLocation.isInsert()
                && dropLocation.getIndex() == index) {

            background = Color.BLUE;
            foreground = Color.WHITE;

        // check if this cell is selected
        } else if (isSelected) {
            background = Color.ORANGE;
            foreground = Color.BLACK;

        // unselected, and not the DnD drop location
        } else {
        	if(index % 2 == 0){
        		 background = new Color(228,235,245,215);
                 foreground = Color.BLACK;
        	}else{
        		background = Color.WHITE;
                foreground = Color.BLACK;
        	}
//            background = Color.WHITE;
//            foreground = Color.BLACK;
        };

        setBackground(background);
        setForeground(foreground);
        
        return this;
    }

}
