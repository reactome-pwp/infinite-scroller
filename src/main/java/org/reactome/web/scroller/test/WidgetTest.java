package org.reactome.web.scroller.test;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.view.client.SingleSelectionModel;
import org.reactome.web.scroller.client.InfiniteScrollList;

import static org.reactome.web.scroller.client.util.Placeholder.ROWS;
import static org.reactome.web.scroller.client.util.Placeholder.START;



/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public class WidgetTest implements EntryPoint {

    // Add a selection model so we can select cells.
    final SingleSelectionModel<ContactInfo> selectionModel = new SingleSelectionModel<>(ContactInfo.KEY_PROVIDER);

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

            sb.appendHtmlConstant("<table style='height:45;'>");
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
        AsyncContactProvider dataProvider = new AsyncContactProvider();
        dataProvider.setURL("/ContentService/search/fireworks?query=pten&species=Homo%20sapiens" + "&" + START.getUrlValue() + "&" + ROWS.getUrlValue());

        InfiniteScrollList<ContactInfo> myList = new InfiniteScrollList(contactCell, ContactInfo.KEY_PROVIDER, dataProvider);

        SimpleLayoutPanel container = new SimpleLayoutPanel();
        container.setHeight(400 + "px");
        container.setWidth(250 + "px");
        container.getElement().getStyle().setBackgroundColor("azure");
        container.add(myList);

        RootLayoutPanel.get().add(container);

        Scheduler.get().scheduleFixedDelay(() -> {
            myList.setPageSize(30);
            myList.loadFirstPage();
            return false;
        }, 2000);


    }
}
