package org.reactome.web.scroller.client;

import java.util.List;

/**
 * @author Kostas Sidiropoulos <ksidiro@ebi.ac.uk>
 */
public interface InfiniteListDataProvider<T> {

    List<T> requestItems(int start, int length);
}
