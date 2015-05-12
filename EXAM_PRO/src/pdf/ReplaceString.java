package pdf;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSString;
import org.apache.pdfbox.exceptions.COSVisitorException;
import org.apache.pdfbox.pdfparser.PDFStreamParser;
import org.apache.pdfbox.pdfwriter.ContentStreamWriter;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDStream;
import org.apache.pdfbox.util.PDFOperator;
/**
 * This is an example that will replace a string in a PDF with a new one.
 *
 * The example is taken from the pdf file format specification.
 *
 * @author <a href="mailto:ben@benlitchfield.com">Ben Litchfield</a>
 * @version $Revision: 1.3 $
 */
public class ReplaceString
{
    /**
     * Constructor.
     */
    public ReplaceString()
    {
        super();
    }

    /**
     * Locate a string in a PDF and replace it with a new string.
     *
     * @param inputFile The PDF to open.
     * @param outputFile The PDF to write to.
     * @param strToFind The string to find in the PDF document.
     * @param message The message to write in the file.
     *
     * @throws IOException If there is an error writing the data.
     * @throws COSVisitorException If there is an error writing the PDF.
     */
    public void doIt( String inputFile, String outputFile, String strToFind, String message)
        throws IOException, COSVisitorException
    {
        // the document
        PDDocument doc = null;
        try
        {
            doc = PDDocument.load( inputFile );
            List pages = doc.getDocumentCatalog().getAllPages();
            for( int i=0; i<pages.size(); i++ )
            {
                PDPage page = (PDPage)pages.get( i );
                PDStream contents = page.getContents();
                PDFStreamParser parser = new PDFStreamParser(contents.getStream() );
                parser.parse();
                List tokens = parser.getTokens();
                for( int j=0; j<tokens.size(); j++ )
                {
                    Object next = tokens.get( j );
                    if( next instanceof PDFOperator )
                    {
                        PDFOperator op = (PDFOperator)next;
                        //Tj and TJ are the two operators that display
                        //strings in a PDF
                        if( op.getOperation().equals( "Tj" ) )
                        {
                            //Tj takes one operator and that is the string
                            //to display so lets update that operator
                            COSString previous = (COSString)tokens.get( j-1 );
                            String string = previous.getString();
                            string = string.replaceFirst( strToFind, message );
                            previous.reset();
                            previous.append( string.getBytes("ISO-8859-1") );
                        }
                        else if( op.getOperation().equals( "TJ" ) )
                        {
                            COSArray previous = (COSArray)tokens.get( j-1 );
                            for( int k=0; k<previous.size(); k++ )
                            {
                                Object arrElement = previous.getObject( k );
                                if( arrElement instanceof COSString )
                                {
                                    COSString cosString = (COSString)arrElement;
                                    String string = cosString.getString();
                                    string = string.replaceFirst( strToFind, message );
                                    cosString.reset();
                                    cosString.append( string.getBytes("ISO-8859-1") );
                                }
                            }
                        }
                    }
                }
                //now that the tokens are updated we will replace the
                //page content stream.
                PDStream updatedStream = new PDStream(doc);
                OutputStream out = updatedStream.createOutputStream();
                ContentStreamWriter tokenWriter = new ContentStreamWriter(out);
                tokenWriter.writeTokens( tokens );
                page.setContents( updatedStream );
            }
            doc.save( outputFile );
        }
        finally
        {
            if( doc != null )
            {
                doc.close();
            }
        }
    }
    
    public static void main(String[] args){
		ReplaceString app = new ReplaceString();
        try
        {
//            if( args.length != 4 )
//            {
//                app.usage();
//            }
//            else
//            {
                app.doIt( "C:\\Users\\Tahmidul\\Desktop\\MERGED.PDF","C:\\Users\\Tahmidul\\Desktop\\looool.PDF" , "4", "1" );
//            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
	}
	
	   /**
     * This will print out a message telling how to use this example.
     */
    public void usage()
    {
        System.err.println( "usage: " + this.getClass().getName() +
            " <input-file> <output-file> <search-string> <Message>" );
    }
}