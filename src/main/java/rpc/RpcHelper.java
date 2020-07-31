package rpc;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONArray;
import org.json.JSONObject;

import entity.Item;


public class RpcHelper {
	// Writes a JSONArray to http response.
	public static void writeJsonArray(HttpServletResponse response, JSONArray array) throws IOException {
		response.setContentType("application/json");
		response.getWriter().print(array);
	}

	// Writes a JSONObject to http response.
	public static void writeJsonObject(HttpServletResponse response, JSONObject obj) throws IOException {
		response.setContentType("application/json");
		response.getWriter().print(obj);
	}

	public static Item parseFavoriteItem(JSONObject favoriteItem) {
        Set<String> keywords = new HashSet<>();
        JSONArray array = favoriteItem.getJSONArray("keywords");
        for (int i = 0; i < array.length(); ++i) {
            keywords.add(array.getString(i));
        }
        return Item.builder()
                .itemId(favoriteItem.getString("item_id"))
                .name(favoriteItem.getString("name"))
                .address(favoriteItem.getString("address"))
                .url(favoriteItem.getString("url"))
                .imageUrl(favoriteItem.getString("image_url"))
                .keywords(keywords)
                .build();
    }



	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.setContentType("application/json");
		PrintWriter writer = response.getWriter();
		
		JSONArray array = new JSONArray();
		array.put(new JSONObject().put("username", "abcd"));
		array.put(new JSONObject().put("username", "1234"));

		writer.print(array);
		RpcHelper.writeJsonArray(response, array);
	}
}