package com.mobi.bright.activities.services;

import android.location.Location;
import android.os.Build;
import android.util.Log;
import com.mobi.analytics.commom.enums.AnalyticFromClientActions;
import com.mobi.analytics.commom.enums.AnalyticFromServerActions;
import com.mobi.analytics.commom.enums.MobiNetworkTypes;
import com.mobi.analytics.commom.json.MobiAnalyticsFromServer;
import com.mobi.analytics.commom.json.MobiEventJson;
import com.mobi.analytics.commom.json.MobiSessionJson;
import com.mobi.analytics.commom.json.PageInstanceProgressJson;
import com.mobi.analytics.commom.util.GsonUtil;
import com.mobi.bright.minerva.dao.CustomerDao;
import com.mobi.bright.minerva.dao.MobiAnalyticsOutgoing;
import com.mobi.bright.minerva.dao.PageInstance;
import com.mobi.bright.minerva.dao.Tags;
import com.mobi.bright.minerva.data.DataHolder;
import com.mobi.bright.util.AnalyticsUtil;
import com.mobi.bright.util.AndroidUtil;
import com.mobi.bright.util.LogUtil;
import de.tavendo.autobahn.WebSocketConnection;
import de.tavendo.autobahn.WebSocketException;
import de.tavendo.autobahn.WebSocketHandler;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;


/**
 * Singleton Class that maintains a websocket connection, and designed to be robust to restart connection if failed.
 */
public class WebSocket {
// ------------------------------ FIELDS ------------------------------

    /**
     * Queue that stores all messages to be to bs sent
     */
    final static ConcurrentLinkedQueue<MobiAnalyticsOutgoing> queue =
            new ConcurrentLinkedQueue<MobiAnalyticsOutgoing>();

    private static final String TAG = LogUtil.createTag(WebSocket.class);

    /**
     * Singleton
     */
    private static WebSocket ourInstance = null;
    private static WebSocketConnection webSocketConnection = null;

    /**
     * Info about the User who is using the APP, if tis is null, we cant do any analytics with server until user
     * registers or we create an anonymous account
     */
    static CustomerDao customer;

    /**
     * This object holds details for anonymous user creation
     */
    static MobiAnalyticsOutgoing anonymousUserOutgoingEvent = null;


    final static String ANALYTICS_WS_URI = AndroidUtil.BASE_WSURI + "mobi-analytics";

    //Boolean socketReady = false;
    //ThothDatabase thothDatabase;
    String uuid;
    Location location;
    MobiAnalyticsOutgoing mobiAnalyticsOutgoing;
    MobiSessionJson mobiSessionJson;

    MobiNetworkTypes networkType;


    Thread webSocketThread = null;
    volatile boolean connecting = true;

// -------------------------- STATIC METHODS --------------------------

    /**
     * Gets the singleton object, If we dont have a customer an don't have an anonymous user, it will return null.
     *
     * @return singleton instance
     */
    public static synchronized WebSocket getInstance() {
        // if we dont have a customer and don't have a anonymous user
        if (customer == null && anonymousUserOutgoingEvent == null) {
            Log.d(TAG,
                  "No customer defined or anonymous user set- no session to create - No analytics processing");
            return null;
        }


        if (ourInstance == null) {
            ourInstance = new WebSocket();
        }
        return ourInstance;
    }

    public static void setCustomer(CustomerDao customerIn) {
        customer = customerIn;
    }

    /**
     * Creates event and stores event in class, When socket connects will send event
     */
    public static void createTempUserEventToBeSent() {
        anonymousUserOutgoingEvent = new MobiAnalyticsOutgoing();
        anonymousUserOutgoingEvent.setTimestamp(new Date().getTime());
        anonymousUserOutgoingEvent.setAction(AnalyticFromClientActions.CreateAnonymousUser
                                                                      .name());
        // Object will be sent when socket is open
    }

// --------------------------- CONSTRUCTORS ---------------------------

    /**
     *
     */
    private WebSocket() {
        uuid = UUID.randomUUID()
                   .toString();

        // thothDatabase = ThothDatabase.getInstance();

        connecting = true;
        new Thread(new WebSocketRunner()).start();
    }

// --------------------- GETTER / SETTER METHODS ---------------------

    public void setLocation(Location location) {
        this.location = location;
    }

