package com.cs4521.bikeroute;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.AmazonClientException;
import com.amazonaws.http.HttpMethodName;
import com.amazonaws.mobile.AWSMobileClient;
import com.amazonaws.mobile.api.CloudLogicAPI;
import com.amazonaws.mobile.api.CloudLogicAPIConfiguration;
import com.amazonaws.mobile.api.CloudLogicAPIFactory;
import com.amazonaws.mobile.api.idm4p6j74mol.BikerMobileHubClient;
import com.amazonaws.mobileconnectors.apigateway.ApiRequest;
import com.amazonaws.mobileconnectors.apigateway.ApiResponse;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.models.nosql.BikerDO;
import com.amazonaws.util.StringUtils;

import java.util.HashMap;
import java.util.Map;


/**
 * Created by EVA Unit 02 on 11/13/2016.
 */
public class AccountTabFragment extends Fragment {

    static final String LOG_TAG = "AccountTabFragment";

    private AutoCompleteTextView loginTextView;
    private EditText passwordTextView;
    private AutoCompleteTextView firstNameTextView;
    private AutoCompleteTextView lastNameTextView;
    private Button registerBtn;
    private TextView loggedInTextView;

    private int statusCode;
    private String statusText;

    boolean loggedIn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.account_tab_fragment, container, false);

        statusCode = 0;
        statusText = "";
        loggedIn = true;

        loginTextView = (AutoCompleteTextView) view.findViewById(R.id.email);
        passwordTextView = (EditText) view.findViewById(R.id.password);
        firstNameTextView = (AutoCompleteTextView) view.findViewById(R.id.first_name);
        lastNameTextView = (AutoCompleteTextView) view.findViewById(R.id.last_name);
        registerBtn = (Button) view.findViewById(R.id.email_sign_in_button);

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                invokeAPI();
            }
        });


        loggedInTextView = (TextView) view.findViewById(R.id.logged_in);
        loggedInTextView.setVisibility(View.GONE);

        return view;
    }

    public void invokeAPI() {

        String email = loginTextView.getText().toString();
        String password = passwordTextView.getText().toString();
        String firstName = firstNameTextView.getText().toString();
        String lastName = lastNameTextView.getText().toString();

        if (!email.isEmpty() && !password.isEmpty() && !firstName.isEmpty() && !lastName.isEmpty()) {
            // Set your request method, path, query string parameters, and request body
            final String method = "POST";
            final String path = "/createUser";
            final String body = "{\n  \"userID\": \"" + email + "\",\n " +
                    " \"password\": \"" + password + "\",\n " +
                    " \"firstName\": \"" + firstName + "\",\n " +
                    " \"lastName\": \"" + lastName + "\"\n}";
            final Map<String, String> queryStringParameters = new HashMap<String, String>();
            final Map<String, String> headers = new HashMap<String, String>();

            // Create an instance of your custom SDK client
            final AWSMobileClient mobileClient = AWSMobileClient.defaultMobileClient();
            final CloudLogicAPI client = mobileClient.createAPIClient(BikerMobileHubClient.class);

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

            if (statusCode == 200) {
                Toast.makeText(getActivity(), "Account Successfully Created", Toast.LENGTH_SHORT).show();
                loggedIn = true;
                loginTextView.setVisibility(View.GONE);
                passwordTextView.setVisibility(View.GONE);
                firstNameTextView.setVisibility(View.GONE);
                lastNameTextView.setVisibility(View.GONE);
                registerBtn.setVisibility(View.GONE);
                loggedInTextView.setVisibility(View.VISIBLE);
                BikerFragment.userId = email;
            }
            else {
                Toast.makeText(getActivity(), "Error " + statusCode + ": " + statusText, Toast.LENGTH_SHORT).show();
            }

        } else {
            Toast.makeText(getActivity(), "Error: One of the fields are empty.", Toast.LENGTH_SHORT).show();
        }
    }

}
