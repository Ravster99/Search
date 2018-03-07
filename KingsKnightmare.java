import java.io.File;
import java.io.FileNotFoundException;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;
import java.util.Stack;
import java.util.AbstractMap.SimpleEntry;

/**
 * @author Ravi Suman.
 * This class is a template for implementation of 
 * HW1 for CS540 section 2
 */
/**
 * Data structure to store each node.
 */
class Location {
	private int x;
	private int y;
	private Location parent;
	public Object new_location_parent;

	public Location(int x, int y, Location parent) {
		this.setX(x);
		this.y = y;
		this.parent = parent;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public Location getParent() {
		return parent;
	}

	@Override
	public String toString() {
		return getX() + " " + y;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj != null && obj instanceof Location) {
			Location loc = (Location) obj;
			return loc.getX() == getX() && loc.y == y;
		}
		return false;
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 31 * (hash + getX());
		hash = 31 * (hash + y);
		return hash;
	}

	public void setX(int x) {
		this.x = x;
	}
}

public class KingsKnightmare {
	//represents the map/board
	private static boolean[][] board;
	//represents the goal node
	private static Location king;
	//represents the start node
	private static Location knight;
	//y dimension of board
	private static int n;
	//x dimension of the board
	private static int m;
	//Frontier for AStar in the form of priority queue.
	private static PriorityQ<Location> pfrontier = new PriorityQ<>();
	//enumerating defining different algorithm types
	enum SearchAlgo{
		BFS, DFS, ASTAR;
	}

	public static void main(String[] args) {
		if (args != null && args.length > 0) {
			//loads the input file and populates the data variables
			SearchAlgo algo = loadFile(args[0]);
			if (algo != null) {
				switch (algo) {
					case DFS :
						executeDFS();
						break;
					case BFS :
						executeBFS();
						break;
					case ASTAR :
						executeAStar();
						break;
					default :
						break;
				}
			}
		}
	}

