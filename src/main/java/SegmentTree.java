package PACKAGE_NAME;

public class SegmentTree{
    public static class IterativeSegmentTree<T> {
        private final int n;
        private final T[] tree;
        private final BinaryOperator<T> op;
        private final T identity;

        @SuppressWarnings("unchecked")
        public IterativeSegmentTree(T[] arr, BinaryOperator<T> op, T identity) {
            this.n = arr.length;
            this.op = op;
            this.identity = identity;
            this.tree = (T[]) new Object[2 * n];
            for (int i = 0; i < n; i++) tree[n + i] = arr[i];
            for (int i = n - 1; i > 0; i--) tree[i] = op.apply(tree[i << 1], tree[i << 1 | 1]);
        }

        public void update(int pos, T val) {
            pos += n;
            tree[pos] = val;
            for (pos >>= 1; pos > 0; pos >>= 1) {
                tree[pos] = op.apply(tree[pos << 1], tree[pos << 1 | 1]);
            }
        }

        public T query(int l, int r) {
            if (l > r || l < 0 || r >= n) return identity;
            T resLeft = identity;
            T resRight = identity;
            for (l += n, r += n + 1; l < r; l >>= 1, r >>= 1) {
                if ((l & 1) == 1) resLeft = op.apply(resLeft, tree[l++]);
                if ((r & 1) == 1) resRight = op.apply(tree[--r], resRight);
            }
            return op.apply(resLeft, resRight);
        }
    }

    public static class LazySegmentTree {
        private final int n;
        private final long[] tree;
        private final long[] lazy;

        public LazySegmentTree(long[] arr) {
            this.n = arr.length;
            this.tree = new long[4 * n];
            this.lazy = new long[4 * n];
            if (n > 0) build(1, 0, n - 1, arr);
        }

        private void build(int node, int l, int r, long[] arr) {
            if (l == r) {
                tree[node] = arr[l];
                return;
            }
            int mid = l + ((r - l) >> 1);
            build(node << 1, l, mid, arr);
            build((node << 1) | 1, mid + 1, r, arr);
            tree[node] = tree[node << 1] + tree[(node << 1) | 1];
        }

        private void pushDown(int node, int l, int r) {
            if (lazy[node] == 0) return;
            int mid = l + ((r - l) >> 1);
            int left = node << 1;
            int right = left | 1;
            long val = lazy[node];

            tree[left] += val * (mid - l + 1);
            lazy[left] += val;

            tree[right] += val * (r - mid);
            lazy[right] += val;

            lazy[node] = 0;
        }

        public void updateRange(int ql, int qr, long val) {
            if (ql > qr || ql < 0 || qr >= n) return;
            updateRange(1, 0, n - 1, ql, qr, val);
        }

        private void updateRange(int node, int l, int r, int ql, int qr, long val) {
            if (ql <= l && r <= qr) {
                tree[node] += val * (r - l + 1);
                lazy[node] += val;
                return;
            }
            pushDown(node, l, r);
            int mid = l + ((r - l) >> 1);
            if (ql <= mid) updateRange(node << 1, l, mid, ql, qr, val);
            if (qr > mid) updateRange((node << 1) | 1, mid + 1, r, ql, qr, val);
            tree[node] = tree[node << 1] + tree[(node << 1) | 1];
        }

        public long querySum(int ql, int qr) {
            if (ql > qr || ql < 0 || qr >= n) return 0;
            return querySum(1, 0, n - 1, ql, qr);
        }

        private long querySum(int node, int l, int r, int ql, int qr) {
            if (ql <= l && r <= qr) return tree[node];
            pushDown(node, l, r);
            int mid = l + ((r - l) >> 1);
            long sum = 0;
            if (ql <= mid) sum += querySum(node << 1, l, mid, ql, qr);
            if (qr > mid) sum += querySum((node << 1) | 1, mid + 1, r, ql, qr);
            return sum;
        }
    }
}