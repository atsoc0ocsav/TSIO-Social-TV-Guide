package iul.iscte.tsio.model;

import iul.iscte.tsio.interfaces.UserDAO;
import iul.iscte.tsio.server.Server;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.neo4j.graphdb.Node;
import org.neo4j.rest.graphdb.RestAPIFacade;
import org.neo4j.rest.graphdb.query.RestCypherQueryEngine;
import org.neo4j.rest.graphdb.util.ConvertedResult;

public class UserDAOImpl implements UserDAO {

	private static UserDAOImpl instance;
	private final RestAPIFacade graphDatabase;
	private final RestCypherQueryEngine cypherQueryEngine;

	private UserDAOImpl() {
		this.graphDatabase = new RestAPIFacade(Server.getInstance()
				.getServer_ROOT_URI());
		this.cypherQueryEngine = new RestCypherQueryEngine(graphDatabase);
	}

	public static UserDAOImpl getInstance() {
		if (instance == null) {
			synchronized (UserDAOImpl.class) {
				instance = new UserDAOImpl();
			}
		}
		return instance;
	}

	@Override
	public UserEntity getUserByEmail(String email) {
		// Ensure that email is unique
		String query = "Match (n:User) Where n.email='" + email + "' return n;";
		System.out.println(query);
		Iterable<Node> user = Collections.emptyList();
		try {
			user = cypherQueryEngine.query(query, null).to(Node.class);
		} catch (Exception e) {
			System.err.print("Something went wrong, please call techSupport");
			e.printStackTrace();
		}
		Iterator<Node> userIterator = user.iterator();
		if (userIterator.hasNext()) {
			Node aux = userIterator.next();
			return new UserEntity(aux.getId(), aux.getProperty("username")
					.toString(), aux.getProperty("email").toString());
		}
		return null;
	}

	@Override
	public long insertUser(UserEntity userToInsert) {
		String query = "Create (u:User {username: \""
				+ userToInsert.getUsername() + "\", email: \""
				+ userToInsert.getEmail() + "\"}) Return id(u);";
		ConvertedResult<Integer> count = null;
		try {
			count = cypherQueryEngine.query(query, null).to(Integer.class);
		} catch (Exception e) {
			System.err.print("Something went wrong, please call techSupport");
			e.printStackTrace();
		}
		long auxID = count.iterator().next();
		return auxID;
	}

	@Override
	public boolean deleteUser(UserEntity userToDelete) {
		String query = "Match (u:User) Where id(u)="
				+ userToDelete.getNodeId() + " OPTIONAL MATCH (u)-[r]-()  Delete u,r;";
		try {
			cypherQueryEngine.query(query, null).to(Node.class);
		} catch (Exception e) {
			System.err.print("Something went wrong, please call techSupport");
			e.printStackTrace();
			return false;
		}
		return true;
	}

	@Override
	public boolean updateUser(UserEntity userToUpdate) {
		String query = "Match (u:User) Where id(u)=" + userToUpdate.getNodeId()
				+ " Set u.username = \"" + userToUpdate.getUsername()
				+ "\", u.email = \"" + userToUpdate.getEmail() + "\";";
		try {
			cypherQueryEngine.query(query, null).to(Node.class);
		} catch (Exception e) {
			System.err.print("Something went wrong, please call techSupport");
			e.printStackTrace();
			return false;
		}
		return true;
	}

	@Override
	public UserEntity getUserByName(String username) {
		String query = "Match (n:User) where n.username=\"" + username
				+ "\" return n;";
		Iterable<Node> user = Collections.emptyList();
		try {
			user = cypherQueryEngine.query(query, null).to(Node.class);
		} catch (Exception e) {
			System.err.print("Something went wrong, please call techSupport");
			e.printStackTrace();
		}
		Iterator<Node> userIterator = user.iterator();
		if (userIterator.hasNext()) {
			Node aux = userIterator.next();
			return new UserEntity(aux.getId(), aux.getProperty("username")
					.toString(), aux.getProperty("email").toString());
		}
		return null;
	}

	@Override
	public List<UserEntity> getAllUsers() {
		String query = "Match (n:User) return n;";
		Iterable<Node> users = Collections.emptyList();
		try {
			users = cypherQueryEngine.query(query, null).to(Node.class);
		} catch (Exception e) {
			System.err.print("Something went wrong, please call techSupport");
			e.printStackTrace();
		}
		List<UserEntity> auxList = new ArrayList<UserEntity>();
		Iterator<Node> userIterator = users.iterator();
		while (userIterator.hasNext()) {
			Node auxNode = userIterator.next();
			auxList.add(new UserEntity(auxNode.getId(), auxNode.getProperty(
					"username").toString(), auxNode.getProperty("email")
					.toString()));
		}
		return auxList;
	}

