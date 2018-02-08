package org.reactome.web.scroller.test;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.cellview.client.CellList;
import com.google.gwt.user.cellview.client.HasKeyboardPagingPolicy;
import com.google.gwt.user.cellview.client.HasKeyboardSelectionPolicy;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.view.client.SingleSelectionModel;
import org.reactome.web.scroller.client.ContactInfo;
import org.reactome.web.scroller.client.ListItemsManager;
import org.reactome.web.scroller.client.ShowMorePagerPanel;


/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public class WidgetTest implements EntryPoint {

    private CellList<ContactInfo> cellList;

    /**
     * The provider that holds the list of contacts in the database.
     */
    private ListItemsManager<ContactInfo> listItemsManager = new ListItemsManager<>();

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

        cellList = new CellList<>(contactCell, ContactInfo.KEY_PROVIDER);

        cellList.setPageSize(30);
        cellList.setKeyboardPagingPolicy(HasKeyboardPagingPolicy.KeyboardPagingPolicy.INCREASE_RANGE);
        cellList.setKeyboardSelectionPolicy(HasKeyboardSelectionPolicy.KeyboardSelectionPolicy.DISABLED);
        cellList.setSelectionModel(selectionModel);
        selectionModel.clear();

//        dataProvider.setDataDisplay(cellList);
        listItemsManager.setDataDisplay(cellList);
        listItemsManager.loadNewData(listItemsManager.getTotalRows(),30);

        ShowMorePagerPanel pagerPanel = new ShowMorePagerPanel();
        pagerPanel.setStyleName(ShowMorePagerPanel.RESOURCES.getCSS().scrollable());
        pagerPanel.setDisplay(cellList);
        pagerPanel.setDataManager(listItemsManager);

        RootLayoutPanel.get().add(pagerPanel);

    }
}
