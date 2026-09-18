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
