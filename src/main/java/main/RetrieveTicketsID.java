package main;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.net.URL;
import java.nio.charset.Charset;

public class RetrieveTicketsID {
    /*public static JSONArray readJsonArrayFromUrl(String url) throws IOException {
        InputStream inputStream = new URL(url).openStream();
        try {

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, Charset.forName("UTF-8")));
            String jsonText = readAll(bufferedReader);

            return new JSONArray(jsonText);
        } finally {
            inputStream.close();
        }
    }*/

    private static String readAll(Reader bufferedReader) throws IOException {
        int charPointer;
        StringBuilder stringBuilder = new StringBuilder();
        while ((charPointer = bufferedReader.read()) != -1) {
            stringBuilder.append((char) charPointer);
        }
        return stringBuilder.toString();
    }

    private static JSONObject readJSONFromUrl(String url) throws IOException {
        InputStream inputStream = new URL(url).openStream();
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, Charset.forName("UTF-8")));
            String jsonText = readAll(bufferedReader);
            return new JSONObject(jsonText);
        } finally {
            inputStream.close();
        }
    }

    private static String generateJiraUrl(String projectName, Integer startAt, Integer maxResults) {
        return "https://issues.apache.org/jira/rest/api/2/search?jql=project=%22"
                + projectName + "%22AND%22issueType%22=%22Bug%22AND(%22status%22=%22closed%22OR"
                + "%22status%22=%22resolved%22)AND%22resolution%22=%22fixed%22&fields=key,resolutiondate,versions,created&startAt="
                + startAt.toString() + "&maxResults=" + maxResults.toString();
    }

    public static void main(String[] args) throws IOException {
        int maxResults = 0;
        int startAt = 0;
        int issuesCount = 1;
        int upperBound = 1000;
        String totalKey = "total";
        String issuesKey = "issues";
        String issueKey = "key";
        String projectName = "QPID";

        do {
            maxResults = startAt + upperBound; //Only gets a max of 1000 at a time, so must do this multiple times if bugs >1000
            String url = generateJiraUrl(projectName, startAt, maxResults);

            JSONObject json = readJSONFromUrl(url);
            issuesCount = json.getInt(totalKey);

            JSONArray issues = json.getJSONArray(issuesKey);

            for (; startAt < issuesCount && startAt < maxResults; startAt++) {
                //Iterate through each bug
                String key = issues.getJSONObject(startAt % upperBound).get(issueKey).toString();
                System.out.println(key);
            }
        } while (startAt < issuesCount);

    }
}

