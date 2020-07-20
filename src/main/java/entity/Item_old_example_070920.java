package entity;

import java.util.Set;

public class Item_old_example_070920 {
	private String itemId;
	private String name;
	private String address;
	private Set<String> keywords;
	private String imageUrl;
	private String url;
	
	private Item_old_example_070920(ItemBuilder builder) { // if public, other people may transfer in unsupported builder 
		this.itemId = builder.itemId;
		this.name=builder.name;
		this.address = builder.address;
		this.imageUrl=builder.imageUrl;
		this.keywords=builder.keywords;
		this.imageUrl=builder.url;

	}
	
	public String getItemId() {
		return itemId;
	}
	public void setItemId(String itemId) {
		this.itemId = itemId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public Set<String> getKeywords() {
		return keywords;
	}
	public void setKeywords(Set<String> keywords) {
		this.keywords = keywords;
	}
	public String getImageUrl() {
		return imageUrl;
	}
	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	
	// the propose of this item builder is to freely set any number of variables in Item, while keep other items set and only readable. If do not care about read-only, can directly use item's set function for values
	public static class ItemBuilder{// static: do not have to instance Item to use builder. With static we can directly use static to make Item
		private String itemId;
		private String name;
		private String address;
		private Set<String> keywords;
		private String imageUrl;
		private String url;
		
		public void setItemId(String itemId) {
			this.itemId = itemId;
		}
		public void setName(String name) {
			this.name = name;
		}
		public void setAddress(String address) {
			this.address = address;
		}
		public void setKeywords(Set<String> keywords) {
			this.keywords = keywords;
		}
		public void setImageUrl(String imageUrl) {
			this.imageUrl = imageUrl;
		}
		public void setUrl(String url) {
			this.url = url;
		}
		
		public ItemBuilder() {} // it's fine we don't write the constructor, that all variables will be set as null
		
		public Item_old_example_070920 build() {
			return new Item_old_example_070920(this);
		}
		
		public void clean() {
			this.itemId=null;
			this.name=null;
			this.address=null;
			this.keywords = null;
			this.imageUrl = null;
			this.url = null;
		}
		//Item item=builder.build(); to construct item
	}
	

}
