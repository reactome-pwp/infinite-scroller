package org.reactome.web.scroller.client;

import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.HasRows;
import com.google.gwt.view.client.ListDataProvider;

import java.util.List;

/**
 * @author Kostas Sidiropoulos <ksidiro@ebi.ac.uk>
 */
public class ListItemsManager<T> {
    private int totalRows = 0;
    private int curStartIndex = 0;
    private int curEndIndex = 0;
    private HasRows display;

    private ListDataProvider<T> buffer = new ListDataProvider<>();

    public ListItemsManager() {
    }

    public void setDataDisplay(final HasData<T> display) {
        buffer.addDataDisplay(display);
        this.display = display;
    }

    public void loadNewData(int s, int l) {
        List<T> newItems = (List<T>) ShowMorePagerPanel.requestContacts(s, l);
        totalRows += newItems.size();

        if (curEndIndex == 0) {
            curEndIndex = totalRows - 1;
            addItemsToEndOfBuffer(newItems);
        } else {
            int start = Math.max(totalRows - ShowMorePagerPanel.DEFAULT_VISIBLE_ITEMS, 0);
            int length = Math.min(ShowMorePagerPanel.DEFAULT_VISIBLE_ITEMS, totalRows);

            updateHeadAndTailOfBuffer(start, length, newItems);
        }

    }

    public void loadPreviousData() {
//        ShowMorePagerPanel._log(" << PreviousPage" );
        int start = Math.max(curStartIndex - 15, 0);
        int length = Math.min(ShowMorePagerPanel.DEFAULT_VISIBLE_ITEMS, totalRows);
        if(start < curStartIndex) {
            List<T> newItems = (List<T>) ShowMorePagerPanel.requestContacts(start, length);
            updateEntireBuffer(start, length, newItems);
        }
    }

    public void loadNextData() {
//        ShowMorePagerPanel._log(" >> NextPage" );
        int start = curEndIndex - 10;
        int length = Math.min(ShowMorePagerPanel.DEFAULT_VISIBLE_ITEMS, 10 + totalRows - curEndIndex);

        List<T> newItems = (List<T>) ShowMorePagerPanel.requestContacts(start, length);
        updateEntireBuffer(start, length, newItems);
    }


    public int getTotalRows() {
        return totalRows;
    }

    public int getCurrentRows() {
        return buffer.getList().size();
    }

    public int getCurStartIndex() {
        return curStartIndex;
    }

    public int getCurEndIndex() {
        return curEndIndex;
    }

    public ListDataProvider<T> getBuffer() {
        return buffer;
    }

    private void addItemsToEndOfBuffer(List<T> newItems){
        buffer.getList().addAll(newItems);
    }

    private void removeItemsFromStartOfBuffer(int numberOfItemsToRemove){
        for (int i = 0; i < numberOfItemsToRemove; i++) {
            buffer.getList().remove(0);
        }
    }

    private void updateHeadAndTailOfBuffer(int start, int length, List<T> newItems) {
        int shift = start - curStartIndex;

        removeItemsFromStartOfBuffer(shift);
        addItemsToEndOfBuffer(newItems);
        buffer.flush();

        curStartIndex += shift;
        curEndIndex = curStartIndex + length - 1;
    }

    private void updateEntireBuffer(int start, int length, List<T> newItems) {
        int shift = start - curStartIndex;

        buffer.getList().clear();
        buffer.getList().addAll(newItems);
        buffer.flush();

        curStartIndex += shift;
        curEndIndex = curStartIndex + length - 1;
    }

    @Override
    public String toString() {
        return " Rows: " + totalRows
                + " buffer size: " + buffer.getList().size()
                + " Start: " + curStartIndex
                + " End: " + curEndIndex;
    }
}
