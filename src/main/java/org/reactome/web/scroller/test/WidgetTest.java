package org.reactome.web.scroller.test;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.cellview.client.CellList;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.view.client.SingleSelectionModel;
import org.reactome.web.scroller.client.InfiniteScrollList;


/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public class WidgetTest implements EntryPoint {

    private CellList<ContactInfo> cellList;

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

        ContactProvider dataProvider = new ContactProvider();
        InfiniteScrollList<ContactInfo> myList = new InfiniteScrollList(contactCell, ContactInfo.KEY_PROVIDER, dataProvider);

        SimpleLayoutPanel container = new SimpleLayoutPanel();
        container.setHeight(400 + "px");
        container.setWidth(250 + "px");
        container.getElement().getStyle().setBackgroundColor("azure");
        container.add(myList);

        RootLayoutPanel.get().add(container);


//        Scheduler.get().scheduleFixedDelay(new Scheduler.RepeatingCommand() {
//            int i = 0;
//            @Override
//            public boolean execute() {
//                if(i%2==0) {
//                    container.setHeight(200 + "px");
//                } else {
//                    container.setHeight(400 + "px");
//                }
//                i++;
//                return true;
//            }
//        }, 3000);

//        Scheduler.get().scheduleFixedDelay(new Scheduler.RepeatingCommand() {
//            int i = 0;
//            @Override
//            public boolean execute() {
//                myList.setPageSize(30);
//                myList.loadFirstPage();
//                return false;
//            }
//        }, 10000);


    }
}
