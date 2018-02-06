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
import com.google.gwt.user.cellview.client.CellList;
import com.google.gwt.user.client.ui.*;
import com.google.gwt.view.client.HasRows;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.SingleSelectionModel;



/**
 * A scrolling pager that automatically increases the range every time the
 * scroll bar reaches the bottom.
 */
public class ShowMorePagerPanel extends Composite { //extends AbstractPager {

    /**
     * The default increment size.
     */
    private static final int DEFAULT_INCREMENT = 20;
    private static final int DEFAULT_VISIBLE_ITEMS = 30;
    private static final int DEFAULT_ROW_HEIGHT = 45;

    private static final boolean isFirefox = isFirefox();

    private HasRows display;

    /**
     * The increment size.
     */
    private int incrementSize = DEFAULT_INCREMENT;

    /**
     * The last scroll position.
     */
    private int lastScrollPos = 0;

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
        offsetStartPanel.getElement().getStyle().setBackgroundColor("white");
        offsetStartPanel.getElement().getStyle().setOpacity(0.01);
        offsetStartPanel.setHeight("0px");

        offsetEndPanel = new SimplePanel();
        offsetEndPanel.getElement().getStyle().setBackgroundColor("white");
        offsetEndPanel.getElement().getStyle().setOpacity(0.01);
        offsetEndPanel.setHeight("0px");

        rootPanel = new FlowPanel();
        rootPanel.add(offsetStartPanel);
        rootPanel.add(offsetEndPanel);

        // Do not let the scrollable take tab focus.
        scrollable.getElement().setTabIndex(-1);

        // Handle scroll events.
        scrollable.addScrollHandler(event -> {
            lastScrollPos = scrollable.getVerticalScrollPosition();

            if (display == null) {
                return;
            }

            int curStartIndex = display.getVisibleRange().getStart();
            int curEndIndex = curStartIndex + display.getVisibleRange().getLength() - 1;
//            _log(" Rows: " + getDisplay().getRowCount() + " [startIndex= " + curStartIndex + " endIndex= " + curEndIndex + "] visible: " + ((curEndIndex + 1) - curStartIndex));
            int start, length;

            if (lastScrollPos <= curStartIndex * DEFAULT_ROW_HEIGHT) {

                start = Math.max(curStartIndex - 15, 0);
                length = Math.min(DEFAULT_VISIBLE_ITEMS, display.getRowCount());

                display.setVisibleRange( start, length);
                offsetStartPanel.setHeight(start * DEFAULT_ROW_HEIGHT + "px");
                offsetEndPanel.setHeight((display.getRowCount() - (start + length))  * DEFAULT_ROW_HEIGHT + "px");

                if (isFirefox) { scrollable.setVerticalScrollPosition(lastScrollPos);}
                else { scrollable.setVerticalScrollPosition(curStartIndex * DEFAULT_ROW_HEIGHT); }

            } else if (lastScrollPos >= (((curEndIndex) * DEFAULT_ROW_HEIGHT) - scrollable.getOffsetHeight())) {

                if (curEndIndex >= display.getRowCount() - 1) {
                    // Requires expanding the rows with new data if available
                    for (int i = 0; i < DEFAULT_INCREMENT; i++) {
                        dataProvider.getList().add(new ContactInfo("Title #" + (ContactInfo.nextId + 1), "Message #" + (ContactInfo.nextId + 1)));
                    }
                    dataProvider.flush();
                    start = Math.max(display.getRowCount() - 30, 0);
                    length = Math.min(DEFAULT_VISIBLE_ITEMS, display.getRowCount());
                } else {
                    start = curEndIndex - 10;
                    length = Math.min(DEFAULT_VISIBLE_ITEMS, 10 + display.getRowCount() - curEndIndex);
                }

                display.setVisibleRange( start, length);
                offsetStartPanel.setHeight(start  * DEFAULT_ROW_HEIGHT + "px");
                offsetEndPanel.setHeight((display.getRowCount() - (start + length))  * DEFAULT_ROW_HEIGHT + "px");

                if (isFirefox) { scrollable.setVerticalScrollPosition(lastScrollPos);}
                else { scrollable.setVerticalScrollPosition((((curEndIndex) * DEFAULT_ROW_HEIGHT) - scrollable.getOffsetHeight())); }
            }

            if (scrollable.getVerticalScrollPosition() < offsetStartPanel.getElement().getOffsetHeight()) {
                offsetStartPanel.setHeight(scrollable.getVerticalScrollPosition() + "px");
            }
        });
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

//    @Override
    public void setDisplay(HasRows display) {
        assert display instanceof Widget : "display must extend Widget";
        this.display = display;
        rootPanel.insert((Widget) display, 1);
        scrollable.setWidget(rootPanel);
        ((CellList)display).setSelectionModel(selectionModel);
        selectionModel.clear();

    }

    public void setDataProvider(ListDataProvider dataProvider) {
        this.dataProvider = dataProvider;
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

    private static native void _log(String message)/*-{
        if($wnd.console){
            $wnd.console.log(message);
        }
    }-*/;

    private static native boolean isFirefox()/*-{
        // Firefox 1.0+
        return typeof InstallTrigger !== 'undefined';
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
