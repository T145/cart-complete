package T145.metaltransport.api;

/**
 * <p>An immutable pair consisting of two {@code Object} elements.</p>
 * 
 * <p>Although the implementation is immutable, there is no restriction on the objects
 * that may be stored. If mutable objects are stored in the pair, then the pair
 * itself effectively becomes mutable. The class is also {@code final}, so a subclass
 * can not add undesirable behaviour.</p>
 * 
 * <p>#ThreadSafe# if both paired objects are thread-safe</p>
 *
 * @param <L> the key element type
 * @param <R> the value element type
 *
 * @since Lang 3.0
 */
public final class ImmutableDuality<L, R> extends Duality<L, R> {

    /** key object */
    public final L key;
    /** value object */
    public final R value;

    /**
     * <p>Obtains an immutable pair of from two objects inferring the generic types.</p>
     * 
     * <p>This factory allows the pair to be created using inference to
     * obtain the generic types.</p>
     * 
     * @param <L> the key element type
     * @param <R> the value element type
     * @param key  the key element, may be null
     * @param value  the value element, may be null
     * @return a pair formed from the two parameters, not null
     */
    public static <L, R> ImmutableDuality<L, R> of(final L key, final R value) {
        return new ImmutableDuality<L, R>(key, value);
    }

    /**
     * Create a new pair instance.
     *
     * @param key  the key value, may be null
     * @param value  the value value, may be null
     */
    public ImmutableDuality(final L key, final R value) {
        super();
        this.key = key;
        this.value = value;
    }

    //-----------------------------------------------------------------------
    /**
     * {@inheritDoc}
     */
    @Override
    public L getKey() {
        return key;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public R getValue() {
        return value;
    }

    /**
     * <p>Throws {@code UnsupportedOperationException}.</p>
     * 
     * <p>This pair is immutable, so this operation is not supported.</p>
     *
     * @param value  the value to set
     * @return never
     * @throws UnsupportedOperationException as this operation is not supported
     */
    @Override
    public R setValue(final R value) {
        throw new UnsupportedOperationException();
    }
}