    public void setNetworkType(MobiNetworkTypes networkType) {
        this.networkType = networkType;
    }

// -------------------------- OTHER METHODS --------------------------

    public void createPagePositionEvent(PageInstance pageInstance,
                                        int currentPercentage,
                                        int maxPercentage) {
        MobiEventJson mobiEvent = new MobiEventJson();

        mobiEvent.setPayLoadInteger(pageInstance.getId());

        mobiEvent.setPayLoadString("" + pageInstance.getPageId());

        mobiEvent.setMaxPercentage(maxPercentage);
        mobiEvent.setCurrentPercentage(currentPercentage);

        send(mobiEvent,
             AnalyticFromClientActions.PagePosition);
    }

    /**
     * Will queue object and attempt to start sending queue objects to websocket.
     * <p/>
     * Places the MobiAnalyticsOutgoing object in the SQL LITE db in case of failure.
     *
     * @param serializable Object to wrap to send
     * @param action       The action we are doing
     */
    public void send(Serializable serializable,
                     AnalyticFromClientActions action) {
        if (customer == null) {
            Log.e(TAG,
                  "No event sent or persisted, No customer defined");
            return;
        }

        mobiAnalyticsOutgoing = new MobiAnalyticsOutgoing();
        mobiAnalyticsOutgoing.setTimestamp(new Date().getTime());
        mobiAnalyticsOutgoing.setAction(action.name());
        mobiAnalyticsOutgoing.setJson(GsonUtil.createInstance()
                                              .toJson(serializable));
        mobiAnalyticsOutgoing.setSessionId(uuid);
        mobiAnalyticsOutgoing.setCustomerId(customer.getMobiId());
        mobiAnalyticsOutgoing.setThothId(DataHolder.getTitle()
                                                   .getThothId());

        mobiAnalyticsOutgoing.insert();

        queue.offer(mobiAnalyticsOutgoing);

        sendQueueObjects();
    }

    /**
     * Will attempt to send
     */
    private synchronized void sendQueueObjects() {
        while (!queue.isEmpty()) {
            if (webSocketConnection == null || !webSocketConnection.isConnected()) {
                disconnect();
                webSocketConnection = null;

                if (webSocketThread == null ||
                    !webSocketThread.isAlive() &&
                    !connecting) { // check to see if we already trying to obtain connection, if not then lets try
                    connecting = true;
                    webSocketThread = new Thread(new WebSocketRunner());
                    webSocketThread.start();
                } else {  // we are already trying to create a connection, so lets not create another
                    Log.d(TAG,
                          "WebSocketThread is alive or connecting=" + connecting);
                }

                break; // leave when and if socket connects successfully this method will be called back again from onOpen method
            } else {
                MobiAnalyticsOutgoing outgoing = queue.poll();
                String json = GsonUtil.createInstance()
                                      .toJson(outgoing);
                try {
                    Log.d(TAG,
                          "Sending Queued message json=" + json);

                    webSocketConnection.sendTextMessage(json);
                } catch (Throwable t) {
                    Log.e(TAG,
                          "Cant send message json=" + json,
                          t);
                }
            }
        }
    }

    synchronized void disconnect() {
        if (webSocketConnection != null) {
            webSocketConnection.disconnect();
            Log.d(TAG,
                  "Socket closing ANALYTICS_WS_URI=" + ANALYTICS_WS_URI);
            webSocketConnection = null;
        } else {
            Log.d(TAG,
                  "Socket already is NULL");
        }
    }

    public void createPageViewEvent(PageInstance pageInstance,
                                    int seconds) {

        MobiEventJson mobiEvent = new MobiEventJson();
        mobiEvent.setPayLoadString(pageInstance.getTitle());
        mobiEvent.setPayLoadInteger(pageInstance.getId());
        mobiEvent.setDuration(seconds);

        send(mobiEvent,
             AnalyticFromClientActions.PageView);
    }

    public void createPausedEvent() {
        MobiEventJson mobiEvent = new MobiEventJson();
        send(mobiEvent,
             AnalyticFromClientActions.Paused);
    }

    public void createRestartEvent() {
        MobiEventJson mobiEvent = new MobiEventJson();
        send(mobiEvent,
             AnalyticFromClientActions.Restart);
    }

    public void createTagEvent(Tags tag) {
        MobiEventJson mobiEvent = new MobiEventJson();
        mobiEvent.setPayLoadString(tag.getTag());
        mobiEvent.setPayLoadInteger(tag.getId());

        send(mobiEvent,
             AnalyticFromClientActions.TagSelected);
    }

