package com.ouattararomuald.saviezvousque.downloaders;

import java.util.Collections;
import java.util.List;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Path;
import org.simpleframework.xml.Root;

/**
 * Represents an RSS feed.
 *
 * @author OUATTARA Gninlikpoho Romuald
 * @version 1.0
 * @see Item
 * @since 1.0
 */
@Root(strict = false) public class Rss {

  /** Title of the RSS feed. */
  @Path("channel[1]") @Element private final String title;

  /**
   * Collection of Items for the current RSS feed.
   *
   * @see Item
   */
  private final List<Item> items;

  public Rss(@Element(name = "title") String title,
      @Path("channel") @ElementList(name = "channel", entry = "item", inline = true)
          List<Item> items) {
    this.title = title;
    this.items = items;
  }

  /**
   * Gets the title of the RSS feed.
   *
   * @since 1.0
   */
  public String getTitle() {
    return title;
  }

  /**
   * Gets the collection of items for the current RSS feed.
   *
   * @since 1.0
   */
  @Path("channel") @ElementList(name = "channel", entry = "item", inline = true)
  public List<Item> getItems() {
    return Collections.unmodifiableList(items);
  }

  @SuppressWarnings("SimplifiableIfStatement")
  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Rss rss = (Rss) o;

    if (!title.equals(rss.title)) return false;
    return items.equals(rss.items);
  }

  @Override public int hashCode() {
    int result = title.hashCode();
    result = 31 * result + items.hashCode();
    return result;
  }
}