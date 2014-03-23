/**
 * Copyright 2014, barter.li
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package li.barter.fragments;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.widget.Toast;

import java.util.concurrent.atomic.AtomicInteger;

import li.barter.R;
import li.barter.activities.AbstractBarterLiActivity;
import li.barter.http.IVolleyHelper;

/**
 * Base fragment class to encapsulate common functionality
 * 
 * @author vinaysshenoy
 */
public abstract class AbstractBarterLiFragment extends Fragment {

    private static final String TAG = "BaseBarterLiFragment";

    /**
     * Flag that indicates that this fragment is attached to an Activity
     */
    private boolean             mIsAttached;

    private RequestQueue        mRequestQueue;
    private ImageLoader         mImageLoader;
    private AtomicInteger       mRequestCounter;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mIsAttached = true;
        mRequestQueue = ((IVolleyHelper) activity.getApplication())
                        .getRequestQueue();
        mImageLoader = ((IVolleyHelper) activity.getApplication())
                        .getImageLoader();
        mRequestCounter = new AtomicInteger(0);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mIsAttached = false;
        mRequestQueue = null;
        mImageLoader = null;
        mRequestCounter = null;
    }

    /**
     * Is the device connected to a network or not.
     * 
     * @return <code>true</code> if connected, <code>false</code> otherwise
     */
    public boolean isConnectedToInternet() {
        return ((AbstractBarterLiActivity) getActivity()).isConnectedToInternet();
    }

    public void setActionBarDisplayOptions(final int displayOptions) {
        if (mIsAttached) {

            ((AbstractBarterLiActivity) getActivity())
                            .setActionBarDisplayOptions(displayOptions);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        mRequestQueue.cancelAll(getVolleyTag());
        getActivity().setProgressBarIndeterminateVisibility(false);
    }

    /**
     * Reference to the {@link ImageLoader}
     * 
     * @return The {@link ImageLoader} for loading images from ntwork
     */
    protected ImageLoader getImageLoader() {
        return mImageLoader;
    }

    /**
     * Add a request on the network queue
     * 
     * @param request The {@link Request} to add
     * @param showErrorOnNoNetwork Whether an error toast should be displayed on
     *            no internet connection
     * @param errorMsgResId String resource Id for error message to show if no
     *            internet connection, 0 for a default error message
     */
    protected void addRequestToQueue(final Request<?> request,
                    final boolean showErrorOnNoNetwork, final int errorMsgResId) {

        if (mIsAttached) {
            request.setTag(getVolleyTag());
            if (isConnectedToInternet()) {
                mRequestCounter.incrementAndGet();
                getActivity().setProgressBarIndeterminateVisibility(true);
                mRequestQueue.add(request);
            } else if (showErrorOnNoNetwork) {
                showToast(errorMsgResId != 0 ? errorMsgResId
                                : R.string.no_network_connection, false);
            }
        }
    }

    /**
     * A Tag to add to all Volley requests. This must be unique for all Fragments types
     * @return An Object that's the tag for this fragment
     */
    protected abstract Object getVolleyTag();

    /**
     * Call this whenever a request has finished, whether successfully or error
     */
    protected void onRequestFinished() {

        if (mRequestCounter.decrementAndGet() == 0) {
            getActivity().setProgressBarIndeterminateVisibility(false);
        }
    }

    /**
     * Display a {@link Toast} message
     * 
     * @param toastMessage The message to display
     * @param isLong Whether it is a long toast
     */
    public void showToast(final String toastMessage, final boolean isLong) {
        if (mIsAttached) {
            ((AbstractBarterLiActivity) getActivity()).showToast(toastMessage,
                            isLong);
        }
    }

    /**
     * Display a {@link Toast} message
     * 
     * @param toastMessageResId The message string resource Id to display
     * @param isLong Whether it is a long toast
     */
    public void showToast(final int toastMessageResId, final boolean isLong) {
        if (mIsAttached) {
            ((AbstractBarterLiActivity) getActivity()).showToast(toastMessageResId,
                            isLong);
        }
    }
    
}
