import cn.jiguang.common.resp.APIConnectionException;
import cn.jiguang.common.resp.APIRequestException;
import cn.jpush.api.JPushClient;
import cn.jpush.api.push.PushResult;
import cn.jpush.api.push.model.Platform;
import cn.jpush.api.push.model.PushPayload;
import cn.jpush.api.push.model.audience.Audience;
import cn.jpush.api.push.model.notification.AndroidNotification;
import cn.jpush.api.push.model.notification.IosNotification;
import cn.jpush.api.push.model.notification.Notification;
import org.apache.log4j.Logger;

import java.util.Set;



public class JPushTool {

    //TODO log4j
    private static Logger logger = Logger.getLogger(JPushTool.class);

    //TODO 跟服务器建立连接的最大尝试次数
    private static final int MAX_RETRY_NUM = 3;
    //TODO 注册应用的主密码，API主密码
    private static final String MASTER_SECRET = "1bf9178cf5d809a157260ebf";
    //TODO 注册应用的APP_KEY
    private static final String APP_KEY = "6d7f6240bf4fa79bf0e377f8";

    private String title = "";
    private JPushClient jpushClient;
    private String content;

    public JPushTool(String content) {
        this.content = content;
        jpushClient = new JPushClient(MASTER_SECRET, APP_KEY, MAX_RETRY_NUM);
    }

    public JPushTool(String title, String content) {
        this(content);
        this.title = (title == null ? "" : title);
    }

    /**
     * 向所有人推送消息
     *
     * @return 消息ID
     */
    public long sendPushAll() {
        PushPayload payload = buildPushObject_all_all_alert();
        long msgId = 0;
        try {
            PushResult result = jpushClient.sendPush(payload);
            msgId = result.msg_id;
        } catch (APIConnectionException e) {
            System.err.println("[Connection Error]:" + e.toString());
        } catch (APIRequestException e) {
            logger.info("HTTP Status:" + e.getStatus());
            msgId = e.getMsgId();
        }
        return msgId;
    }

    /**
     * 向指定别名的客户端推送消息
     *
     * @param alias 所有别名的集合[可以使用编号]
     * @return 消息ID
     */
    public long sendPushAlias(Set<String> alias) {
        //PushPayload payloadAlias = buildPushObject_android_alias_alertWithTitle(alias);
        PushPayload payloadAlias = buildPushObject_all_alias_alertWithTitle(alias);
        long msgId = 0;
        try {
            PushResult result = jpushClient.sendPush(payloadAlias);
            msgId = result.msg_id;
        } catch (APIConnectionException e) {
            System.err.println("[Connection Error]:" + e.toString());
        } catch (APIRequestException e) {
            logger.info("HTTP Status:" + e.getStatus());
            logger.info("Error Code:" + e.getErrorCode());
            logger.info("Error Message:" + e.getErrorMessage());
            logger.info("Msg ID:" + e.getMsgId());
            msgId = e.getMsgId();
        }
        return msgId;
    }

    /**
     * 向指定组推送消息
     *
     * @param tag 组名称
     * @return 消息ID
     */
    public long sendPushTag(String tag) {
        PushPayload payloadtag = buildPushObject_android_tag_alertWithTitle(tag);
        long msgId = 0;
        try {
            PushResult result = jpushClient.sendPush(payloadtag);
            msgId = result.msg_id;
        } catch (APIConnectionException e) {
            System.err.println("[Connection Error]:" + e.toString());
        } catch (APIRequestException e) {
            logger.info("HTTP Status:" + e.getStatus());
            logger.info("Error Code:" + e.getErrorCode());
            logger.info("Error Message:" + e.getErrorMessage());
            logger.info("Msg ID:" + e.getMsgId());
            msgId = e.getMsgId();
        }
        return msgId;
    }


    //TODO 下列封装了三种获得消息推送对象(android)
    public PushPayload buildPushObject_android_alias_alertWithTitle(Set<String> alias) {
        return PushPayload.newBuilder().setPlatform(Platform.android()).setAudience(Audience.alias(alias)).setNotification(Notification.android(content, title, null)).build();
    }

    public PushPayload buildPushObject_android_tag_alertWithTitle(String tag) {
        return PushPayload.newBuilder().setPlatform(Platform.android()).setAudience(Audience.tag(tag)).setNotification(Notification.android(content, title, null)).build();
    }

    public PushPayload buildPushObject_all_all_alert() {
        return PushPayload.alertAll(content);
    }

    //TODO 全平台消息推送
    public PushPayload buildPushObject_all_alias_alertWithTitle(Set<String> alias) {
        return PushPayload.newBuilder()
                .setPlatform(Platform.all())
                .setAudience(Audience.alias(alias))
                .setNotification(Notification.newBuilder()
                        .setAlert(content)
                        .addPlatformNotification(AndroidNotification.newBuilder()
                                .setTitle(title).build())
                        .addPlatformNotification(IosNotification.newBuilder()
                                .incrBadge(1)
                                //extra_key extra_value
                                .addExtra(title, content).build())
                        .build())
                .build();
    }


}
