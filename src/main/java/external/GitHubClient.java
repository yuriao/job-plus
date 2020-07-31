package external;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import entity.Item;


public class GitHubClient {
	private static final String URL_TEMPLATE = "https://jobs.github.com/positions.json?description=%s&lat=%s&long=%s";
	private static final String DEFAULT_KEYWORD = "developer";
    
	public List<Item> search(double lat, double lon, String keyword) { // get JSONArray from github
		if(keyword==null) {
			keyword=DEFAULT_KEYWORD;
		}
		
		try {
			keyword = URLEncoder.encode(keyword,"UTF-8");
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		
		String url = String.format(URL_TEMPLATE, keyword, lat, lon);
		
		CloseableHttpClient httpclient = HttpClients.createDefault();
		HttpGet httpget = new HttpGet(url);
		
		ResponseHandler<List<Item>> responseHandler = new ResponseHandler<List<Item>>() {

		       @Override
		       public List<Item> handleResponse(final HttpResponse response) throws ClientProtocolException, IOException {
		            int status = response.getStatusLine().getStatusCode();
		            if (status != 200) {
		            	return new ArrayList<>();// if not success return null array
		            }
		            HttpEntity entity = response.getEntity();
		            if (entity == null) {
		            	return new ArrayList<>();// if empty input return null array
		            }
		           String responseBody = EntityUtils.toString(entity);
		            JSONArray array = new JSONArray(responseBody);
		            return getItemList(array);

		        }
		};
        
		try {
			return httpclient.execute(new HttpGet(url), responseHandler);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		}
		return new ArrayList<>();

	}
	
	private List<Item> getItemList(JSONArray array) throws JSONException {
		List<Item> itemList = new ArrayList<>();
		List<String> descriptionList = new ArrayList<>();
		
		for (int i = 0; i < array.length(); i++) {
			// We need to extract keywords from description since GitHub API
			// doesn't return keywords.
			String description = getStringFieldOrEmpty(array.getJSONObject(i), "description");
			if (description.equals("") || description.equals("\n")) {
				descriptionList.add(getStringFieldOrEmpty(array.getJSONObject(i), "title"));
			} else {
				descriptionList.add(description);
			}	
		}

		// We need to get keywords from multiple text in one request since
		// MonkeyLearnAPI has limitations on request per minute.
		List<List<String>> keywords = MonkeyLearnClient.extractKeywords(descriptionList.toArray(new String[descriptionList.size()]));
		for (int i = 0; i < array.length(); i++) {
			JSONObject object = array.getJSONObject(i);
			Item item = Item.builder().itemId(getStringFieldOrEmpty(object, "id")).name(getStringFieldOrEmpty(object, "title")).address(getStringFieldOrEmpty(object, "location")).url(getStringFieldOrEmpty(object, "url")).imageUrl(getStringFieldOrEmpty(object, "company_logo")).keywords(new HashSet<String>(keywords.get(i))).build();
			itemList.add(item);
		}

		return itemList;
	}
	
	private String getStringFieldOrEmpty(JSONObject obj, String field) {
		return obj.isNull(field) ? "" : obj.getString(field);
	}

}
