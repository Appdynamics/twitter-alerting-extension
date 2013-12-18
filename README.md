twitter-alerting-extension
==========================

## Use Case

The Twitter alerting extension enables AppDynamics to post custom notifications as tweets on Twitter. Twitter followers can see a brief description of the health rule violation or event and get more detail on AppDynamics by following the URL provided in the tweet.

## Installation

### Prerequisites

- You have a Twitter account.
- You have set the account type to "protected". [How to protect your tweets] (https://support.twitter.com/groups/51-me/topics/205-account-settings/articles/20169886-protecting-and-unprotecting-your-tweets#)
- You have added and approved followers to the Twitter account.
- You have created a Twitter app on [dev.twitter.com/apps] (https://dev.twitter.com/apps) and have the consumer key and secret for the application. (Consumer key and secret can be found under application details.)

### Steps

1. Run ```ant package``` from the twitter-alerting-extension directory.
2. Deploy the file TwitterAlert.zip found in the 'dist' directory into the controller installation directory.
3. Unzip the file. You will see a prompt if you already have a custom.xml file in the <Controller-install-dir>/custom/actions/ directory. **Don't let the unzip process overwrite it.** Instead, merge the contents.
4. Copy the consumer key and secret from your Twitter application detail page and paste them in custom/actions/update-twitter-status/consumer.conf.
5. cd into <Controller-install-dir>/custom/actions/authenticate-twitter-extension and run auth.sh (On Windows systems, run auth.bat).
6. Follow the URL shown in the command line and authorize the app.
7. Enter the PIN shown on the page in the command prompt.

Now you are ready to use this extension as a custom action. In the AppDynamics UI, go to Alert & Respond -> Actions. Click Create Action. Select Custom Action and click OK. In the drop-down menu you can find the action called 'update-twitter-status'.

**Note**: Twitter has a posting limit of 1000 tweets per day and about 15 tweets in a burst; any tweets that exceed this limit will be ignored. Therefore this extension should not be used for any frequently occurring events or notifications.

##Contributing

Always feel free to fork and contribute any changes directly here on GitHub.

##Community

Find out more in the [AppSphere](http://appsphere.appdynamics.com/t5/eXchange/Twitter-Alerting-Extension/idi-p/5571) community.

##Support

For any questions or feature request, please contact [AppDynamics Center of Excellence](mailto:ace-request@appdynamics.com).


