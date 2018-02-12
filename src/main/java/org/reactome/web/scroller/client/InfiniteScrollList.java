package org.reactome.web.scroller.client;

import com.google.gwt.cell.client.Cell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.user.cellview.client.CellList;
import com.google.gwt.user.cellview.client.HasKeyboardPagingPolicy;
import com.google.gwt.user.cellview.client.HasKeyboardSelectionPolicy;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.view.client.ProvidesKey;
import com.google.gwt.view.client.SelectionModel;


/**
 * A list that provides infinite scroll capability.
 * As the user scrolls up or down new data are requested
 * and added in the wrapped CellList
 *
 * @author Kostas Sidiropoulos <ksidiro@ebi.ac.uk>
 */
public class InfiniteScrollList<T> extends Composite {

    public static final int DEFAULT_PAGE_SIZE = 20;
    public static final int DEFAULT_DATA_INCREMENT = DEFAULT_PAGE_SIZE / 2;
    public static final int DEFAULT_ITEM_HEIGHT = 45;

    private static final boolean isFirefox = isFirefox();

    // The last scroll position
    private int lastScrollPos = 0;

    private int pageSize = DEFAULT_PAGE_SIZE;
    private int dataIncrement = DEFAULT_DATA_INCREMENT;

    private int listWindowHeight = 0;
    private int rowSize = DEFAULT_ITEM_HEIGHT;

    private ListManager<T> listManager;
    private CellList<T> display;

    private final ScrollPanel scrollable = new ScrollPanel();

    // Used at the beginning of the list to artificially increase the size of the scrollpanel
    private SimplePanel offsetStartPanel;

    // Used at the end of the list to artificially increase the size of the scrollpanel
    private SimplePanel offsetEndPanel;


    public InfiniteScrollList(final Cell<T> cell, ProvidesKey<T> keyProvider, InfiniteListDataProvider<T> dataProvider) {
        initWidget(scrollable);
        setStyleName(RESOURCES.getCSS().scrollable());

        display = new CellList<>(cell, keyProvider);
        display.setPageSize(pageSize);
        display.setKeyboardPagingPolicy(HasKeyboardPagingPolicy.KeyboardPagingPolicy.INCREASE_RANGE);
        display.setKeyboardSelectionPolicy(HasKeyboardSelectionPolicy.KeyboardSelectionPolicy.DISABLED);

        listManager = new ListManager<>(dataProvider);
        listManager.setDataDisplay(display);
        listManager.loadNewData(listManager.getTotalRows(), pageSize, pageSize);

        offsetStartPanel = new SimplePanel();
        offsetStartPanel.setStyleName(RESOURCES.getCSS().offsetDiv());

        offsetEndPanel = new SimplePanel();
        offsetEndPanel.setStyleName(RESOURCES.getCSS().offsetDiv());

        FlowPanel rootPanel = new FlowPanel();
        rootPanel.add(offsetStartPanel);
        rootPanel.add(offsetEndPanel);
        rootPanel.insert(display, 1);

        scrollable.setWidget(rootPanel);

        // Do not let the scrollable take tab focus.
        scrollable.getElement().setTabIndex(-1);

        // Handle scroll events.
        scrollable.addScrollHandler(event -> {
            lastScrollPos = scrollable.getVerticalScrollPosition();

            if (display == null) {
                return;
            }

            int curStartIndex = listManager.getCurStartIndex();
            int curEndIndex = listManager.getCurEndIndex();

            if (lastScrollPos <= curStartIndex * DEFAULT_ITEM_HEIGHT) {
                listManager.loadPreviousData(pageSize);

                offsetStartPanel.setHeight(listManager.getCurStartIndex()  * DEFAULT_ITEM_HEIGHT + "px");
                offsetEndPanel.setHeight((listManager.getTotalRows() - (listManager.getCurStartIndex() + listManager.getCurrentRows()))  * DEFAULT_ITEM_HEIGHT + "px");

                if (isFirefox) { scrollable.setVerticalScrollPosition(lastScrollPos);}
                else { scrollable.setVerticalScrollPosition(curStartIndex * DEFAULT_ITEM_HEIGHT); }

            } else if (lastScrollPos >= (((curEndIndex) * DEFAULT_ITEM_HEIGHT) - scrollable.getOffsetHeight())) {
                if (curEndIndex >= listManager.getTotalRows() - 1) {
                    // Requires expanding the rows with new data if available
                    listManager.loadNewData(listManager.getTotalRows(), dataIncrement, pageSize);
                    display.setVisibleRange(0, listManager.getCurrentRows());
                } else {
                    listManager.loadNextData(pageSize);
                }

                offsetStartPanel.setHeight(listManager.getCurStartIndex()  * DEFAULT_ITEM_HEIGHT + "px");
                offsetEndPanel.setHeight((listManager.getTotalRows() - (listManager.getCurStartIndex() + listManager.getCurrentRows()))  * DEFAULT_ITEM_HEIGHT + "px");

                if (isFirefox) { scrollable.setVerticalScrollPosition(lastScrollPos);}
                else { scrollable.setVerticalScrollPosition((((curEndIndex) * DEFAULT_ITEM_HEIGHT) - scrollable.getOffsetHeight())); }
            }

            if (scrollable.getVerticalScrollPosition() < offsetStartPanel.getElement().getOffsetHeight()) {
                offsetStartPanel.setHeight(scrollable.getVerticalScrollPosition() + "px");
            }

            _log(" >> " + listManager.toString() + " << ");
        });

        Scheduler.get().scheduleDeferred(() -> {
            listWindowHeight = scrollable.getElement().getOffsetHeight();
        });
    }

    public void loadFirstPage() {
        if (listManager.getTotalRows() == 0) {
            listManager.loadNewData(0, pageSize, pageSize);
        }
    }

    public void setPageSize(int newPageSize) {
        this.pageSize = newPageSize;
        display.setPageSize(newPageSize);

        listManager.clear();
        listManager.loadNewData(0, newPageSize, newPageSize);

        offsetStartPanel.setHeight(listManager.getCurStartIndex()  * DEFAULT_ITEM_HEIGHT + "px");
        offsetEndPanel.setHeight((listManager.getTotalRows() - (listManager.getCurStartIndex() + listManager.getCurrentRows()))  * DEFAULT_ITEM_HEIGHT + "px");

        scrollable.setVerticalScrollPosition(1);

    }

    public void setSelectionModel(SelectionModel<? super T> selectionModel) {
        display.setSelectionModel(selectionModel);
    }


    public CellList<T> getDisplay() {
        return display;
    }

    private static native boolean isFirefox()/*-{
        // Firefox 1.0+
        return typeof InstallTrigger !== 'undefined';
    }-*/;

    public static native void _log(String message)/*-{
        if($wnd.console){
            $wnd.console.log(message);
        }
    }-*/;


    public static Resources RESOURCES;
    static {
        RESOURCES = GWT.create(Resources.class);
        RESOURCES.getCSS().ensureInjected();
    }

    /**
     * A ClientBundle of resources used by this widget.
     */
    public interface Resources extends ClientBundle {
        /**
         * The styles used in this widget.
         */
        @Source(ResourceCSS.CSS)
        ResourceCSS getCSS();
    }

    /**
     * Styles used by this widget.
     */
    @CssResource.ImportedWithPrefix("diagram-InfiniteScrollList")
    public interface ResourceCSS extends CssResource {
        /**
         * The path to the default CSS styles used by this resource.
         */
        String CSS = "org/reactome/web/scroller/client/InfiniteScrollList.css";

        String scrollable();

        String offsetDiv();

        String contactFormCell();
    }
}