    private void getPastPageHistory() {
        MobiEventJson mobiEvent = new MobiEventJson();


        send(mobiEvent,
             AnalyticFromClientActions.PagePositionHistory);
    }

    private void sendHello() {
        MobiAnalyticsOutgoing hello = new MobiAnalyticsOutgoing();
        hello.setTimestamp(new Date().getTime());
        hello.setAction(AnalyticFromClientActions.Hello
                                                 .name());
        hello.setThothId(DataHolder.getTitle()
                                   .getThothId());  // from SQL LITE Title Table
        hello.setSessionId(uuid);  // UUID should be created once ONLY for application running lifecycle
        hello.setCustomerId(customer.getMobiId());  //From SQL LITE customer table

        webSocketConnection.sendTextMessage(GsonUtil.createInstance()
                                                    .toJson(hello));
    }

    public void startMobiSession() {
        mobiSessionJson = new MobiSessionJson();
        // get from OS
        mobiSessionJson.setDeviceVersion(Build.VERSION.RELEASE); // E.G 4.3
        mobiSessionJson.setModel(Build.MODEL);  // E.G Nexxus
        mobiSessionJson.setManufacturer(Build.MANUFACTURER);   // E.G Axis

        // get from SQL LITE
        mobiSessionJson.setMinervaVersion(DataHolder.getTitle()
                                                    .getVersion());
        mobiSessionJson.setThothDbCreated(DataHolder.getTitle()
                                                    .getCreated());
        mobiSessionJson.setThothId(DataHolder.getTitle()
                                             .getThothId());

        mobiSessionJson.setPlatform("Android");  // Defined only once

        // enum Type
        if (networkType != null) {
            mobiSessionJson.setNetworkType(networkType.name());
        }

        // GPS  Location object can be null for many reasons
        if (location != null) {
            mobiSessionJson.setLatitude(location.getLatitude());
            mobiSessionJson.setLongitude(location.getLongitude());
        }

        send(mobiSessionJson,
             AnalyticFromClientActions.Start);
    }

    /**
     * Server sent us all page positions that need to be updated
     *
     * @param incoming What server sent us
     */
    private void updatePagePositions(final MobiAnalyticsFromServer incoming) {
        PageInstanceProgressJson[] progressArray = GsonUtil.createInstance()
                                                           .fromJson(incoming.getJson(),
                                                                     PageInstanceProgressJson[].class);

        for (PageInstanceProgressJson pageInstanceProgressJson : progressArray) {
            updatePagePosition(pageInstanceProgressJson);
        }
    }

    /**
     * Update page position in SQL Lite
     *
     * @param pageInstanceProgressJson
     */
    private void updatePagePosition(final PageInstanceProgressJson pageInstanceProgressJson) {
        if (pageInstanceProgressJson == null) {
            Log.e(TAG,
                  "updatePagePosition parameter pageInstanceProgress is NULL");
            return;
        }

        PageInstance pageInstance = PageInstance.getPageFromDb(pageInstanceProgressJson.getPageInstanceId());

        if (pageInstance == null) {
            Log.e(TAG,
                  "Cannot find pageInstance with pageInstanceId=" + pageInstanceProgressJson.getPageInstanceId());
            return;
        }

        if (pageInstance.getMaxPercentage() == null ||
            pageInstanceProgressJson.getMaxPercentage() > pageInstance.getMaxPercentage()) {
            pageInstance.setMaxPercentage(pageInstanceProgressJson.getMaxPercentage());
        }


        if (pageInstance.getLastUpdate() == null ||
            pageInstanceProgressJson.getLastUpdateTimeStamp() > pageInstance.getLastUpdate()) {
            pageInstance.setLastUpdate(pageInstanceProgressJson.getLastUpdateTimeStamp());
            pageInstance.setCurrentPercentage(pageInstanceProgressJson.getCurrentPercentage());
            pageInstance.setDateRead(pageInstanceProgressJson.getDateReadTimeStamp());
        }

        pageInstance.update();
    }

// -------------------------- INNER CLASSES --------------------------

