package com.cs4521.bikeroute;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.AmazonClientException;
import com.amazonaws.http.HttpMethodName;
import com.amazonaws.mobile.AWSMobileClient;
import com.amazonaws.mobile.api.CloudLogicAPI;
import com.amazonaws.mobile.api.id20v8fpib92.RoutesMobileHubClient;
import com.amazonaws.mobileconnectors.apigateway.ApiRequest;
import com.amazonaws.mobileconnectors.apigateway.ApiResponse;
import com.amazonaws.util.StringUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Created by EVA Unit 02 on 11/13/2016.
 */
public class RoutesTabFragment extends Fragment {

    static final String LOG_TAG = "RoutesTabFragment";

    private SimpleAdapter adapter;

    Button mNewRouteBtn;
    ListView mListView;
    TextView mLoginTextView;

    private int statusCode;
    private String statusText;

    // Array of strings...
    List<Route> routes = new ArrayList<>();
    List<Map<String, String>> data;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.routes_tab_fragment, container, false);

        invokeGetRoutesAPI();

        if (!routes.isEmpty()) {
            updateData();
            adapter = new SimpleAdapter(getActivity(), data,
                    R.layout.route_listview,
                    new String[]{"title", "info"},
                    new int[]{R.id.list_title_text,
                            R.id.list_subtitle_text});

            mListView = (ListView) view.findViewById(R.id.route_list);
            mListView.setAdapter(adapter);

            ListAdapter listAdapter = mListView.getAdapter();
            int totalHeight = 0;
            for (int i = 0; i < listAdapter.getCount(); i++) {
                View listItem = listAdapter.getView(i, null, mListView);
                listItem.measure(0, 0);
                totalHeight += listItem.getMeasuredHeight();
            }

            ViewGroup.LayoutParams params = mListView.getLayoutParams();
            params.height = totalHeight + (mListView.getDividerHeight() * (listAdapter.getCount() - 1));
            mListView.setLayoutParams(params);
            mListView.requestLayout();

            mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {

                    Route route = routes.get(position);
                    invokeUseRouteAPI(route);
                    route.incrementNumberTraveled();
                    routes.set(position, route);
                    updateData();

                    adapter.notifyDataSetChanged();

                    String toastMessage = "The following Route will begin and this will be time # " + route.getNumber_traveled()
                    + " you are traveling this route \n\n" +
                            route.getRouteName() + "\t Start: " + route.getStart_latitude() + " " + route.getStart_longitude() +
                            "\t End: " + route.getEnd_latitude() + " " + route.getEnd_longitude();
                    Toast.makeText(getActivity(),toastMessage,Toast.LENGTH_LONG).show();
                }
            });
        }

        mNewRouteBtn = (Button) view.findViewById(R.id.new_route_button);
        mNewRouteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getFragmentManager();
                NewRouteFragment newRouteFragment = new NewRouteFragment();
                newRouteFragment.show(fm, "Sample Fragment");
            }
        });

        return view;
    }

    private void invokeGetRoutesAPI() {

        if (!BikerFragment.userId.isEmpty()) {
            // Set your request method, path, query string parameters, and request body
            final String method = "POST";
            final String path = "/getRoutes";
            final String body = "{\n  \"userId\": \"" + BikerFragment.userId + "\" \n }";
            final Map<String, String> queryStringParameters = new HashMap<String, String>();
            final Map<String, String> headers = new HashMap<String, String>();

            // Create an instance of your custom SDK client
            final AWSMobileClient mobileClient = AWSMobileClient.defaultMobileClient();
            final CloudLogicAPI client = mobileClient.createAPIClient(RoutesMobileHubClient.class);

            final byte[] content = body.getBytes(StringUtils.UTF8);

            // Construct the request
            final ApiRequest request =
                    new ApiRequest(client.getClass().getSimpleName())
                            .withPath(path)
                            .withHttpMethod(HttpMethodName.valueOf(method))
                            .withHeaders(headers)
                            .addHeader("Content-Type", "application/json")
                            .addHeader("Content-Length", String.valueOf(content.length))
                            .withParameters(queryStringParameters)
                            .withBody(content);


            // Make network call on background thread
            new Thread(new Runnable() {

                @Override
                public void run() {
                    try {

                        // Invoke the API
                        final ApiResponse response = client.execute(request);

                        statusCode = response.getStatusCode();
                        statusText = response.getStatusText();


                        BufferedReader streamReader = new BufferedReader(new InputStreamReader(response.getContent(), "UTF-8"));
                        StringBuilder responseStrBuilder = new StringBuilder();

                        String inputStr;
                        while ((inputStr = streamReader.readLine()) != null)
                            responseStrBuilder.append(inputStr);

                        fillRoutesMap(new JSONObject(responseStrBuilder.toString()).getJSONObject("listOfTop10").getJSONArray("Items"));

                        Log.d(LOG_TAG, "Response Status: " + statusCode);
                        // TODO: Add your custom handling for server response status codes (e.g., 403 Forbidden)

                    } catch (final AmazonClientException exception) {
                        Log.e(LOG_TAG, exception.getMessage(), exception);

                        // TODO: Put your exception handling code here (e.g., network error)
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }).start();


            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void fillRoutesMap(JSONArray array) throws JSONException {
        for (int i = 0; i < 10; i++)
        {
            JSONObject routeInfo = array.getJSONObject(i);
            String end_longitude = routeInfo.getString("end_longitude");
            String start_latitude = routeInfo.getString("start_latitude");
            String routeName = routeInfo.getString("routeName");
            String number_traveled = routeInfo.getString("number_traveled");
            String start_longitude = routeInfo.getString("start_longitude");
            String end_latitude = routeInfo.getString("end_latitude");
            String routeId = routeInfo.getString("routeId");

            Route newRoute = new Route(start_longitude, end_longitude, start_latitude, end_latitude, routeName, number_traveled, routeId);

            routes.add(newRoute);
        }
    }


    public void invokeUseRouteAPI(Route route) {

        // Set your request method, path, query string parameters, and request body
        final String method = "POST";
        final String path = "/useRoute";
        final String body = "{\"userId\": \""+ BikerFragment.userId +"\",\"routeId\": \"" + route.getRouteId() +"\"}";
        final Map<String, String> queryStringParameters = new HashMap<String, String>();
        final Map<String, String> headers = new HashMap<String, String>();

        // Create an instance of your custom SDK client
        final AWSMobileClient mobileClient = AWSMobileClient.defaultMobileClient();
        final CloudLogicAPI client = mobileClient.createAPIClient(RoutesMobileHubClient.class);

        final byte[] content = body.getBytes(StringUtils.UTF8);

        // Construct the request
        final ApiRequest request =
                new ApiRequest(client.getClass().getSimpleName())
                        .withPath(path)
                        .withHttpMethod(HttpMethodName.valueOf(method))
                        .withHeaders(headers)
                        .addHeader("Content-Type", "application/json")
                        .addHeader("Content-Length", String.valueOf(content.length))
                        .withParameters(queryStringParameters)
                        .withBody(content);

        // Make network call on background thread
        new Thread(new Runnable() {

            @Override
            public void run() {
                try {

                    // Invoke the API
                    final ApiResponse response = client.execute(request);

                    statusCode = response.getStatusCode();
                    statusText = response.getStatusText();

                    Log.d(LOG_TAG, "Response Status: " + statusCode);

                    // TODO: Add your custom handling for server response status codes (e.g., 403 Forbidden)

                } catch (final AmazonClientException exception) {
                    Log.e(LOG_TAG, exception.getMessage(), exception);

                    // TODO: Put your exception handling code here (e.g., network error)
                }
            }
        }).start();

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    private void updateData() {
        data = new ArrayList<Map<String, String>>();
        for (Route entry : routes) {
            Map<String, String> datum = new HashMap<String, String>(2);
            datum.put("title", entry.getRouteName());
            datum.put("info", entry.toString());
            data.add(datum);
        }
    }
}