	/**
	 * Implementation of A* algorithm for the problem.
	 */
	private static void executeAStar() {
		//Initializing important stacks for legal moves, frontiers, and paths.
		Stack<Location> legal_moves = new Stack<Location>();
		Stack<Location> legal_moves_copy = new Stack<Location>();
		Stack<Location> frontier_no_parent = new Stack<Location>();
		Stack<Location> explored = new Stack<Location>();
		Stack<Location> path = new Stack<Location>();
		//Calculating the score (h(n)) for the knight.
		//Score is based on Manhattan distance from the goal.
		int h_score = 0;
		h_score = Math.abs(king.getX() - knight.getX()) + Math.abs(king.getY() - knight.getY());		
		pfrontier.add(knight, h_score);
		frontier_no_parent.push(knight);
		Location move1, move2, move3, move4, move5, move6, move7, move8, z, legal_move_added, legal_move_added_no_parent, expanded_node_location;
		SimpleEntry<Location,Integer> expanded_node;
		//Set of legal moves.
		move1 = new Location(+2, +1, null);
		move2 = new Location(+1, +2, null);
		move3 = new Location(-1, +2, null);
		move4 = new Location(-2, +1, null);
		move5 = new Location(-2, -1, null);
		move6 = new Location(-1, -2, null);
		move7 = new Location(+1, -2, null);
		move8 = new Location(+2, -1, null);
		//Pushing the legal moves into the stack.
		legal_moves.push(move8);
		legal_moves.push(move7);
		legal_moves.push(move6);
		legal_moves.push(move5);
		legal_moves.push(move4);
		legal_moves.push(move3);
		legal_moves.push(move2);
		legal_moves.push(move1);
 		legal_moves_copy.addAll(legal_moves);
 		int number_of_expansion = 0;
 		//Initializing a variable to flag when the goal has been reached.
		int goal_flag = 0;
		while (goal_flag == 0){
			number_of_expansion++;
			//Expanding the frontier.
			expanded_node = pfrontier.poll();
			expanded_node_location = expanded_node.getKey();
			frontier_no_parent.pop();
			int explored_x = expanded_node_location.getX();
			int explored_y = expanded_node_location.getY();
			//Updating the explored stack.
			Location explored_node = new Location (explored_x, explored_y, null);
			explored.push(explored_node);
			//Checking if the expanded node is priority queue.
			if (explored_node.equals(king) == true){
				goal_flag = 1;
				//Determining the path when the goal has been reached.
				Location path_node = null;
				path.push(king);
				path_node = expanded_node_location;
				while (path_node.getParent() != null){
					path_node = expanded_node_location.getParent();
					path.push(path_node);
					expanded_node_location = expanded_node_location.getParent();
				}	
				break;
			}
			//For each expanded node, finding its child (legal moves from a node).
			for (int i=0; i<8; i++){
				//Popping the nodes from legal moves stack.
				z = (Location) legal_moves.pop();
				//Determining the child node for the expanded node.
				int new_location_x = expanded_node.getKey().getX() + z.getX();
				int new_location_y = expanded_node.getKey().getY() + z.getY();
				Location target = new Location (new_location_x, new_location_y, null);
				//Checking if the child node has already been explored.
				Integer pos = (Integer) explored.search(target);
				//Checking if the child node is already in the frontier.
				Integer pos1 = (Integer) frontier_no_parent.search(target);
				//Condition for the child.
				if (new_location_x < m && new_location_y < n && new_location_x >= 0 && new_location_y >= 0 && pos == -1 && pos1 == -1){
					if (board[new_location_y][new_location_x] == false){
						legal_move_added = new Location(new_location_x, new_location_y, expanded_node_location);
						//Determine the cost of the node from King.
						int cost = 3;
						while (expanded_node_location.getParent() != null){
							cost+=3;
							expanded_node_location = expanded_node_location.getParent();			
						}
						//Determine the heuristic cost for the node.						
						h_score = Math.abs(king.getX() - legal_move_added.getX()) + Math.abs(king.getY() - legal_move_added.getY());
						int total_cost = cost + h_score;
						//Checking if a higher cost node already exists in the frontier.
						if (pfrontier.exists(legal_move_added) == true){
							if(pfrontier.getPriorityScore(legal_move_added) > total_cost){							
								pfrontier.modifyEntry(legal_move_added, total_cost);
							}
						}
						//If the higher cost node does not exist, we add the node to the frontier.
						else{
							pfrontier.add(legal_move_added, total_cost);
						}
						legal_move_added_no_parent = new Location(new_location_x, new_location_y, null);
						frontier_no_parent.push(legal_move_added_no_parent);
						expanded_node_location = expanded_node.getKey();
					}
				}
			}
				//Replenishing the legal moves stack.
				legal_moves.addAll(legal_moves_copy);
				//Printing when there is no expansion at node 1.
				if (pfrontier.size() == 0){
					System.out.println("Not reachable.");
					System.out.println("Expanded nodes: " + (number_of_expansion - 1));
					break;			
				}
			}
			//Printing the path.
			if (goal_flag == 1){
				while (path.empty() == false){			
					System.out.println(path.pop());
				}
				System.out.println("Expanded nodes: " + (number_of_expansion - 1));			
			}		
	}

	

