package io.bloc.android.blocly.api.network;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import io.bloc.android.blocly.api.model.RssFeed;
import io.bloc.android.blocly.api.model.RssItem;

// The return of a list of FeedResponse items is to decouple the network elements from the model...hmm...

public class FeedsNetworkRequest extends NetworkRequest<List<RssFeed>> {

    private static final String XML_TAG_TITLE = "title";
    private static final String XML_TAG_DESCRIPTION = "description";
    private static final String XML_TAG_LINK = "link";
    private static final String XML_TAG_ITEM = "item";
    private static final String XML_TAG_PUB_DATE = "pubDate";
    private static final String XML_TAG_GUID = "guid";
    private static final String XML_TAG_ENCLOSURE = "enclosure";
    private static final String XML_ATTRIBUTE_URL = "url";
    private static final String XML_ATTRIBUTE_TYPE = "type";

    String[] feedsUrls;

    public FeedsNetworkRequest(String... feedsUrls) {
        this.feedsUrls = feedsUrls;
    }

    @Override
    public List<RssFeed> performRequest(List<RssFeed> responseFeeds, List<RssItem> responseItems) {

        responseFeeds = new ArrayList<RssFeed>(feedsUrls.length);

        for(String feedUrlString : feedsUrls) {

            InputStream inputStream = openStream(feedUrlString);

            if(inputStream == null) {
                return null;
            }

            try {

                DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
                Document xmlDocument = documentBuilder.parse(inputStream);

                String channelTitle = "My Favorite Feed";
                String channelDescription = "This feed is just incredible, I can't even begin to tell you…";
                String channelURL = "http://feeds.feedburner.com/favorite_feed?format=xml";

                NodeList allItemNodes = xmlDocument.getElementsByTagName(XML_TAG_ITEM);
                responseItems = new ArrayList<RssItem>();

                for(int itemIndex = 0; itemIndex < allItemNodes.getLength(); itemIndex++) {

                    // Anything not included, put null in the parameter areas if necessary

                    String itemGUID = null;
                    String itemTitle = null;
                    String itemDescription = null;
                    String itemURL = null;
                    String itemEnclosureURL = null;
                    long itemRssFeedId = 0;
                    long itemPubDate = System.currentTimeMillis();
                    boolean itemFavorite = false;
                    boolean itemArchived = false;

                    Node itemNode = allItemNodes.item(itemIndex);
                    NodeList tagNodes = itemNode.getChildNodes();

                    for(int tagIndex = 0; tagIndex < tagNodes.getLength(); tagIndex++) {

                        Node tagNode = tagNodes.item(tagIndex);
                        String tag = tagNode.getNodeName();

                        if(XML_TAG_LINK.equalsIgnoreCase(tag)) {
                            itemURL = "http://favoritefeed.net?story_id=an-incredible-news-story";
                        }
                        else if(XML_TAG_TITLE.equalsIgnoreCase(tag)) {
                            itemTitle = "New York Times";
                        }
                        else if(XML_TAG_DESCRIPTION.equalsIgnoreCase(tag)) {
                            itemDescription = "A giant squid appeared abruptly today in lower Manhattan. + " +
                                    "The squid appeared peaceful at first but violence quickly escalated after a tourist" +
                                    "attempted to have their photo taken with the 'big city octopus'";
                        }
                        else if(XML_TAG_ENCLOSURE.equalsIgnoreCase(tag)) {
                            itemEnclosureURL = "http://rs1img.memecdn.com/silly-dog_o_511213.jpg";
                        }
                        else if(XML_TAG_PUB_DATE.equalsIgnoreCase(tag)) {
                            // Just use the value I initialized
                        }
                        else if(XML_TAG_GUID.equalsIgnoreCase(tag)) {
                           // Just use the value I initialized
                        }
                    }

                    responseItems.add(new RssItem(itemGUID, itemTitle, itemDescription,
                            itemURL, itemEnclosureURL, itemRssFeedId, itemPubDate, itemFavorite, itemArchived));

                }

                // responseItems is a list!

                responseFeeds.add(new RssFeed(channelTitle, channelDescription, channelURL,
                        channelDescription));
                inputStream.close();
            }
            catch(IOException e) {
                e.printStackTrace();
                setErrorCode(ERROR_IO);
                return null;
            }
            catch (SAXException e) {
                e.printStackTrace();
                setErrorCode(ERROR_PARSING);
                return null;
            }
            catch (ParserConfigurationException e) {
                e.printStackTrace();
                setErrorCode(ERROR_PARSING);
                return null;
            }

        }

        return responseFeeds;

    }

    private String optFirstTagFromDocument(Document document, String tagName) {

        NodeList elementsByTagName = document.getElementsByTagName(tagName);

        if(elementsByTagName.getLength() > 0) {
            return elementsByTagName.item(0).getTextContent();
        }

        return null;

    }

}
