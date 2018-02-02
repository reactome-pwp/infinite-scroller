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
import org.reactome.web.scroller.test.WidgetTest;


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

    private ListDataProvider<WidgetTest.ContactInfo> dataProvider;

    private SimplePanel offsetStartPanel;
    private SimplePanel offsetEndPanel;

    private FlowPanel rootPanel;

    private Object selectedItem = null;

    // Add a selection model so we can select cells.
    final SingleSelectionModel<WidgetTest.ContactInfo> selectionModel = new SingleSelectionModel<WidgetTest.ContactInfo>(WidgetTest.ContactInfo.KEY_PROVIDER);

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

//        // Handle scroll events.
//        scrollable.addScrollHandler(event -> {
//            // If scrolling up, ignore the event.
//            int oldScrollPos = lastScrollPos;
//            lastScrollPos = scrollable.getVerticalScrollPosition();
//            _log(" oldScroll = " + oldScrollPos);
//            _log(" current = " + lastScrollPos);
//
//            HasRows display = getDisplay();
//            if (display == null) {
//                return;
//            }
//
//            if (oldScrollPos >= lastScrollPos) {
//                _log(" display.getVisibleRange().getStart() = " + display.getVisibleRange().getStart());
//                _log(" display.getVisibleRange().getLength() = " + display.getVisibleRange().getLength());
//
//                if (lastScrollPos <= display.getVisibleRange().getStart() * 45 + 10) {
////                    _log(" Now! " + lastScrollPos);
//                    startIndex = Math.max(display.getVisibleRange().getStart() - 15, 0);
//                    endIndex = Math.min(display.getVisibleRange().getStart() + 20, display.getRowCount());
//                    display.setVisibleRange( startIndex, endIndex);
//                    offsetStartPanel.setHeight(startIndex * 45 + "px");
//
//                    scrollable.setVerticalScrollPosition(lastScrollPos);
//
//                    _log(" startIndex " + startIndex);
//                    _log(" endIndex " + endIndex);
//                }
//
//
//                return;
//            }
//
//
//            int maxScrollTop = scrollable.getWidget().getOffsetHeight() - scrollable.getOffsetHeight();
//
//            _log(" maxScrollTop = " + maxScrollTop);
//            _log(" scrollable.getWidget().getOffsetHeight() = " + scrollable.getWidget().getOffsetHeight());
//            _log(" scrollable.getOffsetHeight() = " + scrollable.getOffsetHeight());
//            _log(" display.getRowCount() = " + display.getRowCount());
//            _log(" scrollable.getMaximumVerticalScrollPosition() = " + scrollable.getMaximumVerticalScrollPosition());
//            int frames = display.getRowCount()/DEFAULT_INCREMENT;
//            _log(" Frames = " + frames);
//
////            _log(" display.getVisibleRange().getStart() = " + display.getVisibleRange().getStart());
////            _log(" display.getVisibleRange().getLength() = " + display.getVisibleRange().getLength());
//            _log("========");
//            if (lastScrollPos >= scrollable.getMaximumVerticalScrollPosition()) {
//
//                if (endIndex < display.getRowCount()) {
//
//                }
//
//                // We are near the end, so increase the page size.
////                    int newPageSize = Math.min(
////                            display.getVisibleRange().getLength() + incrementSize,
////                            display.getRowCount());
////                    display.setVisibleRange(0, newPageSize);
//                for (int i = 0; i <DEFAULT_INCREMENT; i++) {
//                    dataProvider.getList().add(new WidgetTest.ContactInfo("Title #" + (WidgetTest.ContactInfo.nextId + 1), "Message #" + (WidgetTest.ContactInfo.nextId + 1)));
//                }
//
//                dataProvider.flush();
//
//
//                startIndex = Math.max(display.getRowCount() - 35, 0);
//                _log(" startIndex = " + startIndex);
//
//                if(selectionModel.getSelectedObject() != null) {
//                    selectedItem = selectionModel.getSelectedObject();
//                    selectionModel.clear();
//                }
//
//                display.setVisibleRange( startIndex, display.getRowCount());
//                offsetStartPanel.setHeight(startIndex * 45 + "px");
//
//                scrollable.setVerticalScrollPosition(lastScrollPos);
//
//            }
//        });

        // Handle scroll events.
        scrollable.addScrollHandler(event -> {
            int oldScrollPos = lastScrollPos;
            lastScrollPos = scrollable.getVerticalScrollPosition();

            HasRows display = getDisplay();
            if (display == null) {
                return;
            }

//            _log(" current = " + lastScrollPos + " oldScroll = " + oldScrollPos);
//            _log(" start = " + display.getVisibleRange().getStart() + " lastIndex = " + (display.getVisibleRange().getLength() - 1));

            if (oldScrollPos > lastScrollPos) {

                if (lastScrollPos <= display.getVisibleRange().getStart() * 45) {
                    _log("       > > Now: ");
                    _log(" startIndex = " + display.getVisibleRange().getStart());
                    _log(" endIndex = " + (getDisplay().getVisibleRange().getStart() + (getDisplay().getVisibleRange().getLength() - 1)) );



//                    startIndex = Math.max(display.getVisibleRange().getStart() - 15, 0);
//                    endIndex = Math.min(display.getVisibleRange().getStart() + 20, display.getRowCount());

                    int start = Math.max(display.getVisibleRange().getStart() - 15, 0);
                    int length = Math.min(25, display.getRowCount());

                    _log("       > > Start: " + start );
                    _log("       > > length: " + length );
                    _log("       > > Rows: " + (getDisplay().getRowCount()) );
//
                    display.setVisibleRange( start, length);
                    offsetStartPanel.setHeight(start * 45 + "px");
                    offsetEndPanel.setHeight((display.getRowCount() - (start + length))  * 45 + "px");

                    scrollable.setVerticalScrollPosition(lastScrollPos + 1);
//
//                    _log("       > > Start: " + getDisplay().getVisibleRange().getStart() );
//                    _log("       > > End: " + (getDisplay().getVisibleRange().getStart() + (getDisplay().getVisibleRange().getLength() - 1)) );
//                    _log("       > > Rows: " + (getDisplay().getRowCount()) );
//
//                    // Update start and end indexes

                }
                updateStartEndIndexes();
                return;

            }


//            int maxScrollTop = scrollable.getWidget().getOffsetHeight() - scrollable.getOffsetHeight();

//            _log(" maxScrollTop = " + maxScrollTop);
//            _log(" scrollable.getWidget().getOffsetHeight() = " + scrollable.getWidget().getOffsetHeight());
//            _log(" scrollable.getOffsetHeight() = " + scrollable.getOffsetHeight());
//            _log(" display.getRowCount() = " + display.getRowCount());
//            _log(" scrollable.getMaximumVerticalScrollPosition() = " + scrollable.getMaximumVerticalScrollPosition());
//            int frames = display.getRowCount()/DEFAULT_INCREMENT;
//            _log(" Frames = " + frames);

//            _log(" display.getVisibleRange().getStart() = " + display.getVisibleRange().getStart());
//            _log(" display.getVisibleRange().getLength() = " + display.getVisibleRange().getLength());
//            _log("========");

//            if (lastScrollPos >= (((endIndex) * 45) - scrollable.getOffsetHeight()) ){
//                _log(" ====== lalalalalala = ");
//                offsetEndPanel.setHeight(0 + "px");
//            }

            if (lastScrollPos >= (((endIndex) * 45) - scrollable.getOffsetHeight())) { //Needs Updating using the endIndex
//                _log(" ====== down = ");

                if (endIndex >= display.getRowCount() - 1) {
                    _log(" Requesting new data... ");
                    // Requires expanding the rows with new data if available
                    for (int i = 0; i < DEFAULT_INCREMENT; i++) {
                        dataProvider.getList().add(new WidgetTest.ContactInfo("Title #" + (WidgetTest.ContactInfo.nextId + 1), "Message #" + (WidgetTest.ContactInfo.nextId + 1)));
                    }
                    dataProvider.flush();

                    int start = Math.max(display.getRowCount() - 30, 0);
                    int length = Math.min(30, display.getRowCount());

                    display.setVisibleRange( start, length);
                    offsetStartPanel.setHeight(start  * 45 + "px");
                    offsetEndPanel.setHeight((display.getRowCount() - (start + length))  * 45 + "px");
                } else {

                    int start = endIndex - 10;
//                    _log("       > > left: " + (display.getRowCount() - endIndex) );

                    int length = Math.min(20, 10 + display.getRowCount() - endIndex);

                    offsetStartPanel.setHeight(start  * 45 + "px");
                    offsetEndPanel.setHeight((display.getRowCount() - (start + length))  * 45 + "px");
                    display.setVisibleRange( start, length);
                }


                //Simply move the window down

//                _log("       > > Start: " + getDisplay().getVisibleRange().getStart() );
//                _log("       > > End: " + (getDisplay().getVisibleRange().getStart() + (getDisplay().getVisibleRange().getLength() - 1)) );
//                _log("       > > Rows: " + (getDisplay().getRowCount()) );


                // We are near the end, so increase the page size.
//                    int newPageSize = Math.min(
//                            display.getVisibleRange().getLength() + incrementSize,
//                            display.getRowCount());
//                    display.setVisibleRange(0, newPageSize);

//                for (int i = 0; i <DEFAULT_INCREMENT; i++) {
//                    dataProvider.getList().add(new WidgetTest.ContactInfo("Title #" + (WidgetTest.ContactInfo.nextId + 1), "Message #" + (WidgetTest.ContactInfo.nextId + 1)));
//                }
//
//                dataProvider.flush();


//                startIndex = Math.max(display.getRowCount() - 35, 0);
//                _log(" startIndex = " + startIndex);

                if(selectionModel.getSelectedObject() != null) {
                    selectedItem = selectionModel.getSelectedObject();
                    selectionModel.clear();
                }

//                _log(" setting visible range " + Math.max(startIndex + 15, 0) + " - " + Math.min(startIndex + 30, display.getRowCount()));
//                display.setVisibleRange( Math.max(startIndex + 15, 0) , Math.min(startIndex + 30, display.getRowCount()));
//                offsetStartPanel.setHeight(Math.max(startIndex + 15, 0)  * 45 + "px");

                scrollable.setVerticalScrollPosition(lastScrollPos);

//                 Update start and end indexes
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
//        rootPanel.add((Widget) display);
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
//        _log(" >> RangeChanged total rows: " + getDisplay().getRowCount());
//        startIndex = getDisplay().getVisibleRange().getStart();
//        endIndex = getDisplay().getVisibleRange().getLength() - 1;
//
//        _log(" [startIndex= " + startIndex + " endIndex= " + endIndex + "]");
//        updateStartEndIndexes();
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
