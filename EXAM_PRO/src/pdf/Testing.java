package pdf;

import java.io.File;

public class Testing {
	
	public static void main(String[] args){
//		ReplaceString app = new ReplaceString();
//        try
//        {
//            if( args.length != 4 )
//            {
//                app.usage();
//            }
//            else
//            {
//                app.doIt( args[0], args[1], args[2], args[3] );
//            }
//        }
//        catch (Exception e)
//        {
//            e.printStackTrace();
//        }
		System.out.println(System.getProperty("user.home")+"\\Desktop");
		File dir = new File(System.getProperty("user.home")+"\\Desktop\\"+"NEWFOLDER");
		dir.mkdir();
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
