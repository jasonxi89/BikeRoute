package com.cs4521.bikeroute;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.AmazonClientException;
import com.amazonaws.http.HttpMethodName;
import com.amazonaws.mobile.AWSMobileClient;
import com.amazonaws.mobile.api.CloudLogicAPI;
import com.amazonaws.mobile.api.id20v8fpib92.RoutesMobileHubClient;
import com.amazonaws.mobile.api.idm4p6j74mol.BikerMobileHubClient;
import com.amazonaws.mobileconnectors.apigateway.ApiRequest;
import com.amazonaws.mobileconnectors.apigateway.ApiResponse;
import com.amazonaws.util.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by EVA Unit 02 on 11/13/2016.
 */
public class NewRouteFragment extends DialogFragment {

    static final String LOG_TAG = "NewRouteFragment";

    Button mCancelButton;
    Button mSubmitButton;
    TextView mRouteNameTextView;
    TextView mStartLatTextView;
    TextView mEndLatTextView;
    TextView mStartLongTextView;
    TextView mEndLongTextView;

    private int statusCode;
    private String statusText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.new_route_fragment, container, false);
        getDialog().setTitle("New Route");

        mRouteNameTextView = (TextView) view.findViewById(R.id.route_name);
        mStartLatTextView = (TextView) view.findViewById(R.id.start_latitude);
        mEndLatTextView = (TextView) view.findViewById(R.id.end_latitude);
        mStartLongTextView = (TextView) view.findViewById(R.id.start_longitude);
        mEndLongTextView = (TextView) view.findViewById(R.id.end_longitude);

        mCancelButton = (Button) view.findViewById(R.id.cancel);
        mCancelButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        mSubmitButton = (Button) view.findViewById(R.id.submit);
        mSubmitButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                invokeAPI();
                dismiss();
            }
        });

        return view;
    }

    public void invokeAPI() {

        String route_name = mRouteNameTextView.getText().toString();
        String start_lat = mStartLatTextView.getText().toString();
        String end_lat = mEndLatTextView.getText().toString();
        String start_long = mStartLongTextView.getText().toString();
        String end_long = mEndLongTextView.getText().toString();

        if (!route_name.isEmpty() && !start_lat.isEmpty() && !end_lat.isEmpty() && !start_long.isEmpty() && !end_long.isEmpty()) {
            // Set your request method, path, query string parameters, and request body
            final String method = "POST";
            final String path = "/storeRoutes";
            final String body = "{\n  \"userID\": \"" + BikerFragment.userId + "\",\n " +
                    " \"routeID\": \"" + UUID.randomUUID() + "\",\n " +
                    " \"start_latitude\": \"" + start_lat + "\",\n " +
                    " \"end_latitude\": \"" + end_lat + "\",\n " +
                    " \"start_longitude\": \"" + start_long + "\",\n " +
                    " \"end_longitude\": \"" + end_long + "\",\n " +
                    " \"routeName\": \"" + route_name + "\"\n }";
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
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (statusCode == 200) {
                Toast.makeText(getActivity(), "New Route Successfully Created", Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(getActivity(), "Error " + statusCode + ": " + statusText, Toast.LENGTH_SHORT).show();
            }

        } else {
            Toast.makeText(getActivity(), "Error: One of the fields are empty.", Toast.LENGTH_SHORT).show();
        }
    }
}
