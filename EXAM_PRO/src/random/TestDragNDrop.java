package random;

import java.awt.datatransfer.DataFlavor;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDropEvent;
import java.io.File;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;

public class TestDragNDrop {

	public static void main(String[] args) {
		JFrame frame = new JFrame();
		
		JPanel panel = new JPanel();
		JTextArea myPanel = new JTextArea();
		myPanel.setDropTarget(new DropTarget() {
		    public synchronized void drop(DropTargetDropEvent evt) {
		        try {
		            evt.acceptDrop(DnDConstants.ACTION_COPY);
		            List<File> droppedFiles = (List<File>)
		                evt.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);
		            for (File file : droppedFiles) {
		               	System.out.println(file.getAbsolutePath());
		            }
		        } catch (Exception ex) {
		            ex.printStackTrace();
		        }
		    }
		});
		frame.add(myPanel);
		
		
		
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
		frame.setSize(1000, 1000);
		frame.setLocationRelativeTo(null);

	}

}
