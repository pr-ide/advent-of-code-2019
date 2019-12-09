package ru.ural;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;

/**
 * @author RandomPie
 */
public class Day6 {
	public static void main(String... arg) {

		List<Node> nodes = new ArrayList<>();

		Path path = Paths.get("\\home\\day6.txt");
		try (Stream<String> lines = Files.lines(path)) {
			lines.forEach(s -> addNode(nodes, s));
		} catch (IOException ex) {
			// do something or re-throw...
		}

		int res = 0;

		for (Node node : nodes) {
			//			if (node.getChildren() == null) {
			res += node.countNodes();
			//			}
		}

		Set<String> sanPath = new HashSet<>();
		Node santa = nodes.stream().filter(node -> node.getName().equals("SAN")).findFirst().orElse(
				null);
		santa.getPathToTop(sanPath);
		Set<String> youPath = new HashSet<>();
		Node you = nodes.stream().filter(node -> node.getName().equals("YOU")).findFirst().orElse(
				null);
		you.getPathToTop(youPath);
		HashSet<String> strings = new HashSet<String>(youPath);

		strings.retainAll(sanPath);

		youPath.removeAll(strings);
		sanPath.removeAll(strings);

		System.out.println(res);
		System.out.println((youPath.size() - 1) + (sanPath.size() - 1));

	}

	private static void addNode(List<Node> nodes, String inp) {
		String[] orb = inp.split("\\)");
		Node pNode;
		Node chNode;
		pNode = nodes.stream().filter(node -> node.getName().equals(orb[0])).findFirst().orElse(
				null);
		chNode = nodes.stream().filter(node -> node.getName().equals(orb[1])).findFirst().orElse(
				null);
		if (pNode == null) {
			pNode = new Node(orb[0]);
			nodes.add(pNode);
		}

		if (chNode == null) {
			chNode = new Node(orb[1]);
			nodes.add(chNode);
		}

		pNode.getChildren().add(chNode);
		chNode.setParent(pNode);
	}

	static class Node {
		private List<Node> children = new ArrayList<>();
		private Node parent;
		private String name;

		public Node(String name) {
			this.name = name;
		}

		public List<Node> getChildren() {
			return children;
		}

		public Node getParent() {
			return parent;
		}

		public void setParent(Node parent) {
			this.parent = parent;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public int countNodes() {
			return (getParent() != null ? getParent().countNodes() + 1 : 0);
		}

		public void getPathToTop(Set<String> stringList) {
			stringList.add(name);
			if(parent != null) {
				parent.getPathToTop(stringList);
			}
		}

		@Override public boolean equals(Object o) {
			if (this == o)
				return true;
			if (o == null || getClass() != o.getClass())
				return false;
			Node node = (Node) o;
			return Objects.equals(getChildren(), node.getChildren()) &&
					Objects.equals(getParent(), node.getParent()) &&
					getName().equals(node.getName());
		}

		@Override public int hashCode() {
			return Objects.hash(getChildren(), getParent(), getName());
		}
	}
}
