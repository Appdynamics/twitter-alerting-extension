package com.appdynamics.alerts.twitter;

import org.apache.log4j.Logger;
import twitter4j.*;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;

import java.io.*;

public class TwitterAlert {
    private static Logger logger = Logger.getLogger(TwitterAlert.class);

    private static final String CONSUMER_KEY = System.getProperty("consumer.key");
    private static final String CONSUMER_SECRET = System.getProperty("consumer.secret");

    private static final int STATUS_LENGTH = 140;
    private static final String TOKEN_FILE = "token";
    private static final String RATE_LIMIT_STATUS_FILE = "rate-limit";

    private static Twitter twitter;
    private static Event event;

    public static void main(String[] args){
        twitter = TwitterFactory.getSingleton();
        twitter.setOAuthConsumer(CONSUMER_KEY,CONSUMER_SECRET);
        twitter.addRateLimitStatusListener(new RateLimitStatusListener() {
            @Override
            public void onRateLimitStatus(RateLimitStatusEvent rateLimitStatusEvent) {
                RateLimitStatus status = rateLimitStatusEvent.getRateLimitStatus();
                if (status.getRemaining() <= 0){
                    logger.warn("Rate limit reached");
                    storeRateLimitStatus(status);
                }
            }

            @Override
            public void onRateLimitReached(RateLimitStatusEvent rateLimitStatusEvent) {
                //never called
            }
        });

        if (args.length == 1 && args[0].equals("auth")){
            try {
                getAuthorization();

                System.out.println("Authenticated for user @" + twitter.getScreenName());
            } catch (TwitterException e) {
                if (e.getStatusCode() == 401) {
                    logger.error("Missing or incorrect authentication credentials");
                } else {
                    logger.error("Authentication failed", e);
                }
            }
        } else {
            try {
                parseEventParams(args);

                AccessToken accessToken = readAccessToken();
                twitter.setOAuthAccessToken(accessToken);

                postUpdate();
            } catch (IOException e) {
                logger.error("Access token unreadable,unable to authenticate. Please rerun authentication script");
            } catch (ClassNotFoundException e) {
                logger.error("Access token unreadable,unable to authenticate. Please rerun authentication script");
            } catch (Exception e) {
                logger.error("Failed to parse arguments");
            }
        }
    }

    private static void parseEventParams(String[] args) throws Exception{
        args = stripQuotes(args);

        //non health rule violation event
        if (args[args.length-1].startsWith("http")){
            OtherEvent oe = new OtherEvent();
            oe.appName = args[0];
            oe.priority = args[3];
            oe.severity = args[4];
            oe.tag = args[5];
            oe.eventName = args[6];
            oe.eventID = args[7];
            oe.deepLinkUrl = args[args.length-1];
            event = oe;
        } else {    //health rule violation
            HealthRuleViolation hrv = new HealthRuleViolation();
            hrv.appName = args[0];
            hrv.priority = args[3];
            hrv.severity = args[4];
            hrv.tag = args[5];
            hrv.healthRuleName = args[6];
            hrv.healthRuleID = args[7];
            hrv.summaryMessage = args[args.length-4];
            hrv.incidentID = args[args.length-3];
            hrv.deepLinkUrl = args[args.length-2];
            hrv.eventType = args[args.length-1];
            event = hrv;
        }
    }

    private static void getAuthorization() throws TwitterException {
        RequestToken requestToken = twitter.getOAuthRequestToken();

        System.out.println("Follow this URL and grant this extension access to your Twitter account:");
        System.out.println(requestToken.getAuthorizationURL());
        System.out.println("Enter PIN shown on page:");
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        AccessToken accessToken;
        try {
            String pin = reader.readLine();

            if (pin.length() > 0) {
                accessToken = twitter.getOAuthAccessToken(requestToken, pin);
            } else {
                System.out.println("No PIN entered");
                accessToken = twitter.getOAuthAccessToken();
            }
        } catch (TwitterException e) {
            logger.error("Failed to get access token", e);
            accessToken = twitter.getOAuthAccessToken();
        } catch (IOException e) {
            logger.error("Cannot read PIN");
            accessToken = twitter.getOAuthAccessToken();
        }
        storeAccessToken(accessToken);

    }

