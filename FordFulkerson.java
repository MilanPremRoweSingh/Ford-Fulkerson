import java.io.*;
import java.util.*;




public class FordFulkerson {

	
	 
	public static ArrayList<Integer> pathDFS(Integer source, Integer destination, WGraph graph){
		ArrayList<Integer> Stack = new ArrayList<Integer>();
		boolean visited[] = new boolean[graph.getNbNodes()];
		Integer p[] = new Integer[graph.getNbNodes()];
		
		for (int i=0;i<graph.getNbNodes();i++){
			visited[i] = false; //Initialise all nodes to having not been visited
		}
		Stack.add(0,source); //Add the source to the stack
		while(Stack.size()!=0){
			Integer top = Stack.remove(0);
			if(!(visited[top])){
				visited[top] = true;
			}


			ArrayList<Integer> adjNodes = findAdjNodes(top,graph); //Return all adjacent nodes
			for(Integer node:adjNodes){ 
				if(!(visited[node])){
					Stack.add(0,node);
					p[node] = top;
					if(node==destination){
						Stack.clear();

						Stack.add(0,node);
						node = p[node];
						while(Stack.get(0)!=source){
							Stack.add(0,node);
							node = p[node];
						}
						return Stack;
					}
				}		
			}
		}

		return Stack;
	}

	public static ArrayList<Integer> tracePath(Integer source, Integer descendant,Integer[] parents,WGraph graph){
		ArrayList<Integer> Path = new ArrayList<Integer>();
		while(descendant !=source ){
			Path.add(0,descendant); //Push descendant to stack
			descendant = parents[descendant];
		}
		Path.add(0,descendant); //Add source to stack
		//Now path is s->initial descendant
		return Path;
	}

	public static ArrayList<Integer> findAdjNodes(Integer node, WGraph graph){
		ArrayList<Integer> adjNodes = new ArrayList<Integer>();
		ArrayList<Edge> edges = graph.getEdges();
		for(Edge e:edges){
			if(e.nodes[0]==node){ //If edge (node,v) for some v
				adjNodes.add(e.nodes[1]); //Add v to adjNodes
			}
		}
		return adjNodes;
	}
	
	public static WGraph computeGf(WGraph g, WGraph gc){
		WGraph gf = new WGraph(gc);
		for(Edge e: g.getEdges()){
			Integer u = e.nodes[0];
			Integer v = e.nodes[1];
			if(e.weight>0){
				Edge ef = gf.getEdge(u,v);
				ef.weight -= e.weight; //update forward edge
				ef = new Edge(v,u,e.weight); //create back edge
				gf.addEdge(ef); // add back edge
			}
		}
		return gf;
	}
	
	
	
	public static void fordfulkerson(Integer source, Integer destination, WGraph graph, String filePath){
		String answer="";
		String myMcGillID = "260654803"; //Please initialize this variable with your McGill ID
		int maxFlow = 0;
		int bottleneck;
		WGraph gf = new WGraph(graph);
		WGraph gc = new WGraph(graph);
		for(Edge e:graph.getEdges()){
			e.weight = 0;
		}

		ArrayList<Integer> path = pathDFS(source,destination,gf);
		while(path.size() != 0){
			bottleneck = findBottleneck(path,gf);
			maxFlow+=bottleneck;
			updatePath(path,graph,bottleneck,gc);
			gf = computeGf(graph,gc);
			gf = removeWeightless(gf);
			path = pathDFS(source,destination,gf);
		}
		System.out.println(graph.toString());
		System.out.println(maxFlow);
		answer += maxFlow + "\n" + graph.toString();	
		writeAnswer(filePath+myMcGillID+".txt",answer);
		//System.out.println(answer);
	}

	public static int findBottleneck(ArrayList<Integer> path, WGraph graph){
		Integer u, v;
		u = path.get(0);
		v = path.get(1);
		Edge et = graph.getEdge(u,v);
		int min = et.weight; 

		System.out.println(path);
		for(int i=1;i<path.size();i++){
			v = path.get(i);
			Edge e = graph.getEdge(u,v);
			int temp = e.weight; 
			if(temp<min){
				min = temp;
			}
			u = v;

		}
		return min;
	}

	public static WGraph removeWeightless(WGraph gf){
		WGraph gt = new WGraph();
		for(Edge e:gf.getEdges()){
			if (e.weight != 0){
				gt.addEdge(e);
			}
		}
		return gt;
	}

	public static void updatePath(ArrayList<Integer> path, WGraph graph,int bottleneck, WGraph gc){
		Integer u, v;
		u = path.get(0);
		for(int i=1;i<path.size();i++){
			v = path.get(i);
			if(gc.getEdge(u,v)!=null){ //If it is a forward edge
				Edge e = graph.getEdge(u,v); //Edge in original graph is (u,v)
				e.weight += bottleneck; //Increase 
			} else { //If it is a backwards edge
				Edge e = graph.getEdge(v,u); //Edge in original graph is (v,u)
				e.weight -= bottleneck;
			}
			
			u = v;

		}
	}
	
	public static void writeAnswer(String path, String line){
		BufferedReader br = null;
		File file = new File(path);
		// if file doesnt exists, then create it
		
		try {
		if (!file.exists()) {
			file.createNewFile();
		}
		FileWriter fw = new FileWriter(file.getAbsoluteFile(),true);
		BufferedWriter bw = new BufferedWriter(fw);
		bw.write(line+"\n");	
		bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null)br.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}
	
	public static void main(String[] args){
		 String file = args[0];
		 File f = new File(file);
		 WGraph g = new WGraph(file);
		 fordfulkerson(g.getSource(),g.getDestination(),g,f.getAbsolutePath().replace(".txt",""));
	 }
	 
}
