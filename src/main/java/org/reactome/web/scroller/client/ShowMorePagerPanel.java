package org.reactome.web.scroller.client;

/**
 * @author Kostas Sidiropoulos <ksidiro@ebi.ac.uk>
 */
/*
 * Copyright 2010 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.user.cellview.client.AbstractPager;
import com.google.gwt.user.cellview.client.CellList;
import com.google.gwt.user.client.ui.*;
import com.google.gwt.view.client.HasRows;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.SingleSelectionModel;



/**
 * A scrolling pager that automatically increases the range every time the
 * scroll bar reaches the bottom.
 */
public class ShowMorePagerPanel extends AbstractPager {

    /**
     * The default increment size.
     */
    private static final int DEFAULT_INCREMENT = 20;

    /**
     * The increment size.
     */
    private int incrementSize = DEFAULT_INCREMENT;

    /**
     * The last scroll position.
     */
    private int lastScrollPos = 0;

    private int startIndex = 0;
    private int endIndex = 0;
    private int visibleWindow = 40;

    /**
     * The scrollable panel.
     */
    private final ScrollPanel scrollable = new ScrollPanel();

    private ListDataProvider<ContactInfo> dataProvider;

    private SimplePanel offsetStartPanel;
    private SimplePanel offsetEndPanel;

    private FlowPanel rootPanel;

    private Object selectedItem = null;

    // Add a selection model so we can select cells.
    final SingleSelectionModel<ContactInfo> selectionModel = new SingleSelectionModel<ContactInfo>(ContactInfo.KEY_PROVIDER);

    /**
     * Construct a new {@link ShowMorePagerPanel}.
     */
    public ShowMorePagerPanel() {
        initWidget(scrollable);

        offsetStartPanel = new SimplePanel();
        offsetStartPanel.getElement().getStyle().setBackgroundColor("green");
        offsetStartPanel.setHeight("0px");

        offsetEndPanel = new SimplePanel();
        offsetEndPanel.getElement().getStyle().setBackgroundColor("red");
        offsetEndPanel.setHeight("0px");

        rootPanel = new FlowPanel();
        rootPanel.add(offsetStartPanel);
        rootPanel.add(offsetEndPanel);

        // Do not let the scrollable take tab focus.
        scrollable.getElement().setTabIndex(-1);

        // Handle scroll events.
        scrollable.addScrollHandler(event -> {
            int oldScrollPos = lastScrollPos;
            lastScrollPos = scrollable.getVerticalScrollPosition();

            HasRows display = getDisplay();
            if (display == null) {
                return;
            }

            int start, length;

            if (oldScrollPos > lastScrollPos) {

                if (lastScrollPos <= display.getVisibleRange().getStart() * 45) {
                    _log("       > > Now: ");
                    _log(" startIndex = " + display.getVisibleRange().getStart());
                    _log(" endIndex = " + (getDisplay().getVisibleRange().getStart() + (getDisplay().getVisibleRange().getLength() - 1)) );

                    start = Math.max(display.getVisibleRange().getStart() - 15, 0);
                    length = Math.min(25, display.getRowCount());

                    _log("       > > Start: " + start );
                    _log("       > > length: " + length );
                    _log("       > > Rows: " + (getDisplay().getRowCount()) );
//
                    display.setVisibleRange( start, length);
                    offsetStartPanel.setHeight(start * 45 + "px");
                    offsetEndPanel.setHeight((display.getRowCount() - (start + length))  * 45 + "px");

                    scrollable.setVerticalScrollPosition(lastScrollPos + 1);

                }
                // Update start and end indexes
                updateStartEndIndexes();
                return;
            }


//            int maxScrollTop = scrollable.getWidget().getOffsetHeight() - scrollable.getOffsetHeight();


            if (lastScrollPos >= (((endIndex) * 45) - scrollable.getOffsetHeight())) { //Needs Updating using the endIndex
//                _log(" ====== down = ");


                if (endIndex >= display.getRowCount() - 1) {
                    // Requires expanding the rows with new data if available
                    _log(" Requesting new data... ");
                    for (int i = 0; i < DEFAULT_INCREMENT; i++) {
                        dataProvider.getList().add(new ContactInfo("Title #" + (ContactInfo.nextId + 1), "Message #" + (ContactInfo.nextId + 1)));
                    }
                    dataProvider.flush();

                    start = Math.max(display.getRowCount() - 30, 0);
                    length = Math.min(30, display.getRowCount());

                    display.setVisibleRange( start, length);
                    offsetStartPanel.setHeight(start  * 45 + "px");
                    offsetEndPanel.setHeight((display.getRowCount() - (start + length))  * 45 + "px");
                } else {

                    start = endIndex - 10;
                    length = Math.min(20, 10 + display.getRowCount() - endIndex);
                }

                display.setVisibleRange( start, length);
                offsetStartPanel.setHeight(start  * 45 + "px");
                offsetEndPanel.setHeight((display.getRowCount() - (start + length))  * 45 + "px");

                if(selectionModel.getSelectedObject() != null) {
                    selectedItem = selectionModel.getSelectedObject();
                    selectionModel.clear();
                }

                scrollable.setVerticalScrollPosition(lastScrollPos);

                // Update start and end indexes
                updateStartEndIndexes();

            }
        });
    }

    private void updateStartEndIndexes() {
        startIndex = getDisplay().getVisibleRange().getStart();
        endIndex = startIndex + getDisplay().getVisibleRange().getLength() - 1;
        _log(" Rows: " + getDisplay().getRowCount() + " [startIndex= " + startIndex + " endIndex= " + endIndex + "] visible: " + ((endIndex + 1) - startIndex));
    }

    /**
     * Get the number of rows by which the range is increased when the scrollbar
     * reaches the bottom.
     *
     * @return the increment size
     */
    public int getIncrementSize() {
        return incrementSize;
    }

    @Override
    public void setDisplay(HasRows display) {
//        assert display instanceof Widget : "display must extend Widget";
//        scrollable.setWidget((Widget) display);
//        super.setDisplay(display);

        assert display instanceof Widget : "display must extend Widget";
        rootPanel.insert((Widget) display, 1);
        scrollable.setWidget(rootPanel);
        super.setDisplay(display);
        ((CellList)display).setSelectionModel(selectionModel);
        selectionModel.clear();

    }

    public void setDataProvider(ListDataProvider dataProvider) {
        this.dataProvider = dataProvider;
        updateStartEndIndexes();
    }

    /**
     * Set the number of rows by which the range is increased when the scrollbar
     * reaches the bottom.
     *
     * @param incrementSize the incremental number of rows
     */
    public void setIncrementSize(int incrementSize) {
        this.incrementSize = incrementSize;
    }

    @Override
    protected void onRangeOrRowCountChanged() {

    }


    private static native void _log(String message)/*-{
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
    @CssResource.ImportedWithPrefix("diagram-ShowMorePagerPanel")
    public interface ResourceCSS extends CssResource {
        /**
         * The path to the default CSS styles used by this resource.
         */
        String CSS = "org/reactome/web/scroller/client/ShowMorePagerPanel.css";

        String scrollable();

        String contactFormCell();
    }
}