	@Override
	public boolean createFriendshipRelationship(UserEntity user,
			UserEntity friend) {
		String query = "MATCH (n:User), (m:User) WHERE id(n)="
				+ user.getNodeId() + " AND id(m)=" + friend.getNodeId()
				+ " MERGE (n)-[r:Friend]->(m) Return r";
		Iterable<Node> relationship = Collections.emptyList();
		try {
			relationship = cypherQueryEngine.query(query, null).to(Node.class);
		} catch (Exception e) {
			System.err.print("Something went wrong, please call techSupport");
			e.printStackTrace();
		}
		if (relationship.iterator().hasNext()) {
			return true;
		}
		return false;
	}

	@Override
	public boolean deleteFriendshipRelationship(UserEntity user,
			UserEntity friend) {
		// Add verification to see if relationship exists
		String query = "MATCH (n:User)-[r:Friend]-(m:User) WHERE id(n)="
				+ user.getNodeId() + " AND id(m)=" + friend.getNodeId()
				+ " Delete r";
		try {
			cypherQueryEngine.query(query, null).to(Node.class);
		} catch (Exception e) {
			System.err.print("Something went wrong, please call techSupport");
			e.printStackTrace();
			return false;
		}
		return true;
	}

	@Override
	public boolean isUserFriend(UserEntity user, UserEntity friend) {
		String query = "Match (u1:User)<-[f:Friend]->(u2:User) Where id(u1)="
				+ user.getNodeId() + " And id(u2)=" + friend.getNodeId()
				+ " return count(f);";
		Iterable<Integer> count = Collections.emptyList();
		try {
			count = cypherQueryEngine.query(query, null).to(Integer.class);
		} catch (Exception e) {
			System.err.print("Something went wrong, please call techSupport");
			e.printStackTrace();
		}
		int auxCount = count.iterator().next();
		if (auxCount != 0)
			return true;
		return false;
	}

	@Override
	public List<UserEntity> getAllFriends(UserEntity user) {
		String query = "Match (u1:User)<-[:Friend]->(u2:User) Where id(u1)="
				+ user.getNodeId() + " return u2;";
		Iterable<Node> friends = Collections.emptyList();
		try {
			friends = cypherQueryEngine.query(query, null).to(Node.class);
		} catch (Exception e) {
			System.err.print("Something went wrong, please call techSupport");
			e.printStackTrace();
		}
		List<UserEntity> auxList = new ArrayList<UserEntity>();
		Iterator<Node> it = friends.iterator();
		while (it.hasNext()) {
			Node auxNode = it.next();
			auxList.add(new UserEntity(auxNode.getId(), auxNode.getProperty(
					"username").toString(), auxNode.getProperty("email")
					.toString()));
		}
		return auxList;
	}
	
	@Override
	public List<UserEntity> getFriendsWithRegex(String name) {
		String query = "Match (u1:User)<-[:Friend]->(u2:User) Where u1.username=~'"
				+ name + ".*' return u2;";
		Iterable<Node> friends = Collections.emptyList();
		try {
			friends = cypherQueryEngine.query(query, null).to(Node.class);
		} catch (Exception e) {
			System.err.print("Something went wrong, please call techSupport");
			e.printStackTrace();
		}
		List<UserEntity> auxList = new ArrayList<UserEntity>();
		Iterator<Node> it = friends.iterator();
		while (it.hasNext()) {
			Node auxNode = it.next();
			auxList.add(new UserEntity(auxNode.getId(), auxNode.getProperty(
					"username").toString(), auxNode.getProperty("email")
					.toString()));
		}
		return auxList;
	}


	@Override
	public List<UserEntity> getUsersWithRegex(String name) {
		String query = "Match (n:User) Where n.username=~'" + name
				+ ".*' return n;";
		System.out.println(query);
		Iterable<Node> users = Collections.emptyList();
		try {
			users = cypherQueryEngine.query(query, null).to(Node.class);
		} catch (Exception e) {
			System.err.print("Something went wrong, please call techSupport");
			e.printStackTrace();
		}
		List<UserEntity> auxList = new ArrayList<UserEntity>();
		Iterator<Node> userIterator = users.iterator();
		while (userIterator.hasNext()) {
			Node auxNode = userIterator.next();
			auxList.add(new UserEntity(auxNode.getId(), auxNode.getProperty(
					"username").toString(), auxNode.getProperty("email")
					.toString()));
		}
		return auxList;
	}
}