	/**
	 * Implementation of BFS algorithm.
	 */
	private static void executeBFS() {
	//Initializing important stacks for legal moves, frontiers, and paths.
	Stack<Location> legal_moves = new Stack<Location>();
	Stack<Location> legal_moves_copy = new Stack<Location>();
	Queue<Location> frontier = new LinkedList<Location>();
	Queue<Location> frontier_no_parent = new LinkedList<Location>();
	Stack<Location> explored = new Stack<Location>();
	Stack<Location> path = new Stack<Location>();
	frontier.add(knight);
	frontier_no_parent.add(knight);
	Location move1, move2, move3, move4, move5, move6, move7, move8, z, legal_move_added, legal_move_added_no_parent, expanded_node;		
	//Set of legal moves.
	move1 = new Location(+2, +1, null);
	move2 = new Location(+1, +2, null);
	move3 = new Location(-1, +2, null);
	move4 = new Location(-2, +1, null);
	move5 = new Location(-2, -1, null);
	move6 = new Location(-1, -2, null);
	move7 = new Location(+1, -2, null);
	move8 = new Location(+2, -1, null);
	//Pushing the legal moves into the stack.
	legal_moves.push(move8);
	legal_moves.push(move7);
	legal_moves.push(move6);
	legal_moves.push(move5);
	legal_moves.push(move4);
	legal_moves.push(move3);
	legal_moves.push(move2);
	legal_moves.push(move1);
	legal_moves_copy.addAll(legal_moves);
	//Initializing a variable to flag when the goal has been reached.
	int goal_flag = 0;
	int number_of_expansion = 0;
	while (goal_flag == 0){
		number_of_expansion++;
		//Expanding the frontier.
		expanded_node = frontier.poll();
		frontier_no_parent.poll();
		int explored_x = expanded_node.getX();
		int explored_y = expanded_node.getY();
		//Updating the explored stack.
		Location explored_node = new Location (explored_x, explored_y, null);
		explored.push(explored_node);
		//For each expanded node, finding its child (legal moves from a node).
		for (int i=0; i<8; i++){
			//Popping the nodes from legal moves stack.
			z = (Location) legal_moves.pop();
			//Determining the child node for the expanded node.
			int new_location_x = expanded_node.getX() + z.getX();
			int new_location_y = expanded_node.getY() + z.getY();
			Location target = new Location (new_location_x, new_location_y, null);
			//Checking if the child node has already been explored.
			Integer pos = (Integer) explored.search(target);
			//Checking if the child node is already in the frontier.
			Boolean pos1 = frontier_no_parent.contains(target);
			//Condition for the child.
			if (new_location_x < m && new_location_y < n && new_location_x >= 0 && new_location_y >= 0 && pos == -1 && pos1 == false){
				if (board[new_location_y][new_location_x] == false && target.equals(king) == false){
					//If the child is legal, we add it to the frontier.
					legal_move_added = new Location(new_location_x, new_location_y, expanded_node);
					frontier.add(legal_move_added);
					legal_move_added_no_parent = new Location(new_location_x, new_location_y, null);
					frontier_no_parent.add(legal_move_added_no_parent);
				}
			}
			//Checking if the expanded node is priority queue.
			if (target.equals(king) == true){
				goal_flag = 1;
				//Determining the path when the goal has been reached.
				Location path_node = null;
				path.push(king);
				path.push(expanded_node);
				path_node = expanded_node;
				while (path_node.getParent() != null){
					path_node = expanded_node.getParent();
					path.push(path_node);
					expanded_node = expanded_node.getParent();
				}	
				break;
			}
		}
		//Replenishing the legal moves stack.
		legal_moves.addAll(legal_moves_copy);
		//Printing when there is no expansion at node 1.
		if (frontier.size() == 0){
			System.out.println("Not reachable.");
			System.out.println("Expanded nodes: " + number_of_expansion);
			break;			
		}
	}
	//Printing the path.
	if (goal_flag == 1){
		while (path.empty() == false){			
			System.out.println(path.pop());
		}
		System.out.println("Expanded nodes: " + number_of_expansion);
	}
}
	
