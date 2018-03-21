package org.reactome.web.scroller.client.provider;

import com.google.gwt.http.client.*;
import org.reactome.web.scroller.client.manager.AsyncListManager;

import java.util.List;

import static org.reactome.web.scroller.client.util.Placeholder.ROWS;
import static org.reactome.web.scroller.client.util.Placeholder.START;

/**
 * @author Kostas Sidiropoulos <ksidiro@ebi.ac.uk>
 */
public abstract class AbstractListAsyncDataProvider<T> implements InfiniteListAsyncDataProvider<T> {

    protected String URL;
    protected Request request;
    private AsyncListManager<T> handler;

    @Override
    public void setHandler(AsyncListManager<T> handler) {
        this.handler = handler;
    }

    @Override
    public void setURL(String url) {
        URL = url;
    }

    @Override
    public final void requestNewItems(int start, int length) {
        try {
            requestItems(start, length, new RequestCallback() {
                @Override
                public void onResponseReceived(Request request, Response response) {
                    switch (response.getStatusCode()) {
                        case Response.SC_OK:
                            handler.onNewDataArrived(processResult(response.getText()), start, length);
                            break;
                        default:
                            handler.onErrorRetrievingData(response.getStatusText());
                    }
                }

                @Override
                public void onError(Request request, Throwable exception) {
                    handler.onErrorRetrievingData(exception.getMessage());
                }
            });
        } catch (RequestException e) {
            handler.onErrorRetrievingData(e.getMessage());
        }
    }

    @Override
    public final void requestNextItems(int start, int length) {
        try {
            requestItems(start, length, new RequestCallback() {
                @Override
                public void onResponseReceived(Request request, Response response) {
                    switch (response.getStatusCode()) {
                        case Response.SC_OK:
                            handler.onNextDataArrived(processResult(response.getText()), start, length);
                            break;
                        default:
                            handler.onErrorRetrievingData(response.getStatusText());
                    }
                }

                @Override
                public void onError(Request request, Throwable exception) {
                    handler.onErrorRetrievingData(exception.getMessage());
                }
            });
        } catch (RequestException e) {
            handler.onErrorRetrievingData(e.getMessage());
        }
    }

    @Override
    public final void requestPreviousItems(int start, int length) {
        try {
            requestItems(start, length, new RequestCallback() {
                @Override
                public void onResponseReceived(Request request, Response response) {
                    switch (response.getStatusCode()){
                        case Response.SC_OK:
                            handler.onPreviousDataArrived(processResult(response.getText()), start, length);
                            break;
                        default:
                            handler.onErrorRetrievingData(response.getStatusText());
                    }
                }
                @Override
                public void onError(Request request, Throwable exception) {
                    handler.onErrorRetrievingData(exception.getMessage());
                }
            });
        } catch (RequestException e) {
            handler.onErrorRetrievingData(e.getMessage());
        }
    }

    protected void requestItems(int start, int length, RequestCallback callback) throws RequestException {

        String url = URL
                .replace(START.getUrlValue(), "start=" + start)
                .replace(ROWS.getUrlValue(), "rows=" + length);

        if (request != null && request.isPending()) return;

        RequestBuilder requestBuilder = new RequestBuilder(RequestBuilder.GET, url);
        requestBuilder.setHeader("Accept", "application/json");

        request = requestBuilder.sendRequest(null, callback);
    }

    protected abstract List<T> processResult(String body);
}
