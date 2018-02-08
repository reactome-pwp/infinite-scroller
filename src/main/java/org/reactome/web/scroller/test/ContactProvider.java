package org.reactome.web.scroller.test;

import org.reactome.web.scroller.client.InfiniteListDataProvider;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Kostas Sidiropoulos <ksidiro@ebi.ac.uk>
 */
public class ContactProvider implements InfiniteListDataProvider<ContactInfo> {

    @Override
    public List<ContactInfo> requestItems(int start, int length) {
        List<ContactInfo> rtn = new ArrayList<>();
        for (int i = start; i <start + length; i++) {
//            _log("generating contact " + i);
            rtn.add(new ContactInfo("Title #" + i, "Message #" + i));
        }
        return rtn;
    }
}
