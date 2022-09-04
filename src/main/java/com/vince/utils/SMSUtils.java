package com.vince.utils;

import com.tencentcloudapi.common.Credential;
import com.tencentcloudapi.common.profile.ClientProfile;
import com.tencentcloudapi.common.profile.HttpProfile;
import com.tencentcloudapi.common.exception.TencentCloudSDKException;
import com.tencentcloudapi.sms.v20190711.SmsClient;
import com.tencentcloudapi.sms.v20190711.models.SendSmsRequest;
import com.tencentcloudapi.sms.v20190711.models.SendSmsResponse;

public class SMSUtils
{

    /**
     * 发送短信，这个功能仅供参考，项目没有用短信
     * @param signName 签名
     * @param templateCode 模板
     * @param phoneNumbers 手机号
     * @param param 参数
     */
    public static void sendMessage(String signName, String templateCode,String phoneNumbers,String param) {
        try{
            // 注意点：填写自己的secreteid和secretekey，密钥在腾讯云访问密钥获取
            Credential cred = new Credential("xx", "xx");
            // 实例化一个http选项，可选的，没有特殊需求可以跳过
            HttpProfile httpProfile = new HttpProfile();
            httpProfile.setEndpoint("sms.tencentcloudapi.com");
            // 实例化一个client选项，可选的，没有特殊需求可以跳过
            ClientProfile clientProfile = new ClientProfile();
            clientProfile.setHttpProfile(httpProfile);
            // 实例化要请求产品的client对象,clientProfile是可选的
            SmsClient client = new SmsClient(cred, "ap-guangzhou", clientProfile);
            // 实例化一个请求对象,每个接口都会对应一个request对象
            com.tencentcloudapi.sms.v20190711.models.SendSmsRequest req = new SendSmsRequest();
            //注意点：+86必须要，为国内号码，不然会识别不了国内号码
            String[] phoneNumberSet1 = {"+8618773227702"};
            req.setPhoneNumberSet(phoneNumberSet1);

            //设置自己的SdkAppID
            req.setSmsSdkAppid("1400515730");
            //设置自己短信签名名称，不是签名id
            req.setSign("Devin的小管家");
            //设置自己短信模板id
            req.setTemplateID("959096");

            //设置验证码
            String[] templateParamSet1 = {"666666"};
            req.setTemplateParamSet(templateParamSet1);

            // 返回的resp是一个SendSmsResponse的实例，与请求对象对应
            SendSmsResponse resp = client.SendSms(req);
            // 输出json格式的字符串回包
            System.out.println(SendSmsResponse.toJsonString(resp));
        } catch (TencentCloudSDKException e) {
            System.out.println(e.toString());
        }
    }
}