    /**
     * Class connects to websocket in a thread
     */
    class WebSocketRunner implements Runnable {
        @Override
        public void run() {
            synchronized (WebSocket.this) {
                try {
                    if (webSocketConnection != null && webSocketConnection.isConnected()) {
                        connecting = false;
                        Log.d(TAG,
                              "Websocket was Already Connected");
                        return;
                    }


                    webSocketConnection = null;

                    System.gc();

                    Log.d(TAG,
                          "Connecting ANALYTICS_WS_URI = " + ANALYTICS_WS_URI);
                    webSocketConnection = new WebSocketConnection();

                    synchronized (webSocketConnection) {
                        webSocketConnection.connect(ANALYTICS_WS_URI,
                                                    new AnalyticsHandler());
                    }
                } catch (WebSocketException e) {
                    connecting = false;

                    Log.e(TAG,
                          "Error Connecting to websocket",
                          e);

                    webSocketConnection = null;
                }
            }
        }
    }

    class AnalyticsHandler extends WebSocketHandler {
        @Override
        public void onOpen() {
            Log.d(TAG,
                  "onOpen=" + ANALYTICS_WS_URI);

            if (anonymousUserOutgoingEvent != null) {
                webSocketConnection.sendTextMessage(GsonUtil.createInstance()
                                                            .toJson(anonymousUserOutgoingEvent));
                anonymousUserOutgoingEvent = null;
            } else {
                sendHello();
            }

            connecting = false;
        }

        private void sendOldStaleObjects() {
            // get from DB
            List<MobiAnalyticsOutgoing> analyticsOutgoingList = MobiAnalyticsOutgoing.getUnsent();

            for (MobiAnalyticsOutgoing analyticsOutgoing : analyticsOutgoingList) {
                queue.add(analyticsOutgoing);
            }

            sendQueueObjects();
        }

        private void removeOutgoingFromDatabase(final MobiAnalyticsFromServer incoming) {
            if (MobiAnalyticsOutgoing.delete(incoming.getTimestamp())) {
                Log.d(TAG,
                      "Deleted MobiAnalyticsOutgoing with timestamp=" + incoming.getTimestamp());
            } else {
                Log.e(TAG,
                      "DID NOT Delete MobiAnalyticsOutgoing with timestamp=" + incoming.getTimestamp());
            }
        }

        public void onTextMessage(String payload) {
            Log.d(TAG,
                  "Got Message payload= " + payload);

            MobiAnalyticsFromServer incoming =
                    GsonUtil.createInstance()
                            .fromJson(payload,
                                      MobiAnalyticsFromServer.class);


            AnalyticFromServerActions analyticFromServerAction;

            try {
                analyticFromServerAction =
                        AnalyticFromServerActions.valueOf(incoming.getAction());
            } catch (Throwable e) {
                Log.e(TAG,
                      "Cannot find  AnalyticFromServerActions with action=" + incoming.getAction(),
                      e);
                return;
            }


            switch (analyticFromServerAction) {
                case Confirm:
                    removeOutgoingFromDatabase(incoming);
                    break;

                case Error:
                    Log.e(TAG,
                          "WEB_SOCKET ERROR " + incoming.getErrorMessage());
                    removeOutgoingFromDatabase(incoming);
                    break;

                case SessionCreated:   // Get All page positions
                    getPastPageHistory();
                    break;

                case HelloNewSession: // serving is telling us we have no session
                    sendOldStaleObjects();
                    startMobiSession();
                    break;

                case HelloReturningSession: // serving is telling us we had a session persisted
                    sendOldStaleObjects();
                    break;


                case PagePositionUpdated:
                    PageInstanceProgressJson pageInstanceProgressJson = GsonUtil.createInstance()
                                                                                .fromJson(incoming.getJson(),
                                                                                          PageInstanceProgressJson.class);
                    updatePagePosition(pageInstanceProgressJson);
                    break;

                case AnonymousCustomerCreated:
                    CustomerDao customer = AnalyticsUtil.setCustomerToSqlLite(incoming);
                    WebSocket.setCustomer(customer);
                    sendHello();
                    break;

                case PagePositions:
                    updatePagePositions(incoming);
                    break;

                default:
                    Log.d(TAG,
                          "No action for action=" + analyticFromServerAction);
            }
        }

        @Override
        public void onClose(int code,
                            String reason) {
            Log.d(TAG,
                  "Connection lost. " + "reason=" + reason + " code=" + code + " " + ANALYTICS_WS_URI);

            webSocketConnection = null;
        }
    }
}



