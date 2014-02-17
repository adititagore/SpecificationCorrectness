import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

//this file actually parses the requires and ensures clauses separately and creates separate smt2 files
 class Parse_file {
	
	FileWriter fstream; 
	BufferedWriter out;
	ArrayList<String> var_decld = new ArrayList<String>();
	ArrayList<String> fun_decld = new ArrayList<String>();
	
	public void parse_file(Node root, String files, Document doc)
	{
		Node formula = root.getLastChild();
		//System.out.println(formula.getNodeName().toString());
		
		
		//calling to make syntactic checks on the parameter modes
		//int flag = 0;
		//Parameter_modes_checker param = new Parameter_modes_checker();
		//flag = param.getParamModesValidity(root, files, doc);
		
		//if(flag ==0)
		//{
			//System.out.println("No error in param modes");
			//getRequires(formula, files, doc);
			//getEnsures(formula, files, doc);
			
			//calling to check the validity of the 2 clauses together using Z3
			Parse_req_ens_z3 pre = new Parse_req_ens_z3();
			pre.getRequiresandEnsuresValidity(formula, files, doc);
			
			//calling to check the validity of the 2 clauses together using dafny --
			//the standard formula
		//	Parse_req_ens_dfy_formula pre1 = new Parse_req_ens_dfy_formula();
			//pre1.getRequiresandEnsuresValidity(formula, files, doc);
			
			//calling to check the validity of the 2 clauses together using dafny --
			//assume and assert -- so that dafny proves more
		//	Parse_req_ens_dfy_alt pre2 = new Parse_req_ens_dfy_alt();
		//	pre2.getRequiresandEnsuresValidity(formula, files, doc);
			
		//}
		
		
		
	}
	
	//this method looks for contradictions in the requires clause
	public void getRequires(Node formula, String files, Document doc)
	{
		try
		{
		fstream = new FileWriter("C:\\MY_STUFF\\research\\specification_check\\" + files.substring(0, (files.length() - 4)) + "_req.smt2");
		out = new BufferedWriter(fstream);

		Node requires = formula.getFirstChild();
		//System.out.println(requires.getNodeName().toString());
	//	String cName = doc.getDocumentElement().getAttribute("cName");
		out.write("(set-option :produce-proofs true)");
		out.newLine();
		out.write("(set-option :produce-models true)");
		out.newLine();
		
		declare_vars(doc);
		
		NodeList facts;// = requires.getChildNodes();
		
	//	System.out.println("requires.getFirstChild().getNodeName()"+requires.getFirstChild().getNodeName());
		if(requires.getFirstChild().getNodeName().equals("and"))
			facts = requires.getFirstChild().getChildNodes();
		else
			 facts = requires.getChildNodes();
		
		int f = facts.getLength();
	//	System.out.println("f is" +f);
		
		if (f> 0) 
		{
			for (int j = 0; j < f; j++)
			{
				out.newLine();
				out.write("(assert ");
				traverseTree(facts.item(j));
				out.write(" )");
			}
		}//end if (f> 0)

		out.newLine();
		out.write("(check-sat)");
		out.newLine();
		
		var_decld.clear();
		
		out.close();
		
		}
		catch (Exception e) 
		{
			e.printStackTrace();  
		}
	}
	
	//this method looks for contradictions in the ensures clause
	public void getEnsures(Node formula, String files, Document doc)
	{
		try
		{
		fstream = new FileWriter("C:\\MY_STUFF\\research\\specification_check\\" + files.substring(0, (files.length() - 4)) + "_ens.smt2");
		out = new BufferedWriter(fstream);
		
		
		Node ensures = formula.getLastChild();
		
		//String cName = doc.getDocumentElement().getAttribute("cName");
		out.write("(set-option :produce-proofs true)");
		out.newLine();
		out.write("(set-option :produce-models true)");
		out.newLine();
		
		declare_vars(doc);
		NodeList obligations;// = ensures.getChildNodes();
		
		if(ensures.getFirstChild().getNodeName().equals("and"))
			obligations = ensures.getFirstChild().getChildNodes();
		else
			obligations = ensures.getChildNodes();
		int f = obligations.getLength();
	
		
		//System.out.println("f is" +f);
		
		if (f> 0) 
		{
			for (int j = 0; j < f; j++)
			{
				out.newLine();
				out.write("(assert ");
				traverseTree(obligations.item(j));
				out.write(" )");
			}
		}//end if (f> 0)

		out.newLine();
		out.write("(check-sat)");
		out.newLine();
		var_decld.clear();
		out.close();
		

		}
		catch (Exception e) 
		{
			e.printStackTrace();  
		}
	}//end getEnsures
	
	public void traverseTree(Node node) throws Exception
	{
		// Extract node info:
				String elementName = node.getNodeName();
				String val = node.getNodeValue();


				if(elementName.equals("neq"))
				{
					//read_binary_operand(node);
					
					out.write(" (not ( = ");
					traverseTree(node.getFirstChild());
					traverseTree(node.getLastChild());
					out.write(") )");
				}

				else if(elementName.equals("eq"))
				{
					
					out.write(" ( = ");

					traverseTree(node.getFirstChild());
					traverseTree(node.getLastChild());
					out.write(") ");
				}					 

				else if(elementName.equals("geq"))
				{
					
					out.write(" ( >= ");
					traverseTree(node.getFirstChild());
					traverseTree(node.getLastChild());
					out.write(")");
				}
				
				else if(elementName.equals("leq"))
				{
					
					out.write(" ( <= ");
					traverseTree(node.getFirstChild());
					traverseTree(node.getLastChild());
					out.write(")");
				}
				
				else if(elementName.equals("gt"))
				{
					
					out.write(" ( > ");
					traverseTree(node.getFirstChild());
					traverseTree(node.getLastChild());
					out.write(")");
				}
				
				else if(elementName.equals("lt"))
				{
					
					out.write(" ( < ");
					traverseTree(node.getFirstChild());
					traverseTree(node.getLastChild());
					out.write(")");
				}
				
				else if(elementName.equals("add")) // for integers
				{
					out.write("(+ ");
					traverseTree(node.getFirstChild());
					traverseTree(node.getLastChild());
					out.write(") ");
				}
				
				else if(elementName.equals("subtract")) // for integers
				{
					out.write("(- ");
					traverseTree(node.getFirstChild());
					traverseTree(node.getLastChild());
					out.write(") ");
				}
				else if(elementName.equals("star")) // for integers
				{
					out.write("(* ");
					traverseTree(node.getFirstChild());
					traverseTree(node.getLastChild());
					out.write(") ");
				}
				else if(elementName.equals("union"))
				{
					out.write("(union ");
					traverseTree(node.getFirstChild());
					traverseTree(node.getLastChild());
					out.write(") ");
				}
				
				else if(elementName.equals("intersection"))
				{
					out.write("(intersect ");
					traverseTree(node.getFirstChild());
					traverseTree(node.getLastChild());
					out.write(") ");
				}

				else if(elementName.equals("bar"))
				{
					out.write("(card ");
					traverseTree(node.getFirstChild());
					out.write(") ");
				}

				else if(elementName.equals("negate"))
				{
					out.write("(- ");
					traverseTree(node.getFirstChild());
					out.write(") ");
				}
				else if(elementName.equals("difference"))
				{
					out.write("(difference ");
					traverseTree(node.getFirstChild());
					traverseTree(node.getLastChild());
					out.write(") ");
				}

				else if(elementName.equals("singleton"))
				{
					out.write("(singleton ");
					traverseTree(node.getFirstChild());
					out.write(") ");
				}
				else if(elementName.equals("symbol"))
				{
					//op = read_text_set(node);
					String symbol = node.getFirstChild().getTextContent();

					//check whether the symbol has been declared or not

					out.write(" " +symbol + " ");
					//traverseTree(node.getFirstChild());
				}
				
				else if(elementName.equals("true"))
				{
					out.write(" true ");
				}
				else if(elementName.equals("false"))
				{
					out.write(" false ");
				}
				
				else if(elementName.equals("constant"))
				{
					//op = read_text_set(node);
					String constant = node.getFirstChild().getTextContent();

					//check whether the symbol has been declared or not

					out.write(" " +constant + " ");
					//traverseTree(node.getFirstChild());
				}

				else if(elementName.equals("emptyset"))
				{
					out.write(" empty");
					//traverseTree(node.getFirstChild());
				}

				else if(elementName.equals("zero"))
				{
					out.write(" 0");
					//  traverseTree(node.getFirstChild());
				}

				else if(elementName.equals("is_initial"))
				{
					
					out.write(" (is_initial ");
					traverseTree(node.getFirstChild());
					out.write(") ");
				}

				else if(elementName.equals("element"))
				{
					/*if(node.getParentNode().getNodeName().equals("not"))
						out.write(" select ");
					else
					{
						//out.newLine();
						out.write(" (select ");
					}
					traverseTree(node.getLastChild());
					traverseTree(node.getFirstChild());

					if(node.getParentNode().getNodeName().equals("not"))
						out.write(" ");
					else
						out.write(") ");
					//System.out.print(") ");*/
					
					out.write(" (select ");

					traverseTree(node.getLastChild());
					traverseTree(node.getFirstChild());
					
					out.write(") ");
				}

				else if(elementName.equals("not"))
				{
					
					out.write("( not ");
					traverseTree(node.getFirstChild());
					out.write(")  ");
				}


	}//end traverseTree
	
	public void declare_vars(Document doc) throws IOException
	{

		NodeList vars = doc.getElementsByTagName("symbol");
		int numSymbols = vars.getLength();

		out.newLine();	

		for (int i = 0; i < numSymbols; i++) 
		{
			//System.out.println(vars.item(i).getFirstChild().toString()); 
			String symbol = vars.item(i).getFirstChild().getTextContent();
			String type = vars.item(i).getAttributes().getNamedItem("type").getNodeValue();

			if (!(var_decld.contains(symbol)))
			{
				var_decld.add(symbol);

				if(type.equals("finiteset(object)"))
				{

					out.write("(declare-const "+ symbol + " Set)");
					out.newLine();
				}

				else if (type.equals("object"))
				{
					out.write("(declare-const "+ symbol + " T)");
					out.newLine();
				}
				
				else if (type.equals("integer"))
				{
					out.write("(declare-const "+ symbol + " Int)");
					out.newLine();
				}
			}

		}//end for

		out.newLine();	

	}//end declare_vars(Document doc)	

	



}
