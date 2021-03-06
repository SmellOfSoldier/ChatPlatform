package Server;

public interface Sign
{
    String Register="REGISTER_COMMEND";                       //注册
    String login="LOGIN_COMMEND";                          //登录
    String SendPrivateMessage="SEND_PRIVATE_MESSAGE_COMMEND";           //给个人发送信息
    String SendPublicMessage="SEND_PUBLIC_MESSAGE_COMMEND";          //给所有人发送信息
    String ClientExit="CLIENT_EXIT_COMMEND";                 //退出客户端
    String SplitSign="SPLIT_SIGN_COMMEND";                       //信息分隔符
    String Pass="PASS_COMMEND";          //密码正确
    String UnPass="UN_PASS_COMMEND";      //密码错误
    String SendObject="SEND_OBJECT_COMMEND";                     //发送对象
    String ServerExit="SERVER_EXIT_COMMEND";                     //服务器退出
    String RepeatOnline="REPEAT_ONLINE_COMMEND";                   //帐号被重复登录
    String FromServerMessage="FROM_SERVER_MESSAGE_COMMEND";              //来自服务器的消息
    String OneUserIsOnline="ONE_USER_IS_ONLINE";                        //用户上线
    String OneUserOffOnline="ONE_USER_OFF_ONLINE";                      //用户离线
    String YoursInformation="YOURS_INFORMATION";                        //用户的信息
}
