package org.reactome.web.scroller.test;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.cellview.client.CellList;
import com.google.gwt.user.cellview.client.HasKeyboardPagingPolicy;
import com.google.gwt.user.cellview.client.HasKeyboardSelectionPolicy;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.ProvidesKey;
import org.reactome.web.scroller.client.ShowMorePagerPanel;

import java.util.LinkedList;
import java.util.List;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public class WidgetTest implements EntryPoint {

    private CellList<ContactInfo> cellList;

    /**
     * The provider that holds the list of contacts in the database.
     */
    private ListDataProvider<ContactInfo> dataProvider = new ListDataProvider<>();


    /**
     * Information about a contact.
     */
    public static class ContactInfo implements Comparable<ContactInfo> {

        /**
         * The key provider that provides the unique ID of a contact.
         */
        public static final ProvidesKey<ContactInfo> KEY_PROVIDER = new ProvidesKey<ContactInfo>() {
            @Override
            public Object getKey(ContactInfo item) {
                return item == null ? null : item.getId();
            }
        };

        public static int nextId = 0;

        private final int id;
        private String title;
        private String message;

        public ContactInfo(String title, String message) {
            this.id = nextId;
            nextId++;
            this.title = title;
            this.message = message;
        }

        @Override
        public int compareTo(ContactInfo o) {
            return (o == null || o.title == null) ? -1 : -o.title.compareTo(title);
        }

        @Override
        public boolean equals(Object o) {
            if (o instanceof ContactInfo) {
                return id == ((ContactInfo) o).id;
            }
            return false;
        }

        public int getId() {
            return id;
        }

        public String getTitle() {
            return title;
        }

        public String getMessage() {
            return message;
        }
    }

    /**
     * The Cell used to render a {@link ContactInfo}.
     */
    static class ContactCell extends AbstractCell<ContactInfo> {

        /**
         * The html of the image used for contacts.
         */

        public ContactCell() {
        }

        @Override
        public void render(Context context, ContactInfo value, SafeHtmlBuilder sb) {
            // Value can be null, so do a null check..
            if (value == null) {
                return;
            }

            sb.appendHtmlConstant("<table>");
            sb.appendHtmlConstant("<tr>");
            // Add the name and address.
            sb.appendHtmlConstant("<td style='font-size:95%;'>");
            sb.appendEscaped(value.getTitle());
            sb.appendHtmlConstant("</td></tr><tr><td>");
            sb.appendEscaped(value.getMessage());
            sb.appendHtmlConstant("</td></tr></table>");
        }
    }

    @Override
    public void onModuleLoad() {

        // Create a CellList.
        ContactCell contactCell = new ContactCell();

        // Set a key provider that provides a unique key for each contact. If key is
        // used to identify contacts when fields (such as the name and address)
        // change.
        cellList = new CellList<>(contactCell, ContactInfo.KEY_PROVIDER);
        cellList.setPageSize(20);
        cellList.setKeyboardPagingPolicy(HasKeyboardPagingPolicy.KeyboardPagingPolicy.INCREASE_RANGE);
        cellList.setKeyboardSelectionPolicy(HasKeyboardSelectionPolicy.KeyboardSelectionPolicy.DISABLED);

        dataProvider.addDataDisplay(cellList);
        addContacts();

        ShowMorePagerPanel pagerPanel = new ShowMorePagerPanel();
        pagerPanel.setStyleName(ShowMorePagerPanel.RESOURCES.getCSS().scrollable());
        pagerPanel.setDisplay(cellList);
        pagerPanel.setDataProvider(dataProvider);

        RootLayoutPanel.get().add(pagerPanel);

    }

    public void addContacts() {
        for (int i = 0; i <20 ; i++) {
            dataProvider.getList().add(new ContactInfo("Title I#" + (ContactInfo.nextId + 1), "Message #" + (ContactInfo.nextId + 1)));
        }
        dataProvider.flush();
    }
}
