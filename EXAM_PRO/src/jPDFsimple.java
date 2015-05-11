package oracle.forms.ms;

import com.qoppa.pdf.PDFException;
import com.qoppa.pdfViewer.PDFViewerBean;

import java.awt.BorderLayout;
import java.awt.DisplayMode;
import java.awt.GraphicsEnvironment;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.JFrame;

import oracle.forms.ui.VBean;

public class jPDFsimple extends VBean {

    private PDFViewerBean PDFVBean = null;
    private static jPDFsimple sf = null;
    private static String fileName = null;
    
    public static void main (String [] args)
    {
        JFrame jf = new JFrame("Oracle Forms Demo");

        jf.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);

        DisplayMode dm = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDisplayMode();
        jf.setSize((int)Math.min (1024, dm.getWidth() * 0.90), (int)Math.min (768, dm.getHeight() * 0.90));
        jf.setLocationRelativeTo(null);
        
        sf = new jPDFsimple();
        jf.add(sf);
        jf.setVisible(true);
        
        for (int i = 0; i < args.length; i++) {
             if (args[i].equalsIgnoreCase("-help") ||
                    args[i].equalsIgnoreCase("-h") ||
                    args[i].equalsIgnoreCase("-?")) {
                System.out.println("flags: [-noThumb] [-help or -h or -?]");
                System.exit(0);
            } else {
                fileName = args[i];
            }
        }
        System.out.println(fileName);
 
        jf.addComponentListener(new ComponentAdapter ()
                {
                        public void componentResized (ComponentEvent e)
                        {
                            sf.setLocation (10, 10);
                        }
                        
                        public void componentShown (ComponentEvent e)
                        {
                            sf.setLocation (10, 10);
                            // Load an initial document
                            if (fileName == null)
                            {
                               System.out.println("Help!");
                            }
                            else
                            {
                                sf.loadDocument(fileName);
                            }
                        }
                });
    } 

    /**
     * This method initializes 
     * 
     */
    public jPDFsimple() 
    {
        PDFVBean = new PDFViewerBean();
        // Buttons from the toolbar can be removed and added here:
        PDFVBean.getToolbar().getjbOpen().setVisible(false);
        PDFVBean.setBorder(javax.swing.BorderFactory.createLineBorder(java.awt.Color.gray,1));
        PDFVBean.revalidate();
        setLayout(new BorderLayout());
        add(PDFVBean, BorderLayout.CENTER);
    }

    /**
     * Open a local file, given a string filename
     * @param name the name of the file to open
     */
     public void loadDocument (String loadDoc)
     {
         if (loadDoc.startsWith("http:") || loadDoc.startsWith("https:"))
         {
             try {
                 PDFVBean.loadPDF(new URL (loadDoc));
             } catch (PDFException e) {
                 // TODO
                 e.printStackTrace();
                 System.exit(1);
             } catch (MalformedURLException e) {
                 // TODO
                 e.printStackTrace();
                 System.exit(1);
             }
         }
         else
         {
             try {
                 PDFVBean.loadPDF(loadDoc);
             } catch (PDFException e) {
                 // TODO
                  e.printStackTrace();
                  System.exit(1);
             }
         }
     }
}  

