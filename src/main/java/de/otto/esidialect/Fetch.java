package de.otto.esidialect;

import java.util.function.Function;

/**
 * <p>
 * Function that is used to fetch the content from the esi-include src
 * </p>
 * <p>Gets the src url as argument.</p>
 * <p>Needs to return a {@code Response}-Objekt with the content and status of the request.</p>
 *
 */
public interface Fetch extends Function<String, Response> {
}
