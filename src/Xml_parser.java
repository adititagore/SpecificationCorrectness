import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

//this file sets up the parser
public class Xml_parser {
	
	int a;
	
	public int setA(int a1)
	{
		a = a1;
		return a;
	}
	
	public void ProcessXmlsFromDirectory(String files, String DirName)
	{
		try
		{
			
			//File file = new File("C:\\MY_STUFF\\research\\Z3_VCs\\Set_trial.xml");		
			String wholepath = DirName + files;
			//System.out.println("The whole path is "+wholepath);
			
			File file = new File(wholepath);
		//	System.out.println(file.getName());
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			dbf.setIgnoringElementContentWhitespace(true);
			dbf.setNamespaceAware(true);
			dbf.setIgnoringComments(true);

			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse(file);

			//doc = db.parse(file);
			doc.normalizeDocument();
			
			Node root = doc.getDocumentElement();
			// remove whitespace nodes
			root.normalize();
			removeWhitespace(root);
			
			
			//call other methods in Parse_file
			//System.out.println("here");
			Parse_file pf = new Parse_file();
			pf.parse_file(root, files, doc);
		}
		catch (Exception e) 
		{
			e.printStackTrace();  
		}	
		
		
	}
	
	public static void removeWhitespace(Node n) {
		NodeList nl = n.getChildNodes();
		for (int pos = 0, c = nl.getLength(); pos < c; pos++) {
			Node child = nl.item(pos);
			if (child.getNodeType() != Node.TEXT_NODE) {
				removeWhitespace(child);
			}
		}

		// count backwards so that pos is correct even if nodes are removed
		for (int pos = nl.getLength() - 1; pos >= 0; pos--) {
			Node child = nl.item(pos);
			if (child.getNodeType() == Node.TEXT_NODE) {
				// if node's text is made up only of whitespace characters
				if (child.getTextContent().trim().equals("")) {
					n.removeChild(child);
				}
			}
		}
	}

}
