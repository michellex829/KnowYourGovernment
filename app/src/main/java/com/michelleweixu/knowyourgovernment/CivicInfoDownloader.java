package com.michelleweixu.knowyourgovernment;

import android.net.Uri;
import android.util.Log;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class CivicInfoDownloader implements Runnable {

    private static final String TAG = "CivicInfoDownloader";
    private static final String URL  = "https://www.googleapis.com/civicinfo/v2/representatives?key=AIzaSyAoT91EMPJhFftoiDIgDBelV46tnGyRbvk&address=";
    private MainActivity mainActivity;
    private String searchTarget;
    // API key: AIzaSyAoT91EMPJhFftoiDIgDBelV46tnGyRbvk

    public CivicInfoDownloader(MainActivity mainActivity, String searchTarget) {
        this.mainActivity = mainActivity;
        this.searchTarget = searchTarget;
    }

    @Override
    public void run() {

        Log.d(TAG, "run: " + URL + searchTarget);
        String endURL = URL + searchTarget;
        StringBuilder sb = new StringBuilder();
        Uri dataUri = Uri.parse(endURL);
        String urlToUse = dataUri.toString();

        try {
            URL url = new URL(urlToUse);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.connect();

            if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
                Log.d(TAG, "run: HTTP ResponseCode NOT OK: " + conn.getResponseCode());
                return;
            }

            InputStream is = conn.getInputStream();
            BufferedReader reader = new BufferedReader((new InputStreamReader(is)));

            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append('\n');
            }

            Log.d(TAG, "run: " + sb.toString());

        } catch (Exception e) {
            Log.e(TAG, "run: ", e);
            return;
        }

        process(sb.toString());
        Log.d(TAG, "run: ");

    }

    private void process(String s) {
        try {
            JSONObject jObject = new JSONObject(s);
            JSONObject jNormalizedInput = jObject.getJSONObject("normalizedInput");
            int index = -1;
            String officeTitle = null;
            String phoneNumber = null;
            String officialName = null;
            String completeAddress = null;
            String addressLine1 = "";
            String addressLine2 = "";
            String addressLine3 = "";
            String official_city = "";
            String official_state = "";
            String official_zip = "";

            String party = "unknown";
            String URL = null;
            String photoURL = null;
            String email = null;
            String FacebookID = null;
            String TwitterID = null;
            String YoutubeID = null;

            JSONArray jarrayOffices = jObject.getJSONArray("offices");
            JSONArray jarrayOfficials = jObject.getJSONArray("officials");
            JSONArray officialIndices = null;
            final String city = jNormalizedInput.getString("city");
            final String state = jNormalizedInput.getString("state");
            final String zip = jNormalizedInput.getString("zip");

            mainActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mainActivity.setCurrLocation(city, state, zip);
                }
            });

            for (int i = 0; i < jarrayOffices.length(); i++) {
                JSONObject jOffice = (JSONObject) jarrayOffices.get(i);

                officeTitle = jOffice.getString("name");
                officialIndices = jOffice.getJSONArray("officialIndices");
                for (int j = 0; j < officialIndices.length(); j++) {
                    String temp = officialIndices.getString(j);
                    index = -1;
                    phoneNumber = null;
                    completeAddress = null;
                    addressLine1 = "";
                    addressLine2 = "";
                    addressLine3 = "";

                    party = "unknown";
                    URL = null;
                    photoURL = null;
                    email = null;
                    FacebookID = null;
                    TwitterID = null;
                    YoutubeID = null;
                    if (!temp.trim().isEmpty() && !temp.trim().equals("null")) {
                        index = Integer.parseInt(temp);

                        JSONObject official =  jarrayOfficials.getJSONObject(index);
                        officialName = official.getString("name");

                        if (official.has("address")) {
                            JSONArray address = official.getJSONArray("address");
                            JSONObject jAddress = (JSONObject) address.getJSONObject(0);
                            if (jAddress.has("line1")) {
                                addressLine1 = jAddress.getString("line1");
                                completeAddress = addressLine1 + "\n";
                            }
                            if (jAddress.has("line2")) {
                                addressLine2 = jAddress.getString("line2");
                                completeAddress += addressLine2 + "\n";
                            }
                            if (jAddress.has("line3")) {
                                addressLine3 = jAddress.getString("line3");
                                completeAddress += addressLine3 + "\n";
                            }
                            if (jAddress.has("city")) {
                                official_city = jAddress.getString("city");
                                completeAddress += official_city;
                            }
                            if (jAddress.has("state")) {
                                official_state = jAddress.getString("state");
                                completeAddress += ", " + official_state;
                            }
                            if (jAddress.has("zip")) {
                                official_zip = jAddress.getString("zip");
                                completeAddress += " " + official_zip;
                            }
                            // check for possible line1, line2 & line3 –
                            // concatenate them into one String), City, State & Zip Code of this
                            // person’s office
                        }
                        if (official.has("party")) {
                            party = official.getString("party");
                        }
                        if (official.has("photoUrl")) {
                            photoURL = official.getString("photoUrl");
                        }
                        if (official.has("phones")) {
                            JSONArray phoneNumbers = official.getJSONArray("phones");
                            if (phoneNumbers.length() > 0) {
                                phoneNumber = phoneNumbers.get(0).toString().trim();
                            }
                        }
                        if (official.has("urls")) {
                            JSONArray URLs = official.getJSONArray("urls");
                            if (URLs.length() > 0) {
                                URL = URLs.get(0).toString();
                            }
                        }

                        if (official.has("emails")) {
                            JSONArray emails = official.getJSONArray("emails");
                            if (emails.length() > 0) {
                                email = emails.get(0).toString();
                            }
                        }
                        if (official.has("channels")) {
                            JSONArray channels = official.getJSONArray("channels"); // social media handles
                            for (int k = 0; k < channels.length(); k++) {
                                JSONObject channel = (JSONObject) channels.getJSONObject(k);
                                if (channel.getString("type").equals("Facebook"))
                                    FacebookID = channel.getString("id");
                                else if (channel.getString("type").equals("Twitter"))
                                    TwitterID = channel.getString("id");
                                else if (channel.getString("type").equals("YouTube"))
                                    YoutubeID = channel.getString("id");
                            }
                        }
                        final Official off = new Official(officeTitle, officialName, party, completeAddress, phoneNumber, email, URL, FacebookID, TwitterID, YoutubeID, photoURL);
                        mainActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mainActivity.addOfficialToList(off);
                            }
                        });
                    }
                }
            }
        } catch (Exception e) {
            Log.d(TAG, "parseJSON: " + e.getMessage());
            e.printStackTrace();
        }
    }
}