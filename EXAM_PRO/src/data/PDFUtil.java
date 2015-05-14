package data;

import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.cos.COSStream;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentCatalog;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.common.COSArrayList;
import org.apache.pdfbox.pdmodel.common.COSObjectable;
import org.apache.pdfbox.pdmodel.edit.PDPageContentStream;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotation;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationWidget;
import org.apache.pdfbox.pdmodel.interactive.form.*;
 
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
 
 
public class PDFUtil {
 
    public static void flattenPDF (PDDocument doc) throws IOException {
 
        //
        //  find the fields and their kids (widgets) on the input document
        //  (each child widget represents an appearance of the field data on the page, there may be multiple appearances)
        //
        PDDocumentCatalog catalog = doc.getDocumentCatalog();
        PDAcroForm form = catalog.getAcroForm();
        List<PDField> tmpfields = form.getFields();
        PDResources formresources = form.getDefaultResources();
        Map formfonts = formresources.getFonts();
        PDAnnotation ann;
 
        //
        // for each input document page convert the field annotations on the page into
        // content stream
        //
        List<PDPage> pages = catalog.getAllPages();
        Iterator<PDPage> pageiterator = pages.iterator();
        while (pageiterator.hasNext()) {
            //
            // get next page from input document
            //
            PDPage page = pageiterator.next();
 
            //
            // add the fonts from the input form to this pages resources
            // so the field values will display in the proper font
            //
            PDResources pageResources = page.getResources();
            Map pageFonts = pageResources.getFonts();
            pageFonts.putAll(formfonts);
            pageResources.setFonts(pageFonts);
 
            //
            // Create a content stream for the page for appending
            //
            PDPageContentStream contentStream = new PDPageContentStream(doc, page, true, true);
 
            //
            // Find the appearance widgets for all fields on the input page and insert them into content stream of the page
            //
            for (PDField tmpfield : tmpfields) {
                List widgets = tmpfield.getKids();
                if(widgets == null) {
                    widgets = new ArrayList();
                    widgets.add(tmpfield.getWidget());
                }
                Iterator<COSObjectable> widgetiterator = widgets.iterator();
                while (widgetiterator.hasNext()) {
                    COSObjectable next = widgetiterator.next();
                    if (next instanceof PDField) {
                        PDField foundfield = (PDField) next;
                        ann = foundfield.getWidget();
                    } else {
                        ann = (PDAnnotation) next;
                    }
                    if (ann.getPage().equals(page)) {
                        COSDictionary dict = ann.getDictionary();
                        if (dict != null) {
                            if(tmpfield instanceof PDVariableText || tmpfield instanceof PDPushButton) {
                                COSDictionary ap = (COSDictionary) dict.getDictionaryObject("AP");
                                if (ap != null) {
 
                                    contentStream.appendRawCommands("q\n");
                                    COSArray rectarray = (COSArray) dict.getDictionaryObject("Rect");
                                    if (rectarray != null) {
                                        float[] rect = rectarray.toFloatArray();
                                        String s = " 1 0 0 1  " + Float.toString(rect[0]) + " " + Float.toString(rect[1]) + " cm\n";
 
                                        contentStream.appendRawCommands(s);
                                    }
                                    COSStream stream = (COSStream) ap.getDictionaryObject("N");
                                    if (stream != null) {
                                        InputStream ioStream = stream.getUnfilteredStream();
                                        ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
                                        byte[] buffer = new byte[4096];
                                        int amountRead = 0;
                                        while ((amountRead = ioStream.read(buffer, 0, buffer.length)) != -1) {
                                            byteArray.write(buffer, 0, amountRead);
                                        }
 
                                        contentStream.appendRawCommands(byteArray.toString() + "\n");
                                    }
 
                                    contentStream.appendRawCommands("Q\n");
                                }
                            } else if (tmpfield instanceof PDChoiceButton) {
                                COSDictionary ap = (COSDictionary) dict.getDictionaryObject("AP");
                                if(ap != null) {
                                    contentStream.appendRawCommands("q\n");
                                    COSArray rectarray = (COSArray) dict.getDictionaryObject("Rect");
                                    if (rectarray != null) {
                                        float[] rect = rectarray.toFloatArray();
                                        String s = " 1 0 0 1  " + Float.toString(rect[0]) + " " + Float.toString(rect[1]) + " cm\n";
 
                                        contentStream.appendRawCommands(s);
                                    }
 
                                    COSName cbValue = (COSName) dict.getDictionaryObject(COSName.AS);
                                    COSDictionary d = (COSDictionary) ap.getDictionaryObject(COSName.D);
                                    if (d != null) {
                                        COSStream stream = (COSStream) d.getDictionaryObject(cbValue);
                                        if(stream != null) {
                                            InputStream ioStream = stream.getUnfilteredStream();
                                            ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
                                            byte[] buffer = new byte[4096];
                                            int amountRead = 0;
                                            while ((amountRead = ioStream.read(buffer, 0, buffer.length)) != -1) {
                                                byteArray.write(buffer, 0, amountRead);
                                            }
 
                                            contentStream.appendRawCommands(byteArray.toString() + "\n");
                                        }
                                    }
 
                                    COSDictionary n = (COSDictionary) ap.getDictionaryObject(COSName.N);
                                    if (n != null) {
                                        COSStream stream = (COSStream) n.getDictionaryObject(cbValue);
                                        if(stream != null) {
                                            InputStream ioStream = stream.getUnfilteredStream();
                                            ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
                                            byte[] buffer = new byte[4096];
                                            int amountRead = 0;
                                            while ((amountRead = ioStream.read(buffer, 0, buffer.length)) != -1) {
                                                byteArray.write(buffer, 0, amountRead);
                                            }
 
                                            contentStream.appendRawCommands(byteArray.toString() + "\n");
                                        }
                                    }
 
                                    contentStream.appendRawCommands("Q\n");
                                }
                            }
                        }
                    }
                }
            }
 
            // delete any field widget annotations and write it all to the page
            // leave other annotations on the page
            COSArrayList newanns = new COSArrayList();
            List anns = page.getAnnotations();
            ListIterator annotiterator = anns.listIterator();
            while (annotiterator.hasNext()) {
                COSObjectable next = (COSObjectable) annotiterator.next();
                if (!(next instanceof PDAnnotationWidget)) {
                    newanns.add(next);
                }
            }
 
            page.setAnnotations(newanns);
            contentStream.close();
        }
 
        //
        // Delete all fields from the form and their widgets (kids)
        //
        for (PDField tmpfield : tmpfields) {
            List kids = tmpfield.getKids();
            if(kids != null) kids.clear();
        }
 
        tmpfields.clear();
 
        // Tell Adobe we don't have forms anymore.
        PDDocumentCatalog pdCatalog = doc.getDocumentCatalog();
        PDAcroForm acroForm = pdCatalog.getAcroForm();
        COSDictionary acroFormDict = acroForm.getDictionary();
        COSArray cosFields = (COSArray) acroFormDict.getDictionaryObject("Fields");
        cosFields.clear();
    }
 
    public static void main(String [] args)
    {
 
        try {
            // for testing
            PDDocument doc = PDDocument.load("C:\\Users\\Tahmidul\\Desktop\\testing.pdf");
            flattenPDF(doc);
            doc.save("C:\\Users\\Tahmidul\\Desktop\\test_flattened.pdf");
        }
        catch (Exception e) {
            System.err.println("Exception: " + e.getLocalizedMessage());
        }
 
    }
};