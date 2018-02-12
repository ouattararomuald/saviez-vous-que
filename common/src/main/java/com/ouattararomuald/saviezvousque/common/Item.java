package com.ouattararomuald.saviezvousque.common;

import android.os.Parcel;
import android.os.Parcelable;
import java.util.regex.Pattern;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Namespace;
import org.simpleframework.xml.Root;

/**
 * Immutable RSS feed Item.
 * <br/>
 * Represents a node of an RSS feed.
 *
 * @see Rss
 */
@Root(strict = false)
public class Item implements Parcelable {

  /** Title of the current Item. */
  @Element private final String title;

  /** Link of the current Item. */
  @Element private final String link;

  /** Publication date of the current Item. */
  @Element private final String pubDate;

  /** Content of the current Item. */
  private final String content;

  public Item(@Element(name = "title") String title,
      @Element(name = "link") String link,
      @Element(name = "pubDate") String pubDate,
      @Namespace(prefix = "content", reference = "http://purl.org/rss/1.0/modules/content/")
      @Element(name = "encoded") String content) {
    this.title = title;
    this.link = link;
    this.pubDate = pubDate;
    this.content = content;
  }

  /** Gets the title of the Item. */
  public String getTitle() {
    return title;
  }

  /** Gets the link of the Item. */
  public String getLink() {
    return link;
  }

  /** Gets the publication date of the Item. */
  public String getPubDate() {
    return pubDate;
  }

  /** Gets the content of the Item. */
  @Namespace(prefix = "content", reference = "http://purl.org/rss/1.0/modules/content/")
  @Element(name = "encoded") public String getContent() {
    try {
      Pattern p = Pattern.compile("src=\""), p2 = Pattern.compile("\"");
      return p2.split(p.split(content)[1])[0];
    } catch (ArrayIndexOutOfBoundsException ignored) {
      return content;
    }
  }

  public String getImageName() {
    return getContent().substring(getContent().lastIndexOf("/") + 1);
  }

  @SuppressWarnings("SimplifiableIfStatement")
  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Item item = (Item) o;

    if (!title.equals(item.title)) return false;
    if (!link.equals(item.link)) return false;
    if (!pubDate.equals(item.pubDate)) return false;
    return content.equals(item.content);
  }

  @Override
  public int hashCode() {
    int result = title.hashCode();
    result = 31 * result + link.hashCode();
    result = 31 * result + pubDate.hashCode();
    result = 31 * result + content.hashCode();
    return result;
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel out, int flags) {
    out.writeString(title);
    out.writeString(link);
    out.writeString(pubDate);
    out.writeString(content);
  }

  public static final Creator<Item> CREATOR = new Creator<Item>() {
    public Item createFromParcel(Parcel in) {
      return new Item(in.readString(), in.readString(), in.readString(), in.readString());
    }

    public Item[] newArray(int size) {
      return new Item[size];
    }
  };
}