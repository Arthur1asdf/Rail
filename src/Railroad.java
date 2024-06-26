/* Arthur Teng
 * Dr. Steinberg
 * COP3503 Spring 2024
 * Programming Assignment 5
 */
import java.io.File;  // Import the File class
import java.io.FileNotFoundException;  // Import this class to handle errors
import java.util.Scanner; // Import the Scanner class to read text files
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Collections;



class Edge
{
    String src;
    String dest;
    int weight;
    public Edge(String src, String dest, int weight)
    {
        this.src = src;
        this.dest = dest;
        this.weight = weight;
    }
    //overriding toString method so I can see what is inside my arraylist
    public String toString() {
        return src + "---" + dest + "\t$" + weight;
     }
    public int getWeight()
    {
        return weight;
    }

}
class DisjointSetImproved
{
	int [] rank;
	int [] parent;
    int n;
	//this is the subset method
    public DisjointSetImproved(int n)
    {
        //all rank 0 atm
        rank = new int[n];
        
        parent = new int[n];
        this.n = n;
        makeSet();
    }
	
	// Creates n sets with single item in each
    public void makeSet()
    {
        for (int i = 0; i < n; i++) 
		{
            parent[i] = i;
        }
    }
	
	//path compression/ find our parent for set
	public int find(int x)
    {
        if (parent[x] != x) 
		{
            parent[x] = find(parent[x]);
        }
 
        return parent[x];
    }
	
	//union by rank
	public void union(int x, int y)
    {
        int xRoot = find(x), yRoot = find(y);
 
        if (xRoot == yRoot)
            return;
		
        if (rank[xRoot] < rank[yRoot])
            parent[xRoot] = yRoot;
		
        else if (rank[yRoot] < rank[xRoot])
            parent[yRoot] = xRoot;
        else 
        {
            parent[yRoot] = xRoot;
            rank[xRoot] = rank[xRoot] + 1;
        }
    }
    public static void printSets(int[] universe, DisjointSetImproved ds)
    {
        for (int i: universe) 
		{
            System.out.print(ds.find(i) + " ");
        }
 
        System.out.println();
    }
}

public class Railroad {
    //To store number of tracks so we can use it to see if we are in a cycle
    private int numOfTracks;
    //To store file name 
    private String fileOfAllPossibleTracks;
    //To store my sets when I make them
    private ArrayList<Edge> sets;
    private HashMap<String, Integer> vertexes;

    public void readFile()
    {
        //simple file i/o stuff
        try
        {
            File file = new File(fileOfAllPossibleTracks);
            Scanner reader = new Scanner(file);
            int i = 0;
            while(reader.hasNextLine())
            {
                //reads entire line first
                String line = reader.nextLine();
                //sees if this is a blank line or not
                if (!line.trim().isEmpty()) {
                    //seperate scanner for single lines
                    Scanner lineScanner = new Scanner(line);
                    if (lineScanner.hasNext()) {
                        //get our vertexes and costs
                        String src = lineScanner.next();
                        String dest = lineScanner.next();
                        String tempWeight = lineScanner.next();

                        //changing our cost from string to int
                        int weight = Integer.parseInt(tempWeight);
                        //adds the edge to our set
                        sets.add(sortLexi(new Edge(src, dest, weight)));
                        //We need to track the vertices to see if we are in a cycle or not, so we need to
                        //use hashmap also disjoint sets all use int para so we need to get our arraylist from 
                        //an integer
                        if(!vertexes.containsKey(src))
                        {
                            vertexes.put(src, i);
                            i++;
                        }
                        if(!vertexes.containsKey(dest))
                        {
                            vertexes.put(dest, i);
                            i++;
                        }
                        //System.out.println("This is var:" + src + " " + dest + " " + weight);
                        //System.out.println(sets.size() + " " + sets.toString());
                        
                    }
                    lineScanner.close();
                }
            }
            reader.close();
        }
        catch(FileNotFoundException e)
        {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }
    //to sort the destinations lexiconacally 
    public Edge sortLexi(Edge singleEdge) {
        // comparing to see if the dest is greather than src
        if (singleEdge.src.compareTo(singleEdge.dest) > 0) {
           // create a new edge with dest and src flipped like the sample outputs
           Edge newEdge = new Edge(singleEdge.dest, singleEdge.src, singleEdge.weight);
  
           return newEdge;
        }
        //return entered parameter if we don't need to swap
        return singleEdge;
     }
    //numOfTracks is the number of lines in each text file
    //allPossible tracks take in the txt file for the tracks
    public Railroad(int numOfTracks, String fileOfAllPossibleTracks)
    {
        this.numOfTracks = numOfTracks;
        this.fileOfAllPossibleTracks = fileOfAllPossibleTracks;
        sets = new ArrayList<Edge>();
        vertexes = new HashMap<String, Integer>();
    }
    
    public String buildRailroad()
    {
        //System.out.println(src + "---" + dest + "\t$" + resultingtracks[i].weight + "\n");
        readFile();
        Collections.sort(sets, Comparator.comparingInt(Edge ::getWeight));
        //System.out.println(sets.toString());
        DisjointSetImproved dus = new DisjointSetImproved(vertexes.size());
        int totalCost = 0;
        ArrayList<Edge> result = new ArrayList<>(); // This will store the resulting MST
        // numOfTracks - 1 because that is the formula for the kruskal's algo 
        // you can only have V - 1 edges
        int edgeCount = 0;
        for (int i = 0; i < sets.size(); i++) 
        {
            int srcParent = vertexes.get(sets.get(i).src);
            int destParent = vertexes.get(sets.get(i).dest);
    
            // If including this edge does not cause a cycle
            if (dus.find(srcParent) != dus.find(destParent)) {
                totalCost += sets.get(i).weight;
                dus.union(srcParent, destParent);
                edgeCount++;
                System.out.print(sets.get(i).src + "---" + sets.get(i).dest + "\t$" + sets.get(i).weight + "\n");
                // Only need vertexCount - 1 edges
                if (edgeCount == vertexes.size() - 1) break;
            }
        }
        System.out.print("The cost of the railroad is $" + totalCost);
    
        // int numOfEdges = 0;
        // while(numOfEdges < vertexes.size())
        // {
        //     int srcParent = dus.find(vertexes.get(sets.get(numOfEdges).src));
        //     int destParent = dus.find(vertexes.get(sets.get(numOfEdges).dest));
        //     // If including this edge does not cause a cycle
        //     if (srcParent != destParent) {
        //         result.add(sets.get(numOfEdges));
        //         totalCost += sets.get(numOfEdges).weight;
        //         dus.union(srcParent, destParent);
        //         numOfEdges++;
        //         System.out.println(sets.get(numOfEdges).src + "---" + sets.get(numOfEdges).dest + "\t$" + sets.get(numOfEdges).weight + "\n");
        //     }
        // }
        // System.out.println("The cost of the railroad is $" + totalCost);
         
        
        
        
        
        // Printing the result
        
        // for (Edge edge : result) {
        //     String src = edge.src;
        //     String dest = edge.dest;
        //     int weight =  edge.weight;
        // }
        // for(int i = 0; i < numOfTracks; i++)
        // {   need to get this to int to use disjoint set functions
        //     dus.union(sets.get(i).src)
        // }
        //int k = vertexes.get(sets.get(numOfTracks).src);
        
        return ".";
    }
}

