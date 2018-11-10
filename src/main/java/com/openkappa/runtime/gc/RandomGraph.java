package com.openkappa.runtime.gc;

import org.openjdk.jmh.annotations.*;

import java.util.Arrays;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

@OutputTimeUnit(TimeUnit.MICROSECONDS)
public class RandomGraph {

  public static class Node {

    private final int id;

    private final Node[] links;

    public Node(int id, int total) {
      this.id = id;
      this.links = new Node[total];
    }

    public void link(Node node) {
      links[node.id] = node;
    }

    public void unlink(Node node) {
      links[node.id] = null;
    }
  }

  @State(Scope.Benchmark)
  public static class Nodes {
    @Param({"10", "100"})
    int rows;
    @Param({"10", "100"})
    int columns;
    @Param({"65536"})
    int period;
    int pos = 0;
    private Node[][] nodes;
    private int[][] transitions;

    @Setup(Level.Trial)
    public void setup() {
      nodes = new Node[columns][rows];
      for (int i = 0; i < columns; ++i) {
        for (int j = 0; j < rows; ++j) {
          nodes[i][j] = new Node(i * rows + j, columns  * rows);
        }
      }
      transitions = new int[period][4];
      for (int i = 0; i < period; ++i) {
        transitions[i] = new int[]{
                ThreadLocalRandom.current().nextInt(columns),
                ThreadLocalRandom.current().nextInt(rows),
                ThreadLocalRandom.current().nextInt(columns),
                ThreadLocalRandom.current().nextInt(rows)
        };
      }
      for (int i = 0; i < columns; ++i) {
        for (int j = 0; j < rows; ++j) {
          link();
        }
      }
    }

    public void link() {
      int i = (pos++ & (period - 1));
      nodes[transitions[i][0]][transitions[i][1]].link(nodes[transitions[i][2]][transitions[i][3]]);
    }

    public void unlink() {
      int i = (pos++ & (period - 1));
      nodes[transitions[i][0]][transitions[i][1]].unlink(nodes[transitions[i][2]][transitions[i][3]]);
    }
  }

  @Benchmark
  public Nodes permute(Nodes nodes) {
    nodes.link();
    nodes.unlink();
    return nodes;
  }
}
