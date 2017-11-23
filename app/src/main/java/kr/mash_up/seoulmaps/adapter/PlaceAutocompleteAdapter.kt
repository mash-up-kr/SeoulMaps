package kr.mash_up.seoulmaps.adapter

import android.content.Context
import android.graphics.Typeface
import android.support.v7.widget.RecyclerView
import android.text.style.StyleSpan
import android.util.Log
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.Toast
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.data.DataBufferUtils
import com.google.android.gms.location.places.AutocompleteFilter
import com.google.android.gms.location.places.AutocompletePrediction
import com.google.android.gms.location.places.Places
import com.google.android.gms.maps.model.LatLngBounds
import kr.mash_up.seoulmaps.adapter.contract.PlaceAdapterContract
import kr.mash_up.seoulmaps.adapter.holder.PlaceViewHolder
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * Created by Tak on 2017. 8. 21..
 */


/**
 * Initializes with a resource for text rows and autocomplete query bounds.
 * Handles autocomplete requests.
 * The bounds used for Places Geo Data autocomplete API requests.
 * The autocomplete filter used to restrict queries to a specific set of place types.
 * @see android.widget.ArrayAdapter.ArrayAdapter
 */
class PlaceAutocompleteAdapter(private val mContext: Context, private val mGoogleApiClient: GoogleApiClient, private var mBounds: LatLngBounds?, private val mPlaceFilter: AutocompleteFilter?):
        RecyclerView.Adapter<PlaceViewHolder>(), Filterable,
        PlaceAdapterContract.View,
        PlaceAdapterContract.Model {

    private var mResultList: ArrayList<AutocompletePrediction>? = null

    interface OnPlaceItemClickListener {
        fun onItemClick(placeItem: AutocompletePrediction?)
    }
    override var onPlaceItemClickListener: OnPlaceItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaceViewHolder
            = PlaceViewHolder(mContext, parent, onPlaceItemClickListener)

    override fun getItem(position: Int): AutocompletePrediction? = mResultList?.get(position)

    override fun onBindViewHolder(holder: PlaceViewHolder?, position: Int) {
        holder?.bindView(getItem(position), position)
    }

    override fun getItemCount(): Int = if(mResultList != null) (mResultList as ArrayList<AutocompletePrediction>).size else 0

    /**
     * Sets the bounds for all subsequent queries.
     */
    fun setBounds(bounds: LatLngBounds) {
        mBounds = bounds
    }


    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence): Filter.FilterResults {
                val results = Filter.FilterResults()
                // We need a separate list to store the results, since
                // this is run asynchronously.
                var filterData: ArrayList<AutocompletePrediction> = ArrayList()

                // Skip the autocomplete query if no constraints are given.
                constraint.let {
                    filterData = getAutocomplete(it) as ArrayList<AutocompletePrediction>
                }
//                if (constraint != null) {
//                    // Query the autocomplete API for the (constraint) search string.
//                    filterData = getAutocomplete(constraint)
//                }
                results.values = filterData
                results.count = filterData.size

//                if (filterData != null) {
//                    results.count = filterData.size
//                } else {
//                    results.count = 0
//                }

                return results
            }

            override fun publishResults(constraint: CharSequence, results: Filter.FilterResults?) {

                if (results != null && results.count > 0) {
                    // The API returned at least one result, update the data.
                    mResultList = results.values as ArrayList<AutocompletePrediction>
                    notifyDataSetChanged()
                } else {
                    // The API did not return any results, invalidate the data set.
                    //                    notifyDataSetInvalidated();
                    Log.d(TAG, "need notifyDataSetInvalidated")
                }
            }

            override fun convertResultToString(resultValue: Any): CharSequence {
                // Override this method to display a readable result in the AutocompleteTextView
                // when clicked.
                if (resultValue is AutocompletePrediction) {
                    return resultValue.getFullText(null)
                } else {
                    return super.convertResultToString(resultValue)
                }
            }
        }
    }

    /**
     * Submits an autocomplete query to the Places Geo Data Autocomplete API.
     * Results are returned as frozen AutocompletePrediction objects, ready to be cached.
     * objects to store the Place ID and description that the API returns.
     * Returns an empty list if no results were found.
     * Returns null if the API client is not available or the query did not complete
     * successfully.
     * This method MUST be called off the main UI thread, as it will block until data is returned
     * from the API, which may include a network request.

     * @param constraint Autocomplete query string
     * *
     * @return Results from the autocomplete API or null if the query was not successful.
     * *
     * @see Places.GEO_DATA_API.getAutocomplete
     * @see AutocompletePrediction.freeze
     */
    private fun getAutocomplete(constraint: CharSequence): ArrayList<AutocompletePrediction>? {
        if (mGoogleApiClient.isConnected) {
            Log.i(TAG, "Starting autocomplete query for: " + constraint)

            // Submit the query to the autocomplete API and retrieve a PendingResult that will
            // contain the results when the query completes.
            val results = Places.GeoDataApi
                    .getAutocompletePredictions(mGoogleApiClient, constraint.toString(), mBounds, mPlaceFilter)

            // This method should have been called off the main UI thread. Block and wait for at most 60s
            // for a result from the API.
            val autocompletePredictions = results.await(60, TimeUnit.SECONDS)

            // Confirm that the query completed successfully, otherwise return null
            val status = autocompletePredictions.status
            if (!status.isSuccess) {
                Toast.makeText(mContext, "Error contacting API: " + status.toString(), Toast.LENGTH_SHORT).show()
                Log.d(TAG, "Error getting autocomplete prediction API call: " + status.toString())
                autocompletePredictions.release()
                return null
            }

            Log.i(TAG, "Query completed. Received " + autocompletePredictions.count
                    + " predictions.")

            // Freeze the results immutable representation that can be stored safely.
            return DataBufferUtils.freezeAndClose(autocompletePredictions)
        }
        Log.e(TAG, "Google API client is not connected for autocomplete query.")
        return null
    }

    companion object {
        private val TAG = PlaceAutocompleteAdapter::class.java.simpleName
        private val STYLE_BOLD = StyleSpan(Typeface.BOLD)
    }

}
