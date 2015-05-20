package modernUi;

/*
 * ===========================================
 * Java Pdf Extraction Decoding Access Library
 * ===========================================
 *
 * Project Info:  http://www.idrsolutions.com
 * Help section for developers at http://www.idrsolutions.com/support/
 *
 * (C) Copyright 1997-2015 IDRsolutions and Contributors.
 *
 * This file is part of JPedal/JPDF2HTML5
 *
 
 *
 * ---------------
 * OpenViewer.java
 * ---------------
 */


import org.jpedal.examples.viewer.Commands;
import org.jpedal.examples.viewer.Viewer;

import java.io.File;
import java.io.InputStream;


public class OpenViewer {

    /**
     * exmaple showing how to programatically open a file
     */
    public OpenViewer() {

        //create and initialise JPedal viewer component
        final Viewer myViewer =new Viewer();
        myViewer.setupViewer();

        //code to open when required
        File file=null; //example is commented out below
        final InputStream stream = null;

//        open the stream or File
        try {
            file = new File("C:\\Users\\Tahmidul\\Desktop\\testing\\testing_QP.pdf");

            // stream = new FileInputStream("/Users/markee/Desktop/PDF3.pdf");
            //stream = new FileInputStream("/Users/markee/Desktop/test.pdf");
        } catch (Exception e) {
            e.printStackTrace();
        }

        //debug code
        //LogWriter.log_name="/Users/markee/Desktop/log.txt";
        //LogWriter.setupLogFile(true,0,"1.0","v",false);
        
        //list of features at this URL http://www.idrsolutions.com/access-pdf-viewer-features-from-your-code/

        //open the PDF (if linear display first page asap and allow access to other objects)
        if(stream !=null) {
            myViewer.executeCommand(Commands.OPENFILE, new Object[]{stream});
        }
        if(file!=null) {
            myViewer.executeCommand(Commands.OPENFILE, new Object[]{file});
        }

        //url case
        if(stream==null) {
            myViewer.executeCommand(Commands.OPENURL, new Object[]{"http://my.site.org/PDF3.pdf"});
        }

    }

    public static void main(final String[] args) {
        new OpenViewer();
    }
}