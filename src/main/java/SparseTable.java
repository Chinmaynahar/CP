import java.util.Objects;
import java.util.function.LongBinaryOperator;

public final class SparseTable {

    private final int n;
    private final int maxK;
    private final long[][] table;
    private final int[] logTable;
    private final LongBinaryOperator op;


    public SparseTable(long[] array, LongBinaryOperator op) {
        Objects.requireNonNull(array, "Input array must not be null");
        this.op = Objects.requireNonNull(op, "Operator must not be null");
        this.n = array.length;

        if (n == 0) {
            this.maxK = 0;
            this.table = new long[0][0];
            this.logTable = new int[1];
            return;
        }

        this.maxK = 31 - Integer.numberOfLeadingZeros(n);
        this.table = new long[maxK + 1][n];


        this.logTable = new int[n + 1];
        this.logTable[0] = 0;
        for (int i = 1; i <= n; i++) {
            this.logTable[i] = 31 - Integer.numberOfLeadingZeros(i);
        }


        System.arraycopy(array, 0, this.table[0], 0, n);

        for (int k = 1; k <= maxK; k++) {
            int halfLength = 1 << (k - 1);
            long[] currentLayer = this.table[k];
            long[] previousLayer = this.table[k - 1];
            int limit = n - (1 << k) + 1;

            for (int i = 0; i < limit; i++) {
                currentLayer[i] = op.applyAsLong(previousLayer[i], previousLayer[i + halfLength]);
            }
        }
    }

    public long queryIdempotent(int l, int r) {
        rangeCheck(l, r);
        int len = r - l + 1;
        int k = logTable[len];
        return op.applyAsLong(table[k][l], table[k][r - (1 << k) + 1]);
    }


    public long queryAssociative(int l, int r) {
        rangeCheck(l, r);
        int len = r - l + 1;
        int k = logTable[len];


        long accumulator = table[k][r - (1 << k) + 1];
        int currR = r - (1 << k);


        while (currR >= l) {
            len = currR - l + 1;
            k = logTable[len];
            accumulator = op.applyAsLong(table[k][currR - (1 << k) + 1], accumulator);
            currR -= (1 << k);
        }

        return accumulator;
    }

    public int length() {
        return n;
    }

    private void rangeCheck(int l, int r) {
        if (l < 0 || r >= n || l > r) {
            throw new IndexOutOfBoundsException(
                    String.format("Invalid range: [%d, %d] for size %d", l, r, n)
            );
        }
    }

    public static long gcd(long a, long b) {
        while (b != 0) {
            long temp = b;
            b = a % b;
            a = temp;
        }
        return Math.abs(a);
    }
}