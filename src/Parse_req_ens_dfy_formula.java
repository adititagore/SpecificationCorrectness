import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Hashtable;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class Parse_req_ens_dfy_formula {

	FileWriter fstream = null;
	BufferedWriter out;
	//ArrayList<String> var_decld_req = new ArrayList<String>();
	Hashtable<String, String> var_decld_req =  new Hashtable<String, String>(); 
	Hashtable<String, String> var_decld_ens = new Hashtable<String, String>();

	// this method checks teh validity of the requires and ensures clause
	// together
	public  void getRequiresandEnsuresValidity(Node formula,
			String files, Document doc) {
		try {
			fstream = new FileWriter(
					"C:\\MY_STUFF\\research\\specification_check\\"
							+ files.substring(0, (files.length() - 4))
							+ "_req_ens_valid.dfy");
			out = new BufferedWriter(fstream);

			Node requires = formula.getFirstChild();
			Node ensures = formula.getLastChild();

			// String cName = doc.getDocumentElement().getAttribute("cName");
			// declare_vars(doc);

			NodeList facts, obligations;
			if(requires.getFirstChild().getNodeName().equals("and"))
				facts = requires.getFirstChild().getChildNodes();
			else
				facts = requires.getChildNodes();
			
			if(ensures.getFirstChild().getNodeName().equals("and"))
				obligations = ensures.getFirstChild().getChildNodes();
			else
				obligations = ensures.getChildNodes();
			
			out.write("class Client<T>{");
			out.newLine();
			declare_funs(doc);
			out.write("method formula() {");
			out.newLine();
			declare_lemmas(doc);
			out.newLine();
			out.write("assert ");
			out.write("forall ");
			getVarsReq(doc);
			out.write(" :: ");
			out.write(" exists ");
			getVarsEns(doc);
			out.write(" :: ");
			printReq(facts, doc);
			printEns(obligations, doc);
			out.write(";");
			out.newLine();
			out.write("} }");
			out.newLine();
			out.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void getVarsReq(Document doc) throws IOException {

		NodeList vars = doc.getElementsByTagName("symbol");
		int numSymbols = vars.getLength();
		//		System.out.println("(DEBUG)- The number of symbols" + numSymbols);



		for (int i = 0; i < numSymbols; i++) {
			// System.out.println(vars.item(i).getFirstChild().getTextContent().toString());
			String symbol = vars.item(i).getFirstChild().getTextContent();
			String type = vars.item(i).getAttributes().getNamedItem("type")
					.getNodeValue();

			
			//i am not going to bother with add old_ prefix to vars now. assume that they are already added in the XML file

			if (vars.item(i).getParentNode().getNodeName().equals("requires")
					|| vars.item(i).getParentNode().getParentNode()
					.getNodeName().equals("requires")
					|| vars.item(i).getParentNode().getParentNode()
					.getParentNode().getNodeName().equals("requires")
					|| vars.item(i).getParentNode().getParentNode()
					.getParentNode().getParentNode().getNodeName().equals("requires")) 
			{
				if (!(var_decld_req.containsKey(symbol))) {

					if (type.equals("integer"))
						type = "int";
					else if (type.equals("string(object)"))
						type = "seq<T>";
					else if (type.equals("object"))
						type = "T";
					var_decld_req.put(symbol, type);
					
				}
			}

		}// end for
		
		int size = var_decld_req.size();
		 Enumeration em=var_decld_req.keys();
		
		 int cnt = 0;
	        while(em.hasMoreElements())
	        {
	            //nextElement is used to get key of Hashtable
	            String key = (String)em.nextElement();

	            //get is used to get value of key in Hashtable
	            String value = var_decld_req.get(key);

	            out.write(" "+key+" :"+value);
	            
	            cnt++;
	            if(cnt < size)
	            	out.write(", ");
	        }

		//print all the elements of the arraylist var_decld_req
		//int n = var_decld_req.size();
		//for (int j = 0; j < n; j++)
		//System.out.println(var_decld_req.values());
		//System.out.println(var_decld_req.keySet());

		//retrieve all the elements in the arraylist and print em out
	}

	public void getVarsEns(Document doc) throws IOException {

		NodeList vars = doc.getElementsByTagName("symbol");
		int numSymbols = vars.getLength();
		//	System.out.println("(DEBUG)- The number of symbols" + numSymbols);

		//	out.newLine();

		for (int i = 0; i < numSymbols; i++)
		{
			// System.out.println(vars.item(i).getFirstChild().toString());
			String symbol = vars.item(i).getFirstChild().getTextContent();
			String type = vars.item(i).getAttributes().getNamedItem("type")
					.getNodeValue();

			// System.out.println("(DEBUG1)--" +vars.item(i).getParentNode());
			// System.out.println("(DEBUG2)--"
			// +vars.item(i).getParentNode().getParentNode());
			// System.out.println("(DEBUG3)--"
			// +vars.item(i).getParentNode().getParentNode());
			if (vars.item(i).getParentNode().getNodeName().equals("ensures")
					|| vars.item(i).getParentNode().getParentNode()
					.getNodeName().equals("ensures")
					|| vars.item(i).getParentNode().getParentNode()
					.getParentNode().getNodeName().equals("ensures")
					|| vars.item(i).getParentNode().getParentNode()
					.getParentNode().getParentNode().getNodeName()
					.equals("ensures")) {
			//	System.out.println("SYMBOLS forund in ensures: "+symbol);
				if (!(var_decld_ens.contains(symbol)))
				{
					if(!(var_decld_req.containsKey(symbol)))
					{
						if (type.equals("integer"))
							type = "int";
						else if (type.equals("string(object)"))
							type = "seq<T>";
						else if (type.equals("object"))
							type = "T";
						var_decld_ens.put(symbol, type);
											
					}// if(!(var_decld_req.containsKey(symbol)))
					else
					{
					//	var_decld_ens.add(var_decld_req.get(symbol));
					}
				}//end if(!(var_decld_ens.contains(symbol)))	
			}

		}// end for

		int size = var_decld_ens.size();
		 Enumeration em=var_decld_ens.keys();
		
		 int cnt = 0;
	        while(em.hasMoreElements())
	        {
	            //nextElement is used to get key of Hashtable
	            String key = (String)em.nextElement();

	            //get is used to get value of key in Hashtable
	            String value = var_decld_ens.get(key);

	            out.write(" "+key+" :"+value);
	            
	            cnt++;
	            if(cnt < size)
	            	out.write(", ");
	        }
		/*	Print all the elements of the arraylist var_decld_ens
		 * int n = var_decld_ens.size();
		for (int j = 0; j < n; j++)
			System.out.println(var_decld_ens.get(j));*/

	}

	public void traverseTree(Node node, Document doc) throws Exception {
		String cName = doc.getDocumentElement().getAttribute("cName");
		// Extract node info:
		String elementName = node.getNodeName();
		String val = node.getNodeValue();

		if(elementName.equals("neq"))
		{
			out.write("(");
			traverseTree(node.getFirstChild(), doc);
			out.write(" != ");
			traverseTree(node.getLastChild(), doc);
			out.write(")");
		}

		else if(elementName.equals("eq"))
		{
			out.write("(");
			traverseTree(node.getFirstChild(), doc);
			out.write(" == ");
			traverseTree(node.getLastChild(), doc);
			out.write(")");

		}					 

		else if(elementName.equals("geq"))
		{
			out.write("(");
			traverseTree(node.getFirstChild(), doc);
			out.write(" >= ");
			traverseTree(node.getLastChild(), doc);
			out.write(")");
		}

		else if(elementName.equals("leq"))
		{
			out.write("(");
			traverseTree(node.getFirstChild(), doc);
			out.write(" <= ");
			traverseTree(node.getLastChild(), doc);
			out.write(")");
		}

		else if(elementName.equals("gt"))
		{
			out.write("(");
			traverseTree(node.getFirstChild(), doc);
			out.write(" > ");
			traverseTree(node.getLastChild(), doc);
			out.write(")");
		}

		else if(elementName.equals("lt"))
		{
			out.write("(");
			traverseTree(node.getFirstChild(), doc);
			out.write(" < ");
			traverseTree(node.getLastChild(), doc);
			out.write(")");
		}
		/*else if(elementName.equals("and"))
		{
			out.write("(");
			traverseTree(node.getFirstChild(), doc);
			out.write(" && ");
			traverseTree(node.getLastChild(), doc);
			out.write(")");
		}*/
		else if(elementName.equals("or"))
		{
			out.write("(");
			traverseTree(node.getFirstChild(), doc);
			out.write(" || ");
			traverseTree(node.getLastChild(), doc);
			out.write(")");
		}
		else if(elementName.equals("add")) // for integers
		{
			out.write("(");
			traverseTree(node.getFirstChild(), doc);
			out.write(" + ");
			traverseTree(node.getLastChild(), doc);
			out.write(")");
		}

		else if(elementName.equals("subtract")) // for integers
		{
			out.write("(");
			traverseTree(node.getFirstChild(), doc);
			out.write(" - ");
			traverseTree(node.getLastChild(), doc);
			out.write(")");
		}

		else if(elementName.equals("star")) // for integers
		{	
			out.write("(");
			traverseTree(node.getFirstChild(), doc);
			if(cName.contains("String") || cName.contains("string"))
			{
				out.write(" + ");
			}
			else
				out.write(" * ");
			traverseTree(node.getLastChild(), doc);
			out.write(")");
		}


		else if(elementName.equals("union"))
		{
			out.write("( ");
			traverseTree(node.getFirstChild(), doc);
			out.write(" + ");
			traverseTree(node.getLastChild(), doc);
			out.write(") ");
		}

		else if(elementName.equals("intersection"))
		{
			out.write("( ");
			traverseTree(node.getFirstChild(), doc);
			out.write(" * ");
			traverseTree(node.getLastChild(), doc);
			out.write(") ");
		}
		

		else if(elementName.equals("bar"))
		{
			if(cName.contains("set"))
			{
				out.write("(card (");
				traverseTree(node.getFirstChild(), doc);
				out.write(")) ");
			}
			else if(cName.contains("string") )
			{
				out.write("(|");
				traverseTree(node.getFirstChild(), doc);
				out.write("|) ");
			}
		}

		else if(elementName.equals("negate"))
		{
			out.write("(- ");
			traverseTree(node.getFirstChild(), doc);
			out.write(") ");
		}
		else if(elementName.equals("difference"))
		{
			out.write("( ");
			traverseTree(node.getFirstChild(), doc);
			out.write(" - ");
			traverseTree(node.getLastChild(), doc);
			out.write(") ");
		}

		else if(elementName.equals("implies"))
		{
			out.write("( ");
			traverseTree(node.getFirstChild(), doc);
			out.write(" ==> ");
			traverseTree(node.getLastChild(), doc);
			out.write(") ");
		}
		else if(elementName.equals("iff"))
		{
			out.write("( ");
			traverseTree(node.getFirstChild(), doc);
			out.write(" <==> ");
			traverseTree(node.getLastChild(), doc);
			out.write(") ");
		}
		else if(elementName.equals("singleton"))
		{
			out.write("({ ");
			traverseTree(node.getFirstChild(), doc);
			out.write("}) ");
		}
		else if(elementName.equals("stringleton"))
		{
			out.write("([ ");
			traverseTree(node.getFirstChild(), doc);
			out.write("]) ");
		}
		else if(elementName.equals("substring") || elementName.equals("SUBSTRING"))
		{
			out.write("substring( ");
			traverseTree(node.getFirstChild(), doc);
			out.write(",");
			traverseTree(node.getFirstChild().getNextSibling(), doc);
			out.write(",");
			traverseTree(node.getLastChild(), doc);
			out.write(")");
		}
		
		else if (elementName.equals("symbol")) {
			// op = read_text_set(node);
			String symbol = node.getFirstChild().getTextContent();

			/*if (node.getParentNode().getNodeName().equals("requires")
					|| node.getParentNode().getParentNode()
					.getNodeName().equals("requires")
					|| node.getParentNode().getParentNode()
					.getParentNode().getNodeName().equals("requires")
					|| node.getParentNode().getParentNode()
					.getParentNode().getParentNode().getNodeName().equals("requires")) 
			{
				String s = (String) var_decld_req.get(symbol);
				out.write(" " + s + " ");
			}*/
			//else
			//{
				out.write(" " + symbol + " ");
			//}
			// traverseTree(node.getFirstChild());
		}

		else if (elementName.equals("constant")) {
			// op = read_text_set(node);
			String constant = node.getFirstChild().getTextContent();

			// check whether the symbol has been declared or not

			out.write(" " + constant + " ");
			// traverseTree(node.getFirstChild());
		}

		else if (elementName.equals("emptyset")) {
			out.write(" {}");
			// traverseTree(node.getFirstChild());
		}

		else if (elementName.equals("zero")) {
			out.write(" 0");
			// traverseTree(node.getFirstChild());
		}

		else if (elementName.equals("is_initial")) {

			out.write(" (is_initial ");
			traverseTree(node.getFirstChild(), doc);
			out.write(") ");
		}

		else if (elementName.equals("element"))
		{
			out.write(" ( ");
			traverseTree(node.getFirstChild(), doc);
			out.write(" in ");
			traverseTree(node.getLastChild(), doc);
		
			out.write(") ");
		}

		else if (elementName.equals("not"))
		{
			out.write("( ! ");
			traverseTree(node.getFirstChild(), doc);
			out.write(")  ");
		}

	}// end traverseTree

	public void printReq(NodeList facts , Document doc) throws Exception
	{
		int f = facts.getLength();
		if (f > 1)
		{
			for (int j = 0; j < f-1; j++)
			{
				traverseTree(facts.item(j), doc);
				out.write(" && ");
			
			}
			traverseTree(facts.item(f-1), doc);
			out.write(" ==> ");

		}// end if (f> 1)

		else if (f>0)
		{
			traverseTree(facts.item(f-1), doc);
			out.write(" ==> ");
		}
		
	}

	public void printEns(NodeList obligations, Document doc) throws Exception
	{

		int o = obligations.getLength();

		if(o > 1)
		{
			for (int j = 0; j < o-1; j++)
			{
				
			//out.write("(");
			traverseTree(obligations.item(j), doc);
			out.write(" && ");
			}

			traverseTree(obligations.item(o-1), doc);
		}
		else if (o>0)
		{				
				traverseTree(obligations.item(o-1), doc);					
		}
		//out.write(")");
	}
	
	public void declare_funs(Document doc) throws IOException
	{
		NodeList substring = doc.getElementsByTagName("substring");
		
		if(substring.getLength() > 0)
		{
			out.newLine();	
			out.write("function substring(s: seq<T>, start : int, finish: int) : seq<T>");
			out.newLine();
			out.newLine();
		}
		
	}
	
	public void declare_lemmas(Document doc) throws IOException
	{
		NodeList substring = doc.getElementsByTagName("substring");
		
		if(substring.getLength() > 0 )
		{
			out.newLine();	
			out.write("//substring definition");
			out.newLine();	
			out.write("assume ( forall s: seq<T>, start : int, finish: int ::(start < 0 || start > finish || finish > |s| ==> substring(s, start, finish) == []) && (!(start < 0 || start > finish || finish > |s|) ==> (exists a:seq<T>, b:seq<T> :: s == a + substring(s, start, finish) + b && |a| == start&& |b| == |s| - finish)));");
			out.newLine();
			out.write("//substring lemmas");
			out.newLine();	
			out.write("assume (forall s: seq<T>, start: int, finish: int :: start < 0 || start > finish || finish > |s| ==> substring(s, start, finish) == []);");
			out.newLine();
			out.write("assume (forall s: seq<T>, start: int, finish: int :: |s| == 0 ==> substring(s, start, finish) == []);");
			out.newLine();
			out.write("assume (forall m: int, n: int, a: seq<T> :: 0 <= m && m <= n && n <= |a| ==> substring(a, 0, m) + substring(a, m, n) == substring(a, 0, n));");
			out.newLine();
			out.write("assume (forall m: int, n: int, a: seq<T> :: 0 <= m && m <= n && n <= |a| ==> substring(a, 0, m) + substring(a, m, n) + substring(a, n, |a|) == a);");
			out.newLine();
			out.write("assume (forall j: int, k: int, s1: seq<T>, s2: seq<T> :: 0 <= j && j <= k && k <= |s1| ==> substring(s1 + s2, j, k) == substring(s1, j, k));");
			out.newLine();
			out.write("assume (forall j: int, k: int, s1: seq<T>, s2: seq<T> :: |s1| <= j && j <= k && k <= (|s1| + |s2|) ==> substring(s1 + s2, j, k) == substring(s2, j - |s1|, k - |s1|));");
			out.newLine();
			out.newLine();
		}
	}
	

}
