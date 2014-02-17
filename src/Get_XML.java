import java.io.File;

//this file retreives all the XML files from the directory 
public class Get_XML {

	public void getxml()
	{		

		String DirName = ("C:\\MY_STUFF\\research\\specification_check\\spec\\");

		File[] listOfFiles = GetFilesFromDirectory(DirName);

		int numofFiles = listOfFiles.length;
		String files;


		for(int i=0; i< numofFiles; i++)
		{
			if (listOfFiles[i].isFile()) 
			{
				files = listOfFiles[i].getName();
				System.out.println("(DEBUG) The XML file is "+files);
				
				Xml_parser xml_parser = new Xml_parser();
				//calling new method
				xml_parser.ProcessXmlsFromDirectory(files, DirName);
				
			}
		}
	}

	public static File[] GetFilesFromDirectory(String path)
	{

		String files;
		File folder = new File(path);
		File[] listOfFiles = folder.listFiles(); 

		for (int i = 0; i < listOfFiles.length; i++) 
		{

			if (listOfFiles[i].isFile()) 
			{
				files = listOfFiles[i].getName();
				//  System.out.println(files);
			}
		}

		return listOfFiles;
	}//end GetFilesFromDirectory(String path)


}