	/**
	 * Implementation of DFS algorithm.
	 */
	private static void executeDFS() {
		//Initializing important stacks for legal moves, frontiers, and paths.
		Stack<Location> legal_moves = new Stack<Location>();
		Stack<Location> legal_moves_copy = new Stack<Location>();
		Stack<Location> frontier = new Stack<Location>();
		Stack<Location> frontier_no_parent = new Stack<Location>();
		Stack<Location> explored = new Stack<Location>();
		Stack<Location> path = new Stack<Location>();
		frontier.push(knight);
		frontier_no_parent.push(knight);
		Location move1, move2, move3, move4, move5, move6, move7, move8, z, legal_move_added, legal_move_added_no_parent, expanded_node;		
		//Set of legal moves.
		move1 = new Location(+2, +1, null);
		move2 = new Location(+1, +2, null);
		move3 = new Location(-1, +2, null);
		move4 = new Location(-2, +1, null);
		move5 = new Location(-2, -1, null);
		move6 = new Location(-1, -2, null);
		move7 = new Location(+1, -2, null);
		move8 = new Location(+2, -1, null);
		//Pushing the legal moves into the stack.
		legal_moves.push(move8);
		legal_moves.push(move7);
		legal_moves.push(move6);
		legal_moves.push(move5);
		legal_moves.push(move4);
		legal_moves.push(move3);
		legal_moves.push(move2);
		legal_moves.push(move1);
		legal_moves_copy.addAll(legal_moves);
		//Initializing a variable to flag when the goal has been reached.
		int goal_flag = 0;
		int number_of_expansion = 0;
		while (goal_flag == 0){
			number_of_expansion++;
			//Expanding the frontier.
			expanded_node = frontier.pop();		
			frontier_no_parent.pop();
			int explored_x = expanded_node.getX();
			int explored_y = expanded_node.getY();
			//Updating the explored stack.
			Location explored_node = new Location (explored_x, explored_y, null);
			explored.push(explored_node);
			//For each expanded node, finding its child (legal moves from a node).
			for (int i=0; i<8; i++){
				//Popping the nodes from legal moves stack.
				z = (Location) legal_moves.pop();
				//Determining the child node for the expanded node.
				int new_location_x = expanded_node.getX() + z.getX();
				int new_location_y = expanded_node.getY() + z.getY();
				Location target = new Location (new_location_x, new_location_y, null);
				//Checking if the child node has already been explored.
				Integer pos = (Integer) explored.search(target);
				//Checking if the child node is already in the frontier.
				Integer pos1 = (Integer) frontier_no_parent.search(target);
				//Condition for the child.
				if (new_location_x < m && new_location_y < n && new_location_x >= 0 && new_location_y >= 0 && pos == -1 && pos1 == -1){
					if (board[new_location_y][new_location_x] == false && target.equals(king) == false){
						//If the child is legal, we add it to the frontier.
						legal_move_added = new Location(new_location_x, new_location_y, expanded_node);
						frontier.push(legal_move_added);
						legal_move_added_no_parent = new Location(new_location_x, new_location_y, null);
						frontier_no_parent.push(legal_move_added_no_parent);
					}
				}
				//Checking if the expanded node is priority queue.
				if (target.equals(king) == true){
					goal_flag = 1;
					//Determining the path when the goal has been reached.
					Location path_node = null;
					path.push(king);
					path.push(expanded_node);
					path_node = expanded_node;
					while (path_node.getParent() != null){						
						path_node = expanded_node.getParent();
						path.push(path_node);
						expanded_node = expanded_node.getParent();
					}	
					break;
				}
			}
			//Replenishing the legal moves stack.
			legal_moves.addAll(legal_moves_copy);
			//Printing when there is no expansion at node 1.
			if (frontier.empty() == true){
				System.out.println("Not reachable.");
				System.out.println("Expanded nodes: " + number_of_expansion);
				break;			
			}
		}
		//Printing the path.
		if (goal_flag == 1){
			while (path.empty() == false){			
				System.out.println(path.pop());
			}
			System.out.println("Expanded nodes: " + number_of_expansion);
		}
	}
		
	/**
	 * @param filename
	 * @return Algorithm type
	 * This method reads the input file and populates all the 
	 * data variables for further processing
	 */
	private static SearchAlgo loadFile(String filename) {
		File file = new File(filename);
		try {
			Scanner sc = new Scanner(file);
			SearchAlgo algo = SearchAlgo.valueOf(sc.nextLine().trim().toUpperCase());
			n = sc.nextInt();
			m = sc.nextInt();
			sc.nextLine();
			board = new boolean[n][m];
			for (int i = 0; i < n; i++) {
				String line = sc.nextLine();
				for (int j = 0; j < m; j++) {
					if (line.charAt(j) == '1') {
						board[i][j] = true;
					} else if (line.charAt(j) == 'S') {
						knight = new Location(j, i, null);
					} else if (line.charAt(j) == 'G') {
						king = new Location(j, i, null);
					}
				}
			}
			sc.close();
			return algo;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}
}