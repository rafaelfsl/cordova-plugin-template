/**
 */
package com.example;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.PluginResult;
import org.apache.cordova.PluginResult.Status;
import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONException;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.api.Scope;

import org.apache.cordova.*;
import org.apache.cordova.engine.SystemWebChromeClient;

import android.util.Log;

import java.util.Date;

public class MyCordovaPlugin extends CordovaPlugin {
  private static final String TAG = "MyCordovaPlugin";

  // Wraps our service connection to Google Play services and provides access to the users sign in state and Google APIs
  private GoogleApiClient mGoogleApiClient;
  private CallbackContext savedCallbackContext;

  public void initialize(CordovaInterface cordova, CordovaWebView webView) {
    super.initialize(cordova, webView);

    Log.d(TAG, "Initializing MyCordovaPlugin");
  }

  public boolean execute(String action, JSONArray args, final CallbackContext callbackContext) throws JSONException {
    if(action.equals("echo")) {
      String phrase = args.getString(0);
      // Echo back the first argument
      Log.d(TAG, phrase);
    } else if(action.equals("getDate")) {
      // An example of returning data back to the web layer
      final PluginResult result = new PluginResult(PluginResult.Status.OK, (new Date()).toString());
      callbackContext.sendPluginResult(result);
    } else if(action.equals("googleSign")) {
      Log.d(TAG, "Trying to build google client api");

      //pass args into api client build
      buildGoogleApiClient(args.optJSONObject(0));

      // Tries to Log the user in
      Log.d(TAG, "Trying to Log in!");
      // cordova.setActivityResultCallback(this); //sets this class instance to be an activity result listener
      // signIn();
    }
    return true;
  }

  /**
     * Set options for login and Build the GoogleApiClient if it has not already been built.
     * @param clientOptions - the options object passed in the login function
     */
    private synchronized void buildGoogleApiClient(JSONObject clientOptions) throws JSONException {
      if (clientOptions == null) {
          return;
      }

      //If options have been passed in, they could be different, so force a rebuild of the client
      // disconnect old client iff it exists
      if (this.mGoogleApiClient != null) this.mGoogleApiClient.disconnect();
      // nullify
      this.mGoogleApiClient = null;

      Log.i(TAG, "Building Google options");

      // Make our SignIn Options builder.
      GoogleSignInOptions.Builder gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN);

      // request the default scopes
      gso.requestEmail().requestProfile();

      // We're building the scopes on the Options object instead of the API Client
      // b/c of what was said under the "addScope" method here:
      // https://developers.google.com/android/reference/com/google/android/gms/common/api/GoogleApiClient.Builder.html#public-methods
      String scopes = clientOptions.optString(ARGUMENT_SCOPES, null);

      if (scopes != null && !scopes.isEmpty()) {
          // We have a string of scopes passed in. Split by space and request
          for (String scope : scopes.split(" ")) {
              gso.requestScopes(new Scope(scope));
          }
      }

      // Try to get web client id
      String webClientId = clientOptions.optString(ARGUMENT_WEB_CLIENT_ID, null);

      // if webClientId included, we'll request an idToken
      if (webClientId != null && !webClientId.isEmpty()) {
          gso.requestIdToken(webClientId);

          // if webClientId is included AND offline is true, we'll request the serverAuthCode
          if (clientOptions.optBoolean(ARGUMENT_OFFLINE_KEY, false)) {
              gso.requestServerAuthCode(webClientId, true);
          }
      }

      // Try to get hosted domain
      String hostedDomain = clientOptions.optString(ARGUMENT_HOSTED_DOMAIN, null);

      // if hostedDomain included, we'll request a hosted domain account
      if (hostedDomain != null && !hostedDomain.isEmpty()) {
          gso.setHostedDomain(hostedDomain);
      }

      //Now that we have our options, let's build our Client
      Log.i(TAG, "Building GoogleApiClient");

      GoogleApiClient.Builder builder = new GoogleApiClient.Builder(webView.getContext())
          .addOnConnectionFailedListener(this)
          .addApi(Auth.GOOGLE_SIGN_IN_API, gso.build());

      this.mGoogleApiClient = builder.build();

      Log.i(TAG, "GoogleApiClient built");
  }

}
