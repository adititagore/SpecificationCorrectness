import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class Parse_req_ens_z3 {

	FileWriter fstream = null;
	BufferedWriter out;

	Hashtable<String, String> var_decld_req =  new Hashtable<String, String>();
	Hashtable<String, String> var_decld_ens = new Hashtable<String, String>();
	ArrayList<String> fun_decld = new ArrayList<String>();
	
	// this method checks the validity of the requires and ensures clause
	// together
	public  void getRequiresandEnsuresValidity(Node formula,
			String files, Document doc) {
		try {
			fstream = new FileWriter(
					"C:\\MY_STUFF\\research\\specification_check\\"
							+ files.substring(0, (files.length() - 4))
							+ "_req_ens_valid.smt2");
			out = new BufferedWriter(fstream);

			Node requires = formula.getFirstChild();
			Node ensures = formula.getLastChild();

			NodeList facts, obligations;
			if(requires.getFirstChild().getNodeName().equals("and"))
				facts = requires.getFirstChild().getChildNodes();
			else
				facts = requires.getChildNodes();

			if(ensures.getFirstChild().getNodeName().equals("and"))
				obligations = ensures.getFirstChild().getChildNodes();
			else
				obligations = ensures.getChildNodes();

			// String cName = doc.getDocumentElement().getAttribute("cName");
			out.write("(set-option :produce-proofs true)");
			out.newLine();
			out.write("(set-option :produce-models true)");
			out.newLine();
			//out.write("(set-option :auto-config false)");
			//out.write("(set-option :mbqi false) ");
			out.write("(set-option :mbqi true)"); 
			out.write("(set-option :pull-nested-quantifiers true)");
		//	out.write("(set-option :mbqi-max-iterations 1000000)");
			
			out.newLine();

			// declare_vars(doc);

			declare_funs(doc);
			getVarsReq(doc);
			getVarsEns(doc);

			//NodeList facts = requires.getChildNodes();
			//NodeList obligations = ensures.getChildNodes();

			out.write("(define-fun conjecture () Bool ");
			if(var_decld_req.size() != 0)
			{
				out.write("(forall (");
				printVarsReq();	
				out.write(")");
			}
			out.write("(exists( ");
			printVarsEns();	
			out.write(")");
			printReq(facts);
			printEns(obligations);
			out.write("))");
			if(var_decld_req.size() != 0)
			{
				out.write(")");
			}

			out.newLine();
			out.write("(assert (not conjecture))");
		//	out.write("(assert conjecture)");
			out.newLine();

			out.write("(check-sat)");
			out.write("(get-model)");
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
				if (!(var_decld_req.contains(symbol))) {

					var_decld_req.put(symbol, type);
					
				}
			}

		}// end for

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
				//	System.out.println("SYMBOLS forund in ensures: "+symbol);
				if (!(var_decld_ens.containsKey(symbol)))
				{
					if(!(var_decld_req.containsKey(symbol)))
					{
						if(mode.equals("restores"))
						{
							var_decld_req.put(symbol, type);
						}
						else
						{
						var_decld_ens.put(symbol, type);
						}
						
					}// if(!(var_decld_req.containsKey(symbol)))
					//else
					//{
					//	var_decld_ens.add(var_decld_req.(symbol));
				//	}
				}

			}

		}// end for		
	}

	public void declare_funs(Document doc) throws IOException
	{
		NodeList card = doc.getElementsByTagName("bar");	
		NodeList div = doc.getElementsByTagName("divides");
		
		
		String cName = doc.getDocumentElement().getAttribute("cName");

		if(cName.contains("Integer"))
		{
			if(card.getLength() > 0)
			{
				out.newLine();
				out.write("(define-fun card ((x Int)) Int (ite (>= x 0) x (- 0 x)))");
				out.newLine();

			}
		}
		if(cName.contains("Integer"))
		{
			if(div.getLength() > 0)
			{
				out.newLine();
			//	out.write("(define-fun mydiv ((x Int) (y Int)) Int (if (not (= y 0)) (/ x y) 0))");
				out.newLine();

			}
		}
		
		NodeList fn = doc.getElementsByTagName("function");

		int numfuns = fn.getLength();

		out.newLine();	

		for (int i = 0; i < numfuns; i++) 
		{
			//System.out.println(vars.item(i).getFirstChild().toString()); 
			String fn_name = fn.item(i).getAttributes().getNamedItem("name").getNodeValue();

			//System.out.println(fn_name);
			if (!(fun_decld.contains(fn_name)))
			{
				fun_decld.add(fn_name);

				if(fn_name.equals("IS_ODD"))
				{
					out.newLine();
				//	out.write("(define-fun IS_ODD ((x Int)) Bool (ite (= (mod x 2) 0) false true))");
				//	out.write("(define-fun IS_ODD ((x Int)) Bool (if (= (mod x 2) 0) false true))");
					out.write("(define-fun IS_ODD ((x Int)) Bool (exists ((k Int)) (= x  (+ (* 2 k) 1))))");
					out.newLine();
				}

			}
		}
	}
	
	public void printVarsReq() throws IOException
	{
		Enumeration symbols;
		String symbol;
		
		symbols = var_decld_req.keys();
		
		while(symbols.hasMoreElements())
		{ 
			symbol = (String) symbols.nextElement(); 
			out.write("("+symbol+ " ");
			if(var_decld_req.get(symbol).equals("integer"))
			{
				out.write("Int)" );
			}
		} 	
	}
	
	public void printVarsEns() throws IOException
	{
		Enumeration symbols;
		String symbol;
		
		symbols = var_decld_ens.keys();
		
		while(symbols.hasMoreElements())
		{ 
			symbol = (String) symbols.nextElement(); 
			out.write("("+symbol+ " ");
			if(var_decld_ens.get(symbol).equals("integer"))
			{
				out.write("Int)" );
			}
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
