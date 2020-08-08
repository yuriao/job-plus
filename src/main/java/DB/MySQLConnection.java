package DB;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

import entity.Item;

public class MySQLConnection {
	private Connection conn;
	private MySQLDBUtil dbutil=new MySQLDBUtil();
    public MySQLConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
            conn = DriverManager.getConnection(dbutil.URL);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void close() {
        if (conn != null) {
            try {
                conn.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    public void setFavoriteItems(String userId, Item item) {
    	// if item is already in item table, do not have to insert item to history; is not ,insert new item
    	// This is done by primary key defination in history
    	//duplicate item_id will raise error
    	// to avoid error the IGNORE statement is used to ignore this insertation
        if (conn == null) {
            System.err.println("DB connection failed, check connection or config file");
            return;
        }
        saveItem(item); // put items to item table
        String sql = "INSERT IGNORE INTO history (user_id, item_id) VALUES (?, ?)";
        try {
            PreparedStatement statement = conn.prepareStatement(sql);
            statement.setString(1, userId);
            statement.setString(2, item.getItemId());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void unsetFavoriteItems(String userId, String itemId) {
        if (conn == null) {
            System.err.println("DB connection failed");
            return;
        }
        // only manipulate history but not item
        // no need to delete in item for real time 
        
        String sql = "DELETE FROM history WHERE user_id = ? AND item_id = ?";
        try {
            PreparedStatement statement = conn.prepareStatement(sql);
            statement.setString(1, userId);
            statement.setString(2, itemId);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void saveItem(Item item) {
        if (conn == null) {
            System.err.println("DB connection failed");
            return;
        }
        String sql = "INSERT IGNORE INTO items VALUES (?, ?, ?, ?, ?)"; // also helps ignore insert duplicate items
        try {
            PreparedStatement statement = conn.prepareStatement(sql);
            statement.setString(1, item.getItemId());
            statement.setString(2, item.getName());
            statement.setString(3, item.getAddress());
            statement.setString(4, item.getImageUrl());
            statement.setString(5, item.getUrl());
            statement.executeUpdate();
            
            sql = "INSERT IGNORE INTO keywords VALUES (?, ?)";// helps ignore insert duplicate items
                    statement = conn.prepareStatement(sql);
            statement.setString(1, item.getItemId());
            for (String keyword : item.getKeywords()) {
                statement.setString(2, keyword);
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public Set<String> getFavoriteItemIds(String userId) {
        if (conn == null) {
            System.err.println("DB connection failed");
            return new HashSet<>();
        }

        Set<String> favoriteItems = new HashSet<>();

        try {
            String sql = "SELECT item_id FROM history WHERE user_id = ?";
            PreparedStatement statement = conn.prepareStatement(sql);
            statement.setString(1, userId);
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                String itemId = rs.getString("item_id");
                favoriteItems.add(itemId);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return favoriteItems;
    }
    
    public Set<Item> getFavoriteItems(String userId) {
        if (conn == null) {
            System.err.println("DB connection failed");
            return new HashSet<>();
        }
        Set<Item> favoriteItems = new HashSet<>();
        Set<String> favoriteItemIds = getFavoriteItemIds(userId);

        String sql = "SELECT * FROM items WHERE item_id = ?";
        try {
            PreparedStatement statement = conn.prepareStatement(sql);
            for (String itemId : favoriteItemIds) {
                statement.setString(1, itemId);
                ResultSet rs = statement.executeQuery();
                if (rs.next()) {
                    favoriteItems.add(Item.builder()
                            .itemId(rs.getString("item_id"))
                            .name(rs.getString("name"))
                            .address(rs.getString("address"))
                            .imageUrl(rs.getString("image_url"))
                            .url(rs.getString("url"))
                            .keywords(getKeywords(itemId))
                            .build());
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return favoriteItems;
    }

    public Set<String> getKeywords(String itemId) {
        if (conn == null) {
            System.err.println("DB connection failed");
            return null;
        }
        Set<String> keywords = new HashSet<>();
        String sql = "SELECT keyword from keywords WHERE item_id = ? ";
        try {
            PreparedStatement statement = conn.prepareStatement(sql);
            statement.setString(1, itemId);
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                String keyword = rs.getString("keyword");
                keywords.add(keyword);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return keywords;
    }

    public String getFullname(String userId) {
    	if(conn == null) {
    	    System.err.println("DB connection failed");
    	    return "";
    	}
    	
    	String name="";
    	String sql = "SELECT first_name, last_name FROM users WHERE user_id = ?";
        try {
            PreparedStatement statement = conn.prepareStatement(sql);
            statement.setString(1, userId);
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                name = rs.getString("first_name") + " " + rs.getString("last_name");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

    	return name;
    }
    
    public boolean verifyLogin(String userId, String password) {
    	if (conn == null) {
            System.err.println("DB connection failed");
            return false;
        }
        String sql = "SELECT user_id FROM users WHERE user_id = ? AND password = ?";
        try {
            PreparedStatement statement = conn.prepareStatement(sql);
            statement.setString(1, userId);
            statement.setString(2, password);
            ResultSet rs = statement.executeQuery();
            return rs.next(); // if not found in userID/pass storage, this user not exist and return false
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return false;
    }
    
    public boolean addUser(String userId, String password, String firstname, String lastname) {
        if (conn == null) {
            System.err.println("DB connection failed");
            return false;
        }

        String sql = "INSERT IGNORE INTO users VALUES (?, ?, ?, ?)"; // ignore already exist users
        try {
            PreparedStatement statement = conn.prepareStatement(sql);
            statement.setString(1, userId);
            statement.setString(2, password);
            statement.setString(3, firstname);
            statement.setString(4, lastname);

            return statement.executeUpdate() == 1;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }


}

