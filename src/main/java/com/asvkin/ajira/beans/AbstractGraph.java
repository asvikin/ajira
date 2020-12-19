package com.asvkin.ajira.beans;

import java.util.*;

public abstract class AbstractGraph<T> {
	// We use Hashmap to store the edges in the graph
	protected Map<T, List<T>> map = new HashMap<>();
	
	// This function adds a new vertex to the graph
	public void addVertex(T s) {
		map.put(s, new LinkedList<T>());
	}
	
	// This function adds the edge
	// between source to destination
	public void addEdge(T source,
			T destination,
			boolean bidirectional) {
		if (!map.containsKey(source))
			addVertex(source);
		if (!map.containsKey(destination))
			addVertex(destination);
		map.get(source).add(destination);
		if (bidirectional == true) {
			map.get(destination).add(source);
		}
	}
	
	// This function gives the count of vertices
	public Set<T> getVertex() {
		return map.keySet();
	}
	
	// This function gives whether
	// a vertex is present or not.
	public boolean hasVertex(T s) {
		return map.containsKey(s);
	}
	
	// This function gives whether an edge is present or not.
	public boolean hasEdge(T s, T d) {
		return map.get(s).contains(d);
	}
	
	// Prints the adjancency list of each vertex.
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		for (T v : map.keySet()) {
			builder.append(v.toString() + ": ");
			for (T w : map.get(v)) {
				builder.append(w.toString() + " ");
			}
			builder.append("\n");
		}
		return (builder.toString());
	}
} 
  