package T145.metaltransport.api;

import java.io.Serializable;
import java.util.Map;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.builder.CompareToBuilder;

public abstract class Duality<L, R> implements Map.Entry<L, R>, Comparable<Duality<L, R>>, Serializable {

	/**
	 * <p>
	 * Obtains an immutable pair of from two objects inferring the generic types.
	 * </p>
	 * 
	 * <p>
	 * This factory allows the pair to be created using inference to obtain the
	 * generic types.
	 * </p>
	 * 
	 * @param <L>   the key element type
	 * @param <R>   the value element type
	 * @param key  the key element, may be null
	 * @param value the value element, may be null
	 * @return a pair formed from the two parameters, not null
	 */
	public static <L, R> Duality<L, R> of(final L key, final R value) {
		return new ImmutableDuality<L, R>(key, value);
	}

	// -----------------------------------------------------------------------
	/**
	 * <p>
	 * Compares the pair based on the key element followed by the value element.
	 * The types must be {@code Comparable}.
	 * </p>
	 * 
	 * @param other the other pair, not null
	 * @return negative if this is less, zero if equal, positive if greater
	 */
	@Override
	public int compareTo(final Duality<L, R> other) {
		return new CompareToBuilder().append(getKey(), other.getKey()).append(getValue(), other.getValue()).toComparison();
	}

	/**
	 * <p>
	 * Compares this pair to another based on the two elements.
	 * </p>
	 * 
	 * @param obj the object to compare to, null returns false
	 * @return true if the elements of the pair are equal
	 */
	@SuppressWarnings("deprecation") // ObjectUtils.equals(Object, Object) has been deprecated in 3.2
	@Override
	public boolean equals(final Object obj) {
		if (obj == this) {
			return true;
		}
		if (obj instanceof Map.Entry<?, ?>) {
			final Map.Entry<?, ?> other = (Map.Entry<?, ?>) obj;
			return ObjectUtils.equals(getKey(), other.getKey()) && ObjectUtils.equals(getValue(), other.getValue());
		}
		return false;
	}

	/**
	 * <p>
	 * Returns a suitable hash code. The hash code follows the definition in
	 * {@code Map.Entry}.
	 * </p>
	 * 
	 * @return the hash code
	 */
	@Override
	public int hashCode() {
		// see Map.Entry API specification
		return (getKey() == null ? 0 : getKey().hashCode()) ^ (getValue() == null ? 0 : getValue().hashCode());
	}

	/**
	 * <p>
	 * Returns a String representation of this pair using the format
	 * {@code ($key,$value)}.
	 * </p>
	 * 
	 * @return a string describing this object, not null
	 */
	@Override
	public String toString() {
		return new StringBuilder().append('(').append(getKey()).append(',').append(getValue()).append(')').toString();
	}

	/**
	 * <p>
	 * Formats the receiver using the given format.
	 * </p>
	 * 
	 * <p>
	 * This uses {@link java.util.Formattable} to perform the formatting. Two
	 * variables may be used to embed the key and value elements. Use {@code %1$s}
	 * for the key element (key) and {@code %2$s} for the value element (value).
	 * The default format used by {@code toString()} is {@code (%1$s,%2$s)}.
	 * </p>
	 * 
	 * @param format the format string, optionally containing {@code %1$s} and
	 *               {@code %2$s}, not null
	 * @return the formatted string, not null
	 */
	public String toString(final String format) {
		return String.format(format, getKey(), getValue());
	}

}