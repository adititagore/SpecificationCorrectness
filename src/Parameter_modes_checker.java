import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;

import javax.swing.text.html.HTMLDocument.Iterator;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class Parameter_modes_checker {

	FileWriter fstream = null;
	BufferedWriter out;
	int flag =0;

	Hashtable<String, String> var_decld_req =  new Hashtable<String, String>();
	Hashtable<String, String> var_decld_ens = new Hashtable<String, String>();
	Hashtable<String, String> param_decld = new Hashtable<String, String>();
	
	ArrayList<String> fun_decld = new ArrayList<String>();

	// this method checks the validity of the requires and ensures clause
	// together
	public  int getParamModesValidity(Node root,
			String files, Document doc) {

		Node formula = root.getLastChild();
		
		Node header = root.getFirstChild();
		Node requires = formula.getFirstChild();
		Node ensures = formula.getLastChild();

		NodeList facts, obligations, params;
		
		params = header.getFirstChild().getChildNodes();
		if(requires.getFirstChild().getNodeName().equals("and"))
			facts = requires.getFirstChild().getChildNodes();
		else
			facts = requires.getChildNodes();
		// declare_vars(doc);
		//	declare_funs(doc);
		mode_check_1(doc); // sec 5.1
		mode_check_2(doc); // sec 5.2 (first bullet) and sec 5.3 (first bullet)
		mode_check_3(doc); // sec 5.2 (second bullet)

		//NodeList facts = requires.getChildNodes();
		//NodeList obligations = ensures.getChildNodes();
		//	printVarsReq();	
		//	printVarsEns();	
		//	printReq(facts);
		//	printEns(obligations);
		return flag;
	}

	public void mode_check_1(Document doc)  {
		// This is section 5.1

		NodeList vars = doc.getElementsByTagName("symbol");
		int numSymbols = vars.getLength();
		//		System.out.println("(DEBUG)- The number of symbols" + numSymbols);

		for (int i = 0; i < numSymbols; i++) {
			//	 System.out.println(vars.item(i).getFirstChild().getTextContent().toString());
			String symbol = vars.item(i).getFirstChild().getTextContent();
			//	String type = vars.item(i).getAttributes().getNamedItem("type")
			//		.getNodeValue();
			String mode = vars.item(i).getAttributes().getNamedItem("mode")
					.getNodeValue();

			// System.out.println("(DEBUG1)--" +vars.item(i).getParentNode());
			// System.out.println("(DEBUG2)--"
			// +vars.item(i).getParentNode().getParentNode());
			// System.out.println("(DEBUG3)--"
			// +vars.item(i).getParentNode().getParentNode());
			//i am not going to bother with add old_ prefix to vars now. assume that they are already added in the XML file

			if (vars.item(i).getParentNode().getNodeName().equals("requires")
					|| vars.item(i).getParentNode().getParentNode()
					.getNodeName().equals("requires")
					|| vars.item(i).getParentNode().getParentNode()
					.getParentNode().getNodeName().equals("requires")
					|| vars.item(i).getParentNode().getParentNode()
					.getParentNode().getParentNode().getNodeName().equals("requires")) 
			{
				//System.out.println("The symbol is" + symbol+ " and the mode is " +mode);
				if (!(var_decld_req.contains(symbol)))
				{
					var_decld_req.put(symbol, mode);
				}
			}
		}// end for
		//print the contents 
		Enumeration<String> symbols;
		String symbol, mode;

		symbols = var_decld_req.keys();
		
		while(symbols.hasMoreElements())
		{ 
			symbol = (String) symbols.nextElement();
			mode = var_decld_req.get(symbol);
			if(mode.equals("replaces"))
			{
				System.out.println("Parameter mode error : The variable "+ symbol + 
						" in the requires clause cannot have the replaces mode");
				flag = 1;
			}
		} 	
	}

	public void mode_check_2(Document doc) 
	{

		NodeList vars = doc.getElementsByTagName("symbol");
		int numSymbols = vars.getLength();
		//	System.out.println("(DEBUG)- The number of symbols" + numSymbols);

		for (int i = 0; i < numSymbols; i++)
		{
			// System.out.println(vars.item(i).getFirstChild().toString());
			String symbol = vars.item(i).getFirstChild().getTextContent();
			String type = vars.item(i).getAttributes().getNamedItem("type")
					.getNodeValue();
			String mode = vars.item(i).getAttributes().getNamedItem("mode")
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
					.equals("ensures") || vars.item(i).getParentNode().getParentNode().getParentNode()
					.getParentNode().getParentNode().getNodeName()
					.equals("ensures")) {
				//	System.out.println("SYMBOLS found in ensures: "+symbol);

				if (!(var_decld_ens.containsKey(symbol)))
				{						
					var_decld_ens.put(symbol, mode);
				}			
			}
		}//end for
		
		
		NodeList params = doc.getElementsByTagName("param");
		int numParams = params.getLength();
		
		for (int i = 0; i < numParams; i++)
		{
			// System.out.println(vars.item(i).getFirstChild().toString());
			String symbol = params.item(i).getFirstChild().getTextContent();
			String mode = params.item(i).getAttributes().getNamedItem("mode")
					.getNodeValue();
			
		//param_decld contains all the parameters declared in the header	
			if (!(param_decld.containsKey(symbol)))
			{						
				param_decld.put(symbol, mode);
			}	
			
		}//end for
		
		//creating a hashset of the params with clears mode
		
		HashSet<String> clears_params = new HashSet<String>();
		Enumeration<String> p;
		String s;
		String m;

		p = param_decld.keys();
		while(p.hasMoreElements())
		{ 
			s = p.nextElement();
			m = var_decld_ens.get(s);
			
			if(m!=null)
			{
			if(m.equals("clears"))
			{
				clears_params.add(s);
			}
			}
		}
			
		

		/*	// the objective is to look at all the variables that have updates mode and then see
		//whether the hashtable var_decl_ens contains both the new and the old values of the
		// variable (eg. n and #n)

		HashMap<String, String> var_ens_rev = new HashMap<String, String>();

		Enumeration<String> symbols;
		String symbol, mode;

		symbols = var_decld_ens.keys();

		while(symbols.hasMoreElements())
		{ 
			symbol = (String) symbols.nextElement();
			mode = var_decld_req.get(symbol);
			if(mode.equals("updates"))
			{
				var_ens_rev.put(mode,symbol);
			}
		}//end while*/

		//doing for the updates mode and clears mode 
		HashSet<String> ens_vars_upd = new HashSet<String>();
		HashSet<String> ens_vars_upd_old = new HashSet<String>();
		HashSet<String> ens_vars_clears = new HashSet<String>();
		HashSet<String> ens_vars_clears_old = new HashSet<String>();

		Enumeration<String> symbols;
		String symbol, mode;

		symbols = var_decld_ens.keys();
		while(symbols.hasMoreElements())
		{ 
			symbol = (String) symbols.nextElement();
			mode = var_decld_ens.get(symbol);
			if(mode.equals("updates"))
			{
				if(!symbol.startsWith("old"))
					ens_vars_upd.add(symbol);
				else 
					ens_vars_upd_old.add(symbol);
			}
			
			
			if(mode.equals("clears"))
			{
				if(!symbol.startsWith("old_"))
				{
					flag = 1;
					//this is section 5.3 (first bullet)-- warning 1
					System.out.println("Parameter mode Warning: The outgoing value of the variable "+symbol+
							" should not be present "); 
					System.out.println("in the postcondition since the mode of this variable is denoted as clears");
					ens_vars_clears.add(symbol);
				}
				else 
					ens_vars_clears_old.add(symbol);
			}
		}//end while
		
		
	//	System.out.println(ens_vars_upd.toString());
	//	System.out.println(ens_vars_upd_old.toString());

		for(String s1: ens_vars_upd)
		{
			String old_s = "old_"+s1;
			if(!(ens_vars_upd_old.contains(old_s)))
			{
				//System.out.println(old_s + " "+ s);
				
				//this is sec 5.2 (first bullet)
				System.out.println("Error detected: the parameter mode updates is used" +
						" and yet the incoming value of " +s1 + " is missing in the post-condition");
				flag = 1;
			}
		}
		
		for(String s1: ens_vars_clears)
		{
			String old_s = "old_"+s1;
			if(!(ens_vars_clears_old.contains(old_s)))
			{
				//System.out.println(old_s + " "+ s);
				
				//this is sec 5.2 (first bullet)
				System.out.println("Error detected: the parameter mode clears is used" +
						" and yet the incoming value of " +s1 + " is missing in the post-condition");
				flag = 1;
			}
		}
	}		


	public void mode_check_3(Document doc) 
	{
		
	
		//creating a hashset of the params with clears mode
		
		HashSet<String> clears_params = new HashSet<String>();
		Enumeration<String> p;
		String s;
		String m;

		p = param_decld.keys();
		while(p.hasMoreElements())
		{ 
			s = p.nextElement();
			m = var_decld_ens.get(s);
			
			if(m!=null)
			{
			if(m.equals("clears"))
			{
				clears_params.add(s);
			}
			}
		}
	
	 //doing for the restores mode and replaces mode 
	//	HashSet<String> ens_vars_upd = new HashSet<String>();
		HashSet<String> ens_vars_rest_old = new HashSet<String>();
		//HashSet<String> ens_vars_clears = new HashSet<String>();
		HashSet<String> ens_vars_repl_old = new HashSet<String>();

		Enumeration<String> symbols;
		String symbol, mode;

		symbols = var_decld_ens.keys();
		while(symbols.hasMoreElements())
		{ 
			symbol = (String) symbols.nextElement();
			mode = var_decld_ens.get(symbol);
			if(mode.equals("restores"))
			{
				if(symbol.startsWith("old"))
					ens_vars_rest_old.add(symbol);
			}
			
			
			if(mode.equals("replaces"))
			{	
				if(symbol.startsWith("old"))
					ens_vars_repl_old.add(symbol);
			}
		}//end while
		
		
	//	System.out.println(ens_vars_upd.toString());
	//	System.out.println(ens_vars_upd_old.toString());
		
		if(ens_vars_rest_old.size() !=0)
		{
			//This is sec 5.2 (second bullet)
			System.out.println("Error detected: the parameter mode restores is used, ");
			System.out.println("yet the incoming values of the following variables ");
						
			for(String s1: ens_vars_rest_old)
			{
				System.out.println(s1.substring(4));
			}
			System.out.println("are detected in the ensures clause");
			
			flag = 1;
		}
		
		if(ens_vars_repl_old.size() !=0)
		{
			//This is sec 5.2 (second bullet)
			System.out.println("Error detected: the parameter mode replaces is used, ");
			System.out.println("yet the incoming values of the following variables ");			
			
			for(String s1: ens_vars_repl_old)
			{
				System.out.println(s1.substring(4));
			}
			System.out.println("are detected in the ensures clause");
			
			flag = 1;
		}
		
	
	}
	public void traverseTree(Node node, Document doc) throws Exception {

		//NodeList card = null;
		//card = doc.getElementsByTagName("bar");
		//String cName = doc.getDocumentElement().getAttribute("cName");

		// Extract node info:
		String elementName = node.getNodeName();
		//String val = node.getNodeValue();

		if (elementName.equals("neq")) {
			// read_binary_operand(node);

			out.write(" (not ( = ");
			traverseTree(node.getFirstChild(), doc);
			traverseTree(node.getLastChild(), doc);
			out.write(") )");
		}

		else if (elementName.equals("eq")) {

			out.write(" (= ");
			traverseTree(node.getFirstChild(), doc);
			traverseTree(node.getLastChild(), doc);
			out.write(") ");
		}

		else if (elementName.equals("geq")) {

			out.write(" (>= ");
			traverseTree(node.getFirstChild(), doc);
			traverseTree(node.getLastChild(), doc);
			out.write(")");
		}

		else if (elementName.equals("leq")) {

			out.write(" (<= ");
			traverseTree(node.getFirstChild(), doc);
			traverseTree(node.getLastChild(), doc);
			out.write(")");
		}

		else if (elementName.equals("gt")) {

			out.write(" (> ");
			traverseTree(node.getFirstChild(), doc);
			traverseTree(node.getLastChild(), doc);
			out.write(")");
		}

		else if (elementName.equals("lt")) {

			out.write(" (< ");
			traverseTree(node.getFirstChild(), doc);
			traverseTree(node.getLastChild(), doc);
			out.write(")");
		}
		else if (elementName.equals("implies")) {

			out.write(" (=> ");
			traverseTree(node.getFirstChild(), doc);
			traverseTree(node.getLastChild(), doc);
			out.write(")");
		}

		else if (elementName.equals("add")) // for integers
		{
			out.write("(+ ");
			traverseTree(node.getFirstChild(), doc);
			traverseTree(node.getLastChild(), doc);
			out.write(") ");
		}
		/*else if (elementName.equals("and")) // for integers
		{
			out.write("(and ");
			traverseTree(node.getFirstChild(), doc);
			traverseTree(node.getLastChild(), doc);
			out.write(") ");
		}*/

		else if (elementName.equals("subtract")) // for integers
		{
			out.write("(- ");
			traverseTree(node.getFirstChild(), doc);
			traverseTree(node.getLastChild(), doc);
			out.write(") ");
		} 
		else if (elementName.equals("star")) // for integers
		{
			out.write("(* ");
			traverseTree(node.getFirstChild(), doc);
			traverseTree(node.getLastChild(), doc);
			out.write(") ");
		}
		else if (elementName.equals("divides")) // for integers
		{
			out.write("(div ");
			traverseTree(node.getFirstChild(), doc);
			traverseTree(node.getLastChild(), doc);
			out.write(") ");
		} 
		else if (elementName.equals("union")) {
			out.write("(union ");
			traverseTree(node.getFirstChild(), doc);
			traverseTree(node.getLastChild(), doc);
			out.write(") ");
		}

		else if (elementName.equals("intersection")) {
			out.write("(intersect ");
			traverseTree(node.getFirstChild(), doc);
			traverseTree(node.getLastChild(), doc);
			out.write(") ");
		}

		else if (elementName.equals("bar")) {
			String type = node.getFirstChild().getAttributes().getNamedItem("type").getNodeValue();;

			if(type.equals("finiteset(object)") || type.equals("integer") || type.equals("finiteset(tuple(d:object,r:object))"))
			{
				out.write("(card ");
				traverseTree(node.getFirstChild(), doc);
				out.write(") ");
				}			
		}

		else if (elementName.equals("negate")) {
			out.write("(- ");
			traverseTree(node.getFirstChild(), doc);
			out.write(") ");
		} 
		else if (elementName.equals("difference")) {
			out.write("(difference ");
			traverseTree(node.getFirstChild(), doc);
			traverseTree(node.getLastChild(), doc);
			out.write(") ");
		}

		else if (elementName.equals("singleton")) {
			out.write("(singleton ");
			traverseTree(node.getFirstChild(), doc);
			out.write(") ");
		} else if (elementName.equals("symbol")) {
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
			}
			else
			{
				out.write(" " + symbol + " ");
			}*/

			out.write(" " + symbol + " ");
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
			out.write(" empty");
			// traverseTree(node.getFirstChild());
		}

		else if (elementName.equals("zero")) {
			out.write(" 0");
			// traverseTree(node.getFirstChild());
		}
		else if (elementName.equals("true")) {
			out.write(" true");
			// traverseTree(node.getFirstChild());
		}

		else if (elementName.equals("is_initial")) {

			out.write(" (is_initial ");
			traverseTree(node.getFirstChild(), doc);
			out.write(") ");
		}

		else if (elementName.equals("element")) {
			/*
			 * if(node.getParentNode().getNodeName().equals("not"))
			 * out.write(" select "); else { //out.newLine();
			 * out.write(" (select "); } traverseTree(node.getLastChild());
			 * traverseTree(node.getFirstChild());
			 * 
			 * if(node.getParentNode().getNodeName().equals("not"))
			 * out.write(" "); else out.write(") "); //System.out.print(") ");
			 */

			out.write(" (select ");

			traverseTree(node.getLastChild(), doc);
			traverseTree(node.getFirstChild(), doc);

			out.write(") ");
		}

		else if (elementName.equals("not")) {

			out.write("(not ");
			traverseTree(node.getFirstChild(), doc);
			out.write(")  ");
		}

		else if(elementName.equals("function"))
		{
			String name = node.getAttributes().getNamedItem("name").getNodeValue();
			String type = node.getAttributes().getNamedItem("type").getNodeValue();

			out.write("("+name+" ");

			NodeList args = node.getChildNodes();
			int a = args.getLength();

			//findMethods(doc);
			if (a> 1) 
			{
				for (int j = 0; j < a-1; j++)
				{
					traverseTree(args.item(j).getFirstChild(), doc);
					out.write(" ");
				}
			}
			traverseTree(args.item(a-1).getFirstChild(), doc);
			out.write(")  ");
		}




	}// end traverseTree

	public void printReq(NodeList facts) throws Exception
	{
		int f = facts.getLength();
		if (f > 1)
		{
			out.write("(=> (and");
			for (int j = 0; j < f; j++)
			{
				traverseTree(facts.item(j), null);

			}
			out.write(")");

		}// end if (f> 1)

		else if (f>0)
		{
			out.write("(=> ");
			for (int j = 0; j < f; j++)
			{
				traverseTree(facts.item(j), null);				
			}
			//	out.write(")");
		}
	}

	public void printEns(NodeList obligations) throws Exception
	{

		int o = obligations.getLength();
		if (o > 1)
		{
			out.write(" (and");
			for (int j = 0; j < o; j++)
			{


				traverseTree(obligations.item(j), null);

			}
			out.write("))");

		}// end if (f> 1)

		else if (o>0)
		{

			for (int j = 0; j < o; j++)
			{

				//out.write("(");
				traverseTree(obligations.item(j), null);
				//out.write(")");
			}
			out.write(")");
		}

	}

}

