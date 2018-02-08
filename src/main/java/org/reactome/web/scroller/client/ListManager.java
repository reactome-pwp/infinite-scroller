package org.reactome.web.scroller.client;

import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.ListDataProvider;

import java.util.List;

/**
 * @author Kostas Sidiropoulos <ksidiro@ebi.ac.uk>
 */
public class ListManager<T> {
    private int totalRows = 0;
    private int curStartIndex = 0;
    private int curEndIndex = 0;

    private InfiniteListDataProvider<T> dataProvider;

    private ListDataProvider<T> buffer = new ListDataProvider<>();

    public ListManager(InfiniteListDataProvider<T> dataProvider) {
        this.dataProvider = dataProvider;
    }

    public void setDataDisplay(final HasData<T> display) {
        buffer.addDataDisplay(display);
    }

    public void setDataProvider(InfiniteListDataProvider<T> dataProvider) {
        this.dataProvider = dataProvider;
    }

    public void loadNewData(int start, int length, int pageSize) {
        List<T> newItems = dataProvider.requestItems(start, length);
        totalRows += newItems.size();

        if (curEndIndex == 0) {
            curEndIndex = totalRows - 1;
            addItemsToEndOfBuffer(newItems);
        } else {
            int newStart = Math.max(totalRows - pageSize, 0);
            int newLength = Math.min(pageSize, totalRows);

            updateHeadAndTailOfBuffer(newStart, newLength, newItems);
        }

    }

    public void loadPreviousData(int pageSize) {
//        ShowMorePagerPanel._log(" << PreviousPage" );
        int start = Math.max(curStartIndex - (pageSize/2), 0); //15
        int length = Math.min(pageSize, totalRows);
        if(start < curStartIndex) {
            List<T> newItems = dataProvider.requestItems(start, length);
            updateEntireBuffer(start, length, newItems);
        }
    }

    public void loadNextData(int pageSize) {
//        ShowMorePagerPanel._log(" >> NextPage" );
        int start = curEndIndex - (pageSize/3); //10
        int length = Math.min(pageSize, (pageSize/3) + totalRows - curEndIndex);

        List<T> newItems = dataProvider.requestItems(start, length);
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
