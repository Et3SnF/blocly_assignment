package io.bloc.android.blocly.api.network;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
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

// The return of a list of FeedResponse items is to decouple the network elements from the model...hmm...

public class GetFeedsNetworkRequest extends NetworkRequest<List<GetFeedsNetworkRequest.FeedResponse>> {

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

    public GetFeedsNetworkRequest(String... feedsUrls) {
        this.feedsUrls = feedsUrls;
    }

    @Override
    public List<FeedResponse> performRequest() {

        List<FeedResponse> responseFeeds = new ArrayList<FeedResponse>(feedsUrls.length);

        for(String feedUrlString : feedsUrls) {

            InputStream inputStream = openStream(feedUrlString);

            if(inputStream == null) {
                return null;
            }

            try {

                DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
                Document xmlDocument = documentBuilder.parse(inputStream);

                String channelTitle = optFirstTagFromDocument(xmlDocument, XML_TAG_TITLE);
                String channelDescription = optFirstTagFromDocument(xmlDocument, XML_TAG_DESCRIPTION);
                String channelURL = optFirstTagFromDocument(xmlDocument, XML_TAG_LINK);

                NodeList allItemNodes = xmlDocument.getElementsByTagName(XML_TAG_ITEM);
                List<ItemResponse> responseItems = new ArrayList<ItemResponse>(allItemNodes.getLength());

                for(int itemIndex = 0; itemIndex < allItemNodes.getLength(); itemIndex++) {

                    // Anything not included, put null in the parameter areas if necessary

                    String itemURL = null;
                    String itemTitle = null;
                    String itemDescription = null;
                    String itemGUID = null;
                    String itemPubDate = null;
                    String itemEnclosureURL = null;
                    String itemEnclosureMIMEType = null;

                    Node itemNode = allItemNodes.item(itemIndex);
                    NodeList tagNodes = itemNode.getChildNodes();

                    for(int tagIndex = 0; tagIndex < tagNodes.getLength(); tagIndex++) {

                        Node tagNode = tagNodes.item(tagIndex);
                        String tag = tagNode.getNodeName();

                        if(XML_TAG_LINK.equalsIgnoreCase(tag)) {
                            itemURL = tagNode.getTextContent();
                        }
                        else if(XML_TAG_TITLE.equalsIgnoreCase(tag)) {
                            itemTitle = tagNode.getTextContent();
                        }
                        else if(XML_TAG_DESCRIPTION.equalsIgnoreCase(tag)) {
                            itemDescription = tagNode.getTextContent();
                        }
                        else if(XML_TAG_ENCLOSURE.equalsIgnoreCase(tag)) {

                            // because the enclosure tag ends with /> and has two items in it, we need to do something about it
                            NamedNodeMap enclosureAttributes = tagNode.getAttributes();
                            itemEnclosureURL = enclosureAttributes.getNamedItem(XML_ATTRIBUTE_URL).getTextContent();
                            itemEnclosureMIMEType = enclosureAttributes.getNamedItem(XML_ATTRIBUTE_TYPE).getTextContent();

                        }
                        else if(XML_TAG_PUB_DATE.equalsIgnoreCase(tag)) {
                            itemPubDate = tagNode.getTextContent();
                        }
                        else if(XML_TAG_GUID.equalsIgnoreCase(tag)) {
                            itemGUID = tagNode.getTextContent();
                        }
                    }

                    responseItems.add(new ItemResponse(itemURL, itemTitle, itemDescription,
                            itemGUID, itemPubDate, itemEnclosureURL, itemEnclosureMIMEType));

                }

                // responseItems is a list!

                responseFeeds.add(new FeedResponse(feedUrlString, channelTitle, channelURL, channelDescription, responseItems));
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

    // Class for reading the feed. Feed first, then items

    public static class FeedResponse {

        // Member variables for feed stuff

        public final String channelFeedURL;
        public final String channelTitle;
        public final String channelURL;
        public final String channelDescription;
        public final List<ItemResponse> channelItems;

        // Constructor

        public FeedResponse(String channelDescription, String channelFeedURL, String channelTitle, String channelURL, List<ItemResponse> channelItems) {
            this.channelDescription = channelDescription;
            this.channelFeedURL = channelFeedURL;
            this.channelTitle = channelTitle;
            this.channelURL = channelURL;
            this.channelItems = channelItems;
        }
    }

    // Class for items objects

    public static class ItemResponse {

        // Member variables. They are final so no need for setters or getters

        public final String itemURL;
        public final String itemTitle;
        public final String itemDescription;
        public final String itemGUID;
        public final String itemPubDate;
        public final String itemEnclosureURL;
        public final String itemEnclosureMIMEType;

        // Constructor

        public ItemResponse(String itemDescription, String itemURL, String itemTitle, String itemGUID, String itemPubDate, String itemEnclosureURL, String itemEnclosureMIMEType) {

            this.itemDescription = itemDescription;
            this.itemURL = itemURL;
            this.itemTitle = itemTitle;
            this.itemGUID = itemGUID;
            this.itemPubDate = itemPubDate;
            this.itemEnclosureURL = itemEnclosureURL;
            this.itemEnclosureMIMEType = itemEnclosureMIMEType;

        }
    }

}