    private static void storeAccessToken(AccessToken token) {
        storeObject(token, TOKEN_FILE, "access token");
    }

    private static AccessToken readAccessToken() throws IOException, ClassNotFoundException {
        FileInputStream fis = new FileInputStream(TOKEN_FILE);
        ObjectInputStream ois = new ObjectInputStream(fis);
        return (AccessToken) ois.readObject();
    }

    private static void postUpdate(){
        try {
            if (!isLimitReached()){
                User user = twitter.verifyCredentials();
                if (user.isProtected()){
                    twitter.updateStatus(createStatus(true));
                } else {
                    logger.error("User @" + user.getScreenName() + " status is not protected, aborting status update");
                }
            } else {
                logger.info("Ignored tweet: " + createStatus(false));
            }
        } catch (TwitterException e) {
            if (e.getStatusCode() == 403 || e.getStatusCode() == 400){
                logger.error(e.getErrorMessage());
            } else {
                logger.error(e);
            }
        }
    }

    private static String createStatus(boolean doAPICall) throws TwitterException {
        int urlLength = 22;     //shortened URL length for http
        if (doAPICall) {
            TwitterAPIConfiguration apiConfiguration = twitter.help().getAPIConfiguration();
            if (event.deepLinkUrl.startsWith("https")) {
                urlLength = apiConfiguration.getShortURLLengthHttps();
            } else {
                urlLength = apiConfiguration.getShortURLLength();
            }
        }

        String status;
        if (event instanceof HealthRuleViolation) {
            status = "Health rule violation. ";
            status += "P" + event.priority + " ";
            status += event.severity + " ";

            String msg;
            msg = "Incident ID:" + ((HealthRuleViolation) event).incidentID + " ";
            if (status.length() + urlLength + msg.length() <= STATUS_LENGTH) {
                status += msg;
            }

            msg = ((HealthRuleViolation) event).healthRuleName + " ";
            if (status.length() + urlLength + msg.length() <= STATUS_LENGTH) {
                status += msg;
            }

            status += event.deepLinkUrl + ((HealthRuleViolation) event).incidentID;
        } else {
            status = "Event. ";
            status += "P" + event.priority + " ";
            status += event.severity + " ";

            String msg;
            msg = "Event ID:" + ((OtherEvent) event).eventID + " ";
            if (status.length() + urlLength + msg.length() <= STATUS_LENGTH) {
                status += msg;
            }

            msg = ((OtherEvent) event).eventName + " ";
            if (status.length() + urlLength + msg.length() <= STATUS_LENGTH) {
                status += msg;
            }

            status += event.deepLinkUrl + ((OtherEvent) event).eventID;
        }
        return status;
    }

    private static void storeRateLimitStatus(RateLimitStatus status){
        storeObject(status, RATE_LIMIT_STATUS_FILE, "rate limit status");
    }

    private static void storeObject(Object obj, String filePath, String desc){
        try {
            FileOutputStream fos = new FileOutputStream(filePath,false);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(obj);
        } catch (FileNotFoundException e) {
            logger.error("Cannot create file '" + filePath + "'," + desc + " not stored");
        } catch (IOException e) {
            logger.error("Failed to " + desc);
        }
    }
    private static RateLimitStatus readRateLimitStatus() {
        File statusFile = new File(RATE_LIMIT_STATUS_FILE);
        if (statusFile.exists()){
            try {
                FileInputStream fis = new FileInputStream(RATE_LIMIT_STATUS_FILE);
                ObjectInputStream ois = new ObjectInputStream(fis);
                return (RateLimitStatus) ois.readObject();
            } catch (IOException e) {
                logger.error("Rate limit status unreadable");
            } catch (ClassNotFoundException e) {
                logger.error("Rate limit status unreadable");
            }
        }
        return null;
    }

    private static boolean isLimitReached() {
        RateLimitStatus status = readRateLimitStatus();
        if (status != null && status.getResetTimeInSeconds() > System.currentTimeMillis() / 1000) {
            logger.warn("Twitter rate limit reached, status update ignored until next reset");
            return true;
        }
        return false;
    }

    private static String[] stripQuotes(String[] args) {
        String[] stripped = new String[args.length];
        for (int i=0;i<args.length;i++){
            stripped[i] = args[i].replaceAll("^\"|\"$","");
        }
        return stripped;
    }
}
