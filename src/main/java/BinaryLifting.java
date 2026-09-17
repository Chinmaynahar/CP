package PACKAGE_NAME;

public class BinaryLifting {
    private final int n;
    private final int LOG;
    private final List<Integer>[] adj;
    private final int[][] up;
    private final int[] tin;
    private final int[] tout;
    private int timer;

    @SuppressWarnings("unchecked")
    public BinaryLifting(int n) {
        this.n = n;
        this.LOG = 31 - Integer.numberOfLeadingZeros(Math.max(n, 1)) + 1;
        this.adj = new ArrayList[n];
        for (int i = 0; i < n; i++) adj[i] = new ArrayList<>();
        this.up = new int[n][LOG + 1];
        this.tin = new int[n];
        this.tout = new int[n];
    }

    public void addEdge(int u, int v) {
        adj[u].add(v);
        adj[v].add(u);
    }

    public void build(int root) {
        timer = 0;

        int[] stackNode = new int[n];
        int[] edgeIndex = new int[n];
        int top = 0;

        stackNode[0] = root;
        edgeIndex[0] = 0;
        up[root][0] = root;
        tin[root] = ++timer;

        while (top >= 0) {
            int u = stackNode[top];
            int p = up[u][0];

            if (edgeIndex[top] < adj[u].size()) {
                int v = adj[u].get(edgeIndex[top]++);
                if (v != p) {
                    up[v][0] = u;
                    tin[v] = ++timer;

                    top++;
                    stackNode[top] = v;
                    edgeIndex[top] = 0;
                }
            } else {
                tout[u] = ++timer;
                top--;
            }
        }
        for (int j = 1; j <= LOG; j++) {
            for (int i = 0; i < n; i++) {
                up[i][j] = up[up[i][j - 1]][j - 1];
            }
        }
    }

    public boolean isAncestor(int u, int v) {
        return tin[u] <= tin[v] && tout[u] >= tout[v];
    }

    public int lca(int u, int v) {
        if (isAncestor(u, v)) return u;
        if (isAncestor(v, u)) return v;

        for (int i = LOG; i >= 0; i--) {
            if (!isAncestor(up[u][i], v)) {
                u = up[u][i];
            }
        }
        return up[u][0];
    }

    public int getKthAncestor(int node, int k) {
        for (int i = 0; i <= LOG; i++) {
            if (((k >> i) & 1) == 1) {
                node = up[node][i];
            }
        }
        return node;
    }
}