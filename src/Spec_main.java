import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;


public class Spec_main {

	/**
	 * @param args
	 */

	public static void main(String[] args) {
		
		//first I will delete the old files

				String DirName1 = ("C:\\MY_STUFF\\research\\specification_check\\");
				File[] listOfFiles1 =  GetFilesFromDirectory(DirName1);

				int numofFiles1 = listOfFiles1.length;
				String files1; 

				for(int i=0; i< numofFiles1; i++)
				{
					if (listOfFiles1[i].isFile()) 
					{
						listOfFiles1[i].delete();
					}
				}
		
		Get_XML g = new Get_XML();
		
		//calling another method
		g.getxml();

		pct();
	}

	public static void pct()
	{
		//this method runs Z3 on the requires and ensures files separately to detect contradictions
		try {
			
			String line;
			String DirName = ("C:\\MY_STUFF\\research\\specification_check\\");
			File[] listOfFiles =  GetFilesFromDirectory(DirName);

			int numofFiles = listOfFiles.length;
			String files;


			for(int i=0; i< numofFiles; i++)
			{
				
				if (listOfFiles[i].isFile() && !listOfFiles[i].getName().endsWith("req_ens_valid.smt2") 
						&& !listOfFiles[i].getName().endsWith("req_ens_valid.dfy") 
						&& !listOfFiles[i].getName().endsWith("req_ens_valid_alt.dfy")) 
				{
					files = listOfFiles[i].getName();
					System.out.print("Filename: "+files + "-- ");  

					String[] cmd = {"z3", "C:\\MY_STUFF\\research\\specification_check\\"+files};
					Process p = Runtime.getRuntime().exec(cmd);

					BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
					BufferedReader stdError = new BufferedReader(new InputStreamReader(p.getErrorStream()));

					while ((line = input.readLine()) != null)
					{
						if(line.contains("unsat"))
						{
							System.out.print("contradiction detected : " );
						System.out.print(line+ " ");
						}
						else if(line.contains("sat"))
						{
							System.out.print("no contradiction  : " );
						System.out.print(line+ " ");
						}
						System.out.println(line);
					}
				}
				else if (listOfFiles[i].getName().endsWith("req_ens_valid.smt2")) 
				{
					files = listOfFiles[i].getName();
					System.out.print("Filename: "+files + "-- ");  

					//String[] cmd = {"z3", "/T:20", "C:\\MY_STUFF\\research\\specification_check\\"
						//	+files,  "MBQI_TRACE=true"};
					String[] cmd = {"z3", "/T:20", "C:\\MY_STUFF\\research\\specification_check\\"
							+files};
					Process p = Runtime.getRuntime().exec(cmd);

					BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
					BufferedReader stdError = new BufferedReader(new InputStreamReader(p.getErrorStream()));

					while ((line = input.readLine()) != null)
					{
						if(line.contains("unsat"))
						{
							System.out.print("Z3 says -- The specification is valid : " );
						System.out.print(line);
						}
						else if(line.contains("sat"))
						{
							System.out.print("Z3 says -- The specification is invalid : " );
						System.out.print(line);
						}
						System.out.println(line);
					}

					System.out.println();
					while ((line = stdError.readLine()) != null) {
						System.out.println(line);
					}
				
					input.close();
					stdError.close();
				}
				
				else if (listOfFiles[i].getName().endsWith("req_ens_valid.dfy") || listOfFiles[i].getName().endsWith("req_ens_valid_alt.dfy")) 
				{
					files = listOfFiles[i].getName();
					System.out.print("Filename: "+files + "-- ");  

					String[] cmd = {"C:\\Users\\adititagore\\Desktop\\research\\dafny_25_May_2012\\dafny.exe", "/timeLimit:10", "/compile:0","/nologo", "C:\\MY_STUFF\\research\\specification_check\\"+files};
					Process p = Runtime.getRuntime().exec(cmd);

					BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
					BufferedReader stdError = new BufferedReader(new InputStreamReader(p.getErrorStream()));

					while ((line = input.readLine()) != null)
					{
						if(line.contains("errors detected") || line.contains("assertion violation"))
						{
						System.out.print("Dafny says -- The specification is invalid : " );
						//System.out.print(line);
						}
						else if (line.contains("time out"))
						{
							System.out.print("Dafny timed out :  " );
						//	System.out.print(line);
						}
						
						else if((line.contains("0 errors") && !(line.contains("time out"))))
						{
						System.out.print("Dafny says -- The specification is valid : " );
						//System.out.print(line);
						}
						
						System.out.println(line);
					}

					System.out.println();
					while ((line = stdError.readLine()) != null) {
						System.out.println(line);
					}
				
					input.close();
					stdError.close();
				}
				
			}


			/*  String[] cmd = {"z3", "C:\\MY_STUFF\\research\\Z3_tests\\spec_1.smt2"};
            Process p = Runtime.getRuntime().exec(cmd);

            BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
            BufferedReader stdError = new BufferedReader(new InputStreamReader(p.getErrorStream()));

            while ((line = input.readLine()) != null) {
                System.out.println(line);
            }

            while ((line = stdError.readLine()) != null) {
                System.out.println(line);
            }
			 
			input.close();
			stdError.close();*/
		}
		catch (Exception e) {
			e.printStackTrace();
